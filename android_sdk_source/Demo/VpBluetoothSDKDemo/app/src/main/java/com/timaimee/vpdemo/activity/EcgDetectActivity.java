package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGDetectListener;
import com.veepoo.protocol.listener.data.ILightDataCallBack;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;

import java.util.Arrays;

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
                VPOperateManager.getMangerInstance(mContext).startDetectECG(writeResponse, true, new IECGDetectListener() {
                    @Override
                    public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
                        String message = "ecgDetectInfo-1:" + ecgDetectInfo.toString();
                        Logger.t(TAG).i(message);
                    }

                    @Override
                    public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
                        String message = "ecgDetectState-2:" + ecgDetectState.toString();
                        Logger.t(TAG).i(message);
                    }

                    @Override
                    public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
                        String message = "ecgDetectResult-3:" + ecgDetectResult.toString();
                        Logger.t(TAG).i(message);
                    }

                    @Override
                    public void onEcgADCChange(int[] ecgData) {
                        String message = "ecgDetectADC-0:" + Arrays.toString(ecgData);
                        Logger.t(TAG).i(message);
                        mEcgHeartView.changeData(ecgData, 25);
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
