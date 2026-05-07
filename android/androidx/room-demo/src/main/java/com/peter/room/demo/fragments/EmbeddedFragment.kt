package com.peter.room.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.peter.room.demo.databinding.FragmentEmbeddedBinding
import com.peter.room.demo.db.AppDatabase
import com.peter.room.demo.repository.ContactRepository
import com.peter.room.demo.viewmodel.EmbeddedViewModel
import com.peter.room.demo.viewmodel.EmbeddedViewModelFactory
import kotlinx.coroutines.launch

class EmbeddedFragment : Fragment() {

    private var _binding: FragmentEmbeddedBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        ContactRepository(AppDatabase.getDatabase(requireContext()).contactDao())
    }

    private val viewModel: EmbeddedViewModel by viewModels {
        EmbeddedViewModelFactory(repository)
    }

    companion object {
        fun newInstance() = EmbeddedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmbeddedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        observeData()
    }

    private fun setupButtons() {
        binding.btnAddContact.setOnClickListener { addContact() }
        binding.btnDeleteAll.setOnClickListener { viewModel.deleteAll() }
    }

    private fun addContact() {
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val phone = binding.etPhone.text?.toString()?.trim() ?: ""
        val homeStreet = binding.etHomeStreet.text?.toString()?.trim() ?: ""
        val homeCity = binding.etHomeCity.text?.toString()?.trim() ?: ""
        val homeZip = binding.etHomeZip.text?.toString()?.trim() ?: ""
        val workStreet = binding.etWorkStreet.text?.toString()?.trim() ?: ""
        val workCity = binding.etWorkCity.text?.toString()?.trim() ?: ""
        val workZip = binding.etWorkZip.text?.toString()?.trim() ?: ""

        if (name.isEmpty() || homeStreet.isEmpty() || workStreet.isEmpty()) {
            Toast.makeText(requireContext(), "请填写必要字段", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.addContact(name, phone, homeStreet, homeCity, homeZip, workStreet, workCity, workZip)

        // Clear fields
        binding.etName.text?.clear()
        binding.etPhone.text?.clear()
        binding.etHomeStreet.text?.clear()
        binding.etHomeCity.text?.clear()
        binding.etHomeZip.text?.clear()
        binding.etWorkStreet.text?.clear()
        binding.etWorkCity.text?.clear()
        binding.etWorkZip.text?.clear()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collect { contacts ->
                val text = if (contacts.isEmpty()) {
                    "暂无联系人，请添加"
                } else {
                    contacts.joinToString("\n\n") { contact ->
                        """📌 ${contact.name} (${contact.phone})
🏠 家庭: ${contact.homeAddress.street}, ${contact.homeAddress.city} ${contact.homeAddress.zipCode}
🏢 工作: ${contact.workAddress.street}, ${contact.workAddress.city} ${contact.workAddress.zipCode}"""
                    }
                }
                binding.tvContactList.text = text
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operationState.collect { state ->
                state?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearOperationState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
