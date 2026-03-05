package com.peter.lifecycle.demo.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.lifecycle.demo.R
import com.peter.lifecycle.demo.databinding.ActivitySharedViewmodelBinding

/**
 * 共享 ViewModel 示例
 * 
 * 知识点：
 * 1. Activity 和 Fragment 共享同一个 ViewModel
 * 2. Fragment 之间通过共享 ViewModel 通信
 * 3. activityViewModels vs viewModels
 * 
 * 使用场景：
 * - Master-Detail 模式
 * - 表单分步填写
 * - Fragment 间数据共享
 */
class SharedViewModelActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedViewmodelBinding
    // 使用 viewModels 获取 Activity 级别的 ViewModel
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedViewmodelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()
        observeData()
    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer1, SenderFragment())
            .replace(R.id.fragmentContainer2, ReceiverFragment())
            .commit()
    }

    private fun observeData() {
        viewModel.message.observe(this) { message ->
            binding.tvActivityMessage.text = "Activity 收到: $message"
        }
    }
}

/**
 * 共享 ViewModel
 */
class SharedViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun sendMessage(msg: String) {
        _message.value = msg
    }
}

/**
 * 发送消息的 Fragment
 */
class SenderFragment : Fragment() {

    // 使用 activityViewModels 获取 Activity 的 ViewModel
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            setText("发送 Fragment")
            setPadding(16, 16, 16, 16)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val button = Button(requireContext()).apply {
            text = "发送消息"
            setOnClickListener {
                viewModel.sendMessage("来自 SenderFragment 的消息 ${System.currentTimeMillis()}")
            }
        }
        
        (view as TextView).text = """
            发送 Fragment
            
            点击按钮发送消息到共享 ViewModel
        """.trimIndent()
    }
}

/**
 * 接收消息的 Fragment
 */
class ReceiverFragment : Fragment() {

    // 使用 activityViewModels 获取 Activity 的 ViewModel
    // 这样就和 SenderFragment 共享同一个 ViewModel 实例
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            setPadding(16, 16, 16, 16)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.message.observe(viewLifecycleOwner) { message ->
            (view as TextView).text = """
                接收 Fragment
                
                收到消息: $message
            """.trimIndent()
        }
    }
}
