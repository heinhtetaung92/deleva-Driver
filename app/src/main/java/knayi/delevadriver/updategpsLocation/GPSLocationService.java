package knayi.delevadriver.updategpsLocation;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import knayi.delevadriver.Config;
import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.gpslocation.GPSLocation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by heinhtetaung on 3/1/15.
 */
public class GPSLocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private boolean currentlyProcessingLocation = false;

    private Activity activity;


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;


    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GPSLocationService(String name) {
        super(name);



    }

    @Override
    public void onCreate() {
        super.onCreate();

        activity = (Activity) getApplicationContext();

        Log.i("Location Service", "Start");

        if(checkPlayServices()){

            //buildGoogleApiClient();
            mGoogleApiClient.connect();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.


        return START_NOT_STICKY;
    }



    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                //finish()
            }
            return false;
        }
        return true;
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        createLocationRequest();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();

    }

    public void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) GPSLocationService.this);

        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location  != null) {

            Long tsLong = System.currentTimeMillis();
            final String ts = tsLong.toString();
            SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
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


    }

    /**
     * Stopping location updates
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(final Location location) {

        //send data to server

        Log.i("Location Service ", "Changed");

        if(location  != null) {

            Long tsLong = System.currentTimeMillis();
            final String ts = tsLong.toString();
            SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
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


    }
}
