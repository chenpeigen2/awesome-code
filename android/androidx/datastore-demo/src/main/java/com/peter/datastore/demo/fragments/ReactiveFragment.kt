package com.peter.datastore.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.peter.datastore.DataStoreManager
import com.peter.datastore.demo.databinding.FragmentReactiveBinding
import kotlinx.coroutines.launch

class ReactiveFragment : Fragment() {

    private var _binding: FragmentReactiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStoreManager: DataStoreManager

    companion object {
        fun newInstance() = ReactiveFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReactiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager.getInstance(requireContext())
        setupReactiveObserver()
        setupListeners()
    }

    private fun setupReactiveObserver() {
        dataStoreManager.getIntLiveData("reactive_counter", 0).observe(viewLifecycleOwner) { value ->
            binding.tvReactiveValue.text = value.toString()
        }
    }

    private fun setupListeners() {
        binding.btnIncrementCounter.setOnClickListener {
            lifecycleScope.launch {
                val current = dataStoreManager.getInt("reactive_counter", 0)
                dataStoreManager.putInt("reactive_counter", current + 1)
            }
        }

        binding.btnDecrementCounter.setOnClickListener {
            lifecycleScope.launch {
                val current = dataStoreManager.getInt("reactive_counter", 0)
                dataStoreManager.putInt("reactive_counter", current - 1)
            }
        }

        binding.btnResetCounter.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.putInt("reactive_counter", 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
