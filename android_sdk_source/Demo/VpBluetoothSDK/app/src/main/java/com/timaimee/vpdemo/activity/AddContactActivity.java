package com.timaimee.vpdemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ISOSCallTimesListener;

public class AddContactActivity extends AppCompatActivity {
    EditText etName, etPhoneNumber, etSOSCount;
    TextView tvSOSInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        boolean isADD = getIntent().getBooleanExtra("_isAdd", true);
        Group gpAdd = findViewById(R.id.gpAdd);
        Group gpSOS = findViewById(R.id.gpSOS);
        gpAdd.setVisibility(isADD ? View.VISIBLE : View.GONE);
        gpSOS.setVisibility(!isADD ? View.VISIBLE : View.GONE);
        etName = findViewById(R.id.etName);
        etSOSCount = findViewById(R.id.etSOSCount);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvSOSInfo = findViewById(R.id.tvSOSCountInfo);
        findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("phoneNumber", phoneNumber);
                setResult(1115, intent);
                finish();
            }
        });
        if (!isADD) {
            VPOperateManager.getInstance().readSOSCallTimes(new ISOSCallTimesListener() {
                @Override
                public void onSOSCallTimesSettingSuccess(int times) {
                    etSOSCount.setText(times + "");
                }

                @Override
                public void onSOSCallTimesSettingFailed() {

                }

                @Override
                public void onSOSCallTimesReadSuccess(int times, int minTimes, int maxTimes) {
                    etSOSCount.setText(times + "");
                    tvSOSInfo.setText("呼叫次数设置范围：[" + minTimes + "-" + maxTimes + "]");
                }

                @Override
                public void onSOSCallTimesReadFailed() {

                }
            }, new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {

                }
            });
        }
        findViewById(R.id.btnSettingCount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String count = etSOSCount.getText().toString();
                int callTimes = Integer.parseInt(count);
                VPOperateManager.getInstance().setSOSCallTimes(callTimes, new ISOSCallTimesListener() {
                    @Override
                    public void onSOSCallTimesSettingSuccess(int times) {
                        Toast.makeText(AddContactActivity.this, "设置成功：" + times + "次", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSOSCallTimesSettingFailed() {
                        Toast.makeText(AddContactActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSOSCallTimesReadSuccess(int times, int minTimes, int maxTimes) {
                        Toast.makeText(AddContactActivity.this, "读取成功：" + times + "-范围：[" + minTimes + "-" + maxTimes + "]", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSOSCallTimesReadFailed() {

                    }
                }, new IBleWriteResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                });
            }
        });
    }
}
