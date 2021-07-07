package org.jml.Matrix.Double;

import org.jml.GPGPU.OpenCL.Buffer.DoubleBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Matrix.Single.MatCL;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.VecCLd;
import org.jml.Vector.Single.VecCL;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCLd {
    final VecCLd vector;
    final int rows, cols;

    public MatCLd(Context context, int rows, int cols) {
        this.vector = new VecCLd(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLd(Context context, Matd values) {
        this(context, values.rows(), values.cols());
        this.vector.set(values.rowMajor());
    }

    public MatCLd(Context context, Vecd... values) {
        this(context, new Matd(values));
    }

    public MatCLd(VecCLd vector, int cols) {
        this.vector = vector;
        this.rows = vector.size() / cols;
        this.cols = cols;
    }

    public MatCLd(Context context, double[]... values) {
        this(context, new Matd(values));
    }

    public MatCLd(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLd(Matd values) {
        this(Context.DEFAULT, values);
    }

    public MatCLd(Vecd... values) {
        this(Context.DEFAULT, values);
    }

    public MatCLd(double[][] values) {
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

    private void checkCompatibility (MatCLd b) {
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
    public MatCLd add (double alpha, MatCLd b) {
        checkCompatibility(b);
        return new MatCLd(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLd add (MatCLd b) {
        return add(1, b);
    }

    public MatCLd add (VecCLd b) {
        MatCLd result = new MatCLd(rows, cols);

        for (int i=0;i<rows;i++) {
            VecCLd row1 = get(i);
            VecCLd row2 = row1.add(b);

            result.set(i, row2);
            row1.release();
            row2.release();
        }

        return result;
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLd subtr (double beta, MatCLd b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLd subtr (MatCLd b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCLd mul (double alpha, MatCLd b, double beta, MatCLd c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLd result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, beta, result.getId(), 0, result.cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLd mul (double alpha, MatCLd b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLd result = new MatCLd(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, 0, result.getId(), 0, result.cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLd mul (MatCLd b) {
        return mul(1, b);
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y
     */
    public VecCLd mul (double alpha, VecCLd x, double beta, VecCLd y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLd result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, beta, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLd mul (double alpha, VecCLd x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLd result = new VecCLd(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, 0, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLd mul (VecCLd x) {
        return mul(1, x);
    }

    /**
     * Performs the operation y = A * inv(B)
     */
    public MatCLd div (MatCLd b) {
        return mul(b.inverse());
    }

    /**
     * Performs the operation y = alpha * x
     */
    public MatCLd scalMul (double alpha) {
        return new MatCLd(vector.mul(alpha), cols);
    }

    /**
     * Performs the operation y = x / alpha
     */
    public MatCLd scalDiv (double alpha) {
        return scalMul(1 / alpha);
    }

    /**
     * Returns matrix inverse
     */
    public MatCLd inverse () {
        Context context = getContext();
        int n = cols;
        int n2 = 2 * n;
        MatCLd matrix = new MatCLd(context, rows(), n2);

        for (int i=0;i<rows;i++) {
            int offset = i * n2;
            matrix.vector.set(n, vector, i * n, 1, offset, 1);
            matrix.vector.set(offset + n + i, 1);
        }

        MatCLd rref = matrix.rref();
        MatCLd result = new MatCLd(context, rows, n);
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
    public MatCLd rref () {
        int cols = cols();
        MatCLd result = clone();

        for (int i=0;i<rows;i++) {
            int offset = i * cols;
            DoubleBuffer abs = new DoubleBuffer(getContext(), 1);
            cl_event event = new cl_event();

            CLBlast.CLBlastDasum(cols, abs.getId(), 0, result.getId(), offset, 1, getContext().queue, event);
            Query.awaitEvents(event);
            if (abs.get(0) == 0) {
                continue;
            }

            VecCLd vector = result.get(i);
            VecCLd div = vector.div(result.get(i,i));
            result.set(i, div);

            div.release();
            vector.release();
            vector = result.get(i);

            for (int j = 0; j< rows(); j++) {
                if (i == j) {
                    continue;
                }

                VecCLd vector2 = result.get(j);
                VecCLd mul = vector.mul(result.get(j,i));
                VecCLd subtr = vector2.subtr(mul);
                mul.release();

                result.set(j, subtr);
                subtr.release();
                vector2.release();
            }

            vector.release();
        }

        return result;
    }

    public double tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        Context context = getContext();
        DoubleBuffer buffer = new DoubleBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDsum(rows(), buffer.getId(), 0, getId(), 0, rows() + 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public MatCLd exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
        int k = 1;
        double factorial = 1;
        MatCLd pow = identity(getContext(), n);

        MatCLd result = pow.clone();
        MatCLd last = null;

        while (!result.equals(last)) {
            MatCLd newPow = pow.mul(this);
            pow.release();
            pow = newPow;

            factorial *= k;
            last = result.clone();

            MatCLd add = pow.scalDiv(factorial);
            MatCLd newResult = result.add(add);

            result.release();
            add.release();

            result = newResult;
            k++;
        }

        return result;
    }

    public MatCLd pow (int b) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (b < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        MatCLd result = identity(rows());
        for (int i=0;i<b;i++) {
            MatCLd newResult = result.mul(this);
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

    public double get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    public VecCLd get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCLd result = new VecCLd(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(cols, getId(), row * cols, 1, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    
    public void set(int row, int col, double val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, double... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Vecd vals) {
        if (cols != vals.size()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCLd vals) {
        if (cols != vals.size()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * cols, 1, this.vector.getContext().queue, event);
        Query.awaitEvents(event);
    }

    public void release () {
        this.vector.release();
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCLd T (double alpha) {
        MatCLd result = new MatCLd(getContext(), cols, rows);

        cl_event event = new cl_event();
        CLBlast.CLBlastDomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, cols, rows, alpha, getId(), 0, rows, result.getId(), 0, cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    
    public MatCLd T () {
        return T(1);
    }

    
    public double[][] toArray() {
        double[] array = vector.toArray();
        double[][] result = new double[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }

    
    public MatCL toFloat() {
        double[] values = vector.toArray();
        float[] casted = new float[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = (float) values[i];
        }

        return new MatCL(new VecCL(getContext(), casted), cols);
    }

    public Matd toCPU () {
        return new Matd(toArray());
    }

    
    public String toString() {
        double[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(new Vecd(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    
    public MatCLd clone() {
        MatCLd matrix = new MatCLd(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }

    
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatCLd ref1DS = (MatCLd) o;
        MatCLd subtr = subtr(ref1DS);
        boolean result = subtr.vector.asum() == 0;

        subtr.release();
        return result;
    }

    public static MatCLd identity (Context context, int k) {
        return Matd.identity(k).toCL(context);
    }

    public static MatCLd identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
