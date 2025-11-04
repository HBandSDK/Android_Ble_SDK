package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.Code;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.IMiniCheckupOptListener;
import com.veepoo.protocol.model.datas.MiniCheckupResultData;
import com.veepoo.protocol.model.enums.EMiniCheckupTestErrorCode;

import java.util.Locale;

public class MiniCheckupActivity extends Activity implements IMiniCheckupOptListener, View.OnClickListener {
    Button btnStartMiniCheckup;
    Button btnStopMiniCheckup;
    TextView tvMiniCheckupInfo;

    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_checkup);
        btnStopMiniCheckup = findViewById(R.id.btnStopMiniCheckup);
        btnStartMiniCheckup = findViewById(R.id.btnStartMiniCheckup);
        tvMiniCheckupInfo = findViewById(R.id.tvMiniCheckupInfo);
        btnStopMiniCheckup.setOnClickListener(this);
        btnStartMiniCheckup.setOnClickListener(this);
    }

    @Override
    public void onMiniCheckupTestProgress(int progress) {
        tvMiniCheckupInfo.setText(String.format(Locale.CHINA,"【开始】微体检指令写入成功！\n 测量中... %d%%", progress));
    }

    @Override
    public void onMiniCheckupStopSuccess() {
        appendMsg("微体检测量已【停止】");
    }

    @Override
    public void onMiniCheckupTestFailed(@NonNull EMiniCheckupTestErrorCode errorCode) {
        appendMsg("微体检测量【失败】:" + errorCode);
    }

    @Override
    public void onMiniCheckupSuccess(@NonNull MiniCheckupResultData testResultData) {
        appendMsg("微体检测量【成功】-->");
        appendMsg("【心率】:" + testResultData.getHeartRate() +"bmp");
        appendMsg("【血氧】:" + testResultData.getBloodOxygen() + "%");
        appendMsg("【压力】:" + testResultData.getStress());
        appendMsg("【情绪】:" + testResultData.getEmotion());
        appendMsg("【疲劳度】:" + testResultData.getFatigue());
        appendMsg("【血糖】:" + testResultData.getBloodGlucose() + "mmol/L");
        appendMsg("【体温】:" + testResultData.getBodyTemperature()+"℃");
        appendMsg("【血压（收缩压）】:" + testResultData.getSystolicBloodPressure());
        appendMsg("【血压（舒张压）】:" + testResultData.getDiastolicBloodPressure());
        appendMsg("【HRV】:" + testResultData.getHrv());
    }

    @Override
    public void onClick(View v) {
        if (v == btnStartMiniCheckup) {
            sb.setLength(0);
            VPOperateManager.getInstance().startMiniCheckup(code -> {
                if (code == Code.REQUEST_SUCCESS) {
                    appendMsg("【开始】微体检指令写入成功！");
                } else {
                    appendMsg("【开始】微体检指令写入失败！");
                }
            }, this);
        }
        if (v == btnStopMiniCheckup) {
            VPOperateManager.getInstance().stopMiniCheckup(code -> {
                if (code == Code.REQUEST_SUCCESS) {
                    appendMsg("【停止】微体检指令写入成功！");
                } else {
                    appendMsg("【停止】微体检指令写入失败！");
                }
            },this);
        }
    }

    private void appendMsg(String msg) {
        sb.append(msg).append("\n");
        tvMiniCheckupInfo.setText(sb.toString());
    }
}
