package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.customui.WatchUIType;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormAGPSListener;
import com.veepoo.protocol.listener.data.IUiUpdateListener;
import com.veepoo.protocol.listener.oad.OnDownLoadListener;
import com.veepoo.protocol.model.datas.UIDataAGPS;
import com.veepoo.protocol.model.enums.EUIFromType;
import com.veepoo.protocol.model.enums.EUiUpdateError;
import com.veepoo.protocol.util.UiServerHttpUtil;
import com.veepoo.protocol.util.UiUpdateUtil;
import com.veepoo.protocol.util.VPLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UiUpdateAGPSActivity extends Activity {
    private final static String TAG = UiUpdateAGPSActivity.class.getSimpleName();
    Context mContext;
    IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("onResponse code:" + code);
        }
    };

    public TextView mUiAGPSSupportTV;
    public TextView mUiAGPSBaseInfoTV;
    public TextView mSendProgressTv, mDownProgressTv;

    WatchUIType mWatchUIType;
    UiUpdateUtil mUiUpdateUtil;

    UiServerHttpUtil uiUpdateCheckOprate;
    private File mAgpsUpdatefile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiupdate_agps);
        mUiAGPSSupportTV = findViewById(R.id.ui_issupport_agps);
        mUiAGPSBaseInfoTV = findViewById(R.id.ui_baseinfo_agps);
        mSendProgressTv = findViewById(R.id.ui_agps_progress);
        mDownProgressTv = findViewById(R.id.tv_download_info);
        mContext = UiUpdateAGPSActivity.this;
        UiUpdateUtil.getInstance().init(this);
        uiUpdateCheckOprate = new UiServerHttpUtil();
    }

    /**
     * 是否支持
     */
    public void isSupport(View view) {
        if (UiUpdateUtil.getInstance().isSupportChangeCustomAGPS()) {
            mUiAGPSSupportTV.setText("1.支持自定义AGPS");
        } else {
            mUiAGPSSupportTV.setText("1.不支持自定义AGPS");
            Toast.makeText(mContext, "不支持自定义AGPS", Toast.LENGTH_LONG).show();
        }
    }

    UIDataAGPS mUiDataAGPS;

    /**
     * 读取基本的信息(自定义表盘)
     */
    public void readBaseInfo(View view) {
        UiUpdateUtil.getInstance().getAGPSWacthUiInfo(new IUIBaseInfoFormAGPSListener() {
            @Override
            public void onBaseUiInfoFormAgps(UIDataAGPS uiDataAGPS) {
                Logger.t(TAG).i("2.自定义AGPS的基本信息 uiAGPS:" + uiDataAGPS.toString());
                mUiDataAGPS = uiDataAGPS;
                mUiAGPSBaseInfoTV.setText(mUiDataAGPS.toString());
            }
        });

    }



    /**
     * 使用的是apgs
     */
    public void setAgps(View view){

        //LTEPH_GPS_1.rtcm
        String fileRtcm = getExternalFilesDir(null) + File.separator + "LTEPH_GPS_1.rtcm";
        File file = new File(fileRtcm);
        if (file.exists()) {
            Uri mUritempFile = Uri.fromFile(file);
            InputStream inputStream = null;
            try {
                inputStream = mContext.getContentResolver().openInputStream(mUritempFile);

                long timeStampAGPS=0;
                UiUpdateUtil.getInstance().setAGPSTimeStamp(timeStampAGPS);

                UiUpdateUtil.getInstance().startSetUiStream(EUIFromType.A_GPS,inputStream, new IUiUpdateListener() {

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
                        mSendProgressTv.setText("发送中：" + progress + "%");
                    }


                    @Override
                    public void onUiUpdateSuccess() {
                        Logger.t(TAG).i("onUiUpdateSuccess");
                        mSendProgressTv.setText("设置成功");
                    }

                    @Override
                    public void onUiUpdateFail(EUiUpdateError eUiUpdateError) {
                        Logger.t(TAG).i("onUiUpdateFail:" + eUiUpdateError);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Logger.t(TAG).i("can not find rtcm");
        }
    }

    public void downloadAgps(View view) {
        String filePath = getExternalFilesDir(null) + File.separator + "vp_agps.pgl";
        new Thread(new Runnable() {
            @Override
            public void run() {
                uiUpdateCheckOprate.downLoadAGpsFile(isInChina(), filePath, new OnDownLoadListener() {
                    @Override
                    public void onProgress(final float progress) {
                        Logger.t(TAG).i("下载进度:" + progress);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDownProgressTv.setText("下载进度:" + progress * 100);
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                mDownProgressTv.setText("下载完成");
                            }
                        });
                        VPLogger.i("下载完成");
                        mAgpsUpdatefile = new File(filePath);
                    }
                });
            }
        }).start();
    }

    private boolean isInChina() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String simCountryIso = telephonyManager.getSimCountryIso().toUpperCase();
        String networkCountryIso = telephonyManager.getNetworkCountryIso().toUpperCase();

        return "CN".equals(simCountryIso) || "CN".equals(networkCountryIso);
    }


}
