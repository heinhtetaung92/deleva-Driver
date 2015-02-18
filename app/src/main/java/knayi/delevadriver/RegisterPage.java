package knayi.delevadriver;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import cn.pedant.SweetAlert.SweetAlertDialog;
import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.gpslocation.GPSLocation;
import knayi.delevadriver.gpslocation.GPSTracker;
import knayi.delevadriver.gpslocation.LidaComLocalizacao;
import knayi.delevadriver.gpslocation.MyLocation;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RegisterPage extends ActionBarActivity implements View.OnClickListener, LocationListener {

    TextView register;
    EditText name, email, password, phone, address;
    ProgressWheel progress;
    GPSLocation gpsconnector;

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
    protected void onPause() {
        super.onPause();
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

        gpsconnector = new GPSLocation(this);

        gpsconnector.Start();
        String loc = gpsconnector.displayLocation();

        if(loc == null){
            loc = "(Couldn't get the location. Make sure location is enabled on the device)";
            gpsconnector.Start();
            gpsconnector.startLocationUpdates();
        }

        Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
        Log.i("Location", loc);


        /*String nam = name.getText().toString();
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
        });*/



    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null) {
            Toast.makeText(this, String.valueOf(location.getLongitude()),
                    Toast.LENGTH_SHORT).show();

            gpsconnector.stopLocationUpdates();
        }else{

        }


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
