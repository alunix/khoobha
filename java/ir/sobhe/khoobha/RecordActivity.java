package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        date = df.format(c.getTime());


        List<Child> childrenList = childDataSource.getAllChildren();
        final Record newRecord = recordDataSource.getRecord(activityId, date);
        if(newRecord != null){
            isupdate = true;
            for(Child child : childrenList){
                if(newRecord.child_list.contains(Long.toString(child.id)))
                    child.selected = true;
            }
        }

        ChildrenAdapter adapter = new ChildrenAdapter(this, childrenList.toArray(new Child[childrenList.size()]));
        final ListView childrenListView = (ListView)findViewById(R.id.childrenList);
        childrenListView.setAdapter(adapter);

        Button btn_ok = (Button)findViewById(R.id.btn_ok);



        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> children = new ArrayList<String>();
                for(int i= 0 ; i < childrenListView.getCount(); i++){
                    Child c = (Child)childrenListView.getItemAtPosition(i);
                    if(c.selected){
                        children.add(Long.toString(c.id));
                    }
                }
                String childrenIds = children.toString();
                Record record = new Record(activityId, childrenIds, children.size(), date);
                if(isupdate == true){
                    record.id = newRecord.id;
                    recordDataSource.updateRecord(record);
                }
                else{
                    recordDataSource.addRecord(record);
                }
                finish();

            }
        });

    }
}
