package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.quests.QuestManager;
import com.vte.libgdx.ortho.test.screens.GameScreen;
import com.vte.libgdx.ortho.test.screens.GenericUI;
import com.vte.libgdx.ortho.test.screens.LoadingScreen;
import com.vte.libgdx.ortho.test.screens.MainMenuScreen;

import static com.vte.libgdx.ortho.test.MyGame.ScreenType.MainMenu;

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

    static private MyGame s_instance;

    public static enum ScreenType {
        MainMenu,
        MainGame,
        LoadingGame,
    }

    public Screen getScreenType(ScreenType screenType) {
        switch (screenType) {
            case MainMenu:
                return mMainMenuScreen;
            case LoadingGame:
                return mLoadingScreen;
            case MainGame:
                if (mGameScreen == null) {
                    mGameScreen = new GameScreen();
                    mGameScreen.loadMap("ortho", null);
                }
                return mGameScreen;

            default:
                return mMainMenuScreen;
        }

    }

    public static MyGame getInstance() {
        return s_instance;
    }

    public SpriteBatch batch;
    public BitmapFont font;
    private MainMenuScreen mMainMenuScreen;
    private LoadingScreen mLoadingScreen;
    private GameScreen mGameScreen;

    public void create() {
        s_instance = this;
        GenericUI.createInstance();
        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
        mMainMenuScreen = new MainMenuScreen();
        mLoadingScreen = new LoadingScreen();
        this.setScreen(getScreenType(MainMenu));
    }

    public void render() {
        super.render(); //important!
    }

    public void newProfile() {
        Profile.getInstance().newProfile();
        QuestManager.getInstance().restoreQuestsFromProfile();
        if (mGameScreen != null)
            mGameScreen.dispose();
        mGameScreen = new GameScreen();
        mGameScreen.loadMap("ortho", null);
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        mMainMenuScreen.dispose();
        mLoadingScreen.dispose();
        if (mGameScreen != null)
            mGameScreen.dispose();
    }


}
