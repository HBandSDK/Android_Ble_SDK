package com.timaimee.vpbluetoothsdkdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import com.timaimee.vpbluetoothsdkdemo.R;
import com.timaimee.vpbluetoothsdkdemo.adapter.GridAdatper;
import com.timaimee.vpbluetoothsdkdemo.oad.activity.OadActivity;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleNotifyResponse;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
import com.veepoo.protocol.listener.data.ICheckWearDataListener;
import com.veepoo.protocol.listener.data.ICountDownListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IAlarm2DataListListener;
import com.veepoo.protocol.listener.data.IScreenLightListener;
import com.veepoo.protocol.listener.data.IScreenStyleListener;
import com.veepoo.protocol.listener.data.ISpo2hDataListener;
import com.veepoo.protocol.listener.data.ISportModelStateListener;
import com.veepoo.protocol.listener.data.IWomenDataListener;
import com.veepoo.protocol.model.datas.AlarmData2;
import com.veepoo.protocol.model.datas.BatteryData;
import com.veepoo.protocol.model.datas.CheckWearData;
import com.veepoo.protocol.model.datas.CountDownData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.ScreenLightData;
import com.veepoo.protocol.model.datas.ScreenStyleData;
import com.veepoo.protocol.model.datas.Spo2hData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.SportModelStateData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.datas.WomenData;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.enums.EWomenStatus;
import com.veepoo.protocol.model.settings.Alarm2Setting;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.CheckWearSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSmsSetting;
import com.veepoo.protocol.model.settings.CountDownSetting;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.veepoo.protocol.model.settings.AlarmSetting;
import com.veepoo.protocol.model.datas.AlarmData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.datas.BpData;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.model.datas.DrinkData;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.datas.FatigueData;
import com.veepoo.protocol.model.datas.FindDeviceData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.model.settings.HeartWaringSetting;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.datas.LanguageData;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.listener.data.IAlarmDataListener;
import com.veepoo.protocol.listener.data.IBPDetectDataListener;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.ICameraDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDrinkDataListener;
import com.veepoo.protocol.listener.data.IFatigueDataListener;
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener;
import com.veepoo.protocol.listener.data.IFindPhonelistener;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.listener.data.IHeartWaringDataListener;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.settings.NightTurnWristSetting;
import com.veepoo.protocol.model.settings.ScreenSetting;
import com.veepoo.protocol.model.settings.WomenSetting;
import com.veepoo.protocol.operate.CameraOperater;
import com.veepoo.protocol.util.VpBleByteUtil;
import com.veepoo.protocol.util.VPLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by timaimee on 2017/2/8.
 */
