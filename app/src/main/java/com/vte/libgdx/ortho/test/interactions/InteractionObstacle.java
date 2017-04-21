package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

import static com.vte.libgdx.ortho.test.entity.components.CollisionComponent.OBSTACLE;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionObstacle extends Interaction {
    private static final String KEY_IS_OPEN = "is_open";
    private static final String KEY_OPEN_STATE = "open_state";
    private static final String KEY_IS_DESTROYED = "is_destroyed";

    protected boolean mIsDestroyed = false;
    protected boolean mIsOpen;
    protected CollisionComponent mCollisionComponent;
    protected boolean mFullObstacle;
    protected boolean mKillableWhenClosed;

    public InteractionObstacle(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.OBSTACLE;
        if (mProperties != null) {
            if (mProperties.containsKey("fullObstacle") && Boolean.parseBoolean((String) mProperties.get("fullObstacle"))) {
                mFullObstacle = true;
                byte type_collision = CollisionComponent.MAPINTERACTION;
                type_collision = (byte) (type_collision | OBSTACLE);
                mCollisionComponent.mType = type_collision;
                this.add(new CollisionComponent(type_collision, getShape(), mId, this, this));
            }
            if (mProperties.containsKey("killableWhenClosed") && Boolean.parseBoolean((String) mProperties.get("killableWhenClosed"))) {
                mKillableWhenClosed = true;
            }
        }

    }

    @Override
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);
        mCollisionComponent = getComponent(CollisionComponent.class);

    }

    public int getZIndex() {
        if (mFullObstacle)
            return 0;
        return super.getZIndex();
    }

    @Override
    public void restoreFromPersistence(GameSession aGameSession) {
        Boolean isOpen = (Boolean) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN);
        if (isOpen != null && isOpen.booleanValue()) {
            mIsOpen = true;
            remove(CollisionComponent.class);
            String state = (String) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE);
            mCurrentState = getState(state == null ? mDef.defaultState : state);
        } else {
            mIsOpen = false;
        }
        Boolean isDestroyed = (Boolean) GameSession.getInstance().getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsDestroyed = true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
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
        if (!res && aAction != null && InteractionEventAction.ActionType.OPEN.name().equals(aAction.id)) {
            Gdx.app.debug("DEBUG", "open obstacle " + getId());
            remove(CollisionComponent.class);
            mIsOpen = true;
            return true;
        } else if (!res && aAction != null && InteractionEventAction.ActionType.CLOSE.name().equals(aAction.id)) {
            Gdx.app.debug("DEBUG", "close obstacle " + getId());
            if (getComponent(CollisionComponent.class) == null) {
                add(mCollisionComponent);
            }
            mIsOpen = false;
            return true;
        } else if (!res && aAction != null && InteractionEventAction.ActionType.REMOVED.name().equals(aAction.id)) {
            mIsDestroyed = true;
            Gdx.app.debug("DEBUG", "remove obstacle " + getId());
            mMap.getInteractions().removeValue(this, true);
            destroy();
            return true;
        }
        return res;
    }

    @Override
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType & CollisionComponent.CHARACTER) != 0 && !mIsOpen && mKillableWhenClosed;
    }

    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {
        if ((aEntity.mType & CollisionComponent.CHARACTER) != 0 && !mIsOpen && mKillableWhenClosed) {
            if(aEntity.mHandler!=null && aEntity.mHandler == mMap.getPlayer().getHero()) {

                if (ShapeUtils.overlaps(mMap.getPlayer().getHero().getShapeForMovementCollision(), mShape)) {
                    EventDispatcher.getInstance().onMapReloadRequested(mMap.getMapName(),mMap.getFromMapId());
                }
            }
        }
    }


}
