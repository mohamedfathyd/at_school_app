package com.atschoolPioneerSchool.fragment;

/**
 * Created by OmarA on 02/10/2017.
 */

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.AdapterNewsList;
import com.atschoolPioneerSchool.adapter.AdapterChatContactsListWithHeader;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.ChatContacts;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CallSonsFragment extends Fragment {
    FillSonsTask myFillSonsTask = null;
    SendRequestTask mySendRequestTask = null;
    private RecyclerView recyclerView;
    public AdapterNewsList mAdapter;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;

    private String ChannelName = "";
    private View view;

    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_sons, null);


        ChannelName = "Call your Sons";

        // animation transition
        ViewCompat.setTransitionName(view.findViewById(R.id.image), EXTRA_OBJCT);


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //fill data
        if (Constant.StudentList != null) {

            if (!isNetworkAvailable(getActivity().getBaseContext())) {
                Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
            } else {
                myFillSonsTask = new CallSonsFragment.FillSonsTask(getContext());
                myFillSonsTask.execute("");
            }
        } else {
            Toast.makeText(getActivity().getBaseContext(), R.string.msgNoData, Toast.LENGTH_SHORT).show();
        }

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.tab_chat));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myFillSonsTask != null) {
            myFillSonsTask.cancel(true);
        }

        if (mySendRequestTask != null) {
            mySendRequestTask.cancel(true);
        }
    }


    private class FillSonsTask extends AsyncTask<String, ChatContacts, String> {

        private Context mContext;
        private int cntall;
        private List<ChatContacts> items = new ArrayList<>();


        final SharedPreferences.Editor edt = sharedpref.edit();

        public FillSonsTask(Context context) {
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

                if (isCancelled()) {
                    return null;
                }

                for (int i = 0; i < Constant.StudentList.size(); i++) {

                    if (isCancelled()) {
                        return null;
                    }

                    if (Constant.StudentList.get(i).StudentId > 0) {

                        ChatContacts objNews = new ChatContacts(String.valueOf(Constant.StudentList.get(i).StudentId), Constant.StudentList.get(i).StuName,
                                Constant.StudentList.get(i).StuNameA,
                                getString(R.string.Press_to_send_request), Constant.StudentList.get(i).StuName, Constant.StudentList.get(i).StudentImageName,
                                Constant.StudentList.get(i).ClassNameA + "  " + Constant.StudentList.get(i).SectionNameA, "", "14", ChannelName, "", "");

                        items.add(objNews);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ChatContacts... values) {

        }

        @Override
        protected void onPostExecute(String msg) {
            if (!isCancelled()) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);


                AdapterChatContactsListWithHeader mAdapter = new AdapterChatContactsListWithHeader(getContext(), items.get(items.size() - 1), items);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new AdapterChatContactsListWithHeader.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, ChatContacts obj, int position) {

                        ProgressBar rowProgressBar = (ProgressBar) v.findViewById(R.id.rowProgressBar);

                        // send request call kids
                        if (!isNetworkAvailable(getActivity().getBaseContext())) {
                            Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                        } else {
                            mySendRequestTask = new CallSonsFragment.SendRequestTask(getContext(), rowProgressBar, v, position);
                            mySendRequestTask.execute("");

                        }
                    }
                });
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


                Thread.sleep(1000);

                if (isCancelled()) {
                    return null;
                }

                String GUARDIAN_NO = sharedpref.getString("ResidencyNO", "").trim();
                String Name = sharedpref.getString("Name", "").trim();


                if (Constant.StudentList.size() >= mPosition) {
                    Student obj = Constant.StudentList.get(mPosition);

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
                    String arrivalTime = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String arrivalDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


                    String tag[] = {"events", "studentID", "studentNameAr", "studentNameEn", "claseID", "SectionId", "cases", "howRequestID"
                            , "howReqiestName", "arrivalDate", "arrivalTime"};
                    String value[] = {"31", String.valueOf(obj.StudentId), obj.StuNameA, obj.StuName, obj.getClassId(), obj.SectionId, "liv", GUARDIAN_NO
                            , Name, arrivalDate, arrivalTime};

                    //http://localhost:5149/API_Mobile.aspx?events=31&studentID=1
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);


                    if (isCancelled()) {
                        return null;
                    }

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

            if (s.contains("Saved successfully...")) {
                mcellView.setVisibility(View.GONE);
            }

            super.onPostExecute(s);

        }
    }

}
