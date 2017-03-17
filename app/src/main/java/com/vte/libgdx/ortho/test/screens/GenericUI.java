package com.vte.libgdx.ortho.test.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.Styles;

/**
 * Created by vincent on 23/01/2017.
 */

public class GenericUI {
    private static GenericUI sInstance;

    public static synchronized void createInstance()
    {
        sInstance = new GenericUI();
    }

    public static GenericUI getInstance()
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

    public GenericUI () {
        super();
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
