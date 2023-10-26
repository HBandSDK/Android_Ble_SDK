package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IECGSwitchListener;
import com.veepoo.protocol.listener.data.IF2DebugListener;
import com.veepoo.protocol.model.enums.DebugCmd;
import com.veepoo.protocol.model.enums.EFunctionStatus;

/**
 * Author: YWX
 * Date: 2022/3/17 15:36
 * Description: ecg开关状态
 */
public class F2DebugActivity extends Activity implements IF2DebugListener {

    Button btnCmd1, btnCmd2, btnCmd3, btnCmd4;
    TextView tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f2_debug);
        btnCmd1 = findViewById(R.id.btnCmd1);
        btnCmd2 = findViewById(R.id.btnCmd2);
        btnCmd3 = findViewById(R.id.btnCmd3);
        btnCmd4 = findViewById(R.id.btnCmd4);
        tvInfo = findViewById(R.id.tvInfo);
        btnCmd1.setText(DebugCmd.CMD1.des);
        btnCmd2.setText(DebugCmd.CMD2.des);
        btnCmd3.setText(DebugCmd.CMD3.des);
        btnCmd4.setText(DebugCmd.CMD4.des);

//        btnCmd1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VPOperateManager.getMangerInstance(F2DebugActivity.this).sendF2DebugCmd(writeResponse, DebugCmd.CMD1, F2DebugActivity.this);
//            }
//        });
//
//        btnCmd2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VPOperateManager.getMangerInstance(F2DebugActivity.this).sendF2DebugCmd(writeResponse, DebugCmd.CMD2, F2DebugActivity.this);
//            }
//        });
//
//        btnCmd3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VPOperateManager.getMangerInstance(F2DebugActivity.this).sendF2DebugCmd(writeResponse, DebugCmd.CMD3, F2DebugActivity.this);
//            }
//        });
//
//        btnCmd4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VPOperateManager.getMangerInstance(F2DebugActivity.this).sendF2DebugCmd(writeResponse, DebugCmd.CMD4, F2DebugActivity.this);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private IBleWriteResponse writeResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {

        }
    };

    @Override
    public void onIF2DebugStatusChanged(boolean isSuccess, DebugCmd debugCmd) {
        tvInfo.setText(debugCmd.toString() + "， 是否成功：" + isSuccess);
    }
}
