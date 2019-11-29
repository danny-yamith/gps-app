package qualisys.com.gpsapp.gps;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class ServicesHelper {

    public static boolean isServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e(ServicesHelper.class.getSimpleName(), "El servicio " + serviceClass.getName() + " está corriendo");
                return true;
            }
        }
        Log.e(ServicesHelper.class.getSimpleName(), "El servicio " + serviceClass.getName() + " NO está corriendo");
        return false;
    }
}
