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
import com.vte.libgdx.ortho.test.screens.GenericUI;

import java.util.ArrayList;

/**
 * Created by vincent on 09/12/2016.
 */

public class InventoryTable extends Table implements IPlayerListener {
    private final int mSlotWidth = 68;
    private final int mSlotHeight = 68;
    private int mLengthSlotRow = 2;
    private Table mInventoryTable;
    private ArrayMap<Item.ItemTypeID, InventorySlot> mSlots;
    private InventorySlot mSelectedItem;
    private InventoryDetails mDetails;
    ArrayList<InventorySlotTarget> mSlotTargets = new ArrayList<>();
    ArrayList<InventorySlotSource> mSlotSources = new ArrayList();

    private DragAndDrop _dragAndDrop;

    public InventoryTable() {
        super();
        init();
    }


    private void init() {
        //setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        // setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));

        mSlots = new ArrayMap();
        _dragAndDrop = new DragAndDrop();
        mInventoryTable = new Table();
        //      mInventoryTable.setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        //     mInventoryTable.setColor(UIStage.getInstance().getSkin().getColor("lt-green"));

        mInventoryTable.setSkin(GenericUI.getInstance().getSkin());
        //  mInventoryTable.align(Align.topLeft);
        mInventoryTable.setName("Inventory_Slot_Table");
        //  mInventoryTable.setPosition(0,25);
        //  mInventoryTable.setSize(mLengthSlotRow*mSlotWidth+5, Settings.TARGET_HEIGHT - 64);
        EventDispatcher.getInstance().addPlayerListener(this);
        mDetails = new InventoryDetails(200, (Settings.TARGET_HEIGHT - 64) / 2);
        add(mInventoryTable).fillY().expand().left();
        add(mDetails).top().left();
        row();
        // mDetails.setPosition(mLengthSlotRow*mSlotWidth+5, (Settings.TARGET_HEIGHT - 64)/2+25);
        mDetails.setVisible(false);


    }

    public DragAndDrop getDragAndDrop()
    {
        return _dragAndDrop;
    }

    public void destroy() {
        EventDispatcher.getInstance().removePlayerListener(this);
    }

    public void update(Player aPlayer) {
        int nbItemInRow = 0;
        mInventoryTable.clear();
        for(InventorySlotTarget slotTarget : mSlotTargets)
        {
            _dragAndDrop.removeTarget(slotTarget);
        }
        mSlotTargets.clear();
        for(InventorySlotSource slotSource : mSlotSources)
        {
            _dragAndDrop.removeSource(slotSource);
        }
        mSlotSources.clear();
        mSlots.clear();

        for (Item item : aPlayer.getInventory()) {
            InventorySlot inventorySlot = mSlots.get(item.getItemTypeID());
            if (inventorySlot == null) {
                inventorySlot = new InventorySlot();

                InventorySlotTarget target = new InventorySlotTarget(inventorySlot);
                mSlotTargets.add(target);
                _dragAndDrop.addTarget(target);
                InventorySlotSource source = new InventorySlotSource(inventorySlot, _dragAndDrop);
                mSlotSources.add(source);
                _dragAndDrop.addSource(source);

                mSlots.put(item.getItemTypeID(), inventorySlot);
                inventorySlot.setTouchable(Touchable.enabled);
                mInventoryTable.top().add(inventorySlot).size(mSlotWidth, mSlotHeight);
                inventorySlot.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        InventorySlot newSelectedItem = null;
                        if (event.getListenerActor() instanceof InventorySlot) {
                            newSelectedItem = (InventorySlot) event.getListenerActor();
                            if (mSelectedItem != newSelectedItem) {
                                if (mSelectedItem != null)
                                    mSelectedItem.setSelected(false);
                                mSelectedItem = newSelectedItem;
                                mSelectedItem.setSelected(true);
                                mDetails.setText(mSelectedItem.getItemOnTop().getItemShortDescription());
                                mDetails.setVisible(true);
                            } else if (mSelectedItem != null && mSelectedItem == newSelectedItem) {
                                mDetails.setVisible(false);
                                mSelectedItem.setSelected(false);
                                mSelectedItem = null;
                            }

                        }

                    }
                });
                nbItemInRow++;
                if (nbItemInRow % mLengthSlotRow == 0) {
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
