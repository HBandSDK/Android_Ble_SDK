package com.timaimee.vpdemo.activity.v2.health

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.activity.v2.DeviceMenu
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.timaimee.vpdemo.utils.switch
import com.timaimee.vpdemo.utils.toDes
import com.veepoo.protocol.listener.data.ICameraDataListener
import com.veepoo.protocol.listener.data.ICheckWearDataListener
import com.veepoo.protocol.listener.data.IDeviceFunctionStatusChangeListener
import com.veepoo.protocol.listener.data.IFatigueDataListener
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener
import com.veepoo.protocol.listener.data.IFunSwitchListener
import com.veepoo.protocol.model.datas.FunSwitchFlags
import com.veepoo.protocol.model.enums.ECameraStatus
import com.veepoo.protocol.model.enums.ECheckWear
import com.veepoo.protocol.model.enums.EDeviceStatus
import com.veepoo.protocol.model.enums.EFindDeviceStatus
import com.veepoo.protocol.model.enums.EFunctionStatus
import com.veepoo.protocol.model.settings.CheckWearSetting

class FunctionTestActivity: BaseVPBLETestActivity() , ICameraDataListener {

    lateinit var ccvTest: CollapseCardLogView
    lateinit var btnStartDetect: Button
    lateinit var btnStopDetect: Button
    lateinit var tvFunName: TextView
    lateinit var rgIsOpen: RadioGroup
    lateinit var spFun: Spinner

    var functionName = ""
    companion object {

        const val FUNCTION_NAME = "-FUNCTION-"

        fun start(context: Context, functionName: String) {
            val intent = Intent(context, FunctionTestActivity::class.java)
            intent.putExtra(FUNCTION_NAME, functionName)
            context.startActivity(intent)
        }
    }

    override fun getLayoutID() = R.layout.activity_single_test

    override fun pageTitle() = functionName

    override fun initView() {
        ccvTest = findViewById(R.id.ccvTest)
        btnStartDetect = findViewById(R.id.btnStartDetect)
        btnStopDetect = findViewById(R.id.btnStopDetect)
        tvFunName = findViewById(R.id.tvFunName)
        rgIsOpen = findViewById(R.id.rgIsOpen)
        spFun = findViewById(R.id.spFun)
    }

    private fun setRadioGroupShow(isShow: Boolean){
        tvFunName.visibility = View.GONE
        rgIsOpen.visibility = View.GONE
    }

    private fun isOpen() : Boolean {
        return rgIsOpen.checkedRadioButtonId == R.id.rbOpen
    }

    override fun initData() {
        functionName = intent.getStringExtra(FUNCTION_NAME)?:"未知功能"
        ccvTest.setFunctionTitle("${functionName}测试")
        ccvTest.setFunctionEnabled(isFunctionEnable)
        initUIWithFunction()
    }

    private fun initSP(data: Array<String>, spinner: Spinner) {
        if (data == null || data.size == 0) return
        val adapter = ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
    }


    private fun initUIWithFunction() {
        setRadioGroupShow(false)
        when(functionName) {
            DeviceMenu.Health.Fatigue -> {
                btnStartDetect.text = "开始疲劳度检测"
                btnStopDetect.text = "停止疲劳度检测"
            }
            DeviceMenu.Other.PHOTOGRAPH -> {
                btnStartDetect.text = "进入📷拍照模式"
                btnStopDetect.text = "退出📷拍照模式"
            }
            DeviceMenu.Other.CHECK_WEAR -> {
                btnStartDetect.text = "开启佩戴检测"
                btnStopDetect.text = "关闭配到检测"
            }
            DeviceMenu.Health.SEDENTARY_REMIND -> {
                btnStartDetect.text = "开启佩戴检测"
                btnStopDetect.text = "关闭配到检测"
            }
            DeviceMenu.Other.DEVICE_ANTI_LOSS -> {
                btnStartDetect.text = "设置设备防丢"
                btnStopDetect.text = "读取设备防丢"
                setRadioGroupShow(true)
            }
            DeviceMenu.Switch.SWITCH_STATUS_LISTENER -> {
                btnStartDetect.text = "设置全局开关监听"
                btnStopDetect.text = "移除全局开关监听"
                ccvTest.setSubTitle("目前支持DeviceFunction枚举类型")
                setRadioGroupShow(false)
            }
            DeviceMenu.Switch.HEALTH_SUPPORT -> {
                btnStartDetect.text = "健康辅助设置"
                btnStopDetect.text = "健康辅助读取"
                setRadioGroupShow(false)
                rgIsOpen.visibility = View.GONE
                tvFunName.visibility = View.VISIBLE
                spFun.visibility = View.VISIBLE
                tvFunName.text = "辅助类型:"
                val funStringList: Array<String> = arrayOf(
                    "血糖",
                    "血压",
                    "血氧",
                    "体温",
                    "HRV",
                    "压力",
                    "梅脱",
                    "血液成分",
                    "身体成分",
                    "微体检",
                    "情绪",
                    "疲劳度",
                    "核辐射",
                    "跌倒提醒",
                    "AI问答",
                    "AI表盘",
                    "皮电测试"
                )
                initSP(funStringList, spFun)
            }
        }
    }

