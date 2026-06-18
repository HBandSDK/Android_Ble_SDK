package com.timaimee.vpdemo.activity.v2.function_switch

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.adapter.MessagePushSwitchAdapter
import com.timaimee.vpdemo.bean.NotificationFunction
import com.veepoo.protocol.listener.data.ISocialMsgDataListener
import com.veepoo.protocol.model.datas.FunctionSocailMsgData
import com.veepoo.protocol.model.enums.ESocailMsg

class MessagePushSwitchActivity : BaseVPBLETestActivity(), ISocialMsgDataListener, MessagePushSwitchAdapter.OnNotifySwitchToggleChangeListener {

    lateinit var rvMsgPush: RecyclerView
    lateinit var adapter: MessagePushSwitchAdapter
    var functions: MutableList<NotificationFunction> = mutableListOf()
    var socailMsgData: FunctionSocailMsgData? = null

    override fun getLayoutID() = R.layout.activity_setting_list

    override fun pageTitle() = "消息推送开关"

    override fun initView() {

    }

    override fun initData() {
        vpBleManager.readSocialMsg(defaultResponse, this)
    }

    override fun initEvent() {
        rvMsgPush = findViewById(R.id.rvSetting)
        rvMsgPush.layoutManager = LinearLayoutManager(this)
        adapter = MessagePushSwitchAdapter(functions, this)
        rvMsgPush.adapter = adapter
    }

    override fun onSocialMsgSupportDataChange(socailMsgData: FunctionSocailMsgData) {
        functions.clear()
        functions.addAll(getNotificationFunctionList(socailMsgData))
        adapter.notifyDataSetChanged()
    }

    override fun onSocialMsgSupportDataChange2(socailMsgData: FunctionSocailMsgData) {
        functions.clear()
        functions.addAll(getNotificationFunctionList(socailMsgData))
        adapter.notifyDataSetChanged()
    }

    private fun getNotificationFunctionList(socailMsgData: FunctionSocailMsgData): MutableList<NotificationFunction> {
        this.socailMsgData = socailMsgData
        val functions: MutableList<NotificationFunction> = java.util.ArrayList<NotificationFunction>()
        functions.add(NotificationFunction(ESocailMsg.PHONE, socailMsgData.phone, "电话"))
        functions.add(NotificationFunction(ESocailMsg.SMS, socailMsgData.msg, "短信"))

        functions.add(NotificationFunction(ESocailMsg.WECHAT, socailMsgData.wechat, "微信"))
        functions.add(NotificationFunction(ESocailMsg.QQ, socailMsgData.qq, "QQ"))
        functions.add(NotificationFunction(ESocailMsg.SINA, socailMsgData.sina, "新浪"))
        functions.add(NotificationFunction(ESocailMsg.FACEBOOK, socailMsgData.facebook, "Facebook"))
        functions.add(NotificationFunction(ESocailMsg.TWITTER, socailMsgData.twitter, "X(原推特)"))

        functions.add(NotificationFunction(ESocailMsg.FLICKR, socailMsgData.flickr, "Flickr"))
        functions.add(NotificationFunction(ESocailMsg.LINKIN, socailMsgData.linkin, "Linkin"))
        functions.add(NotificationFunction(ESocailMsg.WHATS, socailMsgData.whats, "Whats"))
        functions.add(NotificationFunction(ESocailMsg.LINE, socailMsgData.line, "Line"))
        functions.add(NotificationFunction(ESocailMsg.INSTAGRAM, socailMsgData.instagram, "Instagram"))

        functions.add(NotificationFunction(ESocailMsg.SNAPCHAT, socailMsgData.snapchat, "Snapchat"))
        functions.add(NotificationFunction(ESocailMsg.SKYPE, socailMsgData.skype, "Skype"))
        functions.add(NotificationFunction(ESocailMsg.GMAIL, socailMsgData.gmail, "Gmail"))
        functions.add(NotificationFunction(ESocailMsg.DINGDING, socailMsgData.dingding, "钉钉"))
        functions.add(NotificationFunction(ESocailMsg.WXWORK, socailMsgData.wxWork, "企业微信"))

        functions.add(NotificationFunction(ESocailMsg.OTHER, socailMsgData.other, "其他"))
        functions.add(NotificationFunction(ESocailMsg.TIKTOK, socailMsgData.tikTok, "TikTok"))
        functions.add(NotificationFunction(ESocailMsg.TELEGRAM, socailMsgData.telegram, "Telegram"))
        functions.add(NotificationFunction(ESocailMsg.CONNECTED2_ME, socailMsgData.connected2_me, "Connected2me"))
        functions.add(NotificationFunction(ESocailMsg.KAKAO_TALK, socailMsgData.kakaoTalk, "Kakao Talk"))

        functions.add(NotificationFunction(ESocailMsg.SHIELD_POLICE, socailMsgData.shieldPolice, "警右"))
        functions.add(NotificationFunction(ESocailMsg.MESSENGER, socailMsgData.messenger, "MESSENGER"))
        functions.add(NotificationFunction(ESocailMsg.ZALO, socailMsgData.zalo, "Zalo"))
        functions.add(NotificationFunction(ESocailMsg.VIBER, socailMsgData.viber, "Viber"))

        functions.add(NotificationFunction(ESocailMsg.G15MSG, socailMsgData.getWhats(), "G-15"))
        return functions
    }

