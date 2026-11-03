package com.timaimee.vpdemo.activity.v2.health

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import com.orhanobut.logger.Logger
import com.timaimee.vpdemo.R
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity
import com.timaimee.vpdemo.activity.v2.DeviceMenu
import com.timaimee.vpdemo.bean.MyDeviceInfo
import com.timaimee.vpdemo.utils.CollapseCardLogView
import com.timaimee.vpdemo.utils.SleepChartView
import com.veepoo.protocol.listener.data.ISleepDataListener
import com.veepoo.protocol.model.datas.SleepData
import com.veepoo.protocol.model.datas.SleepPrecisionData
import com.veepoo.protocol.model.datas.TimeData
import com.veepoo.protocol.model.settings.ReadSleepSetting
import kotlin.math.roundToInt

class SleepActivity : BaseVPBLETestActivity() {

    companion object {
        const val TAG = "睡眠"

        /** 长睡眠判定阈值：总时长 >= 120 分钟为长睡眠，否则为短睡眠（与 GBand 一致） */
        const val LONG_SLEEP_MINUTES = 120
    }

    private lateinit var ccvSleepRead: CollapseCardLogView
    private lateinit var ccvSleepLog: CollapseCardLogView
    private lateinit var spReadDay: Spinner
    private lateinit var rgReadMode: RadioGroup
    private lateinit var btnReadSleep: Button
    private lateinit var tvProgress: TextView
    private lateinit var spChartDay: Spinner
    private lateinit var layoutLegendInsomnia: View

    private lateinit var layoutLongSleep: View
    private lateinit var layoutLongSleepSelector: View
    private lateinit var sleepChart: SleepChartView
    private lateinit var tvChartStart: TextView
    private lateinit var tvChartEnd: TextView
    private lateinit var tvSleepInfo: TextView
    private lateinit var spLongSleep: Spinner

    private lateinit var layoutNapSleep: View
    private lateinit var layoutNapSleepSelector: View
    private lateinit var sleepChartNap: SleepChartView
    private lateinit var tvNapChartStart: TextView
    private lateinit var tvNapChartEnd: TextView
    private lateinit var tvNapSleepInfo: TextView
    private lateinit var spNapSleep: Spinner

    private var watchDay = 3

    /** 每天可能有多段睡眠，key 为 day（0 今天，1 昨天...） */
    private val dayDataMap = LinkedHashMap<String, MutableList<SleepData>>()

    /** 当前展示的两张图表是否含失眠数据，用于控制图例 */
    private var longHasInsomnia = false
    private var napHasInsomnia = false

    override fun getLayoutID() = R.layout.activity_sleep

    override fun pageTitle() = DeviceMenu.Health.Sleep

    override fun initView() {
        ccvSleepRead = findViewById(R.id.ccvSleepRead)
        ccvSleepLog = findViewById(R.id.ccvSleepLog)
        spReadDay = findViewById(R.id.spReadDay)
        rgReadMode = findViewById(R.id.rgReadMode)
        btnReadSleep = findViewById(R.id.btnReadSleep)
        tvProgress = findViewById(R.id.tvProgress)
        spChartDay = findViewById(R.id.spChartDay)
        layoutLegendInsomnia = findViewById(R.id.layoutLegendInsomnia)

        layoutLongSleep = findViewById(R.id.layoutLongSleep)
        layoutLongSleepSelector = findViewById(R.id.layoutLongSleepSelector)
        sleepChart = findViewById(R.id.sleepChart)
        tvChartStart = findViewById(R.id.tvChartStart)
        tvChartEnd = findViewById(R.id.tvChartEnd)
        tvSleepInfo = findViewById(R.id.tvSleepInfo)
        spLongSleep = findViewById(R.id.spLongSleep)

        layoutNapSleep = findViewById(R.id.layoutNapSleep)
        layoutNapSleepSelector = findViewById(R.id.layoutNapSleepSelector)
        sleepChartNap = findViewById(R.id.sleepChartNap)
        tvNapChartStart = findViewById(R.id.tvNapChartStart)
        tvNapChartEnd = findViewById(R.id.tvNapChartEnd)
        tvNapSleepInfo = findViewById(R.id.tvNapSleepInfo)
        spNapSleep = findViewById(R.id.spNapSleep)
    }

