package knayi.delevadriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.MyTypeFace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DrawerMainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, UpdateProfileActivity.onUpdateListener {

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    List<String> draweritemlist;
    TextView toolbarTitle;

    Bundle savedInstanceState;
    int selection = 0;
    DrawerListAdapter adp;
    SharedPreferences sPref;
    boolean isDrawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_drawer_main);

        sPref = getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        setSupportActionBar(mToolbar);


        toolbarTitle.setTypeface(MyTypeFace.get(DrawerMainActivity.this, MyTypeFace.BOLD));


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerList = (ListView) findViewById(R.id.drawerlist);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name){
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset > .55 && !isDrawerOpen){
                    onDrawerOpened(drawerView);
                    isDrawerOpen = true;
                } else if(slideOffset < .45 && isDrawerOpen) {
                    onDrawerClosed(drawerView);
                    isDrawerOpen = false;
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        draweritemlist = Arrays.asList(new String[]{"Jobs", "Profile", "Pricing Details", "Contact Us", "About Us", "Log Out"});
        adp = new DrawerListAdapter(this, R.layout.drawerlist_item, draweritemlist);
        mDrawerList.setAdapter(adp);
        mDrawerList.setOnItemClickListener(this);

        Intent intent = getIntent();

        setselectedFragment(intent.getIntExtra("selection", 0));

    }


    private void setselectedFragment(int position){

        FragmentManager fragmentManager = getSupportFragmentManager();
        setTitle(draweritemlist.get(position));


        switch (position) {


           case 1:

                ProfileFragment profilefrag;
                if(this.savedInstanceState != null){
                    profilefrag = (ProfileFragment) fragmentManager.findFragmentByTag(draweritemlist.get(position));
                }
                else {
                    profilefrag = new ProfileFragment();

                }
                fragmentManager.beginTransaction().replace(R.id.container, profilefrag, draweritemlist.get(position)).commit();

                break;

             case 2:

                 Log.i("DrawerMain", "PriceCategoryActivity");
                PriceCategoryActivity informationfrag;
                if(this.savedInstanceState != null){
                    informationfrag = (PriceCategoryActivity) fragmentManager.findFragmentByTag(draweritemlist.get(position));
                }else{
                    informationfrag = new PriceCategoryActivity();
                }

                fragmentManager.beginTransaction().replace(R.id.container, informationfrag, draweritemlist.get(position)).commit();

                break;

            case 3:

                ContactUsFragment contactfrag;

                if(this.savedInstanceState != null){
                    contactfrag = (ContactUsFragment) fragmentManager.findFragmentByTag(draweritemlist.get(position));
                }else{
                    contactfrag = new ContactUsFragment();
                }

                fragmentManager.beginTransaction().replace(R.id.container, contactfrag).commit();
                break;

            case 4:
                AboutUsFragment aboutusfrag;

                if(this.savedInstanceState != null){
                    aboutusfrag = (AboutUsFragment) fragmentManager.findFragmentByTag(draweritemlist.get(position));
                }else{
                    aboutusfrag = new AboutUsFragment();
                }

                fragmentManager.beginTransaction().replace(R.id.container, aboutusfrag).commit();
                break;

            case 5:


                /*final Dialog dialog = new Dialog(this);
                dialog.setTitle("");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_textview);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
                dTitle.setTypeface(LoginActivity.faceCicle);
                dContentText.setTypeface(LoginActivity.faceCicle);

                dTitle.setText("Log Out");
                dContentText.setText("You have logged out.\n" +
                        "Thank you for choosing us!");


                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
                dialogButton.setTypeface(LoginActivity.faceCicle);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AvaliableJobsAPI.getInstance().getService().Logout(sPref.getString(Config.TOKEN, ""),
                                new Callback<String>() {
                                    @Override
                                    public void success(String s, Response response) {

                                        SharedPreferences.Editor editor = sPref.edit();
                                        editor.putString(Config.TOKEN, null);
                                        editor.commit();

                                        Intent intent = new Intent(DrawerMainActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivityForResult(intent, 0);
                                        finish();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {


                                        if (error.getBody() == null) {
                                            Toast.makeText(DrawerMainActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                        } else {

                                            String errmsg = error.getBody().toString();
                                            String errcode = "";


                                            try {
                                                JSONObject errobj = new JSONObject(errmsg);

                                                errcode = errobj.getJSONObject("err").getString("message");


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }
                                });

                        dialog.dismiss();
                    }
                });

                dialog.show();*/


                 MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .customView(R.layout.custom_message_dialog, false)
                    .positiveText("OK")
                    .positiveColorRes(android.R.color.white)
                    .backgroundColorRes(R.color.primary)
                    .typeface("ciclefina.ttf", "ciclegordita.ttf")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            super.onPositive(dialog);

                            AvaliableJobsAPI.getInstance().getService().Logout(sPref.getString(Config.TOKEN, ""),
                                    new Callback<String>() {
                                        @Override
                                        public void success(String s, Response response) {

                                            SharedPreferences.Editor editor = sPref.edit();
                                            editor.putString(Config.TOKEN, null);
                                            editor.commit();

                                            Intent intent = new Intent(DrawerMainActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivityForResult(intent, 0);
                                            finish();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {


                                            if (error.getBody() == null) {
                                                Toast.makeText(DrawerMainActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                            } else {

                                                String errmsg = error.getBody().toString();
                                                String errcode = "";


                                                try {
                                                    JSONObject errobj = new JSONObject(errmsg);

                                                    errcode = errobj.getJSONObject("err").getString("message");


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }

                                        }
                                    });

                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                        }
                    })
                    .build();
            dialog.show();


                TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
                TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
                txt_title.setTypeface(MyTypeFace.get(DrawerMainActivity.this, MyTypeFace.NORMAL));
                txt_message.setTypeface(MyTypeFace.get(DrawerMainActivity.this, MyTypeFace.NORMAL));
                txt_title.setText("Log Out");
                txt_message.setText("You have logged out.\n" +
                        "Thank you for choosing us!");



                break;

            case 0:
            default:
                TabMainFragment mainfrag;
                if(this.savedInstanceState != null){
                    mainfrag = (TabMainFragment) fragmentManager.findFragmentByTag(draweritemlist.get(position));
                }
                else {
                    mainfrag = new TabMainFragment();

                }
                fragmentManager.beginTransaction().replace(R.id.container, mainfrag, draweritemlist.get(position)).commit();
                break;


        }

        mDrawerList.setItemChecked(position, true);

        mDrawerLayout.closeDrawer(mDrawerList);

    }

    private void setTitle(String title){

        if(title.equals("Log Out")){

        }
        else if(title.equals("Jobs")){
            getSupportActionBar().setTitle("");
            toolbarTitle.setText("Deleva Dispatcher");
            toolbarTitle.setTextSize(20);
        }else{
            getSupportActionBar().setTitle("");
            toolbarTitle.setText(title);
            toolbarTitle.setTextSize(20);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selection = position;
        adp.notifyDataSetChanged();

        setselectedFragment(position);
    }

    @Override
    public void onProfileUpdated() {
        setselectedFragment(1);
        selection = 1;
    }


    public class DrawerListAdapter extends BaseAdapter {

        private Context mContext;
        private int mLayout;
        private List mItems;

        public DrawerListAdapter(Context context, int layoutid, List<String> items){

            mContext  = context;
            mLayout = layoutid;
            mItems = items;

        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){

                LayoutInflater inflater = LayoutInflater.from(mContext);

                convertView = inflater.inflate(mLayout, parent, false);

            }

            TextView textView = (TextView) convertView.findViewById(R.id.drawer_textview);

            textView.setText(String.valueOf(mItems.get(position)));
            textView.setTypeface(MyTypeFace.get(DrawerMainActivity.this, MyTypeFace.NORMAL));

            if(selection < getCount() && selection == position){
                textView.setBackgroundColor(getResources().getColor(R.color.tranprimary));
                textView.setTextColor(getResources().getColor(android.R.color.white));
            }else{
                textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                textView.setTextColor(getResources().getColor(R.color.drawertextcolor));
            }




            return convertView;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.menu_drawer_main, menu);
        MenuItem item = menu.add("Icon");
        item.setIcon(R.drawable.deleva_dispatcher_white_noeffects_04);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
