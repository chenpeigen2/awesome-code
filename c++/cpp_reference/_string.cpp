//
// Created by chenpeigen on 23-4-22.
//
#include <string>
#include <iostream>

using namespace std;

//https://cplusplus.com/reference/string/string/
void test_string() {
    char buffer[20];
    std::string str("Test string...");
    std::size_t length = str.copy(buffer, 6, 5);
    buffer[length] = '\0';
    std::cout << "buffer contains: " << buffer << '\n';
}