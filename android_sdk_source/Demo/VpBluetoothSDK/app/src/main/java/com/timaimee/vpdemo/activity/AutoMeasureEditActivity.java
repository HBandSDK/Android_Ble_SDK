package com.timaimee.vpdemo.activity;

import static com.timaimee.vpdemo.activity.AutoMeasureActivity.selectData;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.iielse.switchbutton.SwitchView;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAutoMeasureSettingDataListener;
import com.veepoo.protocol.model.datas.AutoMeasureData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.List;
import java.util.Locale;

public class AutoMeasureEditActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvAutoMeasureType;
    TextView tvStepUnitSet;
    TextView tvSupportInfo;
    EditText etInterval;
    Button btnStartTimePicker, btnEndTimePicker, btnSetting;
    SwitchView svStatus;

    private int startMinutes, endMinutes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_auto_measure_edit);
        tvAutoMeasureType = findViewById(R.id.tvAutoMeasureType);
        tvStepUnitSet = findViewById(R.id.tvStepUnitSet);
        tvSupportInfo = findViewById(R.id.tvSupportInfo);
        etInterval = findViewById(R.id.etInterval);
        btnStartTimePicker = findViewById(R.id.btnStartTimePicker);
        btnEndTimePicker = findViewById(R.id.btnEndTimePicker);
        svStatus = findViewById(R.id.svStatus);
        btnSetting = findViewById(R.id.btnSetting);

        btnSetting.setOnClickListener(this);
        btnEndTimePicker.setOnClickListener(this);
        btnStartTimePicker.setOnClickListener(this);
        initData();
    }

    private String getMeasureTime(AutoMeasureData autoMeasureData){
        int startTime = autoMeasureData.getSupportStartMinute();
        int endTime = autoMeasureData.getSupportEndMinute();
        return String.format(Locale.CHINA,"支持设置的时间段: %02d:%02d-%02d:%02d",
                startTime / 60, startTime % 60,
                endTime / 60, endTime % 60);
    }

    private void initData(){
        if(selectData != null) {
            Logger.t("").e("-initData-: | selectData = " + selectData);
            tvAutoMeasureType.setText(getAutoMeasureDataName(selectData));
            etInterval.setText(selectData.getMeasureInterval()+"");
            tvStepUnitSet.setText("测量间隔（步长"+selectData.getStepUnit()+"m):");
            String supportInfo =
                    getMeasureTime(selectData)
                            + "\n是否支持修改时间段:" + selectData.isSlotModify()
                            + "\n是否支持修改间隔: "+ selectData.isIntervalModify()
                    ;
            tvSupportInfo.setText(supportInfo);
            svStatus.setOpened(selectData.isSwitchOpen());
            startMinutes = selectData.getCurrentStartMinute();
            endMinutes = selectData.getCurrentEndMinute();
            btnStartTimePicker.setText(String.format(Locale.US, "%02d:%02d", startMinutes/60, startMinutes%60));
            btnEndTimePicker.setText(String.format(Locale.US, "%02d:%02d", endMinutes/60, endMinutes%60));
            etInterval.setEnabled(selectData.isIntervalModify());
        }
    }

    @Override
    public void onClick(View v) {
        // 开始时间
        if (v.getId() == R.id.btnStartTimePicker) {
            if (!selectData.isSlotModify()) {
                showMsg("暂不支持设置测量时间段");
                return;
            }
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                startMinutes = hourOfDay * 60 + minute;
                btnStartTimePicker.setText(String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute));
            }, startMinutes / 60, startMinutes % 60, true).show();
        }

        // 结束时间
        else if (v.getId() == R.id.btnEndTimePicker) {
            if (!selectData.isSlotModify()) {
                showMsg("暂不支持设置测量时间段");
                return;
            }
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                endMinutes = hourOfDay * 60 + minute;
                btnEndTimePicker.setText(String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute));
            }, endMinutes / 60, endMinutes % 60, true).show();
        }

        // 保存设置
        else if (v.getId() == R.id.btnSetting) {
            // 修复：时间范围判断逻辑
            if (selectData.getSupportStartMinute() != 0 || selectData.getSupportEndMinute() != 0) {
                if (startMinutes < selectData.getSupportStartMinute() || endMinutes > selectData.getSupportEndMinute()) {
                    showMsg(getMeasureTime(selectData));
                    return;
                }
            }

            // 结束时间不能早于开始时间
            if (endMinutes <= startMinutes) {
                showMsg("结束时间必须大于开始时间");
                return;
            }

            String intervalStr = etInterval.getText().toString();
            if(TextUtils.isEmpty(intervalStr)) {
                showMsg("时间间隔不能为空");
                return;
            }
            int interval = Integer.parseInt(intervalStr);
            if (interval < selectData.getStepUnit() || interval % selectData.getStepUnit() != 0 ) {
                showMsg("间隔不能小于：" + selectData.getStepUnit() + "分钟，且为"+selectData.getStepUnit()+"的整数倍");
                return;
            }
            selectData.setCurrentStartMinute(startMinutes);
            selectData.setCurrentEndMinute(endMinutes);
            selectData.setMeasureInterval(interval);
            selectData.setSwitchOpen(svStatus.isOpened());

            VPOperateManager.getInstance().setAutoMeasureSettingData(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {}
            }, selectData, new IAutoMeasureSettingDataListener() {
                @Override
                public void onSettingDataChange(List<AutoMeasureData> autoMeasureDataList) {}

                @Override
                public void onSettingDataChangeFail() {
                    showMsg("设置失败");
                }

                @Override
                public void onSettingDataChangeSuccess() {
                    showMsg("设置成功");
                    finish();
                }
            });
        }
    }

    private String getAutoMeasureDataName(AutoMeasureData autoMeasureData){
        switch (autoMeasureData.getFunType()) {
            case PULSE_RATE:
                return "心率";
            case BLOOD_PRESSURE:
                return "血压";
            case BLOOD_GLUCOSE:
                return "血糖";
            case STRESS:
                return "压力";
            case BLOOD_OXYGEN:
                return "血氧";
            case BODY_TEMPERATURE:
                return "体温";
            case LORENZ:
                return "洛伦兹";
            case HRV:
                return "HRV";
            case BLOOD_COMPOSITION:
                return "血液成分";
        }
        return "UNKNOWN";
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}