package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by gwalarn on 16/11/16.
 */

public class UIStage extends Stage {

    public UIStage () {
        super();
    }

    /** Creates a stage with the specified viewport. The stage will use its own {@link Batch} which will be disposed when the stage
     * is disposed. */
    public UIStage (Viewport viewport) {
        super(viewport);
    }

    /** Creates a stage with the specified viewport and batch. This can be used to avoid creating a new batch (which can be somewhat
     * slow) if multiple stages are used during an application's life time.
     * @param batch Will not be disposed if {@link #dispose()} is called, handle disposal yourself. */
    public UIStage (Viewport viewport, Batch batch) {
        super(viewport, batch);
    }
}
