package com.timaimee.vpdemo.adapter;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.iielse.switchbutton.SwitchView;
import com.timaimee.vpdemo.R;
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
        TextView tvHealthRemindName;
        TextView tvHealthRemindTime;
        SwitchView sv;
        EditText etInterval;

        public ViewHolder(View itemView) {
            super(itemView);
            tvHealthRemindName = itemView.findViewById(R.id.tvHealthRemindName);
            tvHealthRemindTime = itemView.findViewById(R.id.tvHealthRemindTime);
            etInterval = itemView.findViewById(R.id.etInterval);
            sv = itemView.findViewById(R.id.sv);
        }

        public void updateUI(final HealthRemind setting, final OnHealthRemindToggleChangeListener listener) {
            etInterval.setText("" + setting.getInterval());
            tvHealthRemindName.setText(setting.getRemindType().getDes());
            tvHealthRemindTime.setText("时间范围：" + setting.getStartTime().getClock() + "-" + setting.getEndTime().getClock());
            sv.setOpened(setting.getStatus());
            sv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    setting.setStatus(true);
                    String intervalStr = etInterval.getText().toString();
                    if(!TextUtils.isEmpty(intervalStr)) {
                       int interval = Integer.parseInt(intervalStr);
                       if(interval == 0) {
                           interval = setting.getInterval();
                       }
                       setting.setInterval(interval);
                    }
                    view.setOpened(true);
                    if (listener != null) {
                        listener.onToggleChanged(setting);
                    }
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    setting.setStatus(false);
                    String intervalStr = etInterval.getText().toString();
                    if(!TextUtils.isEmpty(intervalStr)) {
                        int interval = Integer.parseInt(intervalStr);
                        if(interval == 0) {
                            interval = setting.getInterval();
                        }
                        setting.setInterval(interval);
                    }
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
