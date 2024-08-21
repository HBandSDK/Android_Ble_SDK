# Android SDK API Document


| Version | Modifications                                                | Modified Date |
| ------- | ------------------------------------------------------------ | ------------- |
| 1.0.0   | SDK initial release                                          | 2023.05.15    |
| 1.0.1   | Added K-series watch faces and OTA documentation             | 2023.05.26    |
| 1.0.2   | Add language setting function API                            | 2023.05.26    |
| 1.0.3   | Add contact function                                         | 2023.06.25    |
| 1.0.4   | 1. Added sports enumeration, added language enumeration<br />2. 30 minutes daily data added ''date'' return<br />3. Added the functions of adding contacts in batches and controlling the volume of the phone<br />4. Improve the description of OTA upgrade process | 2023.09.08    |
| 1.0.5   | Added OTA and power description for dial transmission        | 2023.09.11    |
| 1.0.6   | Added reading and setting of blood glucose multi-calibration mode | 2023.09.19    |
| 1.0.7   | 1. Twitter renamed to X (original Twitter)<br />2. Added body composition and blood composition related function description<br />3. Added blood composition detection switch, uric acid/blood lipid unit function setting<br />4. Added ecg reporting interface | 2023.09.27    |
| 1.0.8   | 1.Body composition increase unit display                     | 2023.10.25    |
| 1.0.9   | 1.Read body composition data interface returns the added measurement time, measurement seconds, etc. | 2023.11.24    |
| 1.1.0   | 1. Added instructions for obtaining blood sugar risk level;<br/>2. Added 05 flag judgment for body temperature reading. | 2024.04.15    |

## Public interface class
**VPOperateManager**（SDK main entry）  
Main operation class

**Get an instance**

```kotlin
VPOperateManager.getInstance()
```
Get the singleton object. The SDK interface exists in the form of a singleton.

Note: **The device does not support asynchronous operations. When multiple time-consuming operations are performed at the same time, data anomalies may occur. Therefore, when interacting with the device, try to avoid performing multiple operations at the same time**



## SDK Initialization

```kotlin
init(context)
```
| Parameter name | Type    | Describe                                    |
| -------------- | ------- | ------------------------------------------- |
| context        | Context | It is recommended to use ApplicationContext |

Note: All interfaces can only be called after the SDK is initialized. During the running of the App, it only needs to be initialized once, and there is no need to initialize it repeatedly.





## Scan Device

#### Start Scan

###### Interface

```kotlin
startScanDevice(searchResponse)
```
Start Bluetooth scanning, and non-company devices will be filtered out. If you need to stop scanning, call stopScan.

Note: When Bluetooth is turned off, the scanning interface will not take effect

###### Parameter Explanation

| Parameter name | Type           | Describe             |
| -------------- | -------------- | -------------------- |
| searchResponse | SearchResponse | Scan result callback |
###### Return data

**SearchResponse**--Scan result callback

```kotlin
/**
* Start scanning devices
*/
fun onSearchStarted()

/**
* Discover scanning devices
*
* @param device Currently discovered device
*/
fun onDeviceFounded(device:SearchResult)

/**
* Stop scanning device callback
*/
fun onSearchStopped()

/**
* Cancel scanning device callback
*/
fun onSearchCanceled()
```

**SearchResult**--Currently discovered devices

| Parameter name | Type            | Describe                      |
| -------------- | --------------- | ----------------------------- |
| device         | BluetoothDevice | Bluetooth device (system)     |
| rssi           | Int             | Bluetooth signal value rssi   |
| scanRecord     | byteArray       | Scanned device broadcast data |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance()
            .startScanDevice(object : SearchResponse {
                override fun onSearchStarted() {

                }

                override fun onDeviceFounded(p0: SearchResult) {
                    
                }

                override fun onSearchStopped() {
                    
                }

                override fun onSearchCanceled() {
                }
            })
```



#### End Scan

###### Interface

```kotlin
stopScanDevice()
```
###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().stopScanDevice()
```





## Connect

#### Connect device

```kotlin
connectDevice(mac,connectResponse,bleNotifyResponse)
```
###### Parameter Explanation

| Parameter name    | Type             | Describe                                                     |
| ----------------- | ---------------- | ------------------------------------------------------------ |
| mac               | String           | The address of the device to be connected                    |
| connectResponse   | IConnectResponse | Connection status callback, first return the connection status, after the connection is successful, it will return the Bluetooth communication status callback |
| bleNotifyResponse | INotifyResponse  | Bluetooth communication status callback, this callback is called after connectResponse |

###### Return data

**IConnectResponse** -- Connection status callback

```kotlin
/**
* Return of connection status
*
* @param code Connection status, only when the value is Code.REQUEST_SUCCESS, it means the connection is successful
* @param profile Bluetooth properties of the device
* @param isOadModel The device has two modes [normal mode/firmware upgrade mode]. In most cases, it is in normal mode. Only when the device fails to perform a firmware upgrade operation, it will enter the firmware upgrade mode
*/
fun connectState(code:Int, profile:BleGattProfile, isOadModel:boolean);
```

**INotifyResponse** - Set the callback for data monitoring

```kotlin
/**
* Set the return of data monitoring
*
* @param state is successful only when the value is equal to Code.REQUEST_SUCCESS
*/
fun notifyState(state:Int);
```

#### Verify password operation

###### Interface

```kotlin
confirmDevicePwd(bleWriteResponse,pwdDataListener,deviceFuctionDataListener，socialMsgDataListener，customSettingDataListener，pwd，mModelIs24)  
```

```kotlin
confirmDevicePwd(bleWriteResponse,pwdDataListener,deviceFuctionDataListener，socialMsgDataListener，customSettingDataListener，pwd，mModelIs24,deviceTimeSetting)
```

Note: **The first step after successful connection is to perform other Bluetooth operations only when [Connection is successful] and [Bluetooth communication is possible]**

###### Parameter Explanation 


| Parameter name            | Type                       | Describe                                                     |
|----------------	|----------------	|----------------	|
| bleWriteResponse 	| IBleWriteResponse 	| code Return Code.REQUEST_SUCCESS means that the command is sent to the device successfully, but the successful sending of the command does not necessarily mean that there will be no data returned. The successful return only means that the device has received the command. If the device cannot process the command, there may be no data returned. This interface is used by developers to find problems |
| pwdDataListener 	| IPwdDataListener 	| Password operation data return monitoring, the data returned here includes: device number, device release version number, device test version number, drinking data status, wrist flip screen status, phone search function status, wearing detection function status |
| deviceFuctionDataListener 	| IDeviceFuctionDataListener 	| The returned monitoring of the functions included in the device. The data returned here includes: The status of each device function [whether supported]: blood pressure, drinking, sedentary, high heart rate reminder, WeChat sports, shake-shake photography, fatigue, blood oxygen |
| socialMsgDataListener 	| ISocialMsgDataListener 	| The returned monitoring of phone calls, text messages, and social software messages. The data returned here includes: whether to support receiving reminders from social software, whether to open phone calls, text messages, and social software reminders |
| customSettingDataListener 	| ICustomSettingDataListener 	| Listening for Personalized Settings Operations |
| pwd 	| String 	| The password length is 4, the initial value is 0000, before entering the password, please make sure it is a 4-digit number |
| mModelIs24 	| boolean 	| Time format, if you choose to display the 24-hour system, pass true, if you choose the 12-hour system, pass false |
| deviceTimeSetting 	| DeviceTimeSetting 	| The default is the phone system time, you can customize the time, accurate to the second |

###### Data return

**IPwdDataListener** -- Data return

```kotlin
/**
* Returns the data of password operation
* @param pwdData Password operation data
*/
fun onPwdDataChange(pwdData:PwdData);
```

**PwdData** -- Data for Password verfiy operations

| Parameter name        | Type            | Describe                                                     |
| --------------------- | --------------- | ------------------------------------------------------------ |
| mStatus               | EPwdStatus      | Returns the status of the current cryptographic operation    |
| pwd                   | String          | Password                                                     |
| deviceNumber          | Int             | Device number                                                |
| deviceVersion         | String          | The official version of the device. The official version number is only displayed on the APP |
| deviceTestVersion     | String          | Device test version. The test version number will be used for firmware upgrades. The firmware upgrade requires the device number and the test version number. |
| isHaveDrinkData       | Boolean         | Is there any drinking data?                                  |
| isOpenNightTurnWriste | EFunctionStatus | Night-time wrist-raising screen-lighting function, supported/unsupported/on/off/unknown |
| findPhoneFunction     | EFunctionStatus | Find phone function, supported/unsupported/enabled/disabled/unknown |
| wearDetectFunction    | EFunctionStatus | Wearing monitoring function, support/not support/on/off/unknown |

**EFunctionStatus** -- Functional status

| Parameter name         | Describe   |
| ------------- | ------ |
| UNSUPPORT     | function unsupport |
| SUPPORT       | function support |
| SUPPORT_OPEN  | function open |
| SUPPORT_CLOSE | function close |
| UNKONW        | unknow state |

**IDeviceFuctionDataListener** -- Device function status monitoring, monitoring once, may be called back twice, depending on the device

```kotlin
/**
 * Returns device functional status
 *
 * @param functionSupport
 */
fun onFunctionSupportDataChange(functionSupport:FunctionDeviceSupportData)
```

**FunctionDeviceSupportData** --Each device function status [supported or not]

| Parameter name        | Type            | Describe                 |
| --------------------- | --------------- | ------------------------ |
| Bp | EFunctionStatus | Blood pressure function status |
| Drink | EFunctionStatus | Drinking function status |
| Longseat | EFunctionStatus | Sedentary function status |
| HeartWaring | EFunctionStatus | Heart rate warning function status |
| WeChatSport | EFunctionStatus | WeChat sports function status |
| Camera | EFunctionStatus | Photo function status |
| Fatigue | EFunctionStatus | Fatigue function status |
| SpoH | EFunctionStatus | Blood oxygen function status |
| SpoHAdjuster | EFunctionStatus | Blood oxygen calibration function status |
| SpoHBreathBreak | EFunctionStatus | Blood oxygen apnea reminder function status |
| Woman | EFunctionStatus | Female function status |
| Alarm2 | EFunctionStatus | New alarm function status |
| newCalcSport | EFunctionStatus | New function for step calculation |
| CountDown | EFunctionStatus | Countdown function status |
| AngioAdjuster | EFunctionStatus | Dynamic blood pressure adjustment function status |
| ScreenLight | EFunctionStatus | Screen brightness adjustment function status |
| HeartDetect | EFunctionStatus | Heart rate detection function status, enabled by default |
| SportModel | EFunctionStatus | Sport mode function status |
| NightTurnSetting | EFunctionStatus | Turn wrist to light up screen setting function status |
| hidFuction | EFunctionStatus | HID function status |
| screenStyleFunction | EFunctionStatus | Screen style function |
| beathFunction | EFunctionStatus | Respiration rate function |
| hrvFunction | EFunctionStatus | HRV function status |
| weatherFunction | EFunctionStatus | Weather function status |
| screenLightTime | EFunctionStatus | Screen on time function status |
| precisionSleep | EFunctionStatus | Precision sleep function status |
| resetData | EFunctionStatus | Reset device/data function status |
| ecg | EFunctionStatus | ECG function status |
| multSportModel | EFunctionStatus | Multi-sport function status |
| lowPower | EFunctionStatus | Low power function status |
| findDeviceByPhone | EFunctionStatus | Phone find device function status |
| agps | EFunctionStatus | AGPS function status |
| temperatureFunction | EFunctionStatus | Body temperature function status |
| textAlarm | EFunctionStatus | Text alarm function status |
| bloodGlucose | EFunctionStatus | Blood glucose function status |
| bloodGlucoseAdjusting | EFunctionStatus | Blood glucose calibration function |
| sleepTag | Int | Sleep flag |
| musicStyle | Int | Music band information 0x99, value 1 |
| WathcDay | Int | Maximum number of days the watch can save |
| contactMsgLength | Int | Contact message length |
| allMsgLength | Int | Maximum number of message reminder packets |
| sportmodelday | Int | Maximum number of days in sports mode |
| screenstyle | Int | Screen style selection |
| weatherStyle | Int | Weather type |
| originProtcolVersion | Int | Protocol version of original data |
| bitDataTranType | Int | Bulk data transmission type |
| watchUiServerCount | Int | Number of watch face markets |
| watchUiCoustomCount | Int | Number of custom watch faces |
| temptureType | Int | Temperature type |
| cpuType | Int | cpu type |
| ecgType | Int | ecg type |

