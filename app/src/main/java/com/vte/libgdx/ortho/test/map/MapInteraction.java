package com.vte.libgdx.ortho.test.map;

import com.vte.libgdx.ortho.test.box2d.Shape;

/**
 * Created by gwalarn on 13/11/16.
 */

public class MapInteraction {
    public enum Type {
        START,
        ITEM,
        CHESS
    }

    protected Type mType;
    protected float mX, mY;

    public MapInteraction(float aX, float aY, Type aType) {
        mX = aX;
        mY = aY;
        mType = aType;
    }

    public float getX() {
        return mX;
    }
    public float getY() {
        return mY;
    }

    public Type getType() {
        return mType;
    }
}
