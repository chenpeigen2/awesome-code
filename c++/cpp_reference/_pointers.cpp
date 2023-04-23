//
// Created by chenpeigen on 23-4-23.
//
#include "_main.h"
#include <iostream>

using namespace std;

//https://blog.csdn.net/majianfei1023/article/details/46629065

void hello(char **pointers) {
    pointers[0] = "abcdef";
    pointers[1] = "defgh";
}

void fn(int argc, char **argv) {
    printf("argc : %d\n", argc);
    for (int i = 0; i < argc; ++i) {
        printf("%s\n", argv[i]);
    }
}

int a = 10;
int b = 100;
int *q;

void func(int **p)  //2
{
    cout << "func:&p=" << &p << ",p=" << p << endl;
    *p = &b;  //3
    cout << "func:&p=" << &p << ",p=" << p << endl;
}

void test_pointer() {
    char *res[3];
    cout << "hello " << res[2] << endl;

    hello(res);
    cout << "hello " << res[1] << endl;

    char *var3[3] = {"arg1", "argument2", "arg3"};
    char *var4[4] = {"One", "Two", "Three", "Four"};

    cout << "&a=" << &a << ",&b=" << &b << ",&q=" << &q << endl;
    q = &a;
    cout << "*q=" << *q << ",q=" << q << ",&q=" << &q << endl;
    func(&q);  //1
    cout << "*q=" << *q << ",q=" << q << ",&q=" << &q << endl;

    fn(3, var3);
    fn(4, var4);
}

