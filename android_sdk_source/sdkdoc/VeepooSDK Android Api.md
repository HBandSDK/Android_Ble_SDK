# Android SDK API文档


| 版本  | 修改内容                                                     | 修改日期   |
| ----- | ------------------------------------------------------------ | ---------- |
| 1.0.0 | SDK初版                                                      | 2023.05.15 |
| 1.0.1 | 添加K系列的表盘与OTA文档                                     | 2023.05.26 |
| 1.0.2 | 添加语言设置功能api                                          | 2023.05.26 |
| 1.0.3 | 增加联系人功能                                               | 2023.06.25 |
| 1.0.4 | 1. 新增运动枚举，新增语言枚举<br />2. 30分钟日常数据增加date返回<br />3. 联系人批量添加，手机音量控制功能<br />4. 完善ota升级相关流程说明 | 2023.09.08 |
| 1.0.5 | 增加ota、表盘传输时电量说明                                  | 2023.09.11 |
| 1.0.6 | 增加血糖多校准模式的读取和设置                               | 2023.09.19 |
| 1.0.7 | 1.推特改名为X(原推特)<br />2.增加身体成分、血液成分相关功能说明<br />3.增加血液成分检测开关、尿酸/血脂单位功能设置<br />4.增加ecg上报接口 | 2023.09.27 |
| 1.0.8 | 1.身体成分增加单位显示                                       | 2023.10.25 |
| 1.0.9 | 读取身体成分数据接口返回增加测量时间、测量秒数等说明         | 2023.11.24 |
| 1.1.0 | 1.增加获取血糖风险等级相关说明；<br/>2.体温读取增加05标志位判断。 | 2024.04.15 |
| 1.1.1 | 1.同步个人信息api补充体重参数说明；<br />2.肤色多档位设置(需设备支持)。 | 2024.11.30 |

## 通用接口类
**VPOperateManager**（SDK主入口）  
主要操作类

**获取实例**

```kotlin
VPOperateManager.getInstance()
```
获取单例对象，SDK接口以单例形式存在

注：**设备不支持异步操作,当多个耗时操作同时进行时,可能会导致数据异常;因此在与设备进行交互时,尽可能避免多个操作同时进行**





## SDK初始化

```kotlin
init(context)
```
| 参数名  	| 类型    	| 备注                        	|
|---------	|---------	|-----------------------------	|
| context 	| Context 	| 配置选项ApplicactionContext 	|

注:所有接口仅在sdk初始化后才能调用,App运行期间，只需要初始化一次，无需重复初始化





## 扫描

#### 开始扫描

###### 接口

```kotlin
startScanDevice(searchResponse)
```
开始蓝牙扫描,会过滤掉非本公司的设备,如需停止扫描，则调用stopScan停止扫描。    

注:蓝牙关闭的情况下，扫描接口不生效

###### 参数解释

| 参数名         	| 类型           	| 备注           	|
|----------------	|----------------	|----------------	|
| searchResponse 	| SearchResponse 	| 扫描结果的回调 	|
###### 返回数据

**SearchResponse**--扫描结果的回调

```kotlin
/**
 * 开始扫描设备
 */
fun onSearchStarted()

/**
 * 发现扫描设备
 *
 * @param device 当前发现的设备
 */
fun onDeviceFounded(device:SearchResult)

/**
 * 停止扫描设备回调
 */
fun onSearchStopped()

/**
 * 取消扫描设备回调
 */
fun onSearchCanceled()
```

**SearchResult**--当前发现的设备

| 变量       | 类型            | 备注                 |
| ---------- | --------------- | -------------------- |
| device     | BluetoothDevice | 蓝牙设备（系统）     |
| rssi       | Int             | 蓝牙信号值rssi       |
| scanRecord | byteArray       | 扫描到的设备广播数据 |

###### 示例代码

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



#### 结束扫描

###### 接口

```kotlin
stopScanDevice()
```
###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().stopScanDevice()
```





## 连接

#### 连接设备

```kotlin
connectDevice(mac,connectResponse,bleNotifyResponse)
```
###### 参数解释

| 参数名            	| 类型             	| 备注                                                                	|
|-------------------	|------------------	|---------------------------------------------------------------------	|
| mac               	| String           	| 需要连接的设备地址                                                  	|
| connectResponse   	| IConnectResponse 	| 连接状态的回调,先返回连接状态，连接成功后，会返回蓝牙通信状态的回调 	|
| bleNotifyResponse 	| INotifyResponse  	| 蓝牙通信状态的回调,此回调在connectResponse之后被调用                	|

###### 返回数据

**IConnectResponse** -- 连接状态的回调

```kotlin
/**
 * 连接状态的返回
 *
 * @param code       连接状态，只有当值为Code.REQUEST_SUCCESS表示连接成功
 * @param profile    设备的蓝牙属性
 * @param isOadModel 设备包含两种模式[正常模式/固件升级模式]，大多情况下是正常模式，只有当设备进行固件升级操作失败时，才会进入固件升级模式
 */
fun connectState(code:Int, profile:BleGattProfile, isOadModel:boolean);
```

**INotifyResponse** - 设置数据监听的回调

```kotlin
/**
 * 设置数据监听的返回
 *
 * @param state 只有值等于Code.REQUEST_SUCCESS时才成功
 */
fun notifyState(state:Int);
```

#### 验证密码操作

###### 接口

```kotlin
confirmDevicePwd(bleWriteResponse,pwdDataListener,deviceFuctionDataListener，socialMsgDataListener，customSettingDataListener，pwd，mModelIs24)  
```

```kotlin
confirmDevicePwd(bleWriteResponse,pwdDataListener,deviceFuctionDataListener，socialMsgDataListener，customSettingDataListener，pwd，mModelIs24,deviceTimeSetting)
```

注：**连接成功后第一步就要执行的操作，需要在[连接成功]并且[可以进行蓝牙通信]的情况下才可以进行其他蓝牙操作** 

###### 参数解释 


| 参数名         	| 类型           	| 备注           	|
|----------------	|----------------	|----------------	|
| bleWriteResponse 	| IBleWriteResponse 	| code 返回Code.REQUEST_SUCCESS表示向设备发送命令成功，但是发送命令成功不一定会有数据返回，返回成功只能说明设备接收到了命令，如果设备处理不了命令，则有可能没有数据返回，此接口用于开发人员查找问题 |
| pwdDataListener 	| IPwdDataListener 	| 密码操作的数据返回监听，此处返回的数据包含:设备号，设备发布版本号，设备测试版本号，饮酒数据状态，翻腕亮屏状态，查找手机功能状态，佩戴检测功能状态 	|
| deviceFuctionDataListener 	| IDeviceFuctionDataListener 	| 设备包含的功能的返回监听，此处返回的数据包含: 各个设备功能状态[是否支持]：血压、饮酒、久坐、心率过高提醒、微信运动、摇-摇拍照、疲劳度、血氧 	|
| socialMsgDataListener 	| ISocialMsgDataListener 	| 电话、短信、社交软件消息的返回监听，此处返回的数据包含:是否支持接收社交软件的提醒，是否打开电话、短信、社交软件的提醒 	|
| customSettingDataListener 	| ICustomSettingDataListener 	| 个性化设置操作的监听 	|
| pwd 	| String 	| 密码长度为4，初使值为0000，传入密码前，请先确保是4位的数字 	|
| mModelIs24 	| boolean 	| 时间制式，若是选择显示24小时制则传入true,选择12小时制则传入false 	|
| deviceTimeSetting 	| DeviceTimeSetting 	| 默认为手机系统时间，可自定义时间，精确到秒 	|

###### 数据返回

**IPwdDataListener** -- 密码操作的数据返回监听

```kotlin
/**
 * 返回密码操作的数据
 * @param pwdData 密码操作的数据
 */
fun onPwdDataChange(pwdData:PwdData);
```

**PwdData** -- 密码操作的数据

| 变量                  | 类型            | 备注                                                         |
| --------------------- | --------------- | ------------------------------------------------------------ |
| mStatus               | EPwdStatus      | 返回当前密码操作的状态                                       |
| pwd                   | String          | 当前密码                                                     |
| deviceNumber          | Int             | 设备编号                                                     |
| deviceVersion         | String          | 设备正式版本，正式版本号仅于APP上显示                        |
| deviceTestVersion     | String          | 设备测试版本，测试版本号将用于固件升级，固件升级要用到设备号跟测试版本号 |
| isHaveDrinkData       | Boolean         | 是否有饮酒数据                                               |
| isOpenNightTurnWriste | EFunctionStatus | 夜间抬腕亮屏功能，支持/不支持/开启/关闭/未知                 |
| findPhoneFunction     | EFunctionStatus | 查找手机功能,支持/不支持/开启/关闭/未知                      |
| wearDetectFunction    | EFunctionStatus | 佩戴监测功能,支持/不支持/开启/关闭/未知                      |

**EFunctionStatus** -- 功能状态

| 变量          | 备注   |
| ------------- | ------ |
| UNSUPPORT     | 不支持 |
| SUPPORT       | 支持   |
| SUPPORT_OPEN  | 开启   |
| SUPPORT_CLOSE | 关闭   |
| UNKONW        | 未知   |

**IDeviceFuctionDataListener** -- 设备功能状态监听,监听一次,可能会被回调2次,依设备而定

```kotlin
/**
 * 返回设备功能状态
 *
 * @param functionSupport
 */
fun onFunctionSupportDataChange(functionSupport:FunctionDeviceSupportData)
```

**FunctionDeviceSupportData** -- 各个设备功能状态[是否支持]

| 变量                  | 类型            | 备注                     |
| --------------------- | --------------- | ------------------------ |
| Bp                    | EFunctionStatus | 血压功能状态             |
| Drink                 | EFunctionStatus | 饮酒功能状态             |
| Longseat              | EFunctionStatus | 久坐功能状态             |
| HeartWaring           | EFunctionStatus | 心率警告功能状态         |
| WeChatSport           | EFunctionStatus | 微信运动功能状态         |
| Camera                | EFunctionStatus | 拍照功能状态             |
| Fatigue               | EFunctionStatus | 疲劳度功能状态           |
| SpoH                  | EFunctionStatus | 血氧功能状态             |
| SpoHAdjuster          | EFunctionStatus | 血氧校准功能状态         |
| SpoHBreathBreak       | EFunctionStatus | 血氧呼吸暂停提醒功能状态 |
| Woman                 | EFunctionStatus | 女性功能状态             |
| Alarm2                | EFunctionStatus | 新闹钟功能状态           |
| newCalcSport          | EFunctionStatus | 计步计算使用新功能       |
| CountDown             | EFunctionStatus | 倒计时功能状态           |
| AngioAdjuster         | EFunctionStatus | 动态血压调整功能状态     |
| SreenLight            | EFunctionStatus | 屏幕亮度调节功能状态     |
| HeartDetect           | EFunctionStatus | 心率检测功能状态，默认有 |
| SportModel            | EFunctionStatus | 运动模式功能状态         |
| NightTurnSetting      | EFunctionStatus | 翻腕亮屏设置功能状态     |
| hidFuction            | EFunctionStatus | HID功能状态              |
| screenStyleFunction   | EFunctionStatus | 屏幕样式功能             |
| beathFunction         | EFunctionStatus | 呼吸率功能               |
| hrvFunction           | EFunctionStatus | HRV功能状态              |
| weatherFunction       | EFunctionStatus | 天气功能状态             |
| screenLightTime       | EFunctionStatus | 亮屏时长功能状态         |
| precisionSleep        | EFunctionStatus | 精准睡眠功能状态         |
| resetData             | EFunctionStatus | 重置设备/数据功能状态    |
| ecg                   | EFunctionStatus | ECG功能状态              |
| multSportModel        | EFunctionStatus | 多运动功能状态           |
| lowPower              | EFunctionStatus | 低功耗功能状态           |
| findDeviceByPhone     | EFunctionStatus | 手机查找设备功能状态     |
| agps                  | EFunctionStatus | AGPS功能状态             |
| temperatureFunction   | EFunctionStatus | 体温功能状态             |
| textAlarm             | EFunctionStatus | 文字闹钟功能状态         |
| bloodGlucose          | EFunctionStatus | 血糖功能状态             |
| bloodGlucoseAdjusting | EFunctionStatus | 血糖校准功能             |
| sleepTag              | Int             | 睡眠标志位               |
| musicStyle            | Int             | 音乐带信息0x99，值为1    |
| WathcDay              | Int             | 手表保存的最大天数       |
| contactMsgLength      | Int             | 联系人消息长度           |
| allMsgLength          | Int             | 消息提醒最大包数         |
| sportmodelday         | Int             | 运动模式的最大天数       |
| screenstyle           | Int             | 屏幕样式的选择           |
| weatherStyle          | Int             | 天气的类型               |
| originProtcolVersion  | Int             | 原始数据的协议版本       |
| bitDataTranType       | Int             | 大块数据传输类型         |
| watchUiServerCount    | Int             | 表盘市场的个数           |
| watchUiCoustomCount   | Int             | 自定义表盘个数           |
| temptureType          | Int             | 温度类型                 |
| cpuType               | Int             | cpu类型                  |
| ecgType               | Int             | ecg类型                  |

**ISocialMsgDataListener** --消息通知开关状的回调监听 具体返回查看【[消息通知](#消息通知)】

**ICustomSettingDataListener** -- 个性化设置的回调监听 具体查看【[个性化设置](#个性化设置)】

###### 示例代码

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



## 断开连接

###### 接口

```
disconnectWatch(bleWriteResponse)
```

###### 参数解释

| 参数名           | 类型              | 备注           |
| ---------------- | ----------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | 写入操作的监听 |

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().disconnectWatch {

}
```





## 同步个人信息

身高体重的设置，会影响卡路里的计算结果

###### 接口

```kotlin
syncPersonInfo(bleWriteResponse, personInfoDataListener, personInfoData)
```

###### 参数解释

| 参数名                 | 类型                    | 备注                                           |
| ---------------------- | ----------------------- | ---------------------------------------------- |
| bleWriteResponse       | IBleWriteResponse       | 写入操作的监听                                 |
| personInfoDataListener | IPersonInfoDataListener | 个人信息操作的回调，返回的数据只包含操作的状态 |
| personInfoData         | PersonInfoData          | 个人信息数据                                   |

**PersonInfoData** -- 个人信息数据

| 参数名   | 类型 | 备注                 |
| -------- | ---- | -------------------- |
| ESex     | ESex | 性别                 |
| height   | Int  | 身高                 |
| weight   | Int  | 体重                 |
| age      | Int  | 年龄                 |
| stepAim  | Int  | 目标步数             |
| sleepAim | Int  | 目标睡眠时间（分钟） |

###### 返回数据

**IPersonInfoDataListener** -- 个人信息操作的回调

```kotlin
/**
 * 返回操作的状态
 *
 * @param EOprateStauts:OPRATE_SUCCESS:操作成功，OPRATE_FAIL:操作失败,UNKNOW:未知
 */
fun OnPersoninfoDataChange(eOprateStauts:EOprateStauts)
```

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().syncPersonInfo(writeResponse,
    { EOprateStauts ->
        val message = "syncPersonInfo:\n$EOprateStauts"

    }, PersonInfoData(ESex.MAN, 178, 60, 20, 8000)
)
```





## 语言设置功能

###### 接口

```kotlin
settingDeviceLanguage(bleWriteResponse, languageDataListener, language)
```

###### 参数解释

| 参数名               | 类型                  | 备注           |
| -------------------- | --------------------- | -------------- |
| bleWriteResponse     | IBleWriteResponse     | 写入操作的监听 |
| languageDataListener | ILanguageDataListener | 设置语言回调   |
| language             | ELanguage             | 语言的种类     |

**ELanguage** 语言种类枚举

| 变量              | 备注       |
| ----------------- | ---------- |
| CHINA             | 中文简体   |
| CHINA_TRADITIONAL | 中文繁体   |
| ENGLISH           | 英语       |
| JAPAN             | 日语       |
| KOREA             | 韩语       |
| DEUTSCH           | 德语       |
| RUSSIA            | 俄语       |
| SPANISH           | 西班牙语   |
| ITALIA            | 意大利语   |
| FRENCH            | 法语       |
| VIETNAM           | 越南语     |
| PORTUGUESA        | 葡萄牙语   |
| THAI              | 泰语       |
| POLISH            | 波兰语     |
| SWEDISH           | 瑞典语     |
| TURKISH           | 土耳其语   |
| DUTCH             | 荷兰语     |
| CZECH             | 捷克语     |
| ARABIC            | 阿拉伯语   |
| HUNGARY           | 匈牙利语   |
| GREEK             | 希腊语     |
| ROMANIAN          | 罗马尼亚   |
| SLOVAK            | 斯洛伐克语 |
| INDONESIAN        | 印尼语     |
| BRAZIL_PORTUGAL   | 巴西葡萄牙 |
| CROATIAN          | 克罗地亚语 |
| LITHUANIAN        | 立陶宛语   |
| UKRAINE           | 乌克兰     |
| HINDI             | 印地语     |
| HEBREW            | 希伯来语   |
| DANISH            | 丹麦语     |
| PERSIAN           | 波斯语     |
| FINNISH           | 芬兰语     |
| MALAY             | 马来语     |
| UNKONW            | 未知       |

###### 返回数据

**ILanguageDataListener** --设置语言回调

```kotlin
/**
 * 返回语言的状态
 *
 * @param languageData 语言数据
 */
fun onLanguageDataChange( languageData:LanguageData)
```

**languageData ** -- 语言数据

| 变量名   | 类型          | 备注               |
| -------- | ------------- | ------------------ |
| stauts   | EOprateStauts | 操作的状态         |
| language | ELanguage     | 获取当前设备的语言 |

**注意：**

[系统语言]&&[女性功能的提示语言]在设备上并不是一致的，女性功能支持多国语言，而系统语言只有中英文。所以可能出现以下正常情况（在[系统语言]只支持设置中英文的设备，进行设置日语，设备会提示设置日语成功，而[系统语言]依然显示为英文，但[女性功能提示语言]为日语）





## 读取当前计步

读取当前计步,计步的功能涉及到距离及卡路里的计算,而这两者的计算与身高有关系，所以在读取计步前应该先调用【[同步个人信息](#同步个人信息)】来设置个人信息

**注**：指的是设备的计步、距离和卡路里，本接口返回的数据是实时的，与日常数据中的步数有差别，日常数据中的步数是每5分钟的汇总体现，存在滞后性。如果应用层需要同步获取设备端的步数，需要固定频率调用本接口获取数据。

###### 接口

```kotlin
readSportStep(bleWriteResponse, sportDataListener)
```

###### 参数解释

| 参数名            | 类型               | 备注             |
| ----------------- | ------------------ | ---------------- |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听   |
| sportDataListener | ISportDataListener | 读取运动数据监听 |

###### 返回数据

**ISportDataListener** -- 读取运动数据监听

```kotlin
/**
 * 返回计步的数据
 *
 * @param sportData 当前的运动数据
 */
fun onSportDataChange(sportData:SportData)
```

**SportData** -- 当前的运动数据

| 变量                            | 类型   | 备注                                                         |
| ------------------------------- | ------ | ------------------------------------------------------------ |
| step                            | Int    | 当前的计步数                                                 |
| dis                             | Double | 当前的距离km                                                 |
| kcal                            | Double | 当前的卡路里kcal                                             |
| calcType                        | Int    | 计算方式， 0 表示传统算法，1 表示新算法公式，2表示运动模式表的表 |
| triaxialX, triaxialY, triaxialZ | Int    | 三轴位置                                                     |

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().readSportStep({
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}) { sportData ->  }
```





## 读取设备电量

1.特别注意⚠️：表盘传输和OTA过程会有较大功耗，需要增加电量限制。手表电量很低时，如果发起表盘传输，在传输过程中手表可能会因低电关机，重新充电后，表盘会变黑。 建议每次传输前，先读取一遍手表当前电量，如果电量状态为低电，则禁止传输。

OTA过程较长，建议电池电量在30%以上，才允许升级。

###### 前提

设备已连接

###### 接口

```kotlin
readBattery(bleWriteResponse, batteryDataListener)
```

###### 参数解释

| 参数名              | 类型                 | 备注           |
| ------------------- | -------------------- | -------------- |
| bleWriteResponse    | IBleWriteResponse    | 写入操作的监听 |
| batteryDataListener | IBatteryDataListener | 读取电量监听   |

###### 返回数据

**IBatteryDataListener** -- 读取电量监听

```kotlin
/**
 * 返回电量的数据
 */
fun onDataChange(batteryData:BatteryData)
```

**BatteryData** -- 电量数据

| 参数名         | 类型    | 备注                                                         |
| -------------- | ------- | ------------------------------------------------------------ |
| batteryLevel   | Int     | 设备当前的电量[1-4],4表示满电                                |
| batteryPercent | Int     | 电量百分比[1-100]                                            |
| powerModel     | Int     | 电源模式：0x00 正常 ，0x01 充电状态，0x02 低压状态，0x03 充满状态 |
| state          | Int     | 睡眠状态：0清醒  1睡眠                                       |
| bat            | Byte    | BAT当前电量                                                  |
| isLowBattery   | Boolean | 是否低电： 0x01表示正常，0x02表示低电                        |
| isPercent      | Boolean | 是否可以显示电量百分比，true表示电量用batteryPercent表示，false表示电量用batteryLevel显示 |

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().readBattery({
    if (it != Code.REQUEST_SUCCESS) {
        
    }
}, {
    
})
```





## 读取日常数据功能

#### 读取健康数据（睡眠数据+5分钟原始数据)

###### 接口

假如手表存储的数据是3天 读取所有的健康数据,读取的顺序为 [睡眠数据（今天-昨天-前天）]-[5分钟原始数据(今天-昨天-前天)],便针对原始数据可以进行自定义天的位置，以及条数的位置 ,比如传入[昨天，150]，那么读取顺序为 [睡眠数据（今天-昨天-前天）]-[5分钟原始数据(昨天（150）-前天)]

```
readAllHealthDataBySettingOrigin(allHealthDataListener,day,position,watchday)
```

###### 参数解释

| 参数名                | 类型                   | 备注                                                         |
| --------------------- | ---------------------- | ------------------------------------------------------------ |
| allHealthDataListener | IAllHealthDataListener | 读取所有数据的回调，返回读取的进度以及健康数据：睡眠数据，计步数据[5分钟、30分钟]，心率数据[5分钟、30分钟]，血压数据[5分钟、30分钟] |
| watchday              | Int                    | 手表可存储的数据容量(单位:天),依设备而定,验证密码后,在IDeviceFuctionDataListener回调数据FunctionDeviceSupportData中,可通过getWatchday()获取返回值 |
| day                   | Int                    | 读取哪天，0表示今天，1表示昨天，2表示前天，以此类推。        |
| position              | Int                    | 读取条数的位置，一天最多288(5分钟每条)条，你可以定义读取的条数位置，此参数的值必须大于等于1 |

###### 返回数据

IAllHealthDataListener

```kotlin
/**
 * 读取的进度回调
 *
 * @param progress 进度值，范围[0-1]
 */
void onProgress(float progress);

/**
 * 返回睡眠的数据
 *
 * @param day       表示正在读取第几天的数据 0：今天，1：昨天，2：前天。
 * @param sleepData 睡眠的数据
 */
void onSleepDataChange(String day, SleepData sleepData);

/**
 * 读取睡眠结束的回调
 */
void onReadSleepComplete();

/**
 * 读取5分钟原始数据的回调
 *
 * @param originData 5分钟的原始数据
 */
void onOringinFiveMinuteDataChange(OriginData originData);

/**
 * 30分钟原始数据的回调,来自5分钟的原始数据，只是在内部进行了数据处理
 *
 * @param originHalfHourData
 */
void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData);

/**
 * 读取所有数据结束
 */
void onReadOriginComplete();
```

**SleepData** -- 睡眠数据

| 变量          | 类型     | 备注                                                         |
| ------------- | -------- | ------------------------------------------------------------ |
| Date          | String   | 睡眠日期                                                     |
| cali_flag     | Int      | 睡眠定标值，目前这个值没有什么用                             |
| sleepQulity   | Int      | 睡眠质量                                                     |
| wakeCount     | Int      | 睡眠中起床的次数                                             |
| deepSleepTime | Int      | 深睡时长(单位min)                                            |
| lowSleepTime  | Int      | 浅睡时长(单位min)                                            |
| allSleepTime  | Int      | 睡眠总时长                                                   |
| sleepLine     | String   | 睡眠曲线，主要用于更具象化的UI来显示睡眠状态，如果您睡眠界面对UI没有特殊要求，可不理会,睡眠曲线分为普通睡眠和精准睡眠，普通睡眠是一组由0,1,2组成的字符串，每一个字符代表时长为5分钟，其中0表示浅睡，1表示深睡，2表示苏醒,比如“201112”，长度为6，表示睡眠阶段共30分钟，头尾各苏醒5分钟，中间浅睡5分钟，深睡15分钟;若是精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒 |
| sleepDown     | TimeData | 入睡时间                                                     |
| sleepUp       | TimeData | 起床时间                                                     |

**OriginData** -- 5分钟的原始数据

| 变量            | 类型     | 备注                                                         |
| --------------- | -------- | ------------------------------------------------------------ |
| date            | String   | 睡眠日期                                                     |
| allPackage      | Int      | 当天数据的总包数                                             |
| packageNumber   | Int      | 此数据在当天的位置                                           |
| mTime           | TimeData | 精准时间                                                     |
| rateValue       | Int      | 心率值，范围[30-200]                                         |
| sportValue      | Int      | 运动量值[0-65536],值越大代表运动越剧烈共分为5个等级，分别是[0-220],[201-700],[701-1400],[1401-3200],[3201-65535] |
| stepValue       | Int      | 计步值                                                       |
| highValue       | Int      | 高压值，范围[60-300]                                         |
| lowValue        | Int      | 低压值，范围[20-200]                                         |
| wear            | Int      | 佩戴标志位                                                   |
| tempOne         | Int      | 预留值                                                       |
| tempTwo         | Int      | 预留值                                                       |
| calValue        | Double   | 消耗的卡路里值                                               |
| disValue        | Double   | 运动的总距离km                                               |
| calcType        | Int      | 计算方式：0 表示传统算法，1 表示新算法公式，2表示运动模式的设备 |
| drankPartOne    | String   | 暂预留                                                       |
| baseTemperature | Double   | 体表温度，当VpSpGetUtil.isSupportReadTempture && VpSpGetUtil.getTemperatureType == 时值有效 |
| temperature     | Double   | 体温，当VpSpGetUtil.isSupportReadTempture && VpSpGetUtil.getTemperatureType == 时值有效 |

**OriginHalfHourData** -- 30分钟数据

| 变量               | 类型                    | 备注                                          |
| ------------------ | ----------------------- | --------------------------------------------- |
| halfHourRateDatas  | List<HalfHourRateData>  | 30分钟的心率数据                              |
| halfHourBps        | List<HalfHourBpData>    | 30分钟的血压数据                              |
| halfHourSportDatas | List<HalfHourSportData> | 30分钟的运动数据                              |
| allStep            | Int                     | 当前读取到的30分钟内（5*6）原始数据的总计步数 |
| date               | String                  | 日期（"yyyy-MM-dd HH:mm:ss）                  |

**HalfHourRateData** -- 30分钟的心率数据

| 变量      | 类型     | 备注                                                         |
| --------- | -------- | ------------------------------------------------------------ |
| date      | String   | 所属日期                                                     |
| time      | TimeData | 具体的时间，最多的可以准确到分钟,如10:00表示的是10:00-10:30这段区间的均值 |
| rateValue | Int      | 心率值                                                       |
| ecgCount  | Int      | ecg总数                                                      |
| ppgCount  | Int      | ppg总数                                                      |

**HalfHourBpData**-- 30分钟血压数据

| 变量      | 类型     | 备注                                                         |
| --------- | -------- | ------------------------------------------------------------ |
| date      | String   | 所属日期                                                     |
| time      | TimeData | 具体的时间，最多的可以准确到分钟,如10:00表示的是10:00-10:30这段区间的均值 |
| highValue | Int      | 血压最高值                                                   |
| lowValue  | Int      | 血压最低值                                                   |

**HalfHourSportData** -- 30分钟的运动数据

| 变量       | 类型     | 备注                                                         |
| ---------- | -------- | ------------------------------------------------------------ |
| date       | String   | 所属日期                                                     |
| time       | TimeData | 具体的时间，最多的可以准确到分钟,如10:00表示的是10:00-10:30这段区间的均值 |
| sportValue | Int      | 30分钟内的总运动量                                           |
| disValue   | Double   | 30分钟内的总距离                                             |
| calValue   | Double   | 30分钟内的总卡路里                                           |

###### 示例代码

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



#### 读取日常数据(5分钟原始数据)

###### 接口

假如手表存储的数据是3天 读取原始数据，每5分钟一条，数据包含计步，心率，血压，运动量，读取顺序为 今天-昨天-前天，理论上一天的数据共288条

```kotlin
readOriginData(bleWriteResponse, originDataListener, watchday)
```

读取原始数据，此方法可以自定义要读取的哪天以及从当天第几条开始读取，是否只读当天

```
readOriginDataBySetting(bleWriteResponse, originDataListener,readOriginSetting)
```

读取原始数据，此方法可以自定义要读取的哪天以及从当天第几条开始读取，避免重复读取 ,比如设置了[昨天,150],那么读取顺序为[昨天{150}-昨天结束-前天]

```kotlin
readOriginDataFromDay(bleWriteResponse, originDataListener, day, position, watchday)
```

读取单天原始数据，此方法可以自定义要读取的哪天以及从当天第几条开始读取，并只读当天 ,比如设置了[昨天,150],那么读取顺序为[昨天{150}-昨天结束]

```kotlin
readOriginDataSingleDay(bleWriteResponse, originDataListener, day, position, watchday)
```

###### 参数解释

| 参数名             | 类型                                     | 备注                                                         |
| ------------------ | ---------------------------------------- | ------------------------------------------------------------ |
| bleWriteResponse   | IBleWriteResponse                        | 写入操作的监听                                               |
| originDataListener | IOriginDataListener/IOriginData3Listener | 原始数据的回调，返回的数据包含计步，心率，血压，运动量       |
| watchday           | Int                                      | 手表可存储的数据容量(单位:天)                                |
| day                | Int                                      | 读取位置，0表示今天，1表示昨天，2表示前天，以此类推。 读取顺序为 今天-昨天- 前天 |
| position           | Int                                      | 读取的条数位置,此值要求必须大于等于1                         |
| readOriginSetting  | ReadOriginSetting                        | 读取原始数据的设置                                           |

注：**读取日常数据时需做设备协议版本判断，当设备协议版本为3或5时，需传入IOriginData3Listener，其他情况使用IOriginDataListener**

判定条件如下：

```kotlin
val originProtocolVersion = VpSpGetUtil.getVpSpVariInstance(mContext).getOriginProtocolVersion()
if(originProtocolVersion==3 || originProtocolVersion == 5){
    //读取日常数据使用IOriginData3Listener做数据监听
}else{
    //读取日常数据使用IOriginDataListener做数据监听
}
```

**ReadOriginSetting** -- 读取原始数据的设置

| 参数名         | 类型    | 备注                                                         |
| -------------- | ------- | ------------------------------------------------------------ |
| day            | Int     | 读取位置，0表示今天，1表示昨天，2表示前天，以此类推。 读取顺序为 今天-昨天- 前天 |
| position       | Int     | 读取的条数位置,此值要求必须大于等于1                         |
| onlyReadOneDay | Boolean | true表示只读今天，false表示按顺序读取                        |
| watchday       | Int     | 手表存储的天数                                               |

###### 返回数据

**IOriginDataListener** -- 日常数据返回监听

```kotlin
/**
 * 返回5分钟原始数据
 *
 * @param originData 5分钟一条的原始数据
 */
