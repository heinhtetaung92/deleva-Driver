package knayi.delevadriver;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.camera.CropImageIntentBuilder;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.makeramen.RoundedImageView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import knayi.delevadriver.api.AvaliableJobsAPI;
import knayi.delevadriver.model.MyTypeFace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class UpdateProfileActivity extends ActionBarActivity implements View.OnClickListener, ImageChooserListener {

    TextView update;
    EditText name, email, phone, address, nrc, vehicle;
    ProgressWheel progress;
    View progress_background;
    String nameval, emailval, phoneval, addressval, nrcval, vehicleval, imagepath;

    RoundedImageView profile_picture;

    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;



    private TextView textViewFile;


    private ImageChooserManager imageChooserManager;

    private String filePath;


    private int chooserType;

    private int CROP_PIC = 919;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        ActionBar actionbar = getSupportActionBar();
        filePath = null;

        if(actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        profile_picture = (RoundedImageView) findViewById(R.id.update_profile_picture);

        update = (TextView) findViewById(R.id.update_button);

        name = (EditText) findViewById(R.id.update_name);
        email = (EditText) findViewById(R.id.update_email);
        phone = (EditText) findViewById(R.id.update_phone);
        address = (EditText) findViewById(R.id.update_address);
        nrc = (EditText) findViewById(R.id.update_nrc);
        vehicle = (EditText) findViewById(R.id.update_vehicle);

        name.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
        email.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
        phone.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
        address.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
        nrc.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
        vehicle.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));

        progress = (ProgressWheel) findViewById(R.id.update_progress_wheel);
        progress_background = findViewById(R.id.update_progresswheel_background);

        progress_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Bundle bundle = getIntent().getExtras();

        nameval = bundle.getString("name");
        emailval = bundle.getString("email");
        phoneval = bundle.getString("mobilenumber");
        addressval = bundle.getString("address");
        nrcval = bundle.getString("nrc");
        vehicleval = bundle.getString("vehicle");
        imagepath = bundle.getString("imagepath");




        if(imagepath.equals("")){
            profile_picture.setImageResource(R.drawable.blank_profile);
            profile_picture.setAdjustViewBounds(true);
            profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.i("Image Path", "null");
        }
        else{
            Log.i("Image Path", "not null");
            Picasso.with(UpdateProfileActivity.this)
                    .load(APIConfig.DOMAIN_URL + imagepath)
                    .into(profile_picture, new com.squareup.picasso.Callback() {

                        @Override
                        public void onSuccess() {
                            //photo_progress.setVisibility(View.INVISIBLE);
                            Log.i("Update Image", "Load success");
                        }

                        @Override
                        public void onError() {

                            //photo_progress.setVisibility(View.INVISIBLE);

                            Log.i("Update Image", "Load Fail");

                            profile_picture.setImageResource(R.drawable.blank_profile);
                            profile_picture.setAdjustViewBounds(true);
                            profile_picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    });
        }






        name.setText(nameval);
        email.setText(emailval);
        phone.setText(phoneval);
        address.setText(addressval);
        nrc.setText(nrcval);
        vehicle.setText(vehicleval);


        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*final Dialog dialog = new Dialog(UpdateProfileActivity.this);
                dialog.setTitle("");
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_chose_dialog);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dTitle.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));


                dTitle.setText("Choose Photo");


                Button btn_camera_chose = (Button) dialog.findViewById(R.id.btn_camera_chose);
                btn_camera_chose.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
                btn_camera_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        takePicture();
                        dialog.dismiss();

                    }
                });

                Button btn_img_chose = (Button) dialog.findViewById(R.id.btn_gallery_chose);
                btn_img_chose.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
                btn_img_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                        dialog.dismiss();
                    }
                });


                dialog.show();*/

                final MaterialDialog dialog = new MaterialDialog.Builder(UpdateProfileActivity.this)
                        .backgroundColorRes(R.color.primary)
                        .customView(R.layout.image_chose_dialog, true)
                        .positiveText("")
                        .typeface("ciclefina.ttf", "ciclegordita.ttf")
                                //.negativeText(android.R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                //Toast.makeText(getActivity().getApplicationContext(), "Positive ".toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                            }
                        }).build();

                dialog.show();

                //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Button btn_camera_chose = (Button) dialog.findViewById(R.id.btn_camera_chose);
                btn_camera_chose.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
                btn_camera_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        takePicture();
                        dialog.dismiss();

                    }
                });

                Button btn_img_chose = (Button) dialog.findViewById(R.id.btn_gallery_chose);
                btn_img_chose.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
                btn_img_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                        dialog.dismiss();
                    }
                });


                //positiveAction = dialog.getActionButton(DialogAction.POSITIVE);



            }
        });


        update.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            /*Intent intent = new Intent(UpdateProfileActivity.this, ProfileFragment.class);
            startActivity(intent);*/
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
        progress_background.setVisibility(View.VISIBLE);
        SharedPreferences sPref = getSharedPreferences(Config.TOKEN_PREF, MODE_PRIVATE);
        String token = sPref.getString(Config.TOKEN, null);


        if (token != null || nam != null || mail != null || phone.getText().toString() != null || nrc.getText().toString() != null || vehicle.getText().toString() != null) {


            if(filePath == null){

                AvaliableJobsAPI.getInstance().getService().updateProfileWithoutPicture(token, nam, mail, phone.getText().toString(), address.getText().toString(), nrc.getText().toString(), vehicle.getText().toString(), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Toast.makeText(getApplicationContext(), "Profile has successfully updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfileActivity.this, DrawerMainActivity.class);
                        intent.putExtra("selection", 1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivityForResult(intent, 0);

                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.setVisibility(View.INVISIBLE);
                        progress_background.setVisibility(View.INVISIBLE);

                        if (error.getBody() == null) {
                            Toast.makeText(UpdateProfileActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                        } else {

                            String errmsg = error.getBody().toString();
                            String errcode = "";


                            try {
                                JSONObject errobj = new JSONObject(errmsg);

                                errcode = errobj.getJSONObject("err").getString("message");

                                Toast.makeText(UpdateProfileActivity.this, errcode, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }


                    }
                });

            }else {


                Log.i("Image File", filePath);
                File imagefile = new File(filePath);
                TypedFile typedFile = new TypedFile("multipart/form-data", imagefile);

                AvaliableJobsAPI.getInstance().getService().updateProfile(token, nam, mail, phone.getText().toString(), address.getText().toString(), typedFile, nrc.getText().toString(), vehicle.getText().toString(), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Toast.makeText(getApplicationContext(), "Profile has successfully updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfileActivity.this, DrawerMainActivity.class);
                        intent.putExtra("selection", 1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivityForResult(intent, 0);
                        finish();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.setVisibility(View.INVISIBLE);
                        progress_background.setVisibility(View.INVISIBLE);

                        if (error.getBody() == null) {
                            Toast.makeText(UpdateProfileActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                        } else {

                            String errmsg = error.getBody().toString();
                            String errcode = "";


                            try {
                                JSONObject errobj = new JSONObject(errmsg);

                                errcode = errobj.getJSONObject("err").getString("message");

                                Toast.makeText(UpdateProfileActivity.this, errcode, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }


                    }
                });

            }

            }else{
            progress.setVisibility(View.INVISIBLE);
            progress_background.setVisibility(View.INVISIBLE);


            /*final Dialog dialog = new Dialog(UpdateProfileActivity.this);
            dialog.setTitle("");
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_textview);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView dContentText = (TextView) dialog.findViewById(R.id.dialog_contenttext);
            dTitle.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
            dContentText.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));


            dTitle.setText("");
            dContentText.setText("Please fill all fields!");



            Button dialogButton = (Button) dialog.findViewById(R.id.dialog_positive);
            dialogButton.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();*/

            MaterialDialog dialog = new MaterialDialog.Builder(UpdateProfileActivity.this)
                    .customView(R.layout.custom_message_dialog, false)
                    .positiveText("OK")
                    .positiveColor(android.R.color.white)
                    .positiveColorRes(android.R.color.white)
                    .backgroundColorRes(R.color.tranprimary)
                    .typeface("ciclefina.ttf", "ciclegordita.ttf")
                    .build();
            dialog.show();

            TextView txt_title = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView txt_message = (TextView) dialog.findViewById(R.id.dialog_message);
            txt_title.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));
            txt_message.setTypeface(MyTypeFace.get(UpdateProfileActivity.this, MyTypeFace.NORMAL));

            txt_message.setText("Please fill all fields!");

            }



    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(UpdateProfileActivity.this,
                ChooserType.REQUEST_PICK_PICTURE, "myfolder", true);
        imageChooserManager.setImageChooserListener(UpdateProfileActivity.this);
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(UpdateProfileActivity.this,
                ChooserType.REQUEST_CAPTURE_PICTURE, "myfolder", true);
        imageChooserManager.setImageChooserListener(UpdateProfileActivity.this);
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }else if (requestCode == CROP_PIC) {
            // get the returned data

            if(data != null) {

                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                profile_picture.setImageBitmap(thePic);
            }


        }else if ((requestCode == REQUEST_PICTURE) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.
            Uri croppedImage = Uri.fromFile(new File(filePath));


            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
            //cropImage.setOutlineColor(#ffffff);
            cropImage.setSourceImage(data.getData());


            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            profile_picture.setImageBitmap(BitmapFactory.decodeFile(new File(filePath).getAbsolutePath()));
        }
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(UpdateProfileActivity.this, chooserType,
                "myfolder", true);
        imageChooserManager.setImageChooserListener(UpdateProfileActivity.this);
        imageChooserManager.reinitialize(filePath);
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {


            @Override
            public void run() {
                //pbar.setVisibility(View.GONE);
                if (image != null) {
                    /*textViewFile.setText(image.getFilePathOriginal());*/
                    /*profile_picture.setImageURI(Uri.parse(new File(image
                            .getFileThumbnail()).toString()));*/
                    /*imageViewThumbSmall.setImageURI(Uri.parse(new File(image
                            .getFileThumbnailSmall()).toString()));*/

                    filePath = image.getFilePathOriginal();

                    performCropLibrary();
                }
            }
        });
    }

    private void performCropLibrary(){
        //startActivityForResult(MediaStoreUtils.getPickImageIntent(this), REQUEST_PICTURE);



        File imageFilePath = new File(filePath);

        Uri croppedImage = Uri.fromFile(imageFilePath);

        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
        cropImage.setSourceImage(croppedImage);
        startActivityForResult(cropImage.getIntent(UpdateProfileActivity.this.getApplicationContext()), REQUEST_CROP_PICTURE);

    }

    private void performCrop() {
        // take care of exceptions

        Log.i("Perfrom Crop", filePath);
        Uri picUri = Uri.fromFile(new File(filePath));

        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");


            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra("output", picUri);

            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            {
                //cropIntent.setAction(Intent.ACTION_GET_CONTENT);
            }
            else
            {
                cropIntent.setAction(Intent.ACTION_PICK);
                //cropIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }*/

            //cropIntent.putExtra("img_path" , picUri.toString());
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onDestroy() {
        /*if (adView != null) {
            adView.destroy();
        }*/
        super.onDestroy();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }

            if (savedInstanceState.containsKey("media_path")) {
                filePath = savedInstanceState.getString("media_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    public interface onUpdateListener{
        public void onProfileUpdated();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item = menu.add("Icon");
        item.setIcon(R.drawable.deleva_dispatcher_white_noeffects_04);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }
}
