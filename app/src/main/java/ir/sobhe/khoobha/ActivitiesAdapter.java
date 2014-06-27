package ir.sobhe.khoobha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by hadi on 14/5/8 AD.
 */
public class ActivitiesAdapter  extends ArrayAdapter<Activity>{

    private final Context context;
    private final Activity[] values;

    public ActivitiesAdapter(Context context, Activity[] values){
        super(context, R.layout.activity_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_item, parent, false);
        TextView title = (TextView)rowView.findViewById(R.id.title);
        title.setText(values[position].title);

        if (values[position].id == -1)
            title.setCompoundDrawables(null, null, null, null);

        return rowView;
    }
}
