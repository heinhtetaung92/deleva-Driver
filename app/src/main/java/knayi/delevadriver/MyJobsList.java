package knayi.delevadriver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.JobItem;
import knayi.delevadriver.model.MyTypeFace;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyJobsList extends Fragment implements View.OnClickListener {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";


    View scrollview, connectionerrorview;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView retrycon;
    RecyclerView recyclerView;
    View headerView;
    SharedPreferences sPref;
    ProgressWheel progress;
    TextView tv1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        final Activity parentActivity = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        recyclerView.setHasFixedSize(false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressViewOffset(true, 0, 90);

        scrollview = view.findViewById(R.id.scroll);
        connectionerrorview = view.findViewById(R.id.connectionerrorlayout);
        retrycon = (TextView) view.findViewById(R.id.retryconnection);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        progress = (ProgressWheel) view.findViewById(R.id.progress_wheel);

        tv1.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        retrycon.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));



        retrycon.setOnClickListener(this);

        headerView = LayoutInflater.from(parentActivity).inflate(R.layout.padding, null);

        scrollview.setVisibility(View.INVISIBLE);
        connectionerrorview.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);


        if(Connection.isOnline(getActivity())){

            getDatafromServer();

        }else{
            tv1.setText("Cannot connect to Server");
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDatafromServer();
            }
        });


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

    private void getDatafromServer(){

        String token = sPref.getString(Config.TOKEN, null);

        if(token != null){

            AvaliableJobsAPI.getInstance().getService().getMyJobList(token, new retrofit.Callback<String>() {
                @Override
                public void success(String s, Response response) {

                    Log.i("APIGet", "Success");
                    ArrayList<JobItem> items = (ArrayList<JobItem>) JSONToJob(s);

                    if(items.size() <= 0){
                        tv1.setText("There are no jobs to display here!");
                        scrollview.setVisibility(View.INVISIBLE);
                        connectionerrorview.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                    }else{
                        scrollview.setVisibility(View.VISIBLE);
                        connectionerrorview.setVisibility(View.INVISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                        if(getActivity() != null) {
                            recyclerView.setAdapter(new JobRecyclerAdapter(getActivity(), null, items, headerView));
                        }
                    }


                        if(swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }



                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("APIGet", "Failuare");

                    scrollview.setVisibility(View.INVISIBLE);
                    connectionerrorview.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);

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





                    }else {

                        if (error.getBody() == null) {
                            Toast.makeText(getActivity(), "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                        } else {

                            /*String errmsg = error.getBody().toString();
                            String errcode = "";*/


                            try {
                                JSONObject errobj = new JSONObject(errmsg);

                                errcode = errobj.getJSONObject("err").getString("message");

                                Toast.makeText(getActivity(), errcode, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }



                }
            });

        }
        else{

            //no login have to handle
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }
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

        getDatafromServer();
    }
}