    override fun initEvent() {
        btnStartDetect.setOnClickListener {
            setWriteCmdTAG(0)
            ccvTest.clearTestInfo()
            ccvTest.appendBlueMiddleText(functionAction1)
            when(functionName) {
                DeviceMenu.Health.Fatigue -> vpBleManager.startDetectFatigue(defaultResponse, fatigueListener)
                DeviceMenu.Other.PHOTOGRAPH -> vpBleManager.startCamera(defaultResponse, cameraListener)
                DeviceMenu.Other.CHECK_WEAR -> vpBleManager.setttingCheckWear(defaultResponse, checkWearListener, CheckWearSetting().apply { isOpen = true })
                DeviceMenu.Other.DEVICE_ANTI_LOSS -> vpBleManager.settingFindDevice(defaultResponse, findDeviceListener, isOpen())
                DeviceMenu.Switch.HEALTH_SUPPORT -> vpBleManager.setFunSwitchState(defaultResponse, funSwitchListener, spFun.selectedItemPosition, isOpen().switch(
                    EFunctionStatus.SUPPORT_OPEN, EFunctionStatus.SUPPORT_CLOSE))
                DeviceMenu.Switch.SWITCH_STATUS_LISTENER -> vpBleManager.setDeviceFunctionStatusChangeListener(deviceFunctionStatusChange)

            }
        }
        btnStopDetect.setOnClickListener {
            setWriteCmdTAG(1)
            ccvTest.appendBlueMiddleText(functionAction2)
            when(functionName) {
                DeviceMenu.Health.Fatigue -> vpBleManager.stopDetectFatigue(defaultResponse, fatigueListener)
                DeviceMenu.Other.PHOTOGRAPH -> vpBleManager.stopCamera(defaultResponse, cameraListener)
                DeviceMenu.Other.CHECK_WEAR -> vpBleManager.setttingCheckWear(defaultResponse, checkWearListener, CheckWearSetting().apply { isOpen = false })
                DeviceMenu.Other.DEVICE_ANTI_LOSS -> vpBleManager.readFindDevice(defaultResponse, findDeviceListener)
                DeviceMenu.Switch.HEALTH_SUPPORT -> vpBleManager.readFunSwitchState(defaultResponse, funSwitchListener)
                DeviceMenu.Switch.SWITCH_STATUS_LISTENER -> vpBleManager.setDeviceFunctionStatusChangeListener(null)

            }
        }
    }

    /**
     * 疲劳度监听
     */
    private val fatigueListener = IFatigueDataListener { fatigueData ->
        when(fatigueData.deviceState) {
            EDeviceStatus.FREE -> {
                ccvTest.appendResult(">>> 测量中:进度=${fatigueData.progress}%, 状态=${fatigueData.deviceState}, 疲劳度=${fatigueData.value}")
            }

            EDeviceStatus.BUSY -> {
                ccvTest.appendRedLargeText("⚠️:设备正忙")
            }

            EDeviceStatus.DETECT_HEART,
            EDeviceStatus.DETECT_FTG,
            EDeviceStatus.DETECT_BP,
            EDeviceStatus.DETECT_PPG,
            EDeviceStatus.DETECT_AUTO_FIVE  -> {
                ccvTest.appendRedLargeText("⚠️:设备正在检测其他...")
            }

            EDeviceStatus.UNPASS_WEAR -> {
                ccvTest.appendRedLargeText("❌️:佩戴不通过")
            }

            EDeviceStatus.CHARG_LOW -> {
                ccvTest.appendRedLargeText("🚨:电池电量过低")
            }

            EDeviceStatus.CHARGING -> {
                ccvTest.appendRedLargeText("🚨:设备正在充电")
            }

            EDeviceStatus.FINISH,EDeviceStatus.KEEP_QUIT-> {
            }

            else -> {}
        }
    }

