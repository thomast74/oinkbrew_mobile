package info.vhowto.oinkbrewmobile.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.activities.DevicesActivity;
import info.vhowto.oinkbrewmobile.adapters.DeviceListAdapter;
import info.vhowto.oinkbrewmobile.domain.Device;
import info.vhowto.oinkbrewmobile.remote.BrewPiRequest;
import info.vhowto.oinkbrewmobile.remote.DeviceRequest;
import info.vhowto.oinkbrewmobile.remote.RequestArrayCallback;
import info.vhowto.oinkbrewmobile.remote.RequestObjectCallback;

public class DeviceListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private List<Device> devices;
    private String device_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        devices = new ArrayList<>();
        device_id = ((DevicesActivity)getActivity()).getDeviceId();

        DeviceListAdapter adapter = new DeviceListAdapter(this, devices);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

        fetchDevices();
    }

    private void fetchDevices() {
        devices.clear();
        DeviceRequest.getDevices(device_id, new RequestArrayCallback<Device>() {
            @Override
            public void onRequestSuccessful() {
                // will not be called
            }

            @Override
            public void onRequestSuccessful(ArrayList<Device> items) {
                devices.addAll(items);
                ((DeviceListAdapter)getListAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onRequestFailure(int statusCode, String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public Context getApplicationContext() {
                return getActivity().getApplicationContext();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        final Device item = devices.get(position);

        final EditText input = new EditText(this.getActivity());
        input.setText(item.name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.action_change_name);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.name = input.getText().toString();
                DeviceRequest.setName(item, new RequestObjectCallback<Device>() {
                    @Override
                    public void onRequestSuccessful() {
                        Toast.makeText(getActivity(), "Device name updated", Toast.LENGTH_LONG).show();
                        fetchDevices();
                    }

                    @Override
                    public void onRequestSuccessful(Device item) {
                        // will not be called
                    }

                    @Override
                    public void onRequestFailure(int statusCode, String errorMessage) {
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public Context getApplicationContext() {
                        return getActivity().getApplicationContext();
                    }
                });
            }
        });
        builder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
