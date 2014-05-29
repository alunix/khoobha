package ir.sobhe.khoobha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class AddChildActivity extends ActionBarActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private ChildDataSource dataSource;
    private Child child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        dataSource = new ChildDataSource(this);
        dataSource.open();
        imageView = (ImageView)findViewById(R.id.img_child);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        ((EditText)findViewById(R.id.txt_childName)).setHint("نام");


        Button btn_saveChild = (Button)findViewById(R.id.btn_saveChild);

        btn_saveChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream out = null;
                Bitmap photo = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                child = new Child(photo, null);
                dataSource.addChild(child);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Khoobha";
                try {
                    File dir = new File(path);
                    dir.mkdirs();
                    out = new FileOutputStream(new File(dir, Long.toString(child.id)+".png"));
                    photo.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try{
                        out.close();
                    } catch(Throwable ignore) {}
                }

                EditText txt_childName = (EditText)findViewById(R.id.txt_childName);
                child.name = txt_childName.getText().toString();
                child.imageName = Long.toString(child.id);
                if(child.name == "نام")
                    child.name = null;
                dataSource.updateChild(child);

                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

        }
        else
            finish();
    }



}

