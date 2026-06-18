package com.timaimee.vpdemo.activity.v2.health

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TimePicker
import com.orhanobut.logger.Logger
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IAllSetDataListener
import com.veepoo.protocol.listener.data.ISpo2hDataListener
import com.veepoo.protocol.model.datas.AllSetData
import com.veepoo.protocol.model.datas.Spo2hData
import com.veepoo.protocol.model.enums.EAllSetType
import com.veepoo.protocol.model.enums.EDeviceStatus
import com.veepoo.protocol.model.enums.ESPO2HStatus
import com.veepoo.protocol.model.settings.AllSetSetting
import java.util.Calendar
import java.util.Locale

class BloodOxygenActivity : BaseVPBLETestActivity(){
    companion object {
        const val TAG = "血氧"
    }

    lateinit var ccvBloodOxygenDetect: CollapseCardLogView
    lateinit var ccvBloodOxygenSetting: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    lateinit var rgIsOpen: RadioGroup
    lateinit var btnBloodOxygenAlarmSetting: Button
    lateinit var btnBloodOxygenAlarmRead: Button
    lateinit var btnStartTime: Button
    lateinit var btnEndTime: Button
    override fun getLayoutID() = R.layout.activity_blood_oxygen

    override fun pageTitle() = "血氧"

    override fun initView() {
        ccvBloodOxygenDetect = findViewById(R.id.ccvBloodOxygenDetect)
        ccvBloodOxygenSetting = findViewById(R.id.ccvBloodOxygenSetting)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        btnBloodOxygenAlarmSetting = findViewById(R.id.btnBloodOxygenAlarmSetting)
        btnBloodOxygenAlarmRead = findViewById(R.id.btnBloodOxygenAlarmRead)
        rgIsOpen = findViewById(R.id.rgIsOpen)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
    }

    override fun initData() {
        rgIsOpen.check(R.id.rbOpen)
        setCollapseExpandReciprocity(ccvBloodOxygenDetect, ccvBloodOxygenSetting)
    }

    override fun initEvent() {
        btnStartDetect.setOnClickListener {
            setWriteCmdTAG(0)
            ccvBloodOxygenDetect.clearTestInfo()
            ccvBloodOxygenDetect.appendBlueMiddleText("▶ 开始测量血氧...")
            vpBleManager.startDetectSPO2H(defaultResponse, ISpo2hDataListener { data: Spo2hData ->
                when (data.deviceState) {
                    EDeviceStatus.FREE -> {
                        if (data.value == 0) {
                            ccvBloodOxygenDetect.appendBlueMiddleText(">>> 测量中...")
                        } else {
                            ccvBloodOxygenDetect.appendResult(">>> 血氧值=${data.value}%")
                        }
                    }

                    EDeviceStatus.BUSY -> {
                        ccvBloodOxygenDetect.appendRedLargeText("⚠️:设备正忙")
                    }

                    EDeviceStatus.DETECT_HEART,
                    EDeviceStatus.DETECT_FTG,
                    EDeviceStatus.DETECT_BP,
                    EDeviceStatus.DETECT_PPG,
                    EDeviceStatus.DETECT_AUTO_FIVE  -> {
                        ccvBloodOxygenDetect.appendRedLargeText("⚠️:设备正在检测其他...")
                    }

                    EDeviceStatus.UNPASS_WEAR -> {
                        ccvBloodOxygenDetect.appendRedLargeText("❌️:佩戴不通过")
                    }

                    EDeviceStatus.CHARG_LOW -> {
                        ccvBloodOxygenDetect.appendRedLargeText("🚨:电池电量过低")
                    }

                    EDeviceStatus.CHARGING -> {
                        ccvBloodOxygenDetect.appendRedLargeText("🚨:设备正在充电")
                    }

                    EDeviceStatus.FINISH,EDeviceStatus.KEEP_QUIT-> {
//                        ccvBloodOxygenDetect.appendResult(">>> 血氧=${heartData.data}bpm")
                    }

                    else -> {}
                }
                Logger.t(TAG).e("-测量中-: | $data")
            })
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvBloodOxygenDetect.appendBlueMiddleText("⏹️ 停止测量血氧...")
            vpBleManager.stopDetectSPO2H(defaultResponse, object : ISpo2hDataListener{
                override fun onSpO2HADataChange(spo2HData: Spo2hData) {
                    if (spo2HData.spState == ESPO2HStatus.CLOSE) {
                        ccvBloodOxygenDetect.appendOrangeText("🛑: 血氧测量已停止")
                    } else {
                        ccvBloodOxygenDetect.appendRedLargeText("❌️: 血氧测量停止失败")
                    }
                }

            })
        }
        btnStartTime.setOnClickListener {
            showTimePicker(btnStartTime)
        }
        btnEndTime.setOnClickListener {
            showTimePicker(btnEndTime)
        }
        btnBloodOxygenAlarmSetting.setOnClickListener {
            setWriteCmdTAG(2)
            val startTimeStr = btnStartTime.text.toString()
            val endTimeStr = btnEndTime.text.toString()

            val startTimeArray = startTimeStr.split(":")
            val endTimeArray = endTimeStr.split(":")
            val startHour = startTimeArray[0].toInt()
            val startMinute = startTimeArray[1].toInt()
            val endHour = endTimeArray[0].toInt()
            val endMinute = endTimeArray[1].toInt()

            ccvBloodOxygenSetting.appendBlueMiddleText("▶ 设置血氧自动监测...")
            val isOpen = if (rgIsOpen.checkedRadioButtonId == R.id.rbOpen) 1 else 0
            val setting = AllSetSetting(EAllSetType.SPO2H_NIGHT_AUTO_DETECT, startHour, startMinute, endHour, endMinute, 0, isOpen)
            vpBleManager.settingSpo2hAutoDetect(defaultResponse,object : IAllSetDataListener {

                override fun onAllSetDataChangeListener(data: AllSetData) {
                    val info = String.format("自动检测时间 = %02d:%02d ~ %02d:%02d , 开关状态=%s",data.startHour, data.startMinute, data.endHour, data.endMinute, if(data.isOpen == 1) "开" else "关")
                    ccvBloodOxygenSetting.appendResult("✅️ 设置成功>>> $info")
                }

            }, setting)

        }

        btnBloodOxygenAlarmRead.setOnClickListener {
            setWriteCmdTAG(3)
            ccvBloodOxygenSetting.appendBlueMiddleText("▶ 读取血氧自动监测...")
            vpBleManager.readSpo2hAutoDetect(defaultResponse) { data ->
                val info = String.format("自动检测时间 = %02d:%02d ~ %02d:%02d , 开关状态=%s",data.startHour, data.startMinute, data.endHour, data.endMinute, if(data.isOpen == 1) "开" else "关")
                ccvBloodOxygenSetting.appendResult("🔚:读取回复>>> $info")
            }
        }
    }

