package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class Child {

    public long id;
    public String name;


    public Child(long col_id, String col_name){
        id = col_id;
        name = col_name;
    }



}
