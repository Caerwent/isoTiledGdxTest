package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.box2d.CircleShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.dialogs.DialogsManager;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.items.Chest;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionChest extends Interaction{
    private static final String KEY_IS_OPEN = "is_open";
    protected boolean mIsOpen;
    protected Chest mChest;
    protected String mRequiredItem;

    public InteractionChest(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.CHEST;
        mChest = ItemFactory.getInstance().getChest(getId());

    }

    @Override
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);
        if (aMapping.properties != null) {
            mRequiredItem = (String) aMapping.properties.get("requiredItem");
        }

    }

    @Override
    public void restoreFromPersistence(GameSession aGameSession) {
        Boolean isOpen = (Boolean) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), getId(), KEY_IS_OPEN);
        if (isOpen != null && isOpen.booleanValue()) {
            mIsOpen = true;
            setState("OPEN");
        } else {
            mIsOpen = false;
            setState("CLOSED");
        }

    }

    @Override
    public GameSession saveInPersistence(GameSession aGameSession) {
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN, mIsOpen);
        return aGameSession;
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

        for (String itemId : mChest.getItems()) {
            Item item = ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(itemId));
            mMap.getPlayer().onItemFound(item);
            AudioManager.getInstance().onAudioEvent(AudioManager.ITEM_FOUND_SOUND);

        }

        if(getPersistence()!=Persistence.NONE)
        {
            saveInPersistence();
        }

    }
}
