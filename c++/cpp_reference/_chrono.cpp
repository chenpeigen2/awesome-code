//
// Created by chenpeigen on 23-4-30.
//
#include <iostream>
#include <chrono>
#include <thread>
#include "_main.h"


void test_chrono() {
    auto start = std::chrono::steady_clock::now(); // 获取当前时间点
    auto refresh_time = std::chrono::seconds(3); // 定义刷新时间间隔

    while (true) {
        auto now = std::chrono::steady_clock::now(); // 获取当前时间点
        auto elapsed_time = std::chrono::duration_cast<std::chrono::milliseconds>(now - start); // 计算时间差

        if (elapsed_time >= refresh_time) { // 如果达到刷新时间间隔
            std::cout << "5分钟已经过去了，重新刷新！" << std::endl; // 输出消息
            start = now; // 更新起始时间点
        }

        // 执行其他操作
        // ...

        std::this_thread::sleep_for(std::chrono::milliseconds(100)); // 等待一段时间
    }
}