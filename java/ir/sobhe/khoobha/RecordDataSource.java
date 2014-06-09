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

    public void addRecord(Record record){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] columns = {DatabaseHelper.COLUMN_ID};
        String whereClause = DatabaseHelper.COLUMN_DATE + " = " + dateFormat.format(record.date)
                             + " AND " + DatabaseHelper.COLUMN_ACTIVITY_ID + " = " + record.activity_id;
        Cursor cursor = database.query(DatabaseHelper.TABLE_RECORD,columns,whereClause,null,null,null,null);
        if( cursor.getCount() == 0)
        {
            try{
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_ACTIVITY_ID, record.activity_id);
                values.put(DatabaseHelper.COLUMN_CHILD_LIST, record.child_list);
                values.put(DatabaseHelper.COLUMN_ITEMS, record.items);
                values.put(DatabaseHelper.COLUMN_DATE, dateFormat.format(record.date));
                record.id = database.insert(DatabaseHelper.TABLE_CHILD, null, values);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            cursor.moveToFirst();
            long id = cursor.getLong(0);
            try{
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_ACTIVITY_ID, record.activity_id);
                values.put(DatabaseHelper.COLUMN_CHILD_LIST, record.child_list);
                values.put(DatabaseHelper.COLUMN_ITEMS, record.items);
                values.put(DatabaseHelper.COLUMN_DATE, dateFormat.format(record.date));
                database.update(DatabaseHelper.TABLE_RECORD,values,DatabaseHelper.COLUMN_ID + " = " + id, null);
                record.id = id;
            }
            catch (Exception e){
                e.printStackTrace();
            }
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
        return new Record(cursor.getLong(0),cursor.getLong(1),cursor.getString(2),cursor.getInt(3), Date.valueOf(cursor.getString(4)));
    }
}
