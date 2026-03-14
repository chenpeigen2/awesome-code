package com.peter.datastore.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.datastore.DataStoreManager
import com.peter.datastore.basic.PreferencesDataStoreHelper
import com.peter.datastore.demo.AppSettings
import com.peter.datastore.demo.DemoConfig
import com.peter.datastore.demo.MultiTypeDataManager
import com.peter.datastore.demo.R
import com.peter.datastore.demo.UserPreferences
import com.peter.datastore.demo.databinding.FragmentMultitypeBinding
import com.peter.datastore.json.JsonDataStoreHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultiTypeFragment : Fragment() {

    private var _binding: FragmentMultitypeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var multiTypeDataManager: MultiTypeDataManager

    companion object {
        fun newInstance() = MultiTypeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultitypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager.getInstance(requireContext())
        
        // 使用 DataStoreManager 的内部 helpers 创建 MultiTypeDataManager
        val preferencesHelper = PreferencesDataStoreHelper(requireContext())
        val jsonHelper = JsonDataStoreHelper(preferencesHelper)
        multiTypeDataManager = MultiTypeDataManager(preferencesHelper, jsonHelper)
        
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSaveMultiType.setOnClickListener {
            lifecycleScope.launch {
                val userData = MultiTypeDataManager.UserData(
                    userName = "张三",
                    userAge = 25,
                    isLoggedIn = true,
                    lastLoginTime = System.currentTimeMillis(),
                    score = 98.5,
                    favoriteTags = setOf("Android", "Kotlin", "DataStore")
                )
                multiTypeDataManager.saveUserData(userData)

                val appConfig = MultiTypeDataManager.AppConfiguration(
                    theme = "dark",
                    fontSize = 16,
                    language = "zh",
                    notificationsEnabled = true
                )
                multiTypeDataManager.saveAppConfiguration(appConfig)

                withContext(Dispatchers.Main) {
                    binding.tvMultiTypeResult.text = "数据保存成功！"
                }
            }
        }

        binding.btnReadMultiType.setOnClickListener {
            lifecycleScope.launch {
                val userData = multiTypeDataManager.readUserData()
                val appConfig = multiTypeDataManager.readAppConfiguration()

                withContext(Dispatchers.Main) {
                    binding.tvMultiTypeResult.text = """
                        用户: ${userData.userName} (${userData.userAge}岁)
                        分数: ${userData.score}
                        标签: ${userData.favoriteTags.joinToString(", ")}
                        主题: ${appConfig.theme}, 字体: ${appConfig.fontSize}
                    """.trimIndent()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}