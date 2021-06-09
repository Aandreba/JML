#include <math.h>
#include <jni.h>
#include "Mathx_Mathf.h"

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_sin (JNIEnv *env, jclass thisClass, jfloat x) {
    return sinf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_cos (JNIEnv *env, jclass thisClass, jfloat x) {
    return cosf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_tan (JNIEnv *env, jclass thisClass, jfloat x) {
    return tanf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_asin (JNIEnv *env, jclass thisClass, jfloat x) {
    return asinf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_acos (JNIEnv *env, jclass thisClass, jfloat x) {
    return acosf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_atan (JNIEnv *env, jclass thisClass, jfloat x) {
    return atanf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_atan2 (JNIEnv *env, jclass thisClass, jfloat x, jfloat y) {
    return atan2f(x, y);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_sinh (JNIEnv *env, jclass thisClass, jfloat x) {
    return sinhf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_cosh (JNIEnv *env, jclass thisClass, jfloat x) {
    return coshf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_tanh (JNIEnv *env, jclass thisClass, jfloat x) {
    return tanhf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_asinh (JNIEnv *env, jclass thisClass, jfloat x) {
    return asinhf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_acosh (JNIEnv *env, jclass thisClass, jfloat x) {
    return acoshf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_atanh (JNIEnv *env, jclass thisClass, jfloat x) {
    return atanhf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_pow (JNIEnv *env, jclass thisClass, jfloat x, jfloat y) {
    return powf(x, y);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_exp (JNIEnv *env, jclass thisClass, jfloat x) {
    return expf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_expm1 (JNIEnv *env, jclass thisClass, jfloat x) {
    return expm1f(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_log (JNIEnv *env, jclass thisClass, jfloat x) {
    return logf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_log1p (JNIEnv *env, jclass thisClass, jfloat x) {
    return log1pf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_log2 (JNIEnv *env, jclass thisClass, jfloat x) {
    return log2f(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_log10 (JNIEnv *env, jclass thisClass, jfloat x) {
    return log10f(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_sqrt (JNIEnv *env, jclass thisClass, jfloat x) {
    return sqrtf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_cbrt (JNIEnv *env, jclass thisClass, jfloat x) {
    return cbrtf(x);
}

JNIEXPORT jfloat JNICALL Java_Mathx_Mathf_hypot (JNIEnv *env, jclass thisClass, jfloat x, jfloat y) {
    return hypotf(x, y);
}