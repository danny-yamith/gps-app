package qualisys.com.gpsapp.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import qualisys.com.gpsapp.gps.GPSService;
import qualisys.com.gpsapp.gps.Preferences;


public class BatteryConnectionReceiver extends BroadcastReceiver {

    private static final String BATTERY_PLUGGED = "GPS_BATTERY_PLUGGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            //Log.d("gps", "Cargador Conectado");
            Preferences.putBoolean(context, BATTERY_PLUGGED, true);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            //Log.d("gps", "Cargador Desconectado");
            Preferences.putBoolean(context, BATTERY_PLUGGED, false);
        }
        GPSService.restartServiceGpsTracking(context);
    }

    public static boolean isBatteryPlugged(Context context) {
        return Preferences.getBoolean(context, BATTERY_PLUGGED, false);
    }

}