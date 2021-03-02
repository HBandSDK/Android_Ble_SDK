# Android_Ble_SDK
Android_Ble_SDK is a toolkit that can quickly interact with bluetooth BLE，only provided to our coperative customers to download for promoting development efficiency.


README: [English](https://github.com/HBandSDK/Android_Ble_SDK/blob/master/README_EN.md)| [Chinese](https://github.com/HBandSDK/Android_Ble_SDK/blob/master/README.md)

## Requirements

    
   * API>19&&BLE 4.0  
   * [vpbluetooth_x.x.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [gson-x.x.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)

## Optional (If it is Goodix's chip, the upgrade will use the following packages)

   * [libble-0.2.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libcomx-0.2.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libdfu-1.2.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libfastdfu-0.2.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)

## How to use

### 1. Configuration （build.gradle）

    compile files('libs/vpbluetooth_x.x.x.jar')  
    compile files('libs/gson-x.x.x.jar') or compile 'com.google.code.gson:gson:x.x.x'  

### 2. Configuration Androidmanifest.xml

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <!--Activity&Service-->
    <service android:name="com.inuker.bluetooth.library.BluetoothService" />        
    <!--Firmware upgrade retevent function -->
    <service android:name=".oad.service.DfuService" /> 
    <activity android:name=".oad.activity.NotificationActivity" />

### 3. Bluetooth Communication Connection  

    Operating instructions：all operations use VPOperateManager Object
    
    3.1 obtain VPOperateManager Object： VPOperateManager.getMangerInstance()
    3.2 scan Bluetooth devices: startScanDevice();
    3.3 connect  devices: connectDevice();
        3.3.1 confirm the password：confirmDevicePwd()
        3.3.2 synchronization the personal information：syncPersonInfo()
    3.4 register connection status of the device to  listen）:registerConnectStatusListener(); //please use this method after the device is connected
    3.5 register switch status of the system bluetooth to listen:registerBluetoothStateListener(); //this method can be used under any of status
    3.6 other data interaction operation
    
    Remarks 1：
    In order to avoid memory leak, please cautiously use context object, and we recommend to use getApplicationContext()
    Such as: example to obtain VPOperateManager object：VPOperateManager.getMangerInstance(getApplicationContext())；
    
    Remarks 2： 
    Such as call scan device:VPOperateManager.getMangerInstance().startScanDevice()；
    Above uniformly abbreviated as:startScanDevice();
    
    Remarks 3:
    After call connectDevice, you must wait bleNotifyResponse() callback success,and then you can call confirmDevicePwd()
    The first interation is code validation：confirmDevicePwd()
    The second interation is personal information synchronization：syncPersonInfo()

    Remarks 4:
    The device does not support asynchronous operations. data may be abnormal when multiple time-consuming operations are performed simultaneously.Therefore, when interacting with the device, please avoid multiple operations at the same time.

### 4. Bluetooth Data Interaction Instrctions  

    The design of SDK issue under bluetooth data only need call method, input setting data and monitor interface, When data returns, the interface triggers a callback, take confirmDevicePwd() for example
    //input data
    String  pwdStr=“0000”;
    boolean is24Hourmodel = false;   
    //call method, anonymous inner classes are used for callback listening
    VPOperateManager.getMangerInstance(mContext).confirmDevicePwd(writeResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    //triggers  callback
                    String message = "PwdData:\n" + pwdData.toString();
                    Logger.t(TAG).i(message);
                }
            }, new IDeviceFuctionDataListener() {
                @Override
                public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                    //triggers  callback
                    String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                    Logger.t(TAG).i(message);
                }
            }, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    //triggers  callback
                    String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                 }
            }, pwdStr, is24Hourmodel);
            
### 5. Firmware Upgrade Instructions  

	Firmware upgrade is mainly to upgrade the software function of the device. This operation requires strict requirements, and the wrong upgrade will bring a very bad experience to customer
	Please perform this operation carefully, and the upgrade operation requires network conditions, including the following 3 steps. Please check carefully
    For specific upgrade example, please check the "com.timaimee.vpbluetoothsdkdemo.oad.Activity.OadActivity" of demo project "VpBluetoothSDKDemo" 
    1.The configuration of AndroidMainfest.xml	     
		we have talked about its configuration stated above on configuration of Activity and Service, it mainly configure two files, service and activity,
		developers can copy these two files under "VpBluetoothSDKDemo".
    
    2.Judgement before upgrade
        a.please verify device version and upgrade file before upgrade
        b.After both are verified, send the firmware upgrade command, and the device will enter the firmware upgrade mode
        c.Then the app scan the device under the firmware upgrade mode
        d.in the end, to call the official upgrade program

        Remarks：checkVersionAndFile() method has encapsulated all of the above steps, and the developer can calls this method directly.
            callback method1：onCheckSucces() means that both the device version and the upgrade file have been verified successfully)
            callback method2：findOadDevice() means that the device in firmware upgrade mode has been found and can call the official upgrade program
         
		Other conditions, The device version and upgrade file have been passed, and the file is correct, but the device in the firmware upgrade mode is not found
		Processing method, call findOadModelDevice() method, then call its official upgrade program on its callback method
		Of course, this rarely happens and we do not recommend developers call findOadModelDevice() individually
    
    3.Calling official upgrade program
		startOad() under OadActivity, usually called after the findOadDevice method callback of interface OnFindOadDeviceListener

## Thanks  

* [Android-DFU-Library](https://github.com/NordicSemiconductor/Android-DFU-Library)
* [BluetoothKit](https://github.com/dingjikerbo/BluetoothKit)  
* [Gson](https://github.com/google/gson)  


## License
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





