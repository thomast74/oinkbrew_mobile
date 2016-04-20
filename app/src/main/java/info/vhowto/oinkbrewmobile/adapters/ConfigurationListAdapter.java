package info.vhowto.oinkbrewmobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;

public class ConfigurationListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Configuration> configurations;

    public ConfigurationListAdapter(Activity activity, List<Configuration> configurations) {
        this.activity = activity;
        this.configurations = configurations;
    }

    @Override
    public int getCount() {
        return configurations.size();
    }

    @Override
    public Object getItem(int location) {
        return configurations.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_configuration_list, null);

        TextView name = (TextView) convertView.findViewById(R.id.configuration_name);
        TextView createDate = (TextView) convertView.findViewById(R.id.configuration_create_date);

        name.setText(String.valueOf(configurations.get(position).getName()));
        createDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(configurations.get(position).getCreateDate()));

        return convertView;
    }
}
