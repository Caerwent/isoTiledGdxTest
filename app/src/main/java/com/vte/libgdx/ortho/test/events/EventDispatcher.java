package com.vte.libgdx.ortho.test.events;

import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.dialogs.GameDialog;

/**
 * Created by vincent on 12/01/2017.
 */

public class EventDispatcher implements IDialogListener {
    private static EventDispatcher _instance = null;

    private Array<IDialogListener> mDialogListeners = new Array<IDialogListener>();

    public static EventDispatcher getInstance() {
        if (_instance == null) {
            _instance = new EventDispatcher();
        }

        return _instance;
    }

    public void addDialogListener(IDialogListener aListener) {
        if (!mDialogListeners.contains(aListener, true)) {
            mDialogListeners.add(aListener);
        }
    }

    public void removeDialogListener(IDialogListener aListener) {
        if (mDialogListeners.contains(aListener, true)) {
            mDialogListeners.removeValue(aListener, true);
        }
    }

    @Override
    public void onStartDialog(GameDialog aDialog) {
        for (IDialogListener listener : mDialogListeners) {
            listener.onStartDialog(aDialog);
        }
    }

    @Override
    public void onStopDialog(GameDialog aDialog) {
        for (IDialogListener listener : mDialogListeners) {
            listener.onStopDialog(aDialog);
        }
    }
}
