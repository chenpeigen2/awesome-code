use jni::objects::{JClass, JString};
use jni::objects::JByteArray;
use jni::sys::{jint, jstring, jboolean, jbyte, jlong};
use jni::JNIEnv;

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_getStringFromRust(
    env: JNIEnv,
    _class: JClass,
) -> jstring {
    let output = env.new_string("Hello from Rust JNI!").unwrap();
    output.into_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_add(
    _env: JNIEnv,
    _class: JClass,
    a: jint,
    b: jint,
) -> jint {
    a + b
}

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_multiply(
    _env: JNIEnv,
    _class: JClass,
    a: jint,
    b: jint,
) -> jint {
    a * b
}

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_fibonacci(
    _env: JNIEnv,
    _class: JClass,
    n: jint,
) -> jlong {
    if n <= 1 {
        return n as jlong;
    }
    let mut a: jlong = 0;
    let mut b: jlong = 1;
    for _ in 2..=n {
        let temp = a + b;
        a = b;
        b = temp;
    }
    b
}

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_reverseString(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    let input_str: String = env.get_string(&input).unwrap().into();
    let reversed: String = input_str.chars().rev().collect();
    let output = env.new_string(&reversed).unwrap();
    output.into_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_xorEncrypt<'local>(
    env: JNIEnv<'local>,
    _class: JClass<'local>,
    data: JByteArray<'local>,
    key: jbyte,
) -> JByteArray<'local> {
    let len = env.get_array_length(&data).unwrap();
    let mut bytes = vec![0i8; len as usize];
    env.get_byte_array_region(&data, 0, &mut bytes).unwrap();

    for b in bytes.iter_mut() {
        *b ^= key;
    }

    let result = env.new_byte_array(len).unwrap();
    env.set_byte_array_region(&result, 0, &bytes).unwrap();
    result
}

#[no_mangle]
pub extern "system" fn Java_com_peter_jni_demo_rust_RustNativeLib_isPrime(
    _env: JNIEnv,
    _class: JClass,
    n: jint,
) -> jboolean {
    if n < 2 {
        return 0;
    }
    if n < 4 {
        return 1;
    }
    if n % 2 == 0 || n % 3 == 0 {
        return 0;
    }
    let mut i: jint = 5;
    while i * i <= n {
        if n % i == 0 || n % (i + 2) == 0 {
            return 0;
        }
        i += 6;
    }
    1
}
