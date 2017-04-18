package com.vte.libgdx.ortho.test.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by gwalarn on 27/11/16.
 */

public class Item {


    public enum ItemTypeID {
        Barrel,
        BookDark,
        Boots,
        ChessClosed,
        ChessOpen,
        CrystalAnimBlue,
        CrystalAnimGreen,
        CrystalAnimGrey,
        CrystalAnimPink,
        CrystalAnimYellow,
        CrystalsBlue,
        CrystalsGreen,
        CrystalsPurple,
        Dagger,
        DaggerBroken,
        FoodAle,
        FoodBiscuit,
        FoodBread,
        FoodBread2,
        FoodShank,
        Gear,
        Gloves,
        GlyphGreen,
        GlyphRed,
        GlyphYellow,
        Hammer,
        HammerSmall,
        Jewels,
        JewelsBlue,
        JewelsRed,
        KeyCrystal,
        KeyDark,
        KeyIron,
        KeyMoon,
        KeyRed,
        KeyRoyal,
        Lantern,
        Lantern2,
        NecklaceJewelBlue,
        NecklaceJewelGreen,
        NecklaceJewelOrange,
        NecklaceJewelRed,
        Pick,
        PotionBlueSmall,
        PotionGreenSmall,
        PotionRedSmall,
        PotionRounededBrown,
        PotionSilver,
        PotionTealBig,
        PotionVioletLarge,
        PotionYellowLarge,
        PotionYellowSmall,
        RobeBlue,
        Scroll01,
        Scroll02,
        Scroll03,
        Scroll04,
        Shovel,
        Shovel2,
        Torch,
        TorchFire,
        VialBlue,
        VialGreen,
        VialRed
    }

    private ItemTypeID itemTypeID;
    private String itemShortDescription;
    private TextureRegion mTextureRegion;


    public Item(TextureRegion textureRegion, ItemTypeID itemTypeID) {
        mTextureRegion = textureRegion;

        this.itemTypeID = itemTypeID;

    }

    public Item() {
        super();
    }

    public Item(ItemTypeID inventoryItemType) {
        super();
        this.itemTypeID = inventoryItemType;
    }

    public Item(Item inventoryItem) {
        super();
        this.itemTypeID = inventoryItem.getItemTypeID();
        this.itemShortDescription = inventoryItem.getItemShortDescription();

    }

    public ItemTypeID getItemTypeID() {
        return itemTypeID;
    }

    public void setItemTypeID(ItemTypeID itemTypeID) {
        this.itemTypeID = itemTypeID;
    }


    public String getItemShortDescription() {
        return itemShortDescription;
    }

    public void setItemShortDescription(String itemShortDescription) {
        this.itemShortDescription = itemShortDescription;
    }


    public boolean isSameItemType(Item candidateInventoryItem) {
        return itemTypeID == candidateInventoryItem.getItemTypeID();
    }

    public TextureRegion getTextureRegion() {
        return mTextureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        mTextureRegion = textureRegion;
    }

}

