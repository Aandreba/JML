package Matrix.Double;

import GPGPU.OpenCL.Buffer.ComplexBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Imaginary.Comp;
import Imaginary.Compf;
import Matrix.Single.MatCLif;
import References.Double.Complex.Ref1Di;
import References.Double.Complex.Ref2Di;
import Vector.Double.VecCLi;
import Vector.Double.Veci;
import Vector.Single.VecCLif;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCLi implements Ref2Di {
    final VecCLi vector;
    final int rows, cols;

    public MatCLi(Context context, int rows, int cols) {
        this.vector = new VecCLi(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLi(Context context, Ref2Di values) {
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

    public MatCLi(Ref2Di values) {
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
    public MatCLi add (double alpha, MatCLi b) {
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
    public MatCLi subtr (double beta, MatCLi b) {
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
        CLBlast.CLBlastZgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, ComplexBuffer.getDoubles(alpha), getId(), 0, cols, b.getId(), 0, b.cols, ComplexBuffer.getDoubles(beta), result.getId(), 0, result.cols, getQueue().id, event);

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
        CLBlast.CLBlastZgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, ComplexBuffer.getDoubles(alpha), getId(), 0, cols, b.getId(), 0, b.cols, new double[2], result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLi mul (double alpha, MatCLi b) {
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
    public VecCLi mul (Comp alpha, Comp beta, VecCLi x, VecCLi y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLi result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastZgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, ComplexBuffer.getDoubles(alpha), getId(), 0, cols, x.getId(), 0, 1, ComplexBuffer.getDoubles(beta), result.getId(), 0, 1, getQueue().id, event);

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
        CLBlast.CLBlastZgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, ComplexBuffer.getDoubles(alpha), getId(), 0, cols, x.getId(), 0, 1, new double[2], result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLi mul (double alpha, VecCLi x) {
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
    public void set(int row, int col, Comp val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, Comp... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Ref1Di vals) {
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
        CLBlast.CLBlastZcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * Sizeof.cl_float, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    @Override
    public MatCLif toFloat () {
        Comp[] values = vector.toArray();
        Compf[] casted = new Compf[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toFloat();
        }

        return new MatCLif(new VecCLif(getContext(), casted), cols);
    }

    public void release () {
        this.vector.release();
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
        CLBlast.CLBlastZcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }
}
