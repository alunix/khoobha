package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordActivity extends android.app.Activity {

    private ChildDataSource childDataSource;
    private RecordDataSource recordDataSource;
    private String date;
    private boolean isupdate;
    private Calendar c;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private Record updatableRecord;
    private Date today;
    private long activityId;
    private GridView childrenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);

        childDataSource = new ChildDataSource(this);
        childDataSource.open();

        recordDataSource = new RecordDataSource(this);
        recordDataSource.open();

        Intent intent = getIntent();
        getActionBar().setTitle(intent.getStringExtra("activityTitle"));
        activityId = intent.getLongExtra("activityId", 0);
        c = Calendar.getInstance();
        ShamsiCalendar shamsiCalendar = new ShamsiCalendar(c.getTime());
        date = df.format(c.getTime());
        String shamsiDate = String.format("%s %d %s",shamsiCalendar.strWeekDay,shamsiCalendar.date, shamsiCalendar.strMonth);
        today = c.getTime();

        updatableRecord = recordDataSource.getRecord(activityId, date);
        List<Child> childrenList = checkSelectedChildren(updatableRecord);
        ChildrenAdapter adapter = new ChildrenAdapter(this, childrenList.toArray(new Child[childrenList.size()]));
        childrenView = (GridView)findViewById(R.id.childrenList);
        childrenView.setAdapter(adapter);

        TextView txt_date = (TextView)findViewById(R.id.txt_date);
        txt_date.setText(shamsiDate);

        final Button btn_yesterday = (Button)findViewById(R.id.btn_yesterday);
        final Button btn_nextDay = (Button)findViewById(R.id.btn_nextDay);
        btn_nextDay.setVisibility(View.INVISIBLE);

        btn_yesterday.setOnClickListener(new View.OnClickListener() {
            TextView txt_date = (TextView)findViewById(R.id.txt_date);
            @Override
            public void onClick(View view) {
                if(btn_nextDay.getVisibility() == View.INVISIBLE)
                    btn_nextDay.setVisibility(View.VISIBLE);
                c.add(c.DATE, -1);
                date = df.format(c.getTime());
                ShamsiCalendar shamsiCalendar = new ShamsiCalendar(c.getTime());
                String shamsiDate = String.format("%s، %d %s",shamsiCalendar.strWeekDay,shamsiCalendar.date, shamsiCalendar.strMonth);
                txt_date.setText(shamsiDate);
                updatableRecord = recordDataSource.getRecord(activityId, date);
                if(updatableRecord == null)
                    isupdate = false;
                List<Child> childrenList = checkSelectedChildren(updatableRecord);
                childrenView.setAdapter(new ChildrenAdapter(RecordActivity.this, childrenList.toArray(new Child[childrenList.size()])));
            }
        });

        btn_nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txt_date = (TextView)findViewById(R.id.txt_date);
                c.add(c.DATE, +1);
                date = df.format(c.getTime());
                ShamsiCalendar shamsiCalendar = new ShamsiCalendar(c.getTime());
                String shamsiDate = String.format("%s %d %s",shamsiCalendar.strWeekDay,shamsiCalendar.date, shamsiCalendar.strMonth);
                txt_date.setText(shamsiDate);
                if(today.equals(c.getTime()))
                    btn_nextDay.setVisibility(View.INVISIBLE);
                updatableRecord = recordDataSource.getRecord(activityId, date);
                if(updatableRecord == null)
                    isupdate = false;
                List<Child> childrenList = checkSelectedChildren(updatableRecord);
                childrenView.setAdapter(new ChildrenAdapter(RecordActivity.this, childrenList.toArray(new Child[childrenList.size()])));
            }
        });
    }

    private void saveRecord() {
        List<String> children = new ArrayList<String>();
        String childrenIds = "";
        for(int i= 0 ; i < childrenView.getCount(); i++){
            Child c = (Child) childrenView.getItemAtPosition(i);
            if(c.selected){
                children.add(Long.toString(c.id));
                if (!childrenIds.isEmpty())
                    childrenIds += ",";
                childrenIds += Long.toString(c.id);
            }
        }
        Record record = new Record(activityId, childrenIds, children.size(), date);
        if(isupdate == true){
            record.id = updatableRecord.id;
            recordDataSource.updateRecord(record);
        }
        else{
            recordDataSource.addRecord(record);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordDataSource.close();
        childDataSource.close();
    }

    private List<Child> checkSelectedChildren(Record newRecord){
        List<Child> childrenList = childDataSource.getAllChildren();
        if(newRecord != null){
            isupdate = true;
            for(Child child : childrenList){
                if(newRecord.child_list.contains(Long.toString(child.id)))
                    child.selected = true;
            }
        }
        return childrenList;
    }
}
