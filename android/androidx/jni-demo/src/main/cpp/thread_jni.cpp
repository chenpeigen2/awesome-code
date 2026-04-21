#include <jni.h>
#include <thread>
#include <vector>
#include <algorithm>
#include <mutex>
#include <android/log.h>

#define LOG_TAG "JNI_THREAD"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_peter_jni_demo_advanced_ThreadCallbackNativeLib_startBackgroundWork(JNIEnv *env, jobject thiz, jobject callback) {
    JavaVM *vm = nullptr;
    env->GetJavaVM(&vm);
    jobject globalCallback = env->NewGlobalRef(callback);

    std::thread([vm, globalCallback]() {
        JNIEnv *threadEnv = nullptr;
        bool attached = false;

        int ret = vm->GetEnv(reinterpret_cast<void **>(&threadEnv), JNI_VERSION_1_6);
        if (ret == JNI_EDETACHED) {
            JavaVMAttachArgs args = {JNI_VERSION_1_6, "JNI_Worker", nullptr};
            if (vm->AttachCurrentThread(&threadEnv, &args) == JNI_OK) {
                attached = true;
            } else {
                LOGI("Failed to attach thread");
                return;
            }
        }

        jclass callbackClass = threadEnv->GetObjectClass(globalCallback);
        jmethodID onProgressMethod = threadEnv->GetMethodID(callbackClass, "onProgress", "(ILjava/lang/String;)V");
        jmethodID onCompleteMethod = threadEnv->GetMethodID(callbackClass, "onComplete", "(Ljava/lang/String;)V");

        const char *steps[] = {
            "Initializing...",
            "Loading data...",
            "Processing...",
            "Calculating...",
            "Finalizing..."
        };

        for (int i = 0; i < 5; i++) {
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            jstring msg = threadEnv->NewStringUTF(steps[i]);
            threadEnv->CallVoidMethod(globalCallback, onProgressMethod, i + 1, msg);
            threadEnv->DeleteLocalRef(msg);
        }

        jstring result = threadEnv->NewStringUTF("All steps completed in C++ thread!");
        threadEnv->CallVoidMethod(globalCallback, onCompleteMethod, result);
        threadEnv->DeleteLocalRef(result);

        threadEnv->DeleteGlobalRef(globalCallback);

        if (attached) {
            vm->DetachCurrentThread();
        }
    }).detach();
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_peter_jni_demo_advanced_ThreadCallbackNativeLib_multiThreadCompute(JNIEnv *env, jobject thiz, jintArray data) {
    jsize len = env->GetArrayLength(data);
    jint *input = env->GetIntArrayElements(data, nullptr);

    jint *results = new jint[len];
    std::vector<std::thread> threads;

    auto computeSquare = [](jint val) -> jint {
        return val * val;
    };

    for (jsize i = 0; i < len; i++) {
        threads.emplace_back([input, results, i, computeSquare]() {
            results[i] = computeSquare(input[i]);
        });
    }

    for (auto &t : threads) {
        t.join();
    }

    jintArray resultArray = env->NewIntArray(len);
    env->SetIntArrayRegion(resultArray, 0, len, results);

    env->ReleaseIntArrayElements(data, input, JNI_ABORT);
    delete[] results;
    return resultArray;
}
