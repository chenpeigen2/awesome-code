package com.peter.statusbar.demo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

/**
 * 状态栏选项数据类
 */
data class StatusBarOption(
    val id: String,
    val title: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    @ColorInt val iconTint: Int,
    @ColorInt val cardBackground: Int = Color.WHITE,
    val colorPreview: Int? = null,  // 用于颜色选项的预览色
    val action: (() -> Unit)? = null
)

/**
 * 状态栏选项适配器
 */
class StatusBarAdapter(
    private val items: List<StatusBarOption>,
    private val onItemClick: (StatusBarOption) -> Unit
) : RecyclerView.Adapter<StatusBarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val viewColorPreview: View = view.findViewById(R.id.viewColorPreview)
        val ivArrow: ImageView = view.findViewById(R.id.ivArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_status_bar_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.ivIcon.setImageResource(item.iconRes)
        holder.ivIcon.setColorFilter(item.iconTint)
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description

        // 颜色预览
        if (item.colorPreview != null) {
            holder.viewColorPreview.visibility = View.VISIBLE
            holder.viewColorPreview.setBackgroundColor(item.colorPreview)
            holder.ivArrow.visibility = View.GONE
        } else {
            holder.viewColorPreview.visibility = View.GONE
            holder.ivArrow.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
            item.action?.invoke()
        }
    }

    override fun getItemCount() = items.size
}
