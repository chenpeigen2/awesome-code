package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentCommunicationBinding

/**
 * Fragment 通信演示
 * 展示三种常见的 Fragment 通信方式：
 * 1. 共享 ViewModel
 * 2. Fragment Result API
 * 3. 回调接口
 */
class CommunicationFragment : Fragment() {

    private var _binding: FragmentCommunicationBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CommunicationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = CommunicationPagerAdapter(requireActivity() as AppCompatActivity)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 通信演示 ViewPager Adapter
     */
    private inner class CommunicationPagerAdapter(
        activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {

        private val fragments = listOf(
            SharedViewModelFragment.newInstance(),
            ResultApiFragment.newInstance(),
            CallbackFragment.newInstance()
        )

        private val tabTitles = listOf(
            getString(R.string.communication_shared_vm),
            getString(R.string.communication_result_api),
            getString(R.string.communication_callback)
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        fun getTabTitle(position: Int): String = tabTitles[position]
    }
}

/**
 * 共享 ViewModel 演示 Fragment
 */
class SharedViewModelFragment : Fragment() {

    private var _binding: com.peter.viewpager2.demo.databinding.FragmentSharedVmBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    companion object {
        fun newInstance() = SharedViewModelFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.peter.viewpager2.demo.databinding.FragmentSharedVmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 观察共享 ViewModel 中的消息
        sharedViewModel.message.observe(viewLifecycleOwner) { message ->
            binding.tvReceived.text = if (message.isNullOrEmpty()) {
                "等待接收消息..."
            } else {
                getString(R.string.communication_received, message)
            }
        }

        // 发送消息
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                sharedViewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }

        // 初始化显示
        binding.tvReceived.text = "等待接收消息..."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Fragment Result API 演示
 */
class ResultApiFragment : Fragment() {

    private var _binding: com.peter.viewpager2.demo.databinding.FragmentResultApiBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_KEY = "result_api_request_key"
        const val BUNDLE_KEY_MESSAGE = "message"

        fun newInstance() = ResultApiFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.peter.viewpager2.demo.databinding.FragmentResultApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置结果监听器
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val message = bundle.getString(BUNDLE_KEY_MESSAGE)
            binding.tvReceived.text = if (message.isNullOrEmpty()) {
                "未收到数据"
            } else {
                "收到结果: $message"
            }
        }

        // 发送结果
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                val result = Bundle().apply {
                    putString(BUNDLE_KEY_MESSAGE, message)
                }
                parentFragmentManager.setFragmentResult(REQUEST_KEY, result)
                binding.etMessage.text?.clear()
            }
        }

        // 初始化显示
        binding.tvReceived.text = "等待接收结果..."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * 回调接口演示
 */
class CallbackFragment : Fragment() {

    private var _binding: com.peter.viewpager2.demo.databinding.FragmentCallbackBinding? = null
    private val binding get() = _binding!!

    private var callback: MessageCallback? = null

    interface MessageCallback {
        fun onMessageSent(message: String)
    }

    companion object {
        fun newInstance() = CallbackFragment()
    }

    fun setMessageCallback(callback: MessageCallback) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.peter.viewpager2.demo.databinding.FragmentCallbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 发送回调
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                callback?.onMessageSent(message)
                binding.etMessage.text?.clear()
            }
        }

        // 初始化显示
        binding.tvReceived.text = "等待接收回调数据..."
    }

    fun displayReceivedMessage(message: String) {
        binding.tvReceived.text = "Activity 收到回调: $message"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
