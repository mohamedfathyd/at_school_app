package com.atschoolPioneerSchool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.MessageNotificationListAdapter;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.MessageNotification;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityNotification extends AppCompatActivity {

    GetDataTask myTask = null;
    public static String KEY_FRIEND = "com.atschoolPioneerSchool.FRIEND";
    public static String KEY_SNIPPET = "com.atschoolPioneerSchool.SNIPPET";

    public static MessageNotificationListAdapter adapter;
    private String USER_MASTER_Id = "-1";

    private ListView listview;
    private ActionBar actionBar;
    private Student friend;
    private List<MessageNotification> items = new ArrayList<>();
    private View parent_view;

    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    SwipeRefreshLayout str;

    //used ti determine patch of returned data to fill the listview
    int SelectedPageNumer = 0;

    //Count Of Rows In LastPatch
    int CountOfRowsInLastPatch = -1;

    boolean isLoading = false;

    // Locate listview last item
    int position = -1;

    private void FillData() {

        if (CountOfRowsInLastPatch < 25 && CountOfRowsInLastPatch > -1) {
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
            myTask = new ActivityNotification.GetDataTask(getBaseContext());
            myTask.execute("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        parent_view = findViewById(android.R.id.content);

        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        USER_MASTER_Id = sharedpref.getString("USER_MASTER_Id", "").trim();
        // Locate listview last item
        position = -1;

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Student) intent.getExtras().getSerializable(KEY_FRIEND);

        initToolbar();
        listview = (ListView) findViewById(R.id.listview);
        adapter = new MessageNotificationListAdapter(this, items);
        listview.setAdapter(adapter);
        listview.setSelectionFromTop(adapter.getCount(), 0);
        listview.requestFocus();
        registerForContextMenu(listview);


        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FillData();

        //swipe dow to refresh
        str = ((SwipeRefreshLayout) findViewById(R.id.swiperefresh));
        str.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                str.setRefreshing(false);

                items = new ArrayList<>();

                position = 0;
                SelectedPageNumer = 0;
                CountOfRowsInLastPatch = -1;

                FillData();
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
                        FillData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }

        });
    }

    @Override
    public void onBackPressed() {
        if (myTask != null) {
            myTask.cancel(true);
        }
        //close activity
        finish();
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
        actionBar.setTitle(getResources().getString(R.string.title_activity_notification));
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetDataTask extends AsyncTask<String, MessageNotification, String> {

        private Context mContext;
        private int cntall;

        SharedPreferences sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetDataTask(Context context) {

            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            super.onPreExecute();
            CountOfRowsInLastPatch = 0;
            json_code = "";
            edt.commit();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {

                    //http://irbid.lms-school.com/API_Mobile.aspx?events=29&NumberRowsInGrid=10&SelectedPageNumer=1&Receiver_User_Master_Id=26
                    String tag[] = {"events", "NumberRowsInGrid", "SelectedPageNumer", "Receiver_User_Master_Id"};
                    String value[] = {"29", "25", String.valueOf(SelectedPageNumer), USER_MASTER_Id};


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

                                        MessageNotification objMsg = new MessageNotification(json.getInt("Id"), json.getString("Send_Date") + "  "
                                                + json.getString("Send_Time"), json.getString("Text_Message").replaceAll("/n", " "));

                                        items.add(objMsg);


                                       /* try {
                                            Thread.sleep(10);
                                        } catch (Exception ex) {
                                        }*/
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
        protected void onProgressUpdate(MessageNotification... values) {


        }


        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            adapter = new MessageNotificationListAdapter(mContext, items);
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
            if (!this.isCancelled()) {
                if (SelectedPageNumer == 1) {

                    listview.setSelectionFromTop(0, 0);

                } else {
                    // Show the latest retrived results on the top
                    listview.setSelectionFromTop(position, 0);
                }
            }

            super.onPostExecute(msg);
        }
    }
}
