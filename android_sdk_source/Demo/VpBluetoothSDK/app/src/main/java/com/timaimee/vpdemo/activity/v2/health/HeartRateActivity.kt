package com.timaimee.vpdemo.activity.v2.health

import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.orhanobut.logger.Logger
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IBPDetectDataListener
import com.veepoo.protocol.listener.data.IBPSettingDataListener
import com.veepoo.protocol.listener.data.IHeartDataListener
import com.veepoo.protocol.listener.data.IHeartWaringDataListener
import com.veepoo.protocol.model.datas.BpSettingData
import com.veepoo.protocol.model.datas.HeartData
import com.veepoo.protocol.model.datas.HeartWaringData
import com.veepoo.protocol.model.enums.EBPDetectModel
import com.veepoo.protocol.model.enums.EHeartStatus
import com.veepoo.protocol.model.settings.BpSetting
import com.veepoo.protocol.model.settings.HeartWaringSetting

class HeartRateActivity : BaseVPBLETestActivity(){
    companion object {
        const val TAG = "心率"
    }

    lateinit var ccvHeartRateDetect: CollapseCardLogView
    lateinit var ccvHeartRateAlarm: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    lateinit var rgIsOpen: RadioGroup
    lateinit var btnHeartRateAlarmSetting: Button
    lateinit var btnHeartRateAlarmRead: Button
    lateinit var etLow: EditText
    lateinit var etHigh: EditText
    override fun getLayoutID() = R.layout.activity_heart_rate

    override fun pageTitle() = "心率"

    override fun initView() {
        ccvHeartRateDetect = findViewById(R.id.ccvHeartRateDetect)
        ccvHeartRateAlarm = findViewById(R.id.ccvHeartRateAlarm)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        btnHeartRateAlarmSetting = findViewById(R.id.btnHeartRateAlarmSetting)
        btnHeartRateAlarmRead = findViewById(R.id.btnHeartRateAlarmRead)
        rgIsOpen = findViewById(R.id.rgIsOpen)
        etLow = findViewById(R.id.etLow)
        etHigh = findViewById(R.id.etHigh)
    }

    override fun initData() {
        rgIsOpen.check(R.id.rbOpen)
        setCollapseExpandReciprocity(ccvHeartRateDetect, ccvHeartRateAlarm)
    }

    override fun initEvent() {
        btnStartDetect.setOnClickListener {
            setWriteCmdTAG(0)
            ccvHeartRateDetect.clearTestInfo()
            ccvHeartRateDetect.appendBlueMiddleText("▶ 开始测量心率...")
            vpBleManager.startDetectHeart(defaultResponse, IHeartDataListener { heartData: HeartData? ->
                when (heartData!!.getHeartStatus()) {
                    EHeartStatus.STATE_INIT -> {
                        ccvHeartRateDetect.appendBlueMiddleText(">>> 设备初始化")
                    }

                    EHeartStatus.STATE_HEART_BUSY -> {
                        ccvHeartRateDetect.appendRedLargeText("⚠️:设备正忙")
                    }

                    EHeartStatus.STATE_HEART_DETECT -> {
                        ccvHeartRateDetect.appendRedLargeText("⚠️:设备正在检测...")
                    }

                    EHeartStatus.STATE_HEART_WEAR_ERROR -> {
                        ccvHeartRateDetect.appendRedLargeText("❌️:佩戴不通过")
                    }

                    EHeartStatus.STATE_LOW_BATTERY -> {
                        ccvHeartRateDetect.appendRedLargeText("🚨:电池电量过低")
                    }

                    EHeartStatus.STATE_HEART_NORMAL -> {
                        ccvHeartRateDetect.appendResult(">>> 心率=${heartData.data}bpm")
                    }
                }
            })
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvHeartRateDetect.appendBlueMiddleText("⏹️ 停止测量心率...")
            vpBleManager.stopDetectHeart(defaultResponse)
        }
        btnHeartRateAlarmSetting.setOnClickListener {
            setWriteCmdTAG(2)
            val lowStr = etLow.text.toString()
            val highStr = etHigh.text.toString()
            if (lowStr.isEmpty()) {
                showToast("心率过高不能为空")
                return@setOnClickListener
            }
            if (highStr.isEmpty()) {
                showToast("心率过低不能为空")
                return@setOnClickListener
            }
            val low = lowStr.toInt()
            val high = highStr.toInt()
            if (low>=high) {
                showToast("心率过高报警不能小于等于心率过低报警")
                return@setOnClickListener
            }
            ccvHeartRateAlarm.appendBlueMiddleText("▶ 开始心率报警...")
            val isOpen = rgIsOpen.checkedRadioButtonId == R.id.rbOpen
            val setting = HeartWaringSetting(high, low, isOpen)
            vpBleManager.settingHeartWarning(defaultResponse,object : IHeartWaringDataListener {

                override fun onHeartWaringDataChange(data: HeartWaringData) {
                    val info = ">>> 状态：${data.status}%, 开关=${data.isOpen} -> ${data.heartLow}/${data.heartHigh}"
                    ccvHeartRateAlarm.appendBlueMiddleText("🔚:设置回复>>> $info")
                }

            }, setting)

        }

        btnHeartRateAlarmRead.setOnClickListener {
            setWriteCmdTAG(3)
            vpBleManager.readHeartWarning(defaultResponse) { data ->
                etHigh.setText(data.heartHigh.toString())
                etLow.setText(data.heartLow.toString())
                val info = ">>> 状态：${data.status}%, 开关=${data.isOpen} -> ${data.heartLow}/${data.heartHigh}"
                ccvHeartRateAlarm.appendBlueMiddleText("🔚:读取回复>>> $info")
            }
        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvHeartRateDetect.appendRedLargeText("⚠️[开始测量心率]指令写入失败！")
            }
            1 -> {
                ccvHeartRateDetect.appendRedLargeText("⚠️[停止测量心率]指令写入失败！")
            }
            2 -> {
                ccvHeartRateAlarm.appendRedLargeText("⚠️[读取心率报警设置]指令写入失败！")
            }
            3 -> {
                ccvHeartRateAlarm.appendRedLargeText("⚠️[读取心率报警设置]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvHeartRateDetect.appendBlueMiddleText("✅️[开始测量心率]指令写入成功！")
            }
            1 -> {
                ccvHeartRateDetect.appendBlueMiddleText("✅[停止测量心率]指令写入成功！")
            }
            2 -> {
                ccvHeartRateAlarm.appendBlueMiddleText("✅[读取心率报警设置]指令写入成功！")
            }
            3 -> {
                ccvHeartRateAlarm.appendBlueMiddleText("✅[读取心率报警设置]指令写入成功！")
            }
        }
    }

}