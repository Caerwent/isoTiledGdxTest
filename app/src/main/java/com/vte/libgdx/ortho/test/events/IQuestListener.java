package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestTask;

/**
 * Created by vincent on 13/01/2017.
 */

public interface IQuestListener {
    public void onQuestActivated(Quest aQuest);
    public void onQuestCompleted(Quest aQuest);
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask);
}
