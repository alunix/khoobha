package ir.sobhe.khoobha;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        getActionBar().setTitle("ثبت نام");

        dataSource = new ChildDataSource(this);
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        child = new Child();
        dataSource.open();
        imageView = (ImageView)findViewById(R.id.img_child);
        final EditText txt_childName = (EditText)findViewById(R.id.txt_childName);
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("editChild", false);
        if (isEdit)
        {
            long childId = intent.getLongExtra("childId", 0);
            child = dataSource.getChild(childId);
            imageView.setImageBitmap(BitmapFactory.decodeFile(Child.DIR_PATH + child.imageName));
            txt_childName.setText(child.name);
        }
        else{
            txt_childName.setHint("نام");
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });



        Button btn_saveChild = (Button)findViewById(R.id.btn_saveChild);

        btn_saveChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream out = null;
                Bitmap photo = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                EditText txt_childName = (EditText)findViewById(R.id.txt_childName);
                child.image = photo;
                child.imageName = Long.toString(System.currentTimeMillis())+".png";
                child.name = txt_childName.getText().toString();
                if(child.name == "نام")
                    child.name = null;
                try {
                    File dir = new File(Child.DIR_PATH);
                    dir.mkdirs();
                    out = new FileOutputStream(new File(dir, child.imageName));
                    photo.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    child.imageName = "";
                    e.printStackTrace();
                } finally {
                    try{
                        out.close();
                    } catch(Throwable ignore) {}
                }

                if(isEdit)
                    dataSource.updateChild(child);
                else
                    dataSource.addChild(child);
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

