#include <jni.h>
#include <string>

extern "C" JNIEXPORT void JNICALL
Java_com_peter_jni_demo_intermediate_CallbackNativeLib_doWork(JNIEnv *env, jobject thiz, jobject callback) {
    jclass callbackClass = env->GetObjectClass(callback);
    jmethodID onProgressMethod = env->GetMethodID(callbackClass, "onProgress", "(I)V");
    jmethodID onCompleteMethod = env->GetMethodID(callbackClass, "onComplete", "(Ljava/lang/String;)V");

    for (int i = 0; i <= 100; i += 20) {
        env->CallVoidMethod(callback, onProgressMethod, i);
    }

    jstring result = env->NewStringUTF("Work completed successfully!");
    env->CallVoidMethod(callback, onCompleteMethod, result);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_intermediate_CallbackNativeLib_callStaticMethod(JNIEnv *env, jobject thiz, jint value) {
    jclass activityClass = env->FindClass("com/peter/jni/demo/intermediate/CallbackActivity");
    jmethodID staticMethod = env->GetStaticMethodID(activityClass, "staticMethod", "(I)Ljava/lang/String;");

    jstring result = (jstring) env->CallStaticObjectMethod(activityClass, staticMethod, value);
    const char *resultStr = env->GetStringUTFChars(result, nullptr);
    std::string copy(resultStr);
    env->ReleaseStringUTFChars(result, resultStr);
    return env->NewStringUTF(copy.c_str());
}
