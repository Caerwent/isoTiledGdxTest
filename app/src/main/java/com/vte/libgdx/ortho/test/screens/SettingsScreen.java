package com.vte.libgdx.ortho.test.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.persistence.PersistenceProvider;

import static com.vte.libgdx.ortho.test.Settings.TARGET_HEIGHT;
import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * Created by vincent on 17/03/2017.
 */

public class SettingsScreen implements Screen {

    private Stage _stage;

    private Slider mVolumeSlider;
    private CheckBox mVolumeActivation;
    private CheckBox[] mLanguagesBox;

    public SettingsScreen() {

        //creation
        _stage = new Stage(new ExtendViewport(TARGET_WIDTH, TARGET_HEIGHT));
        Table table = new Table();
        table.setFillParent(true);

        TextButton exitButton = new TextButton(AssetsUtility.getString("ui_settings_close"), GenericUI.getInstance().getSkin());
        exitButton.addListener(new ClickListener() {

                                   @Override
                                   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                       AudioManager.getInstance().onAudioEvent(AudioManager.UI_CLIC_SOUND);
                                       return true;
                                   }

                                   @Override
                                   public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                       MyGame.getInstance().setScreen(MyGame.ScreenType.MainMenu);
                                   }
                               }
        );
        final Label langLabel = new Label(AssetsUtility.getString("ui_settings_language_label"), GenericUI.getInstance().getSkin());
        table.add(langLabel).left().spaceTop(10);
        table.row();

        mLanguagesBox = new CheckBox[Settings.SupportedLanguages.values().length];
        int idx = 0;
        for (Settings.SupportedLanguages language : Settings.SupportedLanguages.values()) {
            Table langTab = new Table();
            Image img = new Image();
            img.setDrawable(new TextureRegionDrawable(GenericUI.getInstance().getTextureAtlas().findRegion("flag_" + language.getValue())));
            mLanguagesBox[idx] = new CheckBox("", GenericUI.getInstance().getSkin());
            if (PersistenceProvider.getInstance().getSettings().language != null && language.getValue().compareTo(PersistenceProvider.getInstance().getSettings().language) == 0) {
                mLanguagesBox[idx].setChecked(true);
            }
            langTab.add(img);
            langTab.row();
            langTab.add(mLanguagesBox[idx]);
            table.add(langTab);
            mLanguagesBox[idx].addListener(new InputListener() {
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    CheckBox theBox = (CheckBox) event.getListenerActor();
                    if (theBox.isChecked()) {
                        selectLanguage(theBox);
                    }

                }

                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                ;
            });
            idx++;
        }
        table.row();
        mVolumeActivation = new CheckBox(AssetsUtility.getString("ui_settings_activated_music"), GenericUI.getInstance().getSkin());
        mVolumeActivation.setChecked(PersistenceProvider.getInstance().getSettings().musicActivated);
        mVolumeActivation.addListener(new InputListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // set value in persistence
                PersistenceProvider.getInstance().getSettings().musicActivated = mVolumeActivation.isChecked();
                PersistenceProvider.getInstance().saveSettings();
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            ;
        });
        table.add(mVolumeActivation).spaceBottom(10);
        table.row();
        mVolumeSlider = new Slider(0F, 1F, 0.1F, false, GenericUI.getInstance().getSkin());
        final Label volLabel = new Label(AssetsUtility.getString("ui_settings_volume"), GenericUI.getInstance().getSkin());
        final Label minVolLabel = new Label(AssetsUtility.getString("ui_settings_volume_min"), GenericUI.getInstance().getSkin());
        final Label maxVolLabel = new Label(AssetsUtility.getString("ui_settings_volume_max"), GenericUI.getInstance().getSkin());
        table.add(volLabel).left();
        table.row();
        table.add(minVolLabel).padRight(10);
        table.add(mVolumeSlider).padRight(10);
        table.add(maxVolLabel).padRight(10);
        table.row();
        table.add(exitButton).spaceTop(10).spaceBottom(10);
        table.row();
        _stage.addActor(table);

        mVolumeSlider.setValue(PersistenceProvider.getInstance().getSettings().musicVolume);
        mVolumeSlider.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;

                float value = slider.getValue();

                // set value in persistence
                PersistenceProvider.getInstance().getSettings().musicVolume = value;
                PersistenceProvider.getInstance().saveSettings();

            }

        });

    }

    private void selectLanguage(CheckBox aBox) {
        for (int i = 0; i < mLanguagesBox.length; i++) {
            if (mLanguagesBox[i] == aBox) {
                PersistenceProvider.getInstance().getSettings().language = Settings.SupportedLanguages.values()[i].getValue();
                PersistenceProvider.getInstance().saveSettings();
                AssetsUtility.reloadStrings();
            } else {
                mLanguagesBox[i].setChecked(false);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        _stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(_stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        _stage.dispose();
    }
}