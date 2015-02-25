package knayi.delevadriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {


    TextView login, register;
    EditText username, password;
    ProgressWheel progress;
    View progressbackground;


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "1.0.1";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    GoogleCloudMessaging gcm;
    String regid;

    Context context;

    String SENDER_ID = "566326671565";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        context = getApplicationContext();






        login = (TextView) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.login_register);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);

        progressbackground = findViewById(R.id.login_progresswheel_background);

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        progressbackground.bringToFront();
        progress.bringToFront();
        login.setOnClickListener(this);
        register.setOnClickListener(this);

        SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        if(sPref.getString(Config.TOKEN, null) != null){
            startActivity(new Intent(this, TabMainActivity.class));
        }


    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask() {


            @Override
            protected Object doInBackground(Object[] params) {

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend(regid);
                    sendRegisterationToServer(regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    //storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                //
            }
        }.execute(null, null, null);

    }


    private void sendRegisterationToServer(String regID){

        if(regID == null){
            // cannot register to GCM server

            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Cannot register to GCM Server")
                    .show();

        }else{
            String uniquekey = Build.SERIAL + android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);

            JSONObject obj = new JSONObject();
            try {
                obj.put("email", username.getText().toString());
                obj.put("password", password.getText().toString());
                obj.put("uuid", uniquekey);
                obj.put("key", regID);
                obj.put("client_type", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json = obj.toString();

            Log.i("JsonOBj", json);
            try {
                TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));

                AvaliableJobsAPI.getInstance().getService().getToken(in, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {


                        try{

                            JSONObject data = new JSONObject(s);
                            if(data.getString("token") != null){
                                SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sPref.edit();
                                String token = data.getString("token");
                                editor.putString(Config.TOKEN, token);
                                editor.commit();
                                Log.i("TOKEN", token);
                            }else{
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Cannot access Account")
                                        .show();
                            }

                        }catch(JSONException exp){
                            exp.printStackTrace();
                        }

                        progress.setVisibility(View.INVISIBLE);
                        progressbackground.setVisibility(View.INVISIBLE);

                        finish();
                        startActivity(new Intent(getApplicationContext(), TabMainActivity.class));



                    }

                    @Override
                    public void failure(RetrofitError error) {

                        progress.setVisibility(View.INVISIBLE);
                        progressbackground.setVisibility(View.INVISIBLE);

                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("UserName or Password is Incorrect!")
                                .show();


                    }
                });

            }catch (UnsupportedEncodingException exp1){
                exp1.printStackTrace();
            }
        }



    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regID) {
        // Your implementation here.
        Log.i("regID", regID);
        String uniquekey = Build.SERIAL + android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        JSONObject obj = new JSONObject();
        try {
            obj.put("status", "A");
            obj.put("reg_id", regID);
            obj.put("unique_key", uniquekey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = obj.toString();

        try {
            TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));


            /*ProductsAPI.getInstance().getService().sendRegisterationID(in, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("regID", s.toString());
                    storeRegistrationId(context, regid);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("regID", "Error");
                    Log.i("regID", error.toString());
                }
            });*/

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("TAG", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("TAG", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("TAG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.login_button:

                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Please fill Name and Password")
                            .show();
                }

                else
                if(Connection.isOnline(this)){

                progress.setVisibility(View.VISIBLE);
                progressbackground.setVisibility(View.VISIBLE);


                    if (checkPlayServices()) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                        regid = getRegistrationId(this);

                        if (regid.isEmpty()) {
                            registerInBackground();
                        }
                    } else {
                        Log.i("TAG", "No valid Google Play Services APK found.");
                    }



                }
                else{
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Conneciton is loss!")
                            .show();
                }


                break;

            case R.id.login_register:
                startActivity(new Intent(this, RegisterPage.class));
                break;
        }


    }
}
