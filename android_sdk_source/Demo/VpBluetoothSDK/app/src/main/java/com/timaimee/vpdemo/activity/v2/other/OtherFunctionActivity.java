package com.timaimee.vpdemo.activity.v2.other;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.NotificationSettingsActivity;
import com.timaimee.vpdemo.activity.TextImagePushActivity;
import com.timaimee.vpdemo.activity.WorldClockActivity;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.timaimee.vpdemo.activity.v2.health.FunctionTestActivity;
import com.timaimee.vpdemo.adapter.FunctionAdapter;

public class OtherFunctionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gvFunction;
    FunctionAdapter adapter;

    @Override
    public int getLayoutID() {
        return R.layout.activity_functions;
    }

    @Override
    public String pageTitle() {
        return "其他功能";
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
        adapter = new FunctionAdapter(this, DeviceMenu.OtherMenu);
        gvFunction.setAdapter(adapter);
        gvFunction.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String function = DeviceMenu.OtherMenu[i];
        if (!checkFunction(function) && false){
            return;
        }
        showMsg(function);
        switch (function) {
            case DeviceMenu.Other.CONTACT -> startActivity(new Intent(this, ContactActivity.class));
            case DeviceMenu.Other.ALARM -> startActivity(new Intent(this, AlarmClockActivity.class));
            case DeviceMenu.Other.MUSIC -> startActivity(new Intent(this, MusicActivity.class));
            case DeviceMenu.Other.MESSAGE_PUSH -> startActivity(new Intent(this, NotificationSettingsActivity.class));
            case DeviceMenu.Other.IMG_TXT_PUSH -> startActivity(new Intent(this, TextImagePushActivity.class));
            case DeviceMenu.Other.WORLD_CLOCK -> startActivity(new Intent(this, WorldClockActivity.class));
            case DeviceMenu.Other.WEATHER -> startActivity(new Intent(this, WeatherActivity.class));
            case DeviceMenu.Other.GNSS ->  startActivity(new Intent(this, GNSSOptActivity.class));
            case DeviceMenu.Other.PHOTOGRAPH,
                 DeviceMenu.Other.CHECK_WEAR,
                 DeviceMenu.Other.DEVICE_ANTI_LOSS
                    -> FunctionTestActivity.Companion.start(this, function);
            case DeviceMenu.Other.LANGUAGE_BATTER ->   startActivity(new Intent(this, LanguageAndBatteryActivity.class));
            case DeviceMenu.Other.DEVICE_4G ->   startActivity(new Intent(this, Device4gOptActivity.class));
            case DeviceMenu.Other.MAGNETIC ->   startActivity(new Intent(this, MagneticTherapyActivity.class));
        }

    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

    }
}
