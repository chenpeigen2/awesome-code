#include <jni.h>
#include <fstream>
#include <sstream>

extern "C" JNIEXPORT void JNICALL
Java_com_peter_jni_demo_intermediate_FileIONativeLib_writeFile(JNIEnv *env, jobject thiz, jstring path, jstring content) {
    const char *pathStr = env->GetStringUTFChars(path, nullptr);
    const char *contentStr = env->GetStringUTFChars(content, nullptr);

    std::ofstream file(pathStr);
    if (file.is_open()) {
        file << contentStr;
        file.close();
    }

    env->ReleaseStringUTFChars(path, pathStr);
    env->ReleaseStringUTFChars(content, contentStr);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_intermediate_FileIONativeLib_readFile(JNIEnv *env, jobject thiz, jstring path) {
    const char *pathStr = env->GetStringUTFChars(path, nullptr);

    std::ifstream file(pathStr);
    std::stringstream buffer;
    buffer << file.rdbuf();
    file.close();

    env->ReleaseStringUTFChars(path, pathStr);
    return env->NewStringUTF(buffer.str().c_str());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_peter_jni_demo_intermediate_FileIONativeLib_getFileSize(JNIEnv *env, jobject thiz, jstring path) {
    const char *pathStr = env->GetStringUTFChars(path, nullptr);

    std::ifstream file(pathStr, std::ios::binary | std::ios::ate);
    jlong size = file.tellg();
    file.close();

    env->ReleaseStringUTFChars(path, pathStr);
    return size;
}
