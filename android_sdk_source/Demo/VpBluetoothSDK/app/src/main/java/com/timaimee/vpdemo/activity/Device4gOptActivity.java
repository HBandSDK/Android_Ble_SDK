package com.timaimee.vpdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.INetServer4gConfigListener;
import com.veepoo.protocol.model.datas.NetworkSever4gConfigInfo;

import tech.gujin.toast.ToastUtil;

/**
 * @author KYM
 * on 2026/1/12
 */
public class Device4gOptActivity extends AppCompatActivity {

    private TextView tvConfigInfo;
    private EditText etAccount;
    private EditText etPwd;
    private NetworkSever4gConfigInfo config = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device4g_opt);

        // 初始化视图
        tvConfigInfo = findViewById(R.id.tvConfigInfo);
        etAccount = findViewById(R.id.etAccount);
        etPwd = findViewById(R.id.etPwd);

        // 读取配置按钮
        findViewById(R.id.btnRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readConfigInfo();
            }
        });

        // 设置配置按钮
        findViewById(R.id.btnSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config == null) {
                    return;
                }

                String account = etAccount.getText().toString();
                String pwd = etPwd.getText().toString();

                // 设置实体类属性
                config.setPassword(pwd);
                config.setUserName(account);
                // 4g开关
                config.setSwitch4g(true);
                // 上传开关
                config.setSwitchUpload(true);
                // 设备数据上传服务器时间
                config.setReportInterval(10);

                set4gServerInfo();
            }
        });

        // 清除配置按钮
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config == null) {
                    return;
                }
                clear4gAccountInfo();
            }
        });
    }

    private void readConfigInfo() {
        VPOperateManager.getInstance().read4gServerInfo(bleWriteResponse, configListener);
    }

    private void set4gServerInfo() {
        VPOperateManager.getInstance().set4gServerInfo(config, bleWriteResponse, configListener);
    }

    private void clear4gAccountInfo() {
        VPOperateManager.getInstance().clear4gAccountInfo(bleWriteResponse, configListener);
    }

    private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {

        }
    };

    // 实现接口回调
    private final INetServer4gConfigListener configListener = new INetServer4gConfigListener() {
        @Override
        public void onReadSuccess(NetworkSever4gConfigInfo info) {
            if (info != null) {
                tvConfigInfo.setText(info.toString());
                config = info;
            }
        }

        @Override
        public void onSettingSuccess() {
            ToastUtil.show("设置成功");
        }

        @Override
        public void onSettingFailed() {
            ToastUtil.show("设置失败");
        }
    };
}