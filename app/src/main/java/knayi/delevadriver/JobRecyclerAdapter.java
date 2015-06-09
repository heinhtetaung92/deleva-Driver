package knayi.delevadriver;

/**
 * Created by heinhtetaung on 4/16/15.
 */
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import knayi.delevadriver.model.JobItem;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import knayi.delevadriver.model.JobItem;
import knayi.delevadriver.model.MyTypeFace;

public class JobRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private LayoutInflater mInflater;
    private List<JobItem> mItems;
    private View mHeaderView;
    private static Context mContext;
    private Activity activity;
    private String location;

    public JobRecyclerAdapter(Context context, String location, List<JobItem> items, View headerView) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mHeaderView = headerView;
        mContext = context;
        activity = (Activity) context;
        this.location = location;


    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            return mItems.size();
        } else {
            return mItems.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else {

            View v = mInflater.inflate(R.layout.custom_joblist, parent, false);

            v.setOnClickListener(this);

            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            ((ItemViewHolder) viewHolder).jobaddress.setText(mItems.get(position - 1).get_address());

            /*String type = mItems.get(position - 1).get_type();

            if(type.equals("other")){
                ((ItemViewHolder) viewHolder).jobtype.setText("Other");
            }else if(type.equals("express")){
                ((ItemViewHolder) viewHolder).jobtype.setText("Express");
            }else if(type.equals("freezen")){
                ((ItemViewHolder) viewHolder).jobtype.setText("Refrigerated");
            }*/

            //Log.i("Job Requester Name", mItems.get(position -1).get_requester_name());
            ((ItemViewHolder) viewHolder).jobtype.setText("SGD$" + mItems.get(position-1).get_price());

            /*Random rnd = new Random();

                int color = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                ((ItemViewHolder) viewHolder).jobImage.setBackgroundColor(color);*/

            if(mItems.get(position - 1).get_status().equals(Config.PENDING)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Pending");
            }
            else if(mItems.get(position - 1).get_status().equals(Config.POST)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Post");
            }
            else if(mItems.get(position - 1).get_status().equals(Config.PROCESS)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Process");
            }
            else if(mItems.get(position - 1).get_status().equals(Config.BID)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Bid");
            }
            else if(mItems.get(position - 1).get_status().equals(Config.ACTIVE)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Active");
            }
            else if(mItems.get(position - 1).get_status().equals(Config.FINISH)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Finish");
            }
            else if(mItems.get(position - 1).get_status().equals(Config.DELETE)) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Delete");
            }
            ((ItemViewHolder) viewHolder).v.setTag(position - 1);

            if(mItems.get(position-1).get_pictures() != null){



                Picasso.with(mContext)
                        .load(APIConfig.DOMAIN_URL + mItems.get(position-1).get_pictures())
                        .into(((ItemViewHolder) viewHolder).jobImage);

                Log.i("Job Image", mItems.get(position - 1).get_pictures());
            }
            else{
                Log.i("Job Image", "Is Null");
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("ItemClickLocation", String.valueOf(v.getTag()));

        if(v.getTag() != null){

            Intent intent = new Intent(mContext, JobDetailActivity.class);

            intent.putExtra("job_id", mItems.get((Integer)v.getTag()).get_id());
            intent.putExtra("location", location);
            intent.putExtra("type", "none");


            mContext.startActivity(intent);


        }

    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView jobtype, jobaddress, jobstatus;
        ImageView jobImage;
        View v;

        public ItemViewHolder(View view) {
            super(view);
            jobtype = (TextView) view.findViewById(R.id.job_type);
            jobaddress = (TextView) view.findViewById(R.id.job_address);
            jobstatus = (TextView) view.findViewById(R.id.job_status);
            jobstatus.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            jobImage = (ImageView) view.findViewById(R.id.job_image);

            jobtype.setTypeface(MyTypeFace.get(mContext, MyTypeFace.BOLD));
            jobaddress.setTypeface(MyTypeFace.get(mContext, MyTypeFace.NORMAL));
            jobstatus.setTypeface(MyTypeFace.get(mContext, MyTypeFace.BOLD));

            v = view;
        }
    }



}
