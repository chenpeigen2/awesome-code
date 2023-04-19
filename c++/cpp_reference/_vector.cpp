//
// Created by chenpeigen on 23-4-18.
//

#include "_vector.h"
#include <vector>
#include <iostream>
#include <algorithm>

using namespace std;

void test_vec() {
    vector<int> obj;
    for (int i = 0; i < 10; i++) // push_back(elem)在数组最后添加数据
    {
        obj.push_back(i);
        cout << obj[i] << ",";
    }

    for (int i = 0; i < 5; i++)//去掉数组最后一个数据
    {
        obj.pop_back();
    }
    for (int i = 0; i < obj.size(); i++)//size()容器中实际数据个数
    {
        cout << obj[i] << ",";
    }


}
