package com.vte.libgdx.ortho.test;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Since we are using libGDX, the engine allows us deploying the application to
 * different platforms. Therefore, this class is the corresponding launcher of
 * an Android application. It initialises the game as such.
 */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new MyGame(), config);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void exit () {
        super.exit();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}