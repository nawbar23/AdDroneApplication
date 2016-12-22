package com.addrone.connection;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.addrone.R;
import com.addrone.model.ConnectionInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nbar on 2016-09-08.
 */

public abstract class ConnectionsListAdapter extends ArrayAdapter<String> {

    private LayoutInflater layoutInflater;

    private Map<String, ConnectionInfo> connectionsMap;

    private String chosenRowValue;

    public ConnectionsListAdapter(Context context, int textViewResourceId, Map<String, ConnectionInfo> connectionsMap) {
        super(context, textViewResourceId, new ArrayList<>(connectionsMap.keySet()));
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.connectionsMap = connectionsMap;
    }

    public abstract void onEdit(String connectionInfoName);

    public abstract void onDelete(String connectionInfoName);

    public void setChosenRowValue(String chosenRowValue){
        this.chosenRowValue =chosenRowValue;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        chosenRowValue = getItem(position);
        notifyDataSetChanged();
    }

    public ConnectionInfo getChosenConnection() throws Exception{
        if (getCount() < 1) {
            throw new Exception("At least one connection must be defined to proceeded!");
        }
        ConnectionInfo connectionInfo = connectionsMap.get(chosenRowValue);
        if (connectionInfo == null) {
            throw new Exception("Bad connection set from list!");
        }
        return connectionInfo;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.connection_list_row, parent, false);
            Holder holder = new Holder();
            holder.name = (TextView) rowView.findViewById(R.id.connection_name);
            holder.description = (TextView) rowView.findViewById(R.id.connection_description);
            holder.edit = (Button) rowView.findViewById(R.id.connection_edit);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = ((ListView) v.getParent().getParent().getParent()).getPositionForView((View)v.getParent());
                    onEdit(getItem(position));
                }
            });
            holder.remove = (Button) rowView.findViewById(R.id.connection_remove);
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = ((ListView) v.getParent().getParent().getParent()).getPositionForView((View)v.getParent());
                    onDelete(getItem(position));
                }
            });
            rowView.setTag(holder);
        }

        String connectionInfoName = getItem(position);
        ConnectionInfo connectionInfo = connectionsMap.get(connectionInfoName); // access to proper item

        if (connectionInfoName != null && connectionInfo != null) {
            Holder holder = (Holder) rowView.getTag();
            holder.name.setText(connectionInfoName);
            holder.description.setText(connectionInfo.toString());

            if (connectionInfoName.equalsIgnoreCase(chosenRowValue)){
                rowView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.connection_chosen_color));
            } else {
                rowView.setBackgroundColor(Color.WHITE);
            }
        }

        return rowView;
    }

    private static class Holder {
        TextView name;
        TextView description;
        Button edit;
        Button remove;
    }
}
