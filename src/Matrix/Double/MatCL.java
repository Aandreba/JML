package Matrix.Double;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Single.MatCLf;
import Matrix.Single.Matf;
import References.Double.Ref1D;
import References.Double.Ref2D;
import References.Single.Ref2Df;
import Vector.Double.Vec;
import Vector.Double.VecCL;
import Vector.Single.VecCLf;
import Vector.Single.Vecf;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCL implements Ref2D {
    final VecCL vector;
    final int rows, cols;

    public MatCL (Context context, int rows, int cols) {
        this.vector = new VecCL(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCL (Context context, Ref2D values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCL (Context context, Vec... values) {
        this(context, new Mat(values));
    }

    public MatCL (VecCL vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCL(Context context, double[][] values) {
        this(context, new Mat(values));
    }

    public MatCL(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCL(Ref2D values) {
        this(Context.DEFAULT, values);
    }

    public MatCL(Vec... values) {
        this(Context.DEFAULT, values);
    }

    public MatCL(double[][] values) {
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

    private void checkCompatibility (MatCL b) {
        if (rows != b.rows | cols != b.cols | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCL add (double alpha, MatCL b) {
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
    public MatCL subtr (double beta, MatCL b) {
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
    public MatCL mul (double alpha, MatCL b, double beta, MatCL c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCL result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, beta, result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCL mul (double alpha, MatCL b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCL result = new MatCL(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, 0, result.getId(), 0, result.cols, getQueue().id, event);

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
    public VecCL mul (double alpha, double beta, VecCL x, VecCL y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCL result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, beta, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCL mul (double alpha, VecCL x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCL result = new VecCL(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastDgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, 0, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCL mul (VecCL x) {
        return mul(1, x);
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

    public void set (int row, VecCL vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * Sizeof.cl_float, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCL T (double alpha) {
        MatCL result = new MatCL(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastDomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, alpha, getId(), 0, rows, result.getId(), 0, cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public MatCL T () {
        return T(1);
    }

    @Override
    public MatCLf toFloat() {
        double[] values = vector.toArray();
        float[] casted = new float[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = (float) values[i];
        }

        return new MatCLf(new VecCLf(getContext(), casted), cols);
    }

    public void release () {
        this.vector.release();
    }

    @Override
    public String toString() {
        double[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(new Vec(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
    public MatCL clone() {
        MatCL matrix = new MatCL(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }
}
