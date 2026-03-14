package com.peter.mmkv.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.mmkv.demo.databinding.ItemCategoryHeaderBinding
import com.peter.mmkv.demo.databinding.ItemFeatureBinding

/**
 * 功能列表适配器
 */
class FeatureAdapter(
    private val items: List<FeatureItem>,
    private val onItemClick: (MMKVFeature) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    // 构建带分类标题的列表项
    private val itemsWithHeaders: List<Any> = buildList {
        var lastCategory: FeatureCategory? = null
        items.forEach { item ->
            if (item.category != lastCategory) {
                add(item.category)
                lastCategory = item.category
            }
            add(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemsWithHeaders[position]) {
            is FeatureCategory -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ItemViewHolder(
                ItemFeatureBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val category = itemsWithHeaders[position] as FeatureCategory
                holder.bind(category)
            }
            is ItemViewHolder -> {
                val item = itemsWithHeaders[position] as FeatureItem
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = itemsWithHeaders.size

    class HeaderViewHolder(
        private val binding: ItemCategoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: FeatureCategory) {
            binding.tvCategoryHeader.text = category.displayName
            
            // 设置分类标题颜色
            val colorRes = when (category) {
                FeatureCategory.BASIC -> R.color.category_basic
                FeatureCategory.DATA_TYPE -> R.color.category_data_type
                FeatureCategory.ADVANCED -> R.color.category_advanced
            }
            binding.tvCategoryHeader.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class ItemViewHolder(
        private val binding: ItemFeatureBinding,
        private val onItemClick: (MMKVFeature) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FeatureItem) {
            val context = binding.root.context
            
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                // 根据分类设置颜色圆点和阴影颜色
                val (dotRes, shadowColor) = when (item.category) {
                    FeatureCategory.BASIC -> 
                        R.drawable.bg_dot_basic to ContextCompat.getColor(context, R.color.category_basic)
                    FeatureCategory.DATA_TYPE -> 
                        R.drawable.bg_dot_data_type to ContextCompat.getColor(context, R.color.category_data_type)
                    FeatureCategory.ADVANCED -> 
                        R.drawable.bg_dot_advanced to ContextCompat.getColor(context, R.color.category_advanced)
                }
                
                viewColorDot.setBackgroundResource(dotRes)
                
                // 设置圆角形状（确保阴影形状正确）
                val cornerRadius = dpToPx(16, context).toFloat()
                cardView.shapeAppearanceModel = com.google.android.material.shape.ShapeAppearanceModel.builder()
                    .setAllCorners(com.google.android.material.shape.CornerFamily.ROUNDED, cornerRadius)
                    .build()
                
                // 设置 elevation 和带颜色的阴影 (API 28+)
                cardView.elevation = dpToPx(6, context).toFloat()
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor

                // 点击整个卡片
                cardView.setOnClickListener {
                    onItemClick(item.feature)
                }
            }
        }
        
        private fun dpToPx(dp: Int, context: android.content.Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}
