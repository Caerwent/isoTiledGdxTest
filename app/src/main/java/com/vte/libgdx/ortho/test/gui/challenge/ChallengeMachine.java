package com.vte.libgdx.ortho.test.gui.challenge;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.gui.InventorySlot;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;
import com.vte.libgdx.ortho.test.persistence.GameSession;
import com.vte.libgdx.ortho.test.screens.GenericUI;

/**
 * Created by vincent on 07/04/2017.
 */

public class ChallengeMachine extends ChallengeUI {

    private static final int LIQUID_FULL_X = 38;
    private static final int LIQUID_FULL_Y = -116;
    private static final int LIQUID_RESULT_NONE = 0;
    private static final int LIQUID_RESULT_BLUE = 1;
    private static final int LIQUID_RESULT_RED = 2;
    private static final int LIQUID_RESULT_GREEN = 3;
    private static final int LIQUID_RESULT_BLUE_RED = 4;
    private static final int LIQUID_RESULT_BLUE_GREEN = 5;
    private static final int LIQUID_RESULT_RED_GREEN = 6;
    private static final int LIQUID_RESULT_BLUE_GREEN_RED = 7;

    private static final String KEY_HAS_BLUE = "has_blue";
    private static final String KEY_HAS_RED = "has_red";
    private static final String KEY_HAS_GREEN = "has_green";
    private static final String KEY_MIX_RESULT = "mix_result";


    private TextureAtlas mAtlas;
    private Image mBackground;
    private Image mBlue, mBubbleBlue;
    private Image mRed, mBubbleRed;
    private Image mGreen, mBubbleGreen;
    private Image mMixImg;
    private Image mActivatorOn;
    private Image mActivatorOff;
    private ChallengeTarget mBackgroundTarget;
    private Group mGroup;
    private Table mHelpTable;

    private boolean mHasBlue = false;
    private boolean mHasRed = false;
    private boolean mHasGreen = false;
    private int mMixResult = LIQUID_RESULT_NONE;

    public ChallengeMachine() {
        super();


    }

