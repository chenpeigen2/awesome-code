package com.peter.datastore.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.datastore.DataStoreManager
import com.peter.datastore.demo.databinding.FragmentBasicBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager

    companion object {
        fun newInstance() = BasicFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager.getInstance(requireContext())
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSaveString.setOnClickListener {
            val value = binding.etStringInput.text.toString()
            lifecycleScope.launch {
                dataStoreManager.putString("demo_string", value)
                showResult("保存字符串: $value")
            }
        }

        binding.btnSaveInt.setOnClickListener {
            val value = binding.etIntInput.text.toString().toIntOrNull() ?: 0
            lifecycleScope.launch {
                dataStoreManager.putInt("demo_int", value)
                showResult("保存整数: $value")
            }
        }

        binding.btnReadBasic.setOnClickListener {
            lifecycleScope.launch {
                val stringValue = dataStoreManager.getString("demo_string", "默认值")
                val intValue = dataStoreManager.getInt("demo_int", 0)
                withContext(Dispatchers.Main) {
                    binding.tvBasicResult.text = "字符串: $stringValue, 整数: $intValue"
                }
            }
        }

        binding.btnClearBasic.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.remove("demo_string")
                dataStoreManager.remove("demo_int")
                withContext(Dispatchers.Main) {
                    binding.tvBasicResult.text = "当前值: -"
                    binding.etStringInput.text?.clear()
                    binding.etIntInput.text?.clear()
                }
            }
        }
    }

    private fun showResult(message: String) {
        binding.tvBasicResult.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
