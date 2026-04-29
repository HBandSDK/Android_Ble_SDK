package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IQX17DataAcquisitionListener;
import com.veepoo.protocol.listener.data.IQX17DataAcquisitionStateListener;
import com.veepoo.protocol.model.datas.QX17GPSData;
import com.veepoo.protocol.model.datas.QX17HeartRateData;
import com.veepoo.protocol.model.datas.QX17IMUData;
import com.veepoo.protocol.model.enums.EQX17VibrationMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * QX17数据采集流控演示Activity
 */
public class QX17DataAcquisitionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "QX17Demo";
    private VPOperateManager mVpOperateManager;

    private Button btnStart, btnStop, btnContinue, btnClearLog, btnSendVibration;
    private TextView tvStatus, tvLog;
    private ScrollView scrollLog;
    private Spinner spinnerVibrationMode;
    private EditText etVibrationDuration;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
    private int imuCount = 0;
    private int gpsCount = 0;
    private int hrCount = 0;

    // 全局状态标记，由OperaterActivity中的全局监听更新
    public static Boolean lastKnownState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qx17_data_acquisition);

        mVpOperateManager = VPOperateManager.getInstance();

        btnStart = (Button) findViewById(R.id.btn_qx17_start);
        btnStop = (Button) findViewById(R.id.btn_qx17_stop);
        btnContinue = (Button) findViewById(R.id.btn_qx17_continue);
        btnClearLog = (Button) findViewById(R.id.btn_qx17_clear_log);
        tvStatus = (TextView) findViewById(R.id.tv_qx17_status);
        tvLog = (TextView) findViewById(R.id.tv_qx17_log);
        scrollLog = (ScrollView) findViewById(R.id.scroll_qx17_log);
        spinnerVibrationMode = (Spinner) findViewById(R.id.spinner_qx17_vibration_mode);
        etVibrationDuration = (EditText) findViewById(R.id.et_qx17_vibration_duration);
        btnSendVibration = (Button) findViewById(R.id.btn_qx17_send_vibration);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnClearLog.setOnClickListener(this);
        btnSendVibration.setOnClickListener(this);

        // 初始化振动模式Spinner
        String[] vibrationModes = {"开始(0)", "结束(1)", "通知(2)", "提醒(3)", "确认(4)", "节拍(5)", "已连接(6)", "错误(7)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vibrationModes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVibrationMode.setAdapter(adapter);

        // 进入页面时显示当前已知状态
        updateStatusDisplay();

        // 设置状态监听，实时更新状态显示
        mVpOperateManager.setVpQX17DataAcquisitionStateListener(mStateListener);
    }

    private void updateStatusDisplay() {
        if (lastKnownState != null) {
            tvStatus.setText("状态: " + (lastKnownState ? "采集中" : "已停止"));
        } else {
            tvStatus.setText("状态: 未知");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_qx17_start) {
            imuCount = 0;
            gpsCount = 0;
            hrCount = 0;
            appendLog(">>> 发送开启数据采集指令");
            mVpOperateManager.vpQX17StartDataAcquisition(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    appendLog("开启采集写入响应: code=" + code);
                }
            }, mDataListener);
        } else if (id == R.id.btn_qx17_stop) {
            appendLog(">>> 发送关闭数据采集指令");
            mVpOperateManager.vpQX17StopDataAcquisition(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    appendLog("关闭采集写入响应: code=" + code);
                }
            });
        } else if (id == R.id.btn_qx17_continue) {
            appendLog(">>> 发送继续数据采集(重传)指令");
            mVpOperateManager.vpQX17ContinueDataAcquisition(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    appendLog("继续采集写入响应: code=" + code);
                }
            }, mDataListener);
        } else if (id == R.id.btn_qx17_clear_log) {
            tvLog.setText("");
            imuCount = 0;
            gpsCount = 0;
            hrCount = 0;
        } else if (id == R.id.btn_qx17_send_vibration) {
            EQX17VibrationMode mode = EQX17VibrationMode.values()[spinnerVibrationMode.getSelectedItemPosition()];
            String durationStr = etVibrationDuration.getText().toString().trim();
            int duration = 0;
            try {
                duration = Integer.parseInt(durationStr);
            } catch (NumberFormatException e) {
                // ignore
            }
            appendLog(">>> 发送振动指令: mode=" + mode + ", duration=" + duration);
            mVpOperateManager.vpQX17SetVibrationMode(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    appendLog("振动指令写入响应: code=" + code);
                }
            }, mode, duration);
        }
    }

    // 状态监听：设备上报采集状态变更
    private final IQX17DataAcquisitionStateListener mStateListener = new IQX17DataAcquisitionStateListener() {
        @Override
        public void onQX17DataAcquisitionStatus(boolean isOpen) {
            appendLog("[状态变更] isOpen=" + isOpen);
            lastKnownState = isOpen;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateStatusDisplay();
                }
            });
        }
    };

    // 数据监听：接收IMU/GPS/心率数据
    private final IQX17DataAcquisitionListener mDataListener = new IQX17DataAcquisitionListener() {
        @Override
        public void onQX17DataAcquisitionStatus(boolean isOpen) {
            appendLog("[数据监听-状态变更] isOpen=" + isOpen);
        }

        @Override
        public void onQX17IMUData(List<QX17IMUData> imuDataList) {
            imuCount += imuDataList.size();
            QX17IMUData last = imuDataList.get(imuDataList.size() - 1);
            Log.d(TAG, "IMU数据: count=" + imuDataList.size() + ", last=" + last.toString());
            appendLog("[IMU] +" + imuDataList.size() + "条 (总" + imuCount + ") ts=" + last.getTimestamp());
        }

        @Override
        public void onQX17GPSData(QX17GPSData gpsData) {
            gpsCount++;
            appendLog("[GPS] #" + gpsCount + " lat=" + gpsData.getLatitude()
                    + " lon=" + gpsData.getLongitude()
                    + " acc=" + gpsData.getAccuracy() + "m"
                    + " ts=" + gpsData.getTimestamp());
        }

        @Override
        public void onQX17HeartRateData(QX17HeartRateData heartRateData) {
            hrCount++;
            appendLog("[心率] #" + hrCount + " HR=" + heartRateData.getHeartRate()
                    + " ts=" + heartRateData.getTimestamp());
        }
    };

    private void appendLog(String message) {
        String time = mTimeFormat.format(new Date());
        String line = time + " " + message + "\n";
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvLog.append(line);
                scrollLog.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollLog.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        Log.d(TAG, message);
    }
}
