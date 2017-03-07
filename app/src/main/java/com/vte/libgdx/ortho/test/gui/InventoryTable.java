package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.ArrayMap;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IPlayerListener;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.player.Player;

/**
 * Created by vincent on 09/12/2016.
 */

public class InventoryTable extends Table implements IPlayerListener {
    private final int mSlotWidth = 66;
    private final int mSlotHeight = 66;
    private int mLengthSlotRow = 2;
    private Table mInventoryTable;
    private ArrayMap<Item.ItemTypeID, InventorySlot> mSlots;
    private InventorySlot mSelectedItem;
    private InventoryDetails mDetails;

    private DragAndDrop _dragAndDrop;

    public InventoryTable () {
        super();
        init();
    }


    private void init()
    {
        mSlots = new ArrayMap();
        _dragAndDrop = new DragAndDrop();
        mInventoryTable = new Table();
  //      mInventoryTable.setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
  //     mInventoryTable.setColor(UIStage.getInstance().getSkin().getColor("lt-green"));

        mInventoryTable.setSkin(UIStage.getInstance().getSkin());
      //  mInventoryTable.align(Align.topLeft);
        mInventoryTable.setName("Inventory_Slot_Table");
      //  mInventoryTable.setPosition(0,25);
      //  mInventoryTable.setSize(mLengthSlotRow*mSlotWidth+5, Settings.TARGET_HEIGHT - 64);
        EventDispatcher.getInstance().addPlayerListener(this);
        mDetails = new InventoryDetails(200, (Settings.TARGET_HEIGHT - 64)/2);
        add(mInventoryTable).fillY().expand().left();
        add(mDetails).top();
        row();
       // mDetails.setPosition(mLengthSlotRow*mSlotWidth+5, (Settings.TARGET_HEIGHT - 64)/2+25);
        mDetails.setVisible(false);


    }

    public void destroy()
    {
        EventDispatcher.getInstance().removePlayerListener(this);
    }

    public void update(Player aPlayer)
    {
        int nbItemInRow=0;
        mInventoryTable.clear();
        mSlots.clear();
        _dragAndDrop.clear();
        for(Item item : aPlayer.getInventory())
        {
            InventorySlot inventorySlot = mSlots.get(item.getItemTypeID());
            if(inventorySlot==null)
            {
                inventorySlot = new InventorySlot();
                _dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
                _dragAndDrop.addSource(new InventorySlotSource(inventorySlot, _dragAndDrop));

                mSlots.put(item.getItemTypeID(), inventorySlot);
                inventorySlot.setTouchable(Touchable.enabled);
                mInventoryTable.top().add(inventorySlot).size(mSlotWidth, mSlotHeight);
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
                                mDetails.setText(mSelectedItem.getItemOnTop().getItemShortDescription());
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
    public void onInventoryChanged(Player aPlayer) {
        update(aPlayer);
    }
}
