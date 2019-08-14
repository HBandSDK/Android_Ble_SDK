package com.timaimee.vpdemo.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * ECG报告测量页面
 * Created by Administrator on 2018/11/5.
 */

public class EcgHeartRealthView extends View {

    private static final String TAG = EcgHeartRealthView.class.getSimpleName();
    float mWidth, mHeight;
    Paint mLinePiant, mWhiteRectPiant, mGridPiant, mBordGridPiant;
    float heartPositionY[];
    PointF[] mPoints;
    int ecgLineColor = 0, color_line = 0, color_black = 0;
    int linePositionX = 0;


    public EcgHeartRealthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ecgLineColor = context.getResources().getColor(R.color.ecg_line);
        color_line = getResources().getColor(R.color.ecg_line_bg_normal);
        color_black = getResources().getColor(R.color.ecg_line_bg_bold);
        initPaint();
    }


    private void initPaint() {
        mLinePiant = new Paint();
        mLinePiant.setColor(ecgLineColor);
        mLinePiant.setStrokeCap(Paint.Cap.SQUARE);
        mLinePiant.setStyle(Paint.Style.STROKE);
        mLinePiant.setStrokeWidth(4);
        mLinePiant.setAntiAlias(true);

        mWhiteRectPiant = new Paint();
        mWhiteRectPiant.setColor(Color.WHITE);
        mWhiteRectPiant.setStrokeCap(Paint.Cap.SQUARE);
        mWhiteRectPiant.setStyle(Paint.Style.FILL);
        mWhiteRectPiant.setStrokeWidth(4);
        mWhiteRectPiant.setAntiAlias(true);

        mGridPiant = new Paint();
        mGridPiant.setColor(color_line);
        mGridPiant.setStrokeWidth(2);
        mGridPiant.setStrokeCap(Paint.Cap.ROUND);
        mGridPiant.setAntiAlias(true);

        mBordGridPiant = new Paint();
        mBordGridPiant.setColor(color_black);
        mBordGridPiant.setStrokeWidth(2);
        mBordGridPiant.setStrokeCap(Paint.Cap.ROUND);
        mBordGridPiant.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Logger.t(TAG).i("onMeasure");
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        onPagerSetting();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Logger.t(TAG).i("onDraw");
        float stopX = mWidth;
        for (int i = 0; i <= coumlnQutoCount; i++) {
//            Logger.t(TAG).i("onDraw: " + stopX);
            if (i % 5 == 0) {
                canvas.drawLine(0, rowQutoWidth * i, stopX, rowQutoWidth * i, mBordGridPiant);
            } else {
                canvas.drawLine(0, rowQutoWidth * i, stopX, rowQutoWidth * i, mGridPiant);
            }
        }
        for (int i = 0; i <= rowQutoCount; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(coumlnQutoWidth * i, 0, coumlnQutoWidth * i, mHeight, mBordGridPiant);
            } else {
                canvas.drawLine(coumlnQutoWidth * i, 0, coumlnQutoWidth * i, mHeight, mGridPiant);
            }
        }

        for (int i = 0; i < mPoints.length; i++) {
            PointF point = new PointF(rowEcgWidth * i, heartPositionY[i]);
            mPoints[i] = point;
        }

        drawPath(canvas, mPoints, linePositionX, item_contents);
    }

    private float getRowY(float v) {
        float v1 = mHeight / 5 * 3 - v / 175 * coumlnQutoWidth;
        return v1;
    }


    private void drawPath(Canvas canvas, PointF[] mPoints, int pointX, int item_contents) {
        PointF[] arrayLeft = null;
        PointF[] arrayMiddle = null;
        PointF[] arrayRight = null;
        int length = mPoints.length;
        int leftLength, middleLength, rightLength;
        if (pointX <= 0) {
            leftLength = 0;
            middleLength = item_contents;
            rightLength = length - item_contents;
            arrayLeft = new PointF[leftLength];
            arrayMiddle = new PointF[middleLength];
            arrayRight = new PointF[rightLength];
            Log.i(TAG, "drawPath: left arr" + length + ",pointX=" + pointX + ",rightLength=" + rightLength);
            for (int i = 0; i < mPoints.length; i++) {
                if (i < item_contents) {
                    arrayMiddle[i] = mPoints[i];
                } else {
                    arrayRight[i - item_contents] = mPoints[i];
                }
            }
        } else if (pointX + item_contents >= length) {
            leftLength = pointX;
            middleLength = pointX + item_contents - length;
            rightLength = 0;
            Log.i(TAG, "drawPath: right arr" + length + ",pointX=" + pointX + ",rightLength 0");
            arrayLeft = new PointF[leftLength];
            arrayMiddle = new PointF[middleLength];
            arrayRight = new PointF[rightLength];
            for (int i = 0; i < mPoints.length; i++) {
                if (i < pointX) {
                    arrayLeft[i] = mPoints[i];
                } else if (i < pointX + middleLength) {
                    //   Log.i(TAG, "drawPath: right arr,[i - pointX]=" + (i - pointX) + ",i=" + i + ",middleLength=" + middleLength);
                    arrayMiddle[i - pointX] = mPoints[i];
                }
            }
        } else {
            leftLength = pointX;
            middleLength = item_contents;
            rightLength = length - pointX - item_contents;
            arrayLeft = new PointF[leftLength];
            arrayMiddle = new PointF[middleLength];
            arrayRight = new PointF[rightLength];
            Log.i(TAG, "drawPath: middle arr" + length + ",pointX=" + pointX + ",leftLength=" + leftLength + ",middleLength=" + middleLength + ",right=" + rightLength);
            for (int i = 0; i < mPoints.length; i++) {
                if (i < pointX) {
                    arrayLeft[i] = mPoints[i];
                } else if (i < pointX + item_contents) {
                    arrayMiddle[i - pointX] = mPoints[i];
                } else {
                    arrayRight[rightLength - (length - i)] = mPoints[i];
                }
            }
        }
        drawCubicLine(canvas, arrayLeft, ecgLineColor);
        drawCubicLine(canvas, arrayMiddle, Color.TRANSPARENT);
        drawCubicLine(canvas, arrayRight, ecgLineColor);
    }

    private void drawCubicLine(Canvas canvas, PointF[] mPoints, int color) {
        if (mPoints == null || mPoints.length <= 2) {
            return;
        }
        mLinePiant.setColor(color);
        Path mPath = getCubicLinePath(mPoints);
        canvas.drawPath(mPath, mLinePiant);
    }

    @NonNull
    private Path getCubicLinePath(PointF[] mPoints) {
        Path mPath = new Path();
        for (int j = 0; j < mPoints.length; j++) {
            PointF startp = mPoints[j];
            PointF endp;
            if (j != mPoints.length - 1) {
                endp = mPoints[j + 1];
                if (endp == null) {
                    continue;
                }
                float wt = (startp.x + endp.x) / 2;
                PointF p3 = new PointF();
                PointF p4 = new PointF();
                p3.x = wt;
                p3.y = startp.y;
                p4.x = wt;
                p4.y = endp.y;
                if (j == 0) {
                    mPath.moveTo(startp.x, startp.y);
                }
                mPath.cubicTo(p3.x, p3.y, p4.x, p4.y, endp.x, endp.y);
            }

        }
        return mPath;
    }

    private int item_contents;

    public synchronized void changeData(int[] data, int item_content) {
        this.item_contents = item_content;
        linePositionX = linePositionX % rowEcgCount;
        for (int i = linePositionX; i < linePositionX + item_contents; i++) {
            if (i  >= rowEcgCount) {
                linePositionX = -item_content;
                break;
            }
            float rowY = getRowY(data[i - linePositionX]);
            heartPositionY[i] = rowY;
        }
        Logger.t(TAG).i("changeData: columnCount=" + rowEcgCount + ",linePositionX=" + linePositionX);
        invalidate();
        linePositionX = linePositionX + item_content;
    }

    /**
     * 清除以及初始化数据（600个点的计算列表，数据回放的点，）
     */
    public void clearData() {
        linePositionX = 0;
        onPagerSetting();
        invalidate();
    }


    List<Integer> arraylist = new ArrayList<>();

    //采样率&走速
    int HZ = 250;
    int SPEED = 25;
    //一个格子多少个点
    int count = HZ / SPEED;
    //纵向一共放多少个格子
    int coumlnQutoCount = 16 * 5;
    //一个格子的高度
    float coumlnQutoWidth = 1;
    //横向一共放多少格子
    float rowQutoCount = 1;
    //一个格子的宽度
    float rowQutoWidth = 1;
    //横向一共放多少个点

    int rowEcgCount = 1;
    //一个点的宽度
    float rowEcgWidth = 1;

    private void onPagerSetting() {
        coumlnQutoWidth = mHeight / coumlnQutoCount;
        rowQutoWidth = coumlnQutoWidth;
        rowQutoCount = mWidth / rowQutoWidth;
        rowEcgCount = (int) (rowQutoCount * count);
        rowEcgWidth = mWidth / rowEcgCount;
        mPoints = new PointF[rowEcgCount];
        initHeartPosition();
    }

    private void initHeartPosition() {
        heartPositionY = new float[rowEcgCount];
        for (int i = 0; i < rowEcgCount; i++) {
            heartPositionY[i] = mHeight / 5 * 3;
        }

    }


}
