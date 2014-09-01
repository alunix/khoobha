package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ActivitiesActivity extends android.app.Activity {
    private ActivityDataSource dataSource;
    private MenuItem addActivityItem;
    private int category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/app.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main_activity);
        dataSource = new ActivityDataSource(this);
        dataSource.open();
        setProgressBarIndeterminateVisibility(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        List<Activity> activities = new ArrayList<Activity>();
        try {
            Intent intent = getIntent();
            category_id = intent.getIntExtra("categoryId", -1);
            activities = dataSource.getCategoryActivities(category_id);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
// add new activity button
        activities.add(new Activity("+ فعالیت جدید", 0));
        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities.toArray(new Activity[activities.size()]));
        final ListView listView = (ListView)findViewById(R.id.ActivitiesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Activity activity = ((Activity)listView.getItemAtPosition(position));
                Intent recordIntent = new Intent(ActivitiesActivity.this, RecordActivity.class);
                recordIntent.putExtra("activityId", activity.id);
                recordIntent.putExtra("activityTitle", activity.title);
                startActivity(recordIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activities, menu);
        addActivityItem = menu.findItem(R.id.action_add);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent addActivityIntent = new Intent(ActivitiesActivity.this, AddActivityActivity.class);
                addActivityIntent.putExtra("categoryId", category_id);
                startActivity(addActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
//unregisterReceiver(reciever);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
