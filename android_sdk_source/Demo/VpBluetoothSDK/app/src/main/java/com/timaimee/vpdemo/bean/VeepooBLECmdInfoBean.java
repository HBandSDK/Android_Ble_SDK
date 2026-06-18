package com.timaimee.vpdemo.bean;

import java.util.Locale;

public class VeepooBLECmdInfoBean {
    public byte cmdHeader;

    public String hexCode;

    public byte value;
    public String valueHex;

    public int index;

    public boolean isSupport;

    public boolean isOpen;

    public String function;

    public String describe;

    public int packageIndex;

    public static VeepooBLECmdInfoBean createA7CmdInfoBean(String function, int index, byte value, String describe, int packageIndex) {
        return new VeepooBLECmdInfoBean((byte) 0xA7, index,value, function, describe, packageIndex);
    }

    public static VeepooBLECmdInfoBean createB8StateInfoBean(String function, int index, byte value, String describe, int packageIndex) {
        return new VeepooBLECmdInfoBean((byte) 0xB8, index, value, function, describe, packageIndex);
    }

    public VeepooBLECmdInfoBean(byte cmdHeader, int index, byte value, String function, String describe, int packageIndex) {
        this.cmdHeader = cmdHeader;
        this.index = index;
        this.hexCode = ConvertHelper.byte2Hex(new byte[]{cmdHeader});
        this.valueHex = ConvertHelper.byte2Hex(new byte[]{value});
        this.value = value;
        this.function = function;
        this.describe = describe;
        if (cmdHeader == (byte) 0xA7 || cmdHeader == (byte) 0xB8) {
            if (value == 0) {
                this.isSupport = false;
                this.isOpen = false;
            } else {
                isSupport = true;
                if (cmdHeader == (byte) 0xB8) {
                    if (value == 1) {
                        isOpen = true;
                    }
                    if (value == 2) {
                        isOpen = false;
                    }
                }
            }
        } else {
            this.isSupport = false;
            this.isOpen = false;
        }
        this.packageIndex = packageIndex;
    }

    public VeepooBLECmdInfoBean(byte cmdHeader, String function, byte value, boolean isSupport, boolean isOpen, String describe, int packageIndex) {
        this.cmdHeader = cmdHeader;
        this.hexCode = ConvertHelper.byte2Hex(new byte[]{cmdHeader});
        this.value = value;
        this.isSupport = isSupport;
        this.isOpen = isOpen;
        this.describe = describe;
        this.packageIndex = packageIndex;
    }

    @Override
    public String toString() {
        return "VeepooBLECmdInfoBean{" +
                "cmdHeader=" + cmdHeader +
                ", hexCode='" + hexCode + '\'' +
                ", value=" + value +
                ", valueHex='" + valueHex + '\'' +
                ", isSupport=" + isSupport +
                ", isOpen=" + isOpen +
                ", function='" + function + '\'' +
                ", describe='" + describe + '\'' +
                ", packageIndex=" + packageIndex +
                '}';
    }

    public String getLogInfo() {
        return "〖" + hexCode + "〗第" + String.format(Locale.CHINA,"%02d", packageIndex) + "包第" + String.format(Locale.CHINA,"%02d", index)
                + "位:『0x" + valueHex + "』------>功能:【" + function + "】, 描述: 〖" + describe + "〗";
    }
}
