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
import com.atschoolPioneerSchool.model.BusOrder;
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
 * Created by OmarA on 24/11/2017.
 */

public class AdapterBusOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BusOrder> items = new ArrayList<>();

    private Context ctx;
    private BusOrder header;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private AdapterBusOrder.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, BusOrder obj, int position);
    }

    public void setOnItemClickListener(final AdapterBusOrder.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterBusOrder(Context context, BusOrder header, List<BusOrder> items) {
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
        public TextView assistant;
        public TextView triptime;
        public TextView direction;

        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            short_content = (TextView) v.findViewById(R.id.short_content);
            assistant = (TextView) v.findViewById(R.id.assistant);
            triptime = (TextView) v.findViewById(R.id.triptime);
            direction = (TextView) v.findViewById(R.id.direction);
            image = (ImageView) v.findViewById(R.id.image);

            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView assistant;
        public TextView triptime;
        public TextView direction;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ViewHolderHeader(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            assistant = (TextView) v.findViewById(R.id.assistant);
            triptime = (TextView) v.findViewById(R.id.triptime);
            direction = (TextView) v.findViewById(R.id.direction);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bus_order, parent, false);
            return new AdapterBusOrder.ViewHolderHeader(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bus_order, parent, false);
            return new AdapterBusOrder.ViewHolder(v);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterBusOrder.ViewHolderHeader) {
            AdapterBusOrder.ViewHolderHeader vHeader = (AdapterBusOrder.ViewHolderHeader) holder;
            vHeader.title.setText(header.Buss_Information);
            vHeader.assistant.setText(header.Start_Time);
            vHeader.triptime.setText(header.Start_Time + "  -  " + header.End_Time);
            vHeader.direction.setText(header.Direction);
            Picasso.with(ctx).load(header.getImage()).into(vHeader.image);

            vHeader.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, header, position);
                    }
                }
            });

        } else if (holder instanceof AdapterBusOrder.ViewHolder) {
            final BusOrder c = items.get(position);
            AdapterBusOrder.ViewHolder vItem = (AdapterBusOrder.ViewHolder) holder;
            vItem.title.setText(c.Buss_Information);
            vItem.short_content.setText(c.Driver);
            vItem.assistant.setText(c.Assistant);
            vItem.triptime.setText(c.Start_Time + "  -  " + c.End_Time);
            vItem.direction.setText(c.Direction);

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

    public BusOrder getItem(int position) {
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