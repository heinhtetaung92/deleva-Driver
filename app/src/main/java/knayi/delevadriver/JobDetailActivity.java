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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.JobItem;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class JobDetailActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, View.OnClickListener {

    private ImageView mImageView;
    private View mToolbarView;
    private int mParallaxImageHeight;

    private JobItem jobItem;

    TextView jobtype, jobprice, jobstatus, jobaddress, jobcreatetime, requestertitle, requestername, requesterbusinesstype, requesterphone, requesteremail, requesteraddress;
    TextView job_reject, job_bit;

    SharedPreferences sPref;

    ProgressWheel progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobdetail);

        Bundle bundle = getIntent().getExtras();
        jobItem = bundle.getParcelable("JobItem");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);

        sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);

        mImageView = (ImageView) findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        jobtype = (TextView) findViewById(R.id.jobdetail_type);
        jobprice = (TextView) findViewById(R.id.jobdetail_price);
        jobstatus = (TextView) findViewById(R.id.jobdetail_status);
        jobaddress = (TextView) findViewById(R.id.jobdetail_address);
        jobcreatetime = (TextView) findViewById(R.id.jobdetail_createtime);
        requestertitle = (TextView) findViewById(R.id.jobdetail_requestertitle);
        requestername = (TextView) findViewById(R.id.jobdetail_requestername);
        requesterbusinesstype = (TextView) findViewById(R.id.jobdetail_requesterbusinesstype);
        requesterphone = (TextView) findViewById(R.id.jobdetail_requesterphone);
        requesteremail = (TextView) findViewById(R.id.jobdetail_requesteremail);
        requesteraddress = (TextView) findViewById(R.id.jobdetail_requesteraddress);

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);
        progress.bringToFront();

        job_bit = (TextView) findViewById(R.id.job_bit);
        job_reject = (TextView) findViewById(R.id.job_reject);

        job_bit.setOnClickListener(this);
        job_reject.setOnClickListener(this);


        setBackgroundAlpha(mToolbarView, 0, getResources().getColor(R.color.primary));

        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        mImageView.setImageResource(R.drawable.download);


        jobtype.setText("Type : " + jobItem.get_type());
        jobprice.setText("Price : $" + jobItem.get_price());
        jobaddress.setText("Address : " + jobItem.get_address());

        String date = getDateFromtimeFormat(jobItem.get_createAt());

        if(date != null)
            jobcreatetime.setText("CreateTime : " + date);
        else
            jobcreatetime.setText("CreateTime : not include");

        requestertitle.setText("Requester");
        requestername.setText("Name : " + jobItem.get_requester().get_name());
        requesterbusinesstype.setText("Business : " + jobItem.get_requester().get_business_type());
        requesterphone.setText("Phone : " + jobItem.get_requester().get_mobile_number());
        requesteremail.setText("Email : " + jobItem.get_requester().get_email());
        requesteraddress.setText("Address : " + jobItem.get_requester().get_address());


        if(jobItem.get_status().equals("P")) {
            jobstatus.setText("Status : Pending");
        }
        else if(jobItem.get_status().equals("A")) {
            jobstatus.setText("Status : Accepted");
        }
        else if(jobItem.get_status().equals("F")) {
            jobstatus.setText("Status : Finish");
        }
        else if(jobItem.get_status().equals("D")) {
            jobstatus.setText("Status : Delete");
        }

    }



    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        setBackgroundAlpha(mToolbarView, alpha, baseColor);
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
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

        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();
        final String token = sPref.getString(Config.TOKEN, null);
        final String location = GPSLocation.getLocation(this);

        switch(v.getId()){



            case R.id.job_reject:



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

                        if(message.getText().toString().equals("") && message.getText().toString().equals(null)){
                            Toast.makeText(getApplicationContext(), "Please input message to submit", Toast.LENGTH_SHORT).show();
                        }else{
                            msgDialog.dismiss();
                            progress.setVisibility(View.VISIBLE);
                            if(token == null){
                                 new SweetAlertDialog(JobDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("")
                                        .setContentText("Please Login again!")
                                        .show();

                                startActivity(new Intent(JobDetailActivity.this, LoginActivity.class));
                                finish();


                            }
                            else{
                                AvaliableJobsAPI.getInstance().getService().rejectJob(jobItem.get_id(), token, location, ts, message.getText().toString(), new Callback<String>() {
                                    @Override
                                    public void success(String s, Response response) {
                                        progress.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(JobDetailActivity.this, TabMainActivity.class));
                                        finish();

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                    }
                });




                break;

            case R.id.job_bit:


                progress.setVisibility(View.VISIBLE);


                if(token == null){
                    new SweetAlertDialog(JobDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("")
                            .setContentText("Please Login again!")
                            .show();

                    startActivity(new Intent(JobDetailActivity.this, LoginActivity.class));
                    finish();
                }
                else{
                    AvaliableJobsAPI.getInstance().getService().acceptJob(jobItem.get_id(), token, location, ts, new Callback<String>() {
                        @Override
                        public void success(String s, Response response) {
                            progress.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(JobDetailActivity.this, TabMainActivity.class));
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


                break;

            default:
                JobDetailActivity.this.finish();
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

}
