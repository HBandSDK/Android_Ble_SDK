package com.timaimee.vpdemo.activity.v2.custom;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.QX17DataAcquisitionActivity;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.timaimee.vpdemo.adapter.FunctionAdapter;

public class CustomFunctionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gvFunction;
    FunctionAdapter adapter;

    @Override
    public int getLayoutID() {
        return R.layout.activity_functions;
    }

    @Override
    public String pageTitle() {
        return "客户定制功能";
    }

    @Override
    public void initView() {
        gvFunction = findViewById(R.id.gvFunctions);
    }

    @Override
    public void initData() {
        initHealthFunction();
    }

    @Override
    public void initEvent() {

    }

    private void initHealthFunction(){
        adapter = new FunctionAdapter(this, DeviceMenu.CustomMenu);
        gvFunction.setAdapter(adapter);
        gvFunction.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String function = DeviceMenu.CustomMenu[i];
        switch (function) {
            case DeviceMenu.Custom.TCM -> startActivity(new Intent(this, TCMActivity.class));
            case DeviceMenu.Custom.PSAIM -> startActivity(new Intent(this, PSAIMHealthDataActivity.class));
            case DeviceMenu.Custom.ZT163_DEVICE_ALWAYS_OFF_SCREEN -> startActivity(new Intent(this, ZT163DeviceAlwaysOffScreenActivity.class));
            case DeviceMenu.Custom.G15_IMG -> startActivity(new Intent(this, UiUpdateG15ImgActivity.class));
            case DeviceMenu.Custom.QX17_DATA_ACQUISITION -> startActivity(new Intent(this, QX17DataAcquisitionActivity.class));
            case DeviceMenu.Custom.G08W_HEALTH_ALARM_INTERVAL -> startActivity(new Intent(this, G08WHealthAlarmIntervalActivity.class));
            case DeviceMenu.Custom.G15_QR_CODE -> startActivity(new Intent(this, G15QRCodeActivity.class));
            case DeviceMenu.Custom.JH58_PPG -> startActivity(new Intent(this, JH58PPGOptTestActivity.class));
        }

    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

    }
}
