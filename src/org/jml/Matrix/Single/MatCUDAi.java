package org.jml.Matrix.Single;

import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.CUDA.CUDA;
import org.jml.Matrix.Double.MatCUDAid;
import org.jml.Vector.Single.VecCUDAi;
import org.jml.Vector.Single.Veci;
import jcuda.Pointer;
import jcuda.jcublas.JCublas;

public class MatCUDAi {
    static {
        CUDA.init();
    }

    final public int rows, cols, size;
    final public Pointer id;

    public MatCUDAi(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.size = rows * cols;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, VecCUDAi.ELEMSIZE, id);
    }

    public MatCUDAi(Mati values) {
        this(values.rows(), values.cols());
        set(values.colMajor().toArray());
    }

    public MatCUDAi(VecCUDAi values, int rows) {
        this(rows, values.size / rows);
        JCublas.cublasCcopy(size, values.id, 1, this.id, 1);
    }

    public MatCUDAi(Comp[][] values) {
        this(new Mati(values));
    }

    private void checkCompatibility (MatCUDAi b) {
        if (rows != b.rows | cols != b.cols) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCUDAi add (Comp alpha, MatCUDAi b) {
        checkCompatibility(b);

        MatCUDAi result = b.clone();
        JCublas.cublasCaxpy(size, alpha.toCUDA(), id, 1, result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCUDAi add (MatCUDAi b) {
        return add(Comp.ONE, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCUDAi subtr (Comp beta, MatCUDAi b) {
        return b.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCUDAi subtr (MatCUDAi b) {
        return subtr(Comp.ONE, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCUDAi mul (Comp alpha, MatCUDAi b, Comp beta, MatCUDAi c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols) {
            throw new IllegalArgumentException();
        }

        MatCUDAi result = c.clone();
        JCublas.cublasCgemm('n', 'n', rows, b.cols, cols, alpha.toCUDA(), id, rows, b.id, b.rows, beta.toCUDA(), result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCUDAi mul (Comp alpha, MatCUDAi b) {
        if (cols != b.rows) {
            throw new IllegalArgumentException();
        }

        MatCUDAi result = new MatCUDAi(rows, b.cols);
        JCublas.cublasCgemm('n', 'n', rows, b.cols, cols, alpha.toCUDA(), id, rows, b.id, b.rows, Comp.ZERO.toCUDA(), result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCUDAi mul (MatCUDAi b) {
        return mul(Comp.ONE, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDAi mul (Comp alpha, VecCUDAi x, Comp beta, VecCUDAi y) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDAi result = y.clone();
        JCublas.cublasCgemv('n', rows, cols, alpha.toCUDA(), id, rows, x.id, 1, beta.toCUDA(), result.id, 1);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDAi mul (Comp alpha, VecCUDAi x) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDAi result = new VecCUDAi(rows);
        JCublas.cublasCgemv('n', rows, cols, alpha.toCUDA(), id, rows, x.id, 1, Comp.ZERO.toCUDA(), result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCUDAi mul (VecCUDAi x) {
        return mul(Comp.ONE, x);
    }

    public void release () {
        JCublas.cublasFree(id);
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    private float[] toFloatArray () {
        float[] array = new float[2 * size];
        JCublas.cublasGetVector(size, VecCUDAi.ELEMSIZE, id, 1, Pointer.to(array), 1);

        return array;
    }
    
    public Comp get (int row, int col) {
        float[] array = toFloatArray();
        int i = (col * rows) + row;
        int j = 2 * i;

        return new Comp(array[j], array[j+1]);
    }

    public VecCUDAi get (int row) {
        float[] array = toFloatArray();

        Comp[] result = new Comp[cols];
        for (int i=0;i<cols;i++) {
            int j = (i * rows) + row;
            int k = 2 * j;
            result[i] = new Comp(array[k], array[k+1]);
        }

        return new VecCUDAi(result);
    }
    
    public void set (int row, int col, Comp val) {
        float[] array = toFloatArray();
        int i = (col * rows) + row;
        int j = 2 * i;

        array[j] = val.real;
        array[j+1] = val.imaginary;
        JCublas.cublasSetVector(size, VecCUDAi.ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    public void set (int row, Veci values) {
        float[] array = toFloatArray();
        for (int i=0;i<cols;i++) {
            int j = (i * rows) + row;
            int k = 2 * j;

            array[k] = values.get(i).real;
            array[k+1] = values.get(i).imaginary;
        }

        JCublas.cublasSetVector(size, VecCUDAi.ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    public void set (Comp... values) {
        if (values.length != size) {
            throw new IllegalArgumentException();
        }

        float[] cuda = new float[2 * values.length];
        for (int i=0;i<values.length;i++) {
            int j = 2 * i;
            cuda[j] = values[i].real;
            cuda[j+1] = values[i].imaginary;
        }

        JCublas.cublasSetVector(size, VecCUDAi.ELEMSIZE, Pointer.to(cuda), 1, id, 1);
    }

    
    public Comp[][] toArray() {
        float[] array = toFloatArray();
        Comp[][] result = new Comp[rows][cols];

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                int k = (j * rows) + i;
                int q = 2 * k;
                result[i][j] = new Comp(array[q], array[q+1]);
            }
        }

        return result;
    }

    public Mati toCPU () {
        return new Mati(toArray());
    }

    
    public MatCUDAid toDouble () {
        float[] array = toFloatArray();
        double[] casted = new double[array.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = array[i];
        }

        MatCUDAid result = new MatCUDAid(rows, cols);
        JCublas.cublasSetVector(size, VecCUDAi.ELEMSIZE, Pointer.to(casted), 1, result.id, 1);
        return result;
    }

    
    public String toString() {
        Comp[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(new Veci(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    
    public MatCUDAi clone() {
        MatCUDAi clone = new MatCUDAi(rows, cols);
        JCublas.cublasCcopy(size, this.id, 1, clone.id, 1);

        return clone;
    }

    public static MatCUDAi identity (int k) {
        MatCUDAi matrix = new MatCUDAi(k, k);
        JCublas.cublasCcopy(k, Pointer.to(new float[]{ 1,0 }), 0, matrix.id, k + 1);

        return matrix;
    }
}
