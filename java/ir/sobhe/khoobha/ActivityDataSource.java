package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class ActivityDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_POINTS};
    private SharedPreferences prefs;

    public ActivityDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void addActivity(Activity activity){
        try{
            int groupId = prefs.getInt("groupId", 0);
            int firstActivityId = groupId * 1000;
            String query = String.format("SELECT id FROM %s WHERE id = %d", DatabaseHelper.TABLE_ACTIVITY, firstActivityId);
            Cursor c = database.rawQuery(query, null);
            if(c.getCount() == 0)
                activity.id = firstActivityId;
            else{
                query = String.format("SELECT max(%s) FROM %s WHERE id/1000 = %d",DatabaseHelper.COLUMN_ID, DatabaseHelper.TABLE_CHILD, groupId);
                c = database.rawQuery(query,null);
                c.moveToFirst();
                activity.id = c.getInt(0) + 1;
            }
            ContentValues values = new ContentValues();
            if(activity.id != -1)
                values.put(DatabaseHelper.COLUMN_ID, activity.id);
            values.put(DatabaseHelper.COLUMN_TITLE, activity.title);
            values.put(DatabaseHelper.COLUMN_POINTS, activity.points);
            database.insert(DatabaseHelper.TABLE_ACTIVITY, null, values);
            Logger.log(database,DatabaseHelper.TABLE_ACTIVITY, Logger.OPERATIONS.INSERT,activity.id);
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
