package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * Created by gwalarn on 16/11/16.
 */

public class TestActor extends Group  {

    private static final int DIALOG_W = TARGET_WIDTH/4*3;
    private static final int DIALOG_H = 100;

    protected final Table mainTable = new Table();
    private InventoryTable mInventorySlotTable = new InventoryTable();
    protected final DialogTable mDialogTable = new DialogTable(DIALOG_W, DIALOG_H);

    /**
     * the duration of the screen transition for the screenOut method
     */
    public float dur;


    public TestActor() {

        mainTable.setSize(50, 50);


        this.addActor(mainTable);
        mainTable.setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        mainTable.setColor(UIStage.getInstance().getSkin().getColor("dark-blue"));
        mainTable.setSkin(UIStage.getInstance().getSkin());
        mainTable.add("I");
        mainTable.row();

        mInventorySlotTable.setPosition(50, 25);
        addActor(mInventorySlotTable);
        mInventorySlotTable.setVisible(false);
      //  mInventorySlotTable.setTouchable(Touchable.enabled);

      /*  secondTable.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });*/

        mDialogTable.setPosition((TARGET_WIDTH-DIALOG_W)/2, 0);
        mDialogTable.setVisible(false);
        addActor(mDialogTable);
        mainTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // app.switchScreens(new MainScreen(app));
                Gdx.app.log("!!", "klick");
                if (mInventorySlotTable.isVisible()) {
                    mInventorySlotTable.setVisible(false);
                } else {
                    mInventorySlotTable.setVisible(true);
                }
            }
        });
        mainTable.setTouchable(Touchable.enabled);

    }



}
