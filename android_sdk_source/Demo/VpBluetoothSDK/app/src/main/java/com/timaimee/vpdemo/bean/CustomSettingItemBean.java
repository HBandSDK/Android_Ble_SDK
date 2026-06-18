package com.timaimee.vpdemo.bean;

import android.graphics.Color;

import com.veepoo.protocol.model.enums.EFunctionStatus;

public class CustomSettingItemBean {
    public String label;

    public EFunctionStatus status;

    public boolean isUnitSwitch = false;

    public int checkedIndex = 0;

    public String unit1 = "";
    public String unit2 = "";

    public boolean isHaveFunction = true;

    public CustomSettingItemBean(String label, EFunctionStatus status) {
        this.label = label;
        this.status = status;
        this.isUnitSwitch = false;
        this.isHaveFunction = status != EFunctionStatus.UNSUPPORT && status != EFunctionStatus.UNKONW;
    }

    public CustomSettingItemBean(String label, boolean isHaveFunction, int checkedIndex, String unit1, String unit2) {
        this.label = label;
        this.isHaveFunction = isHaveFunction;
        this.checkedIndex = checkedIndex;
        this.unit1 = unit1;
        this.unit2 = unit2;
        this.isUnitSwitch = true;
    }

    public String getStatus(){
        if (isHaveFunction) {
            return "支持";
        } else {
            return "不支持";
        }
    }

    public boolean isSupport() {
        return !(status == EFunctionStatus.UNSUPPORT || status == EFunctionStatus.UNKONW);
    }

    public int getStatusColor() {
        if (!isHaveFunction) {
            return Color.parseColor("#EB232E");
        } else {
            return Color.parseColor("#4ECD42");
        }
    }

    public boolean isOpen() {
        return status == EFunctionStatus.SUPPORT_OPEN;
    }
}
