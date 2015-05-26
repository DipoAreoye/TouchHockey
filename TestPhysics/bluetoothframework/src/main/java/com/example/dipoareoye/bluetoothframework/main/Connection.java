package com.example.dipoareoye.bluetoothframework.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_CONN_FINISH;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_CONN_LOST;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_PUSHOUT_DATA;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_RECIEVED;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_REGISTER_CALLBACK;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_START_CLIENT;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_START_SERVER;


public class Connection {

    public static final String TAG = "CONN";

    public interface OnConnectionServiceReadyListener {
        public void onConnectionServiceReady();
    }

    public interface OnDeviceConnectionListener {
        public void onDeviceConnection(String device);
    }

    public interface OnMessageReceivedListener {
//        public void onMessageReceived(GamePhotos ball);
    }

    public interface OnConnectionLostListener {
        public void onConnectionLost(String device);
    }

    private OnConnectionServiceReadyListener mOnConnectionServiceReadyListener;

    private OnDeviceConnectionListener mOnDeviceConnectionListener;

    private OnConnectionLostListener mOnDisconnectListenter;

    private OnMessageReceivedListener mOnMsgRecievedListener;

    private ServiceConnection mServiceConnection;

    private Context mContext;

    private Boolean mBound;

    private Messenger mService = null;

    private Handler mCallbackHandler;

    private Messenger messenger;

    public Connection(Context ctx, OnConnectionServiceReadyListener ocsrListener , OnDeviceConnectionListener connListener,
                        OnConnectionLostListener onDisconnectListener , OnMessageReceivedListener mOnMsgRecListener) {

        mOnConnectionServiceReadyListener = ocsrListener;
        mOnDeviceConnectionListener = connListener;
        mOnDisconnectListenter = onDisconnectListener;
        mOnMsgRecievedListener = mOnMsgRecListener;

        mContext = ctx;

        Looper.prepare();

        mCallbackHandler = new IncomingHandler();

        messenger = new Messenger(mCallbackHandler);

        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected:");

                mBound = true;
                mService = new Messenger(service);

                if (mOnConnectionServiceReadyListener != null) {
                    mOnConnectionServiceReadyListener.onConnectionServiceReady();
                }

            }

            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };

        Intent intent = new Intent(mContext,ConnectionService.class);
        mContext.bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    public void startServer() {
        if (!mBound) {
            return;
        }

        try {

            sendMessageToService(MSG_START_SERVER, null, null);
            sendMessageToService(MSG_REGISTER_CALLBACK,messenger,null);

        } catch (RemoteException e) {

            Log.e(TAG,"startServer error: "+ e);
        }

        return;
    }

    public void connect(String deviceName){

        Log.d(TAG,"connect to :" +deviceName);

        if(!mBound) {

            return;
        }

        Message msg = Message.obtain();
        msg.what = MSG_START_CLIENT;
        msg.obj = deviceName;

        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return;

    }

    public void sendMessage() {

        try {

            Message msg = Message.obtain();
            msg.what = MSG_PUSHOUT_DATA;

            mService.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in sendMessage", e);
        }
    }

    public void sendMessageToService(int what,Messenger replyto, Bundle b) throws RemoteException {

        Message msg = Message.obtain(null,what,0,0);

        if(b != null)
            msg.setData(b);
        if (replyto != null)
            msg.replyTo = replyto;

        mService.send(msg);

    }

    final class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            processMessage(msg);
        }
    }

    private void processMessage(Message msg){

        switch (msg.what) {

            case MSG_CONN_FINISH :

                Log.d(TAG,"processMessage: Device connected : "+msg.obj);
                mOnDeviceConnectionListener.onDeviceConnection((String) msg.obj);

                break;
            case MSG_CONN_LOST :
                Log.d(TAG,"processMessage: Connection Lost : "+msg.obj);
                mOnDisconnectListenter.onConnectionLost((String) msg.obj);
                break;
            case MSG_RECIEVED :
                Log.d(TAG, "processMessage: Message Recieved :" );
//                mOnMsgRecievedListener.onMessageReceived((GamePhotos) msg.obj);
            default:
                break;

        }


    }


}