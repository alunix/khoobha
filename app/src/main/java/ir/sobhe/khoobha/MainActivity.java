package ir.sobhe.khoobha;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends android.app.Activity {

    private ActivityDataSource dataSource;

    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                int result = bundle.getInt("result");
                if(result == RESULT_OK)
                    Toast.makeText(MainActivity.this, "به روز رسانی با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                else if(result == SyncService.RESULT_ERROR)
                    Toast.makeText(MainActivity.this, "خطا در هنگام به روز رسانی", Toast.LENGTH_LONG).show();
            }

            setProgressBarIndeterminateVisibility(false);
            // todo: visible sync menu
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/bmitra.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        getActionBar().setTitle("خوب‌ها");

        dataSource = new ActivityDataSource(this);
        dataSource.open();

        setProgressBarIndeterminateVisibility(false);

        Button addActivityButton = (Button) findViewById(R.id.AddActivity);
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivityActivity.class));
            }
        });
    }

    private void syncData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyRegistered = prefs.getBoolean(getString(R.string.pref_previously_registered), false);
        if(!previouslyRegistered){
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 201);
        }
        else
        {
            startService(new Intent(MainActivity.this, SyncService.class));
            registerReceiver(reciever, new IntentFilter(SyncService.NOTIFICATION));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 201){
            if(resultCode == 200){
                startService(new Intent(MainActivity.this, SyncService.class));
                registerReceiver(reciever, new IntentFilter(SyncService.NOTIFICATION));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Activity> activities = new ArrayList<Activity>();

        try{
            activities = dataSource.getAllActivities();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ActivitiesAdapter adapter = new ActivitiesAdapter(this, activities.toArray(new Activity[activities.size()]));
        final ListView listView = (ListView)findViewById(R.id.ActivitiesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //go to scoring page for selected activity
                Intent recordIntent = new Intent(MainActivity.this, RecordActivity.class);
                recordIntent.putExtra("activityId", ((Activity)listView.getItemAtPosition(position)).id);
                recordIntent.putExtra("activityTitle", ((Activity)listView.getItemAtPosition(position)).title);
                startActivity(recordIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_children:
                startActivity(new Intent(MainActivity.this, ChildrenListActivity.class));
                return true;
            case R.id.action_sync:
                item.setVisible(false);
                setProgressBarIndeterminateVisibility(true);
                syncData();
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
