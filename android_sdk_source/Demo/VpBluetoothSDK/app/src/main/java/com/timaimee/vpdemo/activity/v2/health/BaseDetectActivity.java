package com.timaimee.vpdemo.activity.v2.health;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
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

public abstract class BaseDetectActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView tvTitle = null;
    public ImageView ivBack = null;

    VPOperateManager vpBleManager;

    boolean isClickStart = false;
    boolean isClickStop = false;

    IBleWriteResponse defaultResponse = code -> {
        if (Code.REQUEST_SUCCESS == code) {
            showToast(pageTitle() + " : 指令写入成功");
            onCMDWriteSuccess();
        } else {
            showToast(pageTitle() + " : 指令写入失败");
            onCMDWriteFailed();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_detect);
        ImmersionBar.with(this)
                .statusBarColorInt(Color.BLACK)      // 状态栏纯黑
                .navigationBarColorInt(Color.BLACK)  // 可选：底部导航栏也黑
                .statusBarDarkFont(true)   // 白色文字（必须false）
                .fitsSystemWindows(true)             // 关键：不侵入布局
                .init();
        vpBleManager = VPOperateManager.getInstance();
        initData();
        initEvent();
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);
        initCommonUI();
        if (tvTitle != null) {
            tvTitle.setText(pageTitle());
        }
        ivBack.setOnClickListener(view -> finish());
    }

    public abstract String pageTitle();

    public abstract void initData();

    public abstract void initEvent();
    public abstract void startDetect();
    public abstract void stopDetect();

    public void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(BaseDetectActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    Button btnStartDetect, btnStopDetect;
    TextView tvProcessInfo, tvTestInfo;
    ScrollView svTestInfo;

    public void initCommonUI() {
        btnStartDetect = findViewById(R.id.btnStartDetect);
        btnStopDetect = findViewById(R.id.btnStopDetect);
        tvProcessInfo = findViewById(R.id.tvDetectProcessStatus);
        tvTestInfo = findViewById(R.id.tvTestInfo);
        svTestInfo = findViewById(R.id.svTestInfo);

        btnStartDetect.setOnClickListener(view -> {
            isClickStart = true;
            isClickStop = false;
            clearTestInfo();
            startDetect();
        });
        btnStopDetect.setOnClickListener(view -> {
            isClickStart = false;
            isClickStop = true;
            stopDetect();
        });
    }

    public SpannableStringBuilder sb = new SpannableStringBuilder();
    public void appendResult(String msg) {
        sb.append("\n").append(msg);
        tvTestInfo.setText(sb.toString());
        svTestInfo.post(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN));
    }

    // 添加 红色大号字体

    // 添加 【柔和红色大号 + 粗体】
    public void appendRedLargeText(String msg) {
        SpannableString spannable = new SpannableString(msg + "\n");

        // 柔和红色（不是刺眼红 #E53935）
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#E53935")),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 大号字体 17sp
        spannable.setSpan(new AbsoluteSizeSpan(15, true),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 粗体（关键）
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(spannable);
        tvTestInfo.setText(sb);
        svTestInfo.post(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN));
    }

    // 添加 【柔和蓝色中号 + 粗体】
    public void appendBlueMiddleText(String msg) {
        SpannableString spannable = new SpannableString(msg + "\n");

        // 柔和蓝色（不刺眼 #1976D2）
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 中号字体 15sp
        spannable.setSpan(new AbsoluteSizeSpan(13, true),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 粗体（关键）
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(spannable);
        tvTestInfo.setText(sb);
        svTestInfo.post(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN));
    }

    // 添加 柔和橘黄色字体 粗体
    public void appendOrangeText(String msg) {
        SpannableString spannable = new SpannableString(msg + "\n");
        // 柔和橘黄色 #F57C00
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F57C00")),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 字体大小16sp
        spannable.setSpan(new AbsoluteSizeSpan(15, true),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 粗体
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(spannable);
        tvTestInfo.setText(sb);
        svTestInfo.post(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN));
    }

    public void clearTestInfo() {
        sb.clear();
        tvTestInfo.setText("");
    }

    public void onCMDWriteSuccess() {
        appendOrangeText(isClickStart? "开始" + pageTitle() +
                "测量指令写入成功！" : "停止" +
                pageTitle() +
                "测量指令写入成功");

    }

    public void onCMDWriteFailed() {
        appendRedLargeText(isClickStop? "开始" +
                pageTitle() +
                "指令写入失败！" : "停止" +
                pageTitle() +
                "指令写入失败");
    }
}
