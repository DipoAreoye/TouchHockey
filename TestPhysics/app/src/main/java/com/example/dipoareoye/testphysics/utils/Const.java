package com.example.dipoareoye.testphysics.utils;

import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.adt.color.Color;

/**
 * Created by dipoareoye on 04/05/15.
 */
public class Const {

    public static final int CAM_WIDTH = 480;
    public static final int CAM_HEIGHT = 800;

    public static final Color BG_COLOR =  new Color(0.306f,0.306f,0.384f);

    public static final int MENU_PADDING = 90 ;
    public static final float MENU_SCALE_SELECTED = 0.48f;
    public static final float MENU_SCALE_UNSELECTED = 0.45f;

    public static final String USER_MALLET = "mallet";
    public static final String USER_PUCK = "puck";

    /* The categories. */
    public static final short CATEGORYBIT_WALL = 1;
    public static final short CATEGORYBIT_MALLET = 2;
    public static final short CATEGORYBIT_PUCK = 4;

    public static final int GROUND_WIDTH = CAM_WIDTH / 3;
    public static final int WALL_THICKNESS = 10;

    public static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_PUCK + CATEGORYBIT_MALLET;
    public static final short MASKBITS_MALLET = CATEGORYBIT_MALLET + CATEGORYBIT_PUCK + CATEGORYBIT_WALL;
    public static final short MASKBITS_PUCK = CATEGORYBIT_PUCK + CATEGORYBIT_WALL + CATEGORYBIT_WALL;

    public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
    public static final FixtureDef MALLET_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0f, 0f);
    public static final FixtureDef PUCK_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);


}
