package com.timaimee.vpdemo.activity.v2.other

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.inuker.bluetooth.library.Code
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseActivity
import com.veepoo.protocol.model.datas.QH15SecurityProtectionModel
import com.veepoo.protocol.model.enums.EQH15SecurityProtectionState
import com.veepoo.protocol.model.enums.EQH15SetProtectionFuncAckState
import com.veepoo.protocol.model.enums.EQH15SPType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * QH15亲情守护示例页
 */
class GNSSOptActivity : BaseActivity() {

    companion object {
        private const val REQ_LOCATION_PERMISSION = 0x21
        /** 设置守护开关下发后无监听回调的超时兜底 */
        private const val RESP_TIMEOUT_MS = 10_000L
        /** 单次定位超时 */
        private const val LOCATION_TIMEOUT_MS = 8_000L
    }

    private lateinit var tvDeviceState: TextView
    private lateinit var tvGuardState: TextView
    private lateinit var tvCoordinate: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    private val mainHandler = Handler(Looper.getMainLooper())
    private var pendingTimeout: Runnable? = null
    private var activeLocationListener: LocationListener? = null

    /** 守护开启时间(手机端开启成功时记录，用于状态展示) */
    private var guardStartTime: Long? = null

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun getLayoutID(): Int = R.layout.activity_gnss_opt

    override fun pageTitle(): String = "QH15亲情守护"

    /** 复用 BaseActivity 的通用日志UI(svTestInfo/tvTestInfo + 自动滚底) */
    override fun hasCommonMsgUI(): Boolean = true

    override fun initView() {
        tvDeviceState = findViewById(R.id.tvDeviceState)
        tvGuardState = findViewById(R.id.tvGuardState)
        tvCoordinate = findViewById(R.id.tvCoordinate)
        btnStart = findViewById(R.id.btnStartSafetyProtection)
        btnStop = findViewById(R.id.btnStopSafetyProtection)
    }

    override fun initData() {
        // 全局监听注册(进程内仅一次)，注册逻辑不放在页面生命周期回调里反复执行
        SafetyGuardManager.ensureRegistered()
        // 接收全局事件(统一已切主线程)
        SafetyGuardManager.uiCallback = { event -> handleEvent(event) }
        refreshStateUI()
        appendResult("页面初始化完成，QH15亲情守护监听已就绪(进程内仅注册一次)")
    }

    override fun initEvent() {
        btnStart.setOnClickListener {
            if (!checkConnected()) return@setOnClickListener
            startGuardFlow()
        }
        btnStop.setOnClickListener {
            if (!checkConnected()) return@setOnClickListener
            stopGuardFlow()
        }
    }

    override fun onResume() {
        super.onResume()
        // 连接状态可能变化，回到页面时刷新展示
        refreshStateUI()
    }

    // ==================== 开启/结束守护 ====================

