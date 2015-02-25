package knayi.delevadriver.googlemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import knayi.delevadriver.R;

public class GoogleMapActivity extends FragmentActivity {

    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        setContentView(R.layout.activity_path_google_map);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(WALL_STREET);
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(16.8064603,96.154376),
                13));
        addMarkers();

    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        //dest 40.7064,-74.0094
        //origin 40.722543,-73.998585
        String url1 = "https://maps.googleapis.com/maps/api/directions/json?origin=16.780833,96.149722&destination=16.8064603,96.154376&travelMode=google.maps.TravelMode.DRIVING";

        return url1;
    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("Third Point"));

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
                    polyLineOptions.width(2);
                    polyLineOptions.color(Color.BLUE);
                }
            }


            googleMap.addPolyline(polyLineOptions);
        }
    }
}
