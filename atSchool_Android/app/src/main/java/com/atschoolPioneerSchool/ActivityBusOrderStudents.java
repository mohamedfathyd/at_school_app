package com.atschoolPioneerSchool;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.AdapterChatContactsListWithHeader;
import com.atschoolPioneerSchool.adapter.BusOrderStudentsListAdapter;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.fragment.CallSonsFragment;
import com.atschoolPioneerSchool.fragment.CategoryFragment;
import com.atschoolPioneerSchool.fragment.HomeLocationFragment;
import com.atschoolPioneerSchool.model.BusOrderStudent;
import com.atschoolPioneerSchool.model.ChatContacts;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by OmarA on 24/11/2017.
 */

public class ActivityBusOrderStudents extends AppCompatActivity implements PopupStudentHomeLocation.Commmunicator {

    ActivityBusOrderStudents.GetStudentsBusOrders myTaskGetStudentsBusOrders = null;
    ActivityBusOrderStudents.GetStudentsBusOrdersTripStart myTaskGetStudentsBusOrdersTripStart = null;
    ActivityBusOrderStudents.GetStudentsBusOrdersTripEnd myTaskGetStudentsBusOrdersTripEnd = null;

    SendRequestTask mySendRequestTask = null;
    SendRequestTaskUndoStepInStepOut mySendRequestTaskUndoStepInStepOut = null;

    private SharedPreferences sharedpref;
    private SharedPreferences.Editor edt;

