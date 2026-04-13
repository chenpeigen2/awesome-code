package com.peter.network.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.peter.network.demo.R
import com.peter.network.demo.databinding.FragmentMediaBinding
import com.peter.network.demo.viewmodel.MediaViewModel

class MediaFragment : Fragment() {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MediaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRefreshImage.setOnClickListener { viewModel.refreshImage() }
        binding.btnDownloadJson.setOnClickListener { viewModel.downloadSampleJson() }

        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            binding.imageBanner.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                memoryCachePolicy(CachePolicy.ENABLED)
            }
            binding.imageAvatar.load(url) {
                crossfade(true)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_launcher_foreground)
            }
        }
        viewModel.status.observe(viewLifecycleOwner) { binding.tvStatus.text = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
