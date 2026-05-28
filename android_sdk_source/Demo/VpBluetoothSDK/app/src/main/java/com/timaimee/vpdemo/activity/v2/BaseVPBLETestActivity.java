package com.timaimee.vpdemo.activity.v2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.inuker.bluetooth.library.Code;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;

public abstract class BaseVPBLETestActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView tvTitle = null;
    public ImageView ivBack = null;

    public VPOperateManager vpBleManager;

    private int cmdTag = 0;

    public final IBleWriteResponse defaultResponse = code -> {
        if (Code.REQUEST_SUCCESS == code) {
            showToast(pageTitle() + " : TAG(" + cmdTag + ")指令写入成功");
            onCMDWriteSuccess(cmdTag);
        } else {
            showToast(pageTitle() + " : TAG(" + cmdTag + ")指令写入失败");
            onCMDWriteFailed(cmdTag);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        ImmersionBar.with(this)
                .statusBarColorInt(Color.BLACK)      // 状态栏纯黑
                .navigationBarColorInt(Color.BLACK)  // 可选：底部导航栏也黑
                .statusBarDarkFont(true)   // 白色文字（必须false）
                .fitsSystemWindows(true)             // 关键：不侵入布局
                .init();
        vpBleManager = VPOperateManager.getInstance();
        initView();
        initData();
        initEvent();
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);

        if (tvTitle != null) {
            tvTitle.setText(pageTitle());
        }
        ivBack.setOnClickListener(view -> finish());
    }

    public abstract int getLayoutID();

    public abstract String pageTitle();

    public abstract void initView();

    public abstract void initData();

    public abstract void initEvent();

    public void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(BaseVPBLETestActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    public void onCMDWriteSuccess(int cmdTag) {
    }

    public void onCMDWriteFailed(int cmdTag) {
    }

    public void setWriteCmdTAG(int cmdTag){
        this.cmdTag = cmdTag;
    }
}
