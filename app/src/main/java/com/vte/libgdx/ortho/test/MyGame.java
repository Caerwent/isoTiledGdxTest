package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vte.libgdx.ortho.test.screens.GameScreen;

/**
 * MyGame class that extends Game, which implements
 * ApplicationListener. It will be used as the "Main" libGDX class, the starting
 * point basically, in the core libGDX project. Its VIEWPORT and BATCHER are
 * used by the all screens. The Viewport is updated when the device's
 * orientation is changed. The Batcher is created once since it is memory
 * expensive.
 */
public class MyGame extends Game {
    public static float SCALE_FACTOR = 1.0F / 32.0F;

    public SpriteBatch batch;
    public BitmapFont font;

    public void create() {
        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();

        this.setScreen(new GameScreen());
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }


}
