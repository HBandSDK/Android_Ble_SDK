package com.timaimee.vpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.model.datas.WorldClock;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Description 世界时钟适配
 *
 * @author KYM.
 * @date 2024/4/10 16:25
 */
public class WorldClockAdapter extends RecyclerView.Adapter<WorldClockAdapter.MyViewHolder> {

    private final List<WorldClock> list;
    private final OnRecycleViewClickCallback onDragItemClick;

    public WorldClockAdapter(List<WorldClock> list, OnRecycleViewClickCallback onDragItemClick) {
        this.onDragItemClick = onDragItemClick;
        this.list = list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvInfo;
        private final TextView tvTime;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvInfo = (TextView) itemView.findViewById(R.id.tvInfo);
            ImageView ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onDragItemClick.OnRecycleViewClick(0x00 | position);
                }
            });

            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    onDragItemClick.OnRecycleViewClick(0x10 | position);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_world_clock, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WorldClock worldClock = list.get(position);
        int offset = worldClock.getTimeZone();
        int minute = offset * 15;
        int hour = minute / 60;
        holder.tvName.setText(worldClock.getTimeZoneName());
        holder.tvTime.setText(getTimeWithOffset(hour, 0, true));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public String getTimeWithOffset(int hour, int minute, boolean is24Hour) {
        int localOffset = TimeZone.getDefault().getRawOffset();
        int iOffset = (hour * 60 * 60 + minute * 60) * 1000;
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTime().getTime() - localOffset + iOffset;
        calendar.setTimeInMillis(time);
        int iHour = calendar.get(Calendar.HOUR_OF_DAY);
        int iMinute = calendar.get(Calendar.MINUTE);
        if (is24Hour) {
            return String.format(Locale.getDefault(), "%02d:%02d", iHour, iMinute);
        } else {
            return get12HourModel(iHour, iMinute);
        }
    }

    private String get12HourModel(int hour, int minute) {
        String amOrPm = "AM";
        if (hour < 12) {
            amOrPm = "AM";
        } else {
            amOrPm = "PM";
        }

        if (hour == 0) {
            hour = 12;
        }
        if (hour > 12) {
            hour = hour % 12;
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amOrPm);
    }
}

