#include <math.h>
#include <jni.h>
#include "Mathx_Mathd.h"

JNIEXPORT jdouble JNICALL Java_org_jml_Mathx_Mathd_asinh (JNIEnv *env, jclass thisClass, jdouble x) {
    return asinh(x);
}

JNIEXPORT jdouble JNICALL Java_org_jml_Mathx_Mathd_acosh (JNIEnv *env, jclass thisClass, jdouble x) {
    return acosh(x);
}

JNIEXPORT jdouble JNICALL Java_org_jml_Mathx_Mathd_atanh (JNIEnv *env, jclass thisClass, jdouble x) {
    return atanh(x);
}

JNIEXPORT jdouble JNICALL Java_Mathx_Mathf_log2 (JNIEnv *env, jclass thisClass, jfloat x) {
    return log2(x);
}