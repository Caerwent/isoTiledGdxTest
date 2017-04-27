package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.audio.AudioEvent;
import com.vte.libgdx.ortho.test.audio.AudioManager;
import com.vte.libgdx.ortho.test.box2d.CircleShape;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.CollisionEffectComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionInteractionComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionObstacleComponent;
import com.vte.libgdx.ortho.test.entity.components.ICollisionInteractionHandler;
import com.vte.libgdx.ortho.test.entity.components.ICollisionObstacleHandler;
import com.vte.libgdx.ortho.test.entity.components.InputComponent;
import com.vte.libgdx.ortho.test.entity.components.InteractionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.events.IInteractionEventListener;
import com.vte.libgdx.ortho.test.events.IQuestListener;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;
import com.vte.libgdx.ortho.test.persistence.Profile;
import com.vte.libgdx.ortho.test.quests.Quest;
import com.vte.libgdx.ortho.test.quests.QuestManager;
import com.vte.libgdx.ortho.test.quests.QuestTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 08/02/2017.
 */

public class Interaction extends Entity implements ICollisionObstacleHandler, ICollisionInteractionHandler, IInteraction, InputProcessor, IInteractionEventListener, IQuestListener {

    protected String mId;
    protected Type mType;
    protected InteractionDef mDef;
    protected boolean mIsMovable;

    protected float mStateTime; // elapsed time
    protected boolean mIsRended = false;
    protected TextureAtlas.AtlasRegion mCurrentFrame; // current animation frame

    protected InteractionState mCurrentState;

    protected Array<CollisionObstacleComponent> mCollisionsObstacle = new Array<CollisionObstacleComponent>();
    protected Array<CollisionInteractionComponent> mCollisionsInteraction = new Array<CollisionInteractionComponent>();
    protected Array<CollisionEffectComponent> mCollisionsEffect = new Array<CollisionEffectComponent>();

    protected Shape mShapeCollision;
    protected int mCollisionHeightFactor = 8;
    protected Shape mShapeInteraction;
    protected Shape mShapeRendering;
    protected float[] mVertices = new float[8];


    protected Camera mCamera;

    public ArrayList<InteractionEventAction> mEventsAction;
    public ArrayList<InteractionEvent> mOutputEvents;

    public ArrayList<InteractionQuestAction> mQuestsActions;

    protected MapProperties mMapProperties;
    protected HashMap mProperties;
    protected GameMap mMap;

    protected Effect mEffectLaunched;
    protected float mEffectLaunchedTime;
    protected CircleShape mZoneLaunchedEffect;

    protected Effect mEffectAction;
    protected float mEffectActionTime;

    @Override
    public void setCamera(Camera aCamera) {
        mCamera = aCamera;
    }

    public Interaction(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        mId = aMapping.id;
        mDef = aDef;
        mEventsAction = aDef.eventsAction;
        if (mEventsAction == null) {
            mEventsAction = new ArrayList<>();
        }
        for (InteractionEventAction action : mEventsAction) {
            if (action.inputEvents != null) {
                for (InteractionEvent event : action.inputEvents) {
                    if (event.sourceId != null && event.sourceId.compareTo(InteractionEvent.THIS) == 0) {
                        event.sourceId = mId;
                    }
                }
            }
        }
        mOutputEvents = aDef.outputEvents;
        if (mOutputEvents == null) {
            mOutputEvents = new ArrayList<>();
        }
        mProperties = aDef.properties;
        if (mProperties == null) {
            mProperties = new HashMap<>();
        }
        if (aMapping.properties != null) {
            mProperties.putAll(aMapping.properties);
        }
        if (aMapping.eventsAction != null) {
            mEventsAction.addAll(aMapping.eventsAction);
        }
        if (aMapping.outputEvents != null) {
            mOutputEvents.addAll(aMapping.outputEvents);
        }
        mQuestsActions = aMapping.questActions;
        mMap = aMap;
        mMapProperties = aProperties;

        EntityEngine.getInstance().addEntity(this);


        this.add(new TransformComponent());

        //    mType = IInteraction.Type.valueOf(mDef.type);
        if (mOutputEvents != null) {
            for (InteractionEvent event : mOutputEvents) {
                event.sourceId = mId;
            }
        }
        mShapeCollision = createShapeCollision();
        mShapeInteraction = createShapeInteraction();
        mShapeRendering = createShapeRendering();
        initialize(x, y, aMapping);


        if (mEventsAction != null) {
            EventDispatcher.getInstance().addInteractionEventListener(this);
        }
        if (mQuestsActions != null) {
            EventDispatcher.getInstance().addQuestListener(this);
        }
        add(new InteractionComponent(this));


    }

