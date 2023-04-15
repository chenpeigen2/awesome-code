cmake_minimum_required(VERSION 3.24)
project(cpp_reference)

set(CMAKE_CXX_STANDARD 17)

add_executable(cpp_reference main.cpp)
target_link_libraries(cpp_reference crypto) #关键，如果不加会报错，编译的时候找不到函数
