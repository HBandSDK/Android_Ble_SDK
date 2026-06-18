package com.timaimee.vpdemo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.iielse.switchbutton.SwitchView;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.bean.CustomSettingItemBean;
import com.veepoo.protocol.model.enums.EFunctionStatus;

import java.util.List;

/**
 * Author: YWX
 * Date: 2021/9/30 17:05
 * Description:
 */
public class CustomSettingSwitchAdapter extends RecyclerView.Adapter<CustomSettingSwitchAdapter.ViewHolder> {

    private List<CustomSettingItemBean> settings;

    public CustomSettingSwitchAdapter(List<CustomSettingItemBean> data, OnCustomSettingOptListener listener) {
        settings = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_setting, parent, false));
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
        TextView tvFunctionName;
        TextView tvIsSupport;
        SwitchView svIsOpen;
        RadioGroup rgUnit;
        RadioButton rbUnit1;
        RadioButton rbUnit2;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFunctionName = itemView.findViewById(R.id.tvFunctionName);
            tvIsSupport = itemView.findViewById(R.id.tvIsSupport);
            svIsOpen = itemView.findViewById(R.id.svIsOpen);
            rgUnit = itemView.findViewById(R.id.rgUnit);
            rbUnit1 = itemView.findViewById(R.id.rbUnit1);
            rbUnit2 = itemView.findViewById(R.id.rbUnit2);
        }

        @SuppressLint("SetTextI18n")
        public void updateUI(final CustomSettingItemBean setting, final OnCustomSettingOptListener listener) {
            tvFunctionName.setText(setting.label);
            tvIsSupport.setText(setting.getStatus());
            tvIsSupport.setTextColor(setting.getStatusColor());

            if (setting.isUnitSwitch) {
                svIsOpen.setVisibility(View.GONE);
                rgUnit.setVisibility(View.VISIBLE);
                rbUnit1.setText(setting.unit1);
                rbUnit2.setText(setting.unit2);
                rgUnit.check(setting.checkedIndex == 0? R.id.rbUnit1 : R.id.rbUnit2);
                rgUnit.setVisibility(setting.isHaveFunction ? View.VISIBLE: View.GONE);
                rgUnit.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                    if (checkedId == R.id.rbUnit1) {
                        setting.checkedIndex = 0;
                    } else if(checkedId == R.id.rbUnit2) {
                        setting.checkedIndex = 1;
                    }
                    if (listener != null && setting.isHaveFunction) {
                        listener.onUnitChange(setting);
                    } else {
                        Toast.makeText(itemView.getContext(), setting.label + "不支持", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                svIsOpen.setVisibility(View.VISIBLE);
                rgUnit.setVisibility(View.GONE);
                svIsOpen.setOpened(setting.isOpen());
                svIsOpen.setEnabled(setting.isSupport());
                svIsOpen.setVisibility(setting.isSupport() ? View.VISIBLE : View.GONE);

                if (setting.label.equals("肤色")) {
                    tvIsSupport.setText("档位:" + setting.checkedIndex + "(根据DeviceFunctionPackage1.skinType自行设置)");
                    svIsOpen.setVisibility(View.GONE);
                }

                svIsOpen.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                    @Override
                    public void toggleToOn(SwitchView view) {
                        setting.status = EFunctionStatus.SUPPORT_OPEN;
                        if (listener != null) {
                            listener.onToggleChanged(setting);
                        }
                    }

                    @Override
                    public void toggleToOff(SwitchView view) {
                        setting.status = EFunctionStatus.SUPPORT_CLOSE;
                        if (listener != null) {
                            listener.onToggleChanged(setting);
                        }
                    }
                });
            }

        }
    }

    public interface OnCustomSettingOptListener {
        void onToggleChanged(CustomSettingItemBean setting);

        void onUnitChange(CustomSettingItemBean setting);
    }

    private OnCustomSettingOptListener listener;

}
