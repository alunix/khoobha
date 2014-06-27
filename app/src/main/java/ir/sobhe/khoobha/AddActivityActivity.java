package ir.sobhe.khoobha;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivityActivity extends android.app.Activity {

    private ActivityDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_activity);
        getActionBar().setTitle("فعالیت جدید");

        dataSource = new ActivityDataSource(this);
    }

    private void saveActivity() {
        EditText txt_title = (EditText)findViewById(R.id.txt_title);
        String title = txt_title.getText().toString();
        int points = 0;
        if(title == null)
        {
            Toast.makeText(AddActivityActivity.this, "لطفا عنوان فعالیت را تکمیل کنید",Toast.LENGTH_LONG).show();
            return;
        }
        EditText txt_points = (EditText)findViewById(R.id.txt_points);
        try {
            points = Integer.parseInt(txt_points.getText().toString());
        }
        catch (Exception e) {
            Toast.makeText(AddActivityActivity.this, "لطفا عدد معتبری را به عنوان امتیاز فعالیت وارد کنید.",Toast.LENGTH_LONG).show();
            return;
        }
        Activity activity = new Activity(title, points);
        dataSource.open();
        dataSource.addActivity(activity);
        dataSource.close();
        AddActivityActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
