package com.timaimee.vpdemo.activity.v2.health;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.timaimee.vpdemo.adapter.HealthFunctionAdapter;

public class HealthFunctionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gvFunction;
    HealthFunctionAdapter adapter;

    @Override
    public int getLayoutID() {
        return R.layout.activity_functions;
    }

    @Override
    public String pageTitle() {
        return "健康功能";
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
        adapter = new HealthFunctionAdapter(this, DeviceMenu.HealthMenu);
        gvFunction.setAdapter(adapter);
        gvFunction.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String function = DeviceMenu.HealthMenu[i];
        showMsg(function);
        switch (function) {
            case DeviceMenu.Health.HealthData -> {
                startActivity(new Intent(this, HealthDataReadOptActivity.class));
            }
            case DeviceMenu.Health.HeartRate -> {
                startActivity(new Intent(this, HeartRateDetectActivity.class));
            }
            case DeviceMenu.Health.BloodPress -> {

            }
            case DeviceMenu.Health.BloodGlucose -> {
                startActivity(new Intent(this, BloodGlucoseActivity.class));
            }
            case DeviceMenu.Health.BloodOxygen -> {

            }
            case DeviceMenu.Health.BloodComponent -> {
                startActivity(new Intent(this, BloodComponentActivity.class));
            }
            case DeviceMenu.Health.BodyComponent -> {

            }
            case DeviceMenu.Health.BodyTemperature -> {
                startActivity(new Intent(this, BodyTemperatureDetectActivity.class));
            }
            case DeviceMenu.Health.Sleep -> {

            }
            case DeviceMenu.Health.MiniCheckUp -> {

            }
            case DeviceMenu.Health.ECG -> {

            }
            case DeviceMenu.Health.HRV -> {

            }
            case DeviceMenu.Health.Step -> {

            }
            case DeviceMenu.Health.DeviceManualTestData -> {
                startActivity(new Intent(this, DeviceManualTestDataActivity.class));
            }
        }

    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

    }
}
