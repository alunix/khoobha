package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class ActivityDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_POINTS};

    public ActivityDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void addActivity(Activity activity){
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID, activity.id);
            values.put(DatabaseHelper.COLUMN_TITLE, activity.title);
            values.put(DatabaseHelper.COLUMN_POINTS, activity.points);
            database.insert(DatabaseHelper.TABLE_ACTIVITY, null, values);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteActivity(Activity activity){
        database.delete(DatabaseHelper.TABLE_ACTIVITY, DatabaseHelper.COLUMN_ID + " = " + activity.id, null);
    }

    public List<Activity> getAllActivities(){
        List<Activity> activities = new ArrayList<Activity>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_ACTIVITY,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Activity activity = cursorToActivity(cursor);
            activities.add(activity);
            cursor.moveToNext();
        }

        cursor.close();
        return activities;
    }

    private Activity cursorToActivity(Cursor cursor){
        return new Activity(cursor.getLong(0),cursor.getString(1), cursor.getInt(2));
    }
}
