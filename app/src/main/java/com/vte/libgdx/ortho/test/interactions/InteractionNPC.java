package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.dialogs.DialogsManager;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.NPCProfile;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.quests.QuestManager;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionNPC extends Interaction{
    private boolean mIsInteractionShown = false;
    private RectangleShape mMarkShape;
    private TextureRegion mInteractionTextureRegion;

    public String mDialogId;

    protected String mQuestId;

    public InteractionNPC(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.NPC;
        mInteractionTextureRegion = AssetsUtility.ITEMS_TEXTUREATLAS.findRegion("bulle");

        mMarkShape = new RectangleShape();
        updateInteractionMarkShape();
        if(aMapping.properties!=null)
        {
            mDialogId = (String) aMapping.properties.get("dialogId");
        }
        NPCProfile profile = Profile.getInstance().getNPCProfile(getId());
        if (profile != null && profile.dialogId != null) {
            mDialogId = profile.dialogId;
        }

    }
    public void updateInteractionMarkShape() {
        if (mMarkShape == null)
            return;

        TransformComponent transform = this.getComponent(TransformComponent.class);
        float width_frame = mCurrentFrame.getRegionWidth();
        float height_frame = mCurrentFrame.getRegionHeight();

        float width = mInteractionTextureRegion.getRegionWidth();
        float height = mInteractionTextureRegion.getRegionHeight();

        //Allow for Offset
        float originX = (width_frame - width) / 2 * transform.scale;//transform.originOffset.x;
        float originY = height_frame * transform.scale;//transform.originOffset.y;

        mMarkShape.getShape().set(0, 0, width, height);
        mMarkShape.setX(transform.position.x + transform.originOffset.x + originX);
        mMarkShape.setY(transform.position.y + transform.originOffset.y + originY);

    }
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateInteractionMarkShape();

    }
    @Override
    public void setPosition(Vector2 pos) {
        super.setPosition(pos);
        updateInteractionMarkShape();
    }
    @Override
    public void render(Batch batch) {
        super.render(batch);
        TransformComponent transform = this.getComponent(TransformComponent.class);

        if (mIsInteractionShown) {
            float width_frame = mCurrentFrame.getRegionWidth();
            float height_frame = mCurrentFrame.getRegionHeight();

            float width = mInteractionTextureRegion.getRegionWidth();
            float height = mInteractionTextureRegion.getRegionHeight();

            //Allow for Offset
            float originX = (width_frame - width) / 2 * transform.scale;//transform.originOffset.x;
            float originY = height_frame * transform.scale;//transform.originOffset.y;

            batch.draw(mInteractionTextureRegion,
                    transform.position.x + transform.originOffset.x, transform.position.y + transform.originOffset.y,
                    originX, originY,
                    width, height,
                    transform.scale, transform.scale,
                    transform.angle);
        }
    }
    @Override
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType&CollisionComponent.CHARACTER)!=0;
    }

    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {
        mIsInteractionShown = true;
    }
    @Override
    public void onStopCollisionInteraction(CollisionComponent aEntity) {
        mIsInteractionShown = false;
        if(getDialogId()!=null) {
            EventDispatcher.getInstance().onStopDialog(DialogsManager.getInstance().getDialog(getDialogId()));
        }
    }
    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return mIsInteractionShown && (mMarkShape.getBounds().contains(x, y) || getShape().getBounds().contains(x, y));
    }
    @Override
    public void onTouchInteraction() {
        QuestManager.getInstance().onNPC(this);
        if(getDialogId()!=null) {
            EventDispatcher.getInstance().onStartDialog(DialogsManager.getInstance().getDialog(getDialogId()));
        }
    }


    public String getDialogId() {
        return mDialogId;
    }

    public void setDialogId(String aDialogId) {
        mDialogId = aDialogId;
        NPCProfile profile = Profile.getInstance().getNPCProfile(getId());
        if (profile == null) {
            profile = new NPCProfile();
        }

        profile.dialogId = mDialogId;
        Profile.getInstance().updateNPCProfile(getId(), profile);


    }

    public String getQuestId() {
        return mQuestId;
    }

    public void setQuestId(String aQuestId) {
        mQuestId = aQuestId;
    }


}
