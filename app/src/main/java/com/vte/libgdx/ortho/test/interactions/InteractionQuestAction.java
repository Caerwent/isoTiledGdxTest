package com.vte.libgdx.ortho.test.interactions;

/**
 * Created by vincent on 02/04/2017.
 */

public class InteractionQuestAction {
    public static enum ActionType {
        DIALOG;
    }

    public static enum QuestState {
        NOT_ACTIVATED,
        NOT_COMPLETED,
        COMPLETED,
        FINISHED;
    }
    public String questId;
    public QuestState questState;
    public ActionType actionId;
    public String actionValue;
}
