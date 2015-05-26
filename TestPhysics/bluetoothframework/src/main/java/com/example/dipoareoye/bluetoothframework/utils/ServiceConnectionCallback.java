package com.example.dipoareoye.bluetoothframework.utils;

/**
 * Created by dipoareoye on 25/04/15.
 */
public interface ServiceConnectionCallback {

    public void onDeviceConnected(String deviceAddress);
    public void onConnectionLost(String deviceAddress);



}
