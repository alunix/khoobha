package ir.sobhe.khoobha;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLInput;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_CHILD = "child";
    public static final String TABLE_ACTIVITY = "activity";
    public static final String TABLE_RECORD = "record";
    public static final String TABLE_LOGS = "logs"

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
    public static final String COLUMN_CREATED_AT = "created_att";
    public static final String COLUMN_OPERATION = "operation";


    private static final String DATABASE_NAME = "khoobha.db";
    private static final int DATABASE_VERSION = 1;





    //database creation script
    private static final String CHILD_CREATE = ""
            + "CREATE TABLE " + TABLE_CHILD + " ("
            + COLUMN_ID + " integer NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(100));";

    private static final String ACTIVITY_CREATE = ""
            + "create table " + TABLE_ACTIVITY + " ("
            + COLUMN_ID + " integer NOT NULL PRIMARY KEY, "
            + COLUMN_TITLE + " varchar(255) NOT NULL UNIQUE, "
            + COLUMN_POINTS + " integer unsigned NOT NULL);";

    private static final String RECORD_CREATE = ""
            + "CREATE TABLE " + TABLE_RECORD + " ("
            + COLUMN_ID + " integer NOT NULL PRIMARY KEY, "
            + COLUMN_ACTIVITY_ID + " integer NOT NULL, "
            + COLUMN_CHILD_LIST + " varchar(1000) NOT NULL, "
            + COLUMN_ITEMS + " integer unsigned NOT NULL, "
            + COLUMN_DATE + " date NOT NULL);";

    private static final String LOGS_CREATE = ""
            + "CREATE TABLE logs ("
            + " table_name varchar(20) not null,"
            + " operation varchar(10) not null, -- enum(\"insert\",\"update\", \"delete\")"
            + " row_id integer not null,"
            + " created_at timestamp default current_timestamp);";

    private static final String GROUP_CREATE = ""
            + "CREATE TABLE group (" +
            "id integer null default null, " +
            "title varchar(255) null default null, " +
            "slug varchar(100) null default null, " +
            "image varchar(50) null default null, " +
            "version varchar(10) null default null, " +
            "synced_at timestamp null default null, " +
            "assistant_email varchar(50) null default null, " +
            "assistant_password varchar(50) null default null, " +
            "options text null default null" +
            ");";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(CHILD_CREATE);
            sqLiteDatabase.execSQL(ACTIVITY_CREATE);
            sqLiteDatabase.execSQL(RECORD_CREATE);
            sqLiteDatabase.execSQL(LOGS_CREATE);
            sqLiteDatabase.execSQL(GROUP_CREATE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + i + " to" + i2
                + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD + ", " + TABLE_RECORD + ", " + TABLE_ACTIVITY);
        onCreate(sqLiteDatabase);
    }
}
