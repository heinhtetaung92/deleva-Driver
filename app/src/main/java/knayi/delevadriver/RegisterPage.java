package knayi.delevadriver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterPage extends ActionBarActivity implements View.OnClickListener {

    TextView register;
    EditText name, email, password, phone, address;
    ProgressWheel progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        register = (TextView) findViewById(R.id.register_button);

        name = (EditText) findViewById(R.id.register_name);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        phone = (EditText) findViewById(R.id.register_phone);
        address = (EditText) findViewById(R.id.register_address);

        progress = (ProgressWheel) findViewById(R.id.register_progress_wheel);

        register.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        String nam = name.getText().toString();
        final String mail = email.getText().toString();
        final String pwd = password.getText().toString();
        progress.setVisibility(View.VISIBLE);
        String location = GPSLocation.getLocation(this);

        AvaliableJobsAPI.getInstance().getService().driverRegister(nam, mail, pwd, phone.getText().toString(), address.getText().toString(), location, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //getToken(mail, pwd);
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                progress.setVisibility(View.INVISIBLE);

                new SweetAlertDialog(RegisterPage.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();


            }
        });



    }

    /*private void getToken(String email, String pwd){
        AvaliableJobsAPI.getInstance().getService().getToken(email, pwd, "uuid", new Callback<String>() {
            @Override
            public void success(String s, Response response) {

                //save to preference

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }*/

}
