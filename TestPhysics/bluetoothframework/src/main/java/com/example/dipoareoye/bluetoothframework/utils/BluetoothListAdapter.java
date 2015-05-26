package com.example.dipoareoye.bluetoothframework.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dipoareoye.bluetoothframework.R;

import java.util.List;

/**
 * Created by dipoareoye on 18/04/15.
 */
public class BluetoothListAdapter extends ArrayAdapter<BluetoothDevice> {

    private final static String TAG = "BT_ADT";

    private List<BluetoothDevice> items;
    private Context mContext;

    /**
     * @param context
     * @param textViewResourceId
     * @param objects
     */
    public BluetoothListAdapter(Context context, int textViewResourceId,
                               List<BluetoothDevice> objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        items = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_bt_devices, null);
        }
        BluetoothDevice device = items.get(position);

        if (device != null) {

            TextView top = (TextView) v.findViewById(R.id.device_name);
            TextView bottom = (TextView) v.findViewById(R.id.device_details);

            if (top != null) {
                top.setText(device.getName());
            }
            if (bottom != null) {
                bottom.setText(device.getAddress());
            }
            Log.d(TAG, "BluetoothListAdapter : getView : " + device.getName());
        }

        Log.d(TAG,"get view called");

        return v;
    }


}
