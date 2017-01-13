package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.dialogs.GameDialog;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestTask;

import java.util.ArrayList;

/**
 * Created by vincent on 12/01/2017.
 */

public class EventDispatcher implements IDialogListener, IItemListener, IQuestListener {
    private static EventDispatcher _instance = null;

    private ArrayList<IDialogListener> mDialogListeners = new ArrayList<IDialogListener>();
    private ArrayList<IItemListener> mItemListeners = new ArrayList<IItemListener>();
    private ArrayList<IQuestListener> mQuestListeners = new ArrayList<IQuestListener>();

    public static EventDispatcher getInstance() {
        if (_instance == null) {
            _instance = new EventDispatcher();
        }

        return _instance;
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
}
