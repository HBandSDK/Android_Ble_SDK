package com.timaimee.vpdemo.activity.v2.health;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.AutoMeasureEditActivity;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.timaimee.vpdemo.adapter.AutoMeasureAdapter;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IAutoMeasureSettingDataListener;
import com.veepoo.protocol.model.datas.AutoMeasureData;
import com.veepoo.protocol.shareprence.VpSpGetUtil;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AutoMeasureActivity extends BaseVPBLETestActivity implements IAutoMeasureSettingDataListener , AutoMeasureAdapter.OnAutoMeasureOptListener {

    private Button btnRefreshList;
    private RecyclerView rvAutoMeasure;
    private AutoMeasureAdapter adapter;
    private List<AutoMeasureData> data = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // 隐藏ActionBar
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_auto_measure;
    }

    @Override
    public String pageTitle() {
        return "自动测量";
    }

    @Override
    public void initView() {
        data.clear();
        btnRefreshList = findViewById(R.id.btnRefreshList);
        rvAutoMeasure = findViewById(R.id.rvAutoMeasure);
        rvAutoMeasure.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AutoMeasureAdapter(data, this);
        rvAutoMeasure.addItemDecoration(createItemDecoration());
        rvAutoMeasure.setAdapter(adapter);
        btnRefreshList.setOnClickListener(v -> {
            data.clear();
            adapter.notifyDataSetChanged();
            VpSpGetUtil util = VpSpGetUtil.getVpSpVariInstance(AutoMeasureActivity.this);
            if (util.isSupportAutoMeasure()) {
                showMsg("支持该功能");
            } else {
                showMsg("不支持该功能");
            }
            VPOperateManager.getInstance().readAutoMeasureSettingData(defaultResponse, this);
        });
        
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        data.clear();
        adapter.notifyDataSetChanged();
        VPOperateManager.getInstance().readAutoMeasureSettingData(defaultResponse, this);
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
    }

    @Override
    public void onSettingDataChange(List<AutoMeasureData> autoMeasureDataList) {
        data.clear();
        data.addAll(autoMeasureDataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSettingDataChangeFail() {
        showMsg("设置失败");
    }

    @Override
    public void onSettingDataChangeSuccess() {
        showMsg("设置成功");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onToggleChanged(AutoMeasureData data) {
        VPOperateManager.getInstance().setAutoMeasureSettingData(defaultResponse, data,this);
    }

    @Override
    public void onItemClick(AutoMeasureData data) {
        selectData = data;
        startActivity(new Intent(this, AutoMeasureEditActivity.class));
    }

    public static AutoMeasureData selectData = null;

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
