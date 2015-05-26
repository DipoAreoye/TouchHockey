package com.example.dipoareoye.bluetoothframework.utils;

/**
 * Created by dipoareoye on 12/04/15.
 */
public class Const {

    public final static String APP_NAME = "TouchHockey";

    public final static String UUID = "b6630802-0338-11e5-8418-1697f925ec7b";

    //Server/Client constants.
    public final static String PLAYER_TYPE = "playerType";
    public final static int TYPE_SERVER = 0 , TYPE_CLIENT = 1;
    public static final int SERVER_LIST_RESULT_CODE = 51;

   //Message Constants
    public static final int MSG_EMPTY = 0;
    public static final int MSG_START_SERVER = 100;
    public static final int MSG_START_CLIENT = 101;
    public static final int MSG_CONN_FINISH = 102;
    public static final int MSG_REGISTER_ACTIVITY = 103;
    public static final int MSG_RECIEVED = 104;
    public static final int MSG_PUSHOUT_DATA = 105;
    public static final int MSG_REGISTER_CALLBACK = 106;
    public static final int MSG_CONN_LOST = 107;

    public static String EXTRA_BLUETOOTH_ADDRESS = "btaddress";

    public static String PACKAGE_NAME = "packageName";
    public static String DEVICE_NAME = "deviceName";


}

