package it.sudchiamanord.quizontheroad.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.sudchiamanord.quizontheroad.R;
import it.sudchiamanord.quizontheroad.stage.Stage;


public class StagesArrayAdapter extends ArrayAdapter<Stage>
{
    public StagesArrayAdapter(Context context)
    {
        super (context, R.layout.activity_single_stage);
    }

    public StagesArrayAdapter(Context context, List<Stage> objects)
    {
        super (context, R.layout.activity_single_stage, objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        Stage stageData = getItem (position);
        if (convertView == null) {
            convertView = LayoutInflater.from (getContext()).inflate (R.layout.stage_data_row, parent,
                    false);
        }

        TextView number = (TextView) convertView.findViewById (R.id.stageNumberTxt);
//        TextView location = (TextView) convertView.findViewById (R.id.stageLocationTxt);
        TextView status = (TextView) convertView.findViewById (R.id.stageStatusTxt);

        number.setText (getContext().getString (R.string.stageNumberTxt) + " " + (stageData.getNumber() + 1));
//        location.setText (location.getText() + " " + stageData.getLocation());
        status.setText (getContext().getString (R.string.stageStatusTxt) + " " +
                getContext().getString (stageData.getStatus().getAppValue()));

        return convertView;
    }
}
