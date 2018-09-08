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
 * Created by OmarA on 14/12/2017.
 */

public class MarefahBooksDetailsListAdapter extends BaseAdapter {

    private List<MessageDetails> mMessages;
    private Context ctx;

    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;


    int downloadId1;
    Uri destinationUri;
    File filesDir;
    Uri downloadUri;
    DownloadRequest downloadRequest1;


    public MarefahBooksDetailsListAdapter(Context context, List<MessageDetails> messages) {
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
        final MessageDetails msg = (MessageDetails) getItem(position);

        final MarefahBooksDetailsListAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new MarefahBooksDetailsListAdapter.ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.row_book_details, parent, false);
            holder.time = (TextView) convertView.findViewById(R.id.text_time);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);


            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            holder.mProgress1 = (ProgressBar) convertView.findViewById(R.id.progress1);
            holder.mProgress1Txt = (TextView) convertView.findViewById(R.id.progressTxt1);
            convertView.setTag(holder);
        } else {
            holder = (MarefahBooksDetailsListAdapter.ViewHolder) convertView.getTag();
        }

        holder.message.setText(msg.getContent());
        holder.time.setText(msg.getDate());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_atschool)
                .showImageForEmptyUri(R.drawable.ic_nav_setting)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        //set image as attachemnt type
        ImageLoadingListener animateFirstListener = new MarefahBooksDetailsListAdapter.AnimateFirstDisplayListener();

//  video info
        if (msg.Attached_File_Extension.equals(".mp4")) {

            holder.image.setVisibility(View.VISIBLE);

            //set image of video
            Drawable myDrawable = convertView.getResources().getDrawable(R.drawable.playvideo);
            holder.image.setImageDrawable(myDrawable);

        } else // Image   info
            if (msg.Attached_File_Extension.equals(".jpg") || msg.Attached_File_Extension.equals(".gif") || msg.Attached_File_Extension.equals(".png") ||
                    msg.Attached_File_Extension.equals(".jpeg") || msg.Attached_File_Extension.equals(".ico")) {

                holder.image.setVisibility(View.VISIBLE);

                ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_ChatUploads_Images) + String.valueOf(msg.Attached_File_Name)
                        , holder.image, options, animateFirstListener);

            } else //text message info
                if (msg.Attached_File_Extension.equals("")) {

                    holder.image.setVisibility(View.GONE);

                } else//attached info
                {
                    holder.image.setVisibility(View.VISIBLE);
                    Drawable myDrawable = convertView.getResources().getDrawable(R.drawable.download_doc);
                    holder.image.setImageDrawable(myDrawable);

                }

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

                //fill video info
                if (msg.Attached_File_Extension.equals(".mp4")) {

                    //fill video info
                    SharedPreferences sharedpref = view.getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor edtPref = sharedpref.edit();
                    edtPref.putString("VideoURL", view.getResources().getString(R.string.URL_Marefah_Book_Video) + msg.Attached_File_Name);
                    edtPref.commit();

                    Intent i = new Intent(view.getContext(), activity_play_video.class);
                    view.getContext().startActivity(i);

                } else //fill image info
                    if (msg.Attached_File_Extension.equals(".jpg") || msg.Attached_File_Extension.equals(".gif") || msg.Attached_File_Extension.equals(".png") ||
                            msg.Attached_File_Extension.equals(".jpeg") || msg.Attached_File_Extension.equals(".ico")) {

                        String ImagePath = view.getResources().getString(R.string.URL_ChatUploads_Images) + msg.Attached_File_Name;

                        Intent i = new Intent(view.getContext(), activity_image.class);
                        i.putExtra("ImagePath", String.valueOf(ImagePath));
                        view.getContext().startActivity(i);

                    } else //this mean attached documents
                    {

                        MarefahBooksDetailsListAdapter.MyDownloadDownloadStatusListenerV1 myDownloadStatusListener = new MarefahBooksDetailsListAdapter.MyDownloadDownloadStatusListenerV1(holder.mProgress1Txt, holder.mProgress1, position);

                        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
                        RetryPolicy retryPolicy = new DefaultRetryPolicy();

                        filesDir = view.getContext().getExternalFilesDir("");


                        // String FILE1 = "http://irbid.lms-school.com/ImagesPortal/ChatUploads/Chat_4f6ef0c1-8aa8-4787-89fa-c725c8561c25.mp4";
                        String FILE1 = view.getResources().getString(R.string.URL_ChatUploads_Images) + msg.Attached_File_Name;
                        downloadUri = Uri.parse(FILE1);
                        // destinationUri = Uri.parse(filesDir + "/" + msg.Attached_File_Name);

                        //save file in download folder
                        destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + msg.Attached_File_Name);
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
            }
        });

/*
        if (msg.isFromMe()) {
            holder.lyt_parent.setPadding(100, 10, 15, 10);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
            holder.lyt_thread.setCardBackgroundColor(ctx.getResources().getColor(R.color.me_chat_bg));
        } else {
            holder.lyt_parent.setPadding(15, 10, 100, 10);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            //holder.image_status.setImageResource(android.R.color.transparent);
        }*/
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
    public void add(MessageDetails msg) {
        mMessages.add(msg);
    }

    private static class ViewHolder {
        TextView time;
        TextView message;
        LinearLayout lyt_parent;

        CardView lyt_thread;
        ImageView image_status;
        ImageView image;
        ProgressBar mProgress1;
        TextView mProgress1Txt;

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
                ((MessageDetails) getItem(ObjectIndex)).ProgressText = ctx.getResources().getString(R.string.strCompleted);
                openFolder();
            }
        }

        public void openFolder() {



/*            Intent intent = new Intent(Intent.ACTION_VIEW, destinationUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            */
/*
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                    + File.separator + "1.png");*/

            //  File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "22.pdf");
            //  File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "22.pdf");

          /*  try {
                Thread.sleep(10001);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            File file = new File(destinationUri.getPath());

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));

            if (destinationUri.getPath().contains(".docx")) {

                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, Uri.fromFile(file));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setType("application/msword");
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);


             /*

                Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);

            */
        }


        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {
                ProgressTxt.setText("Download1 id: " + id + " Failed: ErrorCode " + errorCode + ", " + errorMessage);
                ProgressBar.setProgress(0);
                ((MessageDetails) getItem(ObjectIndex)).ProgressBarLevel = 0;
            }
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();

            if (id == downloadId1) {

                ProgressTxt.setText(progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
                ProgressBar.setProgress(progress);

                ((MessageDetails) getItem(ObjectIndex)).ProgressBarLevel = progress;
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
