package com.peter.notification.demo.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        GroupData(NotificationHelper.GROUP_KEY_CHAT, "聊天消息", "#2196F3"),
        GroupData(NotificationHelper.GROUP_KEY_EMAIL, "邮件", "#4CAF50"),
        GroupData(NotificationHelper.GROUP_KEY_SOCIAL, "社交动态", "#FF9800"),
        GroupData(NotificationHelper.GROUP_KEY_SYSTEM, "系统提醒", "#9E9E9E")
    )

    private var notificationCounters = mutableMapOf<String, Int>()

    data class GroupData(
        val key: String,
        val name: String,
        val color: String
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
        val cardView = CardView(requireContext())
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dpToPx(8))
        }
        cardView.radius = dpToPx(12).toFloat()
        cardView.cardElevation = dpToPx(2).toFloat()
        cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        // 颜色条
        val colorBar = View(context)
        colorBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(4)
        )
        val drawable = GradientDrawable()
        drawable.cornerRadius = dpToPx(2).toFloat()
        drawable.setColor(Color.parseColor(group.color))
        colorBar.background = drawable
        linearLayout.addView(colorBar)

        // 标题行
        val titleRow = LinearLayout(context)
        titleRow.orientation = LinearLayout.HORIZONTAL
        titleRow.setPadding(0, dpToPx(12), 0, 0)

        val titleTextView = TextView(context)
        titleTextView.text = group.name
        titleTextView.textSize = 18f
        titleTextView.setTextColor(Color.BLACK)
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        titleRow.addView(titleTextView)

        val countTextView = TextView(context)
        countTextView.text = "  ${notificationCounters[group.key] ?: 0} 条"
        countTextView.textSize = 14f
        countTextView.setTextColor(Color.parseColor(group.color))
        countTextView.tag = "count_${group.key}"
        titleRow.addView(countTextView)

        linearLayout.addView(titleRow)

        // 按钮行
        val buttonContainer = LinearLayout(context)
        buttonContainer.orientation = LinearLayout.HORIZONTAL
        buttonContainer.setPadding(0, dpToPx(12), 0, 0)

        val btnSendOne = Button(context)
        btnSendOne.text = "发送一条"
        btnSendOne.setPadding(dpToPx(16), 0, dpToPx(16), 0)
        btnSendOne.setOnClickListener {
            if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                sendGroupNotification(group)
                refreshAllCards()
            } else {
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
            }
        }
        buttonContainer.addView(btnSendOne)

        val btnSendFive = Button(context)
        btnSendFive.text = "发送5条"
        btnSendFive.setPadding(dpToPx(16), 0, dpToPx(16), 0)
        btnSendFive.setOnClickListener {
            if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                repeat(5) { sendGroupNotification(group) }
                refreshAllCards()
            } else {
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
            }
        }
        buttonContainer.addView(btnSendFive)

        val btnClear = Button(context)
        btnClear.text = "清除"
        btnClear.setPadding(dpToPx(16), 0, dpToPx(16), 0)
        btnClear.setOnClickListener {
            notificationHelper.clearGroupNotifications(group.key)
            notificationCounters[group.key] = 0
            refreshAllCards()
        }
        buttonContainer.addView(btnClear)

        linearLayout.addView(buttonContainer)
        cardView.addView(linearLayout)

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
