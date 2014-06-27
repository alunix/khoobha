package ir.sobhe.khoobha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hadi on 14/6/3 AD.
 */
public class ChildrenAdapter extends ArrayAdapter<Child> {

    private final Context context;
    private final Child[] values;

    public ChildrenAdapter(Context context, Child[] values){
        super(context, R.layout.record_item, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView image;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        if(convertView == null){
            LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.record_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView)rowView.findViewById(R.id.childName);
            viewHolder.image = (ImageView)rowView.findViewById(R.id.childPicture);
            viewHolder.checkbox = (CheckBox)rowView.findViewById(R.id.check);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Child element = (Child)viewHolder.checkbox.getTag();
                    element.selected = buttonView.isChecked();
                }
            });
            rowView.setTag(viewHolder);
            viewHolder.checkbox.setTag(values[position]);
        }else{
            rowView = convertView;
            ((ViewHolder)rowView.getTag()).checkbox.setTag(values[position]);
        }

        ViewHolder holder = (ViewHolder)rowView.getTag();
        holder.text.setText(values[position].name.split(" ")[0]);
        try {
            Bitmap img = BitmapFactory.decodeFile(Child.DIR_PATH + values[position].imageName);
            holder.image.setImageBitmap(img);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        holder.checkbox.setChecked(values[position].selected);

        return rowView;

    }
}
