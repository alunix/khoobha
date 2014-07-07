package ir.sobhe.khoobha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by hadi on 14/7/6 AD.
 */
public class GroupAdapter extends ArrayAdapter<Group> {
    private Context context;
    private Group[] values;

    public GroupAdapter(Context c, Group[] v){
        super(c,R.id.lst_groups, v);
        values = v;
        context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.group_item, parent, false);

        TextView itemName = (TextView)rowView.findViewById(R.id.txt_groupItem);
        itemName.setText(values[position].groupTitle);

        return rowView;
    }
}
