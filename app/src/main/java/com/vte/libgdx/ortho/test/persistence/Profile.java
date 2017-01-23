package com.vte.libgdx.ortho.test.persistence;

import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 20/01/2017.
 */

public class Profile {
    HashMap<String, MapProfile> maps = new HashMap<>();
    ArrayList<String> inventory = new ArrayList<>();
    HashMap<String, QuestProfile> quests = new HashMap<>();
    HashMap<String, NPCProfile> npcs = new HashMap<>();

    static private Profile sProfile;

    public static Profile getInstance() {

        if (sProfile == null) {
            sProfile = PersistenceProvider.getInstance().loadProfile();
        }
        return sProfile;
    }

    public final MapProfile getMapProfile(String aMapKey) {
        return maps.get(aMapKey);
    }

    public void updateMapProfile(String aMapName, MapProfile aMapProfile) {
        maps.put(aMapName, aMapProfile);
        PersistenceProvider.getInstance().saveMapProfile(aMapName, aMapProfile);
    }

    public final QuestProfile getQuestProfile(String aQuestKey) {
        return quests.get(aQuestKey);
    }

    public void updateQuestProfile(String aQuestKey, Quest aQuest) {
        QuestProfile questProfile = new QuestProfile();
        questProfile.isActivated = aQuest.isActivated();
        questProfile.isCompleted = aQuest.isCompleted();
        for (QuestTask task : aQuest.getTasks()) {
            QuestTaskProfile taskProfile = new QuestTaskProfile();
            taskProfile.id = task.getId();
            taskProfile.isCompleted = task.isCompleted();
            questProfile.tasks.add(taskProfile);
        }
        quests.put(aQuestKey, questProfile);
        PersistenceProvider.getInstance().saveQuestProfile(aQuestKey, questProfile);

    }

    public final ArrayList<String> getInventory() {
        return inventory;
    }

    public void updateInventory(Array<Item> aInventory) {
        inventory.clear();
        for (Item item : aInventory) {
            inventory.add(item.getItemTypeID().name());
        }
        PersistenceProvider.getInstance().saveInventory(inventory);

    }

    public final NPCProfile getNPCProfile(String aNPCId) {
        return npcs.get(aNPCId);
    }

    public void updateNPCProfile(String aNPCId, NPCProfile aNPC) {

        npcs.put(aNPCId, aNPC);
        PersistenceProvider.getInstance().saveNPCProfile(aNPCId, aNPC);

    }

}
