package qualisys.com.gpsapp.gps;

import android.os.Parcel;
import android.os.Parcelable;

public class Speed implements Parcelable {

    public static final int AVG_MINUTES = 3;
    public static final String IS_MOVING_KEY = "GPS_IS_MOVING";

    public static final Creator<Speed> CREATOR = new Creator<Speed>() {
        public Speed createFromParcel(Parcel in) {
            return new Speed(in);
        }

        public Speed[] newArray(int size) {
            return new Speed[size];
        }
    };

    public long captureDate;
    public int speed;

    public Speed() {

    }

    public Speed(long captureDate, int speed) {
        this.captureDate = captureDate;
        this.speed = speed;
    }

    public Speed(Parcel in) {

        captureDate = in.readLong();
        speed = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(captureDate);
        out.writeInt(speed);
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
