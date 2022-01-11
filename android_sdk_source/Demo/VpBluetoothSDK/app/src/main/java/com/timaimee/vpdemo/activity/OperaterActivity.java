package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.veepoo.protocol.listener.data.IDeviceControlPhoneModelState;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IDrinkDataListener;
import com.veepoo.protocol.listener.data.IFatigueDataListener;
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener;
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
import com.veepoo.protocol.model.datas.AlarmData;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.datas.AllSetData;
import com.veepoo.protocol.model.datas.AutoDetectOriginData;
import com.veepoo.protocol.model.datas.AutoDetectStateData;
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
import com.veepoo.protocol.model.enums.ECameraStatus;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EMultiAlarmOprate;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;
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
            VPOperateManager.getMangerInstance(mContext).startDetectHeart(writeResponse, new IHeartDataListener() {

                @Override
                public void onDataChange(HeartData heart) {
                    String message = "heart:\n" + heart.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(TEMPTURE_DETECT_START)) {
            VPOperateManager.getMangerInstance(mContext).startDetectTempture(writeResponse, new ITemptureDetectDataListener() {
                @Override
                public void onDataChange(TemptureDetectData temptureDetectData) {
                    String message = "startDetectTempture temptureDetectData:\n" + temptureDetectData.toString();
                    Logger.t(TAG).i(message);
//                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(TEMPTURE_DETECT_STOP)) {
            VPOperateManager.getMangerInstance(mContext).stopDetectTempture(writeResponse, new ITemptureDetectDataListener() {
                @Override
                public void onDataChange(TemptureDetectData temptureDetectData) {
                    String message = "stopDetectTempture temptureDetectData:\n" + temptureDetectData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });

        } else if (oprater.equals(SET_WATCH_TIME)) {
            DeviceTimeSetting deviceTimeSetting = new DeviceTimeSetting(2020, 11, 6, 15, 30, 14, ETimeMode.MODE_12);
            VPOperateManager.getMangerInstance(mContext).settingTime(writeResponse, new IResponseListener() {
                @Override
                public void response(int state) {
                    String message = "settingTime response :\n" + state;
                    Logger.t(TAG).i(message);
                }
            }, deviceTimeSetting);
        } else if (oprater.equals(WEATHER_READ_STATUEINFO)) {
            VPOperateManager.getMangerInstance(mContext).readWeatherStatusInfo(writeResponse, new IWeatherStatusDataListener() {
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

        } else if (oprater.equals(SYNC_MUSIC_INFO)) {
            int play = 1;//播放状态
            int pause = 2;//暂停状态
            MusicData musicData = new MusicData("周杰伦", "上海一九四三", "范特西", 80, play);
            Logger.t(TAG).i("settingMusicData");
            VPOperateManager.getMangerInstance(mContext).settingMusicData(writeResponse, musicData, new IMusicControlListener() {
                @Override
                public void oprateMusicSuccess() {
                    Logger.t(TAG).i("oprateSuccess");
                }

                @Override
                public void oprateMusicFail() {
                    Logger.t(TAG).i("oprateFail");
                }

                @Override
                public void nextMusic() {

                }

                @Override
                public void previousMusic() {

                }

                @Override
                public void pauseAndPlayMusic() {

                }

                @Override
                public void pauseMusic() {

                }

                @Override
                public void playMusic() {

                }

                @Override
                public void voiceUp() {

                }

                @Override
                public void voiceDown() {

                }


            });
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
            VPOperateManager.getMangerInstance(mContext).settingWeatherStatusInfo(writeResponse, weatherStatusSetting, new IWeatherStatusDataListener() {
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
                setWeatherData1();
            }
        } else if (oprater.equals(LOW_POWER_READ)) {
            VPOperateManager.getMangerInstance(mContext).readLowPower(writeResponse, new ILowPowerListener() {
                @Override
                public void onLowpowerDataDataChange(LowPowerData lowPowerData) {
                    String message = "onLowpowerDataDataChange read:\n" + lowPowerData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(LOW_POWER_OPEN)) {
            VPOperateManager.getMangerInstance(mContext).settingLowpower(writeResponse, new ILowPowerListener() {
                @Override
                public void onLowpowerDataDataChange(LowPowerData lowPowerData) {
                    String message = "onLowpowerDataDataChange open:\n" + lowPowerData.toString();
                    Logger.t(TAG).i(message);
                }
            }, true);
        } else if (oprater.equals(LOW_POWER_CLOSE)) {
            VPOperateManager.getMangerInstance(mContext).settingLowpower(writeResponse, new ILowPowerListener() {
                @Override
                public void onLowpowerDataDataChange(LowPowerData lowPowerData) {
                    String message = "onLowpowerDataDataChange close:\n" + lowPowerData.toString();
                    Logger.t(TAG).i(message);
                }
            }, false);
        } else if (oprater.equals(BP_FUNCTION_READ)) {
            VPOperateManager.getMangerInstance(mContext).readBpFunctionState(writeResponse, new IBPFunctionListener() {
                @Override
                public void onDataChange(BpFunctionData bpFunctionData) {
                    String message = "readBpFunctionState close:\n" + bpFunctionData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(BP_FUNCTION_SETTING)) {
            VPOperateManager.getMangerInstance(mContext).settingBpFunctionState(writeResponse, new IBPFunctionListener() {
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
            VPOperateManager.getMangerInstance(mContext).stopDetectHeart(writeResponse);
        } else if (oprater.equals(BP_DETECT_START)) {
            tv1.setText(BP_DETECT_START + ",等待50s...");
            VPOperateManager.getMangerInstance(mContext).startDetectBP(writeResponse, new IBPDetectDataListener() {
                @Override
                public void onDataChange(BpData bpData) {
                    String message = "BpData date statues:\n" + bpData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, EBPDetectModel.DETECT_MODEL_PUBLIC);
        } else if (oprater.equals(BP_DETECT_STOP)) {
            tv1.setText(BP_DETECT_STOP);
            VPOperateManager.getMangerInstance(mContext).stopDetectBP(writeResponse, EBPDetectModel.DETECT_MODEL_PUBLIC);
        } else if (oprater.equals(BP_DETECTMODEL_SETTING)) {
            boolean isOpenPrivateModel = true;
            boolean isAngioAdjuste = false;
            BpSetting bpSetting = new BpSetting(isOpenPrivateModel, 111, 88);
            //是否开启动态血压调整模式，功能标志位在密码验证的返回
            bpSetting.setAngioAdjuste(isAngioAdjuste);
            VPOperateManager.getMangerInstance(mContext).settingDetectBP(writeResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    String message = "BpSettingData:\n" + bpSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, bpSetting);
        } else if (oprater.equals(BP_DETECTMODEL_READ)) {
            VPOperateManager.getMangerInstance(mContext).readDetectBP(writeResponse, new IBPSettingDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingDetectBP(writeResponse, new IBPSettingDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).cancelAngioAdjust(writeResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    String message = "BpSettingData:\n" + bpSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, bpSetting);
        } else if (oprater.equals(PWD_COMFIRM)) {
            boolean is24Hourmodel = false;
            VPOperateManager.getMangerInstance(mContext).confirmDevicePwd(writeResponse, new IPwdDataListener() {
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

        } else if (oprater.equals(PWD_MODIFY)) {
            VPOperateManager.getMangerInstance(mContext).modifyDevicePwd(writeResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwd) {
                    String message = "PwdData:\n" + pwd.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, "0000");
        } else if (oprater.equals(SPORT_CURRENT_READ)) {
            VPOperateManager.getMangerInstance(mContext).readSportStep(writeResponse, new ISportDataListener() {
                @Override
                public void onSportDataChange(SportData sportData) {
                    String message = "当前计步:\n" + sportData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(PERSONINFO_SYNC)) {
            VPOperateManager.getMangerInstance(mContext).syncPersonInfo(writeResponse, new IPersonInfoDataListener() {
                @Override
                public void OnPersoninfoDataChange(EOprateStauts EOprateStauts) {
                    String message = "同步个人信息:\n" + EOprateStauts.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new PersonInfoData(ESex.MAN, 178, 60, 20, 8000));
        } else if (oprater.equals(CAMERA_START)) {
            VPOperateManager.getMangerInstance(mContext).startCamera(writeResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(ECameraStatus oprateStauts) {

                }


            });
        } else if (oprater.equals(CAMERA_STOP)) {
            VPOperateManager.getMangerInstance(mContext).stopCamera(writeResponse, new ICameraDataListener() {
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

            VPOperateManager.getMangerInstance(mContext).settingAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    String message = "设置闹钟:\n" + alarmData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, alarmSettingList);
        } else if (oprater.equals(ALARM_READ)) {
            VPOperateManager.getMangerInstance(mContext).readAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    String message = "读取闹钟:\n" + alarmData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(ALARM_NEW_READ)) {
            VPOperateManager.getMangerInstance(mContext).readAlarm2(writeResponse, new IAlarm2DataListListener() {
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
            VPOperateManager.getMangerInstance(mContext).deleteAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    String message = "删除闹钟[新版]:\n" + alarmData2.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
                //String bluetoothAddress, int alarmId, int alarmHour, int alarmMinute, String repeatStatus, int scene, String unRepeatDate, boolean isOpen
            }, alarm2Setting);

        } else if(oprater.equals(ALARM_NEW_LISTENER)){
            VPOperateManager.getMangerInstance(mContext).setOnDeviceAlarm2ChangedListener(new OnDeviceAlarm2ChangedListener() {
                @Override
                public void onDeviceAlarm2Changed() {
                    sendMsg("设备端闹钟状态改变了，请调用[readAlarm2更新闹钟列表]", 1);
                }
            });

        } else if (oprater.equals(ALARM_NEW_ADD)) {
            Alarm2Setting alarm2Setting = getMultiAlarmSetting();
            VPOperateManager.getMangerInstance(mContext).addAlarm2(writeResponse, new IAlarm2DataListListener() {
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
            VPOperateManager.getMangerInstance(mContext).modifyAlarm2(writeResponse, new IAlarm2DataListListener() {
                @Override
                public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
                    String message = "修改闹钟[新版]:\n" + alarmData2.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, alarm2Setting);
        } else if (oprater.equals(LONGSEAT_SETTING_OPEN)) {
            VPOperateManager.getMangerInstance(mContext).settingLongSeat(writeResponse, new LongSeatSetting(10, 35, 11, 45, 60, true), new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeat) {
                    String message = "设置久坐-打开:\n" + longSeat.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LONGSEAT_SETTING_CLOSE)) {
            VPOperateManager.getMangerInstance(mContext).settingLongSeat(writeResponse, new LongSeatSetting(10, 40, 12, 40, 40, false), new ILongSeatDataListener() {

                @Override
                public void onLongSeatDataChange(LongSeatData longSeat) {
                    String message = "设置久坐-关闭:\n" + longSeat.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LONGSEAT_READ)) {
            VPOperateManager.getMangerInstance(mContext).readLongSeat(writeResponse, new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeat) {
                    String message = "设置久坐-读取:\n" + longSeat.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LANGUAGE_CHINESE)) {
            VPOperateManager.getMangerInstance(mContext).settingDeviceLanguage(writeResponse, new ILanguageDataListener() {
                @Override
                public void onLanguageDataChange(LanguageData languageData) {
                    String message = "设置语言(中文):\n" + languageData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, ELanguage.CHINA);
        } else if (oprater.equals(LANGUAGE_ENGLISH)) {
            VPOperateManager.getMangerInstance(mContext).settingDeviceLanguage(writeResponse, new ILanguageDataListener() {
                @Override
                public void onLanguageDataChange(LanguageData languageData) {
                    String message = "设置语言(英文):\n" + languageData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, ELanguage.ENGLISH);
        } else if (oprater.equals(BATTERY)) {
            VPOperateManager.getMangerInstance(mContext).readBattery(writeResponse, new IBatteryDataListener() {
                @Override
                public void onDataChange(BatteryData batteryData) {
                    String message = "电池等级:\n" + batteryData.getBatteryLevel() + "\n" + "电量:" + batteryData.getBatteryLevel() * 25 + "%";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(NIGHT_TURN_WRIST_READ)) {
            VPOperateManager.getMangerInstance(mContext).readNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-读取:\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(NIGHT_TURN_WRIST_OPEN)) {
            VPOperateManager.getMangerInstance(mContext).settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-打开:\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, true);
        } else if (oprater.equals(NIGHT_TURN_WRIST_CLOSE)) {
            VPOperateManager.getMangerInstance(mContext).settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-" + isOpen + ":\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new NightTurnWristSetting(isOpen, startTime, endTime, level));
        } else if (oprater.equals(DISCONNECT)) {
            VPOperateManager.getMangerInstance(mContext).disconnectWatch(writeResponse);
            finish();
        } else if (oprater.equals(FINDPHONE)) {
            VPOperateManager.getMangerInstance(mContext).settingFindPhoneListener(new IFindPhonelistener() {
                @Override
                public void findPhone() {
                    String message = "(监听到手环要查找手机)-where is the phone,make some noise!";
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(DEVICE_COUSTOM_READ)) {
            VPOperateManager.getMangerInstance(mContext).readCustomSetting(writeResponse, new ICustomSettingDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-设置:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, customSetting);
        } else if (oprater.equals(CHECK_WEAR_SETING_OPEN)) {
            CheckWearSetting checkWearSetting = new CheckWearSetting();
            checkWearSetting.setOpen(true);
            VPOperateManager.getMangerInstance(mContext).setttingCheckWear(writeResponse, new ICheckWearDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).setttingCheckWear(writeResponse, new ICheckWearDataListener() {
                @Override
                public void onCheckWearDataChange(CheckWearData checkWearData) {
                    String message = "佩戴检测-关闭:\n" + checkWearData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, checkWearSetting);
        } else if (oprater.equals(FINDDEVICE_SETTING_OPEN)) {
            VPOperateManager.getMangerInstance(mContext).settingFindDevice(writeResponse, new IFindDeviceDatalistener() {
                @Override
                public void onFindDevice(FindDeviceData findDeviceData) {
                    String message = "防丢-打开:\n" + findDeviceData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, true);
        } else if (oprater.equals(FINDDEVICE_SETTING_CLOSE)) {
            VPOperateManager.getMangerInstance(mContext).settingFindDevice(writeResponse, new IFindDeviceDatalistener() {
                @Override
                public void onFindDevice(FindDeviceData findDeviceData) {
                    String message = "防丢-关闭:\n" + findDeviceData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, false);
        } else if (oprater.equals(FINDDEVICE_READ)) {
            VPOperateManager.getMangerInstance(mContext).readFindDevice(writeResponse, new IFindDeviceDatalistener() {
                @Override
                public void onFindDevice(FindDeviceData findDeviceData) {
                    String message = "防丢-读取:\n" + findDeviceData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SOCIAL_MSG_READ)) {
            VPOperateManager.getMangerInstance(mContext).readSocialMsg(writeResponse, new ISocialMsgDataListener() {
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


            VPOperateManager.getMangerInstance(mContext).settingSocialMsg(writeResponse, new ISocialMsgDataListener() {
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

            VPOperateManager.getMangerInstance(mContext).settingSocialMsg(writeResponse, new ISocialMsgDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingSOSListener(new ISOSListener() {
                @Override
                public void sos() {
                    String liansuo_sos_call_back = "liansuo_sos call back";
                    Logger.t(TAG).i(liansuo_sos_call_back);
                    sendMsg(liansuo_sos_call_back, 1);
                }
            });
        } else if (oprater.equals(LIANSUO_SEND_ORDER)) {
            VPOperateManager.getMangerInstance(mContext).sendToSoldierCommand(writeResponse, new IResponseListener() {
                @Override
                public void response(int state) {
                    String message = "liansuo send cmd call back:" + state;
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(LIANSUO_SEND_CONTENT)) {
            VPOperateManager.getMangerInstance(mContext).sendToSoldierContent(writeResponse, new SoldierContentSetting("123123123123123123123123123123123"), new IResponseListener() {
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
//            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentsociaSetting4);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ContentSetting contentsociaSetting5 = new ContentSocailSetting(ESocailMsg.MESSENGER, "vepo", "测试反馈 MESSENGER");
//                    VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentsociaSetting5);
//
//                }
//            }, 2000);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ContentSetting contentsociaSetting6 = new ContentSocailSetting(ESocailMsg.PHONE, "vepo", "测试反馈 PHONE");
//                    VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentphoneSetting0);
//                }
//            }, 4000);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ContentSetting contentsociaSetting6 = new ContentSocailSetting(ESocailMsg.CONNECTED2_ME, "vepo", "测试反馈 CONNECTED2_ME");
//                    VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentsociaSetting6);
//                }
//            }, 6000);
//            ContentSetting contentsociaSetting6 = new ContentSocailSetting(ESocailMsg.G15MSG, "G15", "ABCDEFG,今天是星期五，明天星期六");
//            VPOperateManager.getMangerInstance(mContext).sendG15MsgContent(writeResponse, "G15", "ABCDEFG,今天是星期五,敬挽发大水发大水发放大范德萨", new IG15MessageListener() {
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
            VPOperateManager.getMangerInstance(mContext).offhookOrIdlePhone(writeResponse);
        } else if (oprater.equals(DEVICE_CONTROL_PHONE)) {
            VPOperateManager.getMangerInstance(mContext).settingDeviceControlPhone(new IDeviceControlPhoneModelState() {

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
            VPOperateManager.getMangerInstance(mContext).clearDeviceData(writeResponse);
            finish();
        } else if (oprater.equals(HEARTWRING_READ)) {
            VPOperateManager.getMangerInstance(mContext).readHeartWarning(writeResponse, new IHeartWaringDataListener() {
                @Override
                public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                    String message = "心率报警-读取:\n" + heartWaringData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(HEARTWRING_OPEN)) {
            VPOperateManager.getMangerInstance(mContext).settingHeartWarning(writeResponse, new IHeartWaringDataListener() {
                @Override
                public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                    String message = "心率报警-打开:\n" + heartWaringData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new HeartWaringSetting(120, 110, true));
        } else if (oprater.equals(HEARTWRING_CLOSE)) {
            VPOperateManager.getMangerInstance(mContext).settingHeartWarning(writeResponse, new IHeartWaringDataListener() {
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
//            VPOperateManager.getMangerInstance(mContext).sendOrder(writeResponse, cmd);
            VPOperateManager.getMangerInstance(mContext).startDetectSPO2H(writeResponse, new ISpo2hDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).stopDetectSPO2H(writeResponse, new ISpo2hDataListener() {
                @Override
                public void onSpO2HADataChange(Spo2hData spo2HData) {
                    String message = "血氧-结束:\n" + spo2HData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SPO2H_AUTO_DETECT_READ))
            VPOperateManager.getMangerInstance(mContext).readSpo2hAutoDetect(writeResponse, new IAllSetDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingSpo2hAutoDetect(writeResponse, new IAllSetDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingSpo2hAutoDetect(writeResponse, new IAllSetDataListener() {
                @Override
                public void onAllSetDataChangeListener(AllSetData allSetData) {
                    String message = "血氧自动检测-打开\n" + allSetData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, mAlarmSetting);
        } else if (oprater.equals(FATIGUE_OPEN)) {
            VPOperateManager.getMangerInstance(mContext).startDetectFatigue(writeResponse, new IFatigueDataListener() {
                @Override
                public void onFatigueDataListener(FatigueData fatigueData) {
                    String message = "疲劳度-开始:\n" + fatigueData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(FATIGUE_CLOSE)) {
            VPOperateManager.getMangerInstance(mContext).stopDetectFatigue(writeResponse, new IFatigueDataListener() {
                @Override
                public void onFatigueDataListener(FatigueData fatigueData) {
                    String message = "疲劳度-结束:\n" + fatigueData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(WOMEN_SETTING)) {
            VPOperateManager.getMangerInstance(mContext).settingWomenState(writeResponse, new IWomenDataListener() {
                @Override
                public void onWomenDataChange(WomenData womenData) {
                    String message = "女性状态-设置:\n" + womenData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new WomenSetting(EWomenStatus.PREING, new TimeData(2016, 3, 1), new TimeData(2017, 1, 14)));
        } else if (oprater.equals(WOMEN_READ)) {
            VPOperateManager.getMangerInstance(mContext).readWomenState(writeResponse, new IWomenDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
                @Override
                public void OnCountDownDataChange(CountDownData countDownData) {
                    String message = "倒计时-App:\n" + countDownData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(COUNT_DOWN_APP_READ)) {
            VPOperateManager.getMangerInstance(mContext).readCountDown(writeResponse, new ICountDownListener() {
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
            VPOperateManager.getMangerInstance(mContext).readChantingData(writeResponse, new ChantingSetting(timestamp), new IChantingDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).settingScreenLight(writeResponse, new IScreenLightListener() {
                @Override
                public void onScreenLightDataChange(ScreenLightData screenLightData) {
                    String message = "屏幕调节数据-设置:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new ScreenSetting(22, 0, 7, 0, 2, 4));
        } else if (oprater.equals(SCREEN_LIGHT_READ)) {
            VPOperateManager.getMangerInstance(mContext).readScreenLight(writeResponse, new IScreenLightListener() {
                @Override
                public void onScreenLightDataChange(ScreenLightData screenLightData) {
                    String message = "屏幕调节数据-读取:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SCREEN_STYLE_READ)) {
            VPOperateManager.getMangerInstance(mContext).readScreenStyle(writeResponse, new IScreenStyleListener() {
                @Override
                public void onScreenStyleDataChange(ScreenStyleData screenLightData) {
                    String message = "屏幕样式-读取:" + screenLightData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SCREEN_STYLE_SETTING)) {
            int screenstyle = 2;
            VPOperateManager.getMangerInstance(mContext).settingScreenStyle(writeResponse, new IScreenStyleListener() {
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
            VPOperateManager.getMangerInstance(mContext).readTemptureDataBySetting(writeResponse, new ITemptureDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readSleepData(writeResponse, new ISleepDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readSleepDataFromDay(writeResponse, new ISleepDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readSleepDataSingleDay(writeResponse, new ISleepDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readDrinkData(writeResponse, new IDrinkDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readOriginData(writeResponse, originDataListener, 3);
        } else if (oprater.equals(READ_HEALTH_ORIGINAL_FROM)) {
            int yesterday = 1;
            VPOperateManager.getMangerInstance(mContext).readOriginDataFromDay(writeResponse, new IOriginDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readOriginDataSingleDay(writeResponse, originProgressListener, today, 1, watchDataDay);
        } else if (oprater.equals(READ_HEALTH)) {
            VPOperateManager.getMangerInstance(mContext).readAllHealthData(new IAllHealthDataListener() {
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
            String shareperence = VPOperateManager.getMangerInstance(mContext).traversalShareperence();
            Logger.t(TAG).i(shareperence);
        } else if (oprater.equals(SPORT_MODE_ORIGIN_END)) {
            VPOperateManager.getMangerInstance(mContext).stopSportModel(writeResponse, new ISportModelStateListener() {
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
            VPOperateManager.getMangerInstance(mContext).readSportModelState(writeResponse, new ISportModelStateListener() {
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
            VPOperateManager.getMangerInstance(mContext).startMultSportModel(writeResponse, new ISportModelStateListener() {
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
            VPOperateManager.getMangerInstance(mContext).startSportModel(writeResponse, new ISportModelStateListener() {
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
            VPOperateManager.getMangerInstance(mContext).readSportModelOrigin(writeResponse, new ISportModelOriginListener() {
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
            VPOperateManager.getMangerInstance(mContext).readHRVOrigin(writeResponse, new IHRVOriginDataListener() {
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
            VPOperateManager.getMangerInstance(mContext).readAutoDetectOriginDataFromS22(writeResponse, new IAutoDetectOriginDataListener() {

                @Override
                public void onAutoDetectOriginDataChangeListener(List<AutoDetectOriginData> autoDetectOriginDataList) {
                    for (AutoDetectOriginData autoDetectOriginData : autoDetectOriginDataList) {
                        Logger.t(TAG).i("autoDetectOriginData:" + autoDetectOriginData.toString());
                    }
                }
            }, timeData);
        } else if (oprater.equals(S22_READ_STATE)) {
            VPOperateManager.getMangerInstance(mContext).readAutoDetectStateFromS22(writeResponse, new ICustomProtocolStateListener() {

                @Override
                public void onS22AutoDetectStateChangeListener(AutoDetectStateData autoDetectStateData) {
                    Logger.t(TAG).i("autoDetectStateData:" + autoDetectStateData.toString());
                }
            });
        } else if (oprater.equals(S22_SETTING_STATE_OPEN)) {
            AutoDetectStateSetting autoDetectStateSetting = new AutoDetectStateSetting();
            autoDetectStateSetting.setSpo2h24Hour(SUPPORT_OPEN);
            VPOperateManager.getMangerInstance(mContext).setAutoDetectStateToS22(writeResponse, new ICustomProtocolStateListener() {
                @Override
                public void onS22AutoDetectStateChangeListener(AutoDetectStateData autoDetectStateData) {
                    Logger.t(TAG).i("autoDetectStateData:" + autoDetectStateData.toString());
                }
            }, autoDetectStateSetting);
        } else if (oprater.equals(S22_SETTING_STATE_CLOSE)) {
            AutoDetectStateSetting autoDetectStateSetting = new AutoDetectStateSetting();
            autoDetectStateSetting.setSpo2h24Hour(SUPPORT_CLOSE);
            VPOperateManager.getMangerInstance(mContext).setAutoDetectStateToS22(writeResponse, new ICustomProtocolStateListener() {
                @Override
                public void onS22AutoDetectStateChangeListener(AutoDetectStateData autoDetectStateData) {
                    Logger.t(TAG).i("autoDetectStateData:" + autoDetectStateData.toString());
                }
            }, autoDetectStateSetting);
        } else if (oprater.equals(SPO2H_ORIGIN_READ)) {
            VPOperateManager.getMangerInstance(mContext).readSpo2hOrigin(writeResponse, new ISpo2hOriginDataListener() {
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
        }  else if (oprater.equals(TEXT_ALARM)) {
            startActivity(new Intent(this, TextAlarmActivity.class));
        } else if (oprater.equals(ORIGIN_LOG)) {
            startActivity(new Intent(this, OriginalDataLogActivity.class));
        }

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
        TimeData lasTimeUpdate = new TimeData(year, month, day, 12, 59, 23);
        //天气列表（以小时为单位）
        List<WeatherEvery3Hour> weatherEvery3HourList = new ArrayList<>();
        TimeData every3Hour0 = new TimeData(year, month, day, 12, 59, 23);
        TimeData every3Hour1 = new TimeData(year, month, day, 15, 59, 23);
        TimeData every3Hour2 = new TimeData(year, month, day, 18, 59, 23);
        TimeData every3Hour3 = new TimeData(year, month, day, 21, 59, 23);
        WeatherEvery3Hour weatherEvery3Hour0 =
                new WeatherEvery3Hour(every3Hour0, 60, -60, 6, 6, "3-4", 5.0);
        WeatherEvery3Hour weatherEvery3Hour1 =
                new WeatherEvery3Hour(every3Hour1, 70, -70, 7, 7, "10-12", 5.0);
        WeatherEvery3Hour weatherEvery3Hour2 =
                new WeatherEvery3Hour(every3Hour2, 80, -80, 8, 8, "10", 5.0);
        WeatherEvery3Hour weatherEvery3Hour3 =
                new WeatherEvery3Hour(every3Hour3, 90, -90, 9, 9, "15", 5.0);
        weatherEvery3HourList.add(weatherEvery3Hour0);
        weatherEvery3HourList.add(weatherEvery3Hour1);
        weatherEvery3HourList.add(weatherEvery3Hour2);
        weatherEvery3HourList.add(weatherEvery3Hour3);
        //天气列表（以天为单位）
        List<WeatherEveryDay> weatherEveryDayList = new ArrayList<>();
        TimeData everyDay0 = new TimeData(year, month, day, 12, 59, 23);
        WeatherEveryDay weatherEveryDay0 = new WeatherEveryDay(everyDay0, 80, -80, 60,
                -60, 10, 5, 10, "10-12", 5.2);
        weatherEveryDayList.add(weatherEveryDay0);
        WeatherData weatherData = new WeatherData(crc, cityName, sourcr, lasTimeUpdate, weatherEvery3HourList, weatherEveryDayList);
        VPOperateManager.getMangerInstance(mContext).settingWeatherData(writeResponse, weatherData, new IWeatherStatusDataListener() {
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
        VPOperateManager.getMangerInstance(mContext).settingWeatherData2(writeResponse, weatherData2, new IWeatherStatusDataListener() {
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
        VPOperateManager.getMangerInstance(mContext).settingDeviceControlPhone(new IDeviceControlPhoneModelState() {

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
        VPOperateManager.getMangerInstance(mContext).setCameraListener(new ICameraDataListener() {
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
        VPOperateManager.getMangerInstance(mContext).sendOrder(writeResponse, cmd);
        VPOperateManager.getMangerInstance(mContext).startDetectSPO2H(writeResponse, new ISpo2hDataListener() {
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
        VPOperateManager.getMangerInstance(mContext).stopDetectSPO2H(writeResponse, new ISpo2hDataListener() {
            @Override
            public void onSpO2HADataChange(Spo2hData spo2HData) {
                //不用理会
            }
        });

        VPOperateManager.getMangerInstance(mContext).startDetectHeart(writeResponse, new IHeartDataListener() {
            @Override
            public void onDataChange(HeartData heartData) {
                String message = "返回-心率值:" + heartData.toString();
                Logger.t(TAG).i(message);
            }
        });
    }

    public void stopListenADC() {
        Logger.t(TAG).i("停止监听光电信号");
        VPOperateManager.getMangerInstance(mContext).stopDetectHeart(writeResponse);
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
}
