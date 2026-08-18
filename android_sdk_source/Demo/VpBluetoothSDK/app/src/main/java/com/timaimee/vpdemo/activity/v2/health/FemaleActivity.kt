package com.timaimee.vpdemo.activity.v2.health

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.activity.v2.DeviceMenu
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.timaimee.vpdemo.utils.d
import com.timaimee.vpdemo.utils.switch
import com.veepoo.protocol.listener.data.IWomenDataListener
import com.veepoo.protocol.model.datas.TimeData
import com.veepoo.protocol.model.datas.WomenData
import com.veepoo.protocol.model.enums.ESex
import com.veepoo.protocol.model.enums.EWomenOprateStatus
import com.veepoo.protocol.model.enums.EWomenStatus
import com.veepoo.protocol.model.settings.WomenSetting
import java.util.Calendar
import java.util.Locale

class FemaleActivity: BaseVPBLETestActivity() , IWomenDataListener{

    private val TAG = "【女性功能】"

    lateinit var ccvFemale: CollapseCardLogView
    lateinit var spFun: Spinner
    lateinit var rgBayGender: RadioGroup
    lateinit var btnMenesDate: Button
    lateinit var btnDueDate: Button
    lateinit var btnBabyBirthday: Button
    lateinit var btnSetting: Button
    lateinit var btnRead: Button

    lateinit var tvLastMenes: TextView
    lateinit var tvDueDate: TextView
    lateinit var tvBabyBirthday: TextView
    lateinit var tvBabySex: TextView

    var status: EWomenStatus = EWomenStatus.MENES

    override fun getLayoutID() = R.layout.activity_female

    override fun pageTitle() = DeviceMenu.Health.Female

    override fun initView() {
        ccvFemale = findViewById(R.id.ccvFemale)
        spFun = findViewById(R.id.spFun)
        rgBayGender = findViewById(R.id.rgBayGender)
        btnMenesDate = findViewById(R.id.btnMenesDate)
        btnDueDate = findViewById(R.id.btnDueDate)
        btnBabyBirthday = findViewById(R.id.btnBabyBirthday)
        btnSetting = findViewById(R.id.btnSetting)
        btnRead = findViewById(R.id.btnRead)
        tvLastMenes = findViewById(R.id.tvLastMenes)
        tvDueDate = findViewById(R.id.tvDueDate)
        tvBabyBirthday = findViewById(R.id.tvBabyBirthday)
        tvBabySex = findViewById(R.id.tvBabySex)
    }

    override fun initData() {
        status = EWomenStatus.MENES
        initViewWithStatus()
    }

    override fun initEvent() {
        btnMenesDate.setOnClickListener(this)
        btnDueDate.setOnClickListener(this)
        btnBabyBirthday.setOnClickListener(this)
        btnSetting.setOnClickListener(this)
        btnRead.setOnClickListener(this)
        initSP(EWomenStatus.entries.filter { it.value != 0 }.map { it.name }.toTypedArray(), spFun)

        spFun.onItemSelectedListener

        spFun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                status = EWomenStatus.getStatusByValue(position + 1)
                showToast("选中：${status.name}")
                initViewWithStatus()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        spFun.onItemSelectedListener

    }

    private fun initViewWithStatus() {
        when(status) {
            EWomenStatus.NONE -> {}
            EWomenStatus.MENES, EWomenStatus.PREREADY -> { //月经状态 & 备孕状态
                tvLastMenes.visibility = View.VISIBLE
                btnMenesDate.visibility = View.VISIBLE

                tvBabySex.visibility = View.GONE
                rgBayGender.visibility = View.GONE

                tvDueDate.visibility = View.GONE
                btnDueDate.visibility = View.GONE

                tvBabyBirthday.visibility = View.GONE
                btnBabyBirthday.visibility = View.GONE
            }
            EWomenStatus.PREING -> { //怀孕状态
                tvLastMenes.visibility = View.VISIBLE
                btnMenesDate.visibility = View.VISIBLE

                tvBabySex.visibility = View.GONE
                rgBayGender.visibility = View.GONE

                tvDueDate.visibility = View.VISIBLE
                btnDueDate.visibility = View.VISIBLE

                tvBabyBirthday.visibility = View.GONE
                btnBabyBirthday.visibility = View.GONE
            }
            EWomenStatus.MAMAMI -> { //妈咪状态
                tvLastMenes.visibility = View.VISIBLE
                btnMenesDate.visibility = View.VISIBLE

                tvBabySex.visibility = View.VISIBLE
                rgBayGender.visibility = View.VISIBLE

                tvDueDate.visibility = View.GONE
                btnDueDate.visibility = View.GONE

                tvBabyBirthday.visibility = View.VISIBLE
                btnBabyBirthday.visibility = View.VISIBLE
            }
        }
    }

