package com.vte.libgdx.ortho.test.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.persistence.PersistenceProvider;

import java.util.Hashtable;

/**
 * Created by vincent on 17/03/2017.
 */

public class AudioManager implements AudioEventListener {
    private static final String TAG = AudioManager.class.getSimpleName();

    private static AudioManager sInstance = null;

    private Hashtable<String, Music> mQueuedMusic;
    private Hashtable<String, Sound> mQueuedSounds;

    private final static String SOUND_ROOT_PATH = "data/sounds/";
    public final static AudioEvent ITEM_FOUND_SOUND = new AudioEvent(AudioEvent.Type.SOUND_PLAY_ONCE, "item_found.mp3");
    public final static AudioEvent UI_CLIC_SOUND = new AudioEvent(AudioEvent.Type.SOUND_PLAY_ONCE, "menu_click.wav");

    private AudioManager() {
        mQueuedMusic = new Hashtable<String, Music>();
        mQueuedSounds = new Hashtable<String, Sound>();
        onAudioEvent(new AudioEvent(AudioEvent.Type.SOUND_LOAD, "item_found.mp3"));
        onAudioEvent(new AudioEvent(AudioEvent.Type.SOUND_LOAD, "menu_click.wav"));
    }

    public static AudioManager getInstance() {
        if (sInstance == null) {
            sInstance = new AudioManager();
        }

        return sInstance;
    }


    @Override
    public void onAudioEvent(AudioEvent aAudioEvent) {
        if (aAudioEvent == null)
            return;

        switch (aAudioEvent.getType()) {
            case MUSIC_LOAD:
                if(PersistenceProvider.getInstance().getSettings().musicActivated ) {
                    AssetsUtility.loadMusicAsset(SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                }
                break;
            case MUSIC_PLAY_ONCE:
                if(PersistenceProvider.getInstance().getSettings().musicActivated ) {
                    playMusic(false, SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                }
                break;
            case MUSIC_PLAY_LOOP:
                if(PersistenceProvider.getInstance().getSettings().musicActivated ) {
                    playMusic(true, SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                }
                break;
            case MUSIC_STOP:
                Music music = mQueuedMusic.get(SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                if (music != null) {
                    music.stop();
                }
                break;
            case MUSIC_STOP_ALL:
                for (Music musicStop : mQueuedMusic.values()) {
                    musicStop.stop();
                }
                break;
            case SOUND_LOAD:
                AssetsUtility.loadSoundAsset(SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                break;
            case SOUND_PLAY_LOOP:
                playSound(true, SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                break;
            case SOUND_PLAY_ONCE:
                playSound(false, SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                break;
            case SOUND_STOP:
                Sound sound = mQueuedSounds.get(SOUND_ROOT_PATH+aAudioEvent.getAudioFile());
                if (sound != null) {
                    sound.stop();
                }
                break;
            default:
                break;
        }
    }


    private Music playMusic(boolean isLooping, String fullFilePath) {

        Music music = mQueuedMusic.get(fullFilePath);
        if (music != null) {
            music.setLooping(isLooping);
            music.setVolume(PersistenceProvider.getInstance().getSettings().musicVolume);
            music.play();
        } else if (AssetsUtility.isAssetLoaded(fullFilePath)) {
            music = AssetsUtility.getMusicAsset(fullFilePath);
            music.setLooping(isLooping);
            music.setVolume(PersistenceProvider.getInstance().getSettings().musicVolume);
            music.play();
            mQueuedMusic.put(fullFilePath, music);
        } else {
            Gdx.app.debug(TAG, "Music not loaded");
            return null;
        }
        return music;
    }

    private Sound playSound(boolean isLooping, String fullFilePath) {
        Sound sound = mQueuedSounds.get(fullFilePath);
        if (sound != null) {
            long soundId = sound.play();
            sound.setLooping(soundId, isLooping);
        } else if (AssetsUtility.isAssetLoaded(fullFilePath)) {
            sound = AssetsUtility.getSoundAsset(fullFilePath);
            long soundId = sound.play();
            sound.setLooping(soundId, isLooping);
            mQueuedSounds.put(fullFilePath, sound);
        } else {
            Gdx.app.debug(TAG, "Sound not loaded");
            return null;
        }
        return sound;
    }

    public void dispose() {
        for (Music music : mQueuedMusic.values()) {
            music.dispose();
        }

        for (Sound sound : mQueuedSounds.values()) {
            sound.dispose();
        }
    }
}
