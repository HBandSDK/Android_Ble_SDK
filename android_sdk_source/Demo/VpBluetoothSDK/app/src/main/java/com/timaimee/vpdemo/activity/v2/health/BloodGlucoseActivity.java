package com.timaimee.vpdemo.activity.v2.health;

import android.app.TimePickerDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.utils.CollapseCardView;
import com.veepoo.protocol.listener.data.IBloodGlucoseChangeListener;
import com.veepoo.protocol.model.datas.MealInfo;
import com.veepoo.protocol.model.enums.EBloodGlucoseRiskLevel;
import com.veepoo.protocol.model.enums.EBloodGlucoseStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BloodGlucoseActivity extends BaseActivity implements View.OnClickListener, IBloodGlucoseChangeListener{

    private EditText etBreakfastBefore, etBreakfastAfter, etDinnerBefore, etDinnerAfter, etLunchBefore, etLunchAfter, etBloodGlucoseCalibration;
    private Button btnBreakfastTimeBefore, btnBreakfastTimeAfter, btnDinnerTimeBefore, btnDinnerTimeAfter, btnLunchTimeBefore, btnLunchTimeAfter;
    private Button btnSettingBloodGlucosePrivate, btnReadBloodGlucosePrivate, btnSettingBloodGlucoseMultipleCalibration,
            btnReadBloodGlucoseMultipleCalibration, btnStartDetect, btnStopDetect;
    private RadioGroup rgIsOpen;
    TextView tvProcessInfo;

    // 折叠卡片（加互斥）
    private CollapseCardView ccvSettingBloodGlucoseCalibration, ccvSettingBloodGlucoseMultipleCalibration;

    // 默认配置
    private static final String[] defaultMealTime = {"08:00", "09:00", "12:00", "13:10", "18:00", "19:00"};
    private static final float[] defaultBloodGlucose = {5.5f, 7.5f, 5.0f, 6.5f, 5.0f, 6.5f};

    // 三个时段数据模型
    private MealInfo breakfastMeal, lunchMeal, dinnerMeal;

    @Override
    public int getLayoutID() {
        return R.layout.activity_blood_glucose;
    }

    @Override
    public String pageTitle() {
        return "血糖";
    }

    @Override
    public void initView() {
        // 初始化输入框
        etBreakfastBefore = findViewById(R.id.etBreakfastBefore);
        etBreakfastAfter = findViewById(R.id.etBreakfastAfter);
        etDinnerBefore = findViewById(R.id.etDinnerBefore);
        etDinnerAfter = findViewById(R.id.etDinnerAfter);
        etLunchBefore = findViewById(R.id.etLunchBefore);
        etLunchAfter = findViewById(R.id.etLunchAfter);

        btnBreakfastTimeBefore = findViewById(R.id.btnBreakfastTimeBefore);
        btnBreakfastTimeAfter = findViewById(R.id.btnBreakfastTimeAfter);
        btnDinnerTimeBefore = findViewById(R.id.btnDinnerTimeBefore);
        btnDinnerTimeAfter = findViewById(R.id.btnDinnerTimeAfter);
        btnLunchTimeBefore = findViewById(R.id.btnLunchTimeBefore);
        btnLunchTimeAfter = findViewById(R.id.btnLunchTimeAfter);

        etBloodGlucoseCalibration = findViewById(R.id.etBloodGlucoseCalibration);
        rgIsOpen = findViewById(R.id.rgIsOpen);

        tvProcessInfo = findViewById(R.id.tvDetectProcessStatus);

        // 初始化按钮
        btnSettingBloodGlucosePrivate = findViewById(R.id.btnSettingBloodGlucosePrivate);
        btnReadBloodGlucosePrivate = findViewById(R.id.btnReadBloodGlucosePrivate);
        btnSettingBloodGlucoseMultipleCalibration = findViewById(R.id.btnSettingBloodGlucoseMultipleCalibration);
        btnReadBloodGlucoseMultipleCalibration = findViewById(R.id.btnReadBloodGlucoseMultipleCalibration);
        btnStartDetect = findViewById(R.id.btnStartDetect);
        btnStopDetect = findViewById(R.id.btnStopDetect);

        ccvSettingBloodGlucoseCalibration = findViewById(R.id.ccvSettingPSAIMHealthData);
        ccvSettingBloodGlucoseMultipleCalibration = findViewById(R.id.ccvSettingComplianceEvent);

        // ==================== 限制输入类型：只允许数字 + 小数点 ====================
        etBreakfastBefore.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etBreakfastAfter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etDinnerBefore.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etDinnerAfter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etLunchBefore.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etLunchAfter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etBloodGlucoseCalibration.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    public boolean hasCommonMsgUI() {
        return true;
    }

    @Override
    public void initData() {
        // 初始化三个 MealInfo
        breakfastMeal = new MealInfo(1);
        lunchMeal = new MealInfo(2);
        dinnerMeal = new MealInfo(3);

        // 设置默认值
        setDefaultValues();
    }

    /**
     * 1. 设置默认时间 + 默认血糖值
     */
    private void setDefaultValues() {
        // 时间按钮默认值
        btnBreakfastTimeBefore.setText(defaultMealTime[0]);
        btnBreakfastTimeAfter.setText(defaultMealTime[1]);
        btnLunchTimeBefore.setText(defaultMealTime[2]);
        btnLunchTimeAfter.setText(defaultMealTime[3]);
        btnDinnerTimeBefore.setText(defaultMealTime[4]);
        btnDinnerTimeAfter.setText(defaultMealTime[5]);

        // 输入框默认值
        etBreakfastBefore.setText(String.valueOf(defaultBloodGlucose[0]));
        etBreakfastAfter.setText(String.valueOf(defaultBloodGlucose[1]));
        etLunchBefore.setText(String.valueOf(defaultBloodGlucose[2]));
        etLunchAfter.setText(String.valueOf(defaultBloodGlucose[3]));
        etDinnerBefore.setText(String.valueOf(defaultBloodGlucose[4]));
        etDinnerAfter.setText(String.valueOf(defaultBloodGlucose[5]));
    }

    @Override
    public void initEvent() {
        btnBreakfastTimeBefore.setOnClickListener(this);
        btnBreakfastTimeAfter.setOnClickListener(this);
        btnLunchTimeAfter.setOnClickListener(this);
        btnLunchTimeBefore.setOnClickListener(this);
        btnDinnerTimeAfter.setOnClickListener(this);
        btnDinnerTimeBefore.setOnClickListener(this);
        btnSettingBloodGlucosePrivate.setOnClickListener(this);
        btnReadBloodGlucosePrivate.setOnClickListener(this);
        btnSettingBloodGlucoseMultipleCalibration.setOnClickListener(this);
        btnReadBloodGlucoseMultipleCalibration.setOnClickListener(this);
        btnStartDetect.setOnClickListener(this);
        btnStartDetect.setOnClickListener(this);

        ccvSettingBloodGlucoseCalibration.setOnExpandStateChangeListener(new CollapseCardView.OnExpandStateChangeListener() {
            @Override
            public void onExpanded(CollapseCardView cardView) {
                if (ccvSettingBloodGlucoseMultipleCalibration.isExpanded()) {
                    ccvSettingBloodGlucoseMultipleCalibration.collapse();
                }
            }

            @Override
            public void onCollapsed(CollapseCardView cardView) {

            }
        });

        ccvSettingBloodGlucoseMultipleCalibration.setOnExpandStateChangeListener(new CollapseCardView.OnExpandStateChangeListener() {
            @Override
            public void onExpanded(CollapseCardView cardView) {
                if (ccvSettingBloodGlucoseCalibration.isExpanded()) {
                    ccvSettingBloodGlucoseCalibration.collapse();
                }
            }

            @Override
            public void onCollapsed(CollapseCardView cardView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnBreakfastTimeBefore) {
            showTimePicker(btnBreakfastTimeBefore);
        } else if (id == R.id.btnBreakfastTimeAfter) {
            showTimePicker(btnBreakfastTimeAfter);
        } else if (id == R.id.btnLunchTimeBefore) {
            showTimePicker(btnLunchTimeBefore);
        } else if (id == R.id.btnLunchTimeAfter) {
            showTimePicker(btnLunchTimeAfter);
        } else if (id == R.id.btnDinnerTimeBefore) {
            showTimePicker(btnDinnerTimeBefore);
        } else if (id == R.id.btnDinnerTimeAfter) {
            showTimePicker(btnDinnerTimeAfter);
        } else if (id == R.id.btnSettingBloodGlucosePrivate) {
            String value = etBloodGlucoseCalibration.getText().toString();
            if (TextUtils.isEmpty(value)) {
                showToast("请输入血糖私人校准值");
                return;
            }
            float privateCalibration = Float.parseFloat(value);
            vpBleManager.setBloodGlucoseAdjustingData(privateCalibration, true, defaultResponse,this);
        } else if (id == R.id.btnReadBloodGlucosePrivate) {
            vpBleManager.readBloodGlucoseAdjustingData(defaultResponse, this);
        } else if (id == R.id.btnSettingBloodGlucoseMultipleCalibration) {
            clearTestInfo();
            settingSettingBloodGlucoseMultipleCalibration();
        } else if (id == R.id.btnReadBloodGlucoseMultipleCalibration) {
            readBloodGlucoseMultipleCalibration();
        } else if (id == R.id.btnStartDetect) {
            clearTestInfo();
            vpBleManager.startBloodGlucoseDetect(defaultResponse, this);
        }  else if (id == R.id.btnStopDetect) {
            vpBleManager.stopBloodGlucoseDetect(defaultResponse, this);
        }
    }

    /**
     * 3. 时间选择弹框（你要的样式）
     */
    private void showTimePicker(Button targetBtn) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute);
            targetBtn.setText(time);
        }, currentHour, currentMin, true).show();
    }

    private void readBloodGlucoseMultipleCalibration() {
        clearTestInfo();
//        vpBleManager.readBloodComponentCalibration(defaultResponse, this);
    }

    /**
     * 2. 校验逻辑 + 4. 封装 MealInfo
     */
    private void settingSettingBloodGlucoseMultipleCalibration() {
        try {
            // 1. 获取界面值
            float bfBefore = Float.parseFloat(etBreakfastBefore.getText().toString());
            float bfAfter = Float.parseFloat(etBreakfastAfter.getText().toString());
            float lcBefore = Float.parseFloat(etLunchBefore.getText().toString());
            float lcAfter = Float.parseFloat(etLunchAfter.getText().toString());
            float dnBefore = Float.parseFloat(etDinnerBefore.getText().toString());
            float dnAfter = Float.parseFloat(etDinnerAfter.getText().toString());

            // 2. 时间转分钟
            int bfBTime = getTimeInMin(btnBreakfastTimeBefore.getText().toString());
            int bfATime = getTimeInMin(btnBreakfastTimeAfter.getText().toString());
            int lcBTime = getTimeInMin(btnLunchTimeBefore.getText().toString());
            int lcATime = getTimeInMin(btnLunchTimeAfter.getText().toString());
            int dnBTime = getTimeInMin(btnDinnerTimeBefore.getText().toString());
            int dnATime = getTimeInMin(btnDinnerTimeAfter.getText().toString());

            // 3. 封装数据
            breakfastMeal.setBgBeforeMeal(bfBefore);
            breakfastMeal.setBgAfterMeal(bfAfter);
            breakfastMeal.setBeforeMealTime(bfBTime);
            breakfastMeal.setAfterMealTime(bfATime);

            lunchMeal.setBgBeforeMeal(lcBefore);
            lunchMeal.setBgAfterMeal(lcAfter);
            lunchMeal.setBeforeMealTime(lcBTime);
            lunchMeal.setAfterMealTime(lcATime);

            dinnerMeal.setBgBeforeMeal(dnBefore);
            dinnerMeal.setBgAfterMeal(dnAfter);
            dinnerMeal.setBeforeMealTime(dnBTime);
            dinnerMeal.setAfterMealTime(dnATime);

            // 4. 校验规则
            if (!breakfastMeal.isTimeValid()) {
                Toast.makeText(this, "早餐时间：餐前不能晚于餐后！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!breakfastMeal.isBloodGlucoseValid()) {
                Toast.makeText(this, "早餐血糖：餐前必须小于餐后！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!lunchMeal.isTimeValid()) {
                Toast.makeText(this, "午餐时间：餐前不能晚于餐后！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!lunchMeal.isBloodGlucoseValid()) {
                Toast.makeText(this, "午餐血糖：餐前必须小于餐后！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dinnerMeal.isTimeValid()) {
                Toast.makeText(this, "晚餐时间：餐前不能晚于餐后！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!dinnerMeal.isBloodGlucoseValid()) {
                Toast.makeText(this, "晚餐血糖：餐前必须小于餐后！", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isOpen = rgIsOpen.getCheckedRadioButtonId() == R.id.rbOpen;
            vpBleManager.settingMultipleCalibrationBGValue(isOpen, breakfastMeal, lunchMeal, dinnerMeal, defaultResponse, this);
            Toast.makeText(this, "血糖多校准设置成功！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "请输入完整有效的数值！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 时间字符串 转 分钟
     */
    private int getTimeInMin(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
            Date date = sdf.parse(timeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onDetectError(int opt, EBloodGlucoseStatus status) {
//        tvProcessInfo.setText();
//        switch (heartData.getHeartStatus()) {
//            case STATE_INIT -> {
//                tvProcessInfo.setText("设备初始化");
//            }
//            case STATE_HEART_BUSY -> {
//                tvProcessInfo.setText("设备正忙");
//            }
//            case STATE_HEART_DETECT -> {
//                tvProcessInfo.setText("设备正在检测...");
//            }
//            case STATE_HEART_WEAR_ERROR -> {
//                tvProcessInfo.setText("佩戴不通过");
//            }
//            case STATE_LOW_BATTERY -> {
//                tvProcessInfo.setText("电池电量过低");
//            }
//            case STATE_HEART_NORMAL -> {
//                tvProcessInfo.setText("心率正在检测...");
//            }
//        }
//        appendBlueMiddleText("心率值：" + heartData.getData());
    }

    @Override
    public void onBloodGlucoseDetect(int progress, float bloodGlucose, EBloodGlucoseRiskLevel level) {

    }

    @Override
    public void onBloodGlucoseStopDetect() {

    }

    @Override
    public void onBloodGlucoseAdjustingSettingSuccess(boolean isOpen, float adjustingValue) {

    }

    @Override
    public void onBloodGlucoseAdjustingSettingFailed() {

    }

    @Override
    public void onBloodGlucoseAdjustingReadSuccess(boolean isOpen, float adjustingValue) {

    }

    @Override
    public void onBloodGlucoseAdjustingReadFailed() {

    }

    @Override
    public void onBGMultipleAdjustingReadSuccess(boolean isOpen, MealInfo breakfast, MealInfo lunch, MealInfo dinner) {

    }

    @Override
    public void onBGMultipleAdjustingReadFailed() {

    }

    @Override
    public void onBGMultipleAdjustingSettingSuccess() {

    }

    @Override
    public void onBGMultipleAdjustingSettingFailed() {

    }
}