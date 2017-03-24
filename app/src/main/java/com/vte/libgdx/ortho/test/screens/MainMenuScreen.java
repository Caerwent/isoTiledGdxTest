package com.vte.libgdx.ortho.test.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.persistence.PersistenceProvider;

import static com.vte.libgdx.ortho.test.Settings.TARGET_HEIGHT;
import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * Created by vincent on 23/01/2017.
 */

public class MainMenuScreen implements Screen {

    private Stage _stage;

    public MainMenuScreen() {

        //creation
        _stage = new Stage(new ExtendViewport(TARGET_WIDTH, TARGET_HEIGHT));
        Table table = new Table();
        table.setFillParent(true);

        TextButton loadGameButton = new TextButton(AssetsUtility.getString("ui_continue_game"), GenericUI.getInstance().getSkin());
        TextButton newGameButton = new TextButton(AssetsUtility.getString("ui_new_game"), GenericUI.getInstance().getSkin());
        TextButton settingsButton = new TextButton(AssetsUtility.getString("ui_settings"), GenericUI.getInstance().getSkin());
        TextButton exitButton = new TextButton(AssetsUtility.getString("ui_exit"), GenericUI.getInstance().getSkin());


        //Layout
        if (PersistenceProvider.isProfileExist()) {
            table.add(loadGameButton).spaceBottom(10).row();
        }
        table.add(newGameButton).spaceBottom(10).row();
        table.add(settingsButton).spaceBottom(10).row();
        table.add(exitButton).spaceBottom(10).row();

        _stage.addActor(table);

        //Listeners
        newGameButton.addListener(new ClickListener() {
                                      @Override
                                      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                          AudioManager.getInstance().onAudioEvent(AudioManager.UI_CLIC_SOUND);
                                          return true;
                                      }

                                      @Override
                                      public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                                          new Dialog("",  GenericUI.getInstance().getSkin(), "dialog") {
                                              protected void result (Object object) {
                                                  if(object instanceof Boolean)
                                                  {
                                                      if(((Boolean) object).booleanValue())
                                                      {
                                                          MyGame.getInstance().newProfile();
                                                          MyGame.getInstance().setScreen(MyGame.ScreenType.MainGame);
                                                      }
                                                  }
                                              }
                                          }.text(AssetsUtility.getString("ui_dialog_new_profile_msg")).
                                                  button(AssetsUtility.getString("ui_dialog_continue"), true).
                                                  button(AssetsUtility.getString("ui_dialog_cancel"), false).key(Input.Keys.ENTER, true)
                                                  .key(Input.Keys.ESCAPE, false).show(_stage);

                                      }
                                  }
        );

        loadGameButton.addListener(new ClickListener() {

                                       @Override
                                       public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                           AudioManager.getInstance().onAudioEvent(AudioManager.UI_CLIC_SOUND);
                                           return true;
                                       }

                                       @Override
                                       public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                           MyGame.getInstance().setScreen(MyGame.ScreenType.MainGame);
                                       }
                                   }
        );
        settingsButton.addListener(new ClickListener() {

                                       @Override
                                       public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                           AudioManager.getInstance().onAudioEvent(AudioManager.UI_CLIC_SOUND);
                                           return true;
                                       }

                                       @Override
                                       public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                           MyGame.getInstance().setScreen(MyGame.ScreenType.Settings);
                                       }
                                   }
        );

        exitButton.addListener(new ClickListener() {

                                   @Override
                                   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                       AudioManager.getInstance().onAudioEvent(AudioManager.UI_CLIC_SOUND);
                                       return true;
                                   }

                                   @Override
                                   public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                       Gdx.app.exit();
                                   }

                               }
        );


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




