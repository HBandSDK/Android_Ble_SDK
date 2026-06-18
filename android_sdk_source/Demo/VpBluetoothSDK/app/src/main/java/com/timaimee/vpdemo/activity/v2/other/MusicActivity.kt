package com.timaimee.vpdemo.activity.v2.other

import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.veepoo.protocol.listener.data.IMusicControlListener
import com.veepoo.protocol.listener.data.IMusicDataListener
import com.veepoo.protocol.model.datas.MusicData
import com.veepoo.protocol.model.datas.WeatherStatusData

class MusicActivity : BaseVPBLETestActivity() , IMusicDataListener, IMusicControlListener{

    private lateinit var ccvMusicControl: CollapseCardLogView
    private lateinit var etSingerName: EditText
    private lateinit var etMusicName: EditText
    private lateinit var etAlbumName: EditText
    private lateinit var rgStatus: RadioGroup
    private lateinit var sbVoice: SeekBar
    private lateinit var tvVoiceValue: TextView
    private lateinit var btnMusicSetting: Button
    private lateinit var btnVoiceSetting: Button

    override fun getLayoutID() = R.layout.activity_music

    override fun pageTitle() = "音乐"

    override fun initView() {
        // 绑定折叠卡片
        ccvMusicControl = findViewById(R.id.ccvMusicControl)

        // 绑定输入框
        etSingerName = findViewById(R.id.etSingerName)
        etMusicName = findViewById(R.id.etMusicName)
        etAlbumName = findViewById(R.id.etAlbumName)

        // 绑定状态
        rgStatus = findViewById(R.id.rgStatus)

        // 绑定音量
        sbVoice = findViewById(R.id.sbVoice)
        tvVoiceValue = findViewById(R.id.tvVoiceValue)

        // 绑定按钮
        btnMusicSetting = findViewById(R.id.btnMusicSetting)
        btnVoiceSetting = findViewById(R.id.btnVoiceSetting)

        // ========== 音量条初始化 ==========
        sbVoice.max = 100
        sbVoice.progress = 50
        tvVoiceValue.text = "50%"
    }

    override fun initData() {
        ccvMusicControl.setFunctionEnabled(fCheck.isMusicWithInfo())
    }

    override fun initEvent() {
        // ========== 音量滑动监听 ==========
        sbVoice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVoiceValue.text = "${progress}%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ========== 音乐设置 ==========
        btnMusicSetting.setOnClickListener {
            val singer = etSingerName.text.toString().trim().default("G.E.M. 邓紫棋")
            val music = etMusicName.text.toString().trim().default("来自天堂的魔鬼")
            val album = etAlbumName.text.toString().trim().default("新的心跳")
            val isPlay = rgStatus.checkedRadioButtonId == R.id.rbPlay
            val status = if (isPlay) "播放" else "暂停"
            val voice = sbVoice.progress

            // 日志输出（自动追加到折叠卡片日志区）
            ccvMusicControl.appendResult("===== 开始音乐设置 =====")
            ccvMusicControl.appendResult("歌手：$singer")
            ccvMusicControl.appendResult("歌曲：$music")
            ccvMusicControl.appendResult("专辑：$album")
            ccvMusicControl.appendResult("状态：$status")
            ccvMusicControl.appendResult("音量：$voice%")
            ccvMusicControl.appendResult("-----------------------\n")

            val musicData = MusicData(singer, music, album,voice, if(isPlay) 1 else 0)
            vpBleManager.settingMusicData(defaultResponse, musicData, this)
        }

        // ========== 音量设置 ==========
        btnVoiceSetting.setOnClickListener {
            val voice = sbVoice.progress
            ccvMusicControl.appendResult("音量设置：$voice%")
            vpBleManager.settingVolume(voice, defaultResponse, this)
        }
    }

    fun String?.default(def: String) = this.takeIf { it!!.isNotBlank() } ?: def

    override fun onMusicDataChange(weatherStatusData: WeatherStatusData?) {
    }

    override fun nextMusic() {
        ccvMusicControl.appendBlueMiddleText("⏭️下一首")
    }

    override fun previousMusic() {
        ccvMusicControl.appendBlueMiddleText("⏮️上一首")
    }

    override fun pauseAndPlayMusic() {
        ccvMusicControl.appendBlueMiddleText("⏸️暂停->播放")
    }

    override fun pauseMusic() {
        ccvMusicControl.appendBlueMiddleText("⏸️暂停")
    }

    override fun playMusic() {
        ccvMusicControl.appendBlueMiddleText("⏯️播放")
    }

    override fun voiceUp() {
        ccvMusicControl.appendBlueMiddleText("🔊➕️")
    }

    override fun voiceDown() {
        ccvMusicControl.appendBlueMiddleText("🔊➖️")
    }

    override fun oprateMusicSuccess() {
        ccvMusicControl.appendBlueMiddleText("✅️音乐设置成功")
    }

    override fun oprateMusicFail() {
        ccvMusicControl.appendRedLargeText("❌️音乐设置失败")
    }
}