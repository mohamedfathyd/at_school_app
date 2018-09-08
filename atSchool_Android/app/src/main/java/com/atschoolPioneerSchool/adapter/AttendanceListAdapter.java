package com.atschoolPioneerSchool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.model.Attendance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/06/2017.
 */

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.ViewHolder> implements Filterable {

    private List<Attendance> original_items = new ArrayList<>();
    private List<Attendance> filtered_items = new ArrayList<>();
    private AttendanceListAdapter.ItemFilter mFilter = new AttendanceListAdapter.ItemFilter();
    private Context ctx;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView content;
        public TextView date;
        public ImageView image;
        public LinearLayout lyt_parent;
        public TextView Num;
        public TextView Account;
        public TextView amount;
        public CheckBox ch_absence;
        public CheckBox ch_delay;


        public ViewHolder(View v) {
            super(v);
            content = (TextView) v.findViewById(R.id.content);
            date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
            Num = (TextView) v.findViewById(R.id.Num);
            Account = (TextView) v.findViewById(R.id.Account);
            amount = (TextView) v.findViewById(R.id.amount);
            ch_absence = (CheckBox) v.findViewById(R.id.ch_absence);
            ch_delay = (CheckBox) v.findViewById(R.id.ch_delay);

        }

    }

    public Filter getFilter() {
        return mFilter;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AttendanceListAdapter(Context ctx, List<Attendance> items) {
        this.ctx = ctx;
        original_items = items;
        filtered_items = items;
    }

    @Override
    public AttendanceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attendance, parent, false);
        // set the view's size, margins, paddings and layout parameters
        AttendanceListAdapter.ViewHolder vh = new AttendanceListAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final AttendanceListAdapter.ViewHolder holder, final int position) {
        Attendance n = filtered_items.get(position);


        holder.content.setText(Html.fromHtml(n.NameAsInPass));
        holder.date.setText("  " + n.Datestr);
        holder.amount.setText("  ");
        holder.Num.setText(n.NameAsPassEng);
        holder.Account.setText("");

        //  if (n.Absence) {
        holder.ch_absence.setChecked(n.Absence);
        //   }

        // if (n.Delay) {
        holder.ch_delay.setChecked(n.Delay);
        //}

//        Picasso.with(ctx).load(n.getFriend().getPhoto())
//                .resize(60, 60)
//                .transform(new CircleTransform())
//                .into(holder.image);

        holder.ch_absence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.ch_absence.isChecked();
                if (isChecked) {
                    filtered_items.get(position).Absence = true;
                    filtered_items.get(position).Delay = false;
                    holder.ch_delay.setChecked(false);
                } else {
                    filtered_items.get(position).Absence = false;
                }

            }
        });


        holder.ch_delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.ch_delay.isChecked();
                if (isChecked) {
                    filtered_items.get(position).Delay = true;
                    filtered_items.get(position).Absence = false;
                    holder.ch_absence.setChecked(false);
                } else {
                    filtered_items.get(position).Delay = false;
                }

            }
        });


/*
        holder.ch_absence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                filtered_items.get(position).Absence = holder.ch_absence.isChecked() ;
              //  if (holder.ch_absence.isChecked()) {
                    //  holder.ch_delay.setChecked(false);
                     //filtered_items.get(position).Absence = true;
                    //  filtered_items.get(position).Delay = false;
                    //   original_items.get(position).Absence = true;
                    //  original_items.get(position).Delay = false;

               // }
            }
        });*/

       /* holder.ch_delay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filtered_items.get(position).Delay = holder.ch_delay.isChecked() ;

               // if (holder.ch_delay.isChecked()) {
                    //   holder.ch_absence.setChecked(false);
                    //  filtered_items.get(position).Absence = false;
                    //  filtered_items.get(position).Delay = true;
                    // original_items.get(position).Absence = false;
                    // original_items.get(position).Delay = true;

                //}
            }
        });*/


        setAnimation(holder.itemView, position);

           /* // view detail message conversation
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, " " + n.NameAsPassEng , Snackbar.LENGTH_SHORT).show();
                }ch_absence
            });*/

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

    @Override
    public long getItemId(int position) {
        return filtered_items.get(position).getId();
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Attendance> list = original_items;
            final List<Attendance> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).NameAsInPass;
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
            filtered_items = (List<Attendance>) results.values;
            notifyDataSetChanged();
        }

    }
}