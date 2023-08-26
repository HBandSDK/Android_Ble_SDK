package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.GridAdatper;
import com.timaimee.vpdemo.oad.activity.OadActivity;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.AbsBloodGlucoseChangeListener;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.listener.data.IAlarmDataListener;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
import com.veepoo.protocol.listener.data.IAllSetDataListener;
import com.veepoo.protocol.listener.data.IAutoDetectOriginDataListener;
import com.veepoo.protocol.listener.data.IBPDetectDataListener;
import com.veepoo.protocol.listener.data.IBPFunctionListener;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.ICameraDataListener;
import com.veepoo.protocol.listener.data.IChantingDataListener;
import com.veepoo.protocol.listener.data.ICheckWearDataListener;
import com.veepoo.protocol.listener.data.ICountDownListener;
import com.veepoo.protocol.listener.data.ICustomProtocolStateListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceBTConnectionListener;
import com.veepoo.protocol.listener.data.IDeviceBTInfoListener;
import com.veepoo.protocol.listener.data.IDeviceControlPhoneModelState;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IDeviceFunctionStatusChangeListener;
import com.veepoo.protocol.listener.data.IDeviceRenameListener;
import com.veepoo.protocol.listener.data.IDrinkDataListener;
import com.veepoo.protocol.listener.data.IECGAutoReportListener;
import com.veepoo.protocol.listener.data.IFatigueDataListener;
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener;
import com.veepoo.protocol.listener.data.IFindDevicelistener;
import com.veepoo.protocol.listener.data.IFindPhonelistener;
import com.veepoo.protocol.listener.data.IHRVOriginDataListener;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.listener.data.IHeartWaringDataListener;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.ILightDataCallBack;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.listener.data.ILowPowerListener;
import com.veepoo.protocol.listener.data.IMusicControlListener;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IOriginProgressListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.IRRIntervalProgressListener;
import com.veepoo.protocol.listener.data.IResponseListener;
import com.veepoo.protocol.listener.data.ISOSListener;
import com.veepoo.protocol.listener.data.IScreenLightListener;
import com.veepoo.protocol.listener.data.IScreenStyleListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISpo2hDataListener;
import com.veepoo.protocol.listener.data.ISpo2hOriginDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.listener.data.ISportModelOriginListener;
import com.veepoo.protocol.listener.data.ISportModelStateListener;
import com.veepoo.protocol.listener.data.ITemptureDataListener;
import com.veepoo.protocol.listener.data.ITemptureDetectDataListener;
import com.veepoo.protocol.listener.data.ITextAlarmDataListener;
import com.veepoo.protocol.listener.data.IWeatherStatusDataListener;
import com.veepoo.protocol.listener.data.IWomenDataListener;
import com.veepoo.protocol.listener.data.OnDeviceAlarm2ChangedListener;
import com.veepoo.protocol.model.DayState;
import com.veepoo.protocol.model.datas.AlarmData;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.datas.AllSetData;
import com.veepoo.protocol.model.datas.AutoDetectOriginData;
import com.veepoo.protocol.model.datas.AutoDetectStateData;
import com.veepoo.protocol.model.datas.BTInfo;
import com.veepoo.protocol.model.datas.BatteryData;
import com.veepoo.protocol.model.datas.BpData;
import com.veepoo.protocol.model.datas.BpFunctionData;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.datas.ChantingData;
import com.veepoo.protocol.model.datas.CheckWearData;
import com.veepoo.protocol.model.datas.CountDownData;
import com.veepoo.protocol.model.datas.DrinkData;
import com.veepoo.protocol.model.datas.FatigueData;
import com.veepoo.protocol.model.datas.FindDeviceData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.datas.LanguageData;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.datas.LowPowerData;
import com.veepoo.protocol.model.datas.MusicData;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.RRIntervalData;
import com.veepoo.protocol.model.datas.RRLorenzInfo;
import com.veepoo.protocol.model.datas.ScreenLightData;
import com.veepoo.protocol.model.datas.ScreenStyleData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SleepPrecisionData;
import com.veepoo.protocol.model.datas.Spo2hData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.SportModelOriginHeadData;
import com.veepoo.protocol.model.datas.SportModelOriginItemData;
import com.veepoo.protocol.model.datas.SportModelStateData;
import com.veepoo.protocol.model.datas.TemptureData;
import com.veepoo.protocol.model.datas.TemptureDetectData;
import com.veepoo.protocol.model.datas.TextAlarmData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.datas.WeatherData2;
import com.veepoo.protocol.model.datas.WeatherStatusData;
import com.veepoo.protocol.model.datas.WomenData;
import com.veepoo.protocol.model.datas.weather.WeatherData;
import com.veepoo.protocol.model.datas.weather.WeatherEvery3Hour;
import com.veepoo.protocol.model.datas.weather.WeatherEveryDay;
import com.veepoo.protocol.model.enums.EAllSetType;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.enums.EBloodGlucoseStatus;
import com.veepoo.protocol.model.enums.ECameraStatus;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EMultiAlarmOprate;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.enums.ERenameError;
import com.veepoo.protocol.model.enums.ESpo2hDataType;
import com.veepoo.protocol.model.enums.ESportType;
import com.veepoo.protocol.model.enums.ETemperatureUnit;
import com.veepoo.protocol.model.enums.ETimeMode;
import com.veepoo.protocol.model.enums.EWeatherType;
import com.veepoo.protocol.model.enums.EWomenStatus;
import com.veepoo.protocol.model.settings.Alarm2Setting;
import com.veepoo.protocol.model.settings.AlarmSetting;
import com.veepoo.protocol.model.settings.AllSetSetting;
import com.veepoo.protocol.model.settings.AutoDetectStateSetting;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.ChantingSetting;
import com.veepoo.protocol.model.settings.CheckWearSetting;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSocailSetting;
import com.veepoo.protocol.model.settings.CountDownSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.model.settings.DeviceTimeSetting;
import com.veepoo.protocol.model.settings.HeartWaringSetting;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.veepoo.protocol.model.settings.NightTurnWristSetting;
import com.veepoo.protocol.model.settings.ReadOriginSetting;
import com.veepoo.protocol.model.settings.ScreenSetting;
import com.veepoo.protocol.model.settings.SoldierContentSetting;
import com.veepoo.protocol.model.settings.TextAlarm2Setting;
import com.veepoo.protocol.model.settings.WeatherStatusSetting;
import com.veepoo.protocol.model.settings.WomenSetting;
import com.veepoo.protocol.shareprence.VpSpGetUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;
import com.veepoo.protocol.util.TextAlarmSp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.timaimee.vpdemo.activity.Oprate.*;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT_CLOSE;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT_OPEN;
import static com.veepoo.protocol.model.enums.EFunctionStatus.UNSUPPORT;

import org.jetbrains.annotations.NotNull;

import tech.gujin.toast.ToastUtil;

/**
 * Created by timaimee on 2017/2/8.
 */
public class OperaterActivity extends Activity implements AdapterView.OnItemClickListener {
    private final static String TAG = OperaterActivity.class.getSimpleName();
    TextView tv1, tv2, tv3, titleBleInfo;
    GridView mGridView;
    List<Map<String, String>> mGridData = new ArrayList<>();
    GridAdatper mGridAdapter;
    Context mContext = OperaterActivity.this;
    private String deviceaddress;
    boolean isSleepPrecision = false;
    Message msg;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = msg.obj.toString();
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();

