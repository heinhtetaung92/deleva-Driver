package knayi.delevadriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterPage extends ActionBarActivity implements View.OnClickListener,
                                                                    GoogleApiClient.ConnectionCallbacks,
                                                                    GoogleApiClient.OnConnectionFailedListener,
                                                                    LocationListener{

    TextView register;
    EditText name, email, password, phone, address;
    ProgressWheel progress;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    View progressbackground;
    LocationManager locationManager;
    boolean isGPSEnable;

    public Location mLastLocation = null;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        register = (TextView) findViewById(R.id.register_button);

        name = (EditText) findViewById(R.id.register_name);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        phone = (EditText) findViewById(R.id.register_phone);
        address = (EditText) findViewById(R.id.register_address);


        progress = (ProgressWheel) findViewById(R.id.register_progress_wheel);
        progressbackground = findViewById(R.id.register_progresswheel_background);

        register.setOnClickListener(this);

        this.buildGoogleApiClient();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_page, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {



        if(Connection.isOnline(this)) {


            Log.i("Location", String.valueOf(mLastLocation.getLongitude()) + ", " + String.valueOf(mLastLocation.getLatitude()));

            String nam = name.getText().toString();
            final String mail = email.getText().toString();
            final String pwd = password.getText().toString();
            progress.setVisibility(View.VISIBLE);
            progressbackground.setVisibility(View.VISIBLE);

            if(nam.equals("") || mail.equals("") || pwd.equals("") || phone.getText().toString().equals("") || address.getText().toString().equals("")){
                new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Please fill all fields.")
                        .show();

                progress.setVisibility(View.INVISIBLE);
                progressbackground.setVisibility(View.INVISIBLE);

            }
            else {

                String location = null;
                if (mLastLocation != null)
                    location = String.valueOf(mLastLocation.getLongitude()) + "," + String.valueOf(mLastLocation.getLatitude());
                else {
                    location = "96, 16";
                }


                AvaliableJobsAPI.getInstance().getService().driverRegister(nam, mail, pwd, phone.getText().toString(), address.getText().toString(), location, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        //getToken(mail, pwd);
                        progress.setVisibility(View.INVISIBLE);
                        progressbackground.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.setVisibility(View.INVISIBLE);

                        progressbackground.setVisibility(View.INVISIBLE);

                        // get error and show message accrodding to error

                        if(error.getBody() == null){
                            new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong!")
                                    .show();
                        }
                        else{

                            String errmsg = error.getBody().toString();
                            String errcode = "";



                            try {
                                JSONObject errobj = new JSONObject(errmsg);

                                errcode = errobj.getJSONObject("err").getString("name");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(errcode.equals("MongoError")){

                                new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Email is already used!")
                                        .setContentText("")
                                        .show();

                            }
                            else if(errcode.equals("ValidationError")){

                                new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Email is invalid")
                                        .setContentText("")
                                        .show();

                            }
                            else{
                                new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Something went wrong!")
                                        .show();
                            }

                        }





                    }
                });
            }

        }

        else if(!isGPSEnabled()){
            showSettingsAlert();
        }else{
            new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Conneciton is loss!")
                    .show();
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
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

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
