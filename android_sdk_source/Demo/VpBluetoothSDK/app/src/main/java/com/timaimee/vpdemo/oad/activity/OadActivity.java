
package com.timaimee.vpdemo.oad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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

import com.goodix.ble.gr.toolbox.app.libfastdfu.DfuProgressCallback;
import com.goodix.ble.gr.toolbox.app.libfastdfu.EasyDfu2;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.oad.service.DfuService;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.oad.OnFindOadDeviceListener;
import com.veepoo.protocol.model.OadFileBean;
import com.veepoo.protocol.model.enums.ECPUPlatform;
import com.veepoo.protocol.model.enums.ECpuType;
import com.veepoo.protocol.model.settings.OadSetting;
import com.veepoo.protocol.err.OadErrorState;
import com.veepoo.protocol.listener.oad.OnUpdateCheckListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import vpno.nordicsemi.android.dfu.DfuLogListener;
import vpno.nordicsemi.android.dfu.DfuProgressListener;
import vpno.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import vpno.nordicsemi.android.dfu.DfuServiceInitiator;
import vpno.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by timaimee on 2016/8/17.
 */
public class OadActivity extends Activity implements View.OnClickListener, DfuProgressCallback {
    private Context mContext = null;
    private final static String TAG = OadActivity.class.getSimpleName();

    OadSetting oadSetting;
    TextView deviceStateTv, upLoadingTv, textPercentTv;
    Button oadButton;
    ProgressBar progressBar;

