package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordActivity extends android.app.Activity {

    private ChildDataSource childDataSource;
    private RecordDataSource recordDataSource;

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

        List<Child> childrenList = childDataSource.getAllChildren();
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
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = df.format(c.getTime());
                java.sql.Date date = java.sql.Date.valueOf(dateString);
                Record record = new Record(activityId, childrenIds, children.size(), date);
                recordDataSource.addRecord(record);
                finish();

            }
        });

    }


}
