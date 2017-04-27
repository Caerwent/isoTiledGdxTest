package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.entity.components.CollisionObstacleComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionObstacle extends Interaction {
    private static final String KEY_IS_OPEN = "is_open";
    private static final String KEY_OPEN_STATE = "open_state";
    private static final String KEY_IS_DESTROYED = "is_destroyed";

    protected boolean mIsDestroyed = false;
    protected boolean mIsOpen;
    protected CollisionObstacleComponent mCollisionObstacleComponent;
    protected boolean mClosedBoundsAsObstacle;
    protected boolean mOpenBoundsAsObstacle;
    protected int mOpenZIndex = 1;
    protected boolean mKillableWhenClosed;
    protected boolean mHasCollisionWhenOpen;

    public InteractionObstacle(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.OBSTACLE;
        if (mProperties != null) {
            /**
             * closedBoundsAsObstacle : true if collision when closed should be manage as obstacle and not as interaction obstacle
             * killableWhenClosed : restart map if hero has collision with bounds when obstacle becomes closed
             * hasCollisionWhenOpen : true if obstacle collision is managed even when obstacle is open (usefull for barrier)
             * openBoundsAsObstacle : true if collision when open should be manage as obstacle and not as interaction obstacle
             * openZIndex : if 0, hero is always be drawn over the open obstacle
             */
            if (mProperties.containsKey("closedBoundsAsObstacle") && Boolean.parseBoolean((String) mProperties.get("closedBoundsAsObstacle"))) {
                mClosedBoundsAsObstacle = true;
            }
            if (mProperties.containsKey("killableWhenClosed") && Boolean.parseBoolean((String) mProperties.get("killableWhenClosed"))) {
                mKillableWhenClosed = true;
            }
            if (mProperties.containsKey("hasCollisionWhenOpen") && Boolean.parseBoolean((String) mProperties.get("hasCollisionWhenOpen"))) {
                mHasCollisionWhenOpen = true;
            }
            if (mProperties.containsKey("openBoundsAsObstacle") && Boolean.parseBoolean((String) mProperties.get("openBoundsAsObstacle"))) {
                mOpenBoundsAsObstacle = true;
            }
            if (mProperties.containsKey("openZIndex")) {
                mOpenZIndex = ((Float)mProperties.get("openZIndex")).intValue();
            } else {
                mOpenZIndex = super.getZIndex();
            }
        }

        onStateChanged();

    }

    @Override
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);
        mCollisionObstacleComponent = getComponent(CollisionObstacleComponent.class);

    }

    public int getZIndex() {
        return mOpenZIndex;
    }

    @Override
    public void restoreFromPersistence(GameSession aGameSession) {
        Boolean isOpen = (Boolean) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN);
        if (isOpen != null && isOpen.booleanValue()) {
            mIsOpen = true;
            String state = (String) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE);
            mCurrentState = getState(state == null ? mDef.defaultState : state);
        } else {
            mIsOpen = false;
        }
        Boolean isDestroyed = (Boolean) GameSession.getInstance().getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsDestroyed = true;
        } else {
            mIsDestroyed = false;
        }

    }

    public GameSession saveInPersistence(GameSession aGameSession) {
        GameSession.getInstance().putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED, mIsDestroyed);
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN, mIsOpen);
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE, mCurrentState.name);
        return aGameSession;
    }

    @Override
    protected boolean doActionOnEvent(InteractionEventAction aAction) {
        boolean res = super.doActionOnEvent(aAction);
        boolean stateChanged = false;
        if (!res && aAction != null && InteractionEventAction.ActionType.OPEN.name().equals(aAction.id)) {
            mIsOpen = true;
            stateChanged = true;
        } else if (!res && aAction != null && InteractionEventAction.ActionType.CLOSE.name().equals(aAction.id)) {
            mIsOpen = false;
            stateChanged = true;
        } else if (!res && aAction != null && InteractionEventAction.ActionType.REMOVED.name().equals(aAction.id)) {
            mIsDestroyed = true;
            stateChanged = true;
        }
        if (stateChanged) {
            onStateChanged();
            res = stateChanged;
        }

        return res;
    }


    @Override
    public boolean onCollisionObstacleStart(CollisionObstacleComponent aEntity) {

        boolean ret = super.onCollisionObstacleStart(aEntity);
        if (ret && (aEntity.mType & CollisionObstacleComponent.HERO) != 0 && !mIsOpen && mKillableWhenClosed && aEntity.mHandler != null && aEntity.mHandler == mMap.getPlayer().getHero()) {
            EventDispatcher.getInstance().onMapReloadRequested(mMap.getMapName(), mMap.getFromMapId());
                return false;

        }
        return ret;
    }

    protected void onStateChanged() {
        if (mIsDestroyed) {
            Gdx.app.debug("DEBUG", "remove obstacle " + getId());
            mMap.getInteractions().removeValue(this, true);
            destroy();
            return;
        }

        if (mIsOpen) {
            if (mHasCollisionWhenOpen) {
                if(mOpenBoundsAsObstacle)
                {
                    mCollisionHeightFactor = 1;
                }
                else
                {
                    mCollisionHeightFactor = 8;
                }
                if (getComponent(CollisionObstacleComponent.class) == null) {
                    add(mCollisionObstacleComponent);
                }
            } else {
                Gdx.app.debug("DEBUG", "remove obstacle " + getId());
                remove(CollisionObstacleComponent.class);
            }
        } else {
            if(mClosedBoundsAsObstacle)
            {
                mCollisionHeightFactor = 1;
            }
            else
            {
                mCollisionHeightFactor = 8;
            }
            if (getComponent(CollisionObstacleComponent.class) == null) {
                add(mCollisionObstacleComponent);
            }
        }
    }



}
