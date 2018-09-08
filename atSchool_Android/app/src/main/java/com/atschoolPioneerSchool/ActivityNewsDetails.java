package com.atschoolPioneerSchool;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atschoolPioneerSchool.adapter.AdapterNewsListWithHeader;
import com.atschoolPioneerSchool.data.GlobalVariable;
import com.atschoolPioneerSchool.model.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class ActivityNewsDetails extends AppCompatActivity {
    public static final String EXTRA_OBJC = "com.app.sample.news.EXTRA_OBJC";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, News obj) {
        Intent intent = new Intent(activity, ActivityNewsDetails.class);
        intent.putExtra(EXTRA_OBJC, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_OBJC);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private Toolbar toolbar;
    private ActionBar actionBar;
    // extra obj
    private News news;
    private View parent_view;
    private FloatingActionButton fab;
    private GlobalVariable global;
    private String ImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        parent_view = findViewById(android.R.id.content);
        global = (GlobalVariable) getApplication();

        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.image), EXTRA_OBJC);

        // get extra object
        news = (News) getIntent().getSerializableExtra(EXTRA_OBJC);
        initToolbar();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabToggle();

        ((TextView) findViewById(R.id.title)).setText(news.getTitle());
        ((TextView) findViewById(R.id.content)).setText(news.getContent());
        ((TextView) findViewById(R.id.date)).setText(news.getDate());
        TextView channel = (TextView) findViewById(R.id.channel);
        channel.setText(news.getChannel().getName());
        channel.setBackgroundColor(Color.parseColor(news.getChannel().getColor()));
        //Picasso.with(this).load(news.getImage()).into(((ImageView) findViewById(R.id.image)));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_atschool)
                .showImageForEmptyUri(R.drawable.ic_nav_setting)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        // ImageLoadingListener animateFirstListener = new AdapterNewsListWithHeader.AnimateFirstDisplayListener();

        //0
        //1   Activities الأنشطة
        //2   Agenda جدول أعمال المدرسه
        //3   School Managements  إدارة المدارس
        //4   School Facilities   مرافق المدرسة
        //5   Vision الرؤية
        //6   Mission المهمة
        //7   About حول
        //8   News الأخبار
        //9   Complaint

        if (news.NewsType.equals("9")) {
            ImagePath = getString(R.string.URL_Complaint_Images) + String.valueOf(news.Img1);
            ImageLoader.getInstance().displayImage(ImagePath, ((ImageView) findViewById(R.id.image)), options, null);

        } else if (news.NewsType.equals("10")) {
            ImagePath = getString(R.string.URL_Suggestion_Images) + String.valueOf(news.Img1);
            ImageLoader.getInstance().displayImage(ImagePath, ((ImageView) findViewById(R.id.image)), options, null);

        } else if (news.NewsType.equals("11")) {
            ImagePath = getString(R.string.URL_Maintenance_Images) + String.valueOf(news.Img1);
            ImageLoader.getInstance().displayImage(ImagePath, ((ImageView) findViewById(R.id.image)), options, null);

        } else {
            ImagePath = getString(R.string.URL_News_Images) + String.valueOf(news.Img1);
            ImageLoader.getInstance().displayImage(ImagePath, ((ImageView) findViewById(R.id.image)), options, null);

        }





       /* vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, c, position);
                }
            }
        });*/


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if (global.isSaved(news)) {
                    global.removeSaved(news);
                    Snackbar.make(parent_view, "News remove from favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    global.addSaved(news);
                    Snackbar.make(parent_view, "News added to favorites", Snackbar.LENGTH_SHORT).show();
                }
                fabToggle();*/
                Intent i = new Intent(getBaseContext(), activity_image.class);
                i.putExtra("ImagePath", String.valueOf(ImagePath));
                startActivity(i);

            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    private void fabToggle() {
        if (global.isSaved(news)) {
            fab.setImageResource(R.drawable.ic_nav_zoom);
        } else {
            fab.setImageResource(R.drawable.ic_nav_zoom);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            Snackbar.make(parent_view, item.getTitle() + " clicked", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_news_details, menu);
        return true;
    }

}
