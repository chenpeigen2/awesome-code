//
// Created by chenpeigen on 23-4-22.
//
#include <string>
#include <iostream>
#include <cstring>

using namespace std;

void myFunction(char myArray[]);

char *myFunction();

//https://cplusplus.com/reference/string/string/
void test_string() {
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
