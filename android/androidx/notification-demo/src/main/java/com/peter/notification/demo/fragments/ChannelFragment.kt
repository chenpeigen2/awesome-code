package com.peter.notification.demo.fragments

import android.app.NotificationChannel
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.peter.notification.demo.MainActivity
import com.peter.notification.demo.R
import com.peter.notification.demo.channel.ChannelGroupManager
import com.peter.notification.demo.channel.ChannelManager
import com.peter.notification.demo.databinding.FragmentChannelBinding

/**
 * Channel 管理 Fragment
 */
class ChannelFragment : Fragment() {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    private lateinit var channelManager: ChannelManager
    private lateinit var channelGroupManager: ChannelGroupManager

    companion object {
        fun newInstance() = ChannelFragment()
        
        fun dpToPx(dp: Int, context: android.content.Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelManager = ChannelManager(requireContext())
        channelGroupManager = ChannelGroupManager(requireContext())
        channelManager.initializeDefaultChannels()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createChannelItems()
        val adapter = ChannelAdapter(items) {
            channelManager.resetAllChannels()
            setupRecyclerView()
            (requireActivity() as MainActivity).showSnackbar("已重置所有 Channel")
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createChannelItems(): List<ChannelItem> {
        val items = mutableListOf<ChannelItem>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val groups = channelGroupManager.getAllGroups()
            val allChannels = channelManager.getAllChannels()

            groups.forEach { group ->
                val groupChannels = allChannels.filter { it.group == group.id }
                items.add(ChannelItem(
                    groupName = group.name,
                    channels = groupChannels,
                    shadowColorRes = R.color.category_message
                ))
            }

            val ungroupedChannels = allChannels.filter { channel ->
                channel.group.isNullOrEmpty() || groups.none { g -> g.id == channel.group }
            }
            if (ungroupedChannels.isNotEmpty()) {
                items.add(ChannelItem(
                    groupName = "其他 Channel",
                    channels = ungroupedChannels,
                    shadowColorRes = R.color.gray_500
                ))
            }
        }
        
        return items
    }

    data class ChannelItem(
        val groupName: String,
        val channels: List<NotificationChannel>,
        val shadowColorRes: Int
    )

    private class ChannelAdapter(
        private val items: List<ChannelItem>,
        private val onResetClick: () -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_HEADER = 0
        private val TYPE_GROUP = 1
        private val TYPE_BUTTON = 2

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> TYPE_HEADER
                itemCount - 1 -> TYPE_BUTTON
                else -> TYPE_GROUP
            }
        }

        override fun getItemCount(): Int = items.size + 2 // header + items + button

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_HEADER -> HeaderViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_channel_header, parent, false)
                )
                TYPE_BUTTON -> ButtonViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_channel_button, parent, false)
                )
                else -> GroupViewHolder(createGroupCard(parent))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is HeaderViewHolder -> {
                    val messageColor = ContextCompat.getColor(holder.itemView.context, R.color.category_message)
                    val elevation = 4f * holder.itemView.context.resources.displayMetrics.density
                    holder.cardView.elevation = elevation
                    holder.cardView.outlineAmbientShadowColor = messageColor
                    holder.cardView.outlineSpotShadowColor = messageColor
                }
                is ButtonViewHolder -> {
                    holder.btnReset.setOnClickListener { onResetClick() }
                }
                is GroupViewHolder -> {
                    val item = items[position - 1] // -1 因为第一项是 header
                    holder.bind(item)
                }
            }
        }

        private fun createGroupCard(parent: ViewGroup): MaterialCardView {
            val context = parent.context
            
            val cardView = MaterialCardView(context)
            cardView.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dpToPx(16, context), 0, dpToPx(16, context), dpToPx(10, context))
            }
            
            val cornerRadius = dpToPx(16, context).toFloat()
            cardView.shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
                .build()
            
            cardView.elevation = dpToPx(4, context).toFloat()
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            cardView.strokeWidth = 0
            
            return cardView
        }

        private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        }

        private inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val btnReset: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btnReset)
        }

        private inner class GroupViewHolder(private val cardView: MaterialCardView) : RecyclerView.ViewHolder(cardView) {
            
            fun bind(item: ChannelItem) {
                val context = cardView.context
                
                // 设置阴影颜色
                val shadowColor = ContextCompat.getColor(context, item.shadowColorRes)
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor
                
                // 清除旧内容
                cardView.removeAllViews()
                
                // 创建内容
                val contentLayout = LinearLayout(context)
                contentLayout.orientation = LinearLayout.VERTICAL
                contentLayout.setPadding(dpToPx(16, context), dpToPx(16, context), dpToPx(16, context), dpToPx(16, context))

                // 标题行
                val headerRow = LinearLayout(context)
                headerRow.orientation = LinearLayout.HORIZONTAL
                headerRow.gravity = android.view.Gravity.CENTER_VERTICAL

                // 颜色圆点
                val colorDot = View(context)
                val dotSize = dpToPx(8, context)
                colorDot.layoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
                val dotDrawable = GradientDrawable()
                dotDrawable.shape = GradientDrawable.OVAL
                dotDrawable.setColor(shadowColor)
                colorDot.background = dotDrawable
                headerRow.addView(colorDot)

                // 名称
                val nameTextView = TextView(context)
                nameTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dpToPx(12, context), 0, 0, 0)
                }
                nameTextView.text = item.groupName
                nameTextView.textSize = 15f
                nameTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface))
                nameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
                headerRow.addView(nameTextView)

                // 计数
                val countTextView = TextView(context)
                countTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dpToPx(8, context), 0, 0, 0)
                }
                countTextView.text = "${item.channels.size} 个"
                countTextView.textSize = 12f
                countTextView.setTextColor(shadowColor)
                headerRow.addView(countTextView)

                contentLayout.addView(headerRow)

                // Channel 列表
                item.channels.forEach { channel ->
                    val channelTextView = TextView(context)
                    channelTextView.text = "• ${channel.name}"
                    channelTextView.setPadding(dpToPx(20, context), dpToPx(4, context), 0, dpToPx(4, context))
                    channelTextView.textSize = 13f
                    channelTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface_variant))
                    contentLayout.addView(channelTextView)
                }

                cardView.addView(contentLayout)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}