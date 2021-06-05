package Matrix.Double;

import GPGPU.OpenCL.Buffer.CompBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Complex.Compd;
import Complex.Comp;
import Matrix.Single.MatCLi;
import References.Double.Complex.Ref1Did;
import References.Double.Complex.Ref2Did;
import Vector.Double.VecCLid;
import Vector.Double.Vecid;
import Vector.Single.VecCLi;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCLid implements Ref2Did {
    final VecCLid vector;
    final int rows, cols;

    public MatCLid(Context context, int rows, int cols) {
        this.vector = new VecCLid(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLid(Context context, Ref2Did values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCLid(Context context, Vecid... values) {
        this(context, new Matid(values));
    }

    public MatCLid(VecCLid vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCLid(Context context, Compd[][] values) {
        this(context, new Matid(values));
    }

    public MatCLid(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLid(Ref2Did values) {
        this(Context.DEFAULT, values);
    }

    public MatCLid(Vecid... values) {
        this(Context.DEFAULT, values);
    }

    public MatCLid(Compd[][] values) {
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

    private void checkCompatibility (MatCLid b) {
        if (rows != b.rows | cols != b.cols | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCLid add (double alpha, MatCLid b) {
        checkCompatibility(b);
        return new MatCLid(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLid add (MatCLid b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLid subtr (double beta, MatCLid b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLid subtr (MatCLid b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCLid mul (Compd alpha, MatCLid b, Compd beta, MatCLid c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLid result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastZgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompBuffer.getDoubles(alpha), getId(), 0, cols, b.getId(), 0, b.cols, CompBuffer.getDoubles(beta), result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLid mul (Compd alpha, MatCLid b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLid result = new MatCLid(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastZgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompBuffer.getDoubles(alpha), getId(), 0, cols, b.getId(), 0, b.cols, new double[2], result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLid mul (double alpha, MatCLid b) {
        return mul(new Compd(alpha, 0), b);
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLid mul (MatCLid b) {
        return mul(1, b);
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y
     */
    public VecCLid mul (Compd alpha, VecCLid x, Compd beta, VecCLid y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLid result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastZgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompBuffer.getDoubles(alpha), getId(), 0, cols, x.getId(), 0, 1, CompBuffer.getDoubles(beta), result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLid mul (Compd alpha, VecCLid x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLid result = new VecCLid(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastZgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompBuffer.getDoubles(alpha), getId(), 0, cols, x.getId(), 0, 1, new double[2], result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLid mul (double alpha, VecCLid x) {
        return mul(new Compd(alpha, 0), x);
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLid mul (VecCLid x) {
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
    public Compd get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    @Override
    public VecCLid get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCLid result = new VecCLid(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(cols, getId(), Sizeof.cl_double2 * row, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public void set(int row, int col, Compd val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, Compd... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Ref1Did vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCLid vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(cols, vals.getId(), 0, 1, getId(), row * Sizeof.cl_double2, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCLid T (Compd alpha) {
        MatCLid result = new MatCLid(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastZomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, CompBuffer.getDoubles(alpha), getId(), 0, rows, result.getId(), 0, cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public MatCLid T () {
        return T(new Compd(1, 0));
    }

    @Override
    public Compd[][] toArray() {
        Compd[] array = vector.toArray();
        Compd[][] result = new Compd[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }

    @Override
    public MatCLi toFloat () {
        Compd[] values = vector.toArray();
        Comp[] casted = new Comp[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toFloat();
        }

        return new MatCLi(new VecCLi(getContext(), casted), cols);
    }

    public void release (boolean releaseQueue) {
        this.vector.release(releaseQueue);
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
    public MatCLid clone() {
        MatCLid matrix = new MatCLid(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCLid identity (Context context, int k) {
        MatCLid matrix = new MatCLid(context, k, k);
        CompBuffer buffer = new CompBuffer(context, new Compd(1, 0));

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(k, buffer.getId(), 0, 0, matrix.getId(), 0, k + 1, matrix.getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCLid identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
