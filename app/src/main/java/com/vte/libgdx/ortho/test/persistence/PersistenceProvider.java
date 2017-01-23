package com.vte.libgdx.ortho.test.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by vincent on 20/01/2017.
 */

public class PersistenceProvider {
    private Json _json = new Json();
    private final String SAVE_FILE = "save.json";
    private static PersistenceProvider _instance = null;


    private Kryo kryo;

    public static PersistenceProvider getInstance() {
        if (_instance == null) {
            _instance = new PersistenceProvider();
        }

        return _instance;
    }

    private PersistenceProvider() {
        // _json.setIgnoreUnknownFields(true);
        kryo = new Kryo();

    }


    public void writeProfileToStorage(String fullFilename, String fileData, boolean overwrite) {

        boolean localFileExists = Gdx.files.local(fullFilename).exists();

        //If we cannot overwrite and the file exists, exit
        if (localFileExists && !overwrite) {
            return;
        }

        FileHandle file = null;

        if (Gdx.files.isLocalStorageAvailable()) {
            file = Gdx.files.local(fullFilename);
            String encodedString = Base64Coder.encodeString(fileData);
            file.writeString(encodedString, !overwrite);
        }

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
}
