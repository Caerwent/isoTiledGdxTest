package com.vte.libgdx.ortho.test.player;

import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IItemListener;
import com.vte.libgdx.ortho.test.interactions.InteractionFactory;
import com.vte.libgdx.ortho.test.interactions.InteractionHero;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemFactory;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.Profile;

import java.util.ArrayList;

/**
 * Created by gwalarn on 05/12/16.
 */

public class Player implements IItemListener {





    private Array<Item> mInventory;
    private InteractionHero mHero;

    public Player(GameMap aGameMap) {
        mInventory = new Array<Item>();
        ArrayList<String> savedInventory = Profile.getInstance().getInventory();

        if(savedInventory!=null) {
            for (String itemId : savedInventory) {
                mInventory.add(ItemFactory.getInstance().getInventoryItem(Item.ItemTypeID.valueOf(itemId)));
            }
        }
        // instantiate the hero
        mHero = InteractionFactory.getInstance().createInteractionHero(aGameMap);
        EventDispatcher.getInstance().addItemListener(this);
        EventDispatcher.getInstance().onInventoryChanged(this);
    }

    public void destroy()
    {
        EventDispatcher.getInstance().removeItemListener(this);
    }



    public void addItem(Item aItem) {
        if (!mInventory.contains(aItem, true)) {
            mInventory.add(aItem);
            Profile.getInstance().updateInventory(mInventory);
            EventDispatcher.getInstance().onInventoryChanged(this);

        }
    }

    public void removeItem(Item aItem) {
        if (mInventory.contains(aItem, true)) {
            mInventory.removeValue(aItem, true);
            Profile.getInstance().updateInventory(mInventory);
            EventDispatcher.getInstance().onInventoryChanged(this);

        }
    }

    @Override
    public void onItemFound(Item aItem) {
        addItem(aItem);
    }

    @Override
    public void onItemLost(Item aItem) {
        removeItem(aItem);
    }

    public Array<Item> getInventory() {
        return mInventory;
    }

    public Array<Item> getItemsInventoryById(String aItemId) {

        Array<Item> result = new Array<Item>();
        for(Item item:mInventory)
        {
            if(item.getItemTypeID().name().equals(aItemId))
            {
                result.add(item);
            }
        }
        return result;
    }

    public InteractionHero getHero()
    {
        return mHero;
    }
}
