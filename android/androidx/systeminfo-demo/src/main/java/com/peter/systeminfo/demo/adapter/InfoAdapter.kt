package com.peter.systeminfo.demo.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.peter.systeminfo.demo.R
import com.peter.systeminfo.demo.databinding.ItemInfoBinding

/**
 * 信息项数据类
 */
data class InfoItem(
    val label: String,
    val value: String
)

/**
 * 信息列表适配器
 */
class InfoAdapter : ListAdapter<InfoItem, InfoAdapter.InfoViewHolder>(InfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val binding = ItemInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class InfoViewHolder(
        private val binding: ItemInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InfoItem) {
            binding.tvLabel.text = item.label
            binding.tvValue.text = item.value

            // 长按复制到剪贴板
            binding.cardView.setOnLongClickListener {
                copyToClipboard(binding.root.context, item.label, item.value)
                true
            }
        }

        private fun copyToClipboard(context: Context, label: String, value: String) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, value)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(context, "已复制: $value", Toast.LENGTH_SHORT).show()
        }
    }

    class InfoDiffCallback : DiffUtil.ItemCallback<InfoItem>() {
        override fun areItemsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
            return oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
            return oldItem == newItem
        }
    }
}
