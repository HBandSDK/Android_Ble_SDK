package com.timaimee.vpdemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    /**
     * 将本地图片居中裁剪到指定尺寸并保存到本地。
     *
     * @param inputPath   本地图片文件路径
     * @param outputPath  裁剪后图片保存路径
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 成功返回 true，失败返回 false
     */
    public static boolean centerCropAndSave(String inputPath, String outputPath, int targetWidth, int targetHeight) {
        Bitmap sourceBitmap = BitmapFactory.decodeFile(inputPath);
        if (sourceBitmap == null) {
            Log.e(TAG, "无法加载源图片: " + inputPath);
            return false;
        }

        try {
            Bitmap croppedBitmap = centerCrop(sourceBitmap, targetWidth, targetHeight);
            if (croppedBitmap == null) {
                return false;
            }

            // 保存裁剪后的图片
            return saveBitmap(croppedBitmap, outputPath);
        } finally {
            // 释放源Bitmap，如果它没有被复用
            if (sourceBitmap != null && !sourceBitmap.isRecycled()) {
                sourceBitmap.recycle();
            }
        }
    }

    /**
     * 对给定的Bitmap进行居中裁剪和缩放。
     *
     * @param sourceBitmap 源Bitmap
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 裁剪并缩放后的Bitmap
     */
    private static Bitmap centerCrop(Bitmap sourceBitmap, int targetWidth, int targetHeight) {
        int sourceWidth = sourceBitmap.getWidth();
        int sourceHeight = sourceBitmap.getHeight();

        float targetRatio = (float) targetWidth / targetHeight;
        float sourceRatio = (float) sourceWidth / sourceHeight;

        int cropX = 0;
        int cropY = 0;
        int cropWidth = sourceWidth;
        int cropHeight = sourceHeight;

        if (sourceRatio > targetRatio) {
            // 源图片比目标尺寸宽（水平方向需要裁剪）
            // 需要裁剪的高度保持不变，宽度按比例缩放
            cropWidth = (int) (sourceHeight * targetRatio);
            cropX = (sourceWidth - cropWidth) / 2;
        } else if (sourceRatio < targetRatio) {
            // 源图片比目标尺寸高（垂直方向需要裁剪）
            // 需要裁剪的宽度保持不变，高度按比例缩放
            cropHeight = (int) (sourceWidth / targetRatio);
            cropY = (sourceHeight - cropHeight) / 2;
        }

        // 1. 裁剪操作 (Creates a sub-bitmap)
        Bitmap cropped = Bitmap.createBitmap(sourceBitmap, cropX, cropY, cropWidth, cropHeight);

        // 2. 缩放操作 (Resize to final target dimensions)
        // 裁剪后的图片尺寸可能不完全等于目标尺寸，需要最终缩放
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, targetWidth, targetHeight, true);

        // 释放中间裁剪的Bitmap（如果它不是源Bitmap本身）
        if (cropped != sourceBitmap && !cropped.isRecycled()) {
            cropped.recycle();
        }

        return scaledBitmap;
    }

    /**
     * 将 Bitmap 保存到本地文件。
     */
    private static boolean saveBitmap(Bitmap bitmap, String outputPath) {
        FileOutputStream out = null;
        try {
            File outputFile = new File(outputPath);
            // 确保目录存在
            if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            out = new FileOutputStream(outputFile);
            // 通常使用 JPEG 格式，压缩质量为 90
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            Log.d(TAG, "图片裁剪并保存成功: " + outputPath);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "保存图片失败", e);
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 释放最终的Bitmap
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}
