package com.atschoolPioneerSchool.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityBusOrderStudents;
import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.ActivityRegisterStudentsAttendanceEntry;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.ClassesListAdapter;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.Classes;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterStudentsAttendanceFragment  extends Fragment {

    RegisterStudentsAttendanceFragment.GetDataTask myTask = null;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;

    private String ChannelName = "";
    InputMethodManager imm;
    Classes obj;

    View view;
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_students_attendance, null);

        //get the input method manager service
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        ChannelName = "Register students attendance";

        // animation transition
        ViewCompat.setTransitionName(view.findViewById(R.id.image), EXTRA_OBJCT);


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        //fill data
        if (!isNetworkAvailable(getActivity().getBaseContext())) {
            Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            myTask = new RegisterStudentsAttendanceFragment.GetDataTask(getContext());
            myTask.execute("");
        }

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.tab_chat));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myTask != null) {
            myTask.cancel(true);
        }

    }

    private class GetDataTask extends AsyncTask<String, Classes, String> {

        private int cntall;
        private List<Classes> items = new ArrayList<>();

        private Context mContext;

        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetDataTask(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();

            json_code = "";
            edt.commit();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                try {


                    if (isCancelled()) {
                        return null;
                    }


                    String Employee_Id = "1";
                    SharedPreferences sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

                    Employee_Id = sharedpref.getString("Employee_Id", "").trim();

                    //http://localhost:5149/API_Mobile.aspx?events=36&USER_MASTER_Id=6673
                    String tag[] = {"events", "Employee_Id"};
                    String value[] = {"11", Employee_Id};
                    items = new ArrayList<>();

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
                                    Classes objClass = new Classes( );

                                    objClass.CLASS_SECTION_ID = json.getString("CLASS_SECTION_ID");
                                    objClass.SectionNameA = json.getString("SecNameA");
                                    objClass.SectionName = json.getString("SecName");

                                    if(!objClass.CLASS_SECTION_ID.equals("")) {
                                        items.add(objClass);
                                    }

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
        protected void onProgressUpdate(Classes... values) {

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
                if (!this.isCancelled()) {
                    ClassesListAdapter mAdapter = new ClassesListAdapter(getContext(),   items);
                    recyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new ClassesListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, Classes _obj, int position) {
                            obj = _obj;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), ActivityRegisterStudentsAttendanceEntry.class);
                                    Student friend = new Student("sdsd", 1, Integer.valueOf(obj.CLASS_SECTION_ID));

                                    // friend.GCM_Token = obj.GCM_Token;

                                    // Student friend = this.items.get(position);
                                    intent.putExtra("CLASS_SECTION_ID", String.valueOf(obj.CLASS_SECTION_ID));
                                    intent.putExtra("CLASS_SECTION_NAME", String.valueOf(obj.SectionNameA));
                                    intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
                                    startActivity(intent);
                                }
                            });


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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

}
