package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
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
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType&CollisionComponent.CHARACTER)!=0 && !mIsOpen;
    }
    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return getShape().getBounds().contains(x, y) && !mIsOpen;
    }
    @Override
    public void onTouchInteraction() {

        mIsOpen = true;
        setState("OPEN");

        for (String itemId : mChess.getItems()) {
            Item item = ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(itemId));
            mMap.getPlayer().onItemFound(item);

        }
        MapProfile profile = Profile.getInstance().getMapProfile(mMap.getMapName());
        profile.openChessList.add(mChess.getId());
        Profile.getInstance().updateMapProfile(mMap.getMapName(), profile);

    }
}
