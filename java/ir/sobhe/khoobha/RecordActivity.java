package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        childDataSource = new ChildDataSource(this);
        childDataSource.open();

        recordDataSource = new RecordDataSource(this);
        recordDataSource.open();

        Intent intent = getIntent();
        final long activityId = intent.getLongExtra("activityId", 0);
        c = Calendar.getInstance();
        date = df.format(c.getTime());

        updatableRecord = recordDataSource.getRecord(activityId, date);
        List<Child> childrenList = checkSelectedChildren(updatableRecord);
        ChildrenAdapter adapter = new ChildrenAdapter(this, childrenList.toArray(new Child[childrenList.size()]));
        final ListView childrenListView = (ListView)findViewById(R.id.childrenList);
        childrenListView.setAdapter(adapter);


        TextView txt_date = (TextView)findViewById(R.id.txt_date);
        txt_date.setText(date);

        Button btn_yesterday = (Button)findViewById(R.id.btn_yesterday);
        btn_yesterday.setOnClickListener(new View.OnClickListener() {
            TextView txt_date = (TextView)findViewById(R.id.txt_date);
            @Override
            public void onClick(View view) {
                c.add(c.DATE, -1);
                date = df.format(c.getTime());
                txt_date.setText(date);
                updatableRecord = recordDataSource.getRecord(activityId, date);
                List<Child> childrenList = checkSelectedChildren(updatableRecord);
                childrenListView.setAdapter(new ChildrenAdapter(RecordActivity.this, childrenList.toArray(new Child[childrenList.size()])));
            }
        });


        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> children = new ArrayList<String>();
                String childrenIds = "";
                for(int i= 0 ; i < childrenListView.getCount(); i++){
                    Child c = (Child)childrenListView.getItemAtPosition(i);
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
        });

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
