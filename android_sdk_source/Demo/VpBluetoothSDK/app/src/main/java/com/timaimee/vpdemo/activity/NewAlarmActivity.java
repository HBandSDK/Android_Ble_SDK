package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.NewAlarmAdapter;
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
import java.util.List;
import java.util.Random;

/**
 * Author: YWX
 * Date: 2021/9/30 16:51
 * Description:
 */
public class NewAlarmActivity extends Activity implements NewAlarmAdapter.OnNewAlarmToggleChangeListener {
    private static final String TAG = NewAlarmActivity.class.getSimpleName();
    SwipeRecyclerView mRecyclerView;
    NewAlarmAdapter mAdapter;
    List<Alarm2Setting> mSettings = new ArrayList<>();
    EditText mEditText, etHour, etMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_alarm);
        initNewAlarmListView();
        readNewAlarm();
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm2Setting setting = getAlarm2Setting();
                VPOperateManager.getInstance().addAlarm2(writeResponse, new IAlarm2DataListListener() {
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
                            mAdapter.notifyDataSetChanged();
                        } else if (OPT == EMultiAlarmOprate.SETTING_FAIL) {
                            showMsg("闹钟添加失败");
                        }
                    }
                }, setting);
            }
        });
        mEditText = findViewById(R.id.et_content);
        mEditText.setVisibility(View.GONE);

        etHour = findViewById(R.id.et_hour);
        etMinute = findViewById(R.id.et_minute);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
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
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(NewAlarmActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                        .show();
                VPOperateManager.getInstance().deleteAlarm2(writeResponse, new IAlarm2DataListListener() {
                    @Override
                    public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                        EMultiAlarmOprate OPT = alarmData2.getOprate();
                        if (OPT == EMultiAlarmOprate.CLEAR_SUCCESS) {
                            showMsg("闹钟删除成功");
                            mSettings.clear();
                            mSettings.addAll(alarmData2.getAlarm2SettingList());
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
        String strHour = etHour.getText().toString();
        String strMinute = etMinute.getText().toString();
        Alarm2Setting setting = new Alarm2Setting();
        setting.setOpen(true);
        setting.setRepeatStatus("1111111");
        setting.setUnRepeatDate("0000-00-00");
        setting.setAlarmHour(TextUtils.isEmpty(strHour) ? new Random().nextInt(24) : Integer.parseInt(strHour));
        setting.setAlarmMinute(TextUtils.isEmpty(strMinute) ? new Random().nextInt(60) : Integer.parseInt(strMinute));
        return setting;
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
        VPOperateManager.getInstance().readAlarm2(writeResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                EMultiAlarmOprate OPT = alarmData2.getOprate();
                boolean isOk = OPT == EMultiAlarmOprate.READ_SUCCESS ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAME_CRC ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAVE;
                if (isOk) {
                    mSettings.clear();
                    mSettings.addAll(alarmData2.getAlarm2SettingList());
                    mAdapter.notifyDataSetChanged();
                }
                showMsg(isOk ? "读取文字闹钟成功" : "读取文字闹钟失败");
            }
        });

        VPOperateManager.getInstance().readAlarm2(writeResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                EMultiAlarmOprate OPT = alarmData2.getOprate();
                boolean isOk = OPT == EMultiAlarmOprate.READ_SUCCESS ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAME_CRC ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAVE;
                if (isOk) {
                    mSettings.clear();
                    mSettings.addAll(alarmData2.getAlarm2SettingList());
                    mAdapter.notifyDataSetChanged();
                }
                showMsg(isOk ? "读取文字闹钟成功" : "读取文字闹钟失败");
            }
        });
    }

    private IBleWriteResponse writeResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {

        }
    };

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onToggleChanged(Alarm2Setting setting) {
        VPOperateManager.getInstance().modifyAlarm2(writeResponse, new IAlarm2DataListListener() {
            @Override
            public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                showMsg("修改闹钟 --》" + (alarmData2.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
                mSettings.clear();
                mSettings.addAll(alarmData2.getAlarm2SettingList());
                mAdapter.notifyDataSetChanged();
            }
        }, setting);
    }
}
