package com.timaimee.vpdemo.activity.v2.custom;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.veepoo.protocol.listener.data.IYM23ProTestStatusListener;
import com.veepoo.protocol.listener.data.IYM23ProWearReportListener;
import com.veepoo.protocol.model.datas.YM23ProWearReportData;
import com.veepoo.protocol.model.enums.EYM23ProOptStatus;
import com.veepoo.protocol.model.enums.EYM23ProTestStatus;

/**
 * YM23PRO定制功能-佩戴状态上报与测试状态测试页
 * (定制功能，需要设备支持)
 * <p>
 * 交互闭环说明：
 * 1.测试状态只有在佩戴状态上报开启成功后才允许下发，因此测试状态按钮区默认隐藏，
 * 开启成功后显示，关闭成功后再次隐藏；
 * 2.页面退出时若佩戴状态上报仍处于开启状态，自动下发关闭指令，保证状态闭环。
 */
public class YM23ProTestActivity extends BaseActivity {

    /**
     * 佩戴状态上报是否已开启成功(本地标记，用于按钮显隐与页面退出时自动关闭)
     */
    private boolean isWearReportOpened = false;

    private TextView tvTestStatusTitle;
    private LinearLayout llTestStatusButtons;
    private TextView tvWearReportState;

    @Override
    public int getLayoutID() {
        return R.layout.activity_ym23_pro_test;
    }

    @Override
    public String pageTitle() {
        return "YM23PRO佩戴上报与测试状态";
    }

    @Override
    public void initView() {
        tvWearReportState = findViewById(R.id.tvWearReportState);
        tvTestStatusTitle = findViewById(R.id.tvTestStatusTitle);
        llTestStatusButtons = findViewById(R.id.llTestStatusButtons);
        // 佩戴状态上报默认未开启，测试状态按钮区默认隐藏
        updateWearReportStateUI();
        updateTestStatusUI(false);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
        findViewById(R.id.btnOpen).setOnClickListener(v ->
                vpBleManager.openYM23ProWearReport(mWearReportListener, code -> {
                }));
        findViewById(R.id.btnClose).setOnClickListener(v ->
                vpBleManager.closeYM23ProWearReport(mWearReportListener, code -> {
                }));
        findViewById(R.id.btnTesting).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.TESTING));
        findViewById(R.id.btnRepairing).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.REPAIRING));
        findViewById(R.id.btnTestDone).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.TEST_DONE));
        findViewById(R.id.btnRepairDone).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.REPAIR_DONE));
        findViewById(R.id.btnTestPause).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.TEST_PAUSE));
        findViewById(R.id.btnRepairPause).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.REPAIR_PAUSE));
        findViewById(R.id.btnIdle).setOnClickListener(v -> sendTestStatus(EYM23ProTestStatus.IDLE));
    }

    private void sendTestStatus(EYM23ProTestStatus testStatus) {
        vpBleManager.setYM23ProTestStatus(testStatus, mTestStatusListener, code -> {
        });
    }

    /**
     * 佩戴状态上报监听：开关应答 + 主动上报
     * (协议2.3：开关应答仅回ACK；佩戴/心率数据通过主动上报推送)
     */
    private final IYM23ProWearReportListener mWearReportListener = new IYM23ProWearReportListener() {
        @Override
        public void onYM23ProWearReportOpenResult(EYM23ProOptStatus status) {
            if (status == EYM23ProOptStatus.SUCCESS) {
                isWearReportOpened = true;
                updateWearReportStateUI();
                updateTestStatusUI(true);
                appendResult("✅️开启佩戴状态上报成功，等待设备上报佩戴与心率数据");
            } else {
                appendRedLargeText("❌️开启佩戴状态上报失败：" + status.getDes());
            }
        }

        @Override
        public void onYM23ProWearReportCloseResult(EYM23ProOptStatus status) {
            if (status == EYM23ProOptStatus.SUCCESS) {
                isWearReportOpened = false;
                updateWearReportStateUI();
                updateTestStatusUI(false);
                appendResult("✅️关闭佩戴状态上报成功");
            } else {
                appendRedLargeText("❌️关闭佩戴状态上报失败：" + status.getDes());
            }
        }

        @Override
        public void onYM23ProWearReport(YM23ProWearReportData data) {
            appendResult("📩佩戴状态上报：" + data.toString());
        }
    };

    /**
     * 测试状态下发监听
     */
    private final IYM23ProTestStatusListener mTestStatusListener = this::onTestStatusResult;

    private void onTestStatusResult(EYM23ProOptStatus status, EYM23ProTestStatus testStatus) {
        if (status == EYM23ProOptStatus.SUCCESS) {
            if (testStatus == EYM23ProTestStatus.IDLE) {
                // 下发空闲即退出测试界面，相当于关闭佩戴状态上报，同步更新状态并隐藏按钮区
                isWearReportOpened = false;
                updateWearReportStateUI();
                updateTestStatusUI(false);
                appendResult("✅️下发空闲成功，已退出测试界面，佩戴状态上报已关闭");
            } else {
                appendResult("✅️测试状态下发成功，设备当前状态：" + testStatus.getDes());
            }
        } else {
            appendRedLargeText("❌️测试状态下发失败：" + status.getDes() + "，设备状态：" + testStatus.getDes());
        }
    }

    private void updateTestStatusUI(boolean opened) {
        if (tvTestStatusTitle != null) {
            tvTestStatusTitle.setVisibility(opened ? View.VISIBLE : View.GONE);
        }
        if (llTestStatusButtons != null) {
            llTestStatusButtons.setVisibility(opened ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 实时刷新佩戴状态上报的开关状态显示(按钮区上方)
     */
    private void updateWearReportStateUI() {
        if (tvWearReportState == null) return;
        if (isWearReportOpened) {
            tvWearReportState.setText("佩戴状态上报：已开启");
            tvWearReportState.setTextColor(Color.parseColor("#12A30F"));
        } else {
            tvWearReportState.setText("佩戴状态上报：未开启");
            tvWearReportState.setTextColor(Color.parseColor("#666666"));
        }
    }

    @Override
    protected void onDestroy() {
        // 页面退出时若佩戴状态上报仍开启，自动下发关闭，保证状态闭环
        if (isWearReportOpened) {
            isWearReportOpened = false;
            vpBleManager.closeYM23ProWearReport(new IYM23ProWearReportListener() {
                @Override
                public void onYM23ProWearReportOpenResult(EYM23ProOptStatus status) {
                }

                @Override
                public void onYM23ProWearReportCloseResult(EYM23ProOptStatus status) {
                }

                @Override
                public void onYM23ProWearReport(YM23ProWearReportData data) {
                }
            }, code -> {
            });
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean hasCommonMsgUI() {
        return true;
    }
}
