//
// Created by chenpeigen on 23-4-30.
//
#include <iostream>
#include <chrono>
#include <thread>
#include "_main.h"


#define ELAPSE_SECONDS 3

auto refresh_time = std::chrono::seconds(ELAPSE_SECONDS); // 定义刷新时间间隔

std::chrono::time_point<std::chrono::steady_clock, std::chrono::nanoseconds> start;

void refreshIfNeeded() {
    auto now = std::chrono::steady_clock::now(); // 获取当前时间点
    if (start.time_since_epoch().count() == 0) {
        start = now;
    }
    auto elapsed_time = std::chrono::duration_cast<std::chrono::milliseconds>(now - start); // 计算时间差
    if (elapsed_time >= refresh_time) { // 如果达到刷新时间间隔
        std::cout << "3秒已经过去了，重新刷新！"
                  << std::chrono::time_point_cast<std::chrono::seconds>(now).time_since_epoch().count()
                  << std::endl; // 输出消息
        start = now; // 更新起始时间点
    }
}


void test_chrono() {
    while (true) {
        refreshIfNeeded();
        std::this_thread::sleep_for(std::chrono::milliseconds(100)); // 等待一段时间
    }
}