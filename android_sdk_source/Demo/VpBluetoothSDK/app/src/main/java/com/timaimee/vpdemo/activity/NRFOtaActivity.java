package com.timaimee.vpdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.nordic.OnMcuMgrOtaListener;
import com.veepoo.protocol.nordic.McuMgrOtaManager;
import com.orhanobut.logger.Logger;

import io.runtime.mcumgr.dfu.FirmwareUpgradeController;
import io.runtime.mcumgr.dfu.mcuboot.FirmwareUpgradeManager;
import io.runtime.mcumgr.exception.McuMgrException;

import java.io.File;
import java.util.Locale;

public class NRFOtaActivity extends AppCompatActivity {
    TextView tvOTAFilePath, tvUpgradeInfo;
    Button btnStartNRFOta;

    private String firmwareFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nrf_ota);
        tvOTAFilePath = findViewById(R.id.tvOTAFilePath);
        tvUpgradeInfo = findViewById(R.id.tvUpgradeInfo);
        btnStartNRFOta = findViewById(R.id.btnStartNRFOta);//
        firmwareFilePath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/8600_99.99.98.bin";
        initEvent();
        tvOTAFilePath.setText("升级文件路径：" + firmwareFilePath + "(此处用户自行修改)");
//        McuMgrOtaManager.getInstance().setMtu(240);
        Logger.t("NRF-OTA测试").e("设置NRF-OTA MTU=240");
    }


    private int lastBytesSent = 0;
    private long lastTimestamp = 0;

    private void initEvent() {
        btnStartNRFOta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!new File(firmwareFilePath).exists()) {
                    Toast.makeText(NRFOtaActivity.this, "升级文件不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                VPOperateManager.getInstance().startNordicOtaUpgrade(firmwareFilePath, new OnMcuMgrOtaListener() {
                    @Override
                    public void onUpgradeStarted(FirmwareUpgradeController controller) {
                        tvUpgradeInfo.setText("开始升级");
                    }

                    @Override
                    public void onStateChanged(FirmwareUpgradeManager.State prevState, FirmwareUpgradeManager.State newState) {
                        tvUpgradeInfo.setText("升级状态改变： prevState=" + prevState + " , newState=" + newState);
                    }

                    @Override
                    public void onUpgradeCompleted() {
                        tvUpgradeInfo.setText("升级完成");
                    }

                    @Override
                    public void onUpgradeFailed(FirmwareUpgradeManager.State state, McuMgrException error) {
                        tvUpgradeInfo.setText("升级失败：state=" + state + "， error=" + error);
                    }

                    @Override
                    public void onUpgradeCanceled(FirmwareUpgradeManager.State state) {
                        tvUpgradeInfo.setText("升级取消：state=" + state);
                    }

                    @Override
                    public void onUploadProgressChanged(int bytesSent, int imageSize, long timestamp) {
                        int progress = (int) ((bytesSent * 1f / imageSize) * 100);
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
        });
    }

}
