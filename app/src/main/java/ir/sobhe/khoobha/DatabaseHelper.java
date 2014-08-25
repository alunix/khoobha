package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.sql.SQLInput;


public class DatabaseHelper extends SQLiteOpenHelper {
    private InputStream categoriesStream;
    private InputStream activitiesStream;
    public static final String TABLE_CHILD = "child";
    public static final String TABLE_ACTIVITY = "activity";
    public static final String TABLE_RECORD = "record";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_LOGS = "log";
    public static final String TABLE_GROUP = "`group`";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ACTIVITY_ID = "activity_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_CHILD_LIST = "child_list";
    public static final String COLUMN_ITEMS = "items";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ROW_ID = "row_id";
    public static final String COLUMN_TABLE_NAME = "table_name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_OPERATION = "operation";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_SOLITARY = "solitary";

    private static final String DATABASE_NAME = "khoobha.db";
    private static final int DATABASE_VERSION = 2;

    private static final String CHILD_CREATE = ""+
            "create table child ("+
            "id integer not null primary key, "+
            "name varchar(100) null default null, "+
            "image varchar(100) null default null);";

    private static final String ACTIVITY_CREATE = ""+
            "create table activity ("+
            "id integer not null primary key, "+
            "title varchar(255) not null unique, "+
            "points integer unsigned not null, "+
            COLUMN_CATEGORY_ID + " integer null default null, "+
            COLUMN_SOLITARY + " boolean not null default 0);";

    private static final String RECORD_CREATE = ""+
            "create table record ("+
            "id integer not null primary key, "+
            "activity_id integer not null, "+
            "child_list varchar(1000) not null, "+
            "items integer unsigned not null, "+
            "date varchar(10) not null);";

    private static final String CATEGORY_CREATE = ""+
            "create table category ("+
            "id integer not null primary key, "+
            "title varchar(100) not null unique, "+
            "image varchar(100) null default null);";

    private static final String LOGS_CREATE = ""+
            "create table log ("+
            "table_name varchar(20) not null, "+
            "operation varchar(10) not null, "+
            "row_id integer not null, "+
            "created_at timestamp default current_timestamp);";

    private static final String GROUP_CREATE = ""+
            "create table `group` ("+
            "id integer null default null, "+
            "title varchar(255) null default null, "+
            "slug varchar(100) null default null, "+
            "image varchar(50) null default null, "+
            "version varchar(10) null default null, "+
            "sent_at timestamp null default null, "+
            "received_at timestamp null default null, "+
            "assistant_email varchar(50) null default null, "+
            "assistant_password varchar(50) null default null, "+
            "options text null default null);";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        try{
            categoriesStream = context.getResources().getAssets().open("categories.txt");
            activitiesStream = context.getResources().getAssets().open("activities.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(CHILD_CREATE);
            sqLiteDatabase.execSQL(ACTIVITY_CREATE);
            sqLiteDatabase.execSQL(RECORD_CREATE);
            sqLiteDatabase.execSQL(CATEGORY_CREATE);
            sqLiteDatabase.execSQL(LOGS_CREATE);
            sqLiteDatabase.execSQL(GROUP_CREATE);

            //Add default activities and categories
            BufferedReader categoriesReader = new BufferedReader(new InputStreamReader(categoriesStream));
            BufferedReader activitiesReader = new BufferedReader(new InputStreamReader(activitiesStream));
            String categoriesString = "";
            String activitiesString = "";
            String currentLine = null;

            while((currentLine = categoriesReader.readLine()) != null)
                categoriesString += currentLine + "\n";
            while ((currentLine = activitiesReader.readLine()) != null)
                activitiesString += currentLine + "\n";

            String categoriesSql = String.format("INSERT INTO category(id, title) VALUES %s", categoriesString);
            sqLiteDatabase.execSQL(categoriesSql);

            String activitiesSql = String.format("INSERT INTO activity(id, title, points, category_id) VALUES %s", activitiesString);
            sqLiteDatabase.execSQL(activitiesSql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
