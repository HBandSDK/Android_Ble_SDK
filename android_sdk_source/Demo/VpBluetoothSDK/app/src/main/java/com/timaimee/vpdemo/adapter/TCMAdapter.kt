package com.timaimee.vpdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timaimee.vpdemo.R
import com.veepoo.protocol.model.enums.TCMType

class TCMAdapter(private val items: List<TCMItem>) : RecyclerView.Adapter<TCMAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTv: TextView = view.findViewById(R.id.tv_name)
        val valueTv: TextView = view.findViewById(R.id.tv_value)
        val selectSwitch: Switch = view.findViewById(R.id.switch_select)
        val btnAdd: Button = view.findViewById(R.id.btn_add)
        val btnSub: Button = view.findViewById(R.id.btn_sub)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tcm_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 1. 【关键】先移除监听器，防止复用逻辑触发旧的监听
        holder.selectSwitch.setOnCheckedChangeListener(null)

        // 2. 设置状态
        holder.nameTv.text = item.type.description
        holder.valueTv.text = item.value.toString()
        holder.selectSwitch.isChecked = item.isSelected

        // 3. 重新设置监听器
        holder.selectSwitch.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
        }

        // 4. 数值加减（数值不需要移除监听，因为点击事件是覆盖式的）
        holder.btnAdd.setOnClickListener {
            item.value++
            holder.valueTv.text = item.value.toString()
        }

        holder.btnSub.setOnClickListener {
            if (item.value > 0) {
                item.value--
                holder.valueTv.text = item.value.toString()
            }
        }
    }

    override fun getItemCount() = items.size
}

data class TCMItem(
    val type: TCMType,
    var isSelected: Boolean = false,
    var value: Int = 50 // 默认值
)