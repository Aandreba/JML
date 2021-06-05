package Matrix.Double;

import GPGPU.OpenCL.Context;
import References.Double.Ref2Dd;
import Vector.Double.Vecd;
import Vector.Double.Vecid;

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

    public Matd(Vecd... values) {
        this.values = new Vecd[values.length];
        this.values[0] = values[0];

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

    public void set (int row, int col, double value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecd value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
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
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to invert non-square matrix");
        }

        return adj().scalMul(1 / det());
    }

    public Matd cofactor () {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to calculate cofactor of non-square matrix");
        }

        if (rows == 2) {
            return new Matd(new Vecd(get(1, 1), -get(0, 1)), new Vecd(-get(1, 0), get(0, 0)));
        }

        int rowsm1 = rows - 1;
        Matd matrix = new Matd(rows, rows);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<rows;j++) {
                Matd det = new Matd(rowsm1, rowsm1);

                for (int x=0;x<rowsm1;x++) {
                    int X = x < i ? x : x + 1;
                    for (int y=0;y<rowsm1;y++) {
                        int Y = y < j ? y : y + 1;
                        det.set(x, y, get(X, Y));
                    }
                }

                matrix.set(i, j, ((i+j) & 1) == 1 ? -det.det() : det.det());
            }
        }

        return matrix;
    }

    public Matd adj () {
        return cofactor().T();
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

    public Matrix.Single.Mat toFloat () {
        return Matrix.Single.Mat.forEach(getRows(), getCols(), (i, j) -> (float) get(i, j));
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
}
