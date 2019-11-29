package qualisys.com.gpsapp.gps;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

public class GPSTask extends GcmTaskService {

    private static Long period = null;

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.e(GPSTask.class.getSimpleName(), taskParams.getTag());

        /*if (!Preferences.getOffLineMode(this)) {*/
            boolean wasMoving = Coordinate.wasMoving(this);
            boolean isMoving = true;
            if (wasMoving == isMoving) {
                GPSService.startServiceGpsTracking(this);
            } else {
                GPSService.restartServiceGpsTracking(this);
            }
            Intent service = new Intent(this, SendCoordinatesService.class);
            service.putExtra("data", taskParams.getExtras());
            this.startService(service);
/*        } else {
            GPSService.stopServiceGpsTracking(this);
        }*/
        return 0;
    }

    public static void startTask(Context ctx) {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(ctx);
        //Bundle b = new Bundle();
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(GPSTask.class)
                .setTag("SEND_COORDINATES")
                .setPeriod(30)
                .setFlex(30)
                .setRequiredNetwork(NETWORK_STATE_ANY)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .build();
        mGcmNetworkManager.schedule(task);
    }

    public static void stopTask(Context ctx) {
        GcmNetworkManager nm = GcmNetworkManager.getInstance(ctx);
        nm.cancelTask("SEND_COORDINATES", GPSTask.class);
    }

    public static void restartTask(Context ctx) {
        stopTask(ctx);
        startTask(ctx);
    }
}
