package com.vte.libgdx.ortho.test;


public class Settings {

    public static final int TARGET_WIDTH = 800;
    public static final int TARGET_HEIGHT = 480;
    public static final float ASPECT_RATIO = (float) TARGET_WIDTH / (float) TARGET_HEIGHT;

    public float musicVolume=100F;
    public boolean musicActivated=true;

    public String language;
    public String countryCode;

    static public enum SupportedLanguages {
        FR("fr"),
        EN("en");

        String value;

        public String getValue()
        {
            return value;
        }

        SupportedLanguages(String aValue)
        {
            value = aValue;
        }
    }


}