fun onOringinFiveMinuteDataChange(originData:OriginData)

/**
 * 返回30分钟的原始数据，数据来自5分钟原始数据，只是在内部进行了处理后返回
 *
 * @param originHalfHourData 30分钟一条的原始数据
 */
fun onOringinHalfHourDataChange(originHalfHourData:OriginHalfHourData )

/***
 * 返回读取的细节，此包的位置需要记住，下次读取数据时，传入此包的位置，可以避免重复读取
 *
 * @param day            数据在手表中的标志位[0=今天，1=昨天，2=前天]
 * @param date           数据的日期,格式为yyyy-mm-dd
 * @param allPackage     当天数据的总包数
 * @param currentPackage 此包的位置
 */
fun onReadOriginProgressDetail(day: Int,date: String?,allPackage: Int,currentPackage: Int)

/**
 * 返回读取的进度
 *
 * @param progress 进度值，范围[0-1]
 */
fun onReadOriginProgress(progress:Float)

/**
 * 读取结束
 */
fun onReadOriginComplete();
```

**IOriginData3Listener** -- 日常数据返回监听（继承自IOriginDataListener）

```kotlin
/**
 * 该接口会在该天数据读取结束后回调。（一个OriginData3数据表示一个五分钟原始数据，一天最多24小时*60分钟/5分钟 = 288块五分钟原始数据）
 * 例如读取三天的原始数据则会依次返回今天-昨天-前天的五分钟原始数据列表，
 * 具体看读取几天的原始数据，读几天则会调用几次该接口
 *
 * @param originDataList 返回一个5分钟数据的列表，心率值是一个数组，其对应字段的是getPpgs()，不是getRateValue()
 */
fun onOriginFiveMinuteListDataChange(originDataList:List<OriginData3>)


/**
 * 当该天原始数据读取结束后去统计五分钟原始数据局列表生产30分钟数据列表
 *
 * @param originHalfHourDataList 返回一个30分钟数据的对象
 */
fun onOriginHalfHourDataChange(originHalfHourDataList:OriginHalfHourData)

/**
 * 读取该天的原始数据结束后统计产生当天的HRV数据列表
 *
 * @param originHrvDataList 返回一个hrv数据的列表
 */
fun onOriginHRVOriginListDataChange(originHrvDataList:List<HRVOriginData>)

/**
 * 读取该天的原始数据结束后统计产生当天的血氧数据列表
 *
 * @param originSpo2hDataList 返回一个血氧数据的列表
 */
fun onOriginSpo2OriginListDataChange(originSpo2hDataList:List<Spo2hOriginData>)
```

**OriginData3** -- 5分钟日常数据(继承OriginData，原基础上增加多了一下数据返回)

| 变量           | 类型           | 备注                 |
| -------------- | -------------- | -------------------- |
| gesture        | IntArray       | 佩戴姿态类型         |
| ppgs           | IntArray       | 5分钟脉率值          |
| ecgs           | IntArray       | 5分钟心率值          |
| resRates       | IntArray       | 5分钟呼吸率值        |
| sleepStates    | IntArray       | 5分钟睡眠状态值      |
| oxygens        | IntArray       | 5分钟血氧值          |
| apneaResults   | IntArray       | 呼吸暂停次数数组     |
| hypoxiaTimes   | IntArray       | 缺氧时间数组         |
| cardiacLoads   | IntArray       | 心脏负荷数组         |
| isHypoxias     | IntArray       | 呼吸暂停结果数组     |
| corrects       | IntArray       | 血氧矫正值字符串数组 |
| bloodGlucose   | Int            | 血糖值               |
| bloodComponent | BloodComponent | 血液成分             |
|                |                |                      |

**BloodComponent** --血液成分

| 变量     | 类型  | 备注                                                         |
| -------- | ----- | ------------------------------------------------------------ |
| uricAcid | Float | 尿酸值：单位μmol/L，值域[90.0, 1000.0]，上报值域[900, 10000] |
| tCHO     | Float | 总胆固醇：单位mmol/L，值域[0.01, 100.00]，上报值域[1, 10000] |
| tAG      | Float | 甘油三酯：单位mmol/L，值域[0.01, 100.00]                     |
| hDL      | Float | 高密度脂蛋白：单位mmol/L，值域[0.01, 100.00]                 |
| lDL      | Float | 低密度脂蛋白：单位mmol/L，值域[0.01, 100.00]                 |

###### 示例代码

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





## 个性化设置

#### 读取个性化设置

读取个性化设置,个性化设置包含公英制的功能,公英的开关状态,时间制式的状态,自动检测心率的开关状态,自动检测血压的开关状态等

```kotlin
readCustomSetting(bleWriteResponse,customSettingDataListener)
```

###### 参数解释

| 参数名                    | 类型                       | 备注                                      |
| ------------------------- | -------------------------- | ----------------------------------------- |
| bleWriteResponse          | IBleWriteResponse          | 写入操作的监听                            |
| customSettingDataListener | ICustomSettingDataListener | 个性化设置操作的监听,返回个性化设置的数据 |

###### 返回数据

ICustomSettingDataListener

```kotlin
/**
 * 返回个性化设置的数据
 *
 * @param customSettingData 个性化设置的数据
 */
fun OnSettingDataChange(customSettingData: CustomSettingData?)
```

**CustomSettingData**

| 变量                  | 类型              | 备注                                                         |
| --------------------- | ----------------- | ------------------------------------------------------------ |
| status                | ECustomStatus     | 获取操作的状态                                               |
| is24Hour              | boolean           | 时间格式是否是24小时                                         |
| metricSystem          | EFunctionStatus   | 公英制：[SUPPORT_OPEN表示公制,SUPPORT_CLOSE表示英制,UNSOUPRT表示不支持] |
| autoHeartDetect       | EFunctionStatus   | 心率自动测量                                                 |
| autoBpDetect          | EFunctionStatus   | 血压自动检测                                                 |
| sportOverRemain       | EFunctionStatus   | 运动过量提醒                                                 |
| voiceBpHeart          | EFunctionStatus   | 血压/心率播报                                                |
| findPhoneUi           | EFunctionStatus   | 控制查找手机UI                                               |
| secondsWatch          | EFunctionStatus   | 秒表                                                         |
| lowSpo2hRemain        | EFunctionStatus   | 低氧报警                                                     |
| skin                  | EFunctionStatus   | 肤色功能                                                     |
| autoHrv               | EFunctionStatus   | HRV自动检测                                                  |
| autoIncall            | EFunctionStatus   | 自动接听来电                                                 |
| disconnectRemind      | EFunctionStatus   | 断连提醒                                                     |
| SOS                   | EFunctionStatus   | 求救                                                         |
| ppg                   | EFunctionStatus   | ppg功能：脉率自动监测-->科学睡眠-->ppg                       |
| musicControl          | EFunctionStatus   | 音乐控制                                                     |
| longClickLockScreen   | EFunctionStatus   | 长按锁屏                                                     |
| messageScreenLight    | EFunctionStatus   | 消息亮屏功能                                                 |
| autoTemperatureDetect | EFunctionStatus   | 体温自动检测                                                 |
| temperatureUnit       | ETemperatureUnit  | 体温单位设置                                                 |
| ecgAlwaysOpen         | EFunctionStatus   | ecg常开                                                      |
| bloodGlucoseDetection | EFunctionStatus   | 血糖检测                                                     |
| METDetect             | EFunctionStatus   | 梅托检测                                                     |
| stressDetect          | EFunctionStatus   | 压力检测                                                     |
| bloodGlucoseUnit      | EBloodGlucoseUnit | 血糖单位                                                     |
| skinLevel             | Int               | 肤色等级                                                     |
| bloodComponentDetect  | EFunctionStatus   | 血液成分开关                                                 |
| uricAcidUnit          | EUricAcidUnit     | 尿酸单位                                                     |
| bloodFatUnit          | EBloodFatUnit     | 血脂单位                                                     |

**EBloodGlucoseUnit**

| 参数名 | 备注                       |
| ------ | -------------------------- |
| NONE   | 无单位，表示不支持单位设置 |
| mmol_L | mmol/L                     |
| mg_dl  | mg/dl                      |

**EUricAcidUnit**

| 参数名 | 备注                       |
| ------ | -------------------------- |
| NONE   | 无单位，表示不支持单位设置 |
| umol_L | umol/L                     |
| mg_dl  | mg/dl                      |

**EBloodFatUnit**

| 参数名 | 备注                       |
| ------ | -------------------------- |
| NONE   | 无单位，表示不支持单位设置 |
| mmol_L | mmol/L                     |
| mg_dl  | mg/dl                      |

**ETemperatureUnit**

| 参数名     | 备注                       |
| ---------- | -------------------------- |
| NONE       | 无单位，表示不支持单位设置 |
| CELSIUS    | 摄氏度                     |
| FAHRENHEIT | 华氏度                     |

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().readCustomSetting({

},object :ICustomSettingDataListener{
    override fun OnSettingDataChange(customSettingData: CustomSettingData?) {

    }

})
```



#### 修改个性化设置

修改个性化设置,个性化设置包含公英制的功能,公英的开关状态,时间制式的状态,自动检测心率的开关状态,自动检测血压的开关状态等

```kotlin
changeCustomSetting(bleWriteResponse, customSettingDataListener, customSetting)
```

###### 参数解释

| 参数名                    | 类型                       | 备注                                      |
| ------------------------- | -------------------------- | ----------------------------------------- |
| bleWriteResponse          | IBleWriteResponse          | 写入操作的监听                            |
| customSettingDataListener | ICustomSettingDataListener | 个性化设置操作的监听,返回个性化设置的数据 |
| customSetting             | CustomSetting              | 个性化的设置数据                          |

**CustomSetting**

| 参数名                      | 类型              | 备注                                                         |
| --------------------------- | ----------------- | ------------------------------------------------------------ |
| isHaveMetricSystem          | boolean           | 设置公英制的功能状态，返回true表示有此功能，可以设置公英制；返回false表示无此功能，不可以设置公英制 |
| isMetricSystem              | boolean           | 设置公英制的值，返回true表示公制，返回false表示英制,设备语言设置成[英语或繁体]才能体现英制 |
| is24Hour                    | boolean           | 设置时间制的值，返回ture表示24小时制，false表示12小时制      |
| isOpenAutoHeartDetect       | boolean           | 设置自动测量心率的状态，返回true表示打开了自动测量心率功能，返回false表示关闭自动测量心率功能 |
| isOpenAutoBpDetect          | boolean           | 设置自动测量血压的状态，返回true表示打开了自动测量血压功能，返回false表示关闭自动测量血压功能 |
| temperatureUnit             | ETemperatureUnit  | 设置温度单位                                                 |
| isOpenSportRemain           | EFunctionStatus   | 设置运动过量的状态，SUPPORT_OPEN 表示打开了运动过量提醒功能，SUPPORT_CLOSE 表示关闭运动过量提醒功能; UNSUPPORT表示不支持 |
| isOpenVoiceBpHeart          | EFunctionStatus   | 设置心率/血氧/血压的状态,SUPPORT_OPEN 表示打开了心率/血氧/血压播报功能，SUPPORT_CLOSE 表示关闭心率/血氧/血压播报功能; UNSUPPORT表示不支持 |
| isOpenFindPhoneUI           | EFunctionStatus   | 设置手机查找的状态，SUPPORT_OPEN 表示打开了手机查找功能，SUPPORT_CLOSE 表示关闭手机查找功能; UNSUPPORT表示不支持 |
| isOpenStopWatch             | EFunctionStatus   | 设置是否打开秒表功能，SUPPORT_OPEN 表示打开了秒表功能，SUPPORT_CLOSE 表示关闭秒表功能; UNSUPPORT表示不支持 |
| isOpenSpo2hLowRemind        | EFunctionStatus   | 设置低氧提醒，SUPPORT_OPEN 表示打开了低氧提醒功能，SUPPORT_CLOSE 表示关闭低氧提醒功能; UNSUPPORT表示不支持 |
| isOpenWearDetectSkin        | EFunctionStatus   | 设置打开肤色佩戴监测，SUPPORT_OPEN 表示偏白色肤色 ，SUPPORT_CLOSE 表示偏黑色肤色; UNSUPPORT表示不支持 |
| skinType                    | Int               | 肤色档位设置，范围0-6，从白色肤色-偏黑色肤色逐步递增，仅VpSpGetUtil.getVpSpVariInstance(mContext).getSkinType() == 2时设置，其他肤色类型设置无效 |
| isOpenAutoHRV               | EFunctionStatus   | 设置HRV自动检测功能                                          |
| isOpenAutoInCall            | EFunctionStatus   | 设置自动接听来电功能                                         |
| isOpenDisconnectRemind      | EFunctionStatus   | 设置断连提醒功能                                             |
| isOpenSOS                   | EFunctionStatus   | 设置求救功能                                                 |
| isOpenAutoTemperatureDetect | EFunctionStatus   | 设置体温自动检测功能                                         |
| ecgAlwaysOpen               | EFunctionStatus   | 设置ecg常开功能                                              |
| METDetect                   | EFunctionStatus   | 设置梅托检测功能                                             |
| stressDetect                | EFunctionStatus   | 设置压力检测功能                                             |
| isOpenPPG                   | EFunctionStatus   | 设置ppg功能，ppg开关也是精准睡眠开关                         |
| isOpenMusicControl          | EFunctionStatus   | 设置音乐控制功能                                             |
| isOpenLongClickLockScreen   | EFunctionStatus   | 设置长按锁屏                                                 |
| isOpenMessageScreenLight    | EFunctionStatus   | 设置消息亮屏                                                 |
| isOpenBloodGlucoseDetect    | EFunctionStatus   | 设置血糖自动检测功能                                         |
| bloodGlucoseUnit            | EBloodGlucoseUnit | 设置血糖单位                                                 |
| isOpenBloodComponentDetect  | EFunctionStatus   | 血液成分自动检测功能                                         |
| uricAcidUnit                | EUricAcidUnit     | 尿酸单位设置                                                 |
| bloodFatUnit                | EBloodFatUnit     | 血脂单位设置                                                 |

注意：**如果要设置某个功能的开关，需先读取个性化设置，判断该功能是否支持，如果支持才能设置开关状态，如果不支持，仍需下发不支持指令。**

###### 返回数据

