package com.peter.viewpager2.demo.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 共享 ViewModel
 * 用于演示 Fragment 之间通过共享 ViewModel 进行通信
 */
class SharedViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun sendMessage(msg: String) {
        _message.value = msg
    }

    fun clearMessage() {
        _message.value = ""
    }
}
