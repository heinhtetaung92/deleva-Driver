package knayi.delevadriver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.JobItem;
import knayi.delevadriver.model.Requester;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AvaliableJobsList extends Fragment implements View.OnClickListener {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

    View scrollview, connectionerrorview;

    TextView retrycon;
    ObservableRecyclerView recyclerView;
    View headerView;
    ProgressWheel progress;
    SharedPreferences sPref;
    TextView tv1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        final Activity parentActivity = getActivity();
        recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        recyclerView.setHasFixedSize(false);

        scrollview = view.findViewById(R.id.scroll);
        connectionerrorview = view.findViewById(R.id.connectionerrorlayout);
        retrycon = (TextView) view.findViewById(R.id.retryconnection);
        progress = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        tv1 = (TextView) view.findViewById(R.id.tv1);

        retrycon.setOnClickListener(this);

        sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);


        headerView = LayoutInflater.from(parentActivity).inflate(R.layout.padding, null);

        scrollview.setVisibility(View.INVISIBLE);
        connectionerrorview.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);




        Log.i("Connection", String.valueOf(Connection.isOnline(getActivity())));

        if(Connection.isOnline(getActivity())){
            getDatafromServer();

        }else{

            tv1.setText("Cannot connect to Server");
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }



        if (parentActivity instanceof ObservableScrollViewCallbacks) {
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
        }
        return view;
    }


    //get data from server with retrofit
    private void getDatafromServer(){

        String token = sPref.getString(Config.TOKEN, null);

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String location = GPSLocation.getLocation(getActivity());

        if(token != null && location != null){
            AvaliableJobsAPI.getInstance().getService().getJobListByLocation(token, "96.1458786,16.8037958", ts, new retrofit.Callback<String>() {
                @Override
                public void success(String s, Response response) {

                    Log.i("APIGet", "Success");
                    ArrayList<JobItem> items = (ArrayList<JobItem>) JSONToJob(s);
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
                        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(getActivity(), items, headerView));
                    }


                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("APIGet", "Failuare");

                    scrollview.setVisibility(View.INVISIBLE);
                    connectionerrorview.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);


                }
            });
        }
        else{
            scrollview.setVisibility(View.INVISIBLE);
            connectionerrorview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }



    }


    private List JSONToJob(String s)
    {

        Log.i("APIData", s);
        ArrayList arraylist;
        JobItem jobitem;
        Requester requester;
        arraylist = new ArrayList();

        requester = new Requester();


        try {
        JSONObject jsonobject = new JSONObject(s);
        if (jsonobject != null){

            if (!jsonobject.isNull("data")) {

                JSONArray jsonarray = jsonobject.getJSONArray("data");
                int i = 0;
                Log.i("APIData json length", String.valueOf(jsonarray.length()));

                while (i < jsonarray.length()) {
                    jobitem = new JobItem();
                    JSONObject jsonobject1;
                    JSONArray jsonarray1;
                    jsonobject1 = jsonarray.getJSONObject(i);

                    jobitem.set_id(jsonobject1.getString("_id"));
                    jobitem.set_type(jsonobject1.getString("type"));
                    jobitem.set_address(jsonobject1.getString("address"));
                    jobitem.set_price(jsonobject1.getInt("price"));
                    jobitem.set_status(jsonobject1.getString("status"));
                    jobitem.set_createAt(jsonobject1.getString("createAt"));
                    jobitem.set_latAndlan(jsonobject1.getJSONArray("address_ll").getLong(0), jsonobject1.getJSONArray("address_ll").getLong(1));


                    JSONObject jsonobject2 = jsonobject1.getJSONObject("requester");
                    if (jsonobject2 != null) {

                        Log.i("APIData requester jsonobject", jsonobject2.toString());
                        requester.set_id(jsonobject2.getString("_id"));
                        requester.set_type(jsonobject2.getString("type"));
                        requester.set_name(jsonobject2.getString("name"));
                        requester.set_email(jsonobject2.getString("email"));
                        requester.set_business_type(jsonobject2.getString("business_type"));
                        requester.set_mobile_number(jsonobject2.getString("mobile_number"));
                        requester.set_address(jsonobject2.getString("address"));
                        requester.set_confirmed(jsonobject2.getString("confirmed"));
                        requester.set_latAndlan(jsonobject2.getJSONArray("business_address_ll").getLong(0), jsonobject2.getJSONArray("business_address_ll").getLong(1));



                    }
                    jobitem.set_requester(requester);


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
