package ir.sobhe.khoobha;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ActivityDataSource(this);
        dataSource.open();

        final Intent serviceIntent =  new Intent(this, SyncService.class);


        Button addActivityButton = (Button)findViewById(R.id.AddActivity);


        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to adding new activity page
                Intent addActivityIntent = new Intent(MainActivity.this, AddActivityActivity.class);
                startActivity(addActivityIntent);
            }
        });

        Button addChildButton = (Button)findViewById(R.id.AddChild);

        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to adding new child page
                Intent addChildIntent = new Intent(MainActivity.this, AddChildActivity.class);
                startActivity(addChildIntent);
            }
        });

        Button syncBytton = (Button)findViewById(R.id.sync);
        syncBytton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
                if(!previouslyStarted){

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }

                startService(serviceIntent);
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
        ListView listView = (ListView)findViewById(R.id.ActivitiesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //go to scoring page for selected activity
            }
        });
    }
}
