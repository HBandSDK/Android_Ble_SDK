package com.timaimee.vpdemo.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.OnRecycleViewClickCallback;
import com.timaimee.vpdemo.bean.TimeZoneBean;
import com.timaimee.vpdemo.utils.XmlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Description 添加世界时钟
 *
 * @author KYM.
 * @date 2024/4/10 20:01
 */
public class AddClockActivity extends AppCompatActivity implements OnRecycleViewClickCallback {

    private RecyclerView mRecyclerView;
    private WorldClockAddListAdapter mAdapter;

    private List<TimeZoneBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clock);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WorldClockAddListAdapter(dataList, this);
        mRecyclerView.setAdapter(mAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TimeZoneBean> timeZoneBeans = XmlParser.parseXml2TimeZoneBeanList(AddClockActivity.this, R.xml.world_clock_datasource);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataList.clear();
                        dataList.addAll(timeZoneBeans);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).run();
    }

    @Override
    public void OnRecycleViewClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TimeZoneBean", dataList.get(position));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(1115, intent);
        finish();
    }
}