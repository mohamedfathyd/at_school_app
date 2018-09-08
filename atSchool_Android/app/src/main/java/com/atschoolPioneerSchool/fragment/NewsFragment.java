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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.ActivityMain;
import com.atschoolPioneerSchool.ActivityNewsDetails;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.AdapterNewsList;
import com.atschoolPioneerSchool.adapter.AdapterNewsListWithHeader;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * A simple {@link Fragment} subclass.
 *
 * Activities that contain this fragment must implement the
 *
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 *
 * to handle interaction events.
 *
 * Use the {@link NewsFragment#newInstance} factory method to
 *
 * create an instance of this fragment.
 *
 */

public class NewsFragment extends Fragment {

    GetDataTask myTask = null;
    private RecyclerView recyclerView;
    public AdapterNewsList mAdapter;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    private String NewsType = "0";
    private String ChannelName = "";
    private View view;

    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set type
        NewsType = getArguments().getString("NewsType");
        ChannelName = getArguments().getString("ChannelName");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_news, null);

        if (view == null) {
            String p = "";
        }

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
            myTask = new NewsFragment.GetDataTask(getContext());
            myTask.execute("");
        }

        setTitleandImage();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myTask != null) {
            myTask.cancel(true);
        }
    }

    private class GetDataTask extends AsyncTask<String, News, String> {

        private Context mContext;
        private int cntall;
        private List<News> items = new ArrayList<>();
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
                    //9 : get complaints
                    if (NewsType.equals("9")) {

                        String School_Id = "1";
                        SharedPreferences sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                        School_Id = sharedpref.getString("School_Id", "").trim();

                        String tag[] = {"events", "School_Id"};
                        String value[] = {"15", School_Id};

                        //http://localhost:5149/API_Mobile.aspx?events=15&School_Id=1
                        String url = getResources().getString(R.string.Web_URL);
                        json_code = new post_connection_json().makePostRequest(url, tag, value);
                    } else  //10 : get suggestion
                        if (NewsType.equals("10")) {

                            String School_Id = "1";
                            SharedPreferences sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                            School_Id = sharedpref.getString("School_Id", "").trim();

                            String tag[] = {"events", "School_Id"};
                            String value[] = {"18", School_Id};

                            //http://localhost:5149/API_Mobile.aspx?events=18&School_Id=1
                            String url = getResources().getString(R.string.Web_URL);
                            json_code = new post_connection_json().makePostRequest(url, tag, value);
                        } else  //11 : get Maintenance
                            if (NewsType.equals("11")) {

                                String School_Id = "1";
                                SharedPreferences sharedpref = getActivity().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                                School_Id = sharedpref.getString("School_Id", "").trim();

                                String tag[] = {"events", "School_Id"};
                                String value[] = {"21", School_Id};

                                //http://localhost:5149/API_Mobile.aspx?events=21&School_Id=1
                                String url = getResources().getString(R.string.Web_URL);
                                json_code = new post_connection_json().makePostRequest(url, tag, value);
                            } else {
                                String tag[] = {"events", "type"};
                                String value[] = {"13", NewsType};
                                //http://localhost:5149/API_Mobile.aspx?events=13&type=1
                                String url = getResources().getString(R.string.Web_URL);
                                json_code = new post_connection_json().makePostRequest(url, tag, value);
                            }

                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();

                }

                try {


                    if (isCancelled()) {
                        return null;
                    }

                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");
                        jsonArray = new JSONArray(json_code);
                        cntall = jsonArray.length();
                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;

                            for (i = 0; i < cntall; i++) {

                                try {

                                    if (isCancelled()) {
                                        return null;
                                    }

                                    json = jsonArray.getJSONObject(i);

                                    //9 : get complaints
                                    if (NewsType.equals("9")) {
                                        News objNews = new News(json.getString("Id"), json.getString("TypeName"), json.getString("TypeNameA"),
                                                json.getString("Descr"), json.getString("Descr"), json.getString("Image_CS"),
                                                json.getString("Created_Date"), json.getString("RowNumber"), NewsType, json.getString("Created_ByName"));

                                        items.add(objNews);
                                    } else if (NewsType.equals("10")) {//10 : get suggestion(NewsType.equals("10"))
                                        News objNews = new News(json.getString("Id"), json.getString("TypeName"), json.getString("TypeNameA"),
                                                json.getString("Descr"), json.getString("Descr"), json.getString("Image_CS"),
                                                json.getString("Created_Date"), json.getString("RowNumber"), NewsType, json.getString("Created_ByName"));

                                        items.add(objNews);
                                    } else if (NewsType.equals("11")) {//10 : get Maintenance(NewsType.equals("10"))
                                        News objNews = new News(json.getString("Id"), json.getString("TypeName"), json.getString("TypeNameA"),
                                                json.getString("Descr"), json.getString("Descr"), json.getString("Image_CS"),
                                                json.getString("Created_Date"), json.getString("RowNumber"), NewsType, json.getString("Created_ByName")
                                                , json.getString("Maintenance_Location"), json.getString("Priority"));

                                        items.add(objNews);
                                    } else {
                                        News objNews = new News(json.getString("Id"), json.getString("Name"), json.getString("NameA"),

                                                json.getString("Description").replaceAll("/n", " "), json.getString("DescriptionA").replaceAll("/n", " ")
                                                , json.getString("Img1"),
                                                json.getString("published_Date"), json.getString("RowNumber"), NewsType, ChannelName);

                                        items.add(objNews);
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
        protected void onProgressUpdate(News... values) {

        }

        @Override
        protected void onPostExecute(String msg) {
            if (!isCancelled()) {
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
                        AdapterNewsListWithHeader mAdapter = new AdapterNewsListWithHeader(getContext(), items.get(items.size() - 1), items);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new AdapterNewsListWithHeader.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, News obj, int position) {
                                // ActivityNewsDetails.navigate((activity_school_news) v.getContext(), v.findViewById(R.id.image), obj);
                                ActivityNewsDetails.navigate((ActivityMain) v.getContext(), v.findViewById(R.id.image), obj);
                            }
                        });
                    }
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

    public void setTitleandImage() {

        ImageView image = (ImageView) view.findViewById(R.id.image);
        String title = "";

        if (NewsType.equals("1")) //1   Activities
        {
            title = getString(R.string.menu_Activities);
            image.setImageDrawable(getResources().getDrawable(R.drawable.school_activity));

        } else if (NewsType.equals("2")) //2   Agenda جدول أعمال المدرسه
        {
            title = getString(R.string.menu_Agenda);
            image.setImageDrawable(getResources().getDrawable(R.drawable.school_agenda));

        } else if (NewsType.equals("3")) //3   School Managements  إدارة المدارس
        {
            title = getString(R.string.menu_SchoolManagement);
            image.setImageDrawable(getResources().getDrawable(R.drawable.school_management));

        } else if (NewsType.equals("4"))  //4   School Facilities   مرافق المدرسة
        {
            title = getString(R.string.menu_SchoolFacilities);
            image.setImageDrawable(getResources().getDrawable(R.drawable.school_facilities));

        } else if (NewsType.equals("5"))  //5   Vision الرؤية
        {
            title = getString(R.string.menu_Vision);
            image.setImageDrawable(getResources().getDrawable(R.drawable.school_vision));

        } else if (NewsType.equals("6"))  //6   Mission المهمة
        {
            title = getString(R.string.menu_Mission);
            image.setImageDrawable(getResources().getDrawable(R.drawable.school_mission));

        } else if (NewsType.equals("7")) //7   About حول
        {
            title = getString(R.string.menu_Aboutus);
        } else if (NewsType.equals("8"))  //8   News الأخبار
        {
            title = getString(R.string.menu_News);
            image.setImageDrawable(getResources().getDrawable(R.drawable.m_news_events));
        } else if (NewsType.equals("9")) {
            title = getString(R.string.menu_Complaint);
            image.setImageDrawable(getResources().getDrawable(R.drawable.m_complaint));
        } else if (NewsType.equals("10")) {
            title = getString(R.string.menu_Suggestion);
            image.setImageDrawable(getResources().getDrawable(R.drawable.m_suggestion));
        } else if (NewsType.equals("11")) {
            title = getString(R.string.menu_Maintenance);
            image.setImageDrawable(getResources().getDrawable(R.drawable.m_mentainance));
        }
        //set title by type
        // this.setTitle(title);

        //init Toolbar
        //initToolbar(title);

    }

}
