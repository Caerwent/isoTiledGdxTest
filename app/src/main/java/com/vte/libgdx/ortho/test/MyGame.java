package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.systems.BobSystem;
import com.vte.libgdx.ortho.test.entity.systems.CollisionSystem;
import com.vte.libgdx.ortho.test.entity.systems.MovementSystem;
import com.vte.libgdx.ortho.test.entity.systems.PathRenderSystem;

import static com.vte.libgdx.ortho.test.Bob.bobSpriteSheet;
import static com.vte.libgdx.ortho.test.Settings.TARGET_HEIGHT;
import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * MyGame class that extends Game, which implements
 * ApplicationListener. It will be used as the "Main" libGDX class, the starting
 * point basically, in the core libGDX project. Its VIEWPORT and BATCHER are
 * used by the all screens. The Viewport is updated when the device's
 * orientation is changed. The Batcher is created once since it is memory
 * expensive.
 */
public class MyGame extends Game implements InputProcessor {

    public static float SCALE_FACTOR = 1.0F / 32.0F;
    public Rectangle viewport;
    public int orientation;

    private TiledMap map;
    // private IsoTileMapRendererWithSprites renderer;
    private MapAndSpritesRenderer2 renderer;
    private OrthographicCamera camera;
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

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        EntityEngine.getInstance();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, (w / h) * 10, 10);
        camera.zoom = 2;
        camera.update();

        cameraController = new OrthoCamController(camera);

        font = new BitmapFont();
        batch = new SpriteBatch();
        pathRenderer = new ShapeRenderer();
        pathRenderer.setAutoShapeType(true);
        pathRenderer.setProjectionMatrix(camera.combined);

        accumulator = 0.0;
        currentTime = TimeUtils.millis() / 1000.0;

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("data/maps/ortho.tmx", TiledMap.class);
        assetManager.finishLoading();
        map = assetManager.get("data/maps/ortho.tmx");
        renderer = new MapAndSpritesRenderer2(map, SCALE_FACTOR);

        MapBodyManager.createInstance(map);
// instantiate the bob
        bob = new Bob();
// load the bob texture with image from file
        bobSpriteSheet = new
                Texture(Gdx.files.internal("data/characters/universal_walk.png"));
// initialize Bob
        bob.initialize(w, h, bobSpriteSheet);
        renderer.addSprite(bob);

        bobController = new ChararcterMoveController(camera, bob);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(bobController);
        inputMultiplexer.addProcessor(cameraController);
        Gdx.input.setInputProcessor(inputMultiplexer);

        EntityEngine.getInstance().addSystem(new MovementSystem());
        // EntityEngine.getInstance().addSystem(new VisualRenderSystem(camera));
        EntityEngine.getInstance().addSystem(new BobSystem());
        EntityEngine.getInstance().addSystem(new CollisionSystem());
        EntityEngine.getInstance().addSystem(new PathRenderSystem(pathRenderer));


    }


    @Override
    public void render() {
        super.render();
        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);
        float deltaTime = (float) frameTime;

        currentTime = newTime;

        Gdx.gl.glClearColor(100f / 255f, 100f / 255f, 250f / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        pathRenderer.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render();
        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        // bob.render(batch);
        batch.end();

        EntityEngine.getInstance().update(deltaTime);

        // checkTileTouched();
    }

    @Override
    public void resize(int width, int height) {

        // Calculate new viewport when the orientation is changed
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
        //bob.setPosition(viewport.getWidth() / 2, viewport.getHeight() / 2);
        if (height > width)
            orientation = 0;
        else
            orientation = 1;

    }

    @Override
    public void dispose() {

        EntityEngine.getInstance().removeAllEntities();
        bobSpriteSheet.dispose();
        super.dispose();
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
            case Keys.ESCAPE:
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
