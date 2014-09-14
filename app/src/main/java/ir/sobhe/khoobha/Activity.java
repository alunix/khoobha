package ir.sobhe.khoobha;

public class Activity {
    public long id;
    public String title;
    public int points;
    public int category_id;

    public Activity(String col_title, int col_points){
        id = -1;
        title = col_title;
        points = col_points;
        category_id = -1;
    }

    public Activity(String col_title, int col_points, int category) {
        id = -1;
        title = col_title;
        points = col_points;
        category_id = category;
    }

    public Activity(long col_id, String col_title, int col_points, int category) {
        id = col_id;
        title = col_title;
        points = col_points;
        category_id = category;
    }
}
