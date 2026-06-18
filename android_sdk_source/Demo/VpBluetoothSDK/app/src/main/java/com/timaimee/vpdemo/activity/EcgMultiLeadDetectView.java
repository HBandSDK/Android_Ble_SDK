package com.timaimee.vpdemo.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.multi_lead.enums.ELeadFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ECG报告测量页面
 * Created by Administrator on 2018/11/5.
 */

public class EcgMultiLeadDetectView extends View {

    private static final String TAG = EcgMultiLeadDetectView.class.getSimpleName();
    float mWidth, mHeight;
    Paint mLinePiant, mWhiteRectPiant, mGridPiant, mBordGridPiant;
    //float heartPositionY[];
    //PointF[] mPoints;
    int ecgLineColor = 0, color_line = 0, color_black = 0;
    int[] linePositionX = new int[6];

    Map<ELeadFlag, List<Float>> rowYMap = new HashMap<>();
    Map<ELeadFlag, List<PointF>> pointsMap = new HashMap<>();

    int[] colorList = new int[]{
            Color.parseColor("#FF0000"),
            Color.parseColor("#00FF00"),
            Color.parseColor("#0000FF"),
            Color.parseColor("#FFFF00"),
            Color.parseColor("#FFC0CB"),
            Color.parseColor("#00FFFF"),
    };


    public EcgMultiLeadDetectView(Context context, @Nullable AttributeSet attrs) {
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

        for (Map.Entry<ELeadFlag, List<PointF>> entry : pointsMap.entrySet()) {
            // 获取key和value
            ELeadFlag key = entry.getKey();
            //if (key == ELeadFlag.III){
            //ecgLineColor = colorList[key.getValue() - 1];
            List<PointF> mPoints = entry.getValue();
            List<Float> floats = rowYMap.get(key);
            // do something with key and value
            for (int i = 0; i < mPoints.size(); i++) {
                PointF point = new PointF(rowEcgWidth * i, floats.get(i));
                mPoints.set(i, point);
            }

            drawPath(canvas, mPoints.toArray(new PointF[0]), linePositionX[key.getValue() - 1], item_contents[key.getValue() - 1]);
            //}

        }


    }

    private float getRowY(float v, ELeadFlag leadFlag) {
        double pow = Math.pow(2.0, 23.0);
        float one_quto_height_voltage = (float) (pow * 20 / 10000f);
        float itemHeight = mHeight / 6;
        float v1 = itemHeight / 5 * 3 - v * 1.8f / one_quto_height_voltage * coumlnQutoWidth + itemHeight * (leadFlag.getValue() - 1);
        if (v1 > itemHeight) {
            v1 = itemHeight + itemHeight * (leadFlag.getValue() - 1);
        } else if (v1 < itemHeight * (leadFlag.getValue() - 1)) {
            v1 = itemHeight * (leadFlag.getValue() - 1);
        }
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

    private int[] item_contents = new int[6];

    public synchronized void changeData(ELeadFlag eLeadFlag, int[] data, int item_content) {

        this.item_contents[eLeadFlag.getValue() - 1] = item_content;
        int positionX = linePositionX[eLeadFlag.getValue() - 1] % rowEcgCount;
        linePositionX[eLeadFlag.getValue() - 1] = positionX;
        for (int i = positionX; i < positionX + item_contents[eLeadFlag.getValue() - 1]; i++) {
            if (i >= rowEcgCount) {
                linePositionX[eLeadFlag.getValue() - 1] = -item_content;
                positionX = -item_content;
                break;
            }
            if (data[i - positionX] == Integer.MAX_VALUE) {
                continue;
            }
            float rowY = getRowY(data[i - positionX], eLeadFlag);
            List<Float> floats = rowYMap.get(eLeadFlag);
            floats.set(i, rowY);

            //heartPositionY[i] = rowY;
        }
        Logger.t(TAG).i("changeData: columnCount=" + rowEcgCount + ",linePositionX=" + linePositionX);
        invalidate();
        linePositionX[eLeadFlag.getValue() - 1] = positionX + item_content;
    }

    /**
     * 清除以及初始化数据（600个点的计算列表，数据回放的点，）
     */
    public void clearData() {
        for (int i = 0; i < linePositionX.length; i++) {
            linePositionX[i] = 0;
        }
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
        Log.e("Test", "rowEcgCount:" + rowEcgCount);
        float itemHeight = mHeight / 6;
        for (int i = 0; i < 6; i++) {
            List<PointF> pointFList = new ArrayList<>();
            List<Float> rowYList = new ArrayList<>();
            for (int j = 0; j < rowEcgCount; j++) {
                pointFList.add(new PointF());
                rowYList.add(itemHeight / 5 * 3 + itemHeight * i);
            }
            pointsMap.put(ELeadFlag.Companion.fromValue(i + 1), pointFList);
            rowYMap.put(ELeadFlag.Companion.fromValue(i + 1), rowYList);
        }
        //mPoints = new PointF[rowEcgCount];
        //initHeartPosition();
    }

    private void initHeartPosition() {
        for (int i = 0; i < 6; i++) {
            List<Float> rowYList = new ArrayList<>();
            for (int j = 0; j < rowEcgCount; j++) {
                rowYList.add(mHeight / 5 * 3);
            }
            rowYMap.put(ELeadFlag.Companion.fromValue(i + 1), rowYList);
        }

        //heartPositionY = new float[rowEcgCount];
        //for (int i = 0; i < rowEcgCount; i++) {
        //    heartPositionY[i] = mHeight / 5 * 3;
        //}

    }


}
