package knayi.delevadriver.updategpsLocation;

/**
 * Created by heinhtetaung on 3/29/15.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import knayi.delevadriver.Config;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;


/**
 * BackgroundLocationService used for tracking user location in the background.
 *
 * @author cblack
 */
public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    IBinder mBinder = new LocalBinder();

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;



    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    SharedPreferences sPref;

    private Location mLastLocation = null;

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startLocationUpdates();

        Log.i("LocationService", "on Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i("LocationService", "onLOcaitonChanged");

        if(mLastLocation != null){

            SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);

            double dist = distance(location.getLatitude(), location.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), 'K');

            if(dist > 0.05){

                Log.i("LocationService", "over 0.05");

                updateCurrentLocation(location);

                mLastLocation = location;



                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putInt(Config.TOKEN_LOCDELAY_COUNT, 0);
                    editor.commit();

            }

            /*else if(dist < 0.05){

                Log.i("LocationService", "under 0.05");

                int delay_count = sPref.getInt(Config.TOKEN_LOCDELAY_COUNT, 0);

                Log.i("AvaliableJob_Count", String.valueOf(delay_count));
                if(delay_count > 30){
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putBoolean(Config.TOKEN_DELAY, true);
                    editor.putInt(Config.TOKEN_LOCDELAY_COUNT, 0);
                    editor.commit();

                    Log.i("LocationService", "delay is true");


                }else{
                    delay_count++;
                    sPref.edit().putInt(Config.TOKEN_LOCDELAY_COUNT, delay_count).commit();
                    Log.i("LocationService", String.valueOf(delay_count));

                }

            }*/





        }else{

            mLastLocation = location;

            updateCurrentLocation(location);

        }

    }

    public class LocalBinder extends Binder {
        public BackgroundLocationService getServerInstance() {
            return BackgroundLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


        this.buildGoogleApiClient();

        Log.i("LocationService", "onCreate");


    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(BackgroundLocationService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
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



    public int onStartCommand (Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        mGoogleApiClient.connect();


        sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        sPref.edit().putBoolean(Config.TOKEN_SERVICE_ALIVE, true).commit();

        Log.i("LocationService", "onStartCommand");

        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy(){

        mGoogleApiClient.disconnect();

        sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        sPref.edit().putBoolean(Config.TOKEN_SERVICE_ALIVE, false).commit();
        sPref.edit().putInt(Config.TOKEN_LOCDELAY_COUNT, 0).commit();

        super.onDestroy();
    }


    private void updateCurrentLocation(final Location location){

        Long tsLong = System.currentTimeMillis();
        final String ts = tsLong.toString();
        SharedPreferences sPref = this.getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);
        final String token = sPref.getString(Config.TOKEN, null);

        JSONObject obj = new JSONObject();
        try {
            obj.put("location", location.getLongitude() + "," + location.getLatitude());
            obj.put("timestamp", ts);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = obj.toString();

        try {
            TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));


            AvaliableJobsAPI.getInstance().getService().updateLocation(token, in, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("LOCATION UPDATE SUCCESS", location.getLongitude() + "," + location.getLatitude());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("regID", "Error");
                    Log.i("regID", error.toString());
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {

        double theta = lon1 - lon2;

        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);

        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;

        if (unit == 'K') {

            dist = dist * 1.609344;

        } else if (unit == 'N') {

            dist = dist * 0.8684;

        }

        return (dist);

    }

    private double rad2deg(double rad) {

        return (rad * 180 / Math.PI);

    }

    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);

    }






}
