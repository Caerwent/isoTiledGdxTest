package com.vte.libgdx.ortho.test.quests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.dialogs.DialogsManager;
import com.vte.libgdx.ortho.test.dialogs.GameDialog;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IItemListener;
import com.vte.libgdx.ortho.test.events.IPlayerListener;
import com.vte.libgdx.ortho.test.events.IQuestListener;
import com.vte.libgdx.ortho.test.interactions.InteractionNPC;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.persistence.QuestProfile;
import com.vte.libgdx.ortho.test.persistence.QuestTaskProfile;
import com.vte.libgdx.ortho.test.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 13/01/2017.
 */

public class QuestManager implements IItemListener, IQuestListener, IPlayerListener {
    private Json _json = new Json();
    private final String QUEST_LIST = "data/quests/quests.json";
    private static QuestManager _instance = null;

    private HashMap<String, Quest> mQuests = new HashMap<String, Quest>();

    private HashMap<String, Quest> mLivingQuests = new HashMap<String, Quest>();
    private HashMap<String, Quest> mCompletedQuests = new HashMap<String, Quest>();

    private Player mCurrentPlayer;

    public static QuestManager getInstance() {
        if (_instance == null) {
            _instance = new QuestManager();
        }

        return _instance;
    }

    private QuestManager() {
        _json.setIgnoreUnknownFields(true);
        ArrayList<String> list = _json.fromJson(ArrayList.class,
                Gdx.files.internal(QUEST_LIST));
        for (String questFileName : list) {
            Quest theQuest = _json.fromJson(Quest.class,
                    Gdx.files.internal("data/quests/" + questFileName + ".json"));
            // convert strings
            theQuest.setTitle(AssetsUtility.getString(theQuest.getTitle()));
            theQuest.setDescription(AssetsUtility.getString(theQuest.getDescription()));
            mQuests.put(theQuest.id, theQuest);
        }

        restoreQuestsFromProfile();
        EventDispatcher.getInstance().addItemListener(this);
        EventDispatcher.getInstance().addQuestListener(this);
        EventDispatcher.getInstance().addPlayerListener(this);
    }

    public void restoreQuestsFromProfile() {
        for (String entry : mQuests.keySet()) {
            QuestProfile questProfile = Profile.getInstance().getQuestProfile(entry);
            Quest theQuest = mQuests.get(entry);
            if (questProfile != null && theQuest != null) {
                for (QuestTaskProfile taskProfile : questProfile.tasks) {
                    QuestTask theTask = theQuest.getTaskById(taskProfile.id);
                    if (theTask != null) {
                        theTask.setCompleted(taskProfile.isCompleted);
                    }

                }
                theQuest.setActivated(questProfile.isActivated);
                theQuest.setCompleted(questProfile.isCompleted);
                if (theQuest.isCompleted()) {
                    mCompletedQuests.put(theQuest.getId(), theQuest);
                } else if (theQuest.isActivated()) {
                    mLivingQuests.put(theQuest.getId(), theQuest);
                }
            }


        }
    }

    public Quest getQuestFromId(String aId) {
        return mQuests.get(aId);
    }

    public Quest getLivingQuestFromId(String aId) {
        return mLivingQuests.get(aId);
    }


    public void onDialogEnd(GameDialog aDialog) {
        for (Quest quest : mLivingQuests.values()) {
            if (quest.isActivated() && !quest.isCompleted()) {
                for (QuestTask task : quest.getTasks()) {
                    if (!task.isCompleted()) {
                        if (task.getType() == QuestTask.TypeTask.DIALOG) {
                            if (task.getTargetId() != null && task.getTargetId().equals(aDialog.getId())) {
                                if (quest.isTaskDependenciesCompleted(task)) {
                                    task.setCompleted(true);
                                    EventDispatcher.getInstance().onQuestTaskCompleted(quest, task);
                                }
                            }

                        }

                    }
                }
            }
        }
    }

