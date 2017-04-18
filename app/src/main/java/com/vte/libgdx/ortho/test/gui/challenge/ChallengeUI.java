package com.vte.libgdx.ortho.test.gui.challenge;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.vte.libgdx.ortho.test.gui.InventorySlot;
import com.vte.libgdx.ortho.test.gui.UIStage;
import com.vte.libgdx.ortho.test.interactions.InteractionChallenge;
import com.vte.libgdx.ortho.test.persistence.GameSession;
import com.vte.libgdx.ortho.test.screens.GenericUI;

/**
 * Created by vincent on 04/04/2017.
 */

public class ChallengeUI extends Window {

    public static enum ChallengeType {
        TEST(ChallengeTest.class),
        MACHINE(ChallengeMachine.class);

        protected Class mClass;

        ChallengeType(Class aClass) {
            mClass = aClass;
        }
    }

    public static ChallengeUI createInstance(ChallengeType aType) {

        try {
            return (ChallengeUI) aType.mClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    protected Table mTable;
    protected Image mClose;
    protected Table mContent;
    protected InteractionChallenge mInteractionChallenge;

    public ChallengeUI() {
        super("", GenericUI.getInstance().getSkin(), "solidbackground");

        mClose = new Image(GenericUI.getInstance().getTextureAtlas().findRegion("checkbox_cross"));
        mClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIStage.getInstance().closeChallengeUI();
            }
        });
        add(mClose).top().right().expandX();
        row();
        mContent = new Table();
        add(mContent).top().left().fill().expand();
        createView();

        createDragAndDropTarget(UIStage.getInstance().getMainHUD().getItemDragAndDrop());

    }

    public void setInteractionChallenge(InteractionChallenge aInteractionChallenge) {
        mInteractionChallenge = aInteractionChallenge;
    }

    public void restoreFromPersistence() {
        if (mInteractionChallenge != null) {
            mInteractionChallenge.restoreFromPersistence();
        }
    }

    protected void saveInPersistence() {
        if (mInteractionChallenge != null) {
            mInteractionChallenge.saveInPersistence();
        }
    }

    protected void createView() {

    }

    protected void createDragAndDropTarget(DragAndDrop aDragAndDrop) {

    }

    protected void removeDragAndDropTarget(DragAndDrop aDragAndDrop) {

    }

    public void onItemDrop(InventorySlot aSourceSlot) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        //draw content

    }

    @Override
    public void act(float delta) {
        // update content
        super.act(delta);
    }

    public void release() {
        if (UIStage.getInstance().getMainHUD() != null) {
            removeDragAndDropTarget(UIStage.getInstance().getMainHUD().getItemDragAndDrop());
        }
    }

    public void restoreFromPersistence(GameSession aGameSession) {

    }

    public GameSession saveInPersistence(GameSession aGameSession) {
        return aGameSession;
    }
}
