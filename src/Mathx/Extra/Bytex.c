#include <jni.h>
#include "Mathx_Extra_Bytex.h"

JNIEXPORT jbyte JNICALL Java_Mathx_Extra_Bytex_sum (JNIEnv *env, jclass thisCLass, jbyte x, jbyte y) {
    return x + y;
}

JNIEXPORT jbyte JNICALL Java_Mathx_Extra_Bytex_subtr (JNIEnv *env, jclass thisCLass, jbyte x, jbyte y) {
    return x - y;
}

JNIEXPORT jbyte JNICALL Java_Mathx_Extra_Bytex_mul (JNIEnv *env, jclass thisCLass, jbyte x, jbyte y) {
    return x * y;
}

JNIEXPORT jbyte JNICALL Java_Mathx_Extra_Bytex_div (JNIEnv *env, jclass thisCLass, jbyte x, jbyte y) {
    return x / y;
}