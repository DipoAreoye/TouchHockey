package com.example.dipoareoye.testphysics;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.dipoareoye.bluetoothframework.main.Connection;
import com.example.dipoareoye.bluetoothframework.main.ConnectionService;
import com.example.dipoareoye.bluetoothframework.main.SelectServerActivity;
import com.example.dipoareoye.bluetoothframework.utils.Const;
import com.example.dipoareoye.testphysics.manager.ResourceManager;
import com.example.dipoareoye.testphysics.manager.SceneManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import static com.example.dipoareoye.bluetoothframework.utils.Const.SERVER_LIST_RESULT_CODE;
import static com.example.dipoareoye.bluetoothframework.utils.Const.TYPE_SERVER;
import static com.example.dipoareoye.bluetoothframework.utils.Const.*;


import static com.example.dipoareoye.testphysics.utils.Const.*;
import java.io.IOException;

public class MainActivity extends BaseGameActivity  {

    public static final String TAG = "GAME_ACT";

    private Camera mCamera;
    private int playerType;
    private Connection mConnection;
    private String opponentDevice;

    @Override
    public EngineOptions onCreateEngineOptions() {

        mCamera = new Camera(0,0,CAM_WIDTH,CAM_HEIGHT);

        EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAM_WIDTH,CAM_HEIGHT),mCamera);
        options.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

        return options;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {

       ResourceManager.initiateManager(mEngine , this, mCamera , getVertexBufferObjectManager());
       pOnCreateResourcesCallback.onCreateResourcesFinished();

    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {

        mEngine.registerUpdateHandler(new FPSLogger(0.5f , Debug.DebugLevel.DEBUG));

        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);

    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {

        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().createMenuScene();
            }
        }));



        pOnPopulateSceneCallback.onPopulateSceneFinished();

    }

    public void setPlayerType(int playerType){

        this.playerType = playerType;
    }

    public int getPlayerType (){

        return playerType;
    }

    public void setupConnection(){

        mConnection = new Connection(this, serviceReadyListener,connectionListener,disconnectListener , msgRecievedListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((resultCode == Activity.RESULT_OK) && (requestCode == SERVER_LIST_RESULT_CODE)) {
            String device = data.getStringExtra(Const.EXTRA_BLUETOOTH_ADDRESS);

            mConnection.connect(device);
            opponentDevice = device;

            return;
        }
    }

    private Connection.OnConnectionServiceReadyListener serviceReadyListener = new Connection.OnConnectionServiceReadyListener() {
        public void onConnectionServiceReady() {

            Log.d(TAG, "onConnectionServiceReady ");

            if (playerType == TYPE_SERVER) {

                mConnection.startServer();
            } else {

                Intent serverListIntent = new Intent(ResourceManager.getInstance().getMainActivity(), SelectServerActivity.class);
                startActivityForResult(serverListIntent, SERVER_LIST_RESULT_CODE);
            }
        }
    };

    private Connection.OnDeviceConnectionListener connectionListener = new Connection.OnDeviceConnectionListener() {

        public void onDeviceConnection(String device) {

            Log.d(TAG , "onDeviceConnected: "+device);
            opponentDevice = device;

        }
    };


    private Connection.OnMessageReceivedListener msgRecievedListener = new Connection.OnMessageReceivedListener() {

        @Override
        public void onMessageReceived(Bundle bundle) {

            if(bundle.getInt(BUNDLE_TYPE) == PUCK_UPDATE){

                SceneManager.getInstance().updateGameScene(bundle);
            } else {

                SceneManager.getInstance().updateScore();
            }

        }
    };

    private Connection.OnConnectionLostListener disconnectListener = new Connection.OnConnectionLostListener() {


        @Override
        public void onConnectionLost(String device) {

            Log.d(TAG, "onConnectionLost: "+device);

        }

    };

    @Override
    protected void onDestroy() {

        stopService(new Intent(this, ConnectionService.class));
        System.exit(0);
        super.onDestroy();
    }

    public void sendPuckMessage(int posX, int velocityX , int velocityY){

        Bundle bundle = new Bundle();
        bundle.putInt(PUCK_POSITION, posX);
        bundle.putInt(PUCK_VELOCITY_X, velocityX);
        bundle.putInt(PUCK_VELOCITY_Y, velocityY);

        mConnection.sendPuckMessage(bundle);
    }

    public void sendScoreUpdate() {

        mConnection.sendScoreMessage();

    }

}
