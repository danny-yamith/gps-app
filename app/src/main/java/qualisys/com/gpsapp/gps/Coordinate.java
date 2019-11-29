package qualisys.com.gpsapp.gps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import qualisys.com.gpsapp.MainActivity;
import qualisys.com.gpsapp.battery.BatteryStatus;

public class Coordinate implements Parcelable {

    public static final String LAST_LAT_KEY = "GPS_LAST_LAT_KEY";
    public static final String LAST_LON_KEY = "GPS_LAST_LON_KEY";
    public static final String LAST_DT = "GPS_LAST_DT";
    public static final String PRIVATE_MODE = "PRIVATE_MODE";
    public static final String HIGH_PRECISION_MODE = "HIGH_PRECISION_MODE";
    public static final int MAX_BUFFER_TIME_MINUTES = 90;
    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
    private static final String GPS_PROVIDER_STATUS = Coordinate.class.getCanonicalName() + ".GPS_PROVIDER_STATUS";
    private static final String GPS_PROVIDER_LAST_DT = Coordinate.class.getCanonicalName() + ".GPS_PROVIDER_LAST_DT";
    public Integer id;
    public Double latitude;
    public Double longitude;
    public int accuracy;
    public String dateCapture;
    public String type;
    public int speed;
    public int charge;
    public boolean moving;
    public boolean plugged;
    public int interval;
    public String imei;

    public Coordinate() {

    }

    public Coordinate(Parcel in) {
        id = (Integer) in.readValue(Integer.class.getClassLoader());
        latitude = (Double) in.readValue(Double.class.getClassLoader());
        longitude = (Double) in.readValue(Double.class.getClassLoader());
        accuracy = in.readInt();
        dateCapture = in.readString();
        type = in.readString();
        speed = in.readInt();
        charge = in.readInt();
        moving = in.readInt() == 1;
        plugged = in.readInt() == 1;
        interval = in.readInt();
        imei = in.readString();
    }


    public Coordinate(Context ctx) {
        this.latitude = 0.0d;
        this.longitude = 0.0d;
        this.accuracy = 0;
        this.speed = 0;
        this.dateCapture = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.moving = wasMoving(ctx);
        BatteryStatus bl = BatteryStatus.getBatteryLevel(ctx);
        this.charge = (int) bl.level;
        this.plugged = bl.plugged;
        this.interval = GPSService.getInterval(ctx);
        this.imei = Preferences.getString(ctx, "imei", "");
    }

    public Coordinate(String type, Context ctx) {
        this(ctx);
        this.type = type;
    }

    public static synchronized void writeCoordinate(Coordinate coord, Context ctx) throws Exception {
        Log.e(Coordinate.class.getSimpleName(), "Llega la coordenada");
        Preferences.putDouble("lat", coord.latitude, ctx);
        Preferences.putDouble("lon", coord.longitude, ctx);
        Preferences.putString("dt", coord.dateCapture, ctx);
        MainActivity.setTxtCoordinate(coord);
    }

    /*private static synchronized boolean coordinateExists(Coordinate location, SQLiteDatabase db) throws Exception {
        return new SQLiteQuery("SELECT COUNT(*) > 0 FROM coordinates WHERE latitude = " + location.latitude + " AND longitude = " + location.longitude + " AND date_capture = " + location.dateCapture + "").getAsBoolean(db);
    }*/

    /*public static synchronized Coordinate[] getCoordinates(SQLiteDatabase db, int limit) throws Exception {
        Object[][] data = new SQLiteQuery("SELECT "
                + "id, latitude, longitude, accuracy, date_capture, type, speed, charge, mov, plugged, interval "
                + "FROM coordinates "
                + "ORDER BY date_capture ASC" + (limit > 0 ? " LIMIT " + limit : "")).getRecords(db);

        if (data != null && data.length > 0) {
            Coordinate[] rta = new Coordinate[data.length];
            for (int i = 0; i < rta.length; i++) {
                Coordinate coord = new Coordinate();
                rta[i] = coord;
                Object[] row = data[i];
                coord.id = MySQLQuery.getAsInteger(row[0]);
                coord.latitude = MySQLQuery.getAsDouble(row[1]);
                coord.longitude = MySQLQuery.getAsDouble(row[2]);
                coord.accuracy = MySQLQuery.getAsInteger(row[3]);
                coord.dateCapture = MySQLQuery.getAsLong(row[4]);
                coord.type = MySQLQuery.getAsString(row[5]);
                coord.speed = MySQLQuery.getAsInteger(row[6]);
                coord.charge = MySQLQuery.getAsInteger(row[7]);
                coord.moving = MySQLQuery.getAsBoolean(row[8]);
                coord.plugged = MySQLQuery.getAsBoolean(row[9]);
                coord.interval = MySQLQuery.getAsInteger(row[10]);
            }
            return rta;
        } else {
            return new Coordinate[0];
        }
    }*/

    /*public static synchronized Coordinate[] getCoordinates(Context ctx, int limit) throws Exception {
        SQLiteDatabase db = null;
        try {
            db = GPSDBHelper.getReadable(ctx);
            return getCoordinates(db, limit);
        } finally {
            GPSDBHelper.tryClose(db);
        }
    }*/

