//
// Created by chenpeigen on 23-4-18.
//

#ifndef CPP_REFERENCE__MAIN_H
#define CPP_REFERENCE__MAIN_H

#include <string>
#include <vector>

void test_vec();

void test_map();

void test_set();

void test_string();

void test_struct();

void test_pointer();

void test_rv();

void test_chrono();

void test_json();

int acquire_json();

char *acquireVector(int idx);

std::string convert_vec_to_json(std::vector<std::string> &vec);

std::vector<std::string> convert_json_to_vec(std::string basicString);

#endif //CPP_REFERENCE__MAIN_H
