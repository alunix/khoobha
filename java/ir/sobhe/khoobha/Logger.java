package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hadi on 14/5/15 AD.
 */
public class Logger {
    //private static SQLiteDatabase database;
    //private static DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_TABLE_NAME,
            DatabaseHelper.COLUMN_OPERATION,
            DatabaseHelper.COLUMN_ROW_ID,
            DatabaseHelper.COLUMN_CREATED_AT
    };

    public static final class OPERATIONS{
        public static final String INSERT = "insert";
        public static final String DELETE = "delete";
        public static final String UPDATE = "update";
    };

    public static void log(SQLiteDatabase db, String tableName, String operation, long row_id){
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TABLE_NAME, tableName);
            values.put(DatabaseHelper.COLUMN_OPERATION, operation);
            values.put(DatabaseHelper.COLUMN_ROW_ID, row_id);
            db.insert(DatabaseHelper.TABLE_LOGS, null, values);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
