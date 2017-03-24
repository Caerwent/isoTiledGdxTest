package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionObstacle extends Interaction {
    private static final String KEY_IS_DESTROYED = "is_destroyed";

    protected String mDestroyableState;
    protected String mDestroyableEffect;
    protected String mDestroyedState;
    protected boolean mIsDestroyed;

    public InteractionObstacle(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.OBSTACLE;
    }

    @Override
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);
        if (aMapping.properties != null) {
            mDestroyableState = (String) aMapping.properties.get("destroyableState");
            mDestroyableEffect = (String) aMapping.properties.get("destroyableEffect");
            mDestroyedState = (String) aMapping.properties.get("destroyedState");
        }

    }

    public void restoreFromPersistence() {
       Boolean isDestroyed = (Boolean) GameSession.getInstance().getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsDestroyed = true;
            remove(CollisionComponent.class);
            mCurrentState = getState(mDestroyedState);
        } else {
            mIsDestroyed = false;
        }

    }

    public void saveInPersistence() {
        GameSession.getInstance().putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED, mIsDestroyed);
    }

    @Override
    protected void stopLaunchedEffect() {
        Effect stoppedEffect = mEffectLaunched;
        super.stopLaunchedEffect();

        if (stoppedEffect != null && stoppedEffect.id == Effect.Type.PORTAL) {

        }
    }

    @Override
    public boolean onStartEffectInteraction(CollisionComponent aEntity) {
        Effect effect = (Effect) aEntity.mData;
        if (effect != null) {
            InteractionState effectState = getState(effect.targetState);
            if (effectState != null) {

                if (mDestroyableEffect != null && mDestroyableEffect.compareTo(effect.id.name()) == 0) {
                    if (mDestroyableState != null) {
                        if (mCurrentState != null && mCurrentState.name.compareTo(mDestroyableState) == 0) {
                            mIsDestroyed = true;
                        }
                        else
                        {
                            // do not launch effect action
                            return false;
                        }
                    } else {
                        mIsDestroyed = true;
                    }
                }

                mEffectAction = effect;
                mEffectActionTime = 0;

                mBeforeEffectActionState = mIsDestroyed ? getState(mDestroyedState) : mCurrentState;
                setState(effect.targetState);

                if (mIsMovable) {
                    remove(VelocityComponent.class);
                }

                return true;
            }
        }
        return false;
    }

    @Override
    protected void stopEffectAction() {
        super.stopEffectAction();
        if (mIsDestroyed) {
            remove(CollisionComponent.class);
        }
    }
}
