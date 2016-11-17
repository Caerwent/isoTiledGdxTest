package com.vte.libgdx.ortho.test.map;

/**
 * Created by gwalarn on 13/11/16.
 */

public class MapControl {
    public enum Type {
        START

    }

    protected Type mType;
    protected float mX, mY;

    public MapControl(float x, float y, Type aType) {
        mX = x;
        mY = y;
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
