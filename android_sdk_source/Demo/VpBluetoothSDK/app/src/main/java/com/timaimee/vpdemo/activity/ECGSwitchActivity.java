package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGSwitchListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;

/**
 * Author: YWX
 * Date: 2022/3/17 15:36
 * Description: ecg开关状态
 */
public class ECGSwitchActivity extends Activity implements IECGSwitchListener {

    Button btnRead, btnClose, btnOpen;
    TextView tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_switch);
        btnRead = findViewById(R.id.btnReadEcg);
        btnClose = findViewById(R.id.btnCloseEcg);
        btnOpen = findViewById(R.id.btnOpenEcg);
        tvInfo = findViewById(R.id.tvInfo);

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VPOperateManager.getMangerInstance(ECGSwitchActivity.this).readECGSwitchStatus(writeResponse, ECGSwitchActivity.this);
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VPOperateManager.getMangerInstance(ECGSwitchActivity.this).openECGSwitch(writeResponse, ECGSwitchActivity.this);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VPOperateManager.getMangerInstance(ECGSwitchActivity.this).closeECGSwitch(writeResponse, ECGSwitchActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        VPOperateManager.getMangerInstance(ECGSwitchActivity.this).readECGSwitchStatus(writeResponse, ECGSwitchActivity.this);
        Toast.makeText(this, "reading ecg function supportable...", Toast.LENGTH_SHORT).show();
    }

    private boolean isSupport = false;

    @Override
    public void onECGSwitchStatusChanged(EFunctionStatus eFunctionStatus) {
        if (eFunctionStatus == EFunctionStatus.UNSUPPORT) {
            Toast.makeText(this, "device not support ecg function", Toast.LENGTH_SHORT).show();
            tvInfo.setText("ecg not support ");
            isSupport = false;
        } else {
            if (eFunctionStatus == EFunctionStatus.SUPPORT_OPEN) {
                isSupport = true;
                Toast.makeText(this, "ecg function is open", Toast.LENGTH_SHORT).show();
                tvInfo.setText("ecg open success ");

            } else if (eFunctionStatus == EFunctionStatus.SUPPORT_CLOSE) {
                isSupport = true;
                Toast.makeText(this, "ecg function is close", Toast.LENGTH_SHORT).show();
                tvInfo.setText("ecg close success ");

            }
        }

    }


    private IBleWriteResponse writeResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {

        }
    };
}
