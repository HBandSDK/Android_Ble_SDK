package com.timaimee.vpdemo.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.HealthRemindAdapter;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.IHealthRemindListener;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.datas.HealthRemind;
import com.veepoo.protocol.model.enums.HealthRemindType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HealthRemindActivity extends AppCompatActivity implements HealthRemindAdapter.OnHealthRemindToggleChangeListener, IHealthRemindListener {
    RecyclerView mRecyclerView;
    HealthRemindAdapter mAdapter;
    List<HealthRemind> mSettings = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_remind);
        initHealthRemindView();
        VPOperateManager.getInstance().readHealthRemind(HealthRemindType.ALL, this, new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {

            }
        });
    }

    private void initHealthRemindView() {
        mSettings.clear();
        mRecyclerView = findViewById(R.id.rvHealthRemind);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HealthRemindAdapter(mSettings, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onToggleChanged(HealthRemind setting) {
        VPOperateManager.getInstance().settingHealthRemind(setting, this, new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {

            }
        });
    }

    @Override
    public void functionNotSupport() {
        showMsg("暂不支持该功能");
    }

    @Override
    public void onHealthRemindRead(@NotNull HealthRemind healthRemind) {
        if (!mSettings.contains(healthRemind)) {
            mSettings.add(healthRemind);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHealthRemindSettingSuccess(@NotNull HealthRemind healthRemind) {
        showMsg("设置成功：" + healthRemind.toString());
    }

    @Override
    public void onHealthRemindReadFailed() {
        showMsg("读取失败");
    }

    @Override
    public void onHealthRemindSettingFailed(@NotNull HealthRemindType healthRemindType) {
        showMsg("设置失败:" + healthRemindType.getDes());
    }

    public void showMsg(String msg) {
        Toast.makeText(HealthRemindActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHealthRemindReport(@NotNull HealthRemind healthRemind) {
        showMsg("健康提醒上报:" + healthRemind.toString());
    }

    @Override
    public void onHealthRemindReportFailed() {
        showMsg("健康提醒上报:failed" );
    }

    @Override
    public void onHealthRemindReadingComplete() {

    }
}
