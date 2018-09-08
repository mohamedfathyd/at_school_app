package com.atschoolPioneerSchool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atschoolPioneerSchool.data.post_connection_json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class activity_school_offence extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    private ProgressBar progressBar;


    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    public String username;
    public String pass;
    public String AuthenticationKey;
    public String Student_Id = "";// "2062
    private TextView txt_Subject, txt_To_Person, txt_OffenceType, txt_date, txt_OffenceDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_school_offence);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        username = sharedpref.getString("LastValidUserName", "").trim();
        pass = sharedpref.getString("LastValidPassword", "").trim();
        AuthenticationKey = sharedpref.getString("AuthenticationKey", "").trim();
        Student_Id = sharedpref.getString("SelectedStudentId", "").trim();

        txt_Subject = (TextView) findViewById(R.id.txt_Subject);
        txt_To_Person = (TextView) findViewById(R.id.txt_To_Person);
        txt_OffenceType = (TextView) findViewById(R.id.txt_OffenceType);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_OffenceDescription = (TextView) findViewById(R.id.txt_OffenceDescription);

        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            new activity_school_offence.GetDataTask().execute("");
        }
    }

    private class GetDataTask extends AsyncTask<String, String, String> {

        //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=8&username=omar1&pass=omar1&AuthenticationKey=548442&Student_Id=598&STUDY_YEARS_HDR_ID=309230
        String tag[] = {"events", "username", "pass", "AuthenticationKey", "Student_Id"};
        String value[] = {"8", username, pass, AuthenticationKey, Student_Id};

        final SharedPreferences.Editor edt = sharedpref.edit();

        String Subject = "", To_Person = "", OffenceType = "", date = "", OffenceDescription = "";

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
            json_code = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(1000);

                try {
                    //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=1&username=omar1&pass=omar1
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);

                } catch (Exception e) {
                    e.printStackTrace();
                    return String.valueOf(getResources().getString(R.string.msgServiceisnotavailable));
                }

                try {


                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");
                        jsonArray = new JSONArray(json_code);

                        if (jsonArray.length() > 0) {
                            JSONObject json;
                            json = jsonArray.getJSONObject(0);
                            String Message_Id = json.getString("Message_Id");
                            String msgA = json.getString("msgA");
                            String msg = json.getString("msg");

                            if (Message_Id.equals("0")) {
                                Subject = json.getString("Subject");
                                To_Person = json.getString("To_Person");
                                OffenceType = json.getString("OffenceType");
                                date = json.getString("Date1");
                                OffenceDescription = json.getString("OffenceDescription");

                            } else {
                                return msgA;
                            }

                        } else {
                            return String.valueOf(getResources().getString(R.string.msgServiceisnotavailable));
                        }

                    } else {
                        return String.valueOf(getResources().getString(R.string.msgNoData));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return String.valueOf(getResources().getString(R.string.msgServiceisnotavailable));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            txt_Subject.setText(Subject);
            txt_To_Person.setText(To_Person);
            txt_OffenceType.setText(OffenceType);
            txt_date.setText(date);
            txt_OffenceDescription.setText(OffenceDescription);


            Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            //finish();

            txt_Subject.setText(Subject);
            super.onPostExecute(s);
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
