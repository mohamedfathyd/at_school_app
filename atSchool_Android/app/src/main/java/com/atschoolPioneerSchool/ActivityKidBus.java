package com.atschoolPioneerSchool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.AdapterNewsListWithHeader;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.fragment.BusAttendanceFragment;
import com.atschoolPioneerSchool.fragment.BusLocationFragment;
import com.atschoolPioneerSchool.fragment.CategoryFragment;
import com.atschoolPioneerSchool.fragment.FriendAboutFragment;
import com.atschoolPioneerSchool.fragment.FriendActivitiesFragment;
import com.atschoolPioneerSchool.fragment.FriendPhotosFragment;
import com.atschoolPioneerSchool.fragment.FriendWeeklyPlanFragment;
import com.atschoolPioneerSchool.fragment.MarefahFragment;
import com.atschoolPioneerSchool.fragment.NewsFragment;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.Student;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by OmarA on 21/12/2017.
 */

public class ActivityKidBus extends AppCompatActivity {
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.Student";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Student objS) {
        Intent intent = new Intent(activity, ActivityKidBus.class);

        SharedPreferences sharedpref = transitionImage.getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = sharedpref.edit();
        edt.putString("SelectedStudentId", String.valueOf(objS.StudentId));
        edt.commit();

        intent.putExtra(EXTRA_OBJCT, objS);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_OBJCT);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private ViewPager mViewPager;
    private BusLocationFragment frag_BusLocationFragment;
    private BusAttendanceFragment frag_BusAttendance;

    private ActionBar actionBar;
    public static Student friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_bus);

        // animation transition
        ViewCompat.setTransitionName(findViewById(android.R.id.content), EXTRA_OBJCT);


        // animation transition
        //     ViewCompat.setTransitionName(findViewById(R.id.image), EXTRA_OBJCT);


        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // get extra object
        friend = (Student) getIntent().getSerializableExtra(EXTRA_OBJCT);

        // scollable toolbar
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(friend.getName());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager mViewPager) {
        ActivityKidBus.MyPagerAdapter adapter = new ActivityKidBus.MyPagerAdapter(getSupportFragmentManager());

        if (frag_BusLocationFragment == null) {
            frag_BusLocationFragment = new BusLocationFragment();
        }

        if (frag_BusAttendance == null) {
            frag_BusAttendance = new BusAttendanceFragment();
            Bundle bundle = new Bundle();

            bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Suggestion));
            actionBar.setTitle(getString(R.string.menu_marefah));
            bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_marefah));

            bundle.putString("ChannelName", getString(R.string.menu_marefah));
            frag_BusAttendance.setArguments(bundle);
        }

        adapter.addFragment(frag_BusLocationFragment, getString(R.string.menu_BusLocation));
        adapter.addFragment(frag_BusAttendance, getString(R.string.strPhotosinBus));


        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_activity_friend_details, menu);
        return true;
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}

