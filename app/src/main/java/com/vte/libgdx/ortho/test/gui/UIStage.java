package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.Styles;

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
    protected TextureAtlas atlas;
    protected Skin skin;

    public TextureAtlas getTextureAtlas()
    {
        return atlas;
    }
    public Skin getSkin()
    {
        return skin;
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

    protected String atlasPath() {
        return "data/skins/ui.atlas";
    }


    protected String skinPath() {
        return AssetsUtility.UI_SKIN_PATH;
    }

    protected void styleSkin(Skin skin, TextureAtlas atlas) {
        new Styles().styleSkin(skin, atlas);
    }

    protected void init()
    {
        atlas = new TextureAtlas(atlasPath());
        skin = new Skin(atlas);
        String skinPath = skinPath();
        if (skinPath != null)
            skin.load(Gdx.files.internal(skinPath));
        styleSkin(skin, atlas);
    }
}
