package qualisys.com.gpsapp.gps;

import android.content.Context;

public class GPSSettings {
    private static long DEFAULT_MOV_LIMIT_KMH = 9;
    private static float DEFAULT_MIN_DISTANCE_M = 8f;
    private static long DEFAULT_REFRESH_MS = 8000;

    private static final String MIN_SHORT_T_KEY = "MIN_SHORT_T";
    private static final String MIN_MED_T_KEY = "MIN_MED_T";
    private static final String MIN_LONG_T_KEY = "MIN_LONG_T";

    private static final String MIN_SHORT_D_KEY = "MIN_SHORT_D";
    private static final String MIN_MED_D_KEY = "MIN_MED_D";
    private static final String MIN_LONG_D_KEY = "MIN_LONG_D";

    private static final String MOV_LIMIT_KMH_KEY = "MOV_LIMIT_KMH_KEY";

    //GETTERS

    public static long getShortTime(Context ctx) {
        return Preferences.getLong(MIN_SHORT_T_KEY, DEFAULT_REFRESH_MS, ctx);
    }

    public static long getMedTime(Context ctx) {
        return Preferences.getLong(MIN_MED_T_KEY, DEFAULT_REFRESH_MS, ctx);
    }

    public static long geLongTime(Context ctx) {
        return Preferences.getLong(MIN_LONG_T_KEY, DEFAULT_REFRESH_MS, ctx);
    }

    public static float getShortDist(Context ctx) {
        return Preferences.getFloat(MIN_SHORT_D_KEY, DEFAULT_MIN_DISTANCE_M, ctx);
    }

    public static float getMedDist(Context ctx) {
        return Preferences.getFloat(MIN_MED_D_KEY, DEFAULT_MIN_DISTANCE_M, ctx);
    }

    public static float getLongDist(Context ctx) {
        return Preferences.getFloat(MIN_LONG_D_KEY, DEFAULT_MIN_DISTANCE_M, ctx);
    }

    public static float getMovLimitKMH(Context ctx) {
        return Preferences.getFloat(MOV_LIMIT_KMH_KEY, DEFAULT_MOV_LIMIT_KMH, ctx);
    }

    //SETTERS

    public static void setShortTime(long t, Context ctx) {
        Preferences.putLong(MIN_SHORT_T_KEY, t, ctx);
    }

    public static void setMedTime(long t, Context ctx) {
        Preferences.putLong(MIN_MED_T_KEY, t, ctx);
    }

    public static void setLongTime(long t, Context ctx) {
        Preferences.putLong(MIN_LONG_T_KEY, t, ctx);
    }

    public static void setShortDist(float d, Context ctx) {
        Preferences.putFloat(MIN_SHORT_D_KEY, d, ctx);
    }

    public static void setMedDist(float d, Context ctx) {
        Preferences.putFloat(MIN_MED_D_KEY, d, ctx);
    }

    public static void setLongDist(float d, Context ctx) {
        Preferences.putFloat(MIN_LONG_D_KEY, d, ctx);
    }

    public static void setMovLimitKMH(float d, Context ctx) {
        Preferences.putFloat(MOV_LIMIT_KMH_KEY, d, ctx);
    }
}