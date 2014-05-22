package ir.sobhe.khoobha;

import android.app.*;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadi on 14/5/10 AD.
 */
public class SyncService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String NOTIFICATION = "ir.sobhe.khoobha";
    private String groupId, email, password, synced_at;
    private String directory;

    public SyncService(){
        super("SyncService");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Khoobha";
    }

    private void send(String url, List<NameValuePair> args, String filename) {
        HttpPost request = new HttpPost("http://khoobha.net/api/" + url);

        // authentication
        try {
            request.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(email, password), request));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        // parameters
        args.add(new BasicNameValuePair("group", groupId));
        try {
            request.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor, cursor2;

        // read group data
        cursor = database.rawQuery("select id, assistant_email, assistant_password, synced_at from `group`", null);
        cursor.moveToFirst();
        groupId = cursor.getString(0);
        email = cursor.getString(1);
        password = cursor.getString(2);
        synced_at = cursor.getString(3);
        if (synced_at == null) synced_at = "";

        List<NameValuePair> args = new ArrayList<NameValuePair>();
        String table, id, last = "";

        // read logs
        cursor = database.rawQuery("select table_name, row_id, operation, created_at from log where created_at > '"+ synced_at +"'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            table = cursor.getString(0);
            id = cursor.getString(1);
            last = cursor.getString(3);

            cursor2 = database.rawQuery("select * from "+ table +" where id = "+ id, null);
            cursor2.moveToFirst();

            args.clear();
            if (table.equals(DatabaseHelper.TABLE_CHILD)) {
                args.add(new BasicNameValuePair("id", id));
                args.add(new BasicNameValuePair("name", cursor2.getString(1)));
                send("childs/", args, id +".png");
            }
            else if (table.equals(DatabaseHelper.TABLE_ACTIVITY)) {
                args.add(new BasicNameValuePair("id", id));
                args.add(new BasicNameValuePair("title", cursor2.getString(1)));
                args.add(new BasicNameValuePair("points", cursor2.getString(2)));
                send("activities/", args, "");
            }
            else if (table.equals(DatabaseHelper.TABLE_RECORD)) {
                args.add(new BasicNameValuePair("activity", cursor2.getString(1)));
                args.add(new BasicNameValuePair("child_list", cursor2.getString(2)));
                args.add(new BasicNameValuePair("items", cursor2.getString(3)));
                args.add(new BasicNameValuePair("date", cursor2.getString(4)));
                send("records/", args, "");
            }

            cursor.moveToNext();
        }

        // update synced_at
        if (!last.isEmpty())
            database.execSQL("update `group` set synced_at='"+ last +"'");

        dbHelper.close();

        //when everything has been done
        publishResult(result);
        stopSelf();
    }

    private void publishResult(int result){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("result", result);
        sendBroadcast(intent);
    }
}
