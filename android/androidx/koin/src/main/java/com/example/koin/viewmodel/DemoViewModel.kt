package com.example.koin.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class DemoViewModel : ViewModel() {
    val id: Int = (1000..9999).random()
}

class SavedStateViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val id: Int = (1000..9999).random()

    fun saveValue(key: String, value: String) {
        savedStateHandle[key] = value
    }

    fun getValue(key: String): String? {
        return savedStateHandle[key]
    }
}

class FactoryViewModel(
    private val param: String
) : ViewModel() {
    val id: Int = (1000..9999).random()
    val injectedParam: String = param
}

class SharedViewModel : ViewModel() {
    val id: Int = (1000..9999).random()
    private var _data: String = ""
    var data: String
        get() = _data
        set(value) { _data = value }
}
