package ir.sobhe.khoobha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class Child {

    public static final String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Khoobha/";
    public long id;
    public String name;
    public Bitmap image;
    public String imageName;
    public boolean selected = false;


    public Child(long col_id, String col_name, String imgName){
        id = col_id;
        name = col_name;
        imageName = imgName;
    }

    public Child(Bitmap img, String imgName){
        image = img;
        imageName = imgName;
    }

    public Child()
    {

    }

}
