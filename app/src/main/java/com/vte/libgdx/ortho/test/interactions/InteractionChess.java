package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.box2d.CircleShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.dialogs.DialogsManager;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.items.Chess;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.MapProfile;
import com.vte.libgdx.ortho.test.persistence.Profile;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionChess extends Interaction{
    protected boolean mIsOpen;
    protected Chess mChess;
    protected String mRequiredItem;

    public InteractionChess(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.CHESS;
        mChess = ItemFactory.getInstance().getChess(getId());
        MapProfile profile = Profile.getInstance().getMapProfile(aMap.getMapName());
        if (profile != null && profile.openChessList.contains(mChess.getId())) {
            mIsOpen = true;
            setState("OPEN");
        } else {
            mIsOpen = false;
            setState("CLOSED");
        }
    }

    @Override
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);
        if (aMapping.properties != null) {
            mRequiredItem = (String) aMapping.properties.get("requiredItem");
        }

    }

    @Override
    public Shape createShape() {
        if(isRendable())
        {
            return super.createShape();
        }
        else {
            mShape = new CircleShape();
            mShape.setY(0);
            mShape.setX(0);
            float radius = /*isClickable() ? 1F :*/ 0.5F;
            ((CircleShape) mShape).setRadius(radius);
            return mShape;
        }
    }
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType&CollisionComponent.CHARACTER)!=0 && !mIsOpen;
    }
    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return getShape().getBounds().contains(x, y) && !mIsOpen;
    }
    @Override
    public void onTouchInteraction() {

        if(mRequiredItem!=null && !mRequiredItem.isEmpty())
        {
            Array<Item> item = mMap.getPlayer().getItemsInventoryById(mRequiredItem);
            if(item!=null && item.size >0)
            {
                mMap.getPlayer().removeItem(item.get(0));
            }
            else
            {
                EventDispatcher.getInstance().onStartDialog(DialogsManager.getInstance().getDialog("needKey"));
                return;
            }
        }
        mIsOpen = true;
        setState("OPEN");

        for (String itemId : mChess.getItems()) {
            Item item = ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(itemId));
            mMap.getPlayer().onItemFound(item);
            AudioManager.getInstance().onAudioEvent(AudioManager.ITEM_FOUND_SOUND);

        }
        MapProfile profile = Profile.getInstance().getMapProfile(mMap.getMapName());
        profile.openChessList.add(mChess.getId());
        Profile.getInstance().updateMapProfile(mMap.getMapName(), profile);

    }
}
