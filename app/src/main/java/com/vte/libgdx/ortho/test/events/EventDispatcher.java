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


    public void onGameEvent(String aGameEvent) {
        synchronized (mGameEventsListeners) {
            IGameEventListener[] listeners = new IGameEventListener[mGameEventsListeners.size()];
            listeners = mGameEventsListeners.toArray(listeners);
            for (IGameEventListener listener : listeners) {
                listener.onGameEvent(aGameEvent);
            }
        }
    }

    public void addGameEventListener(IGameEventListener aListener) {
        synchronized (mGameEventsListeners) {
            if (!mGameEventsListeners.contains(aListener)) {
                mGameEventsListeners.add(aListener);
            }
        }
    }

    public void removeGameEventListener(IGameEventListener aListener) {
        synchronized (mGameEventsListeners) {
            if (mGameEventsListeners.contains(aListener)) {
                mGameEventsListeners.remove(aListener);
            }
        }
    }

    public void addDialogListener(IDialogListener aListener) {
        synchronized (mDialogListeners) {
            if (!mDialogListeners.contains(aListener)) {
                mDialogListeners.add(aListener);
            }
        }
    }

    public void removeDialogListener(IDialogListener aListener) {
        synchronized (mDialogListeners) {
            if (mDialogListeners.contains(aListener)) {
                mDialogListeners.remove(aListener);
            }
        }
    }

    @Override
    public void onStartDialog(GameDialog aDialog) {
        synchronized (mDialogListeners) {
            IDialogListener[] listeners = new IDialogListener[mDialogListeners.size()];
            listeners = mDialogListeners.toArray(listeners);
            for (IDialogListener listener : listeners) {
                listener.onStartDialog(aDialog);
            }
        }
    }

    @Override
    public void onStopDialog(GameDialog aDialog) {
        synchronized (mDialogListeners) {
            IDialogListener[] listeners = new IDialogListener[mDialogListeners.size()];
            listeners = mDialogListeners.toArray(listeners);
            for (IDialogListener listener : listeners) {
                listener.onStopDialog(aDialog);
            }
        }
    }

    public void addItemListener(IItemListener aListener) {
        synchronized (mItemListeners) {
            if (!mItemListeners.contains(aListener)) {
                mItemListeners.add(aListener);
            }
        }
    }

    public void removeItemListener(IItemListener aListener) {
        synchronized (mItemListeners) {
            if (mItemListeners.contains(aListener)) {
                mItemListeners.remove(aListener);
            }
        }
    }

    @Override
    public void onItemFound(Item aItem) {
        synchronized (mItemListeners) {
            IItemListener[] listeners = new IItemListener[mItemListeners.size()];
            listeners = mItemListeners.toArray(listeners);
            for (IItemListener listener : listeners) {
                listener.onItemFound(aItem);
            }
        }
    }

    @Override
    public void onItemLost(Item aItem) {
        synchronized (mItemListeners) {
            IItemListener[] listeners = new IItemListener[mItemListeners.size()];
            listeners = mItemListeners.toArray(listeners);
            for (IItemListener listener : listeners) {
                listener.onItemLost(aItem);
            }
        }
    }


    public void addQuestListener(IQuestListener aListener) {
        synchronized (mQuestListeners) {
            if (!mQuestListeners.contains(aListener)) {
                mQuestListeners.add(aListener);
            }
        }
    }

    public void removeQuestListener(IQuestListener aListener) {
        synchronized (mQuestListeners) {
            if (mQuestListeners.contains(aListener)) {
                mQuestListeners.remove(aListener);
            }
        }
    }

    @Override
    public void onQuestActivated(Quest aQuest) {
        synchronized (mQuestListeners) {
            IQuestListener[] listeners = new IQuestListener[mQuestListeners.size()];
            listeners = mQuestListeners.toArray(listeners);

            for (IQuestListener listener : listeners) {
                listener.onQuestActivated(aQuest);
            }
        }
    }

    @Override
    public void onQuestCompleted(Quest aQuest) {
        synchronized (mQuestListeners) {
            IQuestListener[] listeners = new IQuestListener[mQuestListeners.size()];
            listeners = mQuestListeners.toArray(listeners);
            for (IQuestListener listener : listeners) {
                listener.onQuestCompleted(aQuest);
            }
        }
    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {
        synchronized (mQuestListeners) {
            IQuestListener[] listeners = new IQuestListener[mQuestListeners.size()];
            listeners = mQuestListeners.toArray(listeners);
            for (IQuestListener listener : listeners) {
                listener.onQuestTaskCompleted(aQuest, aTask);
            }
        }
    }

    public void addPlayerListener(IPlayerListener aListener) {
        synchronized (mPlayerListeners) {
            if (!mPlayerListeners.contains(aListener)) {
                mPlayerListeners.add(aListener);
            }
        }
    }

    public void removePlayerListener(IPlayerListener aListener) {
        synchronized (mPlayerListeners) {
            if (mPlayerListeners.contains(aListener)) {
                mPlayerListeners.remove(aListener);
            }
        }
    }

    @Override
    public void onInventoryChanged(Player aPlayer) {
        synchronized (mPlayerListeners) {
            IPlayerListener[] listeners = new IPlayerListener[mPlayerListeners.size()];
            listeners = mPlayerListeners.toArray(listeners);
            for (IPlayerListener listener : listeners) {
                listener.onInventoryChanged(aPlayer);
            }
        }
    }

    public void addSystemEventListener(ISystemEventListener aListener) {
        synchronized (mSystemEventListeners) {
            if (!mSystemEventListeners.contains(aListener)) {
                mSystemEventListeners.add(aListener);
            }
        }
    }

    public void removeSystemEventListener(ISystemEventListener aListener) {
        synchronized (mSystemEventListeners) {
            if (mSystemEventListeners.contains(aListener)) {
                mSystemEventListeners.remove(aListener);
            }
        }
    }

    @Override
    public void onNewMapRequested(String aMapId, MapTownPortalInfo aTownPortalInfo) {
        synchronized (mSystemEventListeners) {
            ISystemEventListener[] listeners = new ISystemEventListener[mSystemEventListeners.size()];
            listeners = mSystemEventListeners.toArray(listeners);
            for (ISystemEventListener listener : listeners) {
                listener.onNewMapRequested(aMapId, aTownPortalInfo);
            }
        }
    }
    @Override
    public void onMapReloadRequested(String aMapId, String aFromMapId)
    {
        synchronized (mSystemEventListeners) {
            ISystemEventListener[] listeners = new ISystemEventListener[mSystemEventListeners.size()];
            listeners = mSystemEventListeners.toArray(listeners);
            for (ISystemEventListener listener : listeners) {
                listener.onMapReloadRequested(aMapId, aFromMapId);
            }
        }
    }

    @Override
    public void onMapLoaded(GameMap aMap) {
        synchronized (mSystemEventListeners) {
            ISystemEventListener[] listeners = new ISystemEventListener[mSystemEventListeners.size()];
            listeners = mSystemEventListeners.toArray(listeners);
            for (ISystemEventListener listener : listeners) {
                listener.onMapLoaded(aMap);
            }
        }
    }

    @Override
    public void onNewSelectedEffect(Effect.Type aEffectType) {
        synchronized (mSystemEventListeners) {
            ISystemEventListener[] listeners = new ISystemEventListener[mSystemEventListeners.size()];
            listeners = mSystemEventListeners.toArray(listeners);
            for (ISystemEventListener listener : listeners) {
                listener.onNewSelectedEffect(aEffectType);
            }
        }
    }

    @Override
    public void onEffectFound(Effect.Type aEffectType) {
        synchronized (mSystemEventListeners) {
            ISystemEventListener[] listeners = new ISystemEventListener[mSystemEventListeners.size()];
            listeners = mSystemEventListeners.toArray(listeners);
            for (ISystemEventListener listener : listeners) {
                listener.onEffectFound(aEffectType);
            }
        }
    }

    public void addInteractionEventListener(IInteractionEventListener aListener) {
        synchronized (mInteractionEventListeners) {
            if (!mInteractionEventListeners.contains(aListener)) {
                mInteractionEventListeners.add(aListener);
            }
        }
    }

    public void removeInteractionEventListener(IInteractionEventListener aListener) {
        synchronized (mInteractionEventListeners) {
            if (mInteractionEventListeners.contains(aListener)) {
                mInteractionEventListeners.remove(aListener);
            }
        }
    }

    @Override
    public void onInteractionEvent(InteractionEvent aEvent) {
        synchronized (mInteractionEventListeners) {
            IInteractionEventListener[] listeners = new IInteractionEventListener[mInteractionEventListeners.size()];
            listeners = mInteractionEventListeners.toArray(listeners);
            for (IInteractionEventListener listener: listeners) {
                listener.onInteractionEvent(aEvent);

            }
        }
    }
}
