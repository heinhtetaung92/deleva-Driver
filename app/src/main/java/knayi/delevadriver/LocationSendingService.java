package knayi.delevadriver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by heinhtetaung on 2/24/15.
 */
public class LocationSendingService extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public Location mLastLocation = null;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.buildGoogleApiClient();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //Toast.makeText(this, "Get Location", Toast.LENGTH_LONG).show();
            Log.i("lat", String.valueOf(mLastLocation.getLatitude()));
            Log.i("lon", String.valueOf(mLastLocation.getLongitude()));


        } else {
            //Toast.makeText(this, "Cannot get Location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();

        String loc = String.valueOf(location.getLongitude()) + ", " + String.valueOf(location.getLatitude());
        String token = sPref.getString(Config.TOKEN, "");
        Log.i("UpdateLocation", loc);
        Log.i("UpdateLocation", token);


        AvaliableJobsAPI.getInstance().getService().updateLocation(token, loc, ts, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("UpdateLocation", "Success");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("UpdateLocation", error.getBody().toString());
            }
        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