    public void onNPC(InteractionNPC aNPC) {

        for (Quest quest : mLivingQuests.values()) {
            if (quest.isActivated() && !quest.isCompleted()) {
                for (QuestTask task : quest.getTasks()) {
                    if (!task.isCompleted()) {
                        if (task.getTargetId() != null && task.getTargetId().equals(aNPC.getId())) {
                            if (task.getType() == QuestTask.TypeTask.RETURN_ITEM) {
                                // check items not already be found before talking with npj
                                checkItemFoundTask(quest);
                                if (quest.isTaskDependenciesCompleted(task)) {
                                    task.setCompleted(true);


                                    EventDispatcher.getInstance().onQuestTaskCompleted(quest, task);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    @Override
    public void onItemFound(Item aItem) {
        if (mCurrentPlayer == null)
            return;
        for (Quest quest : mLivingQuests.values()) {
            if (quest.isActivated() && !quest.isCompleted()) {
                for (QuestTask task : quest.getTasks()) {
                    if (!task.isCompleted()) {
                        if (task.getTargetId() != null && task.getTargetId().equals(aItem.getItemTypeID().name())) {
                            if (task.getType() == QuestTask.TypeTask.FIND_ITEM) {
                                Array<Item> foundItems = mCurrentPlayer.getItemsInventoryById(task.getTargetId());
                                int nbItem = 1;
                                if (task.getValue() != null) {
                                    nbItem = Integer.valueOf(task.getValue());
                                }
                                if (foundItems.size >= nbItem) {
                                    if (quest.isTaskDependenciesCompleted(task)) {
                                        task.setCompleted(true);
                                        EventDispatcher.getInstance().onQuestTaskCompleted(quest, task);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkItemFoundTask(Quest aQuest) {
        if (mCurrentPlayer == null)
            return;
        for (QuestTask task : aQuest.getTasks()) {
            if (!task.isCompleted() && task.getType() == QuestTask.TypeTask.FIND_ITEM) {
                Array<Item> foundItems = mCurrentPlayer.getItemsInventoryById(task.getTargetId());
                int nbItem = 1;
                if (task.getValue() != null) {
                    nbItem = Integer.valueOf(task.getValue());
                }
                if (foundItems.size >= nbItem) {
                    if (aQuest.isTaskDependenciesCompleted(task)) {
                        task.setCompleted(true);
                        Profile.getInstance().updateQuestProfile(aQuest.getId(), aQuest);
                    }
                }
            }

        }
    }

    private void updateItemsFromFoundTask(Quest aQuest) {
        if (mCurrentPlayer == null)
            return;
        for (QuestTask task : aQuest.getTasks()) {
            if (task.isCompleted() && task.getType() == QuestTask.TypeTask.FIND_ITEM) {
                Array<Item> foundItems = mCurrentPlayer.getItemsInventoryById(task.getTargetId());
                for (int i = 0; i < foundItems.size; i++) {
                    EventDispatcher.getInstance().onItemLost(foundItems.get(i));
                }
            }

        }
    }

    @Override
    public void onItemLost(Item aItem) {

    }

    @Override
    public void onQuestActivated(Quest aQuest) {
        if (!mLivingQuests.containsKey(aQuest.getId())) {
            mLivingQuests.put(aQuest.getId(), aQuest);
            if (aQuest.getStartQuestDialogId() != null) {
                EventDispatcher.getInstance().onStartDialog(DialogsManager.getInstance().getDialog(aQuest.getStartQuestDialogId()));
            }
        }
        Profile.getInstance().updateQuestProfile(aQuest.getId(), aQuest);
    }

    private void internalQuestcompleted(Quest aQuest) {
        updateItemsFromFoundTask(aQuest);
        if (aQuest.getItemsReward() != null) {
            for (Item.ItemTypeID itemID : aQuest.getItemsReward()) {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(itemID));
            }
        }
        if (aQuest.getEffectsReward() != null) {
            for (Effect.Type effectType : aQuest.getEffectsReward()) {
                EventDispatcher.getInstance().onEffectFound(effectType);
            }
        }
        EventDispatcher.getInstance().onGameEvent(aQuest.getId());

        for (Quest quest : mQuests.values()) {
            if (!mLivingQuests.containsKey(quest.getId()) &&
                    !mCompletedQuests.containsKey(quest.getId()) &&
                    quest.getRequiredCompletedQuest() != null) {

                boolean isAllDependenciesCompleted = true;
                for (String requiredId : quest.getRequiredCompletedQuest()) {
                    Quest dependenciyQuest = mQuests.get(requiredId);
                    if (!dependenciyQuest.isCompleted()) {
                        isAllDependenciesCompleted = false;
                        break;
                    }
                }
                if(isAllDependenciesCompleted) {
                    quest.setActivated(true);
                    EventDispatcher.getInstance().onQuestActivated(quest);
                }

            }
        }

        EventDispatcher.getInstance().onQuestCompleted(aQuest);
        if (mLivingQuests.containsKey(aQuest.getId())) {
            mLivingQuests.remove(aQuest.getId());
        }
        if (!mCompletedQuests.containsKey(aQuest.getId())) {
            mCompletedQuests.put(aQuest.getId(), aQuest);
        }
        aQuest.setActivated(false);
        Profile.getInstance().updateQuestProfile(aQuest.getId(), aQuest);

    }

    @Override
    public void onQuestCompleted(Quest aQuest) {

    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {

        //check if quest is completed
        aQuest.computeCompleted();
        if (aQuest.isCompleted()) {
            internalQuestcompleted(aQuest);
        }


    }

    @Override
    public void onInventoryChanged(Player aPlayer) {
        mCurrentPlayer = aPlayer;

    }
}
