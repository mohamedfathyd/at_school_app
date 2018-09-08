package com.atschoolPioneerSchool.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.activity_image;
import com.atschoolPioneerSchool.activity_play_video;
import com.atschoolPioneerSchool.downloadmanager.DefaultRetryPolicy;
import com.atschoolPioneerSchool.downloadmanager.DownloadRequest;
import com.atschoolPioneerSchool.downloadmanager.DownloadStatusListenerV1;
import com.atschoolPioneerSchool.downloadmanager.RetryPolicy;
import com.atschoolPioneerSchool.downloadmanager.ThinDownloadManager;
import com.atschoolPioneerSchool.model.BusOrderStudent;
import com.atschoolPioneerSchool.model.ChatContacts;
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
 * Created by OmarA on 24/11/2017.
 */

public class BusOrderStudentsListAdapter extends BaseAdapter {

    private List<BusOrderStudent> mStudents;
    private Context ctx;
    private BusOrderStudentsListAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(View view, BusOrderStudent obj, int position, int typeRequest);
    }

    public void setOnItemClickListener(final BusOrderStudentsListAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public BusOrderStudentsListAdapter(Context context, List<BusOrderStudent> students) {
        super();
        this.ctx = context;
        this.mStudents = students;
    }

    @Override
    public int getCount() {
        return mStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mStudents.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BusOrderStudent msg = (BusOrderStudent) getItem(position);

        final BusOrderStudentsListAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new BusOrderStudentsListAdapter.ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.row_bus_order_student, parent, false);
            holder.classname = (TextView) convertView.findViewById(R.id.classname);

            holder.txtStepIn = (TextView) convertView.findViewById(R.id.txtStepIn);
            holder.txtApsent = (TextView) convertView.findViewById(R.id.txtApsent);
            holder.txtStepOut = (TextView) convertView.findViewById(R.id.txtStepOut);

            holder.btn_StepIn = (Button) convertView.findViewById(R.id.btn_StepIn);
            holder.btn_StepOut = (Button) convertView.findViewById(R.id.btn_StepOut);
            holder.btn_Absent = (Button) convertView.findViewById(R.id.btn_Absent);


            holder.tel = (TextView) convertView.findViewById(R.id.tel);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);


            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            holder.mProgress1 = (ProgressBar) convertView.findViewById(R.id.progress1);
            holder.mProgress1Txt = (TextView) convertView.findViewById(R.id.progressTxt1);

            holder.menuDots = (TextView) convertView.findViewById(R.id.menu_dots);

            convertView.setTag(holder);
        } else {
            holder = (BusOrderStudentsListAdapter.ViewHolder) convertView.getTag();
        }

        holder.message.setText(msg.StudentName);
        holder.classname.setText(msg.className + "  " + msg.SectionName);
        holder.txtStepIn.setText(msg.StepIn_Time);
        holder.txtStepOut.setText(msg.StepOut_Time);


        if (msg.Track_Trans_Trip_Id <= 0 || msg.IsTripEnded) {

            holder.btn_StepIn.setVisibility(View.GONE);
            holder.btn_StepOut.setVisibility(View.GONE);
            holder.btn_Absent.setVisibility(View.GONE);

            holder.txtApsent.setVisibility(View.GONE);
            holder.txtStepIn.setVisibility(View.GONE);
            holder.txtStepOut.setVisibility(View.GONE);
            holder.menuDots.setVisibility(View.GONE);

        } else {
            holder.btn_StepIn.setVisibility(View.VISIBLE);
            holder.btn_StepOut.setVisibility(View.GONE);
            holder.btn_Absent.setVisibility(View.VISIBLE);

            holder.txtApsent.setVisibility(View.GONE);
            holder.txtStepIn.setVisibility(View.GONE);
            holder.txtStepOut.setVisibility(View.GONE);
            holder.menuDots.setVisibility(View.VISIBLE);

            if (msg.IsAbsent > 0) {

                holder.btn_StepIn.setVisibility(View.GONE);
                holder.btn_StepOut.setVisibility(View.GONE);
                holder.btn_Absent.setVisibility(View.GONE);
                //holder.menuDots.setVisibility(View.GONE);

                holder.txtApsent.setVisibility(View.VISIBLE);
                holder.txtStepIn.setVisibility(View.GONE);
                holder.txtStepOut.setVisibility(View.GONE);


            } else if (msg.StepOut_Time.length() > 2) {
                holder.btn_StepIn.setVisibility(View.GONE);
                holder.btn_StepOut.setVisibility(View.GONE);
                holder.btn_Absent.setVisibility(View.GONE);
               // holder.menuDots.setVisibility(View.GONE);

                holder.txtApsent.setVisibility(View.GONE);
                holder.txtStepIn.setVisibility(View.VISIBLE);
                holder.txtStepOut.setVisibility(View.VISIBLE);

            } else if (msg.StepIn_Time.length() > 2) {

                holder.btn_StepIn.setVisibility(View.GONE);
                holder.btn_StepOut.setVisibility(View.VISIBLE);
                holder.btn_Absent.setVisibility(View.GONE);
                holder.menuDots.setVisibility(View.VISIBLE);

                holder.txtApsent.setVisibility(View.GONE);
                holder.txtStepIn.setVisibility(View.VISIBLE);
                holder.txtStepOut.setVisibility(View.VISIBLE);

            }


            // set if not confirm to blue color
            if (msg.NotConfirm_Time.length() > 2 && msg.StepIn_Time.length() < 3 && msg.IsAbsent <= 0) {
                holder.menuDots.setBackgroundColor(Color.rgb(255, 165, 0));

            } else {
                holder.menuDots.setBackgroundColor(Color.rgb(244, 81, 30));
            }
        }

        final View finalConvertView = convertView;
        holder.btn_StepIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(finalConvertView, msg, position, 1);
                }
            }
        });

        holder.btn_StepOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(finalConvertView, msg, position, 2);
                }
            }
        });

        holder.btn_Absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(finalConvertView, msg, position, 3);
                }
            }
        });


        holder.menuDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(finalConvertView.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_bus_order_student, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            switch (item.getItemId()) {

                                case R.id.item_Attendance_not_confirmed:
                                    //  Toast.makeText(finalConvertView.getContext() , "not confirmed", Toast.LENGTH_SHORT).show();

                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(finalConvertView, msg, position, 4);
                                    }
                                    break;
                                case R.id.item_set_home_location:
                                    // Toast.makeText(finalConvertView.getContext() ,String.valueOf(position)    , Toast.LENGTH_SHORT).show();
                                    //set student home location
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(finalConvertView, msg, position, 5);
                                    }
                                    break;
                                case R.id.item_Undo:
                                    // Toast.makeText(finalConvertView.getContext() ,String.valueOf(position)    , Toast.LENGTH_SHORT).show();
                                    //set student home location
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(finalConvertView, msg, position, 6);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception df) {
                            Toast.makeText(finalConvertView.getContext(), df.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });


        holder.tel.setText(msg.GardianMobile1);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_atschool)
                .showImageForEmptyUri(R.drawable.ic_nav_setting)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        //set image as attachemnt type
        ImageLoadingListener animateFirstListener = new BusOrderStudentsListAdapter.AnimateFirstDisplayListener();


        if (!msg.StudentImageName.equals("")) {

            holder.image.setVisibility(View.VISIBLE);

            ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Account_Profile_Images) + String.valueOf(msg.StudentImageName)
                    , holder.image, options, animateFirstListener);

        } else {
            holder.image.setVisibility(View.VISIBLE);
            Drawable myDrawable = convertView.getResources().getDrawable(R.drawable.ic_people);
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


                if (!msg.StudentImageName.equals("")) {

                    String ImagePath = view.getResources().getString(R.string.URL_Account_Profile_Images) + msg.StudentImageName;

                    Intent i = new Intent(view.getContext(), activity_image.class);
                    i.putExtra("ImagePath", String.valueOf(ImagePath));
                    view.getContext().startActivity(i);

                }
            }
        });

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
        mStudents.remove(position);
    }

    /**
     * add data item to messageAdapter
     **/
    public void add(BusOrderStudent msg) {
        mStudents.add(msg);
    }

    private static class ViewHolder {
        TextView classname;
        TextView txtStepIn;
        TextView txtApsent;
        TextView txtStepOut;
        Button btn_StepIn;
        Button btn_StepOut;
        Button btn_Absent;
        TextView tel;
        TextView message;
        LinearLayout lyt_parent;

        //dots menu
        TextView menuDots;

        CardView lyt_thread;
        ImageView image_status;
        ImageView image;
        ProgressBar mProgress1;
        TextView mProgress1Txt;

    }

}
