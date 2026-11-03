package com.timaimee.vpdemo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 睡眠曲线图（参考 GBand SleepChartV2View 的四段式横向色块）
 * 自上而下：清醒 / 快速眼动 / 浅睡 / 深睡
 */
public class SleepChartView extends View {

    public static final int BAND_AWAKE = 0;
    public static final int BAND_REM = 1;
    public static final int BAND_LIGHT = 2;
    public static final int BAND_DEEP = 3;

    private static final int COLOR_AWAKE = Color.parseColor("#FFBC5C");
    private static final int COLOR_REM = Color.parseColor("#52AEFF");
    private static final int COLOR_LIGHT = Color.parseColor("#8574FF");
    private static final int COLOR_DEEP = Color.parseColor("#7542F5");
    private static final int COLOR_INSOMNIA = Color.parseColor("#9E9E9E");
    private static final int COLOR_EMPTY_TEXT = Color.parseColor("#999999");

    private final Paint blockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF blockRect = new RectF();
    private final List<SleepBlock> sleepBlocks = new ArrayList<>();

    private boolean precision = true;
    private int totalCount = 0;

    public SleepChartView(Context context) {
        this(context, null);
    }

    public SleepChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        blockPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(COLOR_EMPTY_TEXT);
        textPaint.setTextSize(sp2px(13));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setSleepData(String sleepLine) {
        setSleepData(sleepLine, true);
    }

    /**
     * @param sleepLine 睡眠曲线字符串
     * @param precision true:精准睡眠(0深睡/1浅睡/2快速眼动/3失眠/4苏醒,每点1分钟)
     *                  false:普通睡眠(0浅睡/1深睡/2苏醒,每点5分钟)
     */
    public void setSleepData(String sleepLine, boolean precision) {
        this.precision = precision;
        sleepBlocks.clear();
        totalCount = 0;
        if (!TextUtils.isEmpty(sleepLine)) {
            totalCount = sleepLine.length();
            int start = 0;
            for (int i = 1; i < totalCount; i++) {
                if (sleepLine.charAt(i) != sleepLine.charAt(start)) {
                    addBlock(start, i - 1, sleepLine.charAt(start));
                    start = i;
                }
            }
            addBlock(start, totalCount - 1, sleepLine.charAt(start));
        }
        invalidate();
    }

    private void addBlock(int startIndex, int endIndex, char value) {
        int type = Character.getNumericValue(value);
        sleepBlocks.add(new SleepBlock(startIndex, endIndex, type));
    }

    private int getBand(int type) {
        if (precision) {
            switch (type) {
                case 0:
                    return BAND_DEEP;
                case 1:
                    return BAND_LIGHT;
                case 2:
                    return BAND_REM;
                case 3:
                case 4:
                default:
                    return BAND_AWAKE;
            }
        }
        // 普通睡眠：0浅睡 1深睡 2苏醒
        switch (type) {
            case 1:
                return BAND_DEEP;
            case 0:
                return BAND_LIGHT;
            case 2:
            default:
                return BAND_AWAKE;
        }
    }

    private int getColor(int type, int band) {
        if (precision && type == 3) {
            return COLOR_INSOMNIA;
        }
        switch (band) {
            case BAND_AWAKE:
                return COLOR_AWAKE;
            case BAND_REM:
                return COLOR_REM;
            case BAND_LIGHT:
                return COLOR_LIGHT;
            case BAND_DEEP:
            default:
                return COLOR_DEEP;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (totalCount == 0 || sleepBlocks.isEmpty()) {
            canvas.drawText("暂无睡眠数据", getWidth() / 2f,
                    (getHeight() + textPaint.getTextSize()) / 2f - textPaint.descent(), textPaint);
            return;
        }

        float padding = dp2px(4);
        float drawHeight = getHeight() - padding * 2;
        float itemWidth = (float) getWidth() / totalCount;
        float quarter = drawHeight / 4;

        for (SleepBlock block : sleepBlocks) {
            int band = getBand(block.type);
            blockPaint.setColor(getColor(block.type, band));
            float left = block.startIndex * itemWidth;
            float right = (block.endIndex + 1) * itemWidth;
            float top = padding + band * quarter;
            float bottom = top + quarter;
            blockRect.set(left, top, right, bottom);
            canvas.drawRoundRect(blockRect, dp2px(3), dp2px(3), blockPaint);
        }
    }

    private float dp2px(float dp) {
        return dp * getResources().getDisplayMetrics().density + 0.5f;
    }

    private float sp2px(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }

    private static class SleepBlock {
        final int startIndex;
        final int endIndex;
        final int type;

        SleepBlock(int startIndex, int endIndex, int type) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.type = type;
        }
    }
}
