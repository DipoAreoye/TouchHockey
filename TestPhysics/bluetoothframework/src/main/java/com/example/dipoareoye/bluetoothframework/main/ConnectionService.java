package com.example.dipoareoye.bluetoothframework.main;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.dipoareoye.bluetoothframework.proto.GameStateProto;
import com.example.dipoareoye.bluetoothframework.utils.Const;
import com.example.dipoareoye.bluetoothframework.utils.ServiceConnectionCallback;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.dipoareoye.bluetoothframework.utils.Const.BUNDLE_TYPE;
import static com.example.dipoareoye.bluetoothframework.utils.Const.DEVICE_NAME;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_CONN_FINISH;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_CONN_LOST;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_EMPTY;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_PUSHOUT_DATA;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_RECIEVED;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_REGISTER_ACTIVITY;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_REGISTER_CALLBACK;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_SCORE_RECIEVED;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_START_CLIENT;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_START_SERVER;
import static com.example.dipoareoye.bluetoothframework.utils.Const.MSG_UPDATE_SCORE;
import static com.example.dipoareoye.bluetoothframework.utils.Const.PUCK_POSITION;
import static com.example.dipoareoye.bluetoothframework.utils.Const.PUCK_UPDATE;
import static com.example.dipoareoye.bluetoothframework.utils.Const.PUCK_VELOCITY_X;
import static com.example.dipoareoye.bluetoothframework.utils.Const.PUCK_VELOCITY_Y;
import static com.example.dipoareoye.bluetoothframework.utils.Const.SCORE_UPDATE;

/**
 * Created by dipoareoye on 12/04/15.
 */

public class ConnectionService extends Service implements ServiceConnectionCallback {

    public static final String TAG = "CONN_SERV";

    private UUID mUuid;

    private String mBtDeviceAddress = null;

    private BluetoothSocket mBtSocket = null ;

    private BluetoothAdapter mBtAdapter;

    private SelectServerActivity mServerActivity;

    private static ConnectionService mConnService;

    private MessageHandler mHandler;

    final Messenger mMessenger = new Messenger(new MessageHandler());

    private Messenger mCallbackMessenger = null;