    /**
     * 拍照监听
     */
    private val cameraListener = ICameraDataListener { status ->
        when(status) {
            ECameraStatus.OPEN_SUCCESS -> ccvTest.appendResult("✅️进入拍照模式成功")
            ECameraStatus.DEVICE_OPEN_SUCCESS -> ccvTest.appendResult("✅设备进入拍照模式成功")
            ECameraStatus.OPEN_FALI -> ccvTest.appendRedLargeText("❌️进入拍照模式失败")
            ECameraStatus.TAKEPHOTO_CAN ->  ccvTest.appendResult("✅进行拍照")
            ECameraStatus.TAKEPHOTO_CAN_NOT ->  ccvTest.appendResult("⚠️不可以进行拍照")
            ECameraStatus.CLOSE_SUCCESS -> ccvTest.appendResult("✅️退出拍照模式成功")
            ECameraStatus.CLOSE_FAIL -> ccvTest.appendRedLargeText("❌️退出拍照模式失败")
            ECameraStatus.UNKONW -> ccvTest.appendRedLargeText("⚠️未知状态")
        }
    }

    /**
     * 佩戴检测监听
     */
    private val checkWearListener = ICheckWearDataListener { data ->
        when (data.checkWearState) {
            ECheckWear.OPEN_SUCCESS -> ccvTest.appendResult("✅️开启防丢成功")
            ECheckWear.OPEN_FAIL -> ccvTest.appendRedLargeText("❌️开启防丢失败")
            ECheckWear.CLOSE_SUCCESS -> ccvTest.appendResult("✅️关闭防丢成功")
            ECheckWear.CLOSE_FAIL ->  ccvTest.appendRedLargeText("❌️关闭防丢失败")
            ECheckWear.READ_SUCCESS -> ccvTest.appendResult("✅️读取防丢成功")
            ECheckWear.READ_FAIL -> ccvTest.appendRedLargeText("❌️读取防丢失败")
            ECheckWear.UNKONW -> ccvTest.appendRedLargeText("⚠️未知状态")
        }
    }

    /**
     * 佩戴检测监听
     */
    private val findDeviceListener = IFindDeviceDatalistener { data ->
        when (data.status) {
            EFindDeviceStatus.OPEN_SUCCESS -> ccvTest.appendResult("✅️开启佩戴检测成功")
            EFindDeviceStatus.OPEN_FAIL -> ccvTest.appendRedLargeText("❌️开启佩戴检测失败")
            EFindDeviceStatus.CLOSE_SUCCESS -> ccvTest.appendResult("✅️关闭佩戴检测成功")
            EFindDeviceStatus.CLOSE_FAIL ->  ccvTest.appendRedLargeText("❌️关闭佩戴检测失败")
            EFindDeviceStatus.READ_SUCCESS -> ccvTest.appendResult("✅️读取佩戴检测成功")
            EFindDeviceStatus.READ_FAIL -> ccvTest.appendRedLargeText("❌️读取佩戴检测失败")
            EFindDeviceStatus.UNKONW -> ccvTest.appendRedLargeText("⚠️未知状态")
        }
    }

    // 把映射表提到外部，只初始化一次
    private val funDescMap = mapOf(
        FunSwitchFlags.BLOOD_GLUCOSE to "血糖",
        FunSwitchFlags.BLOOD_PRESSURE to "血压",
        FunSwitchFlags.BLOOD_OXYGEN to "血氧",
        FunSwitchFlags.BODY_TEMPERATURE to "体温",
        FunSwitchFlags.HRV to "HRV",
        FunSwitchFlags.STRESS to "压力",
        FunSwitchFlags.MET to "梅脱",
        FunSwitchFlags.BLOOD_COMPONENT to "血液成分",
        FunSwitchFlags.BODY_COMPONENT to "身体成分",
        FunSwitchFlags.MICRO_PHYSICAL_EXAMINATION to "微体检",
        FunSwitchFlags.EMOTION to "情绪",
        FunSwitchFlags.FATIGUE to "疲劳度",
        FunSwitchFlags.NUCLEAR_RADIATION to "核辐射",
        FunSwitchFlags.FALLING_REMINDER to "跌倒提醒",
        FunSwitchFlags.FALLING_AI_CHAT to "AI问答",
        FunSwitchFlags.FALLING_AI_DIAL to "AI表盘",
        FunSwitchFlags.SKIN_ELECTRIC_TEST to "皮电测试"
    )