    private fun startGuardFlow() {
        // 前置校验1：定位权限(守护期间设备会反复索取位置，缺权限整个功能瘫痪)
        if (!hasLocationPermission()) {
            appendResult("⚠️缺少精准定位权限，正在申请 ACCESS_FINE_LOCATION...")
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_LOCATION_PERMISSION
            )
            return
        }
        // 前置校验2：系统定位开关(GPS 或网络定位至少一个可用)
        if (!isLocationProviderEnabled()) {
            appendResult("❌系统定位开关未打开(GPS/网络定位均不可用)，请到系统设置开启后再开启守护")
            showToast("请先打开系统定位开关")
            return
        }
        appendResult("📤下发开启QH15亲情守护... (写入成功≠开启成功，业务结果看监听回调)")
        armTimeout("开启守护")
        SafetyGuardManager.setGuardState(true) { code ->
            if (code == Code.REQUEST_CANCELED) {
                cancelTimeout()
                appendResult("❌开启守护指令写入被拒绝")
            }
        }
    }

    private fun stopGuardFlow() {
        appendResult("📤下发结束QH15亲情守护...")
        armTimeout("结束守护")
        SafetyGuardManager.setGuardState(false) { code ->
            if (code == Code.REQUEST_CANCELED) {
                cancelTimeout()
                appendResult("❌结束守护指令写入被拒绝")
            }
        }
    }

    // ==================== 双向定位 ====================

    /** 设备请求位置 -> App 定位并推送辅助信息给设备(守护进行中的核心循环，辅助信息可提升设备定位速度与精准度) */
    private fun handleGnssRequest() {
        // 本地守护已关闭时忽略设备请求
        if (!SafetyGuardManager.isGuarding) {
            appendResult("⚠️本地守护已关闭，忽略设备的位置请求")
            return
        }
        // 每次收到请求都要重新检查权限/开关，不能沿用开启守护时的一次性检查
        if (!hasLocationPermission()) {
            appendResult("❌缺少定位权限，无法推送辅助信息(不推送空数据)，请授予定位权限")
            showToast("缺少定位权限，无法响应设备位置请求")
            return
        }
        if (!isLocationProviderEnabled()) {
            appendResult("❌系统定位开关未打开，无法推送辅助信息")
            showToast("请先打开系统定位开关")
            return
        }
        fetchAndPushLocation()
    }

    private fun fetchAndPushLocation() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // GPS 与网络定位同时发起请求，谁先出结果用谁(室内GPS慢时网络定位可先到，户外则GPS精度更高)
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                .filter { lm.isProviderEnabled(it) }
        if (providers.isEmpty()) {
            appendResult("❌无可用定位Provider，无法推送辅助信息")
            return
        }
        appendResult("📍同时通过 ${providers.joinToString(" + ")} 获取单次定位(${LOCATION_TIMEOUT_MS / 1000}s超时)，任一先出结果即采用...")
        var settled = false
        // 注意：locTimeout 需先于 listener 声明(listener 内部会引用它)，避免局部变量前向引用编译错误
        val locTimeout = Runnable {
            if (settled) return@Runnable
            settled = true
            activeLocationListener?.let { listener -> providers.forEach { p -> runCatching { lm.removeUpdates(listener) } } }
            appendResult("⚠️${LOCATION_TIMEOUT_MS / 1000}s内未获取到定位结果，推送无效定位(INVALID_LOCATION)辅助信息")
            pushLocation(0.0, 0.0)
        }
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (settled) return
                settled = true
                mainHandler.removeCallbacks(locTimeout)
                // 定位成功后清理所有Provider上的监听
                providers.forEach { p -> runCatching { lm.removeUpdates(this) } }
                appendResult("📍定位成功(provider=${location.provider})：经度=${location.longitude}，纬度=${location.latitude}")
                pushLocation(location.longitude, location.latitude)
            }
        }
        activeLocationListener = listener
        var requested = 0
        providers.forEach { p ->
            try {
                lm.requestSingleUpdate(p, listener, Looper.getMainLooper())
                requested++
            } catch (e: SecurityException) {
                appendResult("⚠️$p 定位请求失败(权限校验异常)")
            }
        }
        if (requested == 0) {
            appendResult("❌所有Provider的定位请求均下发失败，无法推送辅助信息")
            return
        }
        mainHandler.postDelayed(locTimeout, LOCATION_TIMEOUT_MS)
    }

    /**
     * 组包并推送辅助信息给设备(辅助信息配置，可提升设备定位速度与精准度)：
     * 守护场景 gnssState 只填 EFFECTIVE_LOCATION/INVALID_LOCATION，经纬度为0时必须填 INVALID_LOCATION；
     * rssi 默认-128(现行固件不严格校验)；海拔不支持填0
     */
    private fun pushLocation(longitude: Double, latitude: Double) {
        val valid = longitude != 0.0 && latitude != 0.0
        val model = QH15SecurityProtectionModel(
            -128, latitude, longitude,
            if (valid) EQH15SecurityProtectionState.EFFECTIVE_LOCATION else EQH15SecurityProtectionState.INVALID_LOCATION,
            0
        )
        vpBleManager.pushQH15GnssLocationData(model) { code ->
            if (code == Code.REQUEST_CANCELED) {
                appendResult("❌辅助信息推送写入被拒绝")
            } else {
                appendResult("📤已向设备推送辅助信息(可提升设备定位速度与精准度)：$model")
            }
        }
    }

    // ==================== 全局事件分发(已在主线程) ====================

    private fun handleEvent(event: SafetyGuardManager.Event) {
        when (event) {
            is SafetyGuardManager.Event.SetResult -> {
                cancelTimeout()
                if (event.ackState == EQH15SetProtectionFuncAckState.SUCCESS) {
                    if (event.isOpen == true) {
                        guardStartTime = System.currentTimeMillis()
                        appendResult("✅【手机】开启守护被设备确认成功")
                    } else {
                        guardStartTime = null
                        appendResult("✅【手机】${if (event.isOpen == false) "结束" else "设置"}守护被设备确认成功")
                    }
                } else {
                    guardStartTime = null
                    appendResult("❌【手机】设置守护失败：${event.ackState.des}")
                }
            }
            is SafetyGuardManager.Event.StateChanged -> {
                val time = sdf.format(Date(event.timestamp * 1000))
                if (event.isOpen) {
                    // 手表端已开启，App不要再重复下发开启，只同步本地状态与UI
                    appendResult("✅【设备】手表端手动开启守护(时间:$time)，App同步状态，不重复下发")
                } else {
                    guardStartTime = null
                    appendResult("✅【设备】手表端手动结束守护(时间:$time)")
                }
            }
            is SafetyGuardManager.Event.GnssLocation -> {
                when (event.type) {
                    EQH15SPType.REQ -> {
                        val deviceInfo = event.model?.let { "，设备侧数据：$it" } ?: ""
                        appendResult("📩设备请求实时位置$deviceInfo，用于辅助位置校准")
                        handleGnssRequest()
                    }
                    EQH15SPType.PUSH -> {
                        val m = event.model
                        appendResult("📩设备推送实时位置：${m ?: "帧数据不完整"}")
                        if (m != null && m.gnssState == EQH15SecurityProtectionState.EFFECTIVE_LOCATION
                            && m.longitude != 0.0 && m.latitude != 0.0
                        ) {
                            tvCoordinate.text = "最新坐标：经度${m.longitude}，纬度${m.latitude}\n更新时间：${sdf.format(Date())}"
                        }
                    }
                    else -> appendResult("⚠️收到未知类型的GNSS位置通知：${event.type.des}")
                }
            }
        }
        refreshStateUI()
    }

    // ==================== 状态与工具 ====================

    private fun refreshStateUI() {
        val connected = vpBleManager.isCurrentDeviceConnected()
        tvDeviceState.text = "设备连接：" + if (connected) "已连接" else "未连接"
        tvDeviceState.setTextColor(ContextCompat.getColor(this, android.R.color.primary_text_light))
        tvGuardState.text = when {
            SafetyGuardManager.isGuarding -> {
                val start = guardStartTime?.let { "，开启于" + sdf.format(Date(it)) } ?: ""
                "守护状态：守护中$start"
            }
            else -> "守护状态：未开启"
        }
        tvGuardState.setTextColor(
            if (SafetyGuardManager.isGuarding) 0xFF12A30F.toInt() else 0xFF666666.toInt()
        )
        // 无连接/无定位权限时开启按钮置灰
        btnStart.isEnabled = connected && hasLocationPermission()
        btnStop.isEnabled = connected
    }

    private fun checkConnected(): Boolean {
        val connected = vpBleManager.isCurrentDeviceConnected()
        if (!connected) {
            appendResult("❌设备未连接，请先连接设备后再操作")
            showToast("设备未连接")
        }
        return connected
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun isLocationProviderEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /** 下发后10s无监听回调 -> 提示设备无响应(覆盖SDK静默失败场景) */
    private fun armTimeout(actionName: String) {
        cancelTimeout()
        val timeout = Runnable {
            pendingTimeout = null
            appendResult("⏰$actionName：${RESP_TIMEOUT_MS / 1000}s内未收到设备应答(设备可能不支持该功能或无响应)")
        }
        pendingTimeout = timeout
        mainHandler.postDelayed(timeout, RESP_TIMEOUT_MS)
    }

    private fun cancelTimeout() {
        pendingTimeout?.let { mainHandler.removeCallbacks(it) }
        pendingTimeout = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                appendResult("✅定位权限已授予，可重新点击开启守护")
            } else {
                appendResult("❌定位权限被拒绝，守护期间设备索取位置将无法推送辅助信息，开启守护按钮已置灰")
            }
            refreshStateUI()
        }
    }

    override fun onDestroy() {
        // 页面销毁只解绑UI回调，全局监听保持注册(覆盖式注册，重新注册反而丢事件)
        SafetyGuardManager.uiCallback = null
        activeLocationListener = null
        cancelTimeout()
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onClick(view: android.view.View) {
    }
}
