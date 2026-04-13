package com.peter.network.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.network.demo.adapter.PostAdapter
import com.peter.network.demo.databinding.FragmentBasicBinding
import com.peter.network.demo.viewmodel.BasicViewModel

class BasicFragment : Fragment() {

    private var _binding: FragmentBasicBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BasicViewModel>()
    private val postAdapter = PostAdapter()

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
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        binding.btnLoadPosts.setOnClickListener { viewModel.loadPosts() }
        binding.btnLoadSingle.setOnClickListener { viewModel.loadSinglePost() }
        binding.btnCreatePost.setOnClickListener { viewModel.createPost() }

        viewModel.status.observe(viewLifecycleOwner) { binding.tvStatus.text = it }
        viewModel.posts.observe(viewLifecycleOwner) { postAdapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
