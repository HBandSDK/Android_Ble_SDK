package com.timaimee.vpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.SwitchView;
import com.veepoo.protocol.model.settings.Alarm2Setting;

import java.util.List;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class NewAlarmAdapter extends RecyclerView.Adapter<NewAlarmAdapter.ViewHolder> {

    private List<Alarm2Setting> settings;

    public NewAlarmAdapter(List<Alarm2Setting> data, OnNewAlarmToggleChangeListener listener) {
        settings = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_alarm, null));
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
        TextView tvAlarmID;
        TextView tvAlarmRepeat;
        TextView tvAlarmTime;
        SwitchView sv;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAlarmID = itemView.findViewById(R.id.tvAlarmId);
            tvAlarmRepeat = itemView.findViewById(R.id.tvAlarmRepeat);
            tvAlarmTime = itemView.findViewById(R.id.tvAlarmTime);
            sv = itemView.findViewById(R.id.sv);
        }

        public void updateUI(final Alarm2Setting setting, final OnNewAlarmToggleChangeListener listener) {
            tvAlarmID.setText("" + setting.getAlarmId());
            tvAlarmRepeat.setText(setting.getRepeatStatus());
            tvAlarmTime.setText(setting.getAlarmHour() + ":" + setting.getAlarmMinute());
            sv.setOpened(setting.isOpen);
            sv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    setting.setOpen(true);
                    if (listener != null) {
                        listener.onToggleChanged(setting);
                    }
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    setting.setOpen(false);
                    if (listener != null) {
                        listener.onToggleChanged(setting);
                    }
                }
            });
        }
    }

    public interface OnNewAlarmToggleChangeListener {
        void onToggleChanged(Alarm2Setting setting);
    }

    private OnNewAlarmToggleChangeListener listener;

}
