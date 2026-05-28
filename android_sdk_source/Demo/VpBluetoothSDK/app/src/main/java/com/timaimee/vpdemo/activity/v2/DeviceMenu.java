package com.timaimee.vpdemo.activity.v2;

public interface DeviceMenu {

    String[] HealthMenu = {Health.HealthData, Health.HeartRate, Health.BloodPress, Health.BloodGlucose, Health.BloodOxygen, Health.BodyComponent,
            Health.BloodComponent, Health.BodyTemperature, Health.Sleep, Health.MiniCheckUp, Health.ECG,
            Health.HRV, Health.Step, Health.DeviceManualTestData};

    interface Health {
        String HealthData = "健康数据（每五分钟测量一次）";
        String HeartRate = "心率";
        String BloodPress = "血压";
        String BloodGlucose = "血糖";
        String BloodOxygen = "血氧";
        String BodyComponent = "身体成分";
        String BloodComponent = "血液成分";
        String BodyTemperature = "体温";
        String Sleep = "睡眠";
        String MiniCheckUp = "微体检";
        String ECG = "ECG";
        String HRV = "HRV";
        String Step = "计步";
        String DeviceManualTestData = "手动测量数据";
    }

    String[] CustomMenu = {Custom.TCM, Custom.PSAIM};

    interface Custom {
        String TCM = "秋果中医诊断";
        String PSAIM = "QH15[PSAIM]健康数据";
    }

    interface Other {

    }

    interface Switch {

    }

}
