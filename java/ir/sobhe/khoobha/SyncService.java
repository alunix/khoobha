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
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
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

    private void publishResult(int result){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("result", result);
        sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sync();

        //when everything has been done
        publishResult(result);
        stopSelf();
    }

    private void sync() {
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
        String table, id, operation, url = "", filename = "", last = "";
        JSONObject result;

        // read logs
        cursor = database.rawQuery("select table_name, row_id, operation, created_at from log where created_at > '"+ synced_at +"'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            table = cursor.getString(0);
            id = cursor.getString(1);
            operation = cursor.getString(2);
            last = cursor.getString(3);

            cursor2 = database.rawQuery("select * from "+ table +" where id = "+ id, null);
            cursor2.moveToFirst();

            args.clear();
            if (table.equals(DatabaseHelper.TABLE_CHILD)) {
                args.add(new BasicNameValuePair("id", id));
                args.add(new BasicNameValuePair("name", cursor2.getString(1)));
                url = "childs/";
                filename = id +".png";
            }
            else if (table.equals(DatabaseHelper.TABLE_ACTIVITY)) {
                args.add(new BasicNameValuePair("id", id));
                args.add(new BasicNameValuePair("title", cursor2.getString(1)));
                args.add(new BasicNameValuePair("points", cursor2.getString(2)));
                url = "activities/";
            }
            else if (table.equals(DatabaseHelper.TABLE_RECORD)) {
                args.add(new BasicNameValuePair("activity", cursor2.getString(1)));
                args.add(new BasicNameValuePair("child_list", cursor2.getString(2)));
                args.add(new BasicNameValuePair("items", cursor2.getString(3)));
                args.add(new BasicNameValuePair("date", cursor2.getString(4)));
                url = "records/";
            } else
                continue;

            if (operation.equals("insert"))
                result = send(new HttpPost("http://khoobha.net/api/" + url), args, filename);
            else if (operation.equals("update"))
                result = send(new HttpPut("http://khoobha.net/api/" + url + id), args, filename);
            else
                continue;

            if (operation.equals("insert")) {
                try {
                    updateId(database, table, id, result.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            cursor.moveToNext();
        }

        // update synced_at
        if (!last.isEmpty())
            database.execSQL("update `group` set synced_at='"+ last +"'");

        dbHelper.close();
    }

    private void updateId(SQLiteDatabase database, String table, String oldId, String newId) {
        if (table.equals(DatabaseHelper.TABLE_CHILD)) {
            database.execSQL("update child set id = "+ newId +" where id = "+ oldId);
            database.execSQL("update record set child_list = replace(child_list, '*"+ oldId +"*', '*"+ newId +"*')");
        }
        else if (table.equals(DatabaseHelper.TABLE_ACTIVITY)) {
            database.execSQL("update activity set id = "+ newId +" where id = "+ oldId);
            database.execSQL("update record set activity_id = "+ newId +" where activity_id = "+ oldId);
        }

        if (table.equals(DatabaseHelper.TABLE_CHILD) || table.equals(DatabaseHelper.TABLE_ACTIVITY))
            database.execSQL("update log set row_id = "+ newId +" where table_name = '"+ table +"' and row_id = "+ oldId);
    }


    private JSONObject send(HttpEntityEnclosingRequestBase request, List<NameValuePair> args, String filename) {
        // authentication
        try {
            request.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(email, password), request));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

        // parameters
        builder.addTextBody("group", groupId);
        for(int i = 0; i < args.size(); i++)
            builder.addPart(args.get(i).getName(), new StringBody(args.get(i).getValue(), contentType));

        // image
        if (!filename.isEmpty())
            builder.addPart("image", new FileBody(new File(directory , filename)));

        request.setEntity(builder.build());

        // send
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(request);
            return new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }
}
