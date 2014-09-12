package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SolitaryRecordActivity extends android.app.Activity {

    private ChildDataSource childDataSource;
    private RecordDataSource recordDataSource;
    private long activityId;
    private GridView childrenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitary_record);

        childDataSource = new ChildDataSource(this);
        childDataSource.open();

        recordDataSource = new RecordDataSource(this);
        recordDataSource.open();

        Intent intent = getIntent();
        if (android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setTitle(intent.getStringExtra("activityTitle"));

        activityId = intent.getLongExtra("activityId", 0);

        List<Child> childrenList = childDataSource.getAllChildren();
        childrenView = (GridView)findViewById(R.id.childList);
        childrenView.setAdapter(new ChildrenAdapter(this, childrenList.toArray(new Child[childrenList.size()]), R.layout.solitary_record_item));
        childrenView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Child child = (Child)adapterView.getItemAtPosition(i);
                Record record = new Record(activityId,Long.toString(child.id), 1, Calendar.getInstance().getTime().toString());
                recordDataSource.addRecord(record);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.solitary_record, menu);
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
