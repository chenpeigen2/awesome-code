package com.example.koin

import com.example.koin.model.Peter
import com.example.koin.model.Tool
import org.koin.dsl.module


val sampleModule = module {
    single { Tool("tool", "tool description") }
    single { Peter("chen", 18, get<Tool>()) }
}