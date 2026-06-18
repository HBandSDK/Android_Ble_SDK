package com.timaimee.vpdemo.activity.v2.other;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.timaimee.vpdemo.adapter.NewAlarmAdapter;
import com.timaimee.vpdemo.utils.TimeUtils;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.enums.EMultiAlarmOprate;
import com.veepoo.protocol.model.settings.Alarm2Setting;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Author: YWX
 * Date: 2021/9/30 16:51
 * Description:
 */
public class NewAlarmActivity extends BaseVPBLETestActivity implements NewAlarmAdapter.OnNewAlarmToggleChangeListener {
    private static final String TAG = NewAlarmActivity.class.getSimpleName();
    SwipeRecyclerView mRecyclerView;
    NewAlarmAdapter mAdapter;
    List<Alarm2Setting> mSettings = new ArrayList<>();
    EditText etAlarmText;
    TextView tvAlarmText;
    Button btnAlarmTime, btnAlarmDate, btnAdd;
    CheckBox cbDay1, cbDay2, cbDay3, cbDay4, cbDay5, cbDay6, cbDay7;
    RadioGroup rgIsOpen;

    @Override
    public int getLayoutID() {
        return R.layout.activity_text_alarm;
    }

    @Override
    public String pageTitle() {
        return "新闹钟";
    }

    @Override
    public void initView() {
        initNewAlarmListView();
        tvAlarmText = findViewById(R.id.tvAlarmText);
        etAlarmText = findViewById(R.id.etAlarmText);
        btnAlarmTime = findViewById(R.id.btnAlarmTime);
        btnAlarmDate = findViewById(R.id.btnAlarmDate);
        btnAdd = findViewById(R.id.btnAdd);
        cbDay1 = findViewById(R.id.cbDay1);
        cbDay2 = findViewById(R.id.cbDay2);
        cbDay3 = findViewById(R.id.cbDay3);
        cbDay4 = findViewById(R.id.cbDay4);
        cbDay5 = findViewById(R.id.cbDay5);
        cbDay6 = findViewById(R.id.cbDay6);
        cbDay7 = findViewById(R.id.cbDay7);
        rgIsOpen = findViewById(R.id.rgIsOpen);
    }

