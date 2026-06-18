package com.timaimee.vpdemo.activity.v2.other;

import android.os.Build;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IMagneticTherapyListener;
import com.veepoo.protocol.model.datas.MagneticTherapy;
import com.veepoo.protocol.model.enums.MagneticTherapyType;

import cn.carbs.android.segmentcontrolview.library.SegmentControlView;


public class MagneticTherapyActivity extends BaseVPBLETestActivity implements IMagneticTherapyListener {
    SegmentControlView scvType, scvState;

    SeekBar sbDuration, sbLevel;

    TextView tvDuration, tvLevel, tvResult;

    Button btnSetting;

    private int duration = 1;
    private int level = 1;
    private boolean isOpen = true;
    private MagneticTherapyType type = MagneticTherapyType.PULSE_MAGNETIC_THERAPY;
    //如果退出了磁疗相关页面还想继续监听或者移除监听可以设置全局的磁疗监听
//            VPOperateManager.getInstance().setGlobalMagneticTherapy(new IMagneticTherapyListener() {
//                @Override
//                public void functionNotSupport() {
//
//                }
//
//                @Override
//                public void onMagneticTherapyChange(@NonNull MagneticTherapy data) {
//
//                }
//
//                @Override
//                public void onMagneticTherapyOpen(@NonNull MagneticTherapy data) {
//
//                }
//
//                @Override
//                public void onMagneticTherapyClose(@NonNull MagneticTherapy data) {
//
//                }
//            });


    @Override
    public int getLayoutID() {
        return R.layout.activity_magnetic_therapy;
    }

    @Override
    public String pageTitle() {
        return "磁疗";
    }

    @Override
    public void initView() {
        scvType = findViewById(R.id.magneticTherapyType);
        scvState = findViewById(R.id.scvStatus);
        tvDuration = findViewById(R.id.tvDurationValue);
        tvLevel = findViewById(R.id.tvLevelValue);
        btnSetting = findViewById(R.id.btnSetting);
        sbDuration = findViewById(R.id.sbDuration);
        sbLevel = findViewById(R.id.sbLevel);
        tvResult = findViewById(R.id.tvResult);
    }

    @Override
    public void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sbDuration.setMin(1);
        }
        sbDuration.setMax(15);
        sbLevel.setMax(7);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sbLevel.setMin(1);
        }
    }

    @Override
    public void initEvent() {
        scvType.setOnSegmentChangedListener(newSelectedIndex -> {
            if (newSelectedIndex == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sbDuration.setMin(1);
                }
                sbDuration.setMax(255);
                tvDuration.setText("1分钟");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sbDuration.setMin(1);
                }
                sbDuration.setMax(15);
                tvDuration.setText("1分钟");
            }
        });

        scvState.setOnSegmentChangedListener(newSelectedIndex -> isOpen = newSelectedIndex == 0);

        sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                duration = progress;
                tvDuration.setText(duration + "分钟");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                level = progress;
                tvLevel.setText("" + level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSetting.setOnClickListener(v -> {
            if (isOpen) {
                MagneticTherapy magneticTherapy = new MagneticTherapy(type, true, level);
                VPOperateManager.getInstance().openMagneticTherapy(magneticTherapy, duration, defaultResponse, MagneticTherapyActivity.this);
            } else {
                VPOperateManager.getInstance().closeMagneticTherapy(type, defaultResponse, MagneticTherapyActivity.this);
            }
        });
    }

    @Override
    public void functionNotSupport() {
        setResult("磁疗功能不支持");
    }

    @Override
    public void onMagneticTherapyDurationError(int min, int max) {
        setResult("磁疗时间设置错误 请设置在" + min + "~" + max + "分钟之间（包含）");
    }

    @Override
    public void onMagneticTherapyChange(@NonNull MagneticTherapy data) {
        setResult("设备端变更主动上报：" + data.toString());
    }

    @Override
    public void onMagneticTherapyOpen(@NonNull MagneticTherapy data) {
        setResult("磁疗打开：" + data.toString());
    }

    @Override
    public void onMagneticTherapyClose(@NonNull MagneticTherapy data) {
        setResult("磁疗关闭：" + data.toString());
    }

    private void setResult(String info) {
        runOnUiThread(() -> tvResult.setText(info));
    }
}
