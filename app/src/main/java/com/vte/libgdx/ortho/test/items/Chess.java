package com.vte.libgdx.ortho.test.items;

import java.util.ArrayList;

/**
 * Created by vincent on 29/01/2017.
 */

public class Chess {
    private String id;
    private ArrayList<String> items;
    private String openTexture;

    public String getCloseTexture() {
        return closeTexture;
    }

    public void setCloseTexture(String closeTexture) {
        this.closeTexture = closeTexture;
    }

    public String getOpenTexture() {
        return openTexture;
    }

    public void setOpenTexture(String openTexture) {
        this.openTexture = openTexture;
    }

    private String closeTexture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}
