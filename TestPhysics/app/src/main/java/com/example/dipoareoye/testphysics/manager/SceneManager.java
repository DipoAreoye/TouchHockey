package com.example.dipoareoye.testphysics.manager;

import android.os.Bundle;
import android.util.Log;

import com.example.dipoareoye.testphysics.scenes.BaseScene;
import com.example.dipoareoye.testphysics.scenes.GameScene;
import com.example.dipoareoye.testphysics.scenes.MenuOptionScene;
import com.example.dipoareoye.testphysics.scenes.SplashScene;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface;

import static com.example.dipoareoye.testphysics.utils.Const.*;

import static com.example.dipoareoye.bluetoothframework.utils.Const.*;

public class SceneManager {

    //---------------------------------------------
    // SCENES
    //---------------------------------------------

    private BaseScene splashScene;
    private BaseScene gameScene;
    private BaseScene menuScene;

    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    private static final SceneManager INSTANCE = new SceneManager();
    private BaseScene currentScene;
    private SceneType currentSceneType = SceneType.SPLASH;
    private Engine engine = ResourceManager.getInstance().mEngine;

    public enum SceneType {
		SPLASH, MENU, GAME
	}

    public void setSceneType(SceneType sceneType)
    {
        switch (sceneType)
        {
            case SPLASH:
                setScene(splashScene);
                break;
            case MENU:
                setScene(menuScene);
                break;
            case GAME:
                setScene(gameScene);
                break;
            default:
                break;
        }
    }

    public void createSplashScene(IGameInterface.OnCreateSceneCallback pOnCreateSceneCallback) {

        ResourceManager.getInstance().loadSplashScreen();
        splashScene = new SplashScene();
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

    public void createMenuScene() {

        ResourceManager.getInstance().loadMenuResources();
        menuScene = new MenuOptionScene();
        SceneManager.getInstance().setScene(menuScene);
        disposeSplashScene();

    }

    private void disposeSplashScene() {

        ResourceManager.getInstance().unloadSplashScreen();
        splashScene.disposeScene();
        splashScene = null;

    }

    public void setScene(BaseScene scene)  {

        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();

    }

    public void loadGameScene(final Engine mEngine) {

        ResourceManager.getInstance().unloadMenuTextures();
        engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.getInstance().loadGameResources();
                gameScene = new GameScene();
                setScene(gameScene);
            }
        }));

    }

    public void updateGameScene(Bundle bundle ){

            ((GameScene)gameScene).spawnPuck(bundle.getInt(PUCK_POSITION),
                    bundle.getInt(PUCK_VELOCITY_X) , bundle.getInt(PUCK_VELOCITY_Y));

    }

    public void updateScore(){

        ((GameScene)gameScene).updateScore();

    }

    public void loadMenuScene(final Engine mEngine)
    {
        gameScene.disposeScene();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
        {
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourceManager.getInstance().loadMenuTextures();
                setScene(menuScene);
            }
        }));
    }

    public SceneType getCurrentSceneType()
    {
        return currentSceneType;
    }

    public BaseScene getCurrentScene()
    {
        return currentScene;
    }

    public static SceneManager getInstance()
    {
        return INSTANCE;
    }

}