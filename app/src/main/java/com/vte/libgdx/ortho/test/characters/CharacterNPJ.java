package com.vte.libgdx.ortho.test.characters;

import com.vte.libgdx.ortho.test.dialogs.DialogsManager;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.quests.QuestManager;

/**
 * Created by vincent on 13/01/2017.
 */

public class CharacterNPJ extends Character {
    public CharacterNPJ(CharacterDef aDef) {
        super(aDef);
    }
    public void onInteractionStart()
    {
        QuestManager.getInstance().onNPJ(this);
        if(getDialogId()!=null) {
            EventDispatcher.getInstance().onStartDialog(DialogsManager.getInstance().getDialog(getDialogId()));
        }
    }
    public void onInteractionStop()
    {
        if(getDialogId()!=null) {
            EventDispatcher.getInstance().onStopDialog(DialogsManager.getInstance().getDialog(getDialogId()));
        }

    }
}
