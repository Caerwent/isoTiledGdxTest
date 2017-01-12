package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by vincent on 12/01/2017.
 */

public class InputComponent implements Component {

    protected InputProcessor mInputProcessor;

    public InputComponent(InputProcessor aInputProcessor) {
        mInputProcessor = aInputProcessor;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (mInputProcessor != null) {
            return mInputProcessor.touchDown(screenX, screenY, pointer, button);
        } else return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (mInputProcessor != null) {
            return mInputProcessor.touchUp(screenX, screenY, pointer, button);
        } else return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (mInputProcessor != null) {
            return mInputProcessor.touchDragged(screenX, screenY, pointer);
        } else return false;
    }
}
