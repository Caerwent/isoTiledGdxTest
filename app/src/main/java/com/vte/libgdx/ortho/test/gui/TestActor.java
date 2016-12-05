package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.Styles;

/**
 * Created by gwalarn on 16/11/16.
 */

public class TestActor extends Group {

    protected final Table mainTable = new Table();
    protected final Table secondTable = new Table();
    public TextureAtlas atlas;
    public Skin skin;
    /**
     * the duration of the screen transition for the screenOut method
     */
    public float dur;

    protected String atlasPath() {
        return "data/skins/tex.atlas";
    }


    protected String skinPath() {
        return null;
    }

    protected void styleSkin(Skin skin, TextureAtlas atlas) {
        new Styles().styleSkin(skin, atlas);
    }

    public TestActor() {
        atlas = new TextureAtlas(atlasPath());
        skin = new Skin(atlas);
        String skinPath = skinPath();
        if (skinPath != null)
            skin.load(Gdx.files.internal(skinPath));
        styleSkin(skin, atlas);
        mainTable.setSize(50, 50);
        secondTable.setSize(100, Settings.TARGET_HEIGHT - 50);

        this.addActor(mainTable);
        mainTable.setBackground(skin.getDrawable("window1"));
        mainTable.setColor(skin.getColor("dark-blue"));
        mainTable.setSkin(skin);
        mainTable.add("I");
        mainTable.row();

        secondTable.setPosition(50, 25);
        secondTable.setBackground(skin.getDrawable("window1"));
        secondTable.setColor(skin.getColor("lt-blue"));
        secondTable.setSkin(skin);
        secondTable.setTouchable(Touchable.enabled);
        secondTable.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        mainTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // app.switchScreens(new MainScreen(app));
                Gdx.app.log("!!", "klick");
                if (getChildren().contains(secondTable, true)) {
                    removeActor(secondTable);
                } else {
                    addActor(secondTable);
                }
            }
        });
        mainTable.setTouchable(Touchable.enabled);
    }
}
