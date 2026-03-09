package com.peter.notification.demo.fragments

import android.app.NotificationChannel
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
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
        setupViews()
    }

    private fun setupViews() {
        setupGroupsList()
        setupClickListeners()
    }

    private fun setupGroupsList() {
        binding.containerGroups.removeAllViews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val groups = channelGroupManager.getAllGroups()
            val allChannels = channelManager.getAllChannels()

            groups.forEach { group ->
                val groupChannels = allChannels.filter { it.group == group.id }
                val cardView = createGroupCard(group.name, groupChannels)
                binding.containerGroups.addView(cardView)
            }

            val ungroupedChannels = allChannels.filter { channel ->
                channel.group.isNullOrEmpty() || groups.none { g -> g.id == channel.group }
            }
            if (ungroupedChannels.isNotEmpty()) {
                val cardView = createUngroupedChannelsCard(ungroupedChannels)
                binding.containerGroups.addView(cardView)
            }
        } else {
            binding.tvInfo.text = "Channel 功能需要 Android 8.0+"
        }
    }

    private fun createGroupCard(groupName: String, channels: List<NotificationChannel>): FrameLayout {
        val context = requireContext()
        
        // 容器
        val container = FrameLayout(context)
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dpToPx(10))
        }

        // 颜色阴影层
        val shadowView = View(context)
        shadowView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ).apply {
            setMargins(dpToPx(3), dpToPx(3), 0, 0)
        }
        shadowView.setBackgroundResource(R.drawable.bg_card_shadow_message)
        shadowView.alpha = 0.5f
        container.addView(shadowView)

        // 主卡片
        val cardView = MaterialCardView(context)
        cardView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.radius = dpToPx(16).toFloat()
        cardView.cardElevation = 0f
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        cardView.strokeWidth = 0

        val contentLayout = LinearLayout(context)
        contentLayout.orientation = LinearLayout.VERTICAL
        contentLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        // 标题行：圆点 + 名称
        val headerRow = LinearLayout(context)
        headerRow.orientation = LinearLayout.HORIZONTAL
        headerRow.gravity = android.view.Gravity.CENTER_VERTICAL

        // 颜色圆点
        val colorDot = View(context)
        val dotSize = dpToPx(8)
        colorDot.layoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
        val dotDrawable = GradientDrawable()
        dotDrawable.shape = GradientDrawable.OVAL
        dotDrawable.setColor(ContextCompat.getColor(context, R.color.category_message))
        colorDot.background = dotDrawable
        headerRow.addView(colorDot)

        // 名称
        val nameTextView = TextView(context)
        nameTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(dpToPx(12), 0, 0, 0)
        }
        nameTextView.text = groupName
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
            setMargins(dpToPx(8), 0, 0, 0)
        }
        countTextView.text = "${channels.size} 个"
        countTextView.textSize = 12f
        countTextView.setTextColor(ContextCompat.getColor(context, R.color.category_message))
        headerRow.addView(countTextView)

        contentLayout.addView(headerRow)

        // Channel 列表
        channels.forEach { channel ->
            val channelTextView = TextView(context)
            channelTextView.text = "• ${channel.name}"
            channelTextView.setPadding(dpToPx(20), dpToPx(4), 0, dpToPx(4))
            channelTextView.textSize = 13f
            channelTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface_variant))
            contentLayout.addView(channelTextView)
        }

        cardView.addView(contentLayout)
        container.addView(cardView)

        return container
    }

    private fun createUngroupedChannelsCard(channels: List<NotificationChannel>): FrameLayout {
        val context = requireContext()
        
        // 容器
        val container = FrameLayout(context)
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dpToPx(10))
        }

        // 颜色阴影层
        val shadowView = View(context)
        shadowView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ).apply {
            setMargins(dpToPx(3), dpToPx(3), 0, 0)
        }
        shadowView.setBackgroundResource(R.drawable.bg_card_shadow_gray)
        shadowView.alpha = 0.5f
        container.addView(shadowView)

        // 主卡片
        val cardView = MaterialCardView(context)
        cardView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.radius = dpToPx(16).toFloat()
        cardView.cardElevation = 0f
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        cardView.strokeWidth = 0

        val contentLayout = LinearLayout(context)
        contentLayout.orientation = LinearLayout.VERTICAL
        contentLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        // 标题行
        val headerRow = LinearLayout(context)
        headerRow.orientation = LinearLayout.HORIZONTAL
        headerRow.gravity = android.view.Gravity.CENTER_VERTICAL

        val colorDot = View(context)
        val dotSize = dpToPx(8)
        colorDot.layoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
        val dotDrawable = GradientDrawable()
        dotDrawable.shape = GradientDrawable.OVAL
        dotDrawable.setColor(ContextCompat.getColor(context, R.color.gray_500))
        colorDot.background = dotDrawable
        headerRow.addView(colorDot)

        val nameTextView = TextView(context)
        nameTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(dpToPx(12), 0, 0, 0)
        }
        nameTextView.text = "其他 Channel"
        nameTextView.textSize = 15f
        nameTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface))
        nameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        headerRow.addView(nameTextView)

        val countTextView = TextView(context)
        countTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(dpToPx(8), 0, 0, 0)
        }
        countTextView.text = "${channels.size} 个"
        countTextView.textSize = 12f
        countTextView.setTextColor(ContextCompat.getColor(context, R.color.gray_500))
        headerRow.addView(countTextView)

        contentLayout.addView(headerRow)

        channels.forEach { channel ->
            val channelTextView = TextView(context)
            channelTextView.text = "• ${channel.name}"
            channelTextView.setPadding(dpToPx(20), dpToPx(4), 0, dpToPx(4))
            channelTextView.textSize = 13f
            channelTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface_variant))
            contentLayout.addView(channelTextView)
        }

        cardView.addView(contentLayout)
        container.addView(cardView)

        return container
    }

    private fun setupClickListeners() {
        binding.btnReset.setOnClickListener {
            channelManager.resetAllChannels()
            setupGroupsList()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onResume() {
        super.onResume()
        setupGroupsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
