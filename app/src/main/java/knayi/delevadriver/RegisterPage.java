package knayi.delevadriver;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.camera.CropImageIntentBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.MyTypeFace;
import knayi.delevadriver.model.RoundedImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class RegisterPage extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ImageChooserListener {

    TextView register;
    EditText name, email, password, phone, address, nrc_no, vehicle; //, credittype, creditno, creditexp, creditcvv;
    ProgressWheel progress;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    View progressbackground;
    LocationManager locationManager;
    boolean isGPSEnable;

    final int CROP_PIC = 2;

    private static int REQUEST_PICTURE = 3;
    private static int REQUEST_CROP_PICTURE = 4;

    public Location mLastLocation = null;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    RoundedImageView profile_picture;

    private ImageChooserManager imageChooserManager;

    private String filePath = null;


    private int chooserType;

    CheckBox termsCheckbox;
    TextView termsLink;

    private Toolbar mToolbar;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        setSupportActionBar(mToolbar);

        toolbarTitle.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.BOLD));

        getSupportActionBar().setTitle("");
        toolbarTitle.setText("Registration");
        toolbarTitle.setTextSize(20);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        register = (TextView) findViewById(R.id.register_button);

        name = (EditText) findViewById(R.id.register_name);
        name.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        email = (EditText) findViewById(R.id.register_email);
        email.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        password = (EditText) findViewById(R.id.register_password);
        password.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        phone = (EditText) findViewById(R.id.register_phone);
        phone.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        address = (EditText) findViewById(R.id.register_address);
        address.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        profile_picture = (RoundedImageView) findViewById(R.id.register_profile_picture);
        nrc_no = (EditText) findViewById(R.id.register_nrc);
        nrc_no.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        vehicle = (EditText) findViewById(R.id.register_vehicle);
        vehicle.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));


        termsCheckbox = (CheckBox) findViewById(R.id.termscheckbox);
        termsCheckbox.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
        termsLink = (TextView) findViewById(R.id.termsLink);
        termsLink.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));


        String test = name.getText().toString();

        progress = (ProgressWheel) findViewById(R.id.register_progress_wheel);
        progressbackground = findViewById(R.id.register_progresswheel_background);
        progressbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        register.setOnClickListener(this);

        this.buildGoogleApiClient();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        /*profile_picture.setImageResource(R.drawable.blank_profile);
        profile_picture.setAdjustViewBounds(true);
        profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);*/


        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterPage.this, TermsAndConditionsActivity.class));
            }
        });

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(RegisterPage.this)
                        .backgroundColorRes(R.color.primary)
                        .customView(R.layout.image_chose_dialog, true)
                        .typeface("ciclefina.ttf", "ciclegordita.ttf")
                                //.negativeText(android.R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                //Toast.makeText(getActivity().getApplicationContext(), "Positive ".toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                            }
                        }).build();

                dialog.show();

                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Button btn_camera_chose = (Button) dialog.findViewById(R.id.btn_camera_chose);
                btn_camera_chose.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                btn_camera_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        takePicture();
                        dialog.dismiss();

                    }
                });

                Button btn_img_chose = (Button) dialog.findViewById(R.id.btn_gallery_chose);
                btn_img_chose.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                btn_img_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                        dialog.dismiss();
                    }
                });


                //positiveAction = dialog.getActionButton(DialogAction.POSITIVE);



            }
        });


    }

    private void performCropLibrary(){
        //startActivityForResult(MediaStoreUtils.getPickImageIntent(this), REQUEST_PICTURE);



        File imageFilePath = new File(filePath);

        Uri croppedImage = Uri.fromFile(imageFilePath);

        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
        cropImage.setSourceImage(croppedImage);
        startActivityForResult(cropImage.getIntent(RegisterPage.this.getApplicationContext()), REQUEST_CROP_PICTURE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.add("Icon");
        item.setIcon(R.drawable.deleva_dispatcher_white_noeffects_04);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {



        if(Connection.isOnline(this)) {


            String nam = name.getText().toString();
            final String mail = email.getText().toString();
            final String pwd = password.getText().toString();
            progress.setVisibility(View.VISIBLE);
            progressbackground.setVisibility(View.VISIBLE);

            if(nam.equals("") ||
                    mail.equals("") ||
                    pwd.equals("") ||
                    phone.getText().toString().equals("") ||
                    address.getText().toString().equals("") ||
                    nrc_no.getText().toString().equals("") ||
                    vehicle.getText().toString().equals("")
                    ){

                /*final Dialog dialog = new Dialog(this);
                dialog.setTitle("");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_textview);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                dTitle.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                dContentText.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));

                dTitle.setText("");
                dContentText.setText("Please fill all fields!");


                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                dialogButton.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();*/

                MaterialDialog dialog = new MaterialDialog.Builder(RegisterPage.this)
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
                txt_title.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                txt_message.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));

                txt_message.setText("Please fill all fields!");

                progress.setVisibility(View.INVISIBLE);
                progressbackground.setVisibility(View.INVISIBLE);

            }
            else if(!termsCheckbox.isChecked()){

                /*final Dialog dialog = new Dialog(this);
                dialog.setTitle("");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_textview);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                dTitle.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                dContentText.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));

                dTitle.setText("");
                dContentText.setText("You need to agree Terms and Condition");


                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                dialogButton.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();*/

                MaterialDialog dialog = new MaterialDialog.Builder(RegisterPage.this)
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
                txt_title.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
                txt_message.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));

                txt_message.setText("You need to agree Terms and Condition");


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

                if(filePath == null){

                    AvaliableJobsAPI.getInstance().getService().driverRegisterWithoutPicture(nam, mail, pwd, phone.getText().toString(), address.getText().toString(), location, nrc_no.getText().toString(), vehicle.getText().toString(), new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            Toast.makeText(RegisterPage.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            //getToken(mail, pwd);
                            progress.setVisibility(View.INVISIBLE);
                            progressbackground.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progress.setVisibility(View.INVISIBLE);

                            progressbackground.setVisibility(View.INVISIBLE);


                            if (error.getBody() == null) {
                                Toast.makeText(RegisterPage.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                            } else {

                                String errmsg = error.getBody().toString();
                                String errcode = "";


                                try {
                                    JSONObject errobj = new JSONObject(errmsg);

                                    errcode = errobj.getJSONObject("err").getString("message");

                                    Toast.makeText(RegisterPage.this, errcode, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            }

                            /*// get error and show message accrodding to error


                            if (error.getBody() == null) {
                                new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Something went wrong!")
                                        .show();
                            } else {

                                String errmsg = error.getBody().toString();
                                String errcode = "";


                                try {
                                    JSONObject errobj = new JSONObject(errmsg);

                                    errcode = errobj.getJSONObject("err").getString("name");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (errcode.equals("MongoError")) {

                                    new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Email is already used!")
                                            .setContentText("")
                                            .show();

                                } else if (errcode.equals("ValidationError")) {

                                    new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Invalid Values")
                                            .setContentText("Please check again!")
                                            .show();

                                } else {
                                    new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Something went wrong!")
                                            .show();
                                }

                            }*/


                        }
                    });

                }else{

                    Log.i("Image File", filePath);
                    File imagefile = new File(filePath);
                    TypedFile typedFile = new TypedFile("multipart/form-data", imagefile);


                    AvaliableJobsAPI.getInstance().getService().driverRegister(nam, mail, pwd, phone.getText().toString(), address.getText().toString(), location, typedFile, nrc_no.getText().toString(), vehicle.getText().toString(), new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {

                            Toast.makeText(RegisterPage.this, "Registration successful.", Toast.LENGTH_SHORT).show();

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

                            /*if(error.getBody() == null){
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
                                            .setTitleText("Invalid Values")
                                            .setContentText("Please check again!")
                                            .show();

                                }
                                else{
                                    new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Something went wrong!")
                                            .show();
                                }

                            }
*/

                            if (error.getBody() == null) {
                                Toast.makeText(RegisterPage.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                            } else {

                                String errmsg = error.getBody().toString();
                                String errcode = "";


                                try {
                                    JSONObject errobj = new JSONObject(errmsg);

                                    errcode = errobj.getJSONObject("err").getString("message");

                                    Toast.makeText(RegisterPage.this, errcode, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            }




                        }
                    });

                }





            }

        }

        else if(!isGPSEnabled()){
            showSettingsAlert();
        }else{

            /*final Dialog dialog = new Dialog(this);
            dialog.setTitle("");
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_textview);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
            dTitle.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
            dContentText.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));

            dTitle.setText("");
            dContentText.setText("Connection is loss!");


            Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
            dialogButton.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();*/

            MaterialDialog dialog = new MaterialDialog.Builder(RegisterPage.this)
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
            txt_title.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));
            txt_message.setTypeface(MyTypeFace.get(RegisterPage.this, MyTypeFace.NORMAL));

            txt_message.setText("Connection is loss!");


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
        alertDialog.setTitle("GPS settings");

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


    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(RegisterPage.this,
                ChooserType.REQUEST_PICK_PICTURE, "DelevaDriver", true);
        imageChooserManager.setImageChooserListener(RegisterPage.this);
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();

            //performCrop();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(RegisterPage.this,
                ChooserType.REQUEST_CAPTURE_PICTURE, "DelevaDriver", true);
        imageChooserManager.setImageChooserListener(RegisterPage.this);
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();

            //performCrop();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * this function does the crop operation.
     */
    private void performCrop() {
        // take care of exceptions

        Log.i("Perfrom Crop", filePath);
        Uri picUri = Uri.fromFile(new File(filePath));

        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");


            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra("output", picUri);





            if (Build.VERSION.SDK_INT < 19){
                cropIntent.setAction(Intent.ACTION_GET_CONTENT);
            } else {

                cropIntent.addCategory(Intent.CATEGORY_OPENABLE);
            }

            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            {
                cropIntent.setAction(Intent.ACTION_GET_CONTENT);
            }
            else
            {
                cropIntent.setAction(Intent.ACTION_PICK);
                cropIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }*/

            //cropIntent.putExtra("img_path" , picUri.toString());
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);

        } else if (requestCode == CROP_PIC) {
                // get the returned data

                if(data != null) {

                    Bundle extras = data.getExtras();
                    // get the cropped bitmap
                    Bitmap thePic = extras.getParcelable("data");
                    profile_picture.setImageBitmap(thePic);
                }


        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            profile_picture.setImageBitmap(BitmapFactory.decodeFile(new File(filePath).getAbsolutePath()));
        }
    }





    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profile_picture.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

        // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(RegisterPage.this, chooserType,
                "myfolder", true);
        imageChooserManager.setImageChooserListener(RegisterPage.this);
        imageChooserManager.reinitialize(filePath);
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {


            @Override
            public void run() {
                //pbar.setVisibility(View.GONE);
                if (image != null) {

                    //profile_picture.setImageURI(Uri.parse(new File(image
                            //.getFileThumbnail()).toString()));

                    filePath = image.getFilePathOriginal();

                    //Crop.pickImage(RegisterPage.this);
                    //beginCrop(Uri.parse(filePath));

                    //performCrop();
                    performCropLibrary();


                }
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onDestroy() {
        /*if (adView != null) {
            adView.destroy();
        }*/
        super.onDestroy();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }

            if (savedInstanceState.containsKey("media_path")) {
                filePath = savedInstanceState.getString("media_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


}
