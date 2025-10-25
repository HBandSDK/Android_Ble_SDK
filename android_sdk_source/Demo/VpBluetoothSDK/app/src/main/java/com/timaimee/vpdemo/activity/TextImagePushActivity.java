package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.utils.ImageUtils;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IImageMsgPushListener;
import com.veepoo.protocol.listener.data.ITextMsgPushListener;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormImagePushListener;
import com.veepoo.protocol.model.datas.UIDataImagePush;
import com.veepoo.protocol.util.UiUpdateUtil;
import com.veepoo.protocol.util.thread.HBThreadPools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TextImagePushActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "-图文推送-";

    EditText etSendContent;
    Button btnPushText;
    Button btnPushImage;

    ImageView imagePush01;
    ImageView imagePush02;
    RadioGroup rbImageSelect;

    TextView tvPushInfo;

    private String imageMsgPushDirPath = null;
    private String pushImagePath = null;

    private String selectImage = "image_push_01.png";
    private static final String SUB_PATH = "imageMsgPush";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_image_push);
        initView();
        initData();
    }

    private void initView() {
        etSendContent = findViewById(R.id.etSendContent);
        btnPushText = findViewById(R.id.btnPushText);
        btnPushImage = findViewById(R.id.btnPushImage);
        imagePush01 = findViewById(R.id.ivImagePush01);
        imagePush02 = findViewById(R.id.ivImagePush02);
        rbImageSelect = findViewById(R.id.rbImageSelect);
        tvPushInfo = findViewById(R.id.tvPushInfo);
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
    }

    private void copyImage2Local() {
        File externalFilesDir = getExternalFilesDir(null);
        File targetDir = new File(externalFilesDir, SUB_PATH);
        if (!targetDir.exists()) {
            boolean ret = targetDir.mkdirs();
            Logger.t(TAG).e("-文件夹-: | " + ret);
        }
        imageMsgPushDirPath = targetDir.getAbsolutePath();
        UiUpdateUtil.getInstance().init(this);
        UiUpdateUtil.getInstance().getImagePushUiInfo(new IUIBaseInfoFormImagePushListener() {
            @Override
            public void onBaseUiInfoFormImagePush(UIDataImagePush uiDataImage) {
                Logger.t(TAG).e("-获取图片推送信息-: | " + uiDataImage);
                int width = uiDataImage.getWidth();
                int height = uiDataImage.getHeight();
                HBThreadPools.getInstance().execute(() -> {
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
                });
            }
        });

//        HBThreadPools.getInstance().execute(() -> {
//            copyAssetFileToExternalFilesDir(this, "img_push1.jpg", SUB_PATH);
//            copyAssetFileToExternalFilesDir(this, "img_push2.jpg", SUB_PATH);
//
//            ImageUtils.centerCropAndSave(imageMsgPushDirPath + File.separator + "img_push1.jpg",
//                    imageMsgPushDirPath + File.separator + "image_push_01.png", 390, 450);
//
//            ImageUtils.centerCropAndSave(imageMsgPushDirPath + File.separator + "img_push2.jpg",
//                    imageMsgPushDirPath + File.separator + "image_push_02.png", 390, 450);
//
//            Logger.t(TAG).e("-copyImage2Local-: | 文件夹路径 = " + imageMsgPushDirPath);
//            runOnUiThread(() -> {
//                imagePush01.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_01.png"));
//                imagePush02.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_02.png"));
//            });
//        });


//        File file1 = new File(targetDir, "image_push_01.png");
//        File file2 = new File(targetDir, "image_push_02.png");
//        boolean isFileExists = file1.exists() && file2.exists();
//        if (isFileExists) {
//            imagePush01.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_01.png"));
//            imagePush02.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_02.png"));
//        } else {
//            HBThreadPools.getInstance().execute(() -> {
//                copyAssetFileToExternalFilesDir(this,"image_push_01.png", SUB_PATH);
//                copyAssetFileToExternalFilesDir(this,"image_push_02.png", SUB_PATH);
//                Logger.t(TAG).e("-copyImage2Local-: | 文件夹路径 = " + imageMsgPushDirPath);
//                runOnUiThread(() -> {
//                    imagePush01.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_01.png"));
//                    imagePush02.setImageBitmap(BitmapFactory.decodeFile(imageMsgPushDirPath + File.separator + "image_push_02.png"));
//                });
//            });
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPushText: {
                String content = etSendContent.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    showMsg("内容不能为空");
                    return;
                }
                VPOperateManager.getInstance().pushTextMsg(content, new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {

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
                break;
            }
            case R.id.btnPushImage: {
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
                break;
            }
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