    override fun onToggleChanged(notify: NotificationFunction) {
        when (notify.type) {
            ESocailMsg.G15MSG -> ""
            ESocailMsg.PHONE -> socailMsgData!!.phone = notify.status
            ESocailMsg.SMS -> socailMsgData!!.msg = notify.status
            ESocailMsg.WECHAT -> socailMsgData!!.wechat = notify.status
            ESocailMsg.QQ -> socailMsgData!!.qq = notify.status
            ESocailMsg.SINA -> socailMsgData!!.sina = notify.status
            ESocailMsg.FACEBOOK -> socailMsgData!!.phone = notify.status
            ESocailMsg.TWITTER -> socailMsgData!!.twitter = notify.status
            ESocailMsg.FLICKR -> socailMsgData!!.flickr = notify.status
            ESocailMsg.LINKIN -> socailMsgData!!.linkin = notify.status
            ESocailMsg.WHATS -> socailMsgData!!.whats = notify.status
            ESocailMsg.LINE -> socailMsgData!!.line = notify.status
            ESocailMsg.INSTAGRAM -> socailMsgData!!.instagram = notify.status
            ESocailMsg.SNAPCHAT -> socailMsgData!!.snapchat = notify.status
            ESocailMsg.SKYPE -> socailMsgData!!.skype = notify.status
            ESocailMsg.GMAIL -> socailMsgData!!.gmail = notify.status
            ESocailMsg.DINGDING -> socailMsgData!!.dingding = notify.status
            ESocailMsg.WXWORK -> socailMsgData!!.wxWork = notify.status
            ESocailMsg.OTHER -> socailMsgData!!.other = notify.status
            ESocailMsg.TIKTOK -> socailMsgData!!.tikTok = notify.status
            ESocailMsg.TELEGRAM -> socailMsgData!!.telegram = notify.status
            ESocailMsg.CONNECTED2_ME -> socailMsgData!!.connected2_me = notify.status
            ESocailMsg.KAKAO_TALK -> socailMsgData!!.kakaoTalk = notify.status
            ESocailMsg.SHIELD_POLICE -> socailMsgData!!.shieldPolice = notify.status
            ESocailMsg.MESSENGER -> socailMsgData!!.messenger = notify.status
            ESocailMsg.ZALO -> socailMsgData!!.zalo = notify.status
            ESocailMsg.VIBER -> socailMsgData!!.viber = notify.status
            ESocailMsg.APP_CUSTOM_MSG -> ""
        }
        vpBleManager.settingSocialMsg(defaultResponse, this, socailMsgData)
    }

}