package com.example.dipoareoye.testphysics.manager;

import com.example.dipoareoye.testphysics.MainActivity;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

/**
 * Created by dipoareoye on 03/05/15.
 */
public class ResourceManager {

    private static final ResourceManager INSTANCE = new ResourceManager();

    public Engine mEngine;
    public MainActivity mActivity;
    public Camera mCamera;
    public VertexBufferObjectManager mVertexBufferObjectManager;

    //Game Regions
    public ITextureRegion puck_region;
    public ITextureRegion mallet_region;

    //Splash Regions
    public ITextureRegion splash_region;

    //Menu Regions
    public ITextureRegion start_game_region;
    public ITextureRegion join_game_region;
    public ITextureRegion view_stats_region;

    private BitmapTextureAtlas splashTextureAtlas;
    private BuildableBitmapTextureAtlas menuTextureAtlas;

    private BitmapTextureAtlas malletTextureAtlas;
    private BitmapTextureAtlas puckTextureAtlas;

    public static ResourceManager getInstance() {

        return INSTANCE;
    }

    public static void initiateManager(Engine engine , MainActivity activity , Camera camera , VertexBufferObjectManager vboManager){

        getInstance().mEngine = engine;
        getInstance().mActivity = activity;
        getInstance().mCamera = camera;
        getInstance().mVertexBufferObjectManager = vboManager;

    }

    public void loadSplashScreen()  {

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");

        splashTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
        splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, mActivity, "logo_TH.png", 0, 0);

        splashTextureAtlas.load();
    }

    public void unloadSplashScreen() {

        splashTextureAtlas.unload();
        splash_region = null;
    }

    public void loadMenuResources()  {

        loadMenuGraphics();
//        loadMenuAudio();
//        loadMenuFonts();
    }

    private void loadMenuGraphics() {

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

        menuTextureAtlas = new BuildableBitmapTextureAtlas(mActivity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

        start_game_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, mActivity, "create_game.png");
        join_game_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas , mActivity ,"join_game.png");
        view_stats_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, mActivity, "view_stats.png");


        try
        {
            this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.menuTextureAtlas.load();
        }
        catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }

    }

    public void loadMenuTextures() {

        menuTextureAtlas.load();

    }

    public void loadGameResources(){

        loadGameGraphics();

    }

    private void loadGameGraphics()  {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

        malletTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        puckTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager() , 128 , 128 , TextureOptions.BILINEAR);

        mallet_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(malletTextureAtlas,mActivity,"mallet.png" , 0 , 0);

        puck_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(puckTextureAtlas , mActivity , "puck.png", 0 ,0 );

        malletTextureAtlas.load();
        puckTextureAtlas.load();
    }

    public MainActivity getMainActivity(){

        return mActivity;
    }

    public void unloadMenuTextures(){


    }
}
