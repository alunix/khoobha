package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class CategoryActivity extends android.app.Activity {
    private ActivityDataSource dataSource;
    private MenuItem addActivityItem;
    private int category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        dataSource = new ActivityDataSource(this);
        dataSource.open();

        Intent intent = getIntent();
        if (android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setTitle(intent.getStringExtra("categoryTitle"));
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

        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities.toArray(new Activity[activities.size()]));
        final ListView listView = (ListView)findViewById(R.id.ActivitiesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Activity activity = ((Activity)listView.getItemAtPosition(position));
                Intent recordIntent = new Intent(CategoryActivity.this, RecordActivity.class);
                recordIntent.putExtra("activityId", activity.id);
                recordIntent.putExtra("activityTitle", activity.title);
                startActivity(recordIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category, menu);
        addActivityItem = menu.findItem(R.id.action_add);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent addActivityIntent = new Intent(CategoryActivity.this, AddActivityActivity.class);
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
