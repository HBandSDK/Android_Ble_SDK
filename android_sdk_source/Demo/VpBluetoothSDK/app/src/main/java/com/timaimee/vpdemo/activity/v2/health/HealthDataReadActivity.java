package com.timaimee.vpdemo.activity.v2.health;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.timaimee.vpdemo.bean.MyDeviceInfo;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.settings.ReadOriginSetting;

import java.util.Arrays;
import java.util.List;

public class HealthDataReadActivity extends BaseActivity implements IOriginData3Listener {
    private static final String TAG = "-健康数据读取-";

    /* 折叠布局 */
    LinearLayout layoutTitle1, layoutTitle2, layoutTitle3, layoutTitle4;
    LinearLayout layoutContent1, layoutContent2, layoutContent3, layoutContent4;
    ImageView ivArrow1, ivArrow2, ivArrow3, ivArrow4;

    // 互斥展开
    private int currentExpandedIndex = 0;

    /* 自定义读取 */
    Spinner spCustomReadFromDay, spCustomReadFromPosition;
    RadioGroup rgIsReadOneDay;
    Button btnCustomRead;
    /* 读取所有 */
    Button btnReadAll;
    /* 从什么位置开始读 */
    Spinner spReadFromWitchDay, spReadFromPosition;
    Button btnReadFrom;
    /* 只读一天 */
    Spinner spWhichDay, spStartFromOneDayPosition;
    Button btnReadOneDay;


    private TextView tvInfo, tvReadState;
    private ScrollView svInfo;
    private int watchDay = 3;

    @Override
    public int getLayoutID() {
        return R.layout.activity_health_data_read;
    }

    @Override
    public String pageTitle() {
        return "健康（五分钟测量）数据";
    }

    @Override
    public void initView() {
        tvInfo = findViewById(R.id.tvInfo);
        tvReadState = findViewById(R.id.tvReadState);
        svInfo = findViewById(R.id.svInfo);

        layoutTitle1 = findViewById(R.id.layoutTitle1);
        layoutTitle2 = findViewById(R.id.layoutTitle2);
        layoutTitle3 = findViewById(R.id.layoutTitle3);
        layoutTitle4 = findViewById(R.id.layoutTitle4);

        layoutContent1 = findViewById(R.id.layoutContent1);
        layoutContent2 = findViewById(R.id.layoutContent2);
        layoutContent3 = findViewById(R.id.layoutContent3);
        layoutContent4 = findViewById(R.id.layoutContent4);

        ivArrow1 = findViewById(R.id.ivArrow1);
        ivArrow2 = findViewById(R.id.ivArrow2);
        ivArrow3 = findViewById(R.id.ivArrow3);
        ivArrow4 = findViewById(R.id.ivArrow4);

        spCustomReadFromDay = findViewById(R.id.spCustomReadFromDay);
        spCustomReadFromPosition = findViewById(R.id.spCustomReadFromPosition);
        rgIsReadOneDay = findViewById(R.id.rgIsReadOneDay);
        btnCustomRead = findViewById(R.id.btnCustomRead);

        btnReadAll = findViewById(R.id.btnReadAll);

        spReadFromWitchDay = findViewById(R.id.spReadFromWitchDay);
        spReadFromPosition = findViewById(R.id.spReadFromPosition);
        btnReadFrom = findViewById(R.id.btnReadFrom);

        spWhichDay = findViewById(R.id.spWhichDay);
        spStartFromOneDayPosition = findViewById(R.id.spStartFromOneDayPosition);
        btnReadOneDay = findViewById(R.id.btnReadOneDay);
    }

    @Override
    public void initData() {
        watchDay = MyDeviceInfo.INSTANCE.getWatchDataDay();
        Logger.t(TAG).e("设备支持存储天数：" + watchDay);

        String[] witchDays = {"今天", "昨天", "前天"};
        initSP(witchDays, spCustomReadFromDay);
        initPositionSP(spCustomReadFromPosition);
        rgIsReadOneDay.check(R.id.rbNo);

        initSP(witchDays, spReadFromWitchDay);
        initPositionSP(spReadFromPosition);

        initSP(witchDays, spWhichDay);
        initPositionSP(spStartFromOneDayPosition);

        // 默认展开第一个
        expandByIndex(0, true);
    }

    private void initPositionSP(Spinner spinner) {
        String[] positions = new String[288];
        for (int i = 1; i <= 288; i++) {
            positions[i - 1] = i + "   ";
        }
        initSP(positions, spinner);
    }

    private void initSP(String[] data, Spinner spinner) {
        if (data == null || data.length == 0) return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
        layoutTitle1.setOnClickListener(v -> clickToggle(0));
        layoutTitle2.setOnClickListener(v -> clickToggle(1));
        layoutTitle3.setOnClickListener(v -> clickToggle(2));
        layoutTitle4.setOnClickListener(v -> clickToggle(3));
        btnCustomRead.setOnClickListener(this);
        btnReadAll.setOnClickListener(this);
        btnReadFrom.setOnClickListener(this);
        btnReadOneDay.setOnClickListener(this);
    }

    /**
     * 联动切换：只展开一个
     */
    private void clickToggle(int index) {
        if (currentExpandedIndex == index) {
            // 关闭自己
            expandByIndex(index, false);
            currentExpandedIndex = -1;
        } else {
            // 关闭之前
            if (currentExpandedIndex != -1) {
                expandByIndex(currentExpandedIndex, false);
            }
            // 打开新的
            expandByIndex(index, true);
            currentExpandedIndex = index;
        }
    }