public class OperaterActivity extends Activity implements AdapterView.OnItemClickListener {
    private final static String TAG = OperaterActivity.class.getSimpleName();
    int level = 1;
    TextView tv1, tv2, tv3;
    GridView mGridView;
    List<Map<String, String>> mGridData = new ArrayList<>();
    GridAdatper mGridAdapter;
    Context mContext = null;
    private String deviceaddress;
    private final static String PWD_COMFIRM = "1.设备密码-验证";
    private final static String PERSONINFO_SYNC = "2.个人信息-设置";
    private final static String PWD_MODIFY = "设备密码-修改";
    private final static String HEART_DETECT_START = "测量心率-开始";
    private final static String HEART_DETECT_STOP = "测量心率-结束";
    private final static String BP_DETECT_START = "测量血压-开始";
    private final static String BP_DETECT_STOP = "测量血压-结束";
    private final static String BP_DETECTMODEL_SETTING = "血压模式-设置";
    private final static String BP_DETECTMODEL_SETTING_ADJUSTE = "血压模式[动态调整]-设置";
    private final static String BP_DETECTMODEL_SETTING_ADJUSTE_CANCEL = "血压模式[动态调整]-取消";
    private final static String BP_DETECTMODEL_READ = "血压模式-读取";
    private final static String SPORT_CURRENT_READ = "当前计步-读取";
    private final static String CAMERA_START = "拍照模式-开始";
    private final static String CAMERA_STOP = "拍照模式-停止";
    private final static String ALARM_SETTING = "闹钟-设置";
    private final static String ALARM_READ = "闹钟-读取";
    private final static String ALARM_NEW_READ = "新闹钟-读取";
    private final static String ALARM_NEW_ADD = "新闹钟-添加";
    private final static String ALARM_NEW_MODIFY = "新闹钟-修改";
    private final static String ALARM_NEW_DELETE = "新闹钟-删除";
    private final static String LONGSEAT_SETTING_OPEN = "久坐-打开";
    private final static String LONGSEAT_SETTING_CLOSE = "久坐-关闭";
    private final static String LONGSEAT_READ = "久坐-读取";
    private final static String LANGUAGE_CHINESE = "语言设置-中文";
    private final static String LANGUAGE_ENGLISH = "语言设置-英文";
    private final static String BATTERY = "电池状态-读取";
    private final static String NIGHT_TURN_WRIST_OPEN = "夜间转腕-打开";
    private final static String NIGHT_TURN_WRIST_CLOSE = "夜间转腕-关闭";
    private final static String NIGHT_TURN_WRIST_READ = "夜间转腕-读取";
    private final static String NIGHT_TURN_WRIST_CUSTOM_TIME = "夜间转腕-自定义时间";
    private final static String NIGHT_TURN_WRIST_CUSTOM_TIME_LEVEL = "夜间转腕-自定义时间和等级";
    private final static String DISCONNECT = "蓝牙连接-断开";
    private final static String FINDPHONE = "手机防丢";
    private final static String CHECK_WEAR_SETING_OPEN = "佩戴检测-打开";
    private final static String CHECK_WEAR_SETING_CLOSE = "佩戴检测-关闭";
    private final static String FINDDEVICE_SETTING_OPEN = "设备防丢-打开";
    private final static String FINDDEVICE_SETTING_CLOSE = "设备防丢-关闭";
    private final static String FINDDEVICE_READ = "设备防丢-读取";
    private final static String DEVICE_COUSTOM_READ = "个性化-读取";
    private final static String DEVICE_COUSTOM_SETTING = "个性化-设置";
    private final static String SOCIAL_MSG_SETTING = "社交消息提醒-设置";
    private final static String SOCIAL_MSG_READ = "社交消息提醒-读取设置";
    private final static String SOCIAL_MSG_SEND = "社交消息提醒-发送内容";
    private final static String HEARTWRING_READ = "心率报警-读取";
    private final static String HEARTWRING_OPEN = "心率报警-打开";
    private final static String HEARTWRING_CLOSE = "心率报警-关闭";
    private final static String SPO2H_OPEN = "血氧-读取";
    private final static String SPO2H_CLOSE = "血氧-结束";
    private final static String FATIGUE_OPEN = "疲劳度-读取";
    private final static String FATIGUE_CLOSE = "疲劳度-结束";
    private final static String WOMEN_SETTING = "女性状态-设置";
    private final static String WOMEN_READ = "女性状态-读取";
    private final static String COUNT_DOWN_WATCH = "倒计时-手表单独使用";
    private final static String COUNT_DOWN_APP = "倒计时-App使用";
    private final static String SCREEN_LIGHT_SETTING = "屏幕调节-设置";
    private final static String SCREEN_LIGHT_READ = "屏幕调节-读取";
    public final static String SCREEN_STYLE_READ = "屏幕样式-读取";
    public final static String SCREEN_STYLE_SETTING = "屏幕样式-设置";
    private final static String AIM_SPROT_CALC = "目标步数-计算";
    private final static String INSTITUTION_TRANSLATION = "公英制转换";
    private final static String READ_HEALTH_DRINK = "读取健康数据-饮酒";
    private final static String READ_HEALTH_SLEEP = "读取健康数据-睡眠";
    private final static String READ_HEALTH_SLEEP_FROM = "读取健康数据-睡眠-从哪天起";
    private final static String READ_HEALTH_SLEEP_SINGLEDAY = "读取健康数据-睡眠-读这天";
    private final static String READ_HEALTH_ORIGINAL = "读取健康数据-5分钟";
    private final static String READ_HEALTH_ORIGINAL_FROM = "读取健康数据-从哪天起";
    private final static String READ_HEALTH_ORIGINAL_SINGLEDAY = "读取健康数据-读这天";
    private final static String READ_HEALTH = "读取健康数据-全部";
    public final static String SPORT_MODE_ORIGIN_READSTAUTS = "读取状态-运动模式";
    public final static String SPORT_MODE_ORIGIN_START = "开启-运动模式";
    public final static String SPORT_MODE_ORIGIN_END = "结束-运动模式";
    public final static String CLEAR_DEVICE_DATA = "清除数据";
    private final static String OAD = "固件升级";
    private final static String READ_SP = "读取SP";
    String[] oprateStr = new String[]{
            PWD_COMFIRM, PERSONINFO_SYNC, PWD_MODIFY,
            HEART_DETECT_START, HEART_DETECT_STOP, BP_DETECT_START, BP_DETECT_STOP, BP_DETECTMODEL_SETTING, BP_DETECTMODEL_READ,
            BP_DETECTMODEL_SETTING_ADJUSTE_CANCEL, BP_DETECTMODEL_SETTING_ADJUSTE,
            SPORT_CURRENT_READ, CAMERA_START, CAMERA_STOP, ALARM_SETTING, ALARM_READ, ALARM_NEW_READ, ALARM_NEW_ADD, ALARM_NEW_MODIFY, ALARM_NEW_DELETE,
            LONGSEAT_SETTING_OPEN, LONGSEAT_SETTING_CLOSE, LONGSEAT_READ, LANGUAGE_CHINESE, LANGUAGE_ENGLISH,
            BATTERY, NIGHT_TURN_WRIST_OPEN, NIGHT_TURN_WRIST_CLOSE, NIGHT_TURN_WRIST_READ, NIGHT_TURN_WRIST_CUSTOM_TIME, NIGHT_TURN_WRIST_CUSTOM_TIME_LEVEL,
            DISCONNECT, DEVICE_COUSTOM_READ, DEVICE_COUSTOM_SETTING, FINDPHONE,
            CHECK_WEAR_SETING_OPEN, CHECK_WEAR_SETING_CLOSE,
            FINDDEVICE_SETTING_OPEN, FINDDEVICE_SETTING_CLOSE, FINDDEVICE_READ,
            SOCIAL_MSG_SETTING, SOCIAL_MSG_READ, SOCIAL_MSG_SEND, HEARTWRING_READ, HEARTWRING_OPEN, HEARTWRING_CLOSE,
            SPO2H_OPEN, SPO2H_CLOSE, FATIGUE_OPEN, FATIGUE_CLOSE, WOMEN_SETTING, WOMEN_READ,
            COUNT_DOWN_WATCH, COUNT_DOWN_APP, SCREEN_LIGHT_SETTING, SCREEN_LIGHT_READ, SCREEN_STYLE_READ, SCREEN_STYLE_SETTING, AIM_SPROT_CALC, INSTITUTION_TRANSLATION,
            READ_HEALTH_SLEEP, READ_HEALTH_SLEEP_FROM, READ_HEALTH_SLEEP_SINGLEDAY, READ_HEALTH_DRINK, READ_HEALTH_ORIGINAL,
            READ_HEALTH_ORIGINAL_FROM, READ_HEALTH_ORIGINAL_SINGLEDAY, READ_HEALTH,
            OAD, READ_SP, SPORT_MODE_ORIGIN_READSTAUTS, SPORT_MODE_ORIGIN_START, SPORT_MODE_ORIGIN_END, CLEAR_DEVICE_DATA

    };
    Message msg;
    Handler mHandler = new Handler() {
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
    int contactMsgLength = 0;
    int allMsgLenght = 4;
    private int deviceNumber = -1;
    private String deviceVersion;
    private String deviceTestVersion;
    boolean isOadModel = false;
    boolean isNewSportCalc = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate);
        mContext = getApplicationContext();
        deviceaddress = getIntent().getStringExtra("deviceaddress");
        tv1 = (TextView) super.findViewById(R.id.tv1);
        tv2 = (TextView) super.findViewById(R.id.tv2);
        tv3 = (TextView) super.findViewById(R.id.tv3);
        initGridView();
//        listenDeviceCallbackData();
    }

    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.main_gridview);
        for (int i = 0; i < oprateStr.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("str", oprateStr[i]);
            mGridData.add(map);
        }
        mGridAdapter = new GridAdatper(this, mGridData);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String oprater = mGridData.get(position).get("str");
        Toast.makeText(mContext, oprater, Toast.LENGTH_SHORT).show();
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        if (oprater.equals(HEART_DETECT_START)) {
            VPOperateManager.getMangerInstance(mContext).startDetectHeart(writeResponse, new IHeartDataListener() {

                @Override
                public void onDataChange(HeartData heart) {
                    String message = "heart:\n" + heart.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(HEART_DETECT_STOP)) {
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
        } else if (oprater.equals(SPORT_MODE_ORIGIN_END)) {
            VPOperateManager.getMangerInstance(mContext).stopSportModel(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "运动模式状态:" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(SPORT_MODE_ORIGIN_READSTAUTS)) {
            VPOperateManager.getMangerInstance(mContext).readSportModelState(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "运动模式状态" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(SPORT_MODE_ORIGIN_START)) {
            VPOperateManager.getMangerInstance(mContext).startSportModel(writeResponse, new ISportModelStateListener() {
                @Override
                public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                    String message = "运动模式状态" + sportModelStateData.toString();
                    Logger.t(TAG).i(message);
                }
            });
        } else if (oprater.equals(CLEAR_DEVICE_DATA)) {
            VPOperateManager.getMangerInstance(mContext).clearDeviceData(writeResponse);
            finish();
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
                    sendMsg(message, 1);

                    deviceNumber = pwdData.getDeviceNumber();
                    deviceVersion = pwdData.getDeviceVersion();
                    deviceTestVersion = pwdData.getDeviceTestVersion();

                }
            }, new IDeviceFuctionDataListener() {
                @Override
                public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                    String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 2);
                    EFunctionStatus newCalcSport = functionSupport.getNewCalcSport();
                    if (newCalcSport != null && newCalcSport.equals(EFunctionStatus.SUPPORT)) {
                        isNewSportCalc = true;
                    } else {
                        isNewSportCalc = false;
                    }
                    watchDataDay = functionSupport.getWathcDay();
                    contactMsgLength = functionSupport.getContactMsgLength();
                    allMsgLenght = functionSupport.getAllMsgLength();
                    VPLogger.i("数据读取处理，ORIGIN_DATA_DAY:" + watchDataDay);
                }
            }, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 3);
                }
            }, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "FunctionCustomSettingData:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 4);
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
                    String message = "当前计步:\n" + sportData.getStep();
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
                public void OnCameraDataChange(CameraOperater.COStatus oprateStauts) {
                    String message = "打开拍照:\n" + oprateStauts.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(CAMERA_STOP)) {
            VPOperateManager.getMangerInstance(mContext).stopCamera(writeResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(CameraOperater.COStatus oprateStauts) {
                    String message = "关闭拍照:\n" + oprateStauts.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
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
            level++;
            Logger.t(TAG).i("夜间转腕-" + level);
            NightTurnWristSetting nightTurnWristSetting = new NightTurnWristSetting(isOpen, startTime, endTime, level);
            VPOperateManager.getMangerInstance(mContext).settingNightTurnWriste(writeResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    String message = "夜间转腕-" + isOpen + ":\n" + nightTurnWristeData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, nightTurnWristSetting);
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
            VPOperateManager.getMangerInstance(mContext).changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-设置:\n" + customSettingData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect));
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
                    String message = " 社交信息提醒-读取:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
        } else if (oprater.equals(SOCIAL_MSG_SETTING)) {
            FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
            socailMsgData.setPhone(EFunctionStatus.SUPPORT);
            socailMsgData.setMsg(EFunctionStatus.SUPPORT);
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_CLOSE);
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
            socailMsgData.setSina(EFunctionStatus.UNSUPPORT);
            socailMsgData.setFlickr(EFunctionStatus.UNSUPPORT);
            socailMsgData.setLinkin(EFunctionStatus.UNSUPPORT);
            socailMsgData.setLine(EFunctionStatus.UNSUPPORT);
            socailMsgData.setInstagram(EFunctionStatus.UNSUPPORT);
            socailMsgData.setSnapchat(EFunctionStatus.UNSUPPORT);
            socailMsgData.setSkype(EFunctionStatus.UNSUPPORT);
            VPOperateManager.getMangerInstance(mContext).settingSocialMsg(writeResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    String message = " 社交信息提醒-设置:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            }, socailMsgData);
        } else if (oprater.equals(SOCIAL_MSG_SEND)) {
            /**电话,可以只传电话号码**/
//            ContentSetting contentphoneSetting0 = new ContentPhoneSetting(ESocailMsg.PHONE, contactMsgLength, allMsgLenght,"0755-86562490");
            /**电话,传联系人姓名以及电话号码，最终显示的联系人姓名**/
//            ContentSetting contentphoneSetting1 = new ContentPhoneSetting(ESocailMsg.PHONE, contactMsgLength, allMsgLenght,"深圳市维亿魄科技有限公司", "0755-86562490");
//            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentphoneSetting0);

            /**短信，可以只传电话号码**/
            ContentSetting contentsmsSetting0 = new ContentSmsSetting(ESocailMsg.SMS, contactMsgLength, allMsgLenght, "0755-86562490", "公司研发的项目主要在医疗健康智能穿戴、智能家居、新型智能交友产品、飞机航模、智能安全锁五个领域方面");
            /**短信，传联系人姓名以及电话号码，最终显示的联系人姓名**/
            ContentSetting contentsmsSetting1 = new ContentSmsSetting(ESocailMsg.SMS, contactMsgLength, allMsgLenght, "深圳市维亿魄科技有限公司", "0755-86562490", "公司研发的项目主要在医疗健康智能穿戴、智能家居、新型智能交友产品、飞机航模、智能安全锁五个领域方面");
            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentsmsSetting1);

            /**第三方APP推送,发送前先通过密码验证获取FunctionSocailMsgData的状态**/
//            ContentSetting contentsociaSetting = new ContentSocailSetting(ESocailMsg.WECHAT, contactMsgLength, allMsgLenght, "veepoo", "公司研发的项目主要在医疗健康智能穿戴、智能家居、新型智能交友产品、飞机航模、智能安全锁五个领域方面");
//            VPOperateManager.getMangerInstance(mContext).sendSocialMsgContent(writeResponse, contentsociaSetting);
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
            VPOperateManager.getMangerInstance(mContext).startDetectSPO2H(writeResponse, new ISpo2hDataListener() {

                @Override
                public void onSpO2HADataChange(Spo2hData spo2HData) {
                    if (spo2HData.getValue() == 0) {
                        return;
                    }
                    String message = "血氧-开始:\n" + spo2HData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
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
        } else if (oprater.equals(COUNT_DOWN_WATCH)) {
            int second = 7;
            boolean isOpenWatchUI = false;
            boolean isCountDownByWatch = false;
            CountDownSetting countDownSetting = new CountDownSetting(second, isOpenWatchUI, isCountDownByWatch);
            VPOperateManager.getMangerInstance(mContext).settingCountDown(writeResponse, countDownSetting, new ICountDownListener() {
                @Override
                public void OnCountDownDataChange(CountDownData countDownData) {
                    String message = "倒计时-watch:\n" + countDownData.toString();
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
        } else if (oprater.equals(AIM_SPROT_CALC)) {
            Intent intent = new Intent(OperaterActivity.this, AimSportCalcActivity.class);
            intent.putExtra("isnewsportcalc", isNewSportCalc);
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

        } else if (oprater.equals(READ_HEALTH_SLEEP)) {
            VPOperateManager.getMangerInstance(mContext).readSleepData(writeResponse, new ISleepDataListener() {
                        @Override
                        public void onSleepDataChange(SleepData sleepData) {
                            String message = "睡眠数据-返回:" + sleepData.toString();
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
                        public void onSleepDataChange(SleepData sleepData) {
                            String message = "睡眠数据-返回:" + sleepData.toString();
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
                public void onSleepDataChange(SleepData sleepData) {
                    String message = "睡眠数据-返回:" + sleepData.toString();
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
            VPOperateManager.getMangerInstance(mContext).readOriginData(writeResponse, new IOriginDataListener() {
                @Override
                public void onOringinFiveMinuteDataChange(OriginData originData) {
                    String message = "健康数据-返回:" + originData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgress(float progress) {
                    String message = "健康数据[5分钟]-读取进度:" + progress;
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginProgressDetail(int date, String dates, int all, int num) {

                }

                @Override
                public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                    String message = "健康数据[30分钟]-返回:" + originHalfHourData.toString();
                    Logger.t(TAG).i(message);
                }

                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            }, watchDataDay);
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
                public void onReadOriginProgressDetail(int date, String dates, int all, int num) {

                }

                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            }, yesterday, 10, watchDataDay);
        } else if (oprater.equals(READ_HEALTH_ORIGINAL_SINGLEDAY)) {
            int today = 0;
            VPOperateManager.getMangerInstance(mContext).readOriginDataSingleDay(writeResponse, new IOriginDataListener() {
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
                public void onReadOriginProgressDetail(int date, String dates, int all, int num) {

                }

                @Override
                public void onReadOriginComplete() {
                    String message = "健康数据-读取结束";
                    Logger.t(TAG).i(message);
                }
            }, today, 1, watchDataDay);
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
                public void onSleepDataChange(SleepData sleepData) {
                    String message = "onSleepDataChange:" + sleepData;
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

        } else if (oprater.equals(READ_SP)) {
            String shareperence = VPOperateManager.getMangerInstance(mContext).traversalShareperence();
            Logger.t(TAG).i(shareperence);
        }

    }

    @NonNull
    private Alarm2Setting getMultiAlarmSetting() {
        int hour = 2;
        int minute = 0;
        int scene = 1;
        boolean isOpen = true;
        String repestStr = "1000010";
        String unRepeatDdate = "0000-00-00";
        return new Alarm2Setting(hour, minute, repestStr, scene, unRepeatDdate, isOpen);
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
    class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("write cmd status:" + code);

        }
    }

    /**
     * 返回设备的数据
     */
    public void listenDeviceCallbackData() {
        VPOperateManager.getMangerInstance(mContext).listenDeviceCallbackData(new IBleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                Logger.t(TAG).i("设备返回的数据：" + Arrays.toString(VpBleByteUtil.byte2HexToStrArr(value)));
            }

        });
    }
}
