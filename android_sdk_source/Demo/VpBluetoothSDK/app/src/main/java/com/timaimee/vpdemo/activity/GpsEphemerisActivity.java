package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormAGPSListener;
import com.veepoo.protocol.listener.data.IUiUpdateListener;
import com.veepoo.protocol.listener.oad.OnDownLoadListener;
import com.veepoo.protocol.model.datas.UIDataAGPS;
import com.veepoo.protocol.model.enums.EUIFromType;
import com.veepoo.protocol.model.enums.EUiUpdateError;
import com.veepoo.protocol.util.UiServerHttpUtil;
import com.veepoo.protocol.util.UiUpdateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * GPS星历传输示例 Demo（基于 vpprotocol-SDK）
 * <p>
 * 完整流程对应 GBand 工程中 com.veepoo.home.device.utils.GpsEphemerisHelper 的
 * startUpdateFlow() 一键式星历升级：
 * <pre>
 *   1. UiUpdateUtil.init(context)                       // 预热协议通道（MTU协商 + 注册Notify）
 *   2. UiUpdateUtil.getAGPSWacthUiInfo(...)             // 读取设备AGPS基础信息（含接收地址、CRC、有效时长）
 *   3. 网络检测 + UiServerHttpUtil.downLoadAGpsFile(...)// 从服务器下载星历文件 vp_agps.pgl
 *   4. UiUpdateUtil.startSetUiStream(A_GPS, stream, ...)// 通过蓝牙流式写入设备（擦Flash→分块发送→校验CRC）
 * </pre>
 * 页面同时提供分步按钮，方便单独调试每一步。
 */
public class GpsEphemerisActivity extends Activity {
    private static final String TAG = GpsEphemerisActivity.class.getSimpleName();
    private static final String EPHEMERIS_FILE_NAME = "vp_agps.pgl";

    private Context mContext;
    private UIDataAGPS mUiDataAGPS;

