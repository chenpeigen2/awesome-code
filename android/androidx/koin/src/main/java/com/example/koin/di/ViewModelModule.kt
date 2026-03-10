package com.example.koin.di

import com.example.koin.viewmodel.DemoViewModel
import com.example.koin.viewmodel.FactoryViewModel
import com.example.koin.viewmodel.SavedStateViewModel
import com.example.koin.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Basic ViewModel
    viewModel { DemoViewModel() }

    // SavedState ViewModel
    viewModel { SavedStateViewModel(get()) }

    // Factory ViewModel with parameters
    viewModel { params -> FactoryViewModel(params.get()) }

    // Shared ViewModel
    viewModel { SharedViewModel() }
}
