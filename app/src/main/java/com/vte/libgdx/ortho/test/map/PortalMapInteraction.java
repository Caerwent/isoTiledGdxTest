package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;

/**
 * Created by vincent on 26/01/2017.
 */

public class PortalMapInteraction extends DefaultMapInteraction implements IMapInteraction, ICollisionHandler {
    protected Entity mEntity;
    protected RectangleShape mShape;
    protected String mTargetMapId;
    protected boolean mIsDefaultStart = false;
    boolean mIsActivated = false;
    GameMap mMap;

    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();

    public PortalMapInteraction(float aX, float aY, String aTargetMapId, boolean aIsDefaultStart, GameMap aMap) {
        super(aX, aY, Type.PORTAL);
        mMap = aMap;
        mTargetMapId = aTargetMapId;
        mIsDefaultStart = aIsDefaultStart;
        mEntity = new Entity();
        EntityEngine.getInstance().addEntity(mEntity);
        mShape = new RectangleShape();
        mShape.setShape(new Rectangle(getX(), getY(), 1, 1));
        mEntity.add(new CollisionComponent(CollisionComponent.Type.MAPINTERACTION, mShape, mTargetMapId, this, this));

    }

    public boolean isActivated() {
        return mIsActivated;
    }

    public void setActivated(boolean isActivated) {
        mIsActivated = isActivated;
    }

    public String getTargetMapId() {
        return mTargetMapId;
    }

    public boolean isDefaultStart() {
        return mIsDefaultStart;
    }

    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        if (!mCollisions.contains(aEntity, false)) {
            if (aEntity.mType == CollisionComponent.Type.CHARACTER) {
                mCollisions.add(aEntity);
                Gdx.app.debug("DEBUG", "PORTAL onCollisionStart");
                if (mIsActivated)
                    EventDispatcher.getInstance().onNewMapRequested(mTargetMapId);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        if (mCollisions.contains(aEntity, false)) {
            mCollisions.removeValue(aEntity, false);
            if (aEntity.mType == CollisionComponent.Type.CHARACTER) {
                mIsActivated = true;
                Gdx.app.debug("DEBUG", "PORTAL onCollisionStop");
                return true;
            }

        }
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
