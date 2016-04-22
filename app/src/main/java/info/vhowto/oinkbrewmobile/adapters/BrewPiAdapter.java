package info.vhowto.oinkbrewmobile.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.BrewPi;

public class BrewPiAdapter extends RecyclerView.Adapter<BrewPiAdapter.BrewPiViewHolder> {

    private ArrayList<BrewPi> brewpis;

    public static class BrewPiViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView device_id;
        TextView firmware_version;
        TextView system_version;
        TextView spark_version;

        BrewPiViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.brewpi_card_view);
            name = (TextView)itemView.findViewById(R.id.brewpi_name);
            device_id = (TextView)itemView.findViewById(R.id.brewpi_device_id);
            firmware_version = (TextView)itemView.findViewById(R.id.brewpi_firmware_version);
            system_version = (TextView)itemView.findViewById(R.id.brewpi_system_version);
            spark_version = (TextView)itemView.findViewById(R.id.brewpi_spark_version);
        }
    }

    public BrewPiAdapter(ArrayList<BrewPi> brewpis) {
        this.brewpis = brewpis;
    }

    @Override
    public int getItemCount() {
        return brewpis.size();
    }

    @Override
    public BrewPiViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_brewpi_list, viewGroup, false);

        return new BrewPiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BrewPiViewHolder viewHolder, int i) {
        viewHolder.name.setText(brewpis.get(i).name);
        viewHolder.device_id.setText(brewpis.get(i).device_id);
        viewHolder.firmware_version.setText(brewpis.get(i).firmware_version);
        viewHolder.system_version.setText(brewpis.get(i).system_version);
        viewHolder.spark_version.setText(brewpis.get(i).spark_version);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
