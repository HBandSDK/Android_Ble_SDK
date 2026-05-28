package com.timaimee.vpdemo.activity.v2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.JLDeviceOPTActivity;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.nordic.OnMcuMgrOtaListener;
import com.veepoo.protocol.zk.IZkOtaUpgradeListener;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

import io.runtime.mcumgr.dfu.FirmwareUpgradeController;
import io.runtime.mcumgr.dfu.mcuboot.FirmwareUpgradeManager;
import io.runtime.mcumgr.exception.McuMgrException;

public class OTAActivity extends BaseActivity {
    private static final String TAG = "-固件升级-";
    TextView tvOTAFilePath, tvDeviceType, tvUpgradeInfo;
    TextView btnStartNRFOta, btnSelectFirmwareFile;

    String firmwareFilePath = "";

    private int lastBytesSent = 0;
    private long lastTimestamp = 0;
    static final int FILE_PICKER_REQUEST_CODE = 1115;

    @Override
    public int getLayoutID() {
        return R.layout.activity_ota;
    }

    @Override
    public String pageTitle() {
        return "固件升级";
    }

    @Override
    public void initView() {
        tvDeviceType = findViewById(R.id.tvDeviceType);
        tvOTAFilePath = findViewById(R.id.tvOTAFilePath);
        tvUpgradeInfo = findViewById(R.id.tvUpgradeInfo);
        btnStartNRFOta = findViewById(R.id.btnStartNRFOta);
        btnSelectFirmwareFile = findViewById(R.id.btnSelectFirmwareFile);
    }

    @Override
    public void initData() {
        initDeviceType();
    }

    @Override
    public void initEvent() {
        btnStartNRFOta.setOnClickListener(view -> {
            if (TextUtils.isEmpty(firmwareFilePath)) {
                Toast.makeText(this, "请选择升级固件", Toast.LENGTH_SHORT).show();
                return;
            }
            File fileFirmware = new File(firmwareFilePath);
            if (!fileFirmware.exists() || !fileFirmware.isFile() || fileFirmware.length() <=10) {
                Toast.makeText(this, "升级固件有问题请确认无误", Toast.LENGTH_SHORT).show();
                return;
            }

            if (VPOperateManager.getInstance().isNordicDevice()) { //Nordic
                otaUpgradeForNordic();
            } else if (VPOperateManager.getInstance().isBluetrumDevice()) { //中科
                otaUpgradeForBluetrum();
            } else if (VPOperateManager.getInstance().isJLDevice()) { //杰理
                otaUpgradeForJieLi();
            }
        });

        btnSelectFirmwareFile.setOnClickListener(view -> {
            File externalStorage = Environment.getExternalStorageDirectory();
            new MaterialFilePicker()
                    // Pass a source of context. Can be:
                    //    .withActivity(Activity activity)
                    //    .withFragment(Fragment fragment)
                    //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
                    .withActivity(this)
                    // With cross icon on the right side of toolbar for closing picker straight away
                    .withCloseMenu(true)
                    // Entry point path (user will start from it)
                    .withPath("/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/")
                    // Root path (user won't be able to come higher than it)
                    .withRootPath("/storage/emulated/0/Android/data/com.timaimee.vpdemo/")
                    // Showing hidden files
                    .withHiddenFiles(true)
                    // Want to choose only jpg images
                    .withFilter(Pattern.compile(".*\\.(bin|zip|ufw)$"))
                    // Don't apply filter to directories names
                    .withFilterDirectories(false)
                    .withTitle("升级固件")
                    .withRequestCode(FILE_PICKER_REQUEST_CODE)
                    .start();

        });
    }