            switch (msg.what) {
                case 1:
                    tv1.setText(s + "\n");
                    break;
                case 2:
                    tv2.setText(s + "\n");
                    break;
                case 3:
                    tv3.setText(s + "\n");
                    break;
            }
        }
    };
    WriteResponse writeResponse = new WriteResponse();


    /**
     * 密码验证获取以下信息
     */
    int watchDataDay = 3;
    int weatherStyle = 0;
    int contactMsgLength = 0;
    int allMsgLenght = 4;
    private int deviceNumber = -1;
    private String deviceVersion;
    private String deviceTestVersion;
    boolean isOadModel = false;
    boolean isNewSportCalc = false;
    boolean isInPttModel = false;
    ISocialMsgDataListener socialMsgDataListener = new ISocialMsgDataListener() {
        @Override
        public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
            String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
            Logger.t(TAG).i(message);
            sendMsg(message, 3);
        }

        @Override
        public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
            String message = "FunctionSocailMsgData2:\n" + socailMsgData.toString();
            Logger.t(TAG).i(message);
            sendMsg(message, 3);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate);
        mContext = getApplicationContext();
        deviceaddress = getIntent().getStringExtra("deviceaddress");
        tv1 = (TextView) super.findViewById(R.id.tv1);
        tv2 = (TextView) super.findViewById(R.id.tv2);
        tv3 = (TextView) super.findViewById(R.id.tv3);
        titleBleInfo = (TextView) super.findViewById(R.id.main_title_ble);
        initGridView();
        listenDeviceCallbackData();
        listenCamera();
        VPOperateManager.getInstance().init(this);
        VPOperateManager.getInstance().registerBTInfoListener(new IDeviceBTInfoListener() {
            @Override
            public void onDeviceBTFunctionNotSupport() {
                Logger.t("【BT】-").d("【BT】- ---> 不支持BT功能");
                showToast("不支持BT功能");
            }

            @Override
            public void onDeviceBTInfoSettingSuccess(@NotNull BTInfo btInfo) {
                Logger.t("【BT】-").d("【BT】- ---> btInfo : " + btInfo.toString());
                showToast("【BT】- ---> btInfo : " + btInfo.toString());
            }

            @Override
            public void onDeviceBTInfoSettingFailed() {
                Logger.t("【BT】-").d("【BT】- ---> BT设置失败");
                showToast("【BT】- ---> BT设置失败");
            }

            @Override
            public void onDeviceBTInfoReadSuccess(@NotNull BTInfo btInfo) {
                Logger.t("【BT】-").d("【BT】- ---> BT读取成功, btInfo : " + btInfo.toString());
                showToast("【BT】- ---> BT读取成功, btInfo : " + btInfo.toString());
            }

            @Override
            public void onDeviceBTInfoReadFailed() {
                Logger.t("【BT】-").d("【BT】- ---> BT读取失败");
                showToast("【BT】- ---> BT读取失败");
            }

            @Override
            public void onDeviceBTInfoReport(@NotNull BTInfo btInfo) {
                Logger.t("【BT】-").d("【BT】- ---> BT上报，btInfo = " + btInfo.toString());
                showToast("【BT】- ---> BT上报，btInfo = " + btInfo.toString());
            }
        });
        VPOperateManager.getInstance().registerBTConnectionListener(new IDeviceBTConnectionListener() {
            @Override
            public void onDeviceBTConnecting() {
                Logger.t("【BT】-").d("BT设备连接中");
                showToast("BT设备连接中");
            }

            @Override
            public void onDeviceBTConnected() {
                Logger.t("【BT】-").d("BT设备已连接");
                showToast("BT设备已连接");
            }

            @Override
            public void onDeviceBTDisconnected() {
                Logger.t("【BT】-").d("BT设备已断开");
                showToast("BT设备已断开");
//                VPOperateManager.getInstance().setBTStatus(false, true, true, false, new IBleWriteResponse() {
//                    @Override
//                    public void onResponse(int code) {
//
//                    }
//                });
            }

            @Override
            public void onDeviceBTConnectTimeout() {
                Logger.t("【BT】-").d("BT连接超时");
                showToast("BT连接超时");
            }
        });


        VPOperateManager.getInstance().setDeviceFunctionStatusChangeListener(new IDeviceFunctionStatusChangeListener() {
            @Override
            public void onFunctionStatusChanged(@NotNull DeviceFunction function, @NotNull EFunctionStatus status) {
                Logger.t(TAG).d("设备功能状态改变：" + function.getDes() + " -> " + status);
            }
        });

        ToastUtil.initialize(this);
    }

    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.main_gridview);
        int i = 0;
        while (i < oprateStr.length) {
            String s = oprateStr[i];
            Map<String, String> map = new HashMap<>();
            map.put("str", s);
            mGridData.add(map);
            i++;
        }
        mGridAdapter = new GridAdatper(this, mGridData);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        String oprater = mGridData.get(position).get("str");
        Toast.makeText(mContext, oprater, Toast.LENGTH_SHORT).show();
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        if (oprater.equals(HEART_DETECT_START)) {
//            startListenADC();
            VPOperateManager.getInstance().startDetectHeart(writeResponse, new IHeartDataListener() {

                @Override
                public void onDataChange(HeartData heart) {
                    String message = "heart:\n" + heart.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(TEMPTURE_DETECT_START)) {
            VPOperateManager.getInstance().startDetectTempture(writeResponse, new ITemptureDetectDataListener() {
                @Override
                public void onDataChange(TemptureDetectData temptureDetectData) {
                    String message = "startDetectTempture temptureDetectData:\n" + temptureDetectData.toString();
                    Logger.t(TAG).i(message);

//                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(TEMPTURE_DETECT_STOP)) {
            VPOperateManager.getInstance().stopDetectTempture(writeResponse, new ITemptureDetectDataListener() {
                @Override
                public void onDataChange(TemptureDetectData temptureDetectData) {
                    String message = "stopDetectTempture temptureDetectData:\n" + temptureDetectData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });

        } else if (oprater.equals(SET_WATCH_TIME)) {
            DeviceTimeSetting deviceTimeSetting = new DeviceTimeSetting(2020, 11, 6, 15, 30, 14, ETimeMode.MODE_12);
            VPOperateManager.getInstance().settingTime(writeResponse, new IResponseListener() {
                @Override
                public void response(int state) {
                    String message = "settingTime response :\n" + state;
                    Logger.t(TAG).i(message);
                }
            }, deviceTimeSetting);
        } else if (oprater.equals(WEATHER_READ_STATUEINFO)) {
            VPOperateManager.getInstance().readWeatherStatusInfo(writeResponse, new IWeatherStatusDataListener() {
                @Override
                public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
                    String message = "readWeatherStatusInfo onWeatherDataChange read:\n" + weatherStatusData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(UI_UPDATE_AGPS)) {
            int bigTranType = VpSpGetUtil.getVpSpVariInstance(mContext).getBigTranType();
            boolean isSupportAgps = VpSpGetUtil.getVpSpVariInstance(mContext).isSupoortAGPS();
            if (bigTranType == 2 && isSupportAgps) {
                Intent intent = new Intent(OperaterActivity.this, UiUpdateAGPSActivity.class);
                intent.putExtra("deviceNumber", String.valueOf(deviceNumber));
                intent.putExtra("deviceVersion", deviceVersion);
                intent.putExtra("deviceTestVersion", deviceTestVersion);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "不支持自定义表盘", Toast.LENGTH_LONG).show();
            }
        } else if (oprater.equals(UI_UPDATE_CUSTOM)) {

            int bigTranType = VpSpGetUtil.getVpSpVariInstance(mContext).getBigTranType();
            int coustomUICount = VpSpGetUtil.getVpSpVariInstance(mContext).getWatchuiCoustom();
            if (bigTranType == 2 && coustomUICount > 0) {
                Intent intent = new Intent(OperaterActivity.this, UiUpdateCustomActivity.class);
                intent.putExtra("deviceNumber", String.valueOf(deviceNumber));
                intent.putExtra("deviceVersion", deviceVersion);
                intent.putExtra("deviceTestVersion", deviceTestVersion);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "不支持自定义表盘", Toast.LENGTH_LONG).show();
            }

        } else if (oprater.equals(SYNC_MUSIC_INFO_PLAY)) {
            controlMusic(true);
        } else if (oprater.equals(SYNC_MUSIC_INFO_PAUSE)) {
            controlMusic(false);
        } else if (oprater.equals(VOLUME)) {
            controlVolume();
        } else if (oprater.equals(UI_UPDATE_SERVER)) {

            int bigTranType = VpSpGetUtil.getVpSpVariInstance(mContext).getBigTranType();
            int serverUICount = VpSpGetUtil.getVpSpVariInstance(mContext).getWatchuiServer();
            if (bigTranType == 2 && serverUICount > 0) {
                Intent intent = new Intent(OperaterActivity.this, UiUpdateServerActivity.class);
                intent.putExtra("deviceNumber", String.valueOf(deviceNumber));
                intent.putExtra("deviceVersion", deviceVersion);
                intent.putExtra("deviceTestVersion", deviceTestVersion);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "不支持服务器表盘", Toast.LENGTH_LONG).show();
            }

        } else if (oprater.equals(UI_UPDATE_G15IMG)) {
            int bigTranType = VpSpGetUtil.getVpSpVariInstance(mContext).getBigTranType();
            if (bigTranType == 2) {
                Intent intent = new Intent(OperaterActivity.this, UiUpdateG15ImgActivity.class);
                intent.putExtra("deviceNumber", String.valueOf(deviceNumber));
                intent.putExtra("deviceVersion", deviceVersion);
                intent.putExtra("deviceTestVersion", deviceTestVersion);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "不支持大数据传输", Toast.LENGTH_LONG).show();
            }
        } else if (oprater.equals(WEATHER_SETTING_STATUEINFO)) {
            WeatherStatusSetting weatherStatusSetting = new WeatherStatusSetting(0, true, EWeatherType.C);
            VPOperateManager.getInstance().settingWeatherStatusInfo(writeResponse, weatherStatusSetting, new IWeatherStatusDataListener() {
                @Override
                public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
                    String message = "settingWeatherStatusInfo onWeatherDataChange read:\n" + weatherStatusData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(WEATHER_SETTING_DATA)) {
            Logger.t(TAG).i("weatherStyle=" + weatherStyle);
            if (weatherStyle == 2) {
                setWeatherData2();
            } else {
                count++;
                if (count % 2 == 0) {
                    setWeatherData1();
                } else {
                    setWeatherData11();
                }
            }
        } else if (oprater.equals(LOW_POWER_READ)) {
            VPOperateManager.getInstance().readLowPower(writeResponse, new ILowPowerListener() {
                @Override
                public void onLowpowerDataDataChange(LowPowerData lowPowerData) {
                    String message = "onLowpowerDataDataChange read:\n" + lowPowerData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(LOW_POWER_OPEN)) {
            VPOperateManager.getInstance().settingLowpower(writeResponse, new ILowPowerListener() {
                @Override
                public void onLowpowerDataDataChange(LowPowerData lowPowerData) {
                    String message = "onLowpowerDataDataChange open:\n" + lowPowerData.toString();
                    Logger.t(TAG).i(message);
                }
            }, true);
        } else if (oprater.equals(LOW_POWER_CLOSE)) {
            VPOperateManager.getInstance().settingLowpower(writeResponse, new ILowPowerListener() {
                @Override
                public void onLowpowerDataDataChange(LowPowerData lowPowerData) {
                    String message = "onLowpowerDataDataChange close:\n" + lowPowerData.toString();
                    Logger.t(TAG).i(message);
                }
            }, false);
        } else if (oprater.equals(BP_FUNCTION_READ)) {
            VPOperateManager.getInstance().readBpFunctionState(writeResponse, new IBPFunctionListener() {
                @Override
                public void onDataChange(BpFunctionData bpFunctionData) {
                    String message = "readBpFunctionState close:\n" + bpFunctionData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(BP_FUNCTION_SETTING)) {
            VPOperateManager.getInstance().settingBpFunctionState(writeResponse, new IBPFunctionListener() {
                @Override
                public void onDataChange(BpFunctionData bpFunctionData) {
                    String message = "settingBpFunctionState close:\n" + bpFunctionData.toString();
                    Logger.t(TAG).i(message);
                }
            }, true);
        } else if (oprater.equals(DETECT_PTT)) {

            Intent intent = new Intent(OperaterActivity.this, PttActivity.class);
            intent.putExtra("inPttModel", isInPttModel);
            startActivity(intent);

        } else if (oprater.equals(DETECT_START_ECG) || oprater.equals(DETECT_STOP_ECG)) {
            startActivity(new Intent(OperaterActivity.this, EcgDetectActivity.class));
        } else if (oprater.equals(HEART_DETECT_STOP)) {
            Logger.t(TAG).i("HEART_DETECT_STOP");
            VPOperateManager.getInstance().stopDetectHeart(writeResponse);
        } else if (oprater.equals(BP_DETECT_START)) {
            tv1.setText(BP_DETECT_START + ",等待50s...");
            VPOperateManager.getInstance().startDetectBP(writeResponse, new IBPDetectDataListener() {
                @Override
                public void onDataChange(BpData bpData) {
                    String message = "BpData date statues:\n" + bpData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, EBPDetectModel.DETECT_MODEL_PUBLIC);
        } else if (oprater.equals(BP_DETECT_STOP)) {
            tv1.setText(BP_DETECT_STOP);
            VPOperateManager.getInstance().stopDetectBP(writeResponse, EBPDetectModel.DETECT_MODEL_PUBLIC);
        } else if (oprater.equals(BP_DETECTMODEL_SETTING)) {
            boolean isOpenPrivateModel = true;
            boolean isAngioAdjuste = false;
            BpSetting bpSetting = new BpSetting(isOpenPrivateModel, 111, 88);
            //是否开启动态血压调整模式，功能标志位在密码验证的返回
            bpSetting.setAngioAdjuste(isAngioAdjuste);
            VPOperateManager.getInstance().settingDetectBP(writeResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    String message = "BpSettingData:\n" + bpSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, bpSetting);
        } else if (oprater.equals(BP_DETECTMODEL_READ)) {
            VPOperateManager.getInstance().readDetectBP(writeResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {

                    String message = "BpSettingData:\n" + bpSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);

                }
            });
        } else if (oprater.equals(BP_DETECTMODEL_SETTING_ADJUSTE)) {
            boolean isOpenPrivateModel = false;
            boolean isAngioAdjuste = true;
            BpSetting bpSetting = new BpSetting(isOpenPrivateModel, 111, 88);
            //是否开启动态血压调整模式，功能标志位在密码验证的返回
            bpSetting.setAngioAdjuste(isAngioAdjuste);
            VPOperateManager.getInstance().settingDetectBP(writeResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    String message = "BpSettingData:\n" + bpSettingData.toString();
                    Logger.t(TAG).i(message);
//                    sendMsg(message, 1);
                }
            }, bpSetting);
        } else if (oprater.equals(BP_DETECTMODEL_SETTING_ADJUSTE_CANCEL)) {
            boolean isOpenPrivateModel = false;
            boolean isAngioAdjuste = true;
            BpSetting bpSetting = new BpSetting(isOpenPrivateModel, 111, 88);
            //是否开启动态血压调整模式，功能标志位在密码验证的返回
            bpSetting.setAngioAdjuste(isAngioAdjuste);
            VPOperateManager.getInstance().cancelAngioAdjust(writeResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    String message = "BpSettingData:\n" + bpSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, bpSetting);
        } else if (oprater.equals(PWD_COMFIRM)) {
            boolean is24Hourmodel = false;
            VPOperateManager.getInstance().confirmDevicePwd(writeResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    String message = "PwdData:\n" + pwdData.toString();
                    Logger.t(TAG).i(message);
//                    sendMsg(message, 1);

                    deviceNumber = pwdData.getDeviceNumber();
                    deviceVersion = pwdData.getDeviceVersion();
                    deviceTestVersion = pwdData.getDeviceTestVersion();
                    titleBleInfo.setText("设备号：" + deviceNumber + ",版本号：" + deviceVersion + ",\n测试版本号：" + deviceTestVersion);
                }
            }, new IDeviceFuctionDataListener() {
                @Override
                public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                    String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                    Logger.t(TAG).i(message);
//                    sendMsg(message, 2);
                    EFunctionStatus newCalcSport = functionSupport.getNewCalcSport();
                    if (newCalcSport != null && newCalcSport.equals(SUPPORT)) {
                        isNewSportCalc = true;
                    } else {
                        isNewSportCalc = false;
                    }
                    watchDataDay = functionSupport.getWathcDay();
                    weatherStyle = functionSupport.getWeatherStyle();
                    contactMsgLength = functionSupport.getContactMsgLength();
                    allMsgLenght = functionSupport.getAllMsgLength();
                    isSleepPrecision = functionSupport.getPrecisionSleep() == SUPPORT;
                }
            }, socialMsgDataListener, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "CustomSettingData:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
//                    sendMsg(message, 4);
                }
            }, "0000", is24Hourmodel);

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    readOriginData();
//                }
//            },1000);


        } else if (oprater.equals(PWD_MODIFY)) {
            VPOperateManager.getInstance().modifyDevicePwd(writeResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwd) {
                    String message = "PwdData:\n" + pwd.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, "0000");
        } else if (oprater.equals(SPORT_CURRENT_READ)) {
            VPOperateManager.getInstance().readSportStep(writeResponse, new ISportDataListener() {
                @Override
                public void onSportDataChange(SportData sportData) {
                    String message = "当前计步:\n" + sportData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(PERSONINFO_SYNC)) {
            VPOperateManager.getInstance().syncPersonInfo(writeResponse, new IPersonInfoDataListener() {
                @Override
                public void OnPersoninfoDataChange(EOprateStauts EOprateStauts) {
                    String message = "同步个人信息:\n" + EOprateStauts.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new PersonInfoData(ESex.MAN, 178, 60, 20, 8000));
        } else if (oprater.equals(CAMERA_START)) {
            VPOperateManager.getInstance().startCamera(writeResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(ECameraStatus oprateStauts) {

                }


            });
        } else if (oprater.equals(CAMERA_STOP)) {
            VPOperateManager.getInstance().stopCamera(writeResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(ECameraStatus oprateStauts) {

                }


            });
        } else if (oprater.equals(ALARM_SETTING)) {
            List<AlarmSetting> alarmSettingList = new ArrayList<>(3);

            AlarmSetting alarmSetting1 = new AlarmSetting(14, 10, true);
            AlarmSetting alarmSetting2 = new AlarmSetting(15, 20, true);
            AlarmSetting alarmSetting3 = new AlarmSetting(16, 30, true);

            alarmSettingList.add(alarmSetting1);
            alarmSettingList.add(alarmSetting2);
            alarmSettingList.add(alarmSetting3);

            VPOperateManager.getInstance().settingAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    String message = "设置闹钟:\n" + alarmData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, alarmSettingList);
        } else if (oprater.equals(ALARM_READ)) {
            VPOperateManager.getInstance().readAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    String message = "读取闹钟:\n" + alarmData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(ALARM_NEW_READ)) {
            VPOperateManager.getInstance().readAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    String message = "读取闹钟[新版]:\n" + alarmData2.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(ALARM_NEW_DELETE)) {
            int deleteID = 1;
            Alarm2Setting alarm2Setting = getMultiAlarmSetting();
            alarm2Setting.setAlarmId(deleteID);
            VPOperateManager.getInstance().deleteAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    String message = "删除闹钟[新版]:\n" + alarmData2.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
                //String bluetoothAddress, int alarmId, int alarmHour, int alarmMinute, String repeatStatus, int scene, String unRepeatDate, boolean isOpen
            }, alarm2Setting);
        } else if (oprater.equals(ALARM_NEW_LISTENER)) {
            VPOperateManager.getInstance().setOnDeviceAlarm2ChangedListener(new OnDeviceAlarm2ChangedListener() {
                @Override
                public void onDeviceAlarm2Changed() {
                    sendMsg("设备端闹钟状态改变了，请调用[readAlarm2更新闹钟列表]", 1);
                }
            });

        } else if (oprater.equals(ALARM_NEW_ADD)) {
            Alarm2Setting alarm2Setting = getMultiAlarmSetting();
            VPOperateManager.getInstance().addAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    String message = "添加闹钟[新版]:\n" + alarmData2.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, alarm2Setting);
        } else if (oprater.equals(ALARM_NEW_MODIFY)) {
            Alarm2Setting alarm2Setting = getMultiAlarmSetting();
            int modifyID = 2;
            alarm2Setting.setAlarmId(modifyID);
            alarm2Setting.setAlarmHour(10);
            alarm2Setting.setOpen(false);
            VPOperateManager.getInstance().modifyAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    String message = "修改闹钟[新版]:\n" + alarmData2.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, alarm2Setting);
        } else if (oprater.equals(LONGSEAT_SETTING_OPEN)) {
            VPOperateManager.getInstance().settingLongSeat(writeResponse, new LongSeatSetting(10, 35, 11, 45, 60, true), new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeat) {
                    String message = "设置久坐-打开:\n" + longSeat.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LONGSEAT_SETTING_CLOSE)) {
            VPOperateManager.getInstance().settingLongSeat(writeResponse, new LongSeatSetting(10, 40, 12, 40, 40, false), new ILongSeatDataListener() {

                @Override
                public void onLongSeatDataChange(LongSeatData longSeat) {
                    String message = "设置久坐-关闭:\n" + longSeat.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LONGSEAT_READ)) {
            VPOperateManager.getInstance().readLongSeat(writeResponse, new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeat) {
                    String message = "设置久坐-读取:\n" + longSeat.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LANGUAGE_CHINESE)) {
            VPOperateManager.getInstance().settingDeviceLanguage(writeResponse, new ILanguageDataListener() {
                @Override
                public void onLanguageDataChange(LanguageData languageData) {
                    String message = "设置语言(中文):\n" + languageData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, ELanguage.CHINA);
        } else if (oprater.equals(LANGUAGE_ENGLISH)) {
            VPOperateManager.getInstance().settingDeviceLanguage(writeResponse, new ILanguageDataListener() {
                @Override
                public void onLanguageDataChange(LanguageData languageData) {
                    String message = "设置语言(英文):\n" + languageData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, ELanguage.ENGLISH);
        } else if (oprater.equals(BATTERY)) {
            VPOperateManager.getInstance().readBattery(writeResponse, new IBatteryDataListener() {
                @Override
                public void onDataChange(BatteryData batteryData) {
                    String message = "电池等级:\n" + batteryData.getBatteryLevel() + "\n" + "电量:" + batteryData.getBatteryLevel() * 25 + "%";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(NIGHT_TURN_WRIST_READ)) {
            VPOperateManager.getInstance().readNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-读取:\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(NIGHT_TURN_WRIST_OPEN)) {
            VPOperateManager.getInstance().settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-打开:\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, true);
        } else if (oprater.equals(NIGHT_TURN_WRIST_CLOSE)) {
            VPOperateManager.getInstance().settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-关闭:\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, false);
        } else if (oprater.equals(NIGHT_TURN_WRIST_CUSTOM_TIME)) {
            final boolean isOpen = true;
            TimeData startTime = new TimeData(10, 0);
            TimeData endTime = new TimeData(20, 0);
            VPOperateManager.getInstance().settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-" + isOpen + ":\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, isOpen, startTime, endTime);
        } else if (oprater.equals(NIGHT_TURN_WRIST_CUSTOM_TIME_LEVEL)) {
            final boolean isOpen = true;
            TimeData startTime = new TimeData(10, 0);
            TimeData endTime = new TimeData(20, 0);
            int level = 2;
            VPOperateManager.getInstance().settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-" + isOpen + ":\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new NightTurnWristSetting(isOpen, startTime, endTime, level));
        } else if (oprater.equals(DISCONNECT)) {
            VPOperateManager.getInstance().disconnectWatch(writeResponse);
            finish();
        } else if (oprater.equals(FINDPHONE)) {
            VPOperateManager.getInstance().settingFindPhoneListener(new IFindPhonelistener() {
                @Override
                public void findPhone() {
                    String message = "(监听到手环要查找手机)-where is the phone,make some noise!";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void deviceFindingCYPhone() {

                }
            });
        } else if (oprater.equals(DEVICE_COUSTOM_READ)) {
            VPOperateManager.getInstance().readCustomSetting(writeResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-读取:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(DEVICE_COUSTOM_SETTING)) {
            boolean isHaveMetricSystem = true;
            boolean isMetric = true;
            boolean is24Hour = true;
            boolean isOpenAutoHeartDetect = true;
            boolean isOpenAutoBpDetect = true;
            boolean isCelsius = true;
            EFunctionStatus isOpenSportRemain = UNSUPPORT;
            EFunctionStatus isOpenVoiceBpHeart = UNSUPPORT;
            EFunctionStatus isOpenFindPhoneUI = UNSUPPORT;
            EFunctionStatus isOpenStopWatch = UNSUPPORT;
            EFunctionStatus isOpenSpo2hLowRemind = UNSUPPORT;
            EFunctionStatus isOpenWearDetectSkin = UNSUPPORT;
            EFunctionStatus isOpenAutoInCall = UNSUPPORT;
            EFunctionStatus isOpenAutoHRV = UNSUPPORT;
            EFunctionStatus isOpenDisconnectRemind = UNSUPPORT;
            EFunctionStatus isAutoTemperatureDetect = UNSUPPORT;
            boolean isSupportSettingsTemperatureUnit = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportSettingsTemperatureUnit();//是否支持温度单位设置
            boolean isSupportSleep = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportPreciseSleep();//是否支持精准睡眠

            boolean isCanReadTempture = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportReadTempture();//是否支持读取温度
            boolean isCanDetectTempByApp = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportCheckTemptureByApp();//是否可以通过app监测体温
            boolean isCanDetectBloodGlucoseByApp = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportBloodGlucoseDetect();//是否可以通过app监测血糖

            Logger.t(TAG).i("是否可以读取体温：" + isCanReadTempture + " 是否可以通过app自动检测体温");

            CustomSetting customSetting = new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect,
                    isOpenAutoBpDetect, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch,
                    isOpenSpo2hLowRemind, isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind
            );
            customSetting.setIsOpenLongClickLockScreen(SUPPORT_CLOSE);
            if (isSupportSettingsTemperatureUnit) {
                customSetting.setTemperatureUnit(VpSpGetUtil.getVpSpVariInstance(mContext).getTemperatureUnit()
                        == ETemperatureUnit.CELSIUS ? ETemperatureUnit.FAHRENHEIT : ETemperatureUnit.CELSIUS);
            } else {
                customSetting.setTemperatureUnit(ETemperatureUnit.NONE);
            }
            if (isCanDetectTempByApp) {
                boolean isOpenTempDetect = VpSpGetUtil.getVpSpVariInstance(mContext).isOpenTemperatureDetectByApp();
                customSetting.setIsOpenAutoTemperatureDetect(isOpenTempDetect ? SUPPORT_CLOSE : SUPPORT_OPEN);
            } else {
                customSetting.setIsOpenAutoTemperatureDetect(UNSUPPORT);
            }
            if (isCanDetectBloodGlucoseByApp) {
                boolean isOpenTempDetect = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportBloodGlucoseDetect();
                customSetting.setIsOpenAutoTemperatureDetect(isOpenTempDetect ? SUPPORT_CLOSE : SUPPORT_OPEN);
            } else {
                customSetting.setIsOpenAutoTemperatureDetect(UNSUPPORT);
            }
            VPOperateManager.getInstance().changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-设置:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, customSetting);
        } else if (oprater.equals(DEVICE_ECG_ALWAYS_OPEN)) {
            boolean isHaveMetricSystem = true;
            boolean isMetric = true;
            boolean is24Hour = true;
            boolean isOpenAutoHeartDetect = true;
            boolean isOpenAutoBpDetect = true;
            boolean isCelsius = true;
            EFunctionStatus isOpenSportRemain = UNSUPPORT;
            EFunctionStatus isOpenVoiceBpHeart = UNSUPPORT;
            EFunctionStatus isOpenFindPhoneUI = UNSUPPORT;
            EFunctionStatus isOpenStopWatch = UNSUPPORT;
            EFunctionStatus isOpenSpo2hLowRemind = UNSUPPORT;
            EFunctionStatus isOpenWearDetectSkin = UNSUPPORT;
            EFunctionStatus isOpenAutoInCall = UNSUPPORT;
            EFunctionStatus isOpenAutoHRV = UNSUPPORT;
            EFunctionStatus isOpenDisconnectRemind = UNSUPPORT;
            EFunctionStatus isAutoTemperatureDetect = UNSUPPORT;
            boolean isSupportSettingsTemperatureUnit = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportSettingsTemperatureUnit();//是否支持温度单位设置
            boolean isSupportSleep = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportPreciseSleep();//是否支持精准睡眠

            boolean isCanReadTempture = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportReadTempture();//是否支持读取温度
            boolean isCanDetectTempByApp = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportCheckTemptureByApp();//是否可以通过app监测体温


            Logger.t(TAG).i("是否可以读取体温：" + isCanReadTempture + " 是否可以通过app自动检测体温");

            CustomSetting customSetting = new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect,
                    isOpenAutoBpDetect, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch,
                    isOpenSpo2hLowRemind, isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind
            );
            customSetting.setIsOpenLongClickLockScreen(SUPPORT_CLOSE);
            if (isSupportSettingsTemperatureUnit) {
                customSetting.setTemperatureUnit(VpSpGetUtil.getVpSpVariInstance(mContext).getTemperatureUnit()
                        == ETemperatureUnit.CELSIUS ? ETemperatureUnit.FAHRENHEIT : ETemperatureUnit.CELSIUS);
            } else {
                customSetting.setTemperatureUnit(ETemperatureUnit.NONE);
            }
            if (isCanDetectTempByApp) {
                boolean isOpenTempDetect = VpSpGetUtil.getVpSpVariInstance(mContext).isOpenTemperatureDetectByApp();
                customSetting.setIsOpenAutoTemperatureDetect(isOpenTempDetect ? SUPPORT_CLOSE : SUPPORT_OPEN);
            } else {
                customSetting.setIsOpenAutoTemperatureDetect(UNSUPPORT);
            }

            customSetting.setEcgAlwaysOpen(SUPPORT_OPEN);
            VPOperateManager.getInstance().changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "个性化状态--设置:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, customSetting);
        } else if (oprater.equals(DEVICE_ECG_ALWAYS_CLOSE)) {
            boolean isHaveMetricSystem = true;
            boolean isMetric = true;
            boolean is24Hour = true;
            boolean isOpenAutoHeartDetect = true;
            boolean isOpenAutoBpDetect = true;
            boolean isCelsius = true;
            EFunctionStatus isOpenSportRemain = UNSUPPORT;
            EFunctionStatus isOpenVoiceBpHeart = UNSUPPORT;
            EFunctionStatus isOpenFindPhoneUI = UNSUPPORT;
            EFunctionStatus isOpenStopWatch = UNSUPPORT;
            EFunctionStatus isOpenSpo2hLowRemind = UNSUPPORT;
            EFunctionStatus isOpenWearDetectSkin = UNSUPPORT;
            EFunctionStatus isOpenAutoInCall = UNSUPPORT;
            EFunctionStatus isOpenAutoHRV = UNSUPPORT;
            EFunctionStatus isOpenDisconnectRemind = UNSUPPORT;
            EFunctionStatus isAutoTemperatureDetect = UNSUPPORT;
            boolean isSupportSettingsTemperatureUnit = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportSettingsTemperatureUnit();//是否支持温度单位设置
            boolean isSupportSleep = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportPreciseSleep();//是否支持精准睡眠

            boolean isCanReadTempture = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportReadTempture();//是否支持读取温度
            boolean isCanDetectTempByApp = VpSpGetUtil.getVpSpVariInstance(mContext).isSupportCheckTemptureByApp();//是否可以通过app监测体温


            Logger.t(TAG).i("是否可以读取体温：" + isCanReadTempture + " 是否可以通过app自动检测体温");

            CustomSetting customSetting = new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect,
                    isOpenAutoBpDetect, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch,
                    isOpenSpo2hLowRemind, isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind
            );
            customSetting.setIsOpenLongClickLockScreen(SUPPORT_CLOSE);
            if (isSupportSettingsTemperatureUnit) {
                customSetting.setTemperatureUnit(VpSpGetUtil.getVpSpVariInstance(mContext).getTemperatureUnit()
                        == ETemperatureUnit.CELSIUS ? ETemperatureUnit.FAHRENHEIT : ETemperatureUnit.CELSIUS);
            } else {
                customSetting.setTemperatureUnit(ETemperatureUnit.NONE);
            }
            if (isCanDetectTempByApp) {
                boolean isOpenTempDetect = VpSpGetUtil.getVpSpVariInstance(mContext).isOpenTemperatureDetectByApp();
                customSetting.setIsOpenAutoTemperatureDetect(isOpenTempDetect ? SUPPORT_CLOSE : SUPPORT_OPEN);
            } else {
                customSetting.setIsOpenAutoTemperatureDetect(UNSUPPORT);
            }

            customSetting.setEcgAlwaysOpen(SUPPORT_CLOSE);
            VPOperateManager.getInstance().changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "个性化状态--设置:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, customSetting);
        } else if (oprater.equals(CHECK_WEAR_SETING_OPEN)) {
            CheckWearSetting checkWearSetting = new CheckWearSetting();
            checkWearSetting.setOpen(true);
            VPOperateManager.getInstance().setttingCheckWear(writeResponse, new ICheckWearDataListener() {
                @Override
                public void onCheckWearDataChange(CheckWearData checkWearData) {
                    String message = "佩戴检测-打开:\n" + checkWearData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, checkWearSetting);
        } else if (oprater.equals(CHECK_WEAR_SETING_CLOSE)) {
            CheckWearSetting checkWearSetting = new CheckWearSetting();
            checkWearSetting.setOpen(false);
            VPOperateManager.getInstance().setttingCheckWear(writeResponse, new ICheckWearDataListener() {
                @Override
                public void onCheckWearDataChange(CheckWearData checkWearData) {
                    String message = "佩戴检测-关闭:\n" + checkWearData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, checkWearSetting);
        } else if (oprater.equals(FINDDEVICE_SETTING_OPEN)) {
            VPOperateManager.getInstance().settingFindDevice(writeResponse, new IFindDeviceDatalistener() {
                @Override
                public void onFindDevice(FindDeviceData findDeviceData) {
                    String message = "防丢-打开:\n" + findDeviceData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, true);
        } else if (oprater.equals(FINDDEVICE_SETTING_CLOSE)) {
            VPOperateManager.getInstance().settingFindDevice(writeResponse, new IFindDeviceDatalistener() {
                @Override
                public void onFindDevice(FindDeviceData findDeviceData) {
                    String message = "防丢-关闭:\n" + findDeviceData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, false);
        } else if (oprater.equals(FINDDEVICE_READ)) {
            VPOperateManager.getInstance().readFindDevice(writeResponse, new IFindDeviceDatalistener() {
                @Override
                public void onFindDevice(FindDeviceData findDeviceData) {
                    String message = "防丢-读取:\n" + findDeviceData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SOCIAL_MSG_READ)) {
            VPOperateManager.getInstance().readSocialMsg(writeResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒1-读取:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒2-读取:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SOCIAL_MSG_SETTING2)) {
            FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
            socailMsgData.setPhone(SUPPORT);
            socailMsgData.setMsg(SUPPORT);
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSina(UNSUPPORT);
            socailMsgData.setFlickr(UNSUPPORT);
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setLine(SUPPORT_OPEN);
            socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);

            socailMsgData.setTikTok(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setTelegram(SUPPORT_OPEN);
            socailMsgData.setConnected2_me(EFunctionStatus.SUPPORT_OPEN);


            socailMsgData.setPhone(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setKakaoTalk(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setShieldPolice(SUPPORT_OPEN);


            VPOperateManager.getInstance().settingSocialMsg(writeResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒-设置:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒-设置2:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, socailMsgData);
        } else if (oprater.equals(SOCIAL_MSG_SETTING)) {
            FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
            socailMsgData.setPhone(SUPPORT);
            socailMsgData.setMsg(SUPPORT);
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSina(UNSUPPORT);
            socailMsgData.setFlickr(UNSUPPORT);
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setLine(SUPPORT_OPEN);
            socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);

            socailMsgData.setTikTok(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setTelegram(SUPPORT_OPEN);
            socailMsgData.setConnected2_me(EFunctionStatus.SUPPORT_OPEN);

            socailMsgData.setPhone(EFunctionStatus.SUPPORT_CLOSE);
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_CLOSE);
            socailMsgData.setKakaoTalk(SUPPORT_CLOSE);
            socailMsgData.setShieldPolice(SUPPORT_CLOSE);

            VPOperateManager.getInstance().settingSocialMsg(writeResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒-设置:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒-设置2:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, socailMsgData);
        } else if (oprater.equals(LIANSUO_SOS)) {
            String message = " LIANSUO_SOS";
            VPOperateManager.getInstance().settingSOSListener(new ISOSListener() {
                @Override
                public void sos() {
                    String liansuo_sos_call_back = "liansuo_sos call back";
                    Logger.t(TAG).i(liansuo_sos_call_back);
                    sendMsg(liansuo_sos_call_back, 1);
                }
            });
        } else if (oprater.equals(LIANSUO_SEND_ORDER)) {
            VPOperateManager.getInstance().sendToSoldierCommand(writeResponse, new IResponseListener() {
                @Override
                public void response(int state) {
                    String message = "liansuo send cmd call back:" + state;
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LIANSUO_SEND_CONTENT)) {
            VPOperateManager.getInstance().sendToSoldierContent(writeResponse, new SoldierContentSetting("123123123123123123123123123123123"), new IResponseListener() {
                @Override
                public void response(int state) {
                    String message = "liansuo send content call back:" + state;
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SOCIAL_MSG_SEND)) {
            startActivity(new Intent(OperaterActivity.this, NotificationSettingsActivity.class));
//            /**电话,可以只传电话号码**/
//            final ContentSetting contentphoneSetting0 = new ContentPhoneSetting(ESocailMsg.PHONE, "010-6635214");
//            /**电话,传联系人姓名以及电话号码，最终显示的联系人姓名**/
//            ContentSetting contentphoneSetting1 = new ContentPhoneSetting(ESocailMsg.PHONE, "测试头", "010-6635214");
//
//            /**短信，可以只传电话号码**/
//            ContentSetting contentsmsSetting2 = new ContentSmsSetting(ESocailMsg.SMS, "010-6635214", "测试反馈 SMS");
//            /**短信，传联系人姓名以及电话号码，最终显示的联系人姓名**/
//            ContentSetting contentsmsSetting3 = new ContentSmsSetting(ESocailMsg.SMS, "测试头", "010-6635214", "测试反馈 SMS");
//
//            /**第三方APP推送,发送前先通过密码验证获取FunctionSocailMsgData的状态**/
//            ContentSetting contentsociaSetting4 = new ContentSocailSetting(ESocailMsg.SHIELD_POLICE, "警右", "坦白从宽，牢底坐穿，抗拒从严，回家过年");
//            VPOperateManager.getInstance().sendSocialMsgContent(writeResponse, contentsociaSetting4);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ContentSetting contentsociaSetting5 = new ContentSocailSetting(ESocailMsg.MESSENGER, "vepo", "测试反馈 MESSENGER");
//                    VPOperateManager.getInstance().sendSocialMsgContent(writeResponse, contentsociaSetting5);
//
//                }
//            }, 2000);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ContentSetting contentsociaSetting6 = new ContentSocailSetting(ESocailMsg.PHONE, "vepo", "测试反馈 PHONE");
//                    VPOperateManager.getInstance().sendSocialMsgContent(writeResponse, contentphoneSetting0);
//                }
//            }, 4000);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ContentSetting contentsociaSetting6 = new ContentSocailSetting(ESocailMsg.CONNECTED2_ME, "vepo", "测试反馈 CONNECTED2_ME");
//                    VPOperateManager.getInstance().sendSocialMsgContent(writeResponse, contentsociaSetting6);
//                }
//            }, 6000);
//            ContentSetting contentsociaSetting6 = new ContentSocailSetting(ESocailMsg.G15MSG, "G15", "ABCDEFG,今天是星期五，明天星期六");
//            VPOperateManager.getInstance().sendG15MsgContent(writeResponse, "G15", "ABCDEFG,今天是星期五,敬挽发大水发大水发放大范德萨", new IG15MessageListener() {
//                @Override
//                public void onG15MessageSendSuccess() {
//                    Toast.makeText(mContext, "收到应答", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onG15MessageSendFailed() {
//                    Toast.makeText(mContext, "没有收到应答", Toast.LENGTH_SHORT).show();
//                }
//            });

        } else if (oprater.equals(SOCIAL_PHONE_IDLE_OR_OFFHOOK)) {
            VPOperateManager.getInstance().offhookOrIdlePhone(writeResponse);
        } else if (oprater.equals(DEVICE_CONTROL_PHONE)) {
            VPOperateManager.getInstance().settingDeviceControlPhone(new IDeviceControlPhoneModelState() {

                @Override
                public void inPttModel() {
                    String message = "手表提示:手表进入ptt模式\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void outPttModel() {
                    String message = "手表提示:手表退出ptt模式\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void rejectPhone() {
                    String message = "手表提示:请挂断来电\n";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void cliencePhone() {
                    String message = "手表提示:请来电静音\n";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void appAnswerCall() {
                    String message = "手表提示:app处理接听来电\n";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void knocknotify(int type) {
                    String message = "手表提示:敲击提醒，1表示单击，2表示双击\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void sos() {
                    String message = "手表提示:sos\n";
                    Logger.t(TAG).i(message);
                }

                public void nextMusic() {
                    String message = "手表提示:下一曲\n";
                    Logger.t(TAG).i(message);
                }

                public void previousMusic() {
                    String message = "手表提示:上一曲\n";
                    Logger.t(TAG).i(message);
                }

                public void pauseAndPlayMusic() {
                    String message = "手表提示:暂停和播放\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void pauseMusic() {
                    String message = "手表提示:暂停\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void playMusic() {
                    String message = "手表提示:播放\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void voiceUp() {
                    String message = "手表提示:调高音量\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void voiceDown() {
                    String message = "手表提示:调低音量\n";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void oprateMusicSuccess() {

                }

                @Override
                public void oprateMusicFail() {

                }


            });
        } else if (oprater.equals(CLEAR_DEVICE_DATA)) {
            VPOperateManager.getInstance().clearDeviceData(writeResponse);
            finish();
        } else if (oprater.equals(HEARTWRING_READ)) {
            VPOperateManager.getInstance().readHeartWarning(writeResponse, new IHeartWaringDataListener() {
                @Override
                public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                    String message = "心率报警-读取:\n" + heartWaringData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(HEARTWRING_OPEN)) {
            VPOperateManager.getInstance().settingHeartWarning(writeResponse, new IHeartWaringDataListener() {
                @Override
                public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                    String message = "心率报警-打开:\n" + heartWaringData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new HeartWaringSetting(120, 110, true));
        } else if (oprater.equals(HEARTWRING_CLOSE)) {
            VPOperateManager.getInstance().settingHeartWarning(writeResponse, new IHeartWaringDataListener() {
                @Override
                public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                    String message = "心率报警-关闭:\n" + heartWaringData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new HeartWaringSetting(120, 110, false));
        } else if (oprater.equals(SPO2H_OPEN)) {
            byte[] cmd = new byte[20];
            cmd[0] = (byte) 0xf3;
            cmd[1] = (byte) 0x08;
//            VPOperateManager.getInstance().sendOrder(writeResponse, cmd);
            VPOperateManager.getInstance().startDetectSPO2H(writeResponse, new ISpo2hDataListener() {
                @Override
                public void onSpO2HADataChange(Spo2hData spo2HData) {
                    String message = "血氧-开始:\n" + spo2HData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new ILightDataCallBack() {
                @Override
                public void onGreenLightDataChange(int[] data) {
                    String message = "血氧-光电信号:\n" + Arrays.toString(data);
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(SPO2H_CLOSE)) {
            VPOperateManager.getInstance().stopDetectSPO2H(writeResponse, new ISpo2hDataListener() {
                @Override
                public void onSpO2HADataChange(Spo2hData spo2HData) {
                    String message = "血氧-结束:\n" + spo2HData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SPO2H_AUTO_DETECT_READ))
            VPOperateManager.getInstance().readSpo2hAutoDetect(writeResponse, new IAllSetDataListener() {
                @Override
                public void onAllSetDataChangeListener(AllSetData allSetData) {
                    String message = "血氧自动检测-读取\n" + allSetData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        else if (oprater.equals(SPO2H_AUTO_DETECT_OPEN)) {
            int setting = 0, open = 1;
            AllSetSetting mAlarmSetting = new AllSetSetting(EAllSetType.SPO2H_NIGHT_AUTO_DETECT, 22, 0, 8, 0, setting, open);
            VPOperateManager.getInstance().settingSpo2hAutoDetect(writeResponse, new IAllSetDataListener() {
                @Override
                public void onAllSetDataChangeListener(AllSetData allSetData) {
                    String message = "血氧自动检测-打开\n" + allSetData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, mAlarmSetting);
        } else if (oprater.equals(SPO2H_AUTO_DETECT_CLOSE)) {
            int setting = 0, colse = 0;
            AllSetSetting mAlarmSetting = new AllSetSetting(EAllSetType.SPO2H_NIGHT_AUTO_DETECT, 22, 0, 8, 0, setting, colse);
            VPOperateManager.getInstance().settingSpo2hAutoDetect(writeResponse, new IAllSetDataListener() {
                @Override
                public void onAllSetDataChangeListener(AllSetData allSetData) {
                    String message = "血氧自动检测-打开\n" + allSetData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, mAlarmSetting);
        } else if (oprater.equals(FATIGUE_OPEN)) {
            VPOperateManager.getInstance().startDetectFatigue(writeResponse, new IFatigueDataListener() {
                @Override
                public void onFatigueDataListener(FatigueData fatigueData) {
                    String message = "疲劳度-开始:\n" + fatigueData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(FATIGUE_CLOSE)) {
            VPOperateManager.getInstance().stopDetectFatigue(writeResponse, new IFatigueDataListener() {
                @Override
                public void onFatigueDataListener(FatigueData fatigueData) {
                    String message = "疲劳度-结束:\n" + fatigueData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(WOMEN_SETTING)) {
            VPOperateManager.getInstance().settingWomenState(writeResponse, new IWomenDataListener() {
                @Override
                public void onWomenDataChange(WomenData womenData) {
                    String message = "女性状态-设置:\n" + womenData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new WomenSetting(EWomenStatus.PREING, new TimeData(2016, 3, 1), new TimeData(2017, 1, 14)));
        } else if (oprater.equals(WOMEN_READ)) {
            VPOperateManager.getInstance().readWomenState(writeResponse, new IWomenDataListener() {
                @Override
                public void onWomenDataChange(WomenData womenData) {
                    String message = "女性状态-读取:\n" + womenData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(COUNT_DOWN_WATCH_CLOSE_UI)) {
            int second = 11;
            boolean isOpenWatchUI = false;
            boolean isCountDownByWatch = true;
            CountDownSetting countDownSetting = new CountDownSetting(second, isOpenWatchUI, isCountDownByWatch);
            VPOperateManager.getInstance().settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
                @Override
                public void OnCountDownDataChange(CountDownData countDownData) {
                    String message = "倒计时-watch:\n" + countDownData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(COUNT_DOWN_WATCH_OPEN_UI)) {
            int second = 11;
            boolean isOpenWatchUI = true;
            boolean isCountDownByWatch = true;
            CountDownSetting countDownSetting = new CountDownSetting(second, isOpenWatchUI, isCountDownByWatch);
            VPOperateManager.getInstance().settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
                @Override
                public void OnCountDownDataChange(CountDownData countDownData) {
                    String message = "倒计时-App:\n" + countDownData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(COUNT_DOWN_APP)) {
            int second = 11;
            boolean isOpenWatchUI = true;
            boolean isCountDownByWatch = false;
            CountDownSetting countDownSetting = new CountDownSetting(second, isOpenWatchUI, isCountDownByWatch);
            VPOperateManager.getInstance().settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
                @Override
                public void OnCountDownDataChange(CountDownData countDownData) {
                    String message = "倒计时-App:\n" + countDownData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(COUNT_DOWN_APP_READ)) {
            VPOperateManager.getInstance().readCountDown(writeResponse, new ICountDownListener() {
                @Override
                public void OnCountDownDataChange(CountDownData countDownData) {
                    String message = "倒计时-读取:\n" + countDownData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });

        } else if (oprater.equals(AIM_SPROT_CALC)) {
            Intent intent = new Intent(OperaterActivity.this, AimSportCalcActivity.class);
            intent.putExtra("isnewsportcalc", isNewSportCalc);
            startActivity(intent);
        } else if (oprater.equals(READ_CHANTING)) {

            long timestamp = Calendar.getInstance().getTimeInMillis();
            timestamp = 1616557585;
            VPOperateManager.getInstance().readChantingData(writeResponse, new ChantingSetting(timestamp), new IChantingDataListener() {
                @Override
                public void onChantingDataChange(ChantingData chantingData) {
                    String message = "读取诵经计数:" + chantingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(GPS_REPORT_START)) {
            Intent intent = new Intent(OperaterActivity.this, GpsReportActivity.class);
            startActivity(intent);
        } else if (oprater.equals(GPS_KAABA)) {
            Intent intent = new Intent(OperaterActivity.this, GpsLatlonActivity.class);
            startActivity(intent);
        } else if (oprater.equals(SCREEN_LIGHT_SETTING)) {
            //默认的是【22:00-07:00】设置成2档，其他时间设置成4档，用户可以自定义
            VPOperateManager.getInstance().settingScreenLight(writeResponse, new IScreenLightListener() {
                @Override
                public void onScreenLightDataChange(ScreenLightData screenLightData) {
                    String message = "屏幕调节数据-设置:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new ScreenSetting(22, 0, 7, 0, 2, 4));
        } else if (oprater.equals(SCREEN_LIGHT_READ)) {
            VPOperateManager.getInstance().readScreenLight(writeResponse, new IScreenLightListener() {
                @Override
                public void onScreenLightDataChange(ScreenLightData screenLightData) {
                    String message = "屏幕调节数据-读取:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SCREEN_STYLE_READ)) {
            VPOperateManager.getInstance().readScreenStyle(writeResponse, new IScreenStyleListener() {
                @Override
                public void onScreenStyleDataChange(ScreenStyleData screenLightData) {
                    String message = "屏幕样式-读取:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SCREEN_STYLE_SETTING)) {
            int screenstyle = 2;
            VPOperateManager.getInstance().settingScreenStyle(writeResponse, new IScreenStyleListener() {
                @Override
                public void onScreenStyleDataChange(ScreenStyleData screenLightData) {
                    String message = "屏幕样式-设置:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, screenstyle);
        } else if (oprater.equals(INSTITUTION_TRANSLATION)) {
            Intent intent = new Intent(OperaterActivity.this, InstitutionActivity.class);
            startActivity(intent);

        } else if (oprater.equals(READ_TEMPTURE_DATA)) {
            ReadOriginSetting readOriginSetting = new ReadOriginSetting(0, 1, false, watchDataDay);
            VPOperateManager.getInstance().readTemptureDataBySetting(writeResponse, new ITemptureDataListener() {
                @Override
                public void onTemptureDataListDataChange(List<TemptureData> temptureDataList) {
                    String message = "onTemptureDataListDataChange:" + temptureDataList.size();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "温度数据-读取进度:" + "day=" + day + ",currentPackage=" + currentPackage + ",allPackage=" + allPackage;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "onReadOriginProgress:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginComplete() {
                    String message = "onReadOriginComplete";
                    Logger.t(TAG).i(message);
                }
            }, readOriginSetting);
        } else if (oprater.equals(READ_HEALTH_SLEEP)) {
            VPOperateManager.getInstance().readSleepData(writeResponse, new ISleepDataListener() {
                        @Override
                        public void onSleepDataChange(String day, SleepData sleepData) {
                            String message = "";
                            if (sleepData instanceof SleepPrecisionData && isSleepPrecision) {
                                SleepPrecisionData sleepPrecisionData = (SleepPrecisionData) sleepData;
                                message = "精准睡眠数据-返回:" + sleepPrecisionData.toString();
                            } else {
                                message = "普通睡眠数据-返回:" + sleepData.toString();
                            }
                            Logger.t(TAG).i(message);
                            sendMsg(message, 1);
                        }

                        @Override
                        public void onSleepProgress(float progress) {

                            String message = "睡眠数据-读取进度:" + "progress=" + progress;
                            Logger.t(TAG).i(message);
                        }

                        @Override
                        public void onSleepProgressDetail(String day, int packagenumber) {
                            String message = "睡眠数据-读取进度:" + "day=" + day + ",packagenumber=" + packagenumber;
                            Logger.t(TAG).i(message);
                        }

                        @Override
                        public void onReadSleepComplete() {
                            String message = "睡眠数据-读取结束";
                            Logger.t(TAG).i(message);
                        }
                    }, watchDataDay
            );
        } else if (oprater.equals(READ_HEALTH_SLEEP_FROM)) {
            int beforeYesterday = 2;
            VPOperateManager.getInstance().readSleepDataFromDay(writeResponse, new ISleepDataListener() {
                        @Override
                        public void onSleepDataChange(String day, SleepData sleepData) {
                            String message = getDay(day) + "-睡眠数据-返回:" + sleepData.toString();
                            Logger.t(TAG).i(message);
                            sendMsg(message, 1);
                        }

                        @Override
                        public void onSleepProgress(float progress) {
                            String message = "睡眠数据-读取进度:" + "progress=" + progress;
                            Logger.t(TAG).i(message);
                        }

                        @Override
                        public void onSleepProgressDetail(String day, int packagenumber) {
                            String message = "睡眠数据-读取进度:" + "day=" + day + ",packagenumber=" + packagenumber;
                            Logger.t(TAG).i(message);
                        }

                        @Override
                        public void onReadSleepComplete() {
                            String message = "睡眠数据-读取结束";
                            Logger.t(TAG).i(message);
                        }
                    }
                    , beforeYesterday, watchDataDay);
        } else if (oprater.equals(READ_HEALTH_SLEEP_SINGLEDAY)) {
            int yesterday = 1;
            VPOperateManager.getInstance().readSleepDataSingleDay(writeResponse, new ISleepDataListener() {
                @Override
                public void onSleepDataChange(String day, SleepData sleepData) {
                    String message = getDay(day) + "-睡眠数据-返回:" + sleepData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void onSleepProgress(float progress) {
                    String message = "睡眠数据-读取进度:" + "progress=" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onSleepProgressDetail(String day, int packagenumber) {
                    String message = "睡眠数据-读取进度:" + "day=" + day + ",packagenumber=" + packagenumber;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadSleepComplete() {
                    String message = "睡眠数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            }, yesterday, watchDataDay);
        } else if (oprater.equals(READ_HEALTH_DRINK)) {
            VPOperateManager.getInstance().readDrinkData(writeResponse, new IDrinkDataListener() {
                @Override
                public void onDrinkDataChange(int packagenumber, DrinkData drinkdata) {
                    String message = "饮酒数据-返回:" + drinkdata.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }

                @Override
                public void onReadDrinkComplete() {
                    String message = "饮酒数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(READ_HEALTH_ORIGINAL)) {
            IOriginProgressListener originDataListener = new IOriginDataListener() {


                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "健康数据[5分钟]-读取进度:currentPackage" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgress(float progress) {

                }

                @Override
                public void onReadOriginComplete() {

                }

                @Override
                public void onOringinFiveMinuteDataChange(OriginData originData) {

                }

                @Override
                public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {

                }
            };
            IOriginProgressListener originData3Listener = new IOriginData3Listener() {
                @Override
                public void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList) {
                    String message = "健康数据-返回:" + originDataList.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourDataList) {
                    String message = "健康数据[30分钟]-返回:" + originHalfHourDataList.toString();
                    Logger.t(TAG).i(message);
                    Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的心率数据 size = " + originHalfHourDataList.getHalfHourRateDatas().size());
                    Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的血压数据 size = " + originHalfHourDataList.getHalfHourBps().size());
                    Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的运动数据 size = " + originHalfHourDataList.getHalfHourSportDatas().size());
                }

                @Override
                public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {
                    HRVOriginData hrvOriginData = originHrvDataList.get(0);
                    String rate = hrvOriginData.getRate();
                }

                @Override
                public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {

                }

                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "onReadOriginProgress 健康数据[5分钟]-读取进度:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "onReadOriginProgressDetail 健康数据[5分钟]-读取进度:currentPackage=" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                    Logger.t(TAG).i(message);
                }


                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            };
            int protype = 3;
            if (protype == 3) {
                originDataListener = originData3Listener;
            }
            VPOperateManager.getInstance().readOriginData(writeResponse, originDataListener, 3);
        } else if (oprater.equals(READ_HEALTH_ORIGINAL_FROM)) {
            int yesterday = 1;
            VPOperateManager.getInstance().readOriginDataFromDay(writeResponse, new IOriginDataListener() {
                @Override
                public void onOringinFiveMinuteDataChange(OriginData originData) {
                    String message = "健康数据[5分钟]-返回:" + originData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    String message = "健康数据[30分钟]-返回:" + originHalfHourData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "健康数据[5分钟]-读取进度:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "健康数据[5分钟]-读取进度:currentPackage=" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                    Logger.t(TAG).i(message);
                }


                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            }, yesterday, 10, watchDataDay);
        } else if (oprater.equals(READ_HEALTH_ORIGINAL_SINGLEDAY)) {
            int today = 0;
            int originProtocolVersion = 3;
            IOriginProgressListener originDataListener = new IOriginDataListener() {
                @Override
                public void onOringinFiveMinuteDataChange(OriginData originData) {
                    String message = "健康数据[5分钟]-返回:" + originData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    String message = "健康数据[30分钟]-返回:" + originHalfHourData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "健康数据[5分钟]-读取进度:" + progress;
                    Logger.t(TAG).i(message);
                }


                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "健康数据[5分钟]-读取进度:currentPackage=" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                    Logger.t(TAG).i(message);
                }


                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            };
            IOriginProgressListener originData3Listener = new IOriginData3Listener() {
                @Override
                public void onOriginFiveMinuteListDataChange(List<OriginData3> originData3List) {
                    String message = "健康数据[5分钟]-返回:" + originData3List.size();
//                    for (int i = 0; i < originData3List.size(); i++) {
//                        String s = originData3List.get(i).toString();
//                        Logger.t(TAG).i(s);
//                    }

                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    String message = "健康数据[30分钟]-返回:" + originHalfHourData.toString();
                    Logger.t(TAG).i(message);

                }

                @Override
                public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {
                    String message = "健康数据[HRV]-返回:" + originHrvDataList.size();
//                    for (int i = 0; i < originHrvDataList.size(); i++) {
//                        String s = originHrvDataList.get(i).toString();
//                        Logger.t(TAG).i(s);
//                    }
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {
                    String message = "健康数据[Spo2h]-返回:" + originSpo2hDataList.size();
                    Spo2hOriginUtil spo2hOriginUtil = new Spo2hOriginUtil(originSpo2hDataList);
                    for (int i = 0; i < originSpo2hDataList.size(); i++) {
                        String s = originSpo2hDataList.get(i).toString();
                        Logger.t(TAG).i(s);
                    }
                    Logger.t(TAG).i(message);
                    List<Map<String, Float>> tenMinuteData = spo2hOriginUtil.getTenMinuteData(ESpo2hDataType.TYPE_SPO2H_MIN);
                    Logger.t(TAG).i(message);
                }


                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "健康数据[5分钟]-读取进度:currentPackage=" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "健康数据[5分钟]-读取进度:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            };
            IOriginProgressListener originProgressListener;
            if (originProtocolVersion == 3) {
                originProgressListener = originData3Listener;
            } else {
                originProgressListener = originDataListener;
            }
            VPOperateManager.getInstance().readOriginDataSingleDay(writeResponse, originProgressListener, today, 1, watchDataDay);
        } else if (oprater.equals(READ_HEALTH)) {
            VPOperateManager.getInstance().readAllHealthData(new IAllHealthDataListener() {
                @Override
                public void onProgress(float progress) {
                    String message = "onAllProgress:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOringinFiveMinuteDataChange(OriginData originData) {
                    String message = "onOringinFiveMinuteDataChange:" + originData;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    String message = "onOringinHalfHourDataChange:" + originHalfHourData;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginComplete() {
                    String message = "onReadOriginComplete";
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onSleepDataChange(String day, SleepData sleepData) {
                    String message = getDay(day) + "-onSleepDataChange:" + sleepData;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadSleepComplete() {
                    String message = "onReadSleepComplete";
                    Logger.t(TAG).i(message);

                }
            }, watchDataDay);
        } else if (oprater.equals(OAD)) {
            if (deviceNumber < 0) {
                Toast.makeText(mContext, "请先通过密码验证，获取版本号!", Toast.LENGTH_LONG).show();
                return;
            }
            boolean isOadModel = getIntent().getBooleanExtra("isoadmodel", false);
            deviceaddress = getIntent().getStringExtra("deviceaddress");

            Intent intent = new Intent(OperaterActivity.this, OadActivity.class);
            intent.putExtra("deviceaddress", deviceaddress);
            intent.putExtra("isoadmodel", isOadModel);
            intent.putExtra("devicenumber", deviceNumber);
            intent.putExtra("deviceversion", deviceVersion);
            intent.putExtra("devicetestversion", deviceTestVersion);
            startActivity(intent);

        } else if (oprater.equals(SHOW_SP)) {
            String shareperence = VPOperateManager.getInstance().traversalShareperence();
            Logger.t(TAG).i(shareperence);
        } else if (oprater.equals(SPORT_MODE_ORIGIN_END)) {
            VPOperateManager.getInstance().stopSportModel(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "运动模式状态:" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onSportStopped() {
                    Logger.t(TAG).i(SPORT_MODE_ORIGIN_END + "================================运动结束 @_@");
                }
            });
        } else if (oprater.equals(SPORT_MODE_ORIGIN_READSTAUTS)) {
            VPOperateManager.getInstance().readSportModelState(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "运动模式状态" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onSportStopped() {
                    Logger.t(TAG).i(SPORT_MODE_ORIGIN_READSTAUTS + "================================运动结束 @_@");
                }
            });
        } else if (oprater.equals(SPORT_MODE_START_INDOOR)) {
            VPOperateManager.getInstance().startMultSportModel(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "室内步行" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onSportStopped() {
                    Logger.t(TAG).i(SPORT_MODE_START_INDOOR + "================================运动结束 @_@");
                }
            }, ESportType.INDOOR_WALK);
        } else if (oprater.equals(SPORT_MODE_ORIGIN_START)) {
            VPOperateManager.getInstance().startSportModel(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "运动模式状态" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onSportStopped() {
                    Logger.t(TAG).i(SPORT_MODE_ORIGIN_START + "================================运动结束 @_@");
                }
            });
        } else if (oprater.equals(SPORT_MODE_ORIGIN_READ)) {
            VPOperateManager.getInstance().readSportModelOrigin(writeResponse, new ISportModelOriginListener() {
                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "运动模式数据[读取进度]:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    String message = "运动模式数据[读取详情]:" + day +
                            ",allPackage=" + allPackage + ",currentPackage=" + currentPackage;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onHeadChangeListListener(SportModelOriginHeadData sportModelHeadData) {
                    String message = "运动模式数据[头部]:" + sportModelHeadData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onItemChangeListListener(List<SportModelOriginItemData> sportModelItemData) {
                    StringBuffer message = new StringBuffer();
                    message.append("运动模式数据[详细]:");
                    for (SportModelOriginItemData sportModelOriginItemData : sportModelItemData) {
                        message.append("\n");
                        message.append(sportModelOriginItemData.toString());
                    }
                    Logger.t(TAG).i(message.toString());

                }

                @Override
                public void onReadOriginComplete() {
                    String message = "运动模式数据[读取结束]";
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(HRV_ORIGIN_READ)) {
            VPOperateManager.getInstance().readHRVOrigin(writeResponse, new IHRVOriginDataListener() {
                @Override
                public void onReadOriginProgress(float progress) {
                    Logger.t(TAG).i("onReadOriginProgress=" + progress);
                }

                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    Logger.t(TAG).i("onReadOriginProgressDetail,day=" + day + ",date=" + date + ",allPackage=" + allPackage + ",currentPackage=" + currentPackage);
                }

                @Override
                public void onHRVOriginListener(HRVOriginData hrvOriginData) {
                    Logger.t(TAG).i("onHRVOriginListener=" + hrvOriginData.toString());
                }

                @Override
                public void onDayHrvScore(int day, String date, int hrvSocre) {

                }

                @Override
                public void onReadOriginComplete() {
                    Logger.t(TAG).i("onReadOriginComplete");

                }
            }, watchDataDay);
        } else if (oprater.equals(S22_READ_DATA)) {
            TimeData timeData = new TimeData(2017, 9, 11, 8, 13, 20);
//            timeData.setCurrentTime();
            Logger.t(TAG).i("timeData:" + timeData.toString());
            VPOperateManager.getInstance().readAutoDetectOriginDataFromS22(writeResponse, new IAutoDetectOriginDataListener() {

                @Override
                public void onAutoDetectOriginDataChangeListener(List<AutoDetectOriginData> autoDetectOriginDataList) {
                    for (AutoDetectOriginData autoDetectOriginData : autoDetectOriginDataList) {
                        Logger.t(TAG).i("autoDetectOriginData:" + autoDetectOriginData.toString());
                    }
                }
            }, timeData);
        } else if (oprater.equals(S22_READ_STATE)) {
            VPOperateManager.getInstance().readAutoDetectStateFromS22(writeResponse, new ICustomProtocolStateListener() {

                @Override
                public void onS22AutoDetectStateChangeListener(AutoDetectStateData autoDetectStateData) {
                    Logger.t(TAG).i("autoDetectStateData:" + autoDetectStateData.toString());
                }
            });
        } else if (oprater.equals(S22_SETTING_STATE_OPEN)) {
            AutoDetectStateSetting autoDetectStateSetting = new AutoDetectStateSetting();
            autoDetectStateSetting.setSpo2h24Hour(SUPPORT_OPEN);
            VPOperateManager.getInstance().setAutoDetectStateToS22(writeResponse, new ICustomProtocolStateListener() {
                @Override
                public void onS22AutoDetectStateChangeListener(AutoDetectStateData autoDetectStateData) {
                    Logger.t(TAG).i("autoDetectStateData:" + autoDetectStateData.toString());
                }
            }, autoDetectStateSetting);
        } else if (oprater.equals(S22_SETTING_STATE_CLOSE)) {
            AutoDetectStateSetting autoDetectStateSetting = new AutoDetectStateSetting();
            autoDetectStateSetting.setSpo2h24Hour(SUPPORT_CLOSE);
            VPOperateManager.getInstance().setAutoDetectStateToS22(writeResponse, new ICustomProtocolStateListener() {
                @Override
                public void onS22AutoDetectStateChangeListener(AutoDetectStateData autoDetectStateData) {
                    Logger.t(TAG).i("autoDetectStateData:" + autoDetectStateData.toString());
                }
            }, autoDetectStateSetting);
        } else if (oprater.equals(SPO2H_ORIGIN_READ)) {
            VPOperateManager.getInstance().readSpo2hOrigin(writeResponse, new ISpo2hOriginDataListener() {
                @Override
                public void onReadOriginProgress(float progress) {
                    Logger.t(TAG).i("onReadOriginProgress:" + progress);
                }

                @Override
                public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                    Logger.t(TAG).i("onReadOriginProgressDetail:allPackage=" + allPackage + ",currentPackage=" + currentPackage);
                }

                @Override
                public void onSpo2hOriginListener(Spo2hOriginData sportOriginData) {
                    Logger.t(TAG).i("Spo2hOriginData:" + sportOriginData.toString());
                }

                @Override
                public void onReadOriginComplete() {
                    Logger.t(TAG).i("onReadOriginComplete");
                }
            }, watchDataDay);
        } else if (oprater.equals(TEXT_ALARM_READ)) {
            VPOperateManager.getInstance().readTextAlarm(writeResponse, new ITextAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                    Logger.t(TAG).e("读闹钟数据 --》" + textAlarmData.toString());
                    EMultiAlarmOprate OPT = textAlarmData.getOprate();
                    boolean isOk = OPT == EMultiAlarmOprate.READ_SUCCESS ||
                            OPT == EMultiAlarmOprate.READ_SUCCESS_SAME_CRC ||
                            OPT == EMultiAlarmOprate.READ_SUCCESS_SAVE;
                    showToast("读闹钟数据 --》" +
                            (isOk ? ("成功,一共" + textAlarmData.getTextAlarm2SettingList().size() + "条=>" + textAlarmData.toString()) : "失败"));
                }
            });
        } else if (oprater.equals(TEXT_ALARM_ADD)) {
            TextAlarm2Setting setting = getTextAlarm2Setting();
            VPOperateManager.getInstance().addTextAlarm(writeResponse, new ITextAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                    Logger.t(TAG).e("添加闹钟 --》" + textAlarmData.toString());
                    showToast("添加闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? ("成功,一共" + textAlarmData.getTextAlarm2SettingList().size() + "条=>" + textAlarmData.toString()) : "失败"));
                }
            }, setting);
        } else if (oprater.equals(TEXT_ALARM_MODIFY)) {
            Random random = new Random();
            int flag = random.nextInt(100);
            List<TextAlarm2Setting> settings = VPOperateManager.getInstance().getTextAlarmList();
            if (settings == null || settings.size() == 0) {
                showToast("闹钟列表为空，请先添加闹钟或，读取更新闹钟列表");
                return;
            }
            final TextAlarm2Setting setting = settings.get(0);
            setting.setOpen(false);
            setting.setContent("西门官人[" + flag + "]大郎卖烧饼回来了");
            VPOperateManager.getInstance().modifyTextAlarm(writeResponse, new ITextAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                    Logger.t(TAG).e("修改闹钟 --》" + textAlarmData.toString());
                    showToast("修改闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功 : " + setting.toString() : "失败"));

                }
            }, setting);
        } else if (oprater.equals(TEXT_ALARM_DELETE)) {
            List<TextAlarm2Setting> settings = TextAlarmSp.getInstance(mContext).getTextAlarmSetting(deviceaddress);
            if (settings != null && settings.size() > 0) {
                final TextAlarm2Setting setting = settings.get(0);
                for (TextAlarm2Setting s : settings) {
                    Logger.t(TAG).e("存在的文字闹钟：" + s.toString());
                }
                Logger.t(TAG).e("要删除的文字闹钟：" + setting.toString());
                VPOperateManager.getInstance().deleteTextAlarm(writeResponse, new ITextAlarmDataListener() {
                    @Override
                    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
                        Logger.t(TAG).e("删除闹钟 --》" + textAlarmData.toString());
                        showToast("删除闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.CLEAR_SUCCESS ? "成功:" + setting.toString() : "失败"));
                    }
                }, setting);
            } else {
                Logger.t(TAG).e("暂无闹钟可以删除");
                showToast("暂无闹钟可以删除");
            }
        } else if (oprater.equals(TEXT_ALARM)) {
            startActivity(new Intent(this, TextAlarmActivity.class));
        } else if (oprater.equals(ORIGIN_LOG)) {
            startActivity(new Intent(this, OriginalDataLogActivity.class));
        } else if (oprater.equals(G15_QR_CODE)) {
            startActivity(new Intent(this, G15QRCodeActivity.class));
        } else if (oprater.equals(RR)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readRR(DayState.TODAY);
                }
            }).start();
        } else if (oprater.equals(ECG_AUTO_REPORT_TEXT)) {
            VPOperateManager.getInstance().setECGAutoReportListener(new IECGAutoReportListener() {
                @Override
                public void onECGAutoReport(int ecgValue, TimeData date) {
                    String info = "ECG = " + ecgValue + ", date = " + date.getDateAndClockForSleepSecond();
                    Logger.t(TAG).e(info);
                    Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onECGDataReport(int[] ints) {
                    Logger.t(TAG).e("---onECGDataReport => " + Arrays.toString(ints));
                }
            });
        } else if (oprater.equals(START_BLOOD_GLUCOSE)) {
            VPOperateManager.getInstance().startBloodGlucoseDetect(writeResponse, new AbsBloodGlucoseChangeListener() {
                @Override
                public void onDetectError(int opt, EBloodGlucoseStatus status) {
                    showToast("[onDetectError: opt = " + opt + ", status=" + status + "]");
                }

                @Override
                public void onBloodGlucoseDetect(int progress, float bloodGlucose) {
                    showToast("[progress:" + progress + " bloodGlucose: " + bloodGlucose + "]");
                }

                @Override
                public void onBloodGlucoseStopDetect() {
                    showToast("Stop Blood Glucose Detect");
                }

            });
        } else if (oprater.equals(STOP_BLOOD_GLUCOSE)) {
            VPOperateManager.getInstance().stopBloodGlucoseDetect(writeResponse, new AbsBloodGlucoseChangeListener() {
                @Override
                public void onDetectError(int opt, EBloodGlucoseStatus status) {
                    showToast("[onDetectError: opt = " + opt + ", status=" + status + "]");
                }

                @Override
                public void onBloodGlucoseDetect(int progress, float bloodGlucose) {
                    showToast("[progress:" + progress + " bloodGlucose: " + bloodGlucose + "]");
                }

                @Override
                public void onBloodGlucoseStopDetect() {
                    showToast("Stop Blood Glucose Detect");
                }

            });
        } else if (oprater.equals(BLOOD_GLUCOSE_P_READ)) {
            VPOperateManager.getInstance().readBloodGlucoseAdjustingData(writeResponse, new AbsBloodGlucoseChangeListener() {

                @Override
                public void onBloodGlucoseAdjustingReadSuccess(boolean isOpen, float adjustingValue) {
                    showToast("血糖私人模式读取成功：isOpen " + isOpen + " value = " + adjustingValue);
                }

                @Override
                public void onBloodGlucoseAdjustingReadFailed() {
                    showToast("血糖私人模式读取失败");
                }
            });
        } else if (oprater.equals(BLOOD_GLUCOSE_P_SETTING)) {
            VPOperateManager.getInstance().setBloodGlucoseAdjustingData(6.78f, true, writeResponse, new AbsBloodGlucoseChangeListener() {

                @Override
                public void onBloodGlucoseAdjustingSettingSuccess(boolean isOpen, float adjustingValue) {
                    showToast("血糖私人模式设置成功：isOpen " + isOpen + " value = " + adjustingValue);
                }

                @Override
                public void onBloodGlucoseAdjustingSettingFailed() {
                    showToast("血糖私人模式设置失败");
                }
            });
        } else if (oprater.equals(BLE_RENAME)) {
            VPOperateManager.getInstance().bleDeviceRename("abcdefghijk", new IDeviceRenameListener() {
                @Override
                public void onDeviceRenameSuccess(@NotNull String s) {
                    showToast("rename success " + s);
                }

                @Override
                public void onDeviceRenameFail(ERenameError error, @NotNull String s) {
                    showToast(error.getDes() + " s = " + s);
                }
            }, writeResponse);
        } else if (oprater.equals(BT_RENAME)) {
            VPOperateManager.getInstance().bleDeviceRename("YWX", new IDeviceRenameListener() {
                @Override
                public void onDeviceRenameSuccess(@NotNull String s) {
                    showToast("rename success " + s);
                }

                @Override
                public void onDeviceRenameFail(ERenameError error, @NotNull String s) {
                    showToast(error.getDes() + " s = " + s);
                }
            }, writeResponse);
        } else if (oprater.equals(BT_CONNECT)) {

        } else if (oprater.equals(BT_CLOSE)) {

        } else if (oprater.equals(HEALTH_REMIND)) {
            startActivity(new Intent(this, HealthRemindActivity.class));
        } else if (oprater.equals(FIND_DEVICE)) {
            VPOperateManager.getInstance().startFindDeviceByPhone(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {

                }
            }, new IFindDevicelistener() {
                @Override
                public void unSupportFindDeviceByPhone() {
                    Logger.t(TAG).e("unSupportFindDeviceByPhone--->");
                }

                @Override
                public void findedDevice() {
                    Logger.t(TAG).e("findedDevice--->");
                }

                @Override
                public void unFindDevice() {
                    Logger.t(TAG).e("unFindDevice--->");
                }

                @Override
                public void findingDevice() {
                    Logger.t(TAG).e("findingDevice--->");
                }
            });
        } else if (oprater.equals(JL_DEVICE)) {
            if (VPOperateManager.getInstance().isJLDevice()) {
                startActivity(new Intent(this, JLDeviceOPTActivity.class));
            } else {
                ToastUtil.show("当前设备非杰理芯片");
            }
        } else if (oprater.equals(CONTACT)) {
            boolean isHaveContactFunction = VpSpGetUtil.getVpSpVariInstance(this).isSupportContactFunction();
            if (isHaveContactFunction) {
                startActivity(new Intent(OperaterActivity.this, ContactActivity.class));
            } else {
                ToastUtil.show("当前设备无联系人功能");
            }
        } else if (oprater.equals(CY_FIND_PHONE)) {
            VPOperateManager.getInstance().settingFindPhoneListener(new IFindPhonelistener() {
                @Override
                public void findPhone() {

                }

                @Override
                public void deviceFindingCYPhone() {
                    //传音KH70定制需求，设备端进入查找手机模式，只需一次上报;
                }
            });
            //传音KH70定制需求，APP控制设备退出查找手机模式；
            VPOperateManager.getInstance().stopFindCYPhoneByDevice(writeResponse);
        } else if (oprater.equals(GATT_CLOSE)) {
            BluetoothGatt gatt = VPOperateManager.getInstance().getCurrentConnectGatt();
            if (gatt != null) {
                Logger.t(TAG).i("Gatt-Close:" + gatt.getDevice().getName() + " | " + gatt.getDevice().getAddress());
                gatt.disconnect();
                gatt.close();
                gatt = null;
            } else {
                Logger.t(TAG).i("Gatt-Close: Gatt is NULL");
            }

        }

    }

    private void readRR(final DayState dayState) {
        Logger.t(TAG).e("读取RR逐跳帧数据 --》 Day = " + dayState);
        VPOperateManager.getInstance().readRRIntervalByDay(writeResponse, new IRRIntervalProgressListener() {

            @Override
            public void onReadRRIntervalProgressChanged(float progress, RRIntervalData rrIntervalData) {
                Logger.t(TAG).e("onReadRRIntervalProgressChanged --》 " + dayState + ": progress = " + progress + " || -> " + rrIntervalData.toString());
            }

            @Override
            public void onReadRRIntervalComplete(DayState dayState, List<RRIntervalData> rrIntervalData) {
                Logger.t(TAG).e("onReadRRIntervalComplete --》 dayState = " + dayState + " || -> rrIntervalData size = " + rrIntervalData.size());
//                for (RRIntervalData data : rrIntervalData) {
//                    Logger.t(TAG).e("onReadRRIntervalComplete --》 data = " + data.toString());
//                }
                if (dayState == DayState.TODAY) {
                    SystemClock.sleep(50);
                    readRR(DayState.YESTERDAY);
                } else if (dayState == DayState.YESTERDAY) {
                    Logger.t(TAG).e("onReadRRIntervalComplete --》 2天数据已全部读取结束 ");
                    SystemClock.sleep(50);
                    readRR(DayState.BEFORE_YESTERDAY);
                } else if (dayState == DayState.BEFORE_YESTERDAY) {
                    Logger.t(TAG).e("onReadRRIntervalComplete --》 三天数据已全部读取结束 ");
                }

            }
        }, dayState, 0);
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    private TextAlarm2Setting getTextAlarm2Setting() {
        TextAlarm2Setting setting = new TextAlarm2Setting();
        setting.setOpen(true);
        setting.setRepeatStatus("1000010");
        setting.setUnRepeatDate("0000-00-00");
        setting.setAlarmHour(16);
        setting.setAlarmMinute(1);
        setting.setContent("^_^大郎，该吃药了！");
        return setting;
    }

    @NonNull
    private Alarm2Setting getMultiAlarmSetting() {
        int hour = 16;
        int minute = 33;
        int scene = 1;
        boolean isOpen = true;
        String repestStr = "1000010";
        String unRepeatDdate = "0000-00-00";
        return new Alarm2Setting(hour, minute, repestStr, scene, unRepeatDdate, isOpen);
    }


    private void setWeatherData1() {
        //CRC
        int crc = 0;
        //城市名称
        String cityName = "深圳";
        //数据来源
        int sourcr = 0;
        //最近更新时间
        int year = TimeData.getSysYear();
        int month = TimeData.getSysMonth();
        int day = TimeData.getSysDay();
        TimeData lasTimeUpdate = new TimeData(year, month, day, 6, 59, 23);
        //天气列表（以小时为单位）
        List<WeatherEvery3Hour> weatherEvery3HourList = new ArrayList<>();
        TimeData every3Hour0 = new TimeData(year, month, day, 6, 59, 23);
        TimeData every3Hour1 = new TimeData(year, month, day, 9, 59, 23);
        TimeData every3Hour2 = new TimeData(year, month, day, 12, 59, 23);
        TimeData every3Hour3 = new TimeData(year, month, day, 15, 59, 23);
        TimeData every3Hour4 = new TimeData(year, month, day, 18, 59, 23);
        TimeData every3Hour5 = new TimeData(year, month, day, 21, 59, 23);

        TimeData every3Hour01 = new TimeData(year, month, day + 1, 6, 59, 23);
        TimeData every3Hour11 = new TimeData(year, month, day + 1, 9, 59, 23);
        TimeData every3Hour21 = new TimeData(year, month, day + 1, 12, 59, 23);
        TimeData every3Hour31 = new TimeData(year, month, day + 1, 15, 59, 23);
        TimeData every3Hour41 = new TimeData(year, month, day + 1, 18, 59, 23);
        TimeData every3Hour51 = new TimeData(year, month, day + 1, 21, 59, 23);

        TimeData every3Hour02 = new TimeData(year, month, day + 2, 6, 59, 23);
        TimeData every3Hour12 = new TimeData(year, month, day + 2, 9, 59, 23);
        TimeData every3Hour22 = new TimeData(year, month, day + 2, 12, 59, 23);
        TimeData every3Hour32 = new TimeData(year, month, day + 2, 15, 59, 23);
        TimeData every3Hour42 = new TimeData(year, month, day + 2, 18, 59, 23);
        TimeData every3Hour52 = new TimeData(year, month, day + 2, 21, 59, 23);
        /**
         * 天气状态
         * 0-4	晴
         * 5-12	晴转多云
         * 13-16	阴天
         * 17-20	阵雨
         * 21-24	雷阵雨
         * 25-32	冰雹
         * 33-40	小雨
         * 41-48	中雨
         * 49-56	大雨
         * 57-72	暴雨
         * 73-84	小雪
         * 85-100	大雪
         * 101-155	多云
         */
        WeatherEvery3Hour weatherEvery3Hour0 =
                new WeatherEvery3Hour(every3Hour0, 60, 29, 6, 3, "3-4", 15.0);
        WeatherEvery3Hour weatherEvery3Hour1 =
                new WeatherEvery3Hour(every3Hour1, 70, 30, 7, 27, "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour2 =
                new WeatherEvery3Hour(every3Hour2, 80, 38, 8, 22, "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour3 =
                new WeatherEvery3Hour(every3Hour3, 90, 39, 9, 33, "15", 6.0);
        WeatherEvery3Hour weatherEvery3Hour4 =
                new WeatherEvery3Hour(every3Hour4, 90, 32, 2, 22, "3", 8.0);
        WeatherEvery3Hour weatherEvery3Hour5 =
                new WeatherEvery3Hour(every3Hour5, 90, 7, 4, 88, "11", 1.0);
        weatherEvery3HourList.add(weatherEvery3Hour0);
        weatherEvery3HourList.add(weatherEvery3Hour1);
        weatherEvery3HourList.add(weatherEvery3Hour2);
        weatherEvery3HourList.add(weatherEvery3Hour3);
        weatherEvery3HourList.add(weatherEvery3Hour4);
        weatherEvery3HourList.add(weatherEvery3Hour5);

        WeatherEvery3Hour weatherEvery3Hour01 =
                new WeatherEvery3Hour(every3Hour01, 60, 12, 6, 31, "3-4", 15.0);
        WeatherEvery3Hour weatherEvery3Hour11 =
                new WeatherEvery3Hour(every3Hour11, 70, 23, 7, 47, "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour21 =
                new WeatherEvery3Hour(every3Hour21, 80, 25, 8, 52, "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour31 =
                new WeatherEvery3Hour(every3Hour31, 90, 18, 9, 83, "15", 6.0);
        WeatherEvery3Hour weatherEvery3Hour41 =
                new WeatherEvery3Hour(every3Hour41, 90, 22, 2, 62, "3", 8.0);
        WeatherEvery3Hour weatherEvery3Hour51 =
                new WeatherEvery3Hour(every3Hour51, 90, 9, 4, 118, "11", 1.0);
        weatherEvery3HourList.add(weatherEvery3Hour01);
        weatherEvery3HourList.add(weatherEvery3Hour11);
        weatherEvery3HourList.add(weatherEvery3Hour21);
        weatherEvery3HourList.add(weatherEvery3Hour31);
        weatherEvery3HourList.add(weatherEvery3Hour41);
        weatherEvery3HourList.add(weatherEvery3Hour51);

        WeatherEvery3Hour weatherEvery3Hour02 =
                new WeatherEvery3Hour(every3Hour02, 50, 42, 6, 11, "3-4", 15.0);
        WeatherEvery3Hour weatherEvery3Hour12 =
                new WeatherEvery3Hour(every3Hour12, 40, 53, 7, 32, "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour22 =
                new WeatherEvery3Hour(every3Hour22, 30, 35, 8, 72, "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour32 =
                new WeatherEvery3Hour(every3Hour32, 20, 38, 9, 63, "15", 6.0);
        WeatherEvery3Hour weatherEvery3Hour42 =
                new WeatherEvery3Hour(every3Hour42, 60, 32, 2, 22, "3", 8.0);
        WeatherEvery3Hour weatherEvery3Hour52 =
                new WeatherEvery3Hour(every3Hour52, 70, 29, 4, 88, "11", 1.0);
        weatherEvery3HourList.add(weatherEvery3Hour02);
        weatherEvery3HourList.add(weatherEvery3Hour12);
        weatherEvery3HourList.add(weatherEvery3Hour22);
        weatherEvery3HourList.add(weatherEvery3Hour32);
        weatherEvery3HourList.add(weatherEvery3Hour42);
        weatherEvery3HourList.add(weatherEvery3Hour52);

        //天气列表（以天为单位）
        List<WeatherEveryDay> weatherEveryDayList = new ArrayList<>();
        TimeData everyDay0 = new TimeData(year, month, day, 12, 59, 23);
        TimeData everyDay1 = new TimeData(year, month, day + 1, 12, 59, 23);
        TimeData everyDay2 = new TimeData(year, month, day + 2, 12, 59, 23);
        WeatherEveryDay weatherEveryDay0 = new WeatherEveryDay(everyDay0, 80, -80, 34,
                27, 10, 38, 10, "10-12", 5.2);

        WeatherEveryDay weatherEveryDay1 = new WeatherEveryDay(everyDay1, 80, -80, 23,
                9, 10, 68, 22, "10-12", 5.2);

        WeatherEveryDay weatherEveryDay2 = new WeatherEveryDay(everyDay2, 80, -80, 53,
                29, 10, 88, 63, "10-12", 5.2);
        weatherEveryDayList.add(weatherEveryDay0);
        weatherEveryDayList.add(weatherEveryDay1);
        weatherEveryDayList.add(weatherEveryDay2);
        WeatherData weatherData = new WeatherData(crc, cityName, sourcr, lasTimeUpdate, weatherEvery3HourList, weatherEveryDayList);
        VPOperateManager.getInstance().settingWeatherData(writeResponse, weatherData, new IWeatherStatusDataListener() {
            @Override
            public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
                String message = "settingWeatherData onWeatherDataChange read:\n" + weatherStatusData.toString();
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }
        });
    }

    private int TR() {
        Random random = new Random();
        return random.nextInt(60);
    }

    private int WR() {
        Random random = new Random();
        return random.nextInt(155);
    }

    int count = 0;

    private void setWeatherData11() {
        //CRC
        int crc = 0;
        //城市名称
        String cityName = "南山";
        //数据来源
        int sourcr = 0;
        //最近更新时间
        int year = TimeData.getSysYear();
        int month = TimeData.getSysMonth();
        int day = TimeData.getSysDay();
        TimeData lasTimeUpdate = new TimeData(year, month, day, 6, 59, 23);
        //天气列表（以小时为单位）
        List<WeatherEvery3Hour> weatherEvery3HourList = new ArrayList<>();
        TimeData every3Hour0 = new TimeData(year, month, day, 6, 59, 23);
        TimeData every3Hour1 = new TimeData(year, month, day, 9, 59, 23);
        TimeData every3Hour2 = new TimeData(year, month, day, 12, 59, 23);
        TimeData every3Hour3 = new TimeData(year, month, day, 15, 59, 23);
        TimeData every3Hour4 = new TimeData(year, month, day, 18, 59, 23);
        TimeData every3Hour5 = new TimeData(year, month, day, 21, 59, 23);
        TimeData every3Hour6 = new TimeData(year, month, day, 24, 0, 0);

        TimeData every3Hour01 = new TimeData(year, month, day + 1, 6, 59, 23);
        TimeData every3Hour11 = new TimeData(year, month, day + 1, 9, 59, 23);
        TimeData every3Hour21 = new TimeData(year, month, day + 1, 12, 59, 23);
        TimeData every3Hour31 = new TimeData(year, month, day + 1, 15, 59, 23);
        TimeData every3Hour41 = new TimeData(year, month, day + 1, 18, 59, 23);
        TimeData every3Hour51 = new TimeData(year, month, day + 1, 21, 59, 23);
        TimeData every3Hour61 = new TimeData(year, month, day + 1, 24, 0, 0);

        TimeData every3Hour02 = new TimeData(year, month, day + 2, 6, 59, 23);
        TimeData every3Hour12 = new TimeData(year, month, day + 2, 9, 59, 23);
        TimeData every3Hour22 = new TimeData(year, month, day + 2, 12, 59, 23);
        TimeData every3Hour32 = new TimeData(year, month, day + 2, 15, 59, 23);
        TimeData every3Hour42 = new TimeData(year, month, day + 2, 18, 59, 23);
        TimeData every3Hour52 = new TimeData(year, month, day + 2, 21, 59, 23);
        TimeData every3Hour62 = new TimeData(year, month, day + 2, 24, 0, 0);
        /**
         * 天气状态
         * 0-4	晴
         * 5-12	晴转多云
         * 13-16	阴天
         * 17-20	阵雨
         * 21-24	雷阵雨
         * 25-32	冰雹
         * 33-40	小雨
         * 41-48	中雨
         * 49-56	大雨
         * 57-72	暴雨
         * 73-84	小雪
         * 85-100	大雪
         * 101-155	多云
         */
        WeatherEvery3Hour weatherEvery3Hour0 =
                new WeatherEvery3Hour(every3Hour0, 60, TR(), 6, WR(), "3-4", 15.0);
        WeatherEvery3Hour weatherEvery3Hour1 =
                new WeatherEvery3Hour(every3Hour1, 70, TR(), 7, WR(), "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour2 =
                new WeatherEvery3Hour(every3Hour2, 80, TR(), 8, WR(), "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour3 =
                new WeatherEvery3Hour(every3Hour3, 90, TR(), 9, WR(), "15", 6.0);
        WeatherEvery3Hour weatherEvery3Hour4 =
                new WeatherEvery3Hour(every3Hour4, 90, TR(), 2, WR(), "3", 8.0);
        WeatherEvery3Hour weatherEvery3Hour5 =
                new WeatherEvery3Hour(every3Hour5, 90, TR(), 4, WR(), "11", 1.0);
        WeatherEvery3Hour weatherEvery3Hour6 =
                new WeatherEvery3Hour(every3Hour6, 90, TR(), 4, WR(), "11", 1.0);
        weatherEvery3HourList.add(weatherEvery3Hour0);
        weatherEvery3HourList.add(weatherEvery3Hour1);
        weatherEvery3HourList.add(weatherEvery3Hour2);
        weatherEvery3HourList.add(weatherEvery3Hour3);
        weatherEvery3HourList.add(weatherEvery3Hour4);
        weatherEvery3HourList.add(weatherEvery3Hour5);
        weatherEvery3HourList.add(weatherEvery3Hour6);

        WeatherEvery3Hour weatherEvery3Hour01 =
                new WeatherEvery3Hour(every3Hour01, 60, TR(), 6, WR(), "3-4", 15.0);
        WeatherEvery3Hour weatherEvery3Hour11 =
                new WeatherEvery3Hour(every3Hour11, 70, TR(), 7, WR(), "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour21 =
                new WeatherEvery3Hour(every3Hour21, 80, TR(), 8, WR(), "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour31 =
                new WeatherEvery3Hour(every3Hour31, 90, TR(), 9, WR(), "15", 6.0);
        WeatherEvery3Hour weatherEvery3Hour41 =
                new WeatherEvery3Hour(every3Hour41, 90, TR(), 2, WR(), "3", 8.0);
        WeatherEvery3Hour weatherEvery3Hour51 =
                new WeatherEvery3Hour(every3Hour51, 90, TR(), 4, WR(), "11", 1.0);
        WeatherEvery3Hour weatherEvery3Hour61 =
                new WeatherEvery3Hour(every3Hour61, 90, TR(), 4, WR(), "11", 1.0);
        weatherEvery3HourList.add(weatherEvery3Hour01);
        weatherEvery3HourList.add(weatherEvery3Hour11);
        weatherEvery3HourList.add(weatherEvery3Hour21);
        weatherEvery3HourList.add(weatherEvery3Hour31);
        weatherEvery3HourList.add(weatherEvery3Hour41);
        weatherEvery3HourList.add(weatherEvery3Hour51);
        weatherEvery3HourList.add(weatherEvery3Hour61);

        WeatherEvery3Hour weatherEvery3Hour02 =
                new WeatherEvery3Hour(every3Hour02, 50, TR(), 6, WR(), "3-4", 15.0);
        WeatherEvery3Hour weatherEvery3Hour12 =
                new WeatherEvery3Hour(every3Hour12, 40, TR(), 7, WR(), "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour22 =
                new WeatherEvery3Hour(every3Hour22, 30, TR(), 8, WR(), "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour32 =
                new WeatherEvery3Hour(every3Hour32, 20, TR(), 9, WR(), "15", 6.0);
        WeatherEvery3Hour weatherEvery3Hour42 =
                new WeatherEvery3Hour(every3Hour42, 60, TR(), 2, WR(), "3", 8.0);
        WeatherEvery3Hour weatherEvery3Hour52 =
                new WeatherEvery3Hour(every3Hour52, 70, TR(), 4, WR(), "11", 1.0);
        WeatherEvery3Hour weatherEvery3Hour62 =
                new WeatherEvery3Hour(every3Hour62, 70, TR(), 4, WR(), "11", 1.0);
        weatherEvery3HourList.add(weatherEvery3Hour02);
        weatherEvery3HourList.add(weatherEvery3Hour12);
        weatherEvery3HourList.add(weatherEvery3Hour22);
        weatherEvery3HourList.add(weatherEvery3Hour32);
        weatherEvery3HourList.add(weatherEvery3Hour42);
        weatherEvery3HourList.add(weatherEvery3Hour52);
        weatherEvery3HourList.add(weatherEvery3Hour62);

        //天气列表（以天为单位）
        List<WeatherEveryDay> weatherEveryDayList = new ArrayList<>();
        int t1 = TR();
        int t2 = TR();
        int t3 = TR();
        TimeData everyDay0 = new TimeData(year, month, day, 12, 59, 23);
        TimeData everyDay1 = new TimeData(year, month, day + 1, 12, 59, 23);
        TimeData everyDay2 = new TimeData(year, month, day + 2, 12, 59, 23);
        WeatherEveryDay weatherEveryDay0 = new WeatherEveryDay(everyDay0, 80, -80, t1 + 10,
                t1, 10, 38, 10, "10-12", 5.2);

        WeatherEveryDay weatherEveryDay1 = new WeatherEveryDay(everyDay1, 80, -80, t2 + 10,
                t2, 10, WR(), WR(), "10-12", 5.2);

        WeatherEveryDay weatherEveryDay2 = new WeatherEveryDay(everyDay2, 80, -80, t3 + 10,
                t3, 10, WR(), WR(), "10-12", 5.2);
        weatherEveryDayList.add(weatherEveryDay0);
        weatherEveryDayList.add(weatherEveryDay1);
        weatherEveryDayList.add(weatherEveryDay2);
        WeatherData weatherData = new WeatherData(crc, cityName, sourcr, lasTimeUpdate, weatherEvery3HourList, weatherEveryDayList);
        VPOperateManager.getInstance().settingWeatherData(writeResponse, weatherData, new IWeatherStatusDataListener() {
            @Override
            public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
                String message = "settingWeatherData onWeatherDataChange read:\n" + weatherStatusData.toString();
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }
        });
    }


    private void setWeatherData2() {
        List<WeatherData2> weatherData2 = new ArrayList<>();
        WeatherData2 e = new WeatherData2(new TimeData(2020, 11, 16, 10, 0), 18, 28, 1, 1, 1);
        weatherData2.add(e);
        VPOperateManager.getInstance().settingWeatherData2(writeResponse, weatherData2, new IWeatherStatusDataListener() {
            @Override
            public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
                String message = "settingWeatherData onWeatherDataChange read:\n" + weatherStatusData.toString();
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }
        });
    }

    private void sendMsg(String message, int what) {
        msg = Message.obtain();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }

    /**
     * 写入的状态返回
     */
    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("write cmd status:" + code);

        }
    }

    /**
     * 密码验证之前，要调用这个方法
     * 因为在密码验证之后，inPttModel/outPttModel其中一个会有回调
     */
    public void listenDeviceCallbackData() {
        VPOperateManager.getInstance().settingDeviceControlPhone(new IDeviceControlPhoneModelState() {

            @Override
            public void inPttModel() {
                String message = "手表提示:手表进入ptt模式\n";
                isInPttModel = true;
                Logger.t(TAG).i(message);
            }

            @Override
            public void outPttModel() {
                isInPttModel = false;
                String message = "手表提示:手表退出ptt模式\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void rejectPhone() {
                String message = "手表提示:请挂断来电\n";
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }

            @Override
            public void cliencePhone() {
                String message = "手表提示:请来电静音\n";
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }

            @Override
            public void appAnswerCall() {
                String message = "手表提示:app处理接听来电\n";
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }

            @Override
            public void knocknotify(int type) {
                String message = "手表提示:敲击提醒，1表示单击，2表示双击\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void sos() {
                String message = "手表提示:sos\n";
                Logger.t(TAG).i(message);
            }

            public void nextMusic() {
                String message = "手表提示:下一曲\n";
                Logger.t(TAG).i(message);
            }

            public void previousMusic() {
                String message = "手表提示:上一曲\n";
                Logger.t(TAG).i(message);
            }

            public void pauseAndPlayMusic() {
                String message = "手表提示:暂停和播放\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void pauseMusic() {
                String message = "手表提示:暂停\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void playMusic() {
                String message = "手表提示:播放\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void voiceUp() {
                String message = "手表提示:调高音量\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void voiceDown() {
                String message = "手表提示:调低音量\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void oprateMusicSuccess() {
                String message = "手表提示:音乐相关的操作成功了\n";
                Logger.t(TAG).i(message);
            }

            @Override
            public void oprateMusicFail() {
                String message = "手表提示:音乐相关的操作失败了\n";
                Logger.t(TAG).i(message);
            }

        });
    }

    public void listenCamera() {
        VPOperateManager.getInstance().setCameraListener(new ICameraDataListener() {
            @Override
            public void OnCameraDataChange(ECameraStatus oprateStauts) {
                Logger.t(TAG).i("Camera oprateStauts:" + oprateStauts);
            }
        });
    }

    public void startListenADC() {
        Logger.t(TAG).i("开始监听光电信号");
        byte[] cmd = new byte[20];
        cmd[0] = (byte) 0xf3;
        cmd[1] = (byte) 0x08;
        VPOperateManager.getInstance().sendOrder(writeResponse, cmd);
        VPOperateManager.getInstance().startDetectSPO2H(writeResponse, new ISpo2hDataListener() {
            @Override
            public void onSpO2HADataChange(Spo2hData spo2HData) {
                //不用理会
            }
        }, new ILightDataCallBack() {
            @Override
            public void onGreenLightDataChange(int[] data) {
                String message = "返回-光电信号:\n" + Arrays.toString(data);
                Logger.t(TAG).i(message);
            }
        });
        VPOperateManager.getInstance().stopDetectSPO2H(writeResponse, new ISpo2hDataListener() {
            @Override
            public void onSpO2HADataChange(Spo2hData spo2HData) {
                //不用理会
            }
        });

        VPOperateManager.getInstance().startDetectHeart(writeResponse, new IHeartDataListener() {
            @Override
            public void onDataChange(HeartData heartData) {
                String message = "返回-心率值:" + heartData.toString();
                Logger.t(TAG).i(message);
            }
        });
    }

    public void stopListenADC() {
        Logger.t(TAG).i("停止监听光电信号");
        VPOperateManager.getInstance().stopDetectHeart(writeResponse);
    }

    private String getDay(String day) {
        if (day.equals("0")) {
            return "今天";
        } else if (day.equals("1")) {
            return "昨天";
        } else {
            return "前天";
        }
    }

    private void readOriginData() {
        IOriginProgressListener originDataListener = new IOriginDataListener() {


            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                String message = "健康数据[5分钟]-读取进度:currentPackage" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                Logger.t(TAG).i(message);
            }

            @Override
            public void onReadOriginProgress(float progress) {

            }

            @Override
            public void onReadOriginComplete() {

            }

            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {

            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {

            }
        };
        IOriginProgressListener originData3Listener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList) {
                String message = "健康数据-返回:" + originDataList.toString();
                Logger.t(TAG).i(message);
            }

            @Override
            public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourDataList) {
                String message = "健康数据[30分钟]-返回:" + originHalfHourDataList.toString();
                Logger.t(TAG).i(message);
                Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的心率数据 size = " + originHalfHourDataList.getHalfHourRateDatas().size());
                Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的血压数据 size = " + originHalfHourDataList.getHalfHourBps().size());
                Logger.t(TAG).i("健康数据[30分钟]-返回:30分钟的运动数据 size = " + originHalfHourDataList.getHalfHourSportDatas().size());
            }

            @Override
            public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {
                HRVOriginData hrvOriginData = originHrvDataList.get(0);
                String rate = hrvOriginData.getRate();
            }

            @Override
            public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {

            }

            @Override
            public void onReadOriginProgress(float progress) {
                String message = "onReadOriginProgress 健康数据[5分钟]-读取进度:" + progress;
                Logger.t(TAG).i(message);
            }

            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                String message = "onReadOriginProgressDetail 健康数据[5分钟]-读取进度:currentPackage=" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                Logger.t(TAG).i(message);
            }


            @Override
            public void onReadOriginComplete() {
                String message = "健康数据-读取结束";
                Logger.t(TAG).i(message);
            }
        };
        int protype = 3;
        if (protype == 3) {
            originDataListener = originData3Listener;
        }
        VPOperateManager.getInstance().readOriginData(writeResponse, originDataListener, 3);
    }

    @Override
    protected void onDestroy() {
        VPOperateManager.getMangerInstance(this).disconnectWatch(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        });
        super.onDestroy();
    }

    private void controlVolume() {
        Random random = new Random();
        int volume = random.nextInt(100);
        Toast.makeText(mContext, "设置音量：" + volume, Toast.LENGTH_SHORT).show();
        VPOperateManager.getInstance().settingVolume(volume, writeResponse, new IMusicControlListener() {
            @Override
            public void oprateMusicSuccess() {
                Logger.t(TAG).i("Music-oprateMusicSuccess");
            }

            @Override
            public void oprateMusicFail() {
                Logger.t(TAG).i("Music-oprateMusicFail");
            }

            @Override
            public void nextMusic() {
                Logger.t(TAG).i("Music-nextMusic");
            }

            @Override
            public void previousMusic() {
                Logger.t(TAG).i("Music-previousMusic");
            }

            @Override
            public void pauseAndPlayMusic() {
                Logger.t(TAG).i("Music-pauseAndPlayMusic");
            }

            @Override
            public void pauseMusic() {
                Logger.t(TAG).i("Music-pauseMusic");
            }

            @Override
            public void playMusic() {
                Logger.t(TAG).i("Music-playMusic");
            }

            @Override
            public void voiceUp() {
                Logger.t(TAG).i("Music-voiceUp");
            }

            @Override
            public void voiceDown() {
                Logger.t(TAG).i("Music-voiceDown");
            }
        });
    }

    private void controlMusic(boolean isPlay) {
        int play = 1;//播放状态
        int pause = 2;//暂停状态
        MusicData musicData = new MusicData("周杰伦", "上海一九四三", "范特西", 80, isPlay ? play : pause);
        Logger.t(TAG).i("settingMusicData");
        VPOperateManager.getInstance().settingMusicData(writeResponse, musicData, new IMusicControlListener() {
            @Override
            public void oprateMusicSuccess() {
                Logger.t(TAG).i("Music-oprateMusicSuccess");
            }

            @Override
            public void oprateMusicFail() {
                Logger.t(TAG).i("Music-oprateMusicFail");
            }

            @Override
            public void nextMusic() {
                Logger.t(TAG).i("Music-nextMusic");
            }

            @Override
            public void previousMusic() {
                Logger.t(TAG).i("Music-previousMusic");
            }

            @Override
            public void pauseAndPlayMusic() {
                Logger.t(TAG).i("Music-pauseAndPlayMusic");
            }

            @Override
            public void pauseMusic() {
                Logger.t(TAG).i("Music-pauseMusic");
            }

            @Override
            public void playMusic() {
                Logger.t(TAG).i("Music-playMusic");
            }

            @Override
            public void voiceUp() {
                Logger.t(TAG).i("Music-voiceUp");
            }

            @Override
            public void voiceDown() {
                Logger.t(TAG).i("Music-voiceDown");
            }


        });
    }
}
