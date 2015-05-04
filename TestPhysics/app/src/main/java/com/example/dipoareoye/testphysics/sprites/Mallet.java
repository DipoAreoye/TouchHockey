package com.example.dipoareoye.testphysics.sprites;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.andengine.engine.

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.example.dipoareoye.testphysics.MainActivity;
import com.example.dipoareoye.testphysics.R;
import com.example.dipoareoye.testphysics.manager.ResourceManager;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import static com.example.dipoareoye.testphysics.utils.Const.*;

/**
 * Created by dipoareoye on 04/05/15.
 */
public class Mallet extends Sprite {

    final FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
    protected final Body body;
    private Vector2 startPosition;
    private PhysicsWorld physicsWorld;


    public Mallet(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager , PhysicsWorld physicsWorld) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);

        this.physicsWorld = physicsWorld;

        createWalls();

        body = PhysicsFactory.createCircleBody(physicsWorld , this , BodyDef.BodyType.KinematicBody , fixtureDef );
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
        body.setUserData(this);

        startPosition = new Vector2(body.getPosition());

    }


    public void resetPosition() {

        body.setLinearVelocity(0, 0);
        body.setTransform(startPosition, 0);

    }



}
