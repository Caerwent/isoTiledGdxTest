package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vte.libgdx.ortho.test.dialogs.GameDialog;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IDialogListener;
import com.vte.libgdx.ortho.test.interactions.InteractionEvent;

/**
 * Created by vincent on 06/01/2017.
 */

public class DialogTable extends Table implements IDialogListener {


    private Label mLabel;
    private ScrollPane mScrollPane;
    private GameDialog mDialog;
    private int mCurrentDialogIdx;


    public DialogTable(int aWidth, int aHeight) {
        setSize(aWidth, aHeight);
        mLabel = new Label("", UIStage.getInstance().getSkin(), "dialog-detail");
        mLabel.setWrap(true);
        mLabel.setSize(aWidth, aHeight);
        mLabel.setAlignment(Align.topLeft);
        mScrollPane = new ScrollPane(mLabel, UIStage.getInstance().getSkin(), "dialogPane");
        this.add(mScrollPane).width(aWidth).top();
       /* mScrollPane.setForceScroll(false, true);
        mScrollPane.setFlickScroll(false);
        mScrollPane.setOverscroll(false, true);*/
        mScrollPane.setScrollingDisabled(true, false);
        //mScrollPane.setFillParent(true);
        setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
        setSkin(UIStage.getInstance().getSkin());
        setName("Dialog");
        row();
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mDialog != null)
                {
                    displayNextDialog();
                }
            }
        });
        setTouchable(Touchable.enabled);
        EventDispatcher.getInstance().addDialogListener(this);


    }


    private void displayNextDialog()
    {
        if(mDialog!=null)
        {
            mCurrentDialogIdx++;
            if(mDialog.getDialogs().size > mCurrentDialogIdx)
            {
                mLabel.setText(mDialog.getDialogs().get(mCurrentDialogIdx));
                mScrollPane.layout();
            }
            else
            {
                InteractionEvent event = new InteractionEvent();
                event.type = InteractionEvent.EventType.DIALOG.name();
                event.value = mDialog.getId();
                EventDispatcher.getInstance().onInteractionEvent(event);
                onStopDialog(mDialog);
            }
        }
    }
    @Override
    public void onStartDialog(GameDialog aDialog) {
        mDialog = aDialog;
        mCurrentDialogIdx = -1;
        setVisible(true);
        displayNextDialog();
    }
    @Override
    public void onStopDialog(GameDialog aDialog)
    {
        if(mDialog!=null && aDialog!=null && mDialog.getId().equals(aDialog.getId()))
        {
            mDialog=null;
            mCurrentDialogIdx = -1;
            setVisible(false);
        }
    }

}