    public GameMap getMap() {
        return mMap;
    }

    public void restoreFromPersistence(GameSession aGameSession) {

    }

    public GameSession saveInPersistence(GameSession aGameSession) {
        return aGameSession;
    }

    public void restoreFromPersistence() {
        if (getPersistence() == Persistence.GAME) {
            restoreFromPersistence(Profile.getInstance().getPersistentGameSession());
        } else if (getPersistence() == Persistence.SESSION) {
            restoreFromPersistence(GameSession.getInstance());

        }
    }

    public void saveInPersistence() {
        if (getPersistence() == Persistence.GAME) {
            Profile.getInstance().updatePersistentGameSession(saveInPersistence(Profile.getInstance().getPersistentGameSession()));
        } else if (getPersistence() == Persistence.SESSION) {
            saveInPersistence(GameSession.getInstance());

        }
    }


    @Override
    public float getX() {
        return getPosition().x;
    }

    @Override
    public float getY() {
        return getPosition().y;
    }

    @Override
    public Type getType() {
        return mType;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public boolean isClickable() {
        return mDef.isClickable;
    }

    @Override
    public boolean isMovable() {
        return mIsMovable;
    }

    public void setMovable(boolean isMovable) {
        mIsMovable = isMovable;
        if (mIsMovable) {
            this.add(new VelocityComponent());
        } else {
            remove(VelocityComponent.class);
        }
    }

    @Override
    public Persistence getPersistence() {
        return mDef.persistence;
    }


    protected InteractionState getState(String aStateName) {
        if (aStateName == null)
            return null;
        for (InteractionState state : mDef.states) {
            if (state.name.compareTo(aStateName) == 0) {
                return state;
            }
        }
        return null;
    }

    protected void setState(String aStateName) {
        InteractionState state = getState(aStateName);
        if (state != null && state != mCurrentState) {
            String oldState = mCurrentState.name;
            mCurrentState = state;
            mStateTime = 0;
            if (mOutputEvents != null) {
                for (InteractionEvent event : mOutputEvents) {
                   /* if (InteractionEvent.EventType.END_STATE == InteractionEvent.EventType.valueOf(event.type) &&
                            oldState.equals(event.value)) {
                        EventDispatcher.getInstance().onInteractionEvent(event);
                    }*/
                    if (InteractionEvent.EventType.STATE == InteractionEvent.EventType.valueOf(event.type) &&
                            mCurrentState.name.equals(event.value)) {
                        EventDispatcher.getInstance().onInteractionEvent(event);
                    }
                }
            }
            if (mCurrentState.name.compareTo(InteractionState.STATE_FROZEN) == 0) {
                setMovable(false);
            }
        }
    }

    public void destroy() {
        EventDispatcher.getInstance().removeInteractionEventListener(this);
        saveInPersistence();

        EntityEngine.getInstance().removeEntity(this);
    }

    public void initialize(float x, float y, InteractionMapping aMapping) {


        setMovable(mDef.isMovable);

        mCurrentState = getState(mDef.defaultState);

        if (mDef.isRendable) {

            String[] files = ("data/interactions/" + mDef.atlas).split("/");
            ArrayList<String> path = new ArrayList();
            for (int i = 0; i < files.length; i++) {
                if (files[i].compareTo("..") == 0 && path.size() > 0) {
                    path.remove(path.size() - 1);
                } else {
                    path.add(files[i]);
                }
            }
            String filename = "";
            String sep = "";
            while (path.size() > 0) {
                filename += sep + path.remove(0);
                sep = "/";
            }

            AssetsUtility.loadTextureAtlasAsset(filename);

            for (InteractionState state : mDef.states) {
                state.init(AssetsUtility.getTextureAtlasAsset(filename));
            }
            mCurrentFrame = mCurrentState.getTextureRegion(0F);
            if (mCurrentFrame != null) {
                TransformComponent transform = this.getComponent(TransformComponent.class);
                transform.scale = MyGame.SCALE_FACTOR;

                transform.setOriginOffset(0, 0);
                this.add(new VisualComponent(mCurrentFrame, this));
            }

        }
        setPosition(x, y);

        if (mQuestsActions != null) {
            for (InteractionQuestAction action : mQuestsActions) {
                onQuestEvent(QuestManager.getInstance().getQuestFromId(action.questId));
            }
        }
        restoreFromPersistence();

        updateRendering(0);
        updateCollision(0);
        if (isRendable()) {
            this.add(new CollisionObstacleComponent(CollisionObstacleComponent.MAPINTERACTION, getShapeCollision(), mId, this, this));
        }
        updateInteraction(0);
        this.add(new CollisionInteractionComponent(getShapeInteraction(), this, this));


    }

    public void setPosition(float x, float y) {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        transformComponent.position.x = x;
        transformComponent.position.y = y;

    }

    public void setPosition(Vector2 pos) {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        transformComponent.position.x = pos.x;
        transformComponent.position.y = pos.y;

    }

    public Vector2 getPosition() {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        return new Vector2(transformComponent.position.x, transformComponent.position.y);
    }

    public void setVelocity(float vx, float vy) {
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        if (velocity != null) {
            velocity.x = vx;
            velocity.y = vy;
        }
    }

    public void setVelocity(Vector2 v) {
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        if (velocity != null) {
            velocity.x = v.x;
            velocity.y = v.y;
        }
    }

    @Override
    public void update(float dt) {
        mStateTime += dt;
        if (mCurrentState.isCompleted(mStateTime)) {

            if (mOutputEvents != null) {
                for (InteractionEvent event : mOutputEvents) {
                    if (InteractionEvent.EventType.END_STATE == InteractionEvent.EventType.valueOf(event.type) &&
                            mCurrentState.name.equals(event.value)) {
                        EventDispatcher.getInstance().onInteractionEvent(event);
                    }
                }
            }
        }
        updateRendering(dt);
        updateCollision(dt);
        updateInteraction(dt);
        updateEffects(dt);


    }

    /*************************************** RENDERING ************************************/
    @Override
    public boolean isRendable() {
        return mDef.isRendable;
    }

    @Override
    public boolean isRended() {
        return mIsRended;
    }

    @Override
    public int getZIndex() {
        return 1;
    }

    @Override
    public void setRended(boolean aRended) {
        mIsRended = aRended;
    }

    public Shape createShapeRendering() {
        return new PolygonShape();
    }

    public void updateRendering(float dt) {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        if (isRendable()) {
            float width = mCurrentFrame.getRegionWidth() * tfm.scale;
            float height = mCurrentFrame.getRegionHeight() * tfm.scale;

            tfm.setOriginOffset(mCurrentFrame.offsetX * tfm.scale, mCurrentFrame.offsetY * tfm.scale);

            mVertices[0] = tfm.originOffset.x;
            mVertices[1] = tfm.originOffset.y;
            mVertices[2] = tfm.originOffset.x;
            mVertices[3] = height + tfm.originOffset.y;
            mVertices[4] = width + tfm.originOffset.x;
            mVertices[5] = height + tfm.originOffset.y;
            mVertices[6] = width + tfm.originOffset.x;
            mVertices[7] = tfm.originOffset.y;


            if (mShapeRendering.getType() == Shape.Type.POLYGON) {
                ((PolygonShape) mShapeRendering).getShape().setVertices(mVertices);
            }

        }
        mShapeRendering.setX(tfm.position.x);
        mShapeRendering.setY(tfm.position.y);
    }

    @Override
    public Shape getShapeRendering() {

        return mShapeRendering;
    }

    @Override
    public void render(Batch batch) {
        TransformComponent transform = this.getComponent(TransformComponent.class);
        VisualComponent visual = this.getComponent(VisualComponent.class);
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);

        if (velocity != null) {
            if (velocity.y == 0 && velocity.x == 0) {
                setState(mDef.defaultState);
                mStateTime = 0;
            } else {
                if (velocity.y == 0) {
                    setState(velocity.x < 0 ? InteractionState.STATE_MOVE_LEFT : InteractionState.STATE_MOVE_RIGHT);
                } else {
                    double angle = Math.atan2(velocity.y, velocity.x);
                    double PI4 = Math.PI / 4;
                    if (angle < 0) {
                        angle += Math.PI * 2;
                    }

                    if (angle > 7 * PI4 || angle <= PI4) {
                        setState(InteractionState.STATE_MOVE_RIGHT);
                    } else if (angle > PI4 && angle <= 3 * PI4) {
                        setState(InteractionState.STATE_MOVE_UP);
                    } else if (angle > 3 * PI4 && angle <= 5 * PI4) {
                        setState(InteractionState.STATE_MOVE_LEFT);
                    } else if (angle > 5 * PI4 && angle <= 7 * PI4) {
                        setState(InteractionState.STATE_MOVE_DOWN);
                    }
                }
            }
        }
        mCurrentFrame = mCurrentState.getTextureRegion(mStateTime);

        visual.region = mCurrentFrame;
        float width = visual.region.getRegionWidth();
        float height = visual.region.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;

        batch.draw(visual.region,
                transform.position.x + transform.originOffset.x, transform.position.y + transform.originOffset.y,
                originX, originY,
                width, height,
                transform.scale, transform.scale,
                transform.angle);

        renderEffect(batch);
    }


