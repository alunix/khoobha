package ir.sobhe.khoobha;

public class Category {
    int id;
    String title;
    String image;
    boolean solitary;

    public Category(int _id, String _title, String _image, int _solitary) {
        id = _id;
        title = _title;
        image = _image;
        solitary = _solitary > 0;
    }

    public Category(String _title)
    {
        title = _title;
    }
}
