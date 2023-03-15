package com.timaimee.vpdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.DeviceCompare;
import com.timaimee.vpdemo.MyService;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.BleScanViewAdapter;
import com.timaimee.vpdemo.adapter.CustomLogAdapter;
import com.timaimee.vpdemo.adapter.DividerItemDecoration;
import com.timaimee.vpdemo.adapter.OnRecycleViewClickCallback;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IABluetoothStateListener;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.util.VPLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static com.timaimee.vpdemo.activity.Oprate.PWD_COMFIRM;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;

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
    private boolean mIsOadModel;
    BluetoothLeScannerCompat mScanner;
    private boolean isStartConnecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLog();
        VPOperateManager.getInstance().init(this);
        Logger.t(TAG).i("onSearchStarted");
        mScanner = BluetoothLeScannerCompat.getScanner();
        VPLogger.setDebug(true);
        initRecyleView();
        checkPermission();
        registerBluetoothStateListener();
        createFile();
//        startService(new Intent(this, MyService.class));
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
        switch (id) {
            case R.id.scan:
                scanDevice();
                break;
        }
    }

    private void initRecyleView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) super.findViewById(R.id.mian_swipeRefreshLayout);
        mRecyclerView = (RecyclerView) super.findViewById(R.id.main_recylerlist);
        mTitleTextView = (TextView) super.findViewById(R.id.main_title);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bleConnectAdatpter = new BleScanViewAdapter(this, mListData);
        mRecyclerView.setAdapter(bleConnectAdatpter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        bleConnectAdatpter.setBleItemOnclick(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mTitleTextView.setText("Scan Device V" + getAppVersion(mContext));
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
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH);
            }
        } else {
            Logger.t(TAG).i("requestPermission,shouldShowRequestPermissionRationale hehe");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH: {
                Logger.t(TAG).i("onRequestPermissionsResult,MY_PERMISSIONS_REQUEST_BLUETOOTH ");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBLE();
                } else {
                }
                return;
            }
        }
    }

    private void initLog() {
        Logger.init(YOUR_APPLICATION)
//                .methodCount(0)
//                .methodOffset(0)
//                .hideThreadInfo()
                .logLevel(LogLevel.FULL)
                .logAdapter(new CustomLogAdapter());
    }

    private boolean scanDevice() {
        if (!mListAddress.isEmpty()) {
            mListAddress.clear();
        }
        if (!mListData.isEmpty()) {
            mListData.clear();
            bleConnectAdatpter.notifyDataSetChanged();
        }

        if (!BluetoothUtils.isBluetoothEnabled()) {
            Toast.makeText(mContext, "bluetooth is not open", Toast.LENGTH_SHORT).show();
            return true;
        }
//        startScan();
        VPOperateManager.getInstance().startScanDevice(mSearchResponse);
        return false;
    }


    private void startScan() {
        Logger.t(TAG).i("startScan");
        //后台扫描跟前台扫描的方式不一样
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(false)//默认为true，表示如果他们支持硬件分流批处理的话，使用硬件分流批处理;false表示兼容机制
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter scanFilter;

        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        scanFilter = scanFilterBuilder.build();
        filters.add(scanFilter);
        final ScanCallback mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

            }

            @Override
            public void onBatchScanResults(final List<ScanResult> results) {
                super.onBatchScanResults(results);
                Logger.t(TAG).i("onBatchScanResults:" + results.size());
                //Logger.t(TAG).i("address," + bluetoothDevice.getAddress());
                //05,09,42,31,35,50|03,19,41,03|02,01,06|03,03,FF,FF|09,FF,F8,F8,CF,86,07,90,82,DD,
                //flag 03后面是服务
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < results.size(); i++) {
                            ScanResult scanResult = results.get(i);

                            BluetoothDevice device = scanResult.getDevice();
                            if (!mListAddress.contains(device.getAddress())) {
                                mListData.add(new SearchResult(device, scanResult.getRssi(), scanResult.getScanRecord().getBytes()));
                                mListAddress.add(device.getAddress());
                            }
                        }
                        Collections.sort(mListData, new DeviceCompare());
                        bleConnectAdatpter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.i(TAG, "onScanFailed:" + errorCode);
            }
        };
        mScanner.startScan(filters, settings, mScanCallback);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanner.stopScan(mScanCallback);
                refreshStop();
            }
        }, 6 * 1000);
    }


    private void connectDevice(final String mac, final String deviceName) {

        VPOperateManager.getInstance().registerConnectStatusListener(mac, mBleConnectStatusListener);

        VPOperateManager.getInstance().connectDevice(mac, deviceName, new IConnectResponse() {

            @Override
            public void connectState(int code, BleGattProfile profile, boolean isoadModel) {
                if (code == Code.REQUEST_SUCCESS) {
                    //蓝牙与设备的连接状态
                    Logger.t(TAG).i("连接成功");
                    Logger.t(TAG).i("是否是固件升级模式=" + isoadModel);
                    mIsOadModel = isoadModel;
                    isStartConnecting = true;
                } else {
                    Logger.t(TAG).i("连接失败");
                    isStartConnecting = false;
                }
            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int state) {
                if (state == Code.REQUEST_SUCCESS) {
                    //蓝牙与设备的连接状态
                    Logger.t(TAG).i("监听成功-可进行其他操作");
                    isStartConnecting = true;
//                    if(iS2ECGSwitch) {
//                        startPasswordValid(mac);
//                    } else {
                        Intent intent = new Intent(mContext, OperaterActivity.class);
                        intent.putExtra("isoadmodel", mIsOadModel);
                        intent.putExtra("deviceaddress", mac);
                        startActivity(intent);
//                    }
                } else {
                    Logger.t(TAG).i("监听失败，重新连接");
                    isStartConnecting = false;
                }

            }
        });
    }

    private boolean iS2ECGSwitch = false;

    private void  startPasswordValid(final String mac){
            boolean is24Hourmodel = false;
            VPOperateManager.getMangerInstance(mContext).confirmDevicePwd(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {

                }
            }, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    String message = "PwdData:\n" + pwdData.toString();
                    Logger.t(TAG).i(message);
                    int deviceNumber = pwdData.getDeviceNumber();
                    if(deviceNumber == 9010 ||deviceNumber == 9026 ||deviceNumber == 9031||
                            deviceNumber == 9019 ||deviceNumber == 9025 ||deviceNumber == 9030){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, ECGSwitchActivity.class);
                                intent.putExtra("isoadmodel", mIsOadModel);
                                intent.putExtra("deviceaddress", mac);
                                startActivity(intent);
                            }
                        },1000);
                    }
                }
            }, new IDeviceFuctionDataListener() {
                @Override
                public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                    String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                    Logger.t(TAG).i(message);
                }
            }, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {

                }

                @Override
                public void onSocialMsgSupportDataChange2(FunctionSocailMsgData functionSocailMsgData) {

                }
            }, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "CustomSettingData:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                }
            }, "0000", is24Hourmodel);


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
                Logger.t(TAG).i("STATUS_CONNECTED");
            } else if (status == Constants.STATUS_DISCONNECTED) {
                Logger.t(TAG).i("STATUS_DISCONNECTED");
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
                    if (!mListAddress.contains(device.getAddress())) {
                        mListData.add(device);
                        mListAddress.add(device.getAddress());
                    }
                    Collections.sort(mListData, new DeviceCompare());
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
        Logger.t(TAG).i("onRefresh");
        if (checkBLE()) {
            scanDevice();
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
    private boolean checkBLE() {
        if (!BluetoothUtils.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
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
        if (isStartConnecting) {
            return;
        }
        isStartConnecting = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Connecting, please wait...", Toast.LENGTH_SHORT).show();
            }
        });
        SearchResult searchResult = mListData.get(position);
        connectDevice(searchResult.getAddress(), searchResult.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        isStartConnecting = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VPOperateManager.getMangerInstance(this).disconnectWatch(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        });
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