    override fun initData() {
        watchDay = MyDeviceInfo.watchDataDay
        spReadDay.adapter = buildAdapter(arrayOf("今天", "昨天", "前天"))
        rgReadMode.check(R.id.rbAll)
        setCollapseExpandReciprocity(ccvSleepRead, ccvSleepLog)
        tvProgress.text = "尚未读取"
        resetCharts()
    }

    override fun initEvent() {
        btnReadSleep.setOnClickListener {
            setWriteCmdTAG(0)
            dayDataMap.clear()
            ccvSleepLog.clearTestInfo()
            ccvSleepRead.appendBlueMiddleText("▶ 开始读取睡眠数据...")
            tvProgress.text = "读取中..."
            spChartDay.isEnabled = false
            resetCharts()

            val setting = ReadSleepSetting(
                spReadDay.selectedItemPosition,
                rgReadMode.checkedRadioButtonId == R.id.rbOnlyOneDay,
                watchDay
            )
            vpBleManager.readSleepDataBySetting(defaultResponse, sleepDataListener, setting)
        }
    }

    private val sleepDataListener = object : ISleepDataListener {
        override fun onSleepDataChange(day: String?, sleepData: SleepData?) {
            if (sleepData == null) return
            val key = day ?: "-"
            // 同一天可能返回多段睡眠（长睡眠 + 短睡眠），全部保留
            dayDataMap.getOrPut(key) { mutableListOf() }.add(sleepData)

            val precision = sleepData is SleepPrecisionData
            val sleepType = sleepTypeText(sleepData)
            val info = "[${dayLabel(day)}][$sleepType] 日期=${sleepData.getDate()}, " +
                "${clock(sleepData.getSleepDown())}→${clock(sleepData.getSleepUp())}, " +
                "总时长=${sleepData.getAllSleepTime()}分钟, 深睡=${sleepData.getDeepSleepTime()}分钟, " +
                "浅睡=${sleepData.getLowSleepTime()}分钟, 质量=${sleepData.getSleepQulity()}/5, " +
                "醒来次数=${sleepData.getWakeCount()}, 类型=${if (precision) "精准睡眠" else "普通睡眠"}"
            Logger.t(TAG).e("-onSleepDataChange- day=$day $info")

            runOnUiThread {
                ccvSleepLog.appendResult(info)
            }
        }

        override fun onSleepProgress(progress: Float) {
            runOnUiThread {
                tvProgress.text = "读取进度：${(progress * 100).roundToInt()}%"
            }
        }

        override fun onSleepProgressDetail(day: String?, packagenumber: Int) {
            Logger.t(TAG).e("onSleepProgressDetail: day=$day package=$packagenumber")
        }

        override fun onReadSleepComplete() {
            Logger.t(TAG).e("onReadSleepComplete")
            runOnUiThread {
                tvProgress.text = "读取完成"
                ccvSleepRead.appendBlueMiddleText("✅ 睡眠数据读取完成")
                refreshChartSpinner()
            }
        }
    }

    /**
     * 读取完成后，按「今天-昨天-前天」的顺序填充图表日期选择器
     */
    private fun refreshChartSpinner() {
        if (dayDataMap.isEmpty()) {
            ccvSleepRead.appendRedLargeText("⚠️ 未读取到睡眠数据")
            return
        }
        val keys = dayDataMap.keys.toList()
        val labels = keys.map { dayLabel(it) }.toTypedArray()
        spChartDay.onItemSelectedListener = null
        spChartDay.adapter = buildAdapter(labels)
        spChartDay.isEnabled = true
        spChartDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                keys.getOrNull(position)?.let { key -> dayDataMap[key]?.let { renderDay(it) } }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        dayDataMap[keys.first()]?.let { renderDay(it) }
    }

    /**
     * 展示某一天的全部睡眠：长睡眠一张图、短睡眠一张图，各自可切换第几段
     */
    private fun renderDay(segments: List<SleepData>) {
        val sorted = segments.sortedBy { sortKey(it) }
        val longList = sorted.filter { it.getAllSleepTime() >= LONG_SLEEP_MINUTES }
        val napList = sorted.filter { it.getAllSleepTime() < LONG_SLEEP_MINUTES }

        layoutLongSleep.visibility = if (longList.isEmpty()) View.GONE else View.VISIBLE
        layoutNapSleep.visibility = if (napList.isEmpty()) View.GONE else View.VISIBLE

        longHasInsomnia = false
        napHasInsomnia = false

        if (longList.isNotEmpty()) {
            bindSegmentSpinner(spLongSleep, layoutLongSleepSelector, longList) { renderLongSleep(it) }
            renderLongSleep(longList.first())
        } else {
            clearLongChart()
        }

        if (napList.isNotEmpty()) {
            bindSegmentSpinner(spNapSleep, layoutNapSleepSelector, napList) { renderNapSleep(it) }
            renderNapSleep(napList.first())
        } else {
            clearNapChart()
        }

        updateInsomniaLegend()
    }

