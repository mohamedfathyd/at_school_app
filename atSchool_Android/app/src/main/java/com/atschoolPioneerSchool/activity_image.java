package com.atschoolPioneerSchool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.atschoolPioneerSchool.model.News;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class activity_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // get extra object
        String ImagePath = getIntent().getStringExtra("ImagePath");

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_atschool)
                .showImageForEmptyUri(R.drawable.ic_nav_setting)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();


        ImageLoader.getInstance().displayImage(ImagePath, ((ImageView) findViewById(R.id.image)), options, null);

    }
}
