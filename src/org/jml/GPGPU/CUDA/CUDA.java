package org.jml.GPGPU.CUDA;

import jcuda.jcublas.JCublas;

public class CUDA {
    public static void init () {
        JCublas.setExceptionsEnabled(true);
        JCublas.initialize();
        JCublas.cublasInit();
    }
}
