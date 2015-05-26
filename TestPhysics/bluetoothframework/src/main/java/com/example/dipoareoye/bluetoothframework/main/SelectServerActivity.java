package com.example.dipoareoye.bluetoothframework.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dipoareoye.bluetoothframework.R;
import com.example.dipoareoye.bluetoothframework.utils.BluetoothListAdapter;
import com.example.dipoareoye.bluetoothframework.utils.StartDiscoverableModeActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.dipoareoye.bluetoothframework.utils.Const.EXTRA_BLUETOOTH_ADDRESS;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_REGISTER_ACTIVITY;


public class SelectServerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = "BT_ServAct";

    private BluetoothAdapter mBtAdapter;
    private SelectServerActivity selectServerActivity;
    private BluetoothListAdapter mListAdapter;
    private ListView mListView;
    private List<BluetoothDevice> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server);

        Log.d(TAG, "init activity");

        initialise();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBtAdapter.isEnabled()) {

            Intent intent = new Intent();
            intent.setClass(this, StartDiscoverableModeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        if(!mBtAdapter.isDiscovering()) {

            Log.d(TAG,"onCreate : starting discovery ");
            mBtAdapter.startDiscovery();

        }
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onPause: chat activity closed, de-register activity from connection service !");
        mBtAdapter.startDiscovery();
        registerActivityToService(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: chat activity closed, de-register activity from connection service !");
        registerActivityToService(false);
        mBtAdapter.cancelDiscovery();

    }

    private void initialise(){

        selectServerActivity = this;
        mDeviceList = new ArrayList<>();

        mListAdapter = new BluetoothListAdapter(selectServerActivity, R.layout.row_bt_devices,mDeviceList);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);

    }

    public void onDeviceDetected(BluetoothDevice device){

       Log.d(TAG , "onDeviceDetected: " + device.getName());

       mDeviceList.add(device);
       mListAdapter.notifyDataSetChanged();

    }

    private void registerActivityToService(boolean register){

        Log.d(TAG,"registerActivityToService " +register);

        ConnectionService service = ConnectionService.getInstance();

        if (service == null)
            return;

        //More efficient than creating a new message
        Message msg =  service.getHandler().obtainMessage();

        msg.what = MSG_REGISTER_ACTIVITY;
        msg.obj = this;
        msg.arg1 = register ? 1 : 0; // 1 to indicate register 0 to unregister
        service.getHandler().sendMessage(msg);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG,"onItemClicked: "+mDeviceList.get(position).getAddress());

        mBtAdapter.cancelDiscovery();

        String btDeviceAddr = mDeviceList.get(position).getAddress();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_BLUETOOTH_ADDRESS, btDeviceAddr);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
