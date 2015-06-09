/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package knayi.delevadriver;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.googlemap.GoogleMapActivity;
import knayi.delevadriver.model.JobItem;
import knayi.delevadriver.model.MyTypeFace;
import knayi.delevadriver.updategpsLocation.BackgroundLocationService;
import knayi.delevadriver.updategpsLocation.GpsTrackerAlarmReceiver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class JobDetailActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageView imageView;
    private View mToolbarView;
    private int mParallaxImageHeight;
    private View bitLayout;


    TextView jobprice, jobstatus, requestertitle, requesterphone, requestername, requesteraddress, receivername, receivercontact, deliveryaddress, weight, sensitivity, pickupdate, pickuptime;
    TextView job_receiver_title;
    TextView job_reject, job_bid, job_report;


    View viewforPlacing, viewforPlacing1;
    RelativeLayout relativeLayout;

    SharedPreferences sPref;

    ProgressWheel progress, imageprogress;


    String location = "";

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;


    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;



    private boolean currentlyTracking;
    private int intervalInMinutes = 5;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;

    View progress_background;

    JobItem jobitem;

    String job_id;

    List<String> weightlist, weightshowlist;
    List<Double> weightPricelist;//typepricelist;

    int weightpos = 0;//, typepos = 0;

    Double totalprice;

    boolean isFirstDialog = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobdetail);

        //typelist = Arrays.asList(new String[]{"other", "express", "freezen"});

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/ciclefina.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        toolbarText.setText("Job Details");
        toolbarText.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        setTitle("");


        sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        job_id = bundle.getString("job_id");

        Log.i("JOBID", job_id);
        Log.i("JOBID AA", sPref.getString(Config.TOKEN_JOBID, "aa"));
        Log.i("JOBID TRE", String.valueOf(sPref.getBoolean(Config.TOKEN_DELAY, false)));

        /*if(job_id.equals(sPref.getString(Config.TOKEN_JOBID, ""))){
            //check if there is delay and show dialog to report reason
            showDelayReportDialog();
        }*/

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        imageView = (ImageView) findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        //jobtype = (TextView) findViewById(R.id.jobdetail_type_value);
        //jobtype.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        jobprice = (TextView) findViewById(R.id.jobdetail_price_value);
        jobprice.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        jobstatus = (TextView) findViewById(R.id.jobdetail_status_value);
        jobstatus.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        /*jobaddress = (TextView) findViewById(R.id.jobdetail_address_value);
        jobcreatetime = (TextView) findViewById(R.id.jobdetail_createtime_value);*/
        //requestertitle = (TextView) findViewById(R.id.jobdetail_requestertitle_value);
        //requestername = (TextView) findViewById(R.id.jobdetail_requestername_value);
        //requesterbusinesstype = (TextView) findViewById(R.id.jobdetail_requesterbusinesstype_value);
        job_receiver_title = (TextView) findViewById(R.id.jobdetail_receivertitle);
        job_receiver_title.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        TextView job_requester_title = (TextView) findViewById(R.id.jobdetail_requestertitle);
        job_requester_title.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        requesterphone = (TextView) findViewById(R.id.jobdetail_requesterphone_value);
        requesterphone.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        requestername = (TextView) findViewById(R.id.jobdetail_requestername_value);
        requestername.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        requesteraddress = (TextView) findViewById(R.id.jobdetail_requesteraddress_value);
        requesteraddress.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        receivername = (TextView) findViewById(R.id.jobdetail_receiver_name_value);
        receivername.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        receivercontact = (TextView) findViewById(R.id.jobdetail_receiver_contact_value);
        receivercontact.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        /*size = (TextView) findViewById(R.id.jobdetail_size_value);
        size.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));*/
        weight = (TextView) findViewById(R.id.jobdetail_weight_value);
        weight.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        sensitivity = (TextView) findViewById(R.id.jobdetail_sensitivity_value);
        sensitivity.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        //duration = (TextView) findViewById(R.id.jobdetail_duration_value);
        bitLayout = findViewById(R.id.jobBitLayout);

        imageprogress = (ProgressWheel) findViewById(R.id.jobdetail_image_progress_wheel);
        imageprogress.bringToFront();
        viewforPlacing = findViewById(R.id.viewforplacing);
        viewforPlacing1 = findViewById(R.id.viewforplacing1);
        relativeLayout = (RelativeLayout) findViewById(R.id.jobdetail_relativelalyout);
        pickuptime = (TextView) findViewById(R.id.jobdetail_pickuptime_value);
        pickuptime.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        pickupdate = (TextView) findViewById(R.id.jobdetail_pickupdate_value);
        pickupdate.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        deliveryaddress = (TextView) findViewById(R.id.jobdetail_receiver_address_value);
        deliveryaddress.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        TextView price_title = (TextView) findViewById(R.id.jobdetail_price);
        price_title.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.websmithing.gpstracker.prefs", Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);



        setBackgroundAlpha(mToolbarView, 1.0f, getResources().getColor(R.color.primary));

        Log.i("NOTI_TYPE_DETAIL", bundle.getString("type"));

        if(bundle.getString("type").equals("job-nego-agree")){


            Log.i("NOTI", "FROM REQUEST");
            String agree = bundle.getString("agree");
            String price = bundle.getString("price");

            String agreestring;

            if(agree.equals("true")){
                agreestring = "Request was agreed with " + price;
            }else{
                agreestring = "Request was not agreed";
            }




            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .customView(R.layout.custom_message_dialog, false)
                    .positiveText("OK")
                    .positiveColor(R.color.white)
                    .positiveColorRes(R.color.white)
                    .backgroundColorRes(R.color.primary)
                    .callback(new MaterialDialog.ButtonCallback() {
                                  @Override
                                  public void onPositive(final MaterialDialog dialog) {
                                      super.onPositive(dialog);

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

            TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
            txt_title.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
            txt_message.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));

            txt_title.setText("Reply your request!");
            txt_message.setText(agreestring);

        }else{
            Log.i("NOTI", "NOT FROM REQUEST");
        }


        Log.i("JOB_Detail_ID", job_id);
        ((TextView)findViewById(R.id.jobdetail_showmap)).setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));

        findViewById(R.id.jobdetail_showmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(JobDetailActivity.this, GoogleMapActivity.class);
                intent.putExtra("JobItem", jobitem);

                startActivity(intent);
            }
        });


        progress = (ProgressWheel) findViewById(R.id.progress_wheel);
        progress_background = findViewById(R.id.detail_progresswheel_background);
        progress_background.bringToFront();
        progress.bringToFront();



        job_bid = (TextView) findViewById(R.id.job_bid);
        job_bid.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        job_reject = (TextView) findViewById(R.id.job_reject);
        job_reject.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
        job_report = (TextView) findViewById(R.id.job_report);
        job_report.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));

        job_bid.setOnClickListener(this);
        job_reject.setOnClickListener(this);
        job_report.setOnClickListener(this);

        getDataFromServer(job_id);

        progress_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);




    }


    private void getDataFromServer(String jobid){
        progress.setVisibility(View.VISIBLE);
        progress_background.setVisibility(View.VISIBLE);

        if(jobid == null){
            jobid = "";
        }

        AvaliableJobsAPI.getInstance().getService().getJobDetail(jobid, sPref.getString(Config.TOKEN, ""), new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                Log.i("Output Data", s.toString());

                try {
                    JSONObject jsonobject = new JSONObject(s);
                    if (!jsonobject.isNull("data")){


                        JSONObject jsonobj = jsonobject.getJSONObject("data");

                        jobitem = new JobItem();


                        if(!jsonobj.isNull("_id"))
                            jobitem.set_id(jsonobj.getString("_id"));

                        JSONObject requesterobj = jsonobj.getJSONObject("requester");

                        if(requesterobj != null){

                            if(!requesterobj.isNull("name")){
                                jobitem.set_requester_name(requesterobj.getString("name"));
                            }else{
                                jobitem.set_requester_name("none");
                            }

                            if(!requesterobj.isNull("email")) {
                                jobitem.set_requester_email(requesterobj.getString("email"));
                            }else{
                                jobitem.set_requester_email("none");
                            }

                            if(!requesterobj.isNull("mobile_number"))
                                jobitem.set_requester_mobile_number(requesterobj.getString("mobile_number"));

                            if(!requesterobj.isNull("address"))
                                jobitem.set_requester_address(requesterobj.getString("address"));

                                   /* if(requesterobj.getString("business_type") != null)
                                        jobitem.set_requester_business_type(requesterobj.getString("business_type"));
*/
                            if(!requesterobj.isNull("business_address"))
                                jobitem.set_requester_business_address(requesterobj.getString("business_address"));

                            JSONArray requester_pictures = requesterobj.getJSONArray("pictures");


                            List<String> pic_list = new ArrayList<String>();
                            for(int j=0;j<requester_pictures.length();j++){

                                JSONObject obj = requester_pictures.getJSONObject(j);

                                if(!obj.isNull("path"))
                                    pic_list.add(obj.getString("path"));

                            }

                        }


                        /*if(!jsonobj.isNull("type"))
                            jobitem.set_type(jsonobj.getString("type"));
                        else
                            jobitem.set_type("None");*/

                        if(!jsonobj.isNull("address"))
                            jobitem.set_address(jsonobj.getString("address"));
                        else
                            jobitem.set_address("None");

                        if(!jsonobj.isNull("receiver_name"))
                            jobitem.set_receiver_name(jsonobj.getString("receiver_name"));
                        else
                            jobitem.set_receiver_name("None");

                        if(!jsonobj.isNull("receiver_contact"))
                            jobitem.set_receiver_contact(jsonobj.getString("receiver_contact"));
                        else
                            jobitem.set_receiver_contact("None");

                        if(!jsonobj.isNull("size")){
                            jobitem.set_size(jsonobj.getString("size"));
                        }else{
                            jobitem.set_size("None");
                        }

                        if(!jsonobj.isNull("weight")){
                            jobitem.set_weight(jsonobj.getString("weight"));
                        }else{
                            jobitem.set_weight("None");
                        }

                        if(!jsonobj.isNull("sensitivity")){
                            jobitem.set_sensitivity(jsonobj.getString("sensitivity"));
                        }else{
                            jobitem.set_sensitivity("None");
                        }

                        if(!jsonobj.isNull("pickup_time")){
                            jobitem.set_pickuptime(jsonobj.getString("pickup_time"));
                        }
                        else{
                            jobitem.set_pickuptime("None");
                        }

                        if(!jsonobj.isNull("duration_text")){
                            jobitem.set_duration(jsonobj.getString("duration_text"));
                        }else{
                            jobitem.set_duration("None");
                        }

                        if(!jsonobj.isNull("post_code"))
                            jobitem.set_post_code(jsonobj.getString("post_code"));

                        if(!jsonobj.isNull("pickup_ll")) {

                            jobitem.set_pickup_lon((Double) jsonobj.getJSONArray("pickup_ll").get(0));
                            jobitem.set_pickup_lat((Double) jsonobj.getJSONArray("pickup_ll").get(1));

                        }

                        if(!jsonobj.isNull("address_ll")) {

                            jobitem.set_address_lon((Double) jsonobj.getJSONArray("address_ll").get(0));
                            jobitem.set_address_lat((Double) jsonobj.getJSONArray("address_ll").get(1));

                        }
                        if(!jsonobj.isNull("type_express")){
                            jobitem.setIsExpress(String.valueOf(jsonobj.getBoolean("type_express")));
                        }

                        if(!jsonobj.isNull("type_refigerated")){
                            jobitem.setIsRefrigerated(String.valueOf(jsonobj.getBoolean("type_refigerated")));
                        }

                        //jobitem.set_reports(jsonobj.getString("reports"));
                        //jobitem.set_rejectMessage(jsonobj.getString("rejectMessage"));

                        if(!jsonobj.isNull("status"))
                            jobitem.set_status(jsonobj.getString("status"));
                        else
                            jobitem.set_status("None");

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

                                imageprogress.setVisibility(View.VISIBLE);

                                Picasso.with(JobDetailActivity.this)
                                        .load(APIConfig.DOMAIN_URL + pic_list1.get(0))
                                        .into(imageView, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                imageprogress.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });


                            }

                        }

                        if(!jsonobj.isNull("price")) {
                            jobitem.set_price(jsonobj.getString("price"));
                            totalprice = Double.parseDouble(jsonobj.getString("price"));
                        }else{
                            jobitem.set_price("None");
                        }

                        if(!jsonobj.isNull("createAt"))
                            jobitem.set_createAt(jsonobj.getString("createAt"));
                        else{
                            jobitem.set_createAt("None");
                        }


                    }

                } catch (JSONException jsonexception) {
                    jsonexception.printStackTrace();
                    Log.i("APIData", "error");
                }


                if(Integer.parseInt(jobitem.get_weight()) <= 10){
                    weightpos = 0;
                    weightPricelist = Arrays.asList(new Double[]{0.00, 4.00, 8.00});
                    weightlist = Arrays.asList(new String[]{"le10kg", "le20kg", "le30kg"});
                    weightshowlist = Arrays.asList(new String[]{"Less than and equal 10 kg", "Less than and equal 20 kg", "Less than and equal 30 kg"});
                }else if(Integer.parseInt(jobitem.get_weight()) <= 20){
                    weightpos = 1;
                    weightPricelist = Arrays.asList(new Double[]{ -4.00, 0.00, 4.00});
                    weightlist = Arrays.asList(new String[]{"le10kg", "le20kg", "le30kg"});
                    weightshowlist = Arrays.asList(new String[]{"Less than and equal 10 kg", "Less than and equal 20 kg", "Less than and equal 30 kg"});
                }else if(Integer.parseInt(jobitem.get_weight()) <= 30){
                    weightpos = 2;
                    weightPricelist = Arrays.asList(new Double[]{-8.00, -4.00, 0.00});
                    weightlist = Arrays.asList(new String[]{"le10kg", "le20kg", "le30kg"});
                    weightshowlist = Arrays.asList(new String[]{"Less than and equal 10 kg", "Less than and equal 20 kg", "Less than and equal 30 kg"});
                }


                //Log.i("Weight", String.valueOf(Integer.parseInt(jobitem.get_weight())));


                /*String type = jobitem.get_type();

                if(type.equals("other")){
                    typepos = 0;
                    jobtype.setText("Type: Other");
                    //typepricelist = Arrays.asList(new Double[]{0.00, 3.00, 3.00});
                }else if(type.equals("express")){
                    typepos = 1;
                    jobtype.setText("Type: Express");
                    //typepricelist = Arrays.asList(new Double[]{-3.00, 0.00, 0.00});
                }else if(type.equals("freezen")){
                    typepos = 2;
                    jobtype.setText("Type: Refrigerated");
                    //typepricelist = Arrays.asList(new Double[]{-3.00, 0.00, 0.00});
                }else{
                    jobtype.setText("Type: None");
                    //typepricelist = Arrays.asList(new Double[]{0.00, 0.00, 0.00});
                }*/

                //jobtype.setText(jobitem.get_requester_name());

                if(!jobitem.get_pickuptime().equals("None")){

                    String str = jobitem.get_pickuptime();
                    String datestr = str.substring(0, 10);
                    String timestr = str.substring(11, 16);



                    pickuptime.setText("Pick Up Time: " + timestr);
                    pickupdate.setText("Pick Up Date: " + datestr);

                }else{

                    pickuptime.setText("Pick Up Time: " + jobitem.get_pickuptime());
                    pickupdate.setText("Pick Up Date: " + jobitem.get_pickuptime());
                }

                if(jobitem.get_price() != null) {
                    jobprice.setText("SGD$ " + jobitem.get_price());
                }else {
                    jobprice.setText("None");
                }

                if(jobitem.get_address() != null) {
                    deliveryaddress.setText("Delivery Address: " + jobitem.get_address());
                }else{
                    deliveryaddress.setText("None");
                }

                /*if(jobitem.get_createAt() != null) {

                    String date = getDateFromtimeFormat(jobitem.get_createAt());

                    jobcreatetime.setText("" + date);
                }
                else
                    jobcreatetime.setText("not include");
*/
                //requestername.setText("" + jobitem.get_requester());
                //requesterbusinesstype.setText("" + jobitem.get_requester_business_type());

                if(jobitem.get_requester_mobile_number() != null) {
                    requesterphone.setText("Contact: " + jobitem.get_requester_mobile_number());
                }else{
                    requesterphone.setText("None");
                }

                if(jobitem.get_requester_name() != null) {
                    requestername.setText("Name: " + jobitem.get_requester_name());
                }else{
                    requestername.setText("None");
                }

                if(jobitem.get_requester_address() != null) {
                    requesteraddress.setText("Pick Up Address: " + jobitem.get_requester_address());
                }else{
                    requesteraddress.setText("None");
                }

                if(jobitem.get_receiver_name() != null) {
                    receivername.setText("Name: " + jobitem.get_receiver_name());
                }else{
                    receivername.setText("None");
                }

                if(jobitem.get_receiver_contact() != null) {
                    receivercontact.setText("Contact: " + jobitem.get_receiver_contact());
                }else{
                    receivercontact.setText("None");
                }

                /*if(jobitem.get_size() != null) {
                    size.setText("Size: " + jobitem.get_size());
                }else{
                    size.setText("None");
                }*/

                if(jobitem.get_weight() != null) {
                    weight.setText("Weight: " + jobitem.get_weight() + " Kg");
                }else{
                    weight.setText("None");
                }

                if(jobitem.get_sensitivity() != null) {
                    sensitivity.setText("Sensitivity: " + jobitem.get_sensitivity());
                }else{
                    sensitivity.setText("None");
                }

                /*if(jobitem.get_duration() != null) {
                    duration.setText("" + jobitem.get_duration());
                }else{
                    duration.setText("None");
                }*/



                String status = jobitem.get_status();

                if(status == null){
                    job_bid.setText("None");
                }

                else if(status.equals(Config.PENDING)) {
                    jobstatus.setText("Status: Pending");

                    bitLayout.setVisibility(View.VISIBLE);

                    job_reject.setVisibility(View.INVISIBLE);
                    job_bid.setText("Bid");
                    job_report.setVisibility(View.INVISIBLE);
                    relativeLayout.removeView(viewforPlacing);

                }
                else if(jobitem.get_status().equals(Config.BID)) {
                    jobstatus.setText("Status: Bid");

                    bitLayout.setVisibility(View.VISIBLE);

                    job_bid.setText("Agree");
                    job_reject.setText("Reject");

                    job_report.setVisibility(View.VISIBLE);


                }
                else if(jobitem.get_status().equals(Config.ACTIVE)) {
                    jobstatus.setText("Status: Active");

                    bitLayout.setVisibility(View.VISIBLE);

                    job_bid.setText("Finished");

                    RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) bitLayout.getLayoutParams();

                    p.addRule(RelativeLayout.BELOW, R.id.jobdetail_showmap_layout);

                    bitLayout.setLayoutParams(p);

                    job_reject.setVisibility(View.INVISIBLE);
                    job_report.setVisibility(View.INVISIBLE);
                    relativeLayout.removeView(viewforPlacing);

                }
                else if(jobitem.get_status().equals(Config.FINISH)) {
                    jobstatus.setText("Status: Finish");

                    bitLayout.setVisibility(View.GONE);
                    job_report.setVisibility(View.INVISIBLE);
                    relativeLayout.removeView(viewforPlacing);
                    relativeLayout.removeView(viewforPlacing1);

                }
                else if(jobitem.get_status().equals(Config.DELETE)) {
                    jobstatus.setText("Status: Delete");

                    bitLayout.setVisibility(View.GONE);
                    job_report.setVisibility(View.INVISIBLE);
                    relativeLayout.removeView(viewforPlacing);
                    relativeLayout.removeView(viewforPlacing1);

                }
                else if(jobitem.get_status().equals(Config.POST)) {
                    jobstatus.setText("Status: Post");

                    bitLayout.setVisibility(View.GONE);
                    job_report.setVisibility(View.INVISIBLE);
                    relativeLayout.removeView(viewforPlacing);
                    relativeLayout.removeView(viewforPlacing1);

                }
                else if(jobitem.get_status().equals(Config.PROCESS)) {
                    jobstatus.setText("Status: Process");

                    bitLayout.setVisibility(View.VISIBLE);

                    job_bid.setText("Finished");

                    job_reject.setVisibility(View.INVISIBLE);
                    job_report.setVisibility(View.INVISIBLE);
                    relativeLayout.removeView(viewforPlacing);


                }

                progress.setVisibility(View.GONE);
                progress_background.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {

                progress.setVisibility(View.GONE);
                progress_background.setVisibility(View.GONE);

                if (error.getBody() == null) {
                    Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                } else {

                    String errmsg = error.getBody().toString();
                    String errcode = "";


                    try {
                        JSONObject errobj = new JSONObject(errmsg);

                        errcode = errobj.getJSONObject("err").getString("message");

                        Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

                Intent intent = new Intent(JobDetailActivity.this, DrawerMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                JobDetailActivity.this.finish();


            }
        } );
    }



    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        /*int baseColor = getResources().getColor(R.color.primary);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        setBackgroundAlpha(mToolbarView, alpha, baseColor);*/
        //ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }


    @Override
    public void onClick(View v) {

        Calendar calendar = Calendar.getInstance();
        final long timestamp = System.currentTimeMillis();
        final String token = sPref.getString(Config.TOKEN, null);

        switch(v.getId()){



            case R.id.job_reject:


                if(job_reject.getText().toString().equals("Reject")){




                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("")
                            .titleColor(R.color.white)
                            .customView(R.layout.reject_layout, true)
                            .positiveText("REJECT")
                            .positiveColor(R.color.white)
                            .positiveColorRes(R.color.white)
                            .negativeText("CANCEL")
                            .negativeColorRes(R.color.white)
                            .backgroundColorRes(R.color.primary)
                            .typeface("ciclefina", "ciclegordita")
                            .callback(new MaterialDialog.ButtonCallback() {
                                          @Override
                                          public void onPositive(final MaterialDialog dialog) {
                                              super.onPositive(dialog);


                                              EditText et_message = (EditText) dialog.findViewById(R.id.reject_message);


                                                  if(!et_message.getText().toString().equals("")){
                                                      AvaliableJobsAPI.getInstance().getService().rejectJob(jobitem.get_id(), token, et_message.getText().toString(), "true",
                                                              new Callback<String>() {
                                                                  @Override
                                                                  public void success(String s, Response response) {
                                                                      Toast.makeText(JobDetailActivity.this, "Job is successfully rejected", Toast.LENGTH_SHORT).show();
                                                                      dialog.dismiss();

                                                                      //remove job id that is saved to use in locatin update
                                                                      sPref.edit().putString(Config.TOKEN_JOBID, null).commit();
                                                                      sPref.edit().putString(Config.TOKEN_DELAY, null).commit();

                                                                      //check report service is alive
                                                                      //if alive stop service
                                                                      if(sPref.getBoolean(Config.TOKEN_SERVICE_ALIVE, false)){
                                                                          Intent intentservice = new Intent(JobDetailActivity.this, BackgroundLocationService.class);
                                                                          stopService(intentservice);
                                                                      }


                                                                      Intent intent = new Intent(JobDetailActivity.this, DrawerMainActivity.class);
                                                                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                      startActivity(intent);
                                                                      finish();
                                                                  }

                                                                  @Override
                                                                  public void failure(RetrofitError error) {

                                                                      if (error.getBody() == null) {
                                                                          Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                                      } else {

                                                                          String errmsg = error.getBody().toString();
                                                                          String errcode = "";


                                                                          try {
                                                                              JSONObject errobj = new JSONObject(errmsg);

                                                                              errcode = errobj.getJSONObject("err").getString("message");

                                                                              Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                                                          } catch (JSONException e) {
                                                                              e.printStackTrace();
                                                                          }



                                                                      }

                                                                      dialog.dismiss();
                                                                  }
                                                              });
                                                  }else{
                                                      Toast.makeText(JobDetailActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                                                  }


                                              progress.setVisibility(View.INVISIBLE);
                                              progress_background.setVisibility(View.INVISIBLE);


                                          }

                                          @Override
                                          public void onNegative(MaterialDialog dialog) {
                                              super.onNegative(dialog);

                                              dialog.dismiss();
                                              progress.setVisibility(View.INVISIBLE);
                                              progress_background.setVisibility(View.INVISIBLE);

                                          }
                                      }

                            )
                            .build();


                    dialog.show();

                    EditText et_message = (EditText) dialog.findViewById(R.id.reject_message);
                    et_message.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));



                }
                /*else{

                    final Dialog msgDialog = new Dialog(JobDetailActivity.this);

                    msgDialog.setTitle("Why do u reject?");
                    msgDialog.setCancelable(true);
                    msgDialog.setContentView(R.layout.custom_dialog_reason);
                    final EditText message = (EditText) msgDialog.findViewById(R.id.messagebox);
                    Button submit = (Button) msgDialog.findViewById(R.id.submitbutton);

                    msgDialog.show();

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            progress.setVisibility(View.VISIBLE);

                            progress_background.setVisibility(View.VISIBLE);

                            if(message.getText().toString().equals("") && message.getText().toString().equals(null)){
                                Toast.makeText(getApplicationContext(), "Please input message to submit", Toast.LENGTH_SHORT).show();
                            }else{
                                msgDialog.dismiss();

                                progress.setVisibility(View.VISIBLE);
                                progress_background.setVisibility(View.VISIBLE);
                                if(token == null){
                                    new SweetAlertDialog(JobDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("")
                                            .setContentText("Please Login again!")
                                            .show();
                                    finish();
                                    startActivity(new Intent(JobDetailActivity.this, LoginActivity.class));



                                }
                                else{
                                    AvaliableJobsAPI.getInstance().getService().rejectJob(jobitem.get_id(), token, location, String.valueOf(timestamp), message.getText().toString(), new Callback<String>() {
                                        @Override
                                        public void success(String s, Response response) {
                                            progress.setVisibility(View.INVISIBLE);
                                            progress_background.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(JobDetailActivity.this, TabMainActivity.class));
                                            finish();

                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            progress.setVisibility(View.INVISIBLE);
                                            progress_background.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }

                        }
                    });

                }*/







                break;

            case R.id.job_bid:


                progress.setVisibility(View.VISIBLE);
                progress_background.setVisibility(View.VISIBLE);


                if(token == null) {

                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .customView(R.layout.custom_message_dialog, false)
                            .positiveText("OK")
                            .positiveColor(R.color.white)
                            .positiveColorRes(R.color.white)
                            .backgroundColorRes(R.color.primary)
                            .typeface("ciclefina", "ciclegordita")
                            .build();
                    dialog.show();

                    TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
                    TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
                    txt_title.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
                    txt_message.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));

                    txt_title.setText("Please Login again!");
                    txt_message.setText("Server doesn't know this account!");

                    Intent intent = new Intent(JobDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }


                if(job_bid.getText().toString().equals("Bid")) {

                    final long ts1 = System.currentTimeMillis();

                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


                    if (mLastLocation != null) {
                        location = mLastLocation.getLongitude() + "," + mLastLocation.getLatitude();
                    }

                    acceptJob(token, String.valueOf(ts1), "");

                    /*MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("Estimate time of arrival to pick up point")
                            .customView(R.layout.estimatetime_layout, true)
                            .positiveText("BID")
                            .positiveColor(R.color.primary)
                            .positiveColorRes(R.color.primary)
                            .negativeText("CANCEL")
                            .negativeColorRes(R.color.primary)
                            .cancelable(false)
                            .callback(new MaterialDialog.ButtonCallback() {
                                          @Override
                                          public void onPositive(MaterialDialog dialog) {
                                              super.onPositive(dialog);

                                              EditText et_estimatetime = (EditText) dialog.findViewById(R.id.estimatetime_et);


                                              if(!et_estimatetime.getText().toString().equals("")) {
                                                  acceptJob(token, String.valueOf(ts1), et_estimatetime.getText().toString());
                                              }else{
                                                  Toast.makeText(JobDetailActivity.this, "Please Input Fields!", Toast.LENGTH_SHORT).show();
                                                  progress.setVisibility(View.INVISIBLE);
                                                  progress_background.setVisibility(View.INVISIBLE);
                                              }


                                          }

                                          @Override
                                          public void onNegative(MaterialDialog dialog) {
                                              super.onNegative(dialog);

                                              dialog.dismiss();
                                              progress.setVisibility(View.INVISIBLE);
                                              progress_background.setVisibility(View.INVISIBLE);

                                          }
                                      }

                            )
                            .build();


                    dialog.show();

                    final EditText et_estimatetime = (EditText) dialog.findViewById(R.id.estimatetime_et);

                    et_estimatetime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(JobDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    et_estimatetime.setText( selectedHour + ":" + selectedMinute);
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();
                        }
                    });*/






                 }

                else if(job_bid.getText().toString().equals("Agree")){

                    AvaliableJobsAPI.getInstance().getService().agreeJob(jobitem.get_id(), token, "true",
                            new Callback<String>() {
                                @Override
                                public void success(String s, Response response) {
                                    Toast.makeText(JobDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                    getDataFromServer(job_id);
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                    if (error.getBody() == null) {
                                        Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                    } else {

                                        String errmsg = error.getBody().toString();
                                        String errcode = "";


                                        try {
                                            JSONObject errobj = new JSONObject(errmsg);

                                            errcode = errobj.getJSONObject("err").getString("message");

                                            Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }



                                    }

                                }
                            });

                }

                else if(job_bid.getText().toString().equals("Finished")){



                    MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title("")
                            .titleColor(R.color.white)
                            .customView(R.layout.custom_request_message_dialog, true)
                            .positiveText("SEND")
                            .positiveColor(R.color.white)
                            .positiveColorRes(R.color.white)
                            .negativeText("CANCEL")
                            .negativeColorRes(R.color.white)
                            .backgroundColorRes(R.color.primary)
                            .typeface("ciclefina", "ciclegordita")
                            .callback(new MaterialDialog.ButtonCallback() {
                                          @Override
                                          public void onPositive(MaterialDialog dialog) {
                                              super.onPositive(dialog);

                                              EditText request_secret_code = (EditText) dialog.findViewById(R.id.request_secret_code);
                                              /*EditText request_msg = (EditText) dialog.findViewById(R.id.request_msg);*/

                                              if (request_secret_code != null) {

                                                  if (request_secret_code.getText().toString() != null && request_secret_code.getText().toString() != "") {

                                                      Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                                      Long tsLong = System.currentTimeMillis();
                                                      String ts = tsLong.toString();

                                                      String location = "96,16";

                                                      if (mLastLocation != null) {
                                                          location = mLastLocation.getLongitude() + "," + mLastLocation.getLatitude();
                                                      }

                                                      final long timestamp = System.currentTimeMillis();
                                                      JSONObject obj = new JSONObject();
                                                      try {
                                                          obj.put("location", location);
                                                          obj.put("timestamp", String.valueOf(timestamp));
                                                          obj.put("secret_code", request_secret_code.getText().toString());
                                                          obj.put("message", "");//request_msg.getText().toString());
                                                      } catch (JSONException e) {
                                                          e.printStackTrace();
                                                      }

                                                      String json = obj.toString();

                                                      try {
                                                          TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));

                                                          progress.setVisibility(View.VISIBLE);
                                                          progress_background.setVisibility(View.VISIBLE);

                                                          AvaliableJobsAPI.getInstance().getService().jobDone(jobitem.get_id(), token, in, new Callback<String>() {
                                                              @Override
                                                              public void success(String s, Response response) {

                                                                  Toast.makeText(JobDetailActivity.this, "Message is sent successfully", Toast.LENGTH_SHORT).show();

                                                                  sPref.edit().putString(Config.TOKEN_DELAY, null).commit();

                                                                  if(sPref.getBoolean(Config.TOKEN_SERVICE_ALIVE, false)){
                                                                      Intent intentservice = new Intent(JobDetailActivity.this, BackgroundLocationService.class);
                                                                      stopService(intentservice);
                                                                  }

                                                                  progress.setVisibility(View.INVISIBLE);
                                                                  progress_background.setVisibility(View.INVISIBLE);

                                                                  bitLayout.setVisibility(View.GONE);

                                                              }

                                                              @Override
                                                              public void failure(RetrofitError error) {

                                                                  //Toast.makeText(JobDetailActivity.this, "Failed, Please Try Again!", Toast.LENGTH_SHORT).show();

                                                                  if (error.getBody() == null) {
                                                                      Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                                  } else {

                                                                      String errmsg = error.getBody().toString();
                                                                      String errcode = "";


                                                                      try {
                                                                          JSONObject errobj = new JSONObject(errmsg);

                                                                          errcode = errobj.getJSONObject("err").getString("message");

                                                                          Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                                                      } catch (JSONException e) {
                                                                          e.printStackTrace();
                                                                      }



                                                                  }


                                                                  progress.setVisibility(View.INVISIBLE);
                                                                  progress_background.setVisibility(View.INVISIBLE);


                                                              }
                                                          });

                                                      } catch (UnsupportedEncodingException e) {
                                                          e.printStackTrace();
                                                      }

                                                  }

                                              }

                                              progress.setVisibility(View.INVISIBLE);
                                              progress_background.setVisibility(View.INVISIBLE);


                                          }

                                          @Override
                                          public void onNegative(MaterialDialog dialog) {
                                              super.onNegative(dialog);

                                              dialog.dismiss();
                                              progress.setVisibility(View.INVISIBLE);
                                              progress_background.setVisibility(View.INVISIBLE);

                                          }
                                      }

                            )
                            .build();


                            dialog.show();

                            EditText request_secret_code = (EditText) dialog.findViewById(R.id.request_secret_code);
                            /*EditText request_msg = (EditText) dialog.findViewById(R.id.request_msg);*/
                            request_secret_code.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
                            /*request_msg.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));*/

                            }


                    break;

            case R.id.job_report:

                isFirstDialog = true;

                totalprice = Double.parseDouble(jobitem.get_price());

                final MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .customView(R.layout.report_layout, true)
                        .positiveText("REPORT")
                        .positiveColor(R.color.white)
                        .positiveColorRes(R.color.white)
                        .negativeText("CANCEL")
                        .negativeColorRes(R.color.white)
                        .backgroundColorRes(R.color.primary)
                        .typeface("ciclefina", "ciclegordita")
                        .callback(new MaterialDialog.ButtonCallback() {
                                      @Override
                                      public void onPositive(final MaterialDialog dialog) {
                                          super.onPositive(dialog);

                                          CheckBox cb_weight = (CheckBox) dialog.findViewById(R.id.report_cb_overweight);
                                          CheckBox cb_express = (CheckBox) dialog.findViewById(R.id.report_cb_express);
                                          CheckBox cb_refrig = (CheckBox) dialog.findViewById(R.id.report_cb_refrigerated);
                                          //CheckBox cb_type = (CheckBox) dialog.findViewById(R.id.report_cb_type);


                                          final Spinner sp_weight = (Spinner) dialog.findViewById(R.id.report_spinner_overweight);
                                          //final Spinner sp_type = (Spinner) dialog.findViewById(R.id.report_spinner_type);

                                            EditText et_message = (EditText) dialog.findViewById(R.id.report_et_message);

                                          //Refrage

                                          if(!et_message.getText().toString().equals("") && et_message.getText().toString() != null) {

                                              if (cb_weight.isChecked() || cb_express.isChecked() || cb_refrig.isChecked()) {

                                                  String message = "";//et_message.getText().toString();

                                                  if(cb_express.isChecked()){
                                                      message += "true,";
                                                  }else{
                                                      message += "false,";
                                                  }

                                                  if(cb_refrig.isChecked()){
                                                      message += "true,";
                                                  }else{
                                                      message += "false,";
                                                  }



                                                  if (cb_weight.isChecked()) {
                                                      message += weightlist.get(sp_weight.getSelectedItemPosition()) + ",";
                                                  }else{
                                                      message += ",";
                                                  }




                                                  AvaliableJobsAPI.getInstance().getService().reportJob(jobitem.get_id(), token, message, "0",
                                                          new Callback<String>() {
                                                              @Override
                                                              public void success(String s, Response response) {
                                                                  Toast.makeText(JobDetailActivity.this, "Your report has successfully sent", Toast.LENGTH_SHORT).show();

                                                                  dialog.dismiss();
                                                              }

                                                              @Override
                                                              public void failure(RetrofitError error) {
                                                                  //Toast.makeText(JobDetailActivity.this, "Report is Failed", Toast.LENGTH_SHORT).show();

                                                                  if (error.getBody() == null) {
                                                                      Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                                  } else {

                                                                      String errmsg = error.getBody().toString();
                                                                      String errcode = "";


                                                                      try {
                                                                          JSONObject errobj = new JSONObject(errmsg);

                                                                          errcode = errobj.getJSONObject("err").getString("message");

                                                                          Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                                                      } catch (JSONException e) {
                                                                          e.printStackTrace();
                                                                      }



                                                                  }

                                                                  dialog.dismiss();
                                                              }
                                                          });
                                              } else {

                                                  Toast.makeText(JobDetailActivity.this, "Please select report reason", Toast.LENGTH_SHORT).show();
                                              }


                                          }else{
                                              Toast.makeText(JobDetailActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                                          }



                                          progress.setVisibility(View.INVISIBLE);
                                          progress_background.setVisibility(View.INVISIBLE);


                                      }

                                      @Override
                                      public void onNegative(MaterialDialog dialog) {
                                          super.onNegative(dialog);

                                          dialog.dismiss();
                                          progress.setVisibility(View.INVISIBLE);
                                          progress_background.setVisibility(View.INVISIBLE);

                                      }
                                  }

                        )
                        .build();


                dialog.show();



                final CheckBox cb_weight = (CheckBox) dialog.findViewById(R.id.report_cb_overweight);
                //final CheckBox cb_type = (CheckBox) dialog.findViewById(R.id.report_cb_type);

                CheckBox cb_express = (CheckBox) dialog.findViewById(R.id.report_cb_express);
                CheckBox cb_refrig = (CheckBox) dialog.findViewById(R.id.report_cb_refrigerated);
                EditText et_message = (EditText) dialog.findViewById(R.id.report_et_message);

                et_message.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
                cb_weight.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
                cb_express.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));
                cb_refrig.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));

                if(jobitem.getIsExpress().equals("true")){
                    cb_express.setChecked(true);
                }

                if(jobitem.getIsRefrigerated().equals("true")){
                    cb_refrig.setChecked(true);
                }

                //final TextView tv_price = (TextView) dialog.findViewById(R.id.report_price_tag);

                final Spinner sp_weight = (Spinner) dialog.findViewById(R.id.report_spinner_overweight);
                //final Spinner sp_type = (Spinner) dialog.findViewById(R.id.report_spinner_type);

                sp_weight.setEnabled(false);
                //sp_type.setEnabled(false);

                //tv_price.setText("price " + jobitem.get_price());

                sp_weight.setAdapter(new ArrayAdapter<String>(JobDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, weightshowlist));
                //sp_type.setAdapter(new ArrayAdapter<String>(JobDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Other", "Express", "Refrigerated"}));

                sp_weight.setSelection(weightpos);
                //sp_type.setSelection(typepos);

                sp_weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (!isFirstDialog){


                            totalprice += weightPricelist.get(sp_weight.getSelectedItemPosition());

                        }else{
                            isFirstDialog = false;
                        }

                        /*if(cb_type.isChecked()){
                            totalprice += typepricelist.get(position);
                        }*/

