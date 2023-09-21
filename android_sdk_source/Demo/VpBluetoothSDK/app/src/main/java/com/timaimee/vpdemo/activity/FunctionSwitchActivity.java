package com.timaimee.vpdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IDeviceFunctionStatusChangeListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.util.VPLogger;

import org.jetbrains.annotations.NotNull;

public class FunctionSwitchActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_swtich);
        tv = findViewById(R.id.tv);
        if (OperaterActivity.des != null) {
            tv.setText(OperaterActivity.des + "::::" + (OperaterActivity.currentState == EFunctionStatus.SUPPORT_OPEN ? "开" : "关"));
        }
        VPOperateManager.getInstance().setDeviceFunctionStatusChangeListener(new IDeviceFunctionStatusChangeListener() {
            @Override
            public void onFunctionStatusChanged(@NotNull DeviceFunction function, @NotNull EFunctionStatus status) {
                VPLogger.e("未设置设备功能状态改变监听：" + function.getDes() + " - " + status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OperaterActivity.currentState = status;
                        OperaterActivity.des = function.getDes();
                        tv.setText(OperaterActivity.des + "::::" + (OperaterActivity.currentState == EFunctionStatus.SUPPORT_OPEN ? "开" : "关"));
                    }
                });
            }
        });
    }
}