    private TextView mTvStatus;
    private TextView mTvSupport;
    private TextView mTvBaseInfo;
    private TextView mTvDownloadProgress;
    private TextView mTvSendProgress;
    private ProgressBar mPbDownload;
    private ProgressBar mPbSend;
    private Button mBtnOneKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_ephemeris);

        mContext = this;
        mTvStatus = findViewById(R.id.tv_gps_status);
        mTvSupport = findViewById(R.id.tv_gps_support);
        mTvBaseInfo = findViewById(R.id.tv_gps_base_info);
        mTvDownloadProgress = findViewById(R.id.tv_gps_download_progress);
        mTvSendProgress = findViewById(R.id.tv_gps_send_progress);
        mPbDownload = findViewById(R.id.pb_gps_download);
        mPbSend = findViewById(R.id.pb_gps_send);
        mBtnOneKey = findViewById(R.id.btn_gps_onekey);

        // 预热协议通道（MTU协商 + 注册UI数据Notify），与 GpsEphemerisHelper 一致
        UiUpdateUtil.getInstance().init(this);
    }

    /* ============================ 工具方法 ============================ */

    private String getEphemerisFilePath() {
        return getExternalFilesDir(null) + File.separator + EPHEMERIS_FILE_NAME;
    }

    private void setStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvStatus.setText(status);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 判断当前是否在国内环境（决定使用国内/国外星历下载地址）
     * 与 GpsEphemerisHelper.isInChina 逻辑一致
     */
    private boolean isInChina() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String simCountryIso = telephonyManager.getSimCountryIso().toUpperCase();
            String networkCountryIso = telephonyManager.getNetworkCountryIso().toUpperCase();
            return "CN".equals(simCountryIso) || "CN".equals(networkCountryIso);
        } catch (Exception e) {
            return false;
        }
    }

    /* ============================ 分步按钮 ============================ */

    /**
     * a. 是否支持自定义AGPS
     */
    public void isSupport(View view) {
        if (UiUpdateUtil.getInstance().isSupportChangeCustomAGPS()) {
            mTvSupport.setText("支持自定义AGPS");
        } else {
            mTvSupport.setText("不支持自定义AGPS");
            Toast.makeText(mContext, "不支持自定义AGPS", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * b. 读取设备AGPS基础信息（startSetUiStream 前必须先读取，否则会因接收地址未知而失败）
     */
    public void readBaseInfo(View view) {
        setStatus("读取设备AGPS信息...");
        UiUpdateUtil.getInstance().getAGPSWacthUiInfo(new IUIBaseInfoFormAGPSListener() {
            @Override
            public void onBaseUiInfoFormAgps(UIDataAGPS uiDataAGPS) {
                Logger.t(TAG).i("AGPS基本信息:" + uiDataAGPS.toString());
                mUiDataAGPS = uiDataAGPS;
                final String info = uiDataAGPS.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvBaseInfo.setText(info);
                        setStatus("已读取设备AGPS信息");
                    }
                });
            }
        });
    }

    /**
     * c. 下载星历文件（单独步骤）
     */
    public void downloadEphemeris(View view) {
        if (!isNetworkConnected()) {
            Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
            setStatus("网络未连接，无法下载星历");
            return;
        }
        setStatus("正在下载星历文件...");
        mPbDownload.setProgress(0);
        final String filePath = getEphemerisFilePath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new UiServerHttpUtil().downLoadAGpsFile(isInChina(), filePath, new OnDownLoadListener() {
                    @Override
                    public void onProgress(final float progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int p = (int) (progress * 100);
                                mPbDownload.setProgress(p);
                                mTvDownloadProgress.setText("下载进度:" + p + "%");
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvDownloadProgress.setText("下载完成");
                                setStatus("星历文件下载完成，可点击传输");
                            }
                        });
                        Logger.t(TAG).i("星历文件下载完成");
                    }
                });
            }
        }).start();
    }

    /**
     * d. 将已下载的星历文件流式传输至设备（单独步骤）
     */
    public void transferEphemeris(View view) {
        transferEphemerisFile(getEphemerisFilePath());
    }

    /* ============================ 核心：一键式完整流程 ============================ */

    /**
     * ★ 一键星历升级
     * 对应 GpsEphemerisHelper.startUpdateFlow()：
     * init → getAGPSWatchUiInfo → (网络检测 + 下载) → startSetUiStream
     */
    public void startOneKeyUpdate(View view) {
        mBtnOneKey.setEnabled(false);
        mPbDownload.setProgress(0);
        mPbSend.setProgress(0);
        mTvDownloadProgress.setText("进度:0%");
        mTvSendProgress.setText("进度:0%");

        // 1. 初始化并读取设备AGPS信息（前置必要步骤）
        setStatus("1/4 初始化协议通道并读取设备AGPS信息...");
        UiUpdateUtil.getInstance().init(this);
        UiUpdateUtil.getInstance().getAGPSWacthUiInfo(new IUIBaseInfoFormAGPSListener() {
            @Override
            public void onBaseUiInfoFormAgps(UIDataAGPS uiDataAGPS) {
                if (uiDataAGPS == null) {
                    Logger.t(TAG).i("获取设备AGPS信息失败，升级终止");
                    setStatus("获取设备AGPS信息失败，升级终止");
                    mBtnOneKey.setEnabled(true);
                    return;
                }
                mUiDataAGPS = uiDataAGPS;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvBaseInfo.setText(uiDataAGPS.toString());
                    }
                });
                Logger.t(TAG).i("获取设备AGPS信息成功: 有效时长=" + uiDataAGPS.getValidMinute() + "分钟");

                // 2. 检测网络并下载星历文件
                if (!isNetworkConnected()) {
                    Toast.makeText(mContext, "网络未连接", Toast.LENGTH_LONG).show();
                    setStatus("网络未连接，无法下载星历");
                    mBtnOneKey.setEnabled(true);
                    return;
                }
                setStatus("2/4 正在下载星历文件...");
                final String filePath = getEphemerisFilePath();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new UiServerHttpUtil().downLoadAGpsFile(isInChina(), filePath, new OnDownLoadListener() {
                            @Override
                            public void onProgress(final float progress) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int p = (int) (progress * 100);
                                        mPbDownload.setProgress(p);
                                        mTvDownloadProgress.setText("下载进度:" + p + "%");
                                    }
                                });
                            }

                            @Override
                            public void onFinish() {
                                Logger.t(TAG).i("星历文件下载成功，准备传输...");
                                setStatus("3/4 星历下载完成，开始蓝牙传输...");
                                // 3. 执行星历文件至设备的蓝牙流传输
                                transferEphemerisFile(filePath);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * 将已下载的星历文件流式传输至设备（流程第3/4步）
     * 对应 GpsEphemerisHelper.transferGpsEphemerisFile()
     */
    private void transferEphemerisFile(final String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Logger.t(TAG).i("星历文件不存在，传输终止");
            setStatus("星历文件不存在，请先下载");
            mBtnOneKey.setEnabled(true);
            return;
        }

        mPbSend.setProgress(0);
        Uri fileUri = Uri.fromFile(file);
        try {
            final InputStream inputStream = getContentResolver().openInputStream(fileUri);
            // 设置AGPS时间戳（与 UiUpdateAGPSActivity 保持一致，传0）
            UiUpdateUtil.getInstance().setAGPSTimeStamp(0);

            setStatus("4/4 正在通过蓝牙写入星历文件...");
            UiUpdateUtil.getInstance().startSetUiStream(EUIFromType.A_GPS, inputStream, new IUiUpdateListener() {

                @Override
                public void onUiUpdateStart() {
                    Logger.t(TAG).i("蓝牙通道开始写入星历文件...");
                    setStatus("4/4 蓝牙通道已建立，开始写入...");
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPbSend.setProgress(progress);
                            mTvSendProgress.setText("传输进度:" + progress + "%");
                        }
                    });
                }

                @Override
                public void onUiUpdateSuccess() {
                    Logger.t(TAG).i("星历写入设备成功！");
                    setStatus("✅ 星历写入设备成功！");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvSendProgress.setText("传输成功");
                        }
                    });
                    mBtnOneKey.setEnabled(true);
                }

                @Override
                public void onUiUpdateFail(EUiUpdateError eUiUpdateError) {
                    Logger.t(TAG).i("星历写入设备失败:" + eUiUpdateError);
                    if (eUiUpdateError == EUiUpdateError.LOW_BATTERY) {
                        setStatus("❌ 设备电量过低，无法升级");
                    } else {
                        setStatus("❌ 星历写入设备失败: " + eUiUpdateError);
                    }
                    mBtnOneKey.setEnabled(true);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.t(TAG).i("读取文件流异常: " + e.getMessage());
            setStatus("读取星历文件流异常：" + e.getMessage());
            mBtnOneKey.setEnabled(true);
        }
    }
}
