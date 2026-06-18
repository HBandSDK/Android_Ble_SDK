package com.timaimee.vpdemo.activity.v2.other

import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.timaimee.vpdemo.utils.default
import com.timaimee.vpdemo.utils.switch
import com.timaimee.vpdemo.utils.toDes
import com.veepoo.protocol.listener.data.IWeatherStatusDataListener
import com.veepoo.protocol.model.datas.TimeData
import com.veepoo.protocol.model.datas.WeatherStatusData
import com.veepoo.protocol.model.datas.weather.WeatherData
import com.veepoo.protocol.model.datas.weather.WeatherEvery3Hour
import com.veepoo.protocol.model.datas.weather.WeatherEveryDay
import com.veepoo.protocol.model.enums.EWeatherOprateStatus
import com.veepoo.protocol.model.enums.EWeatherType
import com.veepoo.protocol.model.settings.WeatherStatusSetting
import java.util.Calendar
import java.util.Random

class WeatherActivity : BaseVPBLETestActivity(), IWeatherStatusDataListener{

    private lateinit var ccvWeatherControl: CollapseCardLogView
    private lateinit var etCityName: EditText
    private lateinit var rgIsOpen: RadioGroup
    private lateinit var rgTempUnit: RadioGroup
    private lateinit var btnWeatherSwitchUnitSetting: Button
    private lateinit var btnWeatherSwitchUnitRead: Button
    private lateinit var btnWeatherDataSetting: Button

    private var crc = 0

    override fun getLayoutID() = R.layout.activity_weather

    override fun pageTitle() = "☀️☔️☁️⛈️❄️🌨️天气设置"

    override fun initView() {
        ccvWeatherControl = findViewById(R.id.ccvWeatherControl)
        etCityName = findViewById(R.id.etCityName)
        rgIsOpen = findViewById(R.id.rgIsOpen)
        rgTempUnit = findViewById(R.id.rgTempUnit)
        btnWeatherSwitchUnitSetting = findViewById(R.id.btnWeatherSwitchUnitSetting)
        btnWeatherSwitchUnitRead = findViewById(R.id.btnWeatherSwitchUnitRead)
        btnWeatherDataSetting = findViewById(R.id.btnWeatherDataSetting)

    }

    override fun initData() {
        rgIsOpen.check(R.id.rbOpen)
        rgTempUnit.check(R.id.rbC)
        ccvWeatherControl.setFunctionEnabled(fCheck.checkWeather())
        ccvWeatherControl.setSubTitle("⚠️天气数据设置为随机，用户可以自行真实设置")
    }

