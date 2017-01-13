package com.vte.libgdx.ortho.test.quests;

import java.util.ArrayList;

/**
 * Created by vincent on 12/01/2017.
 */

public class QuestTask {
    public static enum TypeTask {
        FIND_ITEM,
        RETURN_ITEM,
        FIND_AREA,
        TALK
    };

    protected boolean mIsCompleted;
    protected String id;
    protected TypeTask type;
    protected String targetId;
    protected String dialogId;
    protected int count;
    protected String completedDialogId;

    public String getCompletedDialogId() {
        return completedDialogId;
    }

    public void setCompletedDialogId(String aCompletedDialogId) {
        completedDialogId = aCompletedDialogId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String aDialogId) {
       dialogId = aDialogId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean completed) {
        mIsCompleted = completed;
    }

    public ArrayList<String> getRequiredCompletedTask() {
        return requiredCompletedTask;
    }

    public void setRequiredCompletedTask(ArrayList<String> aRequiredCompletedTask) {
        requiredCompletedTask = aRequiredCompletedTask;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String aTargetId) {
        targetId = aTargetId;
    }

    public TypeTask getType() {
        return type;
    }

    public void setType(TypeTask aType) {
        type = aType;
    }

    protected ArrayList<String> requiredCompletedTask;

    public int getCount()
    {
        return count;
    }
    public void setCount(int aCount)
    {
        count = aCount;
    }
}
