//
// Created by chenpeigen on 23-4-22.
//
#include "_main.h"
#include <iostream>
#include <string>
#include <set>

void test_set() {
    {
        std::multiset<int> myMultiset;

        // 添加元素
        myMultiset.insert(10);
        myMultiset.insert(20);
        myMultiset.insert(30);
        myMultiset.insert(20);

        // 访问元素
        std::cout << "Multiset contains:";
        for (int elem: myMultiset) {
            std::cout << ' ' << elem;
        }
        std::cout << '\n';

        // 计算元素数量
        std::cout << "Multiset contains " << myMultiset.count(20) << " instances of 20.\n";

        // 删除元素
        myMultiset.erase(20);

        // 再次访问元素
        std::cout << "Multiset contains:";
        for (int elem: myMultiset) {
            std::cout << ' ' << elem;
        }
        std::cout << '\n';
    }

    {
        std::set<int> mySet;

        // Insert some elements into the set
        mySet.insert(3);
        mySet.insert(1);
        mySet.insert(5);

        // Check if an element is in the set
        if (mySet.count(1)) {
            std::cout << "1 is in the set" << std::endl;
        }

        // Iterate over the elements in the set
        for (int x: mySet) {
            std::cout << x << std::endl;
        }
    }

    {
        std::set<int> myset;
        std::set<int>::iterator it;
        std::pair<std::set<int>::iterator, bool> ret;

        // set some initial values:
        for (int i = 1; i <= 5; ++i) myset.insert(i * 10);    // set: 10 20 30 40 50

        ret = myset.insert(20);               // no new element inserted

        if (ret.second == false) it = ret.first;  // "it" now points to element 20

        myset.insert(it, 25);                 // max efficiency inserting
        myset.insert(it, 24);                 // max efficiency inserting
        myset.insert(it, 26);                 // no max efficiency inserting

        int myints[] = {5, 10, 15};              // 10 already in set, not inserted
        myset.insert(myints, myints + 3);

        std::cout << "myset contains:";
        for (it = myset.begin(); it != myset.end(); ++it)
            std::cout << ' ' << *it;
        std::cout << '\n';
    }
}