    private fun initSP(data: Array<String>, spinner: Spinner) {
        if (data.isEmpty()) return
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.btnMenesDate -> {
                showDatePicker(btnMenesDate)
            }
            R.id.btnDueDate -> {
                showDatePicker(btnDueDate)
            }
            R.id.btnBabyBirthday -> {
                showDatePicker(btnBabyBirthday)
            }
            R.id.btnSetting -> {
                val data = getWomenSettingData()
                TAG.d("设置的值 >>> ${getWomenInfo(data)}")
                vpBleManager.settingWomenState(defaultResponse, this, getWomenSettingData())
            }
            R.id.btnRead -> {
                vpBleManager.readWomenState(defaultResponse, this)
            }
        }
    }

    private fun getTimeDataByBtn(btn: Button) : TimeData{
        val currentText = btn.getText().toString().trim { it <= ' ' }
        if (currentText.contains("-")) {
            val parts: Array<String?> = currentText.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size == 3) {
                try {
                    val defYear = parts[0]!!.toInt()
                    val defMonth = parts[1]!!.toInt() // 转换为 Calendar 的 0-11 月
                    val defDay = parts[2]!!.toInt()
                    return TimeData(defYear, defMonth, defDay)
                } catch (e: NumberFormatException) {
                    e.printStackTrace() // 解析失败则保持系统当前时间
                }
            }
        }
        return TimeData(System.currentTimeMillis()/1000L)
    }

    private fun getWomenSettingData(): WomenSetting =
        when(status) {
            EWomenStatus.PREING -> { //怀孕状态
                WomenSetting(status, getTimeDataByBtn(btnMenesDate), getTimeDataByBtn(btnDueDate))
            }
            EWomenStatus.MAMAMI -> { //妈咪状态
                val sex = (rgBayGender.checkedRadioButtonId == R.id.rbMale).switch(ESex.MAN, ESex.WOMEN)
                WomenSetting(status, 4 , 28,
                    getTimeDataByBtn(btnMenesDate), sex, getTimeDataByBtn(btnBabyBirthday))
            }
            else -> {
                WomenSetting(status, 4 , 28, getTimeDataByBtn(btnMenesDate))
            }
        }

    private fun showDatePicker(btnDatePicker: Button) {
        // 1. 获取按钮当前的文本
        val currentText = btnDatePicker.getText().toString().trim { it <= ' ' }
        // 2. 初始化默认年月日（降级方案：当前系统时间）
        val calendar = Calendar.getInstance()
        var defYear = calendar.get(Calendar.YEAR)
        var defMonth = calendar.get(Calendar.MONTH) // 注意：Calendar 月份从 0 开始
        var defDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 3. 尝试解析按钮上的 "yyyy-MM-dd"
        if (currentText.contains("-")) {
            val parts: Array<String?> = currentText.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size == 3) {
                try {
                    defYear = parts[0]!!.toInt()
                    defMonth = parts[1]!!.toInt() - 1 // 转换为 Calendar 的 0-11 月
                    defDay = parts[2]!!.toInt()
                } catch (e: NumberFormatException) {
                    e.printStackTrace() // 解析失败则保持系统当前时间
                }
            }
        }

        // 4. 弹出选择器，并传入解析好的默认值
        DatePickerDialog(this, OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            // 更新按钮显示的文本
            val selectedDate = String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            btnDatePicker.setText(selectedDate)
        }, defYear, defMonth, defDay).show()
    }

    override fun onWomenDataChange(womenData: WomenData?) {
        womenData?.let {
            "${womenData.toString()}".d(">>>>>>>>>>>")
            when(it.oprateStatus) {
                EWomenOprateStatus.SETTING_SUCCESS -> ccvFemale.appendResult("✅️设置成功")
                EWomenOprateStatus.SETTING_FAIL -> ccvFemale.appendResult("❌️️设置失败")
                EWomenOprateStatus.READ_SUCCESS -> ccvFemale.appendResult("✅️读取成功")
                EWomenOprateStatus.READ_FAIL -> ccvFemale.appendResult("️❌️读取失败")
                EWomenOprateStatus.UNKONW -> ccvFemale.appendResult("⚠️未知状态")
            }
            it.womenSetting?.let {
                ccvFemale.appendResult(">>> ${getWomenInfo(it)}")
            }
        }
    }

    private fun getWomenInfo(setting: WomenSetting): String {
        return "状态：${setting.womenStatus}, 经期长度=${setting.menseLength}" +
                ", 经期周期=${setting.menesInterval}" +
                ", Baby性别=${setting.babySex}" +
                ", 上次月经时间=${setting.menesLasterday?.dateAndClock4GBandDb?:"NULL"}" +
                ", 预产期=${setting.confinementDay?.dateAndClock4GBandDb?:"NULL"}" +
                ", Baby生日=${setting.babyBirthday?.dateAndClock4GBandDb?:"NULL"}"
    }



}