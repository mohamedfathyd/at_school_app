package com.atschoolPioneerSchool.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.activity_image;
import com.atschoolPioneerSchool.activity_play_video;
import com.atschoolPioneerSchool.downloadmanager.DefaultRetryPolicy;
import com.atschoolPioneerSchool.downloadmanager.DownloadRequest;
import com.atschoolPioneerSchool.downloadmanager.DownloadStatusListenerV1;
import com.atschoolPioneerSchool.downloadmanager.RetryPolicy;
import com.atschoolPioneerSchool.downloadmanager.ThinDownloadManager;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.WeeklyPlan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by OmarA on 13/10/2017.
 */

public class WeeklyPlanListAdapter extends BaseAdapter {

    private List<WeeklyPlan> mMessages;
    private Context ctx;

    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;


    int downloadId1;
    Uri destinationUri;
    File filesDir;
    Uri downloadUri;
    DownloadRequest downloadRequest1;


    public WeeklyPlanListAdapter(Context context, List<WeeklyPlan> messages) {
        super();
        this.ctx = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mMessages.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final WeeklyPlan msg = (WeeklyPlan) getItem(position);

        final WeeklyPlanListAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new WeeklyPlanListAdapter.ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.row_weekly_plan_details, parent, false);
            holder.time = (TextView) convertView.findViewById(R.id.text_time);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);
            holder.lyt_details = (LinearLayout) convertView.findViewById(R.id.lyt_details);
            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            holder.mProgress1 = (ProgressBar) convertView.findViewById(R.id.progress1);
            holder.mProgress1Txt = (TextView) convertView.findViewById(R.id.progressTxt1);
            holder.txtDay = (TextView) convertView.findViewById(R.id.txtDay);
            holder.txtHome = (TextView) convertView.findViewById(R.id.txtHome);
            holder.txtClass = (TextView) convertView.findViewById(R.id.txtClass);
            holder.text_Homework = (TextView) convertView.findViewById(R.id.text_Homework);
            holder.btn_download = (Button) convertView.findViewById(R.id.btn_download);
            holder.viw = (View) convertView.findViewById(R.id.viw);


            convertView.setTag(holder);
        } else {
            holder = (WeeklyPlanListAdapter.ViewHolder) convertView.getTag();
        }

        holder.message.setText(msg.Description_A);
        holder.text_Homework.setText(msg.Description_B);

        if (msg.Description_B.length() == 0) {

            holder.txtHome.setVisibility(View.GONE);
            holder.text_Homework.setVisibility(View.GONE);
            holder.viw.setVisibility(View.GONE);

        } else {

            holder.txtHome.setVisibility(View.VISIBLE);
            holder.text_Homework.setVisibility(View.VISIBLE);
            holder.viw.setVisibility(View.VISIBLE);

        }

        if (msg.SCH_STUDY_DAY_NAME.equals("Weekly Notes") || msg.SCH_STUDY_DAY_NAME.equals("ملاحظات الاسبوع")) {
            holder.txtDay.setText("Weekly Notes" + "  " + "ملاحظات الاسبوع");
            holder.txtHome.setText("");
            holder.txtClass.setText("");
        } else {
            holder.txtDay.setText(msg.SCH_STUDY_DAY_NAME + "  -  " + msg.SUBJECT_NAME);
        }

        if (msg.Description_A.length() == 0 && msg.Description_B.length() == 0) {
            holder.lyt_details.setVisibility(View.GONE);
        } else {
            holder.lyt_details.setVisibility(View.VISIBLE);
        }

        if (msg.File_Attached.length() == 0) {
            holder.btn_download.setVisibility(View.GONE);
        } else {
            holder.btn_download.setVisibility(View.VISIBLE);
        }


        holder.time.setText("");
        holder.image.setVisibility(View.GONE);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_atschool)
                .showImageForEmptyUri(R.drawable.ic_nav_setting)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        //set image as attachemnt type
        ImageLoadingListener animateFirstListener = new WeeklyPlanListAdapter.AnimateFirstDisplayListener();


        //set download progress level
        if (msg.ProgressBarLevel > 0) {

            holder.mProgress1.setVisibility(View.VISIBLE);
            holder.mProgress1Txt.setVisibility(View.VISIBLE);
            holder.mProgress1Txt.setText("0%");

            holder.mProgress1.setProgress(msg.ProgressBarLevel);
        } else {
            holder.mProgress1.setVisibility(View.GONE);
            holder.mProgress1Txt.setVisibility(View.GONE);
            holder.mProgress1Txt.setText("");
            holder.mProgress1.setProgress(0);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.btn_download.setVisibility(View.GONE);

        if (msg.File_Attached.length() > 0) {

            holder.btn_download.setVisibility(View.VISIBLE);
            //   holder.lyt_details.setVisibility(View.GONE);

            holder.btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    MyDownloadDownloadStatusListenerV1 myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1(holder.mProgress1Txt, holder.mProgress1, position);

                    downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
                    RetryPolicy retryPolicy = new DefaultRetryPolicy();

                    filesDir = view.getContext().getExternalFilesDir("");


                    // String FILE1 = "http://irbid.lms-school.com/ImagesPortal/ChatUploads/Chat_4f6ef0c1-8aa8-4787-89fa-c725c8561c25.mp4";
                    String FILE1 = view.getResources().getString(R.string.URL_Weekly_Plan) + msg.File_Attached;
                    downloadUri = Uri.parse(FILE1);
                    // destinationUri = Uri.parse(filesDir + "/" + msg.Attached_File_Name);

                    //save file in download folder
                    destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + msg.File_Attached);
                    downloadRequest1 = new DownloadRequest(downloadUri)
                            .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                            .setRetryPolicy(retryPolicy)
                            .setDownloadContext("0%")
                            .setStatusListener(myDownloadStatusListener);

                    downloadId1 = downloadManager.add(downloadRequest1);

                    holder.mProgress1.setVisibility(View.VISIBLE);
                    holder.mProgress1Txt.setVisibility(View.VISIBLE);
                    holder.mProgress1Txt.setText("0%");
                }

            });
        }
        //else {34
        //  holder.btn_download.setVisibility(View.GONE);
        // holder.lyt_details.setVisibility(View.VISIBLE);
        //}

        /*

        2	2	Sunday	الأحد	1
        3	3	Monday	الاثنين	1
        4	4	Tuesday	الثلاثاء	1
        5	5	Wednesday	الاربعاء	1
        6	6	Thursday	الخميس	1

        */

        if (msg.SCH_STUDY_DAY_ID.equals("2")) {
            holder.lyt_thread.setCardBackgroundColor(ctx.getResources().getColor(R.color.me_chat_bg));

        } else if (msg.SCH_STUDY_DAY_ID.equals("3")) {
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
        } else if (msg.SCH_STUDY_DAY_ID.equals("4")) {
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#F3E5F5"));
        } else if (msg.SCH_STUDY_DAY_ID.equals("5")) {
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FCE4EC"));
        } else if (msg.SCH_STUDY_DAY_ID.equals("6")) {
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return convertView;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    /**
     * remove data item from messageAdapter
     **/
    public void remove(int position) {
        mMessages.remove(position);
    }

    /**
     * add data item to messageAdapter
     **/
    public void add(WeeklyPlan msg) {
        mMessages.add(msg);
    }

    private static class ViewHolder {
        TextView time;
        TextView message;
        LinearLayout lyt_parent;

        LinearLayout lyt_details;
        CardView lyt_thread;
        ImageView image_status;
        ImageView image;
        ProgressBar mProgress1;
        TextView mProgress1Txt;

        TextView txtDay;
        TextView txtHome;
        TextView txtClass;
        TextView text_Homework;
        View viw;
        Button btn_download;

    }

    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

        TextView ProgressTxt;
        ProgressBar ProgressBar;
        int ObjectIndex = -1;
        Context context;

        MyDownloadDownloadStatusListenerV1(TextView prmProgress, ProgressBar prmProgressBar, int ObjectIndex) {
            ProgressTxt = prmProgress;
            ProgressBar = prmProgressBar;
            this.ObjectIndex = ObjectIndex;

        }

        @Override
        public void onDownloadComplete(DownloadRequest request) {
            final int id = request.getDownloadId
                    ();
            if (id == downloadId1) {
                ProgressTxt.setText(ctx.getResources().getString(R.string.strCompleted));
                ((WeeklyPlan) getItem(ObjectIndex)).ProgressText = ctx.getResources().getString(R.string.strCompleted);
                openFolder();
            }
        }

        public void openFolder() {


//            File file = new File(destinationUri.getPath());
//
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
//
//            if (destinationUri.getPath().contains(".docx")) {
//
//                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, Uri.fromFile(file));
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.setType("application/msword");
//            }
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ctx.startActivity(intent);


            try {
                Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                File file = new File(destinationUri.getPath());
                String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                myIntent.setDataAndType(Uri.fromFile(file), mimetype);
                ctx.startActivity(myIntent);
            } catch (Exception e) {
                // TODO: handle exception
                String data = e.getMessage();
            }
        }


        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {
                ProgressTxt.setText("Download1 id: " + id + " Failed: ErrorCode " + errorCode + ", " + errorMessage);
                ProgressBar.setProgress(0);
                ((WeeklyPlan) getItem(ObjectIndex)).ProgressBarLevel = 0;
            }
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();

            if (id == downloadId1) {

                ProgressTxt.setText(progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
                ProgressBar.setProgress(progress);

                ((WeeklyPlan) getItem(ObjectIndex)).ProgressBarLevel = progress;
            }
        }
    }

    private String getBytesDownloaded(int progress, long totalBytes) {
        //Greater than 1 MB
        long bytesCompleted = (progress * totalBytes) / 100;
        if (totalBytes >= 1000000) {
            return ("" + (String.format("%.1f", (float) bytesCompleted / 1000000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000000)) + "MB");
        }
        if (totalBytes >= 1000) {
            return ("" + (String.format("%.1f", (float) bytesCompleted / 1000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000)) + "Kb");

        } else {
            return ("" + bytesCompleted + "/" + totalBytes);
        }
    }


}
