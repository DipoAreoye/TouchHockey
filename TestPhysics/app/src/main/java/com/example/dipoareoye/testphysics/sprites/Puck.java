package com.example.dipoareoye.testphysics.sprites;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static com.example.dipoareoye.testphysics.utils.Const.*;

/**
 * Created by dipoareoye on 04/05/15.
 */
public class Puck extends Sprite {

    public final Body body;
    private Vector2 startPosition = new Vector2((CAM_WIDTH /  2) /  PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT ,
            (CAM_HEIGHT /  2) /  PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
    private PhysicsWorld physicsWorld;

    public Puck(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld physicsWorld) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);

        this.physicsWorld = physicsWorld;

        setScale(0.3f);

        body = PhysicsFactory.createCircleBody(physicsWorld , this , BodyDef.BodyType.DynamicBody , PUCK_FIXTURE_DEF );

        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));

        body.setUserData(USER_PUCK);
    }

    public Vector2 getVelocityVector(){

        return body.getLinearVelocity();
    }

    public void updatePosition(float posx, float velx, float vely){

        Log.e(null,"transformed x = " + posx / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);//+;

        float x = (((posx - CAM_WIDTH) * -1.0f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        body.setTransform(x,(CAM_HEIGHT / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT),body.getAngle());

        body.setLinearVelocity(
                -velx,-vely);

        Log.i(null, "X position :" + getX());

    }

    public void resetPosition() {

        body.setTransform(startPosition,body.getAngle());
        body.setLinearVelocity(0,0);

    }



}