ICustomSettingDataListener  同【[读取个性化设置](#读取个性化设置-readCustomSetting)】返回数据一致

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().changeCustomSetting({

},object :ICustomSettingDataListener{
    override fun OnSettingDataChange(customSettingData: CustomSettingData?) {

    }

},customSetting)
```





## 翻腕亮屏功能

#### 前提

设备需支持翻腕亮屏功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportNightturnSetting
```

#### 读取翻腕亮屏

设备需支持翻腕亮屏功能

###### 接口

```
readNightTurnWriste(bleWriteResponse, nightTurnWristeDataListener)
```

###### 参数解释

| 参数名                      | 类型                         | 备注             |
| --------------------------- | ---------------------------- | ---------------- |
| bleWriteResponse            | IBleWriteResponse            | 写入操作的监听   |
| nightTurnWristeDataListener | INightTurnWristeDataListener | 翻腕亮屏数据监听 |

###### 返回数据

**INightTurnWristeDataListener** -- 翻腕亮屏数据监听

```kotlin
/**
 * 返回翻腕亮屏[也称抬手亮屏]的数据
 *
 * @param nightTurnWristeData 抬手亮屏的数据
 */
fun onNightTurnWristeDataChange(nightTurnWristeData:NightTurnWristeData)
```

**NightTurnWristeData** -- 抬手亮屏的数据

| 变量                       | 类型                   | 备注                               |
| -------------------------- | ---------------------- | ---------------------------------- |
| OprateStauts               | ENightTurnWristeStatus | 操作翻腕亮屏状态                   |
| isSupportCustomSettingTime | Boolean                | 是否支持自定义时间设置，true为支持 |
| nightTureWirsteStatusOpen  | Boolean                | 翻腕亮屏是否打开                   |
| startTime                  | TimeData               | 开始时间                           |
| endTime                    | TimeData               | 结束时间                           |
| level                      | Int                    | 敏感等级                           |
| defaultLevel               | Int                    | 默认等级                           |

**ENightTurnWristeStatus** -- 状态

| 变量    | 备注     |
| ------- | -------- |
| SUCCESS | 成功     |
| FAIL    | 失败     |
| UNKONW  | 未知状态 |

###### 示例代码

```kotlin
VPOperateManager.getInstance().readNightTurnWriste({
    if (it != Code.REQUEST_SUCCESS) {
        // "write cmd failed"
    }
}, { nightTurnWristeData ->
    //success
})
```



#### 设置翻腕亮屏

需设备支持翻腕亮屏功能

###### 接口

```
settingNightTurnWriste(IBleWriteResponse bleWriteResponse, INightTurnWristeDataListener nightTurnWristeDataListener, NightTurnWristSetting nightTurnWristSetting)
```

###### 参数解释

| 参数名                      | 类型                         | 备注             |
| --------------------------- | ---------------------------- | ---------------- |
| bleWriteResponse            | IBleWriteResponse            | 写入操作的监听   |
| nightTurnWristeDataListener | INightTurnWristeDataListener | 翻腕亮屏数据监听 |
| nightTurnWristSetting       | NightTurnWristSetting        | 翻腕亮屏设置     |

**NightTurnWristSetting** -- 翻腕亮屏设置

| 参数名    | 类型     | 备注                             |
| --------- | -------- | -------------------------------- |
| isOpen    | Boolean  | 是否打开                         |
| startTime | TimeData | 开始时间                         |
| endTime   | TimeData | 结束时间                         |
| level     | Int      | 翻腕等级：范围【1-10.】，默认是5 |

###### 返回数据

同【[读取夜间翻腕](#读取夜间翻腕)】返回数据一致

###### 示例代码

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





## 屏幕调节功能

#### 前提

需设备支持屏幕调节功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportScreenlight
```



#### 读取屏幕调节数据

需支持屏幕调节功能

###### 接口

```kotlin
readScreenLight(bleWriteResponse, screenLightListener)
```

###### 参数解释

| 参数名              | 类型                 | 备注             |
| ------------------- | -------------------- | ---------------- |
| bleWriteResponse    | IBleWriteResponse    | 写入操作的监听   |
| screenLightListener | IScreenLightListener | 屏幕调节数据监听 |

###### 返回数据

**IScreenLightListener** -- 屏幕调节数据监听

```kotlin
/**
 * 屏幕亮度调节的回调
 *
 * @param screenLightData
 */
fun onScreenLightDataChange(screenLightData:ScreenLightData);
```

**ScreenLightData** -- 屏幕调节数据

| 变量          | 类型          | 备注         |
| ------------- | ------------- | ------------ |
| status        | EScreenLight  | 操作状态     |
| screenSetting | ScreenSetting | 屏幕亮度设置 |

**EScreenLight** -- 操作状态

| 变量            | 备注     |
| --------------- | -------- |
| SETTING_SUCCESS | 设置成功 |
| SETTING_FAIL    | 设置失败 |
| READ_SUCCESS    | 读取成功 |
| READ_FAIL       | 读取失败 |
| UNKONW          | 未知状态 |

**ScreenSetting** -- 屏幕设置

| 变量        | 类型 | 备注                          |
| ----------- | ---- | ----------------------------- |
| startHour   | Int  | 第一个档位作用开始小时        |
| startMinute | Int  | 第一个档位作用开始分钟        |
| endHour     | Int  | 第一个档位作用结束小时        |
| endMinute   | Int  | 第一个档位作用结束分钟        |
| level       | Int  | 设置时间段的第一个档位        |
| otherLeverl | Int  | 其他时间段亮度档位            |
| auto        | Int  | 自动调节：1自动 2手动 0旧协议 |
| maxLevel    | Int  | 最大的亮度调节档位            |

###### 示例代码

```kotlin
VPOperateManager.getInstance().readScreenLight(
    writeResponse
) { screenLightData ->
    val message = "屏幕调节数据-读取:$screenLightData"
}
```



#### 设置屏幕调节数据

需设备支持屏幕调节

###### 接口

```kotlin
settingScreenLight(bleWriteResponse, screenLightListener, screensetting)
```

###### 参数解释

| 参数名              | 类型                 | 备注             |
| ------------------- | -------------------- | ---------------- |
| bleWriteResponse    | IBleWriteResponse    | 写入操作的监听   |
| screenLightListener | IScreenLightListener | 屏幕调节数据监听 |
| screensetting       | ScreenSetting        | 屏幕设置参数     |

###### 返回数据

同【[读取屏幕调节数据](#读取屏幕调节数据)】返回的数据一致

###### 示例代码

```kotlin
//默认的是【22:00-07:00】设置成2档，其他时间设置成4档，用户可以自定义
VPOperateManager.getInstance().settingScreenLight(writeResponse,
    { screenLightData ->
        val message = "屏幕调节数据-设置:$screenLightData"
    }, ScreenSetting(22, 0, 7, 0, 2, 4)
)
```





## 屏幕亮度时长功能

#### 前提

需设备支持屏幕亮度时长调节功能，判断代码如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportScreenlightTime
```

#### 读取屏幕亮屏时长

设备需支持屏幕亮屏时长调节功能

###### 接口

```kotlin
readScreenLightTime(bleWriteResponse, screenLightTimeListener)
```

###### 参数解释

| 参数名                  | 类型                     | 备注             |
| ----------------------- | ------------------------ | ---------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听   |
| screenLightTimeListener | IScreenLightTimeListener | 屏幕亮屏时长监听 |

###### 返回数据

**IScreenLightTimeListener** -- 屏幕亮屏时长监听

```kotlin
/**
 * 屏幕亮屏时长的回调
 *
 * @param screenLightTimeData
 */
fun onScreenLightTimeDataChange(screenLightTimeData:ScreenLightTimeData)
```

**screenLightTimeData** -- 屏幕亮屏时长数据

| 变量              | 类型             | 备注     |
| ----------------- | ---------------- | -------- |
| screenLightState  | EScreenLightTime | 回调状态 |
| currentDuration   | Int              | 当前时长 |
| recommendDuration | Int              | 推荐时长 |
| maxDuration       | Int              | 最大时长 |
| minDuration       | Int              | 最小时长 |

**EScreenLightTime** -- 回调状态

| 变量            | 备注     |
| --------------- | -------- |
| SETTING_SUCCESS | 设置成功 |
| SETTING_FAIL    | 设置失败 |
| READ_SUCCESS    | 读取成功 |
| READ_FAIL       | 读取失败 |
| UNKONW          | 未知状态 |

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance()
    .readScreenLightTime(writeResponse, object : IScreenLightTimeListener {
        override fun onScreenLightTimeDataChange(screenLightTimeData: ScreenLightTimeData?) {
        
        }
    }
    )
```



#### 设置屏幕亮屏时长

需设备支持屏幕亮度时长调节功能

###### 接口

```kotlin
setScreenLightTime(IBleWriteResponse bleWriteResponse, IScreenLightTimeListener screenLightTimeListener, int time) 
```

###### 参数解释

| 参数名                  | 类型                     | 备注               |
| ----------------------- | ------------------------ | ------------------ |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听     |
| screenLightTimeListener | IScreenLightTimeListener | 屏幕亮屏时长监听   |
| time                    | Int                      | 亮屏时长，单位：秒 |

###### 返回数据

同【[读取屏幕亮屏时长](#读取屏幕亮屏时长)】返回数据一致

###### 示例代码

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





## 健康提醒

#### 读取健康提醒

###### 接口

```kotlin
readHealthRemind(healthRemindType, listener, bleWriteResponse)
```

###### 参数解释

| 参数名           | 类型                  | 备注             |
| ---------------- | --------------------- | ---------------- |
| healthRemindType | HealthRemindType      | 健康提醒类型     |
| listener         | IHealthRemindListener | 健康提醒数据监听 |
| bleWriteResponse | IBleWriteResponse     | 写入操作监听     |

**HealthRemindType** --- 健康提醒类型

| 参数名        | 备注     |
| ------------- | -------- |
| ALL           | 所有提醒 |
| SEDENTARY     | 久坐     |
| DRINK_WATER   | 喝水     |
| OVERLOOK      | 远眺     |
| SPORTS        | 运动     |
| TAKE_MEDICINE | 吃药     |
| READING       | 看书     |
| GOING_OUT     | 出行     |
| WASH          | 洗手     |

###### 返回数据

**IHealthRemindListener** -- 健康提醒数据监听

```kotlin
/**
 * 该功能不支持
 */
fun functionNotSupport()

/**
 * 健康提醒读取回调
 * @param healthRemind 健康提醒
 */
fun onHealthRemindRead(healthRemind: HealthRemind)

/**
 * 健康提醒读取失败
 */
fun onHealthRemindReadFailed()

/**
 * 健康提醒主动上报回调
 * @param healthRemind 健康提醒
 */
fun onHealthRemindReport(healthRemind: HealthRemind)

/**
 * 健康提醒主动上报失败
 */
fun onHealthRemindReportFailed()

/**
 * 健康提醒设置成功
 * @param healthRemind 健康提醒
 */
fun onHealthRemindSettingSuccess(healthRemind: HealthRemind)

/**
 * 健康提醒设置失败
 * @param healthRemindType 设置失败的健康提醒类型
 */
fun onHealthRemindSettingFailed(healthRemindType: HealthRemindType)
```

**HealthRemind** -- 健康提醒

| 参数名     | 类型             | 备注         |
| ---------- | ---------------- | ------------ |
| remindtype | HealthRemindType | 健康提醒类型 |
| startTime  | TimeData         | 提醒开始时间 |
| endTime    | TimeData         | 提醒结束时间 |
| interval   | Int              | 提醒间隔     |
| status     | Boolean          | 状态         |

###### 示例代码

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

###### 接口

```kotlin
settingHealthRemind(healthRemind, listener, bleWriteResponse)
```

###### 参数解释

| 参数名           | 类型                  | 备注             |
| ---------------- | --------------------- | ---------------- |
| healthRemind     | HealthRemind          | 健康提醒         |
| listener         | IHealthRemindListener | 健康提醒数据监听 |
| bleWriteResponse | IBleWriteResponse     | 写入操作监听     |

###### 返回数据

**IHealthRemindListener** 同【[读取健康提醒](#读取健康提醒)】返回数据一致

###### 示例代码

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





## 心率功能

#### 开始手动测量心率-startDetectHeart

```kotlin
startDetectHeart(bleWriteResponse,heartDataListener)
```

###### 参数解释

| 参数名            | 类型               | 备注               |
| ----------------- | ------------------ | ------------------ |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听     |
| heartDataListener | IHeartDataListener | 心率数据返回的监听 |

###### 返回数据

**IHeartDataListener**

```kotlin
/**
 * 返回心率数据
 *
 * @param heartData 心率数据
 */
fun onDataChange(heartData:HeartData);
```

**HeartData**

| 变量        | 类型         | 备注                           |
| ----------- | ------------ | ------------------------------ |
| data        | Int          | 获取心率值,范围[20-300]        |
| heartStatus | EHeartStatus | 测量心率时，设备可能返回的状态 |

**EHeartStatus**

| 变量                   | 备注               |
| ---------------------- | ------------------ |
| STATE_INIT             | 初使化             |
| STATE_HEART_BUSY       | 设备正忙           |
| STATE_HEART_DETECT     | 设备正在检测       |
| STATE_HEART_WEAR_ERROR | 检测中，但佩戴有误 |
| STATE_HEART_NORMAL     | 检测中             |

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().startDetectHeart({

},object :IHeartDataListener{
    override fun onDataChange(heartData: HeartData?) {
        
    }

})
```



#### 结束手动测量心率-stopDetectHeart

```
stopDetectHeart(bleWriteResponse)
```

###### 参数解释

| 参数名           | 类型              | 备注           |
| ---------------- | ----------------- | -------------- |
| bleWriteResponse | IBleWriteResponse | 写入操作的监听 |

###### 返回数据

无

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance().stopDetectHeart(){
    
}
```



#### 设置心率报警-settingHeartWarning

```kotlin
settingHeartWarning(bleWriteResponse, heartWaringDataListener, heartWaringSetting)
```

###### 参数解释

| 参数名                  | 类型                     | 备注           |
| ----------------------- | ------------------------ | -------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听 |
| heartWaringDataListener | IHeartWaringDataListener | 心率报警的回调 |
| heartWaringSetting      | HeartWaringSetting       | 心率报警的设置 |

**HeartWaringSetting**

| 参数名    | 类型    | 备注                        |
| --------- | ------- | --------------------------- |
| heartHigh | Int     | 心率报警的上限              |
| heartLow  | Int     | 心率报警的下限              |
| isOpen    | boolean | true表示打开，false表示关闭 |

###### 返回数据

IHeartWaringDataListener

```kotlin
/**
 * 返回心率报警的数据
 *
 * @param heartWaringData 心率报警的数据
 */
fun onHeartWaringDataChange(heartWaringData:HeartWaringData);
```

**HeartWaringData**

| 变量   | 类型               | 备注                                 |
| ------ | ------------------ | ------------------------------------ |
| status | EHeartWaringStatus | 心率报警的上限                       |
| ...    | --                 | 其它参数与**HeartWaringSetting**相同 |

**EHeartWaringStatus**

| 变量          | 备注     |
| ------------- | -------- |
| OPEN_SUCCESS  | 打开成功 |
| OPEN_FAIL     | 打开失败 |
| CLOSE_SUCCESS | 关闭成功 |
| CLOSE_FAIL    | 关闭失败 |
| READ_SUCCESS  | 读取成功 |
| READ_FAIL     | 读取失败 |
| UNSUPPORT     | 不支持   |
| UNKONW        | 未知     |

###### 示例代码

```kotlin
// kotlin code
val heartWaringSetting = HeartWaringSetting(120,40,true)
VPOperateManager.getInstance().settingHeartWarning({

},object :IHeartWaringDataListener{
    override fun onHeartWaringDataChange(heartWaringData: HeartWaringData?) {
        
    }

},heartWaringSetting)
```



#### 读取心率报警-readHeartWarning

```kotlin
readHeartWarning(bleWriteResponse,heartWaringDataListener)
```

###### 参数解释

| 参数名                  | 类型                     | 备注           |
| ----------------------- | ------------------------ | -------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听 |
| heartWaringDataListener | IHeartWaringDataListener | 心率报警的回调 |

###### 返回数据

IHeartWaringDataListener  与[设置心率报警-settingHeartWarning](#设置心率报警-settingHeartWarning)相同

###### 示例代码

```kotlin
// kotlin code
VPOperateManager.getInstance().readHeartWarning({

},object :IHeartWaringDataListener{
    override fun onHeartWaringDataChange(heartWaringData: HeartWaringData?) {

    }

})
```



#### 心率日常数据读取

在[读取日常数据功能](#读取日常数据功能)中，会返回心率日常数据。



#### 心率检测开关

在[修改个性化设置](#修改个性化设置-changeCustomSetting)中，可以设置心率检测的开关。





## 体温功能

#### 前提

在使用体温功能之前，需判断设备是否支持体温功能，在确认设置支持体温功能后方可使用体温功能相关接口

判断条件：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportReadTempture
```



#### 开始手动测量温度

###### 前提

1.需设备支持体温；
2.设备支持体温测量。

```kotlin
1.VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportReadTempture 2.VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportCheckTemptureByApp
```

###### 接口

```kotlin
startDetectTempture(bleWriteResponse, responseListener)
```

###### 参数解释

| 参数名           | 类型                        | 备注               |
| ---------------- | --------------------------- | ------------------ |
| bleWriteResponse | IBleWriteResponse           | 写入操作的监听     |
| responseListener | ITemptureDetectDataListener | 温度数据的返回监听 |

###### 返回数据

**ITemptureDetectDataListener**

```kotlin
/**
 * 返回温度数据
 *
 * @param temptureDetectData 温度数据
 */
fun onDataChange(temptureDetectData:TemptureDetectData)
```

**TemptureDetectData**

| 变量         | 类型  | 备注                                                         |
| ------------ | ----- | ------------------------------------------------------------ |
| oprate       | Int   | 0x00 不支持此功能，0x01 开启，0x02关闭                       |
| deviceState  | Int   | 0x00 可用，0x01-0x07 设备正忙，0x08 设备低电，0x09 传感器异常 |
| progress     | Int   | 读取进度                                                     |
| tempture     | Float | 体温值                                                       |
| temptureBase | Float | 体温原始值,基准值                                            |

###### 示例代码

```kotlin
//        kotlin code
        VPOperateManager.getInstance().startDetectTempture({

        },object :ITemptureDetectDataListener{
            override fun onDataChange(temptureDetectData: TemptureDetectData?) {

            }
        })
```



#### 结束手动测量温度

###### 前提

需设备支持温度且支持体温测量且已经开启手动测量温度

###### 接口

```
stopDetectTempture(bleWriteResponse, responseListener) 
```

###### 参数解释

| 参数名           | 类型                        | 备注                               |
| ---------------- | --------------------------- | ---------------------------------- |
| bleWriteResponse | IBleWriteResponse           | 写入操作的监听                     |
| responseListener | ITemptureDetectDataListener | 温度数据的返回监听，此接口可传null |

###### 返回数据

无

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().stopDetectTempture({

},null)
```



#### 日常体温数据读取

###### 前提

设备需支持体温，且需打开体温自动检测功能才会有数据返回（通过[个性化设置](#修改个性化设置-changeCustomSetting)打开）

```kotlin
VpSpGetUtil.getVpSpVariInstance(requireContext()).isSupportReadTempture
```

**注意：**当设备支持体温读取，且设备体温类型为5时，设备通过[读取日常数据功能](#读取日常数据功能)获取，无需调用以下readTemptureDataBySetting接口，判断如下：

```
VpSpGetUtil.getVpSpVariInstance(requireContext()).isSupportReadTempture && VpSpGetUtil.getVpSpVariInstance(requireContext()).getTemperatureType == 5
```

###### 接口

```kotlin
readTemptureDataBySetting(bleWriteResponse, temptureDataListener, readOriginSetting)
```

假如手表存储的数据是3天，读取所有的温度数据,读取的顺序为 [温度数据（今天-昨天-前天）],针对温度数据可以进行自定义天的位置,以及条数的位置
###### 参数解释

| 参数名               | 类型                  | 备注                                      |
| -------------------- | --------------------- | ----------------------------------------- |
| bleWriteResponse     | IBleWriteResponse     | 写入操作的监听                            |
| temptureDataListener | ITemptureDataListener | 读取温度数据的回调,返回读取的进度温度数据 |
| readOriginSetting    | ReadOriginSetting     | 读取日常数据设置                          |

**ReadOriginSetting** 同[读取日常数据](#接口-读取原始健康数据(5分钟原始数据)readOriginDataBySetting)参数一样

###### 返回数据

**ITemptureDataListener**

```kotlin
//kotlin code
/**
 * 读取温度日常数据回调
 * 
 * @param temptureDataList 温度数据列表
 */
fun onTemptureDataListDataChange(temptureDataList:List<TemptureData>)

/***
 * 返回读取的细节，此包的位置需要记住，下次读取数据时，传入此包的位置，可以避免重复读取
 *
 * @param day            数据在手表中的标志位[0=今天，1=昨天，2=前天]
 * @param date           数据的日期,格式为yyyy-mm-dd
 * @param allPackage     当天数据的总包数
 * @param currentPackage 此包的位置
 */
fun onReadOriginProgressDetail(day:Int, date:String, allPackage:Int, currentPackage：Int)

/**
 * 返回读取的进度
 *
 * @param progress 进度值，范围[0-1]
 */
fun onReadOriginProgress(progress:Float)

/**
 * 读取结束
 */
fun onReadOriginComplete()
```

**TemptureData**

| 变量          | 类型     | 备注              |
| ------------- | -------- | ----------------- |
| allPackage    | Int      | 总的包数          |
| packageNumber | Int      | 当前包数          |
| mTime         | TimeData | 时间              |
| isFromHandler | Boolean  | 是否是手动测量    |
| tempture      | Float    | 温度值            |
| baseTempture  | Float    | 温度原始值,基准值 |

###### 示例代码

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



#### 体温检测开关

在[修改个性化设置](#修改个性化设置-changeCustomSetting)中，可以设置体温检测的开关。





## ECG功能

#### 前提

需设备支持ECG功能，且设备处于空闲，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportECG
```

以下所有的接口均在设备支持ECG功能的前提下调用。

#### 开始手动测量ECG

###### 前提

设备支持ECG功能

###### 接口

```kotlin
startDetectECG(bleWriteResponse, isNeedCurve, ecgDetectListener) 
```

###### 参数解释

| 参数名            | 类型               | 备注               |
| ----------------- | ------------------ | ------------------ |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听     |
| isNeedCurve       | Boolean            | 是否要返回曲线数据 |
| ecgDetectListener | IECGDetectListener | ecg测量的回调      |

###### 返回数据

**IECGDetectListener**

```kotlin
/**
 * ECG测量基本信息(波形频率,采样频率)
 *
 * @param ecgDetectInfo
 */
fun onEcgDetectInfoChange(ecgDetectInfo: EcgDetectInfo?)

/**
 * ECG测量过程中的状态
 *
 * @param ecgDetectState
 */
fun onEcgDetectStateChange(ecgDetectState: EcgDetectState?)

/**
 * ECG测量的最终结果,异常时,即存在疾病,才会出值
 *
 * @param ecgDetectResult
 */
fun onEcgDetectResultChange(ecgDetectResult: EcgDetectResult?)

/**
 * ECG的波形数据
 *
 * @param data
 *
 *  * 界面上绘制ecg波形图使用该数据进行绘制，绘制时需要将#data转换成电压值,
 * 转换方法参考[com.veepoo.protocol.util.EcgUtil.convertToMvWithValue]
 * 如果数组data没有值则表示还没有生成合理的波形数据，测量ecg时会不停的谁更新数据，
 * 当数据为Int.MAX_VALUE 即2147483647 （16进制为0x7FFFFFFF）时需要过滤不画该点
 *  * 疲劳度操作的回调,返回疲劳度的数据:是否支持,开/关状态,进度,疲劳度值
 *
 */
fun onEcgADCChange(data: IntArray?)
```

**EcgDetectInfo**

| 变量          | 类型 | 备注     |
| ------------- | ---- | -------- |
| frequency     | Int  | 采样频率 |
| drawFrequency | Int  | 波形频率 |

**EcgDetectState**

| 变量        | 类型          | 备注                                                         |
| ----------- | ------------- | ------------------------------------------------------------ |
| ecgType     | Int           | ECG测量类型,通过VpSpGetUtil.getVpSpVariInstance(applicationContext).ecgType获取 |
| con         | Int           | ECG操作值                                                    |
| dataType    | Int           | 数据类型(0-采样频率)(1-设备实时状态)(2-诊断结果)(3-测试失败)(4-测试正常结束),这时只会出现1 |
| deviceState | EDeviceStatus | 设备状态                                                     |
| hr1         | Int           | 每秒心率                                                     |
| hr2         | Int           | 每秒 平均心率                                                |
| hrv         | Int           | hrv值，其中255为无效值,无需显示                              |
| rr1         | Int           | 每秒RR值                                                     |
| rr2         | Int           | 每6秒平均RR值                                                |
| br1         | Int           | 每秒呼吸率值                                                 |
| br2         | Int           | 每分钟平均呼吸率值                                           |
| wear        | Int           | 导联佩戴值,0表示佩戴通过,1表示佩戴不通过,若佩戴不通过，app应当关闭测量 |
| mid         | Int           | mid值                                                        |
| qtc         | Int           | qtc值                                                        |
| progress    | Int           | 进度                                                         |

**EDeviceStatus**

| 变量             | 备注                            |
| ---------------- | ------------------------------- |
| FREE             | 设备空闲                        |
| BUSY             | 设备正忙                        |
| DETECT_BP        | 设备正忙，正在测量血压          |
| DETECT_HEART     | 设备正忙，正在测量心率          |
| DETECT_AUTO_FIVE | 设备正忙，正在自动测量5分钟数据 |
| DETECT_SP        | 设备正忙，正在测量血氧          |
| DETECT_FTG       | 设备正忙，正在测量疲劳度        |
| DETECT_PPG       | 设备正忙，正在测脉率            |
| CHARGING         | 设备正在充电                    |
| CHARG_LOW        | 设备低电                        |
| UNPASS_WEAR      | 设备佩戴不通过                  |
| UNKONW           | 未知                            |

**EcgDetectResult**

| 变量                | 类型           | 备注               |
| ------------------- | -------------- | ------------------ |
| isSuccess           | Boolean        | 是否测量成功       |
| type                | EECGResultType | ECG结果的数据来向  |
| timeBean            | TimeData       | 测量日期           |
| frequency           | Int            | 采样频率           |
| drawfrequency       | Int            | 波形频率           |
| duration            | Int            | 总的秒数           |
| leadSign            | Int            | 导联信号           |
| originSign          | IntArray       | 原始信号           |
| powers              | IntArray       | 原始信号对应的增益 |
| filterSignals       | IntArray       | 原始信号           |
| result8             | IntArray       | 8个诊断数据        |
| diseaseResult       | IntArray       | 诊断结果           |
| aveHeart            | Int            | 平均心率           |
| aveResRate          | Int            | 平均呼吸率         |
| aveHrv              | Int            | 平均HRV            |
| aveQT               | Int            | 平均QT             |
| progress            | Int            | 进度               |
| detectHeartIntArray | IntArray       | 测量的心率数据     |
| detectBreath        | IntArray       | 呼吸率数据         |
| detectHrv           | IntArray       | HRV数据            |
| detectQT            | IntArray       | 测量的QT值数组     |

###### 示例代码

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



#### 结束手动测量ECG

###### 前提
1.设备支持ecg
2.设备空闲
3.设备已经开始手动测量ECG

###### 接口

```kotlin
stopDetectECG(bleWriteResponse, isNeedCurve, ecgDetectListener)
```

###### 参数解释

所需参数与[开始手动测量ECG-startDetectECG](#开始手动测量ECG-startDetectECG)相同

###### 返回数据

返回数据与[开始手动测量ECG-startDetectECG](#开始手动测量ECG-startDetectECG)相同

###### 示例代码

```kotlin
//kotlin code
VPOperateManager.getInstance()
    .stopDetectECG(bleWriteResponse, isNeedCurve, ecgDetectListener)
```



#### ECG新数据上报监听

###### 前提

设备支持ecg功能

###### 接口

```
setNewEcgDataReportListener(listener)
```

###### 参数解释

| 参数名   | 类型                      | 备注              |
| -------- | ------------------------- | ----------------- |
| listener | INewECGDataReportListener | 新ecg数据上报回调 |

###### 返回数据

**INewECGDataReportListener** -- 新ecg数据上报回调

```java
/**
 * 新的ECG测量数据 上报数据监听接口
 * 只要设备端有新的测量数据诞生时触发，触发时通过{@link VPOperateManager#readECGData(BleWriteResponse bleWriteResponse, TimeData timeData, EEcgDataType eEcgDataType, IECGReadDataListener onReadDataIdFinishCallBack)}接口读取ecg数据详情
 */
void onNewECGDetectDataReport();
```

###### 示例代码

```java
VPOperateManager.getInstance().setNewEcgDataReportListener(new INewECGDataReportListener() {
                @Override
                public void onNewECGDetectDataReport() {
                    showToast("监听到设备有新的ecg测量数据上报，请读取ECG数据获取详细信息");
                }
            });
```



#### ECG数据读取

###### 前提

1.设备支持ecg

###### 接口

```kotlin
readECGData(bleWriteResponse, timeData, eEcgDataType, onReadDataIdFinishCallBack)
```

###### 参数解释

| 参数名                     | 类型                 | 备注                                                         |
| -------------------------- | -------------------- | ------------------------------------------------------------ |
| bleWriteResponse           | IBleWriteResponse    | 写入操作的监听                                               |
| timeData                   | TimeData             | 数据开始读取的时间,当eEcgDataType==ALL的时候,年月日时分秒传0 |
| eEcgDataType               | EEcgDataType         | ECG数据的类型                                                |
| onReadDataIdFinishCallBack | IECGReadDataListener | 读取ECG数据的数据回调                                        |

**EEcgDataTypeEEcgDataType**

| 参数名   | 备注              |
| -------- | ----------------- |
| MANUALLY | 设备手动测量      |
| AUTO     | 设备主动测量      |
| ALL      | 设备手动+设备主动 |

###### 返回数据

**IECGReadDataListener**

```kotlin
/**
 * 数据读取完毕
 *
 * @param resultList ecg测量数组
 */
fun readDataFinish(resultList:List<EcgDetectResult>)
```

**EcgDetectResult**

同[开始手动测量ECG-startDetectECG](#开始手动测量ECG-startDetectECG)返回的EcgDetectResult一样

###### 示例代码

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



#### 读取ECG数据的ID

###### 前提

1.设备支持ecg

###### 接口

```kotlin
readECGId(bleWriteResponse, timeData, eEcgDataType, onReadIdFinishCallBack)
```

###### 参数解释

| 参数名                 | 类型               | 备注                                                         |
| ---------------------- | ------------------ | ------------------------------------------------------------ |
| bleWriteResponse       | IBleWriteResponse  | 写入操作的监听                                               |
| timeData               | TimeData           | 数据开始读取的时间,当eEcgDataType==ALL的时候,年月日时分秒传0 |
| eEcgDataType           | EEcgDataType       | ECG数据的类型                                                |
| onReadIdFinishCallBack | IECGReadIdListener | 读取数据ID结束的回调                                         |

###### 返回数据

IECGReadIdListener

```kotlin
/**
 * 读取数据ID结束的回调
 * @param ids ecg数据id列表
 */
fun readIdFinish(ids:IntArray?)
```

###### 示例代码

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



#### ECG胸口功能读取

###### 前提

1.设备支持ecg

###### 接口

```
readECGSwitchStatus(bleWriteResponse, listener)
```

###### 参数解释

| 参数名           | 类型               | 备注           |
| ---------------- | ------------------ | -------------- |
| bleWriteResponse | IBleWriteResponse  | 写入操作的监听 |
| listener         | IECGSwitchListener | ECG开关监听    |

###### 返回数据

**IECGSwitchListener**

```kotlin
/**
 * ECG开关状态改变
 *
 * @param ecgFunctionStatus ecg的功能状态:UNSUPPORT不支持，SUPPORT支持，SUPPORT_OPEN开，SUPPORT_CLOSE关，UNKONW未知
 */
fun onECGSwitchStatusChanged(ecgFunctionStatus:EFunctionStatus)
```

###### 示例代码

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



#### 打开ECG胸口功能

###### 前提

1.设备支持ecg
2.设备支持胸口功能：通过【[读取ECG胸口功能](#ECG胸口功能读取-readECGSwitchStatus)】判断是否支持

###### 接口

```
openECGSwitch(bleWriteResponse, listener)
```

###### 参数解释

同【[读取ECG胸口功能](#ECG胸口功能读取-readECGSwitchStatus)】参数一致

###### 返回数据

同【[读取ECG胸口功能](#ECG胸口功能读取-readECGSwitchStatus)】返回数据一致

###### 示例代码

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



#### 关闭ECG胸口功能

###### 前提

1.设备支持ecg
2.设备支持胸口功能：通过【[读取ECG胸口功能](#ECG胸口功能读取-readECGSwitchStatus)】判断是否支持

###### 接口

```
openECGSwitch(bleWriteResponse, listener)
```

###### 参数解释

同【[读取ECG胸口功能](#ECG胸口功能读取-readECGSwitchStatus)】参数一致

###### 返回数据

同【[读取ECG胸口功能](#ECG胸口功能读取-readECGSwitchStatus)】返回数据一致

###### 示例代码

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



#### 开始读取PTT数据

###### 前提

1.设备支持ecg；
2.设备支持ECG胸口功能；
3.设备已打开ECG胸口功能。

###### 接口

```
startReadPttSignData(bleWriteResponse, isNeedCurve, ecgDetectListener)
```

###### 参数解释

同【[开始手动测量ECG-startDetectECG](#开始手动测量ECG-startDetectECG)】参数一致

###### 返回数据

同【[开始手动测量ECG-startDetectECG](#开始手动测量ECG-startDetectECG)】返回数据一致

###### 示例代码

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



#### 结束读取PTT数据-stopReadPttSignData

###### 前提

1.设备支持ecg；
2.设备支持ECG胸口功能；
3.设备已打开ECG胸口功能;
4.设备已经开始读取PTT数据。

###### 接口

```
stopReadPttSignData(bleWriteResponse, isNeedCurve, ecgDetectListener)
```

###### 参数解释

同【[结束手动测量ECG-stopDetectECG](#结束手动测量ECG-stopDetectECG)】参数一致

###### 返回数据

同【[结束手动测量ECG-stopDetectECG](#结束手动测量ECG-stopDetectECG)】返回数据一致

###### 示例代码

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





## HRV功能

#### 前提

需设备支持HRV功能，且设备处于空闲，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportHRV
```

以下所有的接口均在设备支持HRV功能的前提下调用。

#### 读取HRV日常数据

HRV日常数据通过【[读取日常数据功能](#读取日常数据功能)】获取





## 睡眠功能

#### 读取睡眠数据

###### 接口

读取睡眠数据,从头开始读取,一直读取到结束

```kotlin
readSleepData(bleWriteResponse, sleepReadDatalistener, watchday)
```

读取睡眠数据,此方法表示可以定义你的读取位置,并按读取的顺序往下读

```kotlin
readSleepDataFromDay(bleWriteResponse, sleepReadDatalistener, day, watchday)
```

读取睡眠数据,此方法表示可以定义你的读取位置,并只读当天

```kotlin
readSleepDataSingleDay(bleWriteResponse, sleepReadDatalistener, day, watchday)
```

读取睡眠数据,此方法表示可以设置你的读取位置以及是否只读当天

```kotlin
readSleepDataBySetting(bleWriteResponse, sleepReadDatalistener, readSleepSetting)
```

###### 参数解释

| 参数名                | 类型                 | 备注                                                 |
| --------------------- | -------------------- | ---------------------------------------------------- |
| bleWriteResponse      | IBleWriteResponseInt | 写入操作的监听                                       |
| sleepReadDatalistener | ISleepDataListener   | 睡眠数据的监听,返回睡眠读取的进度,以及相应的睡眠数据 |
| day                   | Int                  | 读取哪天,0表示今天,1表示昨天,2表示前天,以此类推      |
| watchday              | Int                  | 手表存储的天数                                       |
| readSleepSetting      | ReadSleepSetting     | 睡眠的读取设置                                       |

**ReadSleepSetting**

| 参数名         | 类型    | 备注                                            |
| -------------- | ------- | ----------------------------------------------- |
| dayInt         | Int     | 读取哪天,0表示今天,1表示昨天,2表示前天,以此类推 |
| watchDataDay   | Int     | 手表存储的天数                                  |
| onlyReadOneDay | Boolean | true表示只读今天，false表示按顺序读取           |

###### 返回数据

**ISleepDataListener**

```kotlin
/**
 * 返回数据,睡眠可能是普通睡眠，也可能是精准睡眠，
 * 若为精准睡眠的则需要强制转换为SleepPrecisionData
 * 判断的方式有两种:
 * 1.根据密码通过后的功能标志位;
 * 2.根据instanceof关键字,if(sleepData instanceof SleepPrecisionData)
 *
 * @param day       表示正在读取第几天的数据 0：今天，1：昨天，2：前天。
 * @param sleepData 睡眠数据
 */
fun onSleepDataChange(day: String?, sleepData: SleepData?)

/**
 * 返回睡眠读取的进度,范围[0-1]
 *
 * @param progress 进度
 */
fun onSleepProgress(progress: Float)

/**
 * 返回读取睡眠的细节，此接口仅用于测试，方便开发人员查看读取进度
 *
 * @param day           表示正在读取第几天的数据，一天三天的数据，读取的顺序是今天-昨天-前天
 * @param packagenumber 表示第几包，一天的数据返回从最大包开始，依次递减
 */
fun onSleepProgressDetail(day: String?, packagenumber: Int)

/**
 * 睡眠读取结束
 */
fun onReadSleepComplete()
```

**SleepData**

| 变量          | 类型     | 备注                                                         |
| ------------- | -------- | ------------------------------------------------------------ |
| Date          | String   | 睡眠日期                                                     |
| cali_flag     | Int      | 睡眠定标值，目前这个值没有什么用                             |
| sleepQulity   | Int      | 睡眠质量                                                     |
| wakeCount     | Int      | 睡眠中起床的次数                                             |
| deepSleepTime | Int      | 深睡时长(单位min)                                            |
| lowSleepTime  | Int      | 浅睡时长(单位min)                                            |
| allSleepTime  | Int      | 睡眠总时长                                                   |
| sleepLine     | String   | 睡眠曲线，主要用于更具象化的UI来显示睡眠状态，如果您睡眠界面对UI没有特殊要求，可不理会,睡眠曲线分为普通睡眠和精准睡眠，普通睡眠是一组由0,1,2组成的字符串，每一个字符代表时长为5分钟，其中0表示浅睡，1表示深睡，2表示苏醒,比如“201112”，长度为6，表示睡眠阶段共30分钟，头尾各苏醒5分钟，中间浅睡5分钟，深睡15分钟;若是精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒 |
| sleepDown     | TimeData | 入睡时间                                                     |
| sleepUp       | TimeData | 起床时间                                                     |

###### 示例代码

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





## 杰理设备认证

#### 前提

需为杰理设备，判断条件如下：

```java
VpSpGetUtil.getVpSpVariInstance(mContext).isJieLiDevice()
或者
VPOperateManager.getInstance().isJLDevice()
```

当设备芯片平台为杰理时，有以下功能需要通过杰理SDK实现（维亿魄手表SDK也集成了杰理SDK）：

1. 照片表盘传输（只传输表盘照片和预览照片，不包含表盘元素设置）
2. 市场表盘传输
3. OTA升级

注：**操作这些功能的前提是需先进行杰理设备认证，App启动后，只需完成一次杰理设备认证即可。**

完成杰理设备认证需先打开杰理服务通知，再进行设备认证。

#### 打开杰理服务通知

###### 示例代码

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



#### 开始杰理设备认证

###### 示例代码

```java
private void startDeviceAuth() {
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
```





## 打开杰理文件系统

当设备芯片平台为杰理时，**每次操作以下功能实现之前需先操作打开杰理文件系统**：

1. 照片表盘传输（只传输表盘照片和预览照片，不包含表盘元素设置）
2. 市场表盘传输
3. OTA升级

#### 前提

需先完成【[杰理设备认证](#杰理设备认证)】，完成认证后，方可打开杰理文件系统

###### 示例代码

```java
private void getJLFileSystem() {
        //杰理文件系统
        VPOperateManager.getInstance().listJLWatchList(new JLWatchFaceManager.OnWatchDialInfoGetListener() {
            @Override
            public void onGettingWatchDialInfo() {
                //获取表盘信息中... 此时请勿做其他蓝牙操作
                loadingDialog.showNoTips();//弹出加载框，如需要用户自己实现
            }

            @Override
            public void onWatchDialInfoGetStart() {
                //开始获取手表表盘信息
                loadingDialog.showNoTips();//弹出加载框，如需要用户自己实现
            }

            @Override
            public void onWatchDialInfoGetComplete() {
                //获取表盘信息流程完成
                loadingDialog.disMissDialog();//关闭加载框，如需要用户自己实现
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
                Logger.t(TAG).e("照片表盘--->" + picFatFile.toString());
                loadingDialog.disMissDialog();
            }

            @Override
            public void onWatchDialInfoGetFailed(BaseError error) {
                //获取表盘信息失败
                tvFileSystemInfo.setText("获取文件系统列表-失败:\n" + error.toString());
                loadingDialog.disMissDialog();
            }
        });
    }
```

文件系统获取监听

```java
public interface OnWatchDialInfoGetListener {

    /**
     * 正在获取表盘信息
     * （一般在还未获取完成时再次调用则会回调该方法）
     */
    void onGettingWatchDialInfo();

    /**
     * 开始获取表盘信息
     */
    void onWatchDialInfoGetStart();

    /**
     * 获取表盘信息完成
     */
    void onWatchDialInfoGetComplete();

    /**
     * 获取手表表盘信息成功
     *
     * @param systemFatFiles 系统表盘
     * @param serverFatFiles 服务器表盘
     * @param picFatFile     照片表盘
     */
    void onWatchDialInfoGetSuccess(List<FatFile> systemFatFiles, List<FatFile> serverFatFiles, FatFile picFatFile);

    /**
     * 获取表盘失败
     */
    void onWatchDialInfoGetFailed(BaseError error);
}
```

### 



## 表盘功能

#### 前提

设备需支持屏幕样式读取

```
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportScreenStyle
```

表盘所有指令均需设备支持屏幕样式读取之后才可下发

#### 读取屏幕样式-readScreenStyle

获取当前屏幕的表盘风格和表盘下标。

###### 接口

```
readScreenStyle(bleWriteResponse, screenStyleListener)
```

###### 参数解释

| 参数名              | 类型                 | 备注                          |
| ------------------- | -------------------- | ----------------------------- |
| bleWriteResponse    | IBleWriteResponse    | 写入操作的监听                |
| screenStyleListener | IScreenStyleListener | 屏幕样式的监听,返回操作的状态 |

###### 返回数据

**IScreenStyleListener**

```kotlin
/**
 * 屏幕样式设置的回调
 *
 * @param screenStyleData 屏幕样式的数据
 */
fun onScreenStyleDataChange(screenStyleData:ScreenStyleData);
```

**ScreenStyleData**

| 变量        | 类型         | 备注                                                         |
| ----------- | ------------ | ------------------------------------------------------------ |
| status      | EScreenStyle | 操作状态                                                     |
| screenIndex | Int          | 表盘下标  默认表盘从0开始,最多七个默认表盘。 表盘市场以及自定义表盘都是从1开始 |
| screenType  | EUIFromType  | * 表盘风格 * 0x00 设备默认表盘 * 0x01 表盘市场(需设备支持) * 0x02 自定义表盘(需设备支持) |

**EScreenStyle**

| 变量            | 备注     |
| --------------- | -------- |
| SETTING_SUCCESS | 设置成功 |
| SETTING_FAIL    | 设置失败 |
| READ_SUCCESS    | 读取成功 |
| READ_FAIL       | 读取失败 |
| UNKONW          | 未知状态 |

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().readScreenStyle({

}, object : IScreenStyleListener {
    override fun onScreenStyleDataChange(screenStyleData: ScreenStyleData?) {

    }
}
)
```



#### 设置屏幕样式-settingScreenStyle

设置当前设备屏幕的表盘风格和表盘下标。

###### 接口

设置默认表盘的下标

```kotlin
settingScreenStyle(IBleWriteResponse bleWriteResponse, IScreenStyleListener screenStyleListener, int style)
```

  设置当前设备屏幕的表盘风格和表盘下标

```
settingScreenStyle(IBleWriteResponse bleWriteResponse, IScreenStyleListener screenStyleListener, int style, EUIFromType uiFromType)
```

###### 参数解释

| 参数名              | 类型                 | 备注                          |
| ------------------- | -------------------- | ----------------------------- |
| bleWriteResponse    | IBleWriteResponse    | 写入操作的监听                |
| screenStyleListener | IScreenStyleListener | 屏幕样式的监听,返回操作的状态 |
| style               | Int                  | 表盘的下标                    |
| uiFromType          | EUIFromType          | 表盘类型                      |

**EUIFromType**

| 参数名  | 备注               |
| ------- | ------------------ |
| DEFAULT | 手表自带的表盘     |
| CUSTOM  | 自定义可编辑的表盘 |
| SERVER  | 服务器的表盘       |
| ...     | 其余类型无需关注   |

###### 返回数据

**IScreenStyleListener**  同【[读取屏幕样式-readScreenStyle](#读取屏幕样式-readScreenStyle)】返回数据一致

###### 示例代码

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



### 本地表盘

###### 前提

先获取本地表盘数量，本地表盘数量获取方式

```
val defaultUiCount = SpUtil.getInt(applicationContext, SputilVari.COUNT_SCREEN_STYLE_TYPE, 1)
```

defaultUiCount>1才可以设置本地表盘

###### 接口

调用【[设置屏幕样式-settingScreenStyle](#设置屏幕样式-settingScreenStyle)】方法设置，uiFromType = EUIFromType.DEFAULT，style = 0 -（defaultUiCount - 1）

###### 示例代码

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



### 服务器表盘

#### 前提

1.设备UI传输方式为2；
2.服务器表盘数量大于0.

```kotlin
val bigTranType = VpSpGetUtil.getVpSpVariInstance(applicactionContext).bigTranType
val serverUICount = VpSpGetUtil.getVpSpVariInstance(applicactionContext).watchuiServer
var isSupport = (bigTranType == 2 && serverUICount > 0)
```

**注意⚠️ **：表盘传输需要增加一个异常保护场景：手表电量很低时，如果发起表盘传输，在传输过程中手表可能会因低电关机，重新充电后，表盘会变黑。 我们建议每次传输前，先读取一遍手表当前电量，如果电量状态为低电，则禁止传输。

#### 流程

设置来自服务器表盘UI的步骤大致分为以下几步:

>第1步.判断是否支持表盘市场
第2步.获取基本信息
第3步.获取支持的服务器UI列表
第4步.下载对应的UI文件
第5步.设置UI

#### 类名

注：**该功能操作使用的类名与前面类名不一样**

```kotlin
val mUiUpdateUtil = UiUpdateUtil.getInstance();
val uiUpdateCheckOprate = UiServerHttpUtil();
```

#### 第1步.判断是否支持表盘市场

```kotlin
//支持服务器表盘
if (mUiUpdateUtil.isSupportChangeServerUi()) {
    mUiUpdateUtil.init(context)
} else {
//不支持服务器表盘
    
}
```

#### 第2步.获取基本信息

###### 类名

UiUpdateUtil

###### 接口

```
getServerWatchUiInfo(uiBaseInfoFormServerListener)
```

###### 参数解释

| 参数名                       | 类型                          | 备注                 |
| ---------------------------- | ----------------------------- | -------------------- |
| uiBaseInfoFormServerListener | IUIBaseInfoFormServerListener | 获取基本的UI信息回调 |

###### 返回数据

**IUIBaseInfoFormServerListener**

```kotlin
/**
 * 返回ui基本信息-服务器，
 *
 * @param uiDataServer ui基本信息-服务器的ui
 */
fun onBaseUiInfoFormServer(uiDataServer:UIDataServer);
```

**UIDataServer**

| 变量               | 类型 | 备注                           |
| ------------------ | ---- | ------------------------------ |
| useType            | Int  | 数据使用类型                   |
| oprateType         | Int  | 操作类型                       |
| oprateState        | Int  | 操作状态                       |
| dataReceiveAddress | Int  | UI数据的接收起始地址           |
| dataCanSendLength  | Int  | 可接收的数据长度               |
| binDataType        | Int  | 文件类型，请求服务器要传的字段 |
| deviceAialShape    | Int  | 屏幕类型，请求服务器要传的字段 |
| imgCrcId           | Int  | 表盘CRC校验值                  |
| dataFileLength     | Long | 要发送的文件长度               |
| packageIndex       | Int  | 第几包                         |

###### 示例代码

```kotlin
UiUpdateUtil.getInstance().getServerWatchUiInfo { uiDataServer ->
    //"2.服务器的表盘基本信息 uiDataServer:$uiDataServer"
}
```



#### 第3步.获取支持的服务器UI列表

###### 类名

UiServerHttpUtil

###### 接口

```kotlin
getThemeInfo(uiDataServer, deviceNumber, deviceTestVersion, appPackName, appVersion):List<TUiTheme>
```

注：**此接口为网络请求，不能运行在主线程中**

###### 参数解释

| 参数名            | 类型         | 备注                 |
| ----------------- | ------------ | -------------------- |
| uiDataServer      | UIDataServer | 服务器的表盘基本信息 |
| deviceNumber      | String       | 设备编号             |
| deviceTestVersion | String       | 设备测试版本         |
| appPackName       | String       | app包名              |
| appVersion        | String       | app版本              |

其中，deviceNumber在【[连接设备确认密码时](#验证密码操作)】会返回，返回的类名为PwdData，字段为deviceNumber，deviceTestVersion为PwdData里面的deviceTestVersion值。

###### 返回数据

**List<TUiTheme>**

**TUiTheme**

| 变量        | 类型   | 备注                 |
| ----------- | ------ | -------------------- |
| crc         | String | 服务器表盘CRC        |
| binProtocol | String | 固件（此处无需关注） |
| dialShape   | String | 表盘形状             |
| fileUrl     | String | 文件下载路径         |
| previewUrl  | String | 表盘预览图路径       |

###### 示例代码

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



#### 第4步.下载对应的UI文件

###### 类名

UiServerHttpUtil

###### 接口

```
downloadFile(downUrl, fileSave, onDownLoadListener)
```

注：**此接口为网络请求，不能运行在主线程中**

###### 参数解释

| 参数名             | 类型               | 备注                                     |
| ------------------ | ------------------ | ---------------------------------------- |
| downUrl            | String             | 下载的表盘文件链接（第3步返回的fileUrl） |
| fileSave           | String             | 保存的本地路径                           |
| onDownLoadListener | OnDownLoadListener | 下载回调                                 |

###### 返回数据

OnDownLoadListener

```kotlin
/**
 * 返回下载固件进度值,范围[0-1]
 * @param progress
 */
fun onProgress(progress:Float);

/**
 * 下载结束
 */
fun onFinish();
```

###### 示例代码

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



#### 第5步.设置UI

注：**设置服务器表盘需注意设备是什么平台，杰理平台需单独走杰理的设置UI流程**

##### 非杰理平台设置UI

###### 类名

UiUpdateUtil

###### 接口

```
startSetUiStream(euiFromType, inputStream, uiUpdateListener) 
```

###### 参数解释

| 参数名           | 类型              | 备注                                  |
| ---------------- | ----------------- | ------------------------------------- |
| euiFromType      | EUIFromType       | UI类型 此处默认为：EUIFromType.SERVER |
| inputStream      | InputStream       | 表盘文件输入流                        |
| uiUpdateListener | IUiUpdateListener | UI升级回调                            |

###### 返回数据

**IUiUpdateListener**

```kotlin
/**
 * UI升级开始回调
 */
fun onUiUpdateStart()

/**
 * 开始UI擦除
 * @param sumCount 擦除总大小
 */
fun onStartClearCache(sumCount: Int)

/**
 * UI擦除进度回调
 * @param currentCount  当前擦除数
 * @param sumCount  擦除总大小
 * @param progress  擦除进度
 */
fun onClearCacheProgress(currentCount: Int, sumCount: Int, progress: Int)

/**
 * UI擦除完成
 */
fun onFinishClearCache()

/**
 * UI升级进度
 * @param currentBlock 当前包
 * @param sumBlock  总包
 * @param progress  升级进度
 */
fun onUiUpdateProgress(currentBlock: Int, sumBlock: Int, progress: Int)

/**
 * UI升级成功
 */
fun onUiUpdateSuccess()

/**
 * UI升级失败
 * @param eUiUpdateError 升级失败原因
 */
fun onUiUpdateFail(eUiUpdateError: EUiUpdateError?)
```

**EUiUpdateError**

| 变量                    | 备注                    |
| ----------------------- | ----------------------- |
| LISTENTER_IS_NULL       | 没有设置监听            |
| NEED_READ_BASE_INFO     | 没有先执行读取表盘信息  |
| FILE_UNEXIST            | 文件不存在              |
| LOW_BATTERY             | 电量过低                |
| INTO_UPDATE_MODE_FAIL   | 进入UI模式失败          |
| FILE_LENGTH_NOT_4_POWER | 文件没有4字节对齐       |
| CHECK_CRC_FAIL          | crc校验失败             |
| APP_CRC_SAME_DEVICE_CRC | app的CRC跟设备的CRC一致 |

注：当要传输的表盘CRC跟设备的CRC一致时，无需走服务器表盘传输逻辑，可以直接通过【[设置屏幕样式](#设置屏幕样式-settingScreenStyle)】接口直接设置服务器表盘。

###### 示例代码

```kotlin
al mUritempFile = Uri.fromFile(mUpdatefile)
val inputStream: InputStream =
    getContentResolver().openInputStream(mUritempFile)

/**
 * 升级ui步骤：开始升级-清除缓存数据-发送UI数据-结束发送
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

##### 杰理平台设置UI

###### 前提

需设备为杰理设备且已完成【[打开杰理文件系统](#打开杰理文件系统)】

###### 接口	

```
VPOperateManager.getInstance().setJLWatchDial(localServerDialPath, listener)
```

###### 参数解释

| 参数名              | 类型                                   | 描述                   |
| ------------------- | -------------------------------------- | ---------------------- |
| localServerDialPath | String                                 |                        |
| listener            | JLWatchHolder.OnSetJLWatchDialListener | 杰理照片表盘的传输监听 |

###### 返回数据

OnSetJLWatchDialListener ： 杰理服务器表盘传输监听

```java
/**
     * 传输市场表盘的监听
     */
    public interface OnSetJLWatchDialListener {
        /**
         * 开始传输表盘
         */
        void onStart();

        /**
         * 表盘传输进度
         *
         * @param progress 进度[0-100]
         */
        void onProgress(int progress);

        /**
         * 表盘传输完成
         *
         * @param watchPath 表盘在杰理文件系统中的路径 e.g /WATCH088
         */
        void onComplete(String watchPath);

        /**
         * 表盘传输失败
         *
         * @param code     失败code
         * @param errorMsg 失败原因
         */
        void onFiled(int code, String errorMsg);
    }
```





### 照片表盘

#### 前提

设备需支持照片表盘

**注意⚠️ **：表盘传输需要增加一个异常保护场景：手表电量很低时，如果发起表盘传输，在传输过程中手表可能会因低电关机，重新充电后，表盘会变黑。 我们建议每次传输前，先读取一遍手表当前电量，如果电量状态为低电，则禁止传输。

#### 流程

>第1步.判断是否支持照片表盘
>第2步.读取基本信息
>第3步.选中元素及元素对应的方位
>第4步.背景使用自选图片

#### 类名

**UiUpdateUtil**

#### 第1步.判断是否支持照片表盘

```kotlin
if (mUiUpdateUtil.isSupportChangeCustomUi()) {
  	//支持自定义表盘
    mUiUpdateUtil.init(context);
} else {
	//不支持自定义表盘
 	
}
```

#### 第2步.读取基本信息

###### 接口

```
getCustomWatchUiInfo( uiBaseInfoFormCustomListener)
```

###### 参数解释

| 参数名                       | 类型                          | 备注                     |
| ---------------------------- | ----------------------------- | ------------------------ |
| uiBaseInfoFormCustomListener | IUIBaseInfoFormCustomListener | 读取照片表盘基本信息回调 |

###### 返回数据

**IUIBaseInfoFormCustomListener**

```kotlin
/**
 * 返回ui基本信息-自定义
 *
 * @param uiDataCustom ui基本信息-自定义ui
 */
fun onBaseUiInfoFormCustom(uiDataCustom:UIDataCustom);
```

**UIDataCustom**

| 变量               | 类型                    | 备注                     |
| ------------------ | ----------------------- | ------------------------ |
| dataReceiveAddress | Int                     | UI数据的接收起始地址     |
| dataCanSendLength  | Int                     | 可接收的数据长度         |
| fileLength         | Long                    | 要发送的文件长度         |
| customUIType       | EWatchUIType            | 设备屏幕类型             |
| isDefalutUI        | Boolean                 | 是否是自定义中的默认表盘 |
| timePosition       | EWatchUIElementPosition | 元素的位置               |
| upTimeType         | EWatchUIElementType     | 时间上方的元素类型       |
| downTimeType       | EWatchUIElementType     | 时间下方的元素类型       |
| color888           | Int                     | 字体的显示颜色           |
| crc                | Int                     | 表盘的crc值              |
| packageIndex       | Int                     | 第几包，从1开始          |

###### 示例代码

```kotlin
mUiUpdateUtil.getCustomWatchUiInfo(IUIBaseInfoFormCustomListener { uiDataCustom ->
	
})
```



#### 第3步.选中元素及元素对应的方位

###### 接口

```
setCustomWacthUi(UICustomSetData uiCustomSetData, final IUIBaseInfoFormCustomListener uiBaseInfoFormCustomListener)
```



###### 参数解释

| 参数名                       | 类型                          | 备注             |
| ---------------------------- | ----------------------------- | ---------------- |
| uiCustomSetData              | UICustomSetData               | 照片表盘设置     |
| uiBaseInfoFormCustomListener | IUIBaseInfoFormCustomListener | 照片表盘设置回调 |

**UICustomSetData**

| 参数名imePosition | 类型EWatchUIElementPosition | 备注元素的位置     |
| ----------------- | --------------------------- | ------------------ |
| timePosition      | EWatchUIElementPosition     | 元素的位置         |
| upTimeType        | EWatchUIElementType         | 时间上方的元素类型 |
| downTimeType      | EWatchUIElementType         | 时间下方的元素类型 |
| color888          | Int                         | 字体的显示颜色     |
| isDefalutUI       | Boolean                     | 是否是默认的照片   |

通过查询接口返回customUIType来获取设备的WatchUIType，可以知道设备支持的元素位置、默认图片、照片比例等信息。

###### 返回数据

**IUIBaseInfoFormCustomListener**

```kotlin
/**
 * 返回ui基本信息-自定义
 *
 * @param uiDataCustom ui基本信息-自定义ui
 */
fun onBaseUiInfoFormCustom(uiDataCustom:UIDataCustom);
```

###### 示例代码

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
         * app上颜色跟设备的颜色虽然是同一个颜色值，但是显示上会有差别
         * 因为App上可以显示RCG_888,设备只能显示RGB_565
         * 所以为了显示效果，最好是自己做一个映射表，
         * 比如app上的A1和设备上的A2颜色相近,那么在app上显示A1颜色，下发给设备的是A2颜色。
         */
    })
```

#### 第4步.背景使用自选图片

##### 非杰理平台设置

###### 接口

通过查询接口返回customUIType来获取设备的WatchUIType，可以知道设备支持的元素位置、默认图片、照片宽高等信息

注：选择的图片裁剪宽高需与设备基本信息返回的图片宽高一致。

```
startSetUiStream(euiFromType, inputStream, uiUpdateListener) 
```

###### 参数解释

| 参数名           | 类型              | 备注                                  |
| ---------------- | ----------------- | ------------------------------------- |
| euiFromType      | EUIFromType       | UI类型 此处默认为：EUIFromType.CUSTOM |
| inputStream      | InputStream       | 表盘文件输入流                        |
| uiUpdateListener | IUiUpdateListener | UI升级回调                            |

###### 返回数据

**IUiUpdateListener**

同【[服务器表盘-设置Ui](#第5步.设置UI)】返回一致

###### 示例代码

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



##### 杰理平台设置

###### 前提

需设备为杰理设备且已完成【[打开杰理文件系统](#打开杰理文件系统)】

###### 接口	

```
VPOperateManager.getInstance().setJLWatchPhotoDial(dialPhotoPath, listener)
```

###### 参数解释

| 参数名        | 类型                                         | 描述                                                         |
| ------------- | -------------------------------------------- | ------------------------------------------------------------ |
| dialPhotoPath | String                                       | 裁剪好尺寸的照片文件路径。（建议保存在固定的文件夹目录下。且裁剪的尺寸保持和当前的表盘尺寸一致，否则将无法传输 如 240x280的表盘则改图片需要宽高为240x280的尺寸） |
| listener      | JLWatchFaceManager.JLTransferPicDialListener | 杰理照片表盘的传输监听                                       |

###### 返回数据

JLTransferPicDialListener ： 杰理照片表盘传输监听

```java
/**
 * 杰理照片表盘传输监听
 */
public interface JLTransferPicDialListener {
    /**
     * 开始传输照片表盘
     */
    void onJLTransferPicDialStart();

    /**
     * 表盘传输进度监听
     *
     * @param progress 进度[0-100]
     */
    void onTransferPicDialProgress(int progress);

    /**
     * 预览图传输完成,该方法可以不用关心
     */
    void onScaleBGPFileTransferComplete();

    /**
     * 表盘照片大图传输完成,该方法可以不用关心
     */
    void onBigBGPFileTransferComplete();

    /**
     * 照片表盘传输完成【传输时会先传输预览图再传输表盘大图】
     */
    void onTransferComplete();

    /**
     * 照片表盘传输异常
     *
     * @param code     异常代码
     * @param errorMsg 错误原因
     */
    void onTransferError(int code, String errorMsg);
}
```

###### 示例代码

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





## 查找设备功能

#### 前提

设备需支持手机查找设备功能，该功能所有接口均需设备支持才可调用，判断条件如下：

```
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportFindDeviceByPhone
```

#### 手机主动开始查找设备-startFindDeviceByPhone

###### 接口

```
startFindDeviceByPhone(bleWriteResponse, findDevicelistener)
```

###### 参数解释

| 参数名             | 类型                 | 备注           |
| ------------------ | -------------------- | -------------- |
| bleWriteResponse   | IBleWriteResponseInt | 写入操作的监听 |
| findDevicelistener | IFindDevicelistener  | 查找设备监听   |

###### 返回数据

**IFindDevicelistener**

```kotlin
/**
 * 不支持通过手机找
 */
fun unSupportFindDeviceByPhone()
/**
 * 设备已经被找到
 */
fun findedDevice()
/**
 * 查找超时
 */
fun unFindDevice()
/**
 * 设备正在震动亮屏，处于查找状态中
 */
fun findingDevice()
```

###### 示例代码

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



#### 手机停止查找设备-stopFindDeviceByPhone

###### 接口

```
stopFindDeviceByPhone(bleWriteResponse, findDevicelistener)
```

###### 参数解释

同【[手机主动开始查找设备-startFindDeviceByPhone](#手机主动开始查找设备-startFindDeviceByPhone)】参数一致

###### 返回数据

同【[手机主动开始查找设备-startFindDeviceByPhone](#手机主动开始查找设备-startFindDeviceByPhone)】返回数据

###### 示例代码

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






## 消息通知

当设备调用密码校验时会触发社交消息功能状态的上报回调 请参考[ 验证密码操作](\#验证密码操作)


消息通知功能主要包括

1. 消息通知开关状态的读取和设置
2. 发送消息通知

### 消息通知开关状态的读取和设置

#### 消息通知开关状态读取

```
VPOperateManager.getInstance().readSocialMsg(writeResponse, listener)
```

| 参数名        | 类型                   | 备注                     |
| ------------- | ---------------------- | ------------------------ |
| writeResponse | IBleWriteResponse      | 写入操作的监听           |
| listener      | ISocialMsgDataListener | 消息通知开关状的回调监听 |

#### 或者通过以下方法获取SDK缓存的消息开关状态

```
VPOperateManager.getInstance().getFunctionSocailMsgData()
```

此方法会返回消息通知的开关状态FunctionSocailMsgData，如果此方法获取的实体为null，请调用readSocialMsg方法。

#### 消息通知开关状态设置

```
 VPOperateManager.getInstance().settingSocialMsg(writeResponse, listener, socailMsgData);
```

| 参数名        | 类型                   | 备注                     |
| ------------- | ---------------------- | ------------------------ |
| writeResponse | IBleWriteResponse      | 写入操作的监听           |
| listener      | ISocialMsgDataListener | 消息通知开关状的回调监听 |
| socailMsgData | FunctionSocailMsgData  | 需要设置的开关状态       |

#### 开关状态和监听回调

ISocialMsgDataListener：消息通知开关状态改变监听，当读取或设置时该监听会被调用。**注意：部分手表可能支持的社交消息类型小于17种，则该方法不会被回调**

```
/**
 * 社交消息状态监听
 */
public interface ISocialMsgDataListener extends IListener {
    /**
     * 社交消息状态改变监听，第一包
     * 注意：部分手表可能支持的社交消息类型小于17种，则
     *
     * @param socailMsgData 社交消息开关
     */
    void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData);

    /**
     * 社交消息状态改变监听，第一包
     * 注意：部分手表可能支持的社交消息类型小于17种，则该方法不会被回调
     *
     * @param socailMsgData 社交消息开关
     */
    void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData);
}
```

FunctionSocailMsgData：消息通知状态

| 成员名        | 类型            | 描述             |
| ------------- | --------------- | ---------------- |
| phone         | EFunctionStatus | 电话             |
| msg           | EFunctionStatus | 短信             |
| wechat        | EFunctionStatus | 微信             |
| qq            | EFunctionStatus | QQ               |
| sina          | EFunctionStatus | 新浪             |
| facebook      | EFunctionStatus | Facebook         |
| twitter       | EFunctionStatus | X(原推特twitter) |
| flickr        | EFunctionStatus | Flickr           |
| Linkin        | EFunctionStatus | 领克 Linkin      |
| whats         | EFunctionStatus | Whats            |
| line          | EFunctionStatus | Line             |
| instagram     | EFunctionStatus | Instagram        |
| snapchat      | EFunctionStatus | Snapchat         |
| skype         | EFunctionStatus | Skype            |
| gmail         | EFunctionStatus | Gmail            |
| dingding      | EFunctionStatus | 钉钉 dingding    |
| wxWork        | EFunctionStatus | 企业微信 wxWork  |
| tikTok        | EFunctionStatus | 抖音 TikTok      |
| telegram      | EFunctionStatus | Telegram         |
| connected2_me | EFunctionStatus | Connected2Me     |
| kakaoTalk     | EFunctionStatus | KakaoTalk        |
| messenger     | EFunctionStatus | Messenger        |
| other         | EFunctionStatus | 其他消息         |
| shieldPolice  | EFunctionStatus | 警右             |

EFunctionStatus：功能状态（枚举类型）

| 类型          | 描述         |
| ------------- | ------------ |
| UNSUPPORT     | 该功能不支持 |
| SUPPORT       | 支持         |
| SUPPORT_OPEN  | 支持且打开   |
| SUPPORT_CLOSE | 支持且关闭   |
| UNKONW        | 未知状态     |

### 发送消息通知

消息通知分为三类消息通知

1. 手机来电消息通知
2. 手机短信消息通知
3. 社交消息通知（各类app的通知消息如微信，QQ，X(原推特)等）

#### 消息发送

```
VPOperateManager.getInstance().sendSocialMsgContent(writeResponse, contentSetting)
```

| 参数名         | 类型              | 描述           |
| -------------- | ----------------- | -------------- |
| writeResponse  | IBleWriteResponse | 写入操作的监听 |
| contentSetting | ContentSetting    | 消息内容       |

ContentSetting：消息内容 其中ESocailMsg为信息类型

```
public abstract class ContentSetting {
   /**
    * 信息推送的枚举
    */
   private ESocailMsg eSocailMsg;
  ....
```

ESocailMsg：社交消息类型枚举

```
G15MSG((byte) 0XFE), //G15项目专属，可以不用理会

/**
 * 电话
 */
PHONE((byte) 0x00),
/**
 * 短信
 */
SMS((byte) 0x01),
/**
 * 微信
 */
WECHAT((byte) 0x02),
/**
 * QQ【正式版，轻聊版，国际版】
 */
QQ((byte) 0x03),
/**
 * 新浪微博
 */
SINA((byte) 0x04),
/**
 * facebook脸谱
 */
FACEBOOK((byte) 0x05),
/**
 * X(原推特twitter)
 */
TWITTER((byte) 0x06),
/**
 * filck
 */
FLICKR((byte) 0x07),
/**
 * linkin领英
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
 * gmail邮箱
 */
GMAIL((byte) 0x0E),
/**
 * 钉钉
 */
DINGDING((byte) 0x0F),
/**
 * 企业微信
 */
WXWORK((byte) 0x10),

/**
 * 非以上的其他通知
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
 * 警右
 */
SHIELD_POLICE((byte) 0x16),//专属项目不用理会
/**
 * facebook下的Messenger
 */
MESSENGER((byte) 0x17),
```

#### 手机来电消息通知类型

手机来电消息使用的是 ContentPhoneSetting，继承自ContentSetting，如果同时传入名字跟电话，手表会显示名字，联系人姓名可以传null
ContentPhoneSetting

| 成员名             | 类型   | 备注       |
| ------------------ | ------ | ---------- |
| contactName        | String | 联系人名称 |
| contectPhoneNumber | String | 联系人号码 |

推荐使用以下构造方法

```
ContentPhoneSetting contentSetting = new ContentPhoneSetting(ESocailMsg.PHONE, "张三", "010-6635214");
```

#### 手机短信消息通知类型

手机短信消息使用的是 ContentSmsSetting，继承自ContentSetting，如果同时传入名字跟电话，手表会显示名字
ContentSmsSetting

| 成员名             | 类型   | 备注       |
| ------------------ | ------ | ---------- |
| contactName        | String | 联系人名称 |
| contectPhoneNumber | String | 联系人号码 |
| content            | String | 信息内容   |

推荐使用以下构造方法

```
ContentPhoneSetting contentSetting = new ContentSmsSetting(ESocailMsg.SMS, "李四", "010-6635214", "今天手气如何？");
```

#### 社交消息类型 

社交消息类型 使用的是 ContentSocailSetting，继承自ContentSetting

```
/**
 * @param eSocailMsg 信息的类型
 * @param title      信息的标题
 * @param content    信息的内容
 */
public ContentSocailSetting(ESocailMsg eSocailMsg, String title, String content) {
    super(eSocailMsg);
    this.title = title;
    this.content = content;
}
```





## 音乐控制

#### 前提

需设备支持音乐控制，判断条件如下：

```kotlin
val musicType ==  VpSpGetUtil.getVpSpVariInstance(applicationContext).musicType
if(musicType == 1){
    //设备支持音乐控制
}else{
    //设备不支持音乐控制
}
```

#### 设置音乐数据

需设备支持音乐控制

###### 接口

```kotlin
settingMusicData(bleWriteResponse, musicData, iMusicControlListener)
```

###### 参数解释

| 参数名                | 类型                  | 备注                   |
| --------------------- | --------------------- | ---------------------- |
| bleWriteResponse      | IBleWriteResponse     | 写入操作的监听         |
| musicData             | MusicData             | 音乐数据(歌名、开关等) |
| iMusicControlListener | IMusicControlListener | 音乐控制监听           |

**MusicData** -- 音乐数据

| 参数名          | 类型   | 备注                       |
| --------------- | ------ | -------------------------- |
| musicAppId      | String | 音乐appid                  |
| musicAlbum      | String | 音乐专辑                   |
| musicName       | String | 音乐名                     |
| singerName      | String | 歌手名                     |
| palyStatus      | Int    | 状态：1 播放状态 2暂停状态 |
| musicVoiceLevel | Int    | 音量等级[1-100]            |

###### 返回数据

**IMusicControlListener** -- 音乐控制数据的返回监听，音乐控制功能存在标志位

```kotlin
/**
 * 下一曲,请执行相关的操作
 */
fun nextMusic()

/**
 * 上一曲,请执行相关的操作
 */

fun previousMusic()

/**
 * 暂停和播放,请执行相关的操作
 */
fun pauseAndPlayMusic()

/**
 * 暂停,请执行相关的操作
 */
fun pauseMusic()

/**
 * 播放,请执行相关的操作
 */
fun playMusic()

/**
 * 调高音量请执行相关的操作
 */
fun voiceUp()

/**
 * 音量下降请执行相关的操作
 */
fun voiceDown()

/**
 * 操作成功
 */
fun oprateMusicSuccess()

/**
 * 操作失败
 */
fun oprateMusicFail()
```

###### 示例代码

```kotlin
val play = 1 //播放状态
val pause = 2 //暂停状态
val musicData = MusicData("周杰伦", "上海一九四三", "范特西", 80, play )
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

#### 设置设备音量

需设备支持音乐控制

###### 接口

```kotlin
settingVolume(volumeLevel,bleWriteResponse,iMusicControlListener)
```

###### 参数解释

| 参数名                | 类型                  | 备注                             |
| --------------------- | --------------------- | -------------------------------- |
| bleWriteResponse      | IBleWriteResponse     | 写入操作的监听                   |
| volumeLevel           | Int                   | 需要设置的音量值，范围是从0到100 |
| iMusicControlListener | IMusicControlListener | 音乐控制监听                     |

###### 返回数据

同【[设置音乐数据](#设置音乐数据返回)】返回数据一致

###### 示例代码

```kotlin
VPOperateManager.getInstance().settingVolume(50,bleWriteResponse,iMusicControlListener)
```



## 蓝牙通话功能

#### 注册设备蓝牙通话功能监听-registerBTInfoListener

###### 接口 

```
registerBTInfoListener(iDeviceBTInfoListener)
```

###### 参数解释

| 参数名                | 类型                  | 备注                 |
| --------------------- | --------------------- | -------------------- |
| iDeviceBTInfoListener | IDeviceBTInfoListener | 设备蓝牙通话功能监听 |

###### 返回数据

**IDeviceBTInfoListener**

```kotlin
/**
 * 设备不支持BT（蓝牙3.0功能）,无此回调代表设备支持BT
 */
fun onDeviceBTFunctionNotSupport()

/**
 * 设备经典蓝牙设置回调
 *
 * @param btInfo       BT状态详情
 */
fun onDeviceBTInfoSettingSuccess(btInfo: BTInfo)

/**
 * 设备BT状态设置失败
 */
fun onDeviceBTInfoSettingFailed()

/**
 * 设备经典蓝牙读取回调
 *
 * @param btInfo       BT状态详情
 */
fun onDeviceBTInfoReadSuccess(btInfo: BTInfo)

/**
 * 读取设备BT状态失败
 */
fun onDeviceBTInfoReadFailed()

/**
 * 设备经典蓝牙上报回调
 *
 * @param btInfo       BT状态详情
 */
fun onDeviceBTInfoReport(btInfo: BTInfo)
```

**BTInfo**

| 变量           | 类型    | 备注                                             |
| -------------- | ------- | ------------------------------------------------ |
| status         | Int     | 设备蓝牙通话状态 0：断开， 1：连接， 2：配对中。 |
| isBTOpen       | Boolean | 设备蓝牙通话是否打开                             |
| isAutoCon      | Boolean | 设备蓝牙通话是否会自动回连                       |
| isAudioOpen    | Boolean | 多媒体音频是否打开                               |
| isHavePairInfo | Boolean | 是否有配对信息                                   |

###### 示例代码

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



#### 注册设备蓝牙通话连接状态监听-registerBTConnectionListener

###### 接口

```
registerBTConnectionListener(iDeviceBTConnectionListener)
```

###### 参数解释

| 参数名                      | 类型                        | 备注                     |
| --------------------------- | --------------------------- | ------------------------ |
| iDeviceBTConnectionListener | IDeviceBTConnectionListener | 设备蓝牙通话连接状态监听 |

###### 返回数据

**IDeviceBTConnectionListener**

```kotlin
/**
 * 开始连接BT
 */
fun onDeviceBTConnecting() {}

/**
 * 设备BT已连接
 */
fun onDeviceBTConnected() {}

/**
 * 设备BT已断开
 */
fun onDeviceBTDisconnected() {}

/**
 * 设备BT连接超时
 */
fun onDeviceBTConnectTimeout() {}
```

###### 示例代码

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



#### 手动连接BT-connectBT

###### 前提

注册设备蓝牙通话功能监听后，没有onDeviceBTFunctionNotSupport回调（即设备支持BT功能）

###### 接口

```
connectBT(mac, listener)
```

###### 参数解释

| 参数名   | 类型                        | 备注                |
| -------- | --------------------------- | ------------------- |
| mac      | String                      | 要连接的设备蓝牙mac |
| listener | IDeviceBTConnectionListener | 设备连接监听回调    |

###### 返回数据

**IDeviceBTConnectionListener** 

###### 代码示例

```kotlin
//        kotlin code
        val mac = "F1:F2:F3:F4:F5"
        VPOperateManager.getInstance().connectBT(mac,object :IDeviceBTConnectionListener{
            
        })
```



#### 手动断开BT连接-disconnectBT

###### 前提

设备支持BT且已经连上了BT

###### 接口

```
disconnectBT(String mac, IDeviceBTConnectionListener listener)
```

###### 参数解释

同【[手动连接BT-connectBT](#手动连接BT-connectBT)】参数一致

###### 返回数据

同【[手动连接BT-connectBT](#手动连接BT-connectBT)】返回数据一致

###### 示例代码

```kotlin
//        kotlin code
        val mac = "F1:F2:F3:F4:F5"
        VPOperateManager.getInstance().disconnectBT(mac,object :IDeviceBTConnectionListener{

        })
```



#### 读取BT信息-readBTInfo

```
readBTInfo(bleWriteResponse, listener)
```

###### 参数解释

| 参数名           | 类型                  | 备注             |
| ---------------- | --------------------- | ---------------- |
| bleWriteResponse | IBleWriteResponse     | 写入操作的监听   |
| listener         | IDeviceBTInfoListener | 设备连接监听回调 |

###### 返回数据

同【[手动连接BT-connectBT](#手动连接BT-connectBT)】返回数据一致

###### 示例代码

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



#### 设置BT状态-setBTStatus

###### 前提

设备支持BT，且设备BT连接成功

###### 接口

```kotlin
setBTStatus(boolean isAutoConnect, boolean isBTOpen, boolean isAudioOpen, isClearPairInfo, bleWriteResponse)
```

###### 参数解释

| 参数名           | 类型              | 备注                 |
| ---------------- | ----------------- | -------------------- |
| isAutoConnect    | Boolean           | 设备是否自动回连     |
| isBTOpen         | Boolean           | 设备BT是否开启       |
| isAudioOpen      | Boolean           | 多媒体开关是否打开   |
| isClearPairInfo  | Boolean           | 是否清除设备配对信息 |
| bleWriteResponse | IBleWriteResponse | 写入操作的监听       |

###### 返回数据

无

###### 代码示例

```kotlin
//kotlin code
VPOperateManager.getInstance().setBTStatus(false, isBTOpen, isAudioOpen, false) {
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}
```



#### 设置BT开关状态-setBTSwitchStatus

###### 前提

设备支持BT，且设备BT连接成功

###### 接口

```kotlin
setBTSwitchStatus(isBTOpen, bleWriteResponse)
```

######  参数解释

| 参数名           | 类型              | 备注           |
| ---------------- | ----------------- | -------------- |
| isBTOpen         | Boolean           | 是否打开BT     |
| bleWriteResponse | IBleWriteResponse | 写入操作的监听 |

###### 返回数据

无

###### 示例代码

```kotlin
//        kotlin code
        VPOperateManager.getInstance().setBTSwitchStatus(true){
            
        }
```





## 运动功能

#### 读取运动模式数据

###### 前提

设备需支持运动模式

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportSportModel
```

###### 接口

```kotlin
readSportModelOrigin(bleWriteResponse, sportModelOriginListener)
```

###### 参数解释

| 参数名                   | 类型                      | 备注                 |
| ------------------------ | ------------------------- | -------------------- |
| bleWriteResponse         | IBleWriteResponse         | 写入操作的监听       |
| sportModelOriginListener | ISportModelOriginListener | 读取运动模式数据监听 |

###### 返回数据

**ISportModelOriginListener**

```kotlin
/**
 * 返回读取的进度
 *
 * @param progress 进度值，范围[0-1]
 */
fun onReadOriginProgress(progress: Float)

/***
 * 返回读取的细节，此包的位置需要记住，下次读取数据时，传入此包的位置，可以避免重复读取
 *
 * @param day            数据在手表中的标志位[0=今天，1=昨天，2=前天]
 * @param date           数据的日期,格式为yyyy-mm-dd
 * @param allPackage     当天数据的总包数
 * @param currentPackage 此包的位置
 */
fun onReadOriginProgressDetail(day: Int, date: String?, allPackage: Int, currentPackage: Int)

/**
 * 运动模式原始数据[头部信息]的回调
 *
 * @param sportModelHeadData 运动模式原始数据[头部信息]
 */
fun onHeadChangeListListener(sportModelHeadData: SportModelOriginHeadData?)

/**
 * 运动模式原始数据[详细信息]的回调
 *
 * @param sportModelItemDatas 运动模式原始数据[详细信息]
 */
fun onItemChangeListListener(sportModelItemDatas: List<SportModelOriginItemData?>?)

/**
 * 读取结束
 */
fun onReadOriginComplete()
```

**SportModelOriginHeadData**--运动模式的头部信息

| 变量         | 类型     | 备注                     |
| ------------ | -------- | ------------------------ |
| date         | String   | 运动日期                 |
| startTime    | TimeData | 开始时间                 |
| stopTime     | TimeData | 停止时间                 |
| sportTime    | Int      | 运动总时长               |
| stepCount    | Int      | 运动总步数               |
| sportCount   | Int      | 总运动量                 |
| kcals        | Double   | 运动消耗的千卡           |
| distance     | Double   | 运动距离                 |
| recordCount  | Int      | 总记录条数               |
| pauseCount   | Int      | 暂停次数                 |
| pauseTime    | Int      | 暂停的时长               |
| crc          | Int      | 数据校验码               |
| peisu        | Int      | 配速                     |
| oxsporttimes | Int      | 有氧运动时间             |
| averRate     | Int      | 平均心率                 |
| sportType    | Int      | 运动类型，详见ESportType |

**SportModelOriginItemData**--运动模式的详细信息

| 变量       | 类型     | 备注     |
| ---------- | -------- | -------- |
| date       | String   | 运动日期 |
| startTime  | TimeData | 开始时间 |
| minute     | Int      | 运动分钟 |
| allMinute  | Int      | 总分钟   |
| rate       | Int      | 心率     |
| stepCount  | Int      | 总步数   |
| sportCount | Int      | 总运动量 |
| distance   | Int      | 运动距离 |
| kcal       | Int      | 消耗千卡 |
| beathPause | Int      | 暂停标志 |
| crc        | Int      | 校验码   |

ESportType--运动类型枚举

```java
public enum ESportType {
    /**
     * 默认的运动
     */
    NONE(0),
    /**
     * 户外跑步
     */
    OUTDOOR_RUNNING(1),
    /**
     * 户外步行
     */
    OUTDOOR_WALK(2),
    /**
     * 室内跑步
     */
    INDOOR_RUNNING(3),
    /**
     * 室内步行
     */
    INDOOR_WALK(4),
    /**
     * 徒步
     */
    HIKE(5),
    /**
     * 踏步机
     */
    TREADMILLS(6),
    /**
     * 户外骑行
     */
    OUTDOOR_RIDING(7),
    /**
     * 室内骑行
     */
    INDOOR_RIDING(8),
    /**
     * 椭圆机
     */
    ELLIPTICAL(9),
    /**
     * 划船器
     */
    ROWING_MACHINE(10),
    /**
     * 登山
     */
    Mountaineering(11),
    /**
     * 游泳
     */
    SWIM(12),
    /**
     * 仰卧起坐
     */
    Sit_Ups(13),
    /**
     * 滑雪
     */
    SKI(14),
    /**
     * 跳绳
     */
    JUMP_ROPE(15),
    /**
     * 瑜伽
     */
    YOGA(16),
    /**
     * 乒乓球
     */
    PING_PONG(17),
    /**
     * 篮球
     */
    BASKETBALL(18),
    /**
     * 排球
     */
    VOLLEYBALL(19),
    /**
     * 足球
     */
    FOOTBALL(20),
    /**
     * 羽毛球
     */
    BADMINTON(21),
    /**
     * 网球
     */
    TENNIS(22),
    /**
     * 爬楼梯
     */
    CLIMB_STAIRS(23),
    /**
     * 健身
     */
    FITNESS(24),
    /**
     * 举重
     */
    WEIGHTLIFTING(25),
    /**
     * 潜水
     */
    DIVING(26),
    /**
     * 拳击
     */
    BOXING(27),
    /**
     * 健身球
     */
    GYM_BALL(28),
    /**
     * 深蹲训练
     */
    SQUAT_TRAINING(29),
    /**
     * 铁人三项
     */
    TRIATHLON(30),
    /**
     * 舞蹈
     */
    DANCE(31),
    /**
     * HIIT
     */
    HIIT(32),
    /**
     * 攀岩
     */
    ROCK_CLIMBING(33),
    /**
     * 竞技
     */
    SPORTS(34),
    /**
     * 球类
     */
    BALLS(35),
    /**
     * 健身游戏
     */
    FITNESS_GAME(36),
    /**
     * 自由活动
     */
    FREE_TIME(37),
    /**
     * 健美操
     */
    AEROBICS(38),
    /**
     * 体操
     */
    GYMNASTICS(39),
    /**
     * 自由体操
     */
    FLOOR_EXERCISE(40),
    /**
     * 单杠
     */
    HORIZONTALBAR(41),
    /**
     * 双杠
     */
    PARALLELBARS(42),
    /**
     * 蹦床
     */
    TRAMPOLINE(43),
    /**
     * 田径
     */
    TRACKANDFIELD(44),
    /**
     * 马拉松
     */
    MARATHON(45),
    /**
     * 俯卧撑
     */
    PUSH_UPS(46),
    /**
     * 哑铃
     */
    DUMBBELL(47),
    /**
     * 橄榄球
     */
    RUGBY_FOOTBALL(48),
    /**
     * 手球
     */
    HANDBALL(49),
    /**
     * 棒垒球
     */
    BASEBALL_SOFTBALL(50),
    /**
     * 棒球
     */
    BASEBALL(51),
    /**
     * 曲棍球
     */
    HOCKEY(52),
    /**
     * 高尔夫球
     */
    GOLF(53),
    /**
     * 保龄球
     */
    BOWLING(54),
    /**
     * 台球
     */
    BILLIARDS(55),
    /**
     * 赛艇
     */
    ROWING(56),
    /**
     * 帆船
     */
    SAILBOAT(57),
    /**
     * 滑冰
     */
    SKATE(58),
    /**
     * 冰壶
     */
    CURLING(59),
    /**
     * 冰球
     */
    PUCK(60),
    /**
     * 雪橇
     */
    SLEIGH(61),
    /**
     * 健走
     */
    StrongWalk(62),
    /**
     * 跑步机
     */
    Treadmill(63),
    /**
     * 越野跑
     */
    TrailRunning(64),
    /**
     * 竞走
     */
    RaceWalking(65),
    /**
     * 山地骑行
     */
    MountainBiking(66),
    /**
     * 小轮车
     */
    Bmx(67),
    /**
     * 定向越野
     */
    Orienteering(68),
    /**
     * 钓鱼
     */
    Fishing(69),
    /**
     * 打猎
     */
    Hunt(70),
    /**
     * 滑板
     */

    Skateboard(71),
    /**
     * 轮滑
     */
    RollerSkating(72),
    /**
     * 跑酷
     */
    Parkour(73),
    /**
     * 沙滩车
     */
    Atv(74),
    /**
     * 越野摩托
     */
    Motocross(75),
    /**
     * 爬楼机
     */
    ClimbingMachine(76),
    /**
     * 动感单车
     */
    SpinningBike(77),
    /**
     * 室内健身
     */
    IndoorFitness(78),
    /**
     * 混合有氧
     */
    MixedAerobic(79),
    /**
     * 交叉训练
     */

    CrossTraining(80),
    /**
     * 健身操
     */
    BodybuildingExercise(81),
    /**
     * 团体操
     */
    GroupGymnastics(82),
    /**
     * 搏击操
     */
    Kickboxing(83),
    /**
     * 力量训练
     */
    StrengthTraining(84),
    /**
     * 踏步训练
     */
    SteppingTraining(85),
    /**
     * 核心训练
     */
    CoreTraining(86),
    /**
     * 柔韧训练
     */
    FlexibilityTraining(87),
    /**
     * 自由训练
     */
    FreeTraining(88),
    /**
     * 普拉提
     */
    Pilates(89),
    /**
     * 战绳
     */

    BattleRope(90),
    /**
     * 拉伸
     */
    Stretch(91),
    /**
     * 广场舞
     */
    SquareDance(92),
    /**
     * 交际舞
     */
    BallroomDancing(93),
    /**
     * 肚皮舞
     */
    BellyDance(94),
    /**
     * 芭蕾舞
     */
    Ballet(95),
    /**
     * 街舞
     */
    HipHop(96),
    /**
     * 尊巴
     */
    Zumba(97),
    /**
     * 拉丁舞
     */
    LatinDance(98),
    /**
     * 爵士舞
     */
    Jazz(99),
    /**
     * 嘻哈舞
     */

    HipHopDance(100),
    /**
     * 钢管舞
     */
    PoleDancing(101),
    /**
     * 霹雳舞
     */
    BreakDance(102),
    /**
     * 民族舞
     */
    NationalDance(103),
    /**
     * 现代舞
     */
    ModernDance(104),
    /**
     * 迪斯科
     */
    Disco(105),
    /**
     * 踢踏舞
     */
    TapDance(106),
    /**
     * 摔跤
     */
    Wrestling(107),
    /**
     * 武术
     */
    MartialArts(108),
    /**
     * 太极
     */
    TaiChi(109),
    /**
     * 泰拳
     */

    MuayThai(110),
    /**
     * 柔道
     */
    Judo(111),
    /**
     * 跆拳道
     */
    Taekwondo(112),
    /**
     * 空手道
     */
    Karate(113),
    /**
     * 自由搏击
     */
    FreeSparring(114),
    /**
     * 剑术
     */
    Swordsmanship(115),
    /**
     * 柔术
     */
    Jujitsu(116),
    /**
     * 击剑
     */
    Fencing(117),
    /**
     * 沙滩足球
     */
    BeachSoccer(118),
    /**
     * 沙滩排球
     */
    BeachVolleyball(119),
    /**
     * 垒球
     */

    Softball(120),
    /**
     * 壁球
     */
    Squash(121),
    /**
     * 门球
     */
    Croquet(122),
    /**
     * 板球
     */
    Cricket(123),
    /**
     * 马球
     */
    Polo(124),
    /**
     * 墙球
     */
    Wallball(125),
    /**
     * 藤球
     */
    TakrawBall(126),
    /**
     * 躲避球
     */
    Dodgeball(127),
    /**
     * 水球
     */
    WaterPolo(128),
    /**
     * 毽球
     */
    Shuttlecock(129),
    /**
     * 室内足球
     */

    IndoorSoccer(130),
    /**
     * 沙包球
     */
    SandbagBall(131),
    /**
     * 地掷球
     */
    BocceBall(132),
    /**
     * 回力球
     */
    Jaileyball(133),
    /**
     * 地板球
     */
    Floorball(134),
    /**
     * 户外划船
     */
    OutdoorBoating(135),
    /**
     * 皮划艇
     */
    Kayak(136),
    /**
     * 龙舟
     */
    DragonBoat(137),
    /**
     * 桨板冲浪
     */
    PaddleBoard(138),
    /**
     * 室内充浪
     */
    IndoorFillingWaves(139),
    /**
     * 漂流
     */

    Drifting(140),
    /**
     * 滑水
     */
    WaterSkiing(141),
    /**
     * 双板滑雪
     */
    Snowboarding(142),
    /**
     * 单板滑雪
     */
    Snowboard(143),
    /**
     * 高山滑雪
     */
    AlpineSkiing(144),
    /**
     * 越野滑雪
     */
    CrossCountrySkiing(145),
    /**
     * 定向滑雪
     */
    OrienteeringSki(146),
    /**
     * 冬季两项
     */
    Bathlon(147),
    /**
     * 户外滑冰
     */
    OutdoorSkating(148),
    /**
     * 室内滑冰
     */
    IndoorSkating(149),
    /**
     * 雪车
     */

    SnowCar(150),
    /**
     * 雪地摩托
     */
    Snowmobile(151),
    /**
     * 雪鞋健行
     */
    Snowshoeing(152),
    /**
     * 呼啦圈
     */
    HulaHoop(153),
    /**
     * 飞盘
     */
    Frisbee(154),
    /**
     * 飞镖
     */
    Dart(155),
    /**
     * 放风筝
     */
    FlyAKite(156),
    /**
     * 拔河
     */
    TugOfWar(157),
    /**
     * 踢毽子
     */
    ShuttlecockKicking(158),
    /**
     * 电子竞技
     */
    ESports(159),
    /**
     * 漫步机
     */

    WanderingMachine(160),
    /**
     * 秋千
     */
    Swing(161),
    /**
     * 沙狐球
     */
    Shuffleboard(162),
    /**
     * 桌上足球
     */
    TableSoccer(163),
    /**
     * 体感游戏
     */
    SomatosensoryGame(164),
    /**
     * 国际象棋
     */
    InternationalChess(165),
    /**
     * 国际跳棋
     */
    Draughts(166),
    /**
     * 围棋
     */
    Go(167),
    /**
     * 桥牌
     */
    Bridge(168),
    /**
     * 桌游
     */
    BoardGame(169),
    /**
     * 射箭
     */

    Archery(170),
    /**
     * 马术运动
     */
    EquestrianSports(171),
    /**
     * 爬楼
     */
    ClimbingTheStairs(172),
    /**
     * 驾车
     */
    Drive(173),
    /**
     * 坐姿推举
     */
    SeatedPush(174),
    /**
     * 坐姿胸部推举
     */
    SeatedChestPress(175),
    /**
     * 杠铃
     */
    Barbell(176),
    /**
     * 长跑
     */
    LongDistanceRunning(177),
    /**
     * 全速跑
     */
    FullSpeedRun(178),
    /**
     * 变速跑
     */
    VariableSpeedRun(179),
    /**
     * 赛场骑行
     */

    RaceRiding(180),
    /**
     * 军棋
     */
    MilitaryChess(181),
    /**
     * 麻将
     */
    Mahjong(182),
    /**
     * 扑克
     */
    Poker(183),
    /**
     * 五子棋
     */
    Gobang(184),
    /**
     * 中国象棋
     */
    ChineseChess(185),
    /**
     * 跳高
     */
    HighJump(186),
    /**
     * 跳远
     */
    LongJump(187),
    /**
     * 打陀螺
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



###### 示例代码

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



#### 读取运动模式状态

###### 前提

设备需支持运动模式

###### 接口

```
readSportModelState(IBleWriteResponse bleWriteResponse, ISportModelStateListener sportModelStateListener)
```

###### 参数解释

| 参数名                  | 类型                     | 备注                 |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听       |
| sportModelStateListener | ISportModelStateListener | 读取运动模式状态监听 |

###### 返回数据

**ISportModelStateListener**--运动模式状态的监听

```kotlin
/**
 * 返回运动模式状态数据
 *
 * @param sportModelStateData
 */
fun onSportModelStateChange(sportModelStateData:SportModelStateData)

/**
 * 运动结束监听
 */
fun onSportStopped()
```

**SportModelStateData**--运动模式状态数据

| 变量          | 类型                   | 备注               |
| ------------- | ---------------------- | ------------------ |
| oprateStauts  | ECheckWear             | 运动模式的操作状态 |
| deviceStauts  | ESportModelStateStauts | 运动模式的设备状态 |
| sportModeType | Int                    | 运动模式的类型     |

**ECheckWear**--运动模式的操作状态

| 变量          | 备注     |
| ------------- | -------- |
| OPEN_SUCCESS  | 打开成功 |
| OPEN_FAIL     | 打开失败 |
| CLOSE_SUCCESS | 关闭成功 |
| CLOSE_FAIL    | 关闭失败 |
| READ_SUCCESS  | 读取成功 |
| READ_FAIL     | 读取失败 |
| UNKONW        | 未知状态 |

**ESportModelStateStauts**--运动模式的设备状态

| 变量                    | 备注       |
| ----------------------- | ---------- |
| DEVICE_FREE             | 设备空闲   |
| DEVICE_BUSY             | 设备繁忙   |
| DEVICE_HAD_START_BEFORE | 设备开启过 |
| UNKNOW                  | 未知       |

###### 示例代码

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



#### 开启运动模式

###### 前提

设备需支持运动模式

###### 接口

```
startSportModel(bleWriteResponse, sportModelStateListener)
```

###### 参数解释

| 参数名                  | 类型                     | 备注                 |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听       |
| sportModelStateListener | ISportModelStateListener | 读取运动模式状态监听 |

###### 返回数据

同【[读取运动模式状态](#读取运动模式状态)】返回数据一致

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance()
    .startSportModel({

    }, object : ISportModelStateListener {
        override fun onSportModelStateChange(sportModelStateData: SportModelStateData) {
        }

        override fun onSportStopped() {
        }
    })
```



#### 结束运动模式

###### 前提

设备需支持运动模式且已开启运动模式

###### 接口

```
stopSportModel(bleWriteResponse, sportModelStateListener)
```

###### 参数解释

| 参数名                  | 类型                     | 备注                 |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听       |
| sportModelStateListener | ISportModelStateListener | 读取运动模式状态监听 |

###### 返回数据

同【[读取运动模式状态](#读取运动模式状态)】返回数据一致

###### 示例代码

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



#### 开始多运动模式

###### 前提

设备需支持运动模式

开始多运动模式到结束多运动模式之间要超过1分钟,不足1分钟的运动是无运动数据保存的

###### 接口

```
startMultSportModel(bleWriteResponse, sportModelStateListener, sportType)
```

###### 参数解释

| 参数名                  | 类型                     | 备注                 |
| ----------------------- | ------------------------ | -------------------- |
| bleWriteResponse        | IBleWriteResponse        | 写入操作的监听       |
| sportModelStateListener | ISportModelStateListener | 读取运动模式状态监听 |
| sportType               | ESportType               | 运动类型             |

###### 返回数据

同【[读取运动模式状态](#读取运动模式状态)】返回数据一致

###### 示例代码

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





## 血氧功能

#### 前提

需设备支持血氧功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportSpo2h
```



####  开始测量血氧-startDetectSPO2H

需设备支持血氧功能

###### 接口

```
startDetectSPO2H(IBleWriteResponse bleWriteResponse, ISpo2hDataListener spo2HDataListener, ILightDataCallBack lightDataCallBack)
```

###### 参数解释

| 参数名            | 类型               | 备注                                                    |
| ----------------- | ------------------ | ------------------------------------------------------- |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听                                          |
| spo2HDataListener | ISpo2hDataListener | 血氧操作的回调,返回血氧的数据:是否支持,开/关状态,血氧值 |
| lightDataCallBack | ILightDataCallBack | 原始的光信号监听                                        |

###### 返回数据

**ISpo2hDataListener**--血氧操作的回调

```kotlin
/**
 * 返回血氧操作的数据
 * @param spo2HData 血氧操作的数据
 */
fun onSpO2HADataChange(spo2HData:Spo2hData)
```

**spo2HData**--血氧操作的数据

| 变量             | 类型          | 备注           |
| ---------------- | ------------- | -------------- |
| spState          | ESPO2HStatus  | 血氧功能状态   |
| deviceState      | EDeviceStatus | 设备状态       |
| value            | Int           | 血氧值         |
| isChecking       | Boolean       | 是否正在检测中 |
| checkingProgress | Int           | 血氧检测进度   |
| rateValue        | Int           | 心率值         |

**ESPO2HStatus**--血氧功能状态

| 变量        | 备注         |
| ----------- | ------------ |
| NOT_SUPPORT | 不支持此功能 |
| CLOSE       | 关闭状态     |
| OPEN        | 打开状态     |
| UNKONW      | 未知         |

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().startDetectSPO2H({

}, { spo2HData ->
        val message = "血氧-开始:\n$spo2HData"
    }) { data ->
    val message = "血氧-光电信号:${Arrays.toString(data)}".trimIndent()
}
```



#### 结束测量血氧-stopDetectSPO2H

设备需支持血氧功能

###### 接口

```kotlin
stopDetectSPO2H(bleWriteResponse, spo2HDataListener) 
```

###### 参数解释

| 参数名            | 类型               | 备注                                                    |
| ----------------- | ------------------ | ------------------------------------------------------- |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听                                          |
| spo2HDataListener | ISpo2hDataListener | 血氧操作的回调,返回血氧的数据:是否支持,开/关状态,血氧值 |

###### 返回数据

同【[开始测量血氧-startDetectSPO2H](#开始测量血氧-startDetectSPO2H)】返回数据一致

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().stopDetectSPO2H({

}) { spo2HData ->
    val message = "血氧-结束:\n$spo2HData"
}
```



#### 读取血氧自动检测开关状态

需设备支持血氧功能

###### 接口

```
readSpo2hAutoDetect(bleWriteResponse, allSetDataListener)
```

###### 参数解释

| 参数名             | 类型                | 备注                   |
| ------------------ | ------------------- | ---------------------- |
| bleWriteResponse   | IBleWriteResponse   | 写入操作的监听         |
| allSetDataListener | IAllSetDataListener | 血氧自动检测开关的回调 |

###### 返回数据

**IAllSetDataListener**--血氧自动检测开关的回调

```kotlin
/**
 * 返回多设置数据
 *
 * @param alarmData 多设置数据
 */
fun onAllSetDataChangeListener(alarmData:AllSetData);
```

**AllSetData**--多设置数据

| 变量         | 类型          | 备注                           |
| ------------ | ------------- | ------------------------------ |
| type         | EAllSetType   | 设置的类型0x00代表血氧自动检测 |
| startHour    | Int           | 开始小时                       |
| startMinute  | Int           | 开始分钟                       |
| endHour      | Int           | 结束小时                       |
| endMinute    | Int           | 结束分钟                       |
| oprate       | Int           | 操作：0设置，1读取             |
| openState    | Int           | 开关状态：0关1开               |
| oprateResult | EAllSetStatus | 设置状态                       |
| isOpen       | Int           | 是否打开检测                   |

###### 示例代码

```kotlin
// kotlin code
VPOperateManager.getInstance().readSpo2hAutoDetect({
    if (it != Code.REQUEST_SUCCESS) {
        Log.e("Test", "write cmd failed")
    }
}, {allSetData->
    
})
```



#### 设置血氧自动检测开关状态

需设备支持血氧功能

###### 接口

```kotlin
settingSpo2hAutoDetect(bleWriteResponse, allSetDataListener, allSetSetting)
```

###### 参数解释

| 参数名             | 类型                | 备注                   |
| ------------------ | ------------------- | ---------------------- |
| bleWriteResponse   | IBleWriteResponse   | 写入操作的监听         |
| allSetDataListener | IAllSetDataListener | 血氧自动检测开关的回调 |
| allSetSetting      | AllSetSetting       | 相关设置               |

**AllSetSetting**--相关设置

| 变量        | 类型        | 备注                           |
| ----------- | ----------- | ------------------------------ |
| type        | EAllSetType | 设置的类型0x00代表血氧自动检测 |
| startHour   | Int         | 开始小时                       |
| startMinute | Int         | 开始分钟                       |
| endHour     | Int         | 结束小时                       |
| endMinute   | Int         | 结束分钟                       |
| oprate      | Int         | 操作：0设置，1读取             |
| openState   | Int         | 开关状态：0关1开               |

###### 返回数据

同【[读取血氧自动检测开关状态](#读取血氧自动检测开关状态)】返回的数据一致。

###### 示例代码

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

#### 读取血氧日常数据

血氧日常数据的读写在【[读取日常数据功能](#读取日常数据功能)】中返回





## 血糖功能

血糖功能主要包括

1. 血糖监测开关和单位设置
2. 日常佩戴手表产生的血糖数据读取
3. 手动血糖测量
4. 血糖私人校准模式读取和设置
5. 血糖多校准模式读取和设置



#### 血糖监测开关和单位设置

###### 血糖开关和单位设置

```
VPOperateManager.getInstance().changeCustomSetting(writeResponse, ICustomSettingDataListener, customSetting)
```

###### 血糖单位开关状态和单位读取

```
VPOperateManager.getInstance().readCustomSetting(writeResponse,ICustomSettingDataListener);
```

| 参数名                     | 类型                       | 描述                 |
| -------------------------- | -------------------------- | -------------------- |
| writeResponse              | IBleWriteResponse          | 命令写入回调         |
| ICustomSettingDataListener | ICustomSettingDataListener | 设置成功或失败的回调 |
| customSetting              | CustomSetting              | 相关功能的开关设置   |

个性化设置回调 ICustomSettingDataListener，

当设置改变的时候OnSettingDataChange方法会被回调，将返回当前手表的所有参数设置状态，具体包含那些查看customSettingData中各个开关状态。

```
public interface ICustomSettingDataListener extends IListener {
    /**
     * 返回个性化设置的数据
     *
     * @param customSettingData 个性化设置的数据
     */
    void OnSettingDataChange(CustomSettingData customSettingData);
}
```

CustomSetting

| 属性名                | 类型              | 描述                               |
| :-------------------- | ----------------- | ---------------------------------- |
| bloodGlucoseDetection | EFunctionStatus   | 血糖功能状态：是否支持，是否打开。 |
| bloodGlucoseUnit      | EBloodGlucoseUnit | 血糖单位：默认mmol/L , 或者mgdl    |



#### 日常血糖数据读取

血糖日常数据读取在日常数据读取接口中以下四个接口均可获取日常产生的血糖数据，在血糖监测开关开启且佩戴的情况下最多可以读取三天的日常血糖数据。血糖数据在接口IOriginData3Listener.onOriginFiveMinuteListDataChange(originDataList)中获取

1. 读取多少(入参 watchday)天的日常数据

   ```
    /***
        * 假如手表存储的数据是3天
        * 读取原始数据,每5分钟一条,数据包含计步,心率,血压,运动量,读取顺序为 今天-昨天-前天,理论上一天的数据共288条
        *
        * @param bleWriteResponse   写入操作的监听
        * @param originDataListener 原始数据的回调,返回的数据包含计步,心率,血压,运动量
        * @param watchday           手表可存储的数据容量(单位:天),依设备而定,密码验证后,在onFunctionSupportDataChange回调中,可通过getWatchday()获取返回值
        */
       public void readOriginData(IBleWriteResponse bleWriteResponse, IOriginData3Listener originDataListener, int watchday)
   ```

2. 自定义差异化读取日常数据

   ```
    /**
        * 读取原始数据,此方法可以自定义要读取的哪天以及从当天第几条开始读取,避免重复读取
        * ,比如设置了[昨天,150],那么读取顺序为[昨天{150}-昨天结束-前天]
        *
        * @param bleWriteResponse   写入操作的监听
        * @param originDataListener 原始数据的回调,返回的数据包含计步,心率,血压,运动量
        * @param day                读取哪天,0表示今天,1表示昨天,2表示前天,以此类推。如果传入的值为昨天,那么读取顺序为 昨天-前天-...,
        * @param position           读取条数的位置,一天最多288(5分钟每条)条,你可以定义读取的条数位置,此参数的值必须大于等于1
        * @param watchday           手表可存储的数据容量(单位:天),依设备而定,密码验证后,在onFunctionSupportDataChange回调中,可通过getWatchday()获取返回值
        */
       public void readOriginDataFromDay(IBleWriteResponse bleWriteResponse, IOriginData3Listener originDataListener, int day, int position, int watchday)
   ```

3. 自定义要读取的哪天以及从当天第几条开始读取,并只读当天

   ```
    /***
        * 读取原始数据,此方法可以自定义要读取的哪天以及从当天第几条开始读取,并只读当天
        * ,比如设置了[昨天,150],那么读取顺序为[昨天{150}-昨天结束]
        *
        * @param bleWriteResponse   写入操作的监听
        * @param originDataListener 原始数据的回调,返回的数据包含计步,心率,血压,运动量
        * @param day                读取哪天,0表示今天,1表示昨天,2表示前天,以此类推。
        * @param position           读取条数的位置,一天最多288(5分钟每条)条,你可以定义读取的条数位置,此参数的值必须大于等于1
        * @param watchday           手表可存储的数据容量(单位:天),依设备而定,密码验证后,在onFunctionSupportDataChange回调中,可通过getWatchday()获取返回值
        */
       public void readOriginDataSingleDay(IBleWriteResponse bleWriteResponse, IOriginData3Listener originDataListener, int day, int position, int watchday)
   ```

4. 自定义要读取的哪天以及从当天第几条开始读取,是否只读当天

```
 /***
     * 读取原始数据,此方法可以自定义要读取的哪天以及从当天第几条开始读取,是否只读当天
     *
     * @param bleWriteResponse   写入操作的监听
     * @param originDataListener 原始数据的回调,返回的数据包含计步,心率,血压,运动量
     * @param readOriginSetting  读取原始数据的设置
     */
    public void readOriginDataBySetting(IBleWriteResponse bleWriteResponse, IOriginProgressListener originDataListener, ReadOriginSetting readOriginSetting)
```

##### 日常血糖数据获取回调

该接口会在该天数据读取结束后回调。（一个OriginData3数据表示一个五分钟原始数据，一天最多24小时*60分钟/5分钟 = 288块五分钟原始数据），所以一天中最多会产生288个血糖数据。

```
public interface IOriginData3Listener extends IOriginProgressListener {

    /**
     * 该接口会在该天数据读取结束后回调。（一个OriginData3数据表示一个五分钟原始数据，一天最多24小时*60分钟/5分钟 = 288块五分钟原始数据）
     * 例如读取三天的原始数据则会依次返回今天-昨天-前天的五分钟原始数据列表，
     * 具体看读取几天的原始数据，读几天则会调用几次该接口
     *
     * @param originDataList 返回一个5分钟数据的列表，心率值是一个数组，其对应字段的是getPpgs()，不是getRateValue()
     */
    void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList);
    ...
}
```

OriginData3为原数数据，日常血糖值可以在数据结构中获取

```

public class OriginData3 extends OriginData {
    ...
    
    /**
     * 血糖值
     */
    public float bloodGlucose;
    
    /**
    * 血糖风险等级
 	*/
    public EBloodGlucoseRiskLevel bloodGlucoseRiskLevel;


  ...
}

```

| 参数名                | 类型                   | 默认单位 |
| --------------------- | ---------------------- | -------- |
| bloodGlucose          | float                  | mmol/L   |
| bloodGlucoseRiskLevel | EBloodGlucoseRiskLevel | 无       |

```java
public enum EBloodGlucoseRiskLevel {
    /**
     * 低风险
     */
    LOW,
    /**
     * 中风险
     */
    MIDDLE,
    /**
     * 高风险
     */
    HIGH,
    /**
     * 无等级
     */
    NONE,
}
```

注意：只有设备支持血糖风险评估时，bloodGlucoseRiskLevel才有效，其他情况下level无用，设备是否支持血糖风险评估判断如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBloodGlucoseRiskAssessment
```



#### 血糖测量

##### 开启血糖测量

```
VPOperateManager.getInstance().startBloodGlucoseDetect(writeResponse,listener)
```
| 参数名        | 类型                        | 备注             |
| ------------- | --------------------------- | ---------------- |
| writeResponse | IBleWriteResponse           | 命令写入回调     |
| listener      | IBloodGlucoseChangeListener | 血糖测量结果回调 |
##### 停止血糖监测

```
VPOperateManager.getInstance().startBloodGlucoseDetect(writeResponse,listener)
```
| 参数名        | 类型                        | 备注             |
| :------------ | --------------------------- | ---------------- |
| writeResponse | IBleWriteResponse           | 命令写入回调     |
| listener      | IBloodGlucoseChangeListener | 血糖测量结果回调 |

##### 血糖测量结果回调  

###### 血糖测量中

```java
 /**
     * 血糖测量出值
     *
     * @param progress     测量进度
     * @param bloodGlucose 血糖值
     * @param bloodGlucose 血糖风险等级，只有设备支持血糖风险评估时，该值才有效，其他情况下无效
     */
    void onBloodGlucoseDetect(int progress, float bloodGlucose,EBloodGlucoseRiskLevel level);

```

###### 血糖测量停止

```
 /**
     * 血糖测量停止
     */
    void onBloodGlucoseStopDetect();
```

###### 血糖测量异常

```
    /**
     * 血糖测量失败
     *
     * @param opt    操作码
     * @param status 错误状态
     */
    void onDetectError(int opt, EBloodGlucoseStatus status);
```

***血糖测量状态枚举***

```
EBloodGlucoseStatus
```

| 定义          | 描述           |
| ------------- | -------------- |
| NONSUPPORT    | 不支持         |
| ENABLE        | 可用           |
| DETECTING     | 测量中         |
| LOW_POWER     | 低电量不可测量 |
| BUSY          | 设备正忙       |
| WEARING_ERROR | 佩戴错误       |



#### 血糖私人模式

##### 血糖私人模式读取

```
VPOperateManager.getInstance().readBloodGlucoseAdjustingData(writeResponse,listener)
```

| 参数名        | 类型                        | 备注                 |
| ------------- | --------------------------- | -------------------- |
| writeResponse | IBleWriteResponse           | 命令写入回调         |
| listener      | IBloodGlucoseChangeListener | 血糖私人模式读取回调 |

###### 读取成功回调

```
   /**
     * 血糖私人模式设置读取回调
     *
     * @param isOpen         是否开启
     * @param adjustingValue 私人模式血糖校准值
     */
    void onBloodGlucoseAdjustingReadSuccess(boolean isOpen, float adjustingValue);
```

###### 读取失败回调

```
    /**
     * 血糖私人模式校准读取失败回调
     */
    void onBloodGlucoseAdjustingReadFailed();
```

##### 血糖私人模式设置

```
VPOperateManager.getInstance().setBloodGlucoseAdjustingData(fValue, isOpen, writeResponse, AbsBloodGlucoseChangeListener)
```

| 参数名                        | 类型                        | 描述                 |
| ----------------------------- | --------------------------- | -------------------- |
| fValue                        | float                       | 私人模式血糖校准值   |
| isOpen                        | boolean                     | 是否开启血糖私人模式 |
| writeResponse                 | IBleWriteResponse           | 命令写入回调         |
| AbsBloodGlucoseChangeListener | IBloodGlucoseChangeListener | 血糖操作回调         |

###### 设置成功回调

```
/**
     * 血糖私人模式设置成功回调
     *
     * @param isOpen         是否开启
     * @param adjustingValue 私人模式血糖校准值
     */
    void onBloodGlucoseAdjustingSettingSuccess(boolean isOpen, float adjustingValue);
```

###### 设置失败回调

```
    /**
     * 血糖私人模式校准设置失败回调
     */
    void onBloodGlucoseAdjustingSettingFailed();
```



#### 血糖多校准模式

###### 前提

需设备支持血糖多校准模式，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBloodGlucoseMultipleAdjusting
```

##### 读取血糖多校准模式数据

需设备支持血糖多校准模式

###### 接口

```kotlin
readMultipleCalibrationBGValue(bleWriteResponse, listener)
```

###### 参数解释

| 参数名           | 类型                          | 备注           |
| ---------------- | ----------------------------- | -------------- |
| bleWriteResponse | IBleWriteResponse             | 写入操作的监听 |
| listener         | AbsBloodGlucoseChangeListener | 血糖操作回调   |

###### 读取成功回调

```java
 /**
     * 多校准模式读取成功
     *
     * @param isOpen    是否打开
     * @param breakfast 早餐
     * @param lunch     中餐
     * @param dinner    晚餐
     */
    void onBGMultipleAdjustingReadSuccess(boolean isOpen, MealInfo breakfast, MealInfo lunch, MealInfo dinner);
```

###### 读取失败回调

```java
/**
     * 多校准模式读取失败
     */
    void onBGMultipleAdjustingReadFailed();
```

**MealInfo** -- 多校准模式数据

| 变量              | 类型    | 备注                                              |
| ----------------- | ------- | ------------------------------------------------- |
| index             | Int     | 当前餐的tag。 1：早餐，2：中餐，3：晚餐。         |
| bgBeforeMeal      | Float   | 餐前血糖校准值，单位mmol/L                        |
| bgAfterMeal       | Float   | 餐后血糖校准值，单位mmol/L                        |
| bgBeforeMeal_mgDL | Int     | 餐前血糖校准值，单位mg/dL                         |
| bgAfterMeal_mgDL  | Int     | 餐后血糖校准值，单位mg/dL                         |
| beforeMealTime    | Int     | 餐前时间(分钟)：小时+分钟，例如：08:30  = 8*60+30 |
| afterMealTime     | Int     | 餐后时间(分钟)：小时+分钟                         |
| isUnitMmolL       | Boolean | 单位是否是mmol/L ，优先设置                       |

##### 设置血糖多校准模式数据

需设备支持血糖多校准模式

###### 接口

```kotlin
settingMultipleCalibrationBGValue(isOpen, breakfast, lunch, dinner, bleWriteResponse, listener)
```

###### 参数解释

| 参数名           | 类型                          | 备注               |
| ---------------- | ----------------------------- | ------------------ |
| isOpen           | Boolean                       | 是否打开多校准模式 |
| breakfast        | MealInfo                      | 早餐多校准数据     |
| lunch            | MealInfo                      | 午餐多校准数据     |
| dinner           | MealInfo                      | 晚餐多校准数据     |
| bleWriteResponse | IBleWriteResponse             | 写入操作的监听     |
| listener         | AbsBloodGlucoseChangeListener | 血糖操作的回调     |

###### 设置成功回调

```java
/**
 * 多校准模式设置成功
 *
 */
void onBGMultipleAdjustingSettingSuccess();
```

###### 设置失败回调

```java
/**
 * 多校准模式设置失败
 */
void onBGMultipleAdjustingSettingFailed();
```

**注意：**

1. 两餐之间时间间隔必须大于2小时；
2. 餐后时间必须大于餐前时间





## 女性功能

#### 前提

需设备支持女性功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportWomenSetting
```

#### 设置女性功能

需设备支持女性功能

###### 接口

```kotlin
settingWomenState(bleWriteResponse, womenDataListener, womenSetting)
```

###### 参数解释

| 参数名            | 类型               | 备注                                                         |
| ----------------- | ------------------ | ------------------------------------------------------------ |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听                                               |
| womenDataListener | IWomenDataListener | 女性状态操作的监听,返回操作的状态                            |
| womenSetting      | WomenSetting       | 女性状态的设置类,总共包含4种状态[月经期,备孕期,怀孕期,妈咪期] |

**WomenSetting**--女性状态的设置

| 参数名         | 类型         | 备注                            |
| -------------- | ------------ | ------------------------------- |
| menseLength    | Int          | 女性的月经长度,范围是[4-28天]   |
| menesInterval  | Int          | 月经持续的周期长度              |
| menesLasterday | TimeData     | 最后一次月经的时间,精确到天就行 |
| babyBirthday   | TimeData     | 孩子的出生日,精确到天就行       |
| confinementDay | TimeData     | 孕妇的预产期,精确到天就行       |
| womenStatus    | EWomenStatus | 女性的状态                      |
| babySex        | ESex         | 孩子的性别：MAN男，WOMAN女      |

>1.当女性的状态为[月经期]以及[备孕期]时，使用此构造方法WomenSetting(womenStatus, menseLength, menesInterval, menesLasterday) 
>2.当女性的状态为[妈咪期]，使用此构造方法WomenSetting(womenStatus, menseLength, menesInterval, menesLasterday, babySex, babyBirthday)
>3.当女性的状态为[怀孕期]，使用此构造方法WomenSetting(womenStatus, menesLasterday, confinementDay)

**EWomenStatus**

| 参数名   | 备注     |
| -------- | -------- |
| NONE     | 无状态   |
| MENES    | 月经状态 |
| PREREADY | 备孕状态 |
| PREING   | 怀孕状态 |
| MAMAMI   | 妈咪状态 |

###### 返回数据

**IWomenDataListener**--女性状态操作的监听

```kotlin
/**
 * 返回女性的数据
 *
 * @param womenData 女性数据
 */
fun onWomenDataChange(womenData:WomenData)
```

**WomenData**--女性状态数据

| 变量         | 类型               | 备注             |
| ------------ | ------------------ | ---------------- |
| oprateStatus | EWomenOprateStatus | 女性状态设置状态 |

**EWomenOprateStatus**--设置状态

| 变量            | 备注             |
| --------------- | ---------------- |
| SETTING_SUCCESS | 设置女性状态成功 |
| SETTING_FAIL    | 设置女性状态失败 |
| READ_SUCCESS    | 读取女性状态成功 |
| READ_FAIL       | 读取女性状态失败 |
| UNKONW          | 未知状态         |

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().settingWomenState(
    {

    },
    { womenData ->
        val message = "女性状态-设置:\n$womenData"
    }, WomenSetting(EWomenStatus.PREING, TimeData(2016, 3, 1), TimeData(2017, 1, 14))
)
```

#### 读取女性功能

设备需支持女性功能

###### 接口

```kotlin
readWomenState(IBleWriteResponse bleWriteResponse, IWomenDataListener womenDataListener)
```

###### 参数解释

| 参数名            | 类型               | 备注                              |
| ----------------- | ------------------ | --------------------------------- |
| bleWriteResponse  | IBleWriteResponse  | 写入操作的监听                    |
| womenDataListener | IWomenDataListener | 女性状态操作的监听,返回操作的状态 |

###### 返回数据

同【[设置女性功能](#设置女性功能)】返回数据一致

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().readWomenState(
    writeResponse
) { womenData ->
    val message = "女性状态-读取:\n$womenData"
}
```






## 血压功能

血压功能主要包括

1. 血压自动监测开关设置和读取
2. 日常佩戴手表产生的血压数据读取
3. 手动血压测量
4. 血压模式设置
5. 血压功能设置和读取

### 血压自动监测开关设置和读取

血压功能开关的读取和设置位于个性化设置中，请参考文档模块【[个性化设置](#个性化设置)】

### 日常血压数据读取

日常血压数据读取位于日常数据读取中，请参考文档模块 【[读取日常数据功能](## 读取日常数据功能)】

#### 五分钟原始数据中血压数据

OriginData为五分钟产生原数数据，日常血压值可以在数据结构中获取。改数据在日常读取接口中会被回调

```
public interface IOriginData3Listener extends IOriginProgressListener {

    /**
     * 该接口会在该天数据读取结束后回调。（一个OriginData3数据表示一个五分钟原始数据，一天最多24小时*60分钟/5分钟 = 288块五分钟原始数据）
     * 例如读取三天的原始数据则会依次返回今天-昨天-前天的五分钟原始数据列表，
     * 具体看读取几天的原始数据，读几天则会调用几次该接口
     *
     * @param originDataList 返回一个5分钟数据的列表，心率值是一个数组，其对应字段的是getPpgs()，不是getRateValue()
     */
    void onOriginFiveMinuteListDataChange(List<OriginData3> originDataList);
    
 	...   
 }
```

血压值位于类OriginData中

```
public class OriginData {
    ...
    
    /**
     * 高压值,范围[60-300]
     */
    private int highValue;
    /**
     * 低压值,范围[20-200]
     */
    private int lowValue;
    
  ...
}

```

| 参数名    | 类型 | 描述                           |
| --------- | ---- | ------------------------------ |
| highValue | int  | 高压值，范围在60-300内（包含） |
| lowValue  | int  | 低压值，范围在20-200内（包含） |

#### 三十分钟统计血压数据

OriginHalfHourData为30分钟原始数据,包含计步，心率，血压，是对原始五分钟数据的每30分钟进行汇总。

OriginHalfHourData

| 成员               | 类型                    | 描述                 |
| ------------------ | ----------------------- | -------------------- |
| halfHourRateDatas  | List<HalfHourRateData>  | 三十分钟心率数据数组 |
| halfHourBps        | List<HalfHourBpData>    | 三十分钟血压数据数组 |
| halfHourSportDatas | List<HalfHourSportData> | 三十分钟运动数据数组 |
| allStep            | int                     | 三十分钟总步数汇总   |

HalfHourBpData

| 成员      | 类型     | 描述                                                         |
| --------- | -------- | ------------------------------------------------------------ |
| highValue | int      | 高压值，范围在60-300内（包含）                               |
| lowValue  | int      | 低压值，范围在20-200内（包含）                               |
| date      | String   | 所属日期，日期格式为 yyyy-mm-dd                              |
| time      | TimeDate | 具体的时间，最多的可以准确到分钟,如10:00表示的是10:00-10:30这段区间的均值 |



### 血压手动测量

#### 开启血压手动测量

```
VPOperateManager.getInstance().startDetectBP(writeResponse, listener, detectModel);
```

| 参数名        | 类型                  | 备注                                                         |
| ------------- | --------------------- | ------------------------------------------------------------ |
| writeResponse | IBleWriteResponse     | 写入操作的监听                                               |
| listener      | IBPDetectDataListener | 血糖测量结果回调                                             |
| detectModel   | EBPDetectModel        | 血压测量模式：<br />私人模式：DETECT_MODEL_PRIVATE<br />通用模式：DETECT_MODEL_PUBLIC |

#### 停止血压手动测量

```
VPOperateManager.getInstance().stopDetectBP(writeResponse, detectModel)
```

| 参数名        | 类型              | 备注                                                         |
| :------------ | ----------------- | ------------------------------------------------------------ |
| writeResponse | IBleWriteResponse | 写入操作的监听                                               |
| detectModel   | EBPDetectModel    | 血压测量模式：<br />私人模式：DETECT_MODEL_PRIVATE<br />通用模式：DETECT_MODEL_PUBLIC |

#### 血压测量相关回调和数据

血压手动测量回调接口

```
/**
 * 血压数据返回的监听
 */
public interface IBPDetectDataListener extends IListener {
    /**
     * 返回血压数据
     *
     * @param bpData 血压数据
     */
    void onDataChange(BpData bpData);
}

```

血压数据  BpData

| 成员           | 类型            | 描述                                                         |
| -------------- | --------------- | ------------------------------------------------------------ |
| status         | EBPDetectStatus | 血压检测状态<br />STATE_BP_BUSY：设备正忙，表示无法进行测量血压,收到此返回时，请调用 结束测量血压<br />STATE_BP_NORMAL：表示可以进行测量血压 |
| progress       | int             | 血压测量进度,范围[0-100]                                     |
| highPressure   | int             | 高压值,范围[60-300],如果不在此范围，请提示用用户测量无效     |
| lowPressure    | int             | 低压值,范围[20-200]，,如果不在此范围，请提示用用户测量无效   |
| isHaveProgress | boolean         | true表示手表有返回进度，false表示没有进度，无进度的手表会在开始测量后的55秒返回数据 |

### 血压模式设置

#### 血压测量模式设置

```
VPOperateManager.getInstance().settingDetectBP(writeResponse, listener, bpSetting)
```

| 参数名        | 类型                   | 描述                     |
| ------------- | ---------------------- | ------------------------ |
| writeResponse | IBleWriteResponse      | 写入操作的监听           |
| listener      | IBPSettingDataListener | 血压模式设置回调         |
| bpSetting     | BpSetting              | 血压私人模式设置参数相关 |

血压设置类 BpSetting

| 成员               | 类型    | 描述                             |
| ------------------ | ------- | -------------------------------- |
| isOpenPrivateModel | boolean | 自动测量是否打开私人模式是否开启 |
| high               | int     | 用户私人的高压值                 |
| low                | int     | 用户私人的低压值                 |
| isAngioAdjuste     | boolean | 是否开启动态血压校准             |

**当isOpenPrivateModel设置为true,isAngioAdjuste设置为false时表示此时为设置开启*血压私人模式***

**当isOpenPrivateModel设置为false,isAngioAdjuste设置为true时表示此时为设置开启*血压动态调整模式***

#### 血压测量模式读取

```
VPOperateManager.getInstance().readDetectBP(writeResponse, listener)
```

| 参数名        | 类型                   | 描述             |
| ------------- | ---------------------- | ---------------- |
| writeResponse | IBleWriteResponse      | 写入操作的监听   |
| listener      | IBPSettingDataListener | 血压模式读取回调 |

#### 血压动态调试模式取消

```
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
```

血压模式设置读取监听，当设置或读取成功时IBPSettingDataListener.onDataChange方法将会被回调

```
/**
 * 设备保存私人血压的数据返回的监听
 */
public interface IBPSettingDataListener extends IListener {
    /**
     * 私人保存的血压数据返回
     *
     * @param bpSettingData 私人保存的血压数据
     */
    void onDataChange(BpSettingData bpSettingData);
}
```

BpSettingData

| 成员                  | 类型           | 描述                                                         |
| --------------------- | -------------- | ------------------------------------------------------------ |
| status                | EBPStatus      | 操作动态血压校准的状态：                                     |
| model                 | EBPDetectModel | 血压测量模式：<br />DETECT_MODEL_PRIVATE：私人模式<br />DETECT_MODEL_PUBLIC：通用模式 |
| highPressure          | int            | 设备保存的私人高压值，如果为0，说明在进行私人模式进行测量时,需要先设置私人数据 |
| lowPressure           | int            | 设备保存的私人低压值,范围[20-200]                            |
| angioAdjusterProgress | int            | 动态血压调整的进度[0-100]                                    |
| isAngioAdjuster       | boolean        | 动态血压调整的状态，true表示当前是动态血压调整的状态         |

血压校准状态 ：

EBPStatus

| SETTING_NORMAL_SUCCESS        | 关闭私人模式[也称设置通用模式]成功 |
| ----------------------------- | ---------------------------------- |
| SETTING_NORMAL_FAIL           | 关闭私人模式[也称设置通用模式]失败 |
| SETTING_PRIVATE_SUCCESS       | 设置私人模式成功                   |
| SETTING_PRIVATE_FAIL          | 设置私人模式失败                   |
| READ_SUCCESS                  | 读取血压模式成功                   |
| READ_FAIL                     | 读取血压模式失败                   |
| CANCLE_ANGIO_ADJUSTER_SUCCESS | 取消动态血压调整成功               |
| CANCLE_ANGIO_ADJUSTER_FAIL    | 设置私人模式失败                   |
| ANGIO_ADJUSTER_ING            | 血压动态校准中                     |
| ANGIO_ADJUSTER_FAIL           | 血压动态校准失败                   |
| ANGIO_ADJUSTER_SUCCESS        | 血压动态校准成功                   |
| ANGIO_ADJUSTER_DEVICE_BUSY    | 血压动态校准过程中设备忙           |
| UNKONW                        | 未知状态                           |



### 血压功能设置和读取

#### 读取血压功能

```
VPOperateManager.getInstance().readBpFunctionState(writeResponse, IBPFunctionListener)
```

#### 设置血压功能

```
VPOperateManager.getInstance().settingBpFunctionState(writeResponse, IBPFunctionListener, isOpen)
```

功能接口以及参数说明

| 参数名              | 类型                | 描述                             |
| ------------------- | ------------------- | -------------------------------- |
| writeResponse       | IBleWriteResponse   | 写入操作的监听                   |
| IBPFunctionListener | IBPFunctionListener | 血压功能读取设置回调监听         |
| isOpen              | boolean             | 是否开启 true：打开， false:关闭 |

IBPFunctionListener

```
/**
 * 血压功能的状态返回
 */
public interface IBPFunctionListener extends IListener {
    /**
     * 返回血压功能的状态
     *
     * @param bpFunctionData 血压功能的状态
     */
    void onDataChange(BpFunctionData bpFunctionData);
}
```

BpFunctionData：血压功能状态

| 成本名    | 类型    | 描述             |
| --------- | ------- | ---------------- |
| isSupport | boolean | 是否支持血压检测 |
| isOpen    | boolean | 血压功能是否打开 |





## 闹钟功能

闹钟分为三大类闹钟：

1. 普通闹钟（仅支持3个）
2. 场景闹钟（支持20个）
3. 文字闹钟（支持10个）

连接成功的设备当且只支持一种闹钟。当用户进行设备密码校验操作，设备会更新上报设备的设备功能支持类型，其中就包括闹钟类型。（当设备进行密码校验时传入的监听【IDeviceFuctionDataListener】会上报闹钟类型 [请参考 密码校验](\#验证密码操作)）
闹钟包含对闹钟的增删改查功能。

### 普通闹钟

AlarmSetting：闹钟实体类

```
public class AlarmSetting {
    private int alarmTime;
    private boolean isOpen;

    /**
     * 时，分，是否开关
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

| 成员名    | 类型    | 描述                                              |
| --------- | ------- | ------------------------------------------------- |
| alarmTime | int     | 闹钟时间 = （小时*60 + 分钟）                     |
| isOpen    | boolean | 是否打开，true为闹钟开启状态，false为闹钟关闭状态 |

#### 读取闹钟列表

使用举例：

```
VPOperateManager.getInstance().readAlarm(writeResponse, new IAlarmDataListener() {
                @Override
                public void onAlarmDataChangeListener(AlarmData alarmData) {
                    String message = "读取闹钟:\n" + alarmData.toString();
                    Logger.t(TAG).i(message);
                    sendMsg(message, 1);
                }
            });
```

| 参数名        | 类型               | 描述                                                         |
| ------------- | ------------------ | ------------------------------------------------------------ |
| writeResponse | IBleWriteResponse  | 命令写入回调                                                 |
| listener      | IAlarmDataListener | 闹钟数据的回调，当读、设置以及闹钟状态改变的时候该方法会被回调 |

AlarmData：闹钟列表封装类

| 成员名           | 类型               | 描述                                                         |
| ---------------- | ------------------ | ------------------------------------------------------------ |
| status           | EAalarmStatus      | 操作闹钟后的状态：<br />SETTING_SUCCESS：操作成功<br />SETTING_FAIL：操作失败<br />READ_SUCCESS：读取成功<br />READ_FAIL：读取失败<br />UNKONW：未知异常 |
| alarmSettingList | List<AlarmSetting> | 闹钟列表                                                     |

#### 设置闹钟

使用举例：

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



### 场景闹钟(新闹钟)

场景闹钟对于普通闹钟的区别在于手表可以设置更多的闹钟（最多20个场景闹钟）且闹钟的内容更加丰富，手表支持对应的场景图标。

Alarm2Setting:场景闹钟

| 成员名           | 类型    | 描述                                                         |
| ---------------- | ------- | ------------------------------------------------------------ |
| MAFlag           | String  | 闹钟的唯一标志位                                             |
| BluetoothAddress | String  | 闹钟所属的蓝牙地址                                           |
| alarmId          | int     | 闹钟序号，序号范围是[1-20]                                   |
| alarmHour        | int     | 保存都是24小时制,时                                          |
| alarmMinute      | int     | 保存都是24小时制，分                                         |
| repeatStatus     |         | 闹钟的重复状态(周一，周二，周三...周末)，<br/>  <p><br/>  要求格式为7位由（0,1组成的字符串），从右到左依次为周一，周二，..周六，周日<br/>  <p><br/>  1100000-表示周日,周六<br/>  <p><br/>  0000011-周二,周一<br/>  <p><br/>  如果是要设置非重复状态的闹钟，则设置为0000000<br/> |
| unRepeatDate     | String  | 闹钟非重复状态下，设定日期，<br/> <p><br/> 要求格式为 xxxx-xx-xx,<br/> <p><br/>如果是要设置重复状态的闹钟，则设置为0000-00-00 |
| scene            | int     | 闹钟的场景[0-20] 一共21种场景，默认为普通闹钟icon<br /><br />* 0:闹钟图标 * 1:睡眠 * 2:起坐 * 3:喝水 * 4:吃药 * 5:约会 * 6:读书 * 7:电影 * 8:音乐 * 9:购物 * 10:理发 * 11:生日 * 12:求婚 * 13:上班 * 14:育儿 * 15:亲子游玩 * 16:存钱 * 17:就医 * 18:遛狗 * 19:钓鱼 * 20:出行 |
| isOpen           | boolean | 是否打开，true为闹钟开启状态，false为闹钟关闭状态            |

EMultiAlarmOprate: 场景闹钟操作状态（枚举类型），当用户使用对闹钟进行增删改查的时候在相关的监听中该状态会被返回

| 类型                             | 描述                          |
| -------------------------------- | ----------------------------- |
| SETTING_SUCCESS                  | 设置成功【修改，添加】        |
| SETTING_FAIL                     | 设置失败【修改，添加】        |
| CLEAR_SUCCESS                    | 删除成功                      |
| CLEAR_FAIL                       | 删除失败                      |
| READ_SUCCESS                     | 读取成功                      |
| READ_FAIL                        | 读取失败                      |
| READ_SUCCESS_NULL                | 读取成功并没有闹钟            |
| READ_SUCCESS_SAVE                | 读取成功并保存到本地          |
| READ_SUCCESS_SAME_CRC            | 读取CRC一致 ,表明闹钟没有改变 |
| ALARM_FULL                       | 闹钟数达到上限                |
| ALARM_REPORT                     | 闹钟上报                      |
| ALARM_REPORT_DATE_ERROR          | 闹钟上报但数据错误            |
| DEVICE_ALARM_MODIFY              | 设备上修改闹钟                |
| DEVICE_ADD_ONE_TEXT_ALARM        | 设备添加一条闹钟              |
| DEVICE_DELETE_ONE_TEXT_ALARM,    | 设备端删除一条闹钟            |
| DEVICE_TEXT_ALARM_SWITCH_CHANGED | 设备文字闹钟开关状态改变      |
| UNKONW                           | 未知操作                      |

AlarmData2：场景闹钟列表封装类

| 成员名            | 类型                | 描述               |
| ----------------- | ------------------- | ------------------ |
| status            | EMultiAlarmOprate   | 场景闹钟的操作状态 |
| alarm2SettingList | List<Alarm2Setting> | 场景闹钟列表       |

#### 读取场景闹钟

使用举例：

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

#### 设置场景闹钟

使用举例：

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

#### 删除场景闹钟

使用举例：

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



#### 修改场景闹钟

使用举例：

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

### 文字闹钟

文字闹钟最多支持10组，TextAlarm2Setting(文字)继承自Alarm2Setting（场景），多了一个闹钟提示文字描述。

```
public class TextAlarm2Setting extends Alarm2Setting {

    /**
     * 闹钟信息
     */
    private String content;
    ...
```

TextAlarmData：文字闹钟列表封装类

| 成员名                | 类型                    | 描述               |
| --------------------- | ----------------------- | ------------------ |
| oprate                | EMultiAlarmOprate       | 文字闹钟的操作状态 |
| textAlarm2SettingList | List<TextAlarm2Setting> | 文字闹钟列表       |

ITextAlarmDataListener：文字闹钟操作的监听回调，当在手表上或手机上对文字闹钟进行增删改查时该监听会被回调

```
/**
 * Author: YWX
 * Date: 2021/9/24 14:40
 * Description: 文字闹钟操作回调
 */
interface ITextAlarmDataListener : IListener {

    /**
     * 闹钟数据的回调
     *
     * @param textAlarmData 闹钟数据
     */
    fun onAlarmDataChangeListListener(textAlarmData: TextAlarmData?)
}
```

#### 读取文字闹钟

使用举例：

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
        showMsg(isOk ? "读取文字闹钟成功" : "读取文字闹钟失败");
    }
});
```

| 参数名                 | 类型                   | 描述             |
| ---------------------- | ---------------------- | ---------------- |
| writeResponse          | IBleWriteResponse      | 写入操作的监听   |
| ITextAlarmDataListener | ITextAlarmDataListener | 文字闹钟改变回调 |

#### 增加文字闹钟

使用举例：

```
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
        Logger.t(TAG).e("添加闹钟 --》" + textAlarmData.toString());
        EMultiAlarmOprate OPT = textAlarmData.getOprate();
        showMsg("添加闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
        if (OPT == EMultiAlarmOprate.ALARM_FULL) {
            showMsg("闹钟已满（最多添加十个）");
        } else if (OPT == EMultiAlarmOprate.SETTING_SUCCESS) {
            showMsg("闹钟添加成功");
            mSettings.clear();
            mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
            mAdapter.notifyDataSetChanged();
        } else if (OPT == EMultiAlarmOprate.SETTING_FAIL) {
            showMsg("闹钟添加失败");
        }
    }
}, setting);
```

| 参数名                 | 类型                   | 描述               |
| ---------------------- | ---------------------- | ------------------ |
| writeResponse          | IBleWriteResponse      | 写入操作的监听     |
| ITextAlarmDataListener | ITextAlarmDataListener | 文字闹钟改变回调   |
| setting                | TextAlarm2Setting      | 需要添加的闹钟实体 |

#### 删除文字闹钟

使用举例：

```
VPOperateManager.getInstance().deleteTextAlarm(writeResponse, new ITextAlarmDataListener() {
    @Override
    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
        EMultiAlarmOprate OPT =  textAlarmData.getOprate();
        if(OPT == EMultiAlarmOprate.CLEAR_SUCCESS) {
            showMsg("闹钟删除成功");
            mSettings.clear();
            mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
            mAdapter.notifyDataSetChanged();
        } else {
            showMsg("删除失败");
        }
    }
}, textAlarm);
```

| 参数名                 | 类型                   | 描述               |
| ---------------------- | ---------------------- | ------------------ |
| writeResponse          | IBleWriteResponse      | 写入操作的监听     |
| ITextAlarmDataListener | ITextAlarmDataListener | 文字闹钟改变回调   |
| textAlarm              | TextAlarm2Setting      | 需要删除的闹钟实体 |

#### 修改文字闹钟

使用举例：

```
VPOperateManager.getInstance().modifyTextAlarm(writeResponse, new ITextAlarmDataListener() {
    @Override
    public void onAlarmDataChangeListListener(TextAlarmData textAlarmData) {
        showMsg("修改闹钟 --》" + (textAlarmData.getOprate() == EMultiAlarmOprate.SETTING_SUCCESS ? "成功" : "失败"));
        mSettings.clear();
        mSettings.addAll(textAlarmData.getTextAlarm2SettingList());
        mAdapter.notifyDataSetChanged();
    }
}, setting);
```

| 参数名                 | 类型                   | 描述               |
| ---------------------- | ---------------------- | ------------------ |
| writeResponse          | IBleWriteResponse      | 写入操作的监听     |
| ITextAlarmDataListener | ITextAlarmDataListener | 文字闹钟改变回调   |
| textAlarm              | TextAlarm2Setting      | 需要修改的闹钟实体 |





## 天气功能

当手表支持天气时, （当设备进行密码校验时传入的监听【deviceFunctionDataListener】会上报天气功能有无 [请参考 密码校验](#验证密码操作)）
天气包含以下功能

1. 天气数据设置
2. 天气开关以及单位设置

### 天气数据设置

天气设置调用方法

```
VPOperateManager.getInstance().settingWeatherData(writeResponse, weatherData, IWeatherStatusDataListener)
```

| 参数名        | 类型                       | 描述                 |
| ------------- | -------------------------- | -------------------- |
| writeResponse | IBleWriteResponse          | 命令写入回调         |
| weatherData   | WeatherData                | 天气数据             |
| listener      | IWeatherStatusDataListener | 天气数据设置结果回调 |

WeatherData：天气数据

| 成员名                | 类型                    | 描述                                                         |
| --------------------- | ----------------------- | ------------------------------------------------------------ |
| crc                   | int                     | crc可以根据更新时间或全体数据拼接成数据的CRC，确保唯一就可以 <br />（当crc相同时则不需要更新天气数据，可以避免过快频率的更新天气数据） |
| cityName              | String                  | 城市名，具体城市的UTF-8编码                                  |
| source                | int                     | 天气数据的来源，比如是雅虎还是和风,(此处可以不填)            |
| timeBean              | TimeData                | 最后更新时间                                                 |
| weatherEvery3HourList | List<WeatherEvery3Hour> | 每3小时天气预报列表                                          |
| weatherEverdayList    | List<WeatherEveryDay>   | 每天天气预报列表                                             |

WeatherEvery3Hour：三小时天气预报

| 成员名       | 类型     | 描述                                                         |
| ------------ | -------- | ------------------------------------------------------------ |
| timeBean     | TimeData | 当前的时间，年月日时分，精确到时分秒                         |
| temperatureF | int      | 华氏度                                                       |
| temperatureC | int      | 摄氏度                                                       |
| yellowLevel  | int      | 紫外线强度指数                                               |
| weatherState | int      | 天气状态，此值是一个指定范围内的int值，值域判断如下：<br/>  0-4 晴<br/>  5-12    晴转多云<br/>  13-16   阴天<br/>  17-20   阵雨<br/>  21-24   雷阵雨<br/>  25-32   冰雹<br/>  33-40   小雨<br/>  41-48   中雨<br/>  49-56   大雨<br/>  57-72   暴雨<br/>  73-84   小雪<br/>  85-100  大雪<br/>  101-155 多云 |
| windLevel    | String   | 风向等级,如果风力是一个范围值，请用‘-’连接，例如"3-5";如果是单个值，直接"3" |
| canSeeWay    | double   | 能见度单位m,3.16                                             |

WeatherEveryDay：每天天气预报

| 成员名               | 类型     | 描述                                                         |
| -------------------- | -------- | ------------------------------------------------------------ |
| timeBean             | TimeData | 年月日时分                                                   |
| temperatureMaxF      | int      | 最大华氏度                                                   |
| temperatureMinF      | int      | 最小华氏度                                                   |
| temperatureMaxC      | int      | 最大摄氏度                                                   |
| temperatureMinC      | int      | 最小摄氏度                                                   |
| yellowLevel          | int      | 紫外线强度指数                                               |
| weatherStateWhiteDay | int      | 白天天气状态，此值是一个指定范围内的int值，值域判断如下：<br/>  0-4 晴<br/>  5-12    晴转多云<br/>  13-16   阴天<br/>  17-20   阵雨<br/>  21-24   雷阵雨<br/>  25-32   冰雹<br/>  33-40   小雨<br/>  41-48   中雨<br/>  49-56   大雨<br/>  57-72   暴雨<br/>  73-84   小雪<br/>  85-100  大雪<br/>  101-155 多云 |
| weatherStateNightDay | int      | 夜间天气状态，此值是一个指定范围内的int值，值域判断如下：<br/>  0-4 晴<br/>  5-12    晴转多云<br/>  13-16   阴天<br/>  17-20   阵雨<br/>  21-24   雷阵雨<br/>  25-32   冰雹<br/>  33-40   小雨<br/>  41-48   中雨<br/>  49-56   大雨<br/>  57-72   暴雨<br/>  73-84   小雪<br/>  85-100  大雪<br/>  101-155 多云 |
| windLevel            | String   | 风向等级,如果风力是一个范围值，请用‘-’连接，例如"3-5";如果是单个值，直接"3" |
| canSeeWay            | double   | 能见度单位m,3.16                                             |

IWeatherStatusDataListener：天气数据设置结果回调，当天气数据设置成功，或天气开关，天气单位发生改变时该方法会被回调

```
/**
 * 天气数据的监听，返回操作的状态
 */
public interface IWeatherStatusDataListener extends IListener {
    /**
     * 天气数据的回调
     *
     * @param weatherStatusData
     */
    void onWeatherDataChange(WeatherStatusData weatherStatusData);
}
    /**
     * 设置天气状态成功
     */
    SETTING_STATUS_SUCCESS,
    /**
     * 设置天气状态失败
     */
    SETTING_STATUS_FAIL,
    /**
     * 设置天气数据成功
     */
    SETTING_CONTENT_SUCCESS,
    /**
     * 设置天气数据失败
     */
    SETTING_CONTENT_FAIL,
    /**
     * 读取成功
     */
    READ_SUCCESS,
    /**
     * 读取失败
     */
    READ_FAIL,
    /**
     * 未知
     */
    UNKONW;

```

WeatherStatusData：天气状态数据

| 成员名      | 类型                 | 描述                                                         |
| ----------- | -------------------- | ------------------------------------------------------------ |
| oprate      | EWeatherOprateStatus | 天气操作状态：<br />SETTING_STATUS_SUCCESS：设置天气状态成功<br />SETTING_STATUS_FAIL：设置天气状态失败<br />SETTING_CONTENT_SUCCESS：设置天气数据成功<br />SETTING_CONTENT_FAIL：设置天气数据失败<br />READ_SUCCESS：读取成功<br />READ_FAIL：读取失败<br />UNKONW：未知状态 |
| crc         | int                  | 当前天气的crc                                                |
| isOpen      | boolean              | 是否打开天气功能                                             |
| weatherType | EWeatherType         | 天气的类型（华氏度，摄氏度）：<br />C：摄氏度<br />F：华氏度 |

天气设置代码示例：

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



### 天气开关以及单位设置

```
VPOperateManager.getInstance().settingWeatherStatusInfo(writeResponse, weatherStatusSetting, listener)
```

| 参数名               | 类型                       | 描述                 |
| -------------------- | -------------------------- | -------------------- |
| writeResponse        | IBleWriteResponse          | 命令写入回调         |
| weatherStatusSetting | WeatherStatusSetting       | 天气开关和单位设置   |
| listener             | IWeatherStatusDataListener | 天气数据设置结果回调 |

WeatherStatusSetting：天气单位和开关设置

| 成员名   | 类型         | 描述                                                         |
| -------- | ------------ | ------------------------------------------------------------ |
| crc      | int          | 当前天气的crc, 进行天气设置时，如果天气数据没有改变crc请传入上次保存的天气crc值 |
| isOpen   | boolean      | 是否打开天气功能，true:打开，false:关闭                      |
| eWeather | EWeatherType | 天气的类型（华氏度，摄氏度）：<br />C：摄氏度<br />F：华氏度 |

天气单位和开关代码示例：

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





## 查找手机功能

#### 设置查找手机监听

###### 接口

```kotlin
settingFindPhoneListener(findPhonelistener)
```

###### 参数解释

| 参数名            | 类型               | 描述           |
| ----------------- | ------------------ | -------------- |
| findPhonelistener | IFindPhonelistener | 查找手机的监听 |

###### 返回数据

**IFindPhonelistener**--查找手机的监听

```kotlin
/**
 * 接收到此回调时，手机应该做相应的反馈提醒用户，如振动，响铃等
 */
fun findPhone()
```

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().settingFindPhoneListener {
    val message = "(监听到手环要查找手机)-where is the phone,make some noise!"

}
```





## 久坐功能

#### 前提

需设备支持久坐功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportLongseat
```

#### 读取久坐设置

需设置支持久坐

###### 接口

```
readLongSeat(bleWriteResponse, longSeatDataListener)
```

###### 参数解释

| 参数名               | 类型                  | 描述           |
| -------------------- | --------------------- | -------------- |
| bleWriteResponse     | IBleWriteResponse     | 写入操作的监听 |
| longSeatDataListener | ILongSeatDataListener | 久坐数据监听   |

###### 返回数据

**ILongSeatDataListener** --久坐数据监听

```kotlin
/**
 * 返回久坐的数据
 *
 * @param longSeat 久坐数据
 */
fun onLongSeatDataChange(longSeat:LongSeatData)
```

**LongSeatData**--久坐数据

| 变量        | 类型            | 描述                           |
| ----------- | --------------- | ------------------------------ |
| status      | ELongSeatStatus | 久坐操作的状态                 |
| startHour   | Int             | 开始时间的小时                 |
| startMinute | Int             | 开始时间的分钟                 |
| endHour     | Int             | 结束时间的小时                 |
| endMinute   | Int             | 结束时间的分钟                 |
| threshold   | Int             | 阈值：多久没动过，手表就会提醒 |
| isOpen      | Boolean         | 开关状态                       |

**ELongSeatStatus**--久坐操作的状态

| 变量          | 描述         |
| ------------- | ------------ |
| OPEN_SUCCESS  | 打开成功     |
| OPEN_FAIL     | 打开失败     |
| CLOSE_SUCCESS | 关闭成功     |
| CLOSE_FAIL    | 关闭失败     |
| READ_SUCCESS  | 读取成功     |
| READ_FAIL     | 读取失败     |
| UNSUPPORT     | 不支持此功能 |
| UNKONW        | 未知状态     |

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().readLongSeat(
    writeResponse
) { longSeat ->
    val message = "设置久坐-读取:\n$longSeat"

}
```



#### 设置久坐

需设备支持久坐功能

###### 接口

```
settingLongSeat(IBleWriteResponse bleWriteResponse, LongSeatSetting longSeatSetting, ILongSeatDataListener longSeatDataListener)
```

###### 参数解释

| 参数名               | 类型                  | 描述           |
| -------------------- | --------------------- | -------------- |
| bleWriteResponse     | IBleWriteResponse     | 写入操作的监听 |
| longSeatSetting      | LongSeatSetting       | 久坐设置       |
| longSeatDataListener | ILongSeatDataListener | 久坐数据监听   |

**LongSeatSetting**--久坐设置

| 参数名      | 类型    | 描述                           |
| ----------- | ------- | ------------------------------ |
| startHour   | Int     | 开始时间的小时                 |
| startMinute | Int     | 开始时间的分钟                 |
| endHour     | Int     | 结束时间的小时                 |
| endMinute   | Int     | 结束时间的分钟                 |
| threshold   | Int     | 阈值：多久没动过，手表就会提醒 |
| isOpen      | Boolean | 开关状态                       |

###### 返回数据

同【[读取久坐设置](#读取久坐设置)】返回的数据一致

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().settingLongSeat(
    writeResponse, LongSeatSetting(10, 40, 12, 40, 40, false)
) { longSeat ->
    val message = "设置久坐:\n$longSeat"

}
```





## 拍照功能

#### 前提

设备需支持拍照功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportCamera
```

#### 进入拍照模式

设备需支持拍照功能

###### 接口

```
startCamera(bleWriteResponse, cameraDataListener)
```

###### 参数解释

| 参数名             | 类型                | 描述                                                         |
| ------------------ | ------------------- | ------------------------------------------------------------ |
| bleWriteResponse   | IBleWriteResponse   | 写入操作的监听                                               |
| cameraDataListener | ICameraDataListener | 拍照操作的回调,返回进入拍照模式成功/失败,可以/不可以拍照,退出拍照模式成功/失败 |

###### 返回数据

**ICameraDataListener**--拍照操作的回调

```kotlin
/**
 * 返回拍照的状态
 *
 * @param oprateStatus
 */
fun OnCameraDataChange(oprateStatus:ECameraStatus)
```

**ECameraStatus**--拍照状态

| 变量              | 描述             |
| ----------------- | ---------------- |
| OPEN_SUCCESS      | 进入拍照模式成功 |
| OPEN_FALI         | 进入拍照模式失败 |
| TAKEPHOTO_CAN     | 进行拍照         |
| TAKEPHOTO_CAN_NOT | 不可以进行拍照   |
| CLOSE_SUCCESS     | 退出拍照成功     |
| CLOSE_FAIL        | 退出拍照失败     |
| UNKONW            | 未知             |

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().startCamera(
    writeResponse
) { cameraStatus ->

}
```

#### 退出拍照模式

需设备支持拍照且已进入拍照模式

###### 接口

```
stopCamera(IBleWriteResponse bleWriteResponse, ICameraDataListener cameraDataListener)
```

###### 参数解释

同【[进入拍照模式](#进入拍照模式)】参数一致

###### 返回数据

同【[进入拍照模式](#进入拍照模式)】返回数据一致

###### 示例代码

```kotlin
//        kotlin code
VPOperateManager.getInstance().stopCamera(
    writeResponse
) { cameraStatus ->

}
```





## OTA升级功能

#### 获取当前固件版本号

###### 前提

设备已连接

**注意：**OTA过程较长，如果电池电量不足，传输过程可能会因低电关机，建议电池电量在30%以上，才允许升级；

###### 接口

在【[验证密码](#验证密码操作)】操作后，返回的pwdDataListener数据中包含了当前固件版本号

###### 示例代码

```
pwdData.deviceVersion
```



#### 获取新固件版本号以及升级描述

###### 前提

设备已连接且已知晓设备版本号

###### 接口

```
getOadVersion(oadSetting, listener)
```

###### 参数解释

| 参数名     | 类型                    | 描述         |
| ---------- | ----------------------- | ------------ |
| oadSetting | OadSetting              | 校验版本设置 |
| listener   | OnGetOadVersionListener | 校验版本监听 |

**OadSetting**--校验版本设置

| 参数名              | 类型    | 描述                                                         |
| ------------------- | ------- | ------------------------------------------------------------ |
| deviceAddressString | String  | 设备地址(必要)                                               |
| deviceVersion       | String  | 设备版本(必要)                                               |
| deviceTestVersion   | String  | 设备测试版本(必要)                                           |
| deviceNumber        | String  | 设备号(必要)                                                 |
| isOadModel          | Boolean | 固件升级模式(必要)                                           |
| isDebug             | Boolean | 调试模式(可不传),true表示使用的是我司服务器的调试端口，false表示使用的是我司服务器的发布端口， |
| hostUrl             | String  | 主机地址，传输格式为：http://www.baidu.com;可不传,默认为我司服务器;慎用！！！使用前先对接我司开发人员 |
| isAutoDownload      | Boolean | 是否检测版本后自动下载                                       |

###### 返回数据

**OnGetOadVersionListener **-- 校验版本回调

```java
/**
 * 返回此设备在服务器的最新版本信息
 *
 * @param deviceNumber  设备号
 * @param deviceVersion 最新版本
 * @param des           升级描述
 * @param netIsNew      网络版本大于本地版本，需要更新
 */
void onNetOadInfo(int deviceNumber, String deviceVersion, String des, boolean netIsNew);

void onNetOadDetailInfo(OadFileBean oadFileBean,boolean netIsNew);
```

**OadFileBean** -- 设备升级文件信息

| 变量          | 类型   | 描述            |
| ------------- | ------ | --------------- |
| deviceNumber  | String | 新固件设备号    |
| deviceVersion | String | 新固件版本      |
| downUrl       | String | 新固件下载的url |
| size          | String | 新固件的大小    |
| md5           | String | 新固件md5       |
| des           | String | 新版本升级描述  |



#### 下载新固件

根据获取到的新固件下载地址，进行正常的网络下载即可



#### 杰理平台设备OTA升级

注意，杰理OTA升级过程一般比较长，大概在10-20分钟范围内，升级过程中需要保持手表手机电量充足，且升级中APP处于前台。（部分android系统会将处于后台的app的蓝牙操作视为耗电运行，可能会将蓝牙传输挂起，从而导致ota升级失败）

杰理OTA流程分为三阶段，
第一阶段：OTA文件传输，此过程升级时长视升级文件大小而定。onProgress 方法正常情况下progress值从0.00到99.9

第二阶段：内部文件copy, onProgress 会快速从0.00到99.9

第三阶段：此阶段为内部升级，设备回主动断开连接，并回调onNeedReconnect方法，此时设备会自动更改设备名为DFULang，且设备地址在原有的设备地址+1

SDK会自动搜索重连DFULang设备，连接成功后，SDK会触发内部升级， onProgress 会快速从0.00到100, 并回调onOTASuccess方法

###### 前提

需设备为杰理设备且已完成【[打开杰理文件系统](#打开杰理文件系统)】，并且已经下载好新固件

###### 接口

```
VPOperateManager.getInstance().startJLDeviceOTAUpgrade(firmwareFilePath, listener)
```

###### 参数解释

| 参数名           | 类型                              | 描述                      |
| ---------------- | --------------------------------- | ------------------------- |
| firmwareFilePath | String                            | 本地存放的OTA升级文件路径 |
| listener         | JLOTAHolder.OnJLDeviceOTAListener | 杰理设备OTA监听           |

###### 返回数据

OnJLDeviceOTAListener：杰理OTA升级监听

```java
public interface OnJLDeviceOTAListener {

    /**
     * 开始OTA
     */
    void onOTAStart();

    /**
     * 升级进度，保留小数点后两位
     *
     * @param progress 升级进度[0.00 - 100]
     */
    void onProgress(float progress);

    /**
     * 需要重连时回调
     *
     * @param address          设备地址
     * @param dfuLangAddress   dfuLang状态下的设备地址： address+1
     * @param isReconnectBySdk 是否由SDK内部自动重连，完成ota最后一阶段
     */
    void onNeedReconnect(String address, String dfuLangAddress, boolean isReconnectBySdk);

    /**
     * OTA成功
     */
    void onOTASuccess();

    /**
     * OTA升级失败
     *
     * @param error 失败原因
     */
    void onOTAFailed(BaseError error);
}
```

###### 示例代码

```java
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





## 联系人功能

前提

设备需支持联系人功能，判断条件如下：

```
boolean isHaveContactFunction = VpSpGetUtil.getVpSpVariInstance(this).isSupportContactFunction();
```

是否支持紧急联系人，判断条件如下：

```
boolean isSupportSOSContact = VpSpGetUtil.getVpSpVariInstance(this).isSupportSOSContactFunction();
```

联系人封装实体：Contact

```kotlin
/**
 * 联系人
 * @param contactID 联系人ID
 * @param name 联系人昵称
 * @param phoneNumber 联系人号码
 * @param isSettingSOS 是否设置为紧急联系人
 * @param isSupportSOS 是否支持SOS功能
 */
data class Contact(
        var contactID: Int,
        var name: String,
        var phoneNumber: String,
        var isSettingSOS: Boolean = false,
        var isSupportSOS: Boolean = false
)
```

联系人操作枚举类：EContactOpt

```
enum class EContactOpt(var des: String) {
    /**
     * 读取联系人
     */
    READ("read contact list"),

    /**
     * 设置联系人
     */
    SETTING("setting contact"),

    /**
     * 设置紧急联系人
     */
    SETTING_SOS("setting contact"),

    /**
     * 移动联系人
     */
    MOVE("move contact position"),

    /**
     * 删除联系人
     */
    DELETE("delete one contact");
}
```

联系人操作回调监听：

```
interface IContactOptListener : IListener {

    /**
     * 联系人操作成功
     * @param opt 当前操作类型
     * @param crc 当前操作成功后的crc，每次操作更新后需要保存crc以及联系人列表
     */
    fun onContactOptSuccess(opt: EContactOpt, crc: Int)

    /**
     * 联系人操作失败
     */
    fun onContactOptFailed(opt: EContactOpt)

    /**
     * 读取联系成功
     * @param contactList 联系人列表，如果列表为空则表示设备端无联系人
     */
    fun onContactReadSuccess(contactList: List<Contact>)

    /**
     * 表示读取联系人时下发的crc和设备端的crc一致
     * crc一致表示设备端的联系人没有更改（注意联系人每次更新后都需要保存，通过crc判断是否需要更新联系人）
     */
    fun onContactReadASSameCRC()

    /**
     * 读取联系人失败
     */
    fun onContactReadFailed()

}
```

### 读取联系人

代码示例：

第一次读取的时候crc传入-1，表示会返回完整的设备端联系人。 如果用户此前已经读取过联系人可以将联系人的CRC值传入，如果CRC值和设备端保持一致则会回调onContactReadASSameCRC表示不需要重复读取，否则将返回一个完整的联系人列表。

```
VPOperateManager.getInstance().readContact(-1, IContactOptListener, response);
```

Or

```
/**
 * 读取紧急联系人呼叫次数
 *
 * @param contacts         上一次保存的联系人列表，用来计算crc
 * @param listener         紧急联系人呼叫次数监听
 * @param bleWriteResponse 数据写入回调
 */
public void readContact(List<Contact> contacts, @NotNull IContactOptListener listener, IBleWriteResponse bleWriteResponse) {
.
.
.
```



Crc值的获取：

```
int crc = CrcUtil.INSTANCE.getCrcByContactList(contacts);
```

### 添加联系人

传入一个具有联系人 昵称和手机号码的Contact 实体即可，**注：**如果设备支持sos功能，isSupportSOS需设置为true

```
VPOperateManager.getInstance().addContact(contact, IContactOptListener, IBleWriteResponse);
```

传入一个联系列表即可，需包含昵称、手机号码、是否支持sos功能信息

```
VPOperateManager.getInstance().addContactList(List<Contact>, IContactOptListener, IBleWriteResponse)
```



### 删除联系人

删除联系人需要传入一个从设备端获取的完整信息（包含contactID）的联系人

代码示例：

```
VPOperateManager.getInstance().deleteContact(contact, IContactOptListener, IBleWriteResponse);
```



### 移动联系人

移动联系人是将设备端两个联系人的排序互换。

代码示例：

```
VPOperateManager.getInstance().moveContact(fromContact, toContact, IContactOptListener, IBleWriteResponse);
```

```java
VPOperateManager.getInstance().moveContactWithPosition(fromPosition, toPosition, IContactOptListener, IBleWriteResponse);
```



### 紧急联系人

当设备支持紧急联系人时可以进行紧急联系人操作，判断条件如下：

```
boolean isSupportSOSContact = VpSpGetUtil.getVpSpVariInstance(this).isSupportSOSContactFunction();
```

#### 紧急联系人开启关闭设置

代码示例：

当isOpen 为true时为设置紧急联系人

当isOpen 为false时为取消紧急联系人

```
VPOperateManager.getInstance().setContactSOSState(isOpen, contact, IContactOptListener, IBleWriteResponse);
```



#### 紧急联系人呼叫次数

紧急联系人呼叫次数操作回调监听：

```
/**
 * 联系人SOS呼叫次数设置
 */
interface ISOSCallTimesListener : IListener {
    /**
     * 设置SOS呼叫次数成功
     * @param times SOS呼叫次数
     */
    fun onSOSCallTimesSettingSuccess(times: Int)

    /**
     * 设置SOS呼叫次数失败
     */
    fun onSOSCallTimesSettingFailed()

    /**
     * 读取SOS呼叫次数成功
     * @param times SOS呼叫次数
     * @param minTimes 支持最小的SOS呼叫次数
     * @param maxTimes 支出最大的SOS呼叫次数
     */
    fun onSOSCallTimesReadSuccess(times: Int, minTimes: Int, maxTimes: Int)

    /**
     * 读取SOS呼叫次数失败
     */
    fun onSOSCallTimesReadFailed()
}
```

##### 读取呼叫次数

读取紧急联系人呼叫次数代码示例：

```
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
        tvSOSInfo.setText("呼叫次数设置范围：[" + minTimes + "-" + maxTimes + "]");
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

##### 设置呼叫次数

设置紧急联系人呼叫次数代码示例：

```
VPOperateManager.getInstance().setSOSCallTimes(callTimes, new ISOSCallTimesListener() {
    @Override
    public void onSOSCallTimesSettingSuccess(int times) {
        Toast.makeText(AddContactActivity.this, "设置成功：" + times + "次", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSOSCallTimesSettingFailed() {
        Toast.makeText(AddContactActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSOSCallTimesReadSuccess(int times, int minTimes, int maxTimes) {
        Toast.makeText(AddContactActivity.this, "读取成功：" + times + "-范围：[" + minTimes + "-" + maxTimes + "]", Toast.LENGTH_SHORT).show();
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





## 身体成分功能

前提：需设备支持身体成分功能，判断条件如下：

```kotlin
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBodyComponent
```

注：以下所有接口都需在满足设备支持身体成分功能下才能调用

#### 读取身体成分ID(设备手动测量数据)

###### 前提

需设备支持身体成分功能

###### 接口

```
readBodyComponentId(bleWriteResponse, readIdListener)
```

###### 参数解释

| 参数名           | 类型                         | 描述                             |
| ---------------- | ---------------------------- | -------------------------------- |
| bleWriteResponse | IBleWriteResponse            | 写入操作的监听                   |
| readIdListener   | IBodyComponentReadIdListener | 身体成分读取测量保存的数据ID回调 |

###### 返回数据

**IBodyComponentReadIdListener** -- 身体成分读取测量保存的数据ID回调

```kotlin
/**
 * 读取ID结束回调
 * @param ids 读取到的id列表
 */
fun readIdFinish(ids: ArrayList<Int>)
```

###### 示例代码

```kotlin
VPOperateManager.getInstance().readBodyComponentId(bleWriteResponse, object:IBodyComponentReadIdListener{
        override fun readIdFinish(ids: ArrayList<Int>) {
            val msg = "读取身体成分数据ID列表：$ids"
        }
})
```



#### 读取身体成分数据(设备手动测量)

###### 前提

需设备支持身体成分功能

###### 接口

```kotlin
readBodyComponentData(bleWriteResponse, readDataListener)
```

###### 参数解释

| 参数名           | 类型                           | 描述                       |
| ---------------- | ------------------------------ | -------------------------- |
| bleWriteResponse | IBleWriteResponse              | 写入操作的监听             |
| readDataListener | IBodyComponentReadDataListener | 读取身体成分数据的数据回调 |

###### 返回数据

**IBodyComponentReadDataListener** --读取身体成分数据的数据回调

```kotlin
/**
 * 数据读取完毕
 *
 * @param bodyComponentList 身体成分数据
 */
fun readBodyComponentDataFinish(bodyComponentList: List<BodyComponent>?)
```

**BodyComponent** -- 身体成分数据

| 变量名             | 类型     | 描述                                                         |
| ------------------ | -------- | ------------------------------------------------------------ |
| BMI                | Float    | BMI有效范围【4.0，1114.0】，保留一位小数，上报的值是10倍，下同 |
| bodyFatRate        | Float    | 体脂率，有效范围【2.0，48.0】%                               |
| fatRate            | Float    | 脂肪量，有效范围【10.0，248.0】kg                            |
| FFM                | Float    | 去脂体重，有效范围【1.0，132.0】kg                           |
| muscleRate         | Float    | 肌肉率，有效范围【39.0，90.0】%                              |
| muscleMass         | Float    | 肌肉量，有效范围【9.0，248.0】kg                             |
| subcutaneousFat    | Float    | 皮下脂肪，有效范围【1.0，47.0】%                             |
| bodyWater          | Float    | 体内水分，有效范围【28.0，79.0】%                            |
| waterContent       | Float    | 含水量，有效范围【7.0，217.0】kg                             |
| skeletalMuscleRate | Float    | 骨骼肌率，有效范围【13.0，69.0】%                            |
| boneMass           | Float    | 骨量，有效范围【2.3，4.8】kg                                 |
| proteinProportion  | Float    | 蛋白质占比，有效范围【4.0，26.0】%                           |
| proteinMass        | Float    | 蛋白质量，有效范围【1.0，71.0】kg                            |
| basalMetabolicRate | Float    | 基础代谢率，有效范围【25，14995】kcal                        |
| timeBean           | TimeData | 此次测量的时间                                               |
| duration           | Int      | 测量持续时间                                                 |
| idType             | Int      | 测量设备类型：1:设备测试，2: App测试                         |

###### 示例代码

```kotlin
VPOperateManager.getInstance().readBodyComponentData(bleWriteResponse, object : IBodyComponentReadDataListener {
    override fun readBodyComponentDataFinish(bodyComponentList: List<BodyComponent>?) {
        HBLogger.bleConnectLog("【读取身体成分数据成功】bodyComponentList+${bodyComponentList.toString()}")

    }
})
```



#### 设置身体成分(设备手动测量)数据上报监听

###### 接口

```kotlin
setBodyComponentReportListener(reportListener)
```

###### 参数解释

| 参数名         | 类型                            | 描述                              |
| -------------- | ------------------------------- | --------------------------------- |
| reportListener | INewBodyComponentReportListener | 新的身体成分测量数据 主动上报监听 |

###### 返回数据

**INewBodyComponentReportListener** --新的身体成分测量数据 主动上报监听

```kotlin
/**
 * 新的身体成分测量数据 上报数据监听接口
 * 只要设备端有新的测量数据诞生时触发，触发时通过[VPOperateManager.readBodyComponentData]接口读取新的身体成分测量数据
 */
fun onNewBodyComponentReport()
```

###### 示例代码

```kotlin
VPOperateManager.getInstance().setBodyComponentReportListener(object : INewBodyComponentReportListener {
            override fun onNewBodyComponentReport() {
                "新的身体成分数据上报-----".logd()
                
            }
        })
```



#### 开始测量身体成分

###### 前提

设置支持身体成分

###### 接口

```kotlin
startDetectBodyComponent(bleWriteResponse, bodyDetectListener)
```

###### 参数解释

| 参数名           | 类型                         | 描述             |
| ---------------- | ---------------------------- | ---------------- |
| bleWriteResponse | BleWriteResponse             | 写入操作的监听   |
| detectListener   | IBodyComponentDetectListener | 身体成分测量回调 |

###### 返回数据

**IBodyComponentDetectListener** -- 身体成分测量回调

```kotlin
/**
 * 测量中回调
 * @param progress 测量进度
 * @param leadState 导联状态：0->导联通过；1->导联脱落，连续4个导联脱落后，app主动发停止测量给设备
 */
fun onDetecting(progress: Int, leadState: Int)

/**
 * 测量成功回调
 * @param bodyComponent 本次的身体成分数据
 */
fun onDetectSuccess(bodyComponent: BodyComponent)

/**
 * 测量失败
 * @param detectState 失败原因
 */
fun onDetectFailed(detectState: DetectState)

/**
 * 停止测量
 */
fun onDetectStop()	
```

**DetectState** -- 测量状态枚举

```kotlin
PROGRESS(0, "测量中"),
SUCCESS(1, "测量成功-结果包"),
FAILED(2, "测量失败-无结果"),
BUSY(3, "设备正忙"),
LOW_POWER(4, "低电")
```

###### 示例代码

```kotlin
VPOperateManager.getInstance().startDetectBodyComponent(bleWriteResponse,object :IBodyComponentDetectListener{
    override fun onDetecting(progress: Int, leadState: Int) {
        "【身体成分测量】onDetecting：$progress,$leadState".logd()
    }

    override fun onDetectSuccess(bodyComponent: BodyComponent) {
        "【身体成分测量】onDetectSuccess：${bodyComponent}".logd()
    }

    override fun onDetectFailed(detectState: DetectState) {
        "【身体成分测量】onDetectFailed：${detectState}".loge()
    }

    override fun onDetectStop() {
        "【身体成分测量】onDetectStop".loge()
    }

})
```



#### 结束身体成分测量

###### 前提

设备支持身体成分功能且已开始身体成分测量

###### 接口

```
stopDetectBodyComponent(bleWriteResponse)
```

###### 参数解释

| 参数名           | 类型             | 描述           |
| ---------------- | ---------------- | -------------- |
| bleWriteResponse | BleWriteResponse | 写入操作的监听 |

###### 返回数据

无

###### 示例代码

```kotlin
 VPOperateManager.getInstance().stopDetectBodyComponent(bleWriteResponse)
```





## 血液成分功能

需设备支持血液成分功能，判断条件如下：

```
VpSpGetUtil.getVpSpVariInstance(applicationContext).isSupportBloodComponent
```

#### 血液成分日常数据读取

【[读取日常数据](##读取日常数据)】中五分钟原始数据包含了血液成分相关数据返回



#### 读取血液成分校准值

###### 前提

需设备支持血液成分功能

###### 接口

```
readBloodComponentCalibration(bleWriteResponse, optListener)
```

###### 参数解释

| 参数名           | 类型                       | 描述             |
| ---------------- | -------------------------- | ---------------- |
| bleWriteResponse | BleWriteResponse           | 写入操作的监听   |
| optListener      | IBloodComponentOptListener | 血液成分操作监听 |

###### 返回数据

**IBloodComponentOptListener** -- 血液成分操作回调

```kotlin
/**
 * 血液成分校准读取成功
 * @param isOpen 是否打开
 * @param bloodComposition 血液成分校准值
 */
fun onBloodCompositionReadSuccess(isOpen: Boolean, bloodComposition: BloodComponent)

/**
 * 血液成分校准读取失败
 */
fun onBloodCompositionReadFailed()

/**
 * 血液成分校准值设置成功
 * @param isOpen 是否打开
 * @param bloodComposition 血液成分校准值
 */
fun onBloodCompositionSettingSuccess(isOpen: Boolean, bloodComposition: BloodComponent)

/**
 * 血液成分校准值设置失败
 */
fun onBloodCompositionSettingFailed()
```

**BloodComponent** -- 血液成分校准值，同【[读取日常数据](##读取日常数据)】返回的类一致

###### 示例代码

```kotlin
VPOperateManager.getInstance().readBloodComponentCalibration(bleWriteResponse, optListener)
```



#### 设置血液成分校准值

###### 前提

设备支持血液成分功能

###### 接口

```
settingBloodComponentCalibration(bleWriteResponse, isOpen, bloodComponent, optListener)
```

###### 参数解释

| 参数名           | 类型                       | 描述                 |
| ---------------- | -------------------------- | -------------------- |
| bleWriteResponse | BleWriteResponse           | 写入操作的监听       |
| isOpen           | Boolean                    | 是否开启血液成分校准 |
| bloodComponent   | BloodComponent             | 血液成分校准值       |
| optListener      | IBloodComponentOptListener | 血液成分操作监听     |

###### 返回数据

**IBloodComponentOptListener** -- 血液成分操作监听，同【[读取血液成分校准值](####读取血液成分校准值)】返回一致

###### 示例代码

```kotlin
 VPOperateManager.getInstance().settingBloodComponentCalibration(bleWriteResponse, isOpen, bloodComponent, optListener)
```



#### 开始血液成分测量

###### 前提

设备需支持血液成分功能

###### 接口

```kotlin
startDetectBloodComponent(bleWriteResponse, isUseCalibrationMode, detectListener)
```

###### 参数解释

| 参数名               | 类型                          | 描述             |
| -------------------- | ----------------------------- | ---------------- |
| bleWriteResponse     | BleWriteResponse              | 写入操作的监听   |
| isUseCalibrationMode | Boolean                       | 是否使用校准模式 |
| detectListener       | IBloodComponentDetectListener | 血液成分测量回调 |

###### 数据返回

**IBloodComponentDetectListener** -- 血液成分测量回调

```kotlin
/**
 * 检测失败
 */
fun onDetectFailed(errorState: EBloodComponentDetectState)

/**
 * 检测中
 * @param progress 检测进度
 * @param bloodComponent 血液成分
 */
fun onDetecting(progress: Int, bloodComponent: BloodComponent)

/**
 *
 */
fun onDetectStop()

/**
 * 检测完成
 * @param bloodComponent 血液成分
 */
fun onDetectComplete(bloodComponent: BloodComponent)
```

###### 示例代码

```kotlin
VPOperateManager.getInstance().startDetectBloodComponent(bleWriteResponse, isUseCalibrationMode, detectListener)
```



#### 停止血液功能成分测量

###### 前

设备需支持血液且已开启血液成分测量

###### 接口

```
stopDetectBloodComponent(bleWriteResponse)
```

###### 返回数据

无

###### 示例代码

```kotlin
VPOperateManager.getInstance().stopDetectBloodComponent {

}
```

