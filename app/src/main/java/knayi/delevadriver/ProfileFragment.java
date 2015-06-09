package knayi.delevadriver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.MyTypeFace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    RoundedImageView profile_picture;
    TextView name, email, phone, address, nrc_card, vehicle_type, changePwd;

    SharedPreferences sPref;

    String nameval, emailval, phoneval, addressval, photo, nrcval, vehicleval;

    View progress_wheel, progress_wheel_background;
    View photo_progress;

    boolean isLoadFinish = false;

    FloatingActionButton floatingbutton;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_profile, container, false);

        profile_picture = (RoundedImageView) view.findViewById(R.id.profile_picture);
        name = (TextView) view.findViewById(R.id.profile_name_value);
        email = (TextView) view.findViewById(R.id.profile_email_value);
        phone = (TextView) view.findViewById(R.id.profile_phone_value);
        address = (TextView) view.findViewById(R.id.profile_address_value);
        nrc_card = (TextView) view.findViewById(R.id.profile_nrc_value);
        vehicle_type = (TextView) view.findViewById(R.id.profile_vehicle_value);
        changePwd = (TextView) view.findViewById(R.id.profile_password_change);

        name.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        email.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        phone.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        address.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        nrc_card.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        vehicle_type.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
        changePwd.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));

        photo_progress = view.findViewById(R.id.profile_photo_progress_wheel);
        photo_progress.setVisibility(View.INVISIBLE);
        photo_progress.bringToFront();

        progress_wheel = view.findViewById(R.id.profile_progress_wheel);
        progress_wheel_background = view.findViewById(R.id.profile_progresswheel_background);

        floatingbutton = (FloatingActionButton) view.findViewById(R.id.my_profile_edit);

        progress_wheel.setVisibility(View.VISIBLE);
        progress_wheel_background.setVisibility(View.VISIBLE);


        sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);

        profile_picture.setAdjustViewBounds(true);
        profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String token = sPref.getString(Config.TOKEN, null);

        changePwd.setOnClickListener(this);

        floatingbutton.setOnClickListener(this);

        progress_wheel_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        isLoadFinish = false;

        if(token != null)
            AvaliableJobsAPI.getInstance().getService().getProfile(token, new Callback<String>() {
                @Override
                public void success(String s, Response response) {

                    Log.i("Profile", s);

                    try {
                        JSONObject item = new JSONObject(s);


                        if(!item.isNull("name"))
                            nameval = item.getString("name");

                        if(!item.isNull("email"))
                            emailval = item.getString("email");

                        if(!item.isNull("mobile_number"))
                            phoneval = item.getString("mobile_number");

                        if(!item.isNull("address"))
                            addressval = item.getString("address");

                        if(!item.isNull("id_card"))
                            nrcval = item.getString("id_card");

                        if(!item.isNull("vehicle"))
                            vehicleval = item.getString("vehicle");

                        if(!item.isNull("pictures")) {
                            JSONArray pic_array = item.getJSONArray("pictures");
                            if (pic_array.length() > 0) {

                                photo = pic_array.getJSONObject(0).getString("path");

                                photo_progress.setVisibility(View.VISIBLE);

                                Picasso.with(getActivity())
                                        .load(APIConfig.DOMAIN_URL + photo)
                                        .error(R.drawable.blank_profile)
                                        .into(profile_picture, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                photo_progress.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onError() {

                                                photo_progress.setVisibility(View.INVISIBLE);

                                                profile_picture.setImageResource(R.drawable.blank_profile);
                                                profile_picture.setAdjustViewBounds(true);
                                                profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            }
                                        });

                            } else {

                                photo = "";
                                profile_picture.setImageResource(R.drawable.blank_profile);
                                profile_picture.setAdjustViewBounds(true);
                                profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }

                        }


                        Log.i("Profile", nrcval);
                        Log.i("Profile", vehicleval);

                        if(nameval != null) {
                            name.setText("NAME: " + nameval);
                        }else{
                            name.setText("NAME: None");
                        }

                        if(emailval != null) {
                            email.setText("Email: " + emailval);
                        }else{
                            email.setText("Email: None");
                        }

                        if(phoneval != null) {
                            phone.setText("Phone: " + phoneval);
                        }else{
                            phone.setText("Phone: None");
                        }

                        if(addressval != null) {
                            address.setText("Address: " + addressval);
                        }else{
                            address.setText("Address: None");
                        }

                        if(nrcval != null) {
                            nrc_card.setText("ID Card: " + nrcval);
                        }else{
                            nrc_card.setText("ID Card: None");
                        }

                        if(vehicleval != null) {
                            vehicle_type.setText("Vehicle: " + vehicleval);
                        }else{
                            vehicle_type.setText("Vehicle: None");
                        }





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progress_wheel.setVisibility(View.INVISIBLE);
                    progress_wheel_background.setVisibility(View.INVISIBLE);

                    isLoadFinish = true;

                }

                @Override
                public void failure(RetrofitError error) {

                    if (error.getBody() == null) {
                        Toast.makeText(getActivity(), "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                    } else {

                        String errmsg = error.getBody().toString();
                        String errcode = "";


                        try {
                            JSONObject errobj = new JSONObject(errmsg);

                            errcode = errobj.getJSONObject("err").getString("message");

                            Toast.makeText(getActivity(), errcode, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                    /*Intent intent = new Intent(getActivity(), TabMainFragment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();*/
                }
            });

        return view;
    }








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        /*if(item.getItemId() == 15){

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("")
                    .content("Are you sure?")
                    .positiveText("OK")
                    .positiveColor(R.color.primary)
                    .positiveColorRes(R.color.primary)
                    .negativeText("Cancel")
                    .negativeColor(R.color.primary)
                    .negativeColorRes(R.color.primary)
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

                                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivityForResult(intent, 0);
                                            finish();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {


                                            if (error.getBody() == null) {
                                                Toast.makeText(ProfileActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                            } else {

                                                String errmsg = error.getBody().toString();
                                                String errcode = "";


                                                try {
                                                    JSONObject errobj = new JSONObject(errmsg);

                                                    errcode = errobj.getJSONObject("err").getString("message");

                                                    Toast.makeText(ProfileActivity.this, errcode, Toast.LENGTH_SHORT).show();

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



            return true;
        }*/



        return true;
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.profile_password_change){




            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .backgroundColorRes(R.color.primary)
                    .customView(R.layout.change_password_layout, true)
                    .positiveText("CHANGE")
                    .positiveColor(R.color.white)
                    .positiveColorRes(R.color.white)
                    .negativeText("CANCEL")
                    .negativeColorRes(R.color.white)
                    .typeface("ciclefina", "ciclegordita")
                    .callback(new MaterialDialog.ButtonCallback() {
                                  @Override
                                  public void onPositive(final MaterialDialog dialog) {
                                      super.onPositive(dialog);


                                      EditText et_oldpwd = (EditText) dialog.findViewById(R.id.et_oldPassword);
                                      EditText et_newpwd = (EditText) dialog.findViewById(R.id.et_newPasword);
                                      EditText et_newpwdagain = (EditText) dialog.findViewById(R.id.et_newPaswordAgain);

                                      String tok = sPref.getString(Config.TOKEN, null);

                                      if (!et_oldpwd.getText().toString().equals("") && !et_oldpwd.getText().toString().equals("") && !et_oldpwd.getText().toString().equals("")) {

                                          if (et_newpwd.getText().toString().equals(et_newpwdagain.getText().toString())) {

                                              AvaliableJobsAPI.getInstance().getService().updatePassword(tok, et_oldpwd.getText().toString(), et_newpwd.getText().toString(),
                                                      new Callback<String>() {
                                                          @Override
                                                          public void success(String s, Response response) {
                                                              Toast.makeText(getActivity(), "Password Changing is Success", Toast.LENGTH_SHORT).show();
                                                              dialog.dismiss();
                                                              Intent intent = new Intent(getActivity(), DrawerMainActivity.class);
                                                              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                              startActivity(intent);
                                                              getActivity().finish();
                                                          }

                                                          @Override
                                                          public void failure(RetrofitError error) {

                                                              if (error.getBody() == null) {
                                                                  Toast.makeText(getActivity(), "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                                                              } else {

                                                                  String errmsg = error.getBody().toString();
                                                                  String errcode = "";


                                                                  try {
                                                                      JSONObject errobj = new JSONObject(errmsg);

                                                                      errcode = errobj.getJSONObject("err").getString("message");

                                                                      Toast.makeText(getActivity(), errcode, Toast.LENGTH_SHORT).show();

                                                                  } catch (JSONException e) {
                                                                      e.printStackTrace();
                                                                  }



                                                              }

                                                              dialog.dismiss();
                                                          }
                                                      });

                                          }
                                          else{

                                              Toast.makeText(getActivity(), "New Passwords aren't same!", Toast.LENGTH_SHORT).show();

                                          }


                                      } else {
                                          Toast.makeText(getActivity(), "Please Input Fields!", Toast.LENGTH_SHORT).show();
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

            EditText et_oldpwd = (EditText) dialog.findViewById(R.id.et_oldPassword);
            EditText et_newpwd = (EditText) dialog.findViewById(R.id.et_newPasword);
            EditText et_newpwdagain = (EditText) dialog.findViewById(R.id.et_newPaswordAgain);

            et_oldpwd.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
            et_newpwd.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));
            et_newpwdagain.setTypeface(MyTypeFace.get(getActivity(), MyTypeFace.NORMAL));

        }else if(v.getId() == R.id.my_profile_edit){

            if(isLoadFinish) {

                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                intent.putExtra("name", nameval);
                intent.putExtra("email", emailval);
                intent.putExtra("mobilenumber", phoneval);
                intent.putExtra("address", addressval);
                intent.putExtra("nrc", nrcval);
                intent.putExtra("vehicle", vehicleval);
                intent.putExtra("imagepath", photo);

                startActivity(intent);
            }
            else{

                Toast.makeText(getActivity(), "Please wait to finish the loading", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
