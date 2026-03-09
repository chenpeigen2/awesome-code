package com.peter.notification.demo.fragments

import android.app.NotificationChannel
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.peter.notification.demo.R
import com.peter.notification.demo.channel.ChannelGroupManager
import com.peter.notification.demo.channel.ChannelManager
import com.peter.notification.demo.databinding.FragmentChannelBinding

/**
 * Channel 管理 Fragment
 */
class ChannelFragment : Fragment(R.layout.fragment_channel) {

    private var _binding: FragmentChannelBinding? = null
    private val binding get() = _binding!!

    private lateinit var channelManager: ChannelManager
    private lateinit var channelGroupManager: ChannelGroupManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChannelBinding.bind(view)

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

            // 添加未分组的 Channel
            val ungroupedChannels = allChannels.filter { channel ->
                channel.group.isNullOrEmpty() || groups.none { g -> g.id == channel.group }
            }
            if (ungroupedChannels.isNotEmpty()) {
                val cardView = createUngroupedChannelsCard(ungroupedChannels)
                binding.containerGroups.addView(cardView)
            }
        } else {
            binding.tvInfo.visibility = View.VISIBLE
            binding.tvInfo.text = "Channel 功能需要 Android 8.0 (API 26) 及以上版本"
        }
    }

    private fun createGroupCard(groupName: String, channels: List<NotificationChannel>): CardView {
        val cardView = CardView(requireContext())
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dpToPx(8))
        }
        cardView.radius = dpToPx(8).toFloat()
        cardView.cardElevation = dpToPx(2).toFloat()

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        val titleTextView = TextView(context)
        titleTextView.text = groupName
        titleTextView.textSize = 18f
        titleTextView.setTextColor(Color.BLACK)
        linearLayout.addView(titleTextView)

        val countTextView = TextView(context)
        countTextView.text = "${channels.size} 个 Channel"
        countTextView.setTextColor(Color.GRAY)
        linearLayout.addView(countTextView)

        channels.forEach { channel ->
            val channelTextView = TextView(context)
            channelTextView.text = "• ${channel.name} (${channel.id})"
            channelTextView.setPadding(dpToPx(16), dpToPx(4), 0, dpToPx(4))
            linearLayout.addView(channelTextView)
        }

        cardView.addView(linearLayout)
        return cardView
    }

    private fun createUngroupedChannelsCard(channels: List<NotificationChannel>): CardView {
        val cardView = CardView(requireContext())
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dpToPx(8))
        }
        cardView.radius = dpToPx(8).toFloat()
        cardView.cardElevation = dpToPx(2).toFloat()

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        val titleTextView = TextView(context)
        titleTextView.text = "其他 Channel"
        titleTextView.textSize = 18f
        titleTextView.setTextColor(Color.BLACK)
        linearLayout.addView(titleTextView)

        channels.forEach { channel: NotificationChannel ->
            val channelTextView = TextView(context)
            channelTextView.text = "• ${channel.name} (${channel.id})"
            channelTextView.setPadding(dpToPx(16), dpToPx(4), 0, dpToPx(4))
            linearLayout.addView(channelTextView)
        }

        cardView.addView(linearLayout)
        return cardView
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
