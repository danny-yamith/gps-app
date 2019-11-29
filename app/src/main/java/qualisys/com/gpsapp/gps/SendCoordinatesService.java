package qualisys.com.gpsapp.gps;

import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import qualisys.com.gpsapp.ClientHttpRequest;
import qualisys.com.gpsapp.GpsSqlHelper;

public class SendCoordinatesService extends android.app.IntentService {

    public SendCoordinatesService() {
        super("SendCoordinatesService");
    }

    private static void tryClose(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ex) {
                Log.e(SendCoordinatesService.class.getSimpleName(), ex.getMessage());
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(SendCoordinatesService.class.getSimpleName(), "-------------------------------------- send service");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        List<Coordinate> coordinates = GpsSqlHelper.getCoordinates(getApplication());
        for (int i = 0; i < coordinates.size(); i++) {
            JsonObjectBuilder ob = Json.createObjectBuilder();
            Coordinate coord = coordinates.get(i);
            ob.add("imei" + i, coord.imei);
            ob.add("dt" + i, coord.dateCapture);
            ob.add("lat" + i, String.valueOf(coord.latitude));
            ob.add("lon" + i, String.valueOf(coord.longitude));
            jab.add(ob);
        }
        JsonWriter w = Json.createWriter(baos);

        w.writeArray(jab.build());

        BufferedReader br = null;
        try {
            ClientHttpRequest request = new ClientHttpRequest(new URL("http://qualisys.com.co:8080/GpsApp/GpsCoordinates").openConnection());
            request.setParameter("data", "", new ByteArrayInputStream(baos.toByteArray()));
            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(request.post())));
            String s = br.readLine();
            Log.e("Coords", s);
            if (s.equals("ok")) {
                Log.e("Coords", "entra a borrar las coordenadas");
                GpsSqlHelper.deleteCoordinates(getApplication());
            }
        } catch (Exception e) {
            Log.e("Service", null, e);
        }  finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
