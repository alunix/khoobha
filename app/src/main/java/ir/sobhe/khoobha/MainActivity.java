package ir.sobhe.khoobha;

import android.app.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends android.app.Activity {

    private ActivityDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataSource = new ActivityDataSource(this);
        dataSource.open();

        List<Activity> activities = new ArrayList<Activity>();

        try{
            activities = dataSource.getAllActivities();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }


}
