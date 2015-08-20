package knayi.delevadriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.MyTypeFace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {


    TextView register, forgotpassword;
    Button login;
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

    String SENDER_ID = "979085422591";

    //public static Typeface faceCicle, faceCicleBold, faceCicleItalic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        login = (Button) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.login_register);
        forgotpassword = (TextView) findViewById(R.id.login_forgotpassword);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);

        progressbackground = findViewById(R.id.login_progresswheel_background);

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);


        progressbackground.bringToFront();
        progress.bringToFront();
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        forgotpassword.setOnClickListener(this);



        //faceCicle = Typeface.createFromAsset(this.getAssets(), "fonts/ciclesemi.ttf");
        //faceCicleBold = Typeface.createFromAsset(this.getAssets(), "fonts/ciclegordita.ttf");//Cicle_Gordita.ttf
        //faceCicleItalic = Typeface.createFromAsset(this.getAssets(), "fonts/ciclesemiitalic.ttf");

        TextView text = (TextView) findViewById(R.id.text);
        text.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
        text.setTextColor(getResources().getColor(R.color.drawertextcolor));




        username.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
        password.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));

        /*username.setLetterSpacing(5);
        password.setLetterSpacing(5);*/

        login.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.BOLD));
        register.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
        forgotpassword.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));

        SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        if(sPref.getString(Config.TOKEN, null) != null){

            Intent intent = new Intent(this, DrawerMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        progressbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final String token) {

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
                    Log.i("GCM Regid", regid);

                    sendRegistrationIdToBackend(regid, token);



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




    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regID, String token) {
        // Your implementation here.

        if(regID == null){
            // cannot register to GCM server


            /*final Dialog dialog = new Dialog(this);
            dialog.setTitle("");
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_textview);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
            dTitle.setTypeface(faceCicle);
            dContentText.setTypeface(faceCicle);


            dTitle.setText("");
            dContentText.setText("Cannot register to Server");


            Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
            dialogButton.setTypeface(faceCicle);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();*/

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .backgroundColorRes(R.color.primary)
                    .customView(R.layout.custom_message_dialog, false)
                    .positiveText("OK")
                    .positiveColor(R.color.white)
                    .positiveColorRes(R.color.white)
                    .typeface("ciclefina.ttf", "ciclegordita.ttf")
                    .build();

            dialog.show();

            TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
            txt_title.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
            txt_message.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));

            txt_message.setText("Cannot register to Server");



        }else {


            Log.i("regID", regID);

            JSONObject obj = new JSONObject();
            try {
                obj.put("key", regID);
                obj.put("client_type", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json = obj.toString();

            try {
                TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));


                AvaliableJobsAPI.getInstance().getService().sendGCMRegisterID(token, in, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.i("regID", s.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getBody() == null) {
                            Toast.makeText(LoginActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                        } else {

                            String errmsg = error.getBody().toString();
                            String errcode = "";


                            try {
                                JSONObject errobj = new JSONObject(errmsg);

                                errcode = errobj.getJSONObject("err").getString("message");

                                Toast.makeText(LoginActivity.this, errcode, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    }
                });

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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


                    /*final Dialog dialog = new Dialog(this);
                    dialog.setTitle("");
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog_textview);

                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                    TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                    TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                    dTitle.setTypeface(faceCicle);
                    dContentText.setTypeface(faceCicle);

                    dTitle.setText("");
                    dContentText.setText("Please type email and password!");


                    Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                    dialogButton.setTypeface(faceCicle);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();*/

                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .backgroundColorRes(R.color.primary)
                            .customView(R.layout.custom_message_dialog, false)
                            .positiveText("OK")
                            .positiveColor(R.color.white)
                            .positiveColorRes(R.color.white)
                            .typeface("ciclefina.ttf", "ciclegordita.ttf")
                            .build();
                    dialog.show();

                    TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
                    TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
                    txt_title.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
                    txt_message.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));

                    txt_message.setText("Please type email and password!");


                }

                else
                if(Connection.isOnline(this)){

                progress.setVisibility(View.VISIBLE);
                progressbackground.setVisibility(View.VISIBLE);


                    String uniquekey = Build.SERIAL + android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                            android.provider.Settings.Secure.ANDROID_ID);

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("email", username.getText().toString());
                        obj.put("password", password.getText().toString());
                        obj.put("uuid", uniquekey);
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

                                String token = null;

                                try{

                                    JSONObject data = new JSONObject(s);
                                    if(data.getString("token") != null){
                                        SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sPref.edit();
                                        token = data.getString("token");
                                        editor.putString(Config.TOKEN, token);
                                        editor.commit();
                                        Log.i("TOKEN", token);

                                        //register to GCM
                                        if (checkPlayServices()) {
                                            gcm = GoogleCloudMessaging.getInstance(context);
                                            regid = getRegistrationId(LoginActivity.this);

                                            if (regid.isEmpty() || regid.equals("")) {
                                                registerInBackground(token);
                                            }
                                        } else {
                                            Log.i("TAG", "No valid Google Play Services APK found.");
                                        }

                                    }else{

                                        /*final Dialog dialog = new Dialog(LoginActivity.this);
                                        dialog.setTitle("");
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.custom_dialog_textview);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                                        TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                                        TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                                        dTitle.setTypeface(faceCicle);
                                        dContentText.setTypeface(faceCicle);

                                        dTitle.setText("");
                                        dContentText.setText("Sorry, but an unknown error occurred while trying to connect to server");


                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                                        dialogButton.setTypeface(faceCicle);
                                        // if button is clicked, close the custom dialog
                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog.show();*/

                                        MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
                                                .titleColor(R.color.white).contentColor(R.color.white).backgroundColorRes(R.color.dialog_background)
                                                .content("Sorry, but an unknown error occurred while trying to connect to server")
                                                .positiveText("OK")
                                                .positiveColor(R.color.white)
                                                .positiveColorRes(R.color.white)
                                                .typeface("ciclefina.ttf", "ciclegordita.ttf")
                                                .build();
                                        dialog.show();
                                    }

                                }catch(JSONException exp){
                                    exp.printStackTrace();
                                }

                                progress.setVisibility(View.INVISIBLE);
                                progressbackground.setVisibility(View.INVISIBLE);




                                Intent intent = new Intent(LoginActivity.this, DrawerMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();



                            }

                            @Override
                            public void failure(RetrofitError error) {

                                progress.setVisibility(View.INVISIBLE);
                                progressbackground.setVisibility(View.INVISIBLE);

                                if (error.getBody() == null) {
                                    Toast.makeText(LoginActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                } else {

                                    String errmsg = error.getBody().toString();
                                    String errcode = "";


                                    try {
                                        JSONObject errobj = new JSONObject(errmsg);

                                        errcode = errobj.getJSONObject("err").getString("message");

                                        Toast.makeText(LoginActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }



                                }


                            }
                        });

                    }catch (UnsupportedEncodingException exp1){
                        exp1.printStackTrace();
                    }






                }
                else{


                    /*final Dialog dialog = new Dialog(this);
                    dialog.setTitle("");
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog_textview);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                    TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                    TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                    dTitle.setTypeface(faceCicle);
                    dContentText.setTypeface(faceCicle);

                    dTitle.setText("");
                    dContentText.setText("Connection is loss!");


                    Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                    dialogButton.setTypeface(faceCicle);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();*/

                    MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
                            .backgroundColorRes(R.color.dialog_background)
                            .customView(R.layout.custom_message_dialog, false)
                            .positiveText("OK")
                            .positiveColor(R.color.white)
                            .positiveColorRes(R.color.white)
                            .typeface("ciclefina.ttf", "ciclegordita.ttf")
                            .build();
                    dialog.show();

                    TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
                    TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
                    txt_title.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
                    txt_message.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));

                    txt_message.setText("Connection is loss!");



                }


                break;

            case R.id.login_register:
                startActivity(new Intent(this, RegisterPage.class));
                break;


            case R.id.login_forgotpassword:


                /*final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setTitle("");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgot_email_layout);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                EditText dContentText = (EditText) dialog.findViewById(R.id.et_email_forgotpwd);
                dTitle.setTypeface(faceCicle);
                dContentText.setTypeface(faceCicle);

                dTitle.setText("Please send your registered email to us. We will send password to your email.");


                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                dialogButton.setTypeface(faceCicle);
                dialogButton.setText("SEND");
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et_email = (EditText) dialog.findViewById(R.id.et_email_forgotpwd);

                        if (!et_email.getText().toString().equals("")) {

                            AvaliableJobsAPI.getInstance().getService().forgetPassword(et_email.getText().toString(),
                                    new Callback<String>() {
                                        @Override
                                        public void success(String s, Response response) {
                                            Toast.makeText(LoginActivity.this, "We will send to your email soon", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            if (error.getBody() == null) {
                                                Toast.makeText(LoginActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                            } else {

                                                String errmsg = error.getBody().toString();
                                                String errcode = "";


                                                try {
                                                    JSONObject errobj = new JSONObject(errmsg);

                                                    errcode = errobj.getJSONObject("err").getString("message");

                                                    Toast.makeText(LoginActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }



                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                        }


                        dialog.dismiss();
                    }
                });




                dialog.show();*/


                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .backgroundColorRes(R.color.primary)
                        .customView(R.layout.forgot_email_layout, true)
                        .positiveText("SEND")
                        .positiveColor(R.color.white)
                        .positiveColorRes(R.color.white)
                        .typeface("ciclefina.ttf", "ciclegordita.ttf")
                        .callback(new MaterialDialog.ButtonCallback() {
                                      @Override
                                      public void onPositive(final MaterialDialog dialog) {
                                          super.onPositive(dialog);

                                          EditText et_email = (EditText) dialog.findViewById(R.id.et_email_forgotpwd);

                                          if (!et_email.getText().toString().equals("")) {

                                              AvaliableJobsAPI.getInstance().getService().forgetPassword(et_email.getText().toString(),
                                                      new Callback<String>() {
                                                          @Override
                                                          public void success(String s, Response response) {
                                                              Toast.makeText(LoginActivity.this, "We will send to your email soon", Toast.LENGTH_SHORT).show();
                                                          }

                                                          @Override
                                                          public void failure(RetrofitError error) {
                                                              if (error.getBody() == null) {
                                                                  Toast.makeText(LoginActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                              } else {

                                                                  String errmsg = error.getBody().toString();
                                                                  String errcode = "";


                                                                  try {
                                                                      JSONObject errobj = new JSONObject(errmsg);

                                                                      errcode = errobj.getJSONObject("err").getString("message");

                                                                      Toast.makeText(LoginActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                                                  } catch (JSONException e) {
                                                                      e.printStackTrace();
                                                                  }



                                                              }
                                                          }
                                                      });

                                          } else {
                                              Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                                          }


                                          dialog.dismiss();
                                      }

                                      @Override
                                      public void onNegative(MaterialDialog dialog) {
                                          super.onNegative(dialog);

                                          dialog.dismiss();

                                      }
                                  }

                        )
                        .build();


                dialog.show();
                EditText et_email = (EditText) dialog.findViewById(R.id.et_email_forgotpwd);
                TextView message = (TextView) dialog.findViewById(R.id.et_email_forgotpwd_message);
                message.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
                et_email.setTypeface(MyTypeFace.get(LoginActivity.this, MyTypeFace.NORMAL));
                message.setText("Please send your registered email to us. We will send password to your email.");

                break;
        }


    }
}
