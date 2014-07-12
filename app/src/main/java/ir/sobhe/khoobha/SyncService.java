package ir.sobhe.khoobha;

import android.app.*;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SyncService extends IntentService {

    public static final int RESULT_ERROR = 29;
    private int serviceResult = Activity.RESULT_OK;
    public static final String NOTIFICATION = "ir.sobhe.khoobha";
    public static final String API = "http://khoobha.net/api/";
    private String groupId, received_at, sent_at;
    private String directory;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private UsernamePasswordCredentials credentials;

    public SyncService() {
        super("SyncService");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Khoobha";
    }

    private void publishResult(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("result", result);
        sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncData();

        //when everything has been done
        publishResult(serviceResult);
        stopSelf();
    }

    private void syncData() {
        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // read group data
        Cursor cursor = database.rawQuery("select id, received_at, sent_at, assistant_email, assistant_password from `group`", null);
        cursor.moveToFirst();
        groupId = cursor.getString(0);
        received_at = cursor.getString(1);
        sent_at = cursor.getString(2);
        if (sent_at == null) sent_at = "";
        if (received_at == null) received_at = "";
        credentials = new UsernamePasswordCredentials(cursor.getString(3), cursor.getString(4));

        // receive events log
        JSONArray events = new JSONArray();
        try {
            String url = API + "event/?group="+ groupId +"&received_at="+ received_at;
            HttpResponse response = (new DefaultHttpClient()).execute(new HttpGet(url));
            events = new JSONArray(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            serviceResult = RESULT_ERROR;
        }

        // receive and send data
        if (serviceResult != RESULT_ERROR && sendData() && receiveData(events))
            serviceResult = Activity.RESULT_OK;
        else
            serviceResult = RESULT_ERROR;

        database.close();
        dbHelper.close();
    }

    private boolean receiveData(JSONArray events) {
        JSONObject event, object;
        String table, operation, row_id, created_at = "", url, values, image;
        int i = 0;

        try {
            for (i = 0; i < events.length(); i++) {
                event = events.getJSONObject(i);
                table = event.getString("table");
                operation = event.getString("operation");
                row_id = event.getString("row_id");

                // todo: skip my events

                url = API + table +"/"+ row_id + "/";
                HttpResponse response = (new DefaultHttpClient()).execute(new HttpGet(url));
                object = new JSONObject(EntityUtils.toString(response.getEntity()));

                if (table.equals("child")) {
                    image = object.getString("image").substring(7); // childs/id.png
                    downloadChildImage(image);
                    values = object.getString("id") + ",'" + object.getString("name") + "','" + image + "'";
                } else if (table.equals("activity"))
                    values = object.getString("id") +",'"+ object.getString("title") +"',"+ object.getString("points");
                else if (table.equals("record"))
                    values = object.getString("id") +","+ object.getString("activity") +",'"+ object.getString("child_list") +"',"+ object.getString("items") +",'"+ object.getString("date") +"'";
                else
                    continue;

                database.execSQL("replace into "+ table +" values ("+ values +")");

                created_at = event.getString("created_at");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!created_at.isEmpty())
            database.execSQL("update `group` set received_at='" + created_at + "'");

        return i == events.length();
    }

    private boolean sendData() {
        List<NameValuePair> args = new ArrayList<NameValuePair>();
        String table, id, operation, url = "", filename = "", last = "";
        JSONObject result;

        // read logs
        Cursor cursor2;
        Cursor cursor = database.rawQuery("select table_name, row_id, operation, created_at from log where created_at > '" + sent_at + "'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            table = cursor.getString(0);
            id = cursor.getString(1);
            operation = cursor.getString(2);

            cursor2 = database.rawQuery("select * from " + table + " where id = " + id, null);
            if (cursor2.getCount() > 0) {
                cursor2.moveToFirst();

                args.clear();
                if (table.equals(DatabaseHelper.TABLE_CHILD)) {
                    args.add(new BasicNameValuePair("id", id));
                    args.add(new BasicNameValuePair("name", cursor2.getString(1)));
                    filename = cursor2.getString(2);
                } else if (table.equals(DatabaseHelper.TABLE_ACTIVITY)) {
                    args.add(new BasicNameValuePair("id", id));
                    args.add(new BasicNameValuePair("title", cursor2.getString(1)));
                    args.add(new BasicNameValuePair("points", cursor2.getString(2)));
                } else if (table.equals(DatabaseHelper.TABLE_RECORD)) {
                    args.add(new BasicNameValuePair("activity", cursor2.getString(1)));
                    args.add(new BasicNameValuePair("child_list", cursor2.getString(2)));
                    args.add(new BasicNameValuePair("items", cursor2.getString(3)));
                    args.add(new BasicNameValuePair("date", cursor2.getString(4)));
                } else
                    continue;

                try {
                    if (operation.equals("insert"))
                        result = send(new HttpPost(API + table +"/"), args, filename);
                    else if (operation.equals("update"))
                        result = send(new HttpPut(API + table +"/"+ id +"/"), args, filename);
                    else
                        continue;

                    if (operation.equals("insert"))
                        updateId(database, table, id, result.getString("id"));

                    if (table.equals("record"))
                        database.execSQL("update record set child_list='" + result.getString("child_list") + "', items="+ result.getString("items") +" where id = "+ result.getString("id"));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            last = cursor.getString(3);
            cursor.moveToNext();
        }

        // update sent_at
        if (!last.isEmpty())
            database.execSQL("update `group` set sent_at='" + last + "'");

        return cursor.isAfterLast();
    }

    private void updateId(SQLiteDatabase database, String table, String oldId, String newId) {
        database.execSQL("update `" + table + "` set id = " + newId + " where id = " + oldId);
        database.execSQL("update log set row_id = " + newId + " where table_name = '" + table + "' and row_id = " + oldId);

        if (table.equals(DatabaseHelper.TABLE_CHILD))
            database.execSQL("update record set child_list = replace(trim(replace(replace(','||child_list||',', '," + oldId + ",', '," + newId + ",'), ',', ' ')), ' ', ',')");
        else if (table.equals(DatabaseHelper.TABLE_ACTIVITY))
            database.execSQL("update record set activity_id = " + newId + " where activity_id = " + oldId);
    }

    private JSONObject send(HttpEntityEnclosingRequestBase request, List<NameValuePair> args, String filename) throws IOException, JSONException, AuthenticationException {
        // authentication
        request.addHeader(new BasicScheme().authenticate(credentials, request));

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

        // parameters
        builder.addTextBody("group", groupId);
        for (int i = 0; i < args.size(); i++)
            builder.addPart(args.get(i).getName(), new StringBody(args.get(i).getValue(), contentType));

        // image
        if (!filename.isEmpty())
            builder.addPart("image", new FileBody(new File(directory, filename)));

        request.setEntity(builder.build());

        // send
        HttpResponse response = (new DefaultHttpClient()).execute(request);
        return new JSONObject(EntityUtils.toString(response.getEntity()));
    }

    private void downloadChildImage(String filename) throws IOException {
        URL url = new URL("http://khoobha.net/media/childs/"+ filename);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        File file = new File(directory, filename);
        FileOutputStream fileOutput = new FileOutputStream(file);
        InputStream inputStream = urlConnection.getInputStream();

        int bufferLength = 0;
        byte[] buffer = new byte[1024];
        while ((bufferLength = inputStream.read(buffer)) > 0)
            fileOutput.write(buffer, 0, bufferLength);
        fileOutput.close();
    }
}
