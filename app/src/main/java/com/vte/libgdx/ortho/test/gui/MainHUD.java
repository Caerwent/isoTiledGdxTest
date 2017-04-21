package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.effects.EffectFactory;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IQuestListener;
import com.vte.libgdx.ortho.test.events.ISystemEventListener;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.map.MapTownPortalInfo;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestManager;
import com.vte.libgdx.ortho.test.quests.QuestTask;
import com.vte.libgdx.ortho.test.screens.GameScreen;
import com.vte.libgdx.ortho.test.screens.GenericUI;

import static com.vte.libgdx.ortho.test.MyGame.ScreenType.MainGame;
import static com.vte.libgdx.ortho.test.Settings.TARGET_HEIGHT;
import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * Created by gwalarn on 16/11/16.
 */

public class MainHUD extends Group implements ISystemEventListener, IQuestListener {

    private static final int DIALOG_W = TARGET_WIDTH / 4 * 3;
    private static final int DIALOG_H = TARGET_HEIGHT/4;

    protected final HorizontalGroup mHud = new HorizontalGroup();

    Table mMainPanel = new Table();
    protected HorizontalGroup mTabsPanel = new HorizontalGroup();
    protected Table mContentPanel = new Table();
    private InventoryTable mInventorySlotTable = new InventoryTable();
    private EffectsPanel mEffectsPanel = new EffectsPanel();
    protected final DialogTable mDialogTable = new DialogTable(DIALOG_W, DIALOG_H);
    protected Image mInventoryButton;
    protected Image mSpellButton;
    protected Effect.Type mCurrentEffectType = null;
    /**
     * the duration of the screen transition for the screenOut method
     */
    public float dur;


    public MainHUD() {

        mHud.setSize(128, 64);

        this.addActor(mHud);

        EventDispatcher.getInstance().addSystemEventListener(this);

        mInventoryButton = new Image();
        mInventoryButton.setScaling(Scaling.fit);
        mInventoryButton.setAlign(Align.center);
        mInventoryButton.setSize(64, 64);
        mInventoryButton.setDrawable(new TextureRegionDrawable(GenericUI.getInstance().getTextureAtlas().findRegion("bag")));
        mHud.addActor(mInventoryButton);

        mSpellButton = new Image();
        mSpellButton.setScaling(Scaling.fit);
        mSpellButton.setAlign(Align.center);
        mSpellButton.setSize(64, 64);

        mHud.addActor(mSpellButton);

        mMainPanel.setSize(TARGET_WIDTH / 2, TARGET_HEIGHT - 64 - 25);
        mMainPanel.setPosition(10, 64);
        mMainPanel.setVisible(false);
        addActor(mMainPanel);

        final Button tab1 = new TextButton("Objets", GenericUI.getInstance().getSkin(), "tab");
        final Button tab2 = new TextButton("Pouvoirs", GenericUI.getInstance().getSkin(), "tab");

        mTabsPanel.addActor(tab1);
        mTabsPanel.addActor(tab2);

        // Listen to changes in the tab button checked states
        // Set visibility of the tab content to match the checked state
        ChangeListener tab_listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mInventorySlotTable.setVisible(tab1.isChecked());
                mEffectsPanel.setVisible(tab2.isChecked());
            }
        };
        tab1.addListener(tab_listener);
        tab2.addListener(tab_listener);

        // Let only one tab button be checked at a time
        ButtonGroup tabs = new ButtonGroup();
        tabs.setMinCheckCount(1);
        tabs.setMaxCheckCount(1);
        tabs.add(tab1);
        tabs.add(tab2);

        tab1.setChecked(true);

        mMainPanel.add(mTabsPanel).top().left().padBottom(-1).padLeft(15);
        mMainPanel.row();
        Stack stack = new Stack();
        mMainPanel.add(mContentPanel).top().left().fill().expand();
        mMainPanel.row();
        mContentPanel.add(stack).top().left().fill().expand();
        mContentPanel.row();
        mContentPanel.setBackground(GenericUI.getInstance().getSkin().getDrawable("window"));
        stack.add(mInventorySlotTable);
        mInventorySlotTable.setPosition(0, 0);
        if(QuestManager.getInstance().getQuestFromId(MyGame.QUEST_START_ID).isCompleted())
        {
            mInventoryButton.setVisible(true);
            mSpellButton.setVisible(true);
        }
        else
        {
            EventDispatcher.getInstance().addQuestListener(this);
            mInventoryButton.setVisible(false);
            mSpellButton.setVisible(false);
        }
        stack.add(mEffectsPanel);
        mEffectsPanel.setPosition(0, 0);


        mDialogTable.setPosition((TARGET_WIDTH - DIALOG_W) / 2, 0);
        mDialogTable.setVisible(false);
        addActor(mDialogTable);
        mInventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mMainPanel.isVisible()) {
                    mMainPanel.setVisible(false);
                } else {
                    mMainPanel.setVisible(true);
                }
            }
        });
        mInventoryButton.setTouchable(Touchable.enabled);


        mSpellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mCurrentEffectType != null) {
                    ((GameScreen) MyGame.getInstance().getScreenType(MainGame)).getMap().getPlayer().getHero().launchEffect(EffectFactory.getInstance().getNewInstanceEffect(mCurrentEffectType));
                }
            }
        });
        mSpellButton.setTouchable(Touchable.enabled);

        Effect.Type selectedEffect = Profile.getInstance().getSelectedEffect();
        if (selectedEffect != null) {
            onNewSelectedEffect(selectedEffect);
        } else if (Profile.getInstance().getAvailableEffects().size() > 0) {
            onNewSelectedEffect(Profile.getInstance().getAvailableEffects().get(0));
        } else {
            mEffectsPanel.update();
        }

    }


    @Override
    public void onNewMapRequested(String aMapId, MapTownPortalInfo aTownPortalInfo) {

    }
    @Override
    public void onMapReloadRequested(String aMapId, String aFromMapId) {

    }
    @Override
    public void onMapLoaded(GameMap aMap) {
    }

    @Override
    public void onNewSelectedEffect(Effect.Type aEffectType) {
        mCurrentEffectType = aEffectType;
        if (mCurrentEffectType != null) {
            mSpellButton.setDrawable(new TextureRegionDrawable(EffectFactory.getInstance().getEffect(mCurrentEffectType).getIcon()));
            mEffectsPanel.update();

        }
    }

    @Override
    public void onEffectFound(Effect.Type aEffectType) {
        mEffectsPanel.update();
    }

    public DragAndDrop getItemDragAndDrop()
    {
        return mInventorySlotTable.getDragAndDrop();
    }

    public void destroy()
    {
        EventDispatcher.getInstance().removeQuestListener(this);
        EventDispatcher.getInstance().removeSystemEventListener(this);
    }

    @Override
    public void onQuestActivated(Quest aQuest) {

    }

    @Override
    public void onQuestCompleted(Quest aQuest) {
        if(aQuest.getId().compareTo(MyGame.QUEST_START_ID)==0)
        {
            mInventoryButton.setVisible(true);
            mSpellButton.setVisible(true);
            EventDispatcher.getInstance().removeQuestListener(this);
        }
    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {

    }
}
