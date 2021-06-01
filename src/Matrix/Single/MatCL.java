package Matrix.Single;

import GPGPU.OpenCL.Buffer.DoubleBuffer;
import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Double.MatCLd;
import References.Single.Ref2Df;
import Vector.Double.VecCLd;
import Vector.Single.VecCL;
import Vector.Single.Vec;

import org.jocl.*;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;

public class MatCL implements Ref2Df {
    final VecCL vector;
    final int rows, cols;

    public MatCL(Context context, int rows, int cols) {
        this.vector = new VecCL(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCL(Context context, Ref2Df values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCL(Context context, Vec... values) {
        this(context, new Mat(values));
    }

    public MatCL(VecCL vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCL(Context context, float[][] values) {
        this(context, new Mat(values));
    }

    public MatCL(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCL(Ref2Df values) {
        this(Context.DEFAULT, values);
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
        CLBlast.CLBlastSgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, beta, result.getId(), 0, result.cols, getQueue().id, event);

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
        CLBlast.CLBlastSgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, alpha, getId(), 0, cols, b.getId(), 0, b.cols, 0, result.getId(), 0, result.cols, getQueue().id, event);

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
        CLBlast.CLBlastSgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, beta, result.getId(), 0, 1, getQueue().id, event);

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
        CLBlast.CLBlastSgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, alpha, getId(), 0, cols, x.getId(), 0, 1, 0, result.getId(), 0, 1, getQueue().id, event);

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
        return mul(b.inverse());
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
        return new MatCL(toCPU().inverse());
    }

    /**
     * Calculates matrix determinat
     */
    public float det () {
        return toCPU().det();
    }

    /**
     * Calculates matrix adjugate
     */
    public MatCL adj () {
        return new MatCL(toCPU().adj());
    }

    /**
     * Calculates matrix determinat
     */
    public MatCL cofactor () {
        return new MatCL(toCPU().cofactor());
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
    public VecCL get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCL result = new VecCL(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(cols, getId(), Sizeof.cl_float * row, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
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

    public void set (int row, Vec vals) {
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
        CLBlast.CLBlastScopy(cols, vals.getId(), 0, 1, this.getId(), row * Sizeof.cl_float, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCL T (float alpha) {
        MatCL result = new MatCL(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastSomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, alpha, getId(), 0, rows, result.getId(), 0, cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public MatCL T () {
        return T(1);
    }

    @Override
    public float[][] toArray() {
        float[] array = vector.toArray();
        float[][] result = new float[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }

    @Override
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

    public void release (boolean releaseQueue) {
        this.vector.release(releaseQueue);
    }

    @Override
    public String toString() {
        float[][] array = toArray();

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
        CLBlast.CLBlastScopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCL identity (Context context, int k) {
        MatCL matrix = new MatCL(context, k, k);
        FloatBuffer buffer = new FloatBuffer(context, 1f);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(k, buffer.getId(), 0, 0, matrix.getId(), 0, k + 1, matrix.getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCL identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
