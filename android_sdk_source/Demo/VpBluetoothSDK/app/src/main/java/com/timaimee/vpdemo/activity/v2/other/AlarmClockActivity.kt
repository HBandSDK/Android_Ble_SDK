package com.timaimee.vpdemo.activity.v2.other

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.TimePicker
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IAlarmDataListener
import com.veepoo.protocol.model.datas.AlarmData
import com.veepoo.protocol.model.enums.EAalarmStatus
import com.veepoo.protocol.model.settings.AlarmSetting
import java.util.Calendar
import java.util.Locale

class AlarmClockActivity : BaseVPBLETestActivity() , IAlarmDataListener{

    companion object {
        const val TAG = "闹钟"
    }

    lateinit var ccvOldAlarmClock: CollapseCardLogView
    lateinit var btnSetting: Button
    lateinit var btnRead: Button
    lateinit var btnNewAlarmClock: Button
    lateinit var btnTextAlarmClock: Button
    lateinit var cbAlarmClock1: CheckBox
    lateinit var btnAlarmClockTime1: Button
    lateinit var rgAlarmClock1IsOpen: RadioGroup

    lateinit var cbAlarmClock2: CheckBox
    lateinit var btnAlarmClockTime2: Button
    lateinit var rgAlarmClock2IsOpen: RadioGroup

    lateinit var cbAlarmClock3: CheckBox
    lateinit var btnAlarmClockTime3: Button
    lateinit var rgAlarmClock3IsOpen: RadioGroup

    override fun getLayoutID() = R.layout.activity_alarm_clock

    override fun pageTitle(): String = "闹钟"

    override fun initView() {
        ccvOldAlarmClock = findViewById(R.id.ccvOldAlarmClock)
        btnSetting = findViewById(R.id.btnSetting)
        btnRead = findViewById(R.id.btnRead)
        btnNewAlarmClock = findViewById(R.id.btnNewAlarmClock)
        btnTextAlarmClock = findViewById(R.id.btnTextAlarmClock)

        cbAlarmClock1 = findViewById(R.id.cbAlarmClock1)
        btnAlarmClockTime1 = findViewById(R.id.btnAlarmClockTime1)
        rgAlarmClock1IsOpen = findViewById(R.id.rgAlarmClock1IsOpen)

        cbAlarmClock2 = findViewById(R.id.cbAlarmClock2)
        btnAlarmClockTime2 = findViewById(R.id.btnAlarmClockTime2)
        rgAlarmClock2IsOpen = findViewById(R.id.rgAlarmClock2IsOpen)

        cbAlarmClock3 = findViewById(R.id.cbAlarmClock3)
        btnAlarmClockTime3 = findViewById(R.id.btnAlarmClockTime3)
        rgAlarmClock3IsOpen = findViewById(R.id.rgAlarmClock3IsOpen)
    }

    override fun initData() {
        btnTextAlarmClock.isEnabled = fCheck.checkTextAlarm()
        btnNewAlarmClock.isEnabled = fCheck.checkMultiAlarm()
        if (fCheck.checkMultiAlarm() || fCheck.checkTextAlarm()) {
            ccvOldAlarmClock.setFunctionDisabled()
        }
    }

