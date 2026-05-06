# Android_Ble_SDK

Android_Ble_SDK is a toolkit for quickly interacting with Bluetooth BLE. It is only available to our cooperative customers for download and use to improve their development efficiency.

## Necessary dependencies jar


   * API>19&&BLE 4.0  
   * [vpbluetooth_x.x.x.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [gson-x.x.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [vpprotocol_2.x.xx.xx.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
   * [JL_Watch_V1.13.1_11214-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
   * [jl_rcsp_V0.7.2_527-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
   * [jl_bt_ota_V1.10.0_10931-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
   * [BmpConvert_V1.6.0_10604-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
   * [abpartool-release.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
   * no.nordicsemi.android:mcumgr-core:2.7.4
   * no.nordicsemi.android:mcumgr-ble:2.7.4
   * no.nordicsemi.android.support.v18:scanner:1.4.2


## Optional (If it is a Goodix chip, the upgrade will use the following package)

   * [libble-0.x.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libcomx-0.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libdfu-1.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libfastdfu-0.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)

## How to use

### 1. build.gradle

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

### 2. Androidmanifest.xml

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

### 3. SDK API Documentation
* [中文版](https://github.com/HBandSDK/Android_Ble_SDK/wiki/VeepooSDK-Android-API-%E6%96%87%E6%A1%A3)
* [English](https://github.com/HBandSDK/Android_Ble_SDK/wiki/VeepooSDK-Android-API-Document)
* [deepwiki](https://deepwiki.com/HBandSDK/Android_Ble_SDK)
	
### 4. Example

#### 1. Ble connection


    
    Operation Instructions: All operations are only performed through VPOperateManager;
    
    3.1 Get VPOperateManager instance: VPOperateManager.getMangerInstance()
    3.2 Scan Bluetooth devices: startScanDevice();
    3.3 Connect Bluetooth devices: connectDevice();
    3.3.1 Call password: confirmDevicePwd()
    3.3.2 Call personal information settings: syncPersonInfo()
    3.4 Set the connection status listener of the connected device: registerConnectStatusListener(); //This method is best set after the connection is successful
    3.5 Set the system Bluetooth switch status listener: registerBluetoothStateListener(); //This method can be set in any state
    3.6 Other data interaction operations
    
    Note 1:
    To avoid memory leaks, please use context with caution, it is recommended to use getApplicationContext();
    For example: Get VPOperateManager instance: VPOperateManager.getMangerInstance(getApplicationContext());
    
    Note 2:
    For example, calling the scanning device: VPOperateManager.getMangerInstance().startScanDevice();
    
    The above are uniformly abbreviated as: startScanDevice();
    
    Note 3:
    Call connectDevice(), and then perform the password verification operation after the bleNotifyResponse callback is successful
    
    The first step to interact is to verify the password: confirmDevicePwd()
    
    The second step to interact is to synchronize personal information: syncPersonInfo()
    
    Note 4:
    The device does not support asynchronous operations. When multiple time-consuming operations are performed at the same time, data anomalies may occur; therefore, when interacting with the device, try to avoid multiple operations at the same time.

#### 2. 蓝牙数据交互说明 | Bluetooth data interaction instructions

```java
The design of the SDK for sending Bluetooth data is to only call the method, pass in the setting parameters and listen to the interface. When the data is returned, the interface will trigger the callback. Take confirmDevicePwd as an example

String  pwdStr=“0000”;
boolean is24Hourmodel = false;   
VPOperateManager.getMangerInstance(mContext).confirmDevicePwd(writeResponse, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                String message = "PwdData:\n" + pwdData.toString();
                Logger.t(TAG).i(message);
            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                Logger.t(TAG).i(message);
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
                Logger.t(TAG).i(message);
             }
        }, pwdStr, is24Hourmodel);
```

#### 3. 固件升级说明 | Firmware Upgrade Instructions

**Note**: compatible with noric chips and Goodix chips
	The purpose of firmware upgrade is mainly to upgrade the software functions of the device. This operation requires very strict requirements. Upgrading errors will bring a very bad experience to users.
	Please perform this operation with caution. The upgrade operation requires that it be performed under network conditions, including the following 3 steps. Please be sure to check carefully.
	For specific upgrade cases, please refer to com.timaimee.vpbluetoothsdkdemo.oad.Activity.OadActivity under our company's running demo project VpBluetoothSDKDemo
	

	1. Configuration of AndroidMainfest.xml
	This configuration has been explained in the previous Activity&Service configuration. It mainly configures two files, a service and an activity.
	Developers can also directly copy these two files under VpBluetoothSDKDemo.
	
	2. Judgment before upgrade
	a. Before upgrading, perform device version verification and upgrade file verification
	b. After both are verified, send the firmware upgrade command and the device will enter the firmware upgrade mode
	c. Then when the app finds the device in firmware upgrade mode
	d. Finally, you can call the official upgrade program.
	
	Note: The checkVersionAndFile() method has encapsulated all the above steps. Developers can just call this method.
	Callback method 1: onCheckSucces() indicates that both the device version and the upgrade file have been successfully verified;
	Callback method 2: findOadDevice() indicates that the device in firmware upgrade mode has been found, and the official upgrade program can be called.
	
	Other situations: The device version and upgrade file have passed, and there is no problem with the file, but the device in firmware upgrade mode has not been found.
	Processing method: Call the findOadModelDevice() method, and then call the official upgrade program in its callback method findOadDevice().
	Of course, this situation is very rare, and it is not recommended to develop a separate call to findOadModelDevice();
	
	3. Call the official upgrade program
	startOad() under OadActivity is generally called after the findOadDevice method of the OnFindOadDeviceListener interface is called back.

### Instructions for changing the dial (UI upgrade)

**demo**: VPOperateManager.getMangerInstance(mContext).settingScreenStyle();
The device is now changing the dial. The dial of the device is divided into three parts (default built-in dial (multiple) + editable dial (1) + server dial (1))

	1. The default built-in dial can be set directly through the settingScreenStyle method
	2. For the editable dial, you can select three elements and the placement of the three elements + select the background image. The selected background image needs to be sent to the device
	3. For the server dial, get the server dial list, including preview images, files and other necessary information, and then send it to the device
	
	Default built-in dial setting demo: VPOperateManager.getMangerInstance(mContext).settingScreenStyle();

[Editable watch face demo](https://github.com/HBandSDK/Android_Ble_SDK/blob/caff11a10f493ac8c6e8471708984fd8e6a15a86/android_sdk_source/Demo/VpBluetoothSDK/app/src/main/java/com/timaimee/vpdemo/activity/UiUpdateCustomActivity.java)

[Server dial demo](https://github.com/HBandSDK/Android_Ble_SDK/blob/caff11a10f493ac8c6e8471708984fd8e6a15a86/android_sdk_source/Demo/VpBluetoothSDK/app/src/main/java/com/timaimee/vpdemo/activity/UiUpdateServerActivity.java)


​	

## Acknowledgements

* [Android-DFU-Library](https://github.com/NordicSemiconductor/Android-DFU-Library)
* [BluetoothKit](https://github.com/dingjikerbo/BluetoothKit)  
* [Gson](https://github.com/google/gson)  



## License Agreement
[Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

    Copyright (C) 2010 Michael Pardo
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.