    /*public static synchronized void deleteCoordinates(Context ctx) {
        SQLiteDatabase db = null;
        try {
            db = GPSDBHelper.getWritable(ctx);
            db.execSQL("DELETE FROM coordinates");
        } catch (Exception e) {
            Log.e(Coordinate.class.getSimpleName(), null, e);
        } finally {
            GPSDBHelper.tryClose(db);
        }
    }*/

    public static void lastCoordToPrefs(Coordinate coord, Context ctx) {
        Preferences.putDouble(LAST_LAT_KEY, coord.latitude, ctx);
        Preferences.putDouble(LAST_LON_KEY, coord.longitude, ctx);
        Preferences.putString(LAST_DT, coord.dateCapture, ctx);
    }

    /*public static void setHighPrecisionMode(Context ctx, boolean value) {
        if (!Preferences.existsPreference(ctx, HIGH_PRECISION_MODE) || getHighPrecisionMode(ctx) != value) {
            Preferences.putBoolean(ctx, HIGH_PRECISION_MODE, value);
            GPSService.restartServiceGpsTracking(ctx);
        } else {
            Log.e(Coordinate.class.getSimpleName(), "EL SERVICIO YA ESTA INICIADO");
        }
    }*/

    public static boolean getHighPrecisionMode(Context ctx) {
        return Preferences.getBoolean(ctx, HIGH_PRECISION_MODE, false);
    }

    public static Double getLastLatFromPrefs(Context ctx) {
        double lat = Preferences.getDouble(LAST_LAT_KEY, 1000, ctx);
        return lat != 1000 ? lat : null;
    }

    public static Double getLastLonFromPrefs(Context ctx) {
        double lon = Preferences.getDouble(LAST_LON_KEY, 1000, ctx);
        return lon != 1000 ? lon : null;
    }

    /**
     * @param ctx
     * @return el timestamp de la última coordenada recibida o 0 si no hay ninguna
     */
    public static long getLastTimeFromPrefs(Context ctx) {
        return Preferences.getLong(LAST_DT, 0, ctx);
    }

    public static void setPrivateMode(Context ctx, boolean value) {
        Preferences.putBoolean(ctx, PRIVATE_MODE, value);
    }

    public static boolean isPrivateMode(Context ctx) {
        return Preferences.getBoolean(ctx, PRIVATE_MODE, false);
    }

    public static void setGpsProviderStatus(Context ctx, boolean value) {
        boolean oldStatus = getGpsProviderStatus(ctx);
        if (value != oldStatus) {
            Preferences.putBoolean(ctx, GPS_PROVIDER_STATUS, value);
            Preferences.putLong(GPS_PROVIDER_LAST_DT, new Date().getTime(), ctx);
        }
    }

    public static boolean getGpsProviderStatus(Context ctx) {
        return Preferences.getBoolean(ctx, GPS_PROVIDER_STATUS, false);
    }

    public static boolean isLastCoordValid(Context ctx) {
        long lc = getLastTimeFromPrefs(ctx);
        if (lc == 0) {
            System.out.printf("NO LC found!");
            return false;
        }
        if (!getGpsProviderStatus(ctx)) {
            System.out.printf("NO Provider Status!");
            return false;
        }
        Log.e(Coordinate.class.getSimpleName(), lc + " >= " + Preferences.getLong(GPS_PROVIDER_LAST_DT, new Date().getTime(), ctx));
        return lc >= Preferences.getLong(GPS_PROVIDER_LAST_DT, new Date().getTime(), ctx);
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean status = (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        setGpsProviderStatus(context, status);
        return status;
    }

    public static synchronized void writeSpeed(Speed speed, SQLiteDatabase db) throws Exception {
        db.execSQL("INSERT INTO speed (capture_date,speed) VALUES (" + speed.captureDate + "," + speed.speed + ")");

    }

    /**
     * Consulta el resultado del último cálculo
     *
     * @param ctx
     * @return
     */
    public static boolean wasMoving(Context ctx) {
        return Preferences.getBoolean(ctx, Speed.IS_MOVING_KEY, false);
    }

    /*public static synchronized double getCurrentSpeed(Context ctx) throws Exception {
        SQLiteDatabase db = null;
        try {
            db = GPSDBHelper.getReadable(ctx);
            db.execSQL("DELETE FROM speed WHERE capture_date < " + (new Date().getTime() - (Speed.AVG_MINUTES * 60000)));
            Double avg = new SQLiteQuery("SELECT AVG(speed) FROM speed").getAsDouble(db);
            return avg != null ? avg : 0;
        } finally {
            GPSDBHelper.tryClose(db);
        }
    }*/

    /*
    /**
     * Calcula el movimiento en el instante y guarda su estado para wasMoving
     *
     * @param ctx
     * @return
     */

    /*public static boolean isMoving(Context ctx) {
        try {
            boolean isMoving = getCurrentSpeed(ctx) * 3.6 >= GPSSettings.getMovLimitKMH(ctx);
            Preferences.putBoolean(ctx, Speed.IS_MOVING_KEY, isMoving);
            return isMoving;
        } catch (Exception e) {
            Log.e(Coordinate.class.getSimpleName(), null, e);
            return false;
        }
    }*/

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeInt(accuracy);
        dest.writeString(dateCapture);
        dest.writeString(type);
        dest.writeInt(speed);
        dest.writeInt(charge);
        dest.writeInt(moving ? 1 : 0);
        dest.writeInt(plugged ? 1 : 0);
        dest.writeInt(interval);
        dest.writeString(imei);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
