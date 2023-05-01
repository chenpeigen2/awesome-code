//
// Created by chenpeigen on 23-5-1.
//


#include <openssl/ssl.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/err.h>
#include <string>
#include <openssl/aes.h>

#include <iostream>
#include <chrono>
#include <thread>


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