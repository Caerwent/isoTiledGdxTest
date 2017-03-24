package com.vte.libgdx.ortho.test.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.effects.Effect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by vincent on 20/01/2017.
 */

public class PersistenceProvider {
    private Json _json = new Json();
    private static final String SAVE_FILE = "save.json";
    private static final String SETTINGS_FILE = "settings.json";
    private static PersistenceProvider _instance = null;


    private Kryo kryo;

    private Settings mSettings;

    public static PersistenceProvider getInstance() {
        if (_instance == null) {
            _instance = new PersistenceProvider();
        }

        return _instance;
    }

    private PersistenceProvider() {
        // _json.setIgnoreUnknownFields(true);
        kryo = new Kryo();
        CompatibleFieldSerializer serializer = new CompatibleFieldSerializer<Profile>(kryo, Profile.class);
        serializer.setFieldsCanBeNull(true);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        // use `EXTENDED` name strategy, otherwise serialized object can't be deserialized correctly. Attention, `EXTENDED` strategy increases the serialized footprint.
        kryo.getFieldSerializerConfig().setCachedFieldNameStrategy(FieldSerializer.CachedFieldNameStrategy.EXTENDED);

        loadSettings();
    }

    public static boolean isProfileExist() {
        String filename = SAVE_FILE;
        if (Gdx.files.isLocalStorageAvailable()) {
            return Gdx.files.local(filename).exists();
        } else {
            return false;
        }
    }


    public void saveSettings() {

        if(mSettings==null)
        {
            loadSettings();
        }
        FileHandle encodedFile;
        if (Gdx.files.isLocalStorageAvailable()) {
            encodedFile = Gdx.files.local(SETTINGS_FILE);
            Output output = null;
            try {
                output = new Output(new FileOutputStream(encodedFile.file().getAbsolutePath()));
                kryo.writeObject(output, mSettings);
                output.close();
            } catch (FileNotFoundException e) {
                if (output != null) {
                    output.close();
                }
                e.printStackTrace();
            }
        }
    }

    public void loadSettings() {

        if (Gdx.files.isLocalStorageAvailable()) {
            boolean doesProfileFileExist = Gdx.files.local(SETTINGS_FILE).exists();
            if (!doesProfileFileExist) {
                mSettings = new Settings();
                saveSettings();
            } else {
                FileHandle encodedFile;
                encodedFile = Gdx.files.local(SETTINGS_FILE);
                Input input = null;
                try {
                    input = new Input(new FileInputStream(encodedFile.file().getAbsolutePath()));
                    mSettings = kryo.readObject(input, Settings.class);
                    input.close();
                } catch (FileNotFoundException e) {
                    if (input != null) {
                        input.close();
                    }
                    e.printStackTrace();
                }
            }

        }

    }

    public Settings getSettings()
    {
        return mSettings;
    }


    public void save(Profile aProfile) {
        String filename = SAVE_FILE;
       /* String text = _json.prettyPrint(_json.toJson(aProfile));

        FileHandle encodedFile = Gdx.files.local(filename);
        writeProfileToStorage(encodedFile.name(), text, true);*/

        FileHandle encodedFile;
        if (Gdx.files.isLocalStorageAvailable()) {
            encodedFile = Gdx.files.local(filename);
            Output output = null;
            try {
                output = new Output(new FileOutputStream(encodedFile.file().getAbsolutePath()));
                kryo.writeObject(output, aProfile);
                output.close();
            } catch (FileNotFoundException e) {
                if (output != null) {
                    output.close();
                }
                e.printStackTrace();
            }
        }
    }

    public Profile loadProfile() {
        String filename = SAVE_FILE;
        Profile profile = null;
        if (Gdx.files.isLocalStorageAvailable()) {
            boolean doesProfileFileExist = Gdx.files.local(filename).exists();
            if (!doesProfileFileExist) {
                profile = new Profile();
                save(profile);
            } else {
 /*           FileHandle encodedFile = Gdx.files.local(filename);
            String s = encodedFile.readString();

            String decodedFile = Base64Coder.decodeString(s);

            profile = _json.fromJson(Profile.class, decodedFile);*/

                FileHandle encodedFile;
                encodedFile = Gdx.files.local(filename);
                Input input = null;
                try {
                    input = new Input(new FileInputStream(encodedFile.file().getAbsolutePath()));
                    profile = kryo.readObject(input, Profile.class);
                    input.close();
                } catch (FileNotFoundException e) {
                    if (input != null) {
                        input.close();
                    }
                    e.printStackTrace();
                }
            }

        }
        return profile;

    }

    public void saveMapProfile(String aMapName, MapProfile aMapProfile) {
        save(Profile.getInstance());
    }

    public void saveQuestProfile(String aQuestId, QuestProfile aQuestProfile) {
        save(Profile.getInstance());
    }

    public void saveInventory(ArrayList<String> aInventory) {
        save(Profile.getInstance());
    }

    public void saveNPCProfile(String aNPCId, NPCProfile aNPCProfile) {
        save(Profile.getInstance());
    }

    public void saveLocationProfile(LocationProfile aLocationProfile) {
        save(Profile.getInstance());
    }

    public void saveSelectedEffect(Effect.Type aSelectedEffect)
    {
        save(Profile.getInstance());
    }
    public void saveEffectsList(ArrayList<Effect.Type> aList)
    {
        save(Profile.getInstance());
    }
}
