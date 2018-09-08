package com.atschoolPioneerSchool.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityFriendDetails;
import com.atschoolPioneerSchool.ActivityItemDetails;
import com.atschoolPioneerSchool.ActivityKidBus;
import com.atschoolPioneerSchool.ActivityLogin;
import com.atschoolPioneerSchool.ActivityMain;
import com.atschoolPioneerSchool.ActivityNotification;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.activity_school_statements;
import com.atschoolPioneerSchool.activity_school_vouchers;
import com.atschoolPioneerSchool.activityimages.ComplexImageActivity;
import com.atschoolPioneerSchool.activityimages.HomeActivity;
import com.atschoolPioneerSchool.adapter.ItemGridAdapter;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.model.Student;
import com.atschoolPioneerSchool.model.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    public static String TAG_CATEGORY = "com.atschoolPioneerSchool.tagCategory";

    private View view;
    private RecyclerView recyclerView;
    private ItemGridAdapter mAdapter;
    private LinearLayout lyt_notfound;
    private String category = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, null);
        category = getArguments().getString(TAG_CATEGORY);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        lyt_notfound = (LinearLayout) view.findViewById(R.id.lyt_notfound);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity()));
        recyclerView.setLayoutManager(mLayoutManager);

        //set data and list adapter
        List<ItemModel> items = new ArrayList<>();
        if (category.equals(getString(R.string.menu_Main))) {
            items = Constant.getItemMainMenu(getActivity());
        }

       /* else if (category.equals(getString(R.string.menu_shoes))) {
            items = Constant.getItemShoes(getActivity());
        } else if (category.equals(getString(R.string.menu_watches))) {
            items = Constant.getItemWatches(getActivity());
        } else if (category.equals(getString(R.string.menu_accessories))) {
            items = Constant.getItemAccessories(getActivity());
        } else if (category.equals(getString(R.string.menu_bags))) {
            items = Constant.getItemBags(getActivity());
        } else if (category.equals(getString(R.string.menu_new))) {
            items = Constant.getItemNew(getActivity());
        }*/

        mAdapter = new ItemGridAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new ItemGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, ItemModel obj, int position) {

                //change interface language
                Constant.ChangeLanguage(getActivity());

                if (obj.isKid) {

                    Student student = Constant.StudentList.get(position);
                    Constant.SelectedStudent = student;
                    ActivityFriendDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), student);

                } else if (obj.getName().equals("كشف حساب") || obj.getName().equals("Account Statement")) {

                    Intent intent = new Intent((ActivityMain) getActivity(), activity_school_statements.class);
                    final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), EXTRA_OBJCT);
                    ActivityCompat.startActivity((ActivityMain) getActivity(), intent, options.toBundle());


                } else if (obj.getName().equals("سندات القبض") || obj.getName().equals("Receipt Voucher")) {

                    Intent intent = new Intent((ActivityMain) getActivity(), activity_school_vouchers.class);
                    final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), EXTRA_OBJCT);
                    ActivityCompat.startActivity((ActivityMain) getActivity(), intent, options.toBundle());


                } else if (obj.getName().equals("الأخبار") || obj.getName().equals("News")) {


                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_News));
                    i.putExtras(mBundle);

                    startActivity(i);

                    // ActivityItemDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj);


                } else if (obj.getName().equals("التواصل") || obj.getName().equals("Communication")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Communication));
                    i.putExtras(mBundle);

                    startActivity(i);
                } else if (obj.getName().equals("معرفه") || obj.getName().equals("M3refah")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_marefah));
                    i.putExtras(mBundle);

                    startActivity(i);
                } else if (obj.getName().equals("الباصات") || obj.getName().equals("Buses")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Busses));
                    i.putExtras(mBundle);

                    startActivity(i);
                }
                else if (obj.getName().equals("تسجيل حضور الطلاب") || obj.getName().equals("Register students attendance")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Register_students_attendance));
                    i.putExtras(mBundle);

                    startActivity(i);
                }
                else if (obj.getName().equals("موقع الباص") || obj.getName().equals("Bus Location")) {

                /*    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_BussesLocation));
                    i.putExtras(mBundle);

                    startActivity(i);*/


                    // Student student = Constant.StudentList.get(position);
                    // Constant.SelectedStudent = new Student();
                    ActivityKidBus.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), new Student());


                } else if (obj.getName().equals("شكوى") || obj.getName().equals("Complaint")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Complaint));
                    i.putExtras(mBundle);

                    startActivity(i);


                } else if (obj.getName().equals("إقتراح") || obj.getName().equals("Suggestion")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Suggestion));
                    i.putExtras(mBundle);

                    startActivity(i);


                } else if (obj.getName().equals("موقع البيت") || obj.getName().equals("Home Location")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_HomeLocation));
                    i.putExtras(mBundle);

                    startActivity(i);


                } else if (obj.getName().equals("الصيانه") || obj.getName().equals("Maintenance")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Maintenance));
                    i.putExtras(mBundle);

                    startActivity(i);


                } else if (obj.getName().equals("التنبيهات") || obj.getName().equals("Notifications")) {

                    Intent intent = new Intent(getActivity(), ActivityNotification.class);
                    Student friend = new Student("Notifications", 1, 2625);
                    intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
                    startActivity(intent);

                } else if (obj.getName().equals("معرض الصور") || obj.getName().equals("Images Gallery")) {


                    Intent i = new Intent(getActivity(), ComplexImageActivity.class);


                    startActivity(i);

                } else if (obj.getName().equals("إستدعاء الأبناء") || obj.getName().equals("Call your Sons")) {

                    Intent i = new Intent(getActivity(), ActivityMain.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("SelectedMenu", String.valueOf(R.id.nav_Call_Your_Sons));
                    i.putExtras(mBundle);

                    startActivity(i);
                } else if (obj.getName().equals("الفيسبوك") || obj.getName().equals("Facebook")) {

                    SharedPreferences sharedpref = getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                    String SchoolFaceBook = sharedpref.getString("SchoolFaceBook", "").trim();

                    if (SchoolFaceBook.length() > 0) {

                        //check if facebook app installed
                        if (isPackageExisted(getContext(), "com.facebook.katana")) {
                            PackageManager pm = getContext().getPackageManager();
                            Uri uri = Uri.parse(SchoolFaceBook);

                            try {
                                ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                                if (applicationInfo.enabled) {
                                    uri = Uri.parse("fb://facewebmodal/f?href=" + SchoolFaceBook);
                                }
                            } catch (PackageManager.NameNotFoundException ignored) {
                            }

                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        } else {
                            Toast.makeText(getContext(), R.string.install_Facebook, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getContext(), R.string.URL_is_not_defined, Toast.LENGTH_LONG).show();
                    }

                } else if (obj.getName().equals("اليوتيوب") || obj.getName().equals("YouTube")) {

                    SharedPreferences sharedpref = getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                    String SchoolYouTube = sharedpref.getString("SchoolYouTube", "").trim();

                    if (SchoolYouTube.length() > 0) {

                        //check if YouTube app installed
                        if (isPackageExisted(getContext(), "com.google.android.youtube")) {
                            PackageManager pm = getContext().getPackageManager();
                            Uri uri = Uri.parse(SchoolYouTube);

                            try {
                                ApplicationInfo applicationInfo = pm.getApplicationInfo("com.google.android.youtube", 0);
                                if (applicationInfo.enabled) {
                                    uri = Uri.parse(SchoolYouTube);
                                }
                            } catch (PackageManager.NameNotFoundException ignored) {
                            }

                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        } else {
                            Toast.makeText(getContext(), R.string.install_Facebook, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getContext(), R.string.URL_is_not_defined, Toast.LENGTH_LONG).show();
                    }

                } else if (obj.getName().equals("الموقع الكتروني") || obj.getName().equals("Website")) {

                    SharedPreferences sharedpref = getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                    String SchoolWebsite = sharedpref.getString("SchoolWebsite", "").trim();

                    if (SchoolWebsite.length() > 0) {

                        try {

                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SchoolWebsite));
                            startActivity(myIntent);

                        } catch (ActivityNotFoundException e) {

                            Toast.makeText(getContext(), R.string.URL_is_not_defined, Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getContext(), R.string.URL_is_not_defined, Toast.LENGTH_LONG).show();
                    }

                } else {
                    ActivityItemDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj);
                }
            }
        });

        if (mAdapter.getItemCount() == 0) {
            lyt_notfound.setVisibility(View.VISIBLE);
        } else {
            lyt_notfound.setVisibility(View.GONE);
        }
        return view;
    }

    public static boolean isPackageExisted(Context c, String targetPackage) {

        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage,
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
