package com.atschoolPioneerSchool.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.model.ChatContacts;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by OmarA on 13/12/2017.
 */

public class AdapterMarefahListBooks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatContacts> items = new ArrayList<>();

    private Context ctx;
    private ChatContacts header;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private AdapterMarefahListBooks.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ChatContacts obj, int position);
    }

    public void setOnItemClickListener(final AdapterMarefahListBooks.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterMarefahListBooks(Context context, ChatContacts header, List<ChatContacts> items) {
        this.items = items;
        this.header = header;
        ctx = context;
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView short_content;
        public TextView date;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            short_content = (TextView) v.findViewById(R.id.short_content);
            date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.image);

            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolderHeader(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_marefah_book, parent, false);
            return new AdapterMarefahListBooks.ViewHolderHeader(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_marefah_book, parent, false);
            return new AdapterMarefahListBooks.ViewHolder(v);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterMarefahListBooks.ViewHolderHeader) {
            AdapterMarefahListBooks.ViewHolderHeader vHeader = (AdapterMarefahListBooks.ViewHolderHeader) holder;
            vHeader.title.setText(header.getTitle());
            vHeader.date.setText(header.getDate());
            Picasso.with(ctx).load(header.getImage()).into(vHeader.image);

            vHeader.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, header, position);
                    }
                }
            });

        } else if (holder instanceof AdapterMarefahListBooks.ViewHolder) {
            final ChatContacts c = items.get(position);
            AdapterMarefahListBooks.ViewHolder vItem = (AdapterMarefahListBooks.ViewHolder) holder;
            vItem.title.setText(c.getTitle());


            if (c.ChatTextMessage.equals("")) {
                vItem.short_content.setText(c.getShort_content());
            } else {
                vItem.short_content.setText(c.ChatTextMessage);
            }


            vItem.date.setText(c.ChatSend_Date);
            // Picasso.with(ctx).load(c.getImage()).into(vItem.image);

        /*    DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_atschool)
                    .showImageForEmptyUri(R.drawable.ic_nav_setting)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                    .build();*/


            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_atschool)
                    .showImageForEmptyUri(R.drawable.ic_nav_setting)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoadingListener animateFirstListener = new AdapterMarefahListBooks.AnimateFirstDisplayListener();

            //0
            //1   Activities الأنشطة
            //2   Agenda جدول أعمال المدرسه
            //3   School Managements  إدارة المدارس
            //4   School Facilities   مرافق المدرسة
            //5   Vision الرؤية
            //6   Mission المهمة
            //7   About حول
            //8   ChatContacts الأخبار
            //9   Complaint
            //14 sons kids
//15 marefah books

            if (!c.Img1.equals("")) {

                if (c.NewsType.equals("9")) {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Complaint_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                } else if (c.NewsType.equals("10")) {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Suggestion_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                } else if (c.NewsType.equals("11")) {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Maintenance_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                } else if (c.NewsType.equals("13")) {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Chat_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                } else if (c.NewsType.equals("14")) {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Chat_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                } else if (c.NewsType.equals("15")) {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_Marefah_Book_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                } else {
                    ImageLoader.getInstance().displayImage(ctx.getString(R.string.URL_News_Images) + String.valueOf(c.Img1)
                            , vItem.image, options, animateFirstListener);
                }
            }

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, c, position);
                    }
                }
            });


        }

        setAnimation(holder.itemView, position);

    }

    // Here is the key method to apply the animation
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == -1;
    }

    public ChatContacts getItem(int position) {
        return items.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
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

}