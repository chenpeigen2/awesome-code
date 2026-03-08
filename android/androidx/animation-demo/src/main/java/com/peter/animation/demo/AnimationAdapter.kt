package com.peter.animation.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.peter.animation.demo.databinding.ItemAnimationBinding

/**
 * 动画列表适配器
 */
class AnimationAdapter(
    private val onItemClick: (AnimationItem) -> Unit
) : RecyclerView.Adapter<AnimationAdapter.ViewHolder>() {

    private val items = mutableListOf<AnimationItem>()
    private var currentCategory: AnimationCategory? = null

    fun submitList(newItems: List<AnimationItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnimationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 检查是否需要显示分类标题
        val showCategory = position == 0 || items[position - 1].category != item.category
        holder.bind(item, showCategory)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemAnimationBinding,
        private val onItemClick: (AnimationItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnimationItem, showCategory: Boolean) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description

            // 设置分类颜色指示条
            binding.viewCategoryIndicator.setBackgroundColor(item.category.colorRes)

            // 分类标题
            if (showCategory) {
                binding.tvCategoryHeader.visibility = View.VISIBLE
                binding.tvCategoryHeader.text = item.category.title
                binding.tvCategoryHeader.setTextColor(item.category.colorRes)
            } else {
                binding.tvCategoryHeader.visibility = View.GONE
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
