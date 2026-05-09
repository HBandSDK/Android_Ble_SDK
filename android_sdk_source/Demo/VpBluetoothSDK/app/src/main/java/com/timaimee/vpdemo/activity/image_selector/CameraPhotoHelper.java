package com.timaimee.vpdemo.activity.image_selector;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.timaimee.vpdemo.activity.TextImagePushActivity.SUB_PATH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.orhanobut.logger.Logger;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraPhotoHelper {
    private static final String TAG = "CameraPhotoHelper";
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final int REQUEST_CAMERA_CAPTURE = 1002;

    private WeakReference<Activity> activity;
    private String currentPhotoPath;
    private Uri currentPhotoUri;
    private ImageVideoSelectorManager.OnCameraPhotoListener listener;

    private int width, height;
    private boolean isCircle = false;


    private static class InnerHolder {
        @SuppressLint("StaticFieldLeak")
        private static final CameraPhotoHelper INSTANCE = new CameraPhotoHelper();
    }

    private CameraPhotoHelper() {
    }

    public void init(Context context) {
        if (cropFileDir == null) {
            File externalFilesDir = context.getExternalFilesDir(null);
            File targetDir = new File(externalFilesDir, SUB_PATH);
            cropFileDir = targetDir.getAbsolutePath() + File.separator;
        }
    }

    public static CameraPhotoHelper getInstance() {
        return InnerHolder.INSTANCE;
    }

    public void launch(Activity activity) {
        this.activity = new WeakReference<>(activity);
        init(activity);
    }

    public void release() {
        if (activity != null) {
            activity.clear();
            activity = null;
        }
    }

    /**
     * 启动相机拍照
     */
    public void takePhoto(int width, int height, boolean isCircle, ImageVideoSelectorManager.OnCameraPhotoListener listener) {
        this.width = width;
        this.height = height;
        this.isCircle = isCircle;
        this.listener = listener;
        if (checkCameraPermission()) {
            Logger.t(TAG).e("-takePhoto-: | 开始拍照 dispatchTakePictureIntent");
            dispatchTakePictureIntent();
        } else {
            Logger.t(TAG).e("-takePhoto-: | 开始拍照 requestCameraPermission");
            requestCameraPermission();
        }
    }

    /**
     * 处理拍照返回结果
     */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CAPTURE) { //拍照
                if (currentPhotoUri != null) {
                    // Android 10+ 使用MediaStore的方式
                    if (listener != null) {
                        listener.onPhotoCaptured(currentPhotoUri, getRealPathFromUri(currentPhotoUri));
                    }
                    startCrop(currentPhotoUri, getRealPathFromUri(currentPhotoUri));
                } else if (currentPhotoPath != null) {
                    // 传统方式
                    File photoFile = new File(currentPhotoPath);
                    if (photoFile.exists()) {
                        Uri photoUri = FileProvider.getUriForFile(activity.get(),
                                activity.get().getApplicationContext().getPackageName() + ".provider",
                                photoFile);
                        if (listener != null) {
                            listener.onPhotoCaptured(photoUri, currentPhotoPath);
                        }
                        startCrop(photoUri, currentPhotoPath);
                    } else {
                        if (listener != null) {
                            listener.onCameraError("照片文件不存在");
                        }
                    }
                }
            }

            if (requestCode == UCrop.REQUEST_CROP) { //裁剪
                Uri uri = UCrop.getOutput(data);
                try {
                    String path = uri.getPath();
                    String[] items = path.split("/");
                    Logger.t(TAG).e("-handlerCropResult-: | path = " + path);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.get().getContentResolver(), uri);
                    if (listener != null) {
                        String cropFileName = items[items.length - 1];
                        String outputPath = cropFileDir + cropFileName;
                        listener.onCropSuccess(cropFileName, outputPath, bitmap, uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onCropFailed(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 处理权限请求结果
     */
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                if (listener != null) {
                    listener.onCameraPermissionDenied();
                }
            }
        }
    }

    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            return ContextCompat.checkSelfPermission(activity.get(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(activity.get(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                            ContextCompat.checkSelfPermission(activity.get(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(activity.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }

    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions((Activity) activity.get(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            ActivityCompat.requestPermissions((Activity) activity.get(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void dispatchTakePictureIntent() {
        Logger.t(TAG).e("-dispatchTakePictureIntent-: | 0");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 确保有相机应用可以处理该Intent
        if (takePictureIntent.resolveActivity(activity.get().getPackageManager()) != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ 使用MediaStore
                    createImageFileForQ();
                } else {
                    // 传统方式创建文件
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        Logger.t(TAG).e("-dispatchTakePictureIntent-: | 1");
                        currentPhotoPath = photoFile.getAbsolutePath();
                        Uri photoURI = FileProvider.getUriForFile(activity.get(),
                                activity.get().getApplicationContext().getPackageName() + ".provider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }
                    ((Activity) activity.get()).startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
                }
                Logger.t(TAG).e("-dispatchTakePictureIntent-: | 2");
            } catch (IOException e) {
                Log.e(TAG, "创建照片文件错误", e);
                if (listener != null) {
                    listener.onCameraError("无法创建照片文件");
                }
            } catch (Exception e) {
                Log.e(TAG, "启动相机错误", e);
                if (listener != null) {
                    listener.onCameraError("无法启动相机");
                }
            }
        } else {
            if (listener != null) {
                listener.onCameraError("没有可用的相机应用");
            }
        }
    }

    private File createImageFile() throws IOException {
        // 创建唯一的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = activity.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            storageDir = activity.get().getFilesDir();
        }
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir       /* 目录 */
        );
        return image;
    }

    private void createImageFileForQ() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        ContentResolver resolver = activity.get().getContentResolver();
        ContentValues contentValues = new ContentValues();

        // 对于 Android Q (10) 及以上版本，使用 RELATIVE_PATH 而不是直接设置 DATA
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            // 兼容旧版本
            File imgFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), imageFileName);
            contentValues.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
        }

        currentPhotoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (currentPhotoUri != null) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            activity.get().startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
        } else {
            throw new IOException("无法创建照片URI");
        }
    }

    private String getRealPathFromUri(Uri uri) {
        if (uri == null) return null;

        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            return uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            try (Cursor cursor = activity.get().getContentResolver().query(
                    uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                Log.e(TAG, "获取真实路径失败", e);
            }
        }
        return null;
    }

    private String cropFileDir;

    private void startCrop(Uri imageUri, String imagePath) {
        Logger.t(TAG).e("-startCrop-: imageUri | " + imageUri.getPath());
        Logger.t(TAG).e("-startCrop-: imagePath | " + imagePath);
        ///storage/emulated/0/Pictures/JPEG_20250616_164856.jpg
        String[] split = imagePath.split("/");
        String fileName = split[split.length - 1];
        String outputDir = cropFileDir /*+ "crop_" + System.currentTimeMillis() + ".jpg"*/;
        UCrop.Options options = buildOptions(width, height, outputDir, isCircle);
        Uri from = Uri.fromFile(new File(imagePath));
        Logger.t(TAG).e("-startCrop-: uri | " + from.getPath());
        Logger.t(TAG).e("-startCrop-: outputPath | " + (outputDir + fileName));
        UCrop.of(from, Uri.fromFile(new File(outputDir, fileName))).withOptions(options).start((Activity) activity.get());
    }

    private UCrop.Options buildOptions(int width, int height, String outputDir, boolean isCircle) {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(false);//是否显示裁剪菜单栏
        options.setFreeStyleCropEnabled(false);//裁剪框or图片拖动
        options.setShowCropFrame(true);//是否显示裁剪边框
        options.setShowCropGrid(true);//是否显示裁剪框
        options.setCircleDimmedLayer(isCircle);//是否圆形裁剪
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
        return options;
    }

}

