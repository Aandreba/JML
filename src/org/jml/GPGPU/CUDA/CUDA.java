package org.jml.GPGPU.CUDA;

import jcuda.jcublas.JCublas;
import jcuda.jcublas.JCublas2;

public class CUDA {
    public static void init () {
        JCublas.setExceptionsEnabled(true);
        JCublas.initialize();
        JCublas.cublasInit();
    }
}
