package com.timaimee.vpdemo.activity.v2;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.custom.CustomFunctionActivity;
import com.timaimee.vpdemo.activity.v2.health.HealthFunctionActivity;
import com.timaimee.vpdemo.bean.MyDeviceInfo;

import java.text.MessageFormat;

/**
 * 设备功能菜单页面
 */
public class DeviceFunctionMenuActivity extends BaseActivity {

    Button btnHealthFunction, btnOtherFunction, btnSwitchFunction, btnWatchFace, btnConnectSetting, btnOTAUpgrade, btnCustomFunction;
    TextView tvDeviceInfo;

    @Override
    public int getLayoutID() {
        return R.layout.activity_device_function_menu;
    }

    @Override
    public String pageTitle() {
        return "设备功能菜单";
    }

    @Override
    public void initView() {
        btnHealthFunction = findViewById(R.id.btnHealthFunction);
        btnOtherFunction = findViewById(R.id.btnOtherFunction);
        btnSwitchFunction = findViewById(R.id.btnSwitchFunction);
        btnWatchFace = findViewById(R.id.btnWatchFace);
        btnConnectSetting = findViewById(R.id.btnConnectSetting);
        btnOTAUpgrade = findViewById(R.id.btnOTAUpgrade);
        btnCustomFunction = findViewById(R.id.btnCustomFunction);
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
    }

    @Override
    public void initData() {
        tvDeviceInfo.setText(
                MessageFormat.format("蓝牙名:{0}\n地址:{1}\n设备号:{2}\n固件版本:{3}", MyDeviceInfo.INSTANCE.getDeviceName(), MyDeviceInfo.INSTANCE.getDeviceAddress(), MyDeviceInfo.INSTANCE.getDeviceNumber(), MyDeviceInfo.INSTANCE.getDeviceTestVersion()));
    }

    @Override
    public void initEvent() {
        btnHealthFunction.setOnClickListener(this);
        btnOtherFunction.setOnClickListener(this);
        btnSwitchFunction.setOnClickListener(this);
        btnWatchFace.setOnClickListener(this);
        btnConnectSetting.setOnClickListener(this);
        btnOTAUpgrade.setOnClickListener(this);
        btnCustomFunction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.btnHealthFunction) {
            startActivity(new Intent(this, HealthFunctionActivity.class));
        } else if (id == R.id.btnOtherFunction) {

        } else if (id == R.id.btnSwitchFunction) {

        } else if (id == R.id.btnWatchFace) {

        } else if (id == R.id.btnConnectSetting) {

        } else if (id == R.id.btnOTAUpgrade) {
            startActivity(new Intent(this, OTAActivity.class));
        } else if (id == R.id.btnCustomFunction) {
            startActivity(new Intent(this, CustomFunctionActivity.class));
        }
    }
}
