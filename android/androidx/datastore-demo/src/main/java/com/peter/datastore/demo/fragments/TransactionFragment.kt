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
import com.peter.datastore.demo.databinding.FragmentTransactionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager

    companion object {
        fun newInstance() = TransactionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager.getInstance(requireContext())
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnExecuteTransaction.setOnClickListener {
            lifecycleScope.launch {
                val result = dataStoreManager.executeInTransaction {
                    putString("tx_key1", "事务值1")
                    putInt("tx_key2", 100)
                    putBoolean("tx_key3", true)
                    putObject(
                        DemoConfig.Keys.USER_DATA_JSON, UserPreferences(
                            userName = "事务用户",
                            userAge = 30
                        )
                    )
                    putObject(
                        DemoConfig.Keys.APP_CONFIG_JSON, AppSettings(
                            version = 2,
                            theme = "transaction_theme",
                            fontSize = 18
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    binding.cardResult.visibility = View.VISIBLE
                    if (result.isSuccess) {
                        binding.tvTransactionResult.text = "事务执行成功!\n已批量写入 5 条数据"
                    } else {
                        binding.tvTransactionResult.text = "事务执行失败: ${result.exceptionOrNull()?.message}"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
