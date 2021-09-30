package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormG15ImgListener;
import com.veepoo.protocol.listener.data.IUiUpdateListener;
import com.veepoo.protocol.model.datas.UIDataG15Img;
import com.veepoo.protocol.model.enums.EUIFromType;
import com.veepoo.protocol.model.enums.EUiUpdateError;
import com.veepoo.protocol.util.UiUpdateUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Author: YWX
 * Date: 2021/9/18 10:19
 * Description: G15 图片传输
 */
public class UiUpdateG15ImgActivity extends Activity implements View.OnClickListener {

    private static final String TAG = UiUpdateG15ImgActivity.class.getSimpleName();


    UiUpdateUtil mUiUpdateUtil;

    private UIDataG15Img mUIDataProfile, mUIDataQRCode1, mUIDataQRCode2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiupdate_g15img);
        findViewById(R.id.btnA3).setOnClickListener(this);
        findViewById(R.id.btnA4).setOnClickListener(this);
        findViewById(R.id.btnA5).setOnClickListener(this);

        mUiUpdateUtil = UiUpdateUtil.getInstance();
        mUiUpdateUtil.init(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnA3:
                setG15ImgProfile();
                break;
            case R.id.btnA4:
                setG15ImgQRCode1();
                break;
            case R.id.btnA5:
                setG15ImgQRCode2();
                break;
        }
    }

    private Bitmap createBitmap(int color) {
        Bitmap bitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.RGB_565);
        bitmap.eraseColor(color);
        return bitmap;
    }

    /**
     * 设置用户信息图片
     */
    private void setG15ImgProfile(){
        //先读取手表关于用户信息图片设置的信息，读取成功后调用startTransmission设置图片
        readProfileInfo();
    }

    private void readProfileInfo() {
        mUiUpdateUtil.getG15ImgProfileInfo(new IUIBaseInfoFormG15ImgListener() {
            @Override
            public void onBaseUiInfoFormG15Img(UIDataG15Img uiDataG15Img) {
                Logger.t(TAG).i("获取G15手表个人信息图片UI设置信息:" + uiDataG15Img.toString());
                mUIDataProfile = uiDataG15Img;
                startTransmission(EUIFromType.G15_IMG_PROFILE, createBitmap(Color.parseColor("#F830A0")));
            }
        });
    }

    /**
     * 设置病人腕带二维码图片
     */
    private void setG15ImgQRCode1(){
        readQRCode1Info();
    }

    private void readQRCode1Info() {
        mUiUpdateUtil.getG15ImgQRCode1Info(new IUIBaseInfoFormG15ImgListener() {
            @Override
            public void onBaseUiInfoFormG15Img(UIDataG15Img uiDataG15Img) {
                Logger.t(TAG).i("获取G15手表病人腕带二维码图片UI设置信息:" + uiDataG15Img.toString());
                mUIDataQRCode1 = uiDataG15Img;
                startTransmission(EUIFromType.G15_IMG_QR_CODE_1, createBitmap(Color.parseColor("#0FC2FF")));
            }
        });
    }

    /**
     * 设置医院收款二维码图片
     */
    private void setG15ImgQRCode2(){
        readQRCode2Info();
    }

    private void readQRCode2Info() {
        mUiUpdateUtil.getG15ImgQRCode2Info(new IUIBaseInfoFormG15ImgListener() {
            @Override
            public void onBaseUiInfoFormG15Img(UIDataG15Img uiDataG15Img) {
                Logger.t(TAG).i("获取G15手表医院收款二维码图片UI设置信息:" + uiDataG15Img.toString());
                mUIDataQRCode2 = uiDataG15Img;
                startTransmission(EUIFromType.G15_IMG_QR_CODE_2, createBitmap(Color.parseColor("#5F80FF")));
            }
        });
    }

    /**
     * 获取bitmap的像素值
     *
     * @param bitmap
     * @return
     */
    private static byte[] getBitmapBytes(Bitmap bitmap) {
        int rowBytes = bitmap.getRowBytes();//行的byte数，像素的个数*像素的占位
        int height = bitmap.getHeight();
        int size = rowBytes * height;
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = byteBuffer.array();
        //给设备发送，需要处理一下大小端
        changeBigSmall(byteArray);
        //对齐4byte
        if (byteArray.length % 4 == 0) {
            return byteArray;
        } else {
            int reSize = byteArray.length / 4 + 1;
            reSize = reSize * 4;
            byte[] byteArrays = new byte[reSize];
            Arrays.fill(byteArrays, (byte) 0xff);
            System.arraycopy(byteArray, 0, byteArrays, 0, byteArray.length);
            return byteArrays;
        }
    }

    /**
     * 两两互换位置[1,2,3,4]-->[2,1,4,3]
     *
     * @param array
     */
    private static void changeBigSmall(byte[] array) {
        int size = array.length / 2;
        for (int j = 0; j < size; j++) {
            byte temp = array[j * 2 + 1];
            array[j * 2 + 1] = array[j * 2];
            array[j * 2] = temp;
        }
    }

    private void startTransmission(EUIFromType type, Bitmap bitmap) {
        Logger.t(TAG).i("开始传输:" + type);
        int bytes = bitmap.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        try {
            long timeStampAGPS = 0;
            mUiUpdateUtil.setAGPSTimeStamp(timeStampAGPS);

            mUiUpdateUtil.startSetUiStream(type, inputStream, new IUiUpdateListener() {

                @Override
                public void onUiUpdateStart() {
                    Logger.t(TAG).i("onUiUpdateStart");
                }

                @Override
                public void onStartClearCache(int sumCount) {
                    Logger.t(TAG).i("onStartClearCache:" + sumCount);
                }

                @Override
                public void onClearCacheProgress(int currentCount, int sumCount, int progress) {
                    Logger.t(TAG).i("onClearCacheProgress:" + currentCount + "," + sumCount + "," + progress + "%");
                }

                @Override
                public void onFinishClearCache() {
                    Logger.t(TAG).i("onFinishClearCache");
                }

                @Override
                public void onUiUpdateProgress(int currentBlock, int sumBlock, int progress) {
                    Logger.t(TAG).i("onUiUpdateProgress:" + currentBlock + "," + sumBlock + "," + progress + "%");
                }


                @Override
                public void onUiUpdateSuccess() {
                    Logger.t(TAG).i("onUiUpdateSuccess");
                }

                @Override
                public void onUiUpdateFail(EUiUpdateError eUiUpdateError) {
                    Logger.t(TAG).i("onUiUpdateFail:" + eUiUpdateError);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
