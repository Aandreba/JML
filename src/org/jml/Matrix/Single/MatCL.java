package org.jml.Matrix.Single;

import org.jml.GPGPU.OpenCL.Buffer.FloatBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Matrix.Double.MatCLd;
import org.jml.Vector.Double.VecCLd;
import org.jml.Vector.Single.VecCL;
import org.jml.Vector.Single.Vec;

import org.jocl.*;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;

public class MatCL {
    final VecCL vector;
    final int rows, cols;

    public MatCL(Context context, int rows, int cols) {
        this.vector = new VecCL(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCL(Context context, Mat values) {
        this(context, values.rows(), values.cols());
        this.vector.set(values.rowMajor());
    }

    public MatCL(Context context, Vec... values) {
        this(context, new Mat(values));
    }

    public MatCL(VecCL vector, int cols) {
        this.vector = vector;
        this.rows = vector.size() / cols;
        this.cols = cols;
    }

    public MatCL(Context context, float[]... values) {
        this(context, new Mat(values));
    }

    public MatCL(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCL(Mat values) {
        this(Context.DEFAULT, values.rowMajor());
    }

    public MatCL(Vec... values) {
        this(Context.DEFAULT, values);
    }

    public MatCL(float[][] values) {
        this(Context.DEFAULT, values);
    }

    public cl_mem getId () {
        return vector.getId();
    }

    public Context getContext () {
        return vector.getContext();
    }

    public void setContext (Context context) {
        vector.setContext(context);
    }

    private void checkCompatibility (MatCL b) {
        if (rows != b.rows | cols != b.cols | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isSquare () {
        return rows == cols;
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCL add (float alpha, MatCL b) {
        checkCompatibility(b);
        return new MatCL(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCL add (MatCL b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCL subtr (float beta, MatCL b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCL subtr (MatCL b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCL mul (float alpha, MatCL b, float beta, MatCL c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCL result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, beta, result.getId(), 0, result.cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCL mul (float alpha, MatCL b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCL result = new MatCL(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, 0, result.getId(), 0, result.cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCL mul (MatCL b) {
        return mul(1, b);
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y
     */
    public VecCL mul (float alpha, VecCL x, float beta, VecCL y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCL result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, beta, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCL mul (float alpha, VecCL x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCL result = new VecCL(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, 0, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = A * y
     */
    public VecCL mul (VecCL y) {
        return mul(1, y);
    }

    /**
     * Performs the operation y = A * inv(B)
     */
    public MatCL div (MatCL b) {
        MatCL matrix = b.inverse();
        MatCL result = mul(matrix);
        matrix.release();

        return result;
    }

    /**
     * Performs the scalar operation y = x * y
     */
    public MatCL scalMul (MatCL y) {
        return new MatCL(vector.mul(y.vector), cols);
    }

    /**
     * Performs the operation y = alpha * x
     */
    public MatCL scalMul (float alpha) {
        return new MatCL(vector.mul(alpha), cols);
    }

    /**
     * Performs the operation y = x / alpha
     */
    public MatCL scalDiv (float alpha) {
        return scalMul(1 / alpha);
    }

    /**
     * Returns matrix inverse
     */
    public MatCL inverse () {
        Context context = getContext();
        int n = cols;
        int n2 = 2 * n;
        MatCL matrix = new MatCL(context, rows(), n2);

        for (int i=0;i<rows;i++) {
            int offset = i * n2;
            matrix.vector.set(n, vector, i * n, 1, offset, 1);
            matrix.vector.set(offset + n + i, 1);
        }

        MatCL rref = matrix.rref();
        MatCL result = new MatCL(context, rows, n);
        matrix.release();

        for (int i=0;i<rows;i++) {
            result.vector.set(n, rref.vector, (i * n2) + n, 1, i * n, 1);
        }

        rref.release();
        return result;
    }

    /**
     * Calculates matrix reduced row echelon form
     */
    public MatCL rref () {
        int cols = cols();
        MatCL result = clone();

        for (int i=0;i<rows;i++) {
            int offset = i * cols;
            FloatBuffer abs = new FloatBuffer(getContext(), 1);
            cl_event event = new cl_event();

            CLBlast.CLBlastSasum(cols, abs.getId(), 0, result.getId(), offset, 1, getContext().queue, event);
            Query.awaitEvents(event);
            if (abs.get(0) == 0) {
                continue;
            }

            VecCL vector = result.get(i);
            VecCL div = vector.div(result.get(i,i));
            result.set(i, div);

            div.release();
            vector.release();
            vector = result.get(i);

            for (int j = 0; j< rows(); j++) {
                if (i == j) {
                    continue;
                }

                VecCL vector2 = result.get(j);
                VecCL mul = vector.mul(result.get(j,i));
                VecCL subtr = vector2.subtr(mul);
                mul.release();

                result.set(j, subtr);
                subtr.release();
                vector2.release();
            }

            vector.release();
        }

        return result;
    }

    public float tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        Context context = getContext();
        FloatBuffer buffer = new FloatBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastSsum(rows(), buffer.getId(), 0, getId(), 0, rows() + 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public MatCL exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
        int k = 1;
        long factorial = 1;
        MatCL pow = identity(getContext(), n);

        MatCL result = pow.clone();
        MatCL last = null;

        while (!result.equals(last)) {
            MatCL newPow = pow.mul(this);
            pow.release();
            pow = newPow;

            factorial *= k;
            last = result.clone();

            MatCL add = pow.scalDiv(factorial);
            MatCL newResult = result.add(add);

            result.release();
            add.release();

            result = newResult;
            k++;
        }

        return result;
    }

    public MatCL pow (int b) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (b < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        MatCL result = identity(rows());
        for (int i=0;i<b;i++) {
            MatCL newResult = result.mul(this);
            result.release();
            result = newResult;
        }

        return result;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public float get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    public VecCL get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCL result = new VecCL(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(cols, getId(), row * cols, 1, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }


    public void set(int row, int col, float val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, float... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Vec vals) {
        if (cols != vals.size()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCL vals) {
        if (cols != vals.size()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(cols, vals.getId(), 0, 1, this.getId(), row * cols, 1, this.vector.getContext().queue, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCL T (float alpha) {
        MatCL result = new MatCL(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastSomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, alpha, getId(), 0, rows, result.getId(), 0, cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }


    public MatCL T () {
        return T(1);
    }


    public float[][] toArray() {
        float[] array = vector.toArray();
        float[][] result = new float[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }


    public MatCLd toDouble() {
        float[] values = vector.toArray();
        double[] casted = new double[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i];
        }

        return new MatCLd(new VecCLd(getContext(), casted), cols);
    }

    public Mat toCPU () {
        return new Mat(toArray());
    }

    public void release () {
        this.vector.release();
    }


    public String toString() {
        float[][] array = toArray();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(new Vec(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }


    public MatCL clone() {
        MatCL matrix = new MatCL(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }


    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatCL ref1DS = (MatCL) o;
        MatCL subtr = subtr(ref1DS);
        boolean result = subtr.vector.asum() == 0;

        subtr.release();
        return result;
    }

    public static MatCL identity (Context context, int k) {
        return Mat.identity(k).toCL(context);
    }

    public static MatCL identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