    private void addCheckBoxListener(CheckBox... boxes) {
        for (CheckBox box : boxes) {
            box.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    btnAlarmDate.setEnabled(false);
                } else {
                    boolean hasCheck = false;
                    for (CheckBox checkBox : boxes) {
                        if(checkBox.isChecked()) {
                            hasCheck = true;
                            break;
                        }
                    }
                    btnAlarmDate.setEnabled(!hasCheck);
                }
            });
        }
    }

    @Override
    public void initData() {
        tvAlarmText.setVisibility(View.GONE);
        etAlarmText.setVisibility(View.GONE);
        readNewAlarm();
        btnAlarmDate.setText(TimeUtils.getCurrentDateStr());
        btnAlarmTime.setText(TimeUtils.getCurrentHourMinuteStr());
    }

    @Override
    public void initEvent() {
        btnAlarmTime.setOnClickListener(view -> showTimePicker(btnAlarmTime));
        btnAlarmDate.setOnClickListener(view -> showDatePicker(btnAlarmDate));
        addCheckBoxListener(cbDay1, cbDay2, cbDay3, cbDay4, cbDay5, cbDay6, cbDay7);
        btnAdd.setOnClickListener(v -> {
            Alarm2Setting setting = getAlarm2Setting();
            vpBleManager.addAlarm2(defaultResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    Logger.t(TAG).e("添加闹钟 --》" + alarmData2.toString());
                    EMultiAlarmOprate OPT = alarmData2.getOprate();
                    showMsg("添加闹钟 --》" + (alarmData2.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
                    if (OPT == EMultiAlarmOprate.ALARM_FULL) {
                        showMsg("闹钟已满（最多添加20个）");
                    } else if (OPT == EMultiAlarmOprate.SETTING_SUCCESS) {
                        showMsg("闹钟添加成功");
                        mSettings.clear();
                        mSettings.addAll(alarmData2.getAlarm2SettingList());
                        Collections.sort(mSettings);
                        mAdapter.notifyDataSetChanged();
                    } else if (OPT == EMultiAlarmOprate.SETTING_FAIL) {
                        showMsg("闹钟添加失败");
                    }
                }
            }, setting);
        });
    }

    private void showTimePicker(Button targetBtn) {
        String currentText = targetBtn.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        int defHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defMin = calendar.get(Calendar.MINUTE);
        if (currentText.contains(":")) {
            String[] parts = currentText.split(":");
            if (parts.length >= 2) {
                try {
                    defHour = Integer.parseInt(parts[0]);
                    defMin = Integer.parseInt(parts[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute);
            targetBtn.setText(selectedTime);
        }, defHour, defMin, true).show();
    }

    private void showDatePicker(Button targetBtn) {
        String currentText = targetBtn.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        int defYear = calendar.get(Calendar.YEAR);
        int defMonth = calendar.get(Calendar.MONTH); // 注意：Calendar 月份从 0 开始
        int defDay = calendar.get(Calendar.DAY_OF_MONTH);
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
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            targetBtn.setText(selectedDate);

        }, defYear, defMonth, defDay).show();
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private final SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, position) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // 添加左侧的，如果不添加，则左侧不会出现菜单。
        {
            SwipeMenuItem addItem = new SwipeMenuItem(NewAlarmActivity.this)
                    .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                    .setText("删除")
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(addItem);
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private final OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(NewAlarmActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                        .show();
                VPOperateManager.getInstance().deleteAlarm2(defaultResponse, new IAlarm2DataListListener() {
                    @Override
                    public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                        EMultiAlarmOprate OPT = alarmData2.getOprate();
                        if (OPT == EMultiAlarmOprate.CLEAR_SUCCESS) {
                            showMsg("闹钟删除成功");
                            mSettings.clear();
                            mSettings.addAll(alarmData2.getAlarm2SettingList());
                            Collections.sort(mSettings);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showMsg("删除失败");
                        }
                    }
                    //String bluetoothAddress, int alarmId, int alarmHour, int alarmMinute, String repeatStatus, int scene, String unRepeatDate, boolean isOpen
                }, mSettings.get(position));

            }
        }
    };

    private Alarm2Setting getAlarm2Setting() {
        Alarm2Setting setting = new Alarm2Setting();
        String alarmTimeStr = btnAlarmTime.getText().toString();
        String[] timeStrArr= alarmTimeStr.split(":");
        setting.setOpen(rgIsOpen.getCheckedRadioButtonId() == R.id.rbOpen);
        setting.setRepeatStatus(getRepeatState());
        setting.setUnRepeatDate(getUnRepeatDate());
        setting.setAlarmHour(Integer.parseInt(timeStrArr[0]));
        setting.setAlarmMinute(Integer.parseInt(timeStrArr[1]));
        return setting;
    }

    private String getRepeatState(){
        return (cbDay1.isChecked() ? "1" : "0") +
                (cbDay2.isChecked() ? "1" : "0") +
                (cbDay3.isChecked() ? "1" : "0") +
                (cbDay4.isChecked() ? "1" : "0") +
                (cbDay5.isChecked() ? "1" : "0") +
                (cbDay6.isChecked() ? "1" : "0") +
                (cbDay7.isChecked() ? "1" : "0");
    }

    public String getUnRepeatDate(){
        String repeatState = getRepeatState();
        if (repeatState.equals("0000000")) {
            return btnAlarmDate.getText().toString();
        } else {
            return "0000-00-00";
        }
    }

    private void initNewAlarmListView() {
        mSettings.clear();
        mRecyclerView = findViewById(R.id.rvTextAlarm);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NewAlarmAdapter(mSettings, this);
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.addItemDecoration(createItemDecoration());
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
    }

    private void readNewAlarm() {
        vpBleManager.readAlarm2(defaultResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                EMultiAlarmOprate OPT = alarmData2.getOprate();
                boolean isOk = OPT == EMultiAlarmOprate.READ_SUCCESS ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAME_CRC ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAVE;
                if (isOk) {
                    mSettings.clear();
                    mSettings.addAll(alarmData2.getAlarm2SettingList());
                    Collections.sort(mSettings);
                    mAdapter.notifyDataSetChanged();
                }
                showMsg(isOk ? "读取文字闹钟成功" + OPT : "读取文字闹钟失败" + OPT);
            }
        });
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onToggleChanged(Alarm2Setting setting) {
        vpBleManager.modifyAlarm2(defaultResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                showMsg("修改闹钟 --》" + (alarmData2.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
                mSettings.clear();
                mSettings.addAll(alarmData2.getAlarm2SettingList());
                Collections.sort(mSettings);
                mAdapter.notifyDataSetChanged();
            }
        }, setting);
    }
}