    public static BusOrderStudentsListAdapter adapter;
    private ListView listview;
    private ActionBar actionBar;
    private String Track_Trans_Order_Id;
    private List<BusOrderStudent> ListStudents = new ArrayList<>();
    private View parent_view;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private Button btn_start_trip;
    private Button btn_end_trip;
    PopupStudentHomeLocation pop;
    android.app.FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_order_students);
        parent_view = findViewById(android.R.id.content);

        pop = new PopupStudentHomeLocation();
        manager = getFragmentManager();
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        edt = sharedpref.edit();

        // initialize conversation data
        Intent intent = getIntent();
        Track_Trans_Order_Id = (String) intent.getExtras().getSerializable("Track_Trans_Order_Id");

        edt.putString("Track_Trans_Order_Id", Track_Trans_Order_Id);
        edt.commit();

        initToolbar();

        iniComponen();


        adapter = new BusOrderStudentsListAdapter(this, ListStudents);
        listview.setAdapter(adapter);
        listview.setSelectionFromTop(adapter.getCount(), 0);
        listview.requestFocus();
        registerForContextMenu(listview);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FillStudents();

        btn_start_trip = (Button) findViewById(R.id.btn_start_trip);
        btn_start_trip.setEnabled(false);
        btn_start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fill data
                if (!isNetworkAvailable(getBaseContext())) {
                    Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                } else {

                    //start trackig service
                    try {

                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Toast.makeText(getBaseContext(), "Please Turn GPS First.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (isMyServiceRunning(TrackingService.class)) {

                            Toast.makeText(getBaseContext(), "Service Already Start", Toast.LENGTH_LONG).show();

                        } else {

                            Intent i = new Intent(getBaseContext(), TrackingService.class);
                            startService(i);
                            Toast.makeText(getBaseContext(), "Service Started Successfully", Toast.LENGTH_LONG).show();
                        }

                        //create trip
                        myTaskGetStudentsBusOrdersTripStart = new ActivityBusOrderStudents.GetStudentsBusOrdersTripStart(getBaseContext());
                        myTaskGetStudentsBusOrdersTripStart.execute("");

                    } catch (Exception exc1) {
                    }
                }
            }
        });

        btn_end_trip = (Button) findViewById(R.id.btn_end_trip);
        btn_end_trip.setEnabled(false);
        btn_end_trip.setVisibility(View.GONE);
        btn_end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fill data
                if (!isNetworkAvailable(getBaseContext())) {
                    Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                } else {

                    //stop trackig service
                    try {

                        if (isMyServiceRunning(TrackingService.class)) {
                            stopService(new Intent(ActivityBusOrderStudents.this, TrackingService.class));
                            Toast.makeText(getBaseContext(), "Service Stopped Successfully", Toast.LENGTH_LONG).show();
                        }

                        //End trip
                        myTaskGetStudentsBusOrdersTripEnd = new ActivityBusOrderStudents.GetStudentsBusOrdersTripEnd(getBaseContext());
                        myTaskGetStudentsBusOrdersTripEnd.execute("");

                    } catch (Exception exc1) {
                    }
                }
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception exc1) {
        }
        return false;

    }

    public void FillStudents() {

        //fill data
        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {

            myTaskGetStudentsBusOrders = new ActivityBusOrderStudents.GetStudentsBusOrders(getBaseContext());
            myTaskGetStudentsBusOrders.execute("");
        }
    }

    @Override
    public void onBackPressed() {
        if (myTaskGetStudentsBusOrders != null) {
            myTaskGetStudentsBusOrders.cancel(true);
        }

        if (myTaskGetStudentsBusOrdersTripStart != null) {
            myTaskGetStudentsBusOrdersTripStart.cancel(true);
        }

        if (myTaskGetStudentsBusOrdersTripEnd != null) {
            myTaskGetStudentsBusOrdersTripEnd.cancel(true);
        }
        if (mySendRequestTask != null) {
            mySendRequestTask.cancel(true);
        }

        if (mySendRequestTaskUndoStepInStepOut != null) {
            mySendRequestTaskUndoStepInStepOut.cancel(true);
        }

        //stop tracking service
        if (!isMyServiceRunning(TrackingService.class)) {

            //close activity
            finish();
            return;
        }
        stopService(new Intent(ActivityBusOrderStudents.this, TrackingService.class));
        Toast.makeText(getBaseContext(), "Service Stopped Successfully", Toast.LENGTH_LONG).show();

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
        actionBar.setTitle("");
    }

    public void iniComponen() {
        listview = (ListView) findViewById(R.id.listview);

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
        // Inflate the menu; this adds ListStudents to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus_order_students, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            //take photo
            case R.id.action_message:
                Snackbar.make(parent_view, item.getTitle() + " Take photo", Snackbar.LENGTH_SHORT).show();

                // Intent intent = new Intent(getBaseContext(), HomeLocationFragment.class);
                // startActivity(intent);


//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        PopupStudentHomeLocation pop = new PopupStudentHomeLocation();
//                        android.app.FragmentManager manager = getFragmentManager();
//                        pop.show(manager,null);
//                    }
//                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogMessage(MessageDetails message) {

        Toast.makeText(getBaseContext(), "so ", Toast.LENGTH_SHORT).show();
    }

    private class GetStudentsBusOrders extends AsyncTask<String, BusOrderStudent, String> {

        private Context mContext;
        private int cntall;

        //Get students of bus order
        //http://irbid.lms-school.com/API_Mobile.aspx?events=37&Track_Trans_Order_Id=7
        String tag[] = {"events", "Track_Trans_Order_Id"};
        String value[] = {"37", Track_Trans_Order_Id};


        public GetStudentsBusOrders(Context context) {

            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            super.onPreExecute();
            json_code = "";
            hideKeyboard();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {

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

                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;

                            for (i = 0; i < cntall; i++) {

                                if (isCancelled()) {
                                    return null;
                                }

                                try {
                                    json = jsonArray.getJSONObject(i);

                                    BusOrderStudent objMsg = new BusOrderStudent(i + 1, json.getString("Track_Trans_Order_Id")
                                            , json.getString("StudentName"), json.getString("Student_Id"), json.getString("className"),
                                            json.getString("SectionName")
                                            , json.getString("LAT"), json.getString("LNG"), json.getString("StudentImageName")
                                            , json.getString("GardianMobile1")
                                            , json.getString("GardianMobile2"), json.getString("RowNumber"), json.getString("PicarsId"),
                                            json.getString("GuardianUSER_MASTER_Id"));


                                    ListStudents.add(0, objMsg);


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
        protected void onProgressUpdate(BusOrderStudent... values) {


        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            btn_start_trip.setEnabled(true);
            adapter = new BusOrderStudentsListAdapter(mContext, ListStudents);
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


            //  hideKeyboard();
            super.onPostExecute(msg);
        }
    }

    private class GetStudentsBusOrdersTripStart extends AsyncTask<String, BusOrderStudent, String> {

        private Context mContext;
        private int cntall;
        private boolean IsEnded = false;

        public GetStudentsBusOrdersTripStart(Context context) {

            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            super.onPreExecute();
            json_code = "";
            hideKeyboard();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {
                    //Get students of bus order
                    //http://irbid.lms-school.com/API_Mobile.aspx?events=37&Track_Trans_Order_Id=7

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String tDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                    String sTime = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


                    String tag[] = {"events", "Track_Trans_Order_Id", "Trip_Date", "Start_Time", "End_Time"};
                    String value[] = {"38", Track_Trans_Order_Id, tDate, sTime, ""};


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

                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;

                            for (i = 0; i < cntall; i++) {

                                if (isCancelled()) {
                                    return null;
                                }

                                try {
                                    json = jsonArray.getJSONObject(i);

                                    //get students from last list and update new data
                                    for (int y = 0; y <= ListStudents.size(); y++) {

                                        if (ListStudents.get(y).Student_Id.equals(json.getString("Student_Id"))) {

                                            ListStudents.get(y).Track_Trans_Trip_Students_Id = json.getInt("Track_Trans_Trip_Students_Id");
                                            ListStudents.get(y).Track_Trans_Trip_Id = json.getInt("Track_Trans_Trip_Id");
                                            ListStudents.get(y).NotConfirm_Time = json.getString("NotConfirm_Time");
                                            ListStudents.get(y).StepIn_Time = json.getString("StepIn_Time");
                                            ListStudents.get(y).StepOut_Time = json.getString("StepOut_Time");
                                            ListStudents.get(y).IsAbsent = json.getInt("IsAbsent");
                                            ListStudents.get(y).Description = json.getString("Description");
                                            ListStudents.get(y).GardianGCM = json.getString("GardianGCM");
                                            ListStudents.get(y).GardianAPNS = json.getString("GardianAPNS");
                                            ListStudents.get(y).GuardianUSER_MASTER_Id = json.getString("GuardianUSER_MASTER_Id");

                                            if (!json.getString("End_Time").isEmpty()) {
                                                ListStudents.get(y).IsTripEnded = true;
                                                IsEnded = true;
                                            }

                                            break;
                                        }

                                        if (y == 0) {

                                            edt.putString("Track_Trans_Trip_Id", json.getString("Track_Trans_Trip_Id"));
                                            edt.commit();
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
        protected void onProgressUpdate(BusOrderStudent... values) {


        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            adapter = new BusOrderStudentsListAdapter(mContext, ListStudents);
            listview.setAdapter(adapter);

            if (ListStudents.size() > 0) {
                btn_start_trip.setVisibility(View.GONE);

                if (!IsEnded) {
                    btn_end_trip.setEnabled(true);
                    btn_end_trip.setVisibility(View.VISIBLE);
                } else {
                    //stop trackig service
                    try {

                        if (isMyServiceRunning(TrackingService.class)) {
                            stopService(new Intent(ActivityBusOrderStudents.this, TrackingService.class));
                            Toast.makeText(getBaseContext(), "Service Stopped Successfully", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception exc1) {
                    }
                }
            }

            adapter.setOnItemClickListener(new BusOrderStudentsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, BusOrderStudent obj, int position, int typeRequest) {
                    //Undo Stepin or Step Out
                    if (typeRequest == 6) {

                        ProgressBar rowProgressBar = (ProgressBar) view.findViewById(R.id.rowProgressBar);
                        rowProgressBar.setVisibility(View.VISIBLE);

                        ImageView image = (ImageView) view.findViewById(R.id.image);
                        image.setVisibility(View.GONE);

                        if (!isNetworkAvailable(getBaseContext())) {
                            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                        } else {
                            mySendRequestTaskUndoStepInStepOut = new ActivityBusOrderStudents.SendRequestTaskUndoStepInStepOut(getBaseContext(), rowProgressBar, view, position);
                            mySendRequestTaskUndoStepInStepOut.execute("");

                        }

                    } else if (typeRequest == 5) {  //set student home location

                        Random r = new Random();
                        pop.GuardianUSER_MASTER_Id = ListStudents.get(position).GuardianUSER_MASTER_Id;
                        pop.HomeLAT = ListStudents.get(position).LAT;
                        pop.HomeLNG = ListStudents.get(position).LNG;

                        pop.show(manager, String.valueOf(r.nextInt()));

                    } else {


                        ProgressBar rowProgressBar = (ProgressBar) view.findViewById(R.id.rowProgressBar);
                        rowProgressBar.setVisibility(View.VISIBLE);

                        ImageView image = (ImageView) view.findViewById(R.id.image);
                        image.setVisibility(View.GONE);

                        Calendar c = Calendar.getInstance();

                        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                        String sTime = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                                .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

                        if (typeRequest == 1) {

                            ListStudents.get(position).StepIn_Time = sTime;
                        } else if (typeRequest == 2) {

                            ListStudents.get(position).StepOut_Time = sTime;

                        } else if (typeRequest == 4) {

                            if (ListStudents.get(position).StepIn_Time.length() < 2
                                    && ListStudents.get(position).IsAbsent < 0) {

                                ListStudents.get(position).NotConfirm_Time = sTime;
                            } else {
                                rowProgressBar.setVisibility(View.GONE);
                                image.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else if (typeRequest == 3) {

                            ListStudents.get(position).StepOut_Time = "";
                            ListStudents.get(position).StepIn_Time = "";
                            ListStudents.get(position).IsAbsent = 1;

                        }

                        // send request call kids
                        if (!isNetworkAvailable(getBaseContext())) {
                            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                        } else {
                            mySendRequestTask = new ActivityBusOrderStudents.SendRequestTask(getBaseContext(), rowProgressBar, view, position);
                            mySendRequestTask.execute("");

                        }

                        Toast.makeText(getBaseContext(), R.string.PleaseWait, Toast.LENGTH_SHORT).show();
                    }
                }


            });

            //  hideKeyboard();
            super.onPostExecute(msg);
        }
    }

    private class GetStudentsBusOrdersTripEnd extends AsyncTask<String, BusOrderStudent, String> {

        private Context mContext;
        private int cntall;

        public GetStudentsBusOrdersTripEnd(Context context) {

            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            super.onPreExecute();
            json_code = "";
            hideKeyboard();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {
                    //Get students of bus order
                    //http://irbid.lms-school.com/API_Mobile.aspx?events=37&Track_Trans_Order_Id=7

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String tDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                    String sTime = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


                    String tag[] = {"events", "Track_Trans_Order_Id", "Trip_Date", "Start_Time", "End_Time"};
                    String value[] = {"38", Track_Trans_Order_Id, tDate, "", sTime};


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

                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;

                            for (i = 0; i < cntall; i++) {

                                if (isCancelled()) {
                                    return null;
                                }

                                try {
                                    json = jsonArray.getJSONObject(i);

                                    //get students from last list and update new data
                                    for (int y = 0; y <= ListStudents.size(); y++) {

                                        if (ListStudents.get(y).Student_Id.equals(json.getString("Student_Id"))) {

                                            ListStudents.get(y).Track_Trans_Trip_Students_Id = json.getInt("Track_Trans_Trip_Students_Id");
                                            ListStudents.get(y).Track_Trans_Trip_Id = json.getInt("Track_Trans_Trip_Id");
                                            ListStudents.get(y).NotConfirm_Time = json.getString("NotConfirm_Time");
                                            ListStudents.get(y).StepIn_Time = json.getString("StepIn_Time");
                                            ListStudents.get(y).StepOut_Time = json.getString("StepOut_Time");
                                            ListStudents.get(y).IsAbsent = json.getInt("IsAbsent");
                                            ListStudents.get(y).Description = json.getString("Description");
                                            ListStudents.get(y).GardianGCM = json.getString("GardianGCM");
                                            ListStudents.get(y).GardianAPNS = json.getString("GardianAPNS");
                                            ListStudents.get(y).GuardianUSER_MASTER_Id = json.getString("GuardianUSER_MASTER_Id");
                                            ListStudents.get(y).IsTripEnded = true;

                                            break;
                                        }

                                        if (y == 0) {

                                            edt.putString("Track_Trans_Trip_Id", json.getString("Track_Trans_Trip_Id"));
                                            edt.commit();
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
        protected void onProgressUpdate(BusOrderStudent... values) {


        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            adapter = new BusOrderStudentsListAdapter(mContext, ListStudents);
            listview.setAdapter(adapter);

            if (ListStudents.size() > 0) {
                btn_start_trip.setVisibility(View.GONE);
                btn_end_trip.setVisibility(View.GONE);
            }
        }
    }

    private class SendRequestTask extends AsyncTask<String, String, String> {
        private Context mContext;
        private ProgressBar mcellprogressBar;
        private View mcellView;
        private int mPosition = 0;

        public SendRequestTask(Context context, ProgressBar cellprogressBar, View cellView, int Position) {
            mContext = context;
            mcellprogressBar = cellprogressBar;
            mcellView = cellView;
            mPosition = Position;
        }

        @Override
        protected void onPreExecute() {
            mcellprogressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
            json_code = "";

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isCancelled()) {
                    return null;
                }


                BusOrderStudent obj = ListStudents.get(mPosition);

                String tag[] = {"events", "Track_Trans_Trip_Students_Id", "Student_Id", "StepIn_Time", "StepOut_Time"
                        , "Track_Trans_Trip_Id", "IsAbsent", "Description"
                        , "GardianGCM", "GardianAPNS", "GuardianUSER_MASTER_Id", "StudentName", "PicarsId"
                        , "OrderArrival", "NotConfirm_Time"};
                String value[] = {"41", String.valueOf(obj.Track_Trans_Trip_Students_Id), obj.Student_Id, obj.StepIn_Time,
                        obj.StepOut_Time, String.valueOf(obj.Track_Trans_Trip_Id), String.valueOf(obj.IsAbsent), obj.Description
                        , obj.GardianGCM, obj.GardianAPNS, obj.GuardianUSER_MASTER_Id, obj.StudentName
                        , obj.PicarsId, String.valueOf(obj.OrderArrival), obj.NotConfirm_Time};

                //http://localhost:5149/API_Mobile.aspx?events=41&Track_Trans_Trip_Students_Id=53&
                // Student_Id=6116&StepIn_Time=07:05&StepOut_Time=08:00&Track_Trans_Trip_Id=6&IsAbsent=1&Description=ll
                String url = getResources().getString(R.string.Web_URL);
                json_code = new post_connection_json().makePostRequest(url, tag, value);


                if (isCancelled()) {
                    return null;
                }


            } catch (Exception e) {
                e.printStackTrace();

                return e.getMessage();
            }

            return json_code;
        }

        @Override
        protected void onPostExecute(String s) {
            mcellprogressBar.setVisibility(View.GONE);

            ImageView image = (ImageView) mcellView.findViewById(R.id.image);
            image.setVisibility(View.VISIBLE);

            if (s.contains("Saved successfully...")) {
                adapter.notifyDataSetChanged();
            }

            super.onPostExecute(s);

        }
    }


    private class SendRequestTaskUndoStepInStepOut extends AsyncTask<String, String, String> {
        private Context mContext;
        private ProgressBar mcellprogressBar;
        private View mcellView;
        private int mPosition = 0;

        public SendRequestTaskUndoStepInStepOut(Context context, ProgressBar cellprogressBar, View cellView, int Position) {
            mContext = context;
            mcellprogressBar = cellprogressBar;
            mcellView = cellView;
            mPosition = Position;
        }

        @Override
        protected void onPreExecute() {
            mcellprogressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
            json_code = "";

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isCancelled()) {
                    return null;
                }


                BusOrderStudent obj = ListStudents.get(mPosition);


                String tag[] = {"events", "Track_Trans_Trip_Students_Id", "Track_Trans_Trip_Id"};
                String value[] = {"49", String.valueOf(obj.Track_Trans_Trip_Students_Id), String.valueOf(obj.Track_Trans_Trip_Id)};

                //http://localhost:5149/API_Mobile.aspx?events=41&Track_Trans_Trip_Students_Id=53&
                // Student_Id=6116&StepIn_Time=07:05&StepOut_Time=08:00&Track_Trans_Trip_Id=6&IsAbsent=1&Description=ll
                String url = getResources().getString(R.string.Web_URL);
                json_code = new post_connection_json().makePostRequest(url, tag, value);
                jsonArray = new JSONArray(json_code);

                JSONObject json;

                json = jsonArray.getJSONObject(0);

                try {

                    //get students from last list and update new data

                    ListStudents.get(mPosition).NotConfirm_Time = json.getString("NotConfirm_Time");
                    ListStudents.get(mPosition).StepIn_Time = json.getString("StepIn_Time");
                    ListStudents.get(mPosition).StepOut_Time = json.getString("StepOut_Time");
                    ListStudents.get(mPosition).IsAbsent = json.getInt("IsAbsent");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isCancelled()) {
                    return null;
                }


            } catch (Exception e) {
                e.printStackTrace();

                return e.getMessage();
            }

            return json_code;
        }

        @Override
        protected void onPostExecute(String s) {
            mcellprogressBar.setVisibility(View.GONE);

            ImageView image = (ImageView) mcellView.findViewById(R.id.image);
            image.setVisibility(View.VISIBLE);

            //  if (s.contains("Saved successfully...")) {
            adapter.notifyDataSetChanged();
            //  }

            super.onPostExecute(s);

        }
    }

}

