package com.peter.lifecycle.demo.livedata

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.lifecycle.demo.databinding.ActivityMediatorLiveDataBinding

/**
 * MediatorLiveData 示例
 * 
 * 知识点：
 * 1. 合并多个 LiveData 数据源
 * 2. 监听多个 LiveData 的变化
 * 3. 实现复杂的业务逻辑
 * 
 * 使用场景：
 * - 多条件筛选（价格区间 + 类别）
 * - 表单验证（多个输入字段）
 * - 多数据源合并显示
 */
class MediatorLiveDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatorLiveDataBinding
    private val viewModel: MediatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatorLiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeData()
    }

    private fun setupViews() {
        // 价格区间
        binding.sbPrice.setOnRangeChangeListener { min, max ->
            viewModel.setPriceRange(min, max)
        }
        
        // 类别选择
        binding.rgCategory.setOnCheckedChangeListener { _, checkedId ->
            val category = when (checkedId) {
                binding.rbAll.id -> "all"
                binding.rbFood.id -> "food"
                binding.rbElectronics.id -> "electronics"
                else -> "all"
            }
            viewModel.setCategory(category)
        }
        
        // 表单输入
        binding.etUsername.addTextChangedListener { 
            viewModel.setUsername(it?.toString() ?: "")
        }
        
        binding.etPassword.addTextChangedListener { 
            viewModel.setPassword(it?.toString() ?: "")
        }
        
        binding.etConfirmPassword.addTextChangedListener { 
            viewModel.setConfirmPassword(it?.toString() ?: "")
        }
    }

    private fun observeData() {
        // 观察筛选结果
        viewModel.filterResult.observe(this) { result ->
            binding.tvFilterResult.text = """
                筛选结果:
                ${result.items.size} 个商品
                价格区间: ${result.minPrice} - ${result.maxPrice}
                类别: ${result.category}
            """.trimIndent()
        }
        
        // 观察表单验证结果
        viewModel.formValidation.observe(this) { validation ->
            binding.btnSubmit.isEnabled = validation.isValid
            binding.tvValidationError.text = if (validation.isValid) {
                "表单验证通过 ✓"
            } else {
                "错误: ${validation.error}"
            }
        }
    }
}

/**
 * MediatorLiveData ViewModel
 */
class MediatorViewModel : ViewModel() {

    // 价格区间
    private val _minPrice = MutableLiveData(0)
    private val _maxPrice = MutableLiveData(1000)
    
    // 类别
    private val _category = MutableLiveData("all")
    
    // 合并筛选条件
    private val _filterResult = MediatorLiveData<FilterResult>()
    val filterResult: LiveData<FilterResult> = _filterResult

    // 表单字段
    private val _username = MutableLiveData("")
    private val _password = MutableLiveData("")
    private val _confirmPassword = MutableLiveData("")
    
    // 合并表单验证
    private val _formValidation = MediatorLiveData<ValidationResult>()
    val formValidation: LiveData<ValidationResult> = _formValidation

    init {
        // 添加多个 LiveData 源
        // 当任何一个源发生变化时，都会触发回调
        _filterResult.addSource(_minPrice) { updateFilterResult() }
        _filterResult.addSource(_maxPrice) { updateFilterResult() }
        _filterResult.addSource(_category) { updateFilterResult() }
        
        // 初始化筛选结果
        updateFilterResult()
        
        // 表单验证
        _formValidation.addSource(_username) { updateFormValidation() }
        _formValidation.addSource(_password) { updateFormValidation() }
        _formValidation.addSource(_confirmPassword) { updateFormValidation() }
    }

    private fun updateFilterResult() {
        val min = _minPrice.value ?: 0
        val max = _maxPrice.value ?: 1000
        val category = _category.value ?: "all"
        
        // 模拟筛选商品
        val items = (1..20).filter {
            val price = it * 50
            price in min..max && (category == "all" || it % 3 == 0)
        }
        
        _filterResult.value = FilterResult(
            items = items,
            minPrice = min,
            maxPrice = max,
            category = category
        )
    }

    private fun updateFormValidation() {
        val username = _username.value ?: ""
        val password = _password.value ?: ""
        val confirmPassword = _confirmPassword.value ?: ""
        
        when {
            username.length < 3 -> 
                _formValidation.value = ValidationResult(false, "用户名至少3个字符")
            password.length < 6 -> 
                _formValidation.value = ValidationResult(false, "密码至少6个字符")
            password != confirmPassword -> 
                _formValidation.value = ValidationResult(false, "两次密码不一致")
            else -> 
                _formValidation.value = ValidationResult(true, null)
        }
    }

    fun setPriceRange(min: Int, max: Int) {
        _minPrice.value = min
        _maxPrice.value = max
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setConfirmPassword(password: String) {
        _confirmPassword.value = password
    }
}

/**
 * 筛选结果
 */
data class FilterResult(
    val items: List<Int>,
    val minPrice: Int,
    val maxPrice: Int,
    val category: String
)

/**
 * 验证结果
 */
data class ValidationResult(
    val isValid: Boolean,
    val error: String?
)
