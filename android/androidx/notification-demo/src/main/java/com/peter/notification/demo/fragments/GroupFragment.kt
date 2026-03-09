package com.peter.notification.demo.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.peter.notification.demo.MainActivity
import com.peter.notification.demo.NotificationHelper
import com.peter.notification.demo.R
import com.peter.notification.demo.databinding.FragmentGroupBinding

/**
 * 分组通知 Fragment
 */
class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationHelper: NotificationHelper

    private val groups = listOf(
        GroupData(NotificationHelper.GROUP_KEY_CHAT, "聊天消息", "#6750A4", "💬"),
        GroupData(NotificationHelper.GROUP_KEY_EMAIL, "邮件通知", "#386A20", "📧"),
        GroupData(NotificationHelper.GROUP_KEY_SOCIAL, "社交动态", "#0061A4", "❤️"),
        GroupData(NotificationHelper.GROUP_KEY_SYSTEM, "系统提醒", "#49454F", "⚙️")
    )

    private var notificationCounters = mutableMapOf<String, Int>()

    data class GroupData(
        val key: String,
        val name: String,
        val color: String,
        val icon: String
    )

    companion object {
        fun newInstance() = GroupFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationHelper = NotificationHelper(requireContext())
        setupViews()
    }

    private fun setupViews() {
        groups.forEach { group ->
            val cardView = createGroupCard(group)
            binding.containerGroups.addView(cardView)
        }

        binding.btnSendAll.setOnClickListener {
            if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                sendAllGroupNotifications()
            } else {
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
            }
        }

        binding.btnClearAll.setOnClickListener {
            notificationHelper.clearAllNotifications()
            notificationCounters.clear()
            refreshAllCards()
            (requireActivity() as MainActivity).showSnackbar(getString(R.string.msg_notifications_cleared))
        }
    }

    private fun refreshAllCards() {
        binding.containerGroups.removeAllViews()
        groups.forEach { group ->
            val cardView = createGroupCard(group)
            binding.containerGroups.addView(cardView)
        }
    }

    private fun createGroupCard(group: GroupData): CardView {
        val context = requireContext()
        
        val cardView = CardView(context)
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dpToPx(12))
        }
        cardView.radius = dpToPx(20).toFloat()
        cardView.cardElevation = dpToPx(1).toFloat()
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface))
        cardView.preventCornerOverlap = false

        val containerLayout = LinearLayout(context)
        containerLayout.orientation = LinearLayout.VERTICAL

        // 顶部彩色条
        val colorBar = View(context)
        colorBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(4)
        )
        val drawable = GradientDrawable()
        drawable.cornerRadius = dpToPx(2).toFloat()
        drawable.setColor(Color.parseColor(group.color))
        colorBar.background = drawable
        containerLayout.addView(colorBar)

        // 内容区域
        val contentLayout = LinearLayout(context)
        contentLayout.orientation = LinearLayout.HORIZONTAL
        contentLayout.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))
        contentLayout.gravity = android.view.Gravity.CENTER_VERTICAL

        // 图标背景
        val iconBg = View(context)
        val iconBgSize = dpToPx(44)
        iconBg.layoutParams = LinearLayout.LayoutParams(iconBgSize, iconBgSize)
        val iconBgDrawable = GradientDrawable()
        iconBgDrawable.cornerRadius = dpToPx(12).toFloat()
        iconBgDrawable.setColor(Color.parseColor(group.color + "20")) // 20 = 12.5% alpha
        iconBg.background = iconBgDrawable

        // 图标和名称容器
        val textContainer = LinearLayout(context)
        textContainer.orientation = LinearLayout.VERTICAL
        (textContainer.layoutParams as LinearLayout.LayoutParams).apply {
            setMargins(dpToPx(14), 0, 0, 0)
        }
        
        // 名称
        val nameTextView = TextView(context)
        nameTextView.text = "${group.icon} ${group.name}"
        nameTextView.textSize = 16f
        nameTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface))
        nameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        textContainer.addView(nameTextView)

        // 计数
        val countTextView = TextView(context)
        countTextView.text = "${notificationCounters[group.key] ?: 0} 条通知"
        countTextView.textSize = 13f
        countTextView.setTextColor(Color.parseColor(group.color))
        countTextView.tag = "count_${group.key}"
        textContainer.addView(countTextView)

        contentLayout.addView(iconBg)
        contentLayout.addView(textContainer)

        // 按钮行
        val buttonContainer = LinearLayout(context)
        buttonContainer.orientation = LinearLayout.HORIZONTAL
        buttonContainer.setPadding(dpToPx(12), 0, dpToPx(12), dpToPx(12))
        buttonContainer.gravity = android.view.Gravity.CENTER

        // 发送一条按钮
        val btnSendOne = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle)
        btnSendOne.apply {
            text = "+1"
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(36), 1f).apply {
                setMargins(0, 0, dpToPx(6), 0)
            }
            cornerRadius = dpToPx(10)
            setTextColor(Color.parseColor(group.color))
            setBackgroundColor(Color.parseColor(group.color + "15"))
            setOnClickListener {
                if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                    sendGroupNotification(group)
                    refreshAllCards()
                } else {
                    (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
                }
            }
        }
        buttonContainer.addView(btnSendOne)

        // 发送多条按钮
        val btnSendFive = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle)
        btnSendFive.apply {
            text = "+5"
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(36), 1f).apply {
                setMargins(dpToPx(6), 0, dpToPx(6), 0)
            }
            cornerRadius = dpToPx(10)
            setTextColor(Color.parseColor(group.color))
            setBackgroundColor(Color.parseColor(group.color + "15"))
            setOnClickListener {
                if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                    repeat(5) { sendGroupNotification(group) }
                    refreshAllCards()
                } else {
                    (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
                }
            }
        }
        buttonContainer.addView(btnSendFive)

        // 清除按钮
        val btnClear = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
        btnClear.apply {
            text = "清除"
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(36), 1f).apply {
                setMargins(dpToPx(6), 0, 0, 0)
            }
            cornerRadius = dpToPx(10)
            strokeWidth = dpToPx(1)
            strokeColor = android.content.res.ColorStateList.valueOf(Color.parseColor(group.color + "50"))
            setTextColor(Color.parseColor(group.color))
            setOnClickListener {
                notificationHelper.clearGroupNotifications(group.key)
                notificationCounters[group.key] = 0
                refreshAllCards()
            }
        }
        buttonContainer.addView(btnClear)

        containerLayout.addView(contentLayout)
        containerLayout.addView(buttonContainer)
        cardView.addView(containerLayout)

        return cardView
    }

    private fun sendGroupNotification(group: GroupData) {
        val counter = (notificationCounters[group.key] ?: 0) + 1
        notificationCounters[group.key] = counter

        val notificationId = (group.key.hashCode() and 0xFFFF) + counter

        val titles = when (group.key) {
            NotificationHelper.GROUP_KEY_CHAT -> listOf("张三", "李四", "王五", "赵六")
            NotificationHelper.GROUP_KEY_EMAIL -> listOf("工作邮件", "订阅邮件", "通知邮件", "私人邮件")
            NotificationHelper.GROUP_KEY_SOCIAL -> listOf("点赞", "评论", "关注", "分享")
            NotificationHelper.GROUP_KEY_SYSTEM -> listOf("系统更新", "存储提醒", "安全警告", "备份完成")
            else -> listOf("通知")
        }

        val title = titles.random()
        val content = "第 ${counter} 条消息"

        notificationHelper.sendGroupNotification(group.key, notificationId, title, content)
    }

    private fun sendAllGroupNotifications() {
        groups.forEach { group ->
            sendGroupNotification(group)
        }
        refreshAllCards()
        (requireActivity() as MainActivity).showSnackbar(getString(R.string.msg_notification_sent))
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}