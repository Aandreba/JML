package Matrix.Single;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Double.MatCL;
import References.Single.Ref2Df;
import Vector.Double.VecCL;
import Vector.Single.VecCLf;
import Vector.Single.Vecf;

import org.jocl.*;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;

public class MatCLf implements Ref2Df {
    final VecCLf vector;
    final int rows, cols;

    public MatCLf(Context context, int rows, int cols) {
        this.vector = new VecCLf(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLf(Context context, Ref2Df values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCLf(Context context, Vecf... values) {
        this(context, new Matf(values));
    }

    public MatCLf(VecCLf vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCLf(Context context, float[][] values) {
        this(context, new Matf(values));
    }

    public MatCLf(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLf(Ref2Df values) {
        this(Context.DEFAULT, values);
    }

    public MatCLf(Vecf... values) {
        this(Context.DEFAULT, values);
    }

    public MatCLf(float[][] values) {
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

    private void checkCompatibility (MatCLf b) {
        if (rows != b.rows | cols != b.cols | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCLf add (float alpha, MatCLf b) {
        checkCompatibility(b);
        return new MatCLf(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLf add (MatCLf b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLf subtr (float beta, MatCLf b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLf subtr (MatCLf b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCLf mul (float alpha, MatCLf b, float beta, MatCLf c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLf result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, beta, result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLf mul (float alpha, MatCLf b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLf result = new MatCLf(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, 0, result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLf mul (MatCLf b) {
        return mul(1, b);
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y
     */
    public VecCLf mul (float alpha, float beta, VecCLf x, VecCLf y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLf result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, beta, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLf mul (float alpha, VecCLf x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLf result = new VecCLf(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastSgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, 0, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLf mul (VecCLf x) {
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
    public float get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    @Override
    public void set(int row, int col, float val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, float... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Vecf vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCLf vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * Sizeof.cl_float, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    @Override
    public MatCL toDouble() {
        float[] values = vector.toArray();
        double[] casted = new double[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i];
        }

        return new MatCL(new VecCL(getContext(), casted), cols);
    }

    public void release () {
        this.vector.release();
    }

    @Override
    public String toString() {
        float[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(new Vecf(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
    public MatCLf clone() {
        MatCLf matrix = new MatCLf(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }
}
