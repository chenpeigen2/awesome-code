//
// Created by chenpeigen on 23-5-1.
//

#include "_main.h"

#include <fstream>
#include <nlohmann/json.hpp>
#include <iostream>
#include <fstream>
#include <iosfwd>
#include <ios>

using json = nlohmann::json;

using namespace std;

json j_arr = nullptr;

void read_json_file(json **data, const std::string &file_name) {
    ifstream file(file_name);
    ifstream valid_file(file_name);
    if (!valid_file.is_open() || !json::accept(valid_file)) {
        file.close();
        valid_file.close();
        return;
    };
    json j = json::parse(file);
    *data = new json(std::move(j));
    file.close();
}

int z = 134;

void abc(int *a) {
    *a = 456;
}

void test_json() {

    {
        int b = 2345;
        abc(&b);
        cout << b << endl;
        json *j;
        read_json_file(&j, "/home/chenpeigen/awesome-code/c++/cpp_reference/hello.json");

        auto zx1 = j->at("a").get<string>();

        cout << zx1 << endl;


//        std::istream
        std::ifstream f("/home/chenpeigen/awesome-code/c++/cpp_reference/hello.json");
        json data = json::parse(f);
        // parse from a file

        auto zx = data.at("a").get<string>();

        std::cout << "hello world" << std::endl;
    }



    // create some JSON values
    json j_object = R"( {"key": "value"} )"_json;
    json j_array = R"( [
  {
    "query": "http://localhost:8080/_find"
  },
  {
    "query": "http://localhost:8081/_find"
  }
] )"_json;
    auto zz = j_array.dump();
    j_object = {{"one", 1},
                {"two", 2}};

    auto we = j_object.dump();


    // call contains
    std::cout << std::boolalpha <<
              "j_object contains 'key': " << j_object.contains("key") << '\n' <<
              "j_object contains 'another': " << j_object.contains("another") << '\n' <<
              "j_array contains 'key': " << j_array.contains("0") << std::endl;


    j_arr = json::parse("[\n"
                        "  {\n"
                        "    \"query\": \"http://localhost:8080/_find\"\n"
                        "  },\n"
                        "  {\n"
                        "    \"query\": \"http://localhost:8081/_find\"\n"
                        "  }\n"
                        "]");

    cout << (j_arr == nullptr) << endl;

    json ex1 = json::parse(R"(
  {
    "pi": 3.141,
    "happy": true
  }
)");

    json ex2 = R"(
  {
    "pi": 3.141,
    "happy": true
  }
)"_json;

// Using initializer lists
    json ex3 = {
            {"happy", true},
            {"pi",    3.141},
    };

    json j;

// add a number that is stored as double (note the implicit conversion of j to an object)
    j["pi"] = 3.141;

// add a Boolean that is stored as bool
    j["happy"] = true;

// add a string that is stored as std::string
    j["name"] = "Niels";

// add another null object by passing nullptr
    j["nothing"] = nullptr;

// add an object inside the object
    j["answer"]["everything"] = 42;

// add an array that is stored as std::vector (using an initializer list)
    j["list"] = {1, 0, 2};

// add another object (using an initializer list of pairs)
    j["object"] = {{"currency", "USD"},
                   {"value",    42.99}};

//    cout << "hello " << j.find("value").value() << endl;

    {
        // create an array using push_back
        json j;
        j.push_back("foo");
        j.push_back(1);
        j.push_back(true);

// also use emplace_back
        j.emplace_back(1.78);

        auto zzz = j.dump();

        cout << zzz << endl;

// iterate the array
        for (json::iterator it = j.begin(); it != j.end(); ++it) {
            std::cout << *it << '\n';
        }

// range-based for
        for (auto &element: j) {
            std::cout << element << '\n';
        }

// getter/setter
        const auto tmp = j.at(0).get<std::string>();
        j[1] = 42;
        bool foo = j.at(2);

// comparison
        j == R"(["foo", 1, true, 1.78])"_json;  // true

// other stuff
        j.size();     // 4 entries
        j.empty();    // false
        j.type();     // json::value_t::array
        j.clear();    // the array is empty again

// convenience type checkers
        j.is_null();
        j.is_boolean();
        j.is_number();
        j.is_object();
        j.is_array();
        j.is_string();

// create an object
        json o;
        o["foo"] = 23;
        o["bar"] = false;
        o["baz"] = 3.141;

        auto fff = o["baz"].get<float>();

        cout << "" << endl;

// also use emplace
        o.emplace("weather", "sunny");

// special iterator member functions for objects
        for (json::iterator it = o.begin(); it != o.end(); ++it) {
            std::cout << it.key() << " : " << it.value() << "\n";
        }

// the same code as range for
        for (auto &el: o.items()) {
            std::cout << el.key() << " : " << el.value() << "\n";
        }

// even easier with structured bindings (C++17)
        for (auto &[key, value]: o.items()) {
            std::cout << key << " : " << value << "\n";
        }

// find an entry
        if (o.contains("foo")) {
            // there is an entry with key "foo"
        }

// or via find and an iterator
        if (o.find("foo") != o.end()) {
            // there is an entry with key "foo"
        }

        nlohmann::json j3;
        j3["hash"] = "abcdefg";
        j3["from"] = 0;
        j3["to"] = 10;

        auto jarray = nlohmann::json::array();
        jarray.emplace_back(j3);
        auto str = jarray.dump();

        cout << str << endl;

        auto it = j3.at("hash").get<string>();


        cout << it << endl;


// or simpler using count()
        int foo_present = o.count("foo"); // 1
        int fob_present = o.count("fob"); // 0

// delete an entry
        o.erase("foo");
    }


}