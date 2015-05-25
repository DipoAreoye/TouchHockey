package com.example.dipoareoye.testphysics.scenes;

import android.graphics.Typeface;
import android.text.style.MaskFilterSpan;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.example.dipoareoye.testphysics.manager.ResourceManager;
import com.example.dipoareoye.testphysics.manager.SceneManager;
import com.example.dipoareoye.testphysics.sprites.Mallet;
import com.example.dipoareoye.testphysics.sprites.Puck;
import com.example.dipoareoye.testphysics.utils.Const;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;

import static com.example.dipoareoye.testphysics.utils.Const.CAM_HEIGHT;
import static com.example.dipoareoye.testphysics.utils.Const.CAM_WIDTH;
import static com.example.dipoareoye.testphysics.utils.Const.USER_MALLET;
import static com.example.dipoareoye.testphysics.utils.Const.USER_PUCK;

import  static com.example.dipoareoye.testphysics.utils.Const.*;


/**
 * Created by dipoareoye on 04/05/15.
 */
public class GameScene extends BaseScene  implements IOnSceneTouchListener , IOnAreaTouchListener {

    private PhysicsWorld physicsWorld;
    private MouseJoint mMouseJointActive;

    private Mallet mallet;
    private Puck puck;

    private MouseJoint mjActive;
    private Body mGroundBody;
    private Body body;

    @Override
    public void createScene() {


        createBackground();
        createPhysics();
        createWalls();
        createSprites();

        setOnSceneTouchListener(this);
        setOnAreaTouchListener(this);

        this.registerUpdateHandler(new IUpdateHandler() {
            public void reset() {
            }
            public void onUpdate(float pSecondsElapsed) {

                if (puck.getY() < 0) {

                    puck.setX(CAM_HEIGHT / 2);
                    puck.setY(CAM_WIDTH / 2);

                }



            }
        });

    }

    private void showFrameRate(){

        //        final FPSCounter fpsCounter = new FPSCounter();
//        this.mEngine.registerUpdateHandler(fpsCounter);
//
//        this.mFontTexture = new BitmapTextureAtlas(ResourceManager.getInstance().mActivity.getTextureManager(), 2056, 2056);
//        mFont = new Font(ResourceManager.getInstance().mActivity.getFontManager(),mFontTexture,Typeface.create(Typeface.DEFAULT,Typeface.BOLD), 32 , true ,Color.WHITE);
//
//        mFont.load();
//
//        final Text fpsText = new Text(100, 100, this.mFont, "FPS:", "FPS: XXXXXXXXXXXX".length(),ResourceManager.getInstance().mVertexBufferObjectManager);

//        this.attachChild(fpsText);
//
//        this.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
//            @Override
//            public void onTimePassed(final TimerHandler pTimerHandler) {
//                fpsText.setText("FPS: " + fpsCounter.getFPS());
//            }
//        }));

//        this.mFontTexture = new BitmapTextureAtlas(ResourceManager.getInstance().mActivity.getTextureManager(),256, 256);
//        this.mFont = new Font(ResourceManager.getInstance().mActivity.getFontManager(),
//                this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.BLACK);

    }

