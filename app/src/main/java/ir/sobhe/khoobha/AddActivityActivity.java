package ir.sobhe.khoobha;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivityActivity extends ActionBarActivity {

    private ActivityDataSource dataSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addactivity);
        ((EditText)findViewById(R.id.txt_points)).setHint("1");

        dataSource = new ActivityDataSource(this);


        Button btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txt_title = (EditText)findViewById(R.id.txt_title);
                String title = txt_title.getText().toString();
                int points = 0;
                if(title == null)
                {
                    Toast.makeText(AddActivityActivity.this, "لطفا عنوان فعالیت را تکمیل کنید",Toast.LENGTH_LONG).show();
                    return;
                }
                EditText txt_points = (EditText)findViewById(R.id.txt_points);
                try{
                    points = Integer.parseInt(txt_points.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(AddActivityActivity.this, "لطفا عدد معتبری را به عنوان امتیاز فعالیت وارد کنید.",Toast.LENGTH_LONG).show();
                    return;
                }
                Activity activity = new Activity(title, points);
                dataSource.open();
                dataSource.addActivity(activity);
                dataSource.close();
                AddActivityActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
