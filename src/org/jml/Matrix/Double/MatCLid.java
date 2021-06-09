package org.jml.Matrix.Double;

import org.jml.GPGPU.OpenCL.Buffer.CompdBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Matrix.Single.MatCLi;
import org.jml.References.Double.Complex.Ref1Did;
import org.jml.References.Double.Complex.Ref2Did;
import org.jml.Vector.Double.VecCLid;
import org.jml.Vector.Double.Vecid;
import org.jml.Vector.Single.VecCLi;
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

    public Context getContext () {
        return vector.getContext();
    }

    public void setContext (Context context) {
        vector.setContext(context);
    }

    private void checkCompatibility (MatCLid b) {
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
    public MatCLid add (Compd alpha, MatCLid b) {
        checkCompatibility(b);
        return new MatCLid(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLid add (MatCLid b) {
        return add(Compd.ONE, b);
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLid subtr (Compd beta, MatCLid b) {
        return b.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLid subtr (MatCLid b) {
        return subtr(Compd.ONE, b);
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
        CLBlast.CLBlastZgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompdBuffer.getDoubles(alpha), getId(), 0, cols, b.getId(), 0, b.cols, CompdBuffer.getDoubles(beta), result.getId(), 0, result.cols, getContext().queue, event);

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
        CLBlast.CLBlastZgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompdBuffer.getDoubles(alpha), getId(), 0, cols, b.getId(), 0, b.cols, new double[2], result.getId(), 0, result.cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLid mul (MatCLid b) {
        return mul(Compd.ONE, b);
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
        CLBlast.CLBlastZgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompdBuffer.getDoubles(alpha), getId(), 0, cols, x.getId(), 0, 1, CompdBuffer.getDoubles(beta), result.getId(), 0, 1, getContext().queue, event);

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
        CLBlast.CLBlastZgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompdBuffer.getDoubles(alpha), getId(), 0, cols, x.getId(), 0, 1, new double[2], result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLid mul (VecCLid x) {
        return mul(Compd.ONE, x);
    }

    /**
     * Performs the scalar operation y = x * y
     */
    public MatCLid scalMul (MatCLid y) {
        return new MatCLid(vector.mul(y.vector), cols);
    }

    /**
     * Performs the operation y = alpha * x
     */
    public MatCLid scalMul (Compd alpha) {
        return new MatCLid(vector.mul(alpha), cols);
    }

    /**
     * Performs the operation y = x / alpha
     */
    public MatCLid scalDiv (Compd alpha) {
        return scalMul(alpha.inverse());
    }

    /**
     * Calculates matrix reduced row echelon form
     */
    public MatCLid rref () {
        int cols = getCols();
        MatCLid result = clone();

        for (int i=0;i<rows;i++) {
            int offset = i * cols;
            CompdBuffer abs = new CompdBuffer(getContext(), 1);
            cl_event event = new cl_event();

            CLBlast.CLBlastDzasum(cols, abs.getId(), 0, result.getId(), offset, 1, getContext().queue, event);
            Query.awaitEvents(event);
            if (abs.get(0).equals(Compd.ZERO)) {
                continue;
            }

            VecCLid vector = result.get(i);
            VecCLid div = vector.div(result.get(i,i));
            result.set(i, div);

            div.release();
            vector.release();
            vector = result.get(i);

            for (int j=0;j<rows;j++) {
                if (i == j) {
                    continue;
                }

                VecCLid vector2 = result.get(j);
                VecCLid mul = vector.mul(result.get(j,i));
                VecCLid subtr = vector2.subtr(mul);
                mul.release();

                result.set(j, subtr);
                subtr.release();
                vector2.release();
            }

            vector.release();
        }

        return result;
    }

    public Compd tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        Context context = getContext();
        CompdBuffer buffer = new CompdBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDzsum(getRows(), buffer.getId(), 0, getId(), 0, getRows() + 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public MatCLid exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = getRows();
        int k = 1;
        double factorial = 1;
        MatCLid pow = identity(getContext(), n);

        MatCLid result = pow.clone();
        MatCLid last = null;

        while (!result.equals(last)) {
            MatCLid newPow = pow.mul(this);
            pow.release();
            pow = newPow;

            factorial *= k;
            last = result.clone();

            MatCLid add = pow.scalDiv(new Compd(factorial, 0));
            MatCLid newResult = result.add(add);

            result.release();
            add.release();

            result = newResult;
            k++;
        }

        return result;
    }

    public MatCLid pow (int b) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (b < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        MatCLid result = identity(getRows());
        for (int i=0;i<b;i++) {
            MatCLid newResult = result.mul(this);
            result.release();
            result = newResult;
        }

        return result;
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
        CLBlast.CLBlastZcopy(cols, getId(), row * cols, 1, result.getId(), 0, 1, getContext().queue, event);

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
        CLBlast.CLBlastZcopy(cols, vals.getId(), 0, 1, getId(), row * cols, 1, this.vector.getContext().queue, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCLid T (Compd alpha) {
        MatCLid result = new MatCLid(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastZomatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, rows, cols, CompdBuffer.getDoubles(alpha), getId(), 0, rows, result.getId(), 0, cols, getContext().queue, event);

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

    public void release () {
        this.vector.release();
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
        CLBlast.CLBlastZcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatCLid ref1DS = (MatCLid) o;
        MatCLid subtr = subtr(ref1DS);
        boolean result = subtr.vector.asum().equals(Compd.ZERO);

        subtr.release();
        return result;
    }

    public static MatCLid identity (Context context, int k) {
        return Matid.identity(k).toCL(context);
    }

    public static MatCLid identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
