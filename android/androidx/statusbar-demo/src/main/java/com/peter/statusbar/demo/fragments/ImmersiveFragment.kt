package com.peter.statusbar.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.statusbar.demo.R
import com.peter.statusbar.demo.StatusBarAdapter
import com.peter.statusbar.demo.StatusBarHelper
import com.peter.statusbar.demo.StatusBarOption
import com.peter.statusbar.demo.databinding.FragmentImmersiveBinding

/**
 * 沉浸式模式 Fragment
 */
class ImmersiveFragment : Fragment() {

    private var _binding: FragmentImmersiveBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ImmersiveFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImmersiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = createImmersiveOptions()
        val adapter = StatusBarAdapter(items) { option ->
            applyImmersiveOption(option)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createImmersiveOptions(): List<StatusBarOption> {
        val context = requireContext()
        return listOf(
            StatusBarOption(
                id = "immersive_standard",
                title = getString(R.string.immersive_standard),
                description = getString(R.string.immersive_standard_desc),
                iconRes = R.drawable.ic_immersive,
                iconTint = ContextCompat.getColor(context, R.color.tab_immersive)
            ),
            StatusBarOption(
                id = "immersive_full",
                title = getString(R.string.immersive_full),
                description = getString(R.string.immersive_full_desc),
                iconRes = R.drawable.ic_immersive,
                iconTint = ContextCompat.getColor(context, R.color.tab_immersive)
            ),
            StatusBarOption(
                id = "immersive_sticky",
                title = getString(R.string.immersive_edge_to_edge),
                description = getString(R.string.immersive_edge_to_edge_desc),
                iconRes = R.drawable.ic_immersive,
                iconTint = ContextCompat.getColor(context, R.color.tab_immersive)
            ),
            StatusBarOption(
                id = "immersive_reset",
                title = getString(R.string.action_reset),
                description = "恢复默认沉浸式设置",
                iconRes = R.drawable.ic_immersive,
                iconTint = ContextCompat.getColor(context, R.color.gray_500)
            )
        )
    }

    private fun applyImmersiveOption(option: StatusBarOption) {
        when (option.id) {
            "immersive_standard" -> {
                StatusBarHelper.setImmersiveMode(requireActivity(), true)
                StatusBarHelper.setStatusBarTransparent(requireActivity())
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), true)
            }
            "immersive_full" -> {
                StatusBarHelper.setImmersiveMode(requireActivity(), false)
                StatusBarHelper.setStatusBarTransparent(requireActivity())
                StatusBarHelper.setStatusBarLightIcon(requireActivity(), true)
            }
            "immersive_sticky" -> {
                StatusBarHelper.setImmersiveSticky(requireActivity())
            }
            "immersive_reset" -> {
                StatusBarHelper.resetStatusBar(requireActivity())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
