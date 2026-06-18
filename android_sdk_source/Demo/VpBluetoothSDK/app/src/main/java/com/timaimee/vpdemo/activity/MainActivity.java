package com.timaimee.vpdemo.activity;

import static com.veepoo.protocol.util.VpBleByteUtil.isBeyondVp;
import static com.veepoo.protocol.util.VpBleByteUtil.isBrandDevice;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.log.VPLocalLogger;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.jieli.jl_bt_ota.util.JL_Log;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.DeviceCompare;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.BleScanViewAdapter;
import com.timaimee.vpdemo.adapter.CustomLogAdapter;
import com.timaimee.vpdemo.adapter.DividerItemDecoration;
import com.timaimee.vpdemo.adapter.OnRecycleViewClickCallback;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IABluetoothStateListener;
import com.veepoo.protocol.util.VPLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import tech.gujin.toast.ToastUtil;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, OnRecycleViewClickCallback {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String YOUR_APPLICATION = "timaimee";
    Context mContext = MainActivity.this;
    private final int REQUEST_CODE = 1;
    List<SearchResult> mListData = new ArrayList<>();
    List<String> mListAddress = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    BleScanViewAdapter bleConnectAdatpter;
    Handler mHandler = new Handler();
    private BluetoothManager mBManager;
    private BluetoothAdapter mBAdapter;
    private BluetoothLeScanner mBScanner;
    final static int MY_PERMISSIONS_REQUEST_BLUETOOTH = 0x55;
    RecyclerView mRecyclerView;
    TextView mTitleTextView;
    TextView tvTips;
    private boolean mIsOadModel;
    BluetoothLeScannerCompat mScanner;

    @Override
    protected void onDestroy() {
        VPOperateManager.getInstance().disconnectWatch(code -> {

        });
        VPLocalLogger.stopMonitor();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarColorInt(Color.BLACK)
                .statusBarDarkFont(true)
                .init();
        setContentView(R.layout.activity_main);
        ToastUtil.initialize(this);
        VPOperateManager.getInstance().init(this);
        if (/*BuildConfig.IS_DEBUG ||*/ true) {
            //杰理日志
            com.jieli.jl_rcsp.util.JL_Log.setTagPrefix("HBand-JLFace");
            com.jieli.jl_rcsp.util.JL_Log.configureLog(this, true, true);
            JL_Log.setLog(true);
            JL_Log.setIsSaveLogFile(this, true);
        }
        initLog();
        Logger.t(TAG).i("onSearchStarted");
        VPOperateManager.getInstance().init(this);
        VPOperateManager.getInstance().setAutoConnectBTBySdk(false);
        mScanner = BluetoothLeScannerCompat.getScanner();
        VPLogger.setDebug(true);
        initRecyleView();
        checkPermission();
        registerBluetoothStateListener();
        createFile();

        VPLocalLogger.startMonitor(this);
        VPOperateManager.getInstance().setPwdCheckTimeoutListener(() -> Logger.t(TAG).e("-onPwdCheckTimeout-: | 密码校验超时了"));
    }

    private void createFile() {
        String fileSDK = getExternalFilesDir(null) + File.separator + "LTEPH_GPS_1.rtcm";
        File file = new File(fileSDK);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Logger.t(TAG).i("createNewFile");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.t(TAG).i("exist file");
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.scan) {
            scanDevice();
        }
    }

    private void initRecyleView() {
        mSwipeRefreshLayout = super.findViewById(R.id.swRefresh);
        mRecyclerView = super.findViewById(R.id.rvBleDevice);
        mTitleTextView = super.findViewById(R.id.main_title);
        tvTips = super.findViewById(R.id.tvTips);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bleConnectAdatpter = new BleScanViewAdapter(this, mListData);
        mRecyclerView.setAdapter(bleConnectAdatpter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        bleConnectAdatpter.setBleItemOnclick(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mTitleTextView.setText("扫描设备 V" + getAppVersion(mContext));
    }


    private void checkPermission() {
        Logger.t(TAG).i("Build.VERSION.SDK_INT =" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT <= 22) {
            initBLE();
            return;
        }


        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Logger.t(TAG).i("checkPermission,PERMISSION_GRANTED");
            initBLE();
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestPermission();
            Logger.t(TAG).i("checkPermission,PERMISSION_DENIED");
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Logger.t(TAG).i("requestPermission,shouldShowRequestPermissionRationale");

            } else {
                Logger.t(TAG).i("requestPermission,shouldShowRequestPermissionRationale else");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                /*Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT,*/Manifest.permission.BLUETOOTH_PRIVILEGED},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH);
            }
        } else {
            Logger.t(TAG).i("requestPermission,shouldShowRequestPermissionRationale hehe");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_BLUETOOTH) {
            Logger.t(TAG).i("onRequestPermissionsResult,MY_PERMISSIONS_REQUEST_BLUETOOTH ");
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initBLE();
            }
        }
    }

    private void initLog() {
        Logger.init(YOUR_APPLICATION)
                .methodCount(0)
                .methodOffset(0)
                .hideThreadInfo()
                .logLevel(LogLevel.FULL)
                .logAdapter(new CustomLogAdapter());
    }

    private boolean scanDevice() {
        if (!mListAddress.isEmpty()) {
            mListAddress.clear();
        }
        if (!mListData.isEmpty()) {
            mListData.clear();
            tvTips.setVisibility(mListData.isEmpty() ? View.VISIBLE : View.GONE);
            bleConnectAdatpter.notifyDataSetChanged();
        }

        if (!BluetoothUtils.isBluetoothEnabled()) {
            Toast.makeText(mContext, "蓝牙没有开启", Toast.LENGTH_SHORT).show();
            return true;
        }
        VPOperateManager.getInstance().startScanDevice(8, mSearchResponse);
        return false;
    }

    private static final String KEY_CT = "【重连测试】";

    private int connectTimes = 0;
    long startConnectTime = 0;

    private void connectDevice(final String mac, final String deviceName) {
        startConnectTime = System.currentTimeMillis();
        VPOperateManager.getInstance().registerConnectStatusListener(mac, mBleConnectStatusListener);
        VPOperateManager.getInstance().connectDevice(mac, deviceName, (code, profile, isoadModel) -> {
            if (code == Code.REQUEST_SUCCESS) {
                //蓝牙与设备的连接状态
                connectTimes++;
                Logger.t(TAG + KEY_CT).i("连接成功");
                Logger.t(TAG).i("是否是固件升级模式=" + isoadModel);
                mIsOadModel = isoadModel;
            } else {
                SearchResult searchResult = mListData.get(0);
                Logger.t(TAG + KEY_CT).i("连接失败/链接断开");
            }
        }, state -> {
            if (state == Code.REQUEST_SUCCESS) {
                //蓝牙与设备的连接状态
                Logger.t(TAG + KEY_CT).i("监听成功-可进行其他操作->3s后断开");
//                new Handler().postDelayed(() -> VPOperateManager.getInstance().disconnectWatch(new IBleWriteResponse() {
//                    @Override
//                    public void onResponse(int code) {
//                        if (code == Code.REQUEST_SUCCESS) {
//                            Logger.t(TAG + KEY_CT).i("3s后断开成功");
//                        }
//                    }
//                }), 3000);

                Intent intent = new Intent(mContext, PwdConfirmActivity.class);
                intent.putExtra("deviceName", deviceName);
                intent.putExtra("isoadmodel", mIsOadModel);
                intent.putExtra("deviceaddress", mac);
                startActivity(intent);

//                    VPOperateManager.getInstance().confirmDevicePwd(new IBleWriteResponse() {
//                        @Override
//                        public void onResponse(int code) {
//
//                        }
//                    }, new IPwdDataListener() {
//                        @Override
//                        public void onPwdDataChange(PwdData pwdData) {
//                            String message = "PwdData:\n" + pwdData.toString();
//                            Logger.t(TAG).i(message);
//                            int deviceNumber = pwdData.getDeviceNumber();
//                            String deviceVersion = pwdData.getDeviceVersion();
//                            String deviceTestVersion = pwdData.getDeviceTestVersion();
//                            Logger.t(TAG).e("设备号：" + deviceNumber + ",版本号：" + deviceVersion + ",\n测试版本号：" + deviceTestVersion);
//                        }
//                    }, new IDeviceFuctionDataListener() {
//                        @Override
//                        public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
//                            String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
//                            Logger.t(TAG).i(message);
//                        }
//                    }, new ISocialMsgDataListener() {
//                        @Override
//                        public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
//
//                        }
//
//                        @Override
//                        public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
//
//                        }
//                    }, new ICustomSettingDataListener() {
//                        @Override
//                        public void OnSettingDataChange(CustomSettingData customSettingData) {
//                            String message = "CustomSettingData:\n" + customSettingData.toString();
//                            Logger.t(TAG).i(message);
//                        }
//                    }, "0000", true);

            } else {
                Logger.t(TAG).i("监听失败，重新连接");
                Intent intent = new Intent(mContext, OperaterActivity.class);
                intent.putExtra("isoadmodel", mIsOadModel);
                intent.putExtra("deviceaddress", mac);
                startActivity(intent);
            }
        });
    }

    /**
     * 蓝牙打开or关闭状态
     */
    private void registerBluetoothStateListener() {
        VPOperateManager.getInstance().registerBluetoothStateListener(mBluetoothStateListener);
    }

    /**
     * 监听系统蓝牙的打开和关闭的回调状态
     */
    private final IABleConnectStatusListener mBleConnectStatusListener = new IABleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == Constants.STATUS_CONNECTED) {
                long time = System.currentTimeMillis() - startConnectTime;
                Logger.t(TAG).e("-连接成功-: | 耗时：" + time / 1000 + "s" + time % 1000 + "ms");
                Logger.t(TAG + KEY_CT).i("STATUS_CONNECTED  第" + connectTimes + "次连接成功");

            } else if (status == Constants.STATUS_DISCONNECTED) {
                Logger.t(TAG + KEY_CT).i("-----STATUS_DISCONNECTED");
//                if (connectTimes < 5) {
//                    connectDevice(mac, "ET480");
//                }
            }
        }
    };

    /**
     * 监听蓝牙与设备间的回调状态
     */
    private final IABluetoothStateListener mBluetoothStateListener = new IABluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Logger.t(TAG).i("open=" + openOrClosed);
        }
    };


    public static boolean isShowDevice(byte[] scanRecord) {
        if (isBeyondVp(scanRecord)) {
            if (isBrandDevice(scanRecord)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 扫描的回调
     */
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Logger.t(TAG).i("onSearchStarted");
        }

        @Override
        public void onDeviceFounded(final SearchResult device) {
            Logger.t(TAG).i(String.format("device for %s-%s-%d", device.getName(), device.getAddress(), device.rssi));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mListAddress.contains(device.getAddress()) /*&& isShowDevice(device.scanRecord)*/) {
                        if (device.getName().equals("ET450") || TextUtils.isEmpty(device.getName()) || true) {
                            mListData.add(device);
                            mListAddress.add(device.getAddress());
                        }
                    }
                    mListData.sort(new DeviceCompare());
                    tvTips.setVisibility(mListData.isEmpty() ? View.VISIBLE : View.GONE);
                    bleConnectAdatpter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSearchStopped() {
            refreshStop();
            Logger.t(TAG).i("onSearchStopped");
        }

        @Override
        public void onSearchCanceled() {
            refreshStop();
            Logger.t(TAG).i("onSearchCanceled");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (BluetoothUtils.isBluetoothEnabled()) {
                scanDevice();
            } else {
                refreshStop();
            }
        }
    }

    @Override
    public void onRefresh() {
        if (checkBLE()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Logger.t(TAG).i("onRefresh");
                    scanDevice();
                }
            }, 3000);
        }
    }

    private void initBLE() {
        mBManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBScanner = mBAdapter.getBluetoothLeScanner();
        }
        checkBLE();
    }

    /**
     * 检测蓝牙设备是否开启
     *
     * @return
     */
    private boolean checkBLE() { //258025
        // 蓝牙没打开 → 先打开
        if (!BluetoothUtils.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);
            return false;
        }

        // 安卓 12+ 必须同时申请 SCAN + CONNECT 才能扫描
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                // 一次性申请两个权限
                requestPermissions(
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                        },
                        100
                );
                return false;
            }
        } else {
            // 安卓 11 及以下需要定位权限才能扫描
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        101
                );
                return false;
            }
        }

        // 所有权限都有了
        return true;
    }

    /**
     * 结束刷新
     */
    void refreshStop() {
        Logger.t(TAG).i("refreshComlete");
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void OnRecycleViewClick(int position) {
        mSwipeRefreshLayout.setRefreshing(false);
        VPOperateManager.getInstance().startScanDevice(20, mSearchResponse);
        VPOperateManager.getInstance().stopScanDevice();
        runOnUiThread(() -> Toast.makeText(mContext, "正在连接，请稍等...", Toast.LENGTH_SHORT).show());
        SearchResult searchResult = mListData.get(position);
        connectDevice(searchResult.getAddress(), searchResult.getName());
    }

    public String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
