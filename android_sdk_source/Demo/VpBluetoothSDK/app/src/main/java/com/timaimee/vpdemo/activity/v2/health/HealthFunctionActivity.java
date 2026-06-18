package com.timaimee.vpdemo.activity.v2.health;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.HealthRemindActivity;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.timaimee.vpdemo.adapter.FunctionAdapter;

public class HealthFunctionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gvFunction;
    FunctionAdapter adapter;

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
        adapter = new FunctionAdapter(this, DeviceMenu.HealthMenu);
        gvFunction.setAdapter(adapter);
        gvFunction.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String function = DeviceMenu.HealthMenu[i];
        if (!checkFunction(function) && false) {
            return;
        }
        switch (function) {
            case DeviceMenu.Health.HealthData -> startActivity(new Intent(this, HealthDataReadOptActivity.class));
            case DeviceMenu.Health.HeartRate -> startActivity(new Intent(this, HeartRateActivity.class));
            case DeviceMenu.Health.BloodPress -> startActivity(new Intent(this, BloodPressActivity.class));
            case DeviceMenu.Health.BloodGlucose -> startActivity(new Intent(this, BloodGlucoseActivity.class));
            case DeviceMenu.Health.BloodOxygen -> startActivity(new Intent(this, BloodOxygenActivity.class));
            case DeviceMenu.Health.BloodComponent -> startActivity(new Intent(this, BloodComponentActivity.class));
            case DeviceMenu.Health.BodyComponent -> startActivity(new Intent(this, BodyComponentActivity.class));
            case DeviceMenu.Health.BodyTemperature -> startActivity(new Intent(this, BodyTemperatureActivity.class));
            case DeviceMenu.Health.Sleep -> {

            }
            case DeviceMenu.Health.MiniCheckUp -> startActivity(new Intent(this, MiniCheckupActivity.class));
            case DeviceMenu.Health.GSR -> startActivity(new Intent(this, GSRDetectActivity.class));
            case DeviceMenu.Health.ECG -> {

            }
            case DeviceMenu.Health.HRV -> startActivity(new Intent(this, HrvActivity.class));
            case DeviceMenu.Health.Step -> startActivity(new Intent(this, StepActivity.class));
            case DeviceMenu.Health.Fatigue -> FunctionTestActivity.Companion.start(this, function);
            case DeviceMenu.Health.DeviceManualTestData -> startActivity(new Intent(this, DeviceManualTestDataActivity.class));
            case DeviceMenu.Health.SEDENTARY_REMIND ->   startActivity(new Intent(this, SedentaryRemindActivity.class));
            case DeviceMenu.Health.HEALTH_REMIND ->   startActivity(new Intent(this, HealthRemindActivity.class));
            case DeviceMenu.Health.REMIND_EVENT ->   startActivity(new Intent(this, RemindEventActivity.class));
            case DeviceMenu.Health.AUTO_MEASURE ->   startActivity(new Intent(this, AutoMeasureActivity.class));

        }

    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

    }
}
