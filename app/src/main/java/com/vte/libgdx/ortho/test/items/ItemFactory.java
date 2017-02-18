package com.vte.libgdx.ortho.test.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.vte.libgdx.ortho.test.AssetsUtility;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gwalarn on 27/11/16.
 */

public class ItemFactory {
    private Json _json = new Json();
    private final String INVENTORY_ITEM = "data/items/items.json";
    private final String CHESS_FILE = "data/items/chess.json";
    private static ItemFactory _instance = null;
    private Hashtable<Item.ItemTypeID, Item> mItemsList;
    private Hashtable<String, Chess> mChessList;

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
}
