package ir.sobhe.khoobha;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_CHILD = "child";
    public static final String TABLE_ACTIVITY = "activity";
    public static final String TABLE_RECORD = "record";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ACTIVITY_ID = "activity_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_CHILD_LIST = "child_list";
    public static final String COLUMN_ITEMS = "items";
    public static final String COLUMN_DATE = "date";



    private static final String DATABASE_NAME = "khoobha.db";
    private static final int DATABASE_VERSION = 1;





    //database creation script
    private static final String CHILD_CREATE = ""
            + "CREATE TABLE " + TABLE_CHILD + " ("
            + COLUMN_ID + " integer NOT NULL PRIMARY KEY, "
            + COLUMN_NAME + " varchar(100) NOT NULL);";

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

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(CHILD_CREATE);
            sqLiteDatabase.execSQL(ACTIVITY_CREATE);
            sqLiteDatabase.execSQL(RECORD_CREATE);
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
