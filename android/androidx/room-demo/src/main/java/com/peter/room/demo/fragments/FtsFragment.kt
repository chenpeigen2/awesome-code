package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.peter.room.demo.databinding.FragmentFtsBinding
import com.peter.room.demo.db.AppDatabase
import com.peter.room.demo.repository.ArticleRepository
import com.peter.room.demo.viewmodel.FtsViewModel
import com.peter.room.demo.viewmodel.FtsViewModelFactory
import kotlinx.coroutines.launch

class FtsFragment : Fragment() {

    private var _binding: FragmentFtsBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        ArticleRepository(AppDatabase.getDatabase(requireContext()).articleDao())
    }

    private val viewModel: FtsViewModel by viewModels {
        FtsViewModelFactory(repository)
    }

    companion object {
        fun newInstance() = FtsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        observeData()
    }

    private fun setupButtons() {
        binding.btnSeedData.setOnClickListener { viewModel.seedData() }
        binding.btnSearchMatch.setOnClickListener { searchFts() }
        binding.btnSearchLike.setOnClickListener { searchLike() }
        binding.btnDeleteAll.setOnClickListener { viewModel.deleteAll() }
    }

    private fun searchFts() {
        val keyword = binding.etSearch.text?.toString()?.trim() ?: ""
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.searchFts(keyword)
    }

    private fun searchLike() {
        val keyword = binding.etSearch.text?.toString()?.trim() ?: ""
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.searchLike(keyword)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                val text = if (results.isEmpty()) {
                    "暂无搜索结果"
                } else {
                    results.joinToString("\n\n") { article ->
                        "📰 ${article.title}\n${article.content}"
                    }
                }
                binding.tvSearchResults.text = text
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchMethod.collect { method ->
                binding.tvSearchMethod.text = "当前搜索方式: $method"
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
