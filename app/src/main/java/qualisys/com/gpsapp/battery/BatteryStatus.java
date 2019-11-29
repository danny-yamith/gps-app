package qualisys.com.gpsapp.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryStatus {
    public float level;
    public boolean plugged;

    public BatteryStatus(float level, boolean plugged) {
        this.level = level;
        this.plugged = plugged;
    }

    public static BatteryStatus getBatteryLevel(Context ctx) {
        Intent batteryIntent = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        boolean plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0;
        float fl = ((float) level / (float) scale) * 100.0f;
        return new BatteryStatus(fl, plugged);
    }
}
