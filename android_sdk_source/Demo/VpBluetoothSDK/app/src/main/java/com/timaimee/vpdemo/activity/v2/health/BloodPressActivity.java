package com.timaimee.vpdemo.activity.v2.health;

import android.view.View;

public class BloodPressActivity extends BaseDetectActivity {

    @Override
    public String pageTitle() {
        return "血压";
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void startDetect() {
        vpBleManager.startDetectHeart(defaultResponse, heartData -> {
            switch (heartData.getHeartStatus()) {
                case STATE_INIT -> {
                    tvProcessInfo.setText("设备初始化");
                }
                case STATE_HEART_BUSY -> {
                    tvProcessInfo.setText("设备正忙");
                }
                case STATE_HEART_DETECT -> {
                    tvProcessInfo.setText("设备正在检测...");
                }
                case STATE_HEART_WEAR_ERROR -> {
                    tvProcessInfo.setText("佩戴不通过");
                }
                case STATE_LOW_BATTERY -> {
                    tvProcessInfo.setText("电池电量过低");
                }
                case STATE_HEART_NORMAL -> {
                    tvProcessInfo.setText("心率正在检测...");
                }
            }
            appendBlueMiddleText("心率值：" + heartData.getData());
        });
    }

    @Override
    public void stopDetect() {
        vpBleManager.stopDetectHeart(defaultResponse);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCMDWriteSuccess() {
        super.onCMDWriteSuccess();
        if (isClickStop) {
            tvProcessInfo.setText("检测已停止");
        }
    }
}
