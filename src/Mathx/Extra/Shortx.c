#include <jni.h>
#include "Mathx_Extra_Shortx.h"

JNIEXPORT jshort JNICALL Java_Mathx_Extra_Shortx_sum (JNIEnv *env, jclass thisCLass, jshort x, jshort y) {
    return x + y;
}

JNIEXPORT jshort JNICALL Java_Mathx_Extra_Shortx_subtr (JNIEnv *env, jclass thisCLass, jshort x, jshort y) {
    return x - y;
}

JNIEXPORT jshort JNICALL Java_Mathx_Extra_Shortx_mul (JNIEnv *env, jclass thisCLass, jshort x, jshort y) {
    return x * y;
}

JNIEXPORT jshort JNICALL Java_Mathx_Extra_Shortx_div (JNIEnv *env, jclass thisCLass, jshort x, jshort y) {
    return x / y;
}