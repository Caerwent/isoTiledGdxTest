package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.dialogs.GameDialog;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.interactions.InteractionEvent;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.map.MapTownPortalInfo;
import com.vte.libgdx.ortho.test.player.Player;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestTask;

import java.util.ArrayList;

/**
 * Created by vincent on 12/01/2017.
 */

public class EventDispatcher implements IDialogListener, IItemListener, IQuestListener, IPlayerListener, ISystemEventListener, IGameEventListener, IInteractionEventListener {
    private static EventDispatcher _instance = null;

    private ArrayList<IGameEventListener> mGameEventsListeners = new ArrayList<IGameEventListener>();
    private ArrayList<IDialogListener> mDialogListeners = new ArrayList<IDialogListener>();
    private ArrayList<IItemListener> mItemListeners = new ArrayList<IItemListener>();
    private ArrayList<IQuestListener> mQuestListeners = new ArrayList<IQuestListener>();
    private ArrayList<IPlayerListener> mPlayerListeners = new ArrayList<IPlayerListener>();
    private ArrayList<ISystemEventListener> mSystemEventListeners = new ArrayList<ISystemEventListener>();
    private ArrayList<IInteractionEventListener> mInteractionEventListeners = new ArrayList<IInteractionEventListener>();

    public static EventDispatcher getInstance() {
        if (_instance == null) {
            _instance = new EventDispatcher();
        }

        return _instance;
    }


    public void onGameEvent(String aGameEvent)
    {
        for (IGameEventListener listener : mGameEventsListeners) {
            listener.onGameEvent(aGameEvent);
        }
    }
    public void addGameEventListener(IGameEventListener aListener) {
        if (!mGameEventsListeners.contains(aListener)) {
            mGameEventsListeners.add(aListener);
        }
    }

    public void removeGameEventListener(IGameEventListener aListener) {
        if (mGameEventsListeners.contains(aListener)) {
            mGameEventsListeners.remove(aListener);
        }
    }
    public void addDialogListener(IDialogListener aListener) {
        if (!mDialogListeners.contains(aListener)) {
            mDialogListeners.add(aListener);
        }
    }

    public void removeDialogListener(IDialogListener aListener) {
        if (mDialogListeners.contains(aListener)) {
            mDialogListeners.remove(aListener);
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

    public void addItemListener(IItemListener aListener) {
        if (!mItemListeners.contains(aListener)) {
            mItemListeners.add(aListener);
        }
    }

    public void removeItemListener(IItemListener aListener) {
        if (mItemListeners.contains(aListener)) {
            mItemListeners.remove(aListener);
        }
    }

    @Override
    public void onItemFound(Item aItem) {
        for (IItemListener listener : mItemListeners) {
            listener.onItemFound(aItem);
        }
    }
    @Override
    public void onItemLost(Item aItem) {
        for (IItemListener listener : mItemListeners) {
            listener.onItemLost(aItem);
        }
    }


    public void addQuestListener(IQuestListener aListener) {
        if (!mQuestListeners.contains(aListener)) {
            mQuestListeners.add(aListener);
        }
    }

    public void removeQuestListener(IQuestListener aListener) {
        if (mQuestListeners.contains(aListener)) {
            mQuestListeners.remove(aListener);
        }
    }

    @Override
    public void onQuestActivated(Quest aQuest) {
        for (IQuestListener listener : mQuestListeners) {
            listener.onQuestActivated(aQuest);
        }
    }

    @Override
    public void onQuestCompleted(Quest aQuest) {
        for (IQuestListener listener : mQuestListeners) {
            listener.onQuestCompleted(aQuest);
        }
    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {
        for (IQuestListener listener : mQuestListeners) {
            listener.onQuestTaskCompleted(aQuest, aTask);
        }
    }

    public void addPlayerListener(IPlayerListener aListener) {
        if (!mPlayerListeners.contains(aListener)) {
            mPlayerListeners.add(aListener);
        }
    }

    public void removePlayerListener(IPlayerListener aListener) {
        if (mPlayerListeners.contains(aListener)) {
            mPlayerListeners.remove(aListener);
        }
    }

    @Override
    public void onInventoryChanged(Player aPlayer) {
        for (IPlayerListener listener : mPlayerListeners) {
            listener.onInventoryChanged(aPlayer);
        }
    }
    public void addSystemEventListener(ISystemEventListener aListener) {
        if (!mSystemEventListeners.contains(aListener)) {
            mSystemEventListeners.add(aListener);
        }
    }

    public void removeSystemEventListener(ISystemEventListener aListener) {
        if (mSystemEventListeners.contains(aListener)) {
            mSystemEventListeners.remove(aListener);
        }
    }
    @Override
    public void onNewMapRequested(String aMapId, MapTownPortalInfo aTownPortalInfo) {
        for (ISystemEventListener listener : mSystemEventListeners) {
            listener.onNewMapRequested(aMapId, aTownPortalInfo);
        }
    }

    @Override
    public void onMapLoaded(GameMap aMap) {
        for (ISystemEventListener listener : mSystemEventListeners) {
            listener.onMapLoaded(aMap);
        }
    }

    @Override
    public void onNewSelectedEffect(Effect.Type aEffectType) {
        for (ISystemEventListener listener : mSystemEventListeners) {
            listener.onNewSelectedEffect(aEffectType);
        }
    }
    @Override
    public void onEffectFound(Effect.Type aEffectType)
    {
        for (ISystemEventListener listener : mSystemEventListeners) {
            listener.onEffectFound(aEffectType);
        }
    }

    public void addInteractionEventListener(IInteractionEventListener aListener) {
        if (!mInteractionEventListeners.contains(aListener)) {
            mInteractionEventListeners.add(aListener);
        }
    }

    public void removeInteractionEventListener(IInteractionEventListener aListener) {
        if (mInteractionEventListeners.contains(aListener)) {
            mInteractionEventListeners.remove(aListener);
        }
    }
    @Override
    public void onInteractionEvent(InteractionEvent aEvent) {
        for (IInteractionEventListener listener : mInteractionEventListeners) {
            listener.onInteractionEvent(aEvent);
        }
    }
}
