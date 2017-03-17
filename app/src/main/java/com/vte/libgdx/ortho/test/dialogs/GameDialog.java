package com.vte.libgdx.ortho.test.dialogs;

import com.badlogic.gdx.utils.Array;

/**
 * Created by vincent on 12/01/2017.
 */

public class GameDialog {

    protected String id;
    protected Array<GameDialogStep> dialogs;

    public String getId()
    {
        return id;
    }

    public Array<GameDialogStep> getDialogs()
    {
        return dialogs;
    }
}
