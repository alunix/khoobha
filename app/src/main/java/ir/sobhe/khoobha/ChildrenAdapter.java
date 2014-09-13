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
    public boolean isDataChanged = false;
    public int layout;

    public ChildrenAdapter(Context context, Child[] values, int layout_item){
        super(context, layout_item, values);
        layout = layout_item;
        this.context = context;
        this.values = values;
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView image;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = null;
        if(convertView == null){
            LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layout, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView)rowView.findViewById(R.id.childName);
            viewHolder.image = (ImageView)rowView.findViewById(R.id.childPicture);
            rowView.setTag(viewHolder);
            if(layout == R.layout.record_item){
                viewHolder.checkbox = (CheckBox)rowView.findViewById(R.id.check);
                viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Child element = (Child)viewHolder.checkbox.getTag();
                        if(!element.selected || !buttonView.isChecked())
                            isDataChanged = true;
                        element.selected = buttonView.isChecked();
                    }
                });
                viewHolder.checkbox.setTag(values[position]);
            }
        }else{
            rowView = convertView;
            if(layout == R.layout.record_item)
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
        if(layout == R.layout.record_item)
        holder.checkbox.setChecked(values[position].selected);

        return rowView;

    }
}
