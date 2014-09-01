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

/**
 * Created by hadi on 14/8/21 AD.
 */
public class CategoryDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_IMAGE};
    private SharedPreferences prefs;

    public CategoryDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public Category getCategory(long category_Id){
        Category ret = null;

        Cursor cursor = database.query("category",
                allColumns, "id = " + category_Id, null, null, null, null);

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


    private Category cursorToCategory(Cursor cursor){
        try{
            return new Category(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
