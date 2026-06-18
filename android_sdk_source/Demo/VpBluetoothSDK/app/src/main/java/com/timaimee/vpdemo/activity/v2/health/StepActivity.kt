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
import com.veepoo.protocol.listener.data.ISportDataListener
import com.veepoo.protocol.model.datas.BpData
import com.veepoo.protocol.model.datas.BpSettingData
import com.veepoo.protocol.model.datas.SportData
import com.veepoo.protocol.model.enums.EBPDetectModel
import com.veepoo.protocol.model.settings.BpSetting

class StepActivity : BaseVPBLETestActivity() {

    companion object {
        const val TAG = "计步"
    }

    lateinit var ccvReadStep: CollapseCardLogView
    lateinit var btnReadStep: Button
    private var watchDay = 3

    override fun getLayoutID() = R.layout.activity_step

    override fun pageTitle() = "血压"

    override fun initView() {
        ccvReadStep = findViewById(R.id.ccvReadStep)
        btnReadStep = findViewById(R.id.btnReadStep)
    }

    override fun initData() {
        watchDay = MyDeviceInfo.watchDataDay
    }

    override fun initEvent() {
        btnReadStep.setOnClickListener {
            setWriteCmdTAG(0)
            ccvReadStep.clearTestInfo()
            ccvReadStep.appendBlueMiddleText("▶ 开始读取计步...")
            vpBleManager.readSportStep(defaultResponse) { data ->
                Logger.t(TAG).e("-onSportDataChange-: | $data")
                val testInfo =
                    ">>> 步数=${data.step}, 距离=${data.dis}km,卡路里=${data.kcal}kcal,[X:Y:Z]=[${data.triaxialX}:${data.triaxialY}:${data.triaxialZ}]"
                ccvReadStep.appendResult(testInfo)
            }
        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvReadStep.appendRedLargeText("⚠️[读取计步]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvReadStep.appendBlueMiddleText("✅️[读取计步]指令写入成功！")
            }
        }
    }

}