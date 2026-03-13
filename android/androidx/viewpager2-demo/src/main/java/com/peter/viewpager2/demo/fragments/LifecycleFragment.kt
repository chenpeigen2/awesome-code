package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentLifecycleBinding
import com.peter.viewpager2.demo.databinding.ItemLifecycleLogBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment 生命周期演示
 * 展示 Fragment 的生命周期回调
 */
class LifecycleFragment : Fragment() {

    private var _binding: FragmentLifecycleBinding? = null
    private val binding get() = _binding!!

    private val logs = mutableListOf<String>()
    private lateinit var logAdapter: LogAdapter

    companion object {
        fun newInstance() = LifecycleFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLifecycleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupRecyclerView()
        setupButtons()
        addLog("LifecycleFragment: onViewCreated")
    }

    private fun setupViewPager() {
        val adapter = LifecyclePagerAdapter(requireActivity() as AppCompatActivity) { page, event ->
            addLog("$page: $event")
        }
        binding.viewPager.adapter = adapter

        // 设置预加载为1，确保相邻页面也被创建
        binding.viewPager.offscreenPageLimit = 1
    }

    private fun setupRecyclerView() {
        logAdapter = LogAdapter(logs)
        binding.rvLog.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupButtons() {
        binding.btnClear.setOnClickListener {
            logs.clear()
            logAdapter.notifyDataSetChanged()
        }
    }

    fun addLog(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val logMessage = "[$timestamp] $message"
        logs.add(0, logMessage) // 添加到顶部
        logAdapter.notifyItemInserted(0)
        binding.rvLog.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 生命周期 ViewPager Adapter
     */
    private inner class LifecyclePagerAdapter(
        activity: AppCompatActivity,
        private val onLifecycleEvent: (String, String) -> Unit
    ) : FragmentStateAdapter(activity) {

        private val pageTitles = listOf("Page A", "Page B", "Page C")
        private val pageColors = listOf(
            R.color.page_color_1,
            R.color.page_color_2,
            R.color.page_color_3
        )

        override fun getItemCount(): Int = pageTitles.size

        override fun createFragment(position: Int): Fragment {
            return LifecyclePageFragment.newInstance(
                title = pageTitles[position],
                backgroundColor = pageColors[position],
                onLifecycleEvent = onLifecycleEvent
            )
        }
    }

    /**
     * 日志列表 Adapter
     */
    private inner class LogAdapter(
        private val logs: List<String>
    ) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemLifecycleLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(logs[position])
        }

        override fun getItemCount(): Int = logs.size

        inner class ViewHolder(
            private val binding: ItemLifecycleLogBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(log: String) {
                binding.tvLog.text = log
            }
        }
    }
}
