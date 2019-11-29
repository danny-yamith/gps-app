package qualisys.com.gpsapp.gps;

import android.os.Bundle;

public interface GPSDialogListener {
    void okClickedGpsDialog(int request, Bundle extra);

    void cancelClickedGpsDialog(int request, Bundle extra);
}
