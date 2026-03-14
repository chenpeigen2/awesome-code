package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.room.demo.R
import com.peter.room.demo.databinding.FragmentBasicBinding
import com.peter.room.demo.db.AppDatabase
import com.peter.room.demo.repository.UserRepository
import com.peter.room.demo.viewmodel.BasicViewModel
import com.peter.room.demo.viewmodel.BasicViewModelFactory
import kotlinx.coroutines.launch

/**
 * 基础用法 Fragment
 * 演示 Room 的基本 CRUD 操作
 */
class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserAdapter
    
    private val repository by lazy { 
        UserRepository(AppDatabase.getDatabase(requireContext()).userDao()) 
    }
    
    private val viewModel: BasicViewModel by viewModels {
        BasicViewModelFactory(repository)
    }

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
        setupRecyclerView()
        setupButtons()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter { user ->
            viewModel.deleteUser(user)
        }
        binding.recyclerView.apply {
            this.adapter = this@BasicFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            addUser()
        }
        
        binding.btnDeleteAll.setOnClickListener {
            viewModel.deleteAllUsers()
        }
    }

    private fun addUser() {
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val ageText = binding.etAge.text?.toString()?.trim() ?: ""
        val email = binding.etEmail.text?.toString()?.trim() ?: ""
        
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "请输入姓名", Toast.LENGTH_SHORT).show()
            return
        }
        
        val age = ageText.toIntOrNull() ?: 0
        
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "请输入邮箱", Toast.LENGTH_SHORT).show()
            return
        }
        
        viewModel.addUser(name, age, email)
        
        binding.etName.text?.clear()
        binding.etAge.text?.clear()
        binding.etEmail.text?.clear()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collect { users ->
                adapter.submitList(users)
                binding.tvUserCount.text = "${getString(R.string.basic_user_list)} (${users.size})"
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operationState.collect { state ->
                state?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearOperationState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}