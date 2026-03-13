package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.peter.room.demo.databinding.FragmentRelationBinding

/**
 * 关系映射演示 Fragment
 */
class RelationFragment : Fragment() {

    private var _binding: FragmentRelationBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = RelationFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnLoadDepartment.setOnClickListener {
            Toast.makeText(requireContext(), "请查看源码了解关系映射", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnLoadStudent.setOnClickListener {
            Toast.makeText(requireContext(), "请查看源码了解关系映射", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}