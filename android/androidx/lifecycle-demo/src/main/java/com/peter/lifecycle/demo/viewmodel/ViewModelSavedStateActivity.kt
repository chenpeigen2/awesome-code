package com.peter.lifecycle.demo.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.peter.lifecycle.demo.databinding.ActivityViewModelSavedStateBinding

/**
 * SavedStateHandle 示例
 * 
 * 知识点：
 * 1. SavedStateHandle - 用于保存和恢复数据
 * 2. 自动保存：系统杀死进程时自动保存数据
 * 3. 手动保存：onSaveInstanceState 中保存的数据
 * 
 * 优点：
 * - ViewModel 数据可以在进程重启后恢复
 * - 不需要在 Activity/Fragment 中手动处理 onSaveInstanceState
 * - 数据保存和恢复逻辑集中在 ViewModel 中
 */
class ViewModelSavedStateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewModelSavedStateBinding
    private val viewModel: SavedStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewModelSavedStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.btnSave.setOnClickListener {
            val text = binding.etInput.text.toString()
            viewModel.saveData(text)
            binding.etInput.text?.clear()
        }
        
        binding.btnIncrement.setOnClickListener {
            viewModel.increment()
        }
        
        binding.btnAddToList.setOnClickListener {
            val item = binding.etInput.text.toString()
            if (item.isNotEmpty()) {
                viewModel.addItem(item)
                binding.etInput.text?.clear()
            }
        }
    }

    private fun observeData() {
        viewModel.savedText.observe(this) { text ->
            binding.tvSavedText.text = "保存的文本: $text"
        }
        
        viewModel.counter.observe(this) { count ->
            binding.tvCounter.text = "计数器: $count"
        }
        
        viewModel.items.observe(this) { items ->
            binding.tvItems.text = "列表项:\n${items.joinToString("\n")}"
        }
    }
}

/**
 * 使用 SavedStateHandle 的 ViewModel
 */
class SavedStateViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_TEXT = "key_text"
        private const val KEY_COUNTER = "key_counter"
        private const val KEY_ITEMS = "key_items"
    }

    // 使用 LiveData 包装 SavedStateHandle 中的数据
    val savedText = savedStateHandle.getLiveData<String>(KEY_TEXT, "")
    val counter = savedStateHandle.getLiveData<Int>(KEY_COUNTER, 0)
    
    // 支持自定义类型（需要实现 Parcelable 或 Serializable）
    val items = savedStateHandle.getLiveData<ArrayList<String>>(KEY_ITEMS, arrayListOf())

    fun saveData(text: String) {
        // 方式1：直接设置值
        savedStateHandle[KEY_TEXT] = text
        Log.d("SavedStateVM", "保存文本: $text")
    }

    fun increment() {
        // 方式2：获取并更新值
        val current = savedStateHandle.get<Int>(KEY_COUNTER) ?: 0
        savedStateHandle[KEY_COUNTER] = current + 1
    }

    fun addItem(item: String) {
        val currentList = savedStateHandle.get<ArrayList<String>>(KEY_ITEMS) ?: arrayListOf()
        currentList.add(item)
        savedStateHandle[KEY_ITEMS] = currentList
    }
    
    /**
     * 其他常用方法：
     * - contains(key): 检查是否包含某个 key
     * - remove(key): 移除某个 key
     * - keys(): 获取所有 key
     * - get<T>(key): 获取某个 key 的值
     */
}