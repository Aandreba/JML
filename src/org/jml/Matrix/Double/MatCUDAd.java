package org.jml.Matrix.Double;

import org.jml.GPGPU.CUDA.CUDA;
import org.jml.Matrix.Single.MatCUDA;
import org.jml.References.Double.Ref1Dd;
import org.jml.References.Double.Ref2Dd;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.VecCUDAd;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

public class MatCUDAd implements Ref2Dd {
    static {
        CUDA.init();
    }

    final public int rows, cols, size;
    final public Pointer id;

    public MatCUDAd(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.size = rows * cols;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, Sizeof.DOUBLE, id);
    }

    public MatCUDAd(Ref2Dd values) {
        this(values.getRows(), values.getCols());
        set(values.colMajor().toArray());
    }

    public MatCUDAd(VecCUDAd values, int rows) {
        this(rows, values.size / rows);
        JCublas.cublasDcopy(size, values.id, 1, this.id, 1);
    }

    public MatCUDAd(double[][] values) {
        this(new Matd(values));
    }

    private void checkCompatibility (MatCUDAd b) {
        if (rows != b.rows | cols != b.cols) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCUDAd add (double alpha, MatCUDAd b) {
        checkCompatibility(b);

        MatCUDAd result = b.clone();
        JCublas.cublasDaxpy(size, alpha, id, 1, result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCUDAd add (MatCUDAd b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCUDAd subtr (double beta, MatCUDAd b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCUDAd subtr (MatCUDAd b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCUDAd mul (double alpha, MatCUDAd b, double beta, MatCUDAd c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols) {
            throw new IllegalArgumentException();
        }

        MatCUDAd result = c.clone();
        JCublas.cublasDgemm('n', 'n', rows, b.cols, cols, alpha, id, rows, b.id, b.rows, beta, result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCUDAd mul (double alpha, MatCUDAd b) {
        if (cols != b.rows) {
            throw new IllegalArgumentException();
        }

        MatCUDAd result = new MatCUDAd(rows, b.cols);
        JCublas.cublasDgemm('n', 'n', rows, b.cols, cols, alpha, id, rows, b.id, b.rows, 0, result.id, result.rows);

        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCUDAd mul (MatCUDAd b) {
        return mul(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDAd mul (double alpha, VecCUDAd x, double beta, VecCUDAd y) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDAd result = y.clone();
        JCublas.cublasDgemv('n', rows, cols, alpha, id, rows, x.id, 1, beta, result.id, 1);

        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public VecCUDAd mul (double alpha, VecCUDAd x) {
        if (cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCUDAd result = new VecCUDAd(rows);
        JCublas.cublasDgemv('n', rows, cols, alpha, id, rows, x.id, 1, 0, result.id, 1);

        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCUDAd mul (VecCUDAd x) {
        return mul(1, x);
    }

    public MatCUDAd inverse () { // TODO https://github.com/jcuda/jcuda-samples/blob/66d72e3044b2c2e3df4b54f62f22bb5f10349b71/JCudaSamples/src/main/java/jcuda/jcublas/samples/JCublas2MatrixInvert.java#L103
        return null;
    }

    public void release () {
        JCublas.cublasFree(id);
    }

    @Override
    public int getRows () {
        return rows;
    }

    @Override
    public int getCols () {
        return cols;
    }

    @Override
    public double get (int row, int col) {
        double[] array = new double[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.DOUBLE, id, rows, Pointer.to(array), rows);

        return array[(col * rows) + row];
    }

    @Override
    public VecCUDAd get (int row) {
        double[] array = new double[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.DOUBLE, id, rows, Pointer.to(array), rows);

        double[] result = new double[cols];
        for (int i=0;i<cols;i++) {
            result[i] = array[(i * rows) + row];
        }

        return new VecCUDAd(result);
    }

    @Override
    public void set (int row, int col, double val) {
        double[] array = new double[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.DOUBLE, id, rows, Pointer.to(array), rows);

        array[(col * rows) + row] = val;
        set(array);
    }

    @Override
    public void set(int row, Ref1Dd values) {
        double[] array = new double[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.DOUBLE, id, rows, Pointer.to(array), rows);

        for (int i=0;i<cols;i++) {
            array[(i * rows) + row] = values.get(i);
        }
        set(array);
    }

    public void set (double... values) {
        if (values.length != size) {
            throw new IndexOutOfBoundsException();
        }

        JCublas.cublasSetMatrix(rows, cols, Sizeof.DOUBLE, Pointer.to(values), rows, id, rows);
    }

    @Override
    public double[][] toArray() {
        double[] array = new double[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.DOUBLE, id, rows, Pointer.to(array), rows);

        double[][] result = new double[rows][cols];
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                result[i][j] = array[(j * rows) + i];
            }
        }

        return result;
    }

    public Matd toCPU () {
        return new Matd(toArray());
    }

    @Override
    public MatCUDA toFloat () {
        double[] array = new double[size];
        JCublas.cublasGetMatrix(rows, cols, Sizeof.DOUBLE, id, rows, Pointer.to(array), rows);

        float[] casted = new float[array.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = (float) array[i];
        }

        MatCUDA result = new MatCUDA(rows, cols);
        result.set(casted);
        return result;
    }

    @Override
    public String toString() {
        double[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(new Vecd(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
    public MatCUDAd clone() {
        MatCUDAd clone = new MatCUDAd(rows, cols);
        JCublas.cublasDcopy(size, this.id, 1, clone.id, 1);

        return clone;
    }

    public static MatCUDAd identity (int k) {
        MatCUDAd matrix = new MatCUDAd(k, k);
        JCublas.cublasDcopy(k, Pointer.to(new double[]{1}), 0, matrix.id, k + 1);

        return matrix;
    }
}
