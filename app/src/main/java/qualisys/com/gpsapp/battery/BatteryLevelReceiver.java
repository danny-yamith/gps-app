package qualisys.com.gpsapp.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import qualisys.com.gpsapp.gps.GPSService;

public class BatteryLevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GPSService.restartServiceGpsTracking(context);
    }


    public static boolean isBatteryCharged(Context context){
        return BatteryStatus.getBatteryLevel(context).level  > 20;
    }

}