    private fun showTimePicker(targetBtn: Button) {
        // 1. 获取按钮当前的文本
        val currentText = targetBtn.getText().toString().trim { it <= ' ' }
        // 2. 初始化默认时分（降级方案：当前系统时间）
        val calendar = Calendar.getInstance()
        var defHour = calendar.get(Calendar.HOUR_OF_DAY)
        var defMin = calendar.get(Calendar.MINUTE)

        // 3. 尝试解析按钮上的 "HH:mm:ss" 或 "HH:mm"
        if (currentText.contains(":")) {
            val parts: Array<String?> = currentText.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size >= 2) {
                try {
                    defHour = parts[0]!!.toInt()
                    defMin = parts[1]!!.toInt()
                } catch (e: NumberFormatException) {
                    e.printStackTrace() // 解析失败则保持系统当前时间
                }
            }
        }
        // 4. 弹出选择器，并传入解析好的默认值
        TimePickerDialog(this, OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
            // 始终保持 "HH:mm:00" 的秒级尾缀输出
            val selectedTime = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute)
            targetBtn.setText(selectedTime)
        }, defHour, defMin, true).show()
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBloodOxygenDetect.appendRedLargeText("⚠️[开始测量血氧]指令写入失败！")
            }
            1 -> {
                ccvBloodOxygenDetect.appendRedLargeText("⚠️[停止测量血氧]指令写入失败！")
            }
            2 -> {
                ccvBloodOxygenSetting.appendRedLargeText("⚠️[读取血氧自动监测设置]指令写入失败！")
            }
            3 -> {
                ccvBloodOxygenSetting.appendRedLargeText("⚠️[读取血氧自动监测设置]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBloodOxygenDetect.appendBlueMiddleText("✅️[开始测量血氧]指令写入成功！")
            }
            1 -> {
                ccvBloodOxygenDetect.appendBlueMiddleText("✅[停止测量血氧]指令写入成功！")
            }
            2 -> {
                ccvBloodOxygenSetting.appendBlueMiddleText("✅[读取血氧自动监测设置]指令写入成功！")
            }
            3 -> {
                ccvBloodOxygenSetting.appendBlueMiddleText("✅[读取血氧自动监测设置]指令写入成功！")
            }
        }
    }

}