package info.vhowto.oinkbrewmobile.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.BrewPi;

public class BrewPiListAdapter extends RecyclerView.Adapter<BrewPiListAdapter.BrewPiViewHolder> {

    private Context context;
    private ArrayList<BrewPi> brewpis;
    private ItemListener itemListener;

    public static class BrewPiViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView menu;
        TextView name;
        TextView device_id;
        TextView firmware_version;
        TextView system_version;
        TextView spark_version;

        BrewPiViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.brewpi_card_view);
            menu = (ImageView)itemView.findViewById(R.id.brewpi_overflow);
            name = (TextView)itemView.findViewById(R.id.brewpi_name);
            device_id = (TextView)itemView.findViewById(R.id.brewpi_device_id);
            firmware_version = (TextView)itemView.findViewById(R.id.brewpi_firmware_version);
            system_version = (TextView)itemView.findViewById(R.id.brewpi_system_version);
            spark_version = (TextView)itemView.findViewById(R.id.brewpi_spark_version);
        }

        public void bind(final BrewPi item, final ItemListener listener) {
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
        void onMenuItemClicked(BrewPi item, MenuItem menuItem);
        void onItemClick(BrewPi item);
    }

    public BrewPiListAdapter(ArrayList<BrewPi> brewpis) {
        this.brewpis = brewpis;
    }

    @Override
    public int getItemCount() {
        return brewpis.size();
    }

    @Override
    public BrewPiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_brewpi_list, viewGroup, false);

        return new BrewPiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BrewPiViewHolder viewHolder, int i) {
        final int position = i;

        viewHolder.bind(brewpis.get(i), itemListener);

        viewHolder.name.setText(brewpis.get(position).name);
        viewHolder.device_id.setText(brewpis.get(position).device_id);
        viewHolder.firmware_version.setText(brewpis.get(position).firmware_version);
        viewHolder.system_version.setText(brewpis.get(position).system_version);
        viewHolder.spark_version.setText(brewpis.get(position).spark_version);

        final PopupMenu popup = new PopupMenu(context, viewHolder.menu);
        popup.inflate(R.menu.menu_brewpi_card);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (itemListener != null) {
                    itemListener.onMenuItemClicked(brewpis.get(position), item);
                }
                return true;
            }
        });
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setItemListener(@Nullable ItemListener l) {
        this.itemListener = l;
    }
}
