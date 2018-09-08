package com.atschoolPioneerSchool;

/**
 * Created by OmarA on 13/10/2017.
 */

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

import com.atschoolPioneerSchool.adapter.WeeklyPlanListAdapter;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.WeekNumbers;
import com.atschoolPioneerSchool.model.WeeklyPlan;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityWeeklyPlanDetails extends AppCompatActivity {

    ActivityWeeklyPlanDetails.GetDataTask myTask = null;
    public static String KEY_WeekNumbers = "com.atschoolPioneerSchool.WeekNumbers";
    public static String KEY_SNIPPET = "com.atschoolPioneerSchool.SNIPPET";

    public static WeeklyPlanListAdapter adapter;
    private String USER_MASTER_Id = "-1";

    private ListView listview;
    private ActionBar actionBar;
    private WeekNumbers weekNumbers;
    private List<WeeklyPlan> items = new ArrayList<>();
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

        if (Constant.SelectedStudent == null) {
            onBackPressed();
        }
        //fill data
        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            myTask = new ActivityWeeklyPlanDetails.GetDataTask(getBaseContext());
            myTask.execute("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_plan_details);
        parent_view = findViewById(android.R.id.content);

        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        USER_MASTER_Id = sharedpref.getString("USER_MASTER_Id", "").trim();
        // Locate listview last item
        position = -1;

        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_WeekNumbers);

        // initialize conversation data
        Intent intent = getIntent();
        weekNumbers = (WeekNumbers) intent.getExtras().getSerializable(KEY_WeekNumbers);

        initToolbar();
        listview = (ListView) findViewById(R.id.listview);
        adapter = new WeeklyPlanListAdapter(this, items);
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
        // actionBar.setTitle(getResources().getString(R.string.title_Weekly_Plan) + "  -  " + weekNumbers.RowNumber + "  -  " + weekNumbers.Name);
        actionBar.setTitle(weekNumbers.Name);

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

    private class GetDataTask extends AsyncTask<String, WeeklyPlan, String> {
        public String switchLang = "en";
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


            switchLang = sharedpref.getString("switchLang", "").trim();

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
                    //http://localhost:5149/API_Mobile.aspx?events=34&NumberRowsInGrid=10&SelectedPageNumer=1&Class_Id=10&CLASS_SECTION_ID=497835&Week_No=1
                    String tag[] = {"events", "NumberRowsInGrid", "SelectedPageNumer", "Class_Id", "CLASS_SECTION_ID", "Week_No", "SCH_STUDY_DAY_ID", "SCH_SUBJECT_ID"};
                    String value[] = {"34", "100", String.valueOf(SelectedPageNumer), String.valueOf(Constant.SelectedStudent.getClassId()),
                            String.valueOf(Constant.SelectedStudent.CLASS_SECTION_ID), String.valueOf(weekNumbers.Id),
                            String.valueOf(weekNumbers.SCH_STUDY_DAY_ID), String.valueOf(weekNumbers.SCH_SUBJECT_ID)};


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
                                        if (switchLang.equals("ar")) {
                                            WeeklyPlan objMsg = new WeeklyPlan(json.getLong("Id"), json.getString("SCH_STUDY_DAY_ID"), json.getString("Description_A"),
                                                    json.getString("Description_B"), json.getString("Class_Name"), json.getString("CLASS_SECTION_NAME")
                                                    , json.getString("SCH_STUDY_DAY_NAMEA"), json.getString("SUBJECT_NAMEA"), json.getString("SUBJECT_NAMEA"), json.getString("File_Attached"));

                                            items.add(objMsg);

                                        } else {
                                            WeeklyPlan objMsg = new WeeklyPlan(json.getLong("Id"), json.getString("SCH_STUDY_DAY_ID"), json.getString("Description_A"),
                                                    json.getString("Description_B"), json.getString("Class_Name"), json.getString("CLASS_SECTION_NAME")
                                                    , json.getString("SCH_STUDY_DAY_NAME"), json.getString("SUBJECT_NAMEA"), json.getString("SUBJECT_NAME"), json.getString("File_Attached"));


                                            items.add(objMsg);
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getBaseContext(), R.string.msgNoData, Toast.LENGTH_SHORT).show();

                        return String.valueOf(R.string.msgNoData);
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
        protected void onProgressUpdate(WeeklyPlan... values) {


        }


        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            adapter = new WeeklyPlanListAdapter(mContext, items);
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
