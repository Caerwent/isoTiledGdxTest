package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.physics.box2d.Box2D.init;

/**
 * Created by gwalarn on 16/11/16.
 */

public class UIStage extends Stage {

    private static UIStage sInstance;

    public static synchronized void createInstance(Viewport aViewPort)
    {
        sInstance = new UIStage(aViewPort);
    }

    public static UIStage getInstance()
    {
        return sInstance;
    }

    public UIStage () {
        super();
        init();
    }

    /** Creates a stage with the specified viewport. The stage will use its own {@link Batch} which will be disposed when the stage
     * is disposed. */
    public UIStage (Viewport viewport) {
        super(viewport);
        init();
    }

    /** Creates a stage with the specified viewport and batch. This can be used to avoid creating a new batch (which can be somewhat
     * slow) if multiple stages are used during an application's life time.
     * @param batch Will not be disposed if {@link #dispose()} is called, handle disposal yourself. */
    public UIStage (Viewport viewport, Batch batch) {
        super(viewport, batch);
        init();
    }


}
