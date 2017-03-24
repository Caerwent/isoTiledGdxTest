package com.vte.libgdx.ortho.test.audio;

/**
 * Created by vincent on 17/03/2017.
 */

public class AudioEvent {
    public static enum Type {
        MUSIC_LOAD,
        MUSIC_PLAY_ONCE,
        MUSIC_PLAY_LOOP,
        MUSIC_STOP,
        MUSIC_STOP_ALL,
        SOUND_LOAD,
        SOUND_PLAY_ONCE,
        SOUND_PLAY_LOOP,
        SOUND_STOP
    }

    private String mAudioFile;
    private Type mType;

    public AudioEvent(Type aType, String aFile)
    {
        mAudioFile = aFile;
        mType = aType;
    }

    public String getAudioFile()
    {
        return mAudioFile;
    }

    public Type getType()
    {
        return mType;
    }
}
