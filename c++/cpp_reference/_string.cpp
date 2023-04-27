//
// Created by chenpeigen on 23-4-22.
//
#include <string>
#include <iostream>
#include <cstring>
#include <random>

#include <random>
#include <string>
#include <array>
#include <algorithm>

using std::string;
using std::cout;
using std::endl;

using namespace std;

void myFunction(char myArray[]);

char *myFunction();

void provide_query(const std::string &source) {
    cout << source << endl;
    cout << "stringggggggggggggggggggggggggggggggggggggggg" << endl;
}


std::string generateRandomString(size_t length) {
    // 定义所使用的字符集
    const std::array<char, 64> charset{
            "0123456789"
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            "abcdefghijklmnopqrstuvwxyz"
    };
    const size_t charsetLength = charset.size();

    // 生成随机数引擎
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(0, charsetLength - 1);

    // 生成随机字符串
    std::string result;
    result.reserve(length);
    for (size_t i = 0; i < length; ++i) {
        result.push_back(charset[dis(gen)]);
    }
    return result;
}

string random_string(int len) {
    string str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    random_device rd;
    mt19937 generator(rd());
    shuffle(str.begin(), str.end(), generator);
    return str.substr(0, len);
}

//https://cplusplus.com/reference/string/string/
void test_string() {
    cout << "endingggggggggg" << endl;

    cout << generateRandomString(16) << endl;

    char buffer[20];
    std::string str("Test string...");
    std::size_t length = str.copy(buffer, 6, 5);
    buffer[length] = '\0';
    std::cout << "buffer contains: " << buffer << '\n';

    char myArray[] = "Hello, world!";
    cout << myArray << endl;
    myFunction(myArray);
    cout << myArray << endl;
    char *myReturnedArray = myFunction();
    // Do something with myReturnedArray
    cout << myReturnedArray << endl;
    delete[] myReturnedArray; // Remember to deallocate the memory

    const char *aaa = "sdafsadfdfg";
    std::string xx = "123123213245";
    provide_query(aaa);
}

// 请注意，当你将字符数组作为参数传递给函数时，它实际上是一个指向该数组的指针。
// 因此，在函数中对该数组所做的任何更改都会影响调用该函数的代码中的原始数组。
void myFunction(char myArray[]) {
    // Do something with myArray
    myArray[0] = 'a';
}

char *myFunction() {
    char *myArray = new char[14];
    strcpy(myArray, "Hello, world!");
    return myArray;
}


