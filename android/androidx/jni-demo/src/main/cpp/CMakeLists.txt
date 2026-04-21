cmake_minimum_required(VERSION 3.18.1)

project("jni_demo")

add_library(jni_demo SHARED
        basic_jni.cpp
        param_jni.cpp
        array_jni.cpp
        object_jni.cpp
        callback_jni.cpp
        fileio_jni.cpp
        dynamic_jni.cpp
        thread_jni.cpp
)

target_include_directories(jni_demo PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)

find_library(log-lib log)

target_link_libraries(jni_demo
        ${log-lib}
)
