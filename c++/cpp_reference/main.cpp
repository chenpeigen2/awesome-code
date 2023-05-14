#include <iostream>


#include <openssl/ssl.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/err.h>
#include <string>
#include <openssl/aes.h>

#include <iostream>
#include <chrono>
#include <thread>


#include "_main.h"


//    // The request responds to a url matching:  /query?username=chanchann&password=yyy
//    server.GET("/query", [](const HttpReq *req, HttpResp *resp) {
//        const std::string &user_name = req->query("username");
//        const std::string &password = req->query("password");
//        const std::string &info = req->query("info"); // no this field
//        const std::string &address = req->default_query("address", "china");
//        resp->String(user_name + " " + password + " " + info + " " + address + "\n");
//    });
//
//    // The request responds to a url matching:  /query_has?username=chanchann&password=
//    // The logic for judging whether a parameter exists is that if the parameter value is empty, the parameter is considered to exist
//    // and the parameter does not exist unless the parameter is submitted.
//    server.GET("/query_has", [](const HttpReq *req, HttpResp *resp) {
//        if (req->has_query("password")) {
//            fprintf(stderr, "has password query\n");
//        }
//        if (req->has_query("info")) {
//            fprintf(stderr, "has info query\n");
//        }
//    });

double vals[] = {10.1, 12.6, 33.1, 24.1, 50.0};

double &setValues(int i) {
    double &ref = vals[i];
    return ref;   // 返回第 i 个元素的引用，ref 是一个引用变量，ref 引用 vals[i]


}

using namespace std;

int main() {

    cout << "改变前的值" << endl;
    for (int i = 0; i < 5; i++) {
        cout << "vals[" << i << "] = ";
        cout << vals[i] << endl;
    }

    setValues(1) = 20.23; // 改变第 2 个元素
    setValues(3) = 70.8;  // 改变第 4 个元素
    double &zz = setValues(3);
    zz = 567;

    {
//        test_vec();
//        test_map();
//        test_set();
//        test_string();
//        test_struct();
//        test_pointer();
//        test_rv();

//        test_chrono();
        test_json();
    }

//    std::string ss = text;
//    int a = text.compare("");
//    int &z = aab();
//    int &&zzz = getaa();
//    std::cout << &z << std::endl;
//    if (a == 0) {
//        std::cout << "566" << std::endl;
//    }
//    std::string aesKey = "0123456789abcdefghijklmn";
//    std::string ree11 = "MHWAz7lun/x9nQDf64ncsRN0Wp1EJ1v6sPqkSlMF43/jYyz4++ZOX/J0QfIR/WwvGE/roh12tcff6KvPrCf08ZFXb52pwMOQHM/YkeX0EIEPS6+PGGApU/h3QBO9g5+i4Tar9tZRhkVKE0oBqOoB5fPjF5PETPxRsrPyfNRSj0GQLfCcqslyDy6UPCrHQHZtUJjZzpc9EIcYqdhFNM5MCMI+1t7l4UDXRWE1m8xmc7QguCLHNaLRCOWkee7M/lhKeWqRbJTAOTx6EAnvlkb1BbKlGQC/c/MsUJJFcJ6Aa7DJAq6P6j1BTbckA2FwP7vwkr6pAAICFpvykmgUUpVvqDZd8xn/Dc8fLiAjcvlE516ty8yrofG/Buq3CSeS7Yl5NGrKCjNwgQCSfs+YvsKh1RqG/GPkjHG/CRGAfkYkIi9aCbzhybe58tO8qJGbhLBDX8OEbaZATnqZ5HDWKXRiUFkdnDZMzhJogb7Dv50rRT8RKaQTmUMJNuSmwgBy4u4E9IE+uL89qWZlxWZf9cXeG/5g54wvL4AGyt7zXGc35CLsLCUzoEAvn5b/t2nABy/XraMJYiMvh8Q7dMNm9SWj8DU2VUxuBeTx8HWOB6syCoSBOrheV8ZuRgEy1mSJHvd0kQ6p6a0Z+HqGUIipU5hDOg==";
//    ree11 = decode(ree11.c_str(), strlen(ree11.c_str()));
//    std::string text = ecb_decrypt(ree11.c_str(), ree11.length(),
//                                   reinterpret_cast<const unsigned char *>(aesKey.c_str()),
//                                   aesKey.length());
//    printf("\nthe text: %s\n", text.c_str());
    return 0;
}
