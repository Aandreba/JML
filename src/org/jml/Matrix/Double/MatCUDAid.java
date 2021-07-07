package org.jml.Matrix.Double;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.CUDA.CUDA;
import org.jml.Matrix.Single.MatCUDA;
import org.jml.Matrix.Single.MatCUDAi;
import org.jml.Vector.Double.VecCUDAid;
import org.jml.Vector.Double.Vecid;
import jcuda.Pointer;
import jcuda.cuComplex;
import jcuda.cuDoubleComplex;
import jcuda.jcublas.JCublas;

public class MatCUDAid {
    static {
        CUDA.init();
    }

    final public int rows, cols, size;
    final public Pointer id;

    public MatCUDAid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.size = rows * cols;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, VecCUDAid.ELEMSIZE, id);
    }

    public MatCUDAid(Matid values) {
        this(values.rows(), values.cols());
        set(values.colMajor());
    }

    public MatCUDAid(VecCUDAid values, int rows) {
        this(rows, values.size / rows);
        JCublas.cublasZcopy(size, values.id, 1, this.id, 1);
    }

    public MatCUDAid(Compd[][] values) {
        this(new Matid(values));
    }

    private void checkCompatibility (MatCUDAid b) {
        if (rows != b.rows | cols != b.cols) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCUDAid add (Compd alpha, MatCUDAid b) {
        checkCompatibility(b);

        MatCUDAid result = b.clone();
        JCublas.cublasZaxpy(size, alpha.toCUDA(), id, 1, result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCUDAid add (MatCUDAid b) {
        return add(Compd.ONE, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCUDAid subtr (Compd beta, MatCUDAid b) {
        return b.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCUDAid subtr (MatCUDAid b) {
        return subtr(Compd.ONE, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCUDAid mul (Compd alpha, MatCUDAid b, Compd beta, MatCUDAid c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols) {
            throw new IllegalArgumentException();
        }

        MatCUDAid result = c.clone();
        JCublas.cublasZgemm('n', 'n', rows, b.cols, cols, alpha.toCUDA(), id, rows, b.id, b.rows, beta.toCUDA(), result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCUDAid mul (Compd alpha, MatCUDAid b) {
        if (cols != b.rows) {
            throw new IllegalArgumentException();
        }

        MatCUDAid result = new MatCUDAid(rows, b.cols);
        JCublas.cublasZgemm('n', 'n', rows, b.cols, cols, alpha.toCUDA(), id, rows, b.id, b.rows, Compd.ZERO.toCUDA(), result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCUDAid mul (MatCUDAid b) {
        return mul(Compd.ONE, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDAid mul (Compd alpha, VecCUDAid x, Compd beta, VecCUDAid y) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDAid result = y.clone();
        JCublas.cublasZgemv('n', rows, cols, alpha.toCUDA(), id, rows, x.id, 1, beta.toCUDA(), result.id, 1);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDAid mul (Compd alpha, VecCUDAid x) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDAid result = new VecCUDAid(rows);
        JCublas.cublasZgemv('n', rows, cols, alpha.toCUDA(), id, rows, x.id, 1, Compd.ZERO.toCUDA(), result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCUDAid mul (VecCUDAid x) {
        return mul(Compd.ONE, x);
    }

    public MatCUDAid scalMul (MatCUDAid b) {
        checkCompatibility(b);

        MatCUDAid mul = new MatCUDAid(size, size);
        JCublas.cublasZcopy(size, id, 1, mul.id, size + 1);

        MatCUDAid result = b.clone();
        JCublas.cublasZtrmv('l', 'n', 'n', size, mul.id, size, result.id, 1);

        mul.release();
        return result;
    }

    public MatCUDAid scalMul (Compd b) {
        MatCUDAid result = new MatCUDAid(rows, cols);
        JCublas.cublasZaxpy(size, b.toCUDA(), this.id, 1, result.id, 1);

        return result;
    }

    public MatCUDAid scalMul (double b) {
        MatCUDAid result = new MatCUDAid(rows, cols);
        JCublas.cublasZaxpy(size, cuDoubleComplex.cuCmplx(b, 0), this.id, 1, result.id, 1);

        return result;
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

    private double[] toDoubleArray () {
        double[] array = new double[2 * size];
        JCublas.cublasGetVector(size, VecCUDAid.ELEMSIZE, id, 1, Pointer.to(array), 1);

        return array;
    }
    
    public Compd get (int row, int col) {
        double[] array = toDoubleArray();
        int i = (col * rows) + row;
        int j = 2 * i;

        return new Compd(array[j], array[j+1]);
    }
    
    public VecCUDAid get (int row) {
        double[] array = toDoubleArray();
        Compd[] result = new Compd[cols];

        for (int i=0;i<cols;i++) {
            int j = (i * rows) + row;
            int k = 2 * j;
            result[i] = new Compd(array[k], array[k+1]);
        }

        return new VecCUDAid(result);
    }

    
    public void set (int row, int col, Compd val) {
        double[] array = toDoubleArray();
        int i = (col * rows) + row;
        int j = 2 * i;

        array[j] = val.real;
        array[j+1] = val.imaginary;
        JCublas.cublasSetVector(size, VecCUDAid.ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    
    public void set (int row, Vecid values) {
        double[] array = toDoubleArray();
        for (int i=0;i<cols;i++) {
            int j = (i * rows) + row;
            int k = 2 * j;

            array[k] = values.get(i).real;
            array[k+1] = values.get(i).imaginary;
        }

        JCublas.cublasSetVector(size, VecCUDAid.ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    public void set (Compd... values) {
        if (values.length != size) {
            throw new IllegalArgumentException();
        }

        double[] cuda = new double[2 * values.length];
        for (int i=0;i<values.length;i++) {
            int j = 2 * i;
            cuda[j] = values[i].real;
            cuda[j+1] = values[i].imaginary;
        }

        JCublas.cublasSetVector(size, VecCUDAid.ELEMSIZE, Pointer.to(cuda), 1, id, 1);
    }

    
    public Compd[][] toArray() {
        double[] array = toDoubleArray();
        Compd[][] result = new Compd[rows][cols];

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                int k = (j * rows) + i;
                int q = 2 * k;
                result[i][j] = new Compd(array[q], array[q+1]);
            }
        }

        return result;
    }

    public Matid toCPU () {
        return new Matid(toArray());
    }

    
    public MatCUDAi toFloat () {
        cuDoubleComplex[] array = new cuDoubleComplex[size];
        JCublas.cublasGetMatrix(rows, cols, Pointer.to(id), rows, array, 0, rows);

        cuComplex[] casted = new cuComplex[array.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = cuComplex.cuCmplx((float) array[i].x, (float) array[i].y);
        }

        MatCUDAi result = new MatCUDAi(rows, cols);
        JCublas.cublasSetMatrix(rows, cols, casted, 0, rows, Pointer.to(result.id), rows);
        return result;
    }

    
    public String toString() {
        Compd[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(new Vecid(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    
    public MatCUDAid clone() {
        MatCUDAid clone = new MatCUDAid(rows, cols);
        JCublas.cublasZcopy(size, this.id, 1, clone.id, 1);

        return clone;
    }

    public static MatCUDAid identity (int k) {
        MatCUDAid matrix = new MatCUDAid(k, k);
        JCublas.cublasZcopy(k, Pointer.to(new double[]{ 1,0 }), 0, matrix.id, k + 1);

        return matrix;
    }
}
