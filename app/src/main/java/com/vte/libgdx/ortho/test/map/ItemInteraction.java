package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;

/**
 * Created by gwalarn on 27/11/16.
 */

public class ItemInteraction extends Entity implements IItemInteraction, IMapRendable, ICollisionHandler {

    protected IItemInteraction.Type mType;
    protected float mX, mY;



    protected String mId;
    protected Item mItem;
    protected RectangleShape mShape;
    protected boolean mIsRended = false;
    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();
    private  GameMap mMap;


    public ItemInteraction(float aX, float aY, String aId, GameMap aMap) {
        mX = aX;
        mY = aY;
        mType=Type.ITEM;
        mMap = aMap;
        mId = aId;
        mItem = ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(aId));
        EntityEngine.getInstance().addEntity(this);
        mShape = new RectangleShape();
        mShape.setShape(new Rectangle(getX(), getY(), mItem.getTextureRegion().getRegionWidth() * MyGame.SCALE_FACTOR, mItem.getTextureRegion().getRegionHeight() * MyGame.SCALE_FACTOR));
        add(new CollisionComponent(CollisionComponent.ITEM, mShape, aId, this, this));
        add(new VisualComponent(mItem.getTextureRegion(), this));
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
    public IItemInteraction.Type getInteractionType() {
        return mType;
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

    public boolean isRendable()
    {
        return true;
    }

    public boolean isRended()
    {
        return mIsRended;
    }

    public void setRended(boolean aRended)
    {
        mIsRended = aRended;
    }

    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        if ((aEntity.mType & CollisionComponent.CHARACTER) !=0) {
            mMap.removeItem(this);
            EntityEngine.getInstance().removeEntity(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
