package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.box2d.CircleShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IQuestListener;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestManager;
import com.vte.libgdx.ortho.test.quests.QuestTask;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionPortal extends Interaction implements IQuestListener {
    protected String mTargetMapId;
    protected boolean mIsDefaultStart = false;
    boolean mIsActivated = false;
    String mActivatedByQuestId=null;
    protected String mQuestId=null;

    public InteractionPortal(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.PORTAL;
        if(mProperties.containsKey("questId"))
        {
            mQuestId=mProperties.get("questId", String.class);
        }
        if(mProperties.containsKey("targetMapId"))
        {
            mTargetMapId=mProperties.get("targetMapId", String.class);
        }
        if (mProperties.containsKey("isDefaultStart")) {
            mIsDefaultStart = Boolean.parseBoolean(mProperties.get("isDefaultStart", String.class));
        }
        if(mProperties.containsKey("activatedByQuestId"))
        {
            mActivatedByQuestId = mProperties.get("activatedByQuestId", String.class);
        }
        if (mActivatedByQuestId != null) {
            Quest quest = QuestManager.getInstance().getQuestFromId(mActivatedByQuestId);
            if (quest.isCompleted()) {
                mActivatedByQuestId = null;
            } else {
                EventDispatcher.getInstance().addQuestListener(this);
            }
        }
    }


    @Override
    public Shape createShape() {
        mShape = new CircleShape();
        mShape.setY(0);
        mShape.setX(0);
        ((CircleShape) mShape).getShape().setRadius(1);
        return mShape;
    }
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType & CollisionComponent.CHARACTER) != 0 &&
                mActivatedByQuestId == null;
    }

    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return false;
    }

    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {
        if (mIsActivated && mTargetMapId!=null)
            EventDispatcher.getInstance().onNewMapRequested(mTargetMapId, null);
    }
    @Override
    public void onStopCollisionInteraction(CollisionComponent aEntity) {
        if(mActivatedByQuestId==null) {
            mIsActivated = true;
        }
    }

    public boolean isActivated() {
        return mIsActivated;
    }

    public void setActivated(boolean isActivated) {
        mIsActivated = isActivated;
    }

    public String getTargetMapId() {
        return mTargetMapId;
    }

    public boolean isDefaultStart() {
        return mIsDefaultStart;
    }


    @Override
    public void onQuestActivated(Quest aQuest) {

    }

    @Override
    public void onQuestCompleted(Quest aQuest) {
        if(mActivatedByQuestId!=null && aQuest.getId().compareTo(mActivatedByQuestId)==0)
        {
            mActivatedByQuestId=null;
        }
    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {

    }

    public String getQuestId()
    {
        return mQuestId;
    }

    public void setQuestId(String aQuestId)
    {
        mQuestId = aQuestId;
    }

}
