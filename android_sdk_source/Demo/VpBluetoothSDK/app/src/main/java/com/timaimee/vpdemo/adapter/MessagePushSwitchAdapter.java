package com.timaimee.vpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.iielse.switchbutton.SwitchView;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.bean.NotificationFunction;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.TextAlarm2Setting;

import java.util.List;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class MessagePushSwitchAdapter extends RecyclerView.Adapter<MessagePushSwitchAdapter.ViewHolder> {

    private List<NotificationFunction> settings;

    public MessagePushSwitchAdapter(List<NotificationFunction> data, OnNotifySwitchToggleChangeListener listener) {
        settings = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push, parent, false));
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
        TextView tvMessageName;
        TextView tvMessageStatus;
        SwitchView svMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMessageName = itemView.findViewById(R.id.tvMessageName);
            tvMessageStatus = itemView.findViewById(R.id.tvMessageStatus);
            svMessage = itemView.findViewById(R.id.svMessage);
        }

        public void updateUI(final NotificationFunction notify, final OnNotifySwitchToggleChangeListener listener) {
            tvMessageName.setText(notify.label);
            tvMessageStatus.setText(notify.getStatus());
            tvMessageStatus.setTextColor(notify.getStatusColor());
            svMessage.setOpened(notify.isOpen());
            svMessage.setEnabled(notify.isSupport());
            svMessage.setVisibility(notify.isSupport() ? View.VISIBLE : View.INVISIBLE);
            svMessage.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    notify.status = EFunctionStatus.SUPPORT_OPEN;
                    if (listener != null) {
                        listener.onToggleChanged(notify);
                    }
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    notify.status = EFunctionStatus.SUPPORT_CLOSE;
                    if (listener != null) {
                        listener.onToggleChanged(notify);
                    }
                }
            });
        }
    }

    public interface OnNotifySwitchToggleChangeListener {
        void onToggleChanged(NotificationFunction notify);
    }

    private OnNotifySwitchToggleChangeListener listener;

}
