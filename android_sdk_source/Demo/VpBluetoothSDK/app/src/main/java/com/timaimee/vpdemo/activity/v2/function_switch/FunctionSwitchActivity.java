package com.timaimee.vpdemo.activity.v2.function_switch;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.health.MiniCheckupActivity;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.timaimee.vpdemo.activity.v2.health.BloodComponentActivity;
import com.timaimee.vpdemo.activity.v2.health.BloodOxygenActivity;
import com.timaimee.vpdemo.activity.v2.health.BodyComponentActivity;
import com.timaimee.vpdemo.activity.v2.health.BodyTemperatureActivity;
import com.timaimee.vpdemo.activity.v2.health.DeviceManualTestDataActivity;
import com.timaimee.vpdemo.activity.v2.health.FunctionTestActivity;
import com.timaimee.vpdemo.activity.v2.health.GSRDetectActivity;
import com.timaimee.vpdemo.activity.v2.health.HrvActivity;
import com.timaimee.vpdemo.activity.v2.health.StepActivity;
import com.timaimee.vpdemo.adapter.FunctionAdapter;

public class FunctionSwitchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gvFunction;
    FunctionAdapter adapter;

    @Override
    public int getLayoutID() {
        return R.layout.activity_functions;
    }

    @Override
    public String pageTitle() {
        return "开关设置";
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
        adapter = new FunctionAdapter(this, DeviceMenu.SwitchMenu);
        gvFunction.setAdapter(adapter);
        gvFunction.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String function = DeviceMenu.SwitchMenu[i];
        if (!checkFunction(function) && false) {
            return;
        }
        switch (function) {
            case DeviceMenu.Switch.MSG_PUSH -> startActivity(new Intent(this, MessagePushSwitchActivity.class));
            case DeviceMenu.Switch.CUSTOM_SETTNG -> startActivity(new Intent(this, CustomSettingSwitchActivity.class));
            case DeviceMenu.Switch.HEALTH_SUPPORT -> FunctionTestActivity.Companion.start(this, function);
            case DeviceMenu.Switch.SWITCH_STATUS_LISTENER ->  FunctionTestActivity.Companion.start(this, function);
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
        }

    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

    }
}
