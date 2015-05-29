package com.example.dipoareoye.testphysics.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import static com.example.dipoareoye.testphysics.utils.Const.MALLET_FIXTURE_DEF;
import static com.example.dipoareoye.testphysics.utils.Const.USER_MALLET;

/**
 * Created by dipoareoye on 29/05/15.
 */
public class ScoreCircle extends Sprite {

    public ScoreCircle(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager ) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);

        setScale(0.3f);

    }

    public void enable() {

        setColor(Color.WHITE);

    }

    public void disable() {

        setColor(Color.BLACK);

    }

}
