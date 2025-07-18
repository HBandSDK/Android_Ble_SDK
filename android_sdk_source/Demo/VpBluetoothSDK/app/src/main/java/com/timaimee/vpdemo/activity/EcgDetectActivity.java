package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGDetectListener;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;
import com.veepoo.protocol.model.datas.EcgDiagnosis;
import com.veepoo.protocol.shareprence.VpSpGetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EcgDetectActivity extends Activity implements View.OnClickListener {
    private final static String TAG = EcgDetectActivity.class.getSimpleName();
    EcgHeartRealthView mEcgHeartView;
    Button start, stop;
    Button notify;
    Context mContext;
    WriteResponse writeResponse = new WriteResponse();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecgdetect);
        mContext = EcgDetectActivity.this;
        mEcgHeartView = (EcgHeartRealthView) findViewById(R.id.ecg_real_view);
        notify = (Button) findViewById(R.id.greenlightdata);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        notify.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start:
                VPOperateManager.getInstance().startDetectECG(writeResponse, true, new IECGDetectListener() {
                    @Override
                    public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                        int ecgType = VpSpGetUtil.getVpSpVariInstance(mContext).getECGType();
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

//                    @Override
//                    public void onEcgADCChange(int[] ecgData) {
//                        String message = "-onEcgADCChange-:" + Arrays.toString(ecgData);
//                        Logger.t(TAG).i(message);
////                        mEcgHeartView.changeData(ecgData, 20);
//                    }

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
                                if (powerList.size() > 0 && i < powerList.size()) {
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
                break;
            case R.id.stop:
                mEcgHeartView.clearData();
                VPOperateManager.getMangerInstance(mContext).stopDetectECG(writeResponse, true, null);
                break;
        }
    }

    /**
     * 写入的状态返回
     */
    class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("write cmd status:" + code);

        }
    }
}
