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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityBusOrderStudents;
import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.PopupStudentHomeLocation;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.AdapterBusOrder;
import com.atschoolPioneerSchool.adapter.AdapterNewsList;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.BusOrder;
import com.atschoolPioneerSchool.model.Channel;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarA on 24/11/2017.
 */

public class BusOrderFragment extends Fragment {

    BusOrderFragment.GetDataTask myTask = null;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;

    private String ChannelName = "";
    InputMethodManager imm;
    BusOrder obj;

    View view;
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bus_order_activities, null);

        //get the input method manager service
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        ChannelName = "Bus";

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
            myTask = new BusOrderFragment.GetDataTask(getContext());
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

    private class GetDataTask extends AsyncTask<String, BusOrder, String> {

        private int cntall;
        private List<BusOrder> items = new ArrayList<>();

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


                    String USER_MASTER_Id = "1";
                    SharedPreferences sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

                    USER_MASTER_Id = sharedpref.getString("USER_MASTER_Id", "").trim();

                    //http://localhost:5149/API_Mobile.aspx?events=36&USER_MASTER_Id=6673
                    String tag[] = {"events", "USER_MASTER_Id"};
                    String value[] = {"36", USER_MASTER_Id};
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
                                    //int Id, String Description, String Start_Time, String End_Time, String Buss_Information,
                                    //  String Direction, String Driver, String Assistant, Channel channel

                                    BusOrder objBus = new BusOrder(
                                            json.getInt("Id"), json.getString("Description"), json.getString("Start_Time"),
                                            json.getString("End_Time"), json.getString("Buss_Information"), json.getString("DirectionA"),
                                            json.getString("Driver"), json.getString("Assistant"));

                                    items.add(objBus);

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
        protected void onProgressUpdate(BusOrder... values) {

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
                    AdapterBusOrder mAdapter = new AdapterBusOrder(getContext(), items.get(items.size() - 1), items);
                    recyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new AdapterBusOrder.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, BusOrder _obj, int position) {
                            obj = _obj;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), ActivityBusOrderStudents.class);
                                    Student friend = new Student("sdsd", 1, Integer.valueOf(obj.Id));

                                    // friend.GCM_Token = obj.GCM_Token;

                                    // Student friend = this.items.get(position);
                                    intent.putExtra("Track_Trans_Order_Id", String.valueOf(obj.Id));
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
