package com.timaimee.vpdemo.activity.v2.health;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.timaimee.vpdemo.utils.CollapseCardLogView;
import com.timaimee.vpdemo.utils.TimeUtils;
import com.veepoo.protocol.listener.data.IRemindEventListener;
import com.veepoo.protocol.model.datas.RemindEvent;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.ERemindEvent;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindEventActivity extends BaseVPBLETestActivity implements IRemindEventListener {
    CollapseCardLogView ccvRemindEvent;
    Spinner spFun;
    Button btnReadRemindEvent, btnSendAddEvent, btnDatePicker, btnTimePicker;

    @Override
    public int getLayoutID() {
        return R.layout.activity_remind_event;
    }

    @Override
    public String pageTitle() {
        return "设备手动测量数据";
    }

    @Override
    public void initView() {
        ccvRemindEvent = findViewById(R.id.ccvRemindEvent);
        btnReadRemindEvent = findViewById(R.id.btnReadRemindEvent);
        btnSendAddEvent = findViewById(R.id.btnSendAddEvent);
        spFun = findViewById(R.id.spFun);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
    }

    @Override
    public void initData() {
        btnDatePicker.setText(TimeUtils.getCurrentDateStr());
        btnTimePicker.setText("00:00:00");
        List<String> data = new ArrayList<>();
        for (ERemindEvent entry : ERemindEvent.getEntries()) {
            data.add(entry.getDes());
        }
        initSP(data, spFun);
    }

    private void initSP(List<String> data, Spinner spinner) {
        if (data == null || data.isEmpty()) return;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
        vpBleManager.setReminderEventReportListener(this);
        btnReadRemindEvent.setOnClickListener(this);
        btnSendAddEvent.setOnClickListener(this);
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
        } else if (id == R.id.btnReadRemindEvent) {
            setBtnReadRemindEvent();
        } else if (id == R.id.btnSendAddEvent) {
            vpBleManager.sendCustomCMD(new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0D, 0x0D});
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

    private void setBtnReadRemindEvent() {
        long startReadTimestamp = getSelectedEpochSecond(btnDatePicker.getText().toString(), btnTimePicker.getText().toString());
        vpBleManager.readHistoricalDataReminderEvents(defaultResponse,
                ERemindEvent.Companion.getERemindEvent(spFun.getSelectedItemPosition()), startReadTimestamp);
    }

    @Override
    public void onCMDWriteFailed(int cmdTag) {
        super.onCMDWriteFailed(cmdTag);
        ccvRemindEvent.appendRedLargeText("❌ [手动测量数据] 读取指令写入失败！");
    }

    @Override
    public void onCMDWriteSuccess(int cmdTag) {
        super.onCMDWriteSuccess(cmdTag);
        ccvRemindEvent.appendOrangeText("✅️ [手动测量数据] 读取指令写入成功！");
    }

    private String timeFormat(int timestamp) {
        return TimeUtils.secondsTimestampFormat(timestamp);
    }

    @Override
    public void onRemindEventRead(@NotNull ArrayList<@NotNull RemindEvent> data) {
        ccvRemindEvent.appendResult("✅️健康提醒读取成功");
        if (data.isEmpty()) {
            ccvRemindEvent.appendResult("⚠️当前暂无提醒事件");
        }
        for (RemindEvent datum : data) {
            ccvRemindEvent.appendResult(">>> " + datum.getRemindEvent().getDes() + ", ⌚="+getTimeStr(datum.getTimestamp()));
        }
    }

    private String getTimeStr(long timestampSeconds) {
        return new TimeData((timestampSeconds)).getDateAndClock4GBandDb();
    }

    @Override
    public void onRemindEventReport(@NotNull ArrayList<@NotNull RemindEvent> data) {
        ccvRemindEvent.appendResult("✅️健康提醒上报成功");
        for (RemindEvent datum : data) {
            ccvRemindEvent.appendResult(">>> " + datum.getRemindEvent().getDes() + ", ⌚="+getTimeStr(datum.getTimestamp()));
        }
    }

}
