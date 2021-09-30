package com.timaimee.vpdemo;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: YWX
 * Date: 2021/9/7 15:11
 * Description:
 */
public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();
    protected BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mScanner;
    private boolean isEnable = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotify();
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        Logger.t(TAG).e("onCreate = >>>>>>>>>>>>>>>>>>> 当前进程 = " + getCurProcessName(this));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startScanBluetooth();
            }
        }, 10000);
    }

    private void initNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotifyO();
        } else {
            initNotify26();
        }
    }

    private void initNotify26() {
        Intent mIntent = new Intent(this, MainActivity.class);
        String appName = getString(R.string.app_name);
        String msgcontent = "VpSdk 启动前台服务通知！";
        msgcontent = String.format(msgcontent, appName);
        notifyApp(appName, msgcontent, mIntent, R.mipmap.ic_launcher, 0x12);
    }

    private void notifyApp(String msgtitle, String msgcontent, Intent mIntent, int iconId, int nofityId) {
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this).setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(iconId)
                .setTicker("").setContentTitle(msgtitle)
                .setContentText(msgcontent).setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(resultPendingIntent).build();
        startForeground(nofityId, notification);
    }

    private void initNotifyO() {
        String packageName = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = packageName;
            String CHANNEL_NAME = packageName + ".MyService";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            String msgtitle = getString(R.string.app_name);
            String msgcontent = "VpSdk 启动前台服务通知";
            msgcontent = String.format(msgcontent, msgtitle);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(msgtitle)
                    .setContentText(msgcontent)
                    .build();
            startForeground(1, notification);
        }
    }


    String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    public void startScanBluetooth() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isEnable) {
            Log.e("BluetoothLESearcher", "startScanDevice-------------> 版本>=26 开始搜索");
            startScanDevice2();
        } else {
            Log.e("BluetoothLESearcher", "startScanBluetooth------------->startLeScan 低版本 开始搜索");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    public void stopScanBluetooth() {
        // TODO Auto-generated method stub
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isEnable) {
                Log.e("BluetoothLESearcher", "stopScanBluetooth-------------> 版本>=26 搜索停止");
                mScanner.stopScan(mScanCallback);
            } else {
                Log.e("BluetoothLESearcher", "stopScanBluetooth------------->stopLeScan 低版本 搜索停止");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        } catch (Exception e) {
            BluetoothLog.e(e);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    protected void cancelScanBluetooth() {
        // TODO Auto-generated method stub

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isEnable) {
            Log.e("BluetoothLESearcher", "cancelScanBluetooth-------------> 版本>=26 搜索停止");
            mScanner.stopScan(mScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.e("BluetoothLESearcher", "cancelScanBluetooth-------------> 低版本 搜索停止");
        }

    }


    @TargetApi(Build.VERSION_CODES.O)
    private void startScanDevice2() {
        Log.e("BluetoothLESearcher", "开始搜索设备-------------> ");
        scan();
        mBluetoothAdapter.getBluetoothLeScanner().startScan(buildScanFilters(), mScanSettings, mScanCallback);
    }

    //设置蓝牙扫描过滤器集合
    private List<ScanFilter> scanFilterList;
    //设置蓝牙扫描过滤器
    private ScanFilter.Builder scanFilterBuilder;
    //设置蓝牙扫描设置
    private ScanSettings.Builder scanSettingBuilder;

    @TargetApi(Build.VERSION_CODES.O)
    private List<ScanFilter> buildScanFilters() {
        scanFilterList = new ArrayList<>();
        // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
        scanFilterBuilder = new ScanFilter.Builder();
        ParcelUuid parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
//        ParcelUuid parcelUuid = ParcelUuid.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
        scanFilterBuilder.setServiceUuid(serviceUuid, parcelUuidMask);
        scanFilterBuilder.setServiceUuid(serviceUuid);
        scanFilterList.add(scanFilterBuilder.build());

        scanFilterList.add(new ScanFilter.Builder()
                //过滤扫描蓝牙设备的主服务
                .setServiceUuid(ParcelUuid.fromString("0000ffff-0000-1000-8000-00805f9bfffb"))
                .build());
        return scanFilterList;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private ScanSettings buildScanSettings() {
        scanSettingBuilder = new ScanSettings.Builder();
        //设置蓝牙LE扫描的扫描模式。
        //使用最高占空比进行扫描。建议只在应用程序处于此模式时使用此模式在前台运行
        scanSettingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
        //在主动模式下，即使信号强度较弱，hw也会更快地确定匹配.在一段时间内很少有目击/匹配。
        scanSettingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        //设置蓝牙LE扫描的回调类型
        //为每一个匹配过滤条件的蓝牙广告触发一个回调。如果没有过滤器是活动的，所有的广告包被报告
        scanSettingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        scanSettingBuilder.setLegacy(true);
        return new ScanSettings.Builder().build();
    }

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // TODO Auto-generated method stub
            Logger.t(TAG).i(String.format(" onLeScan device for %s-%s-%d", device.getName(), device.getAddress(), rssi));
        }

    };
    ParcelUuid serviceUuid = ParcelUuid.fromString("0000fee7-0000-1000-8000-00805f9b34fb");

    @TargetApi(Build.VERSION_CODES.O)
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.e("BluetoothLESearcher", "onScanResult-------------> results = " + result);
            if (result.getScanRecord() != null) {
                Logger.t(TAG).i(String.format("onScanResult device for %s-%s-%d", result.getDevice().getName(), result.getDevice().getAddress(), result.getRssi()));
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.e(TAG, "onBatchScanResults-------------> results = " + results);
            for (ScanResult result : results) {
                Log.e(TAG, result.getDevice().getName() + " ---- " + result.getDevice().getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("BluetoothLESearcher", "onScanFailed-------------> errorCode = " + errorCode);
        }
    };


    private ScanSettings mScanSettings;

    public void scan() {
        boolean front = isAppForeground(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null == mScanner) {
                mScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (front) {
                    if (null == mScanSettings) {
                        mScanSettings = new ScanSettings.Builder()
                                //前台设置扫描模式为低时延
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                .build();
                    }
                } else {
                    mScanSettings = new ScanSettings.Builder()
                            //退到后台时设置扫描模式为低能耗
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                            .build();
                }
            } else {
                if (front) {
                    if (null == mScanSettings) {
                        mScanSettings = new ScanSettings.Builder()
                                //前台设置扫描模式为低时延
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build();
                    }
                } else {
                    mScanSettings = new ScanSettings.Builder()
                            //退到后台时设置扫描模式为低能耗
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build();
                }
            }
        }
    }

    private boolean isAppForeground(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList =
                activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName())
                    && (processInfo.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                return true;
            }
        }
        return false;
    }

}
