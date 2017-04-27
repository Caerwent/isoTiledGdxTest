package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by gwalarn on 20/11/16.
 */

public class CircleShape extends Shape<Circle> {

    protected Circle mCircle;
    protected Rectangle mBounds = new Rectangle();

    public CircleShape()
    {
        mType = Type.CIRCLE;
        mCircle = new Circle();
    }
    @Override
    public Circle getShape() {
        return mCircle;
    }
    @Override
    public void setShape(Circle aShape)
    {
        mCircle = aShape;
    }
    @Override
    public float getX()
    {
        return mBounds.x;
    }
    @Override
    public float getY()
    {
        return mBounds.y;
    }
    public void setRadius(float radius)
    {
        mCircle.setRadius(radius);
        mBounds.setWidth(radius*2);
        mBounds.setHeight(radius*2);
    }
    @Override
    public void setX(float x)
    {
        mCircle.x = x+mCircle.radius;
        mBounds.setPosition(getX(), getY());
    }
    @Override
    public void setY(float y)
    {
        mCircle.y = y+mCircle.radius;

        mBounds.setPosition(getX(), getY());
    }
    @Override
    public Rectangle getBounds()
    {

        return mBounds;
    }
    @Override
    public float getWidth()
    {
        return mBounds.getWidth();
    }
    @Override
    public float getHeight()
    {
        return mBounds.getHeight();
    }
    @Override
    public Shape clone()
    {
        CircleShape clone = new CircleShape();
        clone.setRadius(mCircle.radius);
        clone.setX(getX());
        clone.setY(getY());
        return clone;
    }

    @Override
    public float getYAtX(float x)
    {
        if(x>=mBounds.x && x<=(mBounds.x+mBounds.getWidth()))
        {
            float xCircle = mBounds.x+mCircle.radius - x;
            return (float) (getY()+Math.sqrt((mCircle.radius * mCircle.radius)-(xCircle*xCircle)));
        }
        return -1;
    }
}
