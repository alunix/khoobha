package ir.sobhe.khoobha;

import android.app.*;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class ChildrenListActivity extends android.app.Activity {

    private ChildDataSource childDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        childDataSource = new ChildDataSource(this);
        childDataSource.open();


        Button addChild = (Button)findViewById(R.id.childrenList_btn_addChild);

        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChildrenListActivity.this, AddChildActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Child> childList = childDataSource.getAllChildren();
        ChildrenListAdapter adapter = new ChildrenListAdapter(this, childList.toArray(new Child[childList.size()]));
        ListView childrenListView = (ListView)findViewById(R.id.childrenList);
        childrenListView.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        childDataSource.close();
    }
}
