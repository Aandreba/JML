package Matrix.Single;

import GPGPU.OpenCL.Context;
import Matrix.Double.Matd;
import References.Single.Ref2Df;
import Vector.Single.Vec;

public class Mat implements Ref2Df {
    final protected Vec[] values;

    public Mat(int rows, int cols) {
        this.values = new Vec[rows];
        for (int i=0;i<rows;i++) {
            this.values[i] = new Vec(cols);
        }
    }

    public Mat(float[][] values) {
        this.values = new Vec[values.length];
        this.values[0] = new Vec(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, new Vec(values[i]));
        }
    }

    public Mat(Vec... values) {
        this.values = new Vec[values.length];
        this.values[0] = values[0];

        for (int i=1;i<values.length;i++) {
            set(i, values[i]);
        }
    }

    public interface MatfForEachIndex {
        float apply (int row, int col);
    }

    public interface MatfForEachVecf {
        Vec apply (int row);
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

    public Vec get (int row) {
        return values[row];
    }

    public void set (int row, int col, float value) {
        values[row].set(col, value);
    }

    public void set (int row, Vec value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public boolean isSquare () {
        return getRows() == getCols();
    }

    private int finalRows (Mat b) {
        return Math.min(getRows(), b.getRows());
    }

    private int finalCols (Mat b) {
        return Math.min(getCols(), b.getCols());
    }

    public Mat forEach (Mat b, Vec.VecfForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Mat matrix = new Mat(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Mat forEach (float b, Vec.VecfForEach forEach) {
        int rows = getRows();
        int cols = getCols();

        Mat matrix = new Mat(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public static Mat forEach (int rows, int cols, MatfForEachVecf forEach) {
        Mat matrix = new Mat(rows, cols);

        for (int i=0;i<rows;i++) {
            Vec vector = forEach.apply(i);
            if (vector.getSize() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, vector);
        }

        return matrix;
    }

    public static Mat forEach (int rows, int cols, MatfForEachIndex forEach) {
        Mat matrix = new Mat(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Mat add (Mat b) {
        return forEach(b, Float::sum);
    }

    public Mat add (Vec b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) + b.get(j));
    }

    public Mat add (float b) {
        return forEach(b, Float::sum);
    }

    public Mat subtr (Mat b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Mat subtr (Vec b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) - b.get(j));
    }

    public Mat subtr (float b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Mat invSubtr (Vec b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j) - get(i, j));
    }

    public Mat invSubtr (float b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Mat mul (Mat b) {
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

    public Vec mul (Vec b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Mat div (Mat b) {
        return mul(b.inverse());
    }

    public Mat scalMul (Mat b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Mat scalMul (Vec b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) * b.get(j));
    }

    public Mat scalMul (float b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Mat scalDiv (Mat b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Mat scalDiv (Vec b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j) / b.get(j));
    }

    public Mat scalDiv (float b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Mat scalInvDiv (Vec b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j) / get(i, j));
    }

    public Mat scalInvDiv (float b) {
        return forEach(b, (x, y) -> y / x);
    }

    public Mat inverse() {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to invert non-square matrix");
        }

        return adj().scalMul(1 / det());
    }

    public Mat cofactor () {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to calculate cofactor of non-square matrix");
        }

        if (rows == 2) {
            return new Mat(new Vec(get(1, 1), -get(0, 1)), new Vec(-get(1, 0), get(0, 0)));
        }

        int rowsm1 = rows - 1;
        Mat matrix = new Mat(rows, rows);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<rows;j++) {
                Mat det = new Mat(rowsm1, rowsm1);

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

    public Mat adj () {
        return cofactor().T();
    }

    public float det () {
        int k = getRows();
        if (k != getCols()) {
            throw new ArithmeticException("Tried to calculate determinant of non-square matrix");
        } else if (k == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }

        int km1 = k - 1;
        float sum = 0;

        for (int q=0;q<k;q++) {
            Mat matrix = new Mat(km1, km1);

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

    public Mat T () {
        int rows = getCols();
        int cols = getRows();

        return forEach(rows, cols, (i, j) -> get(j, i));
    }

    public Matd toDouble () {
        return Matd.forEach(getRows(), getCols(), (Matd.MatForEachIndex) this::get);
    }

    public MatCL toCL (Context context) {
        return new MatCL(context, this);
    }

    public MatCL toCL() {
        return toCL(Context.DEFAULT);
    }

    public MatCUDA toCUDA () {
        return new MatCUDA(this);
    }

    public Vec toVectorRow () {
        return Vec.fromRef(rowMajor());
    }

    public Vec toVectorCol () {
        return Vec.fromRef(colMajor());
    }

    public static Mat identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? 1 : 0);
    }

    public static Mat fromRef (Ref2Df ref) {
        return ref instanceof Mat ? (Mat) ref : forEach(ref.getRows(), ref.getCols(), (MatfForEachIndex) ref::get);
    }

    @Override
    public Mat clone() {
        Mat matrix = new Mat(getRows(), getCols());
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
