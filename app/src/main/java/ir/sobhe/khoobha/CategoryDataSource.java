package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDataSource {
    SQLiteDatabase database;
    DatabaseHelper dbHelper;
    String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_IMAGE, DatabaseHelper.COLUMN_SOLITARY};

    public CategoryDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Category getCategory(long category_Id){
        Category ret = null;

        Cursor cursor = database.query("category", allColumns, "id = " + category_Id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast())
            ret = cursorToCategory(cursor);

        cursor.close();
        return ret;
    }


    public List<Category> getAllCategories(){
        List<Category> categories = new ArrayList<Category>();

        Cursor cursor = database.query("category",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToNext();
        }

        cursor.close();
        return categories;
    }

    public List<Activity> getCategoryActivities(long category_id) {
        List<Activity> activities = new ArrayList<Activity>();
        Cursor cursor = database.rawQuery("select * from activity where category_id = " + category_id, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Activity activity = ActivityDataSource.cursorToActivity(cursor);
            activities.add(activity);
            cursor.moveToNext();
        }
        return activities;
    }

    private Category cursorToCategory(Cursor cursor){
        try{
            return new Category(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
