package Matrix.Single;

import GPGPU.OpenCL.Buffer.ComplexfBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Imaginary.Comp;
import Imaginary.Compf;
import Matrix.Double.MatCLi;
import References.Single.Complex.Ref1Dif;
import References.Single.Complex.Ref2Dif;
import Vector.Double.VecCLi;
import Vector.Single.VecCLif;
import Vector.Single.Vecif;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCLif implements Ref2Dif {
    final VecCLif vector;
    final int rows, cols;

    public MatCLif(Context context, int rows, int cols) {
        this.vector = new VecCLif(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLif(Context context, Ref2Dif values) {
        this(context, values.getRows(), values.getCols());
        this.vector.set(values.rowMajor().toArray());
    }

    public MatCLif(Context context, Vecif... values) {
        this(context, new Matif(values));
    }

    public MatCLif(VecCLif vector, int cols) {
        this.vector = vector;
        this.rows = vector.getSize() / cols;
        this.cols = cols;
    }

    public MatCLif(Context context, Compf[][] values) {
        this(context, new Matif(values));
    }

    public MatCLif(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLif(Ref2Dif values) {
        this(Context.DEFAULT, values);
    }

    public MatCLif(Vecif... values) {
        this(Context.DEFAULT, values);
    }

    public MatCLif(Compf[][] values) {
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

    private void checkCompatibility (MatCLif b) {
        if (rows != b.rows | cols != b.cols | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A + B
     */
    public MatCLif add (float alpha, MatCLif b) {
        checkCompatibility(b);
        return new MatCLif(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLif add (MatCLif b) {
        return add(1, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLif subtr (float beta, MatCLif b) {
        return b.add(-beta, this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLif subtr (MatCLif b) {
        return subtr(1, b);
    }

    /**
     * Performs the matrix product C = alpha * A * B + beta * C
     */
    public MatCLif mul (Compf alpha, MatCLif b, Compf beta, MatCLif c) {
        if (cols != b.rows | c.rows != rows | c.cols != b.cols | !getContext().equals(b.getContext()) | !b.getContext().equals(c.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLif result = c.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, ComplexfBuffer.getFloats(alpha), getId(), 0, cols, b.getId(), 0, b.cols, ComplexfBuffer.getFloats(beta), result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLif mul (Compf alpha, MatCLif b) {
        if (cols != b.rows | !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }

        MatCLif result = new MatCLif(getContext(), rows, b.cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, ComplexfBuffer.getFloats(alpha), getId(), 0, cols, b.getId(), 0, b.cols, new float[2], result.getId(), 0, result.cols, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = alpha * A * B
     */
    public MatCLif mul (float alpha, MatCLif b) {
        return mul(new Compf(alpha, 0), b);
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLif mul (MatCLif b) {
        return mul(1, b);
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y
     */
    public VecCLif mul (Compf alpha, Compf beta, VecCLif x, VecCLif y) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLif result = y.clone();
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, ComplexfBuffer.getFloats(alpha), getId(), 0, cols, x.getId(), 0, 1, ComplexfBuffer.getFloats(beta), result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLif mul (Compf alpha, VecCLif x) {
        if (!getContext().equals(x.getContext()) || cols != x.size) {
            throw new IllegalArgumentException();
        }

        VecCLif result = new VecCLif(rows);
        cl_event event = new cl_event();
        CLBlast.CLBlastCgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, ComplexfBuffer.getFloats(alpha), getId(), 0, cols, x.getId(), 0, 1, new float[2], result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * A * x
     */
    public VecCLif mul (float alpha, VecCLif x) {
        return mul(new Compf(alpha, 0), x);
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLif mul (VecCLif x) {
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
    public Compf get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }

    @Override
    public void set(int row, int col, Compf val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, Compf... vals) {
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

    public void set (int row, VecCLif vals) {
        if (cols != vals.getSize()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * Sizeof.cl_float, 1, this.vector.getQueue().id, event);
        Query.awaitEvents(event);
    }

    @Override
    public MatCLi toDouble () {
        Compf[] values = vector.toArray();
        Comp[] casted = new Comp[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toDouble();
        }

        return new MatCLi(new VecCLi(getContext(), casted), cols);
    }

    public void release () {
        this.vector.release();
    }

    @Override
    public String toString() {
        Compf[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(new Vecif(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
    public MatCLif clone() {
        MatCLif matrix = new MatCLif(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }
}
