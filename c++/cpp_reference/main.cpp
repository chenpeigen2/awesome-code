#include <iostream>


#include <openssl/ssl.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/err.h>
#include <string>
#include <openssl/aes.h>

#include "_main.h"


std::string encode(const char *src, size_t src_len) {
    BIO *bmem, *b64;
    BUF_MEM *bptr;
    b64 = BIO_new(BIO_f_base64());
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);//不换行
    bmem = BIO_new(BIO_s_mem());
    b64 = BIO_push(b64, bmem);
    BIO_write(b64, src, src_len);
    BIO_flush(b64);
    BIO_get_mem_ptr(b64, &bptr);
    std::string str_result(bptr->data, bptr->length);

    BIO_free_all(b64);
    return str_result;
}

std::string decode(const char *src, size_t src_len) {
    BIO *b64, *bmem;
    b64 = BIO_new(BIO_f_base64());
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);//不换行
    bmem = BIO_new_mem_buf(src, src_len);
    bmem = BIO_push(b64, bmem);

    size_t decode_len = src_len * 3 / 4; //解码出的数据长度 <= 原长度 3/4
    std::string str_result(decode_len, 0);
    BIO_read(b64, (void *) str_result.c_str(), decode_len);
    BIO_free_all(bmem);
    return str_result;
}

/**
* 填充模式
*/
enum PaddingModel {
    ZERO,  //ZERO pading
    PKCS5OR7 //pkcs5 pkcs7 padding
};

void padding(std::string &src, int alignSize, PaddingModel mode) {
    int remainder = src.length() % alignSize;
    int paddingSize = (remainder == 0) ? alignSize : (alignSize - remainder);
    switch (mode) {
        case PKCS5OR7:
            src.append(paddingSize, paddingSize);
            break;
        case ZERO:
        default:
            src.append(paddingSize, 0);
            break;
    }
}

void unpadding(std::string &src) {
    int c = src[src.length() - 1];
    if (c > 0) src.erase(src.length() - c, c);
}

std::string ecb_encrypt(const char *src,
                        const unsigned char *key, size_t key_len,
                        PaddingModel mode) {
    AES_KEY aes_key;
    std::string str_result;
    if (AES_set_encrypt_key(key, key_len * 8, &aes_key) == 0) {
        std::string str_data = src;
        padding(str_data, AES_BLOCK_SIZE, mode);

        unsigned char out[AES_BLOCK_SIZE]{0};
        int blockSize = str_data.length() / AES_BLOCK_SIZE;
        for (int i = 0; i < blockSize; ++i) {
            const unsigned char *in = (const unsigned char *) str_data.c_str() + i * AES_BLOCK_SIZE;
            AES_ecb_encrypt(in, out, &aes_key, AES_ENCRYPT);
            str_result += std::string((const char *) out, AES_BLOCK_SIZE);
            memset(out, 0, AES_BLOCK_SIZE);
        }
    }
    return str_result;
}

std::string ecb_decrypt(const char *src, size_t src_len,
                        const unsigned char *key, size_t key_len) {
    AES_KEY aes_key;
    std::string str_result;
    if (AES_set_decrypt_key(key, key_len * 8, &aes_key) == 0) {
        unsigned char out[AES_BLOCK_SIZE]{0};
        int blockSize = src_len / AES_BLOCK_SIZE;
        for (int i = 0; i < blockSize; ++i) {
            const unsigned char *in = (const unsigned char *) src + i * AES_BLOCK_SIZE;
            AES_ecb_encrypt(in, out, &aes_key, AES_DECRYPT);
            str_result += std::string((const char *) out, AES_BLOCK_SIZE);
            memset(out, 0, AES_BLOCK_SIZE);
        }
        unpadding(str_result);
    }
    return str_result;
}

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

std::string text;

int &aab() {
    int b = 5;
    return b;
}

int &&getaa() {

    return 12;
}

int main() {

    {
        test_vec();
        test_map();
        test_set();
        test_string();
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
