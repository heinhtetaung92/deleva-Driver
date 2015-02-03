package knayi.delevadriver;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by heinhtetaung on 2/3/15.
 */
public class GPSLocation {

    public static String getLocation(Context context){
        String location = null;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,1, this);
        location = String.valueOf(loc.getLongitude()) + "," + String.valueOf(loc.getLatitude());
        Log.i("Location", String.valueOf(loc.getLongitude() + "," + String.valueOf(loc.getLatitude())));

        return location;
    }

}
