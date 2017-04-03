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
        DIALOG
    };

    protected boolean mIsCompleted;
    protected String id;
    protected TypeTask type;
    protected String targetId;
    protected String value;


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

    public String getValue()
    {
        return value;
    }
    public void setValue(String aValue)
    {
        value = aValue;
    }
}
