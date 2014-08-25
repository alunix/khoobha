package ir.sobhe.khoobha;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class Activity {

    public long id;
    public String title;
    public int points;
    public int category_id;
    public boolean solitary;

    public Activity(String col_title, int col_points){
        id = -1;
        title = col_title;
        points = col_points;
        category_id = -1;
        solitary = false;
    }

    public Activity(long col_id, String col_title, int col_points, int category, boolean _solitary){
        id = col_id;
        title = col_title;
        points = col_points;
        category_id = category;
        solitary = _solitary;
    }


}
