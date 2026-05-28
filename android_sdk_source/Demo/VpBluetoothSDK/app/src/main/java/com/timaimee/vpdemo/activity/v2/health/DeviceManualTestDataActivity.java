package com.timaimee.vpdemo.activity.v2.health;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.timaimee.vpdemo.utils.CollapseCardLogView;
import com.timaimee.vpdemo.utils.TimeUtils;
import com.veepoo.protocol.listener.data.IDeviceManualDetectDataListener;
import com.veepoo.protocol.model.datas.BloodComponent;
import com.veepoo.protocol.model.datas.BloodComponentManualData;
import com.veepoo.protocol.model.datas.BloodGlucoseManualData;
import com.veepoo.protocol.model.datas.BloodOxygenManualData;
import com.veepoo.protocol.model.datas.BloodPressureManualData;
import com.veepoo.protocol.model.datas.BodyTemperatureManualData;
import com.veepoo.protocol.model.datas.EmotionManualData;
import com.veepoo.protocol.model.datas.FatigueManualData;
import com.veepoo.protocol.model.datas.HeartRateManualData;
import com.veepoo.protocol.model.datas.HrvManualData;
import com.veepoo.protocol.model.datas.MetoManualData;
import com.veepoo.protocol.model.datas.MiniCheckupManualData;
import com.veepoo.protocol.model.datas.PressureManualData;
import com.veepoo.protocol.model.datas.SkinConductanceManualData;
import com.veepoo.protocol.model.enums.DeviceManualDataType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeviceManualTestDataActivity extends BaseVPBLETestActivity implements IDeviceManualDetectDataListener{
    CollapseCardLogView ccvGetDeviceManualTestData;
    CheckBox cbBloodPress, cbHeartRate, cbBloodGlucose, cbStress, cbBloodOxygen, cbBodyTemperature,
            cbMeto, cbHrv, cbBloodComponent, cbMiniCheckup, cbEmotion, cbFatigue, cbEDA, cbAll;
    Button btnGetDeviceManualTestData, btnDatePicker, btnTimePicker;

    @Override
    public int getLayoutID() {
        return R.layout.activity_device_manual_test_data;
    }

    @Override
    public String pageTitle() {
        return "设备手动测量数据";
    }

    @Override
    public void initView() {
        ccvGetDeviceManualTestData = findViewById(R.id.ccvGetDeviceManualTestData);
        cbBloodPress = findViewById(R.id.cbBloodPress);
        cbHeartRate = findViewById(R.id.cbHeartRate);
        cbBloodGlucose = findViewById(R.id.cbBloodGlucose);
        cbStress = findViewById(R.id.cbStress);
        cbBloodOxygen = findViewById(R.id.cbBloodOxygen);
        cbBodyTemperature = findViewById(R.id.cbBodyTemperature);
        cbMeto = findViewById(R.id.cbMeto);
        cbHrv = findViewById(R.id.cbHrv);
        cbBloodComponent = findViewById(R.id.cbBloodComponent);
        cbMiniCheckup = findViewById(R.id.cbMiniCheckup);
        cbEmotion = findViewById(R.id.cbEmotion);
        cbFatigue = findViewById(R.id.cbFatigue);
        cbEDA = findViewById(R.id.cbEDA);
        cbAll = findViewById(R.id.cbAll);
        btnGetDeviceManualTestData = findViewById(R.id.btnGetDeviceManualTestData);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
    }

    @Override
    public void initData() {
        btnDatePicker.setText(TimeUtils.getCurrentDateStr());
        btnTimePicker.setText("00:00:00");
    }

    @Override
    public void initEvent() {
        btnGetDeviceManualTestData.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDatePicker) {
            showDatePicker();
        } else if (id == R.id.btnTimePicker) {
            showTimePicker();
        } else if (id == R.id.btnGetDeviceManualTestData) {
            getDeviceManualTestData();
        }
    }

    private void showDatePicker() {
        // 1. 获取按钮当前的文本
        String currentText = btnDatePicker.getText().toString().trim();
        // 2. 初始化默认年月日（降级方案：当前系统时间）
        Calendar calendar = Calendar.getInstance();
        int defYear = calendar.get(Calendar.YEAR);
        int defMonth = calendar.get(Calendar.MONTH); // 注意：Calendar 月份从 0 开始
        int defDay = calendar.get(Calendar.DAY_OF_MONTH);

        // 3. 尝试解析按钮上的 "yyyy-MM-dd"
        if (currentText.contains("-")) {
            String[] parts = currentText.split("-");
            if (parts.length == 3) {
                try {
                    defYear = Integer.parseInt(parts[0]);
                    defMonth = Integer.parseInt(parts[1]) - 1; // 转换为 Calendar 的 0-11 月
                    defDay = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // 解析失败则保持系统当前时间
                }
            }
        }

        // 4. 弹出选择器，并传入解析好的默认值
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // 更新按钮显示的文本
            String selectedDate = String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            btnDatePicker.setText(selectedDate);

        }, defYear, defMonth, defDay).show();
    }

    private void showTimePicker() {
        // 1. 获取按钮当前的文本
        String currentText = btnTimePicker.getText().toString().trim();
        // 2. 初始化默认时分（降级方案：当前系统时间）
        Calendar calendar = Calendar.getInstance();
        int defHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defMin = calendar.get(Calendar.MINUTE);

        // 3. 尝试解析按钮上的 "HH:mm:ss" 或 "HH:mm"
        if (currentText.contains(":")) {
            String[] parts = currentText.split(":");
            if (parts.length >= 2) {
                try {
                    defHour = Integer.parseInt(parts[0]);
                    defMin = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // 解析失败则保持系统当前时间
                }
            }
        }
        // 4. 弹出选择器，并传入解析好的默认值
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            // 始终保持 "HH:mm:00" 的秒级尾缀输出
            String selectedTime = String.format(Locale.CHINA, "%02d:%02d:00", hourOfDay, minute);
            btnTimePicker.setText(selectedTime);
        }, defHour, defMin, true).show();
    }

    /**
     * 根据日期按钮和时间按钮的文本，获取合并后的秒级时间戳
     * * @param dateBtnText 日期按钮的文本 (例如: "2026-05-27")
     * @param timeBtnText 时间按钮的文本 (例如: "15:30:00")
     * @return 秒级时间戳（10位）。如果解析失败，默认返回当前系统时间戳。
     */
    public long getSelectedEpochSecond(String dateBtnText, String timeBtnText) {
        // 1. 判空处理，防止按钮文本异常
        if (dateBtnText == null || dateBtnText.trim().isEmpty() ||
                timeBtnText == null || timeBtnText.trim().isEmpty()) {
            return System.currentTimeMillis() / 1000; // 降级返回当前系统时间戳
        }

        // 2. 将两个按钮的文本拼接成完整的日期时间字符串
        // 例如: "2026-05-27" + " " + "15:30:00" -> "2026-05-27 15:30:00"
        String fullDateTimeStr = dateBtnText.trim() + " " + timeBtnText.trim();

        try {
            // 3. 使用线程安全的经典格式化器（或者直接 new SimpleDateFormat）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // 4. 解析为 Date 对象
            Date date = sdf.parse(fullDateTimeStr);

            if (date != null) {
                // 5. date.getTime() 返回的是毫秒级(13位)，除以 1000 得到秒级时间戳(10位)
                return date.getTime() / 1000;
            }
        } catch (Exception e) {
            e.printStackTrace(); // 解析异常（比如格式不匹配）
        }

        // 如果中途出错，稳妥起见返回当前系统时间戳
        return System.currentTimeMillis() / 1000;
    }

    private void getDeviceManualTestData() {
        List<DeviceManualDataType> types = getDeviceManualTypeList();
        if (types.isEmpty()) {
            showToast("请至少勾选一个");
            return;
        }
        long startReadTimestamp = getSelectedEpochSecond(btnDatePicker.getText().toString(), btnTimePicker.getText().toString());
        vpBleManager.readDeviceManualData(defaultResponse, startReadTimestamp, types, types, this);
    }

    private List<DeviceManualDataType> getDeviceManualTypeList() {
        List<DeviceManualDataType> types = new ArrayList<>();
        if (cbAll.isChecked()) {
            types.add(DeviceManualDataType.ALL);
            return types;
        }
        if (cbBloodPress.isChecked()) {
            types.add(DeviceManualDataType.BLOOD_PRESSURE);
        }
        if (cbHeartRate.isChecked()) {
            types.add(DeviceManualDataType.HEART_RATE);
        }
        if (cbBloodGlucose.isChecked()) {
            types.add(DeviceManualDataType.BLOOD_GLUCOSE);
        }
        if (cbStress.isChecked()) {
            types.add(DeviceManualDataType.STRESS);
        }
        if (cbBloodOxygen.isChecked()) {
            types.add(DeviceManualDataType.BLOOD_OXYGEN);
        }
        if (cbBodyTemperature.isChecked()) {
            types.add(DeviceManualDataType.BODY_TEMPERATURE);
        }
        if (cbMeto.isChecked()) {
            types.add(DeviceManualDataType.MET);
        }
        if (cbHrv.isChecked()) {
            types.add(DeviceManualDataType.HRV);
        }
        if (cbBloodComponent.isChecked()) {
            types.add(DeviceManualDataType.BLOOD_COMPOSITION);
        }
        if (cbMiniCheckup.isChecked()) {
            types.add(DeviceManualDataType.MINI_CHECKUP);
        }
        if (cbEmotion.isChecked()) {
            types.add(DeviceManualDataType.EMOTION);
        }
        if (cbFatigue.isChecked()) {
            types.add(DeviceManualDataType.FATIGUE);
        }
        if (cbEDA.isChecked()) {
            types.add(DeviceManualDataType.SKIN_CONDUCTANCE);
        }
        return types;
    }

    @Override
    public void onBloodPressureDataChange(List<BloodPressureManualData> bloodPressureManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取血压=====");
        for (BloodPressureManualData data : bloodPressureManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText(getSimpleBloodPressureManualData(data));
        }
    }

    private String getSimpleBloodPressureManualData(BloodPressureManualData data){
        return "▶ [血压]>" + timeFormat(data.getTimeStamp()) + " : 高压="+data.getSystolic() + ",低压="+data.getDiastolic();
    }

    @Override
    public void onHeartRateDataChange(List<HeartRateManualData> heartRateManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取心率=====");
        for (HeartRateManualData data : heartRateManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [心率]>" + timeFormat(data.getTimeStamp()) + " : 心率=" + data.getRate());
        }
    }

    @Override
    public void onBloodGlucoseDataChange(List<BloodGlucoseManualData> bloodGlucoseManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取血糖=====");
        for (BloodGlucoseManualData data : bloodGlucoseManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [心率]>" + timeFormat(data.getTimeStamp()) + " : 血糖值=" + data.getBloodGlucoseValue()+"mmol/L");
        }
    }

    @Override
    public void onPressureManualDataChange(List<PressureManualData> pressureManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取压力=====");
        for (PressureManualData data : pressureManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [心率]>" + timeFormat(data.getTimeStamp()) + " : 指数=" + data.getPressure());
        }
    }

    @Override
    public void onBloodOxygenDataChange(List<BloodOxygenManualData> bloodOxygenManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取血氧=====");
        for (BloodOxygenManualData data : bloodOxygenManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [血氧]>" + timeFormat(data.getTimeStamp()) + " : " + Arrays.toString(data.getOxygen()));
        }
    }

    @Override
    public void onBodyTemperatureDataChange(List<BodyTemperatureManualData> bodyTemperatureManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取体温=====");
        for (BodyTemperatureManualData data : bodyTemperatureManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [体温]>" + timeFormat(data.getTimeStamp()) + " : 体温=" + data.getTemperature() + ", 皮肤温度=" + data.getBaseTemperature());
        }
    }

    @Override
    public void onMetoManualDataChange(List<MetoManualData> metoManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取MET=====");
        for (MetoManualData data : metoManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [MET]>" + timeFormat(data.getTimeStamp()) + " : 梅脱=" + data.getMeto());
        }
    }

    @Override
    public void onHrvManualDataChange(List<HrvManualData> hrvManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取HRV=====");
        for (HrvManualData data : hrvManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [HRV]>" + timeFormat(data.getTimeStamp()) + " : " + Arrays.toString(data.getHrv()));
        }
    }

    @Override
    public void onBloodComponentManualDataChange(List<BloodComponentManualData> bloodComponentManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取血液成分=====");
        for (BloodComponentManualData data : bloodComponentManualDataList) {
            BloodComponent bloodComponent = new BloodComponent(data.getUricAcid(), data.gettCHO(), data.getUricAcid(), data.gethDL(), data.gethDL());
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [血液成分]>" + timeFormat(data.getTimeStamp()) + " : " + bloodComponent.toString());
        }
    }

    @Override
    public void onMiniCheckupManualDataChange(List<MiniCheckupManualData> miniCheckupManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取微体检=====");
        for (MiniCheckupManualData data : miniCheckupManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText(getSimpleMiniCheckupData(data));
        }
    }

    private String getSimpleMiniCheckupData(MiniCheckupManualData data){
        return "▶ [微体检]>" + timeFormat(data.getTimeStamp()) + " : 心率="+data.getHeart()
                + ",血氧="+data.getOxygen()
                + ",血糖="+data.getBloodGlucose()
                + ",高压="+data.getHighValue()
                + ",低压="+data.getLowValue()
                + ",HRV="+data.getHrv()
                + ",压力="+data.getPressure()
                + ",情绪="+data.getEmotion()
                + ",疲劳度="+data.getFatigue()
                ;
    }

    @Override
    public void onEmotionManualDataChange(List<EmotionManualData> emotionManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取情绪=====");
        for (EmotionManualData data : emotionManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [情绪]>" + timeFormat(data.getTimeStamp()) + " : 指数=" + data.getEmotion());
        }
    }

    @Override
    public void onFatigueManualDataChange(List<FatigueManualData> fatigueManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取疲劳度=====");
        for (FatigueManualData data : fatigueManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText("▶ [疲劳度]>" + timeFormat(data.getTimeStamp()) + " : 指数=" + data.getFatigue());
        }
    }

    @Override
    public void onSkinConductanceManualDataChange(List<SkinConductanceManualData> skinConductanceManualDataList) {
        ccvGetDeviceManualTestData.appendWhiteText("=====获取皮电=====");
        for (SkinConductanceManualData data : skinConductanceManualDataList) {
            ccvGetDeviceManualTestData.appendBlueMiddleText(getSimpleSkinConductanceManualData(data));
        }
    }

    private String getSimpleSkinConductanceManualData(SkinConductanceManualData data){
        return "▶ [皮电]>" + timeFormat(data.getTimeStamp()) + " : " +
                "情绪="+data.getEmotionLevel()
                + ",皮肤含水量="+data.getSkinMoisture()
                + ",抑郁症风险="+data.getDepressionRisk()
                + ",交感神经活跃度="+data.getSnsActivation()
                + ",皮质醇浓度="+data.getCortisolValue()
                ;
    }

    @Override
    public void onReadProgress(float progress) {
        ccvGetDeviceManualTestData.appendOrangeText("▶ [读取进度]:"+progress+"%");
    }

    @Override
    public void onReadComplete() {
        ccvGetDeviceManualTestData.appendOrangeText("✅️ [手动测量数据] 读取完成！");
    }

    @Override
    public void onReadFail() {
        ccvGetDeviceManualTestData.appendRedLargeText("❌ [手动测量数据] 读取失败！");
    }

    @Override
    public void onCMDWriteFailed(int cmdTag) {
        super.onCMDWriteFailed(cmdTag);
        ccvGetDeviceManualTestData.appendRedLargeText("❌ [手动测量数据] 读取指令写入失败！");
    }

    @Override
    public void onCMDWriteSuccess(int cmdTag) {
        super.onCMDWriteSuccess(cmdTag);
        ccvGetDeviceManualTestData.appendOrangeText("✅️ [手动测量数据] 读取指令写入成功！");
    }

    private String timeFormat(int timestamp) {
        return TimeUtils.secondsTimestampFormat(timestamp);
    }
}
