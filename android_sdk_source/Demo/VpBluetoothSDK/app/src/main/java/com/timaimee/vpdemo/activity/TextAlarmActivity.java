package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.TextAlarmAdapter;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ITextAlarmDataListener;
import com.veepoo.protocol.model.datas.TextAlarmData;
import com.veepoo.protocol.model.enums.EMultiAlarmOprate;
import com.veepoo.protocol.model.settings.TextAlarm2Setting;
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
public class TextAlarmActivity extends Activity implements TextAlarmAdapter.OnTextAlarmToggleChangeListener {
    private static final String TAG = TextAlarmActivity.class.getSimpleName();
    SwipeRecyclerView mRecyclerView;
    TextAlarmAdapter mAdapter;
    List<TextAlarm2Setting> mSettings = new ArrayList<>();
    EditText mEditText,etHour,etMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_alarm);
        initTextAlarmListView();
        readTextAlarm();
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextAlarm2Setting setting = getTextAlarm2Setting();
                VPOperateManager.getMangerInstance(TextAlarmActivity.this).addTextAlarm(writeResponse, new ITextAlarmDataListener() {
                    @Override
                    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                        Logger.t(TAG).e("添加闹钟 --》" + textAlarmData.toString());
                        EMultiAlarmOprate OPT = textAlarmData.getOprate();
                        showMsg("添加闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
                        if (OPT == EMultiAlarmOprate.ALARM_FULL) {
                            showMsg("闹钟已满（最多添加十个）");
                        } else if (OPT == EMultiAlarmOprate.SETTING_SUCCESS) {
                            showMsg("闹钟添加成功");
                            mSettings.clear();
                            mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
                            mAdapter.notifyDataSetChanged();
                        } else if (OPT == EMultiAlarmOprate.SETTING_FAIL) {
                            showMsg("闹钟添加失败");
                        }
                    }
                }, setting);
            }
        });
        mEditText = findViewById(R.id.et_content);
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
                SwipeMenuItem addItem = new SwipeMenuItem(TextAlarmActivity.this)
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
                Toast.makeText(TextAlarmActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
                        .show();
                VPOperateManager.getMangerInstance(TextAlarmActivity.this).deleteTextAlarm(writeResponse, new ITextAlarmDataListener() {
                    @Override
                    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                        EMultiAlarmOprate OPT =  textAlarmData.getOprate();
                        if(OPT == EMultiAlarmOprate.CLEAR_SUCCESS) {
                            showMsg("闹钟删除成功");
                            mSettings.clear();
                            mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showMsg("删除失败");
                        }
                    }
                }, mSettings.get(position));

            }
        }
    };

    private TextAlarm2Setting getTextAlarm2Setting() {
        String content = mEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            content = "大郎，该吃药了 @^_^@ !";
        }
        String strHour = etHour.getText().toString();
        String strMinute = etMinute.getText().toString();
        TextAlarm2Setting setting = new TextAlarm2Setting();
        setting.setOpen(true);
        setting.setRepeatStatus("1111111");
        setting.setUnRepeatDate("0000-00-00");
        setting.setAlarmHour(TextUtils.isEmpty(strHour) ? new Random().nextInt(24) : Integer.parseInt(strHour));
        setting.setAlarmMinute(TextUtils.isEmpty(strMinute) ? new Random().nextInt(60) : Integer.parseInt(strMinute));
        setting.setContent(content);
        return setting;
    }

    private void initTextAlarmListView() {
        mSettings.clear();
        mRecyclerView = findViewById(R.id.rvTextAlarm);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TextAlarmAdapter(mSettings, this);
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.addItemDecoration(createItemDecoration());
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
    }

    private void readTextAlarm() {
        VPOperateManager.getMangerInstance(this).readTextAlarm(writeResponse, new ITextAlarmDataListener() {
            @Override
            public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                EMultiAlarmOprate OPT = textAlarmData.getOprate();
                boolean isOk = OPT == EMultiAlarmOprate.READ_SUCCESS ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAME_CRC ||
                        OPT == EMultiAlarmOprate.READ_SUCCESS_SAVE;
                if (isOk) {
                    mSettings.clear();
                    mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
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
    public void onToggleChanged(TextAlarm2Setting setting) {
        VPOperateManager.getMangerInstance(this).modifyTextAlarm(writeResponse, new ITextAlarmDataListener() {
            @Override
            public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                showMsg("修改闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
                mSettings.clear();
                mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
                mAdapter.notifyDataSetChanged();
            }
        }, setting);
    }
}
