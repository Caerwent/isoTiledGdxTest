package com.vte.libgdx.ortho.test.quests;

import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.items.Item;

import java.util.ArrayList;

/**
 * Created by vincent on 12/01/2017.
 */

public class Quest {
    protected boolean mIsActivated;
    protected boolean mIsCompleted;
    protected String title;
    protected String desc;
    protected String id;
    protected ArrayList<String> requiredCompletedQuest;
    protected ArrayList<QuestTask> tasks;
    protected String startQuestDialogId;
    protected ArrayList<Item.ItemTypeID> itemsReward;
    protected ArrayList<Effect.Type> effectsReward;

    public String getId() {
        return id;
    }

    public void setId(String aId) {
        id = aId;
    }

    public void setTitle(String aTitle)
    {
        title = aTitle;
    }
    public String getTitle()
    {
        return title;
    }
    public void setDescription(String aDesc)
    {
        desc = aDesc;
    }
    public String getDescription()
    {
        return desc;
    }
    public void setActivated(boolean activated) {
        mIsActivated = activated;
    }

    public boolean isActivated() {
        return mIsActivated;

    }

    public void setCompleted(boolean completed) {
        mIsCompleted = completed;
    }

    public boolean isCompleted() {
        return mIsCompleted;

    }

    public ArrayList<String> getRequiredCompletedQuest() {
        return requiredCompletedQuest;
    }

    public void setRequiredCompletedQuest(ArrayList<String> aRequiredCompletedQuest) {
        requiredCompletedQuest = aRequiredCompletedQuest;
    }

    public ArrayList<QuestTask> getTasks() {
        return tasks;
    }

    public QuestTask getTaskById(String aTaskId)
    {
        if(tasks==null || aTaskId==null)
            return null;
        for(QuestTask task : tasks)
        {
            if(task.getId().equals(aTaskId))
            {
                return task;
            }
        }
        return null;
    }

    public void setTasks(ArrayList<QuestTask> aTasks) {
        tasks = aTasks;
    }

    public String getStartQuestDialogId() {
        return startQuestDialogId;
    }

    public void setStartQuestDialogId(String aId) {
        startQuestDialogId = aId;
    }

    public void computeCompleted() {
        for (QuestTask task : tasks) {
            if (!task.isCompleted()) {
                mIsCompleted = false;
                return;
            }
        }
        mIsCompleted = true;

    }

    public boolean isTaskDependenciesCompleted(QuestTask task) {


        if (task.getRequiredCompletedTask() != null) {
            for (String taskId : task.getRequiredCompletedTask()) {
                for (QuestTask taskDenpendency : tasks) {
                    if (taskDenpendency.getId().equals(taskId) && !taskDenpendency.isCompleted()) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }

    }

    public ArrayList<Item.ItemTypeID> getItemsReward()
    {
        return itemsReward;
    }

    public ArrayList<Effect.Type> getEffectsReward()
    {
        return effectsReward;
    }
}
