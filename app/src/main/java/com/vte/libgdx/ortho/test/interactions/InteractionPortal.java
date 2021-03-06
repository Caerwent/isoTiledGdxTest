package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.box2d.CircleShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.entity.components.CollisionEffectComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionInteractionComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionObstacleComponent;
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
        remove(CollisionObstacleComponent.class);
        remove(CollisionEffectComponent.class);
        if(mProperties!=null)
        {
            if(mProperties.containsKey("activateQuestId"))
            {
                mQuestId=(String) aMapping.properties.get("activateQuestId");
            }
            if(mProperties.containsKey("targetMapId"))
            {
                mTargetMapId=(String) aMapping.properties.get("targetMapId");
            }
            if (mProperties.containsKey("isDefaultStart")) {
                mIsDefaultStart = Boolean.parseBoolean((String)aMapping.properties.get("isDefaultStart"));
            }
            if(mProperties.containsKey("activatedByQuestId"))
            {
                mActivatedByQuestId = (String) aMapping.properties.get("activatedByQuestId");
            }
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
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);

    }
    @Override
    public Shape createShapeInteraction() {
        mShapeInteraction = new CircleShape();
        mShapeInteraction.setY(getPosition().x);
        mShapeInteraction.setX(getPosition().y);
        float radius = /*isClickable() ? 1F :*/ 0.5F;
        ((CircleShape) mShapeInteraction).setRadius(radius);
        return mShapeInteraction;
    }
    public boolean hasCollisionInteraction(CollisionInteractionComponent aEntity) {
        return mActivatedByQuestId == null;
    }


    protected void teleport()
    {
        if (mIsActivated && mTargetMapId!=null)
            EventDispatcher.getInstance().onNewMapRequested(mTargetMapId, null);
    }

    @Override
    public void onTouchInteraction() {
        teleport();
    }
    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return mDef.isClickable && mIsActivated;
    }

    @Override
    public void onStartCollisionInteraction(CollisionInteractionComponent aEntity) {
        if(!mDef.isClickable) {
            teleport();
        }
    }
    @Override
    public void onStopCollisionInteraction(CollisionInteractionComponent aEntity) {
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
