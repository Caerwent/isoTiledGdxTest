package com.vte.libgdx.ortho.test.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.Bob;
import com.vte.libgdx.ortho.test.ChararcterMoveController;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.OrthoCamController;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;
import com.vte.libgdx.ortho.test.characters.CharactersManager;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.systems.BobSystem;
import com.vte.libgdx.ortho.test.entity.systems.CollisionSystem;
import com.vte.libgdx.ortho.test.entity.systems.MovementSystem;
import com.vte.libgdx.ortho.test.entity.systems.PathRenderSystem;
import com.vte.libgdx.ortho.test.gui.TestActor;
import com.vte.libgdx.ortho.test.gui.UIStage;
import com.vte.libgdx.ortho.test.map.MapAndSpritesRenderer2;
import com.vte.libgdx.ortho.test.map.IMapInteraction;

import static com.vte.libgdx.ortho.test.Settings.TARGET_HEIGHT;
import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * Created by gwalarn on 16/11/16.
 */

public class GameScreen implements Screen, InputProcessor {

    public final static String TAG = GameScreen.class.getSimpleName();
    public Rectangle viewport;
    public int orientation;

    private TiledMap map;
    // private IsoTileMapRendererWithSprites renderer;
    private MapAndSpritesRenderer2 renderer;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    //private OrthoCamController cameraController;
    private OrthoCamController cameraController;
    private ChararcterMoveController bobController;
    private AssetManager assetManager;
    private BitmapFont font;
    private SpriteBatch batch;
    ShapeRenderer pathRenderer;
    private double accumulator;
    private double currentTime;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private float physicsDeltaTime = 1.0f / 60.0f;

    Bob bob;




    public GameScreen() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        EntityEngine.getInstance();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, (w / h) * 10, 10);
        //camera.zoom = 2;
        camera.update();

        cameraController = new OrthoCamController(camera);

        uiCamera = new OrthographicCamera(TARGET_WIDTH, TARGET_HEIGHT);

        font = new BitmapFont();
        batch = new SpriteBatch();
        pathRenderer = new ShapeRenderer();
        pathRenderer.setAutoShapeType(true);
        pathRenderer.setProjectionMatrix(camera.combined);

        accumulator = 0.0;
        currentTime = TimeUtils.millis() / 1000.0;

        CharactersManager.getInstance();
        assetManager = new AssetManager();
       /* assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("data/maps/ortho.tmx", TiledMap.class);
        assetManager.finishLoading();
        map = assetManager.get("data/maps/ortho.tmx");*/

        AssetsUtility.loadMapAsset("data/maps/ortho.tmx");
        if( AssetsUtility.isAssetLoaded("data/maps/ortho.tmx") ) {
            map = AssetsUtility.getMapAsset("data/maps/ortho.tmx");
        }else{
            Gdx.app.debug(TAG, "Map not loaded");
            return;
        }
        renderer = new MapAndSpritesRenderer2(map, MyGame.SCALE_FACTOR);
        UIStage.createInstance(new ExtendViewport(TARGET_WIDTH, TARGET_HEIGHT, uiCamera));

        MapBodyManager.createInstance(map);

        // instantiate the bob
        bob = new Bob();
// initialize Bob
        bob.initialize();
        renderer.addSprite(bob);

        bobController = new ChararcterMoveController(camera, bob);
        Array<IMapInteraction> mapControls = MapBodyManager.getInstance().getInteractions();
        for (IMapInteraction control : mapControls) {
            if (control.getInteractionType() == IMapInteraction.Type.START) {
                bob.setPosition(control.getX(), control.getY());
                break;
            }
        }
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(UIStage.getInstance());
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(bobController);
        inputMultiplexer.addProcessor(cameraController);

        Gdx.input.setInputProcessor(inputMultiplexer);

        EntityEngine.getInstance().addSystem(new MovementSystem());
        // EntityEngine.getInstance().addSystem(new VisualRenderSystem(camera));
        EntityEngine.getInstance().addSystem(new BobSystem());
        EntityEngine.getInstance().addSystem(new CollisionSystem());
        EntityEngine.getInstance().addSystem(new PathRenderSystem(pathRenderer));


        UIStage.getInstance().addActor(new TestActor());
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
       /* double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);
        float deltaTime = (float) frameTime;

        currentTime = newTime;*/
        UIStage.getInstance().act(delta);
        // set viewport
    //    Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
    //            (int) viewport.width, (int) viewport.height);

        // clear previous frame
        Gdx.gl.glClearColor(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        pathRenderer.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render();
        batch.begin();
        //font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        batch.end();
        UIStage.getInstance().draw();
        EntityEngine.getInstance().update(delta/*Time*/);

    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);

        if (aspectRatio > Settings.ASPECT_RATIO) {
            scale = (float) height / (float) TARGET_HEIGHT;
            crop.x = (width - TARGET_WIDTH * scale) / 2f;
        } else if (aspectRatio < Settings.ASPECT_RATIO) {
            scale = (float) width / (float) Settings.TARGET_WIDTH;
            crop.y = (height - TARGET_HEIGHT * scale) / 2f;
        } else {
            scale = (float) width / (float) Settings.TARGET_WIDTH;
        }

        float w = (float) TARGET_WIDTH * scale;
        float h = (float) TARGET_HEIGHT * scale;

        viewport = new Rectangle(crop.x, crop.y, w, h);
        if (height > width)
            orientation = 0;
        else
            orientation = 1;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        EntityEngine.getInstance().removeAllEntities();
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean keyDown(int keyCode) {

        switch (keyCode) {
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            default:
                //  Log.out("unknown key");

        }
        return true;
    }

    @Override
    public boolean keyTyped(char arg0) {
        return false;
    }

    @Override
    public boolean keyUp(int arg0) {
        return false;
    }

    @Override
    public boolean scrolled(int arg0) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {

        final Vector3 curr = new Vector3();
        camera.unproject(curr.set(x, y, 0));


        Gdx.app.debug("DEBUG", "touchX=" + curr.x + " touchY=" + curr.y);
        return false;
    }
}
