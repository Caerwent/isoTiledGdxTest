package com.vte.libgdx.ortho.test.player;

import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IItemListener;
import com.vte.libgdx.ortho.test.items.Item;

/**
 * Created by gwalarn on 05/12/16.
 */

public class Player implements IItemListener {



    public interface IPlayerListener {
        public void onInventoryChanged();
    }

    static private Player sPlayer = new Player();

    public static Player getInstance() {
        return sPlayer;
    }

    private Array<Item> mInventory;
    private Array<IPlayerListener> mListeners;

    public Player() {
        mInventory = new Array<Item>();
        mListeners = new Array<IPlayerListener>();
        EventDispatcher.getInstance().addItemListener(this);
    }


    public void registerListener(IPlayerListener aListener) {
        if (!mListeners.contains(aListener, true)) {
            mListeners.add(aListener);
        }
    }

    public void unregisterListener(IPlayerListener aListener) {
        if (mListeners.contains(aListener, true)) {
            mListeners.removeValue(aListener, true);
        }
    }

    public void addItem(Item aItem) {
        if (!mInventory.contains(aItem, true)) {
            mInventory.add(aItem);
            for (IPlayerListener listener : mListeners) {
                listener.onInventoryChanged();
            }
        }
    }

    public void removeItem(Item aItem) {
        if (mInventory.contains(aItem, true)) {
            mInventory.removeValue(aItem, true);
            for (IPlayerListener listener : mListeners) {
                listener.onInventoryChanged();
            }
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
}