    @Override
    protected void createView() {
        mAtlas = new TextureAtlas("data/challenges/the_machine.atlas");
        mGroup = new Group();
        mBackground = new Image(mAtlas.findRegion("theMachine_bkg"));
        mBlue = new Image(mAtlas.findRegion("theMachine_liquid_blue"));
        mBlue.setScaling(Scaling.none);
        mBlue.setVisible(false);
        mBubbleBlue = new Image(mAtlas.findRegion("theMachine_bubble_on"));
        mBubbleBlue.setScaling(Scaling.none);
        mBubbleBlue.setVisible(false);
        mRed = new Image(mAtlas.findRegion("theMachine_liquid_red"));
        mRed.setScaling(Scaling.none);
        mRed.setVisible(false);
        mBubbleRed = new Image(mAtlas.findRegion("theMachine_bubble_on"));
        mBubbleRed.setScaling(Scaling.none);
        mBubbleRed.setVisible(false);
        mGreen = new Image(mAtlas.findRegion("theMachine_liquid_green"));
        mGreen.setScaling(Scaling.none);
        mGreen.setVisible(false);
        mBubbleGreen = new Image(mAtlas.findRegion("theMachine_bubble_on"));
        mBubbleGreen.setScaling(Scaling.none);
        mBubbleGreen.setVisible(false);

        mActivatorOn = new Image(mAtlas.findRegion("theMachine_liquid_activator_on"));
        mActivatorOn.setScaling(Scaling.none);
        mActivatorOn.setVisible(false);
        mActivatorOn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onActivate(false);
            }
        });

        mActivatorOff = new Image(mAtlas.findRegion("theMachine_liquid_activator_off"));
        mActivatorOff.setScaling(Scaling.none);
        mActivatorOff.setVisible(true);
        mActivatorOff.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onActivate(true);
            }
        });

        mHelpTable = new Table();
        mContent.top().add(mHelpTable).expandX().padBottom(20);
        mContent.row().top().fill().center();
        Image helpVialBlue = new Image(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.VialBlue).getTextureRegion());
        helpVialBlue.setScaling(Scaling.none);
        mHelpTable.add(helpVialBlue);

        Label helpPlus=new Label("+", GenericUI.getInstance().getSkin(), "big-font");
        helpPlus.setAlignment(Align.center);
        mHelpTable.add(helpPlus);

        Image helpVialGreen = new Image(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.VialGreen).getTextureRegion());
        helpVialGreen.setScaling(Scaling.none);
        mHelpTable.add(helpVialGreen);

        Label helpPlus2=new Label("+", GenericUI.getInstance().getSkin(), "big-font");
        helpPlus2.setAlignment(Align.center);
        mHelpTable.add(helpPlus2);

        Image helpVialRed = new Image(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.VialRed).getTextureRegion());
        helpVialRed.setScaling(Scaling.none);
        mHelpTable.add(helpVialRed);

        Label helpPlus3=new Label("+", GenericUI.getInstance().getSkin(), "big-font");
        helpPlus3.setAlignment(Align.center);
        mHelpTable.add(helpPlus3);

        Image helpActivator = new Image(mAtlas.findRegion("theMachine_liquid_activator_on"));
        helpActivator.setScaling(Scaling.none);
        mHelpTable.add(helpActivator);

        mContent.add(mGroup);
        mGroup.addActor(mBackground);
        mBackground.setPosition(0, -mBackground.getHeight());
        mGroup.addActor(mBlue);
        mGroup.addActor(mBubbleBlue);
        mBlue.setPosition(153, -137);
        mBubbleBlue.setPosition(152,-56);
        mGroup.addActor(mGreen);
        mGroup.addActor(mBubbleGreen);
        mGreen.setPosition(182, -137);
        mBubbleGreen.setPosition(182,-56);
        mGroup.addActor(mRed);
        mGroup.addActor(mBubbleRed);
        mRed.setPosition(208, -137);
        mBubbleRed.setPosition(210,-56);

        mMixImg = new Image();
        mMixImg.setScaling(Scaling.none);
        mMixImg.setVisible(false);
        mGroup.addActor(mMixImg);
        mMixImg.setPosition(LIQUID_FULL_X, LIQUID_FULL_Y);

        mGroup.addActor(mActivatorOn);
        mActivatorOn.setPosition(36 - mActivatorOn.getWidth(), -129 - mActivatorOn.getHeight() / 2 -3);
        mGroup.addActor(mActivatorOff);
        mActivatorOff.setPosition(36 - mActivatorOn.getWidth(), -129 - mActivatorOff.getHeight() / 2 +3);



    }

    public void restoreFromPersistence(GameSession aGameSession) {
        Boolean hasBlue = (Boolean) aGameSession.getSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_HAS_BLUE);
        if (hasBlue != null) {
            mHasBlue = hasBlue.booleanValue();
        }
        Boolean hasGreen = (Boolean) aGameSession.getSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_HAS_GREEN);
        if (hasGreen != null) {
            mHasGreen = hasGreen.booleanValue();
        }
        Boolean hasRed = (Boolean) aGameSession.getSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_HAS_RED);
        if (hasRed != null) {
            mHasRed = hasRed.booleanValue();
        }
        Integer mixResult = (Integer) aGameSession.getSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_MIX_RESULT);
        if (mixResult != null) {
            mMixResult = mixResult.intValue();
        }

        updateUI();
    }

    public GameSession saveInPersistence(GameSession aGameSession) {
        aGameSession.putSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_HAS_BLUE, mHasBlue);
        aGameSession.putSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_HAS_RED, mHasRed);
        aGameSession.putSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_HAS_GREEN, mHasGreen);
        aGameSession.putSessionDataForMapAndEntity(mInteractionChallenge.getMap().getMapName(), mInteractionChallenge.getId(), KEY_MIX_RESULT, mMixResult);
        return aGameSession;
    }

    @Override
    protected void createDragAndDropTarget(DragAndDrop aDragAndDrop) {
        mBackgroundTarget = new ChallengeTarget(mBackground, this);
        aDragAndDrop.addTarget(mBackgroundTarget);
    }

    @Override
    protected void removeDragAndDropTarget(DragAndDrop aDragAndDrop) {
        aDragAndDrop.removeTarget(mBackgroundTarget);
    }

    @Override
    public void onItemDrop(InventorySlot aSourceSlot) {
        if (aSourceSlot.doesAcceptItemUseType(Item.ItemTypeID.VialBlue)) {
            mHasBlue = true;
            EventDispatcher.getInstance().onItemLost(aSourceSlot.getItemOnTop());
        } else if (aSourceSlot.doesAcceptItemUseType(Item.ItemTypeID.VialGreen)) {
            mHasGreen = true;
            EventDispatcher.getInstance().onItemLost(aSourceSlot.getItemOnTop());
        } else if (aSourceSlot.doesAcceptItemUseType(Item.ItemTypeID.VialRed)) {
            mHasRed = true;
            EventDispatcher.getInstance().onItemLost(aSourceSlot.getItemOnTop());
        }
        mInteractionChallenge.saveInPersistence();
        updateUI();
    }

    protected void onActivate(boolean isActivated) {
        mActivatorOff.setVisible(!isActivated);
        mActivatorOn.setVisible(isActivated);
        if (isActivated) {
            if (mMixResult == LIQUID_RESULT_BLUE) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.VialBlue));
            } else if (mMixResult == LIQUID_RESULT_GREEN) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.VialGreen));
            } else if (mMixResult == LIQUID_RESULT_RED) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.VialRed));
            } else if (mMixResult == LIQUID_RESULT_RED_GREEN) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.PotionYellowLarge));
            } else if (mMixResult == LIQUID_RESULT_BLUE_GREEN) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.PotionTealBig));
            } else if (mMixResult == LIQUID_RESULT_BLUE_RED) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.PotionVioletLarge));
            } else if (mMixResult == LIQUID_RESULT_BLUE_GREEN_RED) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.PotionSilver));
            }
            mMixResult = LIQUID_RESULT_NONE;
        } else {
            if (mHasBlue && !mHasGreen && !mHasRed) {
                mMixResult = LIQUID_RESULT_BLUE;
            } else if (mHasGreen && !mHasBlue && !mHasRed) {
                mMixResult = LIQUID_RESULT_GREEN;
            } else if (mHasRed && !mHasGreen && !mHasBlue) {
                mMixResult = LIQUID_RESULT_RED;
            } else if (mHasRed && mHasGreen && !mHasBlue) {
                mMixResult = LIQUID_RESULT_RED_GREEN;
            } else if (!mHasRed && mHasGreen && mHasBlue) {
                mMixResult = LIQUID_RESULT_BLUE_GREEN;
            } else if (mHasRed && !mHasGreen && mHasBlue) {
                mMixResult = LIQUID_RESULT_BLUE_RED;
            } else if (mHasRed && mHasGreen && mHasBlue) {
                mMixResult = LIQUID_RESULT_BLUE_GREEN_RED;
            } else {
                mMixResult = LIQUID_RESULT_NONE;
            }
            mHasBlue = false;
            mHasRed = false;
            mHasGreen = false;

        }
        mInteractionChallenge.saveInPersistence();
        updateUI();

    }

    protected void updateUI() {
        mBlue.setVisible(mHasBlue);
        mBubbleBlue.setVisible(mHasBlue);
        mRed.setVisible(mHasRed);
        mBubbleRed.setVisible(mHasRed);
        mGreen.setVisible(mHasGreen);
        mBubbleGreen.setVisible(mHasGreen);

        if (mMixResult == LIQUID_RESULT_BLUE) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_few_blue")));
        } else if (mMixResult == LIQUID_RESULT_GREEN) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_few_green")));
        } else if (mMixResult == LIQUID_RESULT_RED) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_few_red")));
        } else if (mMixResult == LIQUID_RESULT_RED_GREEN) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_demi_green_red")));
        } else if (mMixResult == LIQUID_RESULT_BLUE_GREEN) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_demi_blue_green")));
        } else if (mMixResult == LIQUID_RESULT_BLUE_RED) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_demi_blue_red")));
        } else if (mMixResult == LIQUID_RESULT_BLUE_GREEN_RED) {
            mMixImg.setDrawable(new TextureRegionDrawable(mAtlas.findRegion("theMachine_liquid_mixed_full")));
        }
        mMixImg.setScaling(Scaling.none);
        mMixImg.setSize(mMixImg.getPrefWidth(), mMixImg.getPrefHeight());
        mMixImg.setVisible(mMixResult!=LIQUID_RESULT_NONE);

        mMixImg.setPosition(LIQUID_FULL_X, LIQUID_FULL_Y);
    }
}
