package Matrix.Double;

import GPGPU.OpenCL.Context;
import Complex.Compd;
import Matrix.Single.Mat;
import Matrix.Single.Mati;
import References.Double.Complex.Ref2Di;
import Vector.Double.Vecid;

import java.util.Arrays;

public class Matid implements Ref2Di {
    final protected Vecid[] values;

    public Matid(int rows, int cols) {
        this.values = new Vecid[rows];
        for (int i=0;i<rows;i++) {
            this.values[i] = new Vecid(cols);
        }
    }

    public Matid(Compd[][] values) {
        this.values = new Vecid[values.length];
        this.values[0] = new Vecid(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, new Vecid(values[i]));
        }
    }

    public Matid(Vecid... values) {
        this.values = new Vecid[values.length];
        this.values[0] = values[0];

        for (int i=1;i<values.length;i++) {
            set(i, values[i]);
        }
    }

    public interface MatiForEachIndex {
        Compd apply (int row, int col);
    }

    public interface MatiForEachVeci {
        Vecid apply (int row);
    }

    public int getRows () {
        return values.length;
    }

    public int getCols () {
        return values[0].getSize();
    }

    public Compd get (int row, int col) {
        return values[row].get(col);
    }

    public Vecid get (int row) {
        return values[row];
    }

    public void set (int row, int col, Compd value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecid value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public boolean isSquare () {
        return getRows() == getCols();
    }

    private int finalRows (Matid b) {
        return Math.min(getRows(), b.getRows());
    }

    private int finalCols (Matid b) {
        return Math.min(getCols(), b.getCols());
    }

    public Matid forEach (Matid b, Vecid.VeciForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Matid matrix = new Matid(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Matid forEach (Compd b, Vecid.VeciForEach forEach) {
        int rows = getRows();
        int cols = getCols();

        Matid matrix = new Matid(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public Matid forEach (double b, Vecid.VeciForEach forEach) {
        return forEach(new Compd(b, 0), forEach);
    }

    public static Matid forEach (int rows, int cols, MatiForEachVeci forEach) {
        Matid matrix = new Matid(rows, cols);

        for (int i=0;i<rows;i++) {
            Vecid Vecitor = forEach.apply(i);
            if (Vecitor.getSize() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, Vecitor);
        }

        return matrix;
    }

    public static Matid forEach (int rows, int cols, MatiForEachIndex forEach) {
        Matid matrix = new Matid(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Matid add (Matid b) {
        return forEach(b, Compd::add);
    }

    public Matid add (Vecid b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).add(b.get(j)));
    }

    public Matid add (Compd b) {
        return forEach(b, Compd::add);
    }

    public Matid add (double b) {
        return forEach(b, Compd::add);
    }

    public Matid subtr (Matid b) {
        return forEach(b, Compd::subtr);
    }

    public Matid subtr (Vecid b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).subtr(b.get(j)));
    }

    public Matid subtr (Compd b) {
        return forEach(b, Compd::subtr);
    }

    public Matid subtr (double b) {
        return forEach(b, Compd::subtr);
    }

    public Matid invSubtr (Vecid b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j).subtr(get(i, j)));
    }

    public Matid invSubtr (Compd b) {
        return forEach(getRows(), getCols(), (i,j) -> b.subtr(get(i, j)));
    }

    public Matid invSubtr (double b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).invSubtr(b));
    }

    public Matid mul (Matid b) {
        int rows = getRows();
        int cols = b.getCols();
        int dig = Math.min(getCols(), b.getRows());

        return forEach(rows, cols, (i, j) -> {
            Compd sum = new Compd();
            for (int k=0;k<dig;k++) {
                sum = sum.add(get(i, k).mul(b.get(k, j)));
            }

            return sum;
        });
    }

    public Vecid mul (Vecid b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Matid div (Matid b) {
        return mul(b.inverse());
    }

    public Matid scalMul (Matid b) {
        return forEach(b, Compd::mul);
    }

    public Matid scalMul (Vecid b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).mul(b.get(j)));
    }

    public Matid scalMul (Compd b) {
        return forEach(b, Compd::mul);
    }

    public Matid scalMul (double b) {
        return forEach(b, Compd::mul);
    }

    public Matid scalDiv (Matid b) {
        return forEach(b, Compd::div);
    }

    public Matid scalDiv (Vecid b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).div(b.get(j)));
    }

    public Matid scalDiv (Compd b) {
        return forEach(b, Compd::div);
    }

    public Matid scalDiv (double b) {
        return forEach(b, Compd::div);
    }

    public Matid scalInvDiv (Vecid b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j).div(get(i,j)));
    }

    public Matid scalInvDiv (Compd b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Matid scalInvDiv (double b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Matid inverse() {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to invert non-square matrix");
        }

        return adj().scalMul(det().inverse());
    }

    public Matid cofactor () {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to calculate cofactor of non-square matrix");
        }

        if (rows == 2) {
            return new Matid(new Vecid(get(1, 1), get(0, 1).mul(-1)), new Vecid(get(1, 0).mul(-1), get(0, 0)));
        }

        int rowsm1 = rows - 1;
        Matid matrix = new Matid(rows, rows);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<rows;j++) {
                Matid det = new Matid(rowsm1, rowsm1);

                for (int x=0;x<rowsm1;x++) {
                    int X = x < i ? x : x + 1;
                    for (int y=0;y<rowsm1;y++) {
                        int Y = y < j ? y : y + 1;
                        det.set(x, y, get(X, Y));
                    }
                }

                matrix.set(i, j, ((i+j) & 1) == 1 ? det.det().mul(-1) : det.det());
            }
        }

        return matrix;
    }

    public Matid adj () {
        return cofactor().T();
    }

    public Compd det () {
        int k = getRows();
        if (k != getCols()) {
            throw new ArithmeticException("Tried to calculate determinant of non-square matrix");
        } else if (k == 2) {
            return get(0, 0).mul(get(1, 1)).subtr(get(1, 0).mul(get(0, 1)));
        }

        int km1 = k - 1;
        Compd sum = new Compd();

        for (int q=0;q<k;q++) {
            Matid matrix = new Matid(km1, km1);

            for (int i=1;i<k;i++) {
                for (int j=0;j<km1;j++) {
                    int J = j < q ? j : j + 1;
                    matrix.set(i-1, j, get(i, J));
                }
            }

            Compd n = ((q & 0x1) == 1) ? get(0, q).mul(-1) : get(0, q);
            sum = sum.add(n.mul(matrix.det()));
        }

        return sum;
    }

    public Matid pow (int x) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (x < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        Matid result = identity(getRows());
        for (int i=0;i<x;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Matid exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = getRows();
        int k = 1;
        double factorial = 1;
        Matid pow = identity(n);

        Matid result = pow.clone();
        Matid last = null;

        while (!result.equals(last)) {
            pow = pow.mul(this);
            factorial *= k;

            last = result.clone();
            result = result.add(pow.scalDiv(factorial));
            k++;
        }

        return result;
    }

    public Matid T () {
        int rows = getCols();
        int cols = getRows();

        return forEach(rows, cols, (i, j) -> get(j, i));
    }

    public Mati toFloat () {
        return Mati.forEach(getRows(), getCols(), (i, j) -> get(i, j).toFloat());
    }

    public MatCUDAid toCUDA () {
        return new MatCUDAid(this);
    }

    public MatCLid toCL(Context context) {
        return new MatCLid(context, this);
    }

    public MatCLid toCL() {
        return toCL(Context.DEFAULT);
    }

    public Vecid toVectorRow () {
        return Vecid.fromRef(rowMajor());
    }

    public Vecid toVectorCol () {
        return Vecid.fromRef(colMajor());
    }

    public static Matid identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? new Compd(1,0) : new Compd());
    }

    public static Matid fromRef (Ref2Di ref) {
        return ref instanceof Matid ? (Matid) ref : forEach(ref.getRows(), ref.getCols(), (MatiForEachIndex) ref::get);
    }

    @Override
    public Matid clone() {
        Matid matrix = new Matid(getRows(), getCols());
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
        Matid ref1Dis = (Matid) o;
        return Arrays.equals(values, ref1Dis.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
