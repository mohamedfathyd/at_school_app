package com.atschoolPioneerSchool.fragment;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityFriendDetails;
import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.AdapterChatContactsListWithHeader;
import com.atschoolPioneerSchool.adapter.AdapterNewsList;
import com.atschoolPioneerSchool.adapter.FeedListAdapter;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.ChatContacts;
import com.atschoolPioneerSchool.model.Feed;
import com.atschoolPioneerSchool.model.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResourceType")
public class FriendActivitiesFragment extends Fragment {

    GetDataTask myTask = null;
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
        View view = inflater.inflate(R.layout.fragment_friend_activities, null);


        ChannelName = "Communication";

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
            myTask = new GetDataTask(getContext());
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

    private class GetDataTask extends AsyncTask<String, ChatContacts, String> {

        private Context mContext;
        private int cntall;
        private List<ChatContacts> items = new ArrayList<>();
        private double Balance = 0;


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

                if (isCancelled()) {
                    return null;
                }
                try {
                    String USER_TYPE_Id = "1";
                    String USER_MASTER_Id = "1";
                    SharedPreferences sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                    USER_TYPE_Id = sharedpref.getString("USER_TYPE_Id", "").trim();
                    USER_MASTER_Id = sharedpref.getString("USER_MASTER_Id", "").trim();


                    ////http://localhost:5149/API_Mobile.aspx?events=26&Login_USER_TYPE=1&USER_MASTER_Id=1&NumberRowsInGrid=10&SelectedPageNumer=1
                    String tag[] = {"events", "NumberRowsInGrid", "SelectedPageNumer", "USER_MASTER_Id", "USER_TYPE_Id"};
                    String value[] = {"26", "25", "0", USER_MASTER_Id, USER_TYPE_Id};

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

                                    ChatContacts objNews = new ChatContacts(json.getString("Id"), json.getString("USER_NAME"), json.getString("USER_NAME_A"),
                                            json.getString("ResidencyNO"), json.getString("USER_CODE"), json.getString("Profile_Picture"),
                                            json.getString("Id"), json.getString("RowNumber"), "13", ChannelName, json.getString("GCM_Token"), json.getString("APNS_Token"),
                                            json.getString("ChatTextMessage"), json.getString("ChatSend_Date"), json.getString("ChatRead_Date"));

                                    items.add(objNews);

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
        protected void onProgressUpdate(ChatContacts... values) {

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
                    AdapterChatContactsListWithHeader mAdapter = new AdapterChatContactsListWithHeader(getContext(), items.get(items.size() - 1), items);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new AdapterChatContactsListWithHeader.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, ChatContacts obj, int position) {
                            // ActivityChatContactsDetails.navigate((activity_school_news) v.getContext(), v.findViewById(R.id.image), obj);
                            //ActivityChatContactsDetails.navigate((ActivityMain) v.getContext(), v.findViewById(R.id.image), obj);

                            Intent intent = new Intent(getActivity(), ActivityChatDetails.class);

                            Student friend = new Student("Omar Alfaqeeh", 1, 2625, "");
                            intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
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
