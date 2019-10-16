package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IPttDetectListener;
import com.veepoo.protocol.model.datas.EcgDetectInfo;
import com.veepoo.protocol.model.datas.EcgDetectResult;
import com.veepoo.protocol.model.datas.EcgDetectState;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PttActivity extends Activity {
    private final static String TAG = PttActivity.class.getSimpleName();
    WriteResponse writeResponse = new WriteResponse();
    @BindView(R.id.ptt_model)
    TextView mPttModelTv;
    @BindView(R.id.ptt_real_view)
    EcgHeartRealthView ecgHeartRealthView;

    IPttDetectListener iPttDetectListener = new IPttDetectListener() {
        @Override
        public void onEcgDetectInfoChange(EcgDetectInfo ecgDetectInfo) {
            Logger.t(TAG).i("ECG测量基本信息(波形频率,采样频率):" + ecgDetectInfo.toString());


        }

        @Override
        public void onEcgDetectStateChange(EcgDetectState ecgDetectState) {
            Logger.t(TAG).i("ECG测量过程中的状态,设置顶部文本:" + ecgDetectState.toString());


        }

        @Override
        public void onEcgDetectResultChange(EcgDetectResult ecgDetectResult) {
            Logger.t(TAG).i("ptt出值包(ECG测量的最终结果,在PTT模式下，只是异常时（即存在疾病）,才会出值)");


        }

        @Override
        public void onEcgADCChange(final int[] data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Logger.t(TAG).i("PTT的波形数据:" + Arrays.toString(data));
                    ecgHeartRealthView.changeData(data, 25);
                }
            });

        }

        @Override
        public void inPttModel() {
            Logger.t(TAG).i("进入ptt模式");
            mPttModelTv.setText("手表显示在PTT模式内");
        }

        @Override
        public void outPttModel() {
            Logger.t(TAG).i("退出ptt模式");
            mPttModelTv.setText("手表显示退出PTT模式");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptt);
        ButterKnife.bind(this);
        boolean inPttModel = getIntent().getBooleanExtra("inPttModel", false);
        String ptStr = inPttModel ? "手表显示在PTT模式内" : "手表显示退出PTT模式";
        mPttModelTv.setText(ptStr);
        listenModel();
    }

    private void listenModel() {
        VPOperateManager.getMangerInstance(getApplicationContext()).settingPttModelListener(iPttDetectListener);
    }

    @OnClick(R.id.ptt_sign_open)
    public void enter() {
        ecgHeartRealthView.clearData();
        Logger.t(TAG).i("读取ptt信号");
        VPOperateManager.getMangerInstance(getApplicationContext()).startReadPttSignData(writeResponse, true, iPttDetectListener);
    }

    @OnClick(R.id.ptt_sign_close)
    public void exitModel() {
        Logger.t(TAG).i("关闭ptt信号");
        VPOperateManager.getMangerInstance(getApplicationContext()).stopReadPttSignData(writeResponse, false, iPttDetectListener);
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