    public ConnectionService() {

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    private void init() {

        mConnService = this;
        mHandler = new MessageHandler();
        mUuid = UUID.fromString(Const.UUID);

    }

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    public static ConnectionService getInstance(){
        return mConnService;
    }

    public MessageHandler getHandler(){

        return mHandler;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_STICKY;
    }

    private void handleIntent(Intent intent) {

        if(intent != null) {

            String action = intent.getAction();
            Log.d(TAG,"handleIntent: " +intent.toString());

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);

                switch (state) {

                    //BLUETOOTH IS ON
                    case BluetoothAdapter.STATE_ON :
                        Log.d(TAG,"handle intent: bluetooth enabled " );
                    //BLUETOOTH IS OFF
                    case BluetoothAdapter.STATE_OFF :
                        Log.d(TAG,"handle intent: bluetooth disabled " );

                }

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

               Log.d(TAG , "handle intent: bluetooth device found");

               BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

               if(mServerActivity != null) {
                   mServerActivity.onDeviceDetected(btDevice);

               }
            } else  if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {

                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,BluetoothAdapter.ERROR);

                switch (state){

                    case BluetoothAdapter.STATE_CONNECTED :
                        Log.d(TAG,"handelIntent:  connected to Device");
                    case BluetoothAdapter.STATE_DISCONNECTED :
                        Log.d(TAG,"handelIntent:  disconnected to Device");

                }


            }

        }

    }

    @Override
    public void onDeviceConnected(String deviceAddress) {

        if(mCallbackMessenger == null)
            return;

        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME,deviceAddress);

        sendCallback(MSG_CONN_FINISH, bundle);
    }

    @Override
    public void onConnectionLost(String deviceAddress) {

        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME,deviceAddress);

        sendCallback(MSG_CONN_LOST,bundle);


    }

    public void sendCallback(int what, Bundle bundle ){

        if (mCallbackMessenger == null){

            Log.e(null,"mcallback is null");
            return;

        }

        Message msg = mHandler.obtainMessage();
        msg.what = what;
        msg.setData(bundle);

        try {

            mCallbackMessenger.send(msg);

        } catch (RemoteException e) {

            Log.e(TAG,"sendCallBack : error sending callback - " +e);

        }

    }

    private void sendPuckMessage(Bundle data){

        try {

            if (mBtSocket != null) {

                OutputStream outStream = mBtSocket.getOutputStream();

                GameStateProto.GameState.Builder state = GameStateProto.GameState.newBuilder()
                        .setType(GameStateProto.GameState.MessageType.PUCK).setPosX(data.getInt(PUCK_POSITION))
                            .setVectorX(data.getInt(PUCK_VELOCITY_X)).setVectorY(data.getInt(PUCK_VELOCITY_Y));

                state.build().writeDelimitedTo(outStream);

                Log.d(TAG,"position x "+ state.getPosX() + "message out x "
                        + state.getVectorX() +" MESSAGE Y: " + state.getVectorY());
                return;
            }
        } catch (IOException e) {

            Log.e(TAG,"Send message" + e.toString());
        }
        return;
    }

    private void sendScoreMessage(){

        try {

            OutputStream outStream = mBtSocket.getOutputStream();

            GameStateProto.GameState.Builder state = GameStateProto.GameState.newBuilder()
                    .setType(GameStateProto.GameState.MessageType.SCORE);

            state.build().writeDelimitedTo(outStream);

            Log.d(TAG, "send score: ");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    final class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            processMessage(msg);
        }

    }

    private void processMessage(Message msg){

       switch (msg.what) {

           case MSG_EMPTY :
               break;
           case MSG_REGISTER_ACTIVITY :

                Log.d(TAG, "processMessage : registerActivity()");
                registerActivity((SelectServerActivity) msg.obj, (msg.arg1 == 1));

               break;
           case MSG_REGISTER_CALLBACK :

               Log.d(TAG, "processMessage: registerCallback");
               registerCallback(msg.replyTo);
               break;
           case MSG_START_SERVER :

                Log.d(TAG, "processMessage : startServer()");
                startServer(msg.getData().getString(Const.APP_NAME));
                break;
           case MSG_START_CLIENT :

               String deviceName = msg.getData().getString(DEVICE_NAME);
               Log.d(TAG, "processMessage : onStartClient()" + deviceName);
               connectToServer(deviceName);
               break;
           case MSG_PUSHOUT_DATA :

                Log.d(TAG, "processMessage : onPushoutMsg");
                sendPuckMessage(msg.getData());

               break;
           case MSG_UPDATE_SCORE :

               Log.d(TAG, "processMessage: onScoreUpdate");
               sendScoreMessage();
           default:
               break;

       }
   }

    private void registerActivity(SelectServerActivity serverActivity , Boolean register) {

        if(register)
            mServerActivity = serverActivity;
        else
            mServerActivity = null;
    }

    private void registerCallback(Messenger callback) {

        mCallbackMessenger = callback;
    }

    private void startServer(String appName){

        new Thread(new ServerThread(appName)).start();

    }

    private class ServerThread implements Runnable {

        final String appName;

        public ServerThread(String appName) {

            this.appName = appName;
        }

        @Override
        public void run() {

            try {

                 BluetoothServerSocket serverSocket = mBtAdapter
                        .listenUsingRfcommWithServiceRecord(appName, mUuid);

                mBtSocket = serverSocket.accept();

                Log.d(TAG, "startServer : client connected");

                serverSocket.close();

                mBtDeviceAddress = mBtSocket.getRemoteDevice().getAddress();

                Thread btStream = new Thread(new BtStream(mBtDeviceAddress));
                btStream.start();

            } catch (IOException e) {

                Log.e(TAG, "startServer: " + e.toString());
                return;
            }

            onDeviceConnected(mBtDeviceAddress);

        }
    }

    private class BtStream implements Runnable {
        private String address;

        public BtStream (String address){
            this.address = address;

        }

        @Override
        public void run() {

            try {

                InputStream inputStream;

                while (true){

                    Log.d(TAG,"BtStreamThread Running ....");

                    inputStream = mBtSocket.getInputStream();

                    GameStateProto.GameState state = GameStateProto.GameState.parseDelimitedFrom(inputStream);

                    Bundle bundle = new Bundle();

                    if(state.getType().equals(GameStateProto.GameState.MessageType.PUCK)) {

                        Log.e(TAG, "Bt Stream recieved : ball x is : " + state.getPosX() + "+" + state.getVectorX() / 10000);

                        bundle.putInt(BUNDLE_TYPE,PUCK_UPDATE);
                        bundle.putInt(PUCK_POSITION,state.getPosX());
                        bundle.putInt(PUCK_VELOCITY_X,state.getVectorX() / 10000);
                        bundle.putInt(PUCK_VELOCITY_Y,state.getVectorY() / 10000);


                    } else {

                        Log.e(TAG, "goal scored");

                        bundle.putInt(BUNDLE_TYPE,SCORE_UPDATE);
                    }

                    sendCallback(MSG_RECIEVED, bundle);

                }
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG,"BtStream: " + e.toString());
            } catch (IOException e ) {
                Log.e(TAG,"BtStream: " + e.toString());
            }

            onConnectionLost(address);

        }
    }

    public void connectToServer(String device) {

        Log.d(TAG,"connectToServer " +device);

        BluetoothDevice serverDevice = mBtAdapter.getRemoteDevice(device);
        mBtSocket = getConnectedSocket(serverDevice, mUuid);

        if (mBtSocket == null) {

            try {

                Thread.sleep(200);

            } catch (InterruptedException e) {

                Log.e(TAG, "InterruptedException in connect", e);
            }
        } if (mBtSocket == null ) {

            Log.e(TAG, "no socket connected");
            return;

        }

        mBtDeviceAddress = device;

        Thread btStream = new Thread(new BtStream(mBtDeviceAddress));
        btStream.start();

        return;
    }

    private BluetoothSocket getConnectedSocket(BluetoothDevice myBtServer, UUID uuidToTry) {

        Log.d(TAG,"getConnectedSocket " +uuidToTry.toString());

        BluetoothSocket myBSock;

        try {

            myBSock = myBtServer.createRfcommSocketToServiceRecord(uuidToTry);
            myBSock.connect();
            return myBSock;

        } catch (IOException e) {
            Log.i(TAG, "IOException in getConnectedSocket", e);
        }
        return null;
    }



}
