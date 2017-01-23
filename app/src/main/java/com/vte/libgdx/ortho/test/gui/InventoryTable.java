package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.player.Player;

/**
 * Created by vincent on 09/12/2016.
 */

public class InventoryTable extends Group implements Player.IPlayerListener{
    private final int mSlotWidth = 66;
    private final int mSlotHeight = 66;
    private int mLengthSlotRow = 2;
    private Table mInventoryTable;
    private ArrayMap<Item.ItemTypeID, InventorySlot> mSlots;
    private InventorySlot mSelectedItem;
    private InventoryDetails mDetails;

    public InventoryTable () {
        super();
        init();
    }


    private void init()
    {
        mSlots = new ArrayMap();
        mInventoryTable = new Table();
        mInventoryTable.setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        mInventoryTable.setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
        mInventoryTable.setSkin(UIStage.getInstance().getSkin());
        mInventoryTable.align(Align.topLeft);
        mInventoryTable.setName("Inventory_Slot_Table");
        mInventoryTable.setPosition(0,25);
        mInventoryTable.setSize(mLengthSlotRow*mSlotWidth+5, Settings.TARGET_HEIGHT - 50);
        Player.getInstance().registerListener(this);
        mDetails = new InventoryDetails(200, (Settings.TARGET_HEIGHT - 50)/2);

        addActor(mInventoryTable);
        addActor(mDetails);
        mDetails.setPosition(mLengthSlotRow*mSlotWidth+5, (Settings.TARGET_HEIGHT - 50)/2+25);
        mDetails.setVisible(false);

        update();

    }

    public void update()
    {
        int nbItemInRow=0;
        mInventoryTable.clear();
        mSlots.clear();
        for(Item item : Player.getInstance().getInventory())
        {
            InventorySlot inventorySlot = mSlots.get(item.getItemTypeID());
            if(inventorySlot==null)
            {
                inventorySlot = new InventorySlot();
                mSlots.put(item.getItemTypeID(), inventorySlot);
                inventorySlot.setTouchable(Touchable.enabled);
                mInventoryTable.add(inventorySlot).size(mSlotWidth, mSlotHeight);
                inventorySlot.addListener(new ClickListener() {
                    @Override
                    public void clicked (InputEvent event, float x, float y)
                    {
                        InventorySlot newSelectedItem = null;
                        if(event.getListenerActor() instanceof  InventorySlot)
                        {
                            newSelectedItem = (InventorySlot) event.getListenerActor();
                            if(mSelectedItem!=newSelectedItem)
                            {
                                if(mSelectedItem!=null)
                                    mSelectedItem.setSelected(false);
                                mSelectedItem = newSelectedItem;
                                mSelectedItem.setSelected(true);
                                mDetails.setItem(mSelectedItem.getItemOnTop());
                                mDetails.setVisible(true);
                            }
                            else if(mSelectedItem!=null && mSelectedItem==newSelectedItem)
                            {
                                mDetails.setVisible(false);
                                mSelectedItem.setSelected(false);
                                mSelectedItem=null;
                            }

                        }

                    }
                });
                nbItemInRow++;
                if(nbItemInRow % mLengthSlotRow == 0){
                    mInventoryTable.row();
                }


            }
            inventorySlot.add(item);


        }
    }

    @Override
    public void onInventoryChanged() {
        update();
    }
}
