package com.vte.libgdx.ortho.test;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.vte.libgdx.ortho.test.box2d.PathHero;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.CollisionObstacleComponent;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.screens.GameScreen;

/**
 * Created by vincent on 01/07/2016.
 */

public class ChararcterMoveController2 extends InputAdapter {
    final OrthographicCamera camera;
    final Vector3 mCursorPoint = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();
    Shape mPathSpot;
    int mPointer = -1;

    Vector2 mLastPoint = new Vector2();

    PathHero path;
    public boolean isActive = false;
    private GameMap mMap;

    private ComponentMapper<CollisionObstacleComponent> cm = ComponentMapper.getFor(CollisionObstacleComponent.class);

    private ImmutableArray<Entity> entities;

    public ChararcterMoveController2(OrthographicCamera camera) {
        this.camera = camera;


    }

    public void setMap(GameMap aMap) {
        mMap = aMap;

    }
    /** Check whether the given line segment and {@link Polygon} intersect.
     * @param p1 The first point of the segment
     * @param p2 The second point of the segment
     * @return Whether polygon and segment intersect */
    public static boolean intersectSegmentPolygon (Vector2 p1, Vector2 p2, Polygon polygon) {
        float[] vertices = polygon.getTransformedVertices();
        float x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;
        int n = vertices.length;
        float x3 = vertices[n - 2], y3 = vertices[n - 1];
        for (int i = 0; i < n; i += 2) {
            float x4 = vertices[i], y4 = vertices[i + 1];
            if(y1==y2 && y2==y3 && y3==y4)
            {
                if((x3<=x1 && x2<=x4) || (x1<=x3 && x4<=x2))
                    return true;
            }
            float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
            if (d != 0) {
                float yd = y1 - y3;
                float xd = x1 - x3;
                float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
                if (ua >= 0 && ua <= 1) {
                    float ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
                    if (ub >= 0 && ub <= 1) {
                        return true;
                    }
                }
            }
            x3 = x4;
            y3 = y4;
        }
        return false;
    }
    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (mPointer != pointer)
            return false;
        if (!isActive)
            return false;
        camera.unproject(mCursorPoint.set(x, y, 0));
        //   Gdx.app.debug("DEBUG", "segment (last, mCursorPoint)=[(" + last.x + "," + last.y + ")" + "(" + mCursorPoint.x + "," + mCursorPoint.y + ")]");

        if (mMap != null && !(last.x == -1 && last.y == -1 && last.z == -1) && !(last.x == mCursorPoint.x && last.y == mCursorPoint.y)) {
            delta.set(mCursorPoint);
            delta.sub(last);
            //    Gdx.app.debug("DEBUG", "delta=(" + delta.x + "," + delta.y + ")");

            if (delta.x * delta.x + delta.y * delta.y < PathHero.CHECK_RADIUS)
                return true;
            last.set(mCursorPoint);

            mPathSpot.setX(mPathSpot.getX() + delta.x);
            mPathSpot.setY(mPathSpot.getY() + delta.y);
            boolean hasCollision = false;

            Vector2 V1 = new Vector2(mPathSpot.getBounds().getX(), mPathSpot.getBounds().getY());
            Vector2 V2 = new Vector2(mPathSpot.getBounds().getX() + mPathSpot.getBounds().getWidth(), mPathSpot.getBounds().getY());
            Vector2 V3 = new Vector2(mLastPoint.x, mLastPoint.y);
            //    Array<Shape> collisions = mMap.getBodiesCollision();
            //    if (collisions != null && collisions.size > 0) {
            if (entities != null && entities.size() > 0) {
                for (Entity entity : entities) {

                    CollisionObstacleComponent collision = entity.getComponent(CollisionObstacleComponent.class);

                     //   Gdx.app.debug("DEBUG", "check entity " + entity+ " "+collision.mName);
                    if (((collision.mType & CollisionObstacleComponent.OBSTACLE) != 0 || ((collision.mType & CollisionObstacleComponent.MAPINTERACTION) != 0))
                            && ShapeUtils.overlaps(mPathSpot, collision.mShape)
                            ) {
                           hasCollision = true;
                           break;
                       }


                    }

            }
            //    Gdx.app.debug("DEBUG", "hasCollision=" + hasCollision);

            if (!hasCollision) {

              //  double dx = mPathSpot.getX() - mLastPoint.x;
              //  double dy = mPathSpot.getY() - mLastPoint.y;
                //    Gdx.app.debug("DEBUG", "D=" + (x * dx + dy * dy));
               // if ((dx * dx + dy * dy) >= PathHero.CHECK_RADIUS) {
                    mLastPoint.set(mLastPoint.x + delta.x, mLastPoint.y + delta.y);
                    path.addPoint(mLastPoint.x, mLastPoint.y);
              //  }


            }
            else
            {
                mPathSpot.setX(mPathSpot.getX() - delta.x);
                mPathSpot.setY(mPathSpot.getY() - delta.y);
            }

        }

        return true;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {

        if (mPointer != -1 && mPointer != pointer) {
            return false;
        }
        mPointer = pointer;
        entities = EntityEngine.getInstance().getEntitiesFor(Family.all(CollisionObstacleComponent.class).get());

        camera.unproject(mCursorPoint.set(x, y, 0));

        if (mMap != null && mMap.getPlayer() != null && mMap.getPlayer().getHero() != null && mMap.getPlayer().getHero().getShapeRendering().getBounds().contains(mCursorPoint.x, mCursorPoint.y)) {
            if (path != null)
                path.destroy();
            path = new PathHero();
            mMap.getPlayer().getHero().setVelocity(0, 0);
            Vector2 bobPos = mMap.getPlayer().getHero().getPosition();
            float heroShapeHalfWidth = mMap.getPlayer().getHero().getShapeRendering().getWidth() / 2;
            path.addPoint(bobPos.x+heroShapeHalfWidth, bobPos.y);
            mPathSpot=mMap.getPlayer().getHero().getShapeCollision().clone();
            mPathSpot.setX(bobPos.x);
            mPathSpot.setY(bobPos.y);
            ((GameScreen) MyGame.getInstance().getScreenType(MyGame.ScreenType.MainGame)).setSpotShape(mPathSpot);
            last.set(mCursorPoint);
            mLastPoint.set(bobPos.x+heroShapeHalfWidth, bobPos.y);
            isActive = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (mPointer != pointer)
            return false;
        last.set(-1, -1, -1);
        if (mMap != null && mMap.getPlayer() != null && mMap.getPlayer().getHero() != null && isActive) {
            isActive = false;
            mMap.getPlayer().getHero().setPath(path);
            ((GameScreen) MyGame.getInstance().getScreenType(MyGame.ScreenType.MainGame)).setSpotShape(null);
        }
        return false;
    }


}