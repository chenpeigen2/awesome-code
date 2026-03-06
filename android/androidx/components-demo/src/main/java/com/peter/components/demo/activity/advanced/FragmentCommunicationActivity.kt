package com.peter.components.demo.activity.advanced

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.peter.components.demo.R

/**
 * Fragment 通信方式示例
 *
 * ═══════════════════════════════════════════════════════════════
 * Fragment 通信方式总结
 * ═══════════════════════════════════════════════════════════════
 *
 * 1. ViewModel 共享（推荐）
 *    - Activity 和 Fragment 共享同一个 ViewModel
 *    - 通过 LiveData/Flow 观察数据变化
 *    - 生命周期感知，自动管理订阅
 *
 * 2. Fragment Result API
 *    - setFragmentResultListener / setFragmentResult
 *    - 替代 onActivityResult
 *    - 适合一次性数据传递
 *
 * 3. 接口回调
 *    - 定义回调接口
 *    - Fragment 持有 Activity 引用
 *    - 传统方式，仍可使用
 *
 * 4. Bundle 参数
 *    - 通过 setArguments 传递
 *    - 适合初始化参数
 *
 * ═══════════════════════════════════════════════════════════════
 * 最佳实践
 * ═══════════════════════════════════════════════════════════════
 *
 * - 优先使用 ViewModel + LiveData/Flow
 * - 一次性结果使用 Fragment Result API
 * - 初始化参数使用 Bundle
 * - 避免直接持有 Fragment 引用
 */
class FragmentCommunicationActivity : AppCompatActivity() {

    // 共享 ViewModel
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_communication)

        // 添加两个 Fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.container1, SenderFragment())
            .add(R.id.container2, ReceiverFragment())
            .commit()

        // 观察 ViewModel 数据
        sharedViewModel.sharedData.observe(this) { data ->
            findViewById<TextView>(R.id.tvSharedData).text = "共享数据: $data"
        }
    }
}

/**
 * 共享 ViewModel
 *
 * 在 Activity 中创建，Fragment 通过 activityViewModels 获取
 */
class SharedViewModel : ViewModel() {
    val sharedData = MutableLiveData<String>("初始数据")

    fun updateData(newData: String) {
        sharedData.value = newData
    }
}

/**
 * 发送 Fragment
 */
class SenderFragment : Fragment() {

    private val viewModel: SharedViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            setPadding(16, 16, 16, 16)
            text = "发送者 Fragment"
            textSize = 16f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = Button(requireContext()).apply {
            text = "通过 ViewModel 发送数据"
            setOnClickListener {
                viewModel.updateData("来自 Sender 的数据 ${System.currentTimeMillis() % 10000}")
            }
        }

        val button2 = Button(requireContext()).apply {
            text = "通过 Result API 发送"
            setOnClickListener {
                // 使用 Fragment Result API
                parentFragmentManager.setFragmentResult(
                    "request_key",
                    Bundle().apply { putString("data", "Result API 数据") }
                )
            }
        }

        (view as ViewGroup).addView(button)
        (view as ViewGroup).addView(button2)
    }
}

/**
 * 接收 Fragment
 */
class ReceiverFragment : Fragment() {

    private val viewModel: SharedViewModel by viewModels({ requireActivity() })
    private lateinit var tvData: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            setPadding(16, 16, 16, 16)
            tvData = this
            text = "接收者 Fragment\n等待数据..."
            textSize = 16f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 方式1: 观察 ViewModel
        viewModel.sharedData.observe(viewLifecycleOwner) { data ->
            tvData.append("\n\nViewModel: $data")
        }

        // 方式2: 监听 Fragment Result
        setFragmentResultListener("request_key") { _, bundle ->
            val data = bundle.getString("data")
            tvData.append("\nResult API: $data")
        }
    }
}
