package com.vte.libgdx.ortho.test.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gwalarn on 27/11/16.
 */

public class ItemFactory {
    private Json _json = new Json();
    private final String INVENTORY_ITEM = "data/items/items.json";
    private static ItemFactory _instance = null;
    private Hashtable<Item.ItemTypeID, Item> _inventoryItemList;

    public static ItemFactory getInstance() {
        if (_instance == null) {
            _instance = new ItemFactory();
        }

        return _instance;
    }

    private ItemFactory() {
        ArrayList<JsonValue> list = _json.fromJson(ArrayList.class, Gdx.files.internal(INVENTORY_ITEM));
        _inventoryItemList = new Hashtable<Item.ItemTypeID, Item>();

        for (JsonValue jsonVal : list) {
            Item item = _json.readValue(Item.class, jsonVal);
            _inventoryItemList.put(item.getItemTypeID(), item);
        }
    }

    public Item getInventoryItem(Item.ItemTypeID inventoryItemType) {
        Item item = new Item(_inventoryItemList.get(inventoryItemType));
        item.setTextureRegion(AssetsUtility.ITEMS_TEXTUREATLAS.findRegion(item.getItemTypeID().toString()));
        return item;
    }
}
