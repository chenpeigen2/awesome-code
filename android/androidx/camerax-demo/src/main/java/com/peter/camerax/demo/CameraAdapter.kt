package com.peter.camerax.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.peter.camerax.demo.databinding.ItemCameraOperationBinding

/**
 * Camera operation list adapter
 */
class CameraAdapter(
    private val items: List<CameraItem>,
    private val onItemClick: (CameraOperationType) -> Unit,
    private val tabColorRes: Int = R.color.primary,
    private val dotDrawableRes: Int = R.drawable.bg_color_dot
) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        return CameraViewHolder(
            ItemCameraOperationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick,
            tabColorRes,
            dotDrawableRes
        )
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class CameraViewHolder(
        private val binding: ItemCameraOperationBinding,
        private val onItemClick: (CameraOperationType) -> Unit,
        private val tabColorRes: Int,
        private val dotDrawableRes: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CameraItem) {
            val context = binding.root.context

            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description

                // Set color dot
                viewColorDot.setBackgroundResource(dotDrawableRes)

                // Set shadow color
                val shadowColor = ContextCompat.getColor(context, tabColorRes)
                val cornerRadius = dpToPx(16, context).toFloat()
                cardView.shapeAppearanceModel = com.google.android.material.shape.ShapeAppearanceModel.builder()
                    .setAllCorners(com.google.android.material.shape.CornerFamily.ROUNDED, cornerRadius)
                    .build()

                cardView.elevation = dpToPx(6, context).toFloat()
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor

                // Click event
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
