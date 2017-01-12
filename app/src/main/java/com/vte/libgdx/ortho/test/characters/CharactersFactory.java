package com.vte.libgdx.ortho.test.characters;

import com.badlogic.gdx.utils.Array;

/**
 * Created by vincent on 05/01/2017.
 */

public class CharactersFactory {
    public Array<CharacterDef> characters;

    public Character createCharacter(int id) {

        CharacterDef p = characters.get(id);

        Character m = new Character(p.id, p.type, p.spritesheet, p.dialogId);

        return m;
    }

    public CharacterDef getCharacterDefById(String aId) {
        for (CharacterDef def : characters) {
            if (def.id.compareToIgnoreCase(aId) == 0) {
                return def;
            }
        }
        return null;
    }
}