    private void createWalls() {

        this.mGroundBody = this.physicsWorld.createBody(new BodyDef());

        Rectangle groundLeft = new Rectangle(GROUND_WIDTH / 2, 0, GROUND_WIDTH, WALL_THICKNESS, ResourceManager.getInstance().mVertexBufferObjectManager);
        Rectangle groundRight = new Rectangle(CAM_WIDTH * 5/6, 0, GROUND_WIDTH, WALL_THICKNESS, ResourceManager.getInstance().mVertexBufferObjectManager);
        Rectangle left = new Rectangle(0, CAM_HEIGHT / 2, WALL_THICKNESS, CAM_HEIGHT, ResourceManager.getInstance().mVertexBufferObjectManager);
        Rectangle right = new Rectangle(CAM_WIDTH, CAM_HEIGHT / 2, WALL_THICKNESS, CAM_HEIGHT, ResourceManager.getInstance().mVertexBufferObjectManager);

        PhysicsFactory.createBoxBody(this.physicsWorld, groundLeft, BodyDef.BodyType.KinematicBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(this.physicsWorld, groundRight, BodyDef.BodyType.KinematicBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(this.physicsWorld, left, BodyDef.BodyType.KinematicBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(this.physicsWorld, right, BodyDef.BodyType.KinematicBody, WALL_FIXTURE_DEF);

        this.attachChild(groundLeft);
        this.attachChild(groundRight);
        this.attachChild(left);
        this.attachChild(right);

    }

    private void createBackground() {

        setBackground(new Background(Const.BG_COLOR));

    }

    private void createPhysics() {

        physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        physicsWorld.setContactListener(createContactListener());
        registerUpdateHandler(physicsWorld);

    }

    private void createSprites() {

        mallet = new Mallet(CAM_WIDTH / 2, CAM_HEIGHT / 4 , ResourceManager.getInstance().mallet_region,
                ResourceManager.getInstance().mVertexBufferObjectManager, physicsWorld);

        puck = new Puck(CAM_WIDTH / 2, CAM_HEIGHT / 2, ResourceManager.getInstance().puck_region,
                ResourceManager.getInstance().mVertexBufferObjectManager, physicsWorld);

        this.attachChild(mallet);
        this.attachChild(puck);
        this.registerTouchArea(mallet);
        this.setTouchAreaBindingOnActionDownEnabled(true);
    }

    private ContactListener createContactListener(){

        ContactListener contactListener = new ContactListener() {

            @Override
            public void beginContact(Contact contact) {

                final Fixture fixtureA = contact.getFixtureA();
                final Body bodyA = fixtureA.getBody();
                final String userDataA = (String) bodyA.getUserData();

                final Fixture fixtureB = contact.getFixtureB();
                final Body bodyB = fixtureB.getBody();
                final String userDataB = (String) bodyB.getUserData();


//                if ( userDataA.equals(USER_MALLET) && userDataB.equals(USER_PUCK)) {
//
//                    Log.e(null,"velocity = " + bodyB.getLinearVelocity());
//
//                    bodyB.setLinearVelocity(bodyA.getLinearVelocity());
//
//                }



            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {

            }
        };

        return contactListener;

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

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {

        if(this.physicsWorld != null) {
            switch(pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    return true;
                case TouchEvent.ACTION_MOVE:
                    body.setLinearVelocity((pSceneTouchEvent.getX()-body.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT*10),
                            (pSceneTouchEvent.getY()-body.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT*10));
                case TouchEvent.ACTION_UP:

                    body.setLinearVelocity(0,0);
                    return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

        switch (pSceneTouchEvent.getAction()){


            case TouchEvent.ACTION_MOVE:

                final Mallet face = (Mallet) pTouchArea;
                body = this.physicsWorld.getPhysicsConnectorManager().findBodyByShape(face);

                body.setLinearVelocity((pSceneTouchEvent.getX()-body.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
                        (pSceneTouchEvent.getY()-body.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
                return true;
            case TouchEvent.ACTION_UP:
                body.setLinearVelocity(0,0);

        }

        return false;

    }


    public MouseJoint createMouseJoint(final IShape pFace, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

        final Body body = this.physicsWorld.getPhysicsConnectorManager().findBodyByShape(pFace);

        final MouseJointDef mouseJointDef = new MouseJointDef();

        Vector2 v = body.getWorldPoint(new Vector2(pTouchAreaLocalX/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 0.1f,
                pTouchAreaLocalY/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 0.1f));


        mouseJointDef.bodyA = this.mGroundBody;
        mouseJointDef.bodyB = body;
        mouseJointDef.dampingRatio = 0.2f;
        mouseJointDef.frequencyHz = 30000f;
        mouseJointDef.maxForce = (10000000.0f);

        mouseJointDef.collideConnected = true;

        mouseJointDef.target.set(v);

        return (MouseJoint) this.physicsWorld.createJoint(mouseJointDef);
    }


//    @Override
//    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
//        return false;
//    }
}