    /**
     * 健康辅助
     */
    private val funSwitchListener = IFunSwitchListener { conn, data ->
        val action = when (conn) {
            1 -> "设置"
            2 -> "读取"
            3 -> "上报"
            else -> "未知"
        }
        ccvTest.appendResult("✅️$action 成功")

        data.forEach { (key, value) ->
            // 直接根据 key 获取名称
            val name = funDescMap[key] ?: "未知"
            val status = value.name
            ccvTest.appendResult("$name -> $status")
        }
    }

    /**
     * 设备功能开关状态改变
     */
    private val deviceFunctionStatusChange = object : IDeviceFunctionStatusChangeListener {

        override fun onFunctionStatusChanged(
            function: IDeviceFunctionStatusChangeListener.DeviceFunction,
            status: EFunctionStatus
        ) {
            ccvTest.appendResult(">>> 开关状态改变: ${function.des} = ${status.toDes()}")
        }
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvTest.appendRedLargeText("⚠️[${functionAction1}]指令写入失败！")
            }
            1 -> {
                ccvTest.appendRedLargeText("⚠️[${functionAction2}]指令写入失败！")
            }
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when(cmdTag) {
            0 -> {
                ccvTest.appendBlueMiddleText("✅️[${functionAction1}]指令写入成功！")
            }
            1 -> {
                ccvTest.appendBlueMiddleText("✅[${functionAction2}]指令写入成功！")
            }
        }
    }

    override fun OnCameraDataChange(status: ECameraStatus) {
        when(status) {
            ECameraStatus.OPEN_SUCCESS -> ccvTest.appendResult("✅️进入拍照模式成功")
            ECameraStatus.DEVICE_OPEN_SUCCESS -> ccvTest.appendResult("✅设备进入拍照模式成功")
            ECameraStatus.OPEN_FALI -> ccvTest.appendRedLargeText("❌️进入拍照模式失败")
            ECameraStatus.TAKEPHOTO_CAN ->  ccvTest.appendResult("✅进行拍照")
            ECameraStatus.TAKEPHOTO_CAN_NOT ->  ccvTest.appendResult("⚠️不可以进行拍照")
            ECameraStatus.CLOSE_SUCCESS -> ccvTest.appendResult("✅️退出拍照模式成功")
            ECameraStatus.CLOSE_FAIL -> ccvTest.appendRedLargeText("❌️退出拍照模式失败")
            ECameraStatus.UNKONW -> ccvTest.appendRedLargeText("⚠️未知状态")
        }
    }

    val functionAction1: String = when(functionName) {
        DeviceMenu.Health.Fatigue -> "⏯️开始疲劳度测试..."
        DeviceMenu.Other.PHOTOGRAPH ->  "⏯️进入拍照模式..."
        DeviceMenu.Other.CHECK_WEAR ->  "⏯️开启佩戴检测.."
        DeviceMenu.Other.DEVICE_ANTI_LOSS ->  "⏯️${if(isOpen()) "开启" else "关闭"}设备防丢.."
        DeviceMenu.Switch.HEALTH_SUPPORT ->  "⏯️${if(isOpen()) "开启${spFun.selectedItem as String}" else "关闭${spFun.selectedItem as String}"}健康辅助.."
        else -> "开始${functionName}"
    }

    val functionAction2 : String
        get() =  when(functionName) {
            DeviceMenu.Health.Fatigue -> "🛑停止疲劳度测试..."
            DeviceMenu.Other.PHOTOGRAPH -> "🛑退出拍照模式..."
            DeviceMenu.Other.CHECK_WEAR -> "🛑关闭佩戴检测..."
            DeviceMenu.Other.DEVICE_ANTI_LOSS -> "读取设备防丢..."
            DeviceMenu.Switch.HEALTH_SUPPORT -> "读取健康辅助..."
            else -> "停止${functionName}"
        }

    val isFunctionEnable: Boolean
        get() = when(functionName) {
            DeviceMenu.Health.Fatigue -> fCheck.checkFtg()
            DeviceMenu.Other.PHOTOGRAPH -> fCheck.checkCamera()
            DeviceMenu.Other.DEVICE_ANTI_LOSS -> fCheck.checkFindDevice()
            DeviceMenu.Other.CHECK_WEAR -> fCheck.checkCheckWear()
            DeviceMenu.Switch.HEALTH_SUPPORT -> fCheck.checkHealthAssessment()
            else -> true
        }

}