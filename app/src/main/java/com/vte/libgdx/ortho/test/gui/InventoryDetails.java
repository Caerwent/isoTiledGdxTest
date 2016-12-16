package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.vte.libgdx.ortho.test.items.Item;

/**
 * Created by vincent on 16/12/2016.
 */

public class InventoryDetails extends Table {


    private Label mItemDescLabel;
    private ScrollPane mScrollPane;


    public InventoryDetails(int aWidth, int aHeight) {
        setSize(aWidth, aHeight);
        mItemDescLabel = new Label("", UIStage.getInstance().getSkin(), "inventory-detail");
        mItemDescLabel.setWrap(true);
        mItemDescLabel.setSize(aWidth, aHeight);
        mItemDescLabel.setAlignment(Align.topLeft);
        mScrollPane = new ScrollPane(mItemDescLabel, UIStage.getInstance().getSkin(), "inventoryPane");
        this.add(mScrollPane).width(aWidth).top().left().pad(5);
       /* mScrollPane.setForceScroll(false, true);
        mScrollPane.setFlickScroll(false);
        mScrollPane.setOverscroll(false, true);*/
        mScrollPane.setScrollingDisabled(true, false);
        //mScrollPane.setFillParent(true);
        setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
        setSkin(UIStage.getInstance().getSkin());
        setName("Inventory_Details");
        row();


    }

    public void setItem(Item aItem)
    {
        mItemDescLabel.setText(aItem.getItemShortDescription());
        //mItemDescLabel.setPrefRows(aItem.getItemShortDescription().split("\n").length);
        mScrollPane.layout();
    }

    }
