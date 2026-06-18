package com.timaimee.vpdemo.activity.v2.health

import android.app.TimePickerDialog
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TimePicker
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.ILongSeatDataListener
import com.veepoo.protocol.model.datas.LongSeatData
import com.veepoo.protocol.model.settings.LongSeatSetting
import java.util.Calendar
import java.util.Locale

class SedentaryRemindActivity : BaseVPBLETestActivity(){
    companion object {
        const val TAG = "久坐提醒"
    }

    lateinit var ccvSedentaryRemindSetting: CollapseCardLogView
    lateinit var rgIsOpen: RadioGroup
    lateinit var btnSedentaryRemindSetting: Button
    lateinit var btnSedentaryRemindRead: Button
    lateinit var btnStartTime: Button
    lateinit var btnEndTime: Button
    lateinit var etRemindInterval: EditText

    override fun getLayoutID() = R.layout.activity_sedentary_remind

    override fun pageTitle() = "血氧"

    override fun initView() {
        ccvSedentaryRemindSetting = findViewById(R.id.ccvSedentaryRemindSetting)
        btnSedentaryRemindSetting = findViewById(R.id.btnSedentaryRemindSetting)
        btnSedentaryRemindRead = findViewById(R.id.btnSedentaryRemindRead)
        etRemindInterval = findViewById(R.id.etRemindInterval)
        rgIsOpen = findViewById(R.id.rgIsOpen)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
    }

    override fun initData() {
        rgIsOpen.check(R.id.rbOpen)
    }

    override fun initEvent() {
        btnStartTime.setOnClickListener {
            showTimePicker(btnStartTime)
        }
        btnEndTime.setOnClickListener {
            showTimePicker(btnEndTime)
        }
        btnSedentaryRemindSetting.setOnClickListener {
            setWriteCmdTAG(0)
            val startTimeStr = btnStartTime.text.toString()
            val endTimeStr = btnEndTime.text.toString()

            val startTimeArray = startTimeStr.split(":")
            val endTimeArray = endTimeStr.split(":")
            val startHour = startTimeArray[0].toInt()
            val startMinute = startTimeArray[1].toInt()
            val endHour = endTimeArray[0].toInt()
            val endMinute = endTimeArray[1].toInt()
            val remindIntervalStr = etRemindInterval.text.toString()
            if (remindIntervalStr.isEmpty()) {
                showToast("请设置提醒间隔 建议[10 - 120]分钟")
                return@setOnClickListener
            }
            val remindInterval = remindIntervalStr.toInt()

            ccvSedentaryRemindSetting.appendBlueMiddleText("▶ 设置久坐提醒...")
            val isOpen = rgIsOpen.checkedRadioButtonId == R.id.rbOpen
            val setting = LongSeatSetting(startHour, startMinute, endHour, endMinute, remindInterval, isOpen)
            vpBleManager.settingLongSeat(defaultResponse,setting,object : ILongSeatDataListener {

                override fun onLongSeatDataChange(data: LongSeatData) {
                    val info = String.format("久坐提醒 = %02d:%02d ~ %02d:%02d, 提醒间隔=${data.threshold}分钟 开关状态=%s",data.startHour, data.startMinute, data.endHour, data.endMinute, if(data.isOpen) "开" else "关")
                    ccvSedentaryRemindSetting.appendResult("✅️ 设置成功>>> $info")
                }

            })

        }

        btnSedentaryRemindRead.setOnClickListener {
            setWriteCmdTAG(1)
            ccvSedentaryRemindSetting.appendBlueMiddleText("▶ 读取久坐提醒...")
            vpBleManager.readLongSeat(defaultResponse) { data ->
                val info = String.format("久坐提醒 = %02d:%02d ~ %02d:%02d, 提醒间隔=${data.threshold}分钟 开关状态=%s",data.startHour, data.startMinute, data.endHour, data.endMinute, if(data.isOpen) "开" else "关")
                ccvSedentaryRemindSetting.appendResult("🔚:读取回复>>> $info")
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
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
            // 始终保持 "HH:mm:00" 的秒级尾缀输出
            val selectedTime = String.Companion.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute)
            targetBtn.setText(selectedTime)
        }, defHour, defMin, true).show()
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvSedentaryRemindSetting.appendRedLargeText("⚠️[设置久坐提醒设置]指令写入失败！")
            }
            1 -> {
                ccvSedentaryRemindSetting.appendRedLargeText("⚠️[读取久坐提醒设置]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvSedentaryRemindSetting.appendBlueMiddleText("✅[设置久坐提醒设置]指令写入成功！")
            }
            1 -> {
                ccvSedentaryRemindSetting.appendBlueMiddleText("✅[读取久坐提醒设置]指令写入成功！")
            }
        }
    }

}