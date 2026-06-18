package com.timaimee.vpdemo.activity.v2.health

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import com.orhanobut.logger.Logger
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.bean.MyDeviceInfo
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.ITemptureDataListener
import com.veepoo.protocol.listener.data.ITemptureDetectDataListener
import com.veepoo.protocol.model.datas.TemptureData
import com.veepoo.protocol.model.datas.TemptureDetectData
import com.veepoo.protocol.model.settings.ReadOriginSetting

class BodyTemperatureActivity : BaseVPBLETestActivity(), ITemptureDetectDataListener {

    lateinit var ccvBodyTemperatureDetect: CollapseCardLogView
    lateinit var ccvBodyTemperatureRead: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    /* 自定义读取 */
    lateinit var spCustomReadFromDay: Spinner
    lateinit var spCustomReadFromPosition: Spinner
    lateinit var rgIsReadOneDay: RadioGroup
    lateinit var btnCustomRead: Button
    private var watchDay = 3

    override fun getLayoutID() = R.layout.activity_body_temperature

    override fun pageTitle() = "体温🌡️"

    override fun initView() {
        ccvBodyTemperatureDetect = findViewById(R.id.ccvBodyTemperatureDetect)
        ccvBodyTemperatureRead = findViewById(R.id.ccvBodyTemperatureRead)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        spCustomReadFromDay = findViewById(R.id.spCustomReadFromDay)
        spCustomReadFromPosition = findViewById(R.id.spCustomReadFromPosition)
        rgIsReadOneDay = findViewById(R.id.rgIsReadOneDay)
        btnCustomRead = findViewById(R.id.btnCustomRead)
    }

    override fun initData() {
        watchDay = MyDeviceInfo.watchDataDay
        setCollapseExpandReciprocity(ccvBodyTemperatureDetect, ccvBodyTemperatureRead)
        val witchDays = arrayOf<String?>("今天", "昨天", "前天")
        initSP(witchDays, spCustomReadFromDay)
        initPositionSP(spCustomReadFromPosition)
        rgIsReadOneDay.check(R.id.rbNo)
        ccvBodyTemperatureDetect.setFunctionEnabled(fCheck.checkTemptureByApp())
        ccvBodyTemperatureDetect.setFunctionEnabled(fCheck.isSupportReadTemperature)
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
            ccvBodyTemperatureDetect.clearTestInfo()
            ccvBodyTemperatureDetect.appendBlueMiddleText("▶ 开始测量体温...")
            vpBleManager.startDetectTempture(defaultResponse, this)
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvBodyTemperatureDetect.appendBlueMiddleText("⏹️ 停止测量体温.....")
            vpBleManager.stopDetectTempture(defaultResponse, this)
        }
        btnCustomRead.setOnClickListener {
            setWriteCmdTAG(2)
            ccvBodyTemperatureRead.appendBlueMiddleText("▶ 开始读取体温.....")
            val startReadDay = spCustomReadFromDay.getSelectedItemPosition()
            val position = spCustomReadFromPosition.getSelectedItemPosition() + 1
            val isReadOnlyOneDay = rgIsReadOneDay.getCheckedRadioButtonId() == R.id.rbYes
            val setting = ReadOriginSetting(startReadDay, position, isReadOnlyOneDay, watchDay)
            vpBleManager.readTemptureDataBySetting(defaultResponse,object : ITemptureDataListener{
                override fun onReadOriginProgress(progress: Float) {
                    ccvBodyTemperatureRead.appendResult("读取进度>> ${(progress*100).toInt()}%")
                }

                override fun onReadOriginProgressDetail(day: Int, date: String?, allPackage: Int, currentPackage: Int) {
                    ccvBodyTemperatureRead.appendResult("▶ ${date} : [$currentPackage/$allPackage]")
                }

                override fun onReadOriginComplete() {
                    ccvBodyTemperatureRead.appendBlueMiddleText("🔚:BodyTemperature读取已完成")
                }

                override fun onTemptureDataListDataChange(temptureDataList: List<TemptureData>) {
                    if(temptureDataList.isEmpty()) {
                        ccvBodyTemperatureRead.appendRedLargeText("🚨暂无体温数据读取")
                    } else {
                        temptureDataList.forEach {
                            ccvBodyTemperatureRead.appendResult(temptureData2Str(it))
                        }
                    }
                }

            }, setting)

        }
    }

    private fun temptureData2Str(data: TemptureData): String {
        return "✅️>>> [${data.packageNumber}/${data.allPackage}] : ${data.getmTime().dateAndClock4GBandDb} 体温值=${data.tempture}℃, 皮肤温度=${data.baseTempture}℃  是否手动=${data.isFromHandler}"
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBodyTemperatureDetect.appendRedLargeText("⚠️[开始测量BodyTemperature]指令写入失败！")
            }
            1 -> {
                ccvBodyTemperatureDetect.appendRedLargeText("⚠️[停止测量BodyTemperature]指令写入失败！")
            }
            2 -> {
                ccvBodyTemperatureRead.appendRedLargeText("⚠️[读取BodyTemperature]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBodyTemperatureDetect.appendBlueMiddleText("✅️[开始测量BodyTemperature]指令写入成功！")
            }
            1 -> {
                ccvBodyTemperatureDetect.appendBlueMiddleText("✅[停止测量BodyTemperature]指令写入成功！")
            }
            2 -> {
                ccvBodyTemperatureRead.appendBlueMiddleText("✅[读取BodyTemperature]指令写入成功！")
            }
        }
    }

    override fun onDataChange(detectData: TemptureDetectData) {
        //| oprate       | Int   | 0x00 不支持此功能，0x01 开启，0x02关闭                          |
        //| ------------ | ----- | ------------------------------------------------------------ |
        //| deviceState  | Int   | 0x00 可用，0x01-0x07 设备正忙，0x08 设备低电，0x09 传感器异常     |
        //| progress     | Int   | 读取进度                                                      |
        //| tempture     | Float | 体温值                                                        |
        //| temptureBase | Float | 体温原始值,基准值                                               |
        Logger.t("-体温-").e("-TemptureDetectData-: | 测量值=" + detectData)
        val opt = detectData.getOprate()
        val state = detectData.getDeviceState()
        val progress = detectData.getProgress()
        if (opt == 0) {
            ccvBodyTemperatureDetect.appendRedLargeText("⚠️暂不支持该功能")
        } else if (opt == 2) {
            ccvBodyTemperatureDetect.appendResult("✅测量已停止")
        } else {
            if (progress == 100) {
                ccvBodyTemperatureDetect.appendResult("✅测量完成")
            } else {
                ccvBodyTemperatureDetect.appendResult(">>> 测量中：${progress}%")
            }
        }

        if ((state == 0 || state == 7) && opt != 2) {
            ccvBodyTemperatureDetect.appendResult("✅体温值=${detectData.tempture}℃, 皮肤温度=${detectData.temptureBase}℃")
        } else if (state in 1..<7) {
            ccvBodyTemperatureDetect.appendRedLargeText("⚠️异常（设备正忙）：已停止测量")
        } else if (state == 8) {
            ccvBodyTemperatureDetect.appendRedLargeText("⚠️异常（设备低电）：已停止测量")
        } else if (state == 9) {
            ccvBodyTemperatureDetect.appendRedLargeText("⚠️异常（传感器异常）：已停止测量")
        }
    }


}