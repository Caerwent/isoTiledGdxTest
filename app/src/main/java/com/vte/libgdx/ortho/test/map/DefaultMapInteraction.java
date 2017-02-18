package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;

/**
 * Created by vincent on 05/01/2017.
 */

public class DefaultMapInteraction extends Entity implements IMapInteraction {
    protected IMapInteraction.Type mType;
    protected float mX, mY;
    protected String mQuestId;

    public DefaultMapInteraction(float aX, float aY, Type aType) {
        mX = aX;
        mY = aY;
        mType = aType;
    }

    @Override
    public float getX() {
        return mX;
    }

    @Override
    public float getY() {
        return mY;
    }

    @Override
    public IMapInteraction.Type getInteractionType() {
        return mType;
    }

    @Override
    public String getQuestId()
    {
        return mQuestId;
    }

    @Override
    public void setQuestId(String aQuestId)
    {
        mQuestId = aQuestId;
    }

}
