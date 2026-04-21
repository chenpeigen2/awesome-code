#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_basic_BasicNativeLib_getStringFromNative(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello from C++ JNI!");
}

extern "C" JNIEXPORT jint JNICALL
Java_com_peter_jni_demo_basic_BasicNativeLib_add(JNIEnv *env, jobject thiz, jint a, jint b) {
    return a + b;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_peter_jni_demo_basic_BasicNativeLib_subtract(JNIEnv *env, jobject thiz, jint a, jint b) {
    return a - b;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_peter_jni_demo_basic_BasicNativeLib_multiply(JNIEnv *env, jobject thiz, jint a, jint b) {
    return a * b;
}

extern "C" JNIEXPORT jdouble JNICALL
Java_com_peter_jni_demo_basic_BasicNativeLib_divide(JNIEnv *env, jobject thiz, jdouble a, jdouble b) {
    return a / b;
}
