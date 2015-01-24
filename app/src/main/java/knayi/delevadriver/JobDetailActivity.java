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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;

import knayi.delevadriver.model.JobItem;

public class JobDetailActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, View.OnClickListener {

    private View mImageView;
    private View mToolbarView;
    private int mParallaxImageHeight;

    private JobItem jobItem;

    TextView jobtype, jobprice, jobstatus, jobaddress, jobcreatetime, requestertitle, requestername, requesterbusinesstype, requesterphone, requesteremail, requesteraddress;

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

        mImageView = findViewById(R.id.image);
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


        setBackgroundAlpha(mToolbarView, 0, getResources().getColor(R.color.primary));

        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        jobtype.setText("Type : " + jobItem.get_type());
        jobprice.setText("Price : $" + jobItem.get_price());
        jobstatus.setText("Status : " + jobItem.get_status());
        jobaddress.setText("Address : " + jobItem.get_address());
        jobcreatetime.setText("CreateTime : " + jobItem.get_createAt());
        requestertitle.setText("Requester");
        requestername.setText("Name : " + jobItem.get_requester().get_name());
        requesterbusinesstype.setText("Business : " + jobItem.get_requester().get_business_type());
        requesterphone.setText("Phone : " + jobItem.get_requester().get_mobile_number());
        requesteremail.setText("Email : " + jobItem.get_requester().get_email());
        requesteraddress.setText("Address : " + jobItem.get_requester().get_address());

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
    protected void onPause() {
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        this.finish();

    }
}