    override fun initEvent() {
        btnRead.setOnClickListener {
            ccvOldAlarmClock.appendBlueMiddleText("▶ 开始读取旧版闹钟...")
            vpBleManager.readAlarm(defaultResponse, this)
        }

        btnSetting.setOnClickListener {
            val alarmClockList = mutableListOf<AlarmSetting>()
            if (!cbAlarmClock1.isChecked && !cbAlarmClock2.isChecked && !cbAlarmClock3.isChecked) {
                showToast("请至少设置一个闹钟")
            }
            if (cbAlarmClock1.isChecked) {
                val clockTimeStr = btnAlarmClockTime1.text.toString()
                val clockTimeArray = clockTimeStr.split(":")
                val isOpen = rgAlarmClock1IsOpen.checkedRadioButtonId == R.id.rbAlarmClock1Open
                val hour = clockTimeArray[0].toInt()
                val minute = clockTimeArray[1].toInt()
                val alarm = AlarmSetting(hour, minute, isOpen)
                alarmClockList.add(alarm)
            }
            if (cbAlarmClock2.isChecked) {
                val clockTimeStr = btnAlarmClockTime2.text.toString()
                val clockTimeArray = clockTimeStr.split(":")
                val isOpen = rgAlarmClock2IsOpen.checkedRadioButtonId == R.id.rbAlarmClock2Open
                val hour = clockTimeArray[0].toInt()
                val minute = clockTimeArray[1].toInt()
                val alarm = AlarmSetting(hour, minute, isOpen)
                alarmClockList.add(alarm)
            }
            if (cbAlarmClock3.isChecked) {
                val clockTimeStr = btnAlarmClockTime3.text.toString()
                val clockTimeArray = clockTimeStr.split(":")
                val isOpen = rgAlarmClock3IsOpen.checkedRadioButtonId == R.id.rbAlarmClock3Open
                val hour = clockTimeArray[0].toInt()
                val minute = clockTimeArray[1].toInt()
                val alarm = AlarmSetting(hour, minute, isOpen)
                alarmClockList.add(alarm)
            }
            vpBleManager.settingAlarm(defaultResponse, this, alarmClockList)
        }

        btnNewAlarmClock.setOnClickListener {
            startActivity(Intent(this, NewAlarmActivity::class.java))
        }

        btnTextAlarmClock.setOnClickListener {
            startActivity(Intent(this, TextAlarmActivity::class.java))
        }
        initDateBtn(btnAlarmClockTime1, btnAlarmClockTime2, btnAlarmClockTime3)
    }

    override fun onAlarmDataChangeListener(alarmData: AlarmData) {
        when(alarmData.status) {
            EAalarmStatus.SETTING_SUCCESS -> {
                ccvOldAlarmClock.appendBlueMiddleText("✅️ 旧版闹钟设置成功")
                alarmData.alarmSettingList.forEach {
                    ccvOldAlarmClock.appendResult(">>> 是否开启=${it.isOpen} 时间=${String.format(Locale.US,"%02d:%02d",it.hour, it.minute)}")
                }
            }
            EAalarmStatus.SETTING_FAIL -> {
                ccvOldAlarmClock.appendRedLargeText("❌️ 旧版闹钟设置失败")
            }
            EAalarmStatus.READ_SUCCESS -> {
                ccvOldAlarmClock.appendBlueMiddleText("✅️ 旧版闹钟读取成功")
                alarmData.alarmSettingList.forEach {
                    ccvOldAlarmClock.appendResult(">>> 是否开启=${it.isOpen} 时间=${String.format(Locale.US,"%02d:%02d",it.hour, it.minute)}")
                }
            }
            EAalarmStatus.READ_FAIL -> {
                ccvOldAlarmClock.appendRedLargeText("❌️ 旧版闹钟读取失败")

            }
            EAalarmStatus.UNKONW -> {
                ccvOldAlarmClock.appendRedLargeText("❌️ 未知异常")
            }
        }
    }

    private fun initDateBtn(vararg btnArray: Button) {
        for (button in btnArray) {
            button.setOnClickListener {
                showTimePicker(button)
            }
        }
    }

    private fun showTimePicker(targetBtn: Button) {
        val currentText = targetBtn.getText().toString().trim { it <= ' ' }
        val calendar = Calendar.getInstance()
        var defHour = calendar.get(Calendar.HOUR_OF_DAY)
        var defMin = calendar.get(Calendar.MINUTE)
        if (currentText.contains(":")) {
            val parts: Array<String?> = currentText.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size >= 2) {
                try {
                    defHour = parts[0]!!.toInt()
                    defMin = parts[1]!!.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        TimePickerDialog(this, OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
            val selectedTime = String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute)
            targetBtn.setText(selectedTime)
        }, defHour, defMin, true).show()
    }

}