package com.peter.datastore.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.datastore.DataStoreManager
import com.peter.datastore.demo.AppSettings
import com.peter.datastore.demo.DemoConfig
import com.peter.datastore.demo.UserPreferences
import com.peter.datastore.demo.databinding.FragmentJsonBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JsonFragment : Fragment() {

    private var _binding: FragmentJsonBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    companion object {
        fun newInstance() = JsonFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJsonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager.getInstance(requireContext())
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSaveUser.setOnClickListener {
            val name = binding.etUserName.text.toString()
            val age = binding.etUserAge.text.toString().toIntOrNull() ?: 0

            lifecycleScope.launch {
                val userPrefs = UserPreferences(
                    userName = name,
                    userAge = age,
                    lastLoginTime = System.currentTimeMillis()
                )
                dataStoreManager.putObject(DemoConfig.Keys.USER_DATA_JSON, userPrefs)
                withContext(Dispatchers.Main) {
                    binding.tvUserResult.text = "保存成功: $name, 年龄: $age"
                }
            }
        }

        binding.btnReadUser.setOnClickListener {
            lifecycleScope.launch {
                val userPrefs = dataStoreManager.getObject(DemoConfig.Keys.USER_DATA_JSON, UserPreferences())
                val timeStr = if (userPrefs.lastLoginTime > 0) {
                    dateFormat.format(Date(userPrefs.lastLoginTime))
                } else {
                    "从未登录"
                }
                withContext(Dispatchers.Main) {
                    binding.tvUserResult.text = "用户: ${userPrefs.userName}, 年龄: ${userPrefs.userAge}, 登录: $timeStr"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
