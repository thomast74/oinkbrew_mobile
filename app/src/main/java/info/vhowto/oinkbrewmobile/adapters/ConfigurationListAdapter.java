package info.vhowto.oinkbrewmobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.List;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.domain.ConfigurationType;

public class ConfigurationListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Configuration> configurations;

    private Drawable beerBrew;
    private Drawable beerFermentation;

    static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView brewpi;
        public TextView date;
    }


    public ConfigurationListAdapter(Activity activity, List<Configuration> configurations) {
        this.activity = activity;
        this.configurations = configurations;

        beerBrew = new IconicsDrawable(this.activity.getApplicationContext())
                .icon(CommunityMaterial.Icon.cmd_glass_mug)
                .color(this.activity.getResources().getColor(R.color.material_orange_500, null))
                .sizeDp(32);

        beerFermentation = new IconicsDrawable(this.activity.getApplicationContext())
                .icon(CommunityMaterial.Icon.cmd_fridge)
                .color(this.activity.getResources().getColor(R.color.material_orange_500, null))
                .sizeDp(32);
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

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_configuration_list, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.configuration_type_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.configuration_name);
            viewHolder.brewpi = (TextView) convertView.findViewById(R.id.configuration_brewpi);
            viewHolder.date = (TextView) convertView.findViewById(R.id.configuration_create_date);
            convertView.setTag(viewHolder);
        }

        Configuration configuration = configurations.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.image.setImageDrawable(ConfigurationType.BREW.equals(configuration.type) ? beerBrew : beerFermentation);
        holder.name.setText(String.valueOf(configuration.name));
        holder.brewpi.setText("Running on: " + String.valueOf(configuration.brewpi.name));
        holder.date.setText(new SimpleDateFormat("'Created on' EEE dd MMM yyyy 'at' HH:mm").format(configuration.create_date));

        return convertView;
    }
}
