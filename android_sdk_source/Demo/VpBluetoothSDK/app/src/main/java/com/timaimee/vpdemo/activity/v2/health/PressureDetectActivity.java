package com.timaimee.vpdemo.activity.v2.health;

import android.view.View;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.veepoo.protocol.listener.data.IPressureDetectListener;
import com.veepoo.protocol.model.enums.PressureDetectState;

import org.jetbrains.annotations.NotNull;

/**
 * 压力测量示例（对应 SDK 文档 1.4.0 压力功能）
 * 前提：设备支持压力测量（密码验证返回功能标志 / FunctionCheckUtil.checkStress()）
 */
public class PressureDetectActivity extends BaseDetectActivity {

    private static final String TAG = "-压力测量-";

    @Override
    public String pageTitle() {
        return DeviceMenu.Health.PRESSURE;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void startDetect() {
        vpBleManager.startDetectPressure(defaultResponse, new IPressureDetectListener() {
            @Override
            public void onDetecting(int progress) {
                if (progress == 100) {
                    tvProcessInfo.setText("测量完成");
                } else {
                    tvProcessInfo.setText("测量中：" + progress + "%");
                }
            }

            @Override
            public void onDetectSuccess(int pressure) {
                appendBlueMiddleText("✅️:测量成功");
                appendResult(">>> 压力值:" + pressure + " [0,100]");
            }

            @Override
            public void onDetectFailed(@NotNull PressureDetectState detectState) {
                appendRedLargeText("❌️:测量失败 >>> " + detectState.getDes());
            }

            @Override
            public void onDetectStop() {
                tvProcessInfo.setText("测量已停止");
            }
        });
    }

    @Override
    public void stopDetect() {
        vpBleManager.stopDetectPressure(defaultResponse);
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
