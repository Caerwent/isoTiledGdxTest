package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IQuestListener;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestManager;
import com.vte.libgdx.ortho.test.quests.QuestTask;

/**
 * Created by vincent on 26/01/2017.
 */

public class MapInteractionPortal extends DefaultMapInteraction implements IMapInteraction, ICollisionHandler, IQuestListener {
    protected Entity mEntity;
    protected RectangleShape mShape;
    protected String mTargetMapId;
    protected boolean mIsDefaultStart = false;
    boolean mIsActivated = false;
    String mActivatedByQuestId;
    GameMap mMap;

    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();

    public MapInteractionPortal(float aX, float aY, String aTargetMapId, boolean aIsDefaultStart, GameMap aMap, String aActivatedByQuestId) {
        super(aX, aY, Type.PORTAL);
        mMap = aMap;
        mTargetMapId = aTargetMapId;
        mIsDefaultStart = aIsDefaultStart;
        mEntity = new Entity();
        EntityEngine.getInstance().addEntity(mEntity);
        mShape = new RectangleShape();
        mShape.setShape(new Rectangle(getX(), getY(), 1, 1));
        mEntity.add(new CollisionComponent(CollisionComponent.MAPINTERACTION, mShape, mTargetMapId, this, this));
        mActivatedByQuestId = aActivatedByQuestId;
        if(mActivatedByQuestId!=null)
        {
            Quest quest = QuestManager.getInstance().getQuestFromId(mActivatedByQuestId);
            if(quest.isCompleted()) {
                mActivatedByQuestId=null;
            }
            else
            {
                EventDispatcher.getInstance().addQuestListener(this);
            }
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
    public boolean onCollisionStart(CollisionComponent aEntity) {
        if (!mCollisions.contains(aEntity, false)) {
            mCollisions.add(aEntity);
            if (aEntity.mType == CollisionComponent.CHARACTER && mActivatedByQuestId==null) {
                Gdx.app.debug("DEBUG", "PORTAL onCollisionStart mIsActivated="+mIsActivated);
                if (mIsActivated)
                    EventDispatcher.getInstance().onNewMapRequested(mTargetMapId);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        if (mCollisions.contains(aEntity, false)) {
            mCollisions.removeValue(aEntity, false);
            if ((aEntity.mType & CollisionComponent.CHARACTER)!=0 && mActivatedByQuestId==null) {
                mIsActivated = true;
                Gdx.app.debug("DEBUG", "PORTAL onCollisionStop");
                return true;
            }

        }
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
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
}
