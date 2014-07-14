package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.DHGenParameterSpec;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class RecordDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_ACTIVITY_ID,
            DatabaseHelper.COLUMN_CHILD_LIST,
            DatabaseHelper.COLUMN_ITEMS,
            DatabaseHelper.COLUMN_DATE
    };

    public RecordDataSource(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }


    public void updateRecord(Record record){
        String[] columns = {DatabaseHelper.COLUMN_ID};
        String whereClause = DatabaseHelper.COLUMN_DATE + " = \"" + record.date +"\""
                + " AND " + DatabaseHelper.COLUMN_ACTIVITY_ID + " = " + record.activity_id;
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECORD,columns,whereClause,null,null,null,null);
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ACTIVITY_ID, record.activity_id);
            values.put(DatabaseHelper.COLUMN_CHILD_LIST, record.child_list);
            values.put(DatabaseHelper.COLUMN_ITEMS, record.items);
            values.put(DatabaseHelper.COLUMN_DATE, record.date);
            database.update(DatabaseHelper.TABLE_RECORD,values,DatabaseHelper.COLUMN_ID + " = " + id, null);
            record.id = id;
            Logger.log(database,DatabaseHelper.TABLE_RECORD, Logger.OPERATIONS.UPDATE, record.id);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Record getRecord(long activity_id, String date){
        Record ret = null;
        String whereClause = DatabaseHelper.COLUMN_DATE + " = \"" + date + "\""
                + " AND " + DatabaseHelper.COLUMN_ACTIVITY_ID + " = " + activity_id;
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECORD,allColumns,whereClause,null,null,null,null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast())
            ret = cursorToRecord(cursor);

        return ret;
    }

    public void addRecord(Record record){
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ACTIVITY_ID, record.activity_id);
            values.put(DatabaseHelper.COLUMN_CHILD_LIST, record.child_list);
            values.put(DatabaseHelper.COLUMN_ITEMS, record.items);
            values.put(DatabaseHelper.COLUMN_DATE, record.date);
            record.id = database.insert(DatabaseHelper.TABLE_RECORD, null, values);
            Logger.log(database, DatabaseHelper.TABLE_RECORD, Logger.OPERATIONS.INSERT, record.id);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteRecord(Record record){
        database.delete(DatabaseHelper.TABLE_RECORD, DatabaseHelper.COLUMN_ID + " = " + record.id, null);
    }

    public List<Record> getAllRecords(){
        List<Record> records = new ArrayList<Record>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECORD,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Record record = cursorToRecord(cursor);
            records.add(record);
            cursor.moveToNext();
        }

        cursor.close();
        return records;
    }

    private Record cursorToRecord(Cursor cursor){
        return new Record(cursor.getLong(0),cursor.getLong(1),cursor.getString(2),cursor.getInt(3), cursor.getString(4));
    }
}
