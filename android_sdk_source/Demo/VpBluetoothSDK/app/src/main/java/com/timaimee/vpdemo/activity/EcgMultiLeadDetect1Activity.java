package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
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
 * @date 2023/11/2 15:55
 */
public class EcgMultiLeadDetect1Activity extends Activity implements View.OnClickListener {
    private final static String TAG = EcgMultiLeadDetect1Activity.class.getSimpleName();
    private List<EcgDetectView> viewList = new ArrayList<>();
    TextView start, stop, tvProgress, tvInfo;
    Context mContext;
    WriteResponse writeResponse = new WriteResponse();
    private boolean isDetecting = false;

    private int allLeadOffCount = 0;

    /**
     * 测量秒数
     */
    private int detectSeconds = 0;

    /**
     * 连续导联脱落次数
     */
    private int leadOffCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_multi_lead_detect);
        mContext = EcgMultiLeadDetect1Activity.this;
        EcgDetectView v1 = (EcgDetectView) findViewById(R.id.ehrv1);
        EcgDetectView v2 = (EcgDetectView) findViewById(R.id.ehrv2);
        EcgDetectView v3 = (EcgDetectView) findViewById(R.id.ehrv3);
        EcgDetectView v4 = (EcgDetectView) findViewById(R.id.ehrv4);
        EcgDetectView v5 = (EcgDetectView) findViewById(R.id.ehrv5);
        EcgDetectView v6 = (EcgDetectView) findViewById(R.id.ehrv6);
        viewList.add(v1);
        viewList.add(v2);
        viewList.add(v3);
        viewList.add(v4);
        viewList.add(v5);
        viewList.add(v6);
        start = (TextView) findViewById(R.id.start);
        stop = (TextView) findViewById(R.id.stop);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start:
                if (isDetecting) {
                    return;
                }
                for (int i = 0; i < viewList.size(); i++) {
                    viewList.get(i).clearData();
                }
                isDetecting = true;
                leadOffCount = 0;
                VPOperateManager.getInstance().startMultiLeadDetectECG(writeResponse, true, new IECGMultiLeadDetectListener() {


                    @Override
                    public void onEcgDetectSuccess() {
                        isDetecting = false;
                        Log.e("Test", "onEcgDetectSuccess");
                        Toast.makeText(EcgMultiLeadDetect1Activity.this, "测量成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onEcgDetectPreInfoChange(EcgMultiLeadPreInfo ecgDetectInfo) {
                        Log.e("Test", "onEcgDetectPreInfoChange:" + ecgDetectInfo.toString());
                        if (isDetecting) {
                            tvInfo.setText("正在测量中...");
                        }
                        detectSeconds = 0;
                    }

                    @Override
                    public void onEcgDetectStateChange(EcgMultiLeadDetectState ecgDetectState) {
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
                                    VPOperateManager.getInstance().stopMultiLeadDetectECG(writeResponse);
                                    tvInfo.setText("导联脱落，测量结束");
                                }
                            } else {
                                leadOffCount = 0;
                            }

                        }
                    }

                    @Override
                    public void onDiseaseDiagnosisResults(EcgMultiLeadDetectResult ecgDetectResult) {
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
                    public void onEcgADCChange(ELeadFlag eLeadFlag, int[] data, int packNum) {
                        //Log.e("Test", "onEcgADCChange："+eLeadFlag+"-->" + Arrays.toString(data));
                        viewList.get(eLeadFlag.getValue() - 1).changeData(data, data.length);
                    }
                });
                break;
            case R.id.stop:
                for (int i = 0; i < viewList.size(); i++) {
                    viewList.get(i).clearData();
                }
                isDetecting = false;
                VPOperateManager.getInstance().stopMultiLeadDetectECG(writeResponse);
                break;
            default:
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
