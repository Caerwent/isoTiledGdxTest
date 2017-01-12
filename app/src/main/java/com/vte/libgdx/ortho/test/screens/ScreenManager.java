package com.vte.libgdx.ortho.test.screens;

/**
 * Created by vincent on 06/01/2017.
 */

public class ScreenManager {
    static private ScreenManager s_instance;

    private GameScreen mScreen;

    public static ScreenManager getInstance() {
        if(s_instance==null)
            s_instance = new ScreenManager();
        return s_instance;
    }

    public GameScreen getScreen()
    {
        return mScreen;
    }

    public void setScreen(GameScreen aScreen)
    {
        mScreen = aScreen;
    }

}
