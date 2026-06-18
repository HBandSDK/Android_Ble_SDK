package com.timaimee.vpdemo.activity.v2.function_switch

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.adapter.CustomSettingSwitchAdapter
import com.timaimee.vpdemo.adapter.MessagePushSwitchAdapter
import com.timaimee.vpdemo.bean.CustomSettingItemBean
import com.timaimee.vpdemo.bean.NotificationFunction
import com.veepoo.protocol.listener.data.ICustomSettingDataListener
import com.veepoo.protocol.listener.data.ISocialMsgDataListener
import com.veepoo.protocol.model.datas.FunctionSocailMsgData
import com.veepoo.protocol.model.enums.EBloodFatUnit
import com.veepoo.protocol.model.enums.EBloodGlucoseUnit
import com.veepoo.protocol.model.enums.EGsrUnit
import com.veepoo.protocol.model.enums.ESocailMsg
import com.veepoo.protocol.model.enums.ETemperatureUnit
import com.veepoo.protocol.model.enums.EUricAcidUnit
import com.veepoo.protocol.model.settings.CustomSetting
import com.veepoo.protocol.model.settings.CustomSettingData
import java.util.ArrayList

class CustomSettingSwitchActivity : BaseVPBLETestActivity(), ICustomSettingDataListener, CustomSettingSwitchAdapter.OnCustomSettingOptListener {

    lateinit var rvMsgPush: RecyclerView
    lateinit var adapter: CustomSettingSwitchAdapter
    var functions: MutableList<CustomSettingItemBean> = mutableListOf()
    var customSettingData: CustomSettingData? = null

    override fun getLayoutID() = R.layout.activity_setting_list

    override fun pageTitle() = "功能开关&单位📏"

    override fun initView() {
    }

    override fun initData() {
        vpBleManager.readCustomSetting(defaultResponse, this)
    }

    override fun initEvent() {
        rvMsgPush = findViewById(R.id.rvSetting)
        rvMsgPush.layoutManager = LinearLayoutManager(this)
        adapter = CustomSettingSwitchAdapter(functions, this)
        rvMsgPush.adapter = adapter
    }

    private fun getCustomSettingFunctionList(data: CustomSettingData): MutableList<CustomSettingItemBean> {
        val functions: MutableList<CustomSettingItemBean> = ArrayList<CustomSettingItemBean>()

        functions.add(CustomSettingItemBean("公制", data.metricSystem))
        functions.add(CustomSettingItemBean("心率自动检测", data.autoHeartDetect))
        functions.add(CustomSettingItemBean("血压自动检测", data.autoBpDetect))
        functions.add(CustomSettingItemBean("运动过量提醒", data.sportOverRemain))
        functions.add(CustomSettingItemBean("血压/心率播报", data.voiceBpHeart))
        functions.add(CustomSettingItemBean("查找手机", data.findPhoneUi))
        functions.add(CustomSettingItemBean("秒表", data.secondsWatch))
        functions.add(CustomSettingItemBean("低氧报警", data.lowSpo2hRemain))
        functions.add(CustomSettingItemBean("肤色", data.skin).apply { checkedIndex = data.skinLevel })
        functions.add(CustomSettingItemBean("HRV自动检测", data.autoHrv))
        functions.add(CustomSettingItemBean("自动接听来电", data.autoIncall))
        functions.add(CustomSettingItemBean("断链提醒", data.disconnectRemind))
        functions.add(CustomSettingItemBean("科学睡眠", data.ppg))
        functions.add(CustomSettingItemBean("音乐控制", data.musicControl))
        functions.add(CustomSettingItemBean("长按锁屏", data.longClickLockScreen))
        functions.add(CustomSettingItemBean("消息亮屏", data.messageScreenLight))
        functions.add(CustomSettingItemBean("体温自动检测", data.autoTemperatureDetect))
        functions.add(CustomSettingItemBean("体温单位", data.temperatureUnit != ETemperatureUnit.NONE,
            if (data.temperatureUnit != ETemperatureUnit.CELSIUS) 0 else 1, "℃","℉"))
        functions.add(CustomSettingItemBean("ECG常开", data.ecgAlwaysOpen))
        functions.add(CustomSettingItemBean("血糖自动检测", data.bloodGlucoseDetection))
        functions.add(CustomSettingItemBean("血糖单位", data.bloodGlucoseUnit != EBloodGlucoseUnit.NONE,
            if (data.bloodGlucoseUnit != EBloodGlucoseUnit.mmol_L) 0 else 1, "mmol/L","mg/dl"))
        functions.add(CustomSettingItemBean("梅脱自动检测", data.metDetect))
        functions.add(CustomSettingItemBean("压力自动检测", data.stressDetect))
        functions.add(CustomSettingItemBean("血液成分自动检测", data.bloodComponentDetect))
        functions.add(CustomSettingItemBean("跌倒检测", data.fallDetection))
        functions.add(CustomSettingItemBean("尿酸单位", data.uricAcidUnit != EUricAcidUnit.NONE,
            if (data.uricAcidUnit != EUricAcidUnit.umol_L) 0 else 1, "μmol/L","mg/dl"))
        functions.add(CustomSettingItemBean("血脂单位", data.bloodFatUnit != EBloodFatUnit.NONE,
            if (data.bloodFatUnit != EBloodFatUnit.mmol_L) 0 else 1, "mmol/L","mg/dl"))

        functions.add(CustomSettingItemBean("皮电单位", data.gsrUnit != EGsrUnit.NONE,
            if (data.gsrUnit != EGsrUnit.ug_dL) 0 else 1, "μg/L","nmol/L"))

        return functions
    }

