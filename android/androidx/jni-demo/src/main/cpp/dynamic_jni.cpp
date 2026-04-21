#include <jni.h>
#include <string>
#include <sstream>
#include <iomanip>

// 简单哈希函数 (FNV-1a) - 演示动态注册即可
static uint32_t fnv1a_hash(const char *str, size_t len) {
    uint32_t hash = 0x811c9dc5;
    for (size_t i = 0; i < len; i++) {
        hash ^= (unsigned char)str[i];
        hash *= 0x01000193;
    }
    return hash;
}

static jstring dynamic_getVersion(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("1.0.0-dynamic");
}

static jstring dynamic_hash(JNIEnv *env, jobject thiz, jstring input) {
    const char *inputStr = env->GetStringUTFChars(input, nullptr);
    jsize len = env->GetStringUTFLength(input);

    uint32_t hash = fnv1a_hash(inputStr, len);

    env->ReleaseStringUTFChars(input, inputStr);

    std::ostringstream ss;
    ss << std::hex << std::setw(8) << std::setfill('0') << hash;
    return env->NewStringUTF(ss.str().c_str());
}

static jstring dynamic_base64Encode(JNIEnv *env, jobject thiz, jstring input) {
    const char *inputStr = env->GetStringUTFChars(input, nullptr);
    jsize len = env->GetStringUTFLength(input);

    static const char base64_chars[] =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    std::string result;
    for (int i = 0; i < len; i += 3) {
        unsigned int val = (unsigned char)inputStr[i] << 16;
        if (i + 1 < len) val |= (unsigned char)inputStr[i + 1] << 8;
        if (i + 2 < len) val |= (unsigned char)inputStr[i + 2];

        result += base64_chars[(val >> 18) & 0x3F];
        result += base64_chars[(val >> 12) & 0x3F];
        result += (i + 1 < len) ? base64_chars[(val >> 6) & 0x3F] : '=';
        result += (i + 2 < len) ? base64_chars[val & 0x3F] : '=';
    }

    env->ReleaseStringUTFChars(input, inputStr);
    return env->NewStringUTF(result.c_str());
}

static JNINativeMethod dynamicMethods[] = {
    {"getVersion", "()Ljava/lang/String;", (void *)dynamic_getVersion},
    {"md5", "(Ljava/lang/String;)Ljava/lang/String;", (void *)dynamic_hash},
    {"base64Encode", "(Ljava/lang/String;)Ljava/lang/String;", (void *)dynamic_base64Encode},
};

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass dynamicClass = env->FindClass("com/peter/jni/demo/advanced/DynamicNativeLib");
    if (!dynamicClass) return JNI_ERR;

    env->RegisterNatives(dynamicClass, dynamicMethods, sizeof(dynamicMethods) / sizeof(dynamicMethods[0]));

    return JNI_VERSION_1_6;
}
