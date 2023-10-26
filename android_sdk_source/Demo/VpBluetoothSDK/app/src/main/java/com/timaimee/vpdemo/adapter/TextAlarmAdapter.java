package com.timaimee.vpdemo.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.SwitchView;
import com.veepoo.protocol.model.settings.TextAlarm2Setting;

import java.util.List;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class TextAlarmAdapter extends RecyclerView.Adapter<TextAlarmAdapter.ViewHolder> {

    private List<TextAlarm2Setting> settings;

    public TextAlarmAdapter(List<TextAlarm2Setting> data, OnTextAlarmToggleChangeListener listener) {
        settings = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_alarm, null));
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
        TextView tvAlarmContent;
        TextView tvAlarmTime;
        SwitchView sv;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAlarmID = itemView.findViewById(R.id.tvAlarmId);
            tvAlarmRepeat = itemView.findViewById(R.id.tvAlarmRepeat);
            tvAlarmContent = itemView.findViewById(R.id.tvAlarmContent);
            tvAlarmTime = itemView.findViewById(R.id.tvAlarmTime);
            sv = itemView.findViewById(R.id.sv);
        }
        public void updateUI(final TextAlarm2Setting setting, final OnTextAlarmToggleChangeListener listener) {
            tvAlarmID.setText("" + setting.getAlarmId());
            tvAlarmContent.setText(setting.getContent());
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

    public interface OnTextAlarmToggleChangeListener {
        void onToggleChanged(TextAlarm2Setting setting);
    }


    private OnTextAlarmToggleChangeListener listener;


}
