package com.timaimee.vpdemo.activity.v2.other

import android.widget.Button
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.ICameraDataListener
import com.veepoo.protocol.model.enums.ECameraStatus

class PhotographActivity: BaseVPBLETestActivity() , ICameraDataListener{

    lateinit var ccvPhotograph: CollapseCardLogView
    lateinit var btnEnterPhotograph: Button
    lateinit var btnExitPhotograph: Button

    override fun getLayoutID() = R.layout.activity_photograph

    override fun pageTitle() = "拍照"

    override fun initView() {
        ccvPhotograph = findViewById(R.id.ccvPhotograph)
        btnEnterPhotograph = findViewById(R.id.btnEnterPhotograph)
        btnExitPhotograph = findViewById(R.id.btnExitPhotograph)
    }

    override fun initData() {
        ccvPhotograph.setFunctionEnabled(fCheck.checkCamera())
    }

    override fun initEvent() {
        btnEnterPhotograph.setOnClickListener {
            setWriteCmdTAG(0)
            ccvPhotograph.clearTestInfo()
            ccvPhotograph.appendBlueMiddleText("▶ 进入拍照模式...")
            vpBleManager.startCamera(defaultResponse,this)
        }
        btnExitPhotograph.setOnClickListener {
            setWriteCmdTAG(1)
            ccvPhotograph.appendBlueMiddleText("👋退出拍照模式...")
            vpBleManager.stopCamera(defaultResponse, this)
        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvPhotograph.appendRedLargeText("⚠️[进入拍照模式]指令写入失败！")
            }
            1 -> {
                ccvPhotograph.appendRedLargeText("⚠️[退出拍照模式]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvPhotograph.appendBlueMiddleText("✅️[进入拍照模式]指令写入成功！")
            }
            1 -> {
                ccvPhotograph.appendBlueMiddleText("✅[退出拍照模式]指令写入成功！")
            }
        }
    }

    override fun OnCameraDataChange(status: ECameraStatus) {
        when(status) {
            ECameraStatus.OPEN_SUCCESS -> ccvPhotograph.appendResult("✅️进入拍照模式成功")
            ECameraStatus.DEVICE_OPEN_SUCCESS -> ccvPhotograph.appendResult("✅设备进入拍照模式成功")
            ECameraStatus.OPEN_FALI -> ccvPhotograph.appendRedLargeText("❌️进入拍照模式失败")
            ECameraStatus.TAKEPHOTO_CAN ->  ccvPhotograph.appendResult("✅进行拍照")
            ECameraStatus.TAKEPHOTO_CAN_NOT ->  ccvPhotograph.appendResult("⚠️不可以进行拍照")
            ECameraStatus.CLOSE_SUCCESS -> ccvPhotograph.appendResult("✅️退出拍照模式成功")
            ECameraStatus.CLOSE_FAIL -> ccvPhotograph.appendRedLargeText("❌️退出拍照模式失败")
            ECameraStatus.UNKONW -> ccvPhotograph.appendRedLargeText("⚠️未知状态")
        }
    }
}