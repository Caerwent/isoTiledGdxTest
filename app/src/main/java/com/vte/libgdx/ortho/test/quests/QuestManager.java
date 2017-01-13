package com.vte.libgdx.ortho.test.quests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.OrderedMap;
import com.vte.libgdx.ortho.test.characters.CharacterNPJ;
import com.vte.libgdx.ortho.test.dialogs.DialogsManager;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IItemListener;
import com.vte.libgdx.ortho.test.events.IQuestListener;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.map.IArea;
import com.vte.libgdx.ortho.test.player.Player;

import java.util.ArrayList;

/**
 * Created by vincent on 13/01/2017.
 */

public class QuestManager implements IItemListener, IQuestListener {
    private Json _json = new Json();
    private final String QUEST_LIST = "data/quests/quests.json";
    private static QuestManager _instance = null;

    private OrderedMap<String, Quest> mQuests = new OrderedMap<String, Quest>();

    private OrderedMap<String, Quest> mLivingQuests = new OrderedMap<String, Quest>();
    private OrderedMap<String, Quest> mCompletedQuests = new OrderedMap<String, Quest>();

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
            mQuests.put(theQuest.id, theQuest);
        }
        EventDispatcher.getInstance().addItemListener(this);
        EventDispatcher.getInstance().addQuestListener(this);
    }

    public Quest getQuestFromId(String aId) {
        return mQuests.get(aId);
    }


    public void enterArea(IArea aArea) {

    }

    public void onNPJ(CharacterNPJ aNPJ) {

        for (Quest quest : mLivingQuests.values()) {
            if (quest.isActivated() && !quest.isCompleted()) {
                for (QuestTask task : quest.getTasks()) {
                    if (!task.isCompleted()) {
                        if (task.getTargetId() != null && task.getTargetId().equals(aNPJ.getId())) {
                            if (task.getType() == QuestTask.TypeTask.TALK) {
                                if (quest.isTaskDependenciesCompleted(task)) {
                                    task.setCompleted(true);
                                    aNPJ.setDialogId(task.getCompletedDialogId());
                                    EventDispatcher.getInstance().onQuestTaskCompleted(quest, task);
                                }

                            } else if (task.getType() == QuestTask.TypeTask.RETURN_ITEM) {
                                // check items not already be found before talking with npj
                                checkItemFoundTask(quest);
                                if (quest.isTaskDependenciesCompleted(task)) {
                                    task.setCompleted(true);
                                    if (task.getCompletedDialogId() != null) {
                                        aNPJ.setDialogId(task.getCompletedDialogId());
                                    }

                                    EventDispatcher.getInstance().onQuestTaskCompleted(quest, task);
                                } else if (task.getDialogId() != null) {
                                    aNPJ.setDialogId(task.getDialogId());
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
        for (Quest quest : mLivingQuests.values()) {
            if (quest.isActivated() && !quest.isCompleted()) {
                for (QuestTask task : quest.getTasks()) {
                    if (!task.isCompleted()) {
                        if (task.getTargetId() != null && task.getTargetId().equals(aItem.getItemTypeID().name())) {
                            if (task.getType() == QuestTask.TypeTask.FIND_ITEM) {
                                Array<Item> foundItems = Player.getInstance().getItemsInventoryById(task.getTargetId());
                                if (foundItems.size >= task.getCount()) {
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
        for (QuestTask task : aQuest.getTasks()) {
            if (!task.isCompleted() && task.getType() == QuestTask.TypeTask.FIND_ITEM) {
                Array<Item> foundItems = Player.getInstance().getItemsInventoryById(task.getTargetId());
                if (foundItems.size >= task.getCount()) {
                    if (aQuest.isTaskDependenciesCompleted(task)) {
                        task.setCompleted(true);
                    }
                }
            }

        }
    }
    private void updateItemsFromFoundTask(Quest aQuest) {
        for (QuestTask task : aQuest.getTasks()) {
            if (task.isCompleted() && task.getType() == QuestTask.TypeTask.FIND_ITEM) {
                Array<Item> foundItems = Player.getInstance().getItemsInventoryById(task.getTargetId());
                for(int i=0;i<foundItems.size;i++)
                {
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
    }

    @Override
    public void onQuestCompleted(Quest aQuest) {
        if (mLivingQuests.containsKey(aQuest.getId())) {
            mLivingQuests.remove(aQuest.getId());
        }
        if (!mCompletedQuests.containsKey(aQuest.getId())) {
            mCompletedQuests.put(aQuest.getId(), aQuest);
        }
        updateItemsFromFoundTask(aQuest);

        for (Quest quest : mQuests.values()) {
            if (!mLivingQuests.containsKey(quest.getId()) &&
                    !mCompletedQuests.containsKey(quest.getId()) &&
                    quest.getRequiredCompletedQuest() != null) {

                boolean isAllDependenciesCompleted = true;
                for (String requiredId : quest.getRequiredCompletedQuest()) {
                    Quest dependenciyQuest = mCompletedQuests.get(requiredId);
                    if (mCompletedQuests.containsKey(requiredId)) {
                        isAllDependenciesCompleted = false;
                        break;
                    }
                }
                quest.setActivated(true);
                EventDispatcher.getInstance().onQuestActivated(quest);
            }
        }
    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {
        if (aTask.getCompletedDialogId() != null && aTask.getType() != QuestTask.TypeTask.RETURN_ITEM && aTask.getType() != QuestTask.TypeTask.TALK) {
            EventDispatcher.getInstance().onStartDialog(DialogsManager.getInstance().getDialog(aTask.getCompletedDialogId()));
        }
        //check if quest is completed
        aQuest.computeCompleted();
        if (aQuest.isCompleted()) {
            EventDispatcher.getInstance().onQuestCompleted(aQuest);
        }


    }
}
