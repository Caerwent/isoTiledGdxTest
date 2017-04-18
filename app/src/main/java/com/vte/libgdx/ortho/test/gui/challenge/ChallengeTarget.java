package com.vte.libgdx.ortho.test.gui.challenge;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.vte.libgdx.ortho.test.gui.InventorySlotSource;

/**
 * Created by vincent on 24/02/2017.
 */

public class ChallengeTarget extends Target {

    ChallengeUI mChallengeUI;


    public ChallengeTarget(Actor targetActor, ChallengeUI aChallengeUI) {
        super(targetActor);
        mChallengeUI = aChallengeUI;

    }

    @Override
    public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
        return true;
    }

    @Override
    public void reset(Source source, Payload payload) {
    }

    @Override
    public void drop(Source source, Payload payload, float x, float y, int pointer) {

        mChallengeUI.onItemDrop(((InventorySlotSource) source).getSourceSlot());

    }
}
