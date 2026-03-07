package com.peter.components.demo.activity.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Fragment 通信示例
 * 
 * 知识点：
 * 1. ViewModel 共享数据 - 同一 Activity 的 Fragment 共享 ViewModel
 * 2. Fragment Result API - setFragmentResult / setFragmentResultListener
 * 3. 接口回调 - Fragment 定义接口，Activity 实现
 */
class FragmentCommunicationActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FragmentContainerView(this).apply {
            id = android.R.id.content
        })

        // 共享 ViewModel
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // 添加 Fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, CommunicationFragment())
                .commit()
        }

        // Fragment Result API 监听
        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val result = bundle.getString("data")
            // 处理 Fragment 发送的数据
        }
    }
}

/**
 * 共享 ViewModel
 */
class SharedViewModel : ViewModel() {
    private val _data = kotlinx.coroutines.flow.MutableStateFlow("")
    val data: kotlinx.coroutines.flow.StateFlow<String> = _data

    fun updateData(newData: String) {
        _data.value = newData
    }
}

/**
 * 通信 Fragment
 */
class CommunicationFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 方式1：通过 ViewModel 共享数据
        sharedViewModel.updateData("来自 Fragment 的数据")

        // 方式2：通过 Fragment Result API 发送数据
        parentFragmentManager.setFragmentResult(
            "requestKey",
            Bundle().apply { putString("data", "Fragment Result 数据") }
        )
    }
}
