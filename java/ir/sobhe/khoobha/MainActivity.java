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

            Button syncButton = (Button)findViewById(R.id.sync);
            syncButton.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ActivityDataSource(this);
        dataSource.open();


        Button addActivityButton = (Button)findViewById(R.id.AddActivity);


        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to adding new activity page
                Intent addActivityIntent = new Intent(MainActivity.this, AddActivityActivity.class);
                startActivity(addActivityIntent);
            }
        });

        Button childrenListButton = (Button)findViewById(R.id.AddChild);

        childrenListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to adding new child page
                Intent childrenListIntent = new Intent(MainActivity.this, ChildrenListActivity.class);
                startActivity(childrenListIntent);
            }
        });

        final Button syncBytton = (Button)findViewById(R.id.sync);
        syncBytton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = dataSource.database.rawQuery("select * from `group`", null);
                if (cursor.getCount() <= 0) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }

                syncBytton.setEnabled(false);
                Intent serviceIntent =  new Intent(MainActivity.this, SyncService.class);
                ComponentName name = startService(serviceIntent);
                registerReceiver(reciever, new IntentFilter(SyncService.NOTIFICATION));
            }
        });
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
                startActivity(recordIntent);

            }
        });
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
