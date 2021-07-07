package org.jml.Matrix.Single;

import org.jml.GPGPU.CUDA.CUDA;
import org.jml.Matrix.Double.MatCUDAd;
import org.jml.Vector.Single.VecCUDA;
import org.jml.Vector.Single.Vec;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

public class MatCUDA {
    static {
        CUDA.init();
    }

    final public int rows, cols, size;
    final public Pointer id;

    public MatCUDA(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.size = rows * cols;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, Sizeof.FLOAT, id);
    }

    public MatCUDA(Mat values) {
        this(values.rows(), values.cols());
        set(values.colMajor().toArray());
    }

    public MatCUDA(VecCUDA values, int rows) {
        this(rows, values.size / rows);
        JCublas.cublasScopy(size, values.id, 1, this.id, 1);
    }

    public MatCUDA(float[][] values) {
        this(new Mat(values));
    }

    private void checkCompatibility (MatCUDA b) {
        if (rows != b.rows | cols != b.cols) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCUDA add (float alpha, MatCUDA b) {
        checkCompatibility(b);

        MatCUDA result = b.clone();
        JCublas.cublasSaxpy(size, alpha, id, 1, result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCUDA add (MatCUDA b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCUDA subtr (float beta, MatCUDA b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCUDA subtr (MatCUDA b) {
        return subtr(1, b);
    }

    public MatCUDA invSubtr (float alpha) {
        MatCUDA result = new MatCUDA(Vec.foreach(size, x -> alpha).rowMajor(cols));
        JCublas.cublasSaxpy(size, -1, id, 1, result.id, 1);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCUDA mul (float alpha, MatCUDA b, float beta, MatCUDA c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols) {
            throw new IllegalArgumentException();
        }

        MatCUDA result = c.clone();
        JCublas.cublasSgemm('n', 'n', rows, b.cols, cols, alpha, id, rows, b.id, b.rows, beta, result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCUDA mul (float alpha, MatCUDA b) {
        if (cols != b.rows) {
            throw new IllegalArgumentException();
        }

        MatCUDA result = new MatCUDA(rows, b.cols);
        JCublas.cublasSgemm('n', 'n', rows, b.cols, cols, alpha, id, rows, b.id, b.rows, 0, result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCUDA mul (MatCUDA b) {
        return mul(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDA mul (float alpha, VecCUDA x, float beta, VecCUDA y) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDA result = y.clone();
        JCublas.cublasSgemv('n', rows, cols, alpha, id, rows, x.id, 1, beta, result.id, 1);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDA mul (float alpha, VecCUDA x) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDA result = new VecCUDA(rows);
        JCublas.cublasSgemv('n', rows, cols, alpha, id, rows, x.id, 1, 0, result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCUDA mul (VecCUDA x) {
        return mul(1, x);
    }

    /*public MatCUDA scalMul (MatCUDA b) {
        MatCUDA result = b.clone();
        JCublas.cublasSaxpy(size, 1, this.id, 1, b.id, 1);

        return result;
    }

    public MatCUDA scalMul (float b) {
        MatCUDA result = b.clone();
        JCublas.cublasSaxpy(size, 1, this.id, 1, b.id, 1);

        return result;
    }*/

    public void release () {
        JCublas.cublasFree(id);
    }

    public int rows() {
        return rows;
    }
    
    public int cols() {
        return cols;
    }
    
    public float get (int row, int col) {
        float[] array = new float[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.FLOAT, id, rows, Pointer.to(array), rows);

        return array[(col * rows) + row];
    }
    
    public VecCUDA get (int row) {
        float[] array = new float[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.FLOAT, id, rows, Pointer.to(array), rows);

        float[] result = new float[cols];
        for (int i=0;i<cols;i++) {
            result[i] = array[(i * rows) + row];
        }

        return new VecCUDA(result);
    }

    
    public void set (int row, int col, float val) {
        float[] array = new float[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.FLOAT, id, rows, Pointer.to(array), rows);

        array[(col * rows) + row] = val;
        set(array);
    }

    
    public void set(int row, Vec values) {
        float[] array = new float[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.FLOAT, id, rows, Pointer.to(array), rows);

        for (int i=0;i<cols;i++) {
            array[(i * rows) + row] = values.get(i);
        }
        set(array);
    }

    public void set (float... values) {
        if (values.length != size) {
            throw new IndexOutOfBoundsException();
        }

        JCublas.cublasSetMatrix(rows, cols, Sizeof.FLOAT, Pointer.to(values), rows, id, rows);
    }

    
    public float[][] toArray() {
        float[] array = new float[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.FLOAT, id, rows, Pointer.to(array), rows);

        float[][] result = new float[rows][cols];
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                result[i][j] = array[(j * rows) + i];
            }
        }

        return result;
    }

    public Mat toCPU () {
        return new Mat(toArray());
    }

    
    public MatCUDAd toDouble () {
        float[] array = new float[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.FLOAT, id, rows, Pointer.to(array), rows);

        double[] casted = new double[array.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = array[i];
        }

        MatCUDAd result = new MatCUDAd(rows, cols);
        result.set(casted);
        return result;
    }

    
    public String toString() {
        float[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(new Vec(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    
    public MatCUDA clone() {
        MatCUDA clone = new MatCUDA(rows, cols);
        JCublas.cublasScopy(size, this.id, 1, clone.id, 1);

        return clone;
    }

    public static MatCUDA identity (int k) {
        MatCUDA matrix = new MatCUDA(k, k);
        JCublas.cublasScopy(k, Pointer.to(new float[]{1}), 0, matrix.id, k + 1);

        return matrix;
    }
}
