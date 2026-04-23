package com.timaimee.vpdemo.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inuker.bluetooth.library.Code
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.adapter.TCMAdapter
import com.timaimee.vpdemo.adapter.TCMItem
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.ITCMDataListener
import com.veepoo.protocol.model.datas.TCMDataReport
import com.veepoo.protocol.model.enums.TCMType

class TCMActivity : AppCompatActivity(), ITCMDataListener {

    private lateinit var tcmAdapter: TCMAdapter
    private val tcmItems = TCMType.values().map { TCMItem(it) }

    // 假设你的 SDK 实例名为 mVpOperate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcm)

        // 初始化列表
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tcmAdapter = TCMAdapter(tcmItems)
        recyclerView.adapter = tcmAdapter

        // 读取按钮
        findViewById<Button>(R.id.btn_read).setOnClickListener {
            readTcmData()
        }

        // 发送按钮
        findViewById<Button>(R.id.btn_send).setOnClickListener {
            sendTcmData()
        }
    }

    /**
     * 发送选中的中医数据
     */
    private fun sendTcmData() {
        // 过滤选中的数据并转为 Map<TCMType, Int>
        val selectedData = tcmItems.filter { it.isSelected }
            .associate { it.type to it.value }

        if (selectedData.isEmpty()) {
            Toast.makeText(this, "请至少选择一项数据", Toast.LENGTH_SHORT).show()
            return
        }

        VPOperateManager.getInstance().setJE136PTCMCustomData({ code ->
            if (code == Code.REQUEST_SUCCESS) {
                Log.d("TCM", "指令发送成功")
            }
        }, selectedData, this)
    }

    /**
     * 读取设备中医数据
     */
    private fun readTcmData() {
        VPOperateManager.getInstance().readJE136PTCMCustomData({
            // 处理写入反馈
        }, this)
    }

    // --- ITCMDataListener 回调 ---

    override fun onTCMDataResponse(report: TCMDataReport) {
        runOnUiThread {
            Log.d("TCM", "收到设备报告，时间戳: ${report.timestamp}")
            // 根据返回的 report 更新 UI
            report.metrics.forEach { (type, value) ->
                val item = tcmItems.find { it.type == type }
                item?.let {
                    it.value = value
                    it.isSelected = true
                }
            }
            tcmAdapter.notifyDataSetChanged()
            Toast.makeText(this, "读取成功", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTCMWriteSuccess() {
        runOnUiThread {
            Toast.makeText(this, "设备已确认接收中医数据", Toast.LENGTH_SHORT).show()
        }
    }
}