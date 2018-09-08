package com.atschoolPioneerSchool;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atschoolPioneerSchool.adapter.MyViewPagerAdapter;

public class intro_slider extends AppCompatActivity {

    private LinearLayout dots;
    private ViewPager viewPager;
    private MyViewPagerAdapter adapter;
    private TextView[]mDots;
    private Button mNextbtn;
    private Button mSkipbtn;
    private int currentpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        //getSupportActionBar().hide();
        dots=(LinearLayout)findViewById(R.id.dots_layout);
        viewPager=(ViewPager)findViewById(R.id.slideer_viewpager);

        mNextbtn=(Button)findViewById(R.id.button_next);
        mSkipbtn=(Button) findViewById(R.id.button_skip);


        adapter=new MyViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        addDotsIndecator(0);
        viewPager.addOnPageChangeListener(viewListener);

        mNextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(currentpage+1);

            }
        });

        mSkipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(intro_slider.this,start_page.class);
                finish();
                startActivity(intent);
            }
        });

    }

    public void addDotsIndecator(int position){

        mDots=new TextView[3];//number of slides dots
        dots.removeAllViews();

        for(int i=0 ; i<mDots.length ;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(30);
            mDots[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dots.addView(mDots[i]);


        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.grey_soft));
        }

    }
    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndecator(i);
            currentpage=i;
            if(i==0){
                mNextbtn.setEnabled(true);
                mSkipbtn.setEnabled(true);
                mNextbtn.setVisibility(View.VISIBLE);
                mNextbtn.setText("التالي");
                mSkipbtn.setText("تخطي");
            }
            else if(i==2){
                mNextbtn.setEnabled(true);
                mSkipbtn.setEnabled(true);
                mNextbtn.setVisibility(View.INVISIBLE);
                mNextbtn.setText("");
                mSkipbtn.setText("دخول");


            }
            else {
                mNextbtn.setEnabled(true);
                mSkipbtn.setEnabled(true);
                mNextbtn.setVisibility(View.VISIBLE);
                mNextbtn.setText("التالي");
                mSkipbtn.setText("تخطي");

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
