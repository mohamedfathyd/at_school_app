package com.atschoolPioneerSchool;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.MessageDetailsListAdapter;
import com.atschoolPioneerSchool.adapter.StatementListAdapter;
import com.atschoolPioneerSchool.data.AndroidMultiPartEntity;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.Student;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityChatDetails extends AppCompatActivity implements PopupChatAction.Commmunicator {

    GetDataTask myTask = null;
    SendTextMessage mySendTextMessageTask = null;

    // LogCat tag
    private static final String TAG = ActivityChatDetails.class.getSimpleName();
    public static final String IMAGE_DIRECTORY_NAME = "AndroidFileUpload";

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video


    public static String KEY_FRIEND = "com.atschoolPioneerSchool.FRIEND";
    public static String KEY_SNIPPET = "com.atschoolPioneerSchool.SNIPPET";


    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Student obj, String snippet) {
        Intent intent = new Intent(activity, ActivityChatDetails.class);
        intent.putExtra(KEY_FRIEND, obj);
        intent.putExtra(KEY_SNIPPET, snippet);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    private Button btn_send;
    private EditText et_content;
    public static MessageDetailsListAdapter adapter;
    private ListView listview;
    private ActionBar actionBar;
    private Student friend;
    private List<MessageDetails> items = new ArrayList<>();
    private View parent_view;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor edt;

    SwipeRefreshLayout str;

    //used ti determine patch of returned data to fill the listview
    int SelectedPageNumer = 0;

    //Count Of Rows In LastPatch
    int CountOfRowsInLastPatch = -1;

    boolean isLoading = false;


    // Locate listview last item
    int position = -1;

    public void FillChate() {

        if (CountOfRowsInLastPatch < 100 && CountOfRowsInLastPatch > -1) {
            //no more data
            return;
        }

        SelectedPageNumer = SelectedPageNumer + 1;


        if (SelectedPageNumer == 1) {

            position = -1;

        } else {
            position = listview.getLastVisiblePosition();
        }


        //fill data
        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {

            myTask = new ActivityChatDetails.GetDataTask(getBaseContext());
            myTask.execute("");
        }
    }


    public void AddNewItemFromNotificationChat(MessageDetails prmMessageDetails) {

        items.add(items.size(), prmMessageDetails);

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
                listview.setSelectionFromTop(items.size() - 1, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (myTask != null) {
            myTask.cancel(true);

        }

        if (mySendTextMessageTask != null) {
            mySendTextMessageTask.cancel(true);
        }

        //close activity
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        parent_view = findViewById(android.R.id.content);

        // Locate listview last item
        position = -1;

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Student) intent.getExtras().getSerializable(KEY_FRIEND);

        //save last user chat with him
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        edt = sharedpref.edit();
        edt.putString("Last_Chat_Receiver_User_Master_Id", String.valueOf(friend.StudentId));
        edt.commit();

        String snippets = intent.getStringExtra(KEY_SNIPPET);
        initToolbar();

        iniComponen();


        adapter = new MessageDetailsListAdapter(this, items);
        listview.setAdapter(adapter);
        listview.setSelectionFromTop(adapter.getCount(), 0);
        listview.requestFocus();
        registerForContextMenu(listview);


        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        FillChate();

        //swipe dow to refresh
        str = ((SwipeRefreshLayout) findViewById(R.id.swiperefresh));
        str.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                str.setRefreshing(false);

                FillChate();

/*
                items = new ArrayList<>();

                position = 0;
                SelectedPageNumer = 0;
                CountOfRowsInLastPatch = -1;

                FillChate();*/
            }
        });


        // Create an OnScrollListener
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { // TODO Auto-generated method stub

                int threshold = 1;
                int count = listview.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listview.getLastVisiblePosition() >= count - threshold) {

                        items = new ArrayList<>();

                        position = 0;
                        SelectedPageNumer = 0;
                        CountOfRowsInLastPatch = -1;

                        FillChate();
                    }
                }


