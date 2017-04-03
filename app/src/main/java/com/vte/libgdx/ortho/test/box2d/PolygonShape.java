package com.vte.libgdx.ortho.test.box2d;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by gwalarn on 20/11/16.
 */

public class PolygonShape extends Shape<Polygon> {

    protected Polygon mPoly;

    public PolygonShape() {
        mType = Type.POLYGON;
        mPoly = new Polygon();
    }

    @Override
    public Polygon getShape() {
        return mPoly;
    }

    @Override
    public void setShape(Polygon aShape) {
        mPoly = aShape;
    }

    @Override
    public float getX() {
        return mPoly.getX();
    }

    @Override
    public float getY() {
        return mPoly.getY();
    }

    @Override
    public void setX(float x) {
        mPoly.setPosition(x, mPoly.getY());
    }

    @Override
    public void setY(float y) {
        mPoly.setPosition(mPoly.getX(), y);
    }

    @Override
    public Rectangle getBounds() {
        return mPoly.getBoundingRectangle();
    }

    @Override
    public Shape clone() {
        PolygonShape clone = new PolygonShape();
        clone.getShape().setVertices(mPoly.getVertices());
        clone.getShape().setOrigin(mPoly.getOriginX(), mPoly.getOriginY());
        clone.setX(getX());
        clone.setY(getY());
        return clone;
    }

    @Override
    public float getYAtX(float x) {
        Rectangle bounds = mPoly.getBoundingRectangle();
        if (x >= bounds.x && x <= (bounds.x + bounds.getWidth())) {

            Vector2 vertLineP1 = new Vector2(x, bounds.getY());
            Vector2 vertLineP2 = new Vector2(x, bounds.getHeight() + bounds.getY());

            float[] vertices = mPoly.getTransformedVertices();
            int n = vertices.length;
            Vector2 intersection = new Vector2(0, 0);
            float x1 = vertices[n - 2], y1 = vertices[n - 1];
            float minY=Float.MAX_VALUE;
            for (int i = 0; i < n; i += 2) {
                float x2 = vertices[i], y2 = vertices[i + 1];
                if ( ((x >= x1 && x <= x2) || (x >= x2 && x <= x1) ) && Intersector.intersectLines(vertLineP1.x, vertLineP1.y, vertLineP2.x, vertLineP2.y,
                        x1, y1, x2, y2,
                        intersection)) {
                    if(intersection.y<minY)
                        minY = intersection.y;
                }
                x1 = x2;
                y1 = y2;
            }
            return minY < Float.MAX_VALUE ? minY : -1;
        }
        return -1;
    }
}
