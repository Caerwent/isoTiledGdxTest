package com.vte.libgdx.ortho.test.persistence;

import java.util.HashMap;

/**
 * Created by vincent on 14/03/2017.
 */

public class GameSession {
    static private GameSession sSession;

    public static void createNewSession()
    {
        sSession = new GameSession();
    }
    public static GameSession getInstance() {

        if (sSession == null) {
            sSession = new GameSession();
        }
        return sSession;
    }

    private HashMap<String, HashMap<String, HashMap>> mSessionData = new HashMap();

    public Object getSessionDataForMapAndEntity(String aMapId, String aEntity, String aKey)
    {
        if(!mSessionData.containsKey(aMapId)) {

            HashMap map = new HashMap();
            mSessionData.put(aMapId, map);
        }
        if(!mSessionData.get(aMapId).containsKey(aEntity)) {

            HashMap map = new HashMap();
            mSessionData.get(aMapId).put(aEntity, map);
        }
        return mSessionData.get(aMapId).get(aEntity).get(aKey);
    }


    public void putSessionDataForMapAndEntity(String aMapId, String aEntity, String aKey, Object aValue)
    {
        if(!mSessionData.containsKey(aMapId)) {

            HashMap map = new HashMap();
            mSessionData.put(aMapId, map);
        }
        if(!mSessionData.get(aMapId).containsKey(aEntity)) {

            HashMap map = new HashMap();
            mSessionData.get(aMapId).put(aEntity, map);
        }
        mSessionData.get(aMapId).get(aEntity).put(aKey, aValue);
    }

}
