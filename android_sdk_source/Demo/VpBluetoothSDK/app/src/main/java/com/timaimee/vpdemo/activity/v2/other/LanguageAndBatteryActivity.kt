package com.timaimee.vpdemo.activity.v2.other

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IBatteryDataListener
import com.veepoo.protocol.listener.data.ILanguageDataListener
import com.veepoo.protocol.model.datas.BatteryData
import com.veepoo.protocol.model.datas.LanguageData
import com.veepoo.protocol.model.enums.ELanguage
import com.veepoo.protocol.model.enums.EOprateStauts

class LanguageAndBatteryActivity: BaseVPBLETestActivity() {
    lateinit var ccvBattery: CollapseCardLogView
    lateinit var ccvLanguage: CollapseCardLogView
    lateinit var btnReadBattery: Button
    lateinit var btnSettingLanguage: Button
    lateinit var spLanguage: Spinner

    override fun getLayoutID() = R.layout.activity_language_battery

    override fun pageTitle() = "🔋电量&语言"

    override fun initView() {
        ccvBattery = findViewById(R.id.ccvBattery)
        ccvLanguage = findViewById(R.id.ccvLanguage)
        btnReadBattery = findViewById(R.id.btnReadBattery)
        btnSettingLanguage = findViewById(R.id.btnSettingLanguage)
        spLanguage = findViewById(R.id.spLanguage)
    }

    override fun initData() {
        setCollapseExpandReciprocity(ccvLanguage, ccvBattery)
        val languageStringList: Array<String> = ELanguage.entries.map {
            it.name  // 拿到枚举的字符串：CHINA、ENGLISH、JAPAN...
        }.toTypedArray()
        initSP(languageStringList, spLanguage)
    }

    private fun initSP(data: Array<String>, spinner: Spinner) {
        if (data == null || data.size == 0) return
        val adapter = ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
    }

    override fun initEvent() {
        btnReadBattery.setOnClickListener {
            setWriteCmdTAG(0)
            ccvBattery.clearTestInfo()
            ccvBattery.appendBlueMiddleText("▶ 开始读取电池电量...")
            vpBleManager.readBattery(defaultResponse, object : IBatteryDataListener{
                override fun onDataChange(batteryData: BatteryData) {
                    ccvBattery.appendResult("===== 电池电量获取成功 =====")
                    if (batteryData.isPercent) {
                        ccvBattery.appendResult("电量：${batteryData.batteryPercent}%")
                        ccvBattery.appendResult("电池模式：${getPowerModelDes(batteryData)}")
                        ccvBattery.appendResult("状态：${getBatteryStateDes(batteryData)}")
                        ccvBattery.appendResult("是否低电：${batteryData.isLowBattery}")
                    } else {
                        ccvBattery.appendResult("电量等级：${batteryData.batteryLevel}%")
                        ccvBattery.appendResult("电池模式：${getPowerModelDes(batteryData)}")
                        ccvBattery.appendResult("状态：${getBatteryStateDes(batteryData)}")
                        ccvBattery.appendResult("是否低电：${batteryData.isLowBattery}")
                    }
                    ccvBattery.appendResult("-----------------------\n")
                }

            })
        }
        btnSettingLanguage.setOnClickListener {
            setWriteCmdTAG(1)
            val position = spLanguage.selectedItemPosition
            val language = ELanguage.entries.get(position)
            ccvBattery.appendBlueMiddleText("▶ 开始设置语言$language...")
            vpBleManager.settingDeviceLanguage(defaultResponse, object : ILanguageDataListener{
                override fun onLanguageDataChange(languageData: LanguageData) {
                    if (languageData.stauts == EOprateStauts.OPRATE_SUCCESS) {
                        ccvBattery.appendResult("✅️语言设置成功:${languageData.language}")
                    } else {
                        ccvBattery.appendRedLargeText("❌️️语言设置成功失败")
                    }
                }

            }, language)
        }
    }

    //    //**Power电源模式:** 0x00：正常状态 0x01：充电状态  0x02：低压状态  0x03：充满状态
    //    private int powerModel;
    //    //Sleep：  0----清醒    1----睡眠
    //    private int state;

    private fun getPowerModelDes(batteryData: BatteryData) =
        when (batteryData.powerModel) {
            0-> "正常模式"
            1-> "充电模式"
            2-> "低压模式"
            3-> "充满模式"
            else -> "未知状态"
        }

    private fun getBatteryStateDes(batteryData: BatteryData) =
        when (batteryData.state) {
            0-> "清醒状态"
            1-> "睡眠状态"
            else -> "未知状态"
        }


    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBattery.appendRedLargeText("⚠️[读取电池电量]指令写入失败！")
            }
            1 -> {
                ccvLanguage.appendRedLargeText("⚠️[设置语言]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBattery.appendBlueMiddleText("✅️[读取电池电量]指令写入成功！")
            }
            1 -> {
                ccvLanguage.appendBlueMiddleText("✅[设置语言]指令写入成功！")
            }
        }
    }

}