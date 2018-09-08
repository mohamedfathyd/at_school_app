package com.atschoolPioneerSchool;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class activity_play_video extends AppCompatActivity {


    ProgressDialog mProgressDialog;
    ProgressDialog pDialog;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mProgressDialog = new ProgressDialog(activity_play_video.this);
        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) findViewById(R.id.videoView1);
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

        try {
            SharedPreferences sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
            String VideoURL = sharedpref.getString("VideoURL", "").trim();
            pDialog = new ProgressDialog(activity_play_video.this);
            pDialog.setTitle("Video Streaming");
            pDialog.setMessage("Buffering...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            try {
                MediaController mediacontroller = new MediaController(activity_play_video.this);
                mediacontroller.setAnchorView(videoview);
                Uri video = Uri.parse(VideoURL);
                videoview.setMediaController(mediacontroller);
                videoview.setVideoURI(video);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            videoview.requestFocus();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            pDialog.dismiss();
        } catch (Exception exc) {
        }
        finish();
    }
}
