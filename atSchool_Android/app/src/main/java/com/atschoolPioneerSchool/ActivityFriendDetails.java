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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.AdapterNewsListWithHeader;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.fragment.FriendAboutFragment;
import com.atschoolPioneerSchool.fragment.FriendActivitiesFragment;
import com.atschoolPioneerSchool.fragment.FriendPhotosFragment;
import com.atschoolPioneerSchool.fragment.FriendWeeklyPlanFragment;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.Student;
import com.atschoolPioneerSchool.model.ItemModel;
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

public class ActivityFriendDetails extends AppCompatActivity implements PopupAccountImageUpload.Commmunicator {
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.Student";
    public static final String IMAGE_DIRECTORY_NAME = "AndroidFileUpload";

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    private static Student objStudent;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private SharedPreferences sharedpref;
    private Uri fileUri; // file url to store image/video0


    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Student objS) {
        Intent intent = new Intent(activity, ActivityFriendDetails.class);
        objStudent = objS;

        SharedPreferences sharedpref = transitionImage.getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = sharedpref.edit();
        edt.putString("SelectedStudentId", String.valueOf(objS.StudentId));
        edt.commit();

        intent.putExtra(EXTRA_OBJCT, objS);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_OBJCT);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    private ViewPager mViewPager;
    private FriendAboutFragment frag_friendAbout;
    private FriendActivitiesFragment frag_friendActivity;
    private FriendPhotosFragment frag_friendPhotos;
    private FriendWeeklyPlanFragment frag_FriendWeeklyPlanFragment;

    private ActionBar actionBar;
    public static Student friend;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);

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
        ivImage = (ImageView) findViewById(R.id.ivImage);

        if (!friend.StudentImageName.equals("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_atschool)
                    .showImageForEmptyUri(R.drawable.ic_nav_setting)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoadingListener animateFirstListener = new AdapterNewsListWithHeader.AnimateFirstDisplayListener();

            ImageLoader.getInstance().displayImage(getString(R.string.URL_Account_Profile_Images) + String.valueOf(friend.StudentImageName)
                    , ivImage, options, animateFirstListener);

        } else {
            ivImage.setImageResource(getResources().getIdentifier("ic_people", "drawable", getPackageName()));
        }


        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        if (frag_friendAbout == null) {
            frag_friendAbout = new FriendAboutFragment();
        }
        if (frag_friendActivity == null) {
            frag_friendActivity = new FriendActivitiesFragment();
        }
        if (frag_friendPhotos == null) {
            frag_friendPhotos = new FriendPhotosFragment();
        }
        if (frag_FriendWeeklyPlanFragment == null) {
            frag_FriendWeeklyPlanFragment = new FriendWeeklyPlanFragment();
        }

        frag_FriendWeeklyPlanFragment.objStudent = objStudent;

        adapter.addFragment(frag_friendAbout, "الطالب");
        //adapter.addFragment(frag_friendActivity, "التواصل");
        adapter.addFragment(frag_FriendWeeklyPlanFragment, "الخطه الأسبوعيه");
        // adapter.addFragment(frag_friendPhotos, "الصور");

        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //  mViewPager.setCurrentItem(1,true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_send_message) {
            Intent i = new Intent(getApplicationContext(), ActivityChatDetails.class);
            i.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.action_photo) {
            // capture picture
            captureImage();

            return true;
        } else if (item.getItemId() == R.id.action_attachment) {

            // attached file

            DialogProperties properties = new DialogProperties();

            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = new File(DialogConfigs.STORAGE_DIR);
            properties.error_dir = new File(DialogConfigs.STORAGE_DIR);
            properties.offset = new File(DialogConfigs.STORAGE_DIR);
            properties.extensions = null;// new String[]{".jpg"};

            FilePickerDialog dialog = new FilePickerDialog(ActivityFriendDetails.this, properties);
            dialog.setTitle("Select a File");

            dialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    //files is the array of the paths of files selected by the Application User.

                    fileUri = Uri.fromFile(new File(files[0]));
                    launchUploadActivity(true);
                }
            });

            dialog.show();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_friend_details, menu);
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

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp.replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim() + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp.replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim() + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onDialogMessage(MessageDetails message) {

        //update image name
        friend.StudentImageName = message.getContent();

        //fill student data
        for (int i = 0; i < Constant.StudentList.size(); i++) {
            if (Constant.StudentList.get(i).StudentId == friend.StudentId) {
                //update image name
                Constant.StudentList.get(i).StudentImageName = message.getContent();
                break;
            }
        }


        if (!friend.StudentImageName.equals("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_atschool)
                    .showImageForEmptyUri(R.drawable.ic_nav_setting)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoadingListener animateFirstListener = new AdapterNewsListWithHeader.AnimateFirstDisplayListener();

            ImageLoader.getInstance().displayImage(getString(R.string.URL_Account_Profile_Images) + String.valueOf(friend.StudentImageName)
                    , ivImage, options, animateFirstListener);

        } else {
            ivImage.setImageResource(getResources().getIdentifier("ic_people", "drawable", getPackageName()));
        }

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void launchUploadActivity(boolean isImage) {
        /*
        Intent i = new Intent(ActivityChatDetails.this, ActivityTestUpload.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
        */
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = sharedpref.edit();

        edt.putString("filePath", fileUri.getPath());
        edt.putBoolean("isImage", isImage);
        edt.commit();


        android.app.FragmentManager manager = getFragmentManager();
        PopupAccountImageUpload pop = new PopupAccountImageUpload();
        pop.student = friend;
        pop.show(manager, null);
    }

}
