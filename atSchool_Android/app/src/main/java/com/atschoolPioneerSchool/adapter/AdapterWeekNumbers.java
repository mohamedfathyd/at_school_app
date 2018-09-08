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
import com.atschoolPioneerSchool.model.WeekNumbers;
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
 * Created by OmarA on 13/10/2017.
 */

public class AdapterWeekNumbers extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WeekNumbers> items = new ArrayList<>();

    private Context ctx;
    private WeekNumbers header;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private AdapterWeekNumbers.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, WeekNumbers obj, int position);
    }

    public void setOnItemClickListener(final AdapterWeekNumbers.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterWeekNumbers(Context context, WeekNumbers header, List<WeekNumbers> items) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_week_numbers, parent, false);
            return new AdapterWeekNumbers.ViewHolderHeader(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_week_numbers, parent, false);
            return new AdapterWeekNumbers.ViewHolder(v);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterWeekNumbers.ViewHolderHeader) {
            AdapterWeekNumbers.ViewHolderHeader vHeader = (AdapterWeekNumbers.ViewHolderHeader) holder;
            vHeader.title.setText(header.Name);
            vHeader.date.setText(header.Name);
            // Picasso.with(ctx).load(header.Name).into(vHeader.image);

            vHeader.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, header, position);
                    }
                }
            });

        } else if (holder instanceof AdapterWeekNumbers.ViewHolder) {
            final WeekNumbers c = items.get(position);
            AdapterWeekNumbers.ViewHolder vItem = (AdapterWeekNumbers.ViewHolder) holder;
            vItem.title.setText(c.Title);
            vItem.short_content.setText("     " + c.Name);

            if (c.HideDate) {
                vItem.date.setText("     ");
            } else {
                vItem.date.setText("     " + c.Id);
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

    public WeekNumbers getItem(int position) {
        return items.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}