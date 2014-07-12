package ir.sobhe.khoobha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.List;

public class ChildrenListActivity extends android.app.Activity {

    private ChildDataSource childDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_list_activity);

        childDataSource = new ChildDataSource(this);
        childDataSource.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:
                startActivity(new Intent(ChildrenListActivity.this, AddChildActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Child> childList = childDataSource.getAllChildren();
        ChildrenListAdapter adapter = new ChildrenListAdapter(this, childList.toArray(new Child[childList.size()]));
        GridView childrenView = (GridView)findViewById(R.id.childList);
        childrenView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        childDataSource.close();
    }
}
