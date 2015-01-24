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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SimpleHeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private LayoutInflater mInflater;
    private ArrayList<String> mItems;
    private View mHeaderView;

    public SimpleHeaderRecyclerAdapter(Context context, ArrayList<String> items, View headerView) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mHeaderView = headerView;
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

            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            /*((ItemViewHolder) viewHolder).jobtype.setText(mItems.get(position - 1));
            ((ItemViewHolder) viewHolder).jobaddress.setText(mItems.get(position - 1));
            ((ItemViewHolder) viewHolder).jobstatus.setText(mItems.get(position - 1));*/
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView jobtype, jobaddress, jobstatus;

        public ItemViewHolder(View view) {
            super(view);
            jobtype = (TextView) view.findViewById(R.id.job_type);
            jobaddress = (TextView) view.findViewById(R.id.job_address);
            jobstatus = (TextView) view.findViewById(R.id.job_status);

        }
    }



}
