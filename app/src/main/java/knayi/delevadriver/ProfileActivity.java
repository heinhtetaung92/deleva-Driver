package knayi.delevadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ProfileActivity extends ActionBarActivity {

    RoundedImageView profile_picture;
    TextView name, email, phone, address;

    SharedPreferences sPref;

    String nameval, emailval, phoneval, addressval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionbar = getSupportActionBar();

        if(actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        profile_picture = (RoundedImageView) findViewById(R.id.profile_picture);
        name = (TextView) findViewById(R.id.profile_name_value);
        email = (TextView) findViewById(R.id.profile_email_value);
        phone = (TextView) findViewById(R.id.profile_phone_value);
        address = (TextView) findViewById(R.id.profile_address_value);

        sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);

        profile_picture.setImageResource(R.drawable.profilesampleimage);
        profile_picture.setAdjustViewBounds(true);
        profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String token = sPref.getString(Config.TOKEN, null);

        if(token != null)
            AvaliableJobsAPI.getInstance().getService().getProfile(token, new Callback<String>() {
                @Override
                public void success(String s, Response response) {

                    try {
                        JSONObject item = new JSONObject(s);


                        nameval = item.getString("name");
                        emailval = item.getString("email");
                        phoneval = item.getString("mobile_number");
                        addressval = item.getString("address");

                        name.setText(":  " + nameval);
                        email.setText(":  " + emailval);
                        phone.setText(":  " + phoneval);
                        address.setText(":  " + addressval);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_profile, menu);

        final MenuItem edititem = menu.add(0, 14, 0, "edit");
        //menu.removeItem(12);
        edititem.setIcon(R.drawable.edit);
        edititem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItem.SHOW_AS_ACTION_ALWAYS);

        final MenuItem item = menu.add(0, 15, 0, "logout");
        //menu.removeItem(12);
        item.setIcon(R.drawable.logout);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == 15){

            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("You are going to Logout!")
                    .setConfirmText("Yes")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.dismissWithAnimation();

                            SharedPreferences.Editor editor = sPref.edit();
                            editor.putString(Config.TOKEN, null);
                            editor.commit();

                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                            finish();

                        }
                    })
                    .show();



            return true;
        }
        else if(item.getItemId() == 14){
            Intent intent = new Intent(this, UpdateProfileActivity.class);
            intent.putExtra("name", nameval);
            intent.putExtra("email", emailval);
            intent.putExtra("mobilenumber", phoneval);
            intent.putExtra("address", addressval);

            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}