**ISocialMsgDataListener** --Message notification switch callback monitoring Details return to view【[Message Notification](#Message Notification)】

**ICustomSettingDataListener** -- Personalized callback monitoring View details【[Personalization](#Personalization)】

###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance()
    .confirmDevicePwd({
        //                连接失败
        if (it != Code.REQUEST_SUCCESS) {
            Log.e("TAG", "confirmDevicePwd fail:check fail")
        }
    },
        {
            val message =
                "PwdData:$it"
            if (it.getmStatus() != EPwdStatus.CHECK_FAIL) {
               Log.e("TAG", "confirmDevicePwd fail:check fail")
            } else {
                Log.e("TAG", "confirmDevicePwd fail:check fail")
                VPOperateManager.getInstance()
                    .disconnectWatch { }
            }
        },
        {
            val message =
                "FunctionSupport:$it"
            Log.e("TAG", message)
        }, object : ISocialMsgDataListener {
            override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {
                val message =
                    "FunctionSocailMsgData:${p0.toString()}"
                Log.e("TAG", message)
            }

            override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {
                val message =
                    "FunctionSocailMsgData2:${p0.toString()}"
                Log.e("TAG", message)
            }

        }, customSettingDataListener, "0000", false
    )
```



## Disconnect Device

###### Interface

```
disconnectWatch(bleWriteResponse)
```

###### Parameter Explanation

| Parameter name   | Type              | Describe       |
| ---------------- | ----------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().disconnectWatch {

}
```





## Synchronize personal information

The settings of height and weight will affect the calorie calculation results

###### Interface

```kotlin
syncPersonInfo(bleWriteResponse, personInfoDataListener, personInfoData)
```

###### Parameter Explanation

| Parameter name         | Type                    | Describe                                                     |
| ---------------------- | ----------------------- | ------------------------------------------------------------ |
| bleWriteResponse       | IBleWriteResponse       | Listening for write operations                               |
| personInfoDataListener | IPersonInfoDataListener | The callback of personal information operation. The returned data only contains the status of the operation. |
| personInfoData         | PersonInfoData          | Personal information data                                    |

**PersonInfoData** --Personal information data

| Parameter name | Type | Describe             |
| -------------- | ---- | -------------------- |
| ESex | ESex | Gender |
| height | Int | Height |
| age | Int | Age |
| stepAim | Int | Target number of steps |
| sleepAim | Int | Target sleep time (minutes) |

###### Return data

**IPersonInfoDataListener** -- Callback for personal information operations

```kotlin
/**
* Return the status of the operation
*
* @param EOprateStauts: OPRATE_SUCCESS: operation successful, OPRATE_FAIL: operation failed, UNKNOW: unknown
*/
fun OnPersoninfoDataChange(eOprateStauts:EOprateStauts)
```

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().syncPersonInfo(writeResponse,
    { EOprateStauts ->
        val message = "syncPersonInfo:\n$EOprateStauts"

    }, PersonInfoData(ESex.MAN, 178, 60, 20, 8000)
)
```





## Language setting function

###### Interface

```kotlin
settingDeviceLanguage(bleWriteResponse, languageDataListener, language)
```

###### Parameter Explanation

| Parameter name       | Type                  | Describe       |
| -------------------- | --------------------- | -------------- |
| bleWriteResponse     | IBleWriteResponse     | Listening for write operations |
| languageDataListener | ILanguageDataListener | Set language callback |
| language             | ELanguage             | Types of languages |

**ELanguage** Language Enumeration

| Parameter name             | Describe       |
| ----------------- | ---------- |
| CHINA | Chinese Simplified |
| CHINA_TRADITIONAL | Chinese Traditional |
| ENGLISH | English |
| JAPAN | Japanese |
| KOREA | Korean |
| DEUTSCH | German |
| RUSSIA | Russian |
| SPANISH | Spanish |
| ITALIA | Italian |
| FRENCH | French |
| VIETNAM | Vietnamese |
| PORTUGUESA | Portuguese |
| THAI | Thai |
| POLISH | Polish |
| SWEDISH | Swedish |
| TURKISH | Turkish |
| DUTCH | Dutch |
| CZECH | Czech |
| ARABIC | Arabic |
| HUNGARY | Hungarian |
| GREEK | Greek |
| ROMANIAN | Romanian |
| SLOVAK | Slovak |
| INDONESIAN | Indonesian |
| BRAZIL_PORTUGAL | Brazilian Portuguese |
| CROATIAN | Croatian |
| LITHUANIAN | Lithuanian |
| UKRAINE | Ukrainian |
| HINDI | Hindi |
| HEBREW | Hebrew |
| DANISH | Danish |
| PERSIAN | Persian |
| FINNISH | Finnish |
| MALAY | Malay |
| UNKONW | Unknown |

###### Return data

**ILanguageDataListener** --Set language callback

```kotlin
/**
* Returns the state of the language
*
* @param languageData language data
*/
fun onLanguageDataChange( languageData:LanguageData)
```

**languageData ** -- Language data

| Parameter name | Type          | Describe                               |
| -------------- | ------------- | -------------------------------------- |
| stauts         | EOprateStauts | Status of the operation                |
| language       | ELanguage     | Get the language of the current device |

**Note:**

[System language] &&[Female function prompt language] are not consistent on the device. Female function supports multiple languages, while system language is only Chinese and English. Therefore, the following normal situation may occur (on a device that only supports Chinese and English [System language], when setting Japanese, the device will prompt that Japanese setting is successful, and [System language] is still displayed as English, but [Female function prompt language] is Japanese)





## Read current step count

Read current step count. Step count involves distance and calorie calculations, which are related to height. Therefore, before reading step count, you should first call [[Synchronize Personal Information](#Synchronize Personal Information)] to set personal information

**Note**: This refers to the device's step count, distance, and calories. The data returned by this interface is real-time, which is different from the number of steps in daily data. The number of steps in daily data is a summary every 5 minutes, which has a lag. If the application layer needs to synchronously obtain the number of steps on the device side, it needs to call this interface at a fixed frequency to obtain data.

###### Interface

```kotlin
readSportStep(bleWriteResponse, sportDataListener)
```

###### Parameter Explanation

| Parameter name    | Type               | Describe         |
| ----------------- | ------------------ | ---------------- |
| bleWriteResponse  | IBleWriteResponse  | Listening for write operations   |
| sportDataListener | ISportDataListener | Read motion data monitoring |

###### Return data

**ISportDataListener** -- Read motion data monitoring

```kotlin
/**
* Returns step counting data
*
* @param sportData Current sport data
*/
fun onSportDataChange(sportData:SportData)
```

**SportData** -- Current exercise data

| Parameter name                  | Type   | Describe                                                     |
| ------------------------------- | ------ | ------------------------------------------------------------ |
| step | Int | Current step count |
| dis | Double | Current distance in km |
| kcal | Double | Current calories in kcal |
| calcType | Int | Calculation method, 0 indicates the traditional algorithm, 1 indicates the new algorithm formula, and 2 indicates the table of the motion mode table |
| triaxialX, triaxialY, triaxialZ | Int | Triaxial position |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().readSportStep({
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}) { sportData ->  }
```





## Read device power

1. Special attention⚠️: The dial transmission and OTA process will consume a lot of power, and the power limit needs to be increased. When the watch battery is very low, if the dial transmission is initiated, the watch may shut down due to low power during the transmission process. After recharging, the dial will turn black. It is recommended to read the current power of the watch before each transmission. If the power status is low, the transmission is prohibited.

The OTA process is long, and it is recommended that the battery power is above 30% before the upgrade is allowed.

###### Prerequisites

The device is connected

###### Interface

```kotlin
readBattery(bleWriteResponse, batteryDataListener)
```

###### Parameter Explanation

| Parameter name      | Type                 | Describe       |
| ------------------- | -------------------- | -------------- |
| bleWriteResponse    | IBleWriteResponse    | Listening for write operations |
| batteryDataListener | IBatteryDataListener | Read power monitoring |

###### Return data

**IBatteryDataListener** -- Read power monitoring

```kotlin
/**
 * 返回电量的数据
 */
fun onDataChange(batteryData:BatteryData)
```

**BatteryData** -- Power data

| Parameter name | Type    | Describe                                                     |
| -------------- | ------- | ------------------------------------------------------------ |
| batteryLevel | Int | Current battery level of the device [1-4], 4 represents full power |
| batteryPercent | Int | Battery percentage [1-100] |
| powerModel | Int | Power mode: 0x00 normal, 0x01 charging state, 0x02 low voltage state, 0x03 full power state |
| state | Int | Sleep state: 0 wake up 1 sleep |
| bat | Byte | Current battery level of BAT |
| isLowBattery | Boolean | Is the battery level low: 0x01 represents normal, 0x02 represents low battery |
| isPercent | Boolean | Whether the battery percentage can be displayed, true represents the battery level in batteryPercent, false represents the battery level in batteryLevel

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().readBattery({
    if (it != Code.REQUEST_SUCCESS) { 
        
    }
}, {
    
})
```





## Read daily data function

#### Read health data（Sleep data + 5 minutes of original data)

###### Interface

If the watch stores data for 3 days, read all the health data in the order of [sleep data (today - yesterday - day before yesterday)] - [5 minutes of raw data (today - yesterday - day before yesterday)], then you can customize the position of the day and the position of the number of items for the raw data. For example, if you pass in [yesterday, 150], then the reading order is [sleep data (today - yesterday - day before yesterday)] - [5 minutes of raw data (yesterday (150) - day before yesterday)]

```
readAllHealthDataBySettingOrigin(allHealthDataListener,day,position,watchday)
```

###### Parameter Explanation

| Parameter name        | Type                   | Describe                                                     |
| --------------------- | ---------------------- | ------------------------------------------------------------ |
| allHealthDataListener | IAllHealthDataListener | Callback for reading all data, returning the reading progress and health data: sleep data, step data [5 minutes, 30 minutes], heart rate data [5 minutes, 30 minutes], blood pressure data [5 minutes, 30 minutes] |
| watchday              | Int                    | The data capacity that the watch can store (unit: day) depends on the device. After verifying the password, you can get the return value through getWatchday() in the IDeviceFuctionDataListener callback data FunctionDeviceSupportData. |
| day                   | Int                    | Read the day, 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on. |
| position              | Int                    | The position of reading the number of records. The maximum number of records per day is 288 (5 minutes per record). You can define the position of the number of records to be read. The value of this parameter must be greater than or equal to 1. |

###### Return data

IAllHealthDataListener

```kotlin
/**
* Read progress callback
*
* @param progress progress value, range [0-1]
*/
void onProgress(float progress);

/**
* Returns sleep data
*
* @param day indicates the day of the data being read 0: today, 1: yesterday, 2: the day before yesterday.
* @param sleepData sleep data
*/
void onSleepDataChange(String day, SleepData sleepData);

/**
* callback for reading the end of sleep
*/
void onReadSleepComplete();

/**
* callback for reading 5 minutes of raw data
*
* @param originData 5 minutes of raw data
*/
void onOringinFiveMinuteDataChange(OriginData originData);

/**
* callback for 30 minutes of raw data, from 5 minutes of raw data, but data processing is done internally
*
* @param originHalfHourData
*/
void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData);

/**
* end of reading all data
*/
void onReadOriginComplete();
```

**SleepData** -- Sleep data

| Parameter name | Type     | Describe                                                     |
| -------------- | -------- | ------------------------------------------------------------ |
| Date | String | Sleep date |
| cali_flag | Int | Sleep calibration value, currently this value is useless |
| sleepQulity | Int | Sleep quality |
| wakeCount | Int | Number of times you wake up during sleep |
| deepSleepTime | Int | Deep sleep duration (in min) |
| lowSleepTime | Int | Light sleep duration (in min) |
| allSleepTime | Int | Total sleep duration |
| sleepLine | String | Sleep curve is mainly used to display sleep status in a more concrete UI. If your sleep interface has no special requirements for UI, you can ignore it. Sleep curve is divided into normal sleep and precise sleep. Normal sleep is a string composed of 0, 1, 2. Each character represents a duration of 5 minutes, where 0 represents light sleep, 1 represents deep sleep, and 2 represents awakening. For example, "201112" has a length of 6, which means a total of 30 minutes of sleep, 5 minutes of awakening at the beginning and end, 5 minutes of light sleep in the middle, and 15 minutes of deep sleep. For precise sleep, the sleep curve is a string composed of 0, 1, 2, 3, 4. Each character represents a duration of 1 minute, where 0 represents deep sleep, 1 represents light sleep, 2 represents rapid eye movement, 3 represents insomnia, and 4 represents awakening. |
| sleepDown | TimeData | Time to fall asleep |
| sleepUp | TimeData | Time to wake up |

**OriginData** -- 5 minutes of original data

| Parameter name  | Type     | Describe                                                     |
| --------------- | -------- | ------------------------------------------------------------ |
| date | String | Sleep date |
| allPackage | Int | Total number of packages of data for the day |
| packageNumber | Int | The location of this data on the day |
| mTime | TimeData | Precise time |
| rateValue | Int | Heart rate value, range [30-200] |
| sportValue | Int | Exercise value [0-65536], the larger the value, the more intense the exercise. It is divided into 5 levels, namely [0-220], [201-700], [701-1400], [1401-3200], [3201-65535] |
| stepValue | Int | Step count value |
| highValue | Int | High pressure value, range [60-300] |
| lowValue | Int | Low pressure value, range [20-200] |
| wear | Int | Wear flag |
| tempOne | Int | Reserved value |
| tempTwo | Int | Reserved value |
| calValue | Double | Calories consumed |
| disValue | Double | Total distance in km |
| calcType | Int | Calculation method: 0 indicates the traditional algorithm, 1 indicates the new algorithm formula, and 2 indicates the device in exercise mode |
| drankPartOne | String | Temporarily reserved |
| baseTemperature | Double | Body surface temperature, the value is valid when VpSpGetUtil.isSupportReadTempture && VpSpGetUtil.getTemperatureType == |
| temperature | Double | Body temperature, the value is valid when VpSpGetUtil.isSupportReadTempture && VpSpGetUtil.getTemperatureType == |

**OriginHalfHourData** -- half hour of original data

| Parameter name     | Type                    | Describe                                      |
| ------------------ | ----------------------- | --------------------------------------------- |
| halfHourRateDatas | List<HalfHourRateData> | 30 minutes of heart rate data |
| halfHourBps | List<HalfHourBpData> | 30 minutes of blood pressure data |
| halfHourSportDatas | List<HalfHourSportData> | 30 minutes of exercise data |
| allStep | Int | Total number of steps in the current 30 minutes (5*6) of raw data |
| date | String | Date ("yyyy-MM-dd HH:mm:ss) |

**HalfHourRateData** -- half hour of heart rate data

| Parameter name | Type     | Describe                                                     |
| -------------- | -------- | ------------------------------------------------------------ |
| date | String | Date |
| time | TimeData | Specific time, up to the minute, such as 10:00 means the average value of the period 10:00-10:30 |
| rateValue | Int | Heart rate value |
| ecgCount | Int | Total ecg counts |
| ppgCount | Int | Total ppg counts                                                    |

**HalfHourBpData**-- 30-minute blood pressure data

| Parameter name | Type     | Describe                                                     |
| -------------- | -------- | ------------------------------------------------------------ |
| date | String | The date |
| time | TimeData | The specific time, which can be accurate to the minute at most. For example, 10:00 means the average value of the period from 10:00 to 10:30 |
| highValue | Int | The highest blood pressure value |
| lowValue | Int | The lowest blood pressure value                                                |

**HalfHourSportData** -- 30-minute sport data

| Parameter name | Type     | Describe                                                     |
| -------------- | -------- | ------------------------------------------------------------ |
| date | String | Date |
| time | TimeData | Specific time, up to the minute, such as 10:00 means the average value of the period 10:00-10:30 |
| sportValue | Int | Total exercise in 30 minutes |
| disValue | Double | Total distance in 30 minutes |
| calValue | Double | Total calories in 30 minutes |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().readAllHealthData(object :IAllHealthDataListener{
    override fun onProgress(progress: Float) {
		
    }

    override fun onSleepDataChange(day: String?, sleepData: SleepData?) {

    }

    override fun onReadSleepComplete() {

    }

    override fun onOringinFiveMinuteDataChange(originData: OriginData?) {

    }

    override fun onOringinHalfHourDataChange(originHalfHourData: OriginHalfHourData?) {

    }

    override fun onReadOriginComplete() {

    }

},3)
```



#### Read daily data (5 minutes of original data)

###### Interface

If the watch stores data for 3 days, read the original data every 5 minutes, including steps, heart rate, blood pressure, exercise volume, and read in the order of today-yesterday-the day before yesterday. Theoretically, there are 288 data in one day.

```kotlin
readOriginData(bleWriteResponse, originDataListener, watchday)
```

Read the original data. This method allows you to customize which day to read and which record to start reading from, and whether to read only the current day.

```
readOriginDataBySetting(bleWriteResponse, originDataListener,readOriginSetting)
```

Read raw data. This method allows you to customize the reading date and the number of records to start reading to avoid duplicate reading. For example, if [yesterday, 150] is set, the reading order is [yesterday {150} - end of yesterday - the day before yesterday]

```kotlin
readOriginDataFromDay(bleWriteResponse, originDataListener, day, position, watchday)
```

Read the raw data of a single day. This method allows you to customize the day to read and the number of records to start reading from, and only read the current day. For example, if [yesterday, 150] is set, the reading order is [yesterday {150} - end of yesterday]

```kotlin
readOriginDataSingleDay(bleWriteResponse, originDataListener, day, position, watchday)
```

###### Parameter Explanation

| Parameter name     | Type                                     | Describe                                                     |
| ------------------ | ---------------------------------------- | ------------------------------------------------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| originDataListener | IOriginDataListener/IOriginData3Listener | Callback of raw data. The returned data includes steps, heart rate, blood pressure, and exercise volume. |
| watchday | Int | Data storage capacity of the watch (unit: day) |
| day | Int | Read position. 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on. The reading order is today-yesterday-the day before yesterday |
| position | Int | The number of read positions. This value must be greater than or equal to 1 |
| readOriginSetting | ReadOriginSetting | Read raw data settings                                         |

Note: **When reading daily data, you need to determine the device protocol version. When the device protocol version is 3 or 5, you need to pass in IOriginData3Listener. In other cases, use IOriginDataListener**

The judgment conditions are as follows:

```kotlin
val originProtocolVersion = VpSpGetUtil.getVpSpVariInstance(mContext).getOriginProtocolVersion()
if(originProtocolVersion==3 || originProtocolVersion == 5){
//Read daily data and use IOriginData3Listener for data monitoring
}else{
//Read daily data and use IOriginDataListener for data monitoring
}
```

**ReadOriginSetting** -- Reading original data settings

| Parameter name | Type    | Describe                                                     |
| -------------- | ------- | ------------------------------------------------------------ |
| day | Int | Read position, 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on. The reading order is today-yesterday-the day before yesterday |
| position | Int | The position of the number of reads, this value must be greater than or equal to 1 |
| onlyReadOneDay | Boolean | true means read only today, false means read in order |
| watchday | Int | The number of days stored in the watch |

###### Return data

**IOriginDataListener** -- Daily data return monitoring

```kotlin
/**
* Return 5 minutes of raw data
*
* @param originData 5 minutes of raw data
*/
fun onOringinFiveMinuteDataChange(originData:OriginData)

/**
* Return 30 minutes of raw data, the data comes from 5 minutes of raw data, but it is returned after internal processing
*
* @param originHalfHourData 30 minutes of raw data
*/
fun onOringinHalfHourDataChange(originHalfHourData:OriginHalfHourData )

/***
* Return the details of the reading. The location of this package needs to be remembered. When reading data next time, pass in the location of this package to avoid repeated reading
*
* @param day The flag of the data in the watch [0=today, 1=yesterday, 2=the day before yesterday]
* @param date The date of the data, in the format of yyyy-mm-dd
* @param allPackage The total number of packages of data on the day
* @param currentPackage The location of this package
*/
fun onReadOriginProgressDetail(day: Int,date: String?,allPackage: Int,currentPackage: Int)

/**
* Returns the reading progress
*
* @param progress progress value, range [0-1]
*/
fun onReadOriginProgress(progress:Float)

/**
* Reading completed
*/
fun onReadOriginComplete();
```

**IOriginData3Listener** -- Daily data return listener (inherited from IOriginDataListener)

```kotlin
/**
* This interface will be called back after the data reading for that day is completed. (An OriginData3 data represents a five-minute raw data, and a day is at most 24 hours * 60 minutes / 5 minutes = 288 five-minute raw data)
* For example, reading three days of raw data will return a list of five-minute raw data from today, yesterday, and the day before yesterday.
* Specifically, it depends on how many days of raw data are read. The interface will be called several times for each day of reading.
*
* @param originDataList returns a list of 5-minute data. The heart rate value is an array. The corresponding field is getPpgs(), not getRateValue().
*/
fun onOriginFiveMinuteListDataChange(originDataList:List<OriginData3>)

/**
* When the raw data of the day is finished reading, the five-minute raw data bureau list is counted to produce a 30-minute data list.
*
* @param originHalfHourDataList returns an object of 30-minute data.
*/
fun onOriginHalfHourDataChange(originHalfHourDataList:OriginHalfHourData)

/**
* After reading the original data of the day, the statistics are generated to generate the HRV data list of the day
*
* @param originHrvDataList returns a list of hrv data
*/
fun onOriginHRVOriginListDataChange(originHrvDataList:List<HRVOriginData>)

/**
* After reading the original data of the day, the statistics are generated to generate the blood oxygen data list of the day
*
* @param originSpo2hDataList returns a list of blood oxygen data
*/
fun onOriginSpo2OriginListDataChange(originSpo2hDataList:List<Spo2hOriginData>)
```

**OriginData3** -- 5-minute daily data (inherited from OriginData, with more data returned)

| Parameter name | Type           | Describe             |
| -------------- | -------------- | -------------------- |
| gesture | IntArray | Wearing gesture type |
| ppgs | IntArray | 5-minute pulse rate value |
| ecgs | IntArray | 5-minute heart rate value |
| resRates | IntArray | 5-minute respiration rate value |
| sleepStates | IntArray | 5-minute sleep state value |
| oxygens | IntArray | 5-minute blood oxygen value |
| apneaResults | IntArray | Apnea times array |
| hypoxiaTimes | IntArray | Hypoxia time array |
| cardiacLoads | IntArray | Cardiac load array |
| isHypoxias | IntArray | Apnea results array |
| corrects | IntArray | Blood oxygen correction value string array |
| bloodGlucose | Int | Blood glucose value |
| bloodComponent | BloodComponent | Blood components |
| | | |

**BloodComponent** --Blood components

| Parameter name | Type  | Describe                                                     |
| -------------- | ----- | ------------------------------------------------------------ |
| uricAcid | Float | Uric acid value: unit μmol/L, value range [90.0, 1000.0], reported value range [900, 10000] |
| tCHO | Float | Total cholesterol: unit mmol/L, value range [0.01, 100.00], reported value range [1, 10000] |
| tAG | Float | Triglyceride: unit mmol/L, value range [0.01, 100.00] |
| hDL | Float | High-density lipoprotein: unit mmol/L, value range [0.01, 100.00] |
| lDL | Float | Low-density lipoprotein: unit mmol/L, value range [0.01, 100.00] |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().readOriginData({
                                              
},object :IOriginDataListener{
    override fun onReadOriginProgressDetail(
        day: Int,
        date: String?,
        allPackage: Int,
        currentPackage: Int
    ) {
    }

    override fun onReadOriginProgress(progress: Float) {
    }

    override fun onReadOriginComplete() {
    }

    override fun onOringinFiveMinuteDataChange(originData: OriginData?) {
    }

    override fun onOringinHalfHourDataChange(originHalfHourData: OriginHalfHourData?) {
    }

},3)
```





## Personalization

#### Read Personalization Settings

Read Personalization Settings,Personalized settings include the metric system function, the metric system switch status, the time system status, the automatic heart rate detection switch status, the automatic blood pressure detection switch status, etc.

```kotlin
readCustomSetting(bleWriteResponse,customSettingDataListener)
```

###### Parameter Explanation

| Parameter name            | Type                       | Describe                                                     |
| ------------------------- | -------------------------- | ------------------------------------------------------------ |
| bleWriteResponse          | IBleWriteResponse          | Listening for write operations                               |
| customSettingDataListener | ICustomSettingDataListener | Listen to the personalized settings operation and return the personalized settings data |

###### Return data

ICustomSettingDataListener

```kotlin
/**
* Returns the personalized setting data
*
* @param customSettingData Personalized setting data
*/
fun OnSettingDataChange(customSettingData: CustomSettingData?)
```

**CustomSettingData**

| Parameter name        | Type              | Describe                                                     |
| --------------------- | ----------------- | ------------------------------------------------------------ |
| status | ECustomStatus | Get the status of the operation |
| is24Hour | boolean | Is the time format 24 hours? |
| metricSystem | EFunctionStatus | Metric and imperial: [SUPPORT_OPEN for metric, SUPPORT_CLOSE for imperial, UNSOUPRT for unsupported] |
| autoHeartDetect | EFunctionStatus | Automatic heart rate measurement |
| autoBpDetect | EFunctionStatus | Automatic blood pressure detection |
| sportOverRemain | EFunctionStatus | Excessive exercise reminder |
| voiceBpHeart | EFunctionStatus | Blood pressure/heart rate broadcast |
| findPhoneUi | EFunctionStatus | Control the search for mobile phone UI |
| secondsWatch | EFunctionStatus | Stopwatch |
| lowSpo2hRemain | EFunctionStatus | Low oxygen alarm |
| skin | EFunctionStatus | Skin color function |
| autoHrv | EFunctionStatus | HRV automatic detection |
| autoIncall | EFunctionStatus | Automatic answering of incoming calls |
| disconnectRemind | EFunctionStatus | Disconnection reminder |
| SOS | EFunctionStatus | SOS |
| ppg | EFunctionStatus | ppg function: automatic pulse rate monitoring--> scientific sleep--> ppg |
| musicControl | EFunctionStatus | Music control |
| longClickLockScreen | EFunctionStatus | Long press lock screen |
| messageScreenLight | EFunctionStatus | Message screen light function |
| autoTemperatureDetect | EFunctionStatus | Automatic temperature detection |
| temperatureUnit | ETemperatureUnit | Temperature unit setting |
| ecgAlwaysOpen | EFunctionStatus | ecg always open |
| bloodGlucoseDetection | EFunctionStatus | Blood glucose detection |
| METDetect | EFunctionStatus | MET detection |
| stressDetect | EFunctionStatus | Stress detection |
| bloodGlucoseUnit | EBloodGlucoseUnit | Blood glucose unit |
| skinLevel | Int | Skin color level |
| bloodComponentDetect | EFunctionStatus | Blood component switch |
| uricAcidUnit | EUricAcidUnit | Uric acid unit |
| bloodFatUnit | EBloodFatUnit | Blood fat unit |

**EBloodGlucoseUnit**

| Parameter name | Describe                       |
| ------ | -------------------------- |
| NONE   | No unit, indicating that unit settings are not supported |
| mmol_L | mmol/L                     |
| mg_dl  | mg/dl                      |

**EUricAcidUnit**

| Parameter name | Describe                       |
| ------ | -------------------------- |
| NONE   | No unit, indicating that unit settings are not supported |
| umol_L | umol/L                     |
| mg_dl  | mg/dl                      |

**EBloodFatUnit**

| Parameter name | Describe                       |
| ------ | -------------------------- |
| NONE   | No unit, indicating that unit settings are not supported |
| mmol_L | mmol/L                     |
| mg_dl  | mg/dl                      |

**ETemperatureUnit**

| Parameter name     | Describe                       |
| ---------- | -------------------------- |
| NONE       | No unit, indicating that unit settings are not supported |
| CELSIUS    | Celsius                     |
| FAHRENHEIT | Fahrenheit                     |

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().readCustomSetting({

},object :ICustomSettingDataListener{
    override fun OnSettingDataChange(customSettingData: CustomSettingData?) {

    }

})
```



#### Modify personalization settings

Modify personalization settings,Personalized settings include the metric system function, the metric system switch status, the time system status, the automatic heart rate detection switch status, the automatic blood pressure detection switch status, etc.

```kotlin
changeCustomSetting(bleWriteResponse, customSettingDataListener, customSetting)
```

###### Parameter Explanation

| Parameter name            | Type                       | Describe                                                     |
| ------------------------- | -------------------------- | ------------------------------------------------------------ |
| bleWriteResponse          | IBleWriteResponse          | Listening for write operations                               |
| customSettingDataListener | ICustomSettingDataListener | Listen to the personalized settings operation and return the personalized settings data |
| customSetting             | CustomSetting              | Personalized settings data                                   |

**CustomSetting**

| Parameter name                      | Type              | Describe                                                         |
| --------------------------- | ----------------- | ------------------------------------------------------------ |
| isHaveMetricSystem | boolean | Set the function status of the metric system. Return true to indicate that this function is available and the metric system can be set; return false to indicate that this function is not available and the metric system cannot be set |
| isMetricSystem | boolean | Set the value of the metric system. Return true to indicate the metric system and return false to indicate the imperial system. The device language must be set to [English or Traditional] to reflect the imperial system |
| is24Hour | boolean | Set the value of the time system. Return true to indicate the 24-hour system and false to indicate the 12-hour system |
| isOpenAutoHeartDetect | boolean | Set the status of automatic heart rate measurement. Return true to indicate that the automatic heart rate measurement function is turned on and return false to indicate that the automatic heart rate measurement function is turned off |
| isOpenAutoBpDetect | boolean | Set the status of automatic blood pressure measurement. Return true to indicate that the automatic blood pressure measurement function is turned on and return false to indicate that the automatic blood pressure measurement function is turned off |
| temperatureUnit | ETemperatureUnit | Set the temperature unit |
| isOpenSportRemain | EFunctionStatus | Set the status of excessive exercise, SUPPORT_OPEN means the excessive exercise reminder function is turned on, SUPPORT_CLOSE means the excessive exercise reminder function is turned off; UNSUPPORT means not supported |
| isOpenVoiceBpHeart | EFunctionStatus | Set the status of heart rate/blood oxygen/blood pressure, SUPPORT_OPEN means the heart rate/blood oxygen/blood pressure broadcast function is turned on, SUPPORT_CLOSE means the heart rate/blood oxygen/blood pressure broadcast function is turned off; UNSUPPORT means not supported |
| isOpenFindPhoneUI | EFunctionStatus | Set the status of mobile phone search, SUPPORT_OPEN means the mobile phone search function is turned on, SUPPORT_CLOSE means the mobile phone search function is turned off; UNSUPPORT means not supported |
| isOpenStopWatch | EFunctionStatus | Set whether to turn on the stopwatch function, SUPPORT_OPEN means the stopwatch function is turned on, SUPPORT_CLOSE means the stopwatch function is turned off; UNSUPPORT means not supported |
| isOpenSpo2hLowRemind | EFunctionStatus | Set low oxygen reminder, SUPPORT_OPEN means low oxygen reminder function is turned on, SUPPORT_CLOSE means low oxygen reminder function is turned off; UNSUPPORT means not supported |
| isOpenWearDetectSkin | EFunctionStatus | Set skin color wear monitoring, SUPPORT_OPEN means white skin color, SUPPORT_CLOSE means black skin color; UNSUPPORT means not supported |
| isOpenAutoHRV | EFunctionStatus | Set HRV automatic detection function |
| isOpenAutoInCall | EFunctionStatus | Set automatic answer call function |
| isOpenDisconnectRemind | EFunctionStatus | Set disconnection reminder function |
| isOpenSOS | EFunctionStatus | Set SOS function |
| isOpenAutoTemperatureDetect | EFunctionStatus | Set body temperature automatic detection function |
| ecgAlwaysOpen | EFunctionStatus | Set ecg normally open function |
| METDetect | EFunctionStatus | Set met detection function |
| stressDetect | EFunctionStatus | Set stress detection function |
| isOpenPPG | EFunctionStatus | Set ppg function, ppg switch is also a precise sleep switch |
| isOpenMusicControl | EFunctionStatus | Set music control function |
| isOpenLongClickLockScreen | EFunctionStatus | Set long press lock screen |
| isOpenMessageScreenLight | EFunctionStatus | Set message screen light |
| isOpenBloodGlucoseDetect | EFunctionStatus | Set blood sugar automatic detection function |
| bloodGlucoseUnit | EBloodGlucoseUnit | Set blood sugar unit |
| isOpenBloodComponentDetect | EFunctionStatus | Blood component automatic detection function |
| uricAcidUnit | EUricAcidUnit | Uric acid unit setting |
| bloodFatUnit | EBloodFatUnit | Blood fat unit settings |

Note: **If you want to set a function on or off, you need to read Personalization Settings first to determine whether the function is supported. If it is supported, you can set the on/off status. If it is not supported, you still need to send the unsupported command. **

###### Return data

ICustomSettingDataListener  Same as the data returned by [[Read Personalization Settings](#Read Personalization Settings-readCustomSetting)]

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().changeCustomSetting({

},object :ICustomSettingDataListener{
    override fun OnSettingDataChange(customSettingData: CustomSettingData?) {

    }

},customSetting)
```





## Flip wrist to light up the screen

#### Premise

The device must support the wrist-turning screen-lighting function, and the judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportNightturnSetting
```

#### Read wrist flip screen

The device needs to support the wrist-turning screen-lighting function

###### Interface

```
readNightTurnWriste(bleWriteResponse, nightTurnWristeDataListener)
```

###### Parameter Explanation

| Parameter name              | Type                         | Describe         |
| --------------------------- | ---------------------------- | ---------------- |
| bleWriteResponse            | IBleWriteResponse            | Listening for write operations   |
| nightTurnWristeDataListener | INightTurnWristeDataListener | Flip your wrist to light up the screen and monitor data |

###### Return data

**INightTurnWristeDataListener** -- Flip your wrist to light up the screen and monitor data

```kotlin
/**
* Returns the data of turning the wrist to light up the screen [also known as raising the hand to light up the screen]
*
* @param nightTurnWristeData Data of raising the hand to light up the screen
*/
fun onNightTurnWristeDataChange(nightTurnWristeData:NightTurnWristeData)
```

**NightTurnWristeData** -- Data on screen lighting when hand is raised

| Parameter name             | Type                   | Describe                           |
| -------------------------- | ---------------------- | ---------------------------------- |
| OprateStauts | ENightTurnWristeStatus | Status of wrist-turning screen lighting |
| isSupportCustomSettingTime | Boolean | Whether to support custom time settings, true means support |
| nightTureWirsteStatusOpen | Boolean | Whether wrist-turning screen lighting is turned on |
| startTime | TimeData | Start time |
| endTime | TimeData | End time |
| level | Int | Sensitivity level |
| defaultLevel | Int | Default level |

**ENightTurnWristeStatus** -- Status

| Parameter name | Type     |
| -------------- | -------- |
| SUCCESS | Success |
| FAIL | Failure |
| UNKONW | Unknown status |

###### Example Code

```kotlin
VPOperateManager.getInstance().readNightTurnWriste({
    if (it != Code.REQUEST_SUCCESS) {
        // "write cmd failed"
    }
}, { nightTurnWristeData ->
    //success
})
```



#### Set the screen to turn on by wrist flip

The device needs to support the wrist-turning screen-lighting function

###### Interface

```
settingNightTurnWriste(IBleWriteResponse bleWriteResponse, INightTurnWristeDataListener nightTurnWristeDataListener, NightTurnWristSetting nightTurnWristSetting)
```

###### Parameter Explanation

| Parameter name              | Type                         | Describe         |
| --------------------------- | ---------------------------- | ---------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| nightTurnWristeDataListener | INightTurnWristeDataListener | Wrist-turning screen data listening |
| nightTurnWristSetting | NightTurnWristSetting | Wrist-turning screen setting |

**NightTurnWristSetting** -- Wrist-turn screen-on setting

| Parameter name | Type     | Describe                         |
| -------------- | -------- | -------------------------------- |
| isOpen | Boolean | Is it open? |
| startTime | TimeData | Start time |
| endTime | TimeData | End time |
| level | Int | Wrist flip level: range [1-10.], default is 5 |

###### Return data

Same as the data returned by [[Read wrist flip screen](#Read wrist flip screen)]

###### Example Code

```kotlin
//kotlin code
val nightTurnWristSetting = NightTurnWristSetting(
    true,
    TimeData(20, 0), TimeData
        (8, 0), 5
)
VPOperateManager.getInstance().settingNightTurnWriste({
    if (it != Code.REQUEST_SUCCESS) {

    }
}, {

}, nightTurnWristSetting)
```





## Screen adjustment function

#### Premise

The device needs to support the screen adjustment function. The judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportScreenlight
```



#### Read screen adjustment data

Need to support screen adjustment function

###### Interface

```kotlin
readScreenLight(bleWriteResponse, screenLightListener)
```

###### Parameter Explanation

| Parameter name      | Type                 | Describe         |
| ------------------- | -------------------- | ---------------- |
| bleWriteResponse    | IBleWriteResponse    | Listening for write operations   |
| screenLightListener | IScreenLightListener | Screen adjustment data monitoring |

###### Return data

**IScreenLightListener** -- Screen adjustment data monitoring

```kotlin
/**
 * 屏幕亮度调节的回调
 *
 * @param screenLightData
 */
fun onScreenLightDataChange(screenLightData:ScreenLightData);
```

**ScreenLightData** -- Screen adjustment data

| Parameter name | Type          | Describe     |
| -------------- | ------------- | ------------ |
| status         | EScreenLight  | Operational Status     |
| screenSetting  | ScreenSetting | Screen brightness setting |

**EScreenLight** -- Operational Status

| Parameter name           | Describe     |
| --------------- | -------- |
| SETTING_SUCCESS | Setting success |
| SETTING_FAIL | Setting failure |
| READ_SUCCESS | Read success |
| READ_FAIL | Read failure |
| UNKONW | Unknown status |

**ScreenSetting** -- Screen brightness setting

| Parameter name | Type | Describe                      |
| -------------- | ---- | ----------------------------- |
| startHour | Int | Hour when the first gear starts to work |
| startMinute | Int | Minute when the first gear starts to work |
| endHour | Int | Hour when the first gear ends to work |
| endMinute | Int | Minute when the first gear ends to work |
| level | Int | Set the first gear of the time period |
| otherLeverl | Int | Brightness level of other time periods |
| auto | Int | Automatic adjustment: 1 automatic 2 manual 0 old protocol |
| maxLevel | Int | Maximum brightness adjustment level |

###### Example Code

```kotlin
VPOperateManager.getInstance().readScreenLight(
    writeResponse
) { screenLightData ->
    val message = "屏幕调节数据-读取:$screenLightData"
}
```



#### Setting screen adjustment data

The device must support screen adjustment

###### Interface

```kotlin
settingScreenLight(bleWriteResponse, screenLightListener, screensetting)
```

###### Parameter Explanation

| Parameter name      | Type                 | Describe         |
| ------------------- | -------------------- | ---------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| screenLightListener | IScreenLightListener | Screen adjustment data listening |
| screensetting | ScreenSetting | Screen setting parameters |

###### Return data

The same data as returned by [[Read screen adjustment data](#Read screen adjustment data)]

###### Example Code

```kotlin
//The default setting is [22:00-07:00] is set to level 2, and other times are set to level 4. Users can customize
VPOperateManager.getInstance().settingScreenLight(writeResponse,
{ screenLightData ->
val message = "Screen adjustment data-setting: $screenLightData"
}, ScreenSetting(22, 0, 7, 0, 2, 4)
)
```





## Screen brightness duration function

#### Premise

The device needs to support the screen brightness and duration adjustment function. The judgment code is as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportScreenlightTime
```

#### Read the screen on time

The device needs to support the screen on time adjustment function

###### Interface

```kotlin
readScreenLightTime(bleWriteResponse, screenLightTimeListener)
```

###### Parameter Explanation

| Parameter name                  | Type                     | Describe             |
| ----------------------- | ------------------------ | ---------------- |
| bleWriteResponse        | IBleWriteResponse        | Listening for write operations   |
| screenLightTimeListener | IScreenLightTimeListener | Monitor screen on time |

###### Return data

**IScreenLightTimeListener** -- Monitor screen on time

```kotlin
/**
 * 屏幕亮屏时长的回调
 *
 * @param screenLightTimeData
 */
fun onScreenLightTimeDataChange(screenLightTimeData:ScreenLightTimeData)
```

**screenLightTimeData** --Screen on time data

| Parameter name             | Type             | Describe     |
| ----------------- | ---------------- | -------- |
| screenLightState | EScreenLightTime | callback status |
| currentDuration | Int | current duration |
| recommendDuration | Int | recommended duration |
| maxDuration | Int | maximum duration |
| minDuration | Int | minimum duration |

**EScreenLightTime** -- Callback status

| Parameter name           | Describe     |
| --------------- | -------- |
| SETTING_SUCCESS | Setting success |
| SETTING_FAIL | Setting failure |
| READ_SUCCESS | Read success |
| READ_FAIL | Read failure |
| UNKONW | Unknown status |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance()
    .readScreenLightTime(writeResponse, object : IScreenLightTimeListener {
        override fun onScreenLightTimeDataChange(screenLightTimeData: ScreenLightTimeData?) {
        
        }
    }
    )
```



#### Set the screen on time

The device needs to support screen brightness and duration adjustment function

###### Interface

```kotlin
setScreenLightTime(IBleWriteResponse bleWriteResponse, IScreenLightTimeListener screenLightTimeListener, int time) 
```

###### Parameter Explanation

| Parameter name                  | Type                     | Describe               |
| ----------------------- | ------------------------ | ------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| screenLightTimeListener | IScreenLightTimeListener | Listening for screen light duration |
| time | Int | Screen light duration, unit: seconds |

###### Return data

Same as the data returned by [[Read the screen on time](#Read the screen on time)]

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance()
    .setScreenLightTime(
        writeResponse, object : IScreenLightTimeListener {
            override fun onScreenLightTimeDataChange(screenLightTimeData: ScreenLightTimeData?) {

            }
        }, 10
    )
```





## Health reminder

#### Read health reminders

###### Interface

```kotlin
readHealthRemind(healthRemindType, listener, bleWriteResponse)
```

###### Parameter Explanation

| Parameter name           | Type                  | Describe             |
| ---------------- | --------------------- | ---------------- |
| healthRemindType | HealthRemindType | Health reminder type |
| listener | IHealthRemindListener | Health reminder data listener |
| bleWriteResponse | IBleWriteResponse | Write operation listener |

**HealthRemindType** --- Health reminder type

| Parameter name        | Describe     |
| ------------- | -------- |
| ALL | All reminders |
| SEDENTARY | Sedentary |
| DRINK_WATER | Drink water |
| OVERLOOK | Overlook |
| SPORTS | Sports |
| TAKE_MEDICINE | Take medicine |
| READING | Reading |
| GOING_OUT | Going out |
| WASH | Wash hands |

###### Return data

**IHealthRemindListener** -- Health reminder data monitoring

```kotlin
/**
* This function is not supported
*/
fun functionNotSupport()

/**
* Health reminder read callback
* @param healthRemind health reminder
*/
fun onHealthRemindRead(healthRemind: HealthRemind)

/**
* Health reminder read failed
*/
fun onHealthRemindReadFailed()

/**
* Health reminder active reporting callback
* @param healthRemind health reminder
*/
fun onHealthRemindReport(healthRemind: HealthRemind)

/**
* Health reminder active reporting failed
*/
fun onHealthRemindReportFailed()

/**
* Health reminder setting success
* @param healthRemind health reminder
*/
fun onHealthRemindSettingSuccess(healthRemind: HealthRemind)

/**
* Health reminder setting failed
* @param healthRemindType Setting failed health reminder type
*/
fun onHealthRemindSettingFailed(healthRemindType: HealthRemindType)
```

**HealthRemind** -- Health Remind

| Parameter name     | Type             | Describe         |
| ---------- | ---------------- | ------------ |
| remindtype | HealthRemindType | Health reminder type |
| startTime | TimeData | Reminder start time |
| endTime | TimeData | Reminder end time |
| interval | Int | Reminder interval |
| status | Boolean | Status |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance()
    .readHealthRemind(HealthRemindType.ALL, object : IHealthRemindListener {
        override fun functionNotSupport() {
        }

        override fun onHealthRemindRead(healthRemind: HealthRemind) {
        }

        override fun onHealthRemindReadFailed() {
        }

        override fun onHealthRemindReport(healthRemind: HealthRemind) {
        }

        override fun onHealthRemindReportFailed() {
        }

        override fun onHealthRemindSettingSuccess(healthRemind: HealthRemind) {
        }

        override fun onHealthRemindSettingFailed(healthRemindType: HealthRemindType) {
        }

    }, {
        
    })
```



#### 设置健康提醒

###### Interface

```kotlin
settingHealthRemind(healthRemind, listener, bleWriteResponse)
```

###### Parameter Explanation

| Parameter name           | Type                  | Describe             |
| ---------------- | --------------------- | ---------------- |
| healthRemind | HealthRemind | Health Reminder |
| listener | IHealthRemindListener | Health Reminder Data Listener |
| bleWriteResponse | IBleWriteResponse | Write Operation Listener |

###### Return data

**IHealthRemindListener** Same as [[Read health reminders](#Read health reminders)]

###### Example Code

```kotlin
//kotlin code
val healthRemind =
    HealthRemind(HealthRemindType.ALL, TimeData(8, 0), TimeData(20, 0), 20, true)
VPOperateManager.getInstance()
    .settingHealthRemind(healthRemind, object : IHealthRemindListener {
        override fun functionNotSupport() {
        }

        override fun onHealthRemindRead(healthRemind: HealthRemind) {
        }

        override fun onHealthRemindReadFailed() {
        }

        override fun onHealthRemindReport(healthRemind: HealthRemind) {
        }

        override fun onHealthRemindReportFailed() {
        }

        override fun onHealthRemindSettingSuccess(healthRemind: HealthRemind) {
        }

        override fun onHealthRemindSettingFailed(healthRemindType: HealthRemindType) {
        }

    }) {
        
    }
```





## Heart rate function

#### Start manual heart rate measurement-startDetectHeart

```kotlin
startDetectHeart(bleWriteResponse,heartDataListener)
```

###### Parameter Explanation

| Parameter name            | Type               | Describe               |
| ----------------- | ------------------ | ------------------ |
| bleWriteResponse  | IBleWriteResponse  | Listening for write operations     |
| heartDataListener | IHeartDataListener | Listener for heart rate data return |

###### Return data

**IHeartDataListener**

```kotlin
/**
* Return heart rate data
*
* @param heartData heart rate data
*/
fun onDataChange(heartData:HeartData);
```

**HeartData**

| Parameter name       | Type         | Describe                           |
| ----------- | ------------ | ------------------------------ |
| data | Int | Get heart rate value, range [20-300] |
| heartStatus | EHeartStatus | The status that the device may return when measuring heart rate |

**EHeartStatus**

| Parameter name | Describe |
| ---------------------- | ------------------ |
| STATE_INIT | Initialization |
| STATE_HEART_BUSY | Device is busy |
| STATE_HEART_DETECT | Device is detecting |
| STATE_HEART_WEAR_ERROR | Detecting, but wearing is wrong |
| STATE_HEART_NORMAL | Detecting |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().startDetectHeart({

},object :IHeartDataListener{
    override fun onDataChange(heartData: HeartData?) {
        
    }

})
```



#### End manual heart rate measurement-stopDetectHeart

```
stopDetectHeart(bleWriteResponse)
```

###### Parameter Explanation

| Parameter name           | Type              | Describe           |
| ---------------- | ----------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |

###### Return data

None

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().stopDetectHeart(){
    
}
```



#### Set Heart Rate Alarm-settingHeartWarning

```kotlin
settingHeartWarning(bleWriteResponse, heartWaringDataListener, heartWaringSetting)
```

###### Parameter Explanation

| Parameter name                  | Type                     | Describe           |
| ----------------------- | ------------------------ | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| heartWaringDataListener | IHeartWaringDataListener | Heart rate alarm callback |
| heartWaringSetting | HeartWaringSetting | Heart rate alarm settings |

**HeartWaringSetting**

| Parameter name | Type | Describe |
| --------- | ------- | -------------------------- |
| heartHigh | Int | Upper limit of heart rate alarm |
| heartLow | Int | Lower limit of heart rate alarm |
| isOpen | boolean | true means open, false means closed |

###### Return data

IHeartWaringDataListener

```kotlin
/**
* Returns the heart rate alarm data
*
* @param heartWaringData Heart rate alarm data
*/
fun onHeartWaringDataChange(heartWaringData:HeartWaringData);
```

**HeartWaringData**

| Parameter name  | Type               | Describe                                 |
| ------ | ------------------ | ------------------------------------ |
| status | EHeartWaringStatus | Upper limit of heart rate alarm |
| ... | -- | Other parameters are the same as **HeartWaringSetting** |

**EHeartWaringStatus**

| Parameter name | Describe |
| ------------- | -------- |
| OPEN_SUCCESS | Open successfully |
| OPEN_FAIL | Open failed |
| CLOSE_SUCCESS | Close successfully |
| CLOSE_FAIL | Close failed |
| READ_SUCCESS | Read successfully |
| READ_FAIL | Read failed |
| UNSUPPORT | Unsupported |
| UNKONW | Unknown |

###### Example Code

```kotlin
// kotlin code
val heartWaringSetting = HeartWaringSetting(120,40,true)
VPOperateManager.getInstance().settingHeartWarning({

},object :IHeartWaringDataListener{
    override fun onHeartWaringDataChange(heartWaringData: HeartWaringData?) {
        
    }

},heartWaringSetting)
```



#### Read Heart Rate Alarm-readHeartWarning

```kotlin
readHeartWarning(bleWriteResponse,heartWaringDataListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ----------------------- | ------------------------ | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| heartWaringDataListener | IHeartWaringDataListener | Heart Rate Alarm Callback |

###### Return data

IHeartWaringDataListener is the same as [Set Heart Rate Alarm-settingHeartWarning](#Set Heart Rate Alarm-settingHeartWarning)
###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance().readHeartWarning({

},object :IHeartWaringDataListener{
    override fun onHeartWaringDataChange(heartWaringData: HeartWaringData?) {

    }

})
```



#### Read daily heart rate data

In [Read daily data function](#Read daily data function), the daily heart rate data will be returned.

#### Heart rate detection switch

In [Modify personalization settings](#Modify personalization settings-changeCustomSetting), you can set the heart rate detection switch.

## Body temperature function

#### Premise

Before using the body temperature function, you need to determine whether the device supports the body temperature function. After confirming that the setting supports the body temperature function, you can use the body temperature function related interfaces

Judgment conditions:
```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportReadTempture
```



#### Start manual temperature measurement

###### Premise

1. The device must support body temperature;

2. The device must support body temperature measurement.

```kotlin
1.VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportReadTempture 2.VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportCheckTemptureByApp
```

###### Interface

```kotlin
startDetectTempture(bleWriteResponse, responseListener)
```

###### Parameter Explanation

| Parameter name           | Type                        | Describe               |
| ---------------- | --------------------------- | ------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| responseListener | ITemptureDetectDataListener | Return listener for temperature data |

###### Return data

**ITemptureDetectDataListener**

```kotlin
/**
* Return temperature data
*
* @param temptureDetectData Temperature data
*/
fun onDataChange(temptureDetectData:TemptureDetectData)
```

**TemptureDetectData**

| Parameter name | Type | Describe |
| ------------ | ----- | ------------------------------------------------------------ |
| oprate | Int | 0x00 Not supported, 0x01 Enabled, 0x02 Disabled |
| deviceState | Int | 0x00 Available, 0x01-0x07 Device busy, 0x08 Device low power, 0x09 Sensor abnormality |
| progress | Int | Read progress |
| tempture | Float | Body temperature value |
| temptureBase | Float | Original body temperature value, base value |

###### Example Code

```kotlin
//        kotlin code
        VPOperateManager.getInstance().startDetectTempture({

        },object :ITemptureDetectDataListener{
            override fun onDataChange(temptureDetectData: TemptureDetectData?) {

            }
        })
```



#### End manual temperature measurement

###### Premise

The device must support temperature and body temperature measurement and manual temperature measurement has been enabled

###### Interface

```
stopDetectTempture(bleWriteResponse, responseListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------- | --------------------------- | ---------------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| responseListener | ITemptureDetectDataListener | Return monitoring of temperature data, this interface can pass null |

###### Return data

None

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().stopDetectTempture({

},null)
```



#### Daily temperature data reading

###### Premise

The device needs to support temperature and the automatic temperature detection function needs to be turned on to return data (turn it on through [Personalization Settings](#Modify personalization settings-changeCustomSetting))

```kotlin
VpSpGetUtil.getVpSpVariInstance(requireContext()).isSupportReadTempture
```

**Note:** When the device supports temperature reading and the device temperature type is 5, the device obtains it through [Read daily data function](#Read daily data function) without calling the following readTemptureDataBySetting interface. The judgment is as follows:

```
VpSpGetUtil.getVpSpVariInstance(requireContext()).isSupportReadTempture && VpSpGetUtil.getVpSpVariInstance(requireContext()).getTemperatureType == 5
```

###### Interface

```kotlin
readTemptureDataBySetting(bleWriteResponse, temptureDataListener, readOriginSetting)
```

If the watch stores data for 3 days, read all the temperature data in the order of [temperature data (today - yesterday - the day before yesterday)]. For the temperature data, you can customize the position of the day and the position of the number of bars.
###### Parameter Explanation

| Parameter name               | Type                  | Describe                                      |
| -------------------- | --------------------- | ----------------------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| temptureDataListener | ITemptureDataListener | Callback for reading temperature data, returning the progress temperature data |
| readOriginSetting | ReadOriginSetting | Read daily data settings |

**ReadOriginSetting** Same parameters as [Read daily data function](#Interface-Read raw health data (5 minutes raw data) readOriginDataBySetting)

###### Return data

**ITemptureDataListener**

```kotlin
//kotlin code
/**
* Callback for reading daily temperature data
*
* @param temptureDataList Temperature data list
*/
fun onTemptureDataListDataChange(temptureDataList:List<TemptureData>)

/***
* Returns the details of the reading. The location of this package needs to be remembered. When reading data next time, pass in the location of this package to avoid repeated reading
*
* @param day Data flag in the watch [0=today, 1=yesterday, 2=the day before yesterday]
* @param date Data date, format is yyyy-mm-dd
* @param allPackage Total number of packages of data for the day
* @param currentPackage Location of this package
*/
fun onReadOriginProgressDetail(day:Int, date:String, allPackage:Int, currentPackage：Int)

/**
* Returns the progress of reading
*
* @param progress Progress value, range [0-1]
*/
fun onReadOriginProgress(progress:Float)

/**
* Reading completed
*/
fun onReadOriginComplete()
```

**TemptureData**

| Parameter name         | Type     | Describe              |
| ------------- | -------- | ----------------- |
| allPackage | Int | Total number of packages |
| packageNumber | Int | Current number of packages |
| mTime | TimeData | Time |
| isFromHandler | Boolean | Whether it is manual measurement |
| tempture | Float | Temperature value |
| baseTempture | Float | Original temperature value, base value |
###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportReadTempture){
    return
}
VPOperateManager.getInstance().readTemptureDataBySetting({

},object :ITemptureDataListener{
    override fun onReadOriginProgressDetail(
        day: Int,
        date: String?,
        allPackage: Int,
        currentPackage: Int
    ) {
    }

    override fun onReadOriginProgress(progress: Float) {
    }

    override fun onReadOriginComplete() {
    }

    override fun onTemptureDataListDataChange(temptureDataList: MutableList<TemptureData>?) {
    }

}, readOriginSetting)
```


#### Temperature detection switch

In [Modify personalization settings](#Modify personalization settings-changeCustomSetting), you can set the temperature detection switch.





## ECG function

#### Premise

The device needs to support the ECG function and be idle. The judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG
```

All the following interfaces are called on the premise that the device supports the ECG function.

#### Start manual ECG measurement

###### Premise

The device supports the ECG function

###### Interface

```kotlin
startDetectECG(bleWriteResponse, isNeedCurve, ecgDetectListener) 
```

###### Parameter Explanation

| Parameter name            | Type               | Describe               |
| ----------------- | ------------------ | ------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| isNeedCurve | Boolean | Do you want to return curve data? |
| ecgDetectListener | IECGDetectListener | ECG measurement callback |

###### Return data

**IECGDetectListener**

```kotlin
/**
* Basic information of ECG measurement (waveform frequency, sampling frequency)
*
* @param ecgDetectInfo
*/
fun onEcgDetectInfoChange(ecgDetectInfo: EcgDetectInfo?)

/**
* Status during ECG measurement
*
* @param ecgDetectState
*/
fun onEcgDetectStateChange(ecgDetectState: EcgDetectState?)

/**
* The final result of ECG measurement, when abnormal, that is, when there is a disease, the value will be output
*
* @param ecgDetectResult
*/
fun onEcgDetectResultChange(ecgDetectResult: EcgDetectResult?)

/**
* ECG waveform data
*
* @param data
*
* * Draw the ecg waveform on the interface using this data. When drawing, you need to convert #data into a voltage value.
* Refer to [com.veepoo.protocol.util.EcgUtil.convertToMvWithValue] for the conversion method
* If the array data has no value, it means that reasonable waveform data has not been generated yet. When measuring ecg, the data will be updated continuously.
* When the data is Int.MAX_VALUE, that is, 2147483647 (hexadecimal is 0x7FFFFFFF), it is necessary to filter and not draw this point.
* * Fatigue operation callback, return fatigue data: whether it is supported, on/off status, progress, fatigue value
*
*/
fun onEcgADCChange(data: IntArray?)
```

**EcgDetectInfo**

| Parameter name         | Type | Describe     |
| ------------- | ---- | -------- |
| frequency | Int | Sampling frequency |
| drawFrequency | Int | Waveform frequency |

**EcgDetectState**

| Parameter name | Type | Describe |
| ----------- | ------------- | ------------------------------------------------------------ |
| ecgType | Int | ECG measurement type, obtained through VpSpGetUtil.getVpSpVariInstance(applicationContext).ecgType |
| con | Int | ECG operation value |
| dataType | Int | Data type (0-sampling frequency) (1-real-time device status) (2-diagnosis results) (3-test failure) (4-test normal end), only 1 will appear at this time |
| deviceState | EDeviceStatus | Device status |
| hr1 | Int | Heart rate per second |
| hr2 | Int | Average heart rate per second |
| hrv | Int | hrv value, 255 is an invalid value and does not need to be displayed |
| rr1 | Int | RR value per second |
| rr2 | Int | Average RR value per 6 seconds |
| br1 | Int | Respiration rate value per second |
| br2 | Int | Average respiration rate value per minute |
| wear | Int | Lead wearing value, 0 means wearing passed, 1 means wearing failed, if wearing failed, app should close measurement |
| mid | Int | mid value |
| qtc | Int | qtc value |
| progress | Int | Progress |

**EDeviceStatus**

| Parameter name | Describe |
| ---------------- | ------------------------------- |
| FREE | Device idle |
| BUSY | Device busy |
| DETECT_BP | Device busy, measuring blood pressure |
| DETECT_HEART | Device busy, measuring heart rate |
| DETECT_AUTO_FIVE | Device busy, automatically measuring 5 minutes of data |
| DETECT_SP | The device is busy and measuring blood oxygen |
| DETECT_FTG | The device is busy and measuring fatigue |
| DETECT_PPG | The device is busy and measuring pulse rate |
| CHARGING | The device is charging |
| CHARG_LOW | The device is low in power |
| UNPASS_WEAR | The device cannot be worn |
| UNKONW | Unknown |

**EcgDetectResult**

| Parameter name | Type | Describe |
| ------------------- | -------------- | ------------------ |
| isSuccess | Boolean | Whether the measurement is successful |
| type | EECGResultType | Data source of ECG result |
| timeBean | TimeData | Measurement date |
| frequency | Int | Sampling frequency |
| drawfrequency | Int | Waveform frequency |
| duration | Int | Total seconds |
| leadSign | Int | Lead signal |
| originSign | IntArray | Original signal |
| powers | IntArray | Gain corresponding to the original signal |
| filterSignals | IntArray | Original signal |
| result8 | IntArray | 8 diagnostic data |
| diseaseResult | IntArray | Diagnosis result |
| aveHeart | Int | Average heart rate |
| aveResRate | Int | Average respiration rate |
| aveHrv | Int | Average HRV |
| aveQT | Int | Average QT |
| progress | Int | Progress |
| detectHeartIntArray | IntArray | Measured heart rate data |
| detectBreath | IntArray | Respiration rate data |
| detectHrv | IntArray | HRV data |
| detectQT | IntArray | Array of measured QT values ​​|

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
VPOperateManager.getInstance().startDetectECG({

},true,object :IECGDetectListener{
    override fun onEcgDetectInfoChange(ecgDetectInfo: EcgDetectInfo?) {
    }

    override fun onEcgDetectStateChange(ecgDetectState: EcgDetectState?) {
    }

    override fun onEcgDetectResultChange(ecgDetectResult: EcgDetectResult?) {
    }

    override fun onEcgADCChange(data: IntArray?) {
    }
})
```



#### End manual ECG measurement

###### Premise
1. The device supports ECG
2. The device is idle
3. The device has started manual ECG measurement

###### Interface

```kotlin
stopDetectECG(bleWriteResponse, isNeedCurve, ecgDetectListener)
```

###### Parameter Explanation

Required parameters are the same as [Start manual ECG measurement-startDetectECG](#Start manual ECG measurement-startDetectECG)

###### Return data

Return data is the same as [Start manual ECG measurement-startDetectECG](#Start manual ECG measurement-startDetectECG)

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance()
    .stopDetectECG(bleWriteResponse, isNeedCurve, ecgDetectListener)
```



#### ECG new data report listener

###### Premise

Device supports ecg function

###### Interface

```
setNewEcgDataReportListener(listener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| -------- | ------------------------- | ----------------- |
| listener | INewECGDataReportListener | New ecg data report callback |

###### Return data

**INewECGDataReportListener** -- New ecg data report callback

```java
/**
* New ECG measurement data report data listener interface
* As long as there is new measurement data on the device side, it is triggered by {@link VPOperateManager#readECGData(BleWriteResponse bleWriteResponse, TimeData timeData, EEcgDataType eEcgDataType, IECGReadDataListener onReadDataIdFinishCallBack)} interface to read ecg data details
*/
void onNewECGDetectDataReport();
```

###### Example Code

```java
VPOperateManager.getInstance().setNewEcgDataReportListener(new INewECGDataReportListener() {
                @Override
                public void onNewECGDetectDataReport() {
                    showToast("监听到设备有新的ecg测量数据上报，请读取ECG数据获取详细信息");
                }
            });
```



#### ECG data reading

###### Premise

1. The device supports ecg

###### Interface

```kotlin
readECGData(bleWriteResponse, timeData, eEcgDataType, onReadDataIdFinishCallBack)
```

###### Parameter Explanation

| Parameter name                     | Type                 | Describe                                                         |
| -------------------------- | -------------------- | ------------------------------------------------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| timeData | TimeData | The time when data starts to be read. When eEcgDataType==ALL, year, month, day, hour, minute, and second are passed as 0 |
| eEcgDataType | EEcgDataType | ECG data type |
| onReadDataIdFinishCallBack | IECGReadDataListener | Data callback for reading ECG data |

**EEcgDataTypeEEcgDataType**

| Parameter name | Describe |
| -------- | ----------------- |
| MANUALLY | Device manual measurement |
| AUTO | Device active measurement |
| ALL | Device manual + device active |

###### Return data

**IECGReadDataListener**

```kotlin
/**
* Data reading completed
*
* @param resultList ecg measurement array
*/
fun readDataFinish(resultList:List<EcgDetectResult>)
```

**EcgDetectResult**

Same as the EcgDetectResult returned by [Start Manual ECG Measurement-startDetectECG](#Start Manual ECG Measurement-startDetectECG)

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
val timedata = TimeData(0,0,0,0,0,0,0)
VPOperateManager.getInstance().readECGData({

},timedata,EEcgDataType.ALL,object :IECGReadDataListener{
    override fun readDataFinish(resultList: MutableList<EcgDetectResult>?) {

    }

})
```



#### Read ECG data ID

###### Premise

1. The device supports ecg

###### Interface

```kotlin
readECGId(bleWriteResponse, timeData, eEcgDataType, onReadIdFinishCallBack)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------------- | ------------------ | ------------------------------------------------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| timeData | TimeData | The time when data starts to be read. When eEcgDataType==ALL, the year, month, day, hour, minute, and second are transmitted as 0 |
| eEcgDataType | EEcgDataType | ECG data type |
| onReadIdFinishCallBack | IECGReadIdListener | Callback for the end of reading data ID |

###### Return data

IECGReadIdListener

```kotlin
/**
* Callback for the end of reading data ID
* @param ids ecg data id list
*/
fun readIdFinish(ids:IntArray?)
```

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
val timedata = TimeData(0,0,0,0,0,0,0)
VPOperateManager.getInstance().readECGId({

},timedata,EEcgDataType.ALL,object :IECGReadIdListener{
    override fun readIdFinish(ids: IntArray?) {

    }
})
```



#### ECG chest function reading

###### Premise

1. The device supports ecg

###### Interface

```
readECGSwitchStatus(bleWriteResponse, listener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------- | ------------------ | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| listener | IECGSwitchListener | ECG switch listening |

###### Return data

**IECGSwitchListener**

```kotlin
/**
* ECG switch status changed
*
* @param ecgFunctionStatus ecg function status: UNSUPPORT not supported, SUPPORT supported, SUPPORT_OPEN open, SUPPORT_CLOSE closed, UNKONW unknown
*/
fun onECGSwitchStatusChanged(ecgFunctionStatus:EFunctionStatus)
```

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
VPOperateManager.getInstance().readECGSwitchStatus({

},object :IECGSwitchListener{
    override fun onECGSwitchStatusChanged(ecgFunctionStatus: EFunctionStatus?) {

    }
})
```


#### Open ECG chest function

###### Premise

1. The device supports ecg

2. The device supports chest function: judge whether it is supported by [[ECG chest function reading](# ECG chest function reading-readECGSwitchStatus)]

###### Interface

```
openECGSwitch(bleWriteResponse, listener)
```

###### Parameter Explanation

Same as [ECG chest function reading](# ECG chest function reading-readECGSwitchStatus)] parameter

###### Return data

Same as [ECG chest function reading](# ECG chest function reading-readECGSwitchStatus)] return data

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
VPOperateManager.getInstance().openECGSwitch({

},object :IECGSwitchListener{
    override fun onECGSwitchStatusChanged(ecgFunctionStatus: EFunctionStatus?) {

    }
})
```



#### Turn off ECG chest function

###### Premise

1. The device supports ecg
2. The device supports chest function: judge whether it is supported by [ECG chest function reading](# ECG chest function reading-readECGSwitchStatus)]

###### Interface

```
openECGSwitch(bleWriteResponse, listener)
```

###### Parameter Explanation

Same as [ECG chest function reading](# ECG chest function reading-readECGSwitchStatus)] Parameters

###### Return data

Same as [ECG chest function reading](# ECG chest function reading-readECGSwitchStatus)] Return data

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
VPOperateManager.getInstance().closeECGSwitch({

},object :IECGSwitchListener{
    override fun onECGSwitchStatusChanged(ecgFunctionStatus: EFunctionStatus?) {

    }
})
```



#### Start reading PTT data

###### Premise

1. The device supports ecg;

2. The device supports ECG chest function;

3. The device has turned on the ECG chest function.

###### Interface

```
startReadPttSignData(bleWriteResponse, isNeedCurve, ecgDetectListener)
```

###### Parameter Explanation

Same as [[Start manual ECG measurement-startDetectECG](#Start manual ECG measurement-startDetectECG)] parameters

###### Return data

Same as [[Start manual ECG measurement-startDetectECG](#Start manual ECG measurement-startDetectECG)] return data

###### Example Code

```kotlin
//        kotlin code
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
VPOperateManager.getInstance().startReadPttSignData({

},true,object :IECGDetectListener{
    override fun onEcgDetectInfoChange(ecgDetectInfo: EcgDetectInfo?) {
    }

    override fun onEcgDetectStateChange(ecgDetectState: EcgDetectState?) {
    }

    override fun onEcgDetectResultChange(ecgDetectResult: EcgDetectResult?) {
    }

    override fun onEcgADCChange(data: IntArray?) {
    }
})
```



#### End reading PTT data - stopReadPttSignData

###### Premise

1. The device supports ecg;

2. The device supports ECG chest function;

3. The device has turned on the ECG chest function;

4. The device has started reading PTT data.

###### Interface

```
stopReadPttSignData(bleWriteResponse, isNeedCurve, ecgDetectListener)
```

###### Parameter Explanation

Same as [[End manual ECG measurement-stopDetectECG](#End manual ECG measurement-stopDetectECG)] parameter

###### Return data

Same as [[End manual ECG measurement-stopDetectECG](#End manual ECG measurement-stopDetectECG)] return data

###### Example Code

```kotlin
if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG){
    return
}
VPOperateManager.getInstance().stopReadPttSignData({

},true,object :IECGDetectListener{
    override fun onEcgDetectInfoChange(ecgDetectInfo: EcgDetectInfo?) {
    }

    override fun onEcgDetectStateChange(ecgDetectState: EcgDetectState?) {
    }

    override fun onEcgDetectResultChange(ecgDetectResult: EcgDetectResult?) {
    }

    override fun onEcgADCChange(data: IntArray?) {
    }
})
```





## HRV function

#### Premise

The device needs to support the HRV function and be idle. The judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportHRV
```

All the following interfaces are called on the premise that the device supports the HRV function.

#### Read HRV daily data

HRV daily data is obtained through [[Read daily data function](#Read daily data function)]





## Sleep function

#### Read sleep data

###### Interface

Read sleep data, read from the beginning and read to the end

```kotlin
readSleepData(bleWriteResponse, sleepReadDatalistener, watchday)
```

Read sleep data, this method means you can define your reading position and read in the order of reading

```kotlin
readSleepDataFromDay(bleWriteResponse, sleepReadDatalistener, day, watchday)
```

Read sleep data, this method means you can define your reading position and read only the day

```kotlin
readSleepDataSingleDay(bleWriteResponse, sleepReadDatalistener, day, watchday)
```

Read sleep data, this method means you can set your reading position and whether to read only the day

```kotlin
readSleepDataBySetting(bleWriteResponse, sleepReadDatalistener, readSleepSetting)
```

###### Parameter Explanation

| Parameter name                | Type                 | Describe                                                 |
| --------------------- | -------------------- | ---------------------------------------------------- |
| bleWriteResponse | IBleWriteResponseInt | Listening for write operations |
| sleepReadDatalistener | ISleepDataListener | Listening for sleep data, returning the progress of sleep reading and the corresponding sleep data |
| day | Int | Reading day, 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on |
| watchday | Int | Number of days stored in the watch |
| readSleepSetting | ReadSleepSetting | Reading settings for sleep |

**ReadSleepSetting**

| Parameter name | Type | Describe |
| -------------- | ------- | ---------------------------------------------- |
| dayInt | Int | Reading day, 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on |
| watchDataDay | Int | Number of days stored in the watch |
| onlyReadOneDay | Boolean | true means read only today, false means read in order         |

###### Return data

**ISleepDataListener**

```kotlin
/**
* Return data, sleep may be normal sleep or precision sleep,
* If it is precision sleep, it needs to be forced to convert to SleepPrecisionData
* There are two ways to judge:
* 1. According to the function flag after the password is passed;
* 2. According to the instanceof keyword, if (sleepData instanceof SleepPrecisionData)
*
* @param day indicates the data of which day is being read 0: today, 1: yesterday, 2: the day before yesterday.
* @param sleepData sleep data
*/
fun onSleepDataChange(day: String?, sleepData: SleepData?)

/**
* Returns the progress of sleep reading, range [0-1]
*
* @param progress progress
*/
fun onSleepProgress(progress: Float)

/**
* Returns the details of reading sleep. This interface is only used for testing, which is convenient for developers to view the reading progress
*
* @param day indicates the data of the day being read. The data of three days in a day is read in the order of today-yesterday-the day before yesterday
* @param packagenumber indicates the package number. The data of a day is returned starting from the largest package and decreasing in sequence
*/
fun onSleepProgressDetail(day: String?, packagenumber: Int)

/**
* Sleep reading ends
*/
fun onReadSleepComplete()
```

**SleepData**

| Parameter name         | Type     | Describe                                                         |
| ------------- | -------- | ------------------------------------------------------------ |
| Date | String | Sleep date |
| cali_flag | Int | Sleep calibration value, currently this value is useless |
| sleepQulity | Int | Sleep quality |
| wakeCount | Int | Number of times you wake up during sleep |
| deepSleepTime | Int | Deep sleep duration (in min) |
| lowSleepTime | Int | Light sleep duration (in min) |
| allSleepTime | Int | Total sleep duration |
| sleepLine | String | Sleep curve is mainly used to display sleep status in a more concrete UI. If your sleep interface has no special requirements for UI, you can ignore it. Sleep curve is divided into normal sleep and precise sleep. Normal sleep is a string composed of 0, 1, 2. Each character represents a duration of 5 minutes, where 0 represents light sleep, 1 represents deep sleep, and 2 represents awakening. For example, "201112" has a length of 6, which means a total of 30 minutes of sleep, 5 minutes of awakening at the beginning and end, 5 minutes of light sleep in the middle, and 15 minutes of deep sleep. For precise sleep, the sleep curve is a string composed of 0, 1, 2, 3, 4. Each character represents a duration of 1 minute, where 0 represents deep sleep, 1 represents light sleep, 2 represents rapid eye movement, 3 represents insomnia, and 4 represents awakening. |
| sleepDown | TimeData | Time to fall asleep |
| sleepUp | TimeData | Time to wake up |

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().readSleepData({

},object :ISleepDataListener{
    override fun onSleepDataChange(day: String?, sleepData: SleepData?) {
    }

    override fun onSleepProgress(progress: Float) {
    }

    override fun onSleepProgressDetail(day: String?, packagenumber: Int) {
    }

    override fun onReadSleepComplete() {
    }

},3)
```





## Jieli device authentication

#### Premise

It needs to be a Jieli device, and the judgment conditions are as follows:

```java
VpSpGetUtil.getVpSpVariInstance(mContext).isJieLiDevice()
Or
VPOperateManager.getInstance().isJLDevice()
```

When the device chip platform is Jieli, the following functions need to be implemented through Jieli SDK (Weiyipo watch SDK also integrates Jieli SDK):

1. Photo dial transmission (only dial photos and preview photos are transmitted, and dial element settings are not included)

2. Market dial transmission

3. OTA upgrade

Note: ** The premise of operating these functions is that Jieli device authentication must be completed first. After the App is started, Jieli device authentication only needs to be completed once. **

To complete Jieli device authentication, you need to open Jieli service notification first, and then perform device authentication.

#### Open Jieli service notification

###### Example Code

```java
private void openJLNotify() {
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
```



#### Start JieLi equipment certification

###### Example Code

```java
private void startDeviceAuth() {
    VPOperateManager.getInstance().startJLDeviceAuth(new RcspAuthResponse() {
        @Override
        public void onRcspAuthStart() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.showNoTips();
                    tvAuthInfo.setText("Start device certification");
                }
            });
        }

        @Override
        public void onRcspAuthSuccess() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.disMissDialog();
                    tvAuthInfo.setText("Device certification has passed");
                }
            });
        }

        @Override
        public void onRcspAuthFailed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.disMissDialog();
                    tvAuthInfo.setText("Device certification failed");
                }
            });
        }
    });
}
```





## Open JieLi file system

When the device chip platform is JieLi, **before each operation of the following functions, you need to open the JieLi file system**:

1. Photo dial transfer (only transfer dial photos and preview photos, not including dial element settings)

2. Market dial transfer

3. OTA upgrade

#### Premise

You must first complete [[Start JieLi equipment certification](#Start JieLi equipment certification)], and after completing the certification, you can open the JieLi file system

###### Example Code

```java
private void getJLFileSystem() {
       //Jie Li File System
VPOperateManager.getInstance().listJLWatchList(new JLWatchFaceManager.OnWatchDialInfoGetListener() {
        @Override
        public void onGettingWatchDialInfo() {
            //Getting watch face information... Do not perform other Bluetooth operations at this time
            loadingDialog.showNoTips();//Pop up the loading box, if the user needs to implement it
        }

        @Override
        public void onWatchDialInfoGetStart() {
            //Start getting watch face information
            loadingDialog.showNoTips();//Pop up the loading box, if the user needs to implement it
        }

        @Override
        public void onWatchDialInfoGetComplete() {
            //Get watch face information process completed
            loadingDialog.disMissDialog();//Close the loading box, if the user needs to implement it
        }

        @Override
        public void onWatchDialInfoGetSuccess(List<FatFile> systemFatFiles, List<FatFile> serverFatFiles, FatFile picFatFile) {
            //Get the dial information of the Jie Li platform successfully
            StringBuilder sb = new StringBuilder("Jie Li dial system update=============================Start");
            sb.append("\t\t\t\n").append("[Photo dial] picFatFile = ").append(picFatFile == null ? "NULL" : picFatFile.getPath());
            for (FatFile serverFatFile : serverFatFiles) {
            sb.append("\t\t\t\n").append("[Server dial] serverFatFile = ").append(serverFatFile == null ? "NULL" : serverFatFile.getPath());
            }
            for (FatFile systemFatFile : systemFatFiles) {
                sb.append("\t\t\t\n").append("[System dial] systemFatFile = ")
                .append(systemFatFile == null ? "NULL" : systemFatFile.getPath());
            }
            sb.append("\t\t\t\n").append("[Current server dial] serverFatFile = ")
            .append(serverFatFiles.isEmpty() ? "【Not set yet】" : serverFatFiles.get(0).getPath())
            .append("\n")
            .append("Jie Li dial system update==============================End");
            Log.e(TAG, sb.toString());
            tvFileSystemInfo.setText(sb.toString());
            for (FatFile systemFatFile : systemFatFiles) {
                 Logger.t(TAG).e("System dial--->" + systemFatFile.toString());
            }
            for (FatFile serverFatFile : serverFatFiles) {
                Logger.t(TAG).e("Server dial--->" + serverFatFile.toString());
            }
            Logger.t(TAG).e("Photo dial--->" + picFatFile.toString());
            loadingDialog.disMissDialog();
           }

        @Override
        public void onWatchDialInfoGetFailed(BaseError error) {
            //Failed to get dial information
            tvFileSystemInfo.setText("Get file system list-failed:\n" + error.toString());
            loadingDialog.disMissDialog();
        }
        });
  }
```

File system acquisition monitoring

```java
public interface OnWatchDialInfoGetListener {
	/**
	* Getting dial information
	* (Generally, this method will be called back if it is called again before the acquisition is completed)
	*/
	void onGettingWatchDialInfo();

	/**
	* Start getting dial information
	*/
	void onWatchDialInfoGetStart();

	/**
	* Completed getting dial information
	*/
	void onWatchDialInfoGetComplete();

	/**
	* Successfully obtained watch dial information
	*
	* @param systemFatFiles system dial
	* @param serverFatFiles server dial
	* @param picFatFile photo dial
	*/
	void onWatchDialInfoGetSuccess(List<FatFile> systemFatFiles, List<FatFile> serverFatFiles, FatFile picFatFile);

	/**
	* Failed to obtain dial
	*/
	void onWatchDialInfoGetFailed(BaseError error);
}
```

### 



## Watch face function

#### Premise

The device needs to support screen style reading

```
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportScreenStyle
```

All watch face instructions can only be issued after the device supports screen style reading

#### Read screen style - readScreenStyle

Get the watch face style and watch face index of the current screen.

###### Interface

```
readScreenStyle(bleWriteResponse, screenStyleListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ------------------- | -------------------- | ----------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| screenStyleListener | IScreenStyleListener | Screen style listening, return operation status |

###### Return data

**IScreenStyleListener**

```kotlin
/**
* Screen style setting callback
*
* @param screenStyleData Screen style data
*/
fun onScreenStyleDataChange(screenStyleData:ScreenStyleData);
```

**ScreenStyleData**

| Parameter name | Type | Describe |
| ----------- | ------------ | ------------------------------------------------------------ |
| status | EScreenStyle | Operation status |
| screenIndex | Int | Dial index The default dial starts from 0, with a maximum of seven default dials. The dial market and custom dials start from 1 |
| screenType | EUIFromType | * Dial style * 0x00 Device default dial * 0x01 Dial market (device support required) * 0x02 Custom dial (device support required) |

**EScreenStyle**

| Parameter name | Describe |
| --------------- | -------- |
| SETTING_SUCCESS | Setting success |
| SETTING_FAIL | Setting failure |
| READ_SUCCESS | Read success |
| READ_FAIL | Read failure |
| UNKONW | Unknown status |
###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().readScreenStyle({

}, object : IScreenStyleListener {
    override fun onScreenStyleDataChange(screenStyleData: ScreenStyleData?) {

    }
}
)
```



#### Set screen style - settingScreenStyle

Set the dial style and dial subscript of the current device screen.

###### Interface

Set the subscript of the default dial

```kotlin
settingScreenStyle(IBleWriteResponse bleWriteResponse, IScreenStyleListener screenStyleListener, int style)
```

Set the dial style and dial subscript of the current device screen

```
settingScreenStyle(IBleWriteResponse bleWriteResponse, IScreenStyleListener screenStyleListener, int style, EUIFromType uiFromType)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ------------------- | -------------------- | ----------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| screenStyleListener | IScreenStyleListener | Screen style listening, return operation status |
| style | Int | dial subscript |
| uiFromType | EUIFromType | Watch face type |

**EUIFromType**

| Parameter name | Describe |
| ------- | ------------------ |
| DEFAULT | Watch face that comes with the watch |
| CUSTOM | Custom editable watch face |
| SERVER | Server watch face |
| ... | No need to pay attention to other types |

###### Return data

**IScreenStyleListener** Same as [[Read screen style - readScreenStyle](#Read screen style - readScreenStyle)] Return data is consistent

###### Example Code

```kotlin
//kotlin code
val uiFromType = EUIFromType.DEFAULT
val style = 2
VPOperateManager.getInstance().settingScreenStyle(
    {
        if (it != Code.REQUEST_SUCCESS) {
//                    Log.e("Test", "write cmd failed")
        }
    }, screenStyleListener, style, uiFromType
)
```



### Local dial

###### Premise

First get the number of local dials, how to get the number of local dials

```
val defaultUiCount = SpUtil.getInt(applicationContext, SputilVari.COUNT_SCREEN_STYLE_TYPE, 1)
```

defaultUiCount>1 can set the local dial

###### Interface

Call [[Set screen style-settingScreenStyle](#Set screen style-settingScreenStyle)] method to set, uiFromType = EUIFromType.DEFAULT, style = 0 - (defaultUiCount - 1)

###### Example Code

```kotlin
//kotlin code
val uiFromType = EUIFromType.DEFAULT
val style = 2
VPOperateManager.getInstance().settingScreenStyle(
    {
        if (it != Code.REQUEST_SUCCESS) {
//                    Log.e("Test", "write cmd failed")
        }
    }, screenStyleListener, style, uiFromType
)
```



### Server watch face

#### Premise

1. The device UI transmission mode is 2;

2. The number of server watch faces is greater than 0.

```kotlin
val bigTranType = VpSpGetUtil.getVpSpVariInstance(applicactionContext).bigTranType
val serverUICount = VpSpGetUtil.getVpSpVariInstance(applicactionContext).watchuiServer
var isSupport = (bigTranType == 2 && serverUICount > 0)
```

**Note⚠️ **: Watch face transmission needs to add an abnormal protection scenario: when the watch battery is very low, if the watch face transmission is initiated, the watch may shut down due to low power during the transmission process. After recharging, the watch face will turn black. We recommend that you read the current battery level of the watch before each transmission. If the battery status is low, the transmission is prohibited.

#### Process

The steps to set the dial UI from the server are roughly divided into the following steps:

>Step 1. Determine whether the dial market is supported
Step 2. Get basic information
Step 3. Get the supported server UI list
Step 4. Download the corresponding UI file
Step 5. Set UI

#### Class name

Note: **The class name used for this function operation is different from the previous class name**

```kotlin
val mUiUpdateUtil = UiUpdateUtil.getInstance();
val uiUpdateCheckOprate = UiServerHttpUtil();
```

#### Step 1. Determine whether the dial market is supported

```kotlin
//Support server dials
if (mUiUpdateUtil.isSupportChangeServerUi()) {
mUiUpdateUtil.init(context)
} else {
//Does not support server dials

}
```

#### Step 2. Get basic information

###### Class name

UiUpdateUtil

###### Interface

```
getServerWatchUiInfo(uiBaseInfoFormServerListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------------------- | ----------------------------- | -------------------- |
| uiBaseInfoFormServerListener | IUIBaseInfoFormServerListener | Get basic UI information callback |

###### Return data

**IUIBaseInfoFormServerListener**

```kotlin
/**
* Returns basic UI information - server,
*
* @param uiDataServer basic UI information - server UI
*/
fun onBaseUiInfoFormServer(uiDataServer:UIDataServer);
```

**UIDataServer**

| Parameter name | Type | Describe |
| ------------------ | ---- | ------------------------------ |
| useType | Int | Data usage type |
| oprateType | Int | Operation type |
| oprateState | Int | Operation status |
| dataReceiveAddress | Int | UI data receiving start address |
| dataCanSendLength | Int | Receiveable data length |
| binDataType | Int | File type, request server to send fields |
| deviceAialShape | Int | Screen type, request server to send fields |
| imgCrcId | Int | Watch dial CRC checksum |
| dataFileLength | Long | Length of the file to be sent |
| packageIndex | Int | Which package |

###### Example Code

```kotlin
UiUpdateUtil.getInstance().getServerWatchUiInfo { uiDataServer ->
    //"2.服务器的表盘基本信息 uiDataServer:$uiDataServer"
}
```



#### Step 3. Get the supported server UI list

###### Class name

UiServerHttpUtil

###### Interface

```kotlin
getThemeInfo(uiDataServer, deviceNumber, deviceTestVersion, appPackName, appVersion):List<TUiTheme>
```

Note: **This interface is a network request and cannot be run in the main thread**

###### Parameter Explanation

| Parameter name | Type | Describe |
| ----------------- | ------------ | -------------------- |
| uiDataServer | UIDataServer | Basic information of the server's dial |
| deviceNumber | String | Device number |
| deviceTestVersion | String | Device test version |
| appPackName | String | App package name |
| appVersion | String | App version |

Among them, deviceNumber will be returned when [[connecting device to confirm password](#Verify password operation)], the returned class name is PwdData, the field is deviceNumber, and deviceTestVersion is the deviceTestVersion value in PwdData.

###### Return data

**List<TUiTheme>**

**TUiTheme**

| Parameter name       | Type   | Describe                 |
| ----------- | ------ | -------------------- |
| crc | String | Server dial CRC |
| binProtocol | String | Firmware (no need to pay attention here) |
| dialShape | String | Dial shape |
| fileUrl | String | File download path |
| previewUrl | String | Dial preview image path |

###### Example Code

```kotlin
Thread {
    val appPackName = "com.timaimee.watch"
    val appVersion = "3.1.9"
    val themeInfoList = uiUpdateCheckOprate.getThemeInfo(
        mUiDataServer,
        deviceNumber,
        deviceTestVersion,
        appPackName,
        appVersion
    )
    //拿到themeInfoList 页面做展示
    
}.start()
```



#### Step 4. Download the corresponding UI file

###### Class name

UiServerHttpUtil

###### Interface

```
downloadFile(downUrl, fileSave, onDownLoadListener)
```

Note: **This interface is a network request and cannot be run in the main thread**

###### Parameter Explanation

| Parameter name | Type | Describe |
| ------------------ | ------------------ | ---------------------------------------- |
| downUrl | String | Downloaded dial file link (fileUrl returned in step 3) |
| fileSave | String | Saved local path |
| onDownLoadListener | OnDownLoadListener | Download callback |

###### Return data

OnDownLoadListener

```kotlin
/**
* Return the progress value of downloading firmware, range [0-1]
* @param progress
*/
fun onProgress(progress:Float);

/**
* Download completed
*/
fun onFinish();
```

###### Example Code

```kotlin
Thread {
    uiUpdateCheckOprate.downloadFile(
        fileUrl,
        fileSave,
        object : OnDownLoadListener {
            override fun onProgress(progress: Float) {
                Logger.t(TAG).i("下载进度:$progress")
            }

            override fun onFinish() {
                Logger.t(TAG).i("下载完成")
            }
        })
}.start()
```



#### Step 5. Set up the UI

Note: **When setting up the server dial, you need to pay attention to the platform of the device. The Jie Li platform needs to follow Jie Li's UI setting process separately**

##### Setting up the UI for non-Jie Li platforms

###### Class name

UiUpdateUtil

###### Interface

```
startSetUiStream(euiFromType, inputStream, uiUpdateListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------- | ----------------- | ------------------------------------- |
| euiFromType | EUIFromType | UI type The default here is: EUIFromType.SERVER |
| inputStream | InputStream | Dashboard file input stream |
| uiUpdateListener | IUiUpdateListener | UI upgrade callback |

###### Return data

**IUiUpdateListener**

```kotlin
/**
* UI upgrade start callback
*/
fun onUiUpdateStart()

/**
* UI erase starts
* @param sumCount total erase size
*/
fun onStartClearCache(sumCount: Int)

/**
* UI erase progress callback
* @param currentCount current erase count
* @param sumCount total erase size
* @param progress erase progress
*/
fun onClearCacheProgress(currentCount: Int, sumCount: Int, progress: Int)

/**
* UI erase completes
*/
fun onFinishClearCache()

/**
* UI upgrade progress
* @param currentBlock current block
* @param sumBlock total block
* @param progress upgrade progress
*/
fun onUiUpdateProgress(currentBlock: Int, sumBlock: Int, progress: Int)

/**
* UI upgrade successful
*/
fun onUiUpdateSuccess()

/**
* UI update failed
* @param eUiUpdateError reason for upgrade failure
*/
fun onUiUpdateFail(eUiUpdateError: EUiUpdateError?)
```

**EUiUpdateError**

| Parameter name                   | Describe                    |
| ----------------------- | ----------------------- |
| LISTENTER_IS_NULL | No listener set |
| NEED_READ_BASE_INFO | No dial information read first |
| FILE_UNEXIST | File does not exist |
| LOW_BATTERY | Battery is too low |
| INTO_UPDATE_MODE_FAIL | Failed to enter UI mode |
| FILE_LENGTH_NOT_4_POWER | File is not 4-byte aligned |
| CHECK_CRC_FAIL | CRC check failed |
| APP_CRC_SAME_DEVICE_CRC | CRC of app is consistent with CRC of device |

Note: When the CRC of the dial to be transmitted is consistent with the CRC of the device, there is no need to go through the server dial transmission logic, and the server dial can be directly set through the [[Set Screen Style](#Set Screen Style-settingScreenStyle)] interface.
###### Example Code

```kotlin
al mUritempFile = Uri.fromFile(mUpdatefile)
val inputStream: InputStream =
    getContentResolver().openInputStream(mUritempFile)

/**
 * Upgrade UI steps: Start upgrade - Clear cache data - Send UI data -End sending
 */
UiUpdateUtil.getInstance().startSetUiStream(
    EUIFromType.SERVER,
    inputStream, object : IUiUpdateListener {
        override fun onUiUpdateStart() {
        }

        override fun onStartClearCache(sumCount: Int) {
        }

        override fun onClearCacheProgress(
            currentCount: Int,
            sumCount: Int,
            progress: Int
        ) {
        }

        override fun onFinishClearCache() {
        }

        override fun onUiUpdateProgress(
            currentBlock: Int,
            sumBlock: Int,
            progress: Int
        ) {
        }

        override fun onUiUpdateSuccess() {
        }

        override fun onUiUpdateFail(eUiUpdateError: EUiUpdateError?) {
        }

    }
)
```

##### JiLi platform setting UI

###### Premise

The device must be a Jier device and have completed [[Open JieLi file system](#Open JieLi file system)]

###### Interface	

```
VPOperateManager.getInstance().setJLWatchDial(localServerDialPath, listener)
```

###### Parameter Explanation

| Parameter name              | Type                                   | Describe           |
| ------------------- | -------------------------------------- | ---------------------- |
| localServerDialPath | String                                 |                        |
| listener            | JLWatchHolder.OnSetJLWatchDialListener | JieLi Photo Watch Transmission Monitoring |

###### Return data

OnSetJLWatchDialListener ：JieLi Photo Watch Transmission Monitoring

```java
/**
* Listening for the dial transmission market
*/
public interface OnSetJLWatchDialListener {
    /**
    * Start transmitting the dial
    */
    void onStart();

    /**
    * Dial transmission progress
    *
    * @param progress Progress [0-100]
    */
    void onProgress(int progress);

    /**
    * Dial transmission completed
    *
    * @param watchPath The path of the dial in the JL file system e.g /WATCH088
    */
    void onComplete(String watchPath);

    /**
    * Dial transmission failed
    *
    * @param code Failure code
    * @param errorMsg Failure reason
    */
    void onFiled(int code, String errorMsg);
}
```





### Photo watch face

#### Premise

The device needs to support photo watch faces

**Note⚠️**: Watch face transmission requires an exception protection scenario: when the watch battery is very low, if the watch face transmission is initiated, the watch may shut down due to low power during the transmission process. After recharging, the watch face will turn black. We recommend that you read the current power of the watch before each transmission. If the power status is low, the transmission is prohibited.

#### Process

>Step 1. Determine whether the photo dial is supported
>Step 2. Read basic information
>Step 3. Select the element and the corresponding position of the element
>Step 4. Use the selected image as the background

#### Class name

**UiUpdateUtil**

#### Step 1. Determine whether the photo dial is supported

```kotlin
if (mUiUpdateUtil.isSupportChangeCustomUi()) {
//Support custom dial
mUiUpdateUtil.init(context);
} else {
//Do not support custom dial

}
```

#### Step 2. Read basic information

###### Interface

```
getCustomWatchUiInfo( uiBaseInfoFormCustomListener)
```

###### Parameter Explanation

| Parameter name                       | Type                          | Describe                     |
| ---------------------------- | ----------------------------- | ------------------------ |
| uiBaseInfoFormCustomListener | IUIBaseInfoFormCustomListener | Callback for reading basic information of photo dial |

###### Return data

**IUIBaseInfoFormCustomListener**

```kotlin
/**
* Return ui basic information - custom
*
* @param uiDataCustom ui basic information - custom ui
*/
fun onBaseUiInfoFormCustom(uiDataCustom:UIDataCustom);
```

**UIDataCustom**

| Parameter name              | Type                    | Describe                     |
| ------------------ | ----------------------- | ------------------------ |
| dataReceiveAddress | Int | UI data receiving start address |
| dataCanSendLength | Int | Receiveable data length |
| fileLength | Long | Length of the file to be sent |
| customUIType | EWatchUIType | Device screen type |
| isDefalutUI | Boolean | Whether it is the default dial in the customization |
| timePosition | EWatchUIElementPosition | Element position |
| upTimeType | EWatchUIElementType | Element type above the time |
| downTimeType | EWatchUIElementType | Element type below the time |
| color888 | Int | Font display color |
| crc | Int | CRC value of the dial |
| packageIndex | Int | Which package, starting from 1 |

###### Example Code

```kotlin
mUiUpdateUtil.getCustomWatchUiInfo(IUIBaseInfoFormCustomListener { uiDataCustom ->
	
})
```



#### Step 3. Select the element and its corresponding position

###### Interface

```
setCustomWacthUi(UICustomSetData uiCustomSetData, final IUIBaseInfoFormCustomListener uiBaseInfoFormCustomListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------------------- | ----------------------------- | ---------------- |
| uiCustomSetData | UICustomSetData | Photo dial settings |
| uiBaseInfoFormCustomListener | IUIBaseInfoFormCustomListener | Photo dial settings callback |

**UICustomSetData**

| Parameter nameimePosition | TypeEWatchUIElementPosition | Describe Element position |
| ----------------- | --------------------------- | ------------------ |
| timePosition | EWatchUIElementPosition | Element position |
| upTimeType | EWatchUIElementType | Element type above time |
| downTimeType | EWatchUIElementType | Element type below time |
| color888 | Int | Font display color |
| isDefalutUI | Boolean | Is it the default photo |

By querying the interface to return customUIType to get the WatchUIType of the device, you can know the element position, default image, photo ratio and other information supported by the device.

###### Return data

**IUIBaseInfoFormCustomListener**

```kotlin
/**
* Returns basic UI information - custom UI
*
* @param uiDataCustom basic UI information - custom UI
*/
fun onBaseUiInfoFormCustom(uiDataCustom:UIDataCustom);
```

###### Example Code

```kotlin
val timePosition: EWatchUIElementPosition = EWatchUIElementPosition.LEFT_TOP
val upTimeType: EWatchUIElementType = EWatchUIElementType.HEART
val downTimeType: EWatchUIElementType = EWatchUIElementType.STEP
val fontColor: Int = 0xffffff
val uiCustomSetData =
    UICustomSetData(isDefalutUI, timePosition, upTimeType, downTimeType, fontColor)
UiUpdateUtil.getInstance().setCustomWacthUi(uiCustomSetData,
    IUIBaseInfoFormCustomListener { uiDataCustom ->
        val watchColor = uiDataCustom.color888
        val hexColor = ColorUtil.intColorToHexStr(watchColor)
       /**
        * Although the color on the app and the color on the device have the same color value, there will be a difference in display
        * Because the App can display RCG_888, and the device can only display RGB_565
        * So for the display effect, it is best to make a mapping table yourself,
        * For example, if the color A1 on the app and the color A2 on the device are similar, then the color A1 is displayed on the app, and the color A2 is sent to the device.
        */
    })
```

#### Step 4. Use a self-selected image for the background

##### Non-Jieli platform settings

###### Interface

By querying the interface to return customUIType to get the device's WatchUIType, you can know the element position, default image, photo width and height supported by the device, etc.

Note: The selected image cropping width and height must be consistent with the image width and height returned by the device basic information.

```
startSetUiStream(euiFromType, inputStream, uiUpdateListener)
```

###### Parameter Explanation

| Parameter name   | Type              | Describe                                 |
| ---------------- | ----------------- | ---------------------------------------- |
| euiFromType      | EUIFromType       | UI type Default here: EUIFromType.CUSTOM |
| inputStream      | InputStream       | Watch face file input stream             |
| uiUpdateListener | IUiUpdateListener | UI upgrade callback                      |

###### Return data

**IUiUpdateListener**

Same as [[Server watch face - Set Ui](#Step 5. Set UI)]

###### Example Code

```kotlin
inputStream = resources.assets.open(fileName)
val bmp = BitmapFactory.decodeStream(inputStream) //原图
val sendInputStream: InputStream =
    mWatchUIType.getSendInputStream(context, bmp)
UiUpdateUtil.getInstance().startSetUiStream(
    EUIFromType.CUSTOM,
    sendInputStream,
    object : IUiUpdateListener {
        override fun onUiUpdateStart() {
        }

        override fun onStartClearCache(sumCount: Int) {
        }

        override fun onClearCacheProgress(
            currentCount: Int,
            sumCount: Int,
            progress: Int
        ) {
        }

        override fun onFinishClearCache() {
        }

        override fun onUiUpdateProgress(
            currentBlock: Int,
            sumBlock: Int,
            progress: Int
        ) {
        }

        override fun onUiUpdateSuccess() {
        }

        override fun onUiUpdateFail(eUiUpdateError: EUiUpdateError) {
        }
    })
```



##### JL Platform Settings

###### Premise

The device must be a JL device and [[Open JieLi file system](#Open JieLi file system)] must have been completed

###### Interface

```
VPOperateManager.getInstance().setJLWatchPhotoDial(dialPhotoPath, listener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| ------------- | -------------------------------------------- | ------------------------------------------------------------ |
| dialPhotoPath | String | The path of the cropped photo file. (It is recommended to save it in a fixed folder directory. And the cropped size should be consistent with the current watch face size, otherwise it will not be transferred. For example, for a 240x280 watch face, the image needs to be 240x280 in width and height) |
| listener | JLWatchFaceManager.JLTransferPicDialListener | Jerry Photo Watch Face Transfer Listener |

###### Return data

JLTransferPicDialListener: Jerry Photo Watch Face Transfer Listener

```java
/**
 * JieLi photo dial transmission monitoring
 */
public interface JLTransferPicDialListener {
    /**
    * Start transferring photo dial
    */
    void onJLTransferPicDialStart();

    /**
    * Monitor dial transfer progress
    *
    * @param progress Progress [0-100]
    */
    void onTransferPicDialProgress(int progress);

    /**
    * The preview image is transferred, this method can be ignored
    */
    void onScaleBGPFileTransferComplete();

    /**
    * The large dial photo image is transferred, this method can be ignored
    */
    void onBigBGPFileTransferComplete();

    /**
    * The photo dial transfer is completed [the preview image will be transferred first and then the large dial image]
    */
    void onTransferComplete();

    /**
    * Photo dial transfer exception
    *
    * @param code Exception code
    * @param errorMsg Error reason
    */
    void onTransferError(int code, String errorMsg);
}
```

###### Example Code

```java
private void setPhotoDial() {
    String dialPhotoPath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlDail/20230413093755.png";
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
```





## Find device function

#### Premise

The device needs to support the mobile phone device search function. All interfaces of this function must be supported by the device before they can be called. The judgment conditions are as follows:

```
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportFindDeviceByPhone
```

#### The phone starts searching for devices actively-startFindDeviceByPhone

###### Interface

```
startFindDeviceByPhone(bleWriteResponse, findDevicelistener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ------------------ | -------------------- | -------------- |
| bleWriteResponse | IBleWriteResponseInt | Listening for write operations |
| findDevicelistener | IFindDevicelistener | Find device listening |

###### Return data

**IFindDevicelistener**

```kotlin
/**
* Not supported by mobile phone
*/
fun unSupportFindDeviceByPhone()
/**
* The device has been found
*/
fun foundedDevice()
/**
* The search timed out
*/
fun unFindDevice()
/**
* The device is vibrating and the screen is on, and it is in the search state
*/
fun findingDevice()
```

###### Example Code

```kotlin
//        kotlin code
        if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportFindDevice){
            return
        }
        VPOperateManager.getInstance().startFindDeviceByPhone({

        },object :IFindDevicelistener{
            override fun unSupportFindDeviceByPhone() {
            }

            override fun findedDevice() {
            }

            override fun unFindDevice() {
            }

            override fun findingDevice() {
            }
        })
```



#### The phone stops searching for devices - stopFindDeviceByPhone

###### Interface

```
stopFindDeviceByPhone(bleWriteResponse, findDevicelistener)
```

###### Parameter Explanation

Same as [[The phone starts searching for devices actively - startFindDeviceByPhone](#The phone starts searching for devices actively - startFindDeviceByPhone)] parameters

###### Return data

Same as [[The phone starts searching for devices actively - startFindDeviceByPhone](#The phone starts searching for devices actively - startFindDeviceByPhone)] return data
###### Example Code

```kotlin
//        kotlin code
        if (!VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportFindDevice){
            return
        }
        VPOperateManager.getInstance().stopFindDeviceByPhone({

        },object :IFindDevicelistener{
            override fun unSupportFindDeviceByPhone() {
            }

            override fun findedDevice() {
            }

            override fun unFindDevice() {
            }

            override fun findingDevice() {
            }
        })
```






When the device calls password verification, it will trigger the reporting callback of the social message function status. Please refer to [Verify password operation](\#Verify password operation)

The message notification function mainly includes

1. Read and set the message notification switch status

2. Send message notification

### Read and set the message notification switch status

#### Read the message notification switch status

```
VPOperateManager.getInstance().readSocialMsg(writeResponse, listener)
```

| Parameter name | Type | Describe |
| ------------- | ---------------------- | ------------------------ |
| writeResponse | IBleWriteResponse | Listening for write operations |
| listener | ISocialMsgDataListener | Callback listening for message notification switch status |

#### Or use the following method to get the message switch status cached by SDK

```
VPOperateManager.getInstance().getFunctionSocailMsgData()
```

This method returns the switch status FunctionSocailMsgData of the message notification. If the entity obtained by this method is null, please call the readSocialMsg method.

#### Setting the message notification switch state

```
VPOperateManager.getInstance().settingSocialMsg(writeResponse, listener, socailMsgData);
```

| Parameter name | Type | Describe |
| ------------- | ---------------------- | ------------------------ |
| writeResponse | IBleWriteResponse | Listening for write operations |
| listener | ISocialMsgDataListener | Message notification switch state callback listener |
| socailMsgData | FunctionSocailMsgData | Switch state to be set |

#### Switch state and listener callback

ISocialMsgDataListener: Message notification switch state change listener, this listener will be called when reading or setting. **Note: Some watches may support less than 17 types of social messages, then this method will not be called back**

```
/**
 * Social message status monitoring
 */
public interface ISocialMsgDataListener extends IListener {
   /**
    * Listening for social message status changes, first package
    * Note: Some watches may support less than 17 types of social messages, then
    *
    * @param socailMsgData Social message switch
    */
    void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData);

    /**
    * Listening for social message status changes, first package
    * Note: Some watches may support less than 17 types of social messages, then this method will not be called back
    *
    * @param socailMsgData Social message switch
    */
    void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData);
}
```

FunctionSocailMsgData: Message notification status

| Member name | Type | Description |
| ------------- | --------------- | ---------------- |
| phone | EFunctionStatus | Phone |
| msg | EFunctionStatus | SMS |
| wechat | EFunctionStatus | WeChat |
| qq | EFunctionStatus | QQ |
| sina | EFunctionStatus | Sina |
| facebook | EFunctionStatus | Facebook |
| twitter | EFunctionStatus | X (original Twitter) |
| flickr | EFunctionStatus | Flickr |
| Linkin | EFunctionStatus | Linkin |
| whats | EFunctionStatus | Whats |
| line | EFunctionStatus | Line |
| instagram | EFunctionStatus | Instagram |
| snapchat | EFunctionStatus | Snapchat |
| skype | EFunctionStatus | Skype |
| gmail | EFunctionStatus | Gmail |
| dingding | EFunctionStatus | Dingding |
| wxWork | EFunctionStatus | Enterprise WeChat wxWork |
| tikTok | EFunctionStatus | Douyin TikTok |
| telegram | EFunctionStatus | Telegram |
| connected2_me | EFunctionStatus | Connected2Me |
| kakaoTalk | EFunctionStatus | KakaoTalk |
| messenger | EFunctionStatus | Messenger |
| other | EFunctionStatus | Other messages |
| shieldPolice | EFunctionStatus | Police Right |

EFunctionStatus: Function status (enumeration type)

| Type | Description |
| ------------- | ------------ |
| UNSUPPORT | This function is not supported |
| SUPPORT | Supported |
| SUPPORT_OPEN | Supported and open |
| SUPPORT_CLOSE | Supported and closed |
| UNKONW | Unknown status |

### Send message notification

Message notifications are divided into three categories

1. Mobile phone call message notification

2. Mobile phone text message notification

3. Social message notification (notification messages from various apps such as WeChat, QQ, X (original Twitter), etc.)

#### Send message

```
VPOperateManager.getInstance().sendSocialMsgContent(writeResponse, contentSetting)
```

| Parameter name | Type | Description |
| -------------- | ----------------- | -------------- |
| writeResponse | IBleWriteResponse | Listening for write operations |
| contentSetting | ContentSetting | Message content |

ContentSetting: Message content ESocailMsg is the information type
```
public abstract class ContentSetting {
   /**
    * Enumeration of information push
    */
   private ESocailMsg eSocailMsg;
  ....
```

ESocailMsg：社交消息类型枚举

```
G15MSG((byte) 0XFE), //G15 project exclusive, you can ignore it

/**
* Phone
*/
PHONE((byte) 0x00),
/**
* SMS
*/
SMS((byte) 0x01),
/**
* WeChat
*/
WECHAT((byte) 0x02),
/**
* QQ [Official version, Light Chat version, International version]
*/
QQ((byte) 0x03),
/**
* Sina Weibo
*/
SINA((byte) 0x04),
/**
* Facebook
*/
FACEBOOK((byte) 0x05),
/**
* X(Original Twitter)
*/
TWITTER((byte) 0x06),
/**
* Filck
*/
FLICKR((byte) 0x07),
/**
* Linkin
*/
LINKIN((byte) 0x08),
/**
* whatapp
*/
WHATS((byte) 0x09),
/**
* line
*/
LINE((byte) 0x0A),
/**
* instagram
*/
INSTAGRAM((byte) 0x0B),
/**
* snapchat
*/
SNAPCHAT((byte) 0x0C),
/**
* skype
*/
SKYPE((byte) 0x0D),
/**
* gmail
*/
GMAIL((byte) 0x0E),
/**
* DingDING
*/
DINGDING((byte) 0x0F),
/**
* WeChat for Business
*/
WXWORK((byte) 0x10),

/**
* Other notifications other than the above
*/
OTHER((byte) 0x11),
/**
* com.zhiliaoapp.musically
*/
TIKTOK((byte) 0x12),
/**
* org.telegram.messenger
*/
TELEGRAM((byte) 0x13),
/**
* com.connected2.ozzy.c2m
*/
CONNECTED2_ME((byte) 0x14),
/**
* kakao talk
*/
KAKAO_TALK((byte) 0x15),
/**
* police right
*/
SHIELD_POLICE((byte) 0x16),//Ignore the exclusive project
/**
* Messenger under facebook
*/
MESSENGER((byte) 0x17),
```

#### Mobile phone incoming call message notification type

Mobile phone incoming call messages use ContentPhoneSetting, which inherits from ContentSetting. If you pass in both the name and the phone number, the watch will display the name. The contact name can be null
ContentPhoneSetting

| Member name | Type | Describe |
| ------------------ | ------ | ---------- |
| contactName | String | Contact name |
| contectPhoneNumber | String | Contact number |

The following construction method is recommended

```
ContentPhoneSetting contentSetting = new ContentPhoneSetting(ESocailMsg.PHONE, "张三", "010-6635214");
```

#### Mobile phone text message notification type

Mobile phone text messages use ContentSmsSetting, which inherits from ContentSetting. If you pass in both the name and the phone number, the watch will display the name
ContentSmsSetting

| Member name | Type | Describe |
| ------------------ | ------ | ---------- |
| contactName | String | Contact name |
| contectPhoneNumber | String | Contact number |
| content | String | Message content |

The following construction method is recommended

```
ContentPhoneSetting contentSetting = new ContentSmsSetting(ESocailMsg.SMS, "Li Si", "010-6635214", "How's your luck today?");
```

#### Social message type

Social message type uses ContentSocailSetting, which inherits from ContentSetting

```
/**
* @param eSocailMsg Message type
* @param title Message title
* @param content Message content
 */
public ContentSocailSetting(ESocailMsg eSocailMsg, String title, String content) {
    super(eSocailMsg);
    this.title = title;
    this.content = content;
}
```





## Music control

#### Premise

The device needs to support music control. The judgment conditions are as follows:

```kotlin
val musicType == VpSpGetUtil.getVpSpVariInstance(applicationContext).musicType
if(musicType == 1){
//The device supports music control
}else{
//The device does not support music control
}
```

#### Setting music data

The device needs to support music control

###### Interface

```kotlin
settingMusicData(bleWriteResponse, musicData, iMusicControlListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| --------------------- | --------------------- | ---------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| musicData | MusicData | Music data (song name, switch, etc.) |
| iMusicControlListener | IMusicControlListener | Music control listener |

**MusicData** -- Music data

| Parameter name | Type | Describe |
| --------------- | ------ | -------------------------- |
| musicAppId | String | Music appid |
| musicAlbum | String | Music album |
| musicName | String | Music name |
| singerName | String | Singer name |
| palyStatus | Int | Status: 1 Playing status 2 Pause status |
| musicVoiceLevel | Int | Volume level [1-100] |

###### Return data

**IMusicControlListener** -- Return listener for music control data, flag for music control function

```kotlin
/**
* Next song, please perform related operations
*/
fun nextMusic()

/**
* Previous song, please perform related operations
*/

fun previousMusic()

/**
* Pause and play, please perform related operations
*/
fun pauseAndPlayMusic()

/**
* Pause, please perform related operations
*/
fun pauseMusic()

/**
* Play, please perform related operations
*/
fun playMusic()

/**
* Increase the volume, please perform related operations
*/
fun voiceUp()

/**
* Volume down, please perform related operations
*/
fun voiceDown()

/**
* Operation successful
*/
fun oprateMusicSuccess()

/**
* Operation failed
*/
fun oprateMusicFail()
```

###### Example Code

```kotlin
val play = 1 //playing state
val pause = 2 //pausing state
val musicData = MusicData("Jay Chou", "Shanghai 1943", "Fantasy", 80, play )
VPOperateManager.getInstance()
    .settingMusicData(writeResponse, musicData, object : IMusicControlListener {
        override fun oprateMusicSuccess() {
            Logger.t(OperaterActivity.TAG).i("Music-oprateMusicSuccess")
        }

        override fun oprateMusicFail() {
            Logger.t(OperaterActivity.TAG).i("Music-oprateMusicFail")
        }

        override fun nextMusic() {
            Logger.t(OperaterActivity.TAG).i("Music-nextMusic")
        }

        override fun previousMusic() {
            Logger.t(OperaterActivity.TAG).i("Music-previousMusic")
        }

        override fun pauseAndPlayMusic() {
            Logger.t(OperaterActivity.TAG).i("Music-pauseAndPlayMusic")
        }

        override fun pauseMusic() {
            Logger.t(OperaterActivity.TAG).i("Music-pauseMusic")
        }

        override fun playMusic() {
            Logger.t(OperaterActivity.TAG).i("Music-playMusic")
        }

        override fun voiceUp() {
            Logger.t(OperaterActivity.TAG).i("Music-voiceUp")
        }

        override fun voiceDown() {
            Logger.t(OperaterActivity.TAG).i("Music-voiceDown")
        }
    })
```

#### Set the device volume

The device needs to support music control

###### Interface

```kotlin
settingVolume(volumeLevel,bleWriteResponse,iMusicControlListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| --------------------- | --------------------- | -------------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| volumeLevel | Int | The volume value to be set, ranging from 0 to 100 |
| iMusicControlListener | IMusicControlListener | Music control listening |

###### Return data

The same as the return data of [[Set music data](#Set music data return)]

###### Example Code

```kotlin
VPOperateManager.getInstance().settingVolume(50,bleWriteResponse,iMusicControlListener)
```

## Bluetooth call function

#### Register device Bluetooth call function listener-registerBTInfoListener

###### Interface

```
registerBTInfoListener(iDeviceBTInfoListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| --------------------- | --------------------- | -------------------- |
| iDeviceBTInfoListener | IDeviceBTInfoListener | Device Bluetooth call function listener |

###### Return data

**IDeviceBTInfoListener**

```kotlin
/**
* The device does not support BT (Bluetooth 3.0 function). The absence of this callback means that the device supports BT
*/
fun onDeviceBTFunctionNotSupport()

/**
* Device classic Bluetooth setting callback
*
* @param btInfo BT status details
*/
fun onDeviceBTInfoSettingSuccess(btInfo: BTInfo)

/**
* Device BT status setting failed
*/
fun onDeviceBTInfoSettingFailed()

/**
* Device classic Bluetooth reading callback
*
* @param btInfo BT status details
*/
fun onDeviceBTInfoReadSuccess(btInfo: BTInfo)

/**
* Failed to read device BT status
*/
fun onDeviceBTInfoReadFailed()

/**
* Device classic Bluetooth reporting callback
*
* @param btInfo BT status details
*/
fun onDeviceBTInfoReport(btInfo: BTInfo)
```

**BTInfo**

| Parameter name | Type | Describe |
| -------------- | ------- | ------------------------------------------------ |
| status | Int | Device Bluetooth call status 0: disconnected, 1: connected, 2: pairing. |
| isBTOpen | Boolean | Is the device Bluetooth call turned on? |
| isAutoCon | Boolean | Will the device Bluetooth call automatically reconnect? |
| isAudioOpen | Boolean | Is multimedia audio turned on? |
| isHavePairInfo | Boolean | Is there pairing information? |

###### Example Code

```kotlin
//        kotlin code
        VPOperateManager.getInstance().registerBTInfoListener(object :IDeviceBTInfoListener{
            override fun onDeviceBTFunctionNotSupport() {
            }

            override fun onDeviceBTInfoSettingSuccess(btInfo: BTInfo) {
            }

            override fun onDeviceBTInfoSettingFailed() {
            }

            override fun onDeviceBTInfoReadSuccess(btInfo: BTInfo) {
            }

            override fun onDeviceBTInfoReadFailed() {
            }

            override fun onDeviceBTInfoReport(btInfo: BTInfo) {
            }

        })
```



#### Register device Bluetooth call connection status monitor-registerBTConnectionListener

###### Interface

```
registerBTConnectionListener(iDeviceBTConnectionListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| --------------------------- | --------------------------- | ------------------------ |
| iDeviceBTConnectionListener | IDeviceBTConnectionListener | Device Bluetooth call connection status monitor |

###### Return data

**IDeviceBTConnectionListener**

```kotlin
/**
* Start connecting to BT
*/
fun onDeviceBTConnecting() {}

/**
* Device BT is connected
*/
fun onDeviceBTConnected() {}

/**
* Device BT is disconnected
*/
fun onDeviceBTDisconnected() {}

/**
* Device BT connection timeout
*/
fun onDeviceBTConnectTimeout() {}
```

###### Example Code

```kotlin
//        kotlin code
        VPOperateManager.getInstance().registerBTConnectionListener(object :IDeviceBTConnectionListener{
            override fun onDeviceBTConnectTimeout() {
                super.onDeviceBTConnectTimeout()
            }

            override fun onDeviceBTConnected() {
                super.onDeviceBTConnected()
            }

            override fun onDeviceBTConnecting() {
                super.onDeviceBTConnecting()
            }

            override fun onDeviceBTDisconnected() {
                super.onDeviceBTDisconnected()
            }
        })
```



#### Manually connect BT-connectBT

###### Premise

After registering the device Bluetooth call function listener, there is no onDeviceBTFunctionNotSupport callback (that is, the device supports BT function)

###### Interface

```
connectBT(mac, listener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| -------- | --------------------------- | ------------------- |
| mac | String | Bluetooth mac of the device to be connected |
| listener | IDeviceBTConnectionListener | Device connection listener callback |
###### Return data

**IDeviceBTConnectionListener** 

###### Example Code

```kotlin
//        kotlin code
        val mac = "F1:F2:F3:F4:F5"
        VPOperateManager.getInstance().connectBT(mac,object :IDeviceBTConnectionListener{
            
        })
```



#### Manually disconnect BT connection-disconnectBT

###### Premise

The device supports BT and has been connected to BT

###### Interface

```
disconnectBT(String mac, IDeviceBTConnectionListener listener)
```

###### Parameter Explanation

Same as [[Manually connect BT-connectBT](#Manually connect BT-connectBT)] parameters

###### Return data

Same as [[Manually connect BT-connectBT](#Manually connect BT-connectBT)] return data

###### Example Code

```kotlin
//        kotlin code
        val mac = "F1:F2:F3:F4:F5"
        VPOperateManager.getInstance().disconnectBT(mac,object :IDeviceBTConnectionListener{

        })
```



#### Read BT information - readBTInfo

```
readBTInfo(bleWriteResponse, listener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------- | --------------------- | ---------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| listener | IDeviceBTInfoListener | Device connection listening callback |

###### Return data

Same as the return data of [[Manually connect BT-connectBT](#Manually connect BT-connectBT)]

###### Example Code

```kotlin
//kotlin code
VPOperateManager.getInstance().readBTInfo(
    {
        if (it != Code.REQUEST_SUCCESS) {
            Log.e("Test","write cmd failed")
        }
    }, btInfoListener
)
```



#### Set BT status - setBTStatus

###### Premise

The device supports BT and the device BT connection is successful

###### Interface

```kotlin
setBTStatus(boolean isAutoConnect, boolean isBTOpen, boolean isAudioOpen, isClearPairInfo, bleWriteResponse)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------- | ----------------- | -------------------- |
| isAutoConnect | Boolean | Whether the device automatically reconnects |
| isBTOpen | Boolean | Whether the device BT is turned on |
| isAudioOpen | Boolean | Whether the multimedia switch is turned on |
| isClearPairInfo | Boolean | Whether to clear the device pairing information |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |

###### Return data

None

###### Example Code
```kotlin
//kotlin code
VPOperateManager.getInstance().setBTStatus(false, isBTOpen, isAudioOpen, false) {
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}
```



#### Set BT switch status-setBTSwitchStatus

###### Premise

The device supports BT and the device BT connection is successful

###### Interface

```kotlin
setBTSwitchStatus(isBTOpen, bleWriteResponse)
```

###### Parameter explanation

| Parameter name           | Type              | Describe           |
| ---------------- | ----------------- | -------------- |
| isBTOpen         | Boolean           | Whether to open BT |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |

###### Return data

None

###### Example Code

```kotlin
//        kotlin code
        VPOperateManager.getInstance().setBTSwitchStatus(true){
            
        }
```





## Sports function

#### Read sports mode data

###### Premise

The device must support sports mode

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportSportModel
```

###### Interface

```kotlin
readSportModelOrigin(bleWriteResponse, sportModelOriginListener)
```

###### Parameter Explanation

| Parameter name                   | Type                      | Describe                 |
| ------------------------ | ------------------------- | -------------------- |
| bleWriteResponse         | IBleWriteResponse         | Listening for write operations       |
| sportModelOriginListener | ISportModelOriginListener | Read sports mode data monitoring |

###### Return data

**ISportModelOriginListener**

```kotlin
/**
* Returns the progress of reading
*
* @param progress progress value, range [0-1]
*/
fun onReadOriginProgress(progress: Float)

/***
* Returns the details of reading. The location of this package needs to be remembered. When reading data next time, pass in the location of this package to avoid repeated reading
*
* @param day data flag in the watch [0=today, 1=yesterday, 2=the day before yesterday]
* @param date data date, format yyyy-mm-dd
* @param allPackage total number of data packages for the day
* @param currentPackage location of this package
*/
fun onReadOriginProgressDetail(day: Int, date: String?, allPackage: Int, currentPackage: Int)

/**
* Callback of sports mode raw data [header information]
*
* @param sportModelHeadData sports mode raw data [header information]
*/
fun onHeadChangeListListener(sportModelHeadData: SportModelOriginHeadData?)

/**
* Callback for sports model raw data [details]
*
* @param sportModelItemDatas sports model raw data [details]
*/
fun onItemChangeListListener(sportModelItemDatas: List<SportModelOriginItemData?>?)

/**
* Reading completed
*/
fun onReadOriginComplete()
```

**SportModelOriginHeadData**--Head information of sports mode

| Parameter name | Type | Describe |
| ------------ | -------- | ------------------------ |
| date | String | Sports date |
| startTime | TimeData | Start time |
| stopTime | TimeData | Stop time |
| sportTime | Int | Total sports duration |
| stepCount | Int | Total steps in sports |
| sportCount | Int | Total sports volume |
| kcals | Double | Kilocalories consumed in sports |
| distance | Double | Sports distance |
| recordCount | Int | Total number of records |
| pauseCount | Int | Number of pauses |
| pauseTime | Int | Pause duration |
| crc | Int | Data checksum |
| peisu | Int | Pace |
| oxsporttimes | Int | Aerobic exercise time |
| averRate | Int | Average heart rate |
| sportType | Int | Sport type, see ESportType for details |

**SportModelOriginItemData**--Detailed information on sport mode

| Parameter name | Type | Describe |
| ---------- | -------- | -------- |
| date | String | Sport date |
| startTime | TimeData | Start time |
| minute | Int | Sport minute |
| allMinute | Int | Total minutes |
| rate | Int | Heart rate |
| stepCount | Int | Total steps |
| sportCount | Int | Total exercise volume |
| distance | Int | Sport distance |
| kcal | Int | Consumed kilocalories |
| beathPause | Int | Pause flag |
| crc | Int | Verification code |

ESportType--Sport type enumeration

```java
public enum ESportType {
    /**
    * Default sport
    */
    NONE(0),
    /**
    * Outdoor running
    */
    OUTDOOR_RUNNING(1),
    /**
    * Outdoor walking
    */
    OUTDOOR_WALK(2),
    /**
    * Indoor running
    */
    INDOOR_RUNNING(3),
    /**
    * Indoor walking
    */
    INDOOR_WALK(4),
    /**
    * Hiking
    */
    HIKE(5),
    /**
    * Stepper
    */
    TREADMILLS(6),
    /**
    * Outdoor cycling
    */
    OUTDOOR_RIDING(7),
    /**
    * Indoor cycling
    */
    INDOOR_RIDING(8),
    /**
    * Elliptical
    */
    ELLIPTICAL(9),
    /**
    * Rowing machine
    */
    ROWING_MACHINE(10),
    /**
    * Mountaineering
    */
    Mountaineering(11),
    /**
    * Swimming
    */
    SWIM(12),
    /**
    * Sit-Ups
    */
    Sit_Ups(13),
    /**
    * Skiing
    */
    SKI(14),
    /**
    * Rope Skipping
    */
    JUMP_ROPE(15),
    /**
    * Yoga
    */
    YOGA(16),
    /**
    * Table Tennis
    */
    PING_PONG(17),
    /**
    * Basketball
    */
    BASKETBALL(18),
    /**
    * Volleyball
    */
    VOLLEYBALL(19),
    /**
    * Football
    */
    FOOTBALL(20),
    /**
    * Badminton
    */
    BADMINTON(21),
    /**
    * Tennis
    */
    TENNIS(22),
    /**
    * Stair Climbing
    */
    CLIMB_STAIRS(23),
    /**
    * Fitness
    */
    FITNESS(24),
    /**
    * Weightlifting
    */
    WEIGHTLIFTING(25),
    /**
    * Scuba diving
    */
    DIVING(26),
    /**
    * Boxing
    */
    BOXING(27),
    /**
    * Fitness ball
    */
    GYM_BALL(28),
    /**
    * Squat training
    */
    SQUAT_TRAINING(29),
    /**
    * Triathlon
    */
    TRIATHLON(30),
    /**
    * Dance
    */
    DANCE(31),
    /**
    * HIIT
    */
    HIIT(32),
    /**
    * Rock climbing
    */
    ROCK_CLIMBING(33),
    /**
    * Athletics
    */
    SPORTS(34),
    /**
    * Ball games
    */
    BALLS(35),
    /**
    * Fitness games
    */
    FITNESS_GAME(36),
    /**
    * Free time
    */
    FREE_TIME(37),
    /**
    * Aerobics
    */
    AEROBICS(38),
    /**
    * Gymnastics
    */
    GYMNASTICS(39),
    /**
    * Floor exercises
    */
    FLOOR_EXERCISE(40),
    /**
    * Horizontal bar
    */
    HORIZONTALBAR(41),
    /**
    * Parallel bars
    */
    PARALLELBARS(42),
    /**
    * Trampoline
    */
    TRAMPOLINE(43),
    /**
    * Track and field
    */
    TRACKANDFIELD(44),
    /**
    * Marathon
    */
    MARATHON(45),
    /**
    * Push-ups
    */
    PUSH_UPS(46),
    /**
    * Dumbbells
    */
    DUMBBELL(47),
    /**
    * Rugby_FOOTBALL(48),
    /**
    * Handball
    */
    HANDBALL(49),
    /**
    * Baseball_SOFTBALL(50),
    /**
    * Baseball
    */
    BASEBALL(51),
    /**
    * Hockey
    */
    HOCKEY(52),
    /**
    * Golf
    */
    GOLF(53),
    /**
    * Bowling
    */
    BOWLING(54),
    /**
    * Billiards
    */
    BILLIARDS(55),
    /**
    * Rowing
    */
    ROWING(56),
    /**
    * Sailing
    */
    SAILBOAT(57),
    /**
    * Ice skating
    */
    SKATE(58),
    /**
    * Curling
    */
    CURLING(59),
    /**
    * Ice hockey
    */
    PUCK(60),
    /**
    * Sledding
    */
    SLEIGH(61),
    /**
    * Strong Walking
    */
    StrongWalk(62),
    /**
    * Treadmill
    */
    Treadmill(63),
    /**
    * Trail Running
    */
    TrailRunning(64),
    /**
    * Race Walking
    */
    RaceWalking(65),
    /**
    * Mountain Biking
    */
    MountainBiking(66),
    /**
    * BMX
    */
    BMX(67),
    /**
    * Orienteering
    */
    Orienteering(68),
    /**
    * Fishing
    */
    Fishing(69),
    /**
    * Hunting
    */
    Hunt(70),
    /**
    * Skateboarding
    */

    Skateboard(71),
    /**
    * Roller Skating
    */
    Roller Skating(72),
    /**
    * Parkour
    */
    Parkour(73),
    /**
    * Beach Car
    */
    ATV(74),
    /**
    * Motocross(75),
    /**
    * Stair Climbing Machine
    */
    Climbing Machine(76),
    /**
    * Spinning Bike(77),
    /**
    * Indoor Fitness
    */
    Indoor Fitness(78),
    /**
    * Mixed Aerobic
    */
    Mixed Aerobic(79),
    /**
    * Cross Training
    */

    Cross Training(80),
    /**
    * Aerobics
    */
    Bodybuilding Exercise(81),
    /**
    * Group Gymnastics(82),
    /**
    * Kickboxing
    */
    Kickboxing(83),
    /**
    * Strength Training
    */
    Strength Training(84),
    /**
    * Stepping Training
    */
    Stepping Training(85),
    /**
    * Core Training
    */
    Core Training(86),
    /**
    * Flexibility Training
    */
    Flexibility Training(87),
    /**
    * Free Training
    */
    Free Training(88),
    /**
    * Pilates
    */
    Pilates(89),
    /**
    * Battle Rope
    */

    BattleRope(90),
    /**
    * Stretch
    */
    Stretch(91),
    /**
    * Square Dance
    */
    SquareDance(92),
    /**
    * Ballroom Dance
    */
    BallroomDancing(93),
    /**
    * Belly Dance
    */
    BellyDance(94),
    /**
    * Ballet
    */
    Ballet(95),
    /**
    * Street Dance
    */
    HipHop(96),
    /**
    * Zumba
    */
    Zumba(97),
    /**
    * Latin Dance
    */
    LatinDance(98),
    /**
    * Jazz Dance
    */
    Jazz(99),
    /**
    * Hip-Hop Dance
    */
    HipHopDance(100),
    /**
    * Pole Dance
    */
    PoleDancing(101),
    /**
    * Break Dance
    */
    BreakDance(102),
    /**
    * National Dance
    */
    NationalDance(103),
    /**
    * Modern Dance
    */
    ModernDance(104),
    /**
    * Disco
    */
    Disco(105),
    /**
    * Tap Dance
    */
    TapDance(106),
    /*
    /**
    * Wrestling
    */
    Wrestling(107),
    /**
    * Martial Arts
    */
    Martial Arts(108),
    /**
    * Tai Chi
    */
    Tai Chi(109),
    /**
    * Muay Thai
    */

    Muay Thai(110),
    /**
    * Judo
    */
    Judo(111),
    /**
    * Taekwondo
    */
    Taekwondo(112),
    /**
    * Karate
    */
    Karate(113),
    /**
    * Free Fighting
    */
    Free Sparring(114),
    /**
    * Swordsmanship
    */
    Swordsmanship(115),
    /**
    * Jiu-Jitsu
    */
    Jujitsu(116),
    /**
    * Fencing
    */
    Fencing(117),
    /**
    * Beach Soccer
    */
    BeachSoccer(118),
    /**
    * Beach Volleyball
    */
    BeachVolleyball(119),
    /**
    * Softball
    */

    Softball(120),
    /**
    * Squash
    */
    Squash(121),
    /**
    * Croquet(122),
    /**
    * Cricket
    */
    Cricket(123),
    /**
    * Polo
    */
    Polo(124),
    /**
    * Wallball
    */
    Wallball(125),
    /**
    * Sepak Takraw
    */
    TakrawBall(126),
    /**
    * Dodgeball
    */
    Dodgeball(127),
    /**
    * Water Polo
    */
    WaterPolo(128),
    /**
    * Shuttlecock
    */
    Shuttlecock(129),
    /**
    * Indoor Soccer
    */

    IndoorSoccer(130),
    /**
    * SandbagBall
    */
    SandbagBall(131),
    /**
    * BocceBall
    */
    BocceBall(132),
    /**
    * Jaileyball
    */
    Jaileyball(133),
    /**
    * Floorball
    */
    Floorball(134),
    /**
    * Outdoor Rowing
    */
    OutdoorBoating(135),
    /**
    * Kayaking
    */
    Kayak(136),
    /**
    * Dragon Boat
    */
    DragonBoat(137),
    /**
    * PaddleBoard
    */
    PaddleBoard(138),
    /**
    * IndoorFillingWaves(139),
    /**
    * Rafting
    */

    Drifting(140),
    /**
    * Waterskiing
    */
    WaterSkiing(141),
    /**
    * Skiing
    */
    Snowboarding(142),
    /**
    * Snowboarding
    */
    Snowboard(143),
    /**
    * Alpine Skiing
    */
    AlpineSkiing(144),
    /**
    * Cross-country Skiing
    */
    CrossCountrySkiing(145),
    /**
    * OrienteeringSki(146),
    /**
    * Biathlon
    */
    Bathlon(147),
    /**
    * Outdoor Skating
    */
    OutdoorSkating(148),
    /**
    * Indoor Skating
    */
    IndoorSkating(149),
    /**
    * Snowmobile
    */

    SnowCar(150),
    /**
    * Snowmobile
    */
    Snowmobile(151),
    /**
    * Snowshoeing
    */
    Snowshoeing(152),
    /**
    * Hula Hoop
    */
    HulaHoop(153),
    /**
    * Frisbee
    */
    Frisbee(154),
    /**
    * Darts
    */
    Dart(155),
    /**
    * Kite Flying
    */
    FlyAKite(156),
    /**
    * Tug of War
    */
    TugOfWar(157),
    /**
    * ShuttlecockKicking(158),
    /**
    * E-sports
    */
    ESports(159),
    /**
    * WanderingMachine
    */
    WanderingMachine(160),
    /**
    * Swing
    */
    Swing(161),
    /**
    * Shuffleboard
    */
    Shuffleboard(162),
    /**
    * Foosball
    */
    TableSoccer(163),
    /**
    * SomatosensoryGame(164),
    /**
    * Chess
    */
    InternationalChess(165),
    /**
    * Draughts(166),
    /**
    * Go
    */
    Go(167),
    /**
    * Bridge
    */
    Bridge(168),
    /**
    * BoardGames
    */
    BoardGame(169),
    /**
    * Archery
    */
    Archery(170),
    /**
    * EquestrianSports(171),
    /**
    * ClimbingStairs
    */
    ClimbingTheStairs(172),
    /**
    * Driving
    */
    Drive(173),
    /**
    * SeatedPush(174),
    /**
    * Seated Chest Press
    */
    SeatedChestPress(175),
    /**
    * Barbell
    */
    Barbell(176),
    /**
    * Long DistanceRunning(177),
    /**
    * Full SpeedRunning
    */
    FullSpeedRun(178),
    /**
    * Variable SpeedRunning
    */
    VariableSpeedRun(179),
    /**
    * RaceRiding
    */

    RaceRiding(180),
    /**
    * MilitaryChess
    */
    MilitaryChess(181),
    /**
    * Mahjong
    */
    Mahjong(182),
    /**
    * Poker
    */
    Poker(183),
    /**
    * Gobang
    */
    Gobang(184),
    /**
    * Chinese Chess
    */
    ChineseChess(185),
    /**
    * High Jump
    */
    HighJump(186),
    /**
    * Long jump
    */
    LongJump(187),
    /**
    * Spinning top
    */
    SpinningTop(188),
    ;

    private int value;

    ESportType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

```



###### Example Code

```kotlin
VPOperateManager.getInstance().readSportModelOrigin({
            if (it != Code.REQUEST_SUCCESS) {
                Log.e("Test", "write cmd failed")
            }
        }, object : ISportModelOriginListener {
            override fun onReadOriginProgress(progress: Float) {

            }

            override fun onReadOriginProgressDetail(
                day: Int,
                date: String?,
                allPackage: Int,
                currentPackage: Int
            ) {

            }

            override fun onHeadChangeListListener(sportModelHeadData: SportModelOriginHeadData?) {
                Log.e("Test", "sportModelHeadData:${sportModelHeadData.toString()}")
                
            }

            override fun onItemChangeListListener(sportModelItemDatas: MutableList<SportModelOriginItemData>?) {
                Log.e("Test", "sportModelItemDatas:${sportModelItemDatas.toString()}")
               
            }

            override fun onReadOriginComplete() {
                Log.e("Test", "readSportModelOrigin onReadOriginComplete")
            }

        })
```



#### Read sports mode status

###### Premise

The device needs to support sports mode

###### Interface

```
readSportModelState(IBleWriteResponse bleWriteResponse, ISportModelStateListener sportModelStateListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| sportModelStateListener | ISportModelStateListener | Read sports mode status listening |

###### Return data

**ISportModelStateListener**--Sport mode status listening

```kotlin
/**
* Return sports mode status data
*
* @param sportModelStateData
*/
fun onSportModelStateChange(sportModelStateData:SportModelStateData)

/**
* Sports end monitoring
*/
fun onSportStopped()
```

**SportModelStateData**--Sport mode status data

| Parameter name | Type | Describe |
| ------------- | ---------------------- | ------------------ |
| oprateStauts | ECheckWear | Sports mode operation status |
| deviceStauts | ESportModelStateStauts | Sports mode device status |
| sportModeType | Int | Sports mode type |

**ECheckWear**--Sport mode operation status

| Parameter name | Describe |
| ------------- | -------- |
| OPEN_SUCCESS | Open successfully |
| OPEN_FAIL | Open failed |
| CLOSE_SUCCESS | Close successfully |
| CLOSE_FAIL | Close failed |
| READ_SUCCESS | Read successfully |
| READ_FAIL | Read failed |
| UNKONW | Unknown status |

**ESportModelStateStauts**--Device status of sports mode

| Parameter name | Describe |
| ----------------------- | ---------- |
| DEVICE_FREE | Device is free |
| DEVICE_BUSY | Device is busy |
| DEVICE_HAD_START_BEFORE | Device has been turned on |
| UNKNOW | Unknown |

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance()
    .readSportModelState({

    }, object : ISportModelStateListener {
        override fun onSportModelStateChange(sportModelStateData: SportModelStateData) {
        }

        override fun onSportStopped() {
        }
    })
```



#### Turn on sports mode

###### Premise

The device must support sports mode

###### Interface

```
startSportModel(bleWriteResponse, sportModelStateListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| sportModelStateListener | ISportModelStateListener | Read sports mode state listener |

###### Return data

Same as [[Read sports mode status](#Read sports mode status)] Return data consistent

###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance()
.startSportModel({

}, object : ISportModelStateListener {
override fun onSportModelStateChange(sportModelStateData: SportModelStateData) {
}

override fun onSportStopped() {
}
})
```

#### End sports mode

###### Premise

The device must support sports mode and has turned on sports mode

###### Interface

```
stopSportModel(bleWriteResponse, sportModelStateListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| sportModelStateListener | ISportModelStateListener | Read sports mode status listener |

###### Return data

Same as [[Read sports mode status](#Read sports mode status)] Return data one致

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance()
    .stopSportModel({

    }, object : ISportModelStateListener {
        override fun onSportModelStateChange(sportModelStateData: SportModelStateData) {
        }

        override fun onSportStopped() {
        }
    })
```



#### Start multi-sport mode

###### Premise

The device needs to support sports mode

It takes more than 1 minute from the start to the end of multi-sport mode. No sports data will be saved for sports that are less than 1 minute

###### Interface

```
startMultSportModel(bleWriteResponse, sportModelStateListener, sportType)
```

###### Parameter Explanation

| Parameter name          | Type                     | Describe                         |
| ----------------------- | ------------------------ | -------------------------------- |
| bleWriteResponse        | IBleWriteResponse        | Listening for write operations   |
| sportModelStateListener | ISportModelStateListener | Read sports mode status listener |
| sportType               | ESportType               | Sports type                      |

###### Return data

Same as the return data of [[Read sports mode status](#Read sports mode status)]

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance()
    .startMultSportModel({

    }, object : ISportModelStateListener {
        override fun onSportModelStateChange(sportModelStateData: SportModelStateData) {
        }

        override fun onSportStopped() {
        }
    }, ESportType.INDOOR_WALK)
```





## Blood oxygen function

#### Premise

The device needs to support the blood oxygen function, and the judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportSpo2h
```

#### Start measuring blood oxygen-startDetectSPO2H

The device needs to support the blood oxygen function

###### Interface

```
startDetectSPO2H(IBleWriteResponse bleWriteResponse, ISpo2hDataListener spo2HDataListener, ILightDataCallBack lightDataCallBack)
```

###### Parameter Explanation

| Parameter name    | Type               | Describe                                                     |
| ----------------- | ------------------ | ------------------------------------------------------------ |
| bleWriteResponse  | IBleWriteResponse  | Listening for write operations                               |
| spo2HDataListener | ISpo2hDataListener | Blood oxygen operation callback, return blood oxygen data: whether supported, on/off status, blood oxygen value |
| lightDataCallBack | ILightDataCallBack | Original light signal monitoring                             |

###### Return data

**ISpo2hDataListener**--Blood oxygen operation callback

```kotlin
/**
* Return blood oxygen operation data
* @param spo2HData Blood oxygen operation data
*/
fun onSpO2HADataChange(spo2HData:Spo2hData)
```

**spo2HData**--Blood oxygen operation data

| Parameter name   | Type          | Describe                        |
| ---------------- | ------------- | ------------------------------- |
| spState          | ESPO2HStatus  | Blood oxygen function status    |
| deviceState      | EDeviceStatus | Device status                   |
| value            | Int           | Blood oxygen value              |
| isChecking       | Boolean       | Is it being checked             |
| checkingProgress | Int           | Blood oxygen detection progress |
| rateValue        | Int           | Heart rate value                |

**ESPO2HStatus**--Blood oxygen function status

| Parameter name | Describe                       |
| -------------- | ------------------------------ |
| NOT_SUPPORT    | This function is not supported |
| CLOSE          | Closed state                   |
| OPEN           | Opened state                   |
| UNKONW         | Unknown                        |

###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance().startDetectSPO2H({

}, { spo2HData ->
val message = "Blood oxygen-start:\n$spo2HData"
}) { data ->
val message = "Blood oxygen-photoelectric signal:${Arrays.toString(data)}".trimIndent()
}
```



#### End blood oxygen measurement-stopDetectSPO2H

The device needs to support blood oxygen function

###### Interface

```kotlin
stopDetectSPO2H(bleWriteResponse, spo2HDataListener)
```

###### Parameter Explanation

| Parameter name    | Type               | Describe                                                     |
| ----------------- | ------------------ | ------------------------------------------------------------ |
| bleWriteResponse  | IBleWriteResponse  | Listening for write operations                               |
| spo2HDataListener | ISpo2hDataListener | Blood oxygen operation callback, return blood oxygen data: support, on/off status, blood oxygen value |

###### Return data

Same as [[Start measuring blood oxygen-startDetectSPO2H](#Start measuring blood oxygen-startDetectSPO2H)] return data

####### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance().stopDetectSPO2H({

}) { spo2HData ->
val message = "Blood oxygen-end:\n$spo2HData"
}
```

#### Read the blood oxygen automatic detection switch status

The device needs to support the blood oxygen function

###### Interface

```
readSpo2hAutoDetect(bleWriteResponse, allSetDataListener)
```

###### Parameter Explanation

| Parameter name     | Type                | Describe                                         |
| ------------------ | ------------------- | ------------------------------------------------ |
| bleWriteResponse   | IBleWriteResponse   | Listening for write operations                   |
| allSetDataListener | IAllSetDataListener | Blood oxygen automatic detection switch callback |

###### Return data

**IAllSetDataListener**--Blood oxygen automatic detection switch callback

```kotlin
/**
* Return multiple settings data
*
* @param alarmData multiple settings data
*/
fun onAllSetDataChangeListener(alarmData:AllSetData);
```

**AllSetData**--Multiple settings data

| Parameter name | Type          | Describe                                                     |
| -------------- | ------------- | ------------------------------------------------------------ |
| type           | EAllSetType   | The setting type 0x00 represents automatic blood oxygen detection |
| startHour      | Int           | Start hour                                                   |
| startMinute    | Int           | Start minute                                                 |
| endHour        | Int           | End hour                                                     |
| endMinute      | Int           | End minute                                                   |
| oprate         | Int           | Operation: 0 set, 1 read                                     |
| openState      | Int           | Switch state: 0 off 1 on                                     |
| oprateResult   | EAllSetStatus | Setting state                                                |
| isOpen         | Int           | Whether to open the detection                                |

###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance().readSpo2hAutoDetect({
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}, {allSetData->
    
})
```



#### Set the blood oxygen automatic detection switch state

The device needs to support the blood oxygen function

###### Interface

```kotlin
settingSpo2hAutoDetect(bleWriteResponse, allSetDataListener, allSetSetting)
```

###### Parameter Explanation

| Parameter name     | Type                | Describe                                            |
| ------------------ | ------------------- | --------------------------------------------------- |
| bleWriteResponse   | IBleWriteResponse   | Listening for write operations                      |
| allSetDataListener | IAllSetDataListener | Callback of blood oxygen automatic detection switch |
| allSetSetting      | AllSetSetting       | Related settings                                    |

**AllSetSetting**--Related settings

| Parameter name | Type        | Describe                                                     |
| -------------- | ----------- | ------------------------------------------------------------ |
| type           | EAllSetType | Setting type 0x00 represents blood oxygen automatic detection |
| startHour      | Int         | Start hour                                                   |
| startMinute    | Int         | Start minute                                                 |
| endHour        | Int         | End hour                                                     |
| endMinute      | Int         | End minute                                                   |
| oprate         | Int         | Operation: 0 set, 1 read                                     |
| openState      | Int         | Switch state: 0 off 1 on                                     |

###### Return data

The same data returned by [[Read the blood oxygen automatic detection switch status](#Read the blood oxygen automatic detection switch status)].

###### Example Code

```kotlin
//        kotlin code
val setting = 0
val open = 1
val startHour = 22
val startMinute = 0
val endHour = 8
val endMinute  = 0
val allSetSetting = AllSetSetting(EAllSetType.SPO2H_NIGHT_AUTO_DETECT,startHour,startMinute,endHour,endMinute,setting,open)
VPOperateManager.getInstance().settingSpo2hAutoDetect({
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}, { allSetData ->

}, allSetSetting)
```

#### Read daily blood oxygen data

The reading and writing of daily blood oxygen data are returned in [[Read daily data function](#Read daily data function)]

## Blood glucose function

The blood glucose function mainly includes

1. Blood glucose monitoring switch and unit setting
2. Reading of blood glucose data generated by daily wearing of the watch
3. Manual blood glucose measurement
4. Reading and setting of blood glucose private calibration mode
5. Reading and setting of blood glucose multi-calibration mode

#### Blood glucose monitoring switch and unit setting

###### Blood glucose switch and unit setting

```
VPOperateManager.getInstance().changeCustomSetting(writeResponse, ICustomSettingDataListener, customSetting)
```

###### Blood glucose unit switch status and unit reading

```
VPOperateManager.getInstance().readCustomSetting(writeResponse,ICustomSettingDataListener);
```

| Parameter name             | Type                       | Description                             |
| -------------------------- | -------------------------- | --------------------------------------- |
| writeResponse              | IBleWriteResponse          | Command write callback                  |
| ICustomSettingDataListener | ICustomSettingDataListener | Callback for setting success or failure |
| customSetting              | CustomSetting              | Switch settings for related functions   |

Personalized settings callback ICustomSettingDataListener,

When the settings are changed, the OnSettingDataChange method will be called back and will return all parameter setting states of the current watch, including the switch states of each switch in the customSettingData.

```
public interface ICustomSettingDataListener extends IListener {
/**
* Returns personalized settings data
*
* @param customSettingData Personalized settings data
*/
void OnSettingDataChange(CustomSettingData customSettingData);
}
```

CustomSetting

| Property name         | Type              | Description                                                  |
| :-------------------- | ----------------- | ------------------------------------------------------------ |
| bloodGlucoseDetection | EFunctionStatus   | Blood glucose function status: whether it is supported, whether it is turned on. |
| bloodGlucoseUnit      | EBloodGlucoseUnit | Blood glucose unit: default mmol/L, or mgdl                  |



#### Daily blood sugar data reading

Daily blood sugar data reading In the daily data reading interface, the following four interfaces can obtain daily blood sugar data. When the blood sugar monitoring switch is turned on and the wearer is wearing the device, up to three days of daily blood sugar data can be read. Blood sugar data is obtained in the interface IOriginData3Listener.onOriginFiveMinuteListDataChange(originDataList)

1. Read the daily data of the number of days (input parameter watchday)

   ```
    /***
       * If the watch stores data for 3 days
       * Read the original data, one record every 5 minutes, including steps, heart rate, blood pressure, and exercise volume. The reading order is today-yesterday-the day before yesterday. Theoretically, there are 288 records for one day
       *
       * @param bleWriteResponse Listening for write operations
       * @param originDataListener callback for original data. The returned data includes steps, heart rate, blood pressure, and exercise volume
       * @param watchday data storage capacity of the watch (unit: day), depending on the device. After password verification, in the onFunctionSupportDataChange callback, you can get the return value through getWatchday()
       */
       public void readOriginData(IBleWriteResponse bleWriteResponse, IOriginData3Listener originDataListener, int watchday)
   ```

2. Customize differential reading of daily data

   ```
   /**
       * Read the original data. This method can customize the day to read and the number of the day to start reading from to avoid repeated reading
       * , for example, if [yesterday, 150] is set, the reading order is [yesterday {150}-end of yesterday-day before yesterday]
       *
       * @param bleWriteResponse Listening for write operations
       * @param originDataListener callback for original data. The returned data includes step count, heart rate, blood pressure, and exercise volume
       * @param day which day to read. 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on. If the value passed in is yesterday, the reading order is yesterday-day before yesterday-...,
       * @param position the position of the number of reads, up to 288 (5 minutes per line) per day, you can define the position of the number of reads, and the value of this parameter must be greater than or equal to 1
       * @param watchday the data capacity that the watch can store (unit: day), depends on the device. After password verification, in the onFunctionSupportDataChange callback, you can get the return value through getWatchday()
       */
       public void readOriginDataFromDay(IBleWriteResponse bleWriteResponse, IOriginData3Listener originDataListener, int day, int position, int watchday)
   ```

3. Customize the day to read and the number of the day to start reading, and read only the day

   ```
   /***
       * Read the original data. This method can customize the day to read and the number of the day to start reading, and only read the day
       * , for example, if [yesterday, 150] is set, the reading order is [yesterday {150}-end of yesterday]
       *
       * @param bleWriteResponse Listening for write operations
       * @param originDataListener callback for original data. The returned data includes step count, heart rate, blood pressure, and exercise volume
       * @param day which day to read. 0 means today, 1 means yesterday, 2 means the day before yesterday, and so on.
       * @param position the position of the number of reads, up to 288 (5 minutes per line) per day, you can define the position of the number of reads, and the value of this parameter must be greater than or equal to 1
       * @param watchday the data capacity that the watch can store (unit: day), depends on the device. After password verification, in the onFunctionSupportDataChange callback, you can get the return value through getWatchday()
       */
       public void readOriginDataSingleDay(IBleWriteResponse bleWriteResponse, IOriginData3Listener originDataListener, int day, int position, int watchday)
   ```

4. Customize which day to read and which record to start reading from, whether to read only the current day

```
/***
    * Read the original data. This method can customize the day to be read and the number of the day to start reading, whether to read only the current day
    *
    * @param bleWriteResponse Listening for write operations
    * @param originDataListener callback for the original data. The returned data includes steps, heart rate, blood pressure, and exercise volume
    * @param readOriginSetting settings for reading the original data
    */
    public void readOriginDataBySetting(IBleWriteResponse bleWriteResponse, IOriginProgressListener originDataListener, ReadOriginSetting readOriginSetting)
```

##### Daily blood sugar data acquisition callback

This interface will be called back after the data reading for the day is completed. (One OriginData3 data represents a five-minute raw data, and a day has a maximum of 24 hours * 60 minutes / 5 minutes = 288 five-minute raw data), so a maximum of 288 blood sugar data will be generated in a day.

```
public interface IOriginData3Listener extends IOriginProgressListener {

 /**
    * This interface will be called back after the data reading for that day is completed. (One OriginData3 data represents a five-minute raw data, and a maximum of 24 hours a day*60 minutes/5 minutes = 288 five-minute raw data)
    * For example, reading three days of raw data will return a list of five-minute raw data from today, yesterday, and the day before yesterday in sequence.
    * Specifically, it depends on how many days of raw data are read. How many times will this interface be called for each day of reading
    *
    * @param originDataList returns a list of 5-minute data. The heart rate value is an array. The corresponding field is getPpgs(), not getRateValue()
    */
    void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList);
    ...
}
```

OriginData3 is the original data. Daily blood sugar values can be obtained in the data structure.

```

public class OriginData3 extends OriginData {
    ...
    
    /**
    * Blood sugar value
    */
    public float bloodGlucose;

    /**
    * Blood sugar risk level
    */
    public EBloodGlucoseRiskLevel bloodGlucoseRiskLevel;


  ...
}

```

| Parameter name                | Type                   | Unit |
| --------------------- | ---------------------- | -------- |
| bloodGlucose          | float                  | mmol/L   |
| bloodGlucoseRiskLevel | EBloodGlucoseRiskLevel | None  |

```java
public enum EBloodGlucoseRiskLevel {
    /**
    * Low risk
    */
    LOW,
    /**
    * Medium risk
    */
    MIDDLE,
    /**
    * High risk
    */
    HIGH,
    /**
    * No level
    */
    NONE,
}
```

Note: bloodGlucoseRiskLevel is only valid when the device supports blood glucose risk assessment. In other cases, level is useless. Whether the device supports blood glucose risk assessment is determined as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBloodGlucoseRiskAssessment
```



#### Blood glucose measurement

##### Start blood glucose measurement

```
VPOperateManager.getInstance().startBloodGlucoseDetect(writeResponse,listener)
```

| Parameter name | Type                        | Describe                                  |
| -------------- | --------------------------- | ----------------------------------------- |
| writeResponse  | IBleWriteResponse           | Command write callback                    |
| listener       | IBloodGlucoseChangeListener | Blood glucose measurement result callback |

##### Stop blood glucose monitoring

```
VPOperateManager.getInstance().startBloodGlucoseDetect(writeResponse,listener)
```

| Parameter name | Type                        | Describe                                  |
| :------------- | --------------------------- | ----------------------------------------- |
| writeResponse  | IBleWriteResponse           | Command write callback                    |
| listener       | IBloodGlucoseChangeListener | Blood glucose measurement result callback |

##### Blood glucose measurement result callback

###### Blood glucose measurement in progress

```java
/**
* Blood glucose measurement value
*
* @param progress Measurement progress
* @param bloodGlucose Blood glucose value
* @param bloodGlucose Blood glucose risk level, this value is valid only when the device supports blood glucose risk assessment, otherwise invalid
*/
void onBloodGlucoseDetect(int progress, float bloodGlucose,EBloodGlucoseRiskLevel level);

```

###### Blood glucose measurement stopped

```
/**
* Blood glucose measurement stopped
*/
void onBloodGlucoseStopDetect();
```

###### Blood glucose measurement abnormality

```
/**
* Blood glucose measurement failed
*
* @param opt Operation code
* @param status Error status
*/
void onDetectError(int opt, EBloodGlucoseStatus status);
```

***Blood glucose measurement status enumeration***

```
EBloodGlucoseStatus
```

| Definition | Description |
| ------------- | -------------- |
| NONSUPPORT | Not supported |
| ENABLE | Available |
| DETECTING | Measuring |
| LOW_POWER | Low power cannot be measured |
| BUSY | Device is busy |
| WEARING_ERROR | Wearing error |

#### Blood glucose private mode

##### Blood glucose private mode reading

```
VPOperateManager.getInstance().readBloodGlucoseAdjustingData(writeResponse,listener)
```

| Parameter name | Type | Describe |
| ------------- | --------------------------- | -------------------- |
| writeResponse | IBleWriteResponse | Command write callback |
| listener | IBloodGlucoseChangeListener | Blood glucose private mode read callback |

###### Read success callback

```
/**
* Blood glucose private mode setting read callback
*
* @param isOpen Whether it is open
* @param adjustingValue Private mode blood glucose calibration value
*/
void onBloodGlucoseAdjustingReadSuccess(boolean isOpen, float adjustingValue);
```

###### Read failure callback

```
/**
* Blood glucose private mode calibration read failure callback
*/
void onBloodGlucoseAdjustingReadFailed();
```

##### Blood glucose private mode setting
```
VPOperateManager.getInstance().setBloodGlucoseAdjustingData(fValue, isOpen, writeResponse, AbsBloodGlucoseChangeListener)
```

| Parameter name                        | Type                        | Description |
| ----------------------------- | --------------------------- | -------------------- |
| fValue | float | Private mode blood glucose calibration value |
| isOpen | boolean | Whether to enable private mode of blood glucose |
| writeResponse | IBleWriteResponse | Command write callback |
| AbsBloodGlucoseChangeListener | IBloodGlucoseChangeListener | Blood glucose operation callback |

###### Setting success callback

```
/**
* Blood glucose private mode setting success callback
*
* @param isOpen Whether to enable
* @param adjustingValue Private mode blood glucose calibration value
*/
void onBloodGlucoseAdjustingSettingSuccess(boolean isOpen, float adjustingValue);
```

###### Setting failure callback

```
/**
* Blood glucose private mode calibration setting failure callback
*/
void onBloodGlucoseAdjustingSettingFailed();
```

#### Blood glucose multi-calibration mode

###### Premise

The device needs to support blood glucose multi-calibration mode. The judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBloodGlucoseMultipleAdjusting
```

##### Read blood glucose multiple calibration mode data

The device needs to support blood glucose multiple calibration mode

###### Interface

```kotlin
readMultipleCalibrationBGValue(bleWriteResponse, listener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ---------------- | ----------------------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| listener | AbsBloodGlucoseChangeListener | Blood glucose operation callback |

###### Successful read callback

```java
/**
* Successful reading of multiple calibration mode
*
* @param isOpen Whether it is open
* @param breakfast Breakfast
* @param lunch Lunch
* @param dinner Dinner
*/
    void onBGMultipleAdjustingReadSuccess(boolean isOpen, MealInfo breakfast, MealInfo lunch, MealInfo dinner);
```

###### Reading failure callback

```java
/**
* Multi-calibration mode reading failed
*/
void onBGMultipleAdjustingReadFailed();
```

**MealInfo** -- Multi-calibration mode data

| Parameter name | Type | Describe |
| ----------------- | ------- | ------------------------------------------------- |
| index | Int | The tag of the current meal. 1: breakfast, 2: lunch, 3: dinner. |
| bgBeforeMeal | Float | Pre-meal blood glucose calibration value, unit mmol/L |
| bgAfterMeal | Float | Post-meal blood glucose calibration value, unit mmol/L |
| bgBeforeMeal_mgDL | Int | Pre-meal blood glucose calibration value, unit mg/dL |
| bgAfterMeal_mgDL | Int | Post-meal blood glucose calibration value, unit mg/dL |
| beforeMealTime | Int | Pre-meal time (minutes): hours + minutes, for example: 08:30 = 8*60+30 |
| afterMealTime | Int | Post-meal time (minutes): hours + minutes |
| isUnitMmolL | Boolean | Whether the unit is mmol/L, priority setting |

##### Set blood glucose multi-calibration mode data

The device needs to support blood glucose multi-calibration mode
###### Interface

```kotlin
settingMultipleCalibrationBGValue(isOpen, breakfast, lunch, dinner, bleWriteResponse, listener)
```

###### Parameter Explanation

| Parameter name           | Type                          | Describe               |
| ---------------- | ----------------------------- | ------------------ |
| isOpen | Boolean | Whether to open the multi-calibration mode |
| breakfast | MealInfo | Breakfast multi-calibration data |
| lunch | MealInfo | Lunch multi-calibration data |
| dinner | MealInfo | Dinner multi-calibration data |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| listener | AbsBloodGlucoseChangeListener | Blood glucose operation callback |

###### Setting success callback

```java
/**
* Multi-calibration mode setting success
*
*/
void onBGMultipleAdjustingSettingSuccess();
```

###### Setting failure callback

```java
/**
* Multi-calibration mode setting failure
*/
void onBGMultipleAdjustingSettingFailed();
```

**Note:**

1. The time interval between meals must be greater than 2 hours;
2. The time after a meal must be greater than the time before a meal

## Women's function

#### Premise

The device needs to support the women's function. The judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportWomenSetting
```

#### Set female function

The device needs to support the female function
###### Interface

```kotlin
settingWomenState(bleWriteResponse, womenDataListener, womenSetting)
```

###### Parameter Explanation

| Parameter name            | Type               | Describe                                                         |
| ----------------- | ------------------ | ------------------------------------------------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| womenDataListener | IWomenDataListener | Listening for women's status operations, returning the status of the operation |
| womenSetting | WomenSetting | Women's status setting class, including 4 states in total [menstrual period, pregnancy preparation period, pregnancy period, motherhood period] |

**WomenSetting**--Women's status settings

| Parameter name | Type | Describe |
| -------------- | ------------ | ------------------------------- |
| menseLength | Int | The length of a woman's menstrual period, ranging from [4-28 days] |
| menesInterval | Int | The length of the menstrual cycle |
| menesLasterday | TimeData | The time of the last menstrual period, accurate to the day |
| babyBirthday | TimeData | The child's birth date, accurate to the day |
| confinementDay | TimeData | Pregnant women's due date, accurate to the day |
| womenStatus | EWomenStatus | Women's status |
| babySex | ESex | Child's gender: MAN male, WOMAN female |

>1. When the woman's status is [Menstrual period] and [Preparation period], use this constructor WomenSetting(womenStatus, menseLength, menesInterval, menesLasterday)
>2. When the woman's status is [Mommy period], use this constructor WomenSetting(womenStatus, menseLength, menesInterval, menesLasterday, babySex, babyBirthday)
>3. When the woman's status is [Pregnancy period], use this constructor WomenSetting(womenStatus, menesLasterday, confinementDay)

**EWomenStatus**

| Parameter name | Describe |
| -------- | -------- |
| NONE | No status |
| MENES | Menstrual status |
| PREREADY | Pregnancy status |
| PREING | Pregnancy status |
| MAMAMI | Mommy status |

###### Return data

**IWomenDataListener**--Listening for women's status operations

```kotlin
/**
* Returns women's data
*
* @param womenData Women's data
*/
fun onWomenDataChange(womenData:WomenData)
```

**WomenData**--Women's status data

| Parameter name | Type | Describe |
| ------------ | ------------------ | ---------------- |
| oprateStatus | EWomenOprateStatus | Women's status setting status |

**EWomenOprateStatus**--Setting status

| Parameter name | Describe |
| --------------- | ---------------- |
| SETTING_SUCCESS | Successfully set women's status |
| SETTING_FAIL | Failed to set women's status |
| READ_SUCCESS | Successfully read women's status |
| READ_FAIL | Failed to read female status |
| UNKONW | Unknown status |

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().settingWomenState(
    {

    },
    { womenData ->
        val message = "Women Status-Setting:\n$womenData"
}, WomenSetting(EWomenStatus.PREING, TimeData(2016, 3, 1), TimeData(2017, 1, 14))
)
```

#### Read Women Function

The device must support women functions

###### Interface

```kotlin
readWomenState(IBleWriteResponse bleWriteResponse, IWomenDataListener womenDataListener)
```

###### Parameter Explanation

| Parameter name | Type | Describe |
| ----------------- | ------------------ | --------------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| womenDataListener | IWomenDataListener | Listening for women status operations, returning the status of the operation |

###### Return data

Same as the data returned by [[Set female function](#Set female function)]
###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().readWomenState(
    writeResponse
) { womenData ->
    val message = "Female Status - Read:\n$womenData"
}
```





Female status-read## Blood pressure function

The blood pressure function mainly includes

1. Setting and reading the blood pressure automatic monitoring switch

2. Reading the blood pressure data generated by daily wearing of the watch

3. Manual blood pressure measurement

4. Blood pressure mode setting

5. Setting and reading the blood pressure function

### Setting and reading the blood pressure automatic monitoring switch

The reading and setting of the blood pressure function switch are located in the personalized settings, please refer to the document module [[Personalization](#Personalization)]

### Daily blood pressure data reading

Daily blood pressure data reading is located in daily data reading, please refer to the document module [[Read daily data function](## Read daily data function)]

#### Blood pressure data in five-minute original data

OriginData is the original data generated for five minutes, and the daily blood pressure value can be obtained in the data structure. The changed data will be called back in the daily reading interface

```
public interface IOriginData3Listener extends IOriginProgressListener {

/**
* This interface will be called back after the data reading of the day is completed. (One OriginData3 data represents a five-minute raw data, and a maximum of 24 hours a day*60 minutes/5 minutes = 288 five-minute raw data)
* For example, reading three days of raw data will return a list of five-minute raw data for today, yesterday, and the day before yesterday.
* Specifically, depending on how many days of raw data are read, this interface will be called several times.
*
* @param originDataList returns a list of 5-minute data. The heart rate value is an array. The corresponding field is getPpgs(), not getRateValue().
*/
void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList);

...
}
```

The blood pressure value is in the class OriginData

```
public class OriginData {
    ...
    
    /**
    * High voltage value, range [60-300]
    */
    private int highValue;
    /**
    * Low voltage value, range [20-200]
    */
    private int lowValue;
    
  ...
}

```

| Parameter name    | Type | Description                |
| --------- | ---- | ------------------------------ |
| highValue | int | High pressure value, range 60-300 (inclusive) |
| lowValue | int | Low pressure value, range 20-200 (inclusive) |

#### Thirty-minute blood pressure data

OriginHalfHourData is 30 minutes of raw data, including step counting, heart rate, and blood pressure, which is a summary of the original five-minute data every 30 minutes.

OriginHalfHourData

| Member | Type | Description |
| ------------------ | ---------------------- | -------------------- |
| halfHourRateDatas | List<HalfHourRateData> | 30-minute heart rate data array |
| halfHourBps | List<HalfHourBpData> | 30-minute blood pressure data array |
| halfHourSportDatas | List<HalfHourSportData> | 30-minute exercise data array |
| allStep | int | 30-minute total step count |

HalfHourBpData

| Member | Type | Description |
| --------- | -------- | ------------------------------------------------------------ |
| highValue | int | High pressure value, range 60-300 (inclusive) |
| lowValue | int | Low pressure value, range 20-200 (inclusive) |
| date | String | Date, date format yyyy-mm-dd |
| time | TimeDate | The specific time can be accurate to the minute at most, such as 10:00 means the average value of the period 10:00-10:30 |

### Manual blood pressure measurement

#### Turn on manual blood pressure measurement
```
VPOperateManager.getInstance().startDetectBP(writeResponse, listener, detectModel);
```

| Parameter name        | Type                  | Describe                                                         |
| ------------- | --------------------- | ------------------------------------------------------------ |
| writeResponse | IBleWriteResponse | Listening for write operations |
| listener | IBPDetectDataListener | Blood glucose measurement result callback |
| detectModel | EBPDetectModel | Blood pressure measurement mode: <br /> Private mode: DETECT_MODEL_PRIVATE <br /> General mode: DETECT_MODEL_PUBLIC |

#### Stop manual blood pressure measurement

```
VPOperateManager.getInstance().stopDetectBP(writeResponse, detectModel)
```

| Parameter name | Type | Describe |
| :------------ | ----------------- | ------------------------------------------------------------ |
| writeResponse | IBleWriteResponse | Listening for write operations |
| detectModel | EBPDetectModel | Blood pressure measurement mode: <br /> Private mode: DETECT_MODEL_PRIVATE <br /> General mode: DETECT_MODEL_PUBLIC |

#### Blood pressure measurement related callbacks and data

Blood pressure manual measurement callback interface

```
/**
* Listener for blood pressure data return
*/
public interface IBPDetectDataListener extends IListener {
/**
* Return blood pressure data
*
* @param bpData Blood pressure data
*/
void onDataChange(BpData bpData);
}

```

Blood pressure data BpData

| Member | Type | Description |
| -------------- | --------------- | ------------------------------------------------------------ |
| status | EBPDetectStatus | Blood pressure detection status<br />STATE_BP_BUSY: The device is busy, indicating that blood pressure cannot be measured. When receiving this return, please call End blood pressure measurement<br />STATE_BP_NORMAL: Indicates that blood pressure can be measured |
| progress | int | Blood pressure measurement progress, range [0-100] |
| highPressure | int | High pressure value, range [60-300]. If it is not in this range, please prompt the user that the measurement is invalid. |
| lowPressure | int | Low pressure value, range [20-200]. If it is not in this range, please prompt the user that the measurement is invalid. |
| isHaveProgress | boolean | true means the watch has returned progress, false means no progress. A watch without progress will return data 55 seconds after starting measurement. |

### Blood pressure mode setting

#### Blood pressure measurement mode setting

```
VPOperateManager.getInstance().settingDetectBP(writeResponse, listener, bpSetting)
```

| Parameter name        | Type                   | Description          |
| ------------- | ---------------------- | ------------------------ |
| writeResponse | IBleWriteResponse | Listening for write operations |
| listener | IBPSettingDataListener | Blood pressure mode setting callback |
| bpSetting | BpSetting | Blood pressure private mode setting parameters |

Blood pressure setting class BpSetting

| Member | Type | Description |
| ------------------ | ------- | -------------------------------- |
| isOpenPrivateModel | boolean | Whether the private mode is turned on for automatic measurement |
| high | int | User's private high pressure value |
| low | int | User's private low pressure value |
| isAngioAdjuste | boolean | Whether dynamic blood pressure calibration is turned on |

**When isOpenPrivateModel is set to true and isAngioAdjuste is set to false, it means that the *blood pressure private mode is turned on at this time***

**When isOpenPrivateModel is set to false and isAngioAdjuste is set to true, it means that the *blood pressure dynamic adjustment mode is turned on at this time***

#### Blood pressure measurement mode reading

```
VPOperateManager.getInstance().readDetectBP(writeResponse, listener)
```

| Parameter name | Type | Description |
| ------------- | ---------------------- | ---------------- |
| writeResponse | IBleWriteResponse | Listening for write operations |
| listener | IBPSettingDataListener | Blood pressure mode reading callback |

#### Cancel blood pressure dynamic debugging mode

```
boolean isOpenPrivateModel = false;
boolean isAngioAdjuste = true;
BpSetting bpSetting = new BpSetting(isOpenPrivateModel, 111, 88);
//Whether to enable dynamic blood pressure adjustment mode, the function flag is returned in the password verification
bpSetting.setAngioAdjuste(isAngioAdjuste);
VPOperateManager.getInstance().cancelAngioAdjust(writeResponse, new IBPSettingDataListener() {
@Override
public void onDataChange(BpSettingData bpSettingData) {
String message = "BpSettingData:\n" + bpSettingData.toString();
Logger.t(TAG).i(message);
sendMsg(message, 1);
}
}, bpSetting);
```

Blood pressure mode setting read listener, when setting or reading is successful, IBPSettingDataListener.onDataChange method will be called back

```
/**
* Listener returned by device saving private blood pressure data
*/
public interface IBPSettingDataListener extends IListener {
/**
* Privately saved blood pressure data return
*
* @param bpSettingData Privately saved blood pressure data
*/
void onDataChange(BpSettingData bpSettingData);
}
```

BpSettingData

| Member | Type | Description |
| --------------------- | -------------- | ------------------------------------------------------------ |
| status | EBPStatus | Status of dynamic blood pressure calibration: |
| model | EBPDetectModel | Blood pressure measurement mode: <br />DETECT_MODEL_PRIVATE: Private mode <br />DETECT_MODEL_PUBLIC: Universal mode |
| highPressure | int | Private high pressure value saved by the device. If it is 0, it means that private data needs to be set first when measuring in private mode. |
| lowPressure | int | Private low pressure value saved by the device, range [20-200] |
| angioAdjusterProgress | int | Progress of dynamic blood pressure adjustment [0-100] |
| isAngioAdjuster | boolean | Status of dynamic blood pressure adjustment. True means that the current state is dynamic blood pressure adjustment |

Blood pressure calibration status:

EBPStatus

| SETTING_NORMAL_SUCCESS | Successfully closed private mode [also known as setting universal mode] |
| ----------------------------- | ---------------------------------- |
| SETTING_NORMAL_FAIL | Failed to turn off private mode [also known as setting general mode] |
| SETTING_PRIVATE_SUCCESS | Successfully set private mode |
| SETTING_PRIVATE_FAIL | Failed to set private mode |
| READ_SUCCESS | Successfully read blood pressure mode |
| READ_FAIL | Failed to read blood pressure mode |
| CANCLE_ANGIO_ADJUSTER_SUCCESS | Successfully canceled dynamic blood pressure adjustment |
| CANCLE_ANGIO_ADJUSTER_FAIL | Failed to set private mode |
| ANGIO_ADJUSTER_ING | Dynamic blood pressure calibration in progress |
| ANGIO_ADJUSTER_FAIL | Dynamic blood pressure calibration failed |
| ANGIO_ADJUSTER_SUCCESS | Dynamic blood pressure calibration successful |
| ANGIO_ADJUSTER_DEVICE_BUSY | Device busy during dynamic blood pressure calibration |
| UNKONW | Unknown status |

### Blood pressure function settings and reading

#### Reading blood pressure function

```
VPOperateManager.getInstance().readBpFunctionState(writeResponse, IBPFunctionListener)
```

#### Set the blood pressure function

```
VPOperateManager.getInstance().settingBpFunctionState(writeResponse, IBPFunctionListener, isOpen)
```

Function interface and parameter description

| Parameter name | Type | Description |
| ------------------- | ------------------- | -------------------------------- |
| writeResponse | IBleWriteResponse | Listening for write operations |
| IBPFunctionListener | IBPFunctionListener | Blood pressure function read setting callback listener |
| isOpen | boolean | Whether to open true: open, false: close |

IBPFunctionListener

```
/**
* Blood pressure function status return
*/
public interface IBPFunctionListener extends IListener {
/**
* Return the status of the blood pressure function
*
* @param bpFunctionData Blood pressure function status
*/
void onDataChange(BpFunctionData bpFunctionData);
}
```

BpFunctionData: Blood pressure function status

| Cost name | Type | Description |
| --------- | ------- | ---------------- |
| isSupport | boolean | Whether blood pressure detection is supported |
| isOpen | boolean | Whether blood pressure function is turned on |

## Alarm function

Alarms are divided into three categories:

1. Ordinary alarms (only 3 are supported)

2. Scene alarms (20 are supported)

3. Text alarms (10 are supported)

1. The successfully connected device only supports one type of alarm. When the user performs a device password verification operation, the device will update and report the device function support type, including the alarm type. (When the device performs a password verification, the incoming listener [IDeviceFuctionDataListener] will report the alarm type [Please refer to Password Verification](#Verify password operation))
The alarm includes the functions of adding, deleting, modifying and checking the alarm.

### Ordinary alarm clock

AlarmSetting: Alarm clock entity class

```
public class AlarmSetting {
private int alarmTime;
private boolean isOpen;

/**
* Hour, minute, whether to switch
*
* @param alarmHour
* @param alarmMinute
* @param isOpen
*/
public AlarmSetting(int alarmHour, int alarmMinute, boolean isOpen) {
this.alarmTime = alarmHour * 60 + alarmMinute;
this.isOpen = isOpen;
}
```

| Member name | Type    | Description                                                  |
| ----------- | ------- | ------------------------------------------------------------ |
| alarmTime   | int     | Alarm time = (hour * 60 + minute)                            |
| isOpen      | boolean | Whether to open, true means the alarm is on, false means the alarm is off |

#### Read the alarm list

Example:

```
VPOperateManager.getInstance().readAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    String message = "Read Alarm:\n" + alarmData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
```

| Parameter name        | Type               | Description                                              |
| ------------- | ------------------ | ------------------------------------------------------------ |
| writeResponse | IBleWriteResponse | Command write callback |
| listener | IAlarmDataListener | Alarm data callback, this method will be called back when reading, setting and alarm status change |

AlarmData: Alarm list encapsulation class

| Member name | Type | Description |
| ---------------- | ------------------ | ------------------------------------------------------------ |
| status | EAalarmStatus | Status after operating the alarm: <br />SETTING_SUCCESS: Operation successful <br />SETTING_FAIL: Operation failed <br />READ_SUCCESS: Read successfully <br />READ_FAIL: Read failed <br />UNKONW: Unknown exception |
| alarmSettingList | List<AlarmSetting> | Alarm list |

#### Set the alarm

Example:

```
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
```



### Scene Alarm (New Alarm)

The difference between scene alarm and ordinary alarm is that the watch can set more alarms (up to 20 scene alarms) and the alarm content is richer, and the watch supports corresponding scene icons.

Alarm2Setting: Scene alarm

| Member name | Type | Description |
| ---------------- | ------- | ------------------------------------------------------------ |
| MAFlag | String | Alarm unique flag |
| BluetoothAddress | String | Alarm Bluetooth address |
| alarmId | int | Alarm serial number, serial number range is [1-20] |
| alarmHour | int | All saved are 24-hour system, hour |
| alarmMinute | int | All saved are 24-hour system, minute |
| repeatStatus | | Alarm repeat status (Monday, Tuesday, Wednesday...weekend), <br/> <p><br/> Required format is 7-bit string composed of (0,1), from right to left Monday, Tuesday,...Saturday, Sunday <br/> <p><br/> 1100000-means Sunday, Saturday <br/> <p><br/> 0000011-Tuesday, Monday <br/> <p><br/> If you want to set a non-repeating alarm, set it to 0000000<br/> |
| unRepeatDate | String | When the alarm is in non-repeating state, set the date.<br/> <p><br/> The required format is xxxx-xx-xx,<br/> <p><br/>If you want to set a repeating alarm, set it to 0000-00-00 |
| scene | int | Alarm scene [0-20] There are 21 scenes in total, and the default is the normal alarm icon<br /><br />* 0: Alarm icon * 1: Sleep * 2: Sit up * 3: Drink water * 4: Take medicine * 5: Date * 6: Read * 7: Movie * 8: Music * 9: Shopping * 10: Haircut * 11: Birthday * 12: Propose * 13: Work * 14: Parenting * 15: Parent-child travel * 16: Save money * 17: See a doctor * 18: Dog walking * 19: Fishing * 20: Travel |
| isOpen | boolean | Whether it is turned on, true means the alarm is on, false means the alarm is off |

EMultiAlarmOprate: Scene alarm operation status (enumeration type). When the user adds, deletes, modifies, or queries the alarm, the status will be returned in the relevant monitoring.

| Type | Description |
| -------------------------------- | ----------------------------- |
| SETTING_SUCCESS | Setting success [Modify, add] |
| SETTING_FAIL | Setting failure [Modify, add] |
| CLEAR_SUCCESS | Delete success |
| CLEAR_FAIL | Delete failure |
| READ_SUCCESS | Read success |
| READ_FAIL | Read failure |
| READ_SUCCESS_NULL | Read success but no alarm |
| READ_SUCCESS_SAVE | Read success and save to local |
| READ_SUCCESS_SAME_CRC | Read CRC is consistent, indicating that the alarm has not changed |
| ALARM_FULL | The number of alarms has reached the upper limit |
| ALARM_REPORT | Alarm report |
| ALARM_REPORT_DATE_ERROR | Alarm report but data error |
| DEVICE_ALARM_MODIFY | Modify the alarm on the device |
| DEVICE_ADD_ONE_TEXT_ALARM | Add an alarm to the device |
| DEVICE_DELETE_ONE_TEXT_ALARM, | Delete an alarm on the device |
| DEVICE_TEXT_ALARM_SWITCH_CHANGED | The device text alarm switch status has changed |
| UNKONW | Unknown operation |

AlarmData2: Scene alarm list encapsulation class

| Member name | Type | Description |
| ----------------- | ------------------- | ------------------ |
| status | EMultiAlarmOprate | Operation status of the scene alarm |
| alarm2SettingList | List<Alarm2Setting> | Scene alarm list |

#### Read scene alarm

Usage example:
```
VPOperateManager.getInstance().readAlarm2(writeResponse, new IAlarm2DataListListener() {
    @Override
    public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
        String message = "读取闹钟[新版]:\n" + alarmData2.toString();
        Logger.t(TAG).i(message);
        sendMsg(message, 1);
    }
});
```

#### Set scene alarm

Usage example:

```
private Alarm2Setting getMultiAlarmSetting() {
        int hour = 16;
        int minute = 33;
        int scene = 1;
        boolean isOpen = true;
        String repestStr = "1000010";
        String unRepeatDdate = "0000-00-00";
        return new Alarm2Setting(hour, minute, repestStr, scene, unRepeatDdate, isOpen);
    }
....

Alarm2Setting alarm2Setting = getMultiAlarmSetting();
VPOperateManager.getInstance().addAlarm2(writeResponse, new IAlarm2DataListListener() {
    @Override
    public void onAlarmDataChangeListListener(AlarmData2 alarmData2) {
        String message = "添加闹钟[新版]:\n" + alarmData2.toString();
        Logger.t(TAG).i(message);
        sendMsg(message, 1);
    }
}, alarm2Setting);
```

#### Delete scene alarm

Usage example:

```
private Alarm2Setting getMultiAlarmSetting() {
        int hour = 16;
        int minute = 33;
        int scene = 1;
        boolean isOpen = true;
        String repestStr = "1000010";
        String unRepeatDdate = "0000-00-00";
        return new Alarm2Setting(hour, minute, repestStr, scene, unRepeatDdate, isOpen);
    }

....
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
```



#### Modify scene alarm

Usage example:

```
private Alarm2Setting getMultiAlarmSetting() {
        int hour = 16;
        int minute = 33;
        int scene = 1;
        boolean isOpen = true;
        String repestStr = "1000010";
        String unRepeatDdate = "0000-00-00";
        return new Alarm2Setting(hour, minute, repestStr, scene, unRepeatDdate, isOpen);
    }


....
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
```

### Text alarm

Text alarm supports up to 10 groups. TextAlarm2Setting (text) inherits from Alarm2Setting (scene) and has an additional alarm prompt text description.

```
public class TextAlarm2Setting extends Alarm2Setting {

    /**
     * Alarm information
     */
    private String content;
    ...
```

TextAlarmData：Text alarm list encapsulation class

| Member name           | Type                    | Description                 |
| --------------------- | ----------------------- | --------------------------- |
| oprate                | EMultiAlarmOprate       | Text alarm operation status |
| textAlarm2SettingList | List<TextAlarm2Setting> | Text alarm list             |

ITextAlarmDataListener: Listener callback for text alarm operation. This listener will be called back when adding, deleting, modifying, or checking text alarms on a watch or mobile phone.

```
/**
* Author: YWX
* Date: 2021/9/24 14:40
* Description: Text alarm operation callback
*/
interface ITextAlarmDataListener : IListener {

    /**
    * Alarm data callback
    *
    * @param textAlarmData alarm data
    */
    fun onAlarmDataChangeListListener(textAlarmData: TextAlarmData?)
}
```

#### Read text alarm

Usage example:

```
VPOperateManager.getInstance().readTextAlarm(writeResponse, new ITextAlarmDataListener() {
    @Override
    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
        EMultiAlarmOprate OPT = textAlarmData.getOprate();
        boolean isOk = OPT == EMultiAlarmOprate.READ_SUCCESS ||
                OPT == EMultiAlarmOprate.READ_SUCCESS_SAME_CRC ||
                OPT == EMultiAlarmOprate.READ_SUCCESS_SAVE;
        if (isOk) {
            mSettings.clear();
            mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
            mAdapter.notifyDataSetChanged();
        }
        showMsg(isOk ? "Read text alarm successfully" : "Read text alarm failed");
    }
});
```

| Parameter name                 | Type                   | Description |
| ---------------------- | ---------------------- | ---------------- |
| writeResponse          | IBleWriteResponse      | Listening for write operations   |
| ITextAlarmDataListener | ITextAlarmDataListener | Text alarm changed callback |

#### Add text alarm

Usage example:

```java
  private TextAlarm2Setting getTextAlarm2Setting() {
        String content = mEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            content = "大郎，该吃饭了 @^_^@ !";
        }
        String strHour = etHour.getText().toString();
        String strMinute = etMinute.getText().toString();
        TextAlarm2Setting setting = new TextAlarm2Setting();
        setting.setOpen(true);
        setting.setRepeatStatus("1111111");
        setting.setUnRepeatDate("0000-00-00");
        setting.setAlarmHour(TextUtils.isEmpty(strHour) ? new Random().nextInt(24) : Integer.parseInt(strHour));
        setting.setAlarmMinute(TextUtils.isEmpty(strMinute) ? new Random().nextInt(60) : Integer.parseInt(strMinute));
        setting.setContent(content);
        return setting;
    }

....
TextAlarm2Setting setting = getTextAlarm2Setting();
VPOperateManager.getInstance().addTextAlarm(writeResponse, new ITextAlarmDataListener() {
    @Override
    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
        Logger.t(TAG).e("Add alarm --》" + textAlarmData.toString());
        EMultiAlarmOprate OPT = textAlarmData.getOprate();
        showMsg("Add alarm --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "Success" : "Failure"));
        if (OPT == EMultiAlarmOprate.ALARM_FULL) {
        showMsg("Alarms are full (up to ten can be added)");
        } else if (OPT == EMultiAlarmOprate.SETTING_SUCCESS) {
        showMsg("Alarms added successfully");
        mSettings.clear();
        mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
        mAdapter.notifyDataSetChanged();
        } else if (OPT == EMultiAlarmOprate.SETTING_FAIL) {
        showMsg("Failed to add alarm");
        }
    }
}, setting);
```

| Parameter name                 | Type                   | Description    |
| ---------------------- | ---------------------- | ------------------ |
| writeResponse          | IBleWriteResponse      | Listening for write operations     |
| ITextAlarmDataListener | ITextAlarmDataListener | Text alarm change callback |
| setting | TextAlarm2Setting | Alarm entity to be added |

#### Delete text alarm

Usage example:

```
VPOperateManager.getInstance().deleteTextAlarm(writeResponse, new ITextAlarmDataListener() {
    @Override
    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
        EMultiAlarmOprate OPT =  textAlarmData.getOprate();
        if(OPT == EMultiAlarmOprate.CLEAR_SUCCESS) {
            showMsg("Alarm deleted successfully");
            mSettings.clear();
            mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
            mAdapter.notifyDataSetChanged();
        } else {
            showMsg("Deletion failed");
        }
    }
}, textAlarm);
```

| Parameter name                 | Type                   | Description    |
| ---------------------- | ---------------------- | ------------------ |
| writeResponse          | IBleWriteResponse      | Listening for write operations     |
| ITextAlarmDataListener | ITextAlarmDataListener | Text alarm change callback |
| textAlarm | TextAlarm2Setting | Alarm entity to be deleted |

#### Modify text alarm

Usage example:

```
VPOperateManager.getInstance().modifyTextAlarm(writeResponse, new ITextAlarmDataListener() {
    @Override
    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
        showMsg("Modify alarm --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "Success" : "Failure"));
        mSettings.clear();
        mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
        mAdapter.notifyDataSetChanged();
    }
}, setting);
```

| Parameter name                 | Type                   | Description |
| ---------------------- | ---------------------- | ------------------ |
| writeResponse          | IBleWriteResponse      | Listening for write operations     |
| ITextAlarmDataListener | ITextAlarmDataListener | Text alarm change callback |
| textAlarm | TextAlarm2Setting | Alarm entity to be modified |

## Weather function

When the watch supports weather, (the listener [deviceFunctionDataListener] passed in when the device performs password verification will report whether the weather function exists [please refer to Password Verification](#Verify Password Operation))
Weather includes the following functions

1. Weather data setting

2. Weather switch and unit setting

### Weather data setting

Weather setting call method

```
VPOperateManager.getInstance().settingWeatherData(writeResponse, weatherData, IWeatherStatusDataListener)
```

| Parameter name        | Type                       | Description      |
| ------------- | -------------------------- | -------------------- |
| writeResponse | IBleWriteResponse | Command write callback |
| weatherData | WeatherData | Weather data |
| listener | IWeatherStatusDataListener | Weather data setting result callback |

WeatherData: Weather data

| Member name | Type | Description |
| --------------------- | ----------------------- | ------------------------------------------------------------ |
| crc | int | crc can be spliced ​​into the CRC of the data according to the update time or the entire data, and it is only necessary to ensure that it is unique <br /> (When the crc is the same, there is no need to update the weather data, which can avoid updating the weather data too frequently) |
| cityName | String | City name, UTF-8 encoding of the specific city |
| source | int | The source of weather data, such as Yahoo or Hefeng, (optional) |
| timeBean | TimeData | Last updated time |
| weatherEvery3HourList | List<WeatherEvery3Hour> | List of weather forecasts every 3 hours |
| weatherEverdayList | List<WeatherEveryDay> | Daily weather forecast list |

WeatherEvery3Hour: Three-hour weather forecast

| Member name | Type | Description |
| ------------ | -------- | ------------------------------------------------------------ |
| timeBean | TimeData | Current time, year, month, day, hour, minute, accurate to hour, minute, second |
| temperatureF | int | Fahrenheit |
| temperatureC | int | Celsius |
| yellowLevel | int | Ultraviolet intensity index |
| weatherState | int | Weather status, this value is an int value within a specified range, the value range is determined as follows:<br/> 0-4 Sunny<br/> 5-12 Sunny to cloudy<br/> 13-16 Overcast<br/> 17-20 Showers<br/> 21-24 Thunderstorm<br/> 25-32 Hail<br/> 33-40 Light rain<br/> 41-48 Moderate rain<br/> 49-56 Heavy rain<br/> 57-72 Heavy rain<br/> 73-84 Light snow<br/> 85-100 Heavy snow<br/> 101-155 Cloudy |
| windLevel | String | Wind direction level. If the wind force is a range value, please connect it with ‘-’, such as "3-5"; if it is a single value, just "3" |
| canSeeWay | double | Visibility unit m, 3.16 |

WeatherEveryDay: Daily weather forecast

| Member name | Type | Description |
| -------------------- | -------- | ------------------------------------------------------------ |
| timeBean | TimeData | Year Month Day Hour Minute |
| temperatureMaxF | int | Maximum Fahrenheit |
| temperatureMinF | int | Minimum Fahrenheit |
| temperatureMaxC | int | Maximum Celsius |
| temperatureMinC | int | Minimum Celsius |
| yellowLevel | int | UV intensity index |
| weatherStateWhiteDay | int | Daytime weather status, this value is an int value within a specified range, the value range is as follows:<br/> 0-4 Sunny<br/> 5-12 Sunny to cloudy<br/> 13-16 Overcast<br/> 17-20 Showers<br/> 21-24 Thunderstorm<br/> 25-32 Hail<br/> 33-40 Light rain<br/> 41-48 Moderate rain<br/> 49-56 Heavy rain<br/> 57-72 Torrential rain<br/> 73-84 Light snow<br/> 85-100 Heavy snow<br/> 101-155 Overcast |
| weatherStateNightDay | int | Nighttime weather status, this value is an int value within a specified range, the value range is as follows:<br/> 0-4 Sunny<br/> 5-12 Sunny to cloudy<br/> 13-16 Overcast<br/> 17-20 Showers<br/> 21-24 Thunderstorm<br/> 25-32 Hail<br/> 33-40 Light rain<br/> 41-48 Moderate rain<br/> 49-56 Heavy rain<br/> 57-72 Heavy rain<br/> 73-84 Light snow<br/> 85-100 Heavy snow<br/> 101-155 Overcast |
| windLevel | String | Wind direction level. If the wind force is a range value, please connect it with ‘-’, such as "3-5"; if it is a single value, just "3" |
| canSeeWay | double | Visibility unit m, 3.16 |

IWeatherStatusDataListener: callback for weather data setting results. This method will be called back when the weather data is set successfully, or the weather switch or weather unit changes.

```
/**
* Listening to weather data, returning the status of the operation
*/
public interface IWeatherStatusDataListener extends IListener {
    /**
    * Callback of weather data
    *
    * @param weatherStatusData
    */
    void onWeatherDataChange(WeatherStatusData weatherStatusData);
}
    /**
    * Successfully set weather status
    */
    SETTING_STATUS_SUCCESS,
    /**
    * Failed to set weather status
    */
    SETTING_STATUS_FAIL,
    /**
    * Successfully set weather data
    */
    SETTING_CONTENT_SUCCESS,
    /**
    * Failed to set weather data
    */
    SETTING_CONTENT_FAIL,
    /**
    * Successfully read
    */
    READ_SUCCESS,
    /**
    * Failed to read
    */
    READ_FAIL,
    /**
    * Unknown
    */
    UNKONW;
```

WeatherStatusData: Weather status data

| Member name | Type | Description |
| ----------- | -------------------- | ------------------------------------------------------------ |
| oprate | EWeatherOprateStatus | Weather operation status: <br /> SETTING_STATUS_SUCCESS: Successfully set weather status <br /> SETTING_STATUS_FAIL: Failed to set weather status <br /> SETTING_CONTENT_SUCCESS: Successfully set weather data <br /> SETTING_CONTENT_FAIL: Failed to set weather data <br /> READ_SUCCESS: Successfully read <br /> READ_FAIL: Failed to read <br /> UNKONW: Unknown status |
| crc | int | Current weather crc |
| isOpen | boolean | Whether to open the weather function |
| weatherType | EWeatherType | Weather type (Fahrenheit, Celsius): <br /> C: Celsius <br /> F: Fahrenheit |

Weather setting code example:

```
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
VPOperateManager.getInstance().settingWeatherData(writeResponse, weatherData, new IWeatherStatusDataListener() {
    @Override
    public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
        String message = "settingWeatherData onWeatherDataChange read:\n" + weatherStatusData.toString();
        Logger.t(TAG).i(message);
        sendMsg(message, 1);
    }
});
```



### Weather switch and unit settings

```
VPOperateManager.getInstance().settingWeatherStatusInfo(writeResponse, weatherStatusSetting, listener)
```

| Parameter name | Type | Description |
| -------------------- | -------------------------- | -------------------- |
| writeResponse | IBleWriteResponse | Command write callback |
| weatherStatusSetting | WeatherStatusSetting | Weather switch and unit settings |
| listener | IWeatherStatusDataListener | Weather data setting result callback |

WeatherStatusSetting: Weather unit and switch settings

| Member name | Type | Description |
| -------- | ------------ | ------------------------------------------------------------ |
| crc | int | Current weather crc, when setting weather, if weather data has not changed crc, please pass in the last saved weather crc value |
| isOpen | boolean | Whether to turn on the weather function, true: on, false: off |
| eWeather | EWeatherType | Type of weather (Fahrenheit, Celsius):<br />C: Celsius<br />F: Fahrenheit |

Weather unit and switch code examples:
```
WeatherStatusSetting weatherStatusSetting = new WeatherStatusSetting(crc, true, EWeatherType.C);
VPOperateManager.getInstance().settingWeatherStatusInfo(writeResponse, weatherStatusSetting, new IWeatherStatusDataListener() {
    @Override
    public void onWeatherDataChange(WeatherStatusData weatherStatusData) {
        String message = "settingWeatherStatusInfo onWeatherDataChange read:\n" + weatherStatusData.toString();
        Logger.t(TAG).i(message);
    }
});
```





## Find phone function

#### Set the phone listener

###### Interface

```kotlin
settingFindPhoneListener(findPhonelistener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| ----------------- | ------------------ | -------------- |
| findPhonelistener | IFindPhonelistener | Find phone listener |

###### Return data

**IFindPhonelistener**--Find phone listener

```kotlin
/**
* When receiving this callback, the phone should give corresponding feedback to remind the user, such as vibration, ringing, etc.
*/
fun findPhone()
```

###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance().settingFindPhoneListener {
val message = "(The bracelet is listening to find the phone)-where is the phone, make some noise!"

}
```





## Long-sit function

#### Premise

The device needs to support the long-sit function, and the judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportLongseat
```

#### Read long-sit settings

Long-sit support needs to be set

###### Interface

```
readLongSeat(bleWriteResponse, longSeatDataListener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| -------------------- | --------------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| longSeatDataListener | ILongSeatDataListener | Long-sit data listening |

###### Return data

**ILongSeatDataListener** --Long-sit data listening

```kotlin
/**
* Returns sedentary data
*
* @param longSeat Sedentary data
*/
fun onLongSeatDataChange(longSeat:LongSeatData)
```

**LongSeatData**--Sedentary data

| Parameter name | Type | Description |
| ----------- | --------------- | ------------------------------ |
| status | ELongSeatStatus | Sedentary operation status |
| startHour | Int | Start time hour |
| startMinute | Int | Start time minute |
| endHour | Int | End time hour |
| endMinute | Int | End time minute |
| threshold | Int | Threshold: How long does it take for the watch to remind you if you haven't moved |
| isOpen | Boolean | Switch status |

**ELongSeatStatus**--Sedentary operation status

| Parameter name | Description |
| ------------- | ------------ |
| OPEN_SUCCESS | Open successfully |
| OPEN_FAIL | Open failed |
| CLOSE_SUCCESS | Close successfully |
| CLOSE_FAIL | Close failed |
| READ_SUCCESS | Read successfully |
| READ_FAIL | Read failed |
| UNSUPPORT | This function is not supported |
| UNKONW | Unknown status |

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().readLongSeat(
    writeResponse
) { longSeat ->
    val message = "Set Sedentary-Read:\n$longSeat"

}
```



#### Setting long sitting

The device needs to support the long sitting function

###### Interface

```
settingLongSeat(IBleWriteResponse bleWriteResponse, LongSeatSetting longSeatSetting, ILongSeatDataListener longSeatDataListener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| -------------------- | --------------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| longSeatSetting | LongSeatSetting | Long sitting setting |
| longSeatDataListener | ILongSeatDataListener | Long sitting data listening |

**LongSeatSetting**--Long sitting setting

| Parameter name | Type | Description |
| ----------- | ------- | ------------------------------ |
| startHour | Int | Hour of start time |
| startMinute | Int | Minutes of start time |
| endHour | Int | Hours of end time |
| endMinute | Int | Minutes of end time |
| threshold | Int | Threshold: How long does it take for the watch to remind you if you haven't moved |
| isOpen | Boolean | Switch status |

###### Return data

Same as the data returned by [[Read long-sit settings](#Read long-sit settings)]

###### Example Code

```kotlin
// kotlin code
VPOperateManager.getInstance().settingLongSeat(
writeResponse, LongSeatSetting(10, 40, 12, 40, 40, false)
) { longSeat ->
val message = "Set sedentary settings:\n$longSeat"

}
```





## Photo function

#### Premise

The device needs to support the photo function, and the judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportCamera
```

#### Enter the photo mode

The device needs to support the photo function

###### Interface

```
startCamera(bleWriteResponse, cameraDataListener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| ------------------ | ------------------- | ------------------------------------------------------------ |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| cameraDataListener | ICameraDataListener | Callback for photo operation, return success/failure of entering the photo mode, can/cannot take photos, success/failure of exiting the photo mode |

###### Return data

**ICameraDataListener**--Callback of camera operation

```kotlin
/**
* Return the camera status
*
* @param oprateStatus
*/
fun OnCameraDataChange(oprateStatus:ECameraStatus)
```

**ECameraStatus**--Camera status

| Parameter name | Description |
| ----------------- | ---------------- |
| OPEN_SUCCESS | Successfully entered camera mode |
| OPEN_FALI | Failed to enter camera mode |
| TAKEPHOTO_CAN | Take a photo |
| TAKEPHOTO_CAN_NOT | Cannot take a photo |
| CLOSE_SUCCESS | Successfully exited camera mode |
| CLOSE_FAIL | Failed to exit camera mode |
| UNKONW | Unknown |
###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().startCamera(
    writeResponse
) { cameraStatus ->

}
```

#### Exit camera mode

The device must support camera and has entered camera mode

###### Interface

```
stopCamera(IBleWriteResponse bleWriteResponse, ICameraDataListener cameraDataListener)
```

###### Parameter Explanation

Same parameters as [[Enter the photo mode](#Enter the photo mode)]

###### Return data

Same return data as [[Enter the photo mode](#Enter the photo mode)]

###### Example Code

```kotlin
//        kotlin code
VPOperateManager.getInstance().stopCamera(
    writeResponse
) { cameraStatus ->

}
```





## OTA upgrade function

#### Get the current firmware version number

###### Premise

Device connected

**Note:** The OTA process is long. If the battery is low, the transmission process may shut down due to low power. It is recommended that the battery power is above 30% before the upgrade is allowed;

###### Interface

After the [[Verify Password](#Verify Password Operation)] operation, the returned pwdDataListener data contains the current firmware version number

###### Example Code

```
pwdData.deviceVersion
```



#### Get the new firmware version number and upgrade description

###### Premise

The device is connected and the device version number is known

###### Interface

```
getOadVersion(oadSetting, listener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| ---------- | ----------------------- | ------------ |
| oadSetting | OadSetting | Verify version settings |
| listener | OnGetOadVersionListener | Verify version listener |

**OadSetting**--Verify version settings

| Parameter name | Type | Description |
| ------------------- | ------- | ------------------------------------------------------------ |
| deviceAddressString | String | Device address (required) |
| deviceVersion | String | Device version (required) |
| deviceTestVersion | String | Device test version (required) |
| deviceNumber | String | Device number (required) |
| isOadModel | Boolean | Firmware upgrade mode (required) |
| isDebug | Boolean | Debug mode (optional), true means using the debug port of our server, false means using the release port of our server, |
| hostUrl | String | Host address, transmission format: http://www.baidu.com; optional, default is our server; use with caution!!! Contact our developers before use |
| isAutoDownload | Boolean | Whether to automatically download after detecting the version |

###### Return data

**OnGetOadVersionListener **-- Verify version callback

```java
/**
* Return the latest version information of this device on the server
*
* @param deviceNumber Device number
* @param deviceVersion Latest version
* @param des Upgrade description
* @param netIsNew Network version is greater than the local version and needs to be updated
*/
void onNetOadInfo(int deviceNumber, String deviceVersion, String des, boolean netIsNew);

void onNetOadDetailInfo(OadFileBean oadFileBean,boolean netIsNew);
```

**OadFileBean** -- Device upgrade file information

| Parameter name | Type | Description |
| ------------- | ------ | --------------- |
| deviceNumber | String | New firmware device number |
| deviceVersion | String | New firmware version |
| downUrl | String | New firmware download url |
| size | String | New firmware size |
| md5 | String | New firmware md5 |
| des | String | New version upgrade description |

#### Download new firmware

According to the new firmware download address obtained, perform normal network download

#### Jier platform device OTA upgrade

Note that the Jier OTA upgrade process is generally longer, about 10-20 minutes. During the upgrade process, the watch phone needs to be fully charged, and the APP is in the foreground during the upgrade. (Some Android systems will regard the Bluetooth operation of the app in the background as power consumption, and may suspend the Bluetooth transmission, resulting in OTA upgrade failure)

The Jier OTA process is divided into three stages,
The first stage: OTA file transfer, the upgrade duration of this process depends on the size of the upgrade file. Under normal circumstances, the progress value of the onProgress method ranges from 0.00 to 99.9

Phase 2: Internal file copy, onProgress will quickly increase from 0.00 to 99.9

Phase 3: This stage is an internal upgrade, the device will actively disconnect and call back the onNeedReconnect method. At this time, the device will automatically change the device name to DFULang, and the device address will be the original device address + 1

The SDK will automatically search for and reconnect the DFULang device. After the connection is successful, the SDK will trigger an internal upgrade, onProgress will quickly increase from 0.00 to 100, and call back the onOTASuccess method

###### Premise

The device must be a JLI device and has completed [[Open JieLi File System](#Open JieLi File System)], and the new firmware has been downloaded

###### Interface

```
VPOperateManager.getInstance().startJLDeviceOTAUpgrade(firmwareFilePath, listener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| ---------------- | --------------------------------- | ------------------------- |
| firmwareFilePath | String | Locally stored OTA upgrade file path |
| listener | JLOTAHolder.OnJLDeviceOTAListener | JLI device OTA listener |

###### Return data

OnJLDeviceOTAListener: JLI OTA upgrade listener

```java
public interface OnJLDeviceOTAListener {

    /**
    * Start OTA
    */
    void onOTAStart();

    /**
    * Upgrade progress, keep two decimal places
    *
    * @param progress Upgrade progress [0.00 - 100]
    */
    void onProgress(float progress);

    /**
    * Callback when reconnection is needed
    *
    * @param address Device address
    * @param dfuLangAddress Device address in dfuLang state: address+1
    * @param isReconnectBySdk Whether to automatically reconnect from SDK to complete the last stage of OTA
    */
    void onNeedReconnect(String address, String dfuLangAddress, boolean isReconnectBySdk);

    /**
    * OTA success
    */
    void onOTASuccess();

    /**
    * OTA upgrade failed
    *
    * @param error Failure reason
    */
    void onOTAFailed(BaseError error);
}
```

###### Example Code

```java
private void startOTA() {
        String firmwareFilePath = "/storage/emulated/0/Android/data/com.timaimee.vpdemo/files/hband/jlOta/KH32_9626_00320800_OTA_UI_230421_19.zip";
        tvOTAInfo.setText(firmwareFilePath);
        VPOperateManager.getInstance().startJLDeviceOTAUpgrade(firmwareFilePath, new JLOTAHolder.OnJLDeviceOTAListener() {
            @Override
            public void onOTAStart() {
               Logger.t(TAG).e("【Jie Li OTA】--->OTA upgrade【Start】");
				tvOTAInfo.setText("Start upgrade");
            }

            @Override
            public void onProgress(float progress) {
                Logger.t(TAG).e("【杰理OTA】--->OTA升级中:" + progress + "%");
                tvOTAProgress.setText(String.format(Locale.CHINA, "%.2f", progress) + "%");
                pbOTAProgress.setProgress((int) (progress * 100));
            }

            @Override
            public void onNeedReconnect(String address, String dfuLangAddress, boolean isReconnectBySdk) {
                   Logger.t(TAG).e("【Jie Li OTA】--->OTA upgrade dfuLang reconnecting: address = " + address + ", dfuLangAddress = " + dfuLangAddress + ", whether reconnected by SDK = " + isReconnectBySdk);
    tvOTAInfo.setText("Data transmission is completed, start searching for DFULang devices->Device internal upgrade");
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
```





## Contact function

Prerequisite

The device needs to support the contact function, and the judgment conditions are as follows:

```
boolean isHaveContactFunction = VpSpGetUtil.getVpSpVariInstance(this).isSupportContactFunction();
```

Whether emergency contacts are supported, the judgment conditions are as follows:

```
boolean isSupportSOSContact = VpSpGetUtil.getVpSpVariInstance(this).isSupportSOSContactFunction();
```

Contact encapsulation entity: Contact

```kotlin
/**
* Contact
* @param contactID Contact ID
* @param name Contact nickname
* @param phoneNumber Contact number
* @param isSettingSOS Whether to set as emergency contact
* @param isSupportSOS Whether to support SOS function
*/
data class Contact(
    var contactID: Int,
    var name: String,
    var phoneNumber: String,
    var isSettingSOS: Boolean = false,
    var isSupportSOS: Boolean = false
)
```

Contact operation enumeration class: EContactOpt

```kotlin
enum class EContactOpt(var des: String) {
    /**
    * Read contacts
    */
    READ("read contact list"),

    /**
    * Set contacts
    */
    SETTING("setting contact"),

    /**
    * Set emergency contacts
    */
    SETTING_SOS("setting contact"),

    /**
    * Move contacts
    */
    MOVE("move contact position"),

    /**
    * Delete contacts
    */
    DELETE("delete one contact");
}
```

Contact operation callback listener:

```kotlin
interface IContactOptListener : IListener {

    /**
    * Contact operation successful
    * @param opt Current operation type
    * @param crc CRC after the current operation is successful. CRC and contact list need to be saved after each operation 		  update
    */
    fun onContactOptSuccess(opt: EContactOpt, crc: Int)

    /**
    * Contact operation failed
    */
    fun onContactOptFailed(opt: EContactOpt)

    /**
    * Read contact successfully
    * @param contactList Contact list. If the list is empty, it means there is no contact on the device
    */
    fun onContactReadSuccess(contactList: List<Contact>)

    /**
    * Indicates that the CRC sent when reading the contact is consistent with the CRC on the device
    * CRC consistency means that the contact on the device has not changed (note that the contact needs to be saved after each update, and the CRC is used to determine whether the contact needs to be updated)
    */
    fun onContactReadASSameCRC()

    /**
    * Failed to read the contact
    */
    fun onContactReadFailed()

}
```

### Read contacts

Code example:

When reading for the first time, pass in -1 for crc, which means that the complete device-side contacts will be returned. If the user has read contacts before, the CRC value of the contact can be passed in. If the CRC value is consistent with the device-side, onContactReadASSameCRC will be called back to indicate that repeated reading is not required, otherwise a complete contact list will be returned.
```
VPOperateManager.getInstance().readContact(-1, IContactOptListener, response);
```

Or

```java
/**
* Read the number of emergency contact calls
*
* @param contacts The last saved contact list, used to calculate crc
* @param listener Emergency contact call count listener
* @param bleWriteResponse Data write callback
*/
public void readContact(List<Contact> contacts, @NotNull IContactOptListener listener, IBleWriteResponse bleWriteResponse) {
.
.
.
```



Get Crc value:

```
int crc = CrcUtil.INSTANCE.getCrcByContactList(contacts);
```

### Add contact

Pass in a Contact entity with the contact nickname and mobile phone number. **Note: **If the device supports the sos function, isSupportSOS needs to be set to true

```
VPOperateManager.getInstance().addContact(contact, IContactOptListener, IBleWriteResponse);
```

Pass in a contact list, which must include nickname, mobile phone number, and whether the sos function is supported

```
VPOperateManager.getInstance().addContactList(List<Contact>, IContactOptListener, IBleWriteResponse)
```

### Delete a contact

To delete a contact, you need to pass in a contact with complete information (including contactID) obtained from the device

Code example:

```
VPOperateManager.getInstance().deleteContact(contact, IContactOptListener, IBleWriteResponse);
```

### Move a contact

Moving a contact means swapping the order of two contacts on the device.

Code example:

```
VPOperateManager.getInstance().moveContact(fromContact, toContact, IContactOptListener, IBleWriteResponse);
```

```java
VPOperateManager.getInstance().moveContactWithPosition(fromPosition, toPosition, IContactOptListener, IBleWriteResponse);
```

### Emergency contact

When the device supports emergency contacts, emergency contact operations can be performed. The judgment conditions are as follows:

```
boolean isSupportSOSContact = VpSpGetUtil.getVpSpVariInstance(this).isSupportSOSContactFunction();
```

#### Emergency contact on/off settings

Code example:

When isOpen is true, set emergency contacts

When isOpen When false, the emergency contact is cancelled

```
VPOperateManager.getInstance().setContactSOSState(isOpen, contact, IContactOptListener, IBleWriteResponse);
```

#### Emergency contact call count

Emergency contact call count operation callback listener:

```kotlin
/**
* Contact SOS call count setting
*/
interface ISOSCallTimesListener : IListener {
    /**
    * Set SOS call count successfully
    * @param times SOS call count
    */
    fun onSOSCallTimesSettingSuccess(times: Int)

    /**
    * Set SOS call count failed
    */
    fun onSOSCallTimesSettingFailed()

    /**
    * Read SOS call count successfully
    * @param times SOS call count
    * @param minTimes Supported minimum SOS call count
    * @param maxTimes Maximum SOS call count
    */
    fun onSOSCallTimesReadSuccess(times: Int, minTimes: Int, maxTimes: Int)

    /**
    * Failed to read the number of SOS calls
    */
    fun onSOSCallTimesReadFailed()
}
```

##### Read the number of calls

Code example for reading the number of emergency contact calls：

```java
VPOperateManager.getInstance().readSOSCallTimes(new ISOSCallTimesListener() {
    @Override
    public void onSOSCallTimesSettingSuccess(int times) {
        etSOSCount.setText(times + "");
    }

    @Override
    public void onSOSCallTimesSettingFailed() {

    }

    @Override
    public void onSOSCallTimesReadSuccess(int times, int minTimes, int maxTimes) {
        etSOSCount.setText(times + "");
        tvSOSInfo.setText("Call times setting range：[" + minTimes + "-" + maxTimes + "]");
    }

    @Override
    public void onSOSCallTimesReadFailed() {

    }
}, new IBleWriteResponse() {
    @Override
    public void onResponse(int code) {

    }
});
```

##### Set the number of calls

Code example for setting the number of emergency contact calls:

```java
VPOperateManager.getInstance().setSOSCallTimes(callTimes, new ISOSCallTimesListener() {

    @Override
public void onSOSCallTimesSettingSuccess(int times) {
	Toast.makeText(AddContactActivity.this, "Setting success:" + times + "times", Toast.LENGTH_SHORT).show();
}

@Override
public void onSOSCallTimesSettingFailed() {
	Toast.makeText(AddContactActivity.this, "Setting failed", Toast.LENGTH_SHORT).show();
}

@Override
public void onSOSCallTimesReadSuccess(int times, int minTimes, int maxTimes) {
	Toast.makeText(AddContactActivity.this, "Read success:" + times + "-Range: [" + minTimes + "-" + maxTimes + "]", 				Toast.LENGTH_SHORT).show(); }
    
 @Override 
 public void onSOSCallTimesReadFailed() { } }, new IBleWriteResponse() { @Override public void onResponse(int code) { } }); ```
```



## Body composition function

Prerequisite: The device needs to support the body composition function, and the judgment conditions are as follows:

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBodyComponent
```

Note: All the following interfaces can only be called if the device supports the body composition function

#### Read body composition ID (device manual measurement data)

###### Premise

The device needs to support the body composition function

###### Interface

```
readBodyComponentId(bleWriteResponse, readIdListener)
```

###### Parameter Explanation

| Parameter name   | Type                         | Description                                                  |
| ---------------- | ---------------------------- | ------------------------------------------------------------ |
| bleWriteResponse | IBleWriteResponse            | Listening for write operations                               |
| readIdListener   | IBodyComponentReadIdListener | Body composition read measurement and saved data ID callback |

####### Return data

**IBodyComponentReadIdListener** -- callback for reading and saving body composition measurement data ID

```kotlin
/**
* callback for reading ID completion
* @param ids list of ids read
*/
fun readIdFinish(ids: ArrayList<Int>)
```

###### Example Code

```kotlin
VPOperateManager.getInstance().readBodyComponentId(bleWriteResponse, object:IBodyComponentReadIdListener{
override fun readIdFinish(ids: ArrayList<Int>) {
	val msg = "Read body composition data ID list: $ids"
	}
})
```

#### Read body composition data (manual measurement of device)

###### Premise

The device needs to support the body composition function

###### Interface

```kotlin
readBodyComponentData(bleWriteResponse, readDataListener)
```

###### Parameter Explanation

| Parameter name           | Type                           | Description |
| ---------------- | ------------------------------ | -------------------------- |
| bleWriteResponse | IBleWriteResponse | Listening for write operations |
| readDataListener | IBodyComponentReadDataListener | Data callback for reading body composition data |

###### Return data

**IBodyComponentReadDataListener** --Data callback for reading body composition data

```kotlin
/**
* Data reading completed
*
* @param bodyComponentList body composition data
*/
fun readBodyComponentDataFinish(bodyComponentList: List<BodyComponent>?)
```

**BodyComponent** -- Body composition data

| Variable name | Type | Description |
| ------------------ | -------- | ------------------------------------------------------------ |
| BMI | Float | BMI valid range [4.0, 1114.0], retain one decimal place, the reported value is 10 times, the same below |
| bodyFatRate | Float | Body fat percentage, effective range [2.0, 48.0]% |
| fatRate | Float | Fat mass, effective range [10.0, 248.0]kg |
| FFM | Float | Fat-free mass, effective range [1.0, 132.0]kg |
| muscleRate | Float | Muscle percentage, effective range [39.0, 90.0]% |
| muscleMass | Float | Muscle mass, effective range [9.0, 248.0]kg |
| subcutaneousFat | Float | Subcutaneous fat, effective range [1.0, 47.0]% |
| bodyWater | Float | Body water, effective range [28.0, 79.0]% |
| waterContent | Float | Water content, effective range [7.0, 217.0]kg |
| skeletalMuscleRate | Float | Skeletal muscle rate, valid range [13.0, 69.0]% |
| boneMass | Float | Bone mass, valid range [2.3, 4.8] kg |
| proteinProportion | Float | Protein ratio, valid range [4.0, 26.0]% |
| proteinMass | Float | Protein mass, valid range [1.0, 71.0] kg |
| basalMetabolicRate | Float | Basal metabolic rate, valid range [25, 14995] kcal |
| timeBean | TimeData | Time of this measurement |
| duration | Int | Measurement duration |
| idType | Int | Measurement device type: 1: Device test, 2: App test |

###### Example Code

```kotlin
VPOperateManager.getInstance().readBodyComponentData(bleWriteResponse, object : IBodyComponentReadDataListener {
override fun readBodyComponentDataFinish(bodyComponentList: List<BodyComponent>?) {
	HBLogger.bleConnectLog("[Read body composition data successfully] bodyComponentList+${bodyComponentList.toString()}")
	}
})
```

#### Set body composition (device manual measurement) data reporting listener

####### Interface

```kotlin
setBodyComponentReportListener(reportListener)
```

###### Parameter Explanation

| Parameter name | Type | Description |
| -------------- | ------------------------------- | --------------------------------- |
| reportListener | INewBodyComponentReportListener | New body composition measurement data active reporting listener |

###### Return data

**INewBodyComponentReportListener** --New body composition measurement data active reporting listener

```kotlin
/**
* New body composition measurement data Reporting data listening interface
* Triggered as long as there is new measurement data on the device side, the new body composition measurement data is read through the [VPOperateManager.readBodyComponentData] interface when triggered
*/
fun onNewBodyComponentReport()
```

###### Example Code

```kotlin
VPOperateManager.getInstance().setBodyComponentReportListener(object : INewBodyComponentReportListener {
override fun onNewBodyComponentReport() {
	"New body composition data reported-----".logd()
	}
})
```

#### Start measuring body composition

###### Premise

Set support for body composition
###### Interface

```kotlin
startDetectBodyComponent(bleWriteResponse, bodyDetectListener)
```

###### Parameter Explanation

| Parameter name           | Type                         | Description |
| ---------------- | ---------------------------- | ---------------- |
| bleWriteResponse | BleWriteResponse | Listening for write operations |
| detectListener | IBodyComponentDetectListener | Body composition measurement callback |

###### Return data

**IBodyComponentDetectListener** -- Body composition measurement callback

```kotlin
/**
* Callback during measurement
* @param progress Measurement progress
* @param leadState Lead state: 0-> Lead passed; 1-> Lead dropped. After 4 consecutive leads dropped, the app actively sends a stop measurement to the device
*/
fun onDetecting(progress: Int, leadState: Int)

/**
* Measurement success callback
* @param bodyComponent This body composition data
*/
fun onDetectSuccess(bodyComponent: BodyComponent)

/**
* Measurement failed
* @param detectState Failure reason
*/
fun onDetectFailed(detectState: DetectState)

/**
* Stop measuring
*/
fun onDetectStop()
```

**DetectState** -- Measurement state enumeration

```kotlin
PROGRESS(0, "Measuring"),
SUCCESS(1, "Measurement success-result package"),
FAILED(2, "Measurement failure-no result"),
BUSY(3, "Device is busy"),
LOW_POWER(4, "Low power")
```

###### Example Code

```kotlin
VPOperateManager.getInstance().startDetectBodyComponent(bleWriteResponse,object :IBodyComponentDetectListener{
      override fun onDetecting(progress: Int, leadState: Int) {
        "【Body Composition Measurement】onDetecting：$progress,$leadState".logd()
        }

        override fun onDetectSuccess(bodyComponent: BodyComponent) {
        "【Body Composition Measurement】onDetectSuccess：${bodyComponent}".logd()
        }

        override fun onDetectFailed(detectState: DetectState) {
        "【Body Composition Measurement】onDetectFailed：${detectState}".loge()
        }

        override fun onDetectStop() {
        "【Body Composition Measurement】onDetectStop".loge()
        }

})
```



#### End body composition measurement

###### Premise

The device supports the body composition function and has started body composition measurement

###### Interface

```
stopDetectBodyComponent(bleWriteResponse)
```

###### Parameter Explanation

| Parameter name   | Type             | Description                    |
| ---------------- | ---------------- | ------------------------------ |
| bleWriteResponse | BleWriteResponse | Listening for write operations |

###### Return data

None

###### Example Code

```kotlin
VPOperateManager.getInstance().stopDetectBodyComponent(bleWriteResponse)
```

## Blood composition function

The device needs to support the blood composition function. The judgment conditions are as follows:

```
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBloodComponent
```

#### Read daily data of blood components

[Read daily data function](##Read daily data function)] contains five minutes of raw data containing blood component related data

#### Read blood component calibration value

###### Premise

The device needs to support blood component function

###### Interface

```
readBloodComponentCalibration(bleWriteResponse, optListener)
```

###### Parameter Explanation

| Parameter name   | Type                       | Description                         |
| ---------------- | -------------------------- | ----------------------------------- |
| bleWriteResponse | BleWriteResponse           | Listening for write operations      |
| optListener      | IBloodComponentOptListener | Blood component operation listening |

###### Return data

**IBloodComponentOptListener** -- Blood component operation callback

```kotlin
/**
* Blood component calibration reading success
* @param isOpen open
* @param bloodComposition blood component calibration value
*/
fun onBloodCompositionReadSuccess(isOpen: Boolean, bloodComposition: BloodComponent)

/**
* Blood component calibration reading failed
*/
fun onBloodCompositionReadFailed()

/**
* Blood component calibration value setting success
* @param isOpen open
* @param bloodComposition blood component calibration value
*/
fun onBloodCompositionSettingSuccess(isOpen: Boolean, bloodComposition: BloodComponent)

/**
* Blood component calibration value setting failed
*/
fun onBloodCompositionSettingFailed()
```

**BloodComponent** -- Blood component calibration value, consistent with the class returned by [Read daily data function](##Read daily data function)]

###### Example Code

```kotlin
VPOperateManager.getInstance().readBloodComponentCalibration(bleWriteResponse, optListener)
```

#### Setting blood component calibration value

###### Premise

The device supports blood component function

###### Interface

```
settingBloodComponentCalibration(bleWriteResponse, isOpen, bloodComponent, optListener)
```

###### Parameter Explanation

| Parameter name   | Type                       | Description                                   |
| ---------------- | -------------------------- | --------------------------------------------- |
| bleWriteResponse | BleWriteResponse           | Listening for write operations                |
| isOpen           | Boolean                    | Whether to enable blood component calibration |
| bloodComponent   | BloodComponent             | Blood component calibration value             |
| optListener      | IBloodComponentOptListener | Blood component operation listener            |

###### Return data

**IBloodComponentOptListener** -- Blood component operation listener, same as [[Read blood component calibration value](####Read blood component calibration value)] return

###### Example Code

```kotlin
 VPOperateManager.getInstance().settingBloodComponentCalibration(bleWriteResponse, isOpen, bloodComponent, optListener)
```



#### Start blood component measurement

###### Premise

The device needs to support blood component function

###### Interface

```kotlin
startDetectBloodComponent(bleWriteResponse, isUseCalibrationMode, detectListener)
```

###### Parameter Explanation

| Parameter name       | Type                          | Description                          |
| -------------------- | ----------------------------- | ------------------------------------ |
| bleWriteResponse     | BleWriteResponse              | Listening for write operations       |
| isUseCalibrationMode | Boolean                       | Whether to use calibration mode      |
| detectListener       | IBloodComponentDetectListener | Blood component measurement callback |

###### Data return

**IBloodComponentDetectListener** -- Blood component measurement callback

```kotlin
/**
* Detection failed
*/
fun onDetectFailed(errorState: EBloodComponentDetectState)

/**
* Detecting
* @param progress Detection progress
* @param bloodComponent Blood component
*/
fun onDetecting(progress: Int, bloodComponent: BloodComponent)

/**
*
*/
fun onDetectStop()

/**
* Detection completed
* @param bloodComponent Blood component
*/
fun onDetectComplete(bloodComponent: BloodComponent)
```

###### Example Code

```kotlin
VPOperateManager.getInstance().startDetectBloodComponent(bleWriteResponse, isUseCalibrationMode, detectListener)
```



#### Stop blood component measurement

###### Before

The device must support blood and blood component measurement must be enabled

###### Interface

```
stopDetectBloodComponent(bleWriteResponse)
```

###### Return data

None

###### Example Code

```kotlin
VPOperateManager.getInstance().stopDetectBloodComponent {

}
```

