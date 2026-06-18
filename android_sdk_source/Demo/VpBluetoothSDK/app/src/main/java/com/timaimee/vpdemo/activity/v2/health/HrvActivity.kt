package com.timaimee.vpdemo.activity.v2.health

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.bean.MyDeviceInfo
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IBodyComponentReadDataListener
import com.veepoo.protocol.listener.data.IBodyComponentReadIdListener
import com.veepoo.protocol.listener.data.IHRVOriginDataListener
import com.veepoo.protocol.listener.data.IHrvDetectListener
import com.veepoo.protocol.model.datas.BodyComponent
import com.veepoo.protocol.model.datas.HRVOriginData
import com.veepoo.protocol.model.enums.HrvDetectState
import com.veepoo.protocol.model.settings.ReadOriginSetting

class HrvActivity : BaseVPBLETestActivity(), IHrvDetectListener {

    lateinit var ccvHrvDetect: CollapseCardLogView
    lateinit var ccvHrvRead: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    /* 自定义读取 */
    lateinit var spCustomReadFromDay: Spinner
    lateinit var spCustomReadFromPosition: Spinner
    lateinit var rgIsReadOneDay: RadioGroup
    lateinit var btnCustomRead: Button
    private var watchDay = 3

    override fun getLayoutID() = R.layout.activity_hrv

    override fun pageTitle() = "HRV"

    override fun initView() {
        ccvHrvDetect = findViewById(R.id.ccvHrvDetect)
        ccvHrvRead = findViewById(R.id.ccvHrvRead)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        spCustomReadFromDay = findViewById(R.id.spCustomReadFromDay)
        spCustomReadFromPosition = findViewById(R.id.spCustomReadFromPosition)
        rgIsReadOneDay = findViewById(R.id.rgIsReadOneDay)
        btnCustomRead = findViewById(R.id.btnCustomRead)
    }

    override fun initData() {
        watchDay = MyDeviceInfo.watchDataDay
        setCollapseExpandReciprocity(ccvHrvDetect, ccvHrvRead)
        val witchDays = arrayOf<String?>("今天", "昨天", "前天")
        initSP(witchDays, spCustomReadFromDay)
        initPositionSP(spCustomReadFromPosition)
        rgIsReadOneDay.check(R.id.rbNo)
        ccvHrvDetect.setFunctionEnabled(fCheck.checkHrvAppDetect())
    }

    private fun initPositionSP(spinner: Spinner) {
        val positions = arrayOfNulls<String>(288)
        for (i in 1..288) {
            positions[i - 1] = "$i   "
        }
        initSP(positions, spinner)
    }

    private fun initSP(data: Array<String?>?, spinner: Spinner) {
        if (data == null || data.size == 0) return
        val adapter = ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
    }

    override fun initEvent() {
        btnStartDetect.setOnClickListener {
            setWriteCmdTAG(0)
            ccvHrvDetect.clearTestInfo()
            ccvHrvDetect.appendBlueMiddleText("▶ 开始测量HRV...")
            vpBleManager.startDetectHrv(defaultResponse, this)
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvHrvDetect.appendBlueMiddleText("⏹️ 停止测量HRV...")
            vpBleManager.stopDetectHrv(defaultResponse, this)
        }
        btnCustomRead.setOnClickListener {
            setWriteCmdTAG(2)
            ccvHrvRead.appendBlueMiddleText("▶ 开始读取HRV...")
            val startReadDay = spCustomReadFromDay.getSelectedItemPosition()
            val position = spCustomReadFromPosition.getSelectedItemPosition() + 1
            val isReadOnlyOneDay = rgIsReadOneDay.getCheckedRadioButtonId() == R.id.rbYes
            val setting = ReadOriginSetting(startReadDay, position, isReadOnlyOneDay, watchDay)
            vpBleManager.readHRVOriginBySetting(defaultResponse,object : IHRVOriginDataListener{
                override fun onReadOriginProgress(progress: Float) {
                    ccvHrvRead.appendResult("读取进度>> ${(progress*100).toInt()}%")
                }

                override fun onReadOriginProgressDetail(day: Int, date: String?, allPackage: Int, currentPackage: Int) {
                    ccvHrvRead.appendResult("▶ ${date} : [$currentPackage/$allPackage]")
                }

                override fun onHRVOriginListener(hrvOriginData: HRVOriginData?) {
                    ccvHrvRead.appendBlueMiddleText("▶ :hrv结果>> $hrvOriginData")
                }

                override fun onDayHrvScore(day: Int, date: String?, hrvSocre: Int) {
                    ccvHrvRead.appendBlueMiddleText("▶ :HRV评分>> $date -> 得分=$hrvSocre")
                }

                override fun onReadOriginComplete() {
                    ccvHrvRead.appendBlueMiddleText("🔚:HRV读取已完成")
                }

            }, setting)

        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvHrvDetect.appendRedLargeText("⚠️[开始测量HRV]指令写入失败！")
            }
            1 -> {
                ccvHrvDetect.appendRedLargeText("⚠️[停止测量HRV]指令写入失败！")
            }
            2 -> {
                ccvHrvRead.appendRedLargeText("⚠️[读取HRV]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvHrvDetect.appendBlueMiddleText("✅️[开始测量HRV]指令写入成功！")
            }
            1 -> {
                ccvHrvDetect.appendBlueMiddleText("✅[停止测量HRV]指令写入成功！")
            }
            2 -> {
                ccvHrvRead.appendBlueMiddleText("✅[读取HRV]指令写入成功！")
            }
        }
    }

    override fun onHrvDetect(hrv: Int) {
        ccvHrvDetect.appendResult("测量中>>hrv: $hrv")
    }

    override fun onDetectFailed(detectState: HrvDetectState) {
        ccvHrvDetect.appendRedLargeText("⚠️测量失败: ${detectState.des}")
    }

    override fun onDetectStop() {
        ccvHrvDetect.appendBlueMiddleText("⏹️: ️HRV测量已停止")
    }

}