package com.timaimee.vpdemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.jieli.RcspAuthManager;
import com.inuker.bluetooth.library.jieli.dial.JLWatchFaceManager;
import com.inuker.bluetooth.library.jieli.dial.JLWatchHolder;
import com.inuker.bluetooth.library.jieli.ota.JLOTAHolder;
import com.inuker.bluetooth.library.jieli.response.RcspAuthResponse;
import com.jieli.jl_fatfs.model.FatFile;
import com.jieli.jl_rcsp.model.base.BaseError;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IContactOptListener;
import com.veepoo.protocol.listener.data.IMtuChangeListener;
import com.veepoo.protocol.model.datas.Contact;
import com.veepoo.protocol.model.enums.EContactOpt;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import tech.gujin.toast.ToastUtil;

public class JLDeviceOPTActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "JLDeviceOPTActivity";
    Button btnOpenNotify, btnFileSystem, btnPhotoDial, btnServerDial, btnOTA, btnAuth;
    TextView tvOpenInfo, tvFileSystemInfo, tvDialProgress, tvServerDialProgress, tvOTAProgress, tvOTAInfo, tvDialInfo, tvServerDialInfo, tvAuthInfo;
    ProgressBar pbPhotoDial, pbOTAProgress, pbServerDial;

    CustomProgressDialog loadingDialog;

    int serverDialFlag = 0;
    int photoDialFlag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtil.initialize(this);
        setContentView(R.layout.activity_jl_device);
        loadingDialog = new CustomProgressDialog(this);
        btnOpenNotify = findViewById(R.id.btnOpenNotify);
        btnFileSystem = findViewById(R.id.btnFileSystem);
        btnPhotoDial = findViewById(R.id.btnPhotoDial);
        btnServerDial = findViewById(R.id.btnServerDial);
        btnOTA = findViewById(R.id.btnOTA);
        btnAuth = findViewById(R.id.btnAuth);
        tvOpenInfo = findViewById(R.id.tvOpenInfo);
        tvFileSystemInfo = findViewById(R.id.tvFileSystemInfo);
        tvDialProgress = findViewById(R.id.tvDialProgress);
        tvOTAProgress = findViewById(R.id.tvOTAProgress);
        tvServerDialProgress = findViewById(R.id.tvServerDialProgress);
        tvServerDialInfo = findViewById(R.id.tvServerDialInfo);
        tvOTAInfo = findViewById(R.id.tvOTAInfo);
        tvDialInfo = findViewById(R.id.tvDialInfo);
        tvAuthInfo = findViewById(R.id.tvAuthInfo);
        pbPhotoDial = findViewById(R.id.pbPhotoDial);
        pbOTAProgress = findViewById(R.id.pbOTAProgress);
        pbServerDial = findViewById(R.id.pbServerDial);

        btnOpenNotify.setOnClickListener(this);
        btnAuth.setOnClickListener(this);
        btnFileSystem.setOnClickListener(this);
        btnPhotoDial.setOnClickListener(this);
        btnServerDial.setOnClickListener(this);
        btnOTA.setOnClickListener(this);

        tvOpenInfo.setText(VPOperateManager.getInstance().isJLNotifyOpened() ? "通知已打开" : "通知未打开");
        tvAuthInfo.setText(RcspAuthManager.getInstance().isAuthPass() ? "设备认证已通过" : "设备认证未通过");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpenNotify: {
                openJLNotify();
                break;
            }
            case R.id.btnAuth: {
                startDeviceAuth();
                break;
            }
            case R.id.btnFileSystem: {
                getJLFileSystem();
                break;
            }
            case R.id.btnPhotoDial: {
                setPhotoDial();
                break;
            }
            case R.id.btnOTA: {
                startOTA();
                break;
            }
            case R.id.btnServerDial: {
                setServerDial();
                break;
            }
        }
    }

    private void openJLNotify() {
        if (VPOperateManager.getInstance().isJLNotifyOpened()) {
            ToastUtil.show("通知已打开");
            return;
        }
        VPOperateManager.getInstance().openJLDataNotify(new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {

            }

            @Override
            public void onResponse(int code) {
                tvOpenInfo.setText("已开启通知");
                VPOperateManager.getInstance().changeMTU(247, new IMtuChangeListener() {
                    @Override
                    public void onChangeMtuLength(int cmdLength) {

                    }
                });

            }
        });
    }

    /**
     * 开始设备认证
     */
    private void startDeviceAuth() {
        if (RcspAuthManager.getInstance().isAuthPass()) {
            ToastUtil.show("设备认证已通过");
            return;
        }

        VPOperateManager.getInstance().startJLDeviceAuth(new RcspAuthResponse() {
            @Override
            public void onRcspAuthStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.showNoTips();
                        tvAuthInfo.setText("开始设备认证");
                    }
                });
            }

            @Override
            public void onRcspAuthSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.disMissDialog();
                        tvAuthInfo.setText("设备认证已通过");
                    }
                });
            }

            @Override
            public void onRcspAuthFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.disMissDialog();
                        tvAuthInfo.setText("设备认证未通过");
                    }
                });
            }
        });
    }

    /**
     * 更新杰理文件系统
     */
    private void getJLFileSystem() {
        //杰理文件系统
        VPOperateManager.getInstance().listJLWatchList(new JLWatchFaceManager.OnWatchDialInfoGetListener() {
            @Override
            public void onGettingWatchDialInfo() {
                //获取表盘信息中... 此时请勿做其他蓝牙操作
                ToastUtil.show("正在获取中...请勿重复调用");
                loadingDialog.showNoTips();
            }

            @Override
            public void onWatchDialInfoGetStart() {
                //开始获取手表表盘信息
                ToastUtil.show("获取文件系统列表-开始");
                tvFileSystemInfo.setText("获取文件系统列表-开始");
                loadingDialog.showNoTips();
            }

            @Override
            public void onWatchDialInfoGetComplete() {
                //获取表盘信息流程完成
                Logger.t(TAG).e("系统表盘--->Complete");
                ToastUtil.show("获取文件系统列表-完成");
                loadingDialog.disMissDialog();
            }

            @Override
            public void onWatchDialInfoGetSuccess(List<FatFile> systemFatFiles, List<FatFile> serverFatFiles, FatFile picFatFile) {
                //获取杰理平台的表盘信息成功
                StringBuilder sb = new StringBuilder("杰理表盘系统更新=============================Start");
                sb.append("\t\t\t\n").append("[照片表盘] picFatFile = ").append(picFatFile == null ? "NULL" : picFatFile.getPath());
                for (FatFile serverFatFile : serverFatFiles) {
                    sb.append("\t\t\t\n").append("[服务器表盘] serverFatFile = ").append(serverFatFile == null ? "NULL" : serverFatFile.getPath());
                }
                for (FatFile systemFatFile : systemFatFiles) {
                    sb.append("\t\t\t\n").append("[系统表盘] systemFatFile = ")
                            .append(systemFatFile == null ? "NULL" : systemFatFile.getPath());
                }
                sb.append("\t\t\t\n").append("[当前的服务器表盘] serverFatFile = ")
                        .append(serverFatFiles.isEmpty() ? "【还未设置】" : serverFatFiles.get(0).getPath())
                        .append("\n")
                        .append("杰理表盘系统更新=============================End");
                Log.e(TAG, sb.toString());
                tvFileSystemInfo.setText(sb.toString());
                for (FatFile systemFatFile : systemFatFiles) {
                    Logger.t(TAG).e("系统表盘--->" + systemFatFile.toString());

                }
                for (FatFile serverFatFile : serverFatFiles) {
                    Logger.t(TAG).e("服务器表盘--->" + serverFatFile.toString());
                }
                Logger.t(TAG).e("照片表盘--->" + (picFatFile == null ? "NULL" : picFatFile.getPath()));
                loadingDialog.disMissDialog();
            }

            @Override
            public void onWatchDialInfoGetFailed(BaseError error) {
                //获取表盘信息失败
                Logger.t(TAG).e("系统表盘--->Error:" + error.toString());
                ToastUtil.show("获取文件系统列表-失败");
                tvFileSystemInfo.setText("获取文件系统列表-失败:\n" + error.toString());
                loadingDialog.disMissDialog();
            }
        });
    }

    /**
     * 设置照片表盘
     */
    private void setPhotoDial() {
        String dialPhotoPath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlDail/20230413093755.png";
        if (photoDialFlag % 2 == 0) {
            dialPhotoPath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlDail/20230522144758.png";
        }
        photoDialFlag++;
        tvDialInfo.setText(dialPhotoPath);
        VPOperateManager.getInstance().setJLWatchPhotoDial(dialPhotoPath, new JLWatchFaceManager.JLTransferPicDialListener() {
            @Override
            public void onJLTransferPicDialStart() {
                tvDialInfo.setText("开始传输照片表盘");
                Logger.t(TAG).e("【杰理表盘传输】onJLTransferPicDialStart--->" + Thread.currentThread().toString());
            }

            @Override
            public void onTransferPicDialProgress(int progress) {
                Logger.t(TAG).e("【杰理表盘传输】--->progress = " + progress + " : Thread = " + Thread.currentThread().toString());
                pbPhotoDial.setProgress(progress);
                tvDialProgress.setText(progress + " %");
                tvDialInfo.setText("表盘文件传输中");
            }

            @Override
            public void onScaleBGPFileTransferComplete() {
                Logger.t(TAG).e("【杰理表盘传输】--->缩略图传输完成" + " : Thread = " + Thread.currentThread().toString());
                tvDialInfo.setText("表盘缩略图传输完成");
            }

            @Override
            public void onBigBGPFileTransferComplete() {
                Logger.t(TAG).e("【杰理表盘传输】--->大图传输完成" + " : Thread = " + Thread.currentThread().toString());
                tvDialInfo.setText("表盘大图传输完成");
            }

            @Override
            public void onTransferComplete() {
                Logger.t(TAG).e("【杰理表盘传输】--->表盘传输完成" + " : Thread = " + Thread.currentThread().toString());
                tvDialInfo.setText("照片表盘传输成功");
            }

            @Override
            public void onTransferError(int code, String errorMsg) {
                Logger.t(TAG).e("【杰理表盘传输】--->表盘传输失败 code = " + code + ", errorMsg = " + errorMsg + " : Thread = " + Thread.currentThread().toString());
                tvDialInfo.setText("照片表盘传输失败，code = " + code + " , errorMsg = " + errorMsg);
            }
        });
    }

    /**
     * 设置市场表盘
     */
    private void setServerDial() {
        String localServerDialPath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlDail/watch030";
        serverDialFlag++;
        if (serverDialFlag % 2 == 0) {
            localServerDialPath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlDail/watch064";
        }
        tvServerDialInfo.setText(localServerDialPath);
        VPOperateManager.getInstance().setJLWatchDial(localServerDialPath, new JLWatchHolder.OnSetJLWatchDialListener() {
            @Override
            public void onStart() {
                Logger.t(TAG).e("【杰理表盘传输】onStart--->" + Thread.currentThread().toString());
                tvServerDialInfo.setText("开始传输市场表盘");
            }

            @Override
            public void onProgress(int progress) {
                Logger.t(TAG).e("【杰理表盘传输】--->progress = " + progress + " : Thread = " + Thread.currentThread().toString());
                pbServerDial.setProgress(progress);
                tvServerDialProgress.setText(progress + " %");
                tvServerDialInfo.setText("表盘文件传输中");
            }

            @Override
            public void onComplete(String watchPath) {
                pbServerDial.setProgress(100);
                tvServerDialProgress.setText("100%");
                tvServerDialInfo.setText("设置成功:" + watchPath);
            }

            @Override
            public void onFiled(int code, String errorMsg) {
                tvDialInfo.setText("市场表盘传输失败，code = " + code + " , msg = " + errorMsg);
            }
        });
    }

    private void startOTA() {
        String firmwareFilePath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlOta/KH32_9626_00320800_OTA_UI_230421_19.zip";
        tvOTAInfo.setText(firmwareFilePath);
        VPOperateManager.getInstance().startJLDeviceOTAUpgrade(firmwareFilePath, new JLOTAHolder.OnJLDeviceOTAListener() {
            @Override
            public void onOTAStart() {
                Logger.t(TAG).e("【杰理OTA】--->OTA升级【开始】");
                tvOTAInfo.setText("开始升级");
            }

            @Override
            public void onProgress(float progress) {
                Logger.t(TAG).e("【杰理OTA】--->OTA升级中:" + progress + "%");
                tvOTAProgress.setText(String.format(Locale.CHINA, "%.2f", progress) + "%");
                pbOTAProgress.setProgress((int) (progress * 100));
            }

            @Override
            public void onNeedReconnect(String address, String dfuLangAddress, boolean isReconnectBySdk) {
                Logger.t(TAG).e("【杰理OTA】--->OTA升级dfuLang重连中: address = " + address + " , dfuLangAddress = " + dfuLangAddress + " , 是否由SDK重连 = " + isReconnectBySdk);
                tvOTAInfo.setText("数据传输结束，开始搜索DFULang设备->设备内部升级");
            }

            @Override
            public void onDFULangConnectSuccess(String dfuLangAddress) {
                tvOTAInfo.setText("DFULang设备连接成功->设备内部升级");
            }

            @Override
            public void onDFULangConnectFailed(String dfuLangAddress) {
                tvOTAInfo.setText("【错误】DFULang设备连接失败，请手动连接DFULang设备重新升级");
            }

            @Override
            public void onOTASuccess() {
                Logger.t(TAG).e("【杰理OTA】--->OTA升级【成功】");
                tvOTAInfo.setText("OTA升级成功");
                tvOTAProgress.setText("100%");
            }

            @Override
            public void onOTAFailed(com.jieli.jl_bt_ota.model.base.BaseError error) {
                Logger.t(TAG).e("【杰理OTA】--->OTA升级【失败】:" + error.toString());
                tvOTAInfo.setText("升级失败，error: code = " + error.getSubCode() + " , msg = " + error.getMessage());
            }
        });
    }
}
