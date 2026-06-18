package com.timaimee.vpdemo.activity.v2;

public interface DeviceMenu {

    String[] HealthMenu = {Health.HealthData, Health.HeartRate, Health.BloodPress, Health.BloodGlucose, Health.BloodOxygen, Health.HRV,
            Health.BodyComponent, Health.BloodComponent, Health.BodyTemperature, Health.Sleep, Health.MiniCheckUp, Health.GSR, Health.ECG,
            Health.Step, Health.DeviceManualTestData, Health.Fatigue, Health.Female, Health.SEDENTARY_REMIND, Health.HEALTH_REMIND,
            Health.REMIND_EVENT, Health.AUTO_MEASURE};

    interface Health {
        String HealthData = "健康数据";
        String HeartRate = "心率❤️";
        String BloodPress = "血压";
        String BloodGlucose = "血糖";
        String BloodOxygen = "血氧";
        String HRV = "HRV";
        String BodyComponent = "身体成分";
        String BloodComponent = "血液成分";
        String BodyTemperature = "体温🌡️";
        String MiniCheckUp = "微体检";
        String GSR = "皮电";
        String ECG = "ECG";
        String Sleep = "睡眠";
        String Step = "计步";
        String Fatigue = "疲劳度";
        String Female = "女性";
        String DeviceManualTestData = "手动测量数据";
        String SEDENTARY_REMIND = "久坐提醒";
        String HEALTH_REMIND = "💚健康提醒";
        String REMIND_EVENT = "提醒事件";
        String AUTO_MEASURE = "自动测量";
    }

    String[] CustomMenu = {Custom.TCM, Custom.PSAIM, Custom.ZT163_DEVICE_ALWAYS_OFF_SCREEN, Custom.G15_IMG, Custom.QX17_DATA_ACQUISITION,
        Custom.G08W_HEALTH_ALARM_INTERVAL, Custom.G15_QR_CODE, Custom.JH58_PPG};

    interface Custom {
        String TCM = "秋果中医诊断";
        String PSAIM = "QH15[PSAIM]健康数据";
        String ZT163_DEVICE_ALWAYS_OFF_SCREEN = "合镁ZT163设备常灭屏";
        String G15_IMG = "G15图片传输";
        String QX17_DATA_ACQUISITION = "QX17数据采集";
        String G08W_HEALTH_ALARM_INTERVAL = "G08W-健康报警区间";
        String G15_QR_CODE = "G15医疗二维码";
        String JH58_PPG = "JH58PPG相关";
    }

    String[] OtherMenu = {Other.CONTACT, Other.ALARM, Other.MUSIC, Other.MESSAGE_PUSH, Other.IMG_TXT_PUSH, Other.WORLD_CLOCK, Other.WEATHER, Other.PHOTOGRAPH,
            Other.LANGUAGE_BATTER, Other.CHECK_WEAR, Other.DEVICE_ANTI_LOSS, Other.DEVICE_4G, Other.MAGNETIC};

    interface Other {
        String CONTACT = "联系人";
        String ALARM = "闹钟⏰️";
        String MUSIC = "音乐🎶";
        String MESSAGE_PUSH = "消息推送";
        String IMG_TXT_PUSH = "图文推送";
        String WORLD_CLOCK = "世界时钟⏰️";
        String WEATHER = "☀️⛈️❄️☔️\uD83C\uDF0A天气";
        String GNSS = "GNSS运动";
        String PHOTOGRAPH = "拍照📷";
        String LANGUAGE_BATTER = "🔋电量&语言";
        String CHECK_WEAR = "佩戴检测";
        String DEVICE_ANTI_LOSS = "设备防丢";
        String DEVICE_4G = "4G功能";
        String MAGNETIC = "磁疗";
    }

    String[] SwitchMenu = {Switch.MSG_PUSH, Switch.CUSTOM_SETTNG, Switch.HEALTH_SUPPORT, Switch.SWITCH_STATUS_LISTENER};

    interface Switch {
        String MSG_PUSH = "消息推送";
        String CUSTOM_SETTNG = "功能开关&单位";
        String HEALTH_SUPPORT = "健康辅助";
        String SWITCH_STATUS_LISTENER = "开关状态监听";
    }

    String[] ConnectionMenu = {Connection.BLE, Connection.BT};
    interface Connection {
        String BLE = "蓝牙低功耗";
        String BT = "经典蓝牙";
    }

}
