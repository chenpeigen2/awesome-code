package com.peter.notification.demo.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
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
        GroupData(NotificationHelper.GROUP_KEY_CHAT, "聊天消息", "#6750A4", R.color.category_basic),
        GroupData(NotificationHelper.GROUP_KEY_EMAIL, "邮件", "#386A20", R.color.category_message),
        GroupData(NotificationHelper.GROUP_KEY_SOCIAL, "社交动态", "#7D5260", R.color.category_advanced),
        GroupData(NotificationHelper.GROUP_KEY_SYSTEM, "系统提醒", "#49454F", R.color.gray_500)
    )

    private var notificationCounters = mutableMapOf<String, Int>()

    data class GroupData(
        val key: String,
        val name: String,
        val color: String,
        val shadowColorRes: Int
    )

    companion object {
        fun newInstance() = GroupFragment()
        
        fun dpToPx(dp: Int, context: android.content.Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = GroupAdapter(
            groups = groups,
            counters = notificationCounters,
            onSendOne = { group -> sendGroupNotification(group) },
            onSendFive = { group -> repeat(5) { sendGroupNotification(group) } },
            onClear = { group ->
                notificationHelper.clearGroupNotifications(group.key)
                notificationCounters[group.key] = 0
            },
            onSendAll = { sendAllGroupNotifications() },
            onClearAll = {
                notificationHelper.clearAllNotifications()
                notificationCounters.clear()
            },
            hasPermission = { (requireActivity() as MainActivity).hasNotificationPermission() },
            showSnackbar = { msg -> (requireActivity() as MainActivity).showSnackbar(msg) }
        )
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
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
    }

    private fun sendAllGroupNotifications() {
        groups.forEach { group ->
            sendGroupNotification(group)
        }
        (requireActivity() as MainActivity).showSnackbar(getString(R.string.msg_notification_sent))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class GroupAdapter(
        private val groups: List<GroupData>,
        private val counters: MutableMap<String, Int>,
        private val onSendOne: (GroupData) -> Unit,
        private val onSendFive: (GroupData) -> Unit,
        private val onClear: (GroupData) -> Unit,
        private val onSendAll: () -> Unit,
        private val onClearAll: () -> Unit,
        private val hasPermission: () -> Boolean,
        private val showSnackbar: (String) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_HEADER = 0
            private const val TYPE_GROUP = 1
            private const val TYPE_FOOTER = 2
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> TYPE_HEADER
                itemCount - 1 -> TYPE_FOOTER
                else -> TYPE_GROUP
            }
        }

        override fun getItemCount(): Int = groups.size + 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_HEADER -> HeaderViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_group_header, parent, false)
                )
                TYPE_FOOTER -> FooterViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_group_footer, parent, false)
                )
                else -> GroupViewHolder(createGroupCard(parent))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is HeaderViewHolder -> {
                    val mediaColor = ContextCompat.getColor(holder.itemView.context, R.color.category_media)
                    val elevation = 4f * holder.itemView.context.resources.displayMetrics.density
                    holder.cardView.elevation = elevation
                    holder.cardView.outlineAmbientShadowColor = mediaColor
                    holder.cardView.outlineSpotShadowColor = mediaColor
                }
                is FooterViewHolder -> {
                    holder.btnSendAll.setOnClickListener {
                        if (hasPermission()) {
                            onSendAll()
                            notifyDataSetChanged()
                        } else {
                            showSnackbar("需要通知权限")
                        }
                    }
                    holder.btnClearAll.setOnClickListener {
                        onClearAll()
                        notifyDataSetChanged()
                        showSnackbar("已清除所有通知")
                    }
                }
                is GroupViewHolder -> {
                    val group = groups[position - 1]
                    holder.bind(group, counters[group.key] ?: 0)
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

        private inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val btnSendAll: MaterialButton = itemView.findViewById(R.id.btnSendAll)
            val btnClearAll: MaterialButton = itemView.findViewById(R.id.btnClearAll)
        }

        private inner class GroupViewHolder(private val cardView: MaterialCardView) : RecyclerView.ViewHolder(cardView) {
            
            fun bind(group: GroupData, count: Int) {
                val context = cardView.context
                val shadowColor = ContextCompat.getColor(context, group.shadowColorRes)
                
                cardView.outlineAmbientShadowColor = shadowColor
                cardView.outlineSpotShadowColor = shadowColor
                
                cardView.removeAllViews()
                
                val contentLayout = LinearLayout(context)
                contentLayout.orientation = LinearLayout.VERTICAL
                contentLayout.setPadding(dpToPx(16, context), dpToPx(16, context), dpToPx(16, context), dpToPx(12, context))

                // 标题行
                val headerRow = LinearLayout(context)
                headerRow.orientation = LinearLayout.HORIZONTAL
                headerRow.gravity = android.view.Gravity.CENTER_VERTICAL

                val colorDot = View(context)
                val dotSize = dpToPx(8, context)
                colorDot.layoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
                val dotDrawable = GradientDrawable()
                dotDrawable.shape = GradientDrawable.OVAL
                dotDrawable.setColor(Color.parseColor(group.color))
                colorDot.background = dotDrawable
                headerRow.addView(colorDot)

                val nameTextView = TextView(context)
                nameTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dpToPx(12, context), 0, 0, 0)
                }
                nameTextView.text = group.name
                nameTextView.textSize = 15f
                nameTextView.setTextColor(ContextCompat.getColor(context, R.color.on_surface))
                nameTextView.setTypeface(null, android.graphics.Typeface.BOLD)
                headerRow.addView(nameTextView)

                val countTextView = TextView(context)
                countTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dpToPx(8, context), 0, 0, 0)
                }
                countTextView.text = "$count 条"
                countTextView.textSize = 12f
                countTextView.setTextColor(Color.parseColor(group.color))
                headerRow.addView(countTextView)

                contentLayout.addView(headerRow)

                // 按钮行
                val buttonContainer = LinearLayout(context)
                buttonContainer.orientation = LinearLayout.HORIZONTAL
                buttonContainer.setPadding(0, dpToPx(12, context), 0, 0)
                buttonContainer.gravity = android.view.Gravity.CENTER

                val btnSendOne = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle)
                btnSendOne.apply {
                    text = "+1"
                    textSize = 13f
                    layoutParams = LinearLayout.LayoutParams(0, dpToPx(36, context), 1f).apply {
                        setMargins(0, 0, dpToPx(6, context), 0)
                    }
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, dpToPx(10, context).toFloat())
                        .build()
                    setOnClickListener {
                        if (hasPermission()) {
                            onSendOne(group)
                            notifyDataSetChanged()
                        } else {
                            showSnackbar("需要通知权限")
                        }
                    }
                }
                buttonContainer.addView(btnSendOne)

                val btnSendFive = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonStyle)
                btnSendFive.apply {
                    text = "+5"
                    textSize = 13f
                    layoutParams = LinearLayout.LayoutParams(0, dpToPx(36, context), 1f).apply {
                        setMargins(dpToPx(6, context), 0, dpToPx(6, context), 0)
                    }
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, dpToPx(10, context).toFloat())
                        .build()
                    setOnClickListener {
                        if (hasPermission()) {
                            onSendFive(group)
                            notifyDataSetChanged()
                        } else {
                            showSnackbar("需要通知权限")
                        }
                    }
                }
                buttonContainer.addView(btnSendFive)

                val btnClear = MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
                btnClear.apply {
                    text = "清除"
                    textSize = 13f
                    layoutParams = LinearLayout.LayoutParams(0, dpToPx(36, context), 1f).apply {
                        setMargins(dpToPx(6, context), 0, 0, 0)
                    }
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, dpToPx(10, context).toFloat())
                        .build()
                    setOnClickListener {
                        onClear(group)
                        notifyDataSetChanged()
                    }
                }
                buttonContainer.addView(btnClear)

                contentLayout.addView(buttonContainer)
                cardView.addView(contentLayout)
            }
        }
    }
}