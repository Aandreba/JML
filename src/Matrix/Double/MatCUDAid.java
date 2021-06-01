package Matrix.Double;

import Complex.Compd;
import GPGPU.CUDA.CUDA;
import Matrix.Single.MatCUDAi;
import References.Double.Complex.Ref1Di;
import References.Double.Complex.Ref2Di;
import Vector.Double.VecCUDAid;
import Vector.Double.Vecid;
import jcuda.Pointer;
import jcuda.cuComplex;
import jcuda.cuDoubleComplex;
import jcuda.jcublas.JCublas;

public class MatCUDAid implements Ref2Di {
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

    public MatCUDAid(Ref2Di values) {
        this(values.getRows(), values.getCols());
        set(values.colMajor().toArray());
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

    private double[] toDoubleArray () {
        double[] array = new double[2 * size];
        JCublas.cublasGetVector(size, VecCUDAid.ELEMSIZE, id, 1, Pointer.to(array), 1);

        return array;
    }

    @Override
    public Compd get (int row, int col) {
        double[] array = toDoubleArray();
        int i = (col * rows) + row;
        int j = 2 * i;

        return new Compd(array[j], array[j+1]);
    }

    @Override
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

    @Override
    public void set (int row, int col, Compd val) {
        double[] array = toDoubleArray();
        int i = (col * rows) + row;
        int j = 2 * i;

        array[j] = val.real;
        array[j+1] = val.imaginary;
        JCublas.cublasSetVector(size, VecCUDAid.ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    @Override
    public void set (int row, Ref1Di values) {
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

    @Override
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

    @Override
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

    @Override
    public String toString() {
        Compd[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(new Vecid(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
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
