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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordActivity extends android.app.Activity {

    private ChildDataSource childDataSource;
    private RecordDataSource recordDataSource;
    private String date;
    private Calendar c;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private Record updatebleRecord;
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
        date = df.format(c.getTime());
        today = c.getTime();

        updatebleRecord = recordDataSource.getRecord(activityId, date);
        List<Child> childrenList = checkSelectedChildren(updatebleRecord);
        childrenView = (GridView)findViewById(R.id.childList);
        childrenView.setAdapter(new ChildrenAdapter(this, childrenList.toArray(new Child[childrenList.size()])));

        TextView txt_date = (TextView)findViewById(R.id.txt_date);
        txt_date.setText(getJalaliDate(today));

        final Button btn_yesterday = (Button)findViewById(R.id.btn_yesterday);
        final Button btn_nextDay = (Button)findViewById(R.id.btn_nextDay);
        btn_nextDay.setVisibility(View.INVISIBLE);

        btn_yesterday.setOnClickListener(new View.OnClickListener() {
            TextView txt_date = (TextView)findViewById(R.id.txt_date);
            @Override
            public void onClick(View view) {
                saveRecord();

                if(btn_nextDay.getVisibility() == View.INVISIBLE)
                    btn_nextDay.setVisibility(View.VISIBLE);
                c.add(c.DATE, -1);
                date = df.format(c.getTime());
                txt_date.setText(getJalaliDate(c.getTime()));
                updatebleRecord = recordDataSource.getRecord(activityId, date);
                List<Child> childrenList = checkSelectedChildren(updatebleRecord);
                childrenView.setAdapter(new ChildrenAdapter(RecordActivity.this, childrenList.toArray(new Child[childrenList.size()])));
            }
        });

        btn_nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecord();

                TextView txt_date = (TextView)findViewById(R.id.txt_date);
                c.add(c.DATE, +1);
                date = df.format(c.getTime());
                txt_date.setText(getJalaliDate(c.getTime()));
                if(today.equals(c.getTime()))
                    btn_nextDay.setVisibility(View.INVISIBLE);
                updatebleRecord = recordDataSource.getRecord(activityId, date);
                List<Child> childrenList = checkSelectedChildren(updatebleRecord);
                childrenView.setAdapter(new ChildrenAdapter(RecordActivity.this, childrenList.toArray(new Child[childrenList.size()])));
            }
        });
    }

    public String getJalaliDate(Date date) {
        JalaliDate jalali = new JalaliDate(date);
        String str = String.format("%s، %d %s", jalali.strWeekDay, jalali.date, jalali.strMonth);
        return str.replace("0", "۰").replace("1", "۱").replace("2", "۲").replace("3", "۳").replace("4", "۴").replace("5", "۵").replace("6", "۶").replace("7", "۷").replace("8", "۸").replace("9", "۹");
    }

    private void saveRecord() {
        ChildrenAdapter adapter = (ChildrenAdapter)childrenView.getAdapter();
        
        if (!adapter.isDataChanged)
            return;

        adapter.isDataChanged = false;
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
        if (updatebleRecord != null) {
            record.id = updatebleRecord.id;
            recordDataSource.updateRecord(record);
        } else
            recordDataSource.addRecord(record);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordDataSource.close();
        childDataSource.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveRecord();
    }

    private List<Child> checkSelectedChildren(Record record){
        List<Child> childrenList = childDataSource.getAllChildren();

        if (record != null)
            for(Child child : childrenList){
                if(record.child_list.contains(Long.toString(child.id)))
                    child.selected = true;
            }

        return childrenList;
    }
}
