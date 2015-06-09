package knayi.delevadriver.googlemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import knayi.delevadriver.R;
import knayi.delevadriver.model.JobItem;

public class GoogleMapActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /*private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);*/


    private static LatLng ORIGIN = null;
    private static LatLng DESTINATION = null;

    private static String ORIGIN_STRING = "";
    private static String DESTINATION_STRING = "";

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    JobItem jobItem;


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    View progressbackground;
    LocationManager locationManager;
    boolean isGPSEnable;


    public Location mLastLocation = null;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    Marker driverMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_path_google_map);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        this.buildGoogleApiClient();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }


        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();

        Bundle bundle = getIntent().getExtras();

        jobItem = bundle.getParcelable("JobItem");

        //if(jobItem.get_pickup_lon() != null) {




            ORIGIN = new LatLng((double) jobItem.get_pickup_lat(), (double) jobItem.get_pickup_lon());
            DESTINATION = new LatLng((double) jobItem.get_address_lat(), (double) jobItem.get_address_lon());

            ORIGIN_STRING = String.valueOf(jobItem.get_pickup_lat()) + "," + String.valueOf(jobItem.get_pickup_lon());
            DESTINATION_STRING = String.valueOf(jobItem.get_address_lat()) + "," + String.valueOf(jobItem.get_address_lon());

        Log.i("PickUp", String.valueOf(ORIGIN_STRING));
        Log.i("Address", String.valueOf(DESTINATION_STRING));

            MarkerOptions options = new MarkerOptions();
            options.position(ORIGIN);
            options.position(DESTINATION);
            googleMap.addMarker(options);
            String url = getMapsApiDirectionsUrl();
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ORIGIN,
                    13));
            addMarkers();


    }

    private String getMapsApiDirectionsUrl() {
        /*String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;*/

        //dest 40.7064,-74.0094
        //origin 40.722543,-73.998585
        String url1 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + ORIGIN_STRING + "&destination=" + DESTINATION_STRING + "&travelMode=google.maps.TravelMode.DRIVING";

        return url1;
    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(ORIGIN)
                    .title("First Point"));
            googleMap.addMarker(new MarkerOptions().position(DESTINATION)
                    .title("Second Point"));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {

                Log.i("JSONSTRING", jsonData[0]);

                jObject = new JSONObject(jsonData[0]);
                Log.i("JSONROUTE", jObject.toString());
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);



            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes



            if(routes==null){

                Log.i("ROUTES", "is null");

            }else{

                Log.i("ROUTES", "is not null");
                Log.i("ROUTES", String.valueOf(routes.size()));

                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);

                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(4);
                    polyLineOptions.color(Color.BLUE);
                }

                if(polyLineOptions == null){
                    Toast.makeText(GoogleMapActivity.this, "Cannot Route!", Toast.LENGTH_SHORT).show();
                }else{
                    googleMap.addPolyline(polyLineOptions);
                }



            }



        }
    }


    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequestationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(location != null){
            driverMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }


    /*private void getToken(String email, String pwd){
        AvaliableJobsAPI.getInstance().getService().getToken(email, pwd, "uuid", new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                //save to preference

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }*/

    public boolean isGPSEnabled(){

        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("Please enable device GPS");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
