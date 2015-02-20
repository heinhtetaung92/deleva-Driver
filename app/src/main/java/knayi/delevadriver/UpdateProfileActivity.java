package knayi.delevadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UpdateProfileActivity extends ActionBarActivity implements View.OnClickListener {

    TextView update;
    EditText name, email, phone, address;
    ProgressWheel progress;
    String nameval, emailval, phoneval, addressval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        ActionBar actionbar = getSupportActionBar();

        if(actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        update = (TextView) findViewById(R.id.update_button);

        name = (EditText) findViewById(R.id.update_name);
        email = (EditText) findViewById(R.id.update_email);
        phone = (EditText) findViewById(R.id.update_phone);
        address = (EditText) findViewById(R.id.update_address);

        progress = (ProgressWheel) findViewById(R.id.update_progress_wheel);

        Bundle bundle = getIntent().getExtras();

        nameval = bundle.getString("name");
        emailval = bundle.getString("email");
        phoneval = bundle.getString("mobilenumber");
        addressval = bundle.getString("address");

        name.setText(nameval);
        email.setText(emailval);
        phone.setText(phoneval);
        address.setText(addressval);


        update.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        String nam = name.getText().toString();
        final String mail = email.getText().toString();
        progress.setVisibility(View.VISIBLE);
        SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        String token = sPref.getString(Config.TOKEN, null);

        if (token != null && nam != null && mail != null && phone.getText().toString() != null) {
            AvaliableJobsAPI.getInstance().getService().updateProfile(token, nam, mail, phone.getText().toString(), address.getText().toString(), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    progress.setVisibility(View.INVISIBLE);

                    new SweetAlertDialog(UpdateProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!")
                            .show();


                }
            });
        }
    }
    }
