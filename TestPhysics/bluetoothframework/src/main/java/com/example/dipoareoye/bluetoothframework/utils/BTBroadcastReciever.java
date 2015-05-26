package com.example.dipoareoye.bluetoothframework.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dipoareoye.bluetoothframework.main.ConnectionService;

/**
 * Created by dipoareoye on 12/04/15.
 */
public class BTBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(null,"Device Detected");

        String action = intent.getAction();
        Intent serviceIntent = new Intent(context, ConnectionService.class);
        serviceIntent.setAction(action);
        serviceIntent.putExtras(intent);
        context.startService(serviceIntent);

    }
}
