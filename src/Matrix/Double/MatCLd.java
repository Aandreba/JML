package Matrix.Double;

import GPGPU.OpenCL.Buffer.DoubleBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Single.MatCL;
import References.Double.Ref1D;
import References.Double.Ref2D;
import Vector.Double.Vecd;
import Vector.Double.VecCLd;
import Vector.Single.VecCL;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

import java.util.Arrays;

public class MatCLd implements Ref2D {
    final VecCLd vector;
    final int rows, cols;

    public MatCLd(Context context, int rows, int cols) {
        this.vector = new VecCLd(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLd(Context context, Ref2D values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCLd(Context context, Vecd... values) {
        this(context, new Matd(values));
    }

    public MatCLd(VecCLd vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCLd(Context context, double[][] values) {
        this(context, new Matd(values));
    }

    public MatCLd(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLd(Ref2D values) {
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

    public CommandQueue getQueue () {
        return vector.getQueue();
    }

    public void setQueue (CommandQueue queue) {
        vector.setQueue(queue);
    }

    public Context getContext () {
        return vector.getContext();
    }

    public void setContext (Context context) {
        setQueue(new CommandQueue(context));
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
        CLBlast.CLBlastDgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, beta, result.getId(), 0, result.cols, getQueue().id, event);

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
        CLBlast.CLBlastDgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, 0, result.getId(), 0, result.cols, getQueue().id, event);

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
        CLBlast.CLBlastDgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, beta, result.getId(), 0, 1, getQueue().id, event);

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
        CLBlast.CLBlastDgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, 0, result.getId(), 0, 1, getQueue().id, event);

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
        return new MatCLd(toCPU().inverse());
    }

    /**
     * Calculates matrix determinat
     */
    public double det () {
        return toCPU().det();
    }

    /**
     * Calculates matrix adjugate
     */
    public MatCLd adj () {
        return new MatCLd(toCPU().adj());
    }

    /**
     * Calculates matrix determinat
     */
    public MatCLd cofactor () {
        return new MatCLd(toCPU().cofactor());
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public double get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    @Override
    public VecCLd get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCLd result = new VecCLd(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(cols, getId(), row * cols, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public void set(int row, int col, double val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, double... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Ref1D vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCLd vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * Sizeof.cl_double, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    public void release (boolean releaseQueue) {
        this.vector.release(releaseQueue);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCLd T (double alpha) {
        MatCLd result = new MatCLd(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastDomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, alpha, getId(), 0, rows, result.getId(), 0, cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public MatCLd T () {
        return T(1);
    }

    @Override
    public double[][] toArray() {
        double[] array = vector.toArray();
        double[][] result = new double[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }

    @Override
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
    public MatCLd clone() {
        MatCLd matrix = new MatCLd(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCLd identity (Context context, int k) {
        MatCLd matrix = new MatCLd(context, k, k);
        DoubleBuffer buffer = new DoubleBuffer(context, 1d);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(k, buffer.getId(), 0, 0, matrix.getId(), 0, k + 1, matrix.getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCLd identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
