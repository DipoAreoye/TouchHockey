package com.example.dipoareoye.testphysics.scenes;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import com.example.dipoareoye.bluetoothframework.utils.StartDiscoverableModeActivity;
import com.example.dipoareoye.testphysics.manager.SceneManager;
import com.example.dipoareoye.testphysics.manager.SceneManager.SceneType;
import com.example.dipoareoye.testphysics.utils.Const;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;

/**
 * Created by dipoareoye on 03/05/15.
 */
public class MenuOptionScene extends BaseScene implements IOnMenuItemClickListener {

    private MenuScene menuChildScene;

    private final int MENU_START_SERVER = 0;
    private final int MENU_JOIN_GAME = 1;
    private final int MENU_VIEW_STATS = 2;

    @Override
    public void createScene() {
        menuChildScene = new MenuScene(mCamera);
        menuChildScene.setPosition(0, 0);

        final IMenuItem startServerItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
                MENU_START_SERVER, mResouceManager.start_game_region, mVertexBufferMan), Const.MENU_SCALE_SELECTED, Const.MENU_SCALE_UNSELECTED);
        final IMenuItem joinGameItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
                MENU_JOIN_GAME, mResouceManager.join_game_region, mVertexBufferMan), Const.MENU_SCALE_SELECTED, Const.MENU_SCALE_UNSELECTED);
        final IMenuItem statsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
                MENU_VIEW_STATS, mResouceManager.view_stats_region, mVertexBufferMan), Const.MENU_SCALE_SELECTED, Const.MENU_SCALE_UNSELECTED);

        setBackground(new Background(Const.BG_COLOR));

        menuChildScene.addMenuItem(startServerItem);
        menuChildScene.addMenuItem(joinGameItem);
        menuChildScene.addMenuItem(statsMenuItem);

        menuChildScene.buildAnimations();
        menuChildScene.setBackgroundEnabled(false);

        startServerItem.setPosition(mCamera.getWidth() / 2, mCamera.getHeight() / 2 - Const.MENU_PADDING);
        joinGameItem.setPosition(joinGameItem.getX(), startServerItem.getY() - Const.MENU_PADDING);
        statsMenuItem.setPosition(statsMenuItem.getX(), joinGameItem.getY() - Const.MENU_PADDING);

        menuChildScene.setOnMenuItemClickListener(this);

        setChildScene(menuChildScene);
    }

    @Override
    public void onBackKeyPressed() {


    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {

        switch (pMenuItem.getID())

        {
            case MENU_START_SERVER:
                Log.d(null, "startServerClicked");

                mActivity.setPlayerType(0);// assign player as Server
                SceneManager.getInstance().loadGameScene(mEngine);

                return true;
            case MENU_JOIN_GAME:

                mActivity.setPlayerType(1);// assign player as client
                SceneManager.getInstance().loadGameScene(mEngine);

                return true;
            case MENU_VIEW_STATS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.MENU;
    }

    @Override
    public void disposeScene() {

    }

}

