package qualisys.com.gpsapp.gps;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import qualisys.com.gpsapp.GpsSqlHelper;
import qualisys.com.gpsapp.battery.BatteryConnectionReceiver;
import qualisys.com.gpsapp.battery.BatteryLevelReceiver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GPSService extends Service {

    private static final String LAST_INTERVAL_KEY = "GPS_LAST_INTERVAL_KEY";
    private static final int RESTARTER_DELAY_MILLIS = 180000;
    private final Handler mHandler = new Handler();
    private LocationManager lm = null;
    private QsGPSListener myLocationListener = null;
    private final Runnable mLocationProviderRestarter = new Runnable() {

        public void run() {
            long delta = new Date().getTime() - Coordinate.getLastTimeFromPrefs(GPSService.this);
            if (delta > 60000 * 3) {
                lm.removeUpdates(myLocationListener);
                addLocationListener();
            }
        }
    };

    public static int getInterval(Context ctx) {
        return Preferences.getInt(LAST_INTERVAL_KEY, 0, ctx);
    }

    /**
     * Indica si el Proveedor GPS está disponible.
     *
     * @param ctx
     * @return
     */
    public static boolean isProviderEnabled(Context ctx) {
        return Coordinate.isGPSEnabled(ctx);
    }

    /**
     * Inicia el Servicio, si ya estaba corriendo no hace nada.
     *
     * @param ctx
     */
    public static void startServiceGpsTracking(Context ctx) {
        Log.e("GpsService", "start tracking");
        Log.e("GpsService", "is running ? " + ServicesHelper.isServiceRunning(GPSService.class, ctx));
        if (!ServicesHelper.isServiceRunning(GPSService.class, ctx)) {
            Intent serviceIntent = new Intent(ctx, GPSService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(serviceIntent);
            } else {
                ctx.startService(serviceIntent);
            }
        }
    }

    /**
     * Inicia el servicio, si ya estaba corriendo lo detiene
     *
     * @param ctx
     */
    public static void restartServiceGpsTracking(Context ctx) {
        Log.e("GpsService", "restart tracking");
        Log.e("GpsService", "is running ? " + ServicesHelper.isServiceRunning(GPSService.class, ctx));
        if (ServicesHelper.isServiceRunning(GPSService.class, ctx)) {
            stopServiceGpsTracking(ctx);
        }
        Intent serviceIntent = new Intent(ctx, GPSService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(serviceIntent);
        } else {
            ctx.startService(serviceIntent);
        }
    }

    /**
     * Detiene el servicio
     *
     * @param ctx
     */
    public static void stopServiceGpsTracking(Context ctx) {
        Intent serviceIntent = new Intent(ctx, GPSService.class);
        ctx.stopService(serviceIntent);
    }

    /**
     * LLama a las opciones de configuración del sistema para que el usuario encienda el GPS.
     *
     * @param ctx
     */
    public static void startConfigGps(Context ctx) {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        ctx.getApplicationContext().startActivity(i);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        addLocationListener();
        return START_STICKY;
    }

    private void addLocationListener() {
        Log.e("GPSService", "agregado location listener");
        Thread triggerService = new Thread(new Runnable() {
            public void run() {
                try {
                    Looper.prepare();
                    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria c = new Criteria();
                    c.setAccuracy(Criteria.ACCURACY_COARSE);
                    myLocationListener = new QsGPSListener(GPSService.this);

                    boolean plugged = BatteryConnectionReceiver.isBatteryPlugged(GPSService.this);
                    boolean charged = BatteryLevelReceiver.isBatteryCharged(GPSService.this);
                    boolean moving = Coordinate.wasMoving(GPSService.this);
                    long t = 10;
                    if (!moving && !plugged && !charged) {
                        t = GPSSettings.geLongTime(GPSService.this);
                    } else if (!moving && !plugged && charged) {
                        t = GPSSettings.getMedTime(GPSService.this);
                    } else if (!moving && plugged && !charged) {
                        t = GPSSettings.geLongTime(GPSService.this);
                    } else if (!moving && plugged && charged) {
                        t = GPSSettings.getMedTime(GPSService.this);
                    } else if (moving && !plugged && !charged) {
                        t = GPSSettings.geLongTime(GPSService.this);
                    } else if (moving && !plugged && charged) {
                        t = GPSSettings.getMedTime(GPSService.this);
                    } else if (moving && plugged && !charged) {
                        t = GPSSettings.getMedTime(GPSService.this);
                    } else if (moving && plugged && charged) {
                        t = GPSSettings.getShortTime(GPSService.this);
                    }

                    float d;
                    if (Coordinate.getHighPrecisionMode(GPSService.this)) {
                        d = 1;
                    } else {
                        if (moving) {
                            d = GPSSettings.getShortDist(GPSService.this);
                        } else {
                            d = GPSSettings.getLongDist(GPSService.this);
                        }
                    }

                    Preferences.putInteger(LAST_INTERVAL_KEY, (int) t, GPSService.this);
                    Log.e("GPSService", "PackageManager.PERMISSION_GRANTED " + PackageManager.PERMISSION_GRANTED);
                    Log.e("GPSService", "ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_FINE_LOCATION) " + ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_FINE_LOCATION));
                    Log.e("GPSService", "ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_COARSE_LOCATION) " + ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_COARSE_LOCATION));

                    if (ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("GPSService", "Se va por el return......");
                        return;
                    }
                    lm.requestLocationUpdates(lm.getBestProvider(c, true), t, d, myLocationListener);
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(mLocationProviderRestarter, RESTARTER_DELAY_MILLIS);
                    Looper.loop();
                } catch (Exception ex) {
                    Log.e(GPSService.class.getSimpleName(), null, ex);
                    GPSService.this.stopSelf();
                }
            }
        }, "LocationThread");
        triggerService.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (lm != null) {
            lm.removeUpdates(myLocationListener);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this).build());
        }
    }

    public class QsGPSListener implements LocationListener {
        public Context ctx;

        public QsGPSListener(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onLocationChanged(Location location) {
            try {
                Coordinate coord = new Coordinate("norm", ctx);
                coord.latitude = location.getLatitude();
                coord.longitude = location.getLongitude();
                coord.accuracy = (int) location.getAccuracy();
                coord.speed = (int) (location.hasSpeed() ? location.getSpeed() : -1f);

                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(location.getTime());
                coord.dateCapture = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(gc.getTime());
                Log.e(GPSService.class.getSimpleName(), "lat: " + coord.latitude + " - lon: " + coord.longitude);
                Coordinate.writeCoordinate(coord, GPSService.this);
                GpsSqlHelper.insertCoordinate(getApplication(), coord, Preferences.getString(getApplication(), "imei", ""));
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(mLocationProviderRestarter, RESTARTER_DELAY_MILLIS);
            } catch (Exception e) {
                Log.e(GPSService.class.getSimpleName(), null, e);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //Log.d("gps", "Provider " + s + "status changed: " + i);
        }

        @Override
        public void onProviderEnabled(String s) {
            //Log.d("gps", "Provider Enabled: " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(ctx, "Encienda el GPS", Toast.LENGTH_SHORT).show();
            //Log.d("gps", "Provider Disabled: " + s);
        }
    }
}