    private String mOadAddress, mOadFileName;
    private int failCount = 0;
    private boolean isCanEnterOadModel = false, isFindOadDevice = false;
    private final static int MAX_ALLOW_FAIL_COUNT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oad_custom);
        mContext = getApplicationContext();
        oadSetting = getIntData();
        initView();
        registerListener();
    }

    private void registerListener() {
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        DfuServiceListenerHelper.registerLogListener(this, mDfuLogListener);
    }

    private OadSetting getIntData() {
        boolean isOadModel = getIntent().getBooleanExtra("isoadmodel", false);
        int deviceNumber = getIntent().getIntExtra("devicenumber", 0);
        String deviceAddress = getIntent().getStringExtra("deviceaddress");
        String deviceVersion = getIntent().getStringExtra("deviceversion");
        String deviceTestVersion = getIntent().getStringExtra("devicetestversion");
        return new OadSetting(deviceAddress, deviceVersion, deviceTestVersion, deviceNumber, isOadModel);
    }

    private void initView() {
        deviceStateTv = (TextView) findViewById(R.id.oad_devicestate);
        upLoadingTv = (TextView) findViewById(R.id.oad_uploading);
        textPercentTv = (TextView) findViewById(R.id.oad_upload_progress_gv);
        oadButton = (Button) findViewById(R.id.oad_update);
        progressBar = (ProgressBar) findViewById(R.id.oad_upload_progress);
        oadButton.setOnClickListener(this);
        initDeviceStateView();
    }

    private void initDeviceStateView() {
        String sb = "设备地址：" + oadSetting.getDeviceAddress() +
                "\n" +
                "设备编号：" + oadSetting.getDeviceNumber() +
                "\n" +
                "正式版本号：" + oadSetting.getDeviceVersion() +
                "\n" +
                "测试版本号：" + oadSetting.getDeviceTestVersion() +
                "\n" +
                "是否DFU模式：" + oadSetting.isOadModel();
        deviceStateTv.setText(sb);

    }

    @Override
    public void onClick(View v) {
        if (isCanEnterOadModel) {
            //文件以及版本确定无误
            if (isFindOadDevice) {
                //如果当前是dfulang模式,直接进行升级
                ECpuType cpuType = VPOperateManager.getMangerInstance(mContext).getCpuType();
                selectOad(cpuType);
            } else {
                //如果当前是正常模式,需要进入固件升级模式
                findOadModelDevice();
            }
        } else {
            //文件以及版本还没有确定
            checkVersionAndFile();
        }
    }


    private void checkVersionAndFile() {
//        oadSetting.setDeviceVersion("00.23.00");
//        oadSetting.setDeviceTestVersion("00.23.00.00");
//        oadSetting.setHostUrl("http://www.baidu.com");
        Logger.t(TAG).i("升级前：版本验证->文件验证->查找目标设备");
        oadSetting.setDebug(true);
        oadSetting.setAutoDownload(false);//不自动下载，如果设置为false则会回调onRemoteOadFileGet方法获取远程ota文件信息
        VPOperateManager.getInstance().checkVersionAndFile(oadSetting, new OnUpdateCheckListener() {
            @Override
            public void onNetVersionInfo(int deviceNumber, String deviceVersion, String des) {
                Logger.t(TAG).i("服务器版本信息,设备号=" + deviceNumber + ",最新版本=" + deviceVersion + ",升级描述=" + des);
            }

            @Override
            public void onRemoteOadFileGet(OadFileBean oadFileBean) {
                Logger.t(TAG).i("onRemoteOadFileGet - " + oadFileBean.toString());
            }

            @Override
            public void onDownLoadOadFile(float progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.VISIBLE);
                        int percent = (int) (progress * 100);
                        progressBar.setProgress(percent);
                        textPercentTv.setText("下载进度：" + percent + "%");
                    }
                });
            }

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

            @Override
            public void onCheckSuccess(String oadFileName) {
                Logger.t(TAG).i("版本确认无误，文件确认无误 oadFileName = " + oadFileName);
                mOadFileName = oadFileName;
                if (!TextUtils.isEmpty(mOadFileName)) {
                    isCanEnterOadModel = true;
                }
            }

            @Override
            public void findOadDevice(String oadAddress, final ECpuType eCpuType) {
                Logger.t(TAG).i("找到OAD模式下的设备了:" + eCpuType);
                mOadAddress = oadAddress;
                if (!TextUtils.isEmpty(mOadAddress)) {
                    isFindOadDevice = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectOad(eCpuType);
                        }
                    });
                }
            }

            @Override
            public void findOadDevice(String oadAddress, ECPUPlatform cpuPlatform) {

            }

            @Override
            public void unKnowCpu() {
                Logger.t(TAG).i("不知道设备的CPU是什么类型");
            }
        });
    }


    private void findOadModelDevice() {
        VPOperateManager.getMangerInstance(mContext).findOadModelDevice(oadSetting, new OnFindOadDeviceListener() {
            @Override
            public void findOadDevice(String oadAddress, ECpuType eCpuType) {
                Logger.t(TAG).i("findOadDevice:" + mOadAddress);
                mOadAddress = oadAddress;
                Logger.t(TAG).i("findOadDevice:" + mOadAddress);
                selectOad(eCpuType);
            }

            @Override
            public void findOadDevice(String oadAddress, ECPUPlatform cpuPlatform) {

            }

            @Override
            public void unKnowCpu() {

            }
        });
    }

    private void selectOad(ECpuType eCpuType) {
        switch (eCpuType) {
            case NORIC:
                startOadNoric();
                break;
            case HUITOP:
                startOadHuiDing();
                break;
        }
    }

    private void startOadHuiDing() {

        BluetoothManager mBManager;
        BluetoothAdapter mBAdapter = null;

        mBManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
            BluetoothDevice device = mBAdapter.getRemoteDevice(mOadAddress);
            startEasyDfu(device);
        }
    }

    private void startEasyDfu(BluetoothDevice bluetoothDevice) {
        EasyDfu2 dfu = new EasyDfu2();
        dfu.setListener(this);
        dfu.setLogger(null);
        mOadFileName = getExternalFilesDir(null) + File.separator + "H88T_00880206_6092_fw_encryptandsign.bin";
        File file = new File(mOadFileName);
        Logger.t(TAG).i("fileName:" + mOadFileName);
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(file);
            dfu.startDfu(mContext, bluetoothDevice, targetStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void startOadNoric() {
        Logger.t(TAG).i("执行升级程序，最多尝试5次");
        showProgressBar();
        Boolean isBinder = false;
        final DfuServiceInitiator dfuServiceInitiator = new DfuServiceInitiator(mOadAddress)
                .setDeviceName("veepoo")
                .setKeepBond(isBinder);
        dfuServiceInitiator.setZip(null, mOadFileName);
        dfuServiceInitiator.start(this, DfuService.class);

    }

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

    private final DfuLogListener mDfuLogListener = new DfuLogListener() {
        @Override
        public void onLogEvent(String deviceAddress, int level, String message) {
            Logger.t(TAG).e("deviceAddress=" + deviceAddress + ",message=" + message);
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        DfuServiceListenerHelper.unregisterLogListener(this, mDfuLogListener);
        this.finish();
    }


    private void showProgressBar() {
        oadButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        textPercentTv.setVisibility(View.VISIBLE);
        upLoadingTv.setVisibility(View.GONE);
    }

    private void disMissProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        textPercentTv.setVisibility(View.INVISIBLE);
        upLoadingTv.setVisibility(View.GONE);
        oadButton.setEnabled(true);
    }

    private void oadSuccess() {
        isFindOadDevice = false;
        disMissProgressBar();
        Toast.makeText(mContext, "升级成功", Toast.LENGTH_SHORT).show();
        Logger.t(TAG).e("升级成功");
        this.finish();
    }

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
            startOadNoric();
        } else {
            showOadFailDialog();
        }
    }


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

    @Override
    public void onDfuStart() {
        startdfuprogress();
    }

    @Override
    public void onDfuProgress(int i) {
        Logger.t(TAG).e("onDfuProgress:" + i);
        textPercentTv.setText(i + "%");
    }

    @Override
    public void onDfuComplete() {
        Logger.t(TAG).e("onDfuCompleted");
        textPercentTv.setText(getResources().getString(R.string.dfu_status_completed));
        oadSuccess();
    }

    @Override
    public void onDfuError(String s, Error error) {
        Logger.t(TAG).e("onDfuError");
    }

    private void startdfuprogress() {
        progressBar.setIndeterminate(true);
        textPercentTv.setText(R.string.dfu_status_starting);
    }
}