    /**
     * 为多段睡眠绑定切换下拉：只有一段时隐藏切换行
     */
    private fun bindSegmentSpinner(
        spinner: Spinner,
        selectorRow: View,
        group: List<SleepData>,
        onSelected: (SleepData) -> Unit
    ) {
        selectorRow.visibility = if (group.size > 1) View.VISIBLE else View.GONE
        spinner.onItemSelectedListener = null
        val labels = group.mapIndexed { index, data ->
            "第${index + 1}段  ${clock(data.getSleepDown())}-${clock(data.getSleepUp())}"
        }
        spinner.adapter = buildAdapter(labels.toTypedArray())
        spinner.isEnabled = group.size > 1
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                group.getOrNull(position)?.let(onSelected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun renderLongSleep(data: SleepData) {
        longHasInsomnia = renderChart(sleepChart, tvChartStart, tvChartEnd, tvSleepInfo, data)
        updateInsomniaLegend()
    }

    private fun renderNapSleep(data: SleepData) {
        napHasInsomnia = renderChart(sleepChartNap, tvNapChartStart, tvNapChartEnd, tvNapSleepInfo, data)
        updateInsomniaLegend()
    }

    /**
     * 绘制一段睡眠的曲线并填充文字信息，返回该段是否包含失眠数据
     */
    private fun renderChart(
        chart: SleepChartView,
        tvStart: TextView,
        tvEnd: TextView,
        tvInfo: TextView,
        data: SleepData
    ): Boolean {
        val precision = data is SleepPrecisionData
        val sleepLine = data.getSleepLine()
        chart.setSleepData(sleepLine, precision)
        tvStart.text = clock(data.getSleepDown())
        tvEnd.text = clock(data.getSleepUp())
        tvInfo.text = buildInfo(data, precision, sleepLine)
        return precision && !sleepLine.isNullOrEmpty() && sleepLine.contains('3')
    }

    private fun updateInsomniaLegend() {
        layoutLegendInsomnia.visibility =
            if (longHasInsomnia || napHasInsomnia) View.VISIBLE else View.GONE
    }

    private fun clearLongChart() {
        sleepChart.setSleepData(null, true)
        tvChartStart.text = "--:--"
        tvChartEnd.text = "--:--"
        tvSleepInfo.text = "暂无数据"
        layoutLongSleepSelector.visibility = View.GONE
        longHasInsomnia = false
    }

    private fun clearNapChart() {
        sleepChartNap.setSleepData(null, true)
        tvNapChartStart.text = "--:--"
        tvNapChartEnd.text = "--:--"
        tvNapSleepInfo.text = "暂无数据"
        layoutNapSleepSelector.visibility = View.GONE
        napHasInsomnia = false
    }

    private fun resetCharts() {
        clearLongChart()
        clearNapChart()
        layoutLongSleep.visibility = View.GONE
        layoutNapSleep.visibility = View.GONE
        layoutLegendInsomnia.visibility = View.GONE
    }

    /**
     * 除曲线外的信息仅用文字描述
     */
    private fun buildInfo(data: SleepData, precision: Boolean, sleepLine: String?): String {
        val line = sleepLine ?: ""
        val perPoint = minutesPerPoint(data, precision)
        val all = data.getAllSleepTime()
        val info = StringBuilder()
        info.append("日期：").append(data.getDate() ?: "-").append('\n')
        info.append("入睡 → 起床：").append(clock(data.getSleepDown())).append(" → ")
            .append(clock(data.getSleepUp())).append('\n')
        info.append("睡眠总时长：").append(formatDuration(all)).append('\n')
        info.append("深睡：").append(formatDuration(data.getDeepSleepTime()))
            .append("（").append(percent(data.getDeepSleepTime(), all)).append("%）").append('\n')
        info.append("浅睡：").append(formatDuration(data.getLowSleepTime()))
            .append("（").append(percent(data.getLowSleepTime(), all)).append("%）").append('\n')

        if (precision) {
            val rem = line.count { it == '2' } * perPoint
            val insomnia = line.count { it == '3' } * perPoint
            val awake = line.count { it == '4' } * perPoint
            info.append("快速眼动：").append(formatDuration(rem))
                .append("（").append(percent(rem, all)).append("%）").append('\n')
            if (insomnia > 0) {
                info.append("失眠：").append(formatDuration(insomnia)).append('\n')
            }
            if (awake > 0) {
                info.append("清醒：").append(formatDuration(awake)).append('\n')
            }
        } else {
            val awake = line.count { it == '2' } * perPoint
            if (awake > 0) {
                info.append("苏醒：").append(formatDuration(awake)).append('\n')
            }
        }

        info.append("睡眠质量：").append(data.getSleepQulity()).append("/5，醒来次数：")
            .append(data.getWakeCount()).append('\n')

        if (data is SleepPrecisionData) {
            info.append("睡眠得分：深睡").append(data.getDeepScore())
                .append(" / 入睡").append(data.getFallAsleepScore())
                .append(" / 睡眠效率").append(data.getSleepEfficiencyScore())
                .append(" / 时长").append(data.getSleepTimeScore())
                .append(" / 起夜").append(data.getGetUpScore()).append('\n')
            if (data.getInsomniaTimes() > 0 || data.getInsomniaLength() > 0) {
                info.append("失眠次数：").append(data.getInsomniaTimes())
                    .append("，失眠时长：").append(data.getInsomniaLength()).append("分钟").append('\n')
            }
            val insomniaStart = data.getStartInsomniaTime()
            val insomniaStop = data.getStopInsomniaTime()
            if (!insomniaStart.isNullOrEmpty() && !insomniaStop.isNullOrEmpty()) {
                info.append("失眠时间：").append(insomniaStart).append(" → ").append(insomniaStop).append('\n')
            }
        }

        info.append("曲线精度：每个点 ").append(perPoint).append(" 分钟（")
            .append(if (precision) "精准睡眠" else "普通睡眠").append("）")
        return info.toString()
    }

    /**
     * 曲线上一个点代表的分钟数
     */
    private fun minutesPerPoint(data: SleepData, precision: Boolean): Int {
        if (!precision) return 5
        val seconds = (data as SleepPrecisionData).getOnePointDuration()
        return if (seconds <= 0) 1 else maxOf(1, seconds / 60)
    }

    private fun sleepTypeText(data: SleepData): String =
        if (data.getAllSleepTime() >= LONG_SLEEP_MINUTES) "长睡眠" else "短睡眠"

    private fun sortKey(data: SleepData): String {
        val down = data.getSleepDown() ?: return ""
        return down.getDateAndClockForDb() ?: clock(down)
    }

    private fun clock(time: TimeData?): String = time?.getClock() ?: "--:--"

    private fun formatDuration(minutes: Int): String {
        val total = minutes.coerceAtLeast(0)
        if (total < 60) return "${total}分钟"
        val hour = total / 60
        val minute = total % 60
        return if (minute == 0) "${hour}小时" else "${hour}小时${minute}分钟"
    }

    private fun percent(part: Int, total: Int): Int {
        if (total <= 0) return 0
        return (part * 100f / total).roundToInt()
    }

    private fun dayLabel(day: String?): String {
        val index = day?.toIntOrNull() ?: -1
        return when (index) {
            0 -> "今天"
            1 -> "昨天"
            2 -> "前天"
            -1 -> "未知"
            else -> "${index}天前"
        }
    }

    private fun buildAdapter(labels: Array<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    override fun onCMDWriteFailed(cmdTag: Int) {
        super.onCMDWriteFailed(cmdTag)
        when (cmdTag) {
            0 -> ccvSleepRead.appendRedLargeText("⚠️[读取睡眠数据]指令写入失败！")
        }
    }

    override fun onCMDWriteSuccess(cmdTag: Int) {
        super.onCMDWriteSuccess(cmdTag)
        when (cmdTag) {
            0 -> ccvSleepRead.appendBlueMiddleText("✅️[读取睡眠数据]指令写入成功！")
        }
    }
}
