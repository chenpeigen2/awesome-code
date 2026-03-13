package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentTabContentBinding

/**
 * Tab 内容 Fragment
 * 展示 TabLayout + ViewPager2 中每个 Tab 的内容
 */
class TabContentFragment : Fragment() {

    private var _binding: FragmentTabContentBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var description: String? = null
    private var backgroundColor: Int = R.color.surface

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESCRIPTION = "arg_description"
        private const val ARG_BACKGROUND_COLOR = "arg_background_color"

        fun newInstance(title: String, description: String, backgroundColor: Int): TabContentFragment {
            return TabContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                    putInt(ARG_BACKGROUND_COLOR, backgroundColor)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            description = it.getString(ARG_DESCRIPTION)
            backgroundColor = it.getInt(ARG_BACKGROUND_COLOR, R.color.surface)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置背景色
        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColor))

        // 设置内容
        binding.tvTitle.text = title
        binding.tvDescription.text = description

        // 根据标题设置不同的图标
        val iconRes = when (title) {
            getString(R.string.tab_home) -> R.drawable.ic_home
            getString(R.string.tab_discover) -> R.drawable.ic_discover
            getString(R.string.tab_message) -> R.drawable.ic_message
            getString(R.string.tab_profile) -> R.drawable.ic_profile
            else -> R.drawable.ic_home
        }
        binding.ivIcon.setImageResource(iconRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
