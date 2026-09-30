package com.timaimee.vpdemo.activity.v2.other

import android.os.Handler
import android.os.Looper
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.data.IQH15GnssLocationListener
import com.veepoo.protocol.listener.data.IQH15SafetyProtectionListener
import com.veepoo.protocol.model.datas.QH15SecurityProtectionModel
import com.veepoo.protocol.model.enums.EQH15SetProtectionFuncAckState
import com.veepoo.protocol.model.enums.EQH15SPType

/**
 * QH15亲情守护全局管理器(示例)
 *
 * 设计要点(对应 SDK 两个QH15监听为覆盖式注册的约束)：
 * 1.addQH15SafetyProtectionListener/addQH15GnssLocationListener 全局只有一份实例，
 *   重复注册会替换掉之前的实例导致丢事件，因此本管理器保证进程内【仅注册一次】；
 * 2.页面只通过 [uiCallback] 接收事件，不自己注册监听，注册逻辑不进 Activity 生命周期；
 * 3.本地守护状态以"最后一次收到的任一入口事件"(手机端设置应答/手表端手动操作)为准；
 *   手机端设置应答(ackState==SUCCESS)时，按最后一次下发动作翻转本地状态；
 * 4.SDK 回调来自 BLE 线程，统一 post 到主线程后再分发给页面。
 */
object SafetyGuardManager {

    /** 守护事件：页面据此刷新 UI 与写日志 */
    sealed class Event {
        /**
         * 手机端设置守护状态的应答结果
         * @param isOpen 下发时的动作(true开启/false关闭)，无下发记录时为null
         * @param ackState 应答结果
         */
        data class SetResult(val isOpen: Boolean?, val ackState: EQH15SetProtectionFuncAckState) : Event()

        /** 设备端(手表上用户手动操作)请求开启/结束守护 */
        data class StateChanged(val isOpen: Boolean, val timestamp: Long) : Event()

        /**
         * GNSS实时位置数据通知
         * @param type REQ: 设备请求APP提供位置(需取定位后 pushQH15GnssLocationData 回推)；
         *             PUSH: 设备主动推送实时位置(直接展示即可)
         * @param model 位置数据(帧数据不完整时为null)
         */
        data class GnssLocation(val type: EQH15SPType, val model: QH15SecurityProtectionModel?) : Event()
    }

    /** 本地守护状态 */
    @Volatile
    var isGuarding: Boolean = false
        private set

    /** 当前页面 UI 回调(页面 onCreate 时设置，onDestroy 时置空即可，不影响监听注册) */
    @Volatile
    var uiCallback: ((Event) -> Unit)? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var registered = false

    /** 手机端最后一次下发的守护开关动作，用于设置应答成功后翻转本地状态 */
    @Volatile
    private var pendingSetOpen: Boolean? = null

    /**
     * 注册QH15亲情守护双向监听(进程内仅一次)
     * 建议：真实项目放到设备连接成功的回调里调用；demo 中页面首次进入时调用
     */
    @Synchronized
    fun ensureRegistered() {
        if (registered) return
        registered = true
        val manager = VPOperateManager.getInstance()
        manager.addQH15SafetyProtectionListener(object : IQH15SafetyProtectionListener {
            override fun onQH15SafetyProtectionSetResult(ackState: EQH15SetProtectionFuncAckState) {
                val open = pendingSetOpen
                pendingSetOpen = null
                if (ackState == EQH15SetProtectionFuncAckState.SUCCESS && open != null) {
                    isGuarding = open
                }
                dispatch(Event.SetResult(open, ackState))
            }

            override fun onQH15SafetyProtectionStateChanged(isOpen: Boolean, timestamp: Long) {
                isGuarding = isOpen
                dispatch(Event.StateChanged(isOpen, timestamp))
            }
        })
        manager.addQH15GnssLocationListener(object : IQH15GnssLocationListener {
            override fun onQH15GnssLocation(type: EQH15SPType, model: QH15SecurityProtectionModel?) {
                dispatch(Event.GnssLocation(type, model))
            }
        })
    }

    /**
     * 设置守护开关状态(内部记录下发动作用于应答后翻转本地状态)
     * @param isOpen true开启 false关闭
     * @param onResponse 写入结果回调(REQUEST_CANCELED=写入被拒绝，如设备OTA升级中)
     */
    fun setGuardState(isOpen: Boolean, onResponse: (Int) -> Unit) {
        pendingSetOpen = isOpen
        VPOperateManager.getInstance().setQH15SafetyProtection(isOpen) { code -> onResponse(code) }
    }

    /** 统一切主线程后分发给页面 */
    private fun dispatch(event: Event) {
        mainHandler.post { uiCallback?.invoke(event) }
    }
}
