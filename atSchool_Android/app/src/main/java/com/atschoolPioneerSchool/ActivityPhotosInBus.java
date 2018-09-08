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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.AdapterBusPhotos;
import com.atschoolPioneerSchool.adapter.AdapterBusPhotos;
import com.atschoolPioneerSchool.data.AndroidMultiPartEntity;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.Student;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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

/**
 * Created by OmarA on 25/12/2017.
 */

public class ActivityPhotosInBus extends AppCompatActivity {

    ActivityPhotosInBus.GetDataTask myTask = null;

    // LogCat tag
    private static final String TAG = ActivityPhotosInBus.class.getSimpleName();
    public static String KEY_FRIEND = "com.atschoolPioneerSchool.FRIEND";
    public static String KEY_SNIPPET = "com.atschoolPioneerSchool.SNIPPET";


    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Student obj, String snippet) {
        Intent intent = new Intent(activity, ActivityPhotosInBus.class);
        intent.putExtra(KEY_FRIEND, obj);
        intent.putExtra(KEY_SNIPPET, snippet);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    public static AdapterBusPhotos adapter;
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

        if (CountOfRowsInLastPatch < 8 && CountOfRowsInLastPatch > -1) {
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

            myTask = new ActivityPhotosInBus.GetDataTask(getBaseContext());
            myTask.execute("");
        }
    }

    @Override
    public void onBackPressed() {
        if (myTask != null) {
            myTask.cancel(true);

        }

        //close activity
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_in_bus);
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

        initToolbar();

        iniComponen();


        adapter = new AdapterBusPhotos(this, items);
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
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });
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

    public void iniComponen() {
        listview = (ListView) findViewById(R.id.listview);

        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_chat_details, menu);
        return true;
    }

    private class GetDataTask extends AsyncTask<String, MessageDetails, String> {

        private Context mContext;
        private int cntall;
        private String loginUserMasterId = sharedpref.getString("USER_MASTER_Id", "").trim();

        //http://irbid.lms-school.com/API_Mobile.aspx?events=24&NumberRowsInGrid=10&SelectedPageNumer=1
        //String tag[] = {"events", "NumberRowsInGrid", "SelectedPageNumer", "Sender_User_Master_Id", "Receiver_User_Master_Id"};
        // String value[] = {"24", "8", String.valueOf(SelectedPageNumer), loginUserMasterId, "26"};

        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetDataTask(Context context) {

            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
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

                   /* String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);*/

                    if (friend.PicarsId.length() < 3) {
                        return "";
                    }

                    String tag2[] = {"picarsID", "extradata"};
                    String value2[] = {friend.PicarsId, String.valueOf(friend.StudentId)};
                    /// String value2[] = {"1156613787", String.valueOf(friend.StudentId)};

                    String url2 = "http://api.picars.com/Shcool/getEventDetails?";
                    json_code = new post_connection_json().makePostRequest(url2, tag2, value2);

                    if (!json_code.equals("")) {
                        // json_code = json_code.replace("\\", "/");

                        JSONObject jsono = new JSONObject(json_code);

                        String data2 = jsono.get("data2").toString();

                        JSONObject jsono2 = new JSONObject(data2);
                        String AllImages = jsono2.get("AllImages").toString();

                        jsonArray = new JSONArray(AllImages);

                        if (jsonArray.length() > 0) {
                            jsono2 = new JSONObject(jsonArray.get(0).toString());

                            String images = jsono2.get("images").toString();
                            images = images.replace("\"", "").replace("[", "").replace("]", "");
                            String[] arrimages = images.split(",");

                            if (images != null) {

                                for (int x = 0; x < arrimages.length; x++) {
                                    MessageDetails objMsg = new MessageDetails(x + 1, "",
                                            friend, "", false, arrimages[x].replace("\\/", "/"), ".jpeg");

                                    items.add(0, objMsg);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

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
            adapter = new AdapterBusPhotos(mContext, items);
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

