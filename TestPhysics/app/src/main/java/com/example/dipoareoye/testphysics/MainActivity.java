package com.example.dipoareoye.testphysics;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.example.dipoareoye.testphysics.manager.ResourceManager;
import com.example.dipoareoye.testphysics.manager.SceneManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;


import static com.example.dipoareoye.testphysics.utils.Const.*;
import java.io.IOException;

public class MainActivity extends BaseGameActivity  {

    private PhysicsWorld physicsWorld;
    private Scene scene;
    private BitmapTextureAtlas playerTextureAtlas;
    private TextureRegion playerTextureRegion;
    private SceneManager sceneManager;
    private Camera mCamera;

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

    private void loadGfx()  {

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        playerTextureAtlas = new BitmapTextureAtlas(getTextureManager(),128 ,128);
        playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(playerTextureAtlas, this , "Puck.png" , 0,0);

        playerTextureAtlas.load();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        //        this.scene = new Scene();
//        this.scene.setBackground(new Background(0,125,58));
//
//        this.physicsWorld = new PhysicsWorld(new Vector2(0,-SensorManager.GRAVITY_EARTH) , false);
//        this.scene.registerUpdateHandler(physicsWorld);
//        createWalls();

        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);

    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {

//        Sprite sPuck = new Sprite( CAM_WIDTH / 2 , CAM_HEIGHT / 2  ,
//                playerTextureRegion , this.mEngine.getVertexBufferObjectManager() );
//
//       final FixtureDef PUCK_FIX = PhysicsFactory.createFixtureDef(10.0f , 1.0f , 0.0f);
//
//        Body puckBody = PhysicsFactory.createCircleBody(physicsWorld, sPuck, BodyDef.BodyType.DynamicBody, PUCK_FIX);
//        sPuck.setHeight(66.0f);
//        sPuck.setWidth(66.0f);
//        this.scene.attachChild(sPuck);
//
//        physicsWorld.registerPhysicsConnector(new PhysicsConnector(sPuck , puckBody , true , false));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    protected PhysicsWorld getWorld(){

        return  physicsWorld;
    }

}
