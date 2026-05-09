package com.timaimee.vpdemo.activity.image_selector;

import static com.timaimee.vpdemo.activity.TextImagePushActivity.SUB_PATH;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.orhanobut.logger.Logger;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class MediaPickerHelper {

    private static final String TAG = "-MediaPickerHelper-";
    private static volatile MediaPickerHelper instance;
    private ActivityResultLauncher<String> singleImagePicker;
    private ImageVideoSelectorManager.OnSingleImageSelectionListener singleImageSelectionListener;
    private int width, height;
    private boolean isCircle = false;

    private String cropFileDir;

    private MediaInfo currentMediaInfo;

    private WeakReference<AppCompatActivity> activity;


    // 选择结果监听接口
    public void launch(AppCompatActivity activity) {
        init(activity);
        initPickers(activity);
    }

    public void release() {
        if (activity != null) {
            activity.clear();
            activity = null;
        }
    }

    public void init(Context context) {
        if (cropFileDir == null) {
            File externalFilesDir = context.getExternalFilesDir(null);
            File targetDir = new File(externalFilesDir, SUB_PATH);
            cropFileDir = targetDir.getAbsolutePath() + File.separator;
        }
    }

    public static MediaPickerHelper getInstance() {
        if (instance == null) {
            synchronized (MediaPickerHelper.class) {
                if (instance == null) {
                    instance = new MediaPickerHelper();
                }
            }
        }
        return instance;
    }

    private void initPickers(AppCompatActivity activity) {
        this.activity = new WeakReference<>(activity);
        // 单张图片选择
        singleImagePicker = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        MediaInfo info = getMediaInfo(uri, false);
                        if (singleImageSelectionListener != null) {
                            singleImageSelectionListener.onSingleImageSelected(info);
                            //开始裁剪
                        }
                        startCrop(info);
                    }
                });
    }

    private void startCrop(MediaInfo info) {
        Logger.t(TAG).e("-startCrop-: | " + info);
        currentMediaInfo = info;
        String outputDir = cropFileDir /*+ "crop_" + System.currentTimeMillis() + ".jpg"*/;
        UCrop.Options options = buildOptions(width, height, outputDir, isCircle);
        UCrop.of(info.uri, Uri.fromFile(new File(outputDir, info.displayName))).withOptions(options).start(activity.get());
    }


    public void setSingleImageSelectionListener(ImageVideoSelectorManager.OnSingleImageSelectionListener listener) {
        this.singleImageSelectionListener = listener;
    }

    /**
     * 选择单张图片
     */
    public void pickSingleImage(int width, int height, boolean isCircle) {
        this.width = width;
        this.height = height;
        this.isCircle = isCircle;
        singleImagePicker.launch("image/*");
//        if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//        } else {
//            Dexter.withContext(activity.get()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
//                @Override
//                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                    Logger.t(TAG).e("onPermissionGranted:Granted::::" + permissionGrantedResponse.getRequestedPermission().toString());
//                    ToastUtils.showDebug("WRITE_EXTERNAL_STORAGE 权限已授予");
//                    Logger.t(TAG).e("======================> requestPostNotifications#start2Next");
//                    pickSingleImage(width, height, isCircle);
//                }
//
//                @Override
//                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                    Logger.t(TAG).e("onPermissionDenied:Denied::::" + permissionDeniedResponse.getRequestedPermission().toString());
//                    ToastUtils.showDebug("WRITE_EXTERNAL_STORAGE 权限已拒绝");
//                    if (mediaSelectionListener != null) {
//                        mediaSelectionListener.onError("需要存储权限");
//                    }
//                }
//
//                @Override
//                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                    Logger.t(TAG).e("onPermissionRationaleShouldBeShown:Granted::::" + permissionRequest.getName());
//                    ToastUtils.showDebug("WRITE_EXTERNAL_STORAGE 权限多次拒绝");
//                    if (mediaSelectionListener != null) {
//                        mediaSelectionListener.onError("需要存储权限");
//                    }
//                }
//            }).check();
//        }
    }

    private int maxSelectionCount = 2;

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity.get(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 获取媒体文件详细信息
     */
    @SuppressLint("Range")
    private MediaInfo getMediaInfo(Uri uri, boolean isVideo) {
        MediaInfo info = new MediaInfo();
        info.uri = uri;
        info.isVideo = isVideo;
        Logger.t(TAG).e("-getMediaInfo-: | " + uri);
        ContentResolver resolver = activity.get().getContentResolver();
        // 获取基础信息
        try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // 文件名和大小
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
                info.displayName = cursor.getString(nameIndex);
                info.size = cursor.getLong(sizeIndex);
                info.mimeType = cursor.getString(mimeTypeIndex);

                // 尝试获取路径
                String[] proj = {MediaStore.Images.Media.DATA};
                try (Cursor pathCursor = resolver.query(uri, proj, null, null, null)) {
                    if (pathCursor != null && pathCursor.moveToFirst()) {
                        int columnIndex = pathCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        info.path = pathCursor.getString(columnIndex);
                    }
                }

                // 获取尺寸
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        getImageDimensions(info, uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (singleImageSelectionListener != null) {
                singleImageSelectionListener.onError("获取文件信息失败");
            }
        }

        // 如果无法直接获取路径，尝试复制到缓存目录
        if (info.path == null) {
            info.path = copyToCache(uri);
        }

        return info;
    }

    private void getImageDimensions(MediaInfo info, Uri uri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只解析边界，不加载完整图片

            InputStream input = activity.get().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);
            if (input != null) input.close();

            info.width = options.outWidth;
            info.height = options.outHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将URI指向的文件复制到缓存目录
     */
    private String copyToCache(Uri uri) {
        File cacheDir = activity.get().getCacheDir();
        String fileName = "media_" + System.currentTimeMillis();
        File outputFile = new File(cacheDir, fileName);

        try (InputStream in = activity.get().getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void handlerCropResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri uri = UCrop.getOutput(data);
                try {
                    String path = uri.getPath();
                    Logger.t(TAG).e("-handlerCropResult-: | path = " + path);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.get().getContentResolver(), uri);
                    if (singleImageSelectionListener != null) {
                        String cropFileName = currentMediaInfo.displayName;
                        String outputPath = cropFileDir + cropFileName;
                        singleImageSelectionListener.onCropSuccess(cropFileName, outputPath, bitmap, uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (singleImageSelectionListener != null) {
                        singleImageSelectionListener.onCropFailed(e.getMessage());
                    }
                }
            }
        }
    }

    private UCrop.Options buildOptions(int width, int height, String outputDir, boolean isCircle) {
        Logger.t(TAG).e("-buildOptions-: | width = " + width + " height = " + height);
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(false);//是否显示裁剪菜单栏
        options.setFreeStyleCropEnabled(false);//裁剪框or图片拖动
        options.setShowCropFrame(true);//是否显示裁剪边框
        options.setShowCropGrid(true);//是否显示裁剪框
        options.setCircleDimmedLayer(isCircle);//是否圆形裁剪
        if (isCircle) {
            // 圆形必须 1:1 比例
            options.withAspectRatio(1, 1);
            // 圆形裁剪框 隐藏网格 + 隐藏边框（更美观）
            options.setShowCropGrid(false);
            options.setShowCropFrame(false);
        } else {
            // 普通裁剪使用传入比例
            options.withAspectRatio(width, height);
        }
        options.withAspectRatio(width, height);
        options.withMaxResultSize(width, height);
        options.isCropDragSmoothToCenter(true);//裁剪并自动拖拽到中间
        options.setCropOutputPathDir(outputDir);
        options.isUseCustomLoaderBitmap(true);//设置自定义Loader Bitmap
           String MIME_TYPE_GIF = "image/gif";
           String MIME_TYPE_WEBP = "image/webp";
        options.setSkipCropMimeType(MIME_TYPE_GIF, MIME_TYPE_WEBP);//设置跳过的裁剪类型
        options.isForbidCropGifWebp(true);//设置禁止裁剪GIF
        options.isForbidSkipMultipleCrop(false);
        options.setMaxScaleMultiplier(4);//最大的放大倍数
        options.isDarkStatusBarBlack(true);
        options.setStatusBarColor(Color.parseColor("#f5f5f5"));//状态栏颜色
        options.setToolbarColor(Color.parseColor("#f5f5f5"));//toolbar颜色
//        options.setDimmedLayerColor(ContextCompat.getColor(HBandApplication.instance, R.color.ecg_bg)); //背景色
//        options.setActiveControlsWidgetColor(Color.RED);
//        options.setRootViewBackgroundColor(Color.BLACK);
//        options.setLogoColor(Color.GREEN);
//        options.setCropGridColor(ContextCompat.getColor(HBandApplication.instance, R.color.sleep_start));
        return options;
    }

}
