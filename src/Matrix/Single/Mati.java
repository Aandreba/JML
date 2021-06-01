package Matrix.Single;

import GPGPU.OpenCL.Context;
import Complex.Comp;
import Matrix.Double.Matid;
import References.Single.Complex.Ref2Dif;
import Vector.Single.Veci;

public class Mati implements Ref2Dif {
    final protected Veci[] values;

    public Mati(int rows, int cols) {
        this.values = new Veci[rows];
        for (int i=0;i<rows;i++) {
            this.values[i] = new Veci(cols);
        }
    }

    public Mati(Comp[][] values) {
        this.values = new Veci[values.length];
        this.values[0] = new Veci(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, new Veci(values[i]));
        }
    }

    public Mati(Veci... values) {
        this.values = new Veci[values.length];
        this.values[0] = values[0];

        for (int i=1;i<values.length;i++) {
            set(i, values[i]);
        }
    }

    public interface MatifForEachIndex {
        Comp apply (int row, int col);
    }

    public interface MatifForEachVecif {
        Veci apply (int row);
    }

    public int getRows () {
        return values.length;
    }

    public int getCols () {
        return values[0].getSize();
    }

    public Comp get (int row, int col) {
        return values[row].get(col);
    }

    public Veci get (int row) {
        return values[row];
    }

    public void set (int row, int col, Comp value) {
        values[row].set(col, value);
    }

    public void set (int row, Veci value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public boolean isSquare () {
        return getRows() == getCols();
    }

    private int finalRows (Mati b) {
        return Math.min(getRows(), b.getRows());
    }

    private int finalCols (Mati b) {
        return Math.min(getCols(), b.getCols());
    }

    public Mati forEach (Mati b, Veci.VecifForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Mati matrix = new Mati(rows, 0);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Mati forEach (Comp b, Veci.VecifForEach forEach) {
        int rows = getRows();
        int cols = getCols();

        Mati matrix = new Mati(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public Mati forEach (float b, Veci.VecifForEach forEach) {
        return forEach(new Comp(b, 0), forEach);
    }

    public static Mati forEach (int rows, int cols, MatifForEachVecif forEach) {
        Mati matrix = new Mati(rows, cols);

        for (int i=0;i<rows;i++) {
            Veci Veciftor = forEach.apply(i);
            if (Veciftor.getSize() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, Veciftor);
        }

        return matrix;
    }

    public static Mati forEach (int rows, int cols, MatifForEachIndex forEach) {
        Mati matrix = new Mati(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Mati add (Mati b) {
        return forEach(b, Comp::add);
    }

    public Mati add (Veci b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).add(b.get(j)));
    }

    public Mati add (Comp b) {
        return forEach(b, Comp::add);
    }

    public Mati add (float b) {
        return forEach(b, Comp::add);
    }

    public Mati subtr (Mati b) {
        return forEach(b, Comp::subtr);
    }

    public Mati subtr (Veci b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).subtr(b.get(j)));
    }

    public Mati subtr (Comp b) {
        return forEach(b, Comp::subtr);
    }

    public Mati subtr (float b) {
        return forEach(b, Comp::subtr);
    }

    public Mati invSubtr (Veci b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j).subtr(get(i, j)));
    }

    public Mati invSubtr (Comp b) {
        return forEach(getRows(), getCols(), (i,j) -> b.subtr(get(i, j)));
    }

    public Mati invSubtr (float b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).invSubtr(b));
    }

    public Mati mul (Mati b) {
        int rows = getRows();
        int cols = b.getCols();
        int dig = Math.min(getCols(), b.getRows());

        return forEach(rows, cols, (i, j) -> {
            Comp sum = new Comp();
            for (int k=0;k<dig;k++) {
                sum = sum.add(get(i, k).mul(b.get(k, j)));
            }

            return sum;
        });
    }

    public Veci mul (Veci b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Mati div (Mati b) {
        return mul(b.inverse());
    }

    public Mati scalMul (Mati b) {
        return forEach(b, Comp::mul);
    }

    public Mati scalMul (Veci b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).mul(b.get(j)));
    }

    public Mati scalMul (Comp b) {
        return forEach(b, Comp::mul);
    }

    public Mati scalMul (float b) {
        return forEach(b, Comp::mul);
    }

    public Mati scalDiv (Mati b) {
        return forEach(b, Comp::div);
    }

    public Mati scalDiv (Veci b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).div(b.get(j)));
    }

    public Mati scalDiv (Comp b) {
        return forEach(b, Comp::div);
    }

    public Mati scalDiv (float b) {
        return forEach(b, Comp::div);
    }

    public Mati scalInvDiv (Veci b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j).div(get(i,j)));
    }

    public Mati scalInvDiv (Comp b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Mati scalInvDiv (float b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Mati inverse() {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to invert non-square matrix");
        }

        return adj().scalMul(det().inverse());
    }

    public Mati cofactor () {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to calculate cofactor of non-square matrix");
        }

        if (rows == 2) {
            return new Mati(new Veci(get(1, 1), get(0, 1).mul(-1)), new Veci(get(1, 0).mul(-1), get(0, 0)));
        }

        int rowsm1 = rows - 1;
        Mati matrix = new Mati(rows, rows);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<rows;j++) {
                Mati det = new Mati(rowsm1, rowsm1);

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

    public Mati adj () {
        return cofactor().T();
    }

    public Comp det () {
        int k = getRows();
        if (k != getCols()) {
            throw new ArithmeticException("Tried to calculate determinant of non-square matrix");
        } else if (k == 2) {
            return get(0, 0).mul(get(1, 1)).subtr(get(1, 0).mul(get(0, 1)));
        }

        int km1 = k - 1;
        Comp sum = new Comp();

        for (int q=0;q<k;q++) {
            Mati matrix = new Mati(km1, km1);

            for (int i=1;i<k;i++) {
                for (int j=0;j<km1;j++) {
                    int J = j < q ? j : j + 1;
                    matrix.set(i-1, j, get(i, J));
                }
            }

            Comp n = ((q & 0x1) == 1) ? get(0, q).mul(-1) : get(0, q);
            sum = sum.add(n.mul(matrix.det()));
        }

        return sum;
    }

    public Mati T () {
        int rows = getCols();
        int cols = getRows();

        return forEach(rows, cols, (i, j) -> get(j, i));
    }

    public Matid toDouble () {
        return Matid.forEach(getRows(), getCols(), (i, j) -> get(i, j).toDouble());
    }

    public MatCLi toCL(Context context) {
        return new MatCLi(context, this);
    }

    public MatCLi toCL() {
        return toCL(Context.DEFAULT);
    }

    public MatCUDAi toCUDA() {
        return new MatCUDAi(this);
    }

    public Veci toVectorRow () {
        return Veci.fromRef(rowMajor());
    }

    public Veci toVectorCol () {
        return Veci.fromRef(colMajor());
    }

    public static Mati identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? new Comp(1,0) : new Comp());
    }

    public static Mati fromRef (Ref2Dif ref) {
        return ref instanceof Mati ? (Mati) ref : forEach(ref.getRows(), ref.getCols(), (MatifForEachIndex) ref::get);
    }

    @Override
    public Mati clone() {
        Mati matrix = new Mati(getRows(), getCols());
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
