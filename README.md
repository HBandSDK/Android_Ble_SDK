# Android_Ble_SDK
Android_Ble_SDK是一个用于快速与蓝牙BLE交互的工具包，仅提供给我们合作的客户下载使用，方便提升客户的开发效率。
Android_Ble_SDK is a toolkit for quickly interacting with Bluetooth BLE. It is only available to our cooperative customers for download and use to improve their development efficiency.

README: 
* [中文版](https://github.com/HBandSDK/Android_Ble_SDK/wiki/VeepooSDK-Android-API-%E6%96%87%E6%A1%A3)  
* [English](https://github.com/HBandSDK/Android_Ble_SDK/wiki/VeepooSDK-Android-API-Document)
* [deepwiki](https://deepwiki.com/HBandSDK/Android_Ble_SDK)

## 必要条件 | Necessary dependencies jar

    
   * API>19&&BLE 4.0  
   * [vpbluetooth_x.x.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [gson-x.x.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [vpprotocol_2.1.xx.xx.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)

## 可选（如果是汇顶的芯片，升级会用到如下包）| Optional (If it is a Goodix chip, the upgrade will use the following package)

   * [libble-0.x.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libcomx-0.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libdfu-1.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)
   * [libfastdfu-0.x.jar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)

## 如何使用 | How to use

### 1. build.gradle

    compile files('libs/vpbluetooth_x.x.x.aar')  
    compile files('libs/gson-x.x.x.jar') 或者 compile 'com.google.code.gson:gson:x.x.x'  

### 2. Androidmanifest.xml

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <!--Activity&Service-->
    <service android:name="com.inuker.bluetooth.library.BluetoothService" />        
    <!--固件升级功能相关-->
    <service android:name=".oad.service.DfuService" /> 
    <activity android:name=".oad.activity.NotificationActivity" />
    
### 3. 蓝牙通信连接 | Ble connection


    操作说明:所有的操作都只是通过VPOperateManager;
    
    3.1 获取VPOperateManager实例： VPOperateManager.getMangerInstance()
    3.2 扫描蓝牙设备: startScanDevice();
    3.3 连接蓝牙设备: connectDevice();
        3.3.1 调用密码密码：confirmDevicePwd()
        3.3.2 调用个人信息设置：syncPersonInfo()
    3.4 设置连接设备的连接状态监听:registerConnectStatusListener(); //这个方法最好是在连接成功后设置
    3.5 设置系统蓝牙的开关状态监听:registerBluetoothStateListener(); //这个方法可以在任何状态下设置
    3.6 其他数据交互操作
    
    备注1：
    为了避免内存泄漏的情况，请谨慎使用context,推荐使用getApplicationContext();
    如：获取VPOperateManager实例：VPOperateManager.getMangerInstance(getApplicationContext())；
    
    备注2： 
    如调用扫描设备:VPOperateManager.getMangerInstance().startScanDevice()；
    以上统一简写为:startScanDevice();
    
    备注3:
    调用connectDevice(),然后在bleNotifyResponse回调成功后,才可执行密码验证操作
    第一步要进行的交互是验证密码：confirmDevicePwd()
    第二步要进行的交互是同步个人信息：syncPersonInfo()

    备注4:
    设备不支持异步操作,当多个耗时操作同时进行时,可能会导致数据异常;因此在与设备进行交互时,尽可能避免多个操作同时进行

    en:
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

### 4. 蓝牙数据交互说明 | Bluetooth data interaction instructions

    SDK在蓝牙数据的下发的设计是只需要调用方法,传入设置参数以及监听接口,当数据有返回时,接口会触发回调,以confirmDevicePwd为例
    The design of the SDK for sending Bluetooth data is to only call the method, pass in the setting parameters and listen to the interface. When the data is returned, the interface will trigger the callback. Take confirmDevicePwd as an example
    
    //传入参数
    String  pwdStr=“0000”;
    boolean is24Hourmodel = false;   
    //调用方法,匿名内部类用于回调监听
    VPOperateManager.getMangerInstance(mContext).confirmDevicePwd(writeResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    //触发回调
                    String message = "PwdData:\n" + pwdData.toString();
                    Logger.t(TAG).i(message);
                }
            }, new IDeviceFuctionDataListener() {
                @Override
                public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                    //触发回调
                    String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                    Logger.t(TAG).i(message);
                }
            }, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                    //触发回调
                    String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
                    Logger.t(TAG).i(message);
                 }
            }, pwdStr, is24Hourmodel);
            
