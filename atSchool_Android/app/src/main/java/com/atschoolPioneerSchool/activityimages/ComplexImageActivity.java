/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.atschoolPioneerSchool.activityimages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.adapter.AdapterChatContactsListWithHeader;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.fragment.ChatFragment;
import com.atschoolPioneerSchool.fragmentimages.ImageGridFragment;
import com.atschoolPioneerSchool.fragmentimages.ImageListFragment;
import com.atschoolPioneerSchool.model.ChatContacts;
import com.atschoolPioneerSchool.model.Student;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComplexImageActivity extends FragmentActivity {

    GetImagesGalleryTask myTask = null;
    private static final String STATE_POSITION = "STATE_POSITION";
    private ViewPager pager;
    SwipeRefreshLayout str;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    int pagerPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_complex);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        //clear old data
        Constant.IMAGES = new String[0];
        Constant.IMAGES_Description = new String[0];
        Constant.IMAGES_DescriptionA = new String[0];

        //fill data
        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            myTask = new ComplexImageActivity.GetImagesGalleryTask(this);
            myTask.execute("");
        }

        //   ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ComplexImageActivity.this));
        pagerPosition = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_POSITION);

    }

    @Override
    public void onBackPressed() {
        if (myTask != null) {
            myTask.cancel(true);
        }
        //close activity
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        Fragment listFragment;
        Fragment gridFragment;

        ImagePagerAdapter(FragmentManager fm) {
            super(fm);
            listFragment = new ImageListFragment();
            gridFragment = new ImageGridFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return listFragment;
                case 1:
                    return gridFragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_list);
                case 1:
                    return getString(R.string.title_grid);
                default:
                    return null;
            }
        }
    }

    private class GetImagesGalleryTask extends AsyncTask<String, ChatContacts, String> {

        private Context mContext;
        private int cntall;

        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetImagesGalleryTask(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
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
                    String School_Id = "1";
                    SharedPreferences sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                    School_Id = sharedpref.getString("School_Id", "").trim();

                    String tag[] = {"events", "School_Id"};
                    String value[] = {"30", School_Id};

                    //http://irbid.lms-school.com/API_Mobile.aspx?events=30&School_Id=1
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
                        Constant.IMAGES = new String[cntall];
                        Constant.IMAGES_Description = new String[cntall];
                        Constant.IMAGES_DescriptionA = new String[cntall];

                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;

                            for (i = 0; i < cntall; i++) {

                                if (isCancelled()) {
                                    return null;
                                }

                                try {
                                    json = jsonArray.getJSONObject(i);
                                    Constant.IMAGES[i] = getString(R.string.URL_Gallery_Images) + json.getString("ImageName");
                                    Constant.IMAGES_Description[i] = json.getString("Description");
                                    Constant.IMAGES_DescriptionA[i] = json.getString("DescriptionA");

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


            if (msg != null) {
                if (!msg.isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.msgNoData), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!isCancelled()) {

                    if (ImageLoader.getInstance().isInited()) {
                        ImageLoader.getInstance().destroy();
                    }

                    ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ComplexImageActivity.this));
                }

                pager = (ViewPager) findViewById(R.id.pager);
                pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
                pager.setCurrentItem(pagerPosition);
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