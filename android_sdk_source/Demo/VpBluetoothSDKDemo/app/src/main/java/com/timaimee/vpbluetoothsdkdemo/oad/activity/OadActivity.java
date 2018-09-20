
package com.timaimee.vpbluetoothsdkdemo.oad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import com.timaimee.vpbluetoothsdkdemo.R;
import com.timaimee.vpbluetoothsdkdemo.oad.service.DfuService;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.err.OadErrorState;
import com.veepoo.protocol.listener.oad.OnFindOadDeviceListener;
import com.veepoo.protocol.listener.oad.OnUpdateCheckListener;
import com.veepoo.protocol.model.OadFileBean;
import com.veepoo.protocol.model.settings.OadSetting;

import vpno.nordicsemi.android.dfu.DfuLogListener;
import vpno.nordicsemi.android.dfu.DfuProgressListener;
import vpno.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import vpno.nordicsemi.android.dfu.DfuServiceInitiator;
import vpno.nordicsemi.android.dfu.DfuServiceListenerHelper;


/**
 * 固件升级包含5个步骤，但是经过集成，目前只要调用2个方法就可以了
 * 5个步骤分别是 版本判断-->文件判断-->进入固件升级模式-->查找固件升级下的设备-->执行官方升级程序
 * 2个方法分别是 checkVersionAndFile-->startOad
 * <p/>
 * checkVersionAndFile的主要作用是：版本判断-> 文件判断-->进入固件升级模式-->查找固件升级下的设备
 * startOad的主要作用是：执行官方升级程序
 * <p/>
 * findOadModelDevice的主要作用：进入固件升级模式-->查找固件升级下的设备，但这个方法一般不会被调用，主要用于做容错判断。
 * Created by timaimee on 2016/8/17.
 */
public class OadActivity extends Activity implements View.OnClickListener {
    private Context mContext = OadActivity.this;
    private final static String TAG = OadActivity.class.getSimpleName();

    OadSetting oadSetting;
    TextView deviceStateTv, upLoadingTv, textPercentTv;
    Button oadButton;
    ProgressBar progressBar;

