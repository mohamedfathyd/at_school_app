package com.atschoolPioneerSchool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.atschoolPioneerSchool.data.AndroidMultiPartEntity;
import com.atschoolPioneerSchool.model.MessageDetails;

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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by OmarA on 05/09/2017.
 */

public class PopupChatAction extends DialogFragment implements View.OnClickListener {

    View form;
    // LogCat tag
    private static final String TAG = ActivityTestUpload.class.getSimpleName();
    private SharedPreferences sharedpref;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    private Button btnCancel;
    float totalSize = 0;
    public String Receiver_GCM_Token = "";

    Commmunicator commmunicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        commmunicator = (Commmunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup container, Bundle saveInstanceState) {
        form = _inflater.inflate(R.layout.popup_chat_action, container, false);

        sharedpref = form.getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        txtPercentage = (TextView) form.findViewById(R.id.txtPercentage);
        btnUpload = (Button) form.findViewById(R.id.btnUpload);
        btnCancel = (Button) form.findViewById(R.id.btnCancel);
        progressBar = (ProgressBar) form.findViewById(R.id.progressBar);
        imgPreview = (ImageView) form.findViewById(R.id.imgPreview);
        vidPreview = (VideoView) form.findViewById(R.id.videoPreview);

		/*// Changing action bar background color
        getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor(getResources().getString(
						R.color.action_bar))));*/


        // Receiving the data from previous activity
        //  Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = sharedpref.getString("filePath", "");//i.getStringExtra("filePath");

        // boolean flag to identify the media type, image or video
        boolean isImage = sharedpref.getBoolean("isImage", false);//  i.getBooleanExtra("isImage", true);


        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(form.getContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                new UploadFileToServer().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                ;
            }
        });
        return form;
    }

    @Override
    public void onClick(View v) {

        this.dismiss();

    }

    interface Commmunicator {
        public void onDialogMessage(MessageDetails message);
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

            //return sendFilePost("http://192.168.1.57:1234/uploadedfiles", filePath);
            return uploadFile();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);
            super.onPostExecute(result);
        }


        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(getResources().getString(R.string.Web_URL) + "events=25");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("description", new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));
                entity.addPart("Receiver_GCM_Token", new StringBody(Receiver_GCM_Token));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                String Send_Time = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                        .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();
                entity.addPart("Send_Time", new StringBody(Send_Time));

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String Send_Date = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                        .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();
                entity.addPart("Send_Date", new StringBody(Send_Date));

                entity.addPart("Sender_User_Master_Id", new StringBody(sharedpref.getString("USER_MASTER_Id", "").trim()));
                entity.addPart("Receiver_User_Master_Id", new StringBody(sharedpref.getString("Receiver_User_Master_Id", "").trim()));


                entity.addPart("Sender_GCM", new StringBody(sharedpref.getString("GCM_Token", "").trim()));
                entity.addPart("Sender_Name", new StringBody(sharedpref.getString("Name", "").trim(), Charset.forName(HTTP.UTF_8)));

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
    private void showAlert(String message) {

        if (message.equals("Uploaded Successfully...")) {
            //  this.dismiss();
            commmunicator.onDialogMessage(new MessageDetails());
            this.dismiss();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(form.getContext());
            builder.setMessage(message).setTitle("Response from Servers")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}