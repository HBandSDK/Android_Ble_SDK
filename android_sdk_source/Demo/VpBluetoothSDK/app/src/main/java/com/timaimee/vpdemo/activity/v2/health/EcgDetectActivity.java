package com.timaimee.vpdemo.activity.v2.health;

import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.EcgHeartRealthView;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.veepoo.protocol.listener.data.IECGDetectListener;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;
import com.veepoo.protocol.model.datas.EcgDiagnosis;
import com.veepoo.protocol.shareprence.VpSpGetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EcgDetectActivity extends BaseVPBLETestActivity {
    private final static String TAG = EcgDetectActivity.class.getSimpleName();
    EcgHeartRealthView mEcgHeartView;
    Button btnStart, btnStop;
    Button notify;

    @Override
    public int getLayoutID() {
        return R.layout.activity_ecgdetect;
    }

    @Override
    public String pageTitle() {
        return "ECG测量";
    }

    @Override
    public void initView() {
        mEcgHeartView = findViewById(R.id.ecg_real_view);
        mEcgHeartView.setEcgType(VpSpGetUtil.getVpSpVariInstance(this.getApplicationContext()).getECGType());
        notify = findViewById(R.id.greenlightdata);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        notify.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnStart) {
            if (!fCheck.checkECG()) {
                showToast("当前设备不支持ECG功能");
               return;
            }
            vpBleManager.startDetectECG(defaultResponse, true, new IECGDetectListener() {
                @Override
                public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                    int ecgType = VpSpGetUtil.getVpSpVariInstance(EcgDetectActivity.this).getECGType();
                    String message = "-onEcgDetectInfoChange-:" + ecgDetectInfo.toString()+",ecgType="+ecgType;
                    mEcgHeartView.setEcgType(ecgType);
                    mEcgHeartView.setDrawHz(ecgDetectInfo.getFrequency());
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                    String message = "-onEcgDetectStateChange-:" + ecgDetectState.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
                    String message = "-onEcgDetectResultChange-:" + ecgDetectResult.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onEcgDetectDiagnosisChange(EcgDiagnosis ecgDiagnosis) {
                    Logger.t(TAG).i("ecg多诊断 :: = " + ecgDiagnosis.toString());
                }

                @Override
                public void onEcgADCChange(int[] ecgData, int[] powerData) {
                    String message = "-onEcgADCChange-:" + Arrays.toString(ecgData);
                    Logger.t(TAG).i(message);
                    List<Integer> filterList = new ArrayList<>();
                    List<Integer> powerList = new ArrayList<>();
                    for (int i = 0; i < ecgData.length; i++) {
                        if (ecgData[i] != Integer.MAX_VALUE) {
                            filterList.add(ecgData[i]);
                            if (!powerList.isEmpty() && i < powerList.size()) {
                                powerList.add(powerData[i]);
                            } else {
                                powerList.add(20);
                            }
                        }
                    }
                    int[] filterAdc = new int[filterList.size()];
                    int[] filterPower = new int[powerList.size()];
                    for (int i = 0; i < filterList.size(); i++) {
                        filterAdc[i] = filterList.get(i);
                    }
                    mEcgHeartView.changeData(filterAdc, filterPower,filterAdc.length);
                }
            });
        } else if(id == R.id.btnStop) {
            if (!fCheck.checkECG()) {
                showToast("当前设备不支持ECG功能");
                return;
            }
            mEcgHeartView.clearData();
            vpBleManager.stopDetectECG(defaultResponse, true, null);
        }
    }
}
