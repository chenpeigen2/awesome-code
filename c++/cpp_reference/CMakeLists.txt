cmake_minimum_required(VERSION 3.24)
project(cpp_reference)
include(FetchContent)

FetchContent_Declare(json URL https://github.com/nlohmann/json/releases/download/v3.11.2/json.tar.xz)
FetchContent_MakeAvailable(json)

set(CMAKE_CXX_STANDARD 11)

add_executable(cpp_reference main.cpp _vector.cpp _main.h _map.cpp _set.cpp _string.cpp _struct.cpp _pointers.cpp _rv.cpp _chrono.cpp _json.cpp _crypt.cpp _json.cpp)
#target_link_libraries(cpp_reference crypto) #关键，如果不加会报错，编译的时候找不到函数
target_link_libraries(cpp_reference PRIVATE nlohmann_json::nlohmann_json crypto)
