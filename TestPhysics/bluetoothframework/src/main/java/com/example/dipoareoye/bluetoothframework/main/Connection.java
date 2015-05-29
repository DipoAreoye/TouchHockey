package com.example.dipoareoye.bluetoothframework.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import static com.example.dipoareoye.bluetoothframework.utils.Const.DEVICE_NAME;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_CONN_FINISH;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_CONN_LOST;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_PUSHOUT_DATA;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_RECIEVED;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_REGISTER_CALLBACK;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_START_CLIENT;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_START_SERVER;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_UPDATE_SCORE;
import static com.example.dipoareoye.bluetoothframework.utils.Const.PUCK_VELOCITY_X;


public class Connection {

    public static final String TAG = "CONN";

    public interface OnConnectionServiceReadyListener {
        public void onConnectionServiceReady();
    }

    public interface OnDeviceConnectionListener {
        public void onDeviceConnection(String device);
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(Bundle obj);
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

                Message msg = Message.obtain(null,
                        MSG_REGISTER_CALLBACK);

                msg.replyTo = messenger;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
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

            sendMessageToService(MSG_START_SERVER, messenger, null);

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

        try {
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_NAME,deviceName);
            sendMessageToService(MSG_START_CLIENT, messenger, bundle);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return;

    }

    public void sendPuckMessage(Bundle data) {

        try {

            Message msg = Message.obtain();
            msg.what = MSG_PUSHOUT_DATA;
            msg.setData(data);

            Log.e(null, "Connection sendPuckMessage();" + msg.getData().getInt(PUCK_VELOCITY_X));

            mService.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in sendPuckMessage", e);
        }
    }

    public void sendScoreMessage() {

        try {

            Message msg = Message.obtain();
            msg.what = MSG_UPDATE_SCORE;

            Log.e(null, "Connection sendPuckMessage();" + msg.getData().getInt(PUCK_VELOCITY_X));

            mService.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in sendPuckMessage", e);
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

                Log.d(TAG,"processMessage: Device connected : "+msg.getData().getString(DEVICE_NAME));
                mOnDeviceConnectionListener.onDeviceConnection(msg.getData().getString(DEVICE_NAME));

                break;
            case MSG_CONN_LOST :
                Log.d(TAG,"processMessage: Connection Lost : "+msg.getData().getString(DEVICE_NAME));
                mOnDisconnectListenter.onConnectionLost(msg.getData().getString(DEVICE_NAME));
                break;
            case MSG_RECIEVED :
                Log.d(TAG, "processMessage: Message Recieved :" );
                mOnMsgRecievedListener.onMessageReceived(msg.getData());
                break;
            case MSG_UPDATE_SCORE :

            default:
                break;

        }


    }


}