    private void initDeviceType(){
        String deviceType = VPOperateManager.getInstance().isNordicDevice() ? "Nordic" : VPOperateManager.getInstance().isJLDevice() ? "杰理":
                VPOperateManager.getInstance().isBluetrumDevice() ? "中科" : "其他";
        tvDeviceType.setText("设备类型：" + deviceType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                throw new IllegalArgumentException("data must not be null");
            }

            String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (path != null) {
                firmwareFilePath = path;
                Logger.t(TAG).e("-选择文件-: | firmwareFilePath = " + firmwareFilePath);

                String fullText = "升级文件路径：" + firmwareFilePath;
                String prefix = "升级文件路径：";
                SpannableString spannable = new SpannableString(fullText);
                int prefixEnd = prefix.length();

                spannable.setSpan(
                        new ForegroundColorSpan(0xff22eeee),
                        0,
                        prefixEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannable.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        0,
                        prefixEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                spannable.setSpan(
                        new ForegroundColorSpan(0xffff7700),
                        prefixEnd,
                        fullText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannable.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        prefixEnd,
                        fullText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                tvOTAFilePath.setText(spannable);
            }
        }
    }

    /**
     * Nordic设备固件升级
     */
    private void otaUpgradeForNordic() {
        lastBytesSent = 0;
        lastTimestamp = 0l;
        VPOperateManager.getInstance().startNordicOtaUpgrade(firmwareFilePath, new OnMcuMgrOtaListener() {
            @Override
            public void onUpgradeStarted(FirmwareUpgradeController controller) {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.parseColor("#33ffcc"));
                tvUpgradeInfo.setText("开始升级");
            }

            @Override
            public void onStateChanged(FirmwareUpgradeManager.State prevState, FirmwareUpgradeManager.State newState) {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.parseColor("#3399ff"));
                tvUpgradeInfo.setText("升级状态改变： prevState=" + prevState + " , newState=" + newState);
            }

            @Override
            public void onUpgradeCompleted() {
                btnStartNRFOta.setEnabled(true);
                tvUpgradeInfo.setTextColor(Color.GREEN);
                tvUpgradeInfo.setText("升级完成");
            }

            @Override
            public void onUpgradeFailed(FirmwareUpgradeManager.State state, McuMgrException error) {
                btnStartNRFOta.setEnabled(true);
                tvUpgradeInfo.setTextColor(Color.RED);
                tvUpgradeInfo.setText("升级失败：state=" + state + "， error=" + error);
            }

            @Override
            public void onUpgradeCanceled(FirmwareUpgradeManager.State state) {
                btnStartNRFOta.setEnabled(true);
                tvUpgradeInfo.setTextColor(Color.RED);
                tvUpgradeInfo.setText("升级取消：state=" + state);
            }

            @Override
            public void onUploadProgressChanged(int bytesSent, int imageSize, long timestamp) {
                int progress = (int) ((bytesSent * 1f / imageSize) * 100);
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.parseColor("#3399ff"));
                tvUpgradeInfo.setText(String.format(Locale.US, "升级进度：%d/%d >> %d%%", bytesSent, imageSize, progress));
                Logger.t("NRF-OTA测试").e(String.format(Locale.US, "升级进度：%d/%d >> %d%%", bytesSent, imageSize, progress));

                if (lastTimestamp > 0) {
                    // 1. 计算两次回调之间发送的数据量 (Bytes)
                    int deltaBytes = bytesSent - lastBytesSent;
                    // 2. 计算两次回调之间的时间差 (毫秒)
                    long deltaTime = timestamp - lastTimestamp;
                    if (deltaTime > 0) {
                        // 3. 计算速度 (bytes/ms 等同于 KB/s)
                        // 公式解释：(deltaBytes / 1024) / (deltaTime / 1000)
                        // 简化后：(deltaBytes * 1000) / (deltaTime * 1024)
                        double speedKbps = (deltaBytes * 1000.0) / (deltaTime * 1024.0);
                        Logger.t("NRF-OTA测试").e("-onUploadProgressChanged-: | 固件升级中 " + progress + "%, 当前速度: " + String.format("%.2f", speedKbps) + " KB/s");
                        tvUpgradeInfo.setText("固件升级中: " + progress + "%" + String.format(" : %.2f", speedKbps) + " KB/s");
                    }
                }

                // 更新记录，供下次计算使用
                lastBytesSent = bytesSent;
                lastTimestamp = timestamp;
                // 计算总进度 (百分比)
                System.out.println("总进度: " + progress + "%");
            }
        });
    }

    /**
     * 中科设备固件升级
     */
    private void otaUpgradeForBluetrum() {
        VPOperateManager.getInstance().startZkOtaUpgrade(firmwareFilePath, new IZkOtaUpgradeListener() {
            @Override
            public void onOtaUpgradeReady() {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.parseColor("#33ffcc"));
                tvUpgradeInfo.setText("准备升级");
            }

            @Override
            public void onOtaUpgradeStart() {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.parseColor("#33ffcc"));
                tvUpgradeInfo.setText("开始升级");
            }

            @Override
            public void onOtaUpgradeUpdating(int progress) {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.parseColor("#3399ff"));
                tvUpgradeInfo.setText("升级中：" + progress + "%");
            }

            @Override
            public void onOtaUpgradePause() {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.CYAN);
                tvUpgradeInfo.setText("升级暂停");
            }

            @Override
            public void onOtaUpgradeContinue() {
                btnStartNRFOta.setEnabled(false);
                tvUpgradeInfo.setTextColor(Color.GREEN);
                tvUpgradeInfo.setText("升级继续");
            }

            @Override
            public void onOtaUpgradeSuccess() {
                btnStartNRFOta.setEnabled(true);
                tvUpgradeInfo.setTextColor(Color.GREEN);
                tvUpgradeInfo.setText("升级成功");
            }

            @Override
            public void onOtaUpgradeWaitFinish() {
                btnStartNRFOta.setEnabled(true);
                tvUpgradeInfo.setTextColor(Color.GREEN);
                tvUpgradeInfo.setText("升级成功，等待重启");
            }

            @Override
            public void onOtaUpgradeFail(int code, String msg) {
                btnStartNRFOta.setEnabled(true);
                tvUpgradeInfo.setTextColor(Color.RED);
                tvUpgradeInfo.setText("升级失败：Code = " + code + " , msg = " + msg);
            }
        });
    }

    /**
     * 杰理设备固件升级
     */
    public void otaUpgradeForJieLi() {
        Intent intent = new Intent(this, JLDeviceOPTActivity.class);
        intent.putExtra(JLDeviceOPTActivity.KEY_FIRMWARE_FILE_PATH, firmwareFilePath);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

    }
}
