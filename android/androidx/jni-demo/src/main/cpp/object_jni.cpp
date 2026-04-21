#include <jni.h>
#include <string>

extern "C" JNIEXPORT jobject JNICALL
Java_com_peter_jni_demo_intermediate_ObjectNativeLib_createUser(JNIEnv *env, jobject thiz, jstring name, jint age) {
    jclass userClass = env->FindClass("com/peter/jni/demo/intermediate/User");
    if (!userClass) return nullptr;

    jmethodID constructor = env->GetMethodID(userClass, "<init>", "(Ljava/lang/String;I)V");
    if (!constructor) return nullptr;

    return env->NewObject(userClass, constructor, name, age);
}

extern "C" JNIEXPORT void JNICALL
Java_com_peter_jni_demo_intermediate_ObjectNativeLib_modifyUserFields(JNIEnv *env, jobject thiz, jobject user) {
    jclass userClass = env->GetObjectClass(user);

    jfieldID nameField = env->GetFieldID(userClass, "name", "Ljava/lang/String;");
    jfieldID ageField = env->GetFieldID(userClass, "age", "I");

    jstring newName = env->NewStringUTF("Modified_By_C++");
    env->SetObjectField(user, nameField, newName);
    env->SetIntField(user, ageField, 99);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_intermediate_ObjectNativeLib_callUserMethod(JNIEnv *env, jobject thiz, jobject user) {
    jclass userClass = env->GetObjectClass(user);
    jmethodID getInfoMethod = env->GetMethodID(userClass, "getInfo", "()Ljava/lang/String;");

    jstring result = (jstring) env->CallObjectMethod(user, getInfoMethod);
    const char *resultStr = env->GetStringUTFChars(result, nullptr);
    std::string copy(resultStr);
    env->ReleaseStringUTFChars(result, resultStr);
    return env->NewStringUTF(copy.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_peter_jni_demo_intermediate_ObjectNativeLib_createStringFromChars(JNIEnv *env, jobject thiz, jcharArray chars) {
    jsize len = env->GetArrayLength(chars);
    jchar *elements = env->GetCharArrayElements(chars, nullptr);

    jcharArray newArray = env->NewCharArray(len);
    env->SetCharArrayRegion(newArray, 0, len, elements);

    jmethodID stringConstructor = env->GetMethodID(
        env->FindClass("java/lang/String"),
        "<init>", "([C)V"
    );
    jstring result = (jstring) env->NewObject(
        env->FindClass("java/lang/String"),
        stringConstructor,
        newArray
    );

    env->ReleaseCharArrayElements(chars, elements, JNI_ABORT);
    return result;
}
