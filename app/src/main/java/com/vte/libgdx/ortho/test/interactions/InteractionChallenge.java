package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.gui.UIStage;
import com.vte.libgdx.ortho.test.gui.challenge.ChallengeUI;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 04/04/2017.
 */

public class InteractionChallenge extends Interaction {

    protected ChallengeUI mChallengeUI;

    protected String mChallengeType;
    private static final String KEY_STATE = "state";

    public InteractionChallenge(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.CHALLENGE;

    }

    @Override
    public void initialize(float x, float y, InteractionMapping aMapping) {
        super.initialize(x, y, aMapping);
        mChallengeType = (String) mProperties.get("type");
        mChallengeUI = ChallengeUI.createInstance(ChallengeUI.ChallengeType.valueOf(mChallengeType));
        mChallengeUI.setInteractionChallenge(this);

    }

    @Override
    public void restoreFromPersistence(GameSession aGameSession) {
        String state = (String) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_STATE);
        if (state != null) {
            mCurrentState = getState(state);
        }
        if (mChallengeUI != null) {
            mChallengeUI.restoreFromPersistence(aGameSession);
        }

    }

    @Override
    public GameSession saveInPersistence(GameSession aGameSession) {
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_STATE, mCurrentState.name);
        if (mChallengeUI != null) {
            aGameSession = mChallengeUI.saveInPersistence(aGameSession);
        }
        return aGameSession;
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
