package org.jml.Matrix.Double;

import org.jml.Complex.Double.Compd;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Mathx.Mathd;
import org.jml.Matrix.Single.Mat;
import org.jml.References.Double.Ref1Dd;
import org.jml.References.Double.Ref2Dd;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.Vecid;

import java.util.Arrays;

public class Matd implements Ref2Dd {
    final protected Vecd[] values;

    public Matd(int rows, int cols) {
        this.values = new Vecd[rows];
        for (int i=0;i<rows;i++) {
            this.values[i] = new Vecd(cols);
        }
    }

    public Matd(double[][] values) {
        this.values = new Vecd[values.length];
        this.values[0] = new Vecd(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, new Vecd(values[i]));
        }
    }

    public Matd(Ref1Dd... values) {
        this.values = new Vecd[values.length];
        this.values[0] = Vecd.fromRef(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, values[i]);
        }
    }

    public interface MatForEachIndex {
        double apply (int row, int col);
    }

    public interface MatForEachVec {
        Vecd apply (int row);
    }

    public int getRows () {
        return values.length;
    }

    public int getCols () {
        return values[0].getSize();
    }

    public double get (int row, int col) {
        return values[row].get(col);
    }

    public Vecd get (int row) {
        return values[row];
    }

    public Vecd getCol (int col) {
        if (col < 0 || col >= getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Vecd vec = new Vecd(getCols());
        for (int i=0;i<vec.getSize();i++) {
            vec.set(i, get(i, col));
        }

        return vec;
    }

    public void set (int row, int col, double value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecd value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public void setCol (int col, Vecd value) {
        if (value.getSize() != getRows()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<getRows();i++) {
            set(i, col, value.get(i));
        }
    }

    public boolean isSquare () {
        return getRows() == getCols();
    }

    private int finalRows (Matd b) {
        return Math.min(getRows(), b.getRows());
    }

    private int finalCols (Matd b) {
        return Math.min(getCols(), b.getCols());
    }

    public Matd forEach (Matd b, Vecd.VecForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Matd matrix = new Matd(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Matd forEach (double b, Vecd.VecForEach forEach) {
        int rows = getRows();
        int cols = getCols();

        Matd matrix = new Matd(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public static Matd forEach (int rows, int cols, MatForEachVec forEach) {
        Matd matrix = new Matd(rows, cols);

        for (int i=0;i<rows;i++) {
            Vecd vector = forEach.apply(i);
            if (vector.getSize() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, vector);
        }

        return matrix;
    }

    public static Matd forEach (int rows, int cols, MatForEachIndex forEach) {
        Matd matrix = new Matd(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Matd add (Matd b) {
        return forEach(b, Double::sum);
    }

    public Matd add (Vecd b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) + b.get(j));
    }

    public Matd add (double b) {
        return forEach(b, Double::sum);
    }

    public Matd subtr (Matd b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Matd subtr (Vecd b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) - b.get(j));
    }

    public Matd subtr (double b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Matd invSubtr (Vecd b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j) - get(i, j));
    }

    public Matd invSubtr (double b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Matd mul (Matd b) {
        int rows = getRows();
        int cols = b.getCols();
        int dig = Math.min(getCols(), b.getRows());

        return forEach(rows, cols, (i, j) -> {
            double sum = 0;
            for (int k=0;k<dig;k++) {
                sum += get(i, k) * b.get(k, j);
            }

            return sum;
        });
    }

    public Vecd mul (Vecd b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Matd div (Matd b) {
        return mul(b.inverse());
    }

    public Matd scalMul (Matd b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Matd scalMul (Vecd b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) * b.get(j));
    }

    public Matd scalMul (double b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Matd scalDiv (Matd b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Matd scalDiv (Vecd b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) / b.get(j));
    }

    public Matd scalDiv (double b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Matd scalInvDiv (Vecd b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j) / get(i, j));
    }

    public Matd scalInvDiv (double b) {
        return forEach(b, (x, y) -> y / x);
    }

    public Matd inverse() {
        int n = getCols();
        int n2 = 2 * n;
        Matd matrix = new Matd(getRows(), n2);

        for (int i=0;i<getRows();i++) {
            Vecd vector = new Vecd(n2);
            for (int j=0;j<n;j++) {
                vector.set(j, get(i,j));
            }

            vector.set(n + i, 1);
            matrix.set(i, vector);
        }

        Matd rref = matrix.rref();
        Matd result = new Matd(getRows(), n);

        for (int i=0;i<getRows();i++) {
            for (int j=0;j<n;j++) {
                result.set(i, j, rref.get(i,n + j));
            }
        }

        return result;
    }

    public Matd cofactor () {
        return adj().T();
    }

    public Matd adj () {
        return inverse().scalMul(det());
    }

    public Matd rref () {
        Matd result = clone();
        for (int i=0;i<getRows();i++) {
            if (result.get(i).equals(new Vecd(getCols()))) {
                continue;
            }

            result.set(i, result.get(i).div(result.get(i,i)));
            for (int j=0;j<getRows();j++) {
                if (i == j) {
                    continue;
                }

                result.set(j, result.get(j).subtr(result.get(i).mul(result.get(j,i))));
            }
        }

        return result;
    }

    public double tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        double sum = 0;
        for (int i=0;i<getRows();i++) {
            sum += get(i,i);
        }

        return sum;
    }

    public double det () {
        int k = getRows();
        if (k != getCols()) {
            throw new ArithmeticException("Tried to calculate determinant of non-square matrix");
        } else if (k == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }

        int km1 = k - 1;
        double sum = 0;

        for (int q=0;q<k;q++) {
            Matd matrix = new Matd(km1, km1);

            for (int i=1;i<k;i++) {
                for (int j=0;j<km1;j++) {
                    int J = j < q ? j : j + 1;
                    matrix.set(i-1, j, get(i, J));
                }
            }

            sum += (((q & 0x1) == 1) ? -get(0, q) : get(0, q)) * matrix.det();
        }

        return sum;
    }

    public Matd pow (int x) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (x < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        Matd result = identity(getRows());
        for (int i=0;i<x;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Eigend eigen () {
        return new Eigend();
    }

    public Matd exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = getRows();
        int k = 1;
        double factorial = 1;
        Matd pow = identity(n);

        Matd result = pow.clone();
        Matd last = null;

        while (!result.equals(last)) {
            pow = pow.mul(this);
            factorial *= k;

            last = result.clone();
            result = result.add(pow.scalDiv(factorial));
            k++;
        }

        return result;
    }

    public Matd T () {
        int rows = getCols();
        int cols = getRows();

        return forEach(rows, cols, (i, j) -> get(j, i));
    }

    public Mat toFloat () {
        return Mat.foreach(getRows(), getCols(), (i, j) -> (float) get(i, j));
    }

    @Override
    public Matid toComplex() {
        Vecid[] complex = new Vecid[getRows()];
        for (int i=0;i<getRows();i++) {
            complex[i] = get(i).toComplex();
        }

        return new Matid(complex);
    }

    public MatCLd toCL (Context context) {
        return new MatCLd(context, this);
    }

    public MatCLd toCL () {
        return toCL(Context.DEFAULT);
    }

    public Vecd toVectorRow () {
        return Vecd.fromRef(rowMajor());
    }

    public Vecd toVectorCol () {
        return Vecd.fromRef(colMajor());
    }

    public static Matd identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? 1 : 0);
    }

    public static Matd fromRef (Ref2Dd ref) {
        return ref instanceof Matd ? (Matd) ref : forEach(ref.getRows(), ref.getCols(), (MatForEachIndex) ref::get);
    }

    @Override
    public Matd clone() {
        Matd matrix = new Matd(getRows(), getCols());
        for (int i=0;i<getRows();i++) {
            matrix.set(i, get(i).clone());
        }

        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(get(i));
        }

        return "{ "+builder.substring(2)+" }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matd ref1DS = (Matd) o;
        return Arrays.equals(values, ref1DS.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public class Eigend {
        int n;
        Compd[] values;

        private Eigend () {
            this.n = getRows();
            this.values = calc();
        }

        @Override
        public String toString() {
            return "Eigen {" +
                    "values=" + Arrays.toString(values) +
                    '}';
        }

        private Compd[] calc () {
            if (n == 1) {
                return new Compd[]{ new Compd(get(0,0), 0) };
            } else if (n == 2) {
                return Mathd.quadratic(1, -tr(), det());
            } else if (n == 3) {
                double tr = tr();
                double det = det();

                return Mathd.cubic(-1, tr, (pow(2).tr() - tr * tr) / 2, det);
            }

            Compd[] vals = new Compd[2];
            Compd tr = new Compd(tr(), 0);
            Compd max = closeDownValue(tr);
            Compd min = closeDownValue(Compd.ZERO);

            vals[0] = max;
            vals[1] = min;
            return vals;
        }

        private Compd closeDownValue (Compd value) {
            Matid A = toComplex();
            Matid I = Matid.identity(n);

            double step = 1;
            double dirX = 1;
            double dirY = 1;
            double lastDet = 0;
            boolean flipDirOnReal = true;

            for (int i=0;i<1000;i++) {
                if (i > 0 && i % 100 == 0) {
                    step /= 9;
                }

                Compd det = A.subtr(I.scalMul(value)).det();
                double abs = det.modulus();

                if (i > 0 && abs > lastDet) {
                    if (flipDirOnReal) {
                        dirX *= -1;
                    } else {
                        dirY *= -1;
                    }

                    flipDirOnReal = !flipDirOnReal;
                }

                value = new Compd(value.real + dirX * step, value.imaginary + dirY * step);
                lastDet = abs;
            }

            return value;
        }
    }
}
