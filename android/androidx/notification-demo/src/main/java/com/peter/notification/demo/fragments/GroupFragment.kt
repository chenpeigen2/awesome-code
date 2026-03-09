package com.peter.notification.demo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.peter.notification.demo.MainActivity
import com.peter.notification.demo.NotificationHelper
import com.peter.notification.demo.R
import com.peter.notification.demo.databinding.FragmentGroupBinding

/**
 * 分组通知 Fragment
 */
class GroupFragment : Fragment(R.layout.fragment_group) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupBinding.bind(view)

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
            (requireActivity() as MainActivity).showSnackbar(getString(R.string.msg_notifications_cleared))
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
        cardView.radius = dpToPx(8).toFloat()
        cardView.cardElevation = dpToPx(2).toFloat()

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

        val colorBar = View(context)
        colorBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(4)
        )
        colorBar.setBackgroundColor(Color.parseColor(group.color))
        linearLayout.addView(colorBar)

        val titleTextView = TextView(context)
        titleTextView.text = group.name
        titleTextView.textSize = 18f
        titleTextView.setPadding(0, dpToPx(8), 0, 0)
        linearLayout.addView(titleTextView)

        val countTextView = TextView(context)
        countTextView.text = "0 条通知"
        countTextView.setTextColor(Color.GRAY)
        countTextView.tag = "count_${group.key}"
        linearLayout.addView(countTextView)

        val buttonContainer = LinearLayout(context)
        buttonContainer.orientation = LinearLayout.HORIZONTAL
        buttonContainer.setPadding(0, dpToPx(8), 0, 0)

        val btnSendOne = Button(context)
        btnSendOne.text = "发送一条"
        btnSendOne.setOnClickListener {
            if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                sendGroupNotification(group)
                updateCountTextView(linearLayout, group.key)
            } else {
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
            }
        }
        buttonContainer.addView(btnSendOne)

        val btnSendFive = Button(context)
        btnSendFive.text = "发送5条"
        btnSendFive.setOnClickListener {
            if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                repeat(5) { sendGroupNotification(group) }
                updateCountTextView(linearLayout, group.key)
            } else {
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
            }
        }
        buttonContainer.addView(btnSendFive)

        val btnClear = Button(context)
        btnClear.text = "清除"
        btnClear.setOnClickListener {
            notificationHelper.clearGroupNotifications(group.key)
            notificationCounters[group.key] = 0
            updateCountTextView(linearLayout, group.key)
        }
        buttonContainer.addView(btnClear)

        linearLayout.addView(buttonContainer)
        cardView.addView(linearLayout)

        return cardView
    }

    private fun updateCountTextView(linearLayout: LinearLayout, groupKey: String) {
        for (i in 0 until linearLayout.childCount) {
            val view = linearLayout.getChildAt(i)
            if (view is TextView && view.tag == "count_$groupKey") {
                view.text = "${notificationCounters[groupKey] ?: 0} 条通知"
                break
            }
        }
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
        (requireActivity() as MainActivity).showSnackbar(getString(R.string.msg_notification_sent))
    }

    private fun sendAllGroupNotifications() {
        groups.forEach { group ->
            sendGroupNotification(group)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
