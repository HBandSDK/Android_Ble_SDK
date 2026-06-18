package com.timaimee.vpdemo.activity.v2.health

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import com.orhanobut.logger.Logger
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.bean.MyDeviceInfo
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IBPDetectDataListener
import com.veepoo.protocol.listener.data.IBPSettingDataListener
import com.veepoo.protocol.model.datas.BpData
import com.veepoo.protocol.model.datas.BpSettingData
import com.veepoo.protocol.model.enums.EBPDetectModel
import com.veepoo.protocol.model.settings.BpSetting

class BloodPressActivity : BaseVPBLETestActivity() {

    companion object {
        const val TAG = "血压"
    }

    lateinit var ccvBloodPressDetect: CollapseCardLogView
    lateinit var ccvBloodPressTestModeSetting: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    lateinit var rgPrivateModel: RadioGroup
    lateinit var rgDynamicCalibration: RadioGroup
    lateinit var rgTestMode: RadioGroup
    lateinit var btnBPPMSetting: Button
    lateinit var etLow: EditText
    lateinit var etHigh: EditText

    private var watchDay = 3

    override fun getLayoutID() = R.layout.activity_blood_press

    override fun pageTitle() = "血压"

    override fun initView() {
        ccvBloodPressDetect = findViewById(R.id.ccvBloodPressDetect)
        ccvBloodPressTestModeSetting = findViewById(R.id.ccvBloodPressTestModeSetting)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        btnBPPMSetting = findViewById(R.id.btnBPPMSetting)
        rgPrivateModel = findViewById(R.id.rgPrivateModel)
        rgDynamicCalibration = findViewById(R.id.rgDynamicCalibration)
        rgTestMode = findViewById(R.id.rgTestMode)
        etLow = findViewById(R.id.etLow)
        etHigh = findViewById(R.id.etHigh)
    }

    override fun initData() {
        watchDay = MyDeviceInfo.watchDataDay
        rgPrivateModel.check(R.id.rbPMOpen)
        rgDynamicCalibration.check(R.id.rbDCOpen)
        rgTestMode.check(R.id.rbModeNormal)
        setCollapseExpandReciprocity(ccvBloodPressDetect, ccvBloodPressTestModeSetting)
        ccvBloodPressDetect.setFunctionEnabled(fCheck.checkBp())
        ccvBloodPressTestModeSetting.setFunctionEnabled(fCheck.checkBp())
    }

    override fun initEvent() {
        btnStartDetect.setOnClickListener {
            setWriteCmdTAG(0)
            ccvBloodPressDetect.clearTestInfo()
            ccvBloodPressDetect.appendBlueMiddleText("▶ 开始测量血压...")
            vpBleManager.startDetectBP(defaultResponse, object : IBPDetectDataListener {
                override fun onDataChange(bpData: BpData) {
                    Logger.t(TAG).e("-onDataChange-: | $bpData")
                    val testInfo = ">>> 测量进度：${bpData.progress}%, 状态=${bpData.status} -> ${bpData.lowPressure}/${bpData.highPressure}"
                    ccvBloodPressDetect.appendResult(testInfo)
                }

            }, if(rgTestMode.checkedRadioButtonId == R.id.rbModeNormal) EBPDetectModel.DETECT_MODEL_PUBLIC else EBPDetectModel.DETECT_MODEL_PRIVATE)
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvBloodPressDetect.appendBlueMiddleText("⏹️ 停止测量血压...")
            vpBleManager.stopDetectBP(defaultResponse, if(rgTestMode.checkedRadioButtonId == R.id.rbModeNormal) EBPDetectModel.DETECT_MODEL_PUBLIC else EBPDetectModel.DETECT_MODEL_PRIVATE)
        }
        btnBPPMSetting.setOnClickListener {
            setWriteCmdTAG(2)
            val lowStr = etLow.text.toString()
            val highStr = etHigh.text.toString()
            if (lowStr.isEmpty()) {
                showToast("低压不能为空")
                return@setOnClickListener
            }
            if (highStr.isEmpty()) {
                showToast("高压不能为空")
                return@setOnClickListener
            }
            val low = lowStr.toInt()
            val high = highStr.toInt()
            if (low>=high) {
                showToast("高压不能小于等于低压")
                return@setOnClickListener
            }
            ccvBloodPressTestModeSetting.appendBlueMiddleText("▶ 开始设置血压测量模式...")
            val isDCOpen = rgDynamicCalibration.checkedRadioButtonId == R.id.rbDCOpen
            val isPMOpen = rgPrivateModel.checkedRadioButtonId == R.id.rbPMOpen
            val setting = BpSetting(isPMOpen, high, low)
            setting.setAngioAdjuste(isDCOpen)
            vpBleManager.settingDetectBP(defaultResponse,object : IBPSettingDataListener{
                override fun onDataChange(bpSettingData: BpSettingData?) {
                    ccvBloodPressTestModeSetting.appendBlueMiddleText("🔚:血压读取已完成")
                }

            }, setting)

        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBloodPressDetect.appendRedLargeText("⚠️[开始测量血压]指令写入失败！")
            }
            1 -> {
                ccvBloodPressDetect.appendRedLargeText("⚠️[停止测量血压]指令写入失败！")
            }
            2 -> {
                ccvBloodPressTestModeSetting.appendRedLargeText("⚠️[读取血压]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBloodPressDetect.appendBlueMiddleText("✅️[开始测量血压]指令写入成功！")
            }
            1 -> {
                ccvBloodPressDetect.appendBlueMiddleText("✅[停止测量血压]指令写入成功！")
            }
            2 -> {
                ccvBloodPressTestModeSetting.appendBlueMiddleText("✅[读取血压]指令写入成功！")
            }
        }
    }

}