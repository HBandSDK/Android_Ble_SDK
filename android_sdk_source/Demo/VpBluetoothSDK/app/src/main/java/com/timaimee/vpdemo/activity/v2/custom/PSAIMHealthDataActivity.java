package com.timaimee.vpdemo.activity.v2.custom;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.timaimee.vpdemo.utils.CollapseCardLogView;
import com.timaimee.vpdemo.utils.TimeUtils;
import com.veepoo.protocol.listener.data.IDeviceManualDetectDataListener;
import com.veepoo.protocol.listener.data.IQH15HealthDataOptListener;
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
import com.veepoo.protocol.model.datas.QH15HealthData;
import com.veepoo.protocol.model.datas.SkinConductanceManualData;
import com.veepoo.protocol.model.enums.DeviceManualDataType;
import com.veepoo.protocol.model.enums.EQH15ComplianceType;
import com.veepoo.protocol.model.enums.HealthStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PSAIMHealthDataActivity extends BaseVPBLETestActivity implements IQH15HealthDataOptListener, IDeviceManualDetectDataListener {

    // 三大核心日志折叠面板组件
    CollapseCardLogView ccvSettingPSAIMHealthData;
    CollapseCardLogView ccvSettingComplianceEvent;
    CollapseCardLogView ccvGetRefreshTimeAndSteps;
    CollapseCardLogView ccvGetDeviceManualTestData;

    // 达标事件控制
    Spinner spComplianceType;
    Button btnSettingComplianceEvent;

    // 状态查询控制
    Button btnGetSteps, btnGetRefreshTimestamp;

    // 健康大包控制核心按钮与时间戳
    Button btnSettingHealthData, btnGenerateTimestamp;
    EditText etDataTimestamp;

    // 一、年龄主包参数 (0x10 - 0x18)
    SeekBar sbBiosAge, sbHeartAge, sbFitnessAge;
    SeekBar sbBiosAgeChange, sbHeartAgeChange, sbFitnessAgeChange;
    Spinner spBiosAgeStatus, spHeartAgeStatus, spFitnessAgeStatus;
    TextView tvBiosAgeVal, tvHeartAgeVal, tvFitnessAgeVal;
    TextView tvBiosAgeChangeVal, tvHeartAgeChangeVal, tvFitnessAgeChangeVal;

    // 三、月度对比报告参数 (0x1C - 0x24 / 0x28 - 0x2A)
    SeekBar sbBiosMonth, sbHeartMonth, sbFitnessMonth;
    SeekBar sbBiosMonthChange, sbHeartMonthChange, sbFitnessMonthChange;
    Spinner spBiosMonthStatus, spHeartMonthStatus, spFitnessMonthStatus;
    TextView tvBiosMonthVal, tvHeartMonthVal, tvFitnessMonthVal;
    TextView tvBiosMonthChangeVal, tvHeartMonthChangeVal, tvFitnessMonthChangeVal;

    // 三、年度对比报告参数 (0x1F - 0x21 / 0x25 - 0x27 / 0x2B - 0x2D)
    SeekBar sbBiosYear, sbHeartYear, sbFitnessYear;
    SeekBar sbBiosYearChange, sbHeartYearChange, sbFitnessYearChange;
    Spinner spBiosYearStatus, spHeartYearStatus, spFitnessYearStatus;
    TextView tvBiosYearVal, tvHeartYearVal, tvFitnessYearVal;
    TextView tvBiosYearChangeVal, tvHeartYearChangeVal, tvFitnessYearChangeVal;

    // 四、三大慢病综合风险控制 (0x30 - 0x38)
    SeekBar sbCardioRisk, sbDementiaRisk, sbDiabetesRisk;
    SeekBar sbCardioRiskChange, sbDementiaRiskChange, sbDiabetesRiskChange;
    Spinner spCardioRiskStatus, spDementiaRiskStatus, spDiabetesRiskStatus;
    TextView tvCardioRiskVal, tvDementiaRiskVal, tvDiabetesRiskVal;
    TextView tvCardioRiskChangeVal, tvDementiaRiskChangeVal, tvDiabetesRiskChangeVal;

    // 五、心血管细化子项风险 (0x39 - 0x41)
    SeekBar sbHeartAttackRisk, sbStrokeRisk, sbHeartFailureRisk;
    SeekBar sbHeartAttackChange, sbStrokeChange, sbHeartFailureChange; // ⚙️ 补齐声明
    Spinner spHeartAttackRiskStatus, spStrokeRiskStatus, spHeartFailureRiskStatus;
    TextView tvHeartAttackRiskVal, tvStrokeRiskVal, tvHeartFailureRiskVal;
    TextView tvHeartAttackChangeVal, tvStrokeChangeVal, tvHeartFailureChangeVal; // ⚙️ 补齐声明

    // 六、生活质量与机能损伤风险 (0x42 - 0x4A)
    SeekBar sbMemoryRisk, sbFallRisk, sbIndependenceRisk;
    SeekBar sbMemoryChange, sbFallChange, sbIndependenceChange;
    Spinner spMemoryStatus, spFallStatus, spIndependenceStatus;
    TextView tvMemoryRiskVal, tvFallRiskVal, tvIndependenceRiskVal;
    TextView tvMemoryChangeVal, tvFallChangeVal, tvIndependenceChangeVal;

    // 七、糖尿病专项并发症风险 (0x4B - 0x53)
    SeekBar sbKidneyRisk, sbNerveRisk, sbVisionRisk;
    SeekBar sbKidneyChange, sbNerveChange, sbVisionChange;
    Spinner spKidneyStatus, spNerveStatus, spVisionStatus;
    TextView tvKidneyRiskVal, tvNerveRiskVal, tvVisionRiskVal;
    TextView tvKidneyChangeVal, tvNerveChangeVal, tvVisionChangeVal;

    // 八、整体健康评估摘要指数 (0x54 - 0x55)
    SeekBar sbNutritionScore, sbTargetScore;
    TextView tvNutritionScoreVal, tvTargetScoreVal;

    CheckBox cbBloodPress, cbHeartRate, cbBloodGlucose, cbStress, cbBloodOxygen, cbBodyTemperature,
            cbMeto, cbHrv, cbBloodComponent, cbMiniCheckup, cbEmotion, cbFatigue, cbEDA, cbAll;
    Button btnGetDeviceManualTestData, btnDatePicker, btnTimePicker;

    @Override
    public int getLayoutID() {
        return R.layout.activity_psaim_health_data;
    }

    @Override
    public String pageTitle() {
        return "QH15 Health Data Opt";
    }

    @Override
    public void initView() {
        ccvSettingPSAIMHealthData = findViewById(R.id.ccvSettingPSAIMHealthData);
        ccvSettingComplianceEvent = findViewById(R.id.ccvSettingComplianceEvent);
        ccvGetRefreshTimeAndSteps = findViewById(R.id.ccvGetRefreshTimeAndSteps);
        ccvGetDeviceManualTestData = findViewById(R.id.ccvGetDeviceManualTestData);

        spComplianceType = findViewById(R.id.spComplianceType);
        btnSettingComplianceEvent = findViewById(R.id.btnSettingComplianceEvent);
        btnGetSteps = findViewById(R.id.btnGetSteps);
        btnGetRefreshTimestamp = findViewById(R.id.btnGetRefreshTimestamp);
        btnSettingHealthData = findViewById(R.id.btnSettingHealthData);
        btnGenerateTimestamp = findViewById(R.id.btnGenerateTimestamp);
        etDataTimestamp = findViewById(R.id.etDataTimestamp);

        // 一、年龄主包
        sbBiosAge = findViewById(R.id.sbBiosAge);
        sbHeartAge = findViewById(R.id.sbHeartAge);
        sbFitnessAge = findViewById(R.id.sbFitnessAge);
        tvBiosAgeVal = findViewById(R.id.tvBiosAgeVal);
        tvHeartAgeVal = findViewById(R.id.tvHeartAgeVal);
        tvFitnessAgeVal = findViewById(R.id.tvFitnessAgeVal);
        spBiosAgeStatus = findViewById(R.id.spBiosAgeStatus);
        spHeartAgeStatus = findViewById(R.id.spHeartAgeStatus);
        spFitnessAgeStatus = findViewById(R.id.spFitnessAgeStatus);
        sbBiosAgeChange = findViewById(R.id.sbBiosAgeChange);
        sbHeartAgeChange = findViewById(R.id.sbHeartAgeChange);
        sbFitnessAgeChange = findViewById(R.id.sbFitnessAgeChange);
        tvBiosAgeChangeVal = findViewById(R.id.tvBiosAgeChangeVal);
        tvHeartAgeChangeVal = findViewById(R.id.tvHeartAgeChangeVal);
        tvFitnessAgeChangeVal = findViewById(R.id.tvFitnessAgeChangeVal);

        // 三、月度对比
        sbBiosMonth = findViewById(R.id.sbBiosMonth);
        sbHeartMonth = findViewById(R.id.sbHeartMonth);
        sbFitnessMonth = findViewById(R.id.sbFitnessMonth);
        tvBiosMonthVal = findViewById(R.id.tvBiosMonthVal);
        tvHeartMonthVal = findViewById(R.id.tvHeartMonthVal);
        tvFitnessMonthVal = findViewById(R.id.tvFitnessMonthVal);
        spBiosMonthStatus = findViewById(R.id.spBiosMonthStatus);
        spHeartMonthStatus = findViewById(R.id.spHeartMonthStatus);
        spFitnessMonthStatus = findViewById(R.id.spFitnessMonthStatus);
        sbBiosMonthChange = findViewById(R.id.sbBiosMonthChange);
        sbHeartMonthChange = findViewById(R.id.sbHeartMonthChange);
        sbFitnessMonthChange = findViewById(R.id.sbFitnessMonthChange);
        tvBiosMonthChangeVal = findViewById(R.id.tvBiosMonthChangeVal);
        tvHeartMonthChangeVal = findViewById(R.id.tvHeartMonthChangeVal);
        tvFitnessMonthChangeVal = findViewById(R.id.tvFitnessMonthChangeVal);

        // 三、年度对比
        sbBiosYear = findViewById(R.id.sbBiosYear);
        sbHeartYear = findViewById(R.id.sbHeartYear);
        sbFitnessYear = findViewById(R.id.sbFitnessYear);
        tvBiosYearVal = findViewById(R.id.tvBiosYearVal);
        tvHeartYearVal = findViewById(R.id.tvHeartYearVal);
        tvFitnessYearVal = findViewById(R.id.tvFitnessYearVal);
        spBiosYearStatus = findViewById(R.id.spBiosYearStatus);
        spHeartYearStatus = findViewById(R.id.spHeartYearStatus);
        spFitnessYearStatus = findViewById(R.id.spFitnessYearStatus);
        sbBiosYearChange = findViewById(R.id.sbBiosYearChange);
        sbHeartYearChange = findViewById(R.id.sbHeartYearChange);
        sbFitnessYearChange = findViewById(R.id.sbFitnessYearChange);
        tvBiosYearChangeVal = findViewById(R.id.tvBiosYearChangeVal);
        tvHeartYearChangeVal = findViewById(R.id.tvHeartYearChangeVal);
        tvFitnessYearChangeVal = findViewById(R.id.tvFitnessYearChangeVal);

        // 四、三大慢病总体
        sbCardioRisk = findViewById(R.id.sbCardioRisk);
        sbDementiaRisk = findViewById(R.id.sbDementiaRisk);
        sbDiabetesRisk = findViewById(R.id.sbDiabetesRisk);
        tvCardioRiskVal = findViewById(R.id.tvCardioRiskVal);
        tvDementiaRiskVal = findViewById(R.id.tvDementiaRiskVal);
        tvDiabetesRiskVal = findViewById(R.id.tvDiabetesRiskVal);
        spCardioRiskStatus = findViewById(R.id.spCardioRiskStatus);
        spDementiaRiskStatus = findViewById(R.id.spDementiaRiskStatus);
        spDiabetesRiskStatus = findViewById(R.id.spDiabetesRiskStatus);
        sbCardioRiskChange = findViewById(R.id.sbCardioRiskChange);
        sbDementiaRiskChange = findViewById(R.id.sbDementiaRiskChange);
        sbDiabetesRiskChange = findViewById(R.id.sbDiabetesRiskChange);
        tvCardioRiskChangeVal = findViewById(R.id.tvCardioRiskChangeVal);
        tvDementiaRiskChangeVal = findViewById(R.id.tvDementiaRiskChangeVal);
        tvDiabetesRiskChangeVal = findViewById(R.id.tvDiabetesRiskChangeVal);

        // 五、心血管风险细化 (包含了风险值、状态、变化值全量绑定)
        sbHeartAttackRisk = findViewById(R.id.sbHeartAttackRisk);
        sbStrokeRisk = findViewById(R.id.sbStrokeRisk);
        sbHeartFailureRisk = findViewById(R.id.sbHeartFailureRisk);
        tvHeartAttackRiskVal = findViewById(R.id.tvHeartAttackRiskVal);
        tvStrokeRiskVal = findViewById(R.id.tvStrokeRiskVal);
        tvHeartFailureRiskVal = findViewById(R.id.tvHeartFailureRiskVal);
        spHeartAttackRiskStatus = findViewById(R.id.spHeartAttackRiskStatus);
        spStrokeRiskStatus = findViewById(R.id.spStrokeRiskStatus);
        spHeartFailureRiskStatus = findViewById(R.id.spHeartFailureRiskStatus);

        sbHeartAttackChange = findViewById(R.id.sbHeartAttackChange);     //🔧🛠️
        sbStrokeChange = findViewById(R.id.sbStrokeChange);
        sbHeartFailureChange = findViewById(R.id.sbHeartFailureChange);
        tvHeartAttackChangeVal = findViewById(R.id.tvHeartAttackChangeVal);
        tvStrokeChangeVal = findViewById(R.id.tvStrokeChangeVal);
        tvHeartFailureChangeVal = findViewById(R.id.tvHeartFailureChangeVal);

        // 六、生活质量与机能损伤风险
        sbMemoryRisk = findViewById(R.id.sbMemoryRisk);
        spMemoryStatus = findViewById(R.id.spMemoryStatus);
        sbMemoryChange = findViewById(R.id.sbMemoryChange);
        tvMemoryRiskVal = findViewById(R.id.tvMemoryRiskVal);
        tvMemoryChangeVal = findViewById(R.id.tvMemoryChangeVal);

        sbFallRisk = findViewById(R.id.sbFallRisk);
        spFallStatus = findViewById(R.id.spFallStatus);
        sbFallChange = findViewById(R.id.sbFallChange);
        tvFallRiskVal = findViewById(R.id.tvFallRiskVal);
        tvFallChangeVal = findViewById(R.id.tvFallChangeVal);

        sbIndependenceRisk = findViewById(R.id.sbIndependenceRisk);
        spIndependenceStatus = findViewById(R.id.spIndependenceStatus);
        sbIndependenceChange = findViewById(R.id.sbIndependenceChange);
        tvIndependenceRiskVal = findViewById(R.id.tvIndependenceRiskVal);
        tvIndependenceChangeVal = findViewById(R.id.tvIndependenceChangeVal);

        // 七、糖尿病专项并发症风险
        sbKidneyRisk = findViewById(R.id.sbKidneyRisk);
        spKidneyStatus = findViewById(R.id.spKidneyStatus);
        sbKidneyChange = findViewById(R.id.sbKidneyChange);
        tvKidneyRiskVal = findViewById(R.id.tvKidneyRiskVal);
        tvKidneyChangeVal = findViewById(R.id.tvKidneyChangeVal);

        sbNerveRisk = findViewById(R.id.sbNerveRisk);
        spNerveStatus = findViewById(R.id.spNerveStatus);
        sbNerveChange = findViewById(R.id.sbNerveChange);
        tvNerveRiskVal = findViewById(R.id.tvNerveRiskVal);
        tvNerveChangeVal = findViewById(R.id.tvNerveChangeVal);

        sbVisionRisk = findViewById(R.id.sbVisionRisk);
        spVisionStatus = findViewById(R.id.spVisionStatus);
        sbVisionChange = findViewById(R.id.sbVisionChange);
        tvVisionRiskVal = findViewById(R.id.tvVisionRiskVal);
        tvVisionChangeVal = findViewById(R.id.tvVisionChangeVal);

        // 八、整体健康评估摘要
        sbNutritionScore = findViewById(R.id.sbNutritionScore);
        tvNutritionScoreVal = findViewById(R.id.tvNutritionScoreVal);
        sbTargetScore = findViewById(R.id.sbTargetScore);
        tvTargetScoreVal = findViewById(R.id.tvTargetScoreVal);

        //设备手动测量
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
        String[] complianceType = {
                EQH15ComplianceType.BIOS_AGE.getDes(),
                EQH15ComplianceType.HEART_AGE.getDes(),
                EQH15ComplianceType.FITNESS_AGE.getDes(),
                EQH15ComplianceType.STEPS_GOAL.getDes(),
                EQH15ComplianceType.NUTRITION_GOAL.getDes(),
                EQH15ComplianceType.ALL_GOALS_ACHIEVED.getDes(),
                EQH15ComplianceType.NEW_FITNESS_GOAL.getDes(),
        };
        initSP(complianceType, spComplianceType);

        String[] statusList = new String[]{"↑ 增加", "→ 稳定", "↓ 减少"};

        Spinner[] allStatusSpinners = {
                spBiosAgeStatus, spHeartAgeStatus, spFitnessAgeStatus,
                spBiosMonthStatus, spHeartMonthStatus, spFitnessMonthStatus,
                spBiosYearStatus, spHeartYearStatus, spFitnessYearStatus,
                spCardioRiskStatus, spDementiaRiskStatus, spDiabetesRiskStatus,
                spHeartAttackRiskStatus, spStrokeRiskStatus, spHeartFailureRiskStatus,
                spMemoryStatus, spFallStatus, spIndependenceStatus,
                spKidneyStatus, spNerveStatus, spVisionStatus
        };
        for (Spinner spinner : allStatusSpinners) {
            initSP(statusList, spinner);
            spinner.setSelection(1); // 默认 0x01 稳定
        }

        etDataTimestamp.setText(String.valueOf(System.currentTimeMillis() / 1000));
        setupSeekBarListeners();

        btnDatePicker.setText(TimeUtils.getCurrentDateStr());
        btnTimePicker.setText("00:00:00");
    }

    private void initSP(String[] data, Spinner spinner) {
        if (data == null || data.length == 0 || spinner == null) return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupSeekBarListeners() {
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int id = seekBar.getId();
                // 一、当前年龄与变化
                if (id == R.id.sbBiosAge) tvBiosAgeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbHeartAge) tvHeartAgeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFitnessAge) tvFitnessAgeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbBiosAgeChange) tvBiosAgeChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbHeartAgeChange) tvHeartAgeChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFitnessAgeChange) tvFitnessAgeChangeVal.setText(String.valueOf(progress));

                    // 三、历史月度年龄与变化
                else if (id == R.id.sbBiosMonth) tvBiosMonthVal.setText(String.valueOf(progress));
                else if (id == R.id.sbHeartMonth) tvHeartMonthVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFitnessMonth) tvFitnessMonthVal.setText(String.valueOf(progress));
                else if (id == R.id.sbBiosMonthChange) tvBiosMonthChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbHeartMonthChange) tvHeartMonthChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFitnessMonthChange) tvFitnessMonthChangeVal.setText(String.valueOf(progress));

                    // 三、历史年度年龄与变化
                else if (id == R.id.sbBiosYear) tvBiosYearVal.setText(String.valueOf(progress));
                else if (id == R.id.sbHeartYear) tvHeartYearVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFitnessYear) tvFitnessYearVal.setText(String.valueOf(progress));
                else if (id == R.id.sbBiosYearChange) tvBiosYearChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbHeartYearChange) tvHeartYearChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFitnessYearChange) tvFitnessYearChangeVal.setText(String.valueOf(progress));

                    // 四、三大慢病风险总体与变化
                else if (id == R.id.sbCardioRisk) tvCardioRiskVal.setText(progress + "%");
                else if (id == R.id.sbDementiaRisk) tvDementiaRiskVal.setText(progress + "%");
                else if (id == R.id.sbDiabetesRisk) tvDiabetesRiskVal.setText(progress + "%");
                else if (id == R.id.sbCardioRiskChange) tvCardioRiskChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbDementiaRiskChange) tvDementiaRiskChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbDiabetesRiskChange) tvDiabetesRiskChangeVal.setText(String.valueOf(progress));

                    // 五、心血管风险细化项与变化值
                else if (id == R.id.sbHeartAttackRisk) tvHeartAttackRiskVal.setText(progress + "%");
                else if (id == R.id.sbStrokeRisk) tvStrokeRiskVal.setText(progress + "%");
                else if (id == R.id.sbHeartFailureRisk) tvHeartFailureRiskVal.setText(progress + "%");
                else if (id == R.id.sbHeartAttackChange) tvHeartAttackChangeVal.setText(progress + "%");   // 🛠️ 补齐 UI 刷新逻辑
                else if (id == R.id.sbStrokeChange) tvStrokeChangeVal.setText(progress + "%");             // 🛠️ 补齐 UI 刷新逻辑
                else if (id == R.id.sbHeartFailureChange) tvHeartFailureChangeVal.setText(progress + "%"); // 🛠️ 补齐 UI 刷新逻辑

                    // 六、生活质量与机能损伤风险
                else if (id == R.id.sbMemoryRisk) tvMemoryRiskVal.setText(progress + "%");
                else if (id == R.id.sbMemoryChange) tvMemoryChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbFallRisk) tvFallRiskVal.setText(progress + "%");
                else if (id == R.id.sbFallChange) tvFallChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbIndependenceRisk) tvIndependenceRiskVal.setText(progress + "%");
                else if (id == R.id.sbIndependenceChange) tvIndependenceChangeVal.setText(String.valueOf(progress));

                    // 七、糖尿病细化并发症
                else if (id == R.id.sbKidneyRisk) tvKidneyRiskVal.setText(progress + "%");
                else if (id == R.id.sbKidneyChange) tvKidneyChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbNerveRisk) tvNerveRiskVal.setText(progress + "%");
                else if (id == R.id.sbNerveChange) tvNerveChangeVal.setText(String.valueOf(progress));
                else if (id == R.id.sbVisionRisk) tvVisionRiskVal.setText(progress + "%");
                else if (id == R.id.sbVisionChange) tvVisionChangeVal.setText(String.valueOf(progress));

                    // 八、整体评分
                else if (id == R.id.sbNutritionScore) tvNutritionScoreVal.setText(progress + "%");
                else if (id == R.id.sbTargetScore) tvTargetScoreVal.setText(progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        // 统一注册全量 42 项滑块监听 (39 + 3项补齐的心血管细化变化值滑块)
        int[] seekBarIds = {
                R.id.sbBiosAge, R.id.sbHeartAge, R.id.sbFitnessAge,
                R.id.sbBiosAgeChange, R.id.sbHeartAgeChange, R.id.sbFitnessAgeChange,
                R.id.sbBiosMonth, R.id.sbHeartMonth, R.id.sbFitnessMonth,
                R.id.sbBiosMonthChange, R.id.sbHeartMonthChange, R.id.sbFitnessMonthChange,
                R.id.sbBiosYear, R.id.sbHeartYear, R.id.sbFitnessYear,
                R.id.sbBiosYearChange, R.id.sbHeartYearChange, R.id.sbFitnessYearChange,
                R.id.sbCardioRisk, R.id.sbDementiaRisk, R.id.sbDiabetesRisk,
                R.id.sbCardioRiskChange, R.id.sbDementiaRiskChange, R.id.sbDiabetesRiskChange,
                R.id.sbHeartAttackRisk, R.id.sbStrokeRisk, R.id.sbHeartFailureRisk,
                R.id.sbHeartAttackChange, R.id.sbStrokeChange, R.id.sbHeartFailureChange, // 🛠️ 补齐数组监听
                R.id.sbMemoryRisk, R.id.sbMemoryChange, R.id.sbFallRisk, R.id.sbFallChange, R.id.sbIndependenceRisk, R.id.sbIndependenceChange,
                R.id.sbKidneyRisk, R.id.sbKidneyChange, R.id.sbNerveRisk, R.id.sbNerveChange, R.id.sbVisionRisk, R.id.sbVisionChange,
                R.id.sbNutritionScore, R.id.sbTargetScore
        };
        for (int id : seekBarIds) {
            View sbView = findViewById(id);
            if (sbView instanceof SeekBar) {
                ((SeekBar) sbView).setOnSeekBarChangeListener(seekBarChangeListener);
            }
        }
    }

    @Override
    public void initEvent() {
        btnSettingComplianceEvent.setOnClickListener(this);
        btnGetSteps.setOnClickListener(this);
        btnGetRefreshTimestamp.setOnClickListener(this);
        btnSettingHealthData.setOnClickListener(this);
        btnGenerateTimestamp.setOnClickListener(this);
        btnGetDeviceManualTestData.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnGenerateTimestamp) {
            etDataTimestamp.setText(String.valueOf(System.currentTimeMillis() / 1000));
        } else if (id == R.id.btnSettingHealthData) {
            setWriteCmdTAG(0);
            QH15HealthData healthData = buildHealthDataFromUi();
            ccvSettingPSAIMHealthData.appendBlueMiddleText("▶ 正在发送健康数据...");
            vpBleManager.setQH15HealthData(healthData, defaultResponse, this);
        } else if (id == R.id.btnSettingComplianceEvent) {
            int position = spComplianceType.getSelectedItemPosition();
            EQH15ComplianceType type = EQH15ComplianceType.Companion.getTypeWithTag(position);
            setWriteCmdTAG(1);
            ccvSettingComplianceEvent.appendBlueMiddleText("▶ 正在发送达标事件: " + type.getDes());
            vpBleManager.setQH15ComplianceEvent(type, defaultResponse, this);
        } else if (id == R.id.btnGetSteps) {
            setWriteCmdTAG(2);
            ccvGetRefreshTimeAndSteps.appendBlueMiddleText("▶ 正在读取实时步数...");
            vpBleManager.readQH15StepData(defaultResponse, this);
        } else if (id == R.id.btnGetRefreshTimestamp) {
            setWriteCmdTAG(3);
            ccvGetRefreshTimeAndSteps.appendBlueMiddleText("▶ 正在读取健康数据最近时间戳...");
            vpBleManager.getQH15HealthDataTimestamp(defaultResponse, this);
        } else if (id == R.id.btnGetDeviceManualTestData) {
            getDeviceManualTestData();
        } else if (id == R.id.btnDatePicker) {
            showDatePicker();
        } else if (id == R.id.btnTimePicker) {
            showTimePicker();
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
        setWriteCmdTAG(4);
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

    /**
     * 核心转换：提取全量协议指标并完整注入实体模型（已严格对齐 Kotlin PSAIMHealthData 属性名）
     */
    private QH15HealthData buildHealthDataFromUi() {
        QH15HealthData data = new QH15HealthData();

        // [0x10 - 0x12] 3.1 年龄类参数
        data.setBiosAge(sbBiosAge.getProgress());
        data.setHeartAge(sbHeartAge.getProgress());
        data.setFitnessAge(sbFitnessAge.getProgress());

        // [0x13 - 0x15] 3.2 年龄状态
        data.setBiosAgeStatus(getHealthStatusByPosition(spBiosAgeStatus.getSelectedItemPosition()));
        data.setHeartAgeStatus(getHealthStatusByPosition(spHeartAgeStatus.getSelectedItemPosition()));
        data.setFitnessAgeStatus(getHealthStatusByPosition(spFitnessAgeStatus.getSelectedItemPosition()));

        // [0x16 - 0x18] 3.3 年龄变化值
        data.setBiosAgeChange(sbBiosAgeChange.getProgress());
        data.setHeartAgeChange(sbHeartAgeChange.getProgress());
        data.setFitnessAgeChange(sbFitnessAgeChange.getProgress());

        // [0x19 - 0x1B] 3.4 90天趋势数据自动拟合生成
        int[] mockBiosTrend = new int[90];
        int[] mockHeartTrend = new int[90];
        int[] mockFitnessTrend = new int[90];
        int baseBios = sbBiosAge.getProgress();
        int baseHeart = sbHeartAge.getProgress();
        int baseFitness = sbFitnessAge.getProgress();
        for (int i = 0; i < 90; i++) {
            mockBiosTrend[i] = Math.max(0, Math.min(120, baseBios + (i % 5) - 2));
            mockHeartTrend[i] = Math.max(0, Math.min(120, baseHeart + (i % 4) - 2));
            mockFitnessTrend[i] = Math.max(0, Math.min(120, baseFitness + (i % 6) - 3));
        }
        data.setBiosAge90Days(mockBiosTrend);
        data.setHeartAge90Days(mockHeartTrend);
        data.setFitnessAge90Days(mockFitnessTrend);

        // [0x1C - 0x21] 3.5 上月/去年单点数据
        data.setBiosAgeLastMonth(sbBiosMonth.getProgress());
        data.setHeartAgeLastMonth(sbHeartMonth.getProgress());
        data.setFitnessAgeLastMonth(sbFitnessMonth.getProgress());
        data.setBiosAgeLastYear(sbBiosYear.getProgress());
        data.setHeartAgeLastYear(sbHeartYear.getProgress());
        data.setFitnessAgeLastYear(sbFitnessYear.getProgress());

        // [0x22 - 0x27] 3.6 上月/去年状态
        data.setBiosAgeLastMonthStatus(getHealthStatusByPosition(spBiosMonthStatus.getSelectedItemPosition()));
        data.setHeartAgeLastMonthStatus(getHealthStatusByPosition(spHeartMonthStatus.getSelectedItemPosition()));
        data.setFitnessAgeLastMonthStatus(getHealthStatusByPosition(spFitnessMonthStatus.getSelectedItemPosition()));
        data.setBiosAgeLastYearStatus(getHealthStatusByPosition(spBiosYearStatus.getSelectedItemPosition()));
        data.setHeartAgeLastYearStatus(getHealthStatusByPosition(spHeartYearStatus.getSelectedItemPosition()));
        data.setFitnessAgeLastYearStatus(getHealthStatusByPosition(spFitnessYearStatus.getSelectedItemPosition()));

        // [0x28 - 0x2D] 3.7 上月/去年变化值
        data.setBiosAgeLastMonthChange(sbBiosMonthChange.getProgress());
        data.setHeartAgeLastMonthChange(sbHeartMonthChange.getProgress());
        data.setFitnessAgeLastMonthChange(sbFitnessMonthChange.getProgress());
        data.setBiosAgeLastYearChange(sbBiosYearChange.getProgress());
        data.setHeartAgeLastYearChange(sbHeartYearChange.getProgress());
        data.setFitnessAgeLastYearChange(sbFitnessYearChange.getProgress());

        // [0x30 - 0x32] 3.8 三大慢病风险
        data.setCardiovascularRisk(sbCardioRisk.getProgress());
        data.setDementiaRisk(sbDementiaRisk.getProgress());
        data.setDiabetesRisk(sbDiabetesRisk.getProgress());

        // [0x33 - 0x35] 3.9 慢病风险状态
        data.setCardiovascularRiskStatus(getHealthStatusByPosition(spCardioRiskStatus.getSelectedItemPosition()));
        data.setDementiaRiskStatus(getHealthStatusByPosition(spDementiaRiskStatus.getSelectedItemPosition()));
        data.setDiabetesRiskStatus(getHealthStatusByPosition(spDiabetesRiskStatus.getSelectedItemPosition()));

        // [0x36 - 0x38] 3.10 慢病风险变化值
        data.setCardiovascularRiskChange(sbCardioRiskChange.getProgress());
        data.setDementiaRiskChange(sbDementiaRiskChange.getProgress());
        data.setDiabetesRiskChange(sbDiabetesRiskChange.getProgress());

        // [0x39 - 0x3B] 3.11 心血管细分风险
        data.setHeartAttackRisk(sbHeartAttackRisk.getProgress());
        data.setStrokeRisk(sbStrokeRisk.getProgress());
        data.setHeartFailureRisk(sbHeartFailureRisk.getProgress());

        // [0x3C - 0x3E] 3.12 心血管细分风险状态
        data.setHeartAttackRiskStatus(getHealthStatusByPosition(spHeartAttackRiskStatus.getSelectedItemPosition()));
        data.setStrokeRiskStatus(getHealthStatusByPosition(spStrokeRiskStatus.getSelectedItemPosition()));
        data.setHeartFailureRiskStatus(getHealthStatusByPosition(spHeartFailureRiskStatus.getSelectedItemPosition()));

        // [0x3F - 0x41] 3.13 心血管细分风险变化值
        data.setHeartAttackRiskChange(sbHeartAttackChange.getProgress());
        data.setStrokeRiskChange(sbStrokeChange.getProgress());
        data.setHeartFailureRiskChange(sbHeartFailureChange.getProgress());

        // [0x42 - 0x44] 3.14 生活质量风险
        data.setMemoryLossRisk(sbMemoryRisk.getProgress());
        data.setFallInjuryRisk(sbFallRisk.getProgress());
        data.setLoseIndependentRisk(sbIndependenceRisk.getProgress());

        // [0x45 - 0x47] 3.15 生活质量风险状态
        data.setMemoryLossRiskStatus(getHealthStatusByPosition(spMemoryStatus.getSelectedItemPosition()));
        data.setFallInjuryRiskStatus(getHealthStatusByPosition(spFallStatus.getSelectedItemPosition()));
        data.setLoseIndependentRiskStatus(getHealthStatusByPosition(spIndependenceStatus.getSelectedItemPosition()));

        // [0x48 - 0x4A] 3.16 生活质量风险变化值
        data.setMemoryLossRiskChange(sbMemoryChange.getProgress());
        data.setFallInjuryRiskChange(sbFallChange.getProgress());
        data.setLoseIndependentRiskChange(sbIndependenceChange.getProgress());

        // [0x4B - 0x4D] 3.17 糖尿病并发症风险
        data.setKidneyDiseaseRisk(sbKidneyRisk.getProgress());
        data.setNerveDamageRisk(sbNerveRisk.getProgress());
        data.setVisionLossRisk(sbVisionRisk.getProgress());

        // [0x4E - 0x50] 3.18 糖尿病并发症风险状态
        data.setKidneyDiseaseRiskStatus(getHealthStatusByPosition(spKidneyStatus.getSelectedItemPosition()));
        data.setNerveDamageRiskStatus(getHealthStatusByPosition(spNerveStatus.getSelectedItemPosition()));
        data.setVisionLossRiskStatus(getHealthStatusByPosition(spVisionStatus.getSelectedItemPosition()));

        // [0x51 - 0x53] 3.19 糖尿病并发症风险变化值
        data.setKidneyDiseaseRiskChange(sbKidneyChange.getProgress());
        data.setNerveDamageRiskChange(sbNerveChange.getProgress());
        data.setVisionLossRiskChange(sbVisionChange.getProgress());

        // [0x54 - 0x55] 3.20 健康综合指数
        data.setNutritionStatus(sbNutritionScore.getProgress());
        data.setGoalStatus(sbTargetScore.getProgress());

        // [0xA1] 3.21 数据时间戳
        try {
            data.setTimestamp(Integer.parseInt(etDataTimestamp.getText().toString().trim()));
        } catch (NumberFormatException e) {
            data.setTimestamp((int) (System.currentTimeMillis() / 1000));
        }

        return data;
    }

    private HealthStatus getHealthStatusByPosition(int position) {
        if (position == 0) return HealthStatus.INCREASE;
        if (position == 2) return HealthStatus.DECREASE;
        return HealthStatus.STABLE;
    }

    @Override
    public void onStepReport(long timestamp, int step, int id) {
        ccvGetRefreshTimeAndSteps.appendOrangeText("▶ [实时步数上报]");
        ccvGetRefreshTimeAndSteps.appendResult("时间: " + TimeUtils.secondsTimestampFormat(timestamp) + " | 步数: " + step + " 步 | 设备ID: " + id);
    }

    @Override
    public void onLastHealthDataTimestampRead(long timestamp) {
        ccvGetRefreshTimeAndSteps.appendOrangeText("▶ [上次健康数据时间戳]");
        ccvGetRefreshTimeAndSteps.appendResult("时间戳: " + timestamp +" >> " + TimeUtils.secondsTimestampFormat(timestamp));
    }

    @Override
    public void onDataSettingSuccess() {
        ccvSettingPSAIMHealthData.appendOrangeText("✅️ [PSAIM] 数据发送成功！");
    }

    @Override
    public void onDataSettingFailed() {
        ccvSettingPSAIMHealthData.appendRedLargeText("❌ [PSAIM] 数据发送失败！");
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

    private String timeFormat(int timestamp) {
        return TimeUtils.secondsTimestampFormat(timestamp);
    }

    @Override
    public void onCMDWriteFailed(int cmdTag) {
        super.onCMDWriteFailed(cmdTag);
        switch (cmdTag) {
            case 0: ccvSettingPSAIMHealthData.appendRedLargeText("❌ [健康数据] 读取指令写入失败！"); break;
            case 1: ccvSettingComplianceEvent.appendRedLargeText("❌ [达标指令] 读取指令写入失败！"); break;
            case 2: ccvGetRefreshTimeAndSteps.appendRedLargeText("❌ [获取计步] 读取指令写入失败！"); break;
            case 3: ccvGetRefreshTimeAndSteps.appendRedLargeText("❌ [获取时间戳] 读取指令写入失败！"); break;
            case 4: ccvGetDeviceManualTestData.appendRedLargeText("❌ [手动测量数据] 读取指令写入失败！"); break;
        }
    }

    @Override
    public void onCMDWriteSuccess(int cmdTag) {
        super.onCMDWriteSuccess(cmdTag);
        switch (cmdTag) {
            case 0: ccvSettingPSAIMHealthData.appendBlueMiddleText("✅️ [健康数据] 读取指令写入成功！"); break;
            case 1: ccvSettingComplianceEvent.appendWhiteText("✅️ [达标设置] 读取指令写入成功！"); break;
            case 2: ccvGetRefreshTimeAndSteps.appendWhiteText("✅️ [获取计步] 读取指令写入成功！"); break;
            case 3: ccvGetRefreshTimeAndSteps.appendWhiteText("✅️ [获取时间戳] 读取指令写入成功！"); break;
            case 4: ccvGetDeviceManualTestData.appendWhiteText("✅️ [手动测量数据] 读取指令写入成功！"); break;
        }
    }
}