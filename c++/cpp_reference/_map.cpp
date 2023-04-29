//
// Created by chenpeigen on 23-4-22.
//
#include "_main.h"
#include <map>
#include <iostream>
#include <string>

using namespace std;

struct Person {
    std::string name;
    int age;
};

namespace _map {
    class Person {
    public:
        Person(const std::string &n, int a) : name(n), age(a) {}

        std::string getName() const { return name; }

        int getAge() const { return age; }

    private:
        std::string name;
        int age;
    };

    struct PersonCompare {
        bool operator()(const Person &p1, const Person &p2) const {
            return p1.getAge() < p2.getAge();
        }
    };

}

void test_map() {
    // test multi map
    {
        std::multimap<std::string, Person> people;

        // 插入数据
        people.insert(std::pair<std::string, Person>("Smith", {"John", 25}));
        people.insert(std::pair<std::string, Person>("Jones", {"Alice", 30}));
        people.insert(std::pair<std::string, Person>("Smith", {"Jane", 22}));
        people.insert(std::pair<std::string, Person>("Brown", {"Bob", 40}));

        std::cout << endl;
        // 查找数据
        std::string lastName = "Smith";
        std::multimap<std::string, Person>::iterator it;
        for (it = people.find(lastName); it != people.end() && it->first == lastName; ++it) {
            std::cout << it->second.name << " is " << it->second.age << " years old" << std::endl;
        }

        // 删除数据
        lastName = "Smith";
        auto range = people.equal_range(lastName);
        for (auto it = range.first; it != range.second; ++it) {
            if (it->second.name == "John") {
                people.erase(it);
                break;
            }
        }

        {
            std::multimap<int, std::string> myMap;

            // 插入数据
            myMap.insert(std::pair<int, std::string>(1, "apple"));
            myMap.insert(std::pair<int, std::string>(2, "banana"));
            myMap.insert(std::pair<int, std::string>(1, "orange"));

            // 查找数据
            std::multimap<int, std::string>::iterator it;
            it = myMap.find(1);
            if (it != myMap.end()) {
                std::cout << "Found " << it->second << " with key " << it->first << std::endl;
            }

            // 查找所有的数据 -1
            auto p = myMap.equal_range(1);
            for (it = p.first; it != p.second; ++it) {
                cout << it->second << endl;
            }

            // 查找所有的数据 -2
            it = myMap.find(1);
            for (int i = 0, len = myMap.count(1); i < len; ++i, ++it) {
                cout << it->second << endl;
            }

            // 删除数据
            myMap.erase(1);
        }
    }


    {
        map<string, int> myMap;
        // 插入键值对
        myMap.insert({"apple", 3});
        myMap.insert({"banana", 5});
        myMap.insert({"orange", 2});
        myMap.insert({"apple", 7});
        myMap.emplace("apple", 8);
        myMap.insert(std::pair<string, int>("aaa", 5));

        // 访问元素
        std::cout << "The value of apple is: " << myMap["apple"] << std::endl;

        // 遍历map
        for (const auto &[key, value]: myMap) {
            std::cout << key << " : " << value << std::endl;
        }
    }

    {
        map<int, string> myMap;
        myMap.insert({1, "a"});
        myMap.insert({2, "b"});
        myMap.insert({3, "c"});
        myMap.insert({1, "d"});
        myMap.insert({1, "d"});

    }

    {
        std::map<_map::Person, std::string, _map::PersonCompare> myMap;

        myMap.emplace(_map::Person("Alice", 25), "Programmer");
        myMap.emplace(_map::Person("Bob", 30), "Engineer");
        myMap.emplace(_map::Person("Charlie", 20), "Student");

        for (const auto &[person, profession]: myMap) {
            std::cout << person.getName() << " is a " << profession << ", aged " << person.getAge() << std::endl;
        }

    }

    {
        std::map<char, int> mymap;
        char c;
        mymap['a'] = 101;
        mymap['a'] = 345;
        cout << "mappppppppppppppppppp" << endl;
        cout << mymap['a'] << endl;
    }

    {
        std::map<char, string> mymap;
        mymap['a'] = "aaaabcdede";

        cout << mymap['b'] << endl;
        cout << mymap['c'] << endl;
        cout << "hello " << endl;
    }

}