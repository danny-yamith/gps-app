package qualisys.com.gpsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import qualisys.com.gpsapp.gps.Coordinate;

public class GpsSqlHelper extends SQLiteOpenHelper {

    public GpsSqlHelper(Context context) {
        super(context, "gpsDb", null, 1);
    }

    public static void insertCoordinate(Context ctx, Coordinate coord, String imei) {
        GpsSqlHelper db = new GpsSqlHelper(ctx);
        SQLiteDatabase writer = db.getWritableDatabase();
        writer.beginTransaction();
        writer.execSQL("INSERT INTO gps_app (imei,dt,lat,lon) VALUES ('" + imei + "','" + coord.dateCapture + "', " + coord.latitude + ", " + coord.longitude + ")");
        writer.setTransactionSuccessful();
        writer.endTransaction();
        writer.close();
        db.close();
    }

    public static void deleteCoordinates(Context ctx) {
        GpsSqlHelper db = new GpsSqlHelper(ctx);
        SQLiteDatabase writer = db.getWritableDatabase();
        writer.beginTransaction();
        writer.execSQL("DELETE FROM gps_app");
        writer.setTransactionSuccessful();
        writer.endTransaction();
        writer.close();
        db.close();
    }

    public static List<Coordinate> getCoordinates(Context ctx) {

        GpsSqlHelper openDb = new GpsSqlHelper(ctx);
        SQLiteDatabase db = openDb.getReadableDatabase();
        List<Coordinate> items = new ArrayList<>();
        Cursor res = db.rawQuery("SELECT imei, dt, lat, lon FROM gps_app", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Coordinate obj = new Coordinate();
            obj.imei = res.getString(0);
            obj.dateCapture = res.getString(1);
            obj.latitude = res.getDouble(2);
            obj.longitude = res.getDouble(3);
            items.add(obj);
            res.moveToNext();
        }
        res.close();
        db.close();
        openDb.close();
        return items;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE gps_app (imei TEXT, dt TEXT, lat REAL, lon REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS gps_app");
    }
}
