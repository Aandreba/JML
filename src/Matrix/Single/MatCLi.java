package Matrix.Single;

import GPGPU.OpenCL.Buffer.CompfBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Complex.Compd;
import Complex.Comp;
import Matrix.Double.MatCLid;
import References.Single.Complex.Ref1Dif;
import References.Single.Complex.Ref2Dif;
import Vector.Double.VecCLid;
import Vector.Single.VecCLi;
import Vector.Single.Veci;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCLi implements Ref2Dif {
    final VecCLi vector;
    final int rows, cols;

    public MatCLi(Context context, int rows, int cols) {
        this.vector = new VecCLi(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLi(Context context, Ref2Dif values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCLi(Context context, Veci... values) {
        this(context, new Mati(values));
    }

    public MatCLi(VecCLi vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCLi(Context context, Comp[][] values) {
        this(context, new Mati(values));
    }

    public MatCLi(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLi(Ref2Dif values) {
        this(Context.DEFAULT, values);
    }

    public MatCLi(Veci... values) {
        this(Context.DEFAULT, values);
    }

    public MatCLi(Comp[][] values) {
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

    private void checkCompatibility (MatCLi b) {
        if (rows != b.rows | cols != b.cols | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCLi add (float alpha, MatCLi b) {
        checkCompatibility(b);
        return new MatCLi(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLi add (MatCLi b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLi subtr (float beta, MatCLi b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLi subtr (MatCLi b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCLi mul (Comp alpha, MatCLi b, Comp beta, MatCLi c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLi result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompfBuffer.getFloats(alpha), getId(), 0, cols, b.getId(), 0, b.cols, CompfBuffer.getFloats(beta), result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLi mul (Comp alpha, MatCLi b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLi result = new MatCLi(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompfBuffer.getFloats(alpha), getId(), 0, cols, b.getId(), 0, b.cols, new float[2], result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLi mul (float alpha, MatCLi b) {
        return mul(new Comp(alpha, 0), b);
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLi mul (MatCLi b) {
        return mul(1, b);
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y
     */
    public VecCLi mul (Comp alpha, VecCLi x, Comp beta, VecCLi y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLi result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompfBuffer.getFloats(alpha), getId(), 0, cols, x.getId(), 0, 1, CompfBuffer.getFloats(beta), result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLi mul (Comp alpha, VecCLi x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLi result = new VecCLi(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompfBuffer.getFloats(alpha), getId(), 0, cols, x.getId(), 0, 1, new float[2], result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLi mul (float alpha, VecCLi x) {
        return mul(new Comp(alpha, 0), x);
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLi mul (VecCLi x) {
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
    public Comp get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    @Override
    public VecCLi get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCLi result = new VecCLi(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(cols, getId(), Sizeof.cl_float2 * row, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public void set(int row, int col, Comp val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, Comp... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Ref1Dif vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCLi vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * Sizeof.cl_float, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCLi T (Comp alpha) {
        MatCLi result = new MatCLi(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastComatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, CompfBuffer.getFloats(alpha), getId(), 0, rows, result.getId(), 0, cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    @Override
    public MatCLi T () {
        return T(new Comp(1, 0));
    }

    @Override
    public Comp[][] toArray() {
        Comp[] array = vector.toArray();
        Comp[][] result = new Comp[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }

    @Override
    public MatCLid toDouble () {
        Comp[] values = vector.toArray();
        Compd[] casted = new Compd[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toDouble();
        }

        return new MatCLid(new VecCLid(getContext(), casted), cols);
    }

    public void release (boolean releaseQueue) {
        this.vector.release(releaseQueue);
    }

    @Override
    public String toString() {
        Comp[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(new Veci(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
    public MatCLi clone() {
        MatCLi matrix = new MatCLi(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCLi identity (Context context, int k) {
        MatCLi matrix = new MatCLi(context, k, k);
        CompfBuffer buffer = new CompfBuffer(context, new Comp(1, 0));

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(k, buffer.getId(), 0, 0, matrix.getId(), 0, k + 1, matrix.getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public static MatCLi identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
