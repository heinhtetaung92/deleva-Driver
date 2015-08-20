package knayi.delevadriver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.JobItem;
import knayi.delevadriver.model.MyTypeFace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class AvaliableJobsList extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

    View scrollview, connectionerrorview;

    TextView retrycon;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    View headerView;
    ProgressWheel progress;
    SharedPreferences sPref;
    TextView tv1;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public Location mLastLocation = null, CompareLocation;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 50000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static final int REFRESH = 111112;
    public static final int NOT_REFRESH = 111113;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        final Activity parentActivity = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        /*recyclerView.setHasFixedSize(false);*/

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressViewOffset(true, 0, 90);

        scrollview = view.findViewById(R.id.scroll);
        connectionerrorview = view.findViewById(R.id.connectionerrorlayout);
        retrycon = (TextView) view.findViewById(R.id.retryconnection);
        progress = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv1.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        retrycon.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));

        retrycon.setOnClickListener(this);

        sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);


        headerView = LayoutInflater.from(parentActivity).inflate(R.layout.padding, null);

        scrollview.setVisibility(View.INVISIBLE);
        connectionerrorview.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        this.buildGoogleApiClient();

        mGoogleApiClient.connect();


        /*if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getDatafromServer(REFRESH);

            }

        });


        Log.i("Connection", String.valueOf(Connection.isOnline(getActivity())));



        /*if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified offset after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ViewTreeObserver vto = recyclerView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        recyclerView.scrollVerticallyToPosition(initialPosition);
                    }
                });
            }
            recyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }*/
        return view;
    }


    //get data from server with retrofit
    private void getDatafromServer(final int status){

        String token = sPref.getString(Config.TOKEN, null);

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();



        ;

        if(mLastLocation != null){

            final String location = String.valueOf(mLastLocation.getLongitude()) + "," + String.valueOf(mLastLocation.getLatitude());

            Log.i("avaliabletoken", token);
            Log.i("avaliableTime", ts);
            Log.i("avaliableLocation", location);

            AvaliableJobsAPI.getInstance().getService().getJobListByLocation(token, location, ts, new retrofit.Callback<String>() {

                @Override
                public void success(String s, Response response) {

                    Log.i("APIGet", "Success");
                    List<JobItem> items = JSONToJob(s);
                    Log.i("itemsize", String.valueOf(items.size()));

                    if(items.size() == 0){
                        tv1.setText("No avaliable job near you!");
                        scrollview.setVisibility(View.INVISIBLE);
                        connectionerrorview.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                    }else{
                        scrollview.setVisibility(View.VISIBLE);
                        connectionerrorview.setVisibility(View.INVISIBLE);
                        progress.setVisibility(View.INVISIBLE);


                        if(getActivity() != null) {
                            recyclerView.setAdapter(new AvaliableJobRecyclerAdapter(getActivity(), location, items, headerView));
                        }



                    }

                    if (status == REFRESH){
                        if(swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("APIGet", "Failuare");

                    tv1.setText("Cannot connect to Server");
                    scrollview.setVisibility(View.INVISIBLE);
                    connectionerrorview.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);


                    if(error.getBody() == null){

                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                .titleColor(R.color.white)
                                .customView(R.layout.custom_message_dialog, false)
                                .backgroundColorRes(R.color.primary)
                                .positiveText("OK")
                                .positiveColor(R.color.white)
                                .positiveColorRes(R.color.white)
                                .typeface("ciclefina.ttf", "ciclegordita.ttf")
                                .build();


                        dialog.show();

                        TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
                        TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
                        txt_title.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
                        txt_message.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));

                        txt_message.setText("Sorry, but an unknown error occurred while trying to connect to server.");

                        /*final Dialog dialog = new Dialog(getActivity());
                        dialog.setTitle("");
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.custom_dialog_textview);
                        dialog.setCancelable(true);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                        TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                        TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                        dTitle.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
                        dContentText.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));


                        dTitle.setText("");
                        dContentText.setText("Sorry, but an unknown error occurred while trying to connect to server.");


                        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                        dialogButton.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();*/


                    }
                    else{

                        String errmsg = error.getBody().toString();
                        String errcode = "";



                        try {
                            JSONObject errobj = new JSONObject(errmsg);

                            errcode = errobj.getJSONObject("err").getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(errcode.equals("Your are not authorized!")){

                            /*final Dialog dialog = new Dialog(getActivity());
                            dialog.setTitle("");
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.custom_dialog_textview);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                            TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                            TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                            dTitle.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
                            dContentText.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));


                            dTitle.setText("You are not Authorized!");
                            dContentText.setText("Please Login Again!");


                            Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                            dialogButton.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();*/





                            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                    .customView(R.layout.custom_message_dialog, false)
                                    .backgroundColorRes(R.color.primary)
                                    .positiveText("OK")
                                    .positiveColor(R.color.white)
                                    .positiveColorRes(R.color.white)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            SharedPreferences.Editor editor = sPref.edit();
                                            editor.putString(Config.TOKEN, null);
                                            editor.commit();

                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivityForResult(intent, 0);
                                            getActivity().finish();
                                        }
                                    })
                                    .cancelable(false)
                                    .build();


                            dialog.show();

                            TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
                            TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
                            txt_title.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
                            txt_message.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));

                            txt_message.setText("Please Login Again!");
                            txt_title.setText("You are not Authorized!");





                        }
                        else{

                            if (error.getBody() == null) {
                                Toast.makeText(getActivity(), "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                            } else {

                                String errmsg1 = error.getBody().toString();
                                String errcode1 = "";


                                try {
                                    JSONObject errobj = new JSONObject(errmsg1);

                                    errcode1 = errobj.getJSONObject("err").getString("message");

                                    Toast.makeText(getActivity(), errcode1, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            }
                        }

                    }

                    if(status == REFRESH){
                        if(swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                }



            });

        }
        else{

            tv1.setText("Please make sure GPS is enabled.");
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);

        }



    }

    @Override
    public void onResume() {
        super.onResume();

        /*final SharedPreferences sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);
        boolean is_delay = sPref.getBoolean(Config.TOKEN_ISDELAY, false);

        if(is_delay){

            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("Please Report your Late")
                    .customView(R.layout.forgot_email_layout, true)
                    .positiveText("SEND")
                    .positiveColor(R.color.primary)
                    .positiveColorRes(R.color.primary)
                    .cancelable(false)
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
                                                          Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

                                                          sPref.edit().putBoolean(Config.TOKEN_ISDELAY, false).commit();

                                                      }

                                                      @Override
                                                      public void failure(RetrofitError error) {
                                                          if (error.getBody() == null) {
                                                              Toast.makeText(getActivity(), "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                          } else {

                                                              String errmsg = error.getBody().toString();
                                                              String errcode = "";


                                                              try {
                                                                  JSONObject errobj = new JSONObject(errmsg);

                                                                  errcode = errobj.getJSONObject("err").getString("message");

                                                                  Toast.makeText(getActivity(), errcode, Toast.LENGTH_SHORT).show();

                                                              } catch (JSONException e) {
                                                                  e.printStackTrace();
                                                              }



                                                          }
                                                      }
                                                  });

                                      } else {
                                          Toast.makeText(getActivity(), "Please Input Email!", Toast.LENGTH_SHORT).show();
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
            et_email.setHint("Reason");

        }*/



    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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


    private List<JobItem> JSONToJob(String s)
    {

        Log.i("APIData", s);
        List<JobItem> arraylist = new ArrayList();
        JobItem jobitem;



        try {
        JSONObject jsonobject = new JSONObject(s);
        if (jsonobject != null){

            if (!jsonobject.isNull("data")) {

                JSONArray jsonarray = jsonobject.getJSONArray("data");
                int i = 0;
                Log.i("APIData json length", String.valueOf(jsonarray.length()));

                while (i < jsonarray.length()) {
                    jobitem = new JobItem();

                    JSONObject jsonobj = jsonarray.getJSONObject(i);

                    if(!jsonobj.isNull("_id"))
                        jobitem.set_id(jsonobj.getString("_id"));

                    /*JSONObject requesterobj = jsonobj.getJSONObject("requester");

                    if(requesterobj != null){

                        if(requesterobj.getString("email") != null)
                            jobitem.set_requester_email(requesterobj.getString("email"));

                        if(requesterobj.getString("mobile_number") != null)
                            jobitem.set_requester_mobile_number(requesterobj.getString("mobile_number"));*/
/*
                        if(requesterobj.getString("address") != null)
                            jobitem.set_requester_address(requesterobj.getString("address"));*/

                        /*if(requesterobj.getString("business_type") != null)
                            jobitem.set_requester_business_type(requesterobj.getString("business_type"));
*//*
                        if(requesterobj.getString("business_address") != null)
                            jobitem.set_requester_business_address(requesterobj.getString("business_address"));*/


                        //parsing json for requester picture
                        /*JSONArray requester_pictures = requesterobj.getJSONArray("pictures");


                        if(requester_pictures.length() >0){
                            List<String> pic_list = new ArrayList<String>();
                            for(int j=0;j<requester_pictures.length();j++){

                                JSONObject obj = requester_pictures.getJSONObject(j);

                                if(!obj.isNull("path"))
                                    pic_list.add(obj.getString("path"));

                            }

                        }



                    }*/


                    /*if(!jsonobj.isNull("type"))
                        jobitem.set_type(jsonobj.getString("type"));*/

                    if(!jsonobj.isNull("address"))
                        jobitem.set_address(jsonobj.getString("address"));
/*
                    if(jsonobj.getString("receiver_name") != null)
                        jobitem.set_receiver_name(jsonobj.getString("receiver_name"));*/

                    /*if(jsonobj.getString("receiver_contact") != null)
                        jobitem.set_receiver_contact(jsonobj.getString("receiver_contact"));

                    if(jsonobj.getString("post_code") != null)
                        jobitem.set_post_code(jsonobj.getString("post_code"));

                    if(jsonobj.getJSONArray("pickup_ll") != null) {

                        jobitem.set_pickup_lon((Double) jsonobj.getJSONArray("pickup_ll").get(0));
                        jobitem.set_pickup_lat((Double) jsonobj.getJSONArray("pickup_ll").get(1));

                    }

                    if(jsonobj.getJSONArray("address_ll") != null) {

                        jobitem.set_address_lon((Double) jsonobj.getJSONArray("address_ll").get(0));
                        jobitem.set_address_lat((Double) jsonobj.getJSONArray("address_ll").get(1));

                    }*/


                    //jobitem.set_reports(jsonobj.getString("reports"));
                    //jobitem.set_rejectMessage(jsonobj.getString("rejectMessage"));

                    if(!jsonobj.isNull("status"))
                        jobitem.set_status(jsonobj.getString("status"));

                    if(!jsonobj.isNull("pictures")) {

                        JSONArray pictures = jsonobj.getJSONArray("pictures");

                        List<String> pic_list1 = new ArrayList<String>();
                        for (int j = 0; j < pictures.length(); j++) {

                            JSONObject obj = pictures.getJSONObject(j);

                            if (obj.getString("path") != null)
                                pic_list1.add(obj.getString("path"));

                        }

                        if (pic_list1.size() > 0) {
                            jobitem.set_pictures(pic_list1.get(0));

                        }

                    }

                    if(jsonobj.getString("price") != null)
                        jobitem.set_price(jsonobj.getString("price"));

                    /*if(jsonobj.getString("createAt") != null)
                        jobitem.set_createAt(jsonobj.getString("createAt"));*/




                    arraylist.add(jobitem);


                    i++;

                }

            }

        }

        } catch (JSONException jsonexception) {
        jsonexception.printStackTrace();
            Log.i("APIData", "error");
        }

        Log.i("APIData data size", String.valueOf(arraylist.size()));



        return arraylist;
    }

    @Override
    public void onClick(View v) {

        scrollview.setVisibility(View.INVISIBLE);
        connectionerrorview.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);



        if(!Connection.isOnline(getActivity())){

            tv1.setText("Cannot connect to Server");
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }else{

            if(!mGoogleApiClient.isConnected()){
                this.buildGoogleApiClient();

                mGoogleApiClient.connect();
            }else{

                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }

        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startLocationUpdates();

        if(!Connection.isOnline(getActivity())){

            tv1.setText("Cannot connect to Server");
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }else
            getDatafromServer(NOT_REFRESH);


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        //Log.i("AvaliableJobLocation", location.toString());

        /*if(CompareLocation != null){

            double dist = distance(location.getLatitude(), location.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), 'K');

            if(dist > 0.05){

                updateCurrentLocation(location);

                CompareLocation = location;

                SharedPreferences sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putInt(Config.TOKEN_LOCDELAY_COUNT, 0);
                    editor.commit();

            }

            else if(dist < 0.05){

                SharedPreferences sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);
                int delay_count = sPref.getInt(Config.TOKEN_LOCDELAY_COUNT, 0);

                Log.i("AvaliableJob_Count", String.valueOf(delay_count));
                if(delay_count > 30){
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putBoolean(Config.TOKEN_ISDELAY, true);
                    editor.putInt(Config.TOKEN_LOCDELAY_COUNT, 0);
                    editor.commit();


                }else{
                    delay_count++;
                    sPref.edit().putInt(Config.TOKEN_LOCDELAY_COUNT, delay_count).commit();

                }

            }





        }else{

            CompareLocation = location;

            updateCurrentLocation(location);

        }*/


    }


    private void updateCurrentLocation(final Location location){

        Long tsLong = System.currentTimeMillis();
        final String ts = tsLong.toString();
        SharedPreferences sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);
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
