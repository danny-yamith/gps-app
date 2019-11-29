package qualisys.com.gpsapp.gps;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class Preferences {

    public static String SEND_COORDINATES = "SEND_COORDINATES";

    private static SharedPreferences getPreferences(Context ctx) {
        return ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
    }

    private static Editor getEditor(Context ctx) {
        return getPreferences(ctx).edit();
    }

    public static int getInt(String key, int defaultValue, Context ctx) {
        return getPreferences(ctx).getInt(key, defaultValue);
    }

    public static int getInt(Context ctx, String key, int defaultValue) {
        return getPreferences(ctx).getInt(key, defaultValue);
    }

    public static void putInteger(String key, int value, Context ctx) {
        putInteger(ctx, key, value);
    }

    public static void putInteger(Context ctx, String key, int value) {
        Editor edit = getEditor(ctx);
        edit.putInt(key, value);
        edit.commit();
    }

    public static long getLong(String key, long defaultValue, Context ctx) {
        return getPreferences(ctx).getLong(key, defaultValue);
    }

    public static void putLong(String key, long value, Context ctx) {
        putLong(ctx, key, value);
    }

    public static void putLong(Context ctx, String key, long value) {
        Editor edit = getEditor(ctx);
        edit.putLong(key, value);
        edit.commit();
    }

    public static float getFloat(String key, float defaultValue, Context ctx) {
        return getPreferences(ctx).getFloat(key, defaultValue);
    }

    public static void putFloat(String key, float value, Context ctx) {
        Editor edit = getEditor(ctx);
        edit.putFloat(key, value);
        edit.commit();
    }

    public static double getDouble(String key, double defaultValue, Context ctx) {
        return getDouble(ctx, key, defaultValue);
    }

    public static double getDouble(Context ctx, String key, double defaultValue) {
        SharedPreferences prefs = getPreferences(ctx);
        if (!prefs.contains(key)) {
            return defaultValue;
        }
        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }

    public static void putDouble(String key, double value, Context ctx) {
        putDouble(ctx, key, value);
    }

    public static void putDouble(Context ctx, String key, double value) {
        Editor edit = getEditor(ctx);
        edit.putLong(key, Double.doubleToRawLongBits(value));
        edit.commit();
    }

    public static String getString(String key, String defaultValue, Context ctx) {
        return getString(ctx, key, defaultValue);
    }

    public static String getString(Context ctx, String key, String defaultValue) {
        return getPreferences(ctx).getString(key, defaultValue);
    }

    public static void putString(String key, String value, Context ctx) {
        putString(ctx, key, value);
    }

    public static void putString(Context ctx, String key, String value) {
        Editor edit = getEditor(ctx);
        edit.putString(key, value);
        edit.commit();
    }

    public static boolean getBoolean(Context act, String key, boolean defaultValue) {
        return getPreferences(act).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context act, String key, boolean value) {
        Editor edit = getEditor(act);
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static void setSendCoordinates(Context ctx, boolean sendCoordinates) {
        Preferences.putBoolean(ctx, SEND_COORDINATES, sendCoordinates);
    }

    public static boolean getSendCoordinates(Context ctx) {
        return Preferences.getBoolean(ctx, SEND_COORDINATES, true);
    }

    public static boolean containsKey(Context ctx, String key) {
        SharedPreferences prefs = getPreferences(ctx);
        return prefs.contains(key);
    }

    public static void removeKey(Context ctx, String key) {
        Editor edit = getEditor(ctx);
        edit.remove(key);
        edit.commit();
    }
}
