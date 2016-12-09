package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by gwalarn on 16/11/16.
 */

public class TestActor extends Group  {

    protected final Table mainTable = new Table();
    private Table mInventorySlotTable = new InventoryTable();


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

      //  mInventorySlotTable.setTouchable(Touchable.enabled);

      /*  secondTable.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });*/

        mainTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // app.switchScreens(new MainScreen(app));
                Gdx.app.log("!!", "klick");
                if (getChildren().contains(mInventorySlotTable, true)) {
                    removeActor(mInventorySlotTable);
                } else {
                    addActor(mInventorySlotTable);
                }
            }
        });
        mainTable.setTouchable(Touchable.enabled);

    }



}
