package ir.sobhe.khoobha;

import java.sql.Date;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class Record {

    public long id;
    public long activity_id;
    public String child_list;
    public int items;
    public Date date;

    public Record(long _id, long ai, String cl, int col_items, Date d)
    {
        id = _id;
        activity_id = ai;
        child_list = cl;
        items = col_items;
        date = d;
    }

}
