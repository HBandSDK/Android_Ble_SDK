package com.timaimee.vpdemo.activity.v2.custom;

import android.text.TextUtils;
import android.widget.EditText;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.veepoo.protocol.listener.data.IECGSingleLeadTestDurationListener;

/**
 * JH151定制功能-ECG单导测量时长测试页
 * (定制功能，需要设备支持，内部实现走JP18协议E9 09/0A)
 */
public class JH151ECGSingleLeadDurationActivity extends BaseActivity {

    /**
     * 默认时长30s
     */
    private static final int DEFAULT_DURATION = 30;

    EditText etDuration;

    @Override
    public int getLayoutID() {
        return R.layout.activity_jh151_ecg_single_lead_duration;
    }

    @Override
    public String pageTitle() {
        return "JH151 ECG单导测量时长";
    }

    @Override
    public void initView() {
        etDuration = findViewById(R.id.etDuration);
        etDuration.setText(String.valueOf(DEFAULT_DURATION));
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
        findViewById(R.id.btnRead).setOnClickListener(v ->
                vpBleManager.readJH151ECGSingleLeadTestDuration(mDurationListener, code -> {
                }));
        findViewById(R.id.btnSet).setOnClickListener(v ->
                vpBleManager.setJH151ECGSingleLeadTestDuration(getInputDuration(), mDurationListener, code -> {
                }));
    }

    private int getInputDuration() {
        if (etDuration == null) {
            return DEFAULT_DURATION;
        }
        String input = etDuration.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            return DEFAULT_DURATION;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return DEFAULT_DURATION;
        }
    }

    /**
     * 设置/读取单导测量时长监听
     */
    private final IECGSingleLeadTestDurationListener mDurationListener = this::onDurationResult;

    private void onDurationResult(boolean success, int duration) {
        if (success) {
            appendResult("✅️操作成功，当前单导测量时长=" + duration + "s");
        } else {
            appendRedLargeText("❌️操作失败，时长=" + duration + "s");
        }
    }

    @Override
    public void onClick(android.view.View view) {
    }

    @Override
    public boolean hasCommonMsgUI() {
        return true;
    }
}
