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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.RegisterStudentsAttendanceEntryAdapter;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.RegisterStudentsAttendance;
import com.atschoolPioneerSchool.model.MessageDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ActivityRegisterStudentsAttendanceEntry extends AppCompatActivity implements PopupStudentHomeLocation.Commmunicator {

    GetRegisterStudentsAttendanceEntry myTaskGetRegisterStudentsAttendanceEntry = null;
    ActivityRegisterStudentsAttendanceEntry.SendRequestTask mySendRequestTask = null;

    private SharedPreferences sharedpref;
    private SharedPreferences.Editor edt;

    public static RegisterStudentsAttendanceEntryAdapter adapter;
    private ListView listview;
    private ActionBar actionBar;
    private String CLASS_SECTION_ID;
    private String CLASS_SECTION_NAME;
    private List<RegisterStudentsAttendance> ListStudents = new ArrayList<>();
    private View parent_view;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    PopupStudentHomeLocation pop;
    android.app.FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_students_attendance_entry);
        parent_view = findViewById(android.R.id.content);

        pop = new PopupStudentHomeLocation();
        manager = getFragmentManager();
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        edt = sharedpref.edit();

        // initialize conversation data
        Intent intent = getIntent();
        CLASS_SECTION_ID = (String) intent.getExtras().getSerializable("CLASS_SECTION_ID");
        CLASS_SECTION_NAME = (String) intent.getExtras().getSerializable("CLASS_SECTION_NAME");
        this.setTitle(CLASS_SECTION_NAME);

        edt.putString("CLASS_SECTION_ID", CLASS_SECTION_ID);
        edt.commit();

        initToolbar();

        iniComponen();


        adapter = new RegisterStudentsAttendanceEntryAdapter(this, ListStudents);
        listview.setAdapter(adapter);
        listview.setSelectionFromTop(adapter.getCount(), 0);
        listview.requestFocus();
        registerForContextMenu(listview);

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FillStudents();

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

            myTaskGetRegisterStudentsAttendanceEntry = new ActivityRegisterStudentsAttendanceEntry.GetRegisterStudentsAttendanceEntry(getBaseContext());
            myTaskGetRegisterStudentsAttendanceEntry.execute("");
        }
    }

    @Override
    public void onBackPressed() {
        if (myTaskGetRegisterStudentsAttendanceEntry != null) {
            myTaskGetRegisterStudentsAttendanceEntry.cancel(true);
        }

        if (mySendRequestTask != null) {
            mySendRequestTask.cancel(true);
        }


        //stop tracking service
        if (!isMyServiceRunning(TrackingService.class)) {

            //close activity
            finish();
            return;
        }
        stopService(new Intent(ActivityRegisterStudentsAttendanceEntry.this, TrackingService.class));
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
        //getMenuInflater().inflate(R.menu.menu_bus_order_students, menu);

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

    private class GetRegisterStudentsAttendanceEntry extends AsyncTask<String, RegisterStudentsAttendance, String> {

        private Context mContext;
        private int cntall;

        //Get students

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String tDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


        String tag[] = {"events", "ClassSection_Id", "DT"};
        String value[] = {"12", CLASS_SECTION_ID, tDate};


        public GetRegisterStudentsAttendanceEntry(Context context) {

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

                                    RegisterStudentsAttendance objMsg = new RegisterStudentsAttendance(i + 1
                                            , json.getString("StudentId")
                                            , json.getString("NameAsInPass")
                                            , json.getString("NameAsPassEng")
                                            , json.getString("Absence")
                                            , json.getString("Delay")
                                            , json.getString("ExcusedAbsence")
                                            , json.getString("ExcusedDelay")
                                            , json.getString("StudentImageName")
                                            , json.getString("GardianMobile1")
                                            , json.getString("GardianMobile2")
                                            , json.getString("GCM_Token")
                                            , json.getString("APNS_Token")
                                            , json.getString("GuardianUSER_MASTER_Id")
                                            , json.getString("USER_MASTER_Id"));


                                    ListStudents.add( objMsg);


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
        protected void onProgressUpdate(RegisterStudentsAttendance... values) {


        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);

            adapter = new RegisterStudentsAttendanceEntryAdapter(mContext, ListStudents);
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

            adapter.setOnItemClickListener(new RegisterStudentsAttendanceEntryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RegisterStudentsAttendance obj, int position, int typeRequest) {
                    // Absent
                    if (typeRequest == 1) {

                        ListStudents.get(position).Absence = "1";
                        ListStudents.get(position).Delay = "0";

                    } else if (typeRequest == 2) {
                        ListStudents.get(position).Absence = "0";
                        ListStudents.get(position).Delay = "1";

                    } else if (typeRequest == 3) {

                        ListStudents.get(position).Absence = "0";
                        ListStudents.get(position).Delay = "0";

                    }

                    ProgressBar rowProgressBar = (ProgressBar) view.findViewById(R.id.rowProgressBar);
                    rowProgressBar.setVisibility(View.VISIBLE);

                    ImageView image = (ImageView) view.findViewById(R.id.image);
                    image.setVisibility(View.GONE);

                    if (!isNetworkAvailable(getBaseContext())) {
                        Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                    } else {

                        mySendRequestTask = new ActivityRegisterStudentsAttendanceEntry.SendRequestTask(getBaseContext(), rowProgressBar, view, position);
                        mySendRequestTask.execute("");

                    }

                    Toast.makeText(getBaseContext(), R.string.PleaseWait, Toast.LENGTH_SHORT).show();

                }
            });

            //  hideKeyboard();
            super.onPostExecute(msg);
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


                RegisterStudentsAttendance obj = ListStudents.get(mPosition);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String tDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                    .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


                //http://irbid.lms-school.com/API_Mobile.aspx?events=50&
                // ClassSection_Id=307573&DT=20180521&StudentId=3335&GCM_Token=%22%22&APNS_Token%22%22&Absence=1&Delay=0&NameAsInPass=sami

                String tag[] = {"events", "ClassSection_Id", "DT", "StudentId", "GCM_Token", "APNS_Token", "NameAsInPass", "Absence", "Delay"};
                String value[] = {"50", CLASS_SECTION_ID, tDate, obj.StudentId, obj.GCM_Token, obj.APNS_Token, obj.NameAsInPass, obj.Absence, obj.Delay};


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
}

