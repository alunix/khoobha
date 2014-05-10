package ir.sobhe.khoobha;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class Activity {

    public long id;
    public String title;
    public int points;

    public Activity(String col_title, int col_points){
        id = -1;
        title = col_title;
        points = col_points;
    }

    public Activity(long col_id, String col_title, int col_points){
        id = col_id;
        title = col_title;
        points = col_points;
    }


}
