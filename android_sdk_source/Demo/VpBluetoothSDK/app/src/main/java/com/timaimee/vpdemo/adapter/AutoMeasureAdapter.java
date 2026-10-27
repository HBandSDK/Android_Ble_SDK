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
import com.veepoo.protocol.model.enums.EAutoMeasureType;

import java.util.List;
import java.util.Locale;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class AutoMeasureAdapter extends RecyclerView.Adapter<AutoMeasureAdapter.ViewHolder> {

    /**SwitchView不接受null监听(会抛empty listener)，隐藏开关时用它占位*/
    private static final SwitchView.OnStateChangedListener NO_OP_LISTENER = new SwitchView.OnStateChangedListener() {
        @Override
        public void toggleToOn(SwitchView view) {
        }

        @Override
        public void toggleToOff(SwitchView view) {
        }
    };

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
        View llLowOxygen;
        SwitchView svLowOxygen;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAutoMeasureName = itemView.findViewById(R.id.tvAutoMeasureName);
            tvMeasureTime = itemView.findViewById(R.id.tvMeasureTime);
            tvMeasureInterval = itemView.findViewById(R.id.tvMeasureInterval);
            sv = itemView.findViewById(R.id.sv);
            llLowOxygen = itemView.findViewById(R.id.llLowOxygen);
            svLowOxygen = itemView.findViewById(R.id.svLowOxygen);
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
            case FALL_DETECTION:
                return "跌倒提醒";
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
            //低氧提醒开关：仅血氧测量项且设备支持(lowOxygenAlertSwitch>=0)时显示，否则隐藏
            boolean showLowOxygen = autoMeasureData.getFunType() == EAutoMeasureType.BLOOD_OXYGEN
                    && autoMeasureData.getLowOxygenAlertSwitch() >= 0;
            llLowOxygen.setVisibility(showLowOxygen ? View.VISIBLE : View.GONE);
            if (showLowOxygen) {
                svLowOxygen.setOpened(autoMeasureData.getLowOxygenAlertSwitch() == 1);
                //切换时更新数据并复用主开关的下发通道
                svLowOxygen.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                    @Override
                    public void toggleToOn(SwitchView view) {
                        autoMeasureData.setLowOxygenAlertSwitch(1);
                        if (listener != null) {
                            listener.onToggleChanged(autoMeasureData);
                        }
                    }

                    @Override
                    public void toggleToOff(SwitchView view) {
                        autoMeasureData.setLowOxygenAlertSwitch(0);
                        if (listener != null) {
                            listener.onToggleChanged(autoMeasureData);
                        }
                    }
                });
            } else {
                //SwitchView.setListener(null)会抛IllegalArgumentException，用no-op监听占位
                svLowOxygen.setOnStateChangedListener(NO_OP_LISTENER);
            }
        }
    }

    public interface OnAutoMeasureOptListener {
        void onToggleChanged(AutoMeasureData data);

        void onItemClick(AutoMeasureData data);
    }



    private OnAutoMeasureOptListener listener;

}
