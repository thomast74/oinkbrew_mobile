package info.vhowto.oinkbrewmobile.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.ListFragment;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Device;
import info.vhowto.oinkbrewmobile.domain.DeviceType;

public class DeviceListAdapter extends BaseAdapter {

    private ListFragment fragment;
    private LayoutInflater inflater;
    private List<Device> devices;


    private Drawable actuator;
    private Drawable onewire_temp;

    static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView pin_nr;
        public TextView hw_address;
        public TextView configuration;
    }

    public DeviceListAdapter(ListFragment fragment, List<Device> devices) {
        this.fragment = fragment;
        this.devices = devices;

        actuator = new IconicsDrawable(this.fragment.getActivity().getApplicationContext())
                .icon(CommunityMaterial.Icon.cmd_toggle_switch)
                .color(this.fragment.getActivity().getResources().getColor(R.color.material_orange_500, null))
                .sizeDp(32);

        onewire_temp = new IconicsDrawable(this.fragment.getActivity().getApplicationContext())
                .icon(CommunityMaterial.Icon.cmd_thermometer)
                .color(this.fragment.getActivity().getResources().getColor(R.color.material_orange_500, null))
                .sizeDp(32);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int location) {
        return devices.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_device_list, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.device_type_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.pin_nr = (TextView) convertView.findViewById(R.id.device_pin_nr);
            viewHolder.hw_address = (TextView) convertView.findViewById(R.id.device_hwaddress);
            viewHolder.configuration = (TextView) convertView.findViewById(R.id.device_configuration);
            convertView.setTag(viewHolder);
        }

        Device device = devices.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.image.setImageDrawable(DeviceType.ACTUATOR == device.device_type ? actuator : onewire_temp);
        holder.name.setText(device.name == null ? "< Not Set >" : device.name);
        holder.pin_nr.setText(String.valueOf(device.pin_nr));
        holder.hw_address.setText(device.hw_address);
        holder.configuration.setText(device.configuration.pk == 0 ? "Not used" : device.configuration.name);

        return convertView;
    }

}
