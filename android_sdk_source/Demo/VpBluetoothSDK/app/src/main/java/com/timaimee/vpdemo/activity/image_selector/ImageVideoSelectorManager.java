package com.timaimee.vpdemo.activity.image_selector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class ImageVideoSelectorManager {

    private static final String TAG = "图片视频选择器";
    private int width = 360, height = 360;
    private boolean isCircle = false;
    private WeakReference<AppCompatActivity> activity = null;

    public static class Option {
        public int width = 360;
        public int height = 360;
        public boolean isCircle = false;
    }

    static final class ClassHolder {
        private static final ImageVideoSelectorManager INSTANCE = new ImageVideoSelectorManager();
    }

    private ImageVideoSelectorManager() {

    }

    public static ImageVideoSelectorManager getInstance() {
        return ClassHolder.INSTANCE;
    }

    /**
     * 启动选择器
     * 页面初始化的时候调用
     */
    private void onLaunch(AppCompatActivity activity) {
        this.activity = new WeakReference<>(activity);
        MediaPickerHelper.getInstance().launch(this.activity.get());
        CameraPhotoHelper.getInstance().launch(this.activity.get());
    }

    public static void launch(AppCompatActivity activity) {
        getInstance().onLaunch(activity);
    }

    /**
     * 释放资源
     * 页面销毁的时候调用
     */
    public void onRelease() {
        MediaPickerHelper.getInstance().release();
        CameraPhotoHelper.getInstance().release();
        this.activity.clear();
        this.activity = null;
    }

    public static void release() {
        getInstance().onRelease();
    }

    /**
     * 选择单张图片
     *
     * @param width    宽
     * @param height   高
     * @param isCircle 是否为圆形
     * @param listener 选择监听
     */
    public static void selectSingleImage(int width, int height, boolean isCircle, OnSingleImageSelectionListener listener) {
        MediaPickerHelper.getInstance().setSingleImageSelectionListener(listener);
        MediaPickerHelper.getInstance().pickSingleImage(width, height, isCircle);
    }

    public ImageVideoSelectorManager width(int width) {
        getInstance().width = width;
        return getInstance();
    }

    public ImageVideoSelectorManager height(int height) {
        this.height = height;
        return this;
    }

    public ImageVideoSelectorManager loadWatchUIType(int width, int height, boolean isCircle) {
        this.width = width;
        this.height = height;
        this.isCircle = isCircle;
        return this;
    }

    public ImageVideoSelectorManager isCircle(boolean isCircle) {
        this.isCircle = isCircle;
        return this;
    }

    private boolean isValid() {
        return width > 0 && height > 0;
    }

    public void selectAndCropSingleImage(OnSingleImageSelectionListener listener) {
        if (!getInstance().isValid()) {
            listener.onError("请设置有效的长和宽");
        }
        MediaPickerHelper.getInstance().setSingleImageSelectionListener(listener);
        MediaPickerHelper.getInstance().pickSingleImage(getInstance().width, getInstance().height, getInstance().isCircle);
    }

    /**
     * 拍照
     *
     * @param width    宽
     * @param height   高
     * @param isCircle 是否为圆形
     * @param listener 拍照监听
     */
    public static void takePhoto(int width, int height, boolean isCircle, OnCameraPhotoListener listener) {
        CameraPhotoHelper.getInstance().takePhoto(width, height, isCircle, listener);
    }

    public void takePhotoAndCrop(OnCameraPhotoListener listener) {
        if (!isValid()) {
            listener.onCameraError("请设置有效的长和宽");
        }
        CameraPhotoHelper.getInstance().takePhoto(width, height, isCircle, listener);
    }

    /**
     * 页面onActivityResult方法处理
     */
    public static void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.t("TAG").e("-handlerActivityResult-: | ");
        MediaPickerHelper.getInstance().handlerCropResult(requestCode, resultCode, data);
        CameraPhotoHelper.getInstance().handleActivityResult(requestCode, resultCode, data);
    }

    /**
     * 页面onRequestPermissionsResult方法处理
     */
    public static void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        CameraPhotoHelper.getInstance().handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface OnSingleImageSelectionListener {
        void onSingleImageSelected(MediaInfo info);

        void onCropSuccess(String cropFileName, String outputPath, Bitmap bitmap, Uri cropFileUri);

        void onCropFailed(String errorMsg);

        void onError(String message);
    }

    public interface OnCameraPhotoListener {
        void onPhotoCaptured(Uri imageUri, String imagePath);

        void onCameraPermissionDenied();

        void onCameraError(String errorMessage);

        void onCropSuccess(String cropFileName, String outputPath, Bitmap bitmap, Uri cropFileUri);

        void onCropFailed(String errorMsg);
    }

}
