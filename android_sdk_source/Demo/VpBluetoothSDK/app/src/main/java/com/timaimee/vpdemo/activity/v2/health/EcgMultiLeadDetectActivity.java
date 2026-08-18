package com.timaimee.vpdemo.activity.v2.health;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.EcgDetectView;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.timaimee.vpdemo.activity.v2.DeviceMenu;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.multi_lead.data.EcgMultiLeadDetectResult;
import com.veepoo.protocol.multi_lead.data.EcgMultiLeadDetectState;
import com.veepoo.protocol.multi_lead.data.EcgMultiLeadPreInfo;
import com.veepoo.protocol.multi_lead.enums.ELeadFlag;
import com.veepoo.protocol.multi_lead.listener.IECGMultiLeadDetectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Description ECG多导测量页面
 *
 * @author KYM.
 */
public class EcgMultiLeadDetectActivity extends BaseVPBLETestActivity implements View.OnClickListener {
    private final List<EcgDetectView> viewList = new ArrayList<>();
    TextView start, stop, tvProgress, tvInfo;
    private boolean isDetecting = false;

    /**
     * 测量秒数
     */
    private int detectSeconds = 0;

    /**
     * 连续导联脱落次数
     */
    private int leadOffCount = 0;

    @Override
    public int getLayoutID() {
        return R.layout.activity_ecg_multi_lead_detect;
    }

    @Override
    public String pageTitle() {
        return DeviceMenu.Health.ECG_MULTI;
    }

    @Override
    public void initView() {
        EcgDetectView v1 = findViewById(R.id.ehrv1);
        EcgDetectView v2 = findViewById(R.id.ehrv2);
        EcgDetectView v3 = findViewById(R.id.ehrv3);
        EcgDetectView v4 = findViewById(R.id.ehrv4);
        EcgDetectView v5 = findViewById(R.id.ehrv5);
        EcgDetectView v6 = findViewById(R.id.ehrv6);
        viewList.add(v1);
        viewList.add(v2);
        viewList.add(v3);
        viewList.add(v4);
        viewList.add(v5);
        viewList.add(v6);
        start = findViewById(R.id.btnStart);
        stop = findViewById(R.id.btnStop);
        tvProgress = findViewById(R.id.tvProgress);
        tvInfo = findViewById(R.id.tvInfo);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnStart) {
            if (!fCheck.checkEcgMultiLead()) {
                showToast("当前功能不支持ECG多导联");
                return;
            }
            if (isDetecting) {
                return;
            }
            for (int i = 0; i < viewList.size(); i++) {
                viewList.get(i).clearData();
            }
            isDetecting = true;
            leadOffCount = 0;
            vpBleManager.startMultiLeadDetectECG(defaultResponse, true, new IECGMultiLeadDetectListener() {

                @Override
                public void onEcgDetectSuccess() {
                    isDetecting = false;
                    Log.e("Test", "onEcgDetectSuccess");
                    Toast.makeText(EcgMultiLeadDetectActivity.this, "测量成功", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onEcgDetectPreInfoChange(@NonNull EcgMultiLeadPreInfo ecgDetectInfo) {
                    Log.e("Test", "onEcgDetectPreInfoChange:" + ecgDetectInfo.toString());
                    if (isDetecting) {
                        tvInfo.setText("正在测量中...");
                    }
                    detectSeconds = 0;
                }

                @Override
                public void onEcgDetectStateChange(@NonNull EcgMultiLeadDetectState ecgDetectState) {
                    Log.e("Test", "onEcgDetectStateChange:" + ecgDetectState.toString());
                    tvProgress.setText(ecgDetectState.getProgress() + "%");
                    if (isDetecting) {
                        tvInfo.setText("正在测量中...\n心率:" + ecgDetectState.getHeart() + "   QT:" + ecgDetectState.getQt() + "   HRV:" + ecgDetectState.getHrv());
                    }
                    detectSeconds++;

                    //前4秒不管脱落问题
                    if (detectSeconds > 4) {
                        //始测量4秒后，如果I导联不脱落继续测量；I导联脱落全部脱落;
                        if (ecgDetectState.getLeadI() == 1) {
                            tvInfo.setText("导联脱落");
                            leadOffCount++;
                            //4秒后如果脱落次数连续超过4次，则判断脱落
                            if (leadOffCount > 4) {
                                isDetecting = false;
                                VPOperateManager.getInstance().stopMultiLeadDetectECG(defaultResponse);
                                tvInfo.setText("导联脱落，测量结束");
                            }
                        } else {
                            leadOffCount = 0;
                        }

                    }
                }

                @Override
                public void onDiseaseDiagnosisResults(@NonNull EcgMultiLeadDetectResult ecgDetectResult) {
                    Log.e("Test", "onEcgDetectResultChange:" + ecgDetectResult.toString());
                    tvInfo.setText("疾病诊断结果!\n平均心率:" + ecgDetectResult.getAvgHeart() + "   平均QT:" + ecgDetectResult.getAvgQT() + "   平均HRV:" + ecgDetectResult.getAvgHRV());
                }

                @Override
                public void onEcgDetectFail() {
                    isDetecting = false;
                    Log.e("Test", "onEcgDetectFail");
                    tvInfo.setText("测量失败");
                }

                @Override
                public void onEcgADCChange(@NonNull ELeadFlag leadFlag, @NonNull int[] data, int gain, int packNum) {
                    viewList.get(leadFlag.getValue() - 1).changeData(data, data.length, gain);
                }


            });
        } else if(id == R.id.btnStop) {
            if (!fCheck.checkEcgMultiLead()) {
                showToast("当前功能不支持ECG多导联");
                return;
            }
            for (int i = 0; i < viewList.size(); i++) {
                viewList.get(i).clearData();
            }
            isDetecting = false;
            vpBleManager.stopMultiLeadDetectECG(defaultResponse);
        }
    }

}
