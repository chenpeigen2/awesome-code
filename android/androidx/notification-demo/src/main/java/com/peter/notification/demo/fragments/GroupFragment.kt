package com.peter.notification.demo.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
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
        GroupData(NotificationHelper.GROUP_KEY_CHAT, "聊天消息", "💬", "#6750A4", R.drawable.bg_card_shadow_basic),
        GroupData(NotificationHelper.GROUP_KEY_EMAIL, "邮件", "📧", "#386A20", R.drawable.bg_card_shadow_message),
        GroupData(NotificationHelper.GROUP_KEY_SOCIAL, "社交动态", "👥", "#7D5260", R.drawable.bg_card_shadow_advanced),
        GroupData(NotificationHelper.GROUP_KEY_SYSTEM, "系统提醒", "🔔", "#49454F", R.drawable.bg_card_shadow_gray)
    )

    private var notificationCounters = mutableMapOf<String, Int>()

    data class GroupData(
        val key: String,
        val name: String,
        val icon: String,
        val color: String,
        val shadowRes: Int
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

    private fun createGroupCard(group: GroupData): FrameLayout {
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
        shadowView.setBackgroundResource(group.shadowRes)
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

        // 内容布局
        val contentLayout = LinearLayout(context)
        contentLayout.orientation = LinearLayout.VERTICAL
        contentLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(12))

        // 顶部：图标和名称
        val headerRow = LinearLayout(context)
        headerRow.orientation = LinearLayout.HORIZONTAL
        headerRow.gravity = android.view.Gravity.CENTER_VERTICAL

        // 颜色圆点
        val colorDot = View(context)
        val dotSize = dpToPx(8)
        colorDot.layoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
        val dotDrawable = GradientDrawable()
        dotDrawable.shape = GradientDrawable.OVAL
        dotDrawable.setColor(Color.parseColor(group.color))
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
        nameTextView.text = group.name
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
        countTextView.text = "${notificationCounters[group.key] ?: 0} 条"
        countTextView.textSize = 12f
        countTextView.setTextColor(Color.parseColor(group.color))
        countTextView.tag = "count_${group.key}"
        headerRow.addView(countTextView)

        contentLayout.addView(headerRow)

        // 按钮行
        val buttonContainer = LinearLayout(context)
        buttonContainer.orientation = LinearLayout.HORIZONTAL
        buttonContainer.setPadding(0, dpToPx(12), 0, 0)
        buttonContainer.gravity = android.view.Gravity.CENTER

        // +1 按钮
        val btnSendOne = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle)
        btnSendOne.apply {
            text = "+1"
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(36), 1f).apply {
                setMargins(0, 0, dpToPx(6), 0)
            }
            cornerRadius = dpToPx(10)
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

        // +5 按钮
        val btnSendFive = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle)
        btnSendFive.apply {
            text = "+5"
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(36), 1f).apply {
                setMargins(dpToPx(6), 0, dpToPx(6), 0)
            }
            cornerRadius = dpToPx(10)
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
            setOnClickListener {
                notificationHelper.clearGroupNotifications(group.key)
                notificationCounters[group.key] = 0
                refreshAllCards()
            }
        }
        buttonContainer.addView(btnClear)

        contentLayout.addView(buttonContainer)
        cardView.addView(contentLayout)
        container.addView(cardView)

        return container
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
