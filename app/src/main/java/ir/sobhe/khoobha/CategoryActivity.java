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
    CategoryDataSource dataSource;
    MenuItem addActivityItem;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        dataSource = new CategoryDataSource(this);
        dataSource.open();

        category = dataSource.getCategory(getIntent().getIntExtra("categoryId", -1));

        if (android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setTitle(getIntent().getStringExtra("categoryTitle"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Activity> activities = new ArrayList<Activity>();
        try {
            activities = dataSource.getCategoryActivities(category.id);
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
                Intent recordIntent;
                if(category.solitary){
                    try {
                        recordIntent = new Intent(CategoryActivity.this, SolitaryRecordActivity.class);
                        recordIntent.putExtra("activityId", activity.id);
                        recordIntent.putExtra("activityTitle", activity.title);
                        startActivity(recordIntent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    recordIntent = new Intent(CategoryActivity.this, RecordActivity.class);
                    recordIntent.putExtra("activityId", activity.id);
                    recordIntent.putExtra("activityTitle", activity.title);
                    startActivity(recordIntent);
                }
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
                addActivityIntent.putExtra("categoryId", category.id);
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
