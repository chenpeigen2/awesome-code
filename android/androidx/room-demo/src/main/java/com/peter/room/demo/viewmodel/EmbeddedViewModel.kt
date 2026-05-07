package com.peter.room.demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.peter.room.demo.db.entity.Address
import com.peter.room.demo.db.entity.Contact
import com.peter.room.demo.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmbeddedViewModel(private val repository: ContactRepository) : ViewModel() {

    val contacts: StateFlow<List<Contact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _operationState = MutableStateFlow<String?>(null)
    val operationState: StateFlow<String?> = _operationState.asStateFlow()

    fun addContact(
        name: String, phone: String,
        homeStreet: String, homeCity: String, homeZip: String,
        workStreet: String, workCity: String, workZip: String
    ) {
        viewModelScope.launch {
            val contact = Contact(
                name = name,
                phone = phone,
                homeAddress = Address(homeStreet, homeCity, homeZip),
                workAddress = Address(workStreet, workCity, workZip)
            )
            repository.insert(contact)
            _operationState.value = "添加联系人成功"
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            _operationState.value = "已清空所有联系人"
        }
    }

    fun clearOperationState() { _operationState.value = null }
}

class EmbeddedViewModelFactory(private val repository: ContactRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EmbeddedViewModel(repository) as T
    }
}
