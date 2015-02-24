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

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import knayi.delevadriver.model.JobItem;

public class SimpleHeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private LayoutInflater mInflater;
    private List<JobItem> mItems;
    private View mHeaderView;
    private static Context mContext;
    private Activity activity;
    private String location;

    public SimpleHeaderRecyclerAdapter(Context context, String location, List<JobItem> items, View headerView) {
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
            ((ItemViewHolder) viewHolder).jobtype.setText(mItems.get(position - 1).get_type());
            ((ItemViewHolder) viewHolder).jobaddress.setText(mItems.get(position - 1).get_address());

            if(mItems.get(position - 1).get_status().equals("P")) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Pending");
            }
            else if(mItems.get(position - 1).get_status().equals("A")) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Active");
            }
            else if(mItems.get(position - 1).get_status().equals("F")) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Finish");
            }
            else if(mItems.get(position - 1).get_status().equals("D")) {
                ((ItemViewHolder) viewHolder).jobstatus.setText("Delete");
            }
            ((ItemViewHolder) viewHolder).v.setTag(position - 1);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("ItemClickLocation", String.valueOf(v.getTag()));

        if(v.getTag() != null){

            Intent intent = new Intent(mContext, JobDetailActivity.class);

            intent.putExtra("JobItem", mItems.get((Integer)v.getTag()));
            intent.putExtra("location", location);


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
        View v;

        public ItemViewHolder(View view) {
            super(view);
            jobtype = (TextView) view.findViewById(R.id.job_type);
            jobaddress = (TextView) view.findViewById(R.id.job_address);
            jobstatus = (TextView) view.findViewById(R.id.job_status);
            jobstatus.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            v = view;
        }
    }



}
