package com.timaimee.vpdemo.bean;

import android.graphics.Color;

import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ESocailMsg;

public class NotificationFunction {
    public EFunctionStatus status;
    public String label;
    public ESocailMsg type;

    public NotificationFunction(ESocailMsg type, EFunctionStatus status, String label) {
        this.type = type;
        this.status = status;
        this.label = label;
    }

    @Override
    public String toString() {
        return "NotificationFunction{" +
                "status=" + status +
                ", label='" + label + '\'' +
                ", type=" + type +
                '}';
    }

    public String getStatus(){
        if (isSupport()) {
            return "支持";
        } else {
            return "不支持";
        }
    }

    public boolean isSupport() {
        return !(status == EFunctionStatus.UNSUPPORT || status == EFunctionStatus.UNKONW);
    }

    public int getStatusColor() {
        if (status == EFunctionStatus.UNSUPPORT || status == EFunctionStatus.UNKONW) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }

    public boolean isOpen() {
        return status == EFunctionStatus.SUPPORT_OPEN;
    }
}
