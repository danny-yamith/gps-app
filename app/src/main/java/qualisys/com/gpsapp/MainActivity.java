package qualisys.com.gpsapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import qualisys.com.gpsapp.gps.Coordinate;
import qualisys.com.gpsapp.gps.GPSDialog;
import qualisys.com.gpsapp.gps.GPSDialogListener;
import qualisys.com.gpsapp.gps.GPSService;
import qualisys.com.gpsapp.gps.GPSTask;
import qualisys.com.gpsapp.gps.Preferences;

public class MainActivity extends AppCompatActivity implements GPSDialogListener {

    private static TextView txtCoordinate;
    private static Coordinate coord;


    public static void setTxtCoordinate(Coordinate coordinate) {
        coord = coordinate;
    }

    private void setText() {
        if (coord != null) {
            txtCoordinate.setText("Coordinate received\nLat: " + coord.latitude + "\nLon: " + coord.longitude + "\nTime: " + coord.dateCapture);
        } else {
            txtCoordinate.setText("Aún no hay coordenadas");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCoordinate = findViewById(R.id.txtCoordinate);
        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setText();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] pers = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};
            requestPermissions(pers, 35);
        }

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String deviceId = telephonyManager.getDeviceId();
        Preferences.putString(this, "imei", deviceId);

        setText();
        GPSTask.startTask(this);
        if (!GPSService.isProviderEnabled(this)) {
            GPSDialog.createDialog(100, null, "Alerta", "Encienda el GPS", "Aceptar", null, null).show(this);
        } else {
            GPSService.startServiceGpsTracking(this);
        }
    }

    @Override
    public void okClickedGpsDialog(int request, Bundle extra) {
        GPSService.startConfigGps(this);
    }

    @Override
    public void cancelClickedGpsDialog(int request, Bundle extra) {
    }

}
