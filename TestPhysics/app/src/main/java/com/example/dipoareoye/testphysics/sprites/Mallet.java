package com.example.dipoareoye.testphysics.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static com.example.dipoareoye.testphysics.utils.Const.*;

/**
 * Created by dipoareoye on 04/05/15.
 */
public class Mallet extends Sprite {

    public final Body body;
    private Vector2 startPosition;
    private PhysicsWorld physicsWorld;

    public Mallet(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager , PhysicsWorld physicsWorld) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);

        this.physicsWorld = physicsWorld;

        setScale(0.3f);

        body = PhysicsFactory.createCircleBody(physicsWorld , this , BodyDef.BodyType.DynamicBody , MALLET_FIXTURE_DEF );
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
        body.setUserData(USER_MALLET);

        }

    public void resetPosition() {

        body.setLinearVelocity(0, 0);
        body.setTransform(startPosition, 0);

    }



}
