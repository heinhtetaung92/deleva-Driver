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
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import knayi.delevadriver.api.AvaliableJobsAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ProfileActivity extends ActionBarActivity {

    ImageView profile_picture;
    TextView name, email, phone, address;

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        name = (TextView) findViewById(R.id.profile_name_value);
        email = (TextView) findViewById(R.id.profile_email_value);
        phone = (TextView) findViewById(R.id.profile_phone_value);
        address = (TextView) findViewById(R.id.profile_address_value);

        sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);

        profile_picture.setImageResource(R.drawable.profilesampleimage);

        //getRoundedBitmap(profile_picture.getDrawingCache());

        String token = sPref.getString(Config.TOKEN, null);

        if(token != null)
            AvaliableJobsAPI.getInstance().getService().getProfile(token, new Callback<String>() {
                @Override
                public void success(String s, Response response) {

                    try {
                        JSONObject item = new JSONObject(s);

                        name.setText(":  " + item.get("name"));
                        email.setText(":  " + item.get("email"));
                        phone.setText(":  " + item.get("mobile_number"));
                        address.setText(":  " + item.get("address"));

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

            SharedPreferences.Editor editor = sPref.edit();
            editor.putString(Config.TOKEN, null);
            editor.commit();

            startActivity(new Intent(this, LoginActivity.class));
            finish();

            return true;
        }
        else if(item.getItemId() == 14){
            Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth() , bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
