package org.jml.Matrix.Single;

import org.jml.GPGPU.OpenCL.Buffer.CompBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Matrix.Double.MatCLid;
import org.jml.Vector.Double.VecCLid;
import org.jml.Vector.Single.VecCL;
import org.jml.Vector.Single.VecCLi;
import org.jml.Vector.Single.Veci;
import org.jocl.blast.CLBlast;
import org.jocl.blast.CLBlastLayout;
import org.jocl.blast.CLBlastTranspose;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class MatCLi {
    final VecCLi vector;
    final int rows, cols;

    public MatCLi(Context context, int rows, int cols) {
        this.vector = new VecCLi(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCLi(Context context, Mati values) {
        this(context, values.rows(), values.cols());
        this.vector.set(values.rowMajor());
    }

    public MatCLi(Context context, Veci... values) {
        this(context, new Mati(values));
    }

    public MatCLi(VecCLi vector, int cols) {
        this.vector = vector;
        this.rows = vector.size() / cols;
        this.cols = cols;
    }

    public MatCLi(Context context, Comp[][] values) {
        this(context, new Mati(values));
    }

    public MatCLi(int rows, int cols) {
        this(Context.DEFAULT, rows, cols);
    }

    public MatCLi(Mati values) {
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

    public Context getContext () {
        return vector.getContext();
    }

    public void setContext (Context context) {
        vector.setContext(context);
    }

    private void checkCompatibility (MatCLi b) {
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
    public MatCLi add (Comp alpha, MatCLi b) {
        checkCompatibility(b);
        return new MatCLi(vector.add(alpha, b.vector), cols);
    }

    /**
     * Performs the operation y = A + B
     */
    public MatCLi add (MatCLi b) {
        return add(Comp.ONE, b);
    }

    public MatCLi add (VecCLi b) {
        MatCLi result = new MatCLi(rows, cols);

        for (int i=0;i<rows;i++) {
            VecCLi row1 = get(i);
            VecCLi row2 = row1.add(b);

            result.set(i, row2);
            row1.release();
        }

        return result;
    }

    /**
     * Performs the operation y = A - beta * B
     */
    public MatCLi subtr (Comp beta, MatCLi b) {
        return b.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation y = A - B
     */
    public MatCLi subtr (MatCLi b) {
        return subtr(Comp.ONE, b);
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
        CLBlast.CLBlastCgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompBuffer.getFloats(alpha), getId(), 0, cols, b.getId(), 0, b.cols, CompBuffer.getFloats(beta), result.getId(), 0, result.cols, getContext().queue, event);

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
        CLBlast.CLBlastCgemm(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, CLBlastTranspose.CLBlastTransposeNo, rows, b.cols, cols, CompBuffer.getFloats(alpha), getId(), 0, cols, b.getId(), 0, b.cols, new float[2], result.getId(), 0, result.cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the matrix product C = A * B
     */
    public MatCLi mul (MatCLi b) {
        return mul(Comp.ONE, b);
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
        CLBlast.CLBlastCgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompBuffer.getFloats(alpha), getId(), 0, cols, x.getId(), 0, 1, CompBuffer.getFloats(beta), result.getId(), 0, 1, getContext().queue, event);

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
        CLBlast.CLBlastCgemv(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeNo, rows, cols, CompBuffer.getFloats(alpha), getId(), 0, cols, x.getId(), 0, 1, new float[2], result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = A * x
     */
    public VecCLi mul (VecCLi x) {
        return mul(Comp.ONE, x);
    }

    /**
     * Performs the scalar operation y = x * y
     */
    public MatCLi scalMul (MatCLi y) {
        return new MatCLi(vector.mul(y.vector), cols);
    }

    /**
     * Performs the operation y = alpha * x
     */
    public MatCLi scalMul (Comp alpha) {
        return new MatCLi(vector.mul(alpha), cols);
    }

    /**
     * Performs the operation y = x / alpha
     */
    public MatCLi scalDiv (Comp alpha) {
        return scalMul(alpha.inverse());
    }

    /**
     * Returns matrix inverse
     */
    public MatCLi inverse () {
        Context context = getContext();
        int n = cols;
        int n2 = 2 * n;
        MatCLi matrix = new MatCLi(context, rows(), n2);

        for (int i=0;i<rows;i++) {
            int offset = i * n2;
            matrix.vector.set(n, vector, i * n, 1, offset, 1);
            matrix.vector.set(offset + n + i, Comp.ONE);
        }

        MatCLi rref = matrix.rref();
        MatCLi result = new MatCLi(context, rows, n);
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
    public MatCLi rref () {
        int cols = cols();
        MatCLi result = clone();

        for (int i=0;i<rows;i++) {
            int offset = i * cols;
            CompBuffer abs = new CompBuffer(getContext(), 1);
            cl_event event = new cl_event();

            CLBlast.CLBlastScasum(cols, abs.getId(), 0, result.getId(), offset, 1, getContext().queue, event);
            Query.awaitEvents(event);
            if (abs.get(0).equals(Comp.ZERO)) {
                continue;
            }

            VecCLi vector = result.get(i);
            VecCLi div = vector.div(result.get(i,i));
            result.set(i, div);

            div.release();
            vector.release();
            vector = result.get(i);

            for (int j=0;j<rows;j++) {
                if (i == j) {
                    continue;
                }

                VecCLi vector2 = result.get(j);
                VecCLi mul = vector.mul(result.get(j,i));
                VecCLi subtr = vector2.subtr(mul);
                mul.release();

                result.set(j, subtr);
                subtr.release();
                vector2.release();
            }

            vector.release();
        }

        return result;
    }

    public Comp tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        Context context = getContext();
        CompBuffer buffer = new CompBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastScsum(rows(), buffer.getId(), 0, getId(), 0, rows() + 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public MatCLi exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
        int k = 1;
        long factorial = 1;
        MatCLi pow = identity(getContext(), n);

        MatCLi result = pow.clone();
        MatCLi last = null;

        while (!result.equals(last)) {
            MatCLi newPow = pow.mul(this);
            pow.release();
            pow = newPow;

            factorial *= k;
            last = result.clone();

            MatCLi add = pow.scalDiv(new Comp(factorial, 0));
            MatCLi newResult = result.add(add);

            result.release();
            add.release();

            result = newResult;
            k++;
        }

        return result;
    }

    public MatCLi pow (int b) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (b < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        MatCLi result = identity(rows());
        for (int i=0;i<b;i++) {
            MatCLi newResult = result.mul(this);
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

    public Comp get (int row, int col) {
        return this.vector.get((row * cols) + col);
    }
    
    public VecCLi get (int row) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException();
        }

        VecCLi result = new VecCLi(getContext(), cols);
        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(cols, getId(), row * cols, 1, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    
    public void set(int row, int col, Comp val) {
        this.vector.set((row * cols) + col, val);
    }

    public void set (int row, Comp... vals) {
        if (cols != vals.length) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals);
    }

    public void set (int row, Veci vals) {
        if (cols != vals.size()) {
            throw new IllegalArgumentException();
        }

        this.vector.set(row * cols, vals.toArray());
    }

    public void set (int row, VecCLi vals) {
        if (cols != vals.size()) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(cols, vals.getId(), 0, 1, this.vector.getId(), row * cols, 1, this.vector.getContext().queue, event);
        Query.awaitEvents(event);
    }

    /**
     * Performs scaling and out-of-place transposition/copying of matrices according to B = alpha*op(A)
     */
    public MatCLi T (Comp alpha) {
        MatCLi result = new MatCLi(getContext(), cols, rows);

        cl_event event = new cl_event();
        CLBlast.CLBlastComatcopy(CLBlastLayout.CLBlastLayoutRowMajor, CLBlastTranspose.CLBlastTransposeYes, cols, rows, CompBuffer.getFloats(alpha), getId(), 0, rows, result.getId(), 0, cols, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    
    public MatCLi T () {
        return T(new Comp(1, 0));
    }

    
    public Comp[][] toArray() {
        Comp[] array = vector.toArray();
        Comp[][] result = new Comp[rows][cols];

        for (int i=0;i<rows;i++) {
            if (cols >= 0) System.arraycopy(array, (i * cols), result[i], 0, cols);
        }

        return result;
    }

    
    public MatCLid toDouble () {
        Comp[] values = vector.toArray();
        Compd[] casted = new Compd[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toDouble();
        }

        return new MatCLid(new VecCLid(getContext(), casted), cols);
    }

    public void release () {
        this.vector.release();
    }

    
    public String toString() {
        Comp[][] array = toArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(new Veci(array[i]).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }

    
    public MatCLi clone() {
        MatCLi matrix = new MatCLi(getContext(), rows, cols);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(rows * cols, getId(), 0, 1, matrix.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }

    
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatCLi ref1DS = (MatCLi) o;
        MatCLi subtr = subtr(ref1DS);
        boolean result = subtr.vector.asum().equals(Comp.ZERO);

        subtr.release();
        return result;
    }

    public static MatCLi identity (Context context, int k) {
        return Mati.identity(k).toCL(context);
    }

    public static MatCLi identity (int k) {
        return identity(Context.DEFAULT, k);
    }
}
