package com.peter.viewpager2.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.peter.viewpager2.demo.R
import com.peter.viewpager2.demo.databinding.FragmentLifecyclePageBinding

/**
 * 生命周期页面 Fragment
 * 监听并报告生命周期事件
 */
class LifecyclePageFragment : Fragment() {

    private var _binding: FragmentLifecyclePageBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var backgroundColor: Int = R.color.surface
    private var onLifecycleEvent: ((String, String) -> Unit)? = null

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_BACKGROUND_COLOR = "arg_background_color"

        fun newInstance(
            title: String,
            backgroundColor: Int,
            onLifecycleEvent: (String, String) -> Unit
        ): LifecyclePageFragment {
            return LifecyclePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putInt(ARG_BACKGROUND_COLOR, backgroundColor)
                }
                this.onLifecycleEvent = onLifecycleEvent
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            backgroundColor = it.getInt(ARG_BACKGROUND_COLOR, R.color.surface)
        }
        logLifecycle("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logLifecycle("onCreateView")
        _binding = FragmentLifecyclePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated")

        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColor))
        binding.tvPageTitle.text = title

        // 观察生命周期状态
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            val eventName = when (event) {
                Lifecycle.Event.ON_CREATE -> "ON_CREATE"
                Lifecycle.Event.ON_START -> "ON_START"
                Lifecycle.Event.ON_RESUME -> "ON_RESUME"
                Lifecycle.Event.ON_PAUSE -> "ON_PAUSE"
                Lifecycle.Event.ON_STOP -> "ON_STOP"
                Lifecycle.Event.ON_DESTROY -> "ON_DESTROY"
                else -> event.name
            }
            logLifecycle("ViewLifecycle: $eventName")
            updateStateDisplay()
        })
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    override fun onDestroyView() {
        logLifecycle("onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        logLifecycle("onDestroy")
        super.onDestroy()
    }

    private fun logLifecycle(event: String) {
        title?.let { pageName ->
            onLifecycleEvent?.invoke(pageName, event)
        }
    }

    private fun updateStateDisplay() {
        val state = when (viewLifecycleOwner.lifecycle.currentState) {
            Lifecycle.State.INITIALIZED -> "INITIALIZED"
            Lifecycle.State.CREATED -> "CREATED"
            Lifecycle.State.STARTED -> "STARTED"
            Lifecycle.State.RESUMED -> "RESUMED"
            Lifecycle.State.DESTROYED -> "DESTROYED"
        }
        binding.tvState.text = getString(R.string.lifecycle_state, state)
    }
}