    override fun initEvent() {
        btnWeatherSwitchUnitSetting.setOnClickListener {
            val setting = WeatherStatusSetting()
            setting.crc = crc
            setting.isOpen = rgIsOpen.checkedRadioButtonId == R.id.rbOpen
            val unitType: EWeatherType = (rgTempUnit.checkedRadioButtonId == R.id.rbC).switch(EWeatherType.C ,EWeatherType.F)
            setting.seteWeather(unitType)
            setWriteCmdTAG(0)
            ccvWeatherControl.clearTestInfo()
            ccvWeatherControl.appendBlueMiddleText("⏯️开始设置天气状态数据...")
            vpBleManager.settingWeatherStatusInfo(defaultResponse, setting, this)
        }
        btnWeatherSwitchUnitRead.setOnClickListener {
            setWriteCmdTAG(1)
            ccvWeatherControl.clearTestInfo()
            ccvWeatherControl.appendBlueMiddleText("⏯️开始读取天气状态数据...")
            vpBleManager.readWeatherStatusInfo(defaultResponse, this)
        }
        btnWeatherDataSetting.setOnClickListener {
            val cityName = etCityName.text.toString().trim().default("南山区")
            setWeatherData(cityName)
        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvWeatherControl.appendRedLargeText("⚠️[天气开关&单位设置]指令写入失败！")
            }
            1 -> {
                ccvWeatherControl.appendRedLargeText("⚠️[天气数据读取]指令写入失败！")
            }
            2 -> {
                ccvWeatherControl.appendRedLargeText("⚠️[天气数据设置]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvWeatherControl.appendBlueMiddleText("✅️[天气开关&单位设置}]指令写入成功！")
            }
            1 -> {
                ccvWeatherControl.appendBlueMiddleText("✅[天气数据读取]指令写入成功！")
            }
            2 -> {
                ccvWeatherControl.appendBlueMiddleText("✅[天气数据设置]指令写入成功！")
            }
        }
    }

    private fun setWeatherData(cityName: String) {
        //CRC
        val crc = 0
        //城市名称
        val cityName = cityName
        //数据来源
        val source = 0 //默认0， 用户可以自定定义，比如通过和风天气获取，通过VC天气获取，或者通过自己服务器获取等等
        //最近更新时间
        val year = TimeData.getSysYear()
        val month = TimeData.getSysMonth()
        val day = TimeData.getSysDay()
        val hour = TimeData.getSysHour()
        val minute = TimeData.getSysMiute()
        val lasTimeUpdate = TimeData(year, month, day, hour, minute, 0)
        //天气列表（以小时为单位）
        val weatherEvery3HourList: MutableList<WeatherEvery3Hour> = ArrayList<WeatherEvery3Hour>()
        for (i in 0..23) {
            val hourTime = TimeData(year, month, day, i, 1, 0)
            val weatherEvery3Hour =
                WeatherEvery3Hour(hourTime, c2f(i), i, YR(), WR(), "3-4", 15.0)
            weatherEvery3HourList.add(weatherEvery3Hour)
        }

        for (i in 0..23) {
            val hourTime = TimeData(year, month, day + 1, i, 1, 0)
            val weatherEvery3Hour =
                WeatherEvery3Hour(hourTime, c2f(i + 5), i + 5, YR(), WR(), "3-4", 15.0)
            weatherEvery3HourList.add(weatherEvery3Hour)
        }

        for (i in 0..23) {
            val hourTime = TimeData(year, month, day + 2, i, 1, 0)
            val weatherEvery3Hour =
                WeatherEvery3Hour(hourTime, c2f(i + 10), i + 10, YR(), WR(), "3-4", 15.0)
            weatherEvery3HourList.add(weatherEvery3Hour)
        }

        /**
         * 天气状态
         * 0-4	晴
         * 5-12	晴转多云
         * 13-16	阴天
         * 17-20	阵雨
         * 21-24	雷阵雨
         * 25-32	冰雹
         * 33-40	小雨
         * 41-48	中雨
         * 49-56	大雨
         * 57-72	暴雨
         * 73-84	小雪
         * 85-100	大雪
         * 101-155	多云
         */
        //天气列表（以天为单位）
        val weatherEveryDayList: MutableList<WeatherEveryDay> = ArrayList()
        val everyDay0 = TimeData(
            year, month, day,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE), 0
        )
        val weatherEveryDay0 = WeatherEveryDay(
            everyDay0,
            c2f(23),
            c2f(0),
            23,
            0, 10, weatherEvery3HourList.get(0).weatherState, weatherEvery3HourList.get(1)!!.weatherState, "10-12", 5.2
        )
        weatherEveryDayList.add(weatherEveryDay0)

        val everyDay1 = TimeData(year, month, day + 1, 0, 0, 0)
        val everyDay2 = TimeData(year, month, day + 2, 0, 0, 0)
        val weatherEveryDay1 = WeatherEveryDay(
            everyDay1,
            c2f(28),
            c2f(5),
            28,
            5, 10, weatherEvery3HourList.get(24).weatherState, weatherEvery3HourList.get(1 + 24)!!.weatherState, "10-12", 5.2
        )

        val weatherEveryDay2 = WeatherEveryDay(
            everyDay2,
            c2f(33),
            c2f(10),
            33,
            10, 10, weatherEvery3HourList.get(24 + 24).weatherState, weatherEvery3HourList.get(1 + 24 + 24)!!.weatherState, "10-12", 5.2
        )
        weatherEveryDayList.add(weatherEveryDay1)
        weatherEveryDayList.add(weatherEveryDay2)
        val weatherData = WeatherData(crc, cityName, source, lasTimeUpdate, weatherEvery3HourList, weatherEveryDayList)
        setWriteCmdTAG(2)
        ccvWeatherControl.clearTestInfo()
        ccvWeatherControl.appendBlueMiddleText("⏯️开始设置天气数据...")
        vpBleManager.settingWeatherData(defaultResponse, weatherData, this)
    }

    /**
     * 随机紫外线强度指数
     */
    private fun YR(): Int {
        val random = Random()
        return random.nextInt(6)
    }

    /**
     * 随机天气类型
     */
    private fun WR(): Int {
        val random = Random()
        return random.nextInt(155)
    }

    /**
     * 随机气温
     */
    private fun c2f(c: Int): Int {
        return (32f + c * 1.8f).toInt()
    }

    override fun onWeatherDataChange(weatherData: WeatherStatusData) {
        when (weatherData.oprate) {
            EWeatherOprateStatus.SETTING_STATUS_SUCCESS -> {
                ccvWeatherControl.appendBlueMiddleText("✅️设置天气状态成功")
                setWeatherData(weatherData)
            }
            EWeatherOprateStatus.SETTING_STATUS_FAIL -> {
                ccvWeatherControl.appendRedLargeText("❌️设置天气状态失败")
            }
            EWeatherOprateStatus.SETTING_CONTENT_SUCCESS -> {
                ccvWeatherControl.appendBlueMiddleText("✅️设置天气数据成功")
                setWeatherData(weatherData)
            }
            EWeatherOprateStatus.SETTING_CONTENT_FAIL -> {
                ccvWeatherControl.appendRedLargeText("❌️设置天气数据失败")
            }
            EWeatherOprateStatus.READ_SUCCESS -> {
                ccvWeatherControl.appendBlueMiddleText("✅️读取天气数据成功")
                setWeatherData(weatherData)
            }
            EWeatherOprateStatus.READ_FAIL -> {
                ccvWeatherControl.appendRedLargeText("❌️读取天气数据失败")
            }
            EWeatherOprateStatus.UNKONW -> {
                ccvWeatherControl.appendRedLargeText("❌️未知数据")
            }
        }
    }

    private fun setWeatherData(data: WeatherStatusData) {
        ccvWeatherControl.appendResult("===>天气状态数据<===")
        ccvWeatherControl.appendResult("开关: ${data.isOpen.toDes("开启","关闭")}")
        ccvWeatherControl.appendResult("CRC: ${data.crc}")
        ccvWeatherControl.appendResult("单位: ${(data.weatherType == EWeatherType.C).toDes("℃", "℉")}")
        crc = data.crc
        rgIsOpen.check(data.isOpen.switch(R.id.rbOpen, R.id.rbClose))
        rgTempUnit.check((data.weatherType == EWeatherType.C).switch(R.id.rbC, R.id.rbF))
    }

}