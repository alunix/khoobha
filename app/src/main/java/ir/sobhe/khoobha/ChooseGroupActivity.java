package ir.sobhe.khoobha;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseGroupActivity extends android.app.Activity {

    private Button btn_chooseGroup;
    private ListView lst_groups;
    private EditText txt_groupName;
    private TextView txt_message;
    private GroupDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);

        btn_chooseGroup = (Button)findViewById(R.id.btn_chooseGroup);
        lst_groups = (ListView)findViewById(R.id.lst_groups);
        txt_groupName = (EditText)findViewById(R.id.txt_goupName);
        txt_message = (TextView)findViewById(R.id.txt_message);
        dataSource = new GroupDataSource(this);
        dataSource.open();

        String existingGroups = this.getIntent().getStringExtra("existingGroups");
        final String email = this.getIntent().getStringExtra("email");
        final String password = this.getIntent().getStringExtra("password");

        List<Group> groupList = new ArrayList<Group>();
        try{
            JSONObject jsonObj = new JSONObject(existingGroups);
            JSONArray groupsArray = jsonObj.getJSONArray("groups");
            for(int i=0 ; i < groupsArray.length(); i++){
                JSONObject groupObj = groupsArray.getJSONObject(i);
                groupList.add(new Group(groupObj.getInt("id"), groupObj.getString("title"), email, password));
            }
            if(groupList.size() == 0){
                lst_groups.setVisibility(View.INVISIBLE);
                txt_message.setVisibility(View.VISIBLE);
            }

            GroupAdapter adapter = new GroupAdapter(this, groupList.toArray(new Group[groupList.size()]));
            lst_groups.setAdapter(adapter);
        }
        catch (Exception exc){
            exc.printStackTrace();
        }

        lst_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Group selectedGroup = (Group)(lst_groups.getItemAtPosition(position));
                dataSource.addGroup(selectedGroup);
                setResult(200);
                finish();
            }
        });

        btn_chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txt_groupName.length() == 0){
                    Toast.makeText(ChooseGroupActivity.this, "لطفا نام مجموعه جدید را وارد نمایید.", Toast.LENGTH_LONG).show();
                }
                else{
                    //api call to add group and get id from server
                    String title = txt_groupName.getText().toString();
                    final String ADDRESS = "http://khoobha.net/api/register";
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(ADDRESS);
                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("email", email));
                    pairs.add(new BasicNameValuePair("password", password));
                    pairs.add(new BasicNameValuePair("title", title));

                    try{
                        post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
                        HttpResponse response = client.execute(post);
                        int responseCode = response.getStatusLine().getStatusCode();
                        String result = "";
                        if(responseCode == 200){
                            result = EntityUtils.toString(response.getEntity());
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");
                            if(status.equalsIgnoreCase("success")){
                                int group = jsonObject.getInt("group");
                                dataSource.addGroup(new Group(group, title, email,password));
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putBoolean(getString(R.string.pref_previously_registered), Boolean.TRUE);
                                edit.putInt("groupId", group);
                                edit.commit();
                                setResult(200);
                                finish();
                            }
                            else{
                                Toast.makeText(ChooseGroupActivity.this, "خطا در ارتباط با سرور...", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }


}
