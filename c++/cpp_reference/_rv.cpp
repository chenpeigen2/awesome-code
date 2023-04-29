//
// Created by chenpeigen on 23-4-29.
//
#include "_main.h"
#include <utility>
#include <iostream>
#include <vector>
#include <string>


using namespace std;


class DataBlock {
public:

    int *data;
    static const int blockSize = 1000;
    string str;

    DataBlock(string s) : data(new int[blockSize]) {
        this->str = s;
        cout << str << " Constructor being invoked!" << this << endl;
    }

    // Complete copy
//    DataBlock(const DataBlock &db) : data(new int[blockSize]) {
//        this->str = db.str;
//        for (int i = 0; i < blockSize; i++) {
//            this->data[i] = db.data[i];
//        }
//
//        cout << str << " Copy Constructor being invoked!" << this << " " << &db << endl;
//    }

    DataBlock(DataBlock &&db) noexcept {
        this->data = db.data;
        this->str = "wer";
        db.data = nullptr;
        cout << str << " Copy Constructor being invoked!" << this << " " << &db << endl;
    }

    ~DataBlock() {
        if (this->data != nullptr) {
            delete[] data;
            data = nullptr;
        }

        cout << str << " Destructor being invoked!" << this << endl;
    }
};

void test_rv() {
    string foo = "foo-string";
    string bar = "bar-string";

    string &&z1 = std::move(foo);

    z1.append("dfdsg");

    cout << &foo << " " << &bar << endl;

    std::vector<string> myvector;

    myvector.push_back(foo);
//    myvector.push_back(bar);

    myvector.push_back(std::move(bar));
    std::cout << "my vec contains\n";
    for (auto &x: myvector) {
        cout << "value " << x;
        cout << "the address " << &x << '\n';
    }
    cout << endl;
    cout << &foo << " " << &bar << endl;
    foo.append("dfgdfg");
    cout << foo << " " << bar;
    string sx = "dsfadsf";
    string &&r = std::move(sx);

    string t(r);


    cout << '\n';

    vector<DataBlock> dbVec;
    DataBlock z = DataBlock("First");
    dbVec.push_back(std::move(z));
    cout << "" << endl;
//    cout << "=====================================" << endl;
//    dbVec.push_back(DataBlock("Second"));
//    cout << "=====================================" << endl;
//    dbVec.push_back(DataBlock("Third"));
}