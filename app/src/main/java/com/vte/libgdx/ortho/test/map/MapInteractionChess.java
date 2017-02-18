package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.InputComponent;
import com.vte.libgdx.ortho.test.items.Chess;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;
import com.vte.libgdx.ortho.test.persistence.MapProfile;
import com.vte.libgdx.ortho.test.persistence.Profile;

/**
 * Created by vincent on 05/01/2017.
 */

public class MapInteractionChess extends DefaultMapInteraction implements IMapInteractionRendable, InputProcessor, ICollisionHandler {


    protected boolean mIsRended = false;
    protected Chess mChess;
    private RectangleShape mShape;
    private Camera mCamera;
    private TextureRegion mTextureRegionOpen;
    private TextureRegion mTextureRegionClose;
    protected TextureRegion mCurrentTextureRegion;
    protected boolean mIsOpen;

    protected GameMap mMap;
    protected Entity mEntity;
    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();

    public MapInteractionChess(float aX, float aY, Camera aCamera, Chess aChess, GameMap aMap) {
        super(aX, aY, Type.CHESS);
        mChess = aChess;
        mCamera = aCamera;
        mMap = aMap;
        mTextureRegionOpen = AssetsUtility.ITEMS_TEXTUREATLAS.findRegion(mChess.getOpenTexture());
        mTextureRegionClose = AssetsUtility.ITEMS_TEXTUREATLAS.findRegion(mChess.getCloseTexture());
        initialize();

    }

    public void initialize() {
        MapProfile profile = Profile.getInstance().getMapProfile(mMap.getMapName());
        if (profile != null && profile.openChessList.contains(mChess.getId())) {
            mIsOpen = true;
            mCurrentTextureRegion = mTextureRegionOpen;
        } else {
            mIsOpen = false;
            mCurrentTextureRegion = mTextureRegionClose;
        }
        EntityEngine.getInstance().addEntity(this);
        mShape = new RectangleShape();
        mShape.setShape(new Rectangle(getX(), getY(), mCurrentTextureRegion.getRegionWidth() * MyGame.SCALE_FACTOR, mCurrentTextureRegion.getRegionHeight() * MyGame.SCALE_FACTOR));
        add(new CollisionComponent(CollisionComponent.MAPINTERACTION, mShape, mChess.getId(), this, this));
        add(new InputComponent(this));

    }

    public boolean isRendable() {
        return true;
    }

    public boolean isRended() {
        return mIsRended;
    }

    public void setRended(boolean aRended) {
        mIsRended = aRended;
    }

    @Override
    public void render(Batch batch) {
        float width = mCurrentTextureRegion.getRegionWidth();
        float height = mCurrentTextureRegion.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;

        batch.draw(mCurrentTextureRegion,
                getX(), getY(),
                originX, originY,
                width, height,
                MyGame.SCALE_FACTOR, MyGame.SCALE_FACTOR,
                0);
    }


    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        if ((aEntity.mType & CollisionComponent.CHARACTER)!=0 && !mIsOpen) {
            if (!mCollisions.contains(aEntity, false)) {

                if (aEntity.mShape.getBounds().getY() > mShape.getBounds().getY())
                    return false;
                mCollisions.add(aEntity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        if ((aEntity.mType & CollisionComponent.CHARACTER)!=0 && !mIsOpen) {
            if (mCollisions.contains(aEntity, false)) {
                mCollisions.removeValue(aEntity, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(mCollisions.size<=0)
            return false;
        
        Vector3 cursorPoint = new Vector3();

        mCamera.unproject(cursorPoint.set(screenX, screenY, 0));

        if (!mIsOpen && mShape.getBounds().contains(cursorPoint.x, cursorPoint.y)) {
            onInteractionStart();
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void onInteractionStart() {
        mIsOpen = true;
        mCurrentTextureRegion = mTextureRegionOpen;
        for (String itemId : mChess.getItems()) {
            Item item = ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(itemId));
            mMap.getPlayer().onItemFound(item);

        }
        MapProfile profile = Profile.getInstance().getMapProfile(mMap.getMapName());
        profile.openChessList.add(mChess.getId());
        Profile.getInstance().updateMapProfile(mMap.getMapName(), profile);

    }
}
