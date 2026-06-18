package com.timaimee.vpdemo.activity.v2.connection

import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.model.BleGattProfile
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.timaimee.vpdemo.utils.switch
import com.timaimee.vpdemo.utils.toDes
import com.veepoo.protocol.listener.base.IConnectResponse
import com.veepoo.protocol.listener.base.INotifyResponse
import com.veepoo.protocol.listener.data.IDeviceBTConnectionListener
import com.veepoo.protocol.listener.data.IDeviceBTInfoListener
import com.veepoo.protocol.model.datas.BTInfo

class ConnectionActivity: BaseVPBLETestActivity(), IDeviceBTInfoListener, IDeviceBTConnectionListener {
    companion object {
        const val TAG = "连接控制"
    }

    lateinit var ccvBLE: CollapseCardLogView
    lateinit var ccvBT: CollapseCardLogView
    lateinit var etMacAddress: EditText
    lateinit var btnBLEConnect: Button
    lateinit var btnBLEDisconnect: Button
    lateinit var btnBTConnect: Button
    lateinit var btnBTDisconnect: Button
    lateinit var btnSettingBTStatus: Button
    lateinit var btnReadBTStatus: Button

    lateinit var rgConnectModel: RadioGroup
    lateinit var rgOpenBT: RadioGroup
    lateinit var rgDeviceAutoConnect: RadioGroup
    lateinit var rgOpenAudio: RadioGroup
    lateinit var rgClearPairInfo: RadioGroup

    override fun getLayoutID() = R.layout.activity_connection

    override fun pageTitle() = "BLE/BT🔗控制"

    var macAddress = ""

    override fun initView() {
        ccvBLE = findViewById(R.id.ccvBLE)
        ccvBT = findViewById(R.id.ccvBT)
        etMacAddress = findViewById(R.id.etMacAddress)
        btnBLEConnect = findViewById(R.id.btnBLEConnect)
        btnBLEDisconnect = findViewById(R.id.btnBLEDisconnect)
        btnBTConnect = findViewById(R.id.btnBTConnect)
        btnBTDisconnect = findViewById(R.id.btnBTDisconnect)
        btnSettingBTStatus = findViewById(R.id.btnSettingBTStatus)
        btnReadBTStatus = findViewById(R.id.btnReadBTStatus)
        rgConnectModel = findViewById(R.id.rgConnectModel)
        rgOpenBT = findViewById(R.id.rgOpenBT)
        rgDeviceAutoConnect = findViewById(R.id.rgDeviceAutoConnect)
        rgOpenAudio = findViewById(R.id.rgOpenAudio)
        rgClearPairInfo = findViewById(R.id.rgClearPairInfo)
    }

    override fun initData() {
        rgConnectModel.check(R.id.rbDefault)
        setCollapseExpandReciprocity(ccvBLE, ccvBT)
        macAddress = vpBleManager.macAddress
        etMacAddress.setText(macAddress)
    }

