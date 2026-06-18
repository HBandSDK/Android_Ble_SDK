package com.timaimee.vpdemo.activity.v2.health

import android.widget.Button
import android.widget.EditText
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IBodyComponentDetectListener
import com.veepoo.protocol.listener.data.IBodyComponentReadDataListener
import com.veepoo.protocol.listener.data.IBodyComponentReadIdListener
import com.veepoo.protocol.model.datas.BodyComponent
import com.veepoo.protocol.model.enums.DetectState

class BodyComponentActivity : BaseVPBLETestActivity() {

    lateinit var ccvBodyComponentDetect: CollapseCardLogView
    lateinit var ccvBodyComponentRead: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    lateinit var btnReadAll: Button
    lateinit var btnReadID: Button
    lateinit var btnReadByID: Button
    lateinit var etId: EditText

    override fun getLayoutID() = R.layout.activity_body_component

    override fun pageTitle() = "身体成分"

    override fun initView() {
        ccvBodyComponentDetect = findViewById(R.id.ccvBodyComponentDetect)
        ccvBodyComponentRead = findViewById(R.id.ccvBodyComponentRead)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        btnReadAll = findViewById(R.id.btnReadAll)
        btnReadID = findViewById(R.id.btnReadID)
        btnReadByID = findViewById(R.id.btnReadByID)
        etId = findViewById(R.id.etId)
    }

    override fun initData() {
        setCollapseExpandReciprocity(ccvBodyComponentDetect, ccvBodyComponentRead)
        ccvBodyComponentDetect.setFunctionEnabled(fCheck.checkBodyComponent())
        ccvBodyComponentRead.setFunctionEnabled(fCheck.checkBodyComponent())
    }

    override fun initEvent() {
        btnStartDetect.setOnClickListener {
            setWriteCmdTAG(0)
            ccvBodyComponentDetect.clearTestInfo()
            ccvBodyComponentDetect.appendBlueMiddleText("▶ 开始测量身体成分...")
            vpBleManager.startDetectBodyComponent(defaultResponse,object : IBodyComponentDetectListener {
                override fun onDetecting(progress: Int, leadState: Int) {
                    val leadInfo = if(leadState == 0) "通过" else "脱落"
                    ccvBodyComponentDetect.appendResult(">>进度: $progress% | 导联状态=$leadInfo")
                }

                override fun onDetectSuccess(bodyComponent: BodyComponent) {
                    ccvBodyComponentDetect.appendOrangeText("测量成功:$bodyComponent" )
                }

                override fun onDetectFailed(detectState: DetectState) {
                    ccvBodyComponentDetect.appendRedLargeText("⚠️测量失败: ${detectState.des}")
                }

                override fun onDetectStop() {
                    ccvBodyComponentDetect.appendBlueMiddleText("⏹️: ️身体成分测量已停止")
                }

            })
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvBodyComponentDetect.appendBlueMiddleText("⏹️ 停止测量身体成分...")
            vpBleManager.stopDetectBodyComponent(defaultResponse)
        }
        btnReadAll.setOnClickListener {
            setWriteCmdTAG(2)
            ccvBodyComponentRead.appendBlueMiddleText("▶ 开始读取身体成分...")
            vpBleManager.readBodyComponentData(defaultResponse, object :IBodyComponentReadDataListener{
                override fun readBodyComponentDataFinish(bodyComponentList: List<BodyComponent>?) {
                    bodyComponentList?.let {
                        if (it.isEmpty()) {
                            ccvBodyComponentRead.appendRedLargeText("🚨暂无身体成分数据")
                        } else {
                            it.forEach {
                                ccvBodyComponentRead.appendResult(">>> $it")
                                ccvBodyComponentRead.appendRedLargeText("-----------------")
                            }
                        }
                    }
                }

            })
        }
        btnReadID.setOnClickListener {
            setWriteCmdTAG(3)
            ccvBodyComponentRead.appendBlueMiddleText("▶ 开始读取身体成分ID...")
            vpBleManager.readBodyComponentId(defaultResponse, object: IBodyComponentReadIdListener{
                override fun readIdFinish(ids: ArrayList<Int>) {
                    if (ids.isEmpty()) {
                        ccvBodyComponentRead.appendRedLargeText("🚨暂无身体成分数据")
                    } else {
                        ccvBodyComponentRead.appendResult("id列表：${ids.toIntArray().contentToString()}" )
                    }
                }
            })
        }
        btnReadByID.setOnClickListener {
            setWriteCmdTAG(4)
            val idContent = etId.text.toString()
            if (idContent.isEmpty()) {
                showToast("请输入id")
            } else {
                val id = idContent.toInt()
                ccvBodyComponentRead.appendBlueMiddleText("▶ 开始通过ID[$id]读取身体成分...")
                vpBleManager.readBodyComponentData(defaultResponse, object : IBodyComponentReadDataListener{
                    override fun readBodyComponentDataFinish(bodyComponentList: List<BodyComponent>?) {
                        bodyComponentList?.let {
                            if (it.isEmpty()) {
                                ccvBodyComponentRead.appendRedLargeText("🚨暂无身体成分数据")
                            } else {
                                it.forEach {
                                    ccvBodyComponentRead.appendResult(">>> $it")
                                    ccvBodyComponentRead.appendRedLargeText("-----------------")
                                }
                            }
                        }
                    }
                },arrayListOf<Int>(id))
            }
        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBodyComponentDetect.appendRedLargeText("⚠️[开始测量]身体成分指令写入失败！")
            }
            1 -> {
                ccvBodyComponentDetect.appendRedLargeText("⚠️[停止测量]身体成分指令写入失败！")
            }
            2 -> {
                ccvBodyComponentRead.appendRedLargeText("⚠️[读取身体成分]指令写入失败！")
            }
            3 -> {
                ccvBodyComponentRead.appendRedLargeText("⚠️[读取身体成分ID]指令写入失败！")
            }
            4 -> {
                ccvBodyComponentRead.appendRedLargeText("⚠️[通过ID读取身体成分]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvBodyComponentDetect.appendBlueMiddleText("✅️[开始测量身体成分]指令写入成功！")
            }
            1 -> {
                ccvBodyComponentDetect.appendBlueMiddleText("✅[停止测量身体成分]指令写入成功！")
            }
            2 -> {
                ccvBodyComponentRead.appendBlueMiddleText("✅[读取身体成分]指令写入成功！")
            }
            3 -> {
                ccvBodyComponentRead.appendBlueMiddleText("✅[读取身体成分ID]指令写入成功！")
            }
            4 -> {
                ccvBodyComponentRead.appendBlueMiddleText("✅[通过ID读取身体成分]指令写入成功！")
            }
        }
    }

}