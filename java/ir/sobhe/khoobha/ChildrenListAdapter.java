package ir.sobhe.khoobha;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hadi on 14/6/14 AD.
 */
public class ChildrenListAdapter extends ArrayAdapter<Child> {

    private final Context context;
    private final Child[] values;

    public ChildrenListAdapter(Context context, Child[] values){
        super(context, R.layout.childrenlist_layout, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.childrenlist_layout, parent, false);
        final long childId = values[position].id;

        TextView childName = (TextView)rowView.findViewById(R.id.childrenList_name);
        childName.setText(values[position].name);

        Button editChildButton = (Button)rowView.findViewById(R.id.childrenList_btn_editChild);
        ImageView childImage = (ImageView)rowView.findViewById(R.id.childrenList_picture);
        try{
            Bitmap img = BitmapFactory.decodeFile(Child.DIR_PATH + values[position].imageName);
            childImage.setImageBitmap(img);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        editChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addChildIntent = new Intent(context, AddChildActivity.class);
                addChildIntent.putExtra("editChild", true);
                addChildIntent.putExtra("childId", childId);
                context.startActivity(addChildIntent);

            }
        });
        return rowView;

    }
}
