package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peter.room.demo.databinding.FragmentAdvancedBinding

/**
 * 高级查询演示 Fragment
 */
class AdvancedFragment : Fragment() {

    private var _binding: FragmentAdvancedBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AdvancedFragment()
    }

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
        // 显示高级查询说明
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
