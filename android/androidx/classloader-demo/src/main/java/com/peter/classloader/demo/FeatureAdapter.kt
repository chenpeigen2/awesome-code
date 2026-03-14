package com.peter.classloader.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.classloader.demo.databinding.ItemCategoryHeaderBinding
import com.peter.classloader.demo.databinding.ItemFeatureBinding

/**
 * 功能列表适配器
 */
class FeatureAdapter(
    private val items: List<FeatureItem>,
    private val onItemClick: (ClassLoaderFeature) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

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
            
            val colorRes = when (category) {
                FeatureCategory.CONCEPT -> R.color.category_concept
                FeatureCategory.CUSTOM -> R.color.category_custom
                FeatureCategory.ADVANCED -> R.color.category_advanced
            }
            binding.tvCategoryHeader.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    class ItemViewHolder(
        private val binding: ItemFeatureBinding,
        private val onItemClick: (ClassLoaderFeature) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FeatureItem) {
            val context = binding.root.context
            
            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                val (dotRes, shadowColor) = when (item.category) {
                    FeatureCategory.CONCEPT -> 
                        R.drawable.bg_dot_concept to ContextCompat.getColor(context, R.color.category_concept)
                    FeatureCategory.CUSTOM -> 
                        R.drawable.bg_dot_custom to ContextCompat.getColor(context, R.color.category_custom)
                    FeatureCategory.ADVANCED -> 
                        R.drawable.bg_dot_advanced to ContextCompat.getColor(context, R.color.category_advanced)
                }
                
                viewColorDot.setBackgroundResource(dotRes)
                
                val cornerRadius = dpToPx(16, context).toFloat()
                cardView.shapeAppearanceModel = com.google.android.material.shape.ShapeAppearanceModel.builder()
                    .setAllCorners(com.google.android.material.shape.CornerFamily.ROUNDED, cornerRadius)
                    .build()
                
                cardView.elevation = dpToPx(6, context).toFloat()
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor

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
