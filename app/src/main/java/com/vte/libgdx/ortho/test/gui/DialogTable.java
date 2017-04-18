package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.dialogs.GameDialog;
import com.vte.libgdx.ortho.test.dialogs.GameDialogStep;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IDialogListener;
import com.vte.libgdx.ortho.test.interactions.InteractionEvent;
import com.vte.libgdx.ortho.test.quests.QuestManager;
import com.vte.libgdx.ortho.test.screens.GenericUI;

/**
 * Created by vincent on 06/01/2017.
 */

public class DialogTable extends Table implements IDialogListener {


    private Label mLabel, mSpeakerLabel;
    private ScrollPane mScrollPane;
    private GameDialog mDialog;
    private int mCurrentDialogIdx, mCurrentDialogPhraseIdx;
    private String mSpeaker;


    public DialogTable(int aWidth, int aHeight) {
        setSize(aWidth, aHeight);
        mSpeakerLabel=new Label("", GenericUI.getInstance().getSkin(), "dialog-speaker");
        mSpeakerLabel.setAlignment(Align.topLeft);
        this.add(mSpeakerLabel).top().left();
        this.row();
        mLabel = new Label("", GenericUI.getInstance().getSkin(), "dialog-detail");
        mLabel.setWrap(true);
        mLabel.setSize(aWidth, aHeight);
        mLabel.setAlignment(Align.topLeft);
        mScrollPane = new ScrollPane(mLabel, GenericUI.getInstance().getSkin(), "dialogPane");
        this.add(mScrollPane).pad(14,14,14,14).expandX().fill().top().left();
       /* mScrollPane.setForceScroll(false, true);
        mScrollPane.setFlickScroll(false);
        mScrollPane.setOverscroll(false, true);*/
        mScrollPane.setScrollingDisabled(true, false);
        //mScrollPane.setFillParent(true);
        setBackground(GenericUI.getInstance().getSkin().getDrawable("dialog"));
       // setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
        setSkin(GenericUI.getInstance().getSkin());
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
            if(mCurrentDialogIdx < mDialog.getDialogs().size)
            {
                GameDialogStep dialogStep = mDialog.getDialogs().get(mCurrentDialogIdx);

                if(dialogStep.speaker!=null && !dialogStep.speaker.isEmpty())
                {
                    mSpeaker = AssetsUtility.getString(dialogStep.speaker);
                }
                else
                {
                    mSpeaker=null;
                }
                if(mCurrentDialogPhraseIdx <dialogStep.phrases.size())
                {
                    mLabel.setText(AssetsUtility.getString(dialogStep.phrases.get(mCurrentDialogPhraseIdx)));
                    mSpeakerLabel.setVisible(mSpeaker!=null);
                    mSpeakerLabel.setText(mSpeaker!=null ? mSpeaker:"");
                    mCurrentDialogPhraseIdx++;
                }
                else
                {
                    mCurrentDialogIdx++;
                    mCurrentDialogPhraseIdx=0;
                    mSpeaker=null;
                    displayNextDialog();
                    return;
                }
                mScrollPane.layout();
            }
            else
            {
                QuestManager.getInstance().onDialogEnd(mDialog);
                InteractionEvent event = new InteractionEvent(null, InteractionEvent.EventType.DIALOG.name(), mDialog.getId());
                EventDispatcher.getInstance().onInteractionEvent(event);
                onStopDialog(mDialog);
            }
        }
    }
    @Override
    public void onStartDialog(GameDialog aDialog) {
        mDialog = aDialog;
        mCurrentDialogIdx = 0;
        mCurrentDialogPhraseIdx =0;
        mSpeaker=null;
        setVisible(true);
        displayNextDialog();
    }
    @Override
    public void onStopDialog(GameDialog aDialog)
    {
        if(mDialog!=null && aDialog!=null && mDialog.getId().equals(aDialog.getId()))
        {
            mDialog=null;
            mCurrentDialogIdx = 0;
            mCurrentDialogPhraseIdx =0;
            mSpeaker=null;
            setVisible(false);
        }
    }

}
