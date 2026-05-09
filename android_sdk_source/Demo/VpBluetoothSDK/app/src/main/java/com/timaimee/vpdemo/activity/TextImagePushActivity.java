package com.timaimee.vpdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.image_selector.ImageVideoSelectorManager;
import com.timaimee.vpdemo.activity.image_selector.MediaInfo;
import com.timaimee.vpdemo.utils.ImageUtils;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IImageMsgPushListener;
import com.veepoo.protocol.listener.data.ITextMsgPushListener;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormImagePushListener;
import com.veepoo.protocol.model.datas.UIDataImagePush;
import com.veepoo.protocol.util.UiUpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TextImagePushActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "-图文推送-";

    EditText etSendContent;
    Button btnPushText;
    Button btnPushImage;
    Button btnSelectImage;

    ImageView imagePush01;
    ImageView imagePush02;
    RadioGroup rbImageSelect;
    EditText etWidth;
    EditText etHeight;

    TextView tvPushInfo;

    private String imageMsgPushDirPath = null;
    private String pushImagePath = null;

    private String selectImage = "image_push_01.png";
    public static final String SUB_PATH = "imageMsgPush";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageVideoSelectorManager.launch(this);
        setContentView(R.layout.activity_text_image_push);
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageVideoSelectorManager.handlerActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImageVideoSelectorManager.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageVideoSelectorManager.release();
    }

    private void initView() {
        etSendContent = findViewById(R.id.etSendContent);
        btnPushText = findViewById(R.id.btnPushText);
        btnPushImage = findViewById(R.id.btnPushImage);
        imagePush01 = findViewById(R.id.ivWatchFace1);
        imagePush02 = findViewById(R.id.ivWatchFace2);
        rbImageSelect = findViewById(R.id.rbImageSelect);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        tvPushInfo = findViewById(R.id.tvPushInfo);
        etWidth = findViewById(R.id.etWidth);
        etHeight = findViewById(R.id.etHeight);
    }

    private void initData() {
        btnPushText.setOnClickListener(this);
        btnPushImage.setOnClickListener(this);
        copyImage2Local();
        rbImageSelect.check(R.id.rbImage01);
        pushImagePath = imageMsgPushDirPath + File.separator + selectImage;
        rbImageSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbImage01) {
                    selectImage = "image_push_01.png";
                }
                if (checkedId == R.id.rbImage02) {
                    selectImage = "image_push_02.png";
                }
                pushImagePath = imageMsgPushDirPath + File.separator + selectImage;
                tvPushInfo.setText("图片本地地址：\n" + pushImagePath);
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiDataImage == null) {
                    showMsg("手表尺寸无法获取");
                    return;
                }
                String widthStr = etWidth.getText().toString();
                String heightStr = etHeight.getText().toString();
                if (TextUtils.isEmpty(widthStr) || TextUtils.isEmpty(heightStr)) {
                    selectAndCropPicture(uiDataImage.getWidth(), uiDataImage.getHeight());
                } else {
                    selectAndCropPicture(Integer.parseInt(widthStr), Integer.parseInt(heightStr));
                }
            }
        });
    }

    private void selectAndCropPicture(int aspectRatioX, int aspectRatioY) {
        Logger.t(TAG).i("selectAndCropPicture 选择和裁剪照片 aspectRatioX = " + aspectRatioX + " , aspectRatioY = " + aspectRatioY);
        ImageVideoSelectorManager
                .getInstance()
                .width(aspectRatioX)
                .height(aspectRatioY)
                .isCircle(false)
                .selectAndCropSingleImage(new ImageVideoSelectorManager.OnSingleImageSelectionListener() {
                    @Override
                    public void onSingleImageSelected(MediaInfo info) {

                    }

                    @Override
                    public void onCropSuccess(String cropFileName, String outputPath, Bitmap bitmap, Uri cropFileUri) {
                        Drawable drawable = new BitmapDrawable(null, bitmap);
                        Logger.t(TAG).e("-onCropSuccess-  cropFileName = " + cropFileName);
                        Logger.t(TAG).e("-onCropSuccess-  outputPath = " + outputPath);
                        Logger.t(TAG).e("-onCropSuccess-  bitmap = " + bitmap.getByteCount());
                        imagePush01.setImageBitmap(bitmap);
                        pushImagePath = outputPath;
                    }

                    @Override
                    public void onCropFailed(String errorMsg) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    UIDataImagePush uiDataImage = null;

    private void copyImage2Local() {
        File externalFilesDir = getExternalFilesDir(null);
        File targetDir = new File(externalFilesDir, SUB_PATH);
        if (!targetDir.exists()) {
            boolean ret = targetDir.mkdirs();
            Logger.t(TAG).e("-文件夹-: | " + ret);
        }
        Logger.t(TAG).e("-文件夹-: | >>" + targetDir.getAbsolutePath());

        imageMsgPushDirPath = targetDir.getAbsolutePath();
        UiUpdateUtil.getInstance().init(this);
        UiUpdateUtil.getInstance().getImagePushUiInfo(new IUIBaseInfoFormImagePushListener() {
            @Override
            public void onBaseUiInfoFormImagePush(UIDataImagePush uiDataImage) {
                TextImagePushActivity.this.uiDataImage = uiDataImage;
                Logger.t(TAG).e("-获取图片推送信息-: | " + uiDataImage);
                int width = uiDataImage.getWidth();
                int height = uiDataImage.getHeight();
                etWidth.setText(width + "");
                etHeight.setText(height + "");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        copyAssetFileToExternalFilesDir(TextImagePushActivity.this, "img_push1.jpg", SUB_PATH);
                        copyAssetFileToExternalFilesDir(TextImagePushActivity.this, "img_push2.jpg", SUB_PATH);

                        ImageUtils.centerCropAndSave(imageMsgPushDirPath + File.separator + "img_push1.jpg",
                                imageMsgPushDirPath + File.separator + "image_push_01.png", width, height);

                        ImageUtils.centerCropAndSave(imageMsgPushDirPath + File.separator + "img_push2.jpg",
                                imageMsgPushDirPath + File.separator + "image_push_02.png", width, height);

                        Logger.t(TAG).e("-copyImage2Local-: | 文件夹路径 = " + imageMsgPushDirPath);
                        runOnUiThread(() -> {
                            imagePush01.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_01.png"));
                            imagePush02.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_02.png"));
                        });
                    }
                }).start();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnPushText) {
            String content = etSendContent.getText().toString();
            if (TextUtils.isEmpty(content)) {
                showMsg("内容不能为空");
                return;
            }
            tvPushInfo.setText("开始文本推送");
            VPOperateManager.getInstance().pushTextMsg(content, new BleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    if (code != Code.REQUEST_SUCCESS) {
                        tvPushInfo.setText("蓝牙数据发送失败");
                    }
                }
            }, new ITextMsgPushListener() {
                @Override
                public void onTextMsgPushSuccess() {
                    tvPushInfo.setText("文本推送成功");
                }

                @Override
                public void onTextMsgPushFailed() {
                    tvPushInfo.setText("文本推送失败");
                }

                @Override
                public void onFunctionNotSupport() {
                    tvPushInfo.setText("不支持该功能");
                }
            });
        } else if (v.getId() == R.id.btnPushImage) {
            tvPushInfo.setText("开始图片推送");
            VPOperateManager.getInstance().pushImageMsg(pushImagePath, new IImageMsgPushListener() {
                @Override
                public void onImageMsgPushSuccess() {
                    tvPushInfo.setText("图片推送成功");
                }

                @Override
                public void onImageMsgPushProgress(int currentBlock, int sumBlock, int progress) {
                    tvPushInfo.setText("图片推送进度：" + progress + "%");
                }

                @Override
                public void onImageMsgPushFailed(ErrorCode errorCode) {
                    tvPushInfo.setText("图片推送错误：" + errorCode.info);
                }
            });
        }
    }


    /**
     * 将 Assets 目录下的指定文件复制到应用的外部私有文件目录下的指定子路径。
     * * @param context 上下文对象，用于访问 Assets 和文件系统。
     *
     * @param assetFileName Assets 目录下的文件名（例如："my_image.png"）。
     * @param subPath       目标子路径（例如："hband/jlDail"）。
     * @return 复制成功后的目标文件的绝对路径，如果复制失败则返回 null。
     */
    public static String copyAssetFileToExternalFilesDir(
            Context context,
            String assetFileName,
            String subPath) {

        // 1. 获取外部私有文件根目录（路径如：/storage/.../Android/data/包名/files）
        File filesDir = context.getExternalFilesDir(null);
        if (filesDir == null) {
            // Log.e(TAG, "无法获取外部私有文件目录。");
            return null;
        }

        // 2. 构建目标目录
        File targetDir = new File(filesDir, subPath);
        if (!targetDir.exists()) {
            // 尝试创建所有必要的目录
            if (!targetDir.mkdirs()) {
                // Log.e(TAG, "无法创建目标目录: " + targetDir.getAbsolutePath());
                return null;
            }
        }

        // 3. 构建目标文件
        File targetFile = new File(targetDir, assetFileName);

        // 使用 try-with-resources 确保流的自动关闭
        try (InputStream inputStream = context.getAssets().open(assetFileName);
             OutputStream outputStream = new FileOutputStream(targetFile)) {

            // 4. 读取 Assets 流，并写入到目标文件流
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            // Log.i(TAG, "文件复制成功到: " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();

        } catch (IOException e) {
            // Log.e(TAG, "复制 Assets 文件失败: " + assetFileName, e);
            e.printStackTrace();
            return null; // 复制失败
        }
    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
