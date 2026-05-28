package com.timaimee.vpdemo.activity.v2.custom;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.TCMActivity;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.timaimee.vpdemo.adapter.HealthFunctionAdapter;

public class CustomFunctionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    GridView gvFunction;
    HealthFunctionAdapter adapter;

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
        adapter = new HealthFunctionAdapter(this, DeviceMenu.CustomMenu);
        gvFunction.setAdapter(adapter);
        gvFunction.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String function = DeviceMenu.CustomMenu[i];
        showMsg(function);
        switch (function) {
            case DeviceMenu.Custom.TCM -> {
                startActivity(new Intent(this, TCMActivity.class));
            }
            case DeviceMenu.Custom.PSAIM -> {
                startActivity(new Intent(this, PSAIMHealthDataActivity.class));
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
