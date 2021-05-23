package Matrix;

import References.Single.Ref2Df;
import Vector.Vec;
import Vector.Vecf;

public class Matf implements Ref2Df {
    final protected Vecf[] values;

    public Matf (int rows, int cols) {
        this.values = new Vecf[rows];
        for (int i=0;i<rows;i++) {
            this.values[i] = new Vecf(cols);
        }
    }

    public Matf (Vecf... values) {
        this.values = values;
    }

    public interface MatfForEachIndex {
        float apply (int row, int col);
    }

    public interface MatfForEachVecf {
        Vecf apply (int row);
    }

    public int getRows () {
        return values.length;
    }

    public int getCols () {
        return values[0].getSize();
    }

    @Override
    public float get (int row, int col) {
        return values[row].get(col);
    }

    public Vecf get (int row) {
        return values[row];
    }

    public void set (int row, int col, float value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecf value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public boolean isSquare () {
        return getRows() == getCols();
    }

    private int finalRows (Matf b) {
        return Math.min(getRows(), b.getRows());
    }

    private int finalCols (Matf b) {
        return Math.min(getCols(), b.getCols());
    }

    public Matf forEach (Matf b, Vecf.VecfForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Matf matrix = new Matf(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Matf forEach (float b, Vecf.VecfForEach forEach) {
        int rows = getRows();
        int cols = getCols();

        Matf matrix = new Matf(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public static Matf forEach (int rows, int cols, MatfForEachVecf forEach) {
        Matf matrix = new Matf(rows, cols);

        for (int i=0;i<rows;i++) {
            Vecf vector = forEach.apply(i);
            if (vector.getSize() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, vector);
        }

        return matrix;
    }

    public static Matf forEach (int rows, int cols, MatfForEachIndex forEach) {
        Matf matrix = new Matf(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Matf add (Matf b) {
        return forEach(b, Float::sum);
    }

    public Matf add (Vecf b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) + b.get(j));
    }

    public Matf add (float b) {
        return forEach(b, Float::sum);
    }

    public Matf subtr (Matf b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Matf subtr (Vecf b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) - b.get(j));
    }

    public Matf subtr (float b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Matf invSubtr (Vecf b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j) - get(i, j));
    }

    public Matf invSubtr (float b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Matf mul (Matf b) {
        int rows = getRows();
        int cols = b.getCols();
        int dig = Math.min(getCols(), b.getRows());

        return forEach(rows, cols, (i, j) -> {
            float sum = 0;
            for (int k=0;k<dig;k++) {
                sum += get(i, k) * b.get(k, j);
            }

            return sum;
        });
    }

    public Vecf mul (Vecf b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Matf div (Matf b) {
        return mul(b.inverse());
    }

    public Matf scalMul (Matf b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Matf scalMul (Vecf b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) * b.get(j));
    }

    public Matf scalMul (float b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Matf scalDiv (Matf b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Matf scalDiv (Vecf b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) / b.get(j));
    }

    public Matf scalDiv (float b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Matf scalInvDiv (Vecf b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j) / get(i, j));
    }

    public Matf scalInvDiv (float b) {
        return forEach(b, (x, y) -> y / x);
    }

    public Matf inverse() {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to invert non-square matrix");
        }

        return adj().scalMul(1 / det());
    }

    public Matf cofactor () {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to calculate cofactor of non-square matrix");
        }

        if (rows == 2) {
            return new Matf(new Vecf(get(1, 1), -get(0, 1)), new Vecf(-get(1, 0), get(0, 0)));
        }

        int rowsm1 = rows - 1;
        Matf matrix = new Matf(rows, rows);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<rows;j++) {
                Matf det = new Matf(rowsm1, rowsm1);

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

    public Matf adj () {
        return cofactor().T();
    }

    public float det () {
        int k = getRows();
        if (k != getCols()) {
            return 0;
        } else if (k == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }

        int km1 = k - 1;
        float sum = 0;

        for (int q=0;q<k;q++) {
            Matf matrix = new Matf(km1, km1);

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

    public Matf T () {
        int rows = getCols();
        int cols = getRows();

        return forEach(rows, cols, (i, j) -> get(j, i));
    }

    public Mat toDouble () {
        return Mat.forEach(getRows(), getCols(), (Mat.MatForEachIndex) this::get);
    }

    public Vecf toVectorRow () {
        return Vecf.fromRef(rowMajor());
    }

    public Vecf toVectorCol () {
        return Vecf.fromRef(colMajor());
    }

    public static Matf identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? 1 : 0);
    }

    public static Matf fromRef (Ref2Df ref) {
        return ref instanceof Matf ? (Matf) ref : forEach(ref.getRows(), ref.getCols(), (MatfForEachIndex) ref::get);
    }

    @Override
    public Matf clone() {
        Matf matrix = new Matf(getRows(), getCols());
        for (int i=0;i<getRows();i++) {
            matrix.set(i, get(i).clone());
        }

        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getRows();i++) {
            builder.append(", ").append(get(i).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }
}
