package Matrix.Single;

import GPGPU.CUDA.CUDA;
import References.Single.Ref2Df;
import Vector.Single.VecCUDAf;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

public class MatCUDAf implements Ref2Df {
    static {
        CUDA.init();
    }

    final int rows, cols;
    final Pointer id;

    public MatCUDAf (int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.id = new Pointer();
        JCublas.cublasAlloc(rows * cols, Sizeof.FLOAT, id);
    }

    public MatCUDAf (Ref2Df values) {
        this(values.getRows(), values.getCols());
        set(values.colMajor().toArray());
    }

    public MatCUDAf (VecCUDAf values, int rows) {
        this(values.colMajor(rows));
    }

    public MatCUDAf (float[][] values) {
        this(new Matf(values));
    }

    private void checkCompatibility (MatCUDAf b) {
        if (rows != b.rows | cols != b.cols) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCUDAf add (float alpha, MatCUDAf b) {
        checkCompatibility(b);
        return new MatCLf()
    }

    private void set (float... values) {
        JCublas.cublasSetMatrix(rows, cols, Sizeof.FLOAT, Pointer.to(values), rows, id, rows);
    }
}
