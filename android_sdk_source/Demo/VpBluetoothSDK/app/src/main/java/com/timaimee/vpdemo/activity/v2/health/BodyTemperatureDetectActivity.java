package com.timaimee.vpdemo.activity.v2.health;

import android.view.View;

import com.orhanobut.logger.Logger;
import com.veepoo.protocol.listener.data.ITemptureDetectDataListener;
import com.veepoo.protocol.model.datas.TemptureDetectData;

public class BodyTemperatureDetectActivity extends BaseDetectActivity {

    private static final String TAG = "-体温测量-";

    @Override
    public String pageTitle() {
        return "体温";
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void startDetect() {
        vpBleManager.startDetectTempture(defaultResponse, new ITemptureDetectDataListener() {
            @Override
            public void onDataChange(TemptureDetectData detectData) {
                refreshUI(detectData);
            }
        });
    }

    private void refreshUI(TemptureDetectData detectData) {
        //| oprate       | Int   | 0x00 不支持此功能，0x01 开启，0x02关闭                          |
        //| ------------ | ----- | ------------------------------------------------------------ |
        //| deviceState  | Int   | 0x00 可用，0x01-0x07 设备正忙，0x08 设备低电，0x09 传感器异常     |
        //| progress     | Int   | 读取进度                                                      |
        //| tempture     | Float | 体温值                                                        |
        //| temptureBase | Float | 体温原始值,基准值                                               |
        Logger.t(TAG).e("-TemptureDetectData-: | 测量值=" + detectData);
        int opt = detectData.getOprate();
        int state = detectData.getDeviceState();
        int progress = detectData.getProgress();
        if (opt == 0) {
            tvProcessInfo.setText("暂不支持该功能");
        } else if(opt == 2) {
            tvProcessInfo.setText("测量已停止");
        } else {
            if (progress == 100) {
                tvProcessInfo.setText("测量完成");
            } else {
                tvProcessInfo.setText("测量中：" + progress + "%");
            }
        }

        if ((state == 0 || state == 7) && opt != 2) {
            appendBlueMiddleText("体温测量：体温值="+detectData.getTempture() + ", 皮肤温度="+detectData.getTemptureBase());
        } else if (state >= 1 && state < 7) {
            tvProcessInfo.setText("设备正忙");
            appendRedLargeText("异常（设备正忙）：已停止测量");
        } else if (state == 8) {
            tvProcessInfo.setText("设备低电");
            appendRedLargeText("异常（设备低电）：已停止测量");
        } else if (state == 9) {
            tvProcessInfo.setText("传感器异常");
            appendRedLargeText("异常（传感器异常）：已停止测量");
        }
    }

    @Override
    public void stopDetect() {
        vpBleManager.stopDetectTempture(defaultResponse, new ITemptureDetectDataListener() {
            @Override
            public void onDataChange(TemptureDetectData detectData) {
                refreshUI(detectData);
            }
        });
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
