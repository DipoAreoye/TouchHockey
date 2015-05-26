package com.example.dipoareoye.testphysics.scenes;

import android.app.Activity;

import com.example.dipoareoye.testphysics.MainActivity;
import com.example.dipoareoye.testphysics.manager.ResourceManager;
import com.example.dipoareoye.testphysics.manager.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class BaseScene extends Scene {
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    protected Engine mEngine;
    protected MainActivity mActivity;
    protected ResourceManager mResouceManager;
    protected VertexBufferObjectManager mVertexBufferMan;
    protected Camera mCamera;

    //---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------

    public BaseScene() {
        this.mResouceManager = ResourceManager.getInstance();
        this.mEngine = mResouceManager.mEngine;
        this.mActivity = mResouceManager.mActivity;
        this.mVertexBufferMan = mResouceManager.mVertexBufferObjectManager;
        this.mCamera = mResouceManager.mCamera;
        createScene();
    }

    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------

    public abstract void createScene();

    public abstract void onBackKeyPressed();

    public abstract SceneManager.SceneType getSceneType();

    public abstract void disposeScene();
}