    override fun onToggleChanged(setting: CustomSettingItemBean) {
        // 根据条目名称，更新 customSettingData 对应字段
        when (setting.label) {
            "公制" -> {
                customSettingData?.metricSystem = setting.status
            }
            "心率自动检测" -> {
                customSettingData?.autoHeartDetect = setting.status
            }
            "血压自动检测" -> {
                customSettingData?.autoBpDetect = setting.status
            }
            "运动过量提醒" -> {
                customSettingData?.sportOverRemain = setting.status
            }
            "血压/心率播报" -> {
                customSettingData?.voiceBpHeart = setting.status
            }
            "查找手机" -> {
                customSettingData?.findPhoneUi = setting.status
            }
            "秒表" -> {
                customSettingData?.secondsWatch = setting.status
            }
            "低氧报警" -> {
                customSettingData?.lowSpo2hRemain = setting.status
            }
            "肤色" -> {
                customSettingData?.skin = setting.status
            }
            "HRV自动检测" -> {
                customSettingData?.autoHrv = setting.status
            }
            "自动接听来电" -> {
                customSettingData?.autoIncall = setting.status
            }
            "断链提醒" -> {
                customSettingData?.disconnectRemind = setting.status
            }
            "科学睡眠" -> {
                customSettingData?.ppg = setting.status
            }
            "音乐控制" -> {
                customSettingData?.musicControl = setting.status
            }
            "长按锁屏" -> {
                customSettingData?.longClickLockScreen = setting.status
            }
            "消息亮屏" -> {
                customSettingData?.messageScreenLight = setting.status
            }
            "体温自动检测" -> {
                customSettingData?.autoTemperatureDetect = setting.status
            }
            "ECG常开" -> {
                customSettingData?.ecgAlwaysOpen = setting.status
            }
            "血糖自动检测" -> {
                customSettingData?.bloodGlucoseDetection = setting.status
            }
            "梅脱自动检测" -> {
                customSettingData?.metDetect = setting.status
            }
            "压力自动检测" -> {
                customSettingData?.stressDetect = setting.status
            }
            "血液成分自动检测" -> {
                customSettingData?.bloodComponentDetect = setting.status
            }
            "跌倒检测" -> {
                customSettingData?.fallDetection = setting.status
            }
        }
        // 下发配置到设备
        customSettingData?.let {
            vpBleManager.changeCustomSetting(defaultResponse, this, CustomSetting(it))
        }
    }

    override fun onUnitChange(setting: CustomSettingItemBean) {
        // checkedIndex 0 / 1 对应两个单位
        val index = setting.checkedIndex
        when (setting.label) {
            "体温单位" -> {
                customSettingData?.temperatureUnit = if (index == 0) {
                    ETemperatureUnit.FAHRENHEIT
                } else {
                    ETemperatureUnit.CELSIUS
                }
            }
            "血糖单位" -> {
                customSettingData?.bloodGlucoseUnit = if (index == 0) {
                    EBloodGlucoseUnit.mg_dl
                } else {
                    EBloodGlucoseUnit.mmol_L
                }
            }
            "尿酸单位" -> {
                customSettingData?.uricAcidUnit = if (index == 0) {
                    EUricAcidUnit.mg_dl
                } else {
                    EUricAcidUnit.umol_L
                }
            }
            "血脂单位" -> {
                customSettingData?.bloodFatUnit = if (index == 0) {
                    EBloodFatUnit.mg_dl
                } else {
                    EBloodFatUnit.mmol_L
                }
            }
            "皮电单位" -> {
                customSettingData?.gsrUnit = if (index == 0) {
                    EGsrUnit.ug_dL
                } else {
                    EGsrUnit.nmol_L
                }
            }
        }
        // 下发配置到设备
        customSettingData?.let {
            vpBleManager.changeCustomSetting(defaultResponse, this, CustomSetting(it))
        }
    }

    override fun OnSettingDataChange(customSettingData: CustomSettingData) {
        this.customSettingData = customSettingData
        functions.clear()
        functions.addAll(getCustomSettingFunctionList(customSettingData))
        adapter.notifyDataSetChanged()
    }

}