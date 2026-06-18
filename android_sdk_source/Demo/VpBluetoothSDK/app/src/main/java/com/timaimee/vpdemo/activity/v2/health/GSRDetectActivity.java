package com.timaimee.vpdemo.activity.v2.health;

import android.view.View;

import com.orhanobut.logger.Logger;
import com.veepoo.protocol.listener.data.IGsrDetectListener;
import com.veepoo.protocol.listener.data.ITemptureDetectDataListener;
import com.veepoo.protocol.model.datas.GsrDetectResult;
import com.veepoo.protocol.model.datas.TemptureDetectData;
import com.veepoo.protocol.model.enums.GsrDetectAck;

import org.jetbrains.annotations.NotNull;

public class GSRDetectActivity extends BaseDetectActivity {

    private static final String TAG = "-皮电测量-";

    @Override
    public String pageTitle() {
        return "皮电";
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void startDetect() {
        vpBleManager.startDetectGsr(defaultResponse, new IGsrDetectListener() {
            @Override
            public void onGsrDetectProgress(int progress) {
                if (progress == 100) {
                    tvProcessInfo.setText("测量完成");
                } else {
                    tvProcessInfo.setText("测量中：" + progress + "%");
                }
            }

            @Override
            public void onGsrDetectSuccess(@NotNull GsrDetectResult detectResult) {
                appendBlueMiddleText("✅️:测量成功");
                appendResult(">>> 情绪:" + detectResult.getEmotionLevel());
                appendResult(">>> 皮肤含水量:" + detectResult.getSkinMoisture()+"%");
                appendResult(">>> 交感神经活跃度:" + detectResult.getSnsActivation()+"%");
                appendResult(">>> 皮质醇浓度:" + detectResult.getCortisolValue()+"ug/L");
                appendResult(">>> 抑郁症风险:" + detectResult.getDepressionRisk());
            }

            @Override
            public void onGsrDetectFailed(@NotNull GsrDetectAck detectAck) {
                appendRedLargeText("❌️:测量失败 >>> " + detectAck.getDescription());
            }

            @Override
            public void onGsrDetectStop() {
                tvProcessInfo.setText("测量已停止");
            }
        });
    }

    @Override
    public void stopDetect() {
        vpBleManager.stopDetectGsr(defaultResponse);
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