    /**
     * 核心：拉伸动画 + 箭头旋转
     */
    private void expandByIndex(int index, boolean expand) {
        LinearLayout content = null;
        ImageView arrow = null;

        switch (index) {
            case 0:
                content = layoutContent1;
                arrow = ivArrow1;
                break;
            case 1:
                content = layoutContent2;
                arrow = ivArrow2;
                break;
            case 2:
                content = layoutContent3;
                arrow = ivArrow3;
                break;
            case 3:
                content = layoutContent4;
                arrow = ivArrow4;
                break;
        }

        if (content == null) return;

        if (expand) {
            content.setVisibility(View.VISIBLE);
            animateExpand(content, arrow);
        } else {
            animateCollapse(content, arrow);
        }
    }

    /**
     * 展开动画（拉伸）
     */
    private void animateExpand(View view, ImageView arrow) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int targetHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) animation.getAnimatedValue();
            view.setLayoutParams(params);
        });
        animator.setDuration(250);
        animator.start();

        arrow.animate().rotation(90).setDuration(250).start();
    }

    /**
     * 收起动画（压缩）
     */
    private void animateCollapse(final View view, ImageView arrow) {
        int initialHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) animation.getAnimatedValue();
            view.setLayoutParams(params);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                view.setLayoutParams(params);
            }
        });
        animator.setDuration(250);
        animator.start();

        arrow.animate().rotation(0).setDuration(250).start();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        sb.setLength(0);
        tvInfo.setText("");
        if (id == R.id.btnCustomRead) {
            readCustomSetting();
        } else if (id == R.id.btnReadAll) {
            readAll();
        } else if (id == R.id.btnReadFrom) {
            readFrom();
        } else if (id == R.id.btnReadOneDay) {
            readOnlyOneDay();
        }
    }

    /**
     * 自定义读取
     */
    private void readCustomSetting() {
        int startReadDay = spCustomReadFromDay.getSelectedItemPosition();
        int position = spCustomReadFromPosition.getSelectedItemPosition() + 1;
        boolean isReadOnlyOneDay = rgIsReadOneDay.getCheckedRadioButtonId() == R.id.rbYes;
        ReadOriginSetting setting = new ReadOriginSetting(startReadDay, position, isReadOnlyOneDay, watchDay);
        vpBleManager.readOriginDataBySetting(defaultResponse, this, setting);
    }

    private void readAll(){
        vpBleManager.readOriginData(defaultResponse, this, watchDay);
    }

    private void readFrom(){
        int startReadDay = spReadFromWitchDay.getSelectedItemPosition();
        int position = spReadFromPosition.getSelectedItemPosition() + 1;
        vpBleManager.readOriginDataFromDay(defaultResponse,this, startReadDay, position, watchDay);
    }

    private void readOnlyOneDay(){
        int whichDay = spWhichDay.getSelectedItemPosition();
        int position = spStartFromOneDayPosition.getSelectedItemPosition() + 1;
        vpBleManager.readOriginDataSingleDay(defaultResponse,this, whichDay, position, watchDay);
    }


    // ---------------- 接口回调 ----------------
    private StringBuilder sb = new StringBuilder();

/**
 * Method to format and return a string containing origin data information
 * @param data3 OriginData3 object containing the data to be formatted
 * @return Formatted string containing package number, time, heart rate, steps, blood pressure values
 */
    private String getShortOriginDataInfo(OriginData3 data3){
        return  "["+data3.getPackageNumber()+"/"+data3.getAllPackage()+"] " + data3.getmTime().getDateAndClockForDb()
                + ": 心率=" + data3.getRateValue()
                + " , 步数=" + data3.getStepValue()
                + " , 高压=" + data3.getHighValue()
                + " , 低压=" + data3.getLowValue()
                + " , 运动状态版本=" + data3.getSportStatusVersion()
                + " , 运动状态=" + Arrays.toString(data3.getSportStatus());
    }

    @Override
    public void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList) {
        sb.append("=========[原始数据-START]=========");
        for (OriginData3 originData3 : originDataList) {
            sb.append("\n").append(getShortOriginDataInfo(originData3));
        }
        sb.append("=========[原始数据-E N D]=========");
        tvInfo.setText(sb.toString());
        svInfo.fullScroll(View.FOCUS_DOWN);
    }
    @Override
    public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {
        System.out.println("==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>");
        System.out.println("AllStep:" + originHalfHourData.getAllStep());
        System.out.println("HalfHourRateDataSize:" + originHalfHourData.getHalfHourRateDatas().size());
        System.out.println("HalfHourSportDataSize:" + originHalfHourData.getHalfHourSportDatas().size());
        System.out.println("HalfHourBpSize:" + originHalfHourData.getHalfHourBps().size());
        for (HalfHourSportData data : originHalfHourData.getHalfHourSportDatas()) {
            System.out.println("HalfHourSportData ==> " + data.toString());
        }
        System.out.println("==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>==>");
    }
    @Override
    public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {}
    @Override
    public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {}
    @Override
    public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
        Logger.t(TAG).e("-onReadOriginProgressDetail-: | day="+day + " date="+date + "["+currentPackage+"/"+allPackage+"] ");
        tvReadState.setText("读取进度："+(day == 0 ? "今天": day == 1 ? "昨天" : "前天" )+":" + date + "["+currentPackage+"/"+allPackage+"] ");
    }
    @Override
    public void onReadOriginProgress(float progress) {
        tvReadState.setText("读取进度："+(progress*100)+"%");
    }
    @Override
    public void onReadOriginComplete() {
        tvReadState.setText("读取完成");
    }
}