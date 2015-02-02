package knayi.delevadriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {


    TextView login, register;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        login = (TextView) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.login_register);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);

        login.setOnClickListener(this);
        register.setOnClickListener(this);

        SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        if(sPref.getString(Config.TOKEN, null) != null){
            startActivity(new Intent(this, TabMainActivity.class));
        }


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.login_button:


                String uniquekey = Build.SERIAL + android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("email", username.getText().toString());
                    obj.put("password", password.getText().toString());
                    obj.put("uuid", uniquekey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String json = obj.toString();

                Log.i("JsonOBj", json);
                try {
                    TypedInput in = new TypedByteArray("application/json", json.getBytes("UTF-8"));




                    AvaliableJobsAPI.getInstance().getService().getToken(in, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {


                        try{

                            JSONObject data = new JSONObject(s);
                            if(data.getString("token") != null){
                                SharedPreferences sPref = getApplicationContext().getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sPref.edit();
                                String token = data.getString("token");
                                editor.putString(Config.TOKEN, token);
                                editor.commit();
                                Log.i("TOKEN", token);
                            }else{
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Something went wrong!")
                                        .show();
                            }

                        }catch(JSONException exp){
                            exp.printStackTrace();
                        }
                        startActivity(new Intent(getApplicationContext(), TabMainActivity.class));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Something went wrong!")
                                .show();
                    }
                });

                }catch (UnsupportedEncodingException exp1){
                    exp1.printStackTrace();
                }




                break;

            case R.id.login_register:
                startActivity(new Intent(this, RegisterPage.class));
                break;
        }


    }
}
