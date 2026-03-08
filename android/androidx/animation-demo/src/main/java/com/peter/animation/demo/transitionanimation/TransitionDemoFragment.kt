package com.peter.animation.demo.transitionanimation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.animation.demo.databinding.FragmentTransitionDemoBinding

/**
 * 转场演示Fragment
 */
class TransitionDemoFragment : Fragment() {

    private var _binding: FragmentTransitionDemoBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var color: String? = null

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_COLOR = "color"

        fun newInstance(title: String, color: String): TransitionDemoFragment {
            return TransitionDemoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_COLOR, color)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            color = it.getString(ARG_COLOR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransitionDemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = title
        try {
            binding.root.setBackgroundColor(Color.parseColor(color))
        } catch (e: Exception) {
            // Use default color
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
