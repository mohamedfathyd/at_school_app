package com.atschoolPioneerSchool.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityBusOrderStudents;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.activity_school_evaluation;
import com.atschoolPioneerSchool.activity_school_grades;
import com.atschoolPioneerSchool.activity_school_offence;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.BusOrderStudent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FriendAboutFragment extends Fragment {

    View view;
    private Button btn_absent;
    private Button btn_notconfirm;

    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    SendRequestTask mySendRequestTask = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_about, null);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        btn_absent = (Button) view.findViewById(R.id.btn_absent);
        btn_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // send request call kids
                    if (!isNetworkAvailable(getContext())) {
                        Toast.makeText(getContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                    } else {

                        if (!Constant.SelectedStudent.USER_MASTER_Id_Student.equals("")) {
                            mySendRequestTask = new SendRequestTask(Constant.SelectedStudent.USER_MASTER_Id_Student, 1, 0);
                            mySendRequestTask.execute("");
                        }
                    }

                } catch (Exception x) {
                    String c = x.getMessage();
                }
            }
        });


        btn_notconfirm = (Button) view.findViewById(R.id.btn_notconfirm);
        btn_notconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // send request call kids
                    if (!isNetworkAvailable(getContext())) {
                        Toast.makeText(getContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                    } else {

                        if (!Constant.SelectedStudent.USER_MASTER_Id_Student.equals("")) {
                            mySendRequestTask = new SendRequestTask(Constant.SelectedStudent.USER_MASTER_Id_Student, 0, 1);
                            mySendRequestTask.execute("");
                        }
                    }

                } catch (Exception x) {
                    String c = x.getMessage();
                }
            }
        });
        final LinearLayout lyt_group_cat = (LinearLayout) view.findViewById(R.id.lyt_grades);
        lyt_group_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), activity_school_grades.class);
                startActivity(i);

            }
        });

        final LinearLayout lyt_evaluation = (LinearLayout) view.findViewById(R.id.lyt_evaluation);
        lyt_evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), activity_school_evaluation.class);
                startActivity(i);

            }
        });


        final LinearLayout lyt_offence = (LinearLayout) view.findViewById(R.id.lyt_offence);
        lyt_offence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), activity_school_offence.class);
                startActivity(i);

            }
        });

        return view;
    }

    private class SendRequestTask extends AsyncTask<String, String, String> {

        private String mUSER_MASTER_Id_Student = "";
        int mIsAbsent = 0;
        int mIsNotConfirm = 0;

        public SendRequestTask(String USER_MASTER_Id_Student, int IsAbsent, int IsNotConfirm) {

            mUSER_MASTER_Id_Student = USER_MASTER_Id_Student;
            mIsAbsent = IsAbsent;
            mIsNotConfirm = IsNotConfirm;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            btn_absent.setVisibility(View.GONE);
            btn_notconfirm.setVisibility(View.GONE);

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

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String tDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                        .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

                SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                String sTime = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                        .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

                //http://localhost:5149/API_Mobile.aspx?events=41&Track_Trans_Trip_Students_Id=53&
                // Student_Id=6116&StepIn_Time=07:05&StepOut_Time=08:00&Track_Trans_Trip_Id=6&IsAbsent=1&Description=ll
                String url = getResources().getString(R.string.Web_URL);

                if (mIsAbsent > 0) {

                    //http://irbid.lms-school.com/API_Mobile.aspx?events=47&USER_MASTER_Student_Id=3215&Trip_Date=20180421&Time=%27%27&NotConfirm_Time=''&IsAbsent=1
                    String tag[] = {"events", "USER_MASTER_Student_Id", "Trip_Date", "Time", "NotConfirm_Time", "IsAbsent"};
                    String value[] = {"47", mUSER_MASTER_Id_Student, tDate, sTime, "", "1"};

                    json_code = new post_connection_json().makePostRequest(url, tag, value);
                } else if (mIsNotConfirm > 0) {

                    //http://irbid.lms-school.com/API_Mobile.aspx?events=47&USER_MASTER_Student_Id=3215&Trip_Date=20180421&Time=%27%27&NotConfirm_Time=12:00&IsAbsent=0
                    String tag[] = {"events", "USER_MASTER_Student_Id", "Trip_Date", "Time", "NotConfirm_Time", "IsAbsent"};
                    String value[] = {"47", mUSER_MASTER_Id_Student, tDate, sTime, sTime, "0"};

                    json_code = new post_connection_json().makePostRequest(url, tag, value);
                }


                if (isCancelled()) {
                    return null;
                }

                try {

                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");
                        jsonArray = new JSONArray(json_code);

                        if (jsonArray.length() > 0) {

                            Toast.makeText(getContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

                        return String.valueOf(R.string.msgFaildLogin);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();

                return e.getMessage();
            }

            return json_code;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            btn_absent.setVisibility(View.VISIBLE);
            btn_notconfirm.setVisibility(View.VISIBLE);
            super.onPostExecute(s);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mySendRequestTask != null) {
            mySendRequestTask.cancel(true);
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
}