    /*************************************** PHYSICAL COLLISION ************************************/

    public void updateCollision(float dt) {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        mShapeCollision.setX(tfm.position.x);
        mShapeCollision.setY(tfm.position.y);
        if (isRendable()) {
            if (mShapeCollision instanceof RectangleShape) {
                RectangleShape shape = (RectangleShape) mShapeCollision;
                shape.getShape().setWidth(mShapeRendering.getWidth());
                shape.getShape().setHeight(mShapeRendering.getHeight() / mCollisionHeightFactor);

            } else if (mShapeCollision instanceof CircleShape) {
                CircleShape shape = (CircleShape) mShapeCollision;
                shape.getShape().setRadius(mShapeRendering.getHeight() / mCollisionHeightFactor);
            }
        }
    }

    public Shape createShapeCollision() {
        RectangleShape shape = new RectangleShape();
        shape.setShape(new Rectangle(0, 0, 1, 1));
        return shape;
    }

    public Shape getShapeCollision() {

        return mShapeCollision;
    }

    @Override
    public boolean onCollisionObstacleStart(CollisionObstacleComponent aEntity) {

        if (!mCollisionsObstacle.contains(aEntity, false)) {
            mCollisionsObstacle.add(aEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCollisionObstacleStop(CollisionObstacleComponent aEntity) {
        if (mCollisionsObstacle.contains(aEntity, false)) {
            mCollisionsObstacle.removeValue(aEntity, false);
            return true;
        }
        return false;
    }

    @Override
    public Array<CollisionObstacleComponent> getCollisionObstacle() {
        return mCollisionsObstacle;
    }

    /*************************************** INTERACTIONS  ************************************/

    public void updateInteraction(float dt) {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        if (isRendable()) {
            RectangleShape interactionArea = (RectangleShape) mShapeInteraction;
            interactionArea.getShape().set(mShapeRendering.getX() - 0.1F, mShapeRendering.getY() - 0.1F, mShapeRendering.getWidth() + 0.2f, mShapeRendering.getHeight() + 0.2f);

        } else if(getComponent(CollisionObstacleComponent.class)!=null){
            mShapeInteraction = mShapeCollision;
        }
        else
        {
            mShapeInteraction.setX(tfm.position.x);
            mShapeInteraction.setY(tfm.position.y);
        }

    }

    public Shape createShapeInteraction() {
        return new RectangleShape();
    }

    @Override
    public Shape getShapeInteraction() {
        return mShapeInteraction;
    }

    public boolean hasCollisionInteraction(CollisionInteractionComponent aEntity) {
        return false;
    }

    public void onStartCollisionInteraction(CollisionInteractionComponent aEntity) {

    }

    public void onStopCollisionInteraction(CollisionInteractionComponent aEntity) {

    }

    @Override
    public boolean onCollisionInteractionStart(CollisionInteractionComponent aEntity) {
        if (!mCollisionsInteraction.contains(aEntity, false)) {

            boolean ret = hasCollisionInteraction(aEntity);
            if (ret) {
                mCollisionsInteraction.add(aEntity);
                if (isClickable()) {
                    add(new InputComponent(this));
                }

                onStartCollisionInteraction(aEntity);
            }
            return ret;
        }
        return false;

    }

    @Override
    public boolean onCollisionInteractionStop(CollisionInteractionComponent aEntity) {
        if (mCollisionsInteraction.contains(aEntity, false)) {
            mCollisionsInteraction.removeValue(aEntity, false);

            if (isClickable()) {
                remove(InputComponent.class);
            }
            onStopCollisionInteraction(aEntity);
            return true;
        }
        return false;
    }

    @Override
    public Array<CollisionInteractionComponent> getCollisionInteraction() {
        return mCollisionsInteraction;
    }

    /*************************************** EVENTS ************************************/
    @Override
    public void onInteractionEvent(InteractionEvent aEvent) {
        if (mEventsAction != null && aEvent != null) {
            for (InteractionEventAction action : mEventsAction) {
                if (action.inputEvents != null) {
                    boolean performed = false;
                    for (InteractionEvent expectedEvent : action.inputEvents) {
                        if ((expectedEvent.sourceId == null || expectedEvent.sourceId.isEmpty() || expectedEvent.sourceId.equals(aEvent.sourceId)) && expectedEvent.type.equals(aEvent.type)) {
                            expectedEvent.setPerformed(expectedEvent.value.equals(aEvent.value));
                            performed = expectedEvent.isPerformed();
                            break;
                        }
                    }
                    if (performed) {
                        boolean allPerformed = true;
                        for (InteractionEvent expectedEvent : action.inputEvents) {
                            if (!expectedEvent.isPerformed()) {
                                allPerformed = false;
                                break;
                            }
                        }
                        if (allPerformed) {
                            doActionOnEvent(action);
                        }
                    }
                }
            }
        }
    }

    /**
     * check if an action should be done
     *
     * @param aAction the action to be checked
     * @return true if action has be done, false in other cases
     */
    protected boolean doActionOnEvent(InteractionEventAction aAction) {
        if (aAction != null && InteractionEventAction.ActionType.SET_STATE.name().equals(aAction.id)) {
            if (getState(aAction.value) != null) {
                setState(aAction.value);
                return true;
            }
        }
        return false;
    }

    protected void doQuestAction(InteractionQuestAction aAction) {
    }

    protected void onQuestEvent(Quest aQuest) {
        if (mQuestsActions != null && aQuest != null) {
            Gdx.app.debug("DEBUG", "onQuestEvent questID=" + aQuest.getId() + " isActivated=" + aQuest.isActivated() + " isCompleted=" + aQuest.isCompleted());
            for (InteractionQuestAction action : mQuestsActions) {
                if ((aQuest.getId() != null && action.questId != null && aQuest.getId().equals(action.questId))) {
                    boolean doAction = false;
                    if (action.questState == InteractionQuestAction.QuestState.FINISHED && aQuest.isCompleted() && QuestManager.getInstance().getLivingQuestFromId(aQuest.getId()) == null) {
                        doAction = true;
                    } else if (action.questState == InteractionQuestAction.QuestState.COMPLETED && aQuest.isCompleted() && QuestManager.getInstance().getLivingQuestFromId(aQuest.getId()) != null) {
                        doQuestAction(action);
                        return;
                    } else if (action.questState == InteractionQuestAction.QuestState.NOT_COMPLETED &&
                            QuestManager.getInstance().getLivingQuestFromId(aQuest.getId()) != null &&
                            aQuest.isActivated() &&
                            !aQuest.isCompleted()) {
                        doQuestAction(action);
                        return;
                    } else if (action.questState == InteractionQuestAction.QuestState.NOT_ACTIVATED &&
                            QuestManager.getInstance().getLivingQuestFromId(aQuest.getId()) != null &&
                            !aQuest.isActivated()) {
                        doQuestAction(action);
                        return;
                    }

                    if (doAction) {
                        doQuestAction(action);
                        return;
                    }


                }
            }
        }
    }

    ;

    @Override
    public void onQuestActivated(Quest aQuest) {
        onQuestEvent(aQuest);
    }

    @Override
    public void onQuestCompleted(Quest aQuest) {
        onQuestEvent(aQuest);
    }

    @Override
    public void onQuestTaskCompleted(Quest aQuest, QuestTask aTask) {
    }

    /*************************************** EFFECTS ************************************/

    protected void updateEffects(float dt) {
        if (mEffectLaunched != null) {
            updateLaunchedEffect(dt);
        }
        if (mEffectAction != null) {
            updateEffectAction(dt);
        }
    }


    protected void updateLaunchedEffect(float dt) {
        mZoneLaunchedEffect.setX(getShapeInteraction().getX() + getShapeInteraction().getBounds().width / 2);
        mZoneLaunchedEffect.setY(getShapeInteraction().getY() + getShapeInteraction().getBounds().height / 2);
        mEffectLaunchedTime += dt;
        float timeAction = mEffectLaunched.duration;
        if (timeAction < 0) {
            timeAction = mEffectLaunched.frames.size() / mEffectLaunched.fps;
        }
        if (mEffectLaunchedTime > timeAction) {
            stopLaunchedEffect();
        }
    }

    protected void updateEffectAction(float dt) {
        if (mEffectAction != null && mEffectAction.targetDuration != 0) {
            mEffectActionTime += dt;
            float timeAction = mEffectAction.targetDuration;
            boolean isTerminated = false;
            if (timeAction < 0) {
                //timeAction =  mEffectAction.frames.size()/mEffectAction.fps;
                isTerminated = mCurrentState.isCompleted(mStateTime);
            } else if (mEffectActionTime > timeAction) {
                isTerminated = true;

            }
            if (isTerminated) {
                stopEffectAction();
            }
        }
    }


    public boolean onStartEffectInteraction(CollisionEffectComponent aEntity) {
        Effect effect = (Effect) aEntity.mEffect;
        if (effect != null) {
            mEffectAction = effect;
            Gdx.app.debug("DEBUG", "onStartEffectInteraction " + getId());
            EventDispatcher.getInstance().onInteractionEvent(new InteractionEvent(getId(), InteractionEvent.EventType.EFFECT_START.name(), effect.id.name()));
        }
        return false;
    }

    public boolean onStopEffectInteraction(CollisionEffectComponent aEntity) {
        if (mEffectAction != null && mEffectAction.duration == 0) {
            stopEffectAction();
            return true;
        }

        return false;
    }

    @Override
    public boolean onCollisionEffectStart(CollisionEffectComponent aEntity) {
        if (!mCollisionsEffect.contains(aEntity, false)) {
            mCollisionsEffect.add(aEntity);
            onStartEffectInteraction(aEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCollisionEffectStop(CollisionEffectComponent aEntity) {
        if (mCollisionsEffect.contains(aEntity, false)) {
            mCollisionsEffect.removeValue(aEntity, false);
            onStopEffectInteraction(aEntity);
            return true;
        }
        return false;
    }

    @Override
    public Array<CollisionEffectComponent> getCollisionEffect() {
        return mCollisionsEffect;
    }

    public void launchEffect(Effect aEffect) {
        mEffectLaunched = aEffect;
        mEffectLaunchedTime = 0;
        mZoneLaunchedEffect = new CircleShape();
        mZoneLaunchedEffect.setRadius(mEffectLaunched.distance);
        mZoneLaunchedEffect.setX(getShapeInteraction().getX() + getShapeInteraction().getBounds().width / 2);
        mZoneLaunchedEffect.setY(getShapeInteraction().getY() + getShapeInteraction().getBounds().height / 2);
        add(new CollisionEffectComponent(mEffectLaunched, mZoneLaunchedEffect, mId));

        if (mEffectLaunched.sound != null && !mEffectLaunched.sound.isEmpty()) {
            AudioManager.getInstance().onAudioEvent(new AudioEvent(AudioEvent.Type.SOUND_PLAY_ONCE, mEffectLaunched.sound));
        }
    }

    protected void stopLaunchedEffect() {
        remove(CollisionEffectComponent.class);
        if (mEffectLaunched == null)
            return;

        if (mEffectLaunched.sound != null && !mEffectLaunched.sound.isEmpty()) {
            AudioManager.getInstance().onAudioEvent(new AudioEvent(AudioEvent.Type.SOUND_STOP, mEffectLaunched.sound));
        }

        mEffectLaunched = null;
        mEffectLaunchedTime = 0;
    }

    protected void stopEffectAction() {
        Gdx.app.debug("DEBUG", "stopEffectAction " + getId());
        EventDispatcher.getInstance().onInteractionEvent(new InteractionEvent(getId(), InteractionEvent.EventType.EFFECT_STOP.name(), mEffectAction.id.name()));
        mEffectAction = null;
        mEffectActionTime = 0;
    }

    protected void renderEffect(Batch aBatch) {
        if (mEffectLaunched != null) {
            TransformComponent transform = this.getComponent(TransformComponent.class);

            mEffectLaunched.renderEffect(aBatch,
                    mCurrentFrame.getRegionWidth(),
                    mCurrentFrame.getRegionHeight(),
                    transform.position.x + transform.originOffset.x,
                    transform.position.y + transform.originOffset.y,
                    transform.scale,
                    mEffectLaunchedTime);


        }
    }

    /*************************************** TOUCH ************************************/

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 cursorPoint = new Vector3();

        mCamera.unproject(cursorPoint.set(screenX, screenY, 0));

        if (hasTouchInteraction(cursorPoint.x, cursorPoint.y)) {
            onTouchInteraction();
            return true;
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    protected boolean hasTouchInteraction(float x, float y) {
        return isClickable();
    }

    public void onTouchInteraction() {

    }


}
