package com.peter.motion.demo.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.peter.motion.demo.R
import com.peter.motion.demo.databinding.FragmentTransitionBinding

class TransitionFragment : Fragment() {

    private var _binding: FragmentTransitionBinding? = null
    private val binding get() = _binding!!
    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFade.setOnClickListener { showInnerFragment(Fade()) }
        binding.btnSlide.setOnClickListener { showInnerFragment(Slide()) }
        binding.btnExplode.setOnClickListener { showInnerFragment(Explode()) }

        binding.sharedTitle.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                binding.sharedTitle,
                "shared_title"
            )
            startActivity(Intent(requireContext(), DetailActivity::class.java), options.toBundle())
        }

        if (savedInstanceState == null) {
            showInnerFragment(Fade())
        }
    }

    private fun showInnerFragment(transition: android.transition.Transition) {
        counter++
        val inner = TransitionInnerFragment.newInstance(
            "${transition.javaClass.simpleName} 转场 #${counter}"
        )
        inner.enterTransition = transition
        inner.exitTransition = Fade()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_transition_container, inner)
            .commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TransitionInnerFragment : Fragment() {

    private var _binding: com.peter.motion.demo.databinding.FragmentTransitionInnerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.peter.motion.demo.databinding.FragmentTransitionInnerBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvInnerText.text = arguments?.getString(ARG_TEXT) ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TEXT = "arg_text"

        fun newInstance(text: String): TransitionInnerFragment {
            return TransitionInnerFragment().apply {
                arguments = Bundle().apply { putString(ARG_TEXT, text) }
            }
        }
    }
}
