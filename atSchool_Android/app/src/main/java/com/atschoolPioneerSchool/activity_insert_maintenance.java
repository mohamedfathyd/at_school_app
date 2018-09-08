package com.atschoolPioneerSchool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.atschoolPioneerSchool.data.AndroidMultiPartEntity;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.ReceiptVoucher;
import com.atschoolPioneerSchool.model.SpinnerData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.atschoolPioneerSchool.ActivityTestSelectFile.IMAGE_DIRECTORY_NAME;

public class activity_insert_maintenance extends AppCompatActivity {
    GetmaintenanceType myGetmaintenanceTypTask = null;
    UploadFileToServer myUploadFileToServerTask = null;

    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    private EditText txtmaintenance;
    private EditText txtLocation;
    Switch switchPriority;

    private ArrayList<SpinnerData> Lst_SpinnerData;
    String School_Id = "1";
    String USER_MASTER_Id = "1";

    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";
    public String switchLang = "en";
    private Uri fileUri; // file url to store imgPreview/video
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    // LogCat tag
    private static final String TAG = ActivityTestUpload.class.getSimpleName();

    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    float totalSize = 0;
    FloatingActionButton fab;
    FloatingActionButton fabPicture;
    Spinner mySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_maintenance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.imgPreview), EXTRA_OBJCT);
        switchPriority = (Switch) findViewById(R.id.switchPriority);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide key board
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                hideKeyboard();

                if (mySpinner.getSelectedItem().toString().length() == 0) {
                    Toast.makeText(getBaseContext(), R.string.Please_select_type, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (txtmaintenance.getText().length() == 0) {
                    Toast.makeText(getBaseContext(), R.string.please_enter_your_maintenance, Toast.LENGTH_SHORT).show();
                } else {
                    // uploading the file to server
                    myUploadFileToServerTask = new activity_insert_maintenance.UploadFileToServer();
                    myUploadFileToServerTask.execute();
                }


            }
        });

        fabPicture = (FloatingActionButton) findViewById(R.id.fabPicture);
        fabPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // capture picture
                captureImage();
            }
        });


        //  this.setTitle(getString(R.string.menu_Newmaintenance));
        this.setTitle("");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        txtmaintenance = (EditText) findViewById(R.id.txtmaintenance);
        txtLocation = (EditText) findViewById(R.id.txtLocation);

        mySpinner = (Spinner) findViewById(R.id.spinner);
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        switchLang = sharedpref.getString("switchLang", "").trim();

        //fill data
        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            myGetmaintenanceTypTask = new activity_insert_maintenance.GetmaintenanceType(this);
            myGetmaintenanceTypTask.execute("");
        }

        //hide key board
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onBackPressed() {

        if (myGetmaintenanceTypTask != null) {
            myGetmaintenanceTypTask.cancel(true);
        }

        if (myUploadFileToServerTask != null) {
            myUploadFileToServerTask.cancel(true);
        }
        Intent i = new Intent(getApplicationContext(), ActivityMain.class);
        startActivity(i);

        //close activity
        finish();
    }

    private class GetmaintenanceType extends AsyncTask<String, ReceiptVoucher, String> {

        private Context mContext;
        private int cntall;


        // you can use this array to populate your spinner
        ArrayList<String> SpinnerData = new ArrayList<String>();

        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetmaintenanceType(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar2.setVisibility(View.VISIBLE);
            mySpinner.setVisibility(View.GONE);
            super.onPreExecute();

            json_code = "";
            Lst_SpinnerData = new ArrayList<SpinnerData>();
            edt.commit();
        }

        @Override
        protected String doInBackground(String... strings) {

            if (isCancelled()) {
                return null;
            }

            try {

                SharedPreferences sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                School_Id = sharedpref.getString("School_Id", "").trim();
                USER_MASTER_Id = sharedpref.getString("USER_MASTER_Id", "").trim();
                //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=4&username=omar1&pass=omar1&AuthenticationKey=604531&GUARDIAN_NO=1022088460
                String tag[] = {"events", "School_Id"};
                String value[] = {"20", School_Id};


                //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=1&username=omar1&pass=omar1
                String url = getResources().getString(R.string.Web_URL);
                json_code = new post_connection_json().makePostRequest(url, tag, value);

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

            }

            try {
                //SpinnerData

                if (!json_code.equals("")) {

                    json_code = json_code.replace("\\", "/");
                    jsonArray = new JSONArray(json_code);
                    cntall = jsonArray.length();
                    JSONObject json;
                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < cntall; i++) {

                            try {
                                json = jsonArray.getJSONObject(i);
                                SpinnerData objS = new SpinnerData();

                                objS.Id = json.getString("Id");
                                if (switchLang.equals("ar")) {

                                    objS.Name = json.getString("NameA");
                                    objS.NameA = json.getString("NameA");
                                } else {
                                    objS.Name = json.getString("Name");
                                    objS.NameA = json.getString("Name");
                                }

                                Lst_SpinnerData.add(objS);
                                SpinnerData.add(objS.Name);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        return getString(R.string.msgNoData);
                    }

                } else {

                    return getString(R.string.msgNoData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return getString(R.string.msgServiceisnotavailable);

            }

            return "";
        }

        @Override
        protected void onProgressUpdate(ReceiptVoucher... values) {

        }

        @Override
        protected void onPostExecute(String msg) {

            mySpinner.setVisibility(View.VISIBLE);
            progressBar2.setVisibility(View.GONE);

            if (msg != null) {
                if (!msg.isEmpty()) {
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

                    if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                } else {

                    mySpinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, SpinnerData));

                }
            } else {

                if (!isCancelled()) {

                    mySpinner.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, SpinnerData));
                }
            }

            super.onPostExecute(msg);
        }
    }

    public boolean isNetworkAvailable(Context ctx) {
        if (ctx == null)
            return false;

        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Launching camera app to capture imgPreview
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the imgPreview capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Creating file uri to store imgPreview/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning imgPreview / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp.replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim() + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp.replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim() + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the imgPreview
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled imgPreview capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture imgPreview
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture imgPreview", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity(boolean isImage) {

        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);

        // Receiving the data from previous activity
        //  Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = fileUri.getPath();

        // boolean flag to identify the media type, image or video
        isImage = true;

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Displaying captured image/video on the screen
     */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);

            //  fab.setVisibility(View.INVISIBLE);
            fab.setEnabled(false);
            fabPicture.setEnabled(false);
            mySpinner.setEnabled(false);
            txtmaintenance.setEnabled(false);
            txtLocation.setEnabled(false);


            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }

            return uploadFile();
        }

        @Override
        protected void onPostExecute(String result) {
            if (!isCancelled()) {

                // fab.setVisibility(View.VISIBLE);
                fab.setEnabled(true);
                fabPicture.setEnabled(true);
                mySpinner.setEnabled(true);
                txtmaintenance.setEnabled(true);
                txtLocation.setEnabled(true);

                // showing the server response in an alert dialog
                showAlert(result);
                super.onPostExecute(result);
            }
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            String url = getResources().getString(R.string.Web_URL);
            HttpPost httppost = new HttpPost(url + "events=22");
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                File sourceFile = null;

                if (filePath != null) {
                    sourceFile = new File(filePath);

                    // Adding file data to http body
                    entity.addPart("image", new FileBody(sourceFile));
                }
                // Extra parameters if you want to pass to server
                entity.addPart("Description", new StringBody(txtmaintenance.getText().toString(), Charset.forName(HTTP.UTF_8)));
                entity.addPart("Location", new StringBody(txtLocation.getText().toString(), Charset.forName(HTTP.UTF_8)));

                String Prio = "0";
                if (switchPriority.isChecked()) {
                    Prio = "1";
                }
                entity.addPart("Priority", new StringBody(Prio, Charset.forName(HTTP.UTF_8)));


                //get type Id
                String typeId = "";
                for (SpinnerData obj : Lst_SpinnerData) {
                    if (obj.Name.equals(mySpinner.getSelectedItem().toString()) || obj.Name.equals(mySpinner.getSelectedItem().toString())) {
                        typeId = obj.Id.toString();
                    }
                }

                entity.addPart("Type", new StringBody(typeId));
                entity.addPart("TypeId", new StringBody(typeId));
                entity.addPart("School_Id", new StringBody(School_Id));
                entity.addPart("USER_MASTER_Id", new StringBody(USER_MASTER_Id));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (message.equals("Uploaded Successfully...")) {
                            //go to home activity
                            Intent i = new Intent(getApplicationContext(), ActivityMain.class);
                            startActivity(i);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
