package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hadi on 14/5/17 AD.
 */
public class GroupDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public GroupDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void addGroup(Group group){
        try{
            ContentValues values = new ContentValues();
            values.put("id", group.id);
            values.put("assistant_email", group.assistantEmail);
            values.put("assistant_password", group.assisrantPassword);
            values.put("title", group.groupTitle);
            database.insert(DatabaseHelper.TABLE_GROUP,null,values);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
