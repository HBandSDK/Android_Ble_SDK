package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginProgressListener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: YWX
 * Date: 2021/12/25 9:30
 * Description: 原始数据打印
 */
public class OriginalDataLogActivity extends Activity implements View.OnClickListener {
    private static final String TAG = OriginalDataLogActivity.class.getSimpleName();
    RecyclerView mRecyclerView;
    LogAdapter mAdapter;
    List<ShowLog> logs;
    ProgressBar mProgressBar;

    private int dataType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_log);
        initData();
    }

    private void initData() {
        initRecycler();
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btnToTop).setOnClickListener(this);
        findViewById(R.id.btnToBottom).setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);
    }


    private void initHRVLog(List<HRVOriginData> originHrvDataList) {
        clearLogData();
        for (HRVOriginData data : originHrvDataList) {
            String timeTag = "【" + data.getmTime().getDateAndClockForSleepSecond() + "-" + data.getCurrentPackNumber() + "/" + data.getAllCurrentPackNumber() + "】";
            String log = timeTag + data.rate;
            logs.add(new ShowLog(log, isContainTime(timeTag) ? ShowLog.Level.ERROR : ShowLog.Level.BLUE));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initSpo2Log(List<Spo2hOriginData> originSpo2hDataList, int tag) {
//        clearLogData();
        for (Spo2hOriginData data : originSpo2hDataList) {
            String timeTag = "【" + data.getmTime().getDateAndClockForSleepSecond() + "-" + data.getCurrentPackNumber() + "/" + data.getAllPackNumner() + "】";
            String log = timeTag
                    + "-> 血氧 = " + data.getOxygenValue() + " 心率 = " + data.getHeartValue() + " 呼吸率 = " + data.getRespirationRate();
            logs.add(new ShowLog(log, isContainTime(timeTag) ? ShowLog.Level.ERROR : tag == 0 ? ShowLog.Level.GREEN : tag == 1 ? ShowLog.Level.BLACK : ShowLog.Level.BLUE));
        }
        mAdapter.notifyDataSetChanged();
    }

    private boolean isContainTime(String time) {
        for (ShowLog log : logs) {
            if (log.log.contains(time)) {
                return true;
            }
        }
        return false;
    }

    private void initOriginDataLog(List<OriginData3> originDataList, int tag) {
        for (OriginData3 data : originDataList) {
            String timeTag = "【" + data.getmTime().getDateAndClockForSleepSecond() + "-" + data.getPackageNumber() + "/" + data.getAllPackage() + "】";
            String log = timeTag + data.toString();
            logs.add(new ShowLog(log, isContainTime(timeTag) ? ShowLog.Level.ERROR : tag == 0 ? ShowLog.Level.GREEN : tag == 1 ? ShowLog.Level.BLACK : ShowLog.Level.BLUE));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void clearLogData() {
        if (logs == null) {
            logs = new ArrayList<>();
        }
        logs.clear();
        count = 0;
        mAdapter.notifyDataSetChanged();
    }

    private boolean isReadFinished = false;
    private int count = 0;

    private boolean isShowOrigin = false;
    private boolean isHRV = false;
    private boolean isSpo2 = false;

    private void initShow(int tag) {
        isShowOrigin = tag == 1;
        isHRV = tag == 2;
        isSpo2 = tag == 3;
    }

    private void get5MinuteOriginalData() {
        clearLogData();
        isReadFinished = false;
        mProgressBar.setVisibility(View.VISIBLE);
        IOriginProgressListener originDataListener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList) {
                String message = "健康数据-返回:" + originDataList.toString();
                Logger.t(TAG).i(message);
                if (isShowOrigin) {
                    initOriginDataLog(originDataList, count);
                    count++;
                }
            }

            @Override
            public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourDataList) {
                String message = "健康数据[30分钟]-返回:" + originHalfHourDataList.toString();
                Logger.t(TAG).i(message);
                Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的心率数据 size = " + originHalfHourDataList.getHalfHourRateDatas().size());
                Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的血压数据 size = " + originHalfHourDataList.getHalfHourBps().size());
                Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的运动数据 size = " + originHalfHourDataList.getHalfHourSportDatas().size());
            }

            @Override
            public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {
                Logger.t(TAG).i("onOriginHRVOriginListDataChange ===== > " + originHrvDataList == null ? "NULL" : originHrvDataList.size() + "");
                if (isHRV) {
                    initHRVLog(originHrvDataList);
                }
            }

            @Override
            public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {
                Logger.t(TAG).e("onOriginSpo2OriginListDataChange ===== > " + (originSpo2hDataList == null ? "NULL" : originSpo2hDataList.size() + ""));
                if (isSpo2) {
                    assert originSpo2hDataList != null;
                    initSpo2Log(originSpo2hDataList, count);
                    count++;
                }
            }

            @Override
            public void onReadOriginProgress(float progress) {
//                String message = "onReadOriginProgress 健康数据[5分钟]-读取进度:" + progress;
//                Logger.t(TAG).i(message);
            }

            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
//                String message = "onReadOriginProgressDetail 健康数据[5分钟]-读取进度:currentPackage=" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
//                Logger.t(TAG).i(message);
            }


            @Override
            public void onReadOriginComplete() {
                String message = "健康数据-读取结束";
                Logger.t(TAG).i(message);
                isReadFinished = true;
                mProgressBar.setVisibility(View.GONE);
            }
        };
        VPOperateManager.getMangerInstance(this).readOriginData(writeResponse, originDataListener, 3);
    }

    WriteResponse writeResponse = new WriteResponse();

    /**
     * 写入的状态返回
     */
    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("write cmd status:" + code);

        }
    }

    private void initRecycler() {
        mRecyclerView = findViewById(R.id.rv_log);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        logs = new ArrayList<>();
        mAdapter = new LogAdapter(logs);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                initShow(1);
                get5MinuteOriginalData();
                break;
            case R.id.btn2:
                initShow(2);
                get5MinuteOriginalData();
                break;
            case R.id.btn3:
                initShow(3);
                get5MinuteOriginalData();
                break;
            case R.id.btnToTop:
                mRecyclerView.scrollToPosition(0);
                break;
            case R.id.btnToBottom:
                mRecyclerView.scrollToPosition(logs.size() == 0 ? 0 : logs.size() - 1);
                break;
        }
    }

    public static class ShowLog {
        public String log;
        public Level level;//0 黑色 1 红色 2 蓝色

        public enum Level implements Serializable {
            BLACK(Color.BLACK),
            ERROR(Color.RED),
            BLUE(Color.BLUE),
            GREEN(Color.parseColor("#009933"));
            int color;

            Level(int color) {
                this.color = color;
            }

        }

        public ShowLog(String log, Level level) {
            this.log = log;
            this.level = level;
        }
    }


    private class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

        List<ShowLog> logs;

        public LogAdapter(List<ShowLog> logs) {
            this.logs = logs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(OriginalDataLogActivity.this).inflate(R.layout.item_log, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ShowLog log = logs.get(position);
            holder.tvLog.setText(log.log);
            holder.tvLog.setTextColor(log.level.color);
        }

        public int getColor(int level) {
            switch (level) {
                case 0:
                    return Color.BLACK;
                case 1:
                    return Color.RED;
                case 2:
                    return Color.BLUE;
            }
            return Color.BLACK;
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvLog;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvLog = itemView.findViewById(R.id.tvLog);
            }
        }
    }
}
