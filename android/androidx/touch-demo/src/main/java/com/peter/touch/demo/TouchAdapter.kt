package com.peter.touch.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peter.touch.demo.databinding.ItemTouchBinding

/**
 * 列表适配器
 */
class TouchAdapter(
    private val onItemClick: (TouchItem) -> Unit
) : RecyclerView.Adapter<TouchAdapter.ViewHolder>() {

    private val items = mutableListOf<TouchItem>()
    private var lastCategory: TouchItem.Category? = null

    fun submitList(newItems: List<TouchItem>) {
        items.clear()
        items.addAll(newItems)
        lastCategory = null
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTouchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        // 判断是否需要显示分类标题
        val showCategory = item.category != lastCategory
        if (showCategory) {
            lastCategory = item.category
        }
        
        holder.bind(item, showCategory)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemTouchBinding,
        private val onItemClick: (TouchItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cardView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick((bindingAdapter as TouchAdapter).items[position])
                }
            }
        }

        fun bind(item: TouchItem, showCategory: Boolean) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            
            // 分类指示条颜色
            binding.viewCategoryIndicator.setBackgroundColor(item.category.getColor())
            
            // 分类标题
            if (showCategory) {
                binding.tvCategoryHeader.visibility = View.VISIBLE
                binding.tvCategoryHeader.text = item.category.getDisplayName()
                binding.tvCategoryHeader.setTextColor(item.category.getColor())
            } else {
                binding.tvCategoryHeader.visibility = View.GONE
            }
        }
    }
}
