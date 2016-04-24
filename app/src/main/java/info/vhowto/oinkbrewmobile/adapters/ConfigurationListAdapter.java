package info.vhowto.oinkbrewmobile.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.domain.ConfigurationType;

public class ConfigurationListAdapter extends RecyclerView.Adapter<ConfigurationListAdapter.ConfigurationViewHolder> {

    private ArrayList<Configuration> configurations;
    private ItemListener itemListener;

    public static class ConfigurationViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView thumbnail;
        TextView name;
        TextView brewpi;
        TextView create_date;
        Drawable brewDrawable;
        Drawable fermentationDrawable;

        ConfigurationViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.configuration_card_view);
            thumbnail = (ImageView)itemView.findViewById(R.id.configuration_thumbnail);
            name = (TextView)itemView.findViewById(R.id.configuration_name);
            brewpi = (TextView)itemView.findViewById(R.id.configuration_brewpi);
            create_date = (TextView)itemView.findViewById(R.id.configuration_create_date);

            brewDrawable = itemView.getContext().getDrawable(R.drawable.config_beer_card);
            fermentationDrawable = itemView.getContext().getDrawable(R.drawable.config_fermentation_card);
        }

        public void bind(final Configuration item, final ItemListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                }
            });
        }
    }

    public interface ItemListener {
        void onItemClick(Configuration item);
    }

    public ConfigurationListAdapter(ArrayList<Configuration> configurations) {
        this.configurations = configurations;
    }

    @Override
    public int getItemCount() {
        return configurations.size();
    }

    @Override
    public ConfigurationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_configuration_list, viewGroup, false);

        return new ConfigurationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ConfigurationViewHolder viewHolder, int i) {
        final int position = i;

        viewHolder.bind(configurations.get(i), itemListener);

        viewHolder.thumbnail.setImageDrawable(ConfigurationType.BREW.equals(configurations.get(i).type) ? viewHolder.brewDrawable : viewHolder.fermentationDrawable);
        viewHolder.name.setText(configurations.get(position).name);
        viewHolder.brewpi.setText(configurations.get(position).brewpi.name);
        viewHolder.create_date.setText(new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm").format(configurations.get(i).create_date));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setItemListener(@Nullable ItemListener l) {
        this.itemListener = l;
    }
}
