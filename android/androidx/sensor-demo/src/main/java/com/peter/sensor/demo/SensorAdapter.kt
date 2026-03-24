package com.peter.sensor.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.sensor.demo.databinding.ItemSensorOperationBinding

/**
 * 传感器操作列表适配器
 */
class SensorAdapter(
    private val items: List<SensorItem>,
    private val onItemClick: (SensorOperationType) -> Unit,
    private val tabColorRes: Int = R.color.primary,
    private val dotDrawableRes: Int = R.drawable.bg_color_dot
) : RecyclerView.Adapter<SensorAdapter.SensorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        return SensorViewHolder(
            ItemSensorOperationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick,
            tabColorRes,
            dotDrawableRes
        )
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class SensorViewHolder(
        private val binding: ItemSensorOperationBinding,
        private val onItemClick: (SensorOperationType) -> Unit,
        private val tabColorRes: Int,
        private val dotDrawableRes: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SensorItem) {
            val context = binding.root.context

            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                // 设置颜色圆点
                viewColorDot.setBackgroundResource(dotDrawableRes)

                // 设置阴影颜色
                val shadowColor = ContextCompat.getColor(context, tabColorRes)
                val cornerRadius = dpToPx(16, context).toFloat()
                cardView.shapeAppearanceModel = com.google.android.material.shape.ShapeAppearanceModel.builder()
                    .setAllCorners(com.google.android.material.shape.CornerFamily.ROUNDED, cornerRadius)
                    .build()

                cardView.elevation = dpToPx(6, context).toFloat()
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor

                // 点击事件
                cardView.setOnClickListener {
                    onItemClick(item.type)
                }
            }
        }
    }

    companion object {
        fun dpToPx(dp: Int, context: android.content.Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}