    override fun initEvent() {
        btnBLEConnect.setOnClickListener {
            val macAddress = etMacAddress.text.toString()
            if(macAddress == "NULL" || macAddress.isBlank()) {
                showToast("连接地址不能为空")
            }
            if (vpBleManager.isCurrentDeviceConnected && vpBleManager.macAddress == macAddress) {
                ccvBLE.appendRedLargeText("⚠️当前设备已连接！")
                return@setOnClickListener
            }
            setWriteCmdTAG(0)
            ccvBLE.clearTestInfo()
            ccvBLE.appendBlueMiddleText("▶ 开始连接🔗...")

            vpBleManager.connectDevice(macAddress, object : IConnectResponse {
                override fun connectState(code: Int, profile: BleGattProfile?, isOadModel: Boolean) {
                    if (code == Code.REQUEST_SUCCESS) {
                        ccvBLE.appendResult("✅️【${macAddress}】连接成功，${isOadModel.toDes("处于OTA模式", "未处于OTA模式")}")
                    } else {
                        ccvBLE.appendRedLargeText("❌️【${macAddress}】连接失败！")
                    }
                }

            }, object : INotifyResponse {
                override fun notifyState(state: Int) {
                    if (state == Code.REQUEST_SUCCESS) {
                        ccvBLE.appendResult("✅️【${macAddress}】通知打开成功>>>请进行密码校验")
                    } else {
                        ccvBLE.appendRedLargeText("❌️【${macAddress}】通知打开失败！")
                    }
                }

            })
        }
        btnBLEDisconnect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvBLE.appendBlueMiddleText("\uD83E\uDDF7 断开连接...")
            vpBleManager.disconnectWatch { defaultResponse }
        }
        btnSettingBTStatus.setOnClickListener {
            setWriteCmdTAG(2)
            val isAutoConnect = rgDeviceAutoConnect.checkedRadioButtonId == R.id.rbDACNo
            val isBTOpen = rgOpenBT.checkedRadioButtonId == R.id.rbBTOpen
            val isAudioOpen = rgOpenAudio.checkedRadioButtonId == R.id.rbAudioOpen
            val isClearPairInfo = rgClearPairInfo.checkedRadioButtonId == R.id.rbCPIYes
            ccvBT.appendBlueMiddleText("▶ 开始设置BT状态...")
            ccvBT.appendBlueMiddleText("▶ 设备是否自动回连:${isAudioOpen.toDes("回连", "不回连")}")
            ccvBT.appendBlueMiddleText("▶ 设备BT开关:${isAudioOpen.toDes("打开", "关闭")}")
            ccvBT.appendBlueMiddleText("▶ 多媒体开关:${isAudioOpen.toDes("打开", "关闭")}")
            ccvBT.appendBlueMiddleText("▶ 是否清除设备配对信息:${isAudioOpen.toDes("清除", "不清除")}")
            vpBleManager.setBTStatus(isAutoConnect, isBTOpen, isAudioOpen, isClearPairInfo, defaultResponse)
        }
        btnReadBTStatus.setOnClickListener {
            setWriteCmdTAG(3)
            vpBleManager.readBTInfo(defaultResponse, this)
        }
        btnBTConnect.setOnClickListener {
            val isDefault = rgConnectModel.checkedRadioButtonId == R.id.rbDefault
            ccvBT.appendBlueMiddleText("▶ 开始BT${isDefault.switch("默认",
                (rgConnectModel.checkedRadioButtonId == R.id.rbDual).switch("双模", "单模"))}连接...")
            if(isDefault) {
                vpBleManager.connectBT(macAddress, this)
            } else {
                vpBleManager.connectBT(macAddress, (rgConnectModel.checkedRadioButtonId == R.id.rbDual).switch(0, 1),this)
            }
        }
        btnBTDisconnect.setOnClickListener {
            ccvBT.appendBlueMiddleText("▶ 开始断开BT连接...")
            vpBleManager.disconnectBT(macAddress, this)
        }
    }

    override fun onDeviceBTFunctionNotSupport() {
        ccvBT.appendRedLargeText("⚠️当前设备不支持BT功能")
    }

    override fun onDeviceBTInfoSettingSuccess(btInfo: BTInfo) {
        ccvBT.appendResult("✅️BT状态设置成功")
        ccvBT.appendResult(">>> 设备是否自动回连:${btInfo.isAutoCon.toDes("回连", "不回连")}")
        ccvBT.appendResult(">>> 设备BT开关:${btInfo.isBTOpen.toDes("打开", "关闭")}")
        ccvBT.appendResult(">>> 多媒体开关:${btInfo.isAudioOpen.toDes("打开", "关闭")}")
        ccvBT.appendResult(">>> 是有设备配对信息:${btInfo.isHavePairInfo.toDes("有", "无")}")
    }

    override fun onDeviceBTInfoSettingFailed() {
        ccvBT.appendRedLargeText("❌️BT状态设置失败")
    }

    override fun onDeviceBTInfoReadSuccess(btInfo: BTInfo) {
        ccvBT.appendResult("✅️BT状态读取成功")
        ccvBT.appendResult(">>> 设备是否自动回连:${btInfo.isAutoCon.toDes("回连", "不回连")}")
        ccvBT.appendResult(">>> 设备BT开关:${btInfo.isBTOpen.toDes("打开", "关闭")}")
        ccvBT.appendResult(">>> 多媒体开关:${btInfo.isAudioOpen.toDes("打开", "关闭")}")
        ccvBT.appendResult(">>> 是有设备配对信息:${btInfo.isHavePairInfo.toDes("有", "无")}")
    }

    override fun onDeviceBTInfoReadFailed() {
        ccvBT.appendRedLargeText("❌️BT状态读取失败")
    }

    override fun onDeviceBTInfoReport(btInfo: BTInfo) {
        ccvBT.appendResult("✅️BT状态上报成功")
        ccvBT.appendResult(">>> 设备是否自动回连:${btInfo.isAutoCon.toDes("回连", "不回连")}")
        ccvBT.appendResult(">>> 设备BT开关:${btInfo.isBTOpen.toDes("打开", "关闭")}")
        ccvBT.appendResult(">>> 多媒体开关:${btInfo.isAudioOpen.toDes("打开", "关闭")}")
        ccvBT.appendResult(">>> 是有设备配对信息:${btInfo.isHavePairInfo.toDes("有", "无")}")
    }


    /**
     * 开始连接BT
     */
    override fun onDeviceBTConnecting() {
        ccvBT.appendBlueMiddleText("BT连接中...")
    }

    /**
     * 设备BT已连接
     */
    override  fun onDeviceBTConnected() {
        ccvBT.appendResult("✅️BT连接成功")
    }

    /**
     * 设备BT已断开
     */
    override fun onDeviceBTDisconnected() {
        ccvBT.appendRedLargeText("\uD83D\uDED1BT已断开")
    }

    /**
     * 设备BT连接超时
     */
    override  fun onDeviceBTConnectTimeout() {
        ccvBT.appendRedLargeText("☣️BT连接超时")
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            1 -> {
                ccvBLE.appendRedLargeText("⚠️[断开BLE]指令写入失败！")
            }
            2 -> {
                ccvBT.appendRedLargeText("⚠️[设置BT状态]指令写入失败！")
            }
            3 -> {
                ccvBT.appendRedLargeText("⚠️[读取BT状态]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            1 -> {
                ccvBLE.appendBlueMiddleText("✅[断开BLE]指令写入成功！")
            }
            2 -> {
                ccvBT.appendBlueMiddleText("✅[设置BT状态]指令写入成功！")
            }
            3 -> {
                ccvBT.appendBlueMiddleText("✅[读取BT状态]指令写入成功！")
            }
        }
    }

}