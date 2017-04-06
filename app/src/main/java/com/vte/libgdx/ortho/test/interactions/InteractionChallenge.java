package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.gui.challenge.ChallengeUI;
import com.vte.libgdx.ortho.test.gui.UIStage;
import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 04/04/2017.
 */

public class InteractionChallenge extends Interaction {

    protected ChallengeUI mChallengeUI;

    protected String mChallengeType;


    public InteractionChallenge(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.CHALLENGE;

        mChallengeType = (String) aMapping.properties.get("type");
        mChallengeUI = ChallengeUI.createInstance(ChallengeUI.ChallengeType.valueOf(mChallengeType));
    }


    @Override
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        return (aEntity.mType & CollisionComponent.CHARACTER) != 0;
    }

    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {

    }

    @Override
    public void onStopCollisionInteraction(CollisionComponent aEntity) {
        if ((aEntity.mType & CollisionComponent.CHARACTER) != 0) {
            ChallengeUI currentChallengeUI = UIStage.getInstance().getChallengeUIOpened();
            if (currentChallengeUI != null && currentChallengeUI == mChallengeUI) {
                UIStage.getInstance().closeChallengeUI();
            }
        }
    }

    @Override
    protected boolean hasTouchInteraction(float x, float y) {

        return getShape().getBounds().contains(x, y);
    }

    @Override
    public void onTouchInteraction() {
        ChallengeUI currentChallengeUI = UIStage.getInstance().getChallengeUIOpened();
        if (currentChallengeUI != null) {
            UIStage.getInstance().closeChallengeUI();
            if (currentChallengeUI != mChallengeUI) {
                UIStage.getInstance().openChallengeUI(mChallengeUI);
            }
        } else {
            UIStage.getInstance().openChallengeUI(mChallengeUI);
        }

    }

    protected void closeChallengeUI() {
        ChallengeUI currentChallengeUI = UIStage.getInstance().getChallengeUIOpened();
        if (currentChallengeUI != null && currentChallengeUI != mChallengeUI) {
            UIStage.getInstance().closeChallengeUI();
        }
    }


    @Override
    public void destroy() {
        mChallengeUI.release();
        super.destroy();
    }
}
