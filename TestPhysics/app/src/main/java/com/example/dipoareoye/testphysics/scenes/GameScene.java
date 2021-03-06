package com.example.dipoareoye.testphysics.scenes;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.example.dipoareoye.testphysics.manager.ResourceManager;
import com.example.dipoareoye.testphysics.manager.SceneManager;
import com.example.dipoareoye.testphysics.sprites.Mallet;
import com.example.dipoareoye.testphysics.sprites.Puck;
import com.example.dipoareoye.testphysics.sprites.ScoreCircle;
import com.example.dipoareoye.testphysics.utils.Const;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import java.lang.reflect.Array;

import static com.example.dipoareoye.testphysics.utils.Const.CAM_HEIGHT;
import static com.example.dipoareoye.testphysics.utils.Const.CAM_WIDTH;

import  static com.example.dipoareoye.testphysics.utils.Const.*;


/**
 * Created by dipoareoye on 04/05/15.
 */
public class GameScene extends BaseScene  implements IOnAreaTouchListener,IOnSceneTouchListener {

    public final static int TYPE_SERVER = 0 , TYPE_CLIENT = 1;

    private PhysicsWorld physicsWorld;

    private Mallet mallet;
    private Puck puck;
    private ScoreCircle circle;

    private Body mGroundBody;
    private Body malletBody;

    private Body puckBody;

    private int playerType;

    private int opponentScore = 0;
    private int myScore = 0;

    private Text myScoreText;
    private Text opponentScoreText;

    private ScoreCircle[] myCircles;
    private ScoreCircle[] oppnentCircles;

    @Override
    public void createScene() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mActivity.setupConnection();
            }
        });

        playerType =  mActivity.getPlayerType();


        createHUD();
        createBackground();
        createPhysics();
        createWalls();
        createSprites();

        setOnAreaTouchListener(this);
        setOnSceneTouchListener(this);

        this.registerUpdateHandler(new IUpdateHandler() {
            public void reset() {
            }

            public void onUpdate(float pSecondsElapsed) {

                //Puck has reached screen boundary
                if (puck.getY() > (CAM_HEIGHT + CAM_HEIGHT / 10)) {

                    mActivity.sendPuckMessage((int) (puck.getX()), (int) (puck.getVelocityVector().x * 10000.0f),
                            (int) (puck.getVelocityVector().y * 10000.0f));

                    puckBody.setTransform(puck.getX(), ((CAM_HEIGHT + CAM_HEIGHT / 10) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT), puckBody.getAngle());
                    puckBody.setLinearVelocity(0, 0);

                }

                if (puck.getY() + puck.getHeight() / 2 < 0) {

                    mActivity.sendScoreUpdate();
                    opponentScore++;
                    opponentScoreText.setText(String.valueOf(opponentScore));
                    oppnentCircles[opponentScore - 1].setColor(SCORE_CIRCLE_ON);

                    puck.resetPosition();

                }

            }
        });

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
        registerUpdateHandler(physicsWorld);

    }

    private void createSprites() {

        mallet = new Mallet(CAM_WIDTH / 2, CAM_HEIGHT / 4 , ResourceManager.getInstance().mallet_region,
                ResourceManager.getInstance().mVertexBufferObjectManager, physicsWorld);

        malletBody = mallet.body;
        if(playerType == TYPE_SERVER) {


            puck = new Puck(CAM_WIDTH / 2, CAM_HEIGHT / 2, ResourceManager.getInstance().puck_region,
                    ResourceManager.getInstance().mVertexBufferObjectManager, physicsWorld);

        } else {


            puck = new Puck(CAM_WIDTH / 2,   CAM_HEIGHT + CAM_HEIGHT / 10 , ResourceManager.getInstance().puck_region,
                    ResourceManager.getInstance().mVertexBufferObjectManager, physicsWorld);

        }


        puckBody = puck.body;

        this.attachChild(puck);
        this.attachChild(mallet);

        this.registerTouchArea(mallet);
        this.setTouchAreaBindingOnActionDownEnabled(true);
    }

    private void createHUD(){

        myCircles = new ScoreCircle[MAX_SCORE];
        oppnentCircles = new ScoreCircle[MAX_SCORE];

        loadScoreIndicator();

        float myScorePosition = (myCircles[0].getX() + myCircles[myCircles.length -1].getX()) / 2;
        float opponentScorePosition = (oppnentCircles[0].getX() + oppnentCircles[oppnentCircles.length -1].getX()) / 2;

        myScoreText = new Text(myScorePosition, CAM_HEIGHT - (2* MARGIN_OUTER) , ResourceManager.getInstance().gameScoreFont, "0", ResourceManager.getInstance().mVertexBufferObjectManager);
        opponentScoreText = new Text(opponentScorePosition , CAM_HEIGHT - (2* MARGIN_OUTER) , ResourceManager.getInstance().gameScoreFont, "0", ResourceManager.getInstance().mVertexBufferObjectManager);

        this.attachChild(myScoreText);
        this.attachChild(opponentScoreText);

    }

    private void loadScoreIndicator(){

        for (int i = 0 ; i < myCircles.length ; i++){

            myCircles[i] = new ScoreCircle(((i+1)* MARGIN_OUTER),CAM_HEIGHT - MARGIN_OUTER,
                    ResourceManager.getInstance().score_circle_region,mEngine.getVertexBufferObjectManager());

            attachChild(myCircles[i]);
        }

        for (int i = oppnentCircles.length - 1 ; i >= 0 ; i--){

            oppnentCircles[i] = new ScoreCircle((CAM_WIDTH - ((oppnentCircles.length - i)* MARGIN_OUTER)),
                    CAM_HEIGHT - MARGIN_OUTER,ResourceManager.getInstance().score_circle_region,mEngine.getVertexBufferObjectManager());

            attachChild(oppnentCircles[i]);

        }


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

//                    Log.e(null, "YOOO SCENE DOWN");
//                    malletBody.setLinearVelocity((pSceneTouchEvent.getX()- malletBody.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
//                            (pSceneTouchEvent.getY()- malletBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
                    return true;
                case TouchEvent.ACTION_MOVE:

                    malletBody.setLinearVelocity(0, 0);
                    malletBody.setLinearVelocity((pSceneTouchEvent.getX()- malletBody.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
                            (pSceneTouchEvent.getY()- malletBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
                    return true;
                case TouchEvent.ACTION_UP:

                    malletBody.setLinearVelocity(0, 0);
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

               float diffX = Math.abs(pTouchAreaLocalX - mallet.getX());
               float diffY = Math.abs(pTouchAreaLocalY - mallet.getY());

                malletBody.setLinearVelocity(0, 0);

                if ( diffX > 15 && diffY > 15){

                    malletBody.setLinearVelocity((pSceneTouchEvent.getX()- malletBody.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),
                            (pSceneTouchEvent.getY()- malletBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
                }


                return true;
            case TouchEvent.ACTION_UP:
                malletBody.setLinearVelocity(0, 0);
                return true;

        }

        return false;

    }

    public void spawnPuck(int posx , int velocX, int velocY) {

        Log.e(null, "spawnPuckX: " + posx + "spawnPuckY: " + velocY);

        puck.updatePosition(posx, velocX, velocY);



    }

    public void updateScore(){

        myScore++;
        myScoreText.setText(String.valueOf(myScore));
        myCircles[myScore-1].setColor(SCORE_CIRCLE_ON);
    }


}
