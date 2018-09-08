package com.atschoolPioneerSchool.fragment;

/**
 * Created by OmarA on 08/10/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityFriendDetails;
import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.ActivityNotification;
import com.atschoolPioneerSchool.ActivityWeeklyPlanDetails;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.AdapterWeekNumbers;
import com.atschoolPioneerSchool.adapter.AdapterNewsList;
import com.atschoolPioneerSchool.adapter.FeedListAdapter;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.WeekNumbers;
import com.atschoolPioneerSchool.model.Feed;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendWeeklyPlanFragment extends Fragment {

    GetWeeklyPlanWeeks myTaskWeeks = null;
    GetWeeklyPlanDays myTaskDays = null;
    GetWeeklyPlanSubjects myTaskSubjects = null;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    public static Student objStudent;
    static String ViewWeeklyPlanDaysInMobile = "0";
    static String ViewWeeklyPlanSubjectsInMobile = "0";
    private Button btn_ViewWeek;
    private Button btn_ViewDays;
    private String SelectedWeekNo = "0";
    private String SelectedDay = "0";
    private String SelectedSubject = "0";
    private WeekNumbers Selectedobj;

    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_weekly_plan, null);

        // animation transition
        ViewCompat.setTransitionName(view.findViewById(R.id.image), EXTRA_OBJCT);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btn_ViewWeek = (Button) view.findViewById(R.id.btn_ViewWeek);
        btn_ViewDays = (Button) view.findViewById(R.id.btn_ViewDays);
        sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        ViewWeeklyPlanDaysInMobile = sharedpref.getString("ViewWeeklyPlanDaysInMobile", "0").trim();
        ViewWeeklyPlanSubjectsInMobile = sharedpref.getString("ViewWeeklyPlanSubjectsInMobile", "0").trim();


        btn_ViewWeek.setVisibility(View.GONE);
        btn_ViewDays.setVisibility(View.GONE);
        //fill data
        if (!isNetworkAvailable(getActivity().getBaseContext())) {
            Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            myTaskWeeks = new GetWeeklyPlanWeeks(getContext());
            myTaskWeeks.execute("");
        }

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.tab_chat));

        btn_ViewWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fill data
                if (!isNetworkAvailable(getActivity().getBaseContext())) {
                    Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                } else {

                    btn_ViewWeek.setVisibility(View.GONE);
                    btn_ViewDays.setVisibility(View.GONE);

                    myTaskWeeks = new GetWeeklyPlanWeeks(getContext());
                    myTaskWeeks.execute("");
                }
            }
        });


        btn_ViewDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fill data
                if (!isNetworkAvailable(getActivity().getBaseContext())) {
                    Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                } else {

                    btn_ViewWeek.setVisibility(View.GONE);
                    btn_ViewDays.setVisibility(View.GONE);

                    myTaskDays = new GetWeeklyPlanDays(getContext());
                    myTaskDays.execute("");
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myTaskWeeks != null) {
            myTaskWeeks.cancel(true);
        }

        if (myTaskDays != null) {
            myTaskDays.cancel(true);
        }

        if (myTaskSubjects != null) {
            myTaskSubjects.cancel(true);
        }
    }

    private class GetWeeklyPlanWeeks extends AsyncTask<String, WeekNumbers, String> {

        private Context mContext;
        private int cntall;
        private List<WeekNumbers> items = new ArrayList<>();

        public GetWeeklyPlanWeeks(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();

            json_code = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }
                try {
                    String tag[] = {"events", "Class_Id", "CLASS_SECTION_ID"};
                    String value[] = {"33", String.valueOf(Constant.SelectedStudent.getClassId()), String.valueOf(Constant.SelectedStudent.CLASS_SECTION_ID)};

                    //http://localhost:5149/API_Mobile.aspx?events=15&School_Id=1
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);


                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();

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

                                    WeekNumbers objWeekNumbers =
                                            new WeekNumbers(json.getString("Id"), json.getString("Name"), json.getString("NameA"), json.getString("RowNumber")
                                                    , getString(R.string.strWeekNumber), false);

                                    items.add(objWeekNumbers);

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
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
                    return e.getMessage();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(WeekNumbers... values) {

        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (msg != null) {
                if (!msg.isEmpty()) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                        Intent i = new Intent(getContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                }
            } else {
                if (!isCancelled()) {
                    AdapterWeekNumbers mAdapter = new AdapterWeekNumbers(getContext(), items.get(items.size() - 1), items);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new AdapterWeekNumbers.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, WeekNumbers obj, int position) {

                            //check if open the weekly plane direct or view subjects before
                            if (ViewWeeklyPlanSubjectsInMobile.equals("0")) {
                                Intent intent = new Intent(getActivity(), ActivityWeeklyPlanDetails.class);
                                intent.putExtra(ActivityWeeklyPlanDetails.KEY_WeekNumbers, obj);
                                startActivity(intent);
                            } else {

                                //fill data
                                Selectedobj = obj;
                                SelectedWeekNo = obj.Id;
                                if (!isNetworkAvailable(getActivity().getBaseContext())) {
                                    Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                                } else {
                                    myTaskDays = new GetWeeklyPlanDays(getContext());
                                    myTaskDays.execute("");
                                }
                            }
                        }
                    });

                }
            }
            super.onPostExecute(msg);
        }
    }

    private class GetWeeklyPlanDays extends AsyncTask<String, WeekNumbers, String> {

        private Context mContext;
        private int cntall;
        private List<WeekNumbers> items = new ArrayList<>();

        public GetWeeklyPlanDays(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();

            json_code = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }
                try {
                    String tag[] = {"events", "Week_No", "Class_Id", "CLASS_SECTION_ID"};
                    String value[] = {"45", SelectedWeekNo, String.valueOf(Constant.SelectedStudent.getClassId()),
                            String.valueOf(Constant.SelectedStudent.CLASS_SECTION_ID)};

                    //http://localhost:5149/API_Mobile.aspx?events=15&School_Id=1
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);


                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();

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

                                    WeekNumbers objWeekNumbers =
                                            new WeekNumbers(json.getString("SCH_STUDY_DAY_ID"), "",
                                                    json.getString("SCH_STUDY_DAY_NAMEA"), "", json.getString("SCH_STUDY_DAY_NAME") + "  -  "
                                                    + json.getString("SCH_STUDY_DAY_NAMEA"), true);

                                    items.add(objWeekNumbers);

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
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
                    return e.getMessage();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(WeekNumbers... values) {

        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_ViewWeek.setVisibility(View.VISIBLE);
            btn_ViewDays.setVisibility(View.GONE);

            if (msg != null) {
                if (!msg.isEmpty()) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                        Intent i = new Intent(getContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                }
            } else {
                if (!isCancelled()) {
                    AdapterWeekNumbers mAdapter = new AdapterWeekNumbers(getContext(), items.get(items.size() - 1), items);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new AdapterWeekNumbers.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, WeekNumbers obj, int position) {

                            //check if open the weekly plane direct or view subjects before
                            if (ViewWeeklyPlanDaysInMobile.equals("0")) {
                                Intent intent = new Intent(getActivity(), ActivityWeeklyPlanDetails.class);
                                intent.putExtra(ActivityWeeklyPlanDetails.KEY_WeekNumbers, obj);
                                startActivity(intent);
                            } else {
                                //fill data
                                SelectedDay = obj.Id;
                                Selectedobj.SCH_STUDY_DAY_ID = SelectedDay;
                                if (!isNetworkAvailable(getActivity().getBaseContext())) {
                                    Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                                } else {
                                    myTaskSubjects = new GetWeeklyPlanSubjects(getContext());
                                    myTaskSubjects.execute("");
                                }
                            }

                        }
                    });

                }
            }
            super.onPostExecute(msg);
        }
    }

    private class GetWeeklyPlanSubjects extends AsyncTask<String, WeekNumbers, String> {

        private Context mContext;
        private int cntall;
        private List<WeekNumbers> items = new ArrayList<>();

        public GetWeeklyPlanSubjects(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();

            json_code = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }
                try {
                    String tag[] = {"events", "Week_No", "Class_Id", "CLASS_SECTION_ID"};
                    String value[] = {"46", SelectedWeekNo, String.valueOf(Constant.SelectedStudent.getClassId()), String.valueOf(Constant.SelectedStudent.CLASS_SECTION_ID)};

                    //http://localhost:5149/API_Mobile.aspx?events=15&School_Id=1
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);


                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();

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
                                    WeekNumbers objWeekNumbers =
                                            new WeekNumbers(json.getString("SCH_SUBJECT_ID"), "",
                                                    json.getString("SUBJECT_NAMEA"), "", json.getString("SUBJECT_NAME") + "  -  " + json.getString("SUBJECT_NAMEA"), true);

                                    items.add(objWeekNumbers);

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
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
                    return e.getMessage();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(WeekNumbers... values) {

        }

        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_ViewWeek.setVisibility(View.GONE);
            btn_ViewDays.setVisibility(View.VISIBLE);

            if (msg != null) {
                if (!msg.isEmpty()) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                        Intent i = new Intent(getContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                }
            } else {
                if (!isCancelled()) {
                    AdapterWeekNumbers mAdapter = new AdapterWeekNumbers(getContext(), items.get(items.size() - 1), items);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new AdapterWeekNumbers.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, WeekNumbers obj, int position) {

                            SelectedSubject = obj.Id;
                            Selectedobj.SCH_SUBJECT_ID = obj.Id;
                            Intent intent = new Intent(getActivity(), ActivityWeeklyPlanDetails.class);
                            //  Student friend = new Student("Notifications", 1,  2625  );
                            intent.putExtra(ActivityWeeklyPlanDetails.KEY_WeekNumbers, Selectedobj);
                            startActivity(intent);

                        }
                    });

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

}
