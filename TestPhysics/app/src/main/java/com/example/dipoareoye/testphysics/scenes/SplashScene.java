package com.example.dipoareoye.testphysics.scenes;

import com.example.dipoareoye.testphysics.manager.SceneManager.SceneType;
import com.example.dipoareoye.testphysics.utils.Const;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

public class SplashScene extends BaseScene {
    private Sprite splash;

    @Override
    public void createScene() {

        splash = new Sprite(0, 0, mResouceManager.splash_region, mVertexBufferMan) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };

        splash.setScale(0.25f);
        splash.setPosition(mCamera.getWidth() / 2, mCamera.getHeight() / 2);
        setBackground(new Background(Const.BG_COLOR));
        attachChild(splash);

    }

    @Override
    public void onBackKeyPressed() {
        return;
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SPLASH;
    }

    @Override
    public void disposeScene() {
        splash.detachSelf();
        splash.dispose();
        this.detachSelf();
        this.dispose();
    }
}