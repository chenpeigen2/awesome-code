package com.peter.notification.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.notification.demo.*
import com.peter.notification.demo.databinding.FragmentTypeBinding

/**
 * 通知类型 Fragment
 */
class TypeFragment : Fragment() {

    private var _binding: FragmentTypeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationHelper: NotificationHelper

    companion object {
        fun newInstance() = TypeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationHelper = NotificationHelper(requireContext())
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createNotificationItems()
        val adapter = NotificationAdapter(items) { type ->
            if ((requireActivity() as MainActivity).hasNotificationPermission()) {
                notificationHelper.sendNotificationByType(type)
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.msg_notification_sent))
            } else {
                (requireActivity() as MainActivity).showSnackbar(getString(R.string.permission_required))
            }
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createNotificationItems(): List<NotificationItem> {
        return listOf(
            // 基础通知
            NotificationItem(
                type = NotificationType.NORMAL,
                title = getString(R.string.type_normal),
                description = getString(R.string.type_normal_desc),
                category = NotificationCategory.BASIC
            ),
            NotificationItem(
                type = NotificationType.BIG_TEXT,
                title = getString(R.string.type_big_text),
                description = getString(R.string.type_big_text_desc),
                category = NotificationCategory.BASIC
            ),
            NotificationItem(
                type = NotificationType.BIG_PICTURE,
                title = getString(R.string.type_big_picture),
                description = getString(R.string.type_big_picture_desc),
                category = NotificationCategory.BASIC
            ),
            NotificationItem(
                type = NotificationType.INBOX,
                title = getString(R.string.type_inbox),
                description = getString(R.string.type_inbox_desc),
                category = NotificationCategory.BASIC
            ),
            // 消息通知
            NotificationItem(
                type = NotificationType.MESSAGING,
                title = getString(R.string.type_messaging),
                description = getString(R.string.type_messaging_desc),
                category = NotificationCategory.MESSAGE
            ),
            NotificationItem(
                type = NotificationType.CONVERSATION,
                title = getString(R.string.type_conversation),
                description = getString(R.string.type_conversation_desc),
                category = NotificationCategory.MESSAGE
            ),
            NotificationItem(
                type = NotificationType.BUBBLE,
                title = getString(R.string.type_bubble),
                description = getString(R.string.type_bubble_desc),
                category = NotificationCategory.MESSAGE
            ),
            NotificationItem(
                type = NotificationType.REPLY,
                title = getString(R.string.type_reply),
                description = getString(R.string.type_reply_desc),
                category = NotificationCategory.MESSAGE
            ),
            NotificationItem(
                type = NotificationType.SOCIAL,
                title = getString(R.string.type_social),
                description = getString(R.string.type_social_desc),
                category = NotificationCategory.MESSAGE
            ),
            // 进度与状态
            NotificationItem(
                type = NotificationType.PROGRESS,
                title = getString(R.string.type_progress),
                description = getString(R.string.type_progress_desc),
                category = NotificationCategory.PROGRESS
            ),
            NotificationItem(
                type = NotificationType.ONGOING,
                title = getString(R.string.type_ongoing),
                description = getString(R.string.type_ongoing_desc),
                category = NotificationCategory.PROGRESS
            ),
            NotificationItem(
                type = NotificationType.FOREGROUND,
                title = getString(R.string.type_foreground),
                description = getString(R.string.type_foreground_desc),
                category = NotificationCategory.PROGRESS
            ),
            // 媒体通知
            NotificationItem(
                type = NotificationType.MEDIA,
                title = getString(R.string.type_media),
                description = getString(R.string.type_media_desc),
                category = NotificationCategory.MEDIA
            ),
            // 高级功能
            NotificationItem(
                type = NotificationType.CUSTOM,
                title = getString(R.string.type_custom),
                description = getString(R.string.type_custom_desc),
                category = NotificationCategory.ADVANCED
            ),
            NotificationItem(
                type = NotificationType.SCHEDULED,
                title = getString(R.string.type_scheduled),
                description = getString(R.string.type_scheduled_desc),
                category = NotificationCategory.ADVANCED
            ),
            NotificationItem(
                type = NotificationType.ACTION,
                title = getString(R.string.type_action),
                description = getString(R.string.type_action_desc),
                category = NotificationCategory.ADVANCED
            ),
            NotificationItem(
                type = NotificationType.CONFIRM,
                title = getString(R.string.type_confirm),
                description = getString(R.string.type_confirm_desc),
                category = NotificationCategory.ADVANCED
            ),
            NotificationItem(
                type = NotificationType.DOWNLOAD,
                title = getString(R.string.type_download),
                description = getString(R.string.type_download_desc),
                category = NotificationCategory.ADVANCED
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
