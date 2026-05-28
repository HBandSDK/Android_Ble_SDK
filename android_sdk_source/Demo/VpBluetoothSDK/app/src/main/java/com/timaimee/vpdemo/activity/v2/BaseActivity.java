package com.timaimee.vpdemo.activity.v2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.inuker.bluetooth.library.Code;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView tvTitle = null;
    public ImageView ivBack = null;

    public VPOperateManager vpBleManager;

    public SpannableStringBuilder sb = new SpannableStringBuilder();
    public TextView tvTestInfo = null;
    public ScrollView svTestInfo = null;

    public final IBleWriteResponse defaultResponse = code -> {
        if (Code.REQUEST_SUCCESS == code) {
            showToast(pageTitle() + " : 指令写入成功");
        } else {
            showToast(pageTitle() + " : 指令写入失败");
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

        if (hasCommonMsgUI()) {
            initCommonUI();
        }

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
        runOnUiThread(() -> Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    public void initCommonUI() {
        svTestInfo = findViewById(R.id.svTestInfo);
        tvTestInfo = findViewById(R.id.tvTestInfo);
    }

    public boolean hasCommonMsgUI() {
        return false;
    }

    public void appendResult(String msg) {
        if (!checkCommonUI()) return;
        sb.append("\n").append(msg);
        tvTestInfo.setText(sb.toString());
    }

    // 添加 红色大号字体
    public void appendRedLargeText(String msg) {
        if (!checkCommonUI()) return;
        SpannableString spannable = new SpannableString(msg + "\n");
        // 红色
        spannable.setSpan(new ForegroundColorSpan(Color.RED),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 大号字体 20sp
        spannable.setSpan(new AbsoluteSizeSpan(20, true),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(spannable);
        tvTestInfo.setText(sb);
    }

    // 添加 蓝色中号字体
    public void appendBlueMiddleText(String msg) {
        if (!checkCommonUI()) return;
        SpannableString spannable = new SpannableString(msg + "\n");
        // 蓝色
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 中号字体 16sp
        spannable.setSpan(new AbsoluteSizeSpan(16, true),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(spannable);
        tvTestInfo.setText(sb);
    }


    public void clearTestInfo() {
        if (!checkCommonUI()) return;
        sb.clear();
        tvTestInfo.setText("");
    }


    private boolean checkCommonUI(){
        return hasCommonMsgUI() && tvTestInfo != null && svTestInfo != null;
    }

}
