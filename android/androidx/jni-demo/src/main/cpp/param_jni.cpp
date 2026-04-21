#include <jni.h>
#include <string>
#include <sstream>

extern "C" JNIEXPORT jint JNICALL
Java_com_peter_jni_demo_basic_ParamNativeLib_square(JNIEnv *env, jobject thiz, jint n) {
    return n * n;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_basic_ParamNativeLib_formatFloat(JNIEnv *env, jobject thiz, jfloat value) {
    std::ostringstream ss;
    ss << "float value = " << value << ", as int = " << (int)value;
    return env->NewStringUTF(ss.str().c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_peter_jni_demo_basic_ParamNativeLib_invertBoolean(JNIEnv *env, jobject thiz, jboolean value) {
    return !value;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_basic_ParamNativeLib_greet(JNIEnv *env, jobject thiz, jstring name) {
    const char *nameStr = env->GetStringUTFChars(name, nullptr);
    std::string result = "Hello, " + std::string(nameStr) + "! Welcome to JNI.";
    env->ReleaseStringUTFChars(name, nameStr);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_basic_ParamNativeLib_reverseString(JNIEnv *env, jobject thiz, jstring str) {
    const char *strChars = env->GetStringUTFChars(str, nullptr);
    jsize len = env->GetStringUTFLength(str);
    std::string reversed(strChars, len);
    std::reverse(reversed.begin(), reversed.end());
    env->ReleaseStringUTFChars(str, strChars);
    return env->NewStringUTF(reversed.c_str());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_peter_jni_demo_basic_ParamNativeLib_factorial(JNIEnv *env, jobject thiz, jlong n) {
    jlong result = 1;
    for (jlong i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
}
