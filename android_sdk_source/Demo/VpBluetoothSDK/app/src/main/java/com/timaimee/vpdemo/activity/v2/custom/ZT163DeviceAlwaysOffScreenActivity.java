package com.timaimee.vpdemo.activity.v2.custom;

import android.view.View;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.veepoo.protocol.listener.data.IZT163DeviceAlwaysOffScreenOptListener;

public class ZT163DeviceAlwaysOffScreenActivity extends BaseActivity implements IZT163DeviceAlwaysOffScreenOptListener{

    @Override
    public int getLayoutID() {
        return R.layout.activity_zt163_device_always_off_screen;
    }

    @Override
    public String pageTitle() {
        return "合镁ZT163设备常灭屏";
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        findViewById(R.id.btnOpen).setOnClickListener(v -> {
            vpBleManager.setZT163DeviceAlwaysOffScreen(true, code -> {
            },this);
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            vpBleManager.setZT163DeviceAlwaysOffScreen(false, code -> {
            },this);
        });

        findViewById(R.id.btnRead).setOnClickListener(v -> {
            vpBleManager.readZT163DeviceAlwaysOffScreen(code -> {
            },this);
        });
    }

    @Override
    public void onZT163DeviceAlwaysOffScreenSettingSuccess(boolean isOpen) {
        appendResult("⏯️:设置成功：" + (isOpen?"开启":"关闭"));
    }

    @Override
    public void onZT163DeviceAlwaysOffScreenSettingFailed() {
        appendRedLargeText("❌️:设置失败" );
    }

    @Override
    public void onZT163DeviceAlwaysOffScreenReport(boolean isOpen) {
        appendBlueMiddleText("✔️:读取上报成功成功：" + (isOpen?"开启":"关闭"));
    }

    @Override
    public void onFunctionNotSupport() {
        appendRedLargeText("⚠️:当前设备不支持改功能" );
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean hasCommonMsgUI() {
        return true;
    }
}