//                        tv_price.setText("price " + String.valueOf(totalprice));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                /*sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        totalprice = Double.parseDouble(jobitem.get_price());
                        if (!isFirstDialog) {
                            totalprice += typepricelist.get(position);

                        } else {
                            isFirstDialog = false;
                        }

                        if (cb_weight.isChecked()) {
                            totalprice += weightPricelist.get(sp_weight.getSelectedItemPosition());
                        }

                        tv_price.setText("price " + String.valueOf(totalprice));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });*/

                cb_weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            sp_weight.setEnabled(true);

                            totalprice += weightPricelist.get(sp_weight.getSelectedItemPosition());

                        }else{
                            sp_weight.setEnabled(false);
                            totalprice -= weightPricelist.get(sp_weight.getSelectedItemPosition());
                        }

                        //tv_price.setText("price " + String.valueOf(totalprice));
                    }
                });

                /*cb_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            sp_type.setEnabled(true);
                            totalprice += typepricelist.get(sp_type.getSelectedItemPosition());
                        }else{
                            sp_type.setEnabled(false);
                            totalprice -= typepricelist.get(sp_type.getSelectedItemPosition());
                        }
                        tv_price.setText("price " + String.valueOf(totalprice));
                    }
                });*/




                break;


            default:
                Intent intent = new Intent(JobDetailActivity.this, DrawerMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

        }
    }

    private String getDateFromtimeFormat(String timestring){

        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = sdf.parse(timestring);


            return date.toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        return timestring.substring(0, 10);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(jobItem.get_lat(), jobItem.get_lon()), 11));
    }

    protected void trackLocation() {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.websmithing.gpstracker.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        if (!checkIfGooglePlayEnabled()) {
            return;
        }

        if (currentlyTracking) {
            cancelAlarmManager();

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
        } else {
            startAlarmManager();

            currentlyTracking = true;
            editor.putBoolean("currentlyTracking", true);
            editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
            editor.putString("sessionID",  UUID.randomUUID().toString());
        }

        editor.apply();
    }


    private boolean checkIfGooglePlayEnabled() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Log.e("TAG", "unable to connect to google play services.");
            Toast.makeText(getApplicationContext(), "Google Play Service is unavaliable", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void cancelAlarmManager() {
        Log.d("TAG", "cancelAlarmManager");

        Context context = getBaseContext();
        //Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    public boolean currentlyTracking() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Log.e("TAG", "unable to connect to google play services.");
            Toast.makeText(getApplicationContext(), "Google Play Service is unavaliable", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void startAlarmManager() {
        Log.d("TAG", "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 10000, // 60000 = 1 minute
                pendingIntent);

        //startService(new Intent(JobDetailActivity.this, GPSLocationService.class));

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void showDelayReportDialog(){

        if(sPref.getBoolean(Config.TOKEN_DELAY, false)){




            /*MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Delay Report")
                    .customView(R.layout.delay_report_layout, true)
                    .positiveText("SEND")
                    .positiveColor(R.color.primary)
                    .positiveColorRes(R.color.primary)
                    .cancelable(false)
                    .typeface("ciclefina", "ciclegordita")
                    .callback(new MaterialDialog.ButtonCallback() {
                                  @Override
                                  public void onPositive(final MaterialDialog dialog) {
                                      super.onPositive(dialog);

                                      EditText editText = (EditText) dialog.findViewById(R.id.delay_reason_et);

                                      String message = editText.getText().toString();

                                      if (!message.equals("")) {

                                          sendDelayMessageToServer(message, dialog);
                                          dialog.dismiss();

                                      } else {

                                          Toast.makeText(JobDetailActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                                            showDelayReportDialog();
                                      }


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

            EditText editText = (EditText) dialog.findViewById(R.id.delay_reason_et);
            editText.setTypeface(MyTypeFace.get(JobDetailActivity.this, MyTypeFace.NORMAL));*/


        }

    }

    public void sendDelayMessageToServer(String message, final MaterialDialog dialog){

        String jobid = sPref.getString(Config.TOKEN_JOBID, null);
        String token = sPref.getString(Config.TOKEN, null);
        String ts = String.valueOf(System.currentTimeMillis());

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(jobid != null && token != null && location != null) {

            String loc = location.getLongitude() + "," + location.getLatitude();

            AvaliableJobsAPI.getInstance().getService().jobDelayReport(jobid, token, loc, ts, message,
                                  new Callback<String>() {
                                      @Override
                                      public void success(String s, Response response) {

                                          Toast.makeText(JobDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                          SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);

                                              SharedPreferences.Editor editor = sPref.edit();
                                              editor.putBoolean(Config.TOKEN_DELAY, false).commit();

                                          dialog.dismiss();
                                      }

                                      @Override
                                      public void failure(RetrofitError error) {

                                          //Toast.makeText(JobDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                                          if (error.getBody() == null) {
                                              Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                          } else {

                                              String errmsg = error.getBody().toString();
                                              String errcode = "";


                                              try {
                                                  JSONObject errobj = new JSONObject(errmsg);

                                                  errcode = errobj.getJSONObject("err").getString("message");

                                                  Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                                              } catch (JSONException e) {
                                                  e.printStackTrace();
                                              }



                                          }

                                          dialog.dismiss();
                                      }
                                  });

        }else{
            if(dialog.isShowing())
                dialog.dismiss();
        }


    }


    public void acceptJob(String token, String ts1, String time){

        AvaliableJobsAPI.getInstance().getService().acceptJob(jobitem.get_id(), token, location, ts1, time, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                progress.setVisibility(View.INVISIBLE);
                progress_background.setVisibility(View.INVISIBLE);

                currentlyTracking();

                //startAlarmManager();

                Toast.makeText(getApplicationContext(), "Your job is successfully bid.", Toast.LENGTH_SHORT).show();

                sPref.edit().putString(Config.TOKEN_JOBID, jobitem.get_id()).commit();

                Intent intentservice = new Intent(JobDetailActivity.this, BackgroundLocationService.class);
                startService(intentservice);


                Intent intent = new Intent(JobDetailActivity.this, DrawerMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.INVISIBLE);
                progress_background.setVisibility(View.INVISIBLE);

                if (error.getBody() == null) {
                    Toast.makeText(JobDetailActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                } else {

                    String errmsg = error.getBody().toString();
                    String errcode = "";


                    try {
                        JSONObject errobj = new JSONObject(errmsg);

                        errcode = errobj.getJSONObject("err").getString("message");

                        Toast.makeText(JobDetailActivity.this, errcode, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item = menu.add("Icon");
        item.setIcon(R.drawable.deleva_dispatcher_white_noeffects_04);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }
}
