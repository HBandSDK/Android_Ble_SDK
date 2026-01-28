package com.timaimee.vpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.iielse.switchbutton.SwitchView;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.model.datas.AutoMeasureData;

import java.util.List;
import java.util.Locale;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class AutoMeasureAdapter extends RecyclerView.Adapter<AutoMeasureAdapter.ViewHolder> {
    private List<AutoMeasureData> data;
    public AutoMeasureAdapter(List<AutoMeasureData> data, OnAutoMeasureOptListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auto_measure, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.updateUI(data.get(position), listener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(data.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAutoMeasureName;
        TextView tvMeasureTime;
        TextView tvMeasureInterval;
        SwitchView sv;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAutoMeasureName = itemView.findViewById(R.id.tvAutoMeasureName);
            tvMeasureTime = itemView.findViewById(R.id.tvMeasureTime);
            tvMeasureInterval = itemView.findViewById(R.id.tvMeasureInterval);
            sv = itemView.findViewById(R.id.sv);
        }

        private String getAutoMeasureDataName(AutoMeasureData autoMeasureData){
            switch (autoMeasureData.getFunType()) {
                case PULSE_RATE:
                    return "心率";
                case BLOOD_PRESSURE:
                    return "血压";
                case BLOOD_GLUCOSE:
                    return "血糖";
                case STRESS:
                    return "压力";
                case BLOOD_OXYGEN:
                    return "血氧";
                case BODY_TEMPERATURE:
                    return "体温";
                case LORENZ:
                    return "洛伦兹";
                case HRV:
                    return "HRV";
                case BLOOD_COMPOSITION:
                    return "血液成分";
            }
            return "UNKNOWN";
        }

        private String getMeasureTime(AutoMeasureData autoMeasureData){
            int startTime = autoMeasureData.getCurrentStartMinute();
            int endTime = autoMeasureData.getCurrentEndMinute();
            return String.format(Locale.CHINA,"测量时间: %02d:%02d-%02d:%02d", startTime / 60,startTime % 60,endTime / 60,endTime % 60);
        }

        public void updateUI(final AutoMeasureData autoMeasureData, final OnAutoMeasureOptListener listener) {
            tvAutoMeasureName.setText(getAutoMeasureDataName(autoMeasureData));
            tvMeasureTime.setText(getMeasureTime(autoMeasureData));
            tvMeasureInterval.setText("测量间隔: " + autoMeasureData.getMeasureInterval() + "分钟");
            sv.setOpened(autoMeasureData.isSwitchOpen());
            sv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    autoMeasureData.setSwitchOpen(true);
                    if (listener != null) {
                        listener.onToggleChanged(autoMeasureData);
                    }
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    autoMeasureData.setSwitchOpen(false);
                    if (listener != null) {
                        listener.onToggleChanged(autoMeasureData);
                    }
                }
            });
        }
    }

    public interface OnAutoMeasureOptListener {
        void onToggleChanged(AutoMeasureData data);

        void onItemClick(AutoMeasureData data);
    }



    private OnAutoMeasureOptListener listener;

}
