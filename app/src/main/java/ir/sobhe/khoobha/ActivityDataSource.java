package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ActivityDataSource {

    public SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE,
                                    DatabaseHelper.COLUMN_POINTS, DatabaseHelper.COLUMN_CATEGORY_ID,
                                    DatabaseHelper.COLUMN_SOLITARY};

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
            values.put("id", DatabaseHelper.getNewId(database, "activity"));
            values.put(DatabaseHelper.COLUMN_TITLE, activity.title);
            values.put(DatabaseHelper.COLUMN_POINTS, activity.points);
            if(activity.category_id >= 0)
                values.put(DatabaseHelper.COLUMN_CATEGORY_ID, activity.category_id);
            values.put(DatabaseHelper.COLUMN_SOLITARY, activity.solitary);
            activity.id = database.insert(DatabaseHelper.TABLE_ACTIVITY, null, values);
            Logger.log(database,DatabaseHelper.TABLE_ACTIVITY, Logger.OPERATIONS.INSERT, activity.id);
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

    public List<Activity> getCategoryActivities(long category_id)
    {
        List<Activity> activities = new ArrayList<Activity>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_ACTIVITY, allColumns,
                "category_id = " + category_id, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Activity activity = cursorToActivity(cursor);
            activities.add(activity);
            cursor.moveToNext();
        }
        return activities;
    }

    private Activity cursorToActivity(Cursor cursor){
        return new Activity(cursor.getLong(0),cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4)>0);
    }
}
