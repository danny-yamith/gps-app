package qualisys.com.gpsapp.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class GPSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Coordinate.isGPSEnabled(context)) {
            GPSService.restartServiceGpsTracking(context);
        } else {
            try {
                Coordinate.writeCoordinate(new Coordinate("gps_off", context), context);
            } catch (Exception e) {
                Log.e(GPSBroadcastReceiver.class.getSimpleName(), null, e);
            }
        }
    }
}