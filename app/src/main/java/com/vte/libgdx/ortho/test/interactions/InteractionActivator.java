package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionActivator extends Interaction{
    public InteractionActivator(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.ACTIVATOR;
    }
    @Override
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType&CollisionComponent.CHARACTER)!=0;
    }
    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {
        if((aEntity.mType&CollisionComponent.CHARACTER)!=0 && !isClickable())
        {
            toggleActivation();
        }
    }
    @Override
    public void onStopCollisionInteraction(CollisionComponent aEntity) {
        if((aEntity.mType&CollisionComponent.CHARACTER)!=0  && !isClickable())
        {
            toggleActivation();
        }
    }
    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return getShape().getBounds().contains(x, y);
    }
    @Override
    public void onTouchInteraction() {
        toggleActivation();

    }

    protected void toggleActivation()
    {
        if(mCurrentState.name.compareTo(mDef.states.get(0).name)==0)
        {
            setState(mDef.states.get(1).name);
        }
        else
        {
            setState(mDef.states.get(0).name);
        }
    }
}
