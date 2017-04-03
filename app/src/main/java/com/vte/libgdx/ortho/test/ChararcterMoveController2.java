package com.vte.libgdx.ortho.test;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.vte.libgdx.ortho.test.box2d.PathHero;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
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

    private ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);

    private ImmutableArray<Entity> entities;

    public ChararcterMoveController2(OrthographicCamera camera) {
        this.camera = camera;


    }

    public void setMap(GameMap aMap) {
        mMap = aMap;
        mPathSpot = mMap.getPlayer().getHero().getShape().clone();
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

            mPathSpot.setX(mLastPoint.x + delta.x);
            mPathSpot.setY(mLastPoint.y + delta.y);
            boolean hasCollision = false;
            //    Array<Shape> collisions = mMap.getBodiesCollision();
            //    if (collisions != null && collisions.size > 0) {
            if (entities != null && entities.size() > 0) {
                for (Entity entity : entities) {

                    CollisionComponent collision = entity.getComponent(CollisionComponent.class);
                    //    Gdx.app.debug("DEBUG", "check entity " + entity+ " "+collision.mName);
                    if (((collision.mType & CollisionComponent.OBSTACLE) != 0 || ((collision.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) != 0))
                            && ShapeUtils.overlaps(mPathSpot, collision.mShape)) {
                        //  Gdx.app.debug("DEBUG", "overlaps obstacle");
                        float Ycollision = collision.mShape.getYAtX(mPathSpot.getX());
                        float Ytmp = collision.mShape.getYAtX(mPathSpot.getX() + mPathSpot.getBounds().getWidth());
                        if (Ycollision == -1 || (Ytmp!=-1 && Ytmp < Ycollision)) {
                            Ycollision = Ytmp;
                        }
                        Ytmp = collision.mShape.getYAtX(mPathSpot.getX() + mPathSpot.getBounds().getWidth() / 2);
                        if (Ycollision == -1 || (Ytmp!=-1 && Ytmp < Ycollision)) {
                            Ycollision = Ytmp;
                        }
                        if (Ycollision == -1) {
                            Ycollision = collision.mShape.getBounds().getY();
                        }
                        float Yspot = mPathSpot.getYAtX(mPathSpot.getX());
                        Ytmp = mPathSpot.getYAtX(mPathSpot.getX() + mPathSpot.getBounds().getWidth());
                        if (Yspot == -1 || (Ytmp!=-1 && Ytmp < Yspot)) {
                            Yspot = Ytmp;
                        }
                        Ytmp = mPathSpot.getYAtX(mPathSpot.getX() + mPathSpot.getBounds().getWidth() / 2);
                        if (Yspot == -1 || (Ytmp!=-1 && Ytmp<Yspot))  {
                            Yspot = Ytmp;
                        }
                        if (Yspot == -1) {
                            Yspot = mPathSpot.getBounds().getY();
                        }
                        if (((collision.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) == 0 &&
                                Yspot >= Ycollision) ||

                                (Yspot >= Ycollision &&
                                        (collision.mType & CollisionComponent.OBSTACLE_MAPINTERACTION) != 0 &&
                                        ((Yspot - Ycollision) <= 0.5)
                                )
                                ) {
                            //Gdx.app.debug("DEBUG", "collision");
                            hasCollision = true;
                            break;

                        }
                    }
                }

            }
            //    Gdx.app.debug("DEBUG", "hasCollision=" + hasCollision);

            if (!hasCollision) {

                double dx = mPathSpot.getX() - mLastPoint.x;
                double dy = mPathSpot.getY() - mLastPoint.y;
                //    Gdx.app.debug("DEBUG", "D=" + (x * dx + dy * dy));
                if ((dx * dx + dy * dy) >= PathHero.CHECK_RADIUS) {
                    path.addPoint(mPathSpot.getX(), mPathSpot.getY());
                    mLastPoint.set(mPathSpot.getX(), mPathSpot.getY());
                }


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
        entities = EntityEngine.getInstance().getEntitiesFor(Family.all(CollisionComponent.class).get());

        camera.unproject(mCursorPoint.set(x, y, 0));

        if (mMap != null && mMap.getPlayer() != null && mMap.getPlayer().getHero() != null && mMap.getPlayer().getHero().getShape().getBounds().contains(mCursorPoint.x, mCursorPoint.y)) {
            if (path != null)
                path.destroy();
            path = new PathHero();
            mMap.getPlayer().getHero().setVelocity(0, 0);
            Vector2 bobPos = mMap.getPlayer().getHero().getPosition();
            path.addPoint(bobPos.x, bobPos.y);
            mPathSpot.setX(bobPos.x);
            mPathSpot.setY(bobPos.y);
            ((GameScreen) MyGame.getInstance().getScreenType(MyGame.ScreenType.MainGame)).setSpotShape(mPathSpot);
            last.set(mCursorPoint);
            mLastPoint.set(bobPos.x, bobPos.y);
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