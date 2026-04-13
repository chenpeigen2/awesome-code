package com.peter.network.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.network.demo.adapter.CommentAdapter
import com.peter.network.demo.databinding.FragmentAdvancedBinding
import com.peter.network.demo.viewmodel.AdvancedViewModel

class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AdvancedViewModel>()
    private val commentAdapter = CommentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvancedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        binding.btnUpdatePost.setOnClickListener { viewModel.updatePost() }
        binding.btnDeletePost.setOnClickListener { viewModel.deletePost() }
        binding.btnLoadComments.setOnClickListener { viewModel.loadComments() }
        binding.btnUpload.setOnClickListener { viewModel.uploadDemo() }

        viewModel.status.observe(viewLifecycleOwner) { binding.tvStatus.text = it }
        viewModel.comments.observe(viewLifecycleOwner) { commentAdapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
