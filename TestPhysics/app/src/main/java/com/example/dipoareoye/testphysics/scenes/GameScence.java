package com.example.dipoareoye.testphysics.scenes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.example.dipoareoye.testphysics.manager.ResourceManager;
import com.example.dipoareoye.testphysics.manager.SceneManager;
import com.example.dipoareoye.testphysics.utils.Const;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import static com.example.dipoareoye.testphysics.utils.Const.CAM_HEIGHT;
import static com.example.dipoareoye.testphysics.utils.Const.CAM_WIDTH;

/**
 * Created by dipoareoye on 04/05/15.
 */
public class GameScence extends BaseScene {

    private PhysicsWorld physicsWorld;

    @Override
    public void createScene() {

        createbackground();

    }

    private void createWalls() {

        physicsWorld = new PhysicsWorld(new Vector2(0,0), false);

        Rectangle ground = new Rectangle(CAM_WIDTH / 2 , 0 ,CAM_WIDTH, 20 , ResourceManager.getInstance().mVertexBufferObjectManager);
        Rectangle left = new Rectangle(0 , CAM_HEIGHT / 2 , 20 , CAM_HEIGHT , ResourceManager.getInstance().mVertexBufferObjectManager);
        Rectangle right = new Rectangle(CAM_WIDTH , CAM_HEIGHT / 2 , 20 , CAM_HEIGHT , ResourceManager.getInstance().mVertexBufferObjectManager);

        FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.5f);

        PhysicsFactory.createBoxBody(physicsWorld , ground , BodyDef.BodyType.StaticBody , WALL_FIX);
        PhysicsFactory.createBoxBody(physicsWorld , left , BodyDef.BodyType.StaticBody , WALL_FIX);
        PhysicsFactory.createBoxBody(physicsWorld , right , BodyDef.BodyType.StaticBody , WALL_FIX);

        this.attachChild(ground);
        this.attachChild(left);
        this.attachChild(right);

    }

    private void createbackground() {

        setBackground(new Background(Const.BG_COLOR));

    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.GAME;
    }

    @Override
    public void disposeScene() {

    }
}