### 5. 固件升级说明 | Firmware Upgrade Instructions

    注意：已兼容noric的芯片以及汇顶的芯片
    固件升级的作用主要是针对设备的软件功能进行升级,此操作要求非常严谨，升级出错会给用户带来非常不好的体验。  
    请谨慎执行此操作，升级操作要求在有网络的条件下进行，包括以下3个步骤,请务必仔细核对。  
    具体的升级案例,可参考我司的运行demo工程VpBluetoothSDKDemo下的com.timaimee.vpbluetoothsdkdemo.oad.Activity.OadActivity
    
    1.AndroidMainfest.xml的配置	      
        这个配置已在前面的Activity&Service配置进行过说明，主要是配置两个文件,一个service和一个activity,
        开发者也可以直接拷贝VpBluetoothSDKDemo下的这两个文件。
    
    2.升级前的判断
        a.升级前先进行设备版本校验，升级文件校验
        b.两者进行校验后，发送固件升级命令，设备会进入固件升级模式
        c.然后app找到固件升级模式下的设备时
        d.最后可以调用官方升级程序。

        备注：checkVersionAndFile()方法已封装以上的所有步骤，开发者调用此方法就行。
            回调方法1：onCheckSucces()表示设备版本以及升级文件两者校验成功;
            回调方法2：findOadDevice()表示找到固件升级模式下的设备,可以调用官方升级程序。
         
        其他情况:设备版本以及升级文件已经通过,且文件没有问题，但未发现固件升级模式下的设备，
        处理方式:调用findOadModelDevice()方法，然后在其回调方法findOadDevice()中调用官方升级程序，
        当然这种情况极少，不建议开发单独调用findOadModelDevice();
    
    3.调用官方升级程序
        OadActivity下的startOad()，一般在接口OnFindOadDeviceListener的findOadDevice方法回调后被调用。

 en:
	Note: compatible with noric chips and Goodix chips
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

### 6. 更换表盘说明（UI升级）| Instructions for changing the dial (UI upgrade)
	
	设备现在进行更换表盘操作，设备的表盘分为三部分（默认自带的表盘（多个）+可以编辑的表盘（1个）+服务器的表盘（1个））

	1.默认自带的表盘可直接通过settingScreenStyle方法设置
	2.可以编辑的表盘，可以自选三种元素及三种元素的放置位置+自选背景图片，选择好的背景图片需要下发到设备
	3.服务器的表盘，获取服务器的表盘列表，包含了预览图、文件以及其他一些必要的信息，然后下发到设备

默认自带的表盘设置demo: VPOperateManager.getMangerInstance(mContext).settingScreenStyle();

en:
	The device is now changing the dial. The dial of the device is divided into three parts (default built-in dial (multiple) + editable dial (1) + server dial (1))
	
	1. The default built-in dial can be set directly through the settingScreenStyle method
	2. For the editable dial, you can select three elements and the placement of the three elements + select the background image. The selected background image needs to be sent to the device
	3. For the server dial, get the server dial list, including preview images, files and other necessary information, and then send it to the device
	
	Default built-in dial setting demo: VPOperateManager.getMangerInstance(mContext).settingScreenStyle();

[Editable watch face demo](https://github.com/HBandSDK/Android_Ble_SDK/blob/caff11a10f493ac8c6e8471708984fd8e6a15a86/android_sdk_source/Demo/VpBluetoothSDK/app/src/main/java/com/timaimee/vpdemo/activity/UiUpdateCustomActivity.java)

[Server dial demo](https://github.com/HBandSDK/Android_Ble_SDK/blob/caff11a10f493ac8c6e8471708984fd8e6a15a86/android_sdk_source/Demo/VpBluetoothSDK/app/src/main/java/com/timaimee/vpdemo/activity/UiUpdateServerActivity.java)

	
	

## 鸣谢 | Acknowledgements

* [Android-DFU-Library](https://github.com/NordicSemiconductor/Android-DFU-Library)
* [BluetoothKit](https://github.com/dingjikerbo/BluetoothKit)  
* [Gson](https://github.com/google/gson)  



## 许可协议 | License Agreement
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








