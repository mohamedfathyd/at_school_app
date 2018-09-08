package com.atschoolPioneerSchool.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.activity_image;
import com.atschoolPioneerSchool.model.RegisterStudentsAttendance;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RegisterStudentsAttendanceEntryAdapter extends BaseAdapter {

    private List<RegisterStudentsAttendance> mStudents;
    private Context ctx;
    private RegisterStudentsAttendanceEntryAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(View view, RegisterStudentsAttendance obj, int position, int typeRequest);
    }

    public void setOnItemClickListener(final RegisterStudentsAttendanceEntryAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public RegisterStudentsAttendanceEntryAdapter(Context context, List<RegisterStudentsAttendance> students) {
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
        final RegisterStudentsAttendance msg = (RegisterStudentsAttendance) getItem(position);

        final RegisterStudentsAttendanceEntryAdapter.ViewHolder holder;
        if (convertView == null) {

            holder = new RegisterStudentsAttendanceEntryAdapter.ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.row_register_students_attendance_entry, parent, false);
            holder.txtDelay = (TextView) convertView.findViewById(R.id.txtDelay);
            holder.txtApsent = (TextView) convertView.findViewById(R.id.txtApsent);
                holder.tel = (TextView) convertView.findViewById(R.id.tel);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);

            holder.btn_Delay = (Button) convertView.findViewById(R.id.btn_Delay);
            holder.btn_Absent = (Button) convertView.findViewById(R.id.btn_Absent);
            holder.btn_Undo = (Button) convertView.findViewById(R.id.btn_Undo);



            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);


            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            holder.mProgress1 = (ProgressBar) convertView.findViewById(R.id.progress1);
            holder.mProgress1Txt = (TextView) convertView.findViewById(R.id.progressTxt1);

            convertView.setTag(holder);
        } else {
            holder = (RegisterStudentsAttendanceEntryAdapter.ViewHolder) convertView.getTag();
        }

        holder.message.setText(msg.NameAsPassEng);

        if (!msg.Absence.equals("1") &&  !msg.Delay.equals("1") && !msg.Absence.equals("True") &&  !msg.Delay.equals("True")) {

            holder.btn_Absent.setVisibility(View.VISIBLE);
            holder.btn_Delay.setVisibility(View.VISIBLE);

            holder.btn_Undo.setVisibility(View.GONE);

            holder.txtApsent.setVisibility(View.GONE);
            holder.txtDelay.setVisibility(View.GONE);

        }else  if ( msg.Absence.equals("1") || msg.Absence.equals("True")   ) {

            holder.btn_Absent.setVisibility(View.GONE);
            holder.btn_Delay.setVisibility(View.GONE);

            holder.btn_Undo.setVisibility(View.VISIBLE);

            holder.txtApsent.setVisibility(View.VISIBLE);
            holder.txtDelay.setVisibility(View.GONE);

        }else  if ( msg.Delay.equals("1") ||  msg.Delay.equals("True") ) {

            holder.btn_Absent.setVisibility(View.GONE);
            holder.btn_Delay.setVisibility(View.GONE);

            holder.btn_Undo.setVisibility(View.VISIBLE);

            holder.txtApsent.setVisibility(View.GONE);
            holder.txtDelay.setVisibility(View.VISIBLE);
        }


        final View finalConvertView = convertView;

        holder.btn_Delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(finalConvertView, msg, position, 2);
                }
            }
        });

        holder.btn_Undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(finalConvertView, msg, position, 3);
                }
            }
        });

        holder.btn_Absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(finalConvertView, msg, position, 1);
                }
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
        ImageLoadingListener animateFirstListener = new RegisterStudentsAttendanceEntryAdapter.AnimateFirstDisplayListener();


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
    public void add(RegisterStudentsAttendance msg) {
        mStudents.add(msg);
    }

    private static class ViewHolder {
        TextView txtDelay;
        TextView txtApsent;
        TextView tel;
        TextView message;

        Button btn_Undo;
        Button btn_Delay;
        Button btn_Absent;

        LinearLayout lyt_parent;

        //dots menu
        CardView lyt_thread;
        ImageView image_status;
        ImageView image;
        ProgressBar mProgress1;
        TextView mProgress1Txt;

    }

}