    private String mOadAddress, mOadFileName;
    private int failCount = 0;
    private boolean isCanEnterOadModel = false, isFindOadDevice = false;
    private final static int MAX_ALLOW_FAIL_COUNT = 5;
    String deviceVersion;
    String deviceTestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oad_custom);
        oadSetting = getIntData();
        initView();
        registerListener();
    }

    /**
     * 注册固件升级的操作的监听
     */
    private void registerListener() {
        /**
         * 注册升级过程的监听，可在回调中做相应的处理
         */
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        /**
         * 注册升级过程的监听，主要是用于打印升级的日志，方便调试
         */
        DfuServiceListenerHelper.registerLogListener(this, mDfuLogListener);
    }

    /**
     * 固件升级必要的几个参数
     *
     * @return
     */
    private OadSetting getIntData() {
        boolean isOadModel = getIntent().getBooleanExtra("isoadmodel", false);
        int deviceNumber = getIntent().getIntExtra("devicenumber", 0);
        String deviceAddress = getIntent().getStringExtra("deviceaddress");
        String deviceVersion = getIntent().getStringExtra("deviceversion");
        String deviceTestVersion = getIntent().getStringExtra("devicetestversion");
        OadSetting oadSetting = new OadSetting(deviceAddress, deviceVersion, deviceTestVersion, deviceNumber, isOadModel);
        return oadSetting;
    }

    /**
     * 初使化界面
     */
    private void initView() {
        deviceStateTv = (TextView) findViewById(R.id.oad_devicestate);
        upLoadingTv = (TextView) findViewById(R.id.oad_uploading);
        textPercentTv = (TextView) findViewById(R.id.oad_upload_progress_gv);
        oadButton = (Button) findViewById(R.id.oad_update);
        progressBar = (ProgressBar) findViewById(R.id.oad_upload_progress);
        oadButton.setOnClickListener(this);

        initDeviceStateView();
    }

    /**
     * 显示当前设备的基本信息
     */
    private void initDeviceStateView() {
        String deviceVersion = oadSetting.getDeviceVersion();
        String deviceTestVersion = oadSetting.getDeviceTestVersion();
        StringBuffer sb = new StringBuffer();
        sb.append("设备地址：" + oadSetting.getDeviceAddress());
        sb.append("\n");
        sb.append("设备编号：" + oadSetting.getDeviceNumber());
        sb.append("\n");
        sb.append("正式版本号：" + deviceVersion);
        sb.append("\n");
        sb.append("测试版本号：" + deviceTestVersion);
        sb.append("\n");
        sb.append("是否DFU模式：" + oadSetting.isOadModel());
        deviceStateTv.setText(sb.toString());

    }

    @Override
    public void onClick(View v) {
        if (isCanEnterOadModel) {
            if (isFindOadDevice) {
                /**
                 * 当然执行这步的可能很少
                 * 如果设备版本以及文件校验成功，设备也进入升级模式，同时固件升级下的设备也被找到，就执行官方固件升级程序
                 */
                startOad();
            } else {
                /**
                 * 当然执行这步的可能很少
                 * 如果设备版本以及文件校验成功，就先让设备进入升级模式，同时查找固件升级下的设备
                 */
                findOadModelDevice();
            }
        } else {

            checkVersionAndFile();
        }
    }

    /**
     * 升级前的检验，设备版本以及文件校验，同时查找固件升级下的设备
     */
    private void checkVersionAndFile() {

        Logger.t(TAG).i("升级前：版本验证->文件验证->查找目标设备");
        VPOperateManager.getMangerInstance(mContext).checkVersionAndFile(oadSetting, new OnUpdateCheckListener() {

            /**
             * 获取服务器上的最新版本信息
             * @param deviceNumber 当前的设备号
             * @param deviceVersion 当前设备的最新版本
             * @param des 升级的文本提示
             */
            public void onNetVersionInfo(int deviceNumber, String deviceVersion, String des) {
                Logger.t(TAG).i("服务器版本信息,设备号=" + deviceNumber + ",最新版本=" + deviceVersion + ",升级描述=" + des);
            }


            /**
             * 如果有从服务上下载文件，返回下载的进度。文件很小，目前不会超过100k,下载很快，可以在这个回调后不做任何操作
             * @param progress
             */
            @Override
            public void onDownLoadOadFile(float progress) {
                Logger.t(TAG).i("从服务器下载文件,进度=" + progress);
            }

            /**
             * 表示版本检验或者是文件检验没有通过
             * @param endState
             */
            @Override
            public void onCheckFail(int endState) {
                switch (endState) {
                    case OadErrorState.UNCONNECT_NETWORK:
                        Logger.t(TAG).i("网络出错");
                        break;
                    case OadErrorState.UNCONNECT_SERVER:
                        Logger.t(TAG).i("服务器连接不上");
                        break;
                    case OadErrorState.SERVER_NOT_HAVE_NEW:
                        Logger.t(TAG).i("服务器无此版本");
                        break;
                    case OadErrorState.DEVICE_IS_NEW:
                        Logger.t(TAG).i("设备是最新版本");
                        break;
                    case OadErrorState.OAD_FILE_UNEXITS:
                        Logger.t(TAG).i("文件不存在");
                        break;
                    case OadErrorState.OAD_FILE_MD5_UNSAME:
                        Logger.t(TAG).i("文件md5不一致");
                        break;
                }
            }

            /**
             * 表示版本以及文件校验通过，通过此回调返回升级文件的文件路径，调用startOad方法会用到此参数
             * @param oadFileName
             */
            @Override
            public void onCheckSuccess(String oadFileName) {
                Logger.t(TAG).i("版本确认无误，文件确认无误");
                mOadFileName = oadFileName;
                if (!TextUtils.isEmpty(mOadFileName)) {
                    isCanEnterOadModel = true;
                }
            }

            /**
             * 表示设备成功进入固件模式并被找到，通过此回调返回升级设备在固件升级模式下的地址，调用startOad方法时会用到此参数
             * 为什么前面已经知道了设备地址， 还要用回调的地址？因为通过仔细观察，你就会发现设备通过正常模式下的升级，进入固件升级模式后，设备地址会自动加1
             * @param oadAddress
             */
            @Override
            public void findOadDevice(String oadAddress) {
                Logger.t(TAG).i("找到OAD模式下的设备了");
                mOadAddress = oadAddress;
                if (!TextUtils.isEmpty(mOadAddress)) {
                    isFindOadDevice = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startOad();
                        }
                    });
                }
            }
        });
    }

    /**
     * 如果设备版本以及升级文件没有问题，但是没有查找固件升级下的设备，就执行这个步骤
     * 这个不般不会被调用
     */
    private void findOadModelDevice() {
        VPOperateManager.getMangerInstance(mContext).findOadModelDevice(oadSetting, new OnFindOadDeviceListener() {
            @Override
            public void findOadDevice(String oadAddress) {
                startOad();
            }
        });
    }


    /**
     * 执行官方升级程序
     * mOadFileName 通过onCheckSuccess回调取得
     * mOadAddress 通过findOadDevice回调取得
     */
    private void startOad() {
        Logger.t(TAG).i("执行升级程序，最多尝试5次");
        showProgressBar();
        Boolean isBinder = false;
        final DfuServiceInitiator dfuServiceInitiator = new DfuServiceInitiator(mOadAddress)
                .setDeviceName("veepoo")
                .setKeepBond(isBinder);
        dfuServiceInitiator.setZip(null, mOadFileName);
        dfuServiceInitiator.start(this, DfuService.class);

    }

    /**
     * 升级过程的监听，回调会返回相应的状态
     */
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            //   progressBar.setIndeterminate(true);
            //   textPercentTv.setText(R.string.dfu_status_connecting);
            progressBar.setIndeterminate(true);
            textPercentTv.setText(getResources().getString(R.string.dfu_status_connecting));
            Logger.t(TAG).e("onDeviceConnecting");
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            //    progressBar.setIndeterminate(true);
            //    textPercentTv.setText(R.string.dfu_status_starting);
            progressBar.setIndeterminate(true);
            textPercentTv.setText(getResources().getString(R.string.dfu_status_starting));
            Logger.t(TAG).e("onDfuProcessStarting");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Logger.t(TAG).e("onDfuProcessStarted");
            super.onDfuProcessStarted(deviceAddress);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Logger.t(TAG).e("onProgressChanged-" + percent);
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(percent);
            textPercentTv.setText(percent + "%");
            progressBar.setIndeterminate(false);
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            super.onDeviceConnected(deviceAddress);

        }


        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            super.onEnablingDfuMode(deviceAddress);

        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            progressBar.setIndeterminate(true);
            textPercentTv.setText(getResources().getString(R.string.dfu_status_validating));
            super.onFirmwareValidating(deviceAddress);
            Logger.t(TAG).e("onFirmwareValidating");
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            progressBar.setIndeterminate(true);
            textPercentTv.setText(getResources().getString(R.string.dfu_status_disconnecting));
            super.onDeviceDisconnecting(deviceAddress);
            Logger.t(TAG).e("onDeviceDisconnecting");
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
            Logger.t(TAG).e("onDeviceDisconnected");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            Logger.t(TAG).e("onDfuCompleted");
            textPercentTv.setText(getResources().getString(R.string.dfu_status_completed));

            // let's wait a bit until we cancel the notification. When canceled
            // immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    oadSuccess();

                    // if this activity is still open and upload process was
                    // completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(
                            Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);

        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Logger.t(TAG).e("onDfuAborted");
            super.onDfuAborted(deviceAddress);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Logger.t(TAG).e("onError=" + error + ",errorType=" + errorType + ",message=" + message);
            super.onError(deviceAddress, error, errorType, message);
            oadFail();

        }
    };

    /**
     * 升级过程的监听，主要用于查看日志，方便调试
     */
    private final DfuLogListener mDfuLogListener = new DfuLogListener() {
        @Override
        public void onLogEvent(String deviceAddress, int level, String message) {
            Logger.t(TAG).e("deviceAddress=" + deviceAddress + ",message=" + message);
        }
    };

    /**
     * 退出固件升级界面
     */
    protected void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        DfuServiceListenerHelper.unregisterLogListener(this, mDfuLogListener);
        this.finish();
    }

    /**
     * 点击开始升级后，显示的界面
     */
    private void showProgressBar() {
        oadButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        textPercentTv.setVisibility(View.VISIBLE);
        upLoadingTv.setVisibility(View.GONE);
    }

    /**
     * 未点击开始升级时的界面
     */
    private void disMissProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        textPercentTv.setVisibility(View.INVISIBLE);
        upLoadingTv.setVisibility(View.GONE);
        oadButton.setEnabled(true);
    }

    /**
     * 固件升级成功
     */
    private void oadSuccess() {
        isFindOadDevice = false;
        disMissProgressBar();
        Toast.makeText(mContext, "升级成功", Toast.LENGTH_SHORT).show();
        Logger.t(TAG).e("升级成功");
        this.finish();
    }

    /**
     * 固件升级失败，默认会尝试5次
     */
    private void oadFail() {
        failCount++;
        disMissProgressBar();
        Logger.t(TAG).e("showErrorMessage");
        try {
            Thread.currentThread();
            Thread.sleep(300);
        } catch (Exception e) {
        }

        if (failCount < MAX_ALLOW_FAIL_COUNT) {
            Logger.t(TAG).e("再试一次=" + failCount);
            showProgressBar();
            startOad();
        } else {
            showOadFailDialog();
        }
    }

    /**
     * 5次升级失败后的提示框
     */
    private void showOadFailDialog() {
        isFindOadDevice = false;
        String mStringContent = "升级失败，设备名字会变成DfuLang";
        String mStringTitle = "提示";
        String mStringOk = "知道了";
        AlertDialog oadFailDialog = new AlertDialog.Builder(mContext).setTitle(mStringTitle)
                .setIconAttribute(android.R.attr.alertDialogIcon).setCancelable(false)
                .setMessage(mStringContent)
                .setPositiveButton(mStringOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OadActivity.this.finish();
                    }
                }).create();
        oadFailDialog.setCanceledOnTouchOutside(false);
        oadFailDialog.show();
    }
}
