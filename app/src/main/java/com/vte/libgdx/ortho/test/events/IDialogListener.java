package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.dialogs.GameDialog;

/**
 * Created by vincent on 12/01/2017.
 */

public interface IDialogListener {

    public void onStartDialog(GameDialog aDialog);
    public void onStopDialog(GameDialog aDialog);
}
