package com.vte.libgdx.ortho.test.entity.fsm;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap;


public class StateMachine {

    private Logger logger;
    private ObjectMap<Class<? extends State>, State> states;
    private State initialState;
    private State currentState;
    private State nextState;
    private Entity entity;

    public StateMachine(String name, Entity entity) {
        logger = new Logger(name, Logger.DEBUG);
        logger.info("initialising");
        states = new ObjectMap<Class<? extends State>, State>();
        this.entity = entity;
    }

    public void addState(State state) {
        if (states.size == 0) {
            initialState = state;
        }

        states.put(state.getClass(), state);
    }

    public void update(float deltaTime) {
        if (currentState == null && initialState != null) {
            currentState = initialState;
            currentState.enter();
        }

        if (currentState != null) {
            currentState.update(deltaTime);
        }

        if (nextState != null && nextState != currentState) {
            logger.info("switching to state " + nextState.getClass().getSimpleName());
            currentState.exit();
            currentState = nextState;
            nextState = null;
            currentState.enter();
        }
    }

    public State getState() {
        return currentState;
    }

    public <T extends State> T getState(Class<T> stateClass) {
        return stateClass.cast(states.get(stateClass));
    }

    public void setNextState(Class<? extends State> stateClass) {
        nextState = states.get(stateClass);
    }

    public Entity getEntity() {
        return entity;
    }
}
