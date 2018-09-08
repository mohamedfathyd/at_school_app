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
import com.atschoolPioneerSchool.model.MessageNotification;
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
 * Created by OmarA on 29/09/2017.
 */

public class MessageNotificationListAdapter extends BaseAdapter {

    private List<MessageNotification> mMessages;
    private Context ctx;

    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;


    int downloadId1;
    Uri destinationUri;
    File filesDir;
    Uri downloadUri;
    DownloadRequest downloadRequest1;


    public MessageNotificationListAdapter(Context context, List<MessageNotification> messages) {
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
        final MessageNotification msg = (MessageNotification) getItem(position);

        final MessageNotificationListAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new MessageNotificationListAdapter.ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.row_notification_details, parent, false);
            holder.time = (TextView) convertView.findViewById(R.id.text_time);

            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);
            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);

            convertView.setTag(holder);
        } else {
            holder = (MessageNotificationListAdapter.ViewHolder) convertView.getTag();
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
        ImageLoadingListener animateFirstListener = new MessageNotificationListAdapter.AnimateFirstDisplayListener();


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
    public void add(MessageNotification msg) {
        mMessages.add(msg);
    }

    private static class ViewHolder {
        TextView time;
        TextView message;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;
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
