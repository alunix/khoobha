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
public class ChildDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_IMAGE};
    private SharedPreferences prefs;

    public ChildDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void addChild(Child child){
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, child.name);
            values.put(DatabaseHelper.COLUMN_IMAGE, child.imageName);
            child.id = database.insert(DatabaseHelper.TABLE_CHILD, null, values);
            Logger.log(database,DatabaseHelper.TABLE_CHILD, Logger.OPERATIONS.INSERT,child.id);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteChild(Child child){
        database.delete(DatabaseHelper.TABLE_CHILD, DatabaseHelper.COLUMN_ID + " = " + child.id, null);
    }

    public List<Child> getAllChildren(){
        List<Child> children = new ArrayList<Child>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_CHILD,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Child child = cursorToChild(cursor);
            children.add(child);
            cursor.moveToNext();
        }

        cursor.close();
        return children;
    }

    public boolean updateChild(Child child){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, child.id);
        values.put(DatabaseHelper.COLUMN_NAME, child.name);
        values.put(DatabaseHelper.COLUMN_IMAGE, child.imageName);
        return database.update(DatabaseHelper.TABLE_CHILD,values, DatabaseHelper.COLUMN_ID + "=" + child.id, null) > 0;
    }

    private Child cursorToChild(Cursor cursor){
        try{
            return new Child(cursor.getLong(0),cursor.getString(1),cursor.getString(2));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
