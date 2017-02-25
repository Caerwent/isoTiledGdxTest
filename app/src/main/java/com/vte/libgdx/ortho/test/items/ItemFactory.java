package com.vte.libgdx.ortho.test.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.items.Item.ItemTypeID;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gwalarn on 27/11/16.
 */

public class ItemFactory {
    private Json _json = new Json();
    private final String INVENTORY_ITEM = "data/items/items.json";
    private final String ITEM_COMBINAISON_FILE = "data/items/item_combinaisons.json";
    private final String CHESS_FILE = "data/items/chess.json";
    private static ItemFactory _instance = null;
    private Hashtable<Item.ItemTypeID, Item> mItemsList;
    private Hashtable<String, Chess> mChessList;
    private OrderedMap<ItemTypeID, OrderedMap<ItemTypeID, ItemCombinaison>> mCombinaisonList;

    public static ItemFactory getInstance() {
        if (_instance == null) {
            _instance = new ItemFactory();
        }

        return _instance;
    }

    private ItemFactory() {
        ArrayList<JsonValue> list = _json.fromJson(ArrayList.class, Gdx.files.internal(INVENTORY_ITEM));
        mItemsList = new Hashtable<Item.ItemTypeID, Item>();

        for (JsonValue jsonVal : list) {
            Item item = _json.readValue(Item.class, jsonVal);
            mItemsList.put(item.getItemTypeID(), item);
        }

        ArrayList<JsonValue> combList = _json.fromJson(ArrayList.class, Gdx.files.internal(ITEM_COMBINAISON_FILE));
        mCombinaisonList = new OrderedMap<ItemTypeID, OrderedMap<ItemTypeID, ItemCombinaison>>();
        for (JsonValue jsonVal : combList) {
            ItemCombinaison itemComb = _json.readValue(ItemCombinaison.class, jsonVal);
            addItemCombinaison(itemComb);
        }


        ArrayList<JsonValue> chessList = _json.fromJson(ArrayList.class, Gdx.files.internal(CHESS_FILE));
        mChessList = new Hashtable<String, Chess>();

        for (JsonValue jsonVal : chessList) {
            Chess chess = _json.readValue(Chess.class, jsonVal);
            mChessList.put(chess.getId(), chess);
        }
    }

    public Item getInventoryItem(Item.ItemTypeID inventoryItemType) {
        Item item = new Item(mItemsList.get(inventoryItemType));
        item.setTextureRegion(AssetsUtility.ITEMS_TEXTUREATLAS.findRegion(item.getItemTypeID().toString()));
        return item;
    }

    public Chess getChess(String aChessId)
    {
        return mChessList.get(aChessId);
    }

    public void addItemCombinaison(ItemCombinaison aItemComb)
    {
        OrderedMap<ItemTypeID, ItemCombinaison> entry1 = mCombinaisonList.get(aItemComb.item1);
        OrderedMap<ItemTypeID, ItemCombinaison> entry2 = mCombinaisonList.get(aItemComb.item2);
        if(entry1==null)
        {
            entry1 = new OrderedMap<ItemTypeID, ItemCombinaison>();
            entry1.put(aItemComb.item2, aItemComb);
            mCombinaisonList.put(aItemComb.item1, entry1);
        }
        else
        {
            if(!entry1.containsKey(aItemComb.item2))
            {
                entry1.put(aItemComb.item2, aItemComb);
            }
        }

        if(entry2==null)
        {
            entry2 = new OrderedMap<ItemTypeID, ItemCombinaison>();
            entry2.put(aItemComb.item1, aItemComb);
            mCombinaisonList.put(aItemComb.item2, entry2);
        }
        else
        {
            if(!entry2.containsKey(aItemComb.item1))
            {
                entry2.put(aItemComb.item1, aItemComb);
            }
        }
    }
    public ItemCombinaison getItemCombinaison(ItemTypeID item1, ItemTypeID item2)
    {
        ItemTypeID firstKey = item1;
        ItemTypeID secondKey = item2;
        OrderedMap<ItemTypeID, ItemCombinaison> row = mCombinaisonList.get(firstKey);
        if(row==null)
        {
            firstKey = item2;
            secondKey = item1;
            row = mCombinaisonList.get(firstKey);
        }
        if(row==null)
        {
            return null;
        }

        return row.get(secondKey);
    }
}
