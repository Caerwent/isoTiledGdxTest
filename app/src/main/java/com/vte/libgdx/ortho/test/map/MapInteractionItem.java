package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;

/**
 * Created by gwalarn on 27/11/16.
 */

public class MapInteractionItem extends MapInteraction implements ICollisionHandler {
    protected String mId;
    protected Entity mEntity;
    protected Item mItem;
    protected RectangleShape mShape;
    public boolean mIsRended = false;
    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();

    public MapInteractionItem(float aX, float aY, String aId) {
        super(aX, aY, Type.ITEM);
        mId = aId;
        mItem = ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(aId));
        mEntity = new Entity();
        EntityEngine.getInstance().addEntity(mEntity);
        mShape = new RectangleShape();
        mShape.setShape(new Rectangle(getX(), getY(), mItem.getTextureRegion().getRegionWidth() * MyGame.SCALE_FACTOR, mItem.getTextureRegion().getRegionHeight() * MyGame.SCALE_FACTOR));
        mEntity.add(new CollisionComponent(CollisionComponent.Type.ITEM, mShape, aId, this, this));
    }

    public String getId() {
        return mId;
    }

    public RectangleShape getShape() {
        return mShape;
    }

    public void render(Batch batch) {
        float width = mItem.getTextureRegion().getRegionWidth();
        float height = mItem.getTextureRegion().getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;

        batch.draw(mItem.getTextureRegion(),
                getX(), getY(),
                originX, originY,
                width, height,
                MyGame.SCALE_FACTOR, MyGame.SCALE_FACTOR,
                0);
    }

    public Item getItem() {
        return mItem;
    }

    @Override
    public void onCollisionStart(CollisionComponent aEntity) {
        if (aEntity.mType == CollisionComponent.Type.CHARACTER) {
            MapBodyManager.getInstance().getInteractions().removeValue(this, true);
            EntityEngine.getInstance().removeEntity(mEntity);
        }
    }

    @Override
    public void onCollisionStop(CollisionComponent aEntity) {

    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
