package com.timaimee.vpdemo.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.OnRecycleViewClickCallback;
import com.timaimee.vpdemo.bean.TimeZoneBean;

import java.util.List;

/**
 * Description 世界时钟适配
 *
 * @author KYM.
 * @date 2024/4/10 16:25
 */
public class WorldClockAddListAdapter extends RecyclerView.Adapter<WorldClockAddListAdapter.MyViewHolder> {

    private final List<TimeZoneBean> list;
    private final OnRecycleViewClickCallback onDragItemClick;

    public WorldClockAddListAdapter(List<TimeZoneBean> list, OnRecycleViewClickCallback onDragItemClick) {
        this.onDragItemClick = onDragItemClick;
        this.list = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onDragItemClick.OnRecycleViewClick(position);
                }
            });

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onDragItemClick.OnRecycleViewClick(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_clock_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TimeZoneBean worldClock = list.get(position);
        holder.tvName.setText(worldClock.getCityName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}