/*
                int threshold = 1;
                int count = listview.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listview.getLastVisiblePosition() >= count - threshold) {
                        FillChate();
                    }
                }*/
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }

        });

        Constant.CurrentActivityChatDetails = this;
    }

    @Override
    public void onDialogMessage(MessageDetails message) {

        items = new ArrayList<>();

        position = 0;
        SelectedPageNumer = 0;
        CountOfRowsInLastPatch = -1;

        FillChate();
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

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(friend.getName());
    }

    public void bindView() {
        try {
            adapter.notifyDataSetChanged();
            listview.setSelectionFromTop(adapter.getCount(), 0);
        } catch (Exception e) {

        }
    }

    public void iniComponen() {
        listview = (ListView) findViewById(R.id.listview);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_content = (EditText) findViewById(R.id.text_content);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // uploading the file to server
                mySendTextMessageTask = new ActivityChatDetails.SendTextMessage();
                mySendTextMessageTask.execute();


            }
        });
        et_content.addTextChangedListener(contentWatcher);
        if (et_content.length() == 0) {
            btn_send.setEnabled(false);
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setEnabled(false);
            } else {
                btn_send.setEnabled(true);
            }
            //draft.setContent(etd.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_details, menu);
        return true;
    }

    /**
     * Handle click on action bar
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            //attached file
            case R.id.action_attachment:
                Snackbar.make(parent_view, item.getTitle() + " Recording video", Snackbar.LENGTH_SHORT).show();

                // attached file
                //   SelectFile();
                DialogProperties properties = new DialogProperties();

                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.STORAGE_DIR);
                properties.error_dir = new File(DialogConfigs.STORAGE_DIR);
                properties.offset = new File(DialogConfigs.STORAGE_DIR);
                properties.extensions = null;// new String[]{".jpg"};

                FilePickerDialog dialog = new FilePickerDialog(ActivityChatDetails.this, properties);
                dialog.setTitle("Select a File");

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        //files is the array of the paths of files selected by the Application User.

                        fileUri = Uri.fromFile(new File(files[0]));
                        launchUploadActivity(true);
                    }
                });


                dialog.show();


                return true;

            //recording video
            case R.id.action_video:
                Snackbar.make(parent_view, item.getTitle() + " Recording video", Snackbar.LENGTH_SHORT).show();

                // record video
                recordVideo();


                return true;

            //take photo
            case R.id.action_photo:
                Snackbar.make(parent_view, item.getTitle() + " Take photo", Snackbar.LENGTH_SHORT).show();

                // capture picture
                captureImage();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
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


    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Launching camera app to record video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * ------------ Helper Methods ----------------------
     */
    private void launchUploadActivity(boolean isImage) {
        /*
        Intent i = new Intent(ActivityChatDetails.this, ActivityTestUpload.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
        */

        final SharedPreferences.Editor edt = sharedpref.edit();
        edt.putString("filePath", fileUri.getPath());
        edt.putBoolean("isImage", isImage);
        edt.putString("Receiver_User_Master_Id", String.valueOf(friend.StudentId));

        edt.commit();


        FragmentManager manager = getFragmentManager();
        PopupChatAction pop = new PopupChatAction();
        pop.Receiver_GCM_Token = friend.GCM_Token;
        pop.show(manager, null);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
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
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
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
     * Uploading the file to server
     */
    private class SendTextMessage extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            progressBar.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);

            progressBar.setProgress(progress[0]);

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

                et_content.setText("");

                items = new ArrayList<>();
                position = 0;
                SelectedPageNumer = 0;
                CountOfRowsInLastPatch = -1;

                FillChate();

                bindView();
            }

            super.onPostExecute(result);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            // HttpPost httppost = new HttpPost("http://192.168.1.57:1234/uploadedfiles");
            HttpPost httppost = new HttpPost("http://irbid.lms-school.com/API_Mobile.aspx?events=25");
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            int totalSize = 0;

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                Calendar c = Calendar.getInstance();

                SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                String Send_Time = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                        .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();
                entity.addPart("Send_Time", new StringBody(Send_Time));

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String Send_Date = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                        .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();
                entity.addPart("Send_Date", new StringBody(Send_Date));

                entity.addPart("Text_Message", new StringBody(et_content.getText().toString(), Charset.forName(HTTP.UTF_8)));


                // Extra parameters if you want to pass to server
                entity.addPart("description", new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                entity.addPart("Receiver_GCM_Token", new StringBody(friend.GCM_Token));
                entity.addPart("Receiver_APNS_Token", new StringBody(friend.APNS_Token));
                entity.addPart("Sender_User_Master_Id", new StringBody(sharedpref.getString("USER_MASTER_Id", "").trim()));
                entity.addPart("Receiver_User_Master_Id", new StringBody(String.valueOf(friend.StudentId)));

                entity.addPart("Sender_GCM", new StringBody(sharedpref.getString("GCM_Token", "").trim()));
                entity.addPart("Sender_Name", new StringBody(sharedpref.getString("Name", "").trim(), Charset.forName(HTTP.UTF_8)));

                //  totalSize = entity.getContentLength();
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


    private class GetDataTask extends AsyncTask<String, MessageDetails, String> {

        private Context mContext;
        private int cntall;
        private String loginUserMasterId = sharedpref.getString("USER_MASTER_Id", "").trim();


        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetDataTask(Context context) {

            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);
            super.onPreExecute();
            CountOfRowsInLastPatch = 0;
            json_code = "";

            edt.commit();
            hideKeyboard();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {
                    //http://irbid.lms-school.com/API_Mobile.aspx?events=24&NumberRowsInGrid=10&SelectedPageNumer=1
                    String tag[] = {"events", "NumberRowsInGrid", "SelectedPageNumer", "Sender_User_Master_Id", "Receiver_User_Master_Id"};
                    String value[] = {"24", "100", String.valueOf(SelectedPageNumer), loginUserMasterId, String.valueOf(friend.StudentId)};

                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

                }

                try {

                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");
                        jsonArray = new JSONArray(json_code);
                        cntall = jsonArray.length();

                        CountOfRowsInLastPatch = cntall;

                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;
                            double Message_Id = 0;

                            for (i = 0; i < cntall; i++) {

                                if (isCancelled()) {
                                    return null;
                                }

                                try {
                                    json = jsonArray.getJSONObject(i);

                                    Message_Id = 0;//json.getDouble("Message_Id");
                                    String msg = "";// json.getString("msg");
                                    String msgA = "";// json.getString("msgA");

                                    if (Message_Id > 0) {
                                        return msgA;

                                    } else {

                                        MessageDetails objMsg = new MessageDetails(i + 1, json.getString("Send_Date") + "  " + json.getString("Send_Time"),
                                                friend, json.getString("Text_Message").replaceAll("/n", "\n"),

                                                loginUserMasterId.equals(json.getString("Sender_User_Master_Id")) ? true : false
                                                , json.getString("Attached_File_Name"), json.getString("Attached_File_Extension"));


                                        items.add(0, objMsg);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

                        return String.valueOf(R.string.msgFaildLogin);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(MessageDetails... values) {


        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
            adapter = new MessageDetailsListAdapter(mContext, items);
            listview.setAdapter(adapter);


            if (msg != null) {
                if (!msg.isEmpty()) {
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

                    if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                }
            }

            if (SelectedPageNumer == 1) {

                listview.setSelectionFromTop(items.size() - 1, 0);

            } else {
                // Show the latest retrived results on the top
                //  listview.setSelectionFromTop(position, 0);
            }
            //  hideKeyboard();
            super.onPostExecute(msg);
        }
    }
}
