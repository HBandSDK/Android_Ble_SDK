package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.customui.WatchUIType;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormAGPSListener;
import com.veepoo.protocol.listener.data.IUiUpdateListener;
import com.veepoo.protocol.model.datas.UIDataAGPS;
import com.veepoo.protocol.model.enums.EUIFromType;
import com.veepoo.protocol.model.enums.EUiUpdateError;
import com.veepoo.protocol.util.UiUpdateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UiUpdateAGPSActivity extends Activity {
    private final static String TAG = UiUpdateAGPSActivity.class.getSimpleName();
    Context mContext;
    IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("onResponse code:" + code);
        }
    };

    @BindView(R.id.ui_issupport_agps)
    public TextView mUiAGPSSupportTV;

    @BindView(R.id.ui_baseinfo_agps)
    public TextView mUiAGPSBaseInfoTV;



    @BindView(R.id.ui_agps_progress)
    public TextView mSendProgressTv;

    WatchUIType mWatchUIType;
    UiUpdateUtil mUiUpdateUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiupdate_agps);
        ButterKnife.bind(this);
        mContext = UiUpdateAGPSActivity.this;
        mUiUpdateUtil = new UiUpdateUtil(mContext);

    }

    /**
     * 是否支持
     */
    @OnClick(R.id.is_support_agps_ui)
    public void isSupport() {
        if (mUiUpdateUtil.isSupportChangeCustomAGPS()) {
            mUiAGPSSupportTV.setText("1.支持自定义AGPS");
            mUiUpdateUtil.init();
        } else {
            mUiAGPSSupportTV.setText("1.不支持自定义AGPS");
            Toast.makeText(mContext, "不支持自定义AGPS", Toast.LENGTH_LONG).show();
        }
    }

    UIDataAGPS mUiDataAGPS;

    /**
     * 读取基本的信息(自定义表盘)
     */

    @OnClick(R.id.read_base_info_agps)
    public void readBaseInfo() {
        mUiUpdateUtil.getAGPSWacthUiInfo(new IUIBaseInfoFormAGPSListener() {
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
    @OnClick(R.id.ui_agps_change_img_set)
    public void setAgps(){

        //LTEPH_GPS_1.rtcm
        String fileRtcm = getExternalFilesDir(null) + File.separator + "LTEPH_GPS_1.rtcm";
        File file = new File(fileRtcm);
        if (file.exists()) {
            Uri mUritempFile = Uri.fromFile(file);
            InputStream inputStream = null;
            try {
                inputStream = mContext.getContentResolver().openInputStream(mUritempFile);

                long timeStampAGPS=0;
                mUiUpdateUtil.setAGPSTimeStamp(timeStampAGPS);

                mUiUpdateUtil.startSetUiStream(EUIFromType.A_GPS,inputStream, new IUiUpdateListener() {

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


}
