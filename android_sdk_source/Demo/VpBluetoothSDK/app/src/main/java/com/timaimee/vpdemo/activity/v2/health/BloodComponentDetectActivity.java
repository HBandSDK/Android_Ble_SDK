package com.timaimee.vpdemo.activity.v2.health;

import android.view.View;

import com.veepoo.protocol.listener.data.IBloodComponentDetectListener;
import com.veepoo.protocol.model.datas.BloodComponent;
import com.veepoo.protocol.model.enums.EBloodComponentDetectState;

import org.jetbrains.annotations.NotNull;

public class BloodComponentDetectActivity extends BaseDetectActivity implements IBloodComponentDetectListener {

    private static final String TAG = "-血液成分测量-";

    @Override
    public String pageTitle() {
        return "血液成分测量";
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void startDetect() {
        vpBleManager.startDetectBloodComponent(defaultResponse,  false, this);
    }

    @Override
    public void stopDetect() {
        vpBleManager.stopDetectBloodComponent(defaultResponse);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCMDWriteSuccess() {
        super.onCMDWriteSuccess();
        if (isClickStop) {
            appendRedLargeText("测量停止");
            tvProcessInfo.setText("检测已停止");
        }
    }

    @Override
    public void onDetectFailed(@NotNull EBloodComponentDetectState errorState) {
        tvProcessInfo.setText("测量失败：" + errorState.getDes());
        switch (errorState) {
            case ENABLE -> {
            }
            case DETECTING -> {
            }
            case LOW_POWER -> {
            }
            case BUSY -> {
            }
            case WEAR_ERROR -> {
            }
            case UNKNOWN -> {
            }
        }
    }

    @Override
    public void onDetecting(int progress, @NotNull BloodComponent bloodComponent) {
        appendBlueMiddleText("测量中：" + bloodComponent);
        tvProcessInfo.setText("测量中："+progress+"%");
    }

    @Override
    public void onDetectStop() {
        tvProcessInfo.setText("停止测量");
    }

    @Override
    public void onDetectComplete(@NotNull BloodComponent bloodComponent) {
        appendOrangeText("测量完成："+bloodComponent.toString());
        tvProcessInfo.setText("停止完成");
    }
}
