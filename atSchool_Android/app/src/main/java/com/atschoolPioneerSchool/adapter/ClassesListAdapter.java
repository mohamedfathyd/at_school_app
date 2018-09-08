package com.atschoolPioneerSchool.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.activity_school_register_attendance;
import com.atschoolPioneerSchool.model.Classes;
import com.atschoolPioneerSchool.widget.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10/06/2017.
 */

public class ClassesListAdapter extends RecyclerView.Adapter<ClassesListAdapter.ViewHolder> implements Filterable {

    private List<Classes> original_items = new ArrayList<>();
    private List<Classes> filtered_items = new ArrayList<>();
    private ClassesListAdapter.ItemFilter mFilter = new ClassesListAdapter.ItemFilter();

    private Context ctx;
    private ClassesListAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Classes obj, int position);
    }

    public void setOnItemClickListener(final ClassesListAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ClassesListAdapter(Context context, List<Classes> items) {
        original_items = items;
        filtered_items = items;
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public ImageView image;
        public LinearLayout lyt_parent;
        public Button but_attendance;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
            but_attendance = (Button) v.findViewById(R.id.but_attendance);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }


    @Override
    public ClassesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_classes, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ClassesListAdapter.ViewHolder vh = new ClassesListAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ClassesListAdapter.ViewHolder holder, final int position) {
        final Classes c = filtered_items.get(position);

        holder.name.setText(c.ClassNameA + "  " + c.SectionNameA);
        Picasso.with(ctx).load(c.getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);
        setAnimation(holder.itemView, position);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, c, position);
                }
            }
        });


        holder.but_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), c.CLASS_SECTION_ID, Toast.LENGTH_SHORT).show();

                //save class section id to view students
                SharedPreferences sharedpref = v.getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = sharedpref.edit();
                edt.putString("CLASS_SECTION_ID", c.CLASS_SECTION_ID);
                edt.commit();

                Intent i = new Intent(v.getContext(), activity_school_register_attendance.class);
                v.getContext().startActivity(i);
            }
        });
    }

    public Classes getItem(int position) {
        return filtered_items.get(position);
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtered_items.size();
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Classes> list = original_items;
            final List<Classes> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).SectionNameA;
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered_items = (List<Classes>) results.values;
            notifyDataSetChanged();
        }
    }

}