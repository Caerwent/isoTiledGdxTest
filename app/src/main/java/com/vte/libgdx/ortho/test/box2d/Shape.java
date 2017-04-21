package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gwalarn on 20/11/16.
 */

public abstract class Shape<T> {
    public static enum Type {
        CIRCLE,
        RECT,
        POLYGON
    }

    protected Type mType;
    public Type getType()
    {
        return mType;
    }

    abstract public T getShape();
    abstract public void setShape(T aShape);

    abstract public float getX();
    abstract public float getY();
    abstract public void setX(float x);
    abstract public void setY(float y);
    abstract public float getWidth();
    abstract public float getHeight();
    abstract public Rectangle getBounds();
    abstract public Shape clone();
    abstract public float getYAtX(float x);
}
