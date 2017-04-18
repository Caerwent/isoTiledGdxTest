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
    private final String ITEM_COMBINAISON_FILE = "data/items/item_combinaisons.json";
    private final String CHEST_FILE = "data/items/chest.json";
    private static ItemFactory _instance = null;
    private Hashtable<String, Chest> mChestList;
    private OrderedMap<ItemTypeID, OrderedMap<ItemTypeID, ItemCombinaison>> mCombinaisonList;

    public static ItemFactory getInstance() {
        if (_instance == null) {
            _instance = new ItemFactory();
        }

        return _instance;
    }

    private ItemFactory() {
        ArrayList<JsonValue> combList = _json.fromJson(ArrayList.class, Gdx.files.internal(ITEM_COMBINAISON_FILE));
        mCombinaisonList = new OrderedMap<ItemTypeID, OrderedMap<ItemTypeID, ItemCombinaison>>();
        for (JsonValue jsonVal : combList) {
            ItemCombinaison itemComb = _json.readValue(ItemCombinaison.class, jsonVal);
            addItemCombinaison(itemComb);
        }


        ArrayList<JsonValue> chestList = _json.fromJson(ArrayList.class, Gdx.files.internal(CHEST_FILE));
        mChestList = new Hashtable<String, Chest>();

        for (JsonValue jsonVal : chestList) {
            Chest chest = _json.readValue(Chest.class, jsonVal);
            mChestList.put(chest.getId(), chest);
        }
    }

    public Item getInventoryItem(Item.ItemTypeID inventoryItemType) {
        Item item = new Item(inventoryItemType);
        item.setItemShortDescription(AssetsUtility.getString("itemDesc_"+item.getItemTypeID()));
        item.setTextureRegion(AssetsUtility.ITEMS_TEXTUREATLAS.findRegion(item.getItemTypeID().toString()));
        return item;
    }

    public Chest getChest(String aChestId)
    {
        return mChestList.get(aChestId);
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
