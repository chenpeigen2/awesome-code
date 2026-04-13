package com.peter.network.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.network.demo.adapter.UserAdapter
import com.peter.network.demo.databinding.FragmentPracticalBinding
import com.peter.network.demo.viewmodel.PracticalViewModel

class PracticalFragment : Fragment() {

    private var _binding: FragmentPracticalBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PracticalViewModel>()
    private val userAdapter = UserAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        binding.btnLoadUsers.setOnClickListener { viewModel.loadUsers() }
        binding.btnLoadError.setOnClickListener { viewModel.loadErrorCase() }
        binding.btnRefreshNetwork.setOnClickListener { viewModel.refreshNetworkStatus() }

        viewModel.status.observe(viewLifecycleOwner) { binding.tvStatus.text = it }
        viewModel.networkStatus.observe(viewLifecycleOwner) { binding.tvNetworkStatus.text = it }
        viewModel.users.observe(viewLifecycleOwner) { userAdapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
