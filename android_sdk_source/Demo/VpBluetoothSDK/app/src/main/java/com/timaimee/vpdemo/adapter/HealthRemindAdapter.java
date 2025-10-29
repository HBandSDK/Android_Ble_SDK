package com.timaimee.vpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.SwitchView;
import com.veepoo.protocol.model.datas.HealthRemind;

import java.util.List;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class HealthRemindAdapter extends RecyclerView.Adapter<HealthRemindAdapter.ViewHolder> {

    private List<HealthRemind> settings;

    public HealthRemindAdapter(List<HealthRemind> data, OnHealthRemindToggleChangeListener listener) {
        settings = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_remind, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.updateUI(settings.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHealthRemindInfo;
        TextView tvHealthTime;
        SwitchView sv;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHealthRemindInfo = itemView.findViewById(R.id.tvHealthInfo);
            tvHealthTime = itemView.findViewById(R.id.tvTime);
            sv = itemView.findViewById(R.id.sv);
        }

        public void updateUI(final HealthRemind setting, final OnHealthRemindToggleChangeListener listener) {
            tvHealthRemindInfo.setText(setting.getRemindType().getDes() + "提醒 间隔（分钟）：" + setting.getInterval());
            tvHealthTime.setText("时间范围：" + setting.getStartTime().getClock() + "-" + setting.getEndTime().getClock());
            sv.setOpened(setting.getStatus());
            sv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    setting.setStatus(true);
                    view.setOpened(true);
                    if (listener != null) {
                        listener.onToggleChanged(setting);
                    }
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    setting.setStatus(false);
                    view.setOpened(false);
                    if (listener != null) {
                        listener.onToggleChanged(setting);
                    }
                }
            });
        }
    }

    public interface OnHealthRemindToggleChangeListener {
        void onToggleChanged(HealthRemind setting);
    }

    private OnHealthRemindToggleChangeListener listener;

}
