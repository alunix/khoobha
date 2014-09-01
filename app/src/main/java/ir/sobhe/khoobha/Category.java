package ir.sobhe.khoobha;

/**
 * Created by hadi on 14/8/21 AD.
 */
public class Category {
    int id;
    String title;
    String image;


    public Category(int _id, String _title, String _image){
        id = _id;
        title = _title;
        image = _image;
    }

    public Category(String _title)
    {
        title = _title;
    }

}