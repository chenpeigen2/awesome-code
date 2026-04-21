#include <jni.h>
#include <algorithm>
#include <numeric>

extern "C" JNIEXPORT jint JNICALL
Java_com_peter_jni_demo_basic_ArrayNativeLib_sumArray(JNIEnv *env, jobject thiz, jintArray arr) {
    jint *elements = env->GetIntArrayElements(arr, nullptr);
    jsize len = env->GetArrayLength(arr);
    jint sum = 0;
    for (jsize i = 0; i < len; i++) {
        sum += elements[i];
    }
    env->ReleaseIntArrayElements(arr, elements, JNI_ABORT);
    return sum;
}

extern "C" JNIEXPORT void JNICALL
Java_com_peter_jni_demo_basic_ArrayNativeLib_sortArray(JNIEnv *env, jobject thiz, jintArray arr) {
    jint *elements = env->GetIntArrayElements(arr, nullptr);
    jsize len = env->GetArrayLength(arr);
    std::sort(elements, elements + len);
    env->ReleaseIntArrayElements(arr, elements, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_com_peter_jni_demo_basic_ArrayNativeLib_reverseArray(JNIEnv *env, jobject thiz, jintArray arr) {
    jint *elements = env->GetIntArrayElements(arr, nullptr);
    jsize len = env->GetArrayLength(arr);
    std::reverse(elements, elements + len);
    env->ReleaseIntArrayElements(arr, elements, 0);
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_peter_jni_demo_basic_ArrayNativeLib_xorByteArray(JNIEnv *env, jobject thiz, jbyteArray data, jbyte key) {
    jsize len = env->GetArrayLength(data);
    jbyte *elements = env->GetByteArrayElements(data, nullptr);

    jbyteArray result = env->NewByteArray(len);
    jbyte *resultElements = new jbyte[len];

    for (jsize i = 0; i < len; i++) {
        resultElements[i] = elements[i] ^ key;
    }

    env->SetByteArrayRegion(result, 0, len, resultElements);
    env->ReleaseByteArrayElements(data, elements, JNI_ABORT);
    delete[] resultElements;
    return result;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_peter_jni_demo_basic_ArrayNativeLib_createIntArray(JNIEnv *env, jobject thiz, jint size) {
    jintArray result = env->NewIntArray(size);
    jint *elements = new jint[size];
    for (jint i = 0; i < size; i++) {
        elements[i] = i * i;
    }
    env->SetIntArrayRegion(result, 0, size, elements);
    delete[] elements;
    return result;
}
