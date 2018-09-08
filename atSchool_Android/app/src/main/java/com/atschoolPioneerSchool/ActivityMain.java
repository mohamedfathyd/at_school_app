 package com.atschoolPioneerSchool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.GlobalVariable;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.fragment.BusLocationFragment;
import com.atschoolPioneerSchool.fragment.BusOrderFragment;
import com.atschoolPioneerSchool.fragment.CallSonsFragment;
import com.atschoolPioneerSchool.fragment.CategoryFragment;
import com.atschoolPioneerSchool.fragment.ChatFragment;
import com.atschoolPioneerSchool.fragment.HomeLocationFragment;
import com.atschoolPioneerSchool.fragment.MarefahFragment;
import com.atschoolPioneerSchool.fragment.NewsFragment;
import com.atschoolPioneerSchool.fragment.RegisterStudentsAttendanceFragment;

 public class ActivityMain extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private Menu menu;
    private View parent_view;
    private GlobalVariable global;
    private NavigationView nav_view;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor edt;
    private boolean InMainFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //change interface language
        Constant.ChangeLanguage(getApplicationContext());

        setContentView(R.layout.activity_main);
        parent_view = findViewById(R.id.main_content);
        global = (GlobalVariable) getApplication();

        initToolbar();

        setupDrawerLayout();


        //read menu if call from Category fragment
        String SelectedMenu = "";

        if (getIntent().getExtras() != null) {
            SelectedMenu = getIntent().getExtras().getString("SelectedMenu");
        }

        if (!SelectedMenu.equals("")) {
            displayView(Integer.valueOf(SelectedMenu));

        } else {

            // display first page
            displayView(R.id.nav_Atschool);
            actionBar.setTitle(R.string.menu_Main);
        }

        // for system bar in lollipop
        Tools.systemBarLolipop(this);

        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        edt = sharedpref.edit();
    }

    @Override
    public void onBackPressed() {

        if (!actionBar.getTitle().equals(getResources().getString(R.string.menu_Main))) {
            // display first page
            displayView(R.id.nav_Atschool);
            actionBar.setTitle(R.string.menu_Main);
        } else {

            Intent i12 = new Intent(getApplicationContext(), start_page.class);
            startActivity(i12);

            finish();
        }

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    @Override
    protected void onResume() {
        updateChartCounter(nav_view, R.id.nav_Activities, global.getCartItem());
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_view = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                updateChartCounter(nav_view, R.id.nav_Activities, global.getCartItem());
                super.onDrawerOpened(drawerView);
            }
        };


        //set lofin account name

        View hView = nav_view.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.txtLoginAccountName);
        nav_user.setText(Constant.AccountName);


        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);
        updateChartCounter(nav_view, R.id.nav_Activities, global.getCartItem());

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                actionBar.setTitle(menuItem.getTitle());
                displayView(menuItem.getItemId());
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //5 = parent
        if (!Constant.USER_TYPE_Id.equals("5")) {
            getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_Atschool:
                onBackPressed();
                break;

            case R.id.action_New_Complaint:
                Intent i1 = new Intent(getApplicationContext(), activity_insert_complaint.class);
                startActivity(i1);
                break;
            case R.id.action_New_Suggestion:
                Intent i2 = new Intent(getApplicationContext(), activity_insert_suggestion.class);
                startActivity(i2);
                break;
            case R.id.action_New_Maintenance:
                Intent i3 = new Intent(getApplicationContext(), activity_insert_maintenance.class);
                startActivity(i3);
                break;
            case R.id.action_cart:
                displayView(R.id.nav_Activities);
                actionBar.setTitle(R.string.menu_Activities);
                break;
            case R.id.action_credit:
                Snackbar.make(parent_view, "Credit Clicked", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Snackbar.make(parent_view, "Setting Clicked", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_about: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.menu_Aboutus));
                builder.setMessage(getString(R.string.about_text));
                builder.setNeutralButton(getString(R.string.OK), null);
                builder.show();
                break;
            }
            case R.id.action_Logout:

                //remove password

                edt.putString("Password", "");
                edt.putString("LastValidPassword", "");
                edt.commit();


                Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    private void displayView(int id) {
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        Fragment fragment = null;
        boolean isLogout = false;

        Bundle bundle = new Bundle();
        InMainFragment = false;

        switch (id) {
            case R.id.nav_Atschool:
                InMainFragment = true;
                fragment = new CategoryFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Main));
                break;
            case R.id.
                    nav_TestUpload:
                isLogout = true;

                Intent i = new Intent(getApplicationContext(), ActivityTestSelectFile.class);
                startActivity(i);
                break;


            case R.id.nav_Activities:

                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_Activities));
                bundle.putString("NewsType", "1");
                bundle.putString("ChannelName", getString(R.string.menu_Activities));


                break;

            case R.id.nav_Agenda:
                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_Agenda));
                bundle.putString("NewsType", "2");
                bundle.putString("ChannelName", getString(R.string.menu_Agenda));
                break;


            case R.id.nav_SchoolManagement:
                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_SchoolManagement));
                bundle.putString("NewsType", "3");
                bundle.putString("ChannelName", getString(R.string.menu_SchoolManagement));
                break;

            case R.id.nav_SchoolFacilities:


                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_SchoolFacilities));
                bundle.putString("NewsType", "4");
                bundle.putString("ChannelName", getString(R.string.menu_SchoolFacilities));
                break;

            case R.id.nav_HomeLocation:


                HomeLocationFragment busLocationFragment1 = new HomeLocationFragment();

                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_HomeLocation));
                actionBar.setTitle(getString(R.string.menu_HomeLocation));
                bundle.putString("ChannelName", getString(R.string.menu_HomeLocation));

                busLocationFragment1.setArguments(bundle);

                if (busLocationFragment1 != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                    HomeLocationFragment myFragment;
                    myFragment = (HomeLocationFragment) getSupportFragmentManager().findFragmentByTag("HomeLocationFragment");

                    if (myFragment == null) {


                        fragmentTransaction.replace(R.id.frame_content, busLocationFragment1, "HomeLocationFragment");
                        fragmentTransaction.commit();

                    }
                }


                isLogout = true;
                break;
            case R.id.nav_News:

                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_News));
                bundle.putString("NewsType", "8");
                bundle.putString("ChannelName", getString(R.string.menu_News));


                break;

            case R.id.nav_Call_Your_Sons:

                if (Constant.StudentList.size() > 0) {
                    if (Constant.StudentList.get(0).StudentId > 0) {

                        CallSonsFragment CallSonsFragment1 = new CallSonsFragment();
                        bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Call_your_Sons));
                        actionBar.setTitle(getString(R.string.menu_Call_your_Sons));
                        bundle.putString("ChannelName", getString(R.string.menu_Call_your_Sons));

                        CallSonsFragment1.setArguments(bundle);

                        if (CallSonsFragment1 != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_content, CallSonsFragment1, "");
                            fragmentTransaction.commit();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), R.string.sons_registered, Toast.LENGTH_SHORT).show();
                    }
                }

                isLogout = true;

                break;

            case R.id.nav_Communication:

                ChatFragment fragmentChat = new ChatFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Communication));
                actionBar.setTitle(getString(R.string.menu_Communication));
                bundle.putString("ChannelName", getString(R.string.menu_Communication));

                fragmentChat.setArguments(bundle);

                if (fragmentChat != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, fragmentChat, "");
                    fragmentTransaction.commit();
                }

                isLogout = true;
                break;
            case R.id.nav_Busses:

                BusOrderFragment busOrderFragment = new BusOrderFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_BussesTrip));
                actionBar.setTitle(getString(R.string.menu_BussesTrip));
                bundle.putString("ChannelName", getString(R.string.menu_BussesTrip));

                busOrderFragment.setArguments(bundle);

                if (busOrderFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, busOrderFragment, "");
                    fragmentTransaction.commit();
                }

                isLogout = true;
                break;

            case R.id.nav_Register_students_attendance:

                RegisterStudentsAttendanceFragment RegStuAtteFragment = new RegisterStudentsAttendanceFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_RegisterStudentsAttendance));
                actionBar.setTitle(getString(R.string.menu_RegisterStudentsAttendance));
                bundle.putString("ChannelName", getString(R.string.menu_RegisterStudentsAttendance));

                RegStuAtteFragment.setArguments(bundle);

                if (RegStuAtteFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, RegStuAtteFragment, "");
                    fragmentTransaction.commit();
                }

                isLogout = true;
                break;


            case R.id.nav_BussesLocation:

                BusLocationFragment busLocationFragment = new BusLocationFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_BusLocation));
                actionBar.setTitle(getString(R.string.menu_BusLocation));
                bundle.putString("ChannelName", getString(R.string.menu_BusLocation));

                busLocationFragment.setArguments(bundle);

                if (busLocationFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, busLocationFragment, "");
                    fragmentTransaction.commit();
                }

                isLogout = true;
                break;

            case R.id.nav_marefah:

                MarefahFragment marefahFragment = new MarefahFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_marefah));
                actionBar.setTitle(getString(R.string.menu_marefah));
                bundle.putString("ChannelName", getString(R.string.menu_marefah));

                marefahFragment.setArguments(bundle);

                if (marefahFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, marefahFragment, "");
                    fragmentTransaction.commit();
                }

                isLogout = true;
                break;
            case R.id.nav_Complaint:
                sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                String Use_Complaint_As_Responsible = sharedpref.getString("Use_Complaint_As_Responsible", "");

                if (!Use_Complaint_As_Responsible.equals("1")) {

                    Intent i1 = new Intent(getApplicationContext(), activity_insert_complaint.class);
                    startActivity(i1);

                    isLogout = true;
                } else {
                    fragment = new NewsFragment();
                    bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Complaint));
                    actionBar.setTitle(getString(R.string.menu_Complaint));
                    bundle.putString("NewsType", "9");
                    bundle.putString("ChannelName", getString(R.string.menu_Complaint));
                }

                break;
            case R.id.nav_Suggestion:

                sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                String Use_Suggestions_As_Responsible = sharedpref.getString("Use_Suggestions_As_Responsible", "");

                if (!Use_Suggestions_As_Responsible.equals("1")) {

                    Intent i1 = new Intent(getApplicationContext(), activity_insert_suggestion.class);
                    startActivity(i1);

                    isLogout = true;
                } else {
                    fragment = new NewsFragment();
                    bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Suggestion));
                    actionBar.setTitle(getString(R.string.menu_Suggestion));
                    bundle.putString("NewsType", "10");
                    bundle.putString("ChannelName", getString(R.string.menu_Suggestion));
                }
                break;

            case R.id.nav_Maintenance:

                sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                String Use_Maintenance_As_Responsible = sharedpref.getString("Use_Maintenance_As_Responsible", "");

                if (!Use_Maintenance_As_Responsible.equals("1")) {

                    Intent i1 = new Intent(getApplicationContext(), activity_insert_maintenance.class);
                    startActivity(i1);

                    isLogout = true;
                } else {
                    fragment = new NewsFragment();
                    bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_Maintenance));
                    actionBar.setTitle(getString(R.string.menu_Maintenance));
                    bundle.putString("NewsType", "11");
                    bundle.putString("ChannelName", getString(R.string.menu_Maintenance));
                }
                break;

            case R.id.nav_Settings:

                isLogout = true;

                break;


            case R.id.nav_Vision:
                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_Vision));
                bundle.putString("NewsType", "5");
                bundle.putString("ChannelName", getString(R.string.menu_Vision));
                break;

            case R.id.nav_Mission:
                fragment = new NewsFragment();
                bundle.putString(CategoryFragment.TAG_CATEGORY, getString(R.string.menu_News));
                actionBar.setTitle(getString(R.string.menu_Mission));
                bundle.putString("NewsType", "6");
                bundle.putString("ChannelName", getString(R.string.menu_Mission));
                break;

            case R.id.nav_Aboutus: {
                isLogout = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.menu_Aboutus));
                builder.setMessage(getString(R.string.about_text));
                builder.setNeutralButton(getString(R.string.OK), null);
                builder.show();

            }
            break;

            case R.id.nav_Logout:
                isLogout = true;

                //remove password
                SharedPreferences sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = sharedpref.edit();
                edt.putString("Password", "");
                edt.putString("LastValidPassword", "");
                edt.commit();

                Intent i12 = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(i12);
                break;


            default:
                isLogout = true;
                break;
        }

        if (!isLogout) {

            fragment.setArguments(bundle);

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, fragment, "");
                fragmentTransaction.commit();

                //initToolbar();
            }
        }
    }

    public void doExitApp() {

        // display first page
        displayView(R.id.nav_Atschool);
        actionBar.setTitle(R.string.menu_Main);


           /* if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }*/

    }

    private void updateChartCounter(NavigationView nav, @IdRes int itemId, int count) {
        TextView view = (TextView) nav.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(String.valueOf(count));
    }

}


