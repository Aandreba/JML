package Matrix;
import Vector.Vecf;

public class Matf {
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

    public int getRows () {
        return values.length;
    }

    public int getCols () {
        return values[0].getSize();
    }

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
        values[row] = value;
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

        Matf matrix = new Matf(rows, 0);
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

    public Matf add (float b) {
        return forEach(b, Float::sum);
    }

    public Matf subtr (Matf b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Matf subtr (float b) {
        return forEach(b, (x, y) -> x - y);
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

    public Matf scalMul (float b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Matf scalDiv (Matf b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Matf scalDiv (float b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Matf inverse () {
        return scalMul(1 / det());
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
        return Mat.forEach(getRows(), getCols(), this::get);
    }

    public static Matf identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? 1 : 0);
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
            builder.append(", ").append(get(i));
        }

        return "{ "+builder.substring(2)+" }";
    }
}
