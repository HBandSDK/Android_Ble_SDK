# Veepoo  Android SDK 接入文档

Android_Ble_SDK是一个用于快速与蓝牙BLE交互的工具包，仅提供给我们合作的客户下载使用，方便提升客户的开发效率。

## 必要条件

- [vpbluetooth_x.x.x.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)   
- [vpprotocol_2.x.xx.xx.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- [gson-x.x.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
- [JL_Watch_V1.13.1_11214-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- [jl_rcsp_V0.7.2_527-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- [jl_bt_ota_V1.10.0_10931-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- [BmpConvert_V1.6.0_10604-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- [abpartool-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- no.nordicsemi.android:mcumgr-core:2.7.4
- no.nordicsemi.android:mcumgr-ble:2.7.4
- no.nordicsemi.android.support.v18:scanner:1.4.2

### 可选（如果是汇顶的芯片，升级会用到如下包）

- [libble-0.x.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
- [libcomx-0.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
- [libdfu-1.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
- [libfastdfu-0.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)

## 如何使用 

### 1. build.gradle

```groovy
// goodix Upgrade Library (Optional) | 汇顶升级库相关（可选）
implementation files('libs/libcomx-0.5.jar')

// Veepoo Protocol Library (Required) | Veepoo协议库（必选）
implementation files('libs/vpprotocol-2.3.63.15.aar')

// Veepoo Bluetooth Connection Library (Required) | Veepoo蓝牙连接库（必选）
implementation files('libs/vpbluetooth-1.20.aar')

// Gson Library (Required) | Gson数据解析库（必选）
implementation files('libs/gson-2.2.4.jar') //或者 implementation 'com.google.code.gson:gson:x.x.x'  

// JieLi Bluetooth Protocol (Required) | 杰理蓝牙协议相关（必选）
implementation files('libs/JL_Watch_V1.13.1_11214-release.aar')
implementation files('libs/jl_rcsp_V0.7.2_527-release.aar')
implementation files('libs/jl_bt_ota_V1.10.0_10931-release.aar')
implementation files('libs/BmpConvert_V1.6.0_10604-release.aar')

// bluetrum Bluechip Protocol & Data Tool (Required) | 中科蓝汛蓝牙协议解析+数据处理+工具（必选）
implementation files('libs/abpartool-release.aar')

// Nordic OTA Upgrade (Required) | Nordic固件升级相关（必选）
implementation 'no.nordicsemi.android:mcumgr-core:2.7.4'
implementation 'no.nordicsemi.android:mcumgr-ble:2.7.4'

// Nordic BLE Scanner Compat (Required) | Nordic蓝牙扫描兼容库（必选）
implementation 'no.nordicsemi.android.support.v18:scanner:1.4.2'

// AndroidX Local Broadcast (Required) | 本地广播适配库（必选）
implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.1.0'
```



### 2. Androidmanifest.xml

```xml
<!-- 网络权限 | Network Permission -->
<uses-permission android:name="android.permission.INTERNET" />
<!-- 模糊定位（Android 9 及以下需要，用于 WiFi + BLE 扫描）| Coarse Location (for BLE scanning below Android 9) -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<!-- 精确定位（Android 11 及以下 BLE 扫描强制要求）| Fine Location (required for BLE scan before Android 12) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<!-- 基础蓝牙权限（Android 11 及以下使用）| Basic Bluetooth (used up to Android 11) -->	
<uses-permission
	android:name="android.permission.BLUETOOTH"
	android:maxSdkVersion="30" />
<!-- 蓝牙管理权限（Android 11 及以下使用）| Bluetooth Admin (used up to Android 11) -->
<uses-permission
	android:name="android.permission.BLUETOOTH_ADMIN"
	android:maxSdkVersion="30" />
<!-- Android 12+ 蓝牙扫描权限（不用于定位）| Bluetooth Scan (Android 12+, not for location) -->
<uses-permission
	android:name="android.permission.BLUETOOTH_SCAN"
	android:usesPermissionFlags="neverForLocation"
 	tools:targetApi="s" />
<!-- Android 12+ 蓝牙广播权限 | Bluetooth Advertise (Android 12+) -->
<uses-permission
	android:name="android.permission.BLUETOOTH_ADVERTISE"
	tools:targetApi="s" />
<!-- Android 12+ 蓝牙连接权限（连接设备必备）| Bluetooth Connect (Android 12+, required for connection) -->
<uses-permission
	android:name="android.permission.BLUETOOTH_CONNECT"
	tools:targetApi="s" />
<!-- 声明设备必须支持 BLE 蓝牙 | Declare BLE hardware is required -->
<uses-feature
 	android:name="android.hardware.bluetooth_le"
	android:required="true" />
<!-- 声明使用低功耗蓝牙功能 | Declare BLE feature is required -->
<uses-feature
	android:name="android.bluetooth.le"
	android:required="true" />
<!--Activity&Service-->
<service android:name="com.inuker.bluetooth.library.BluetoothService" />        
<!--固件升级功能相关-->
<service android:name=".oad.service.DfuService" /> //旧版本nordic升级-可选
<activity android:name=".oad.activity.NotificationActivity" /> //旧版本nordic升级-可选
```



### 3. SDK API 文档

Veepoo android sdk api文档请参考以下链接 

- [中文版](https://github.com/HBandSDK/Android_Ble_SDK/wiki/VeepooSDK-Android-API-文档)
- [English](https://github.com/HBandSDK/Android_Ble_SDK/wiki/VeepooSDK-Android-API-Document)
- [deepwiki](https://deepwiki.com/HBandSDK/Android_Ble_SDK)



### 4.API 调用以及功能演示

请参考Demo code 以及 Apk

- [Demo code](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/Demo)   

- [Apk](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/app_debug.apk)

  