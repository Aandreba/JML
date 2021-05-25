package Matrix.Single;

import GPGPU.OpenCL.Context;
import Imaginary.Compf;
import Matrix.Double.Mati;
import References.Single.Complex.Ref2Dif;
import Vector.Single.Vecif;

public class Matif implements Ref2Dif {
    final protected Vecif[] values;

    public Matif(int rows, int cols) {
        this.values = new Vecif[rows];
        for (int i=0;i<rows;i++) {
            this.values[i] = new Vecif(cols);
        }
    }

    public Matif(Compf[][] values) {
        this.values = new Vecif[values.length];
        this.values[0] = new Vecif(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, new Vecif(values[i]));
        }
    }

    public Matif(Vecif... values) {
        this.values = new Vecif[values.length];
        this.values[0] = values[0];

        for (int i=1;i<values.length;i++) {
            set(i, values[i]);
        }
    }

    public interface MatifForEachIndex {
        Compf apply (int row, int col);
    }

    public interface MatifForEachVecif {
        Vecif apply (int row);
    }

    public int getRows () {
        return values.length;
    }

    public int getCols () {
        return values[0].getSize();
    }

    public Compf get (int row, int col) {
        return values[row].get(col);
    }

    public Vecif get (int row) {
        return values[row];
    }

    public void set (int row, int col, Compf value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecif value) {
        if (value.getSize() != getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public boolean isSquare () {
        return getRows() == getCols();
    }

    private int finalRows (Matif b) {
        return Math.min(getRows(), b.getRows());
    }

    private int finalCols (Matif b) {
        return Math.min(getCols(), b.getCols());
    }

    public Matif forEach (Matif b, Vecif.VecifForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Matif matrix = new Matif(rows, 0);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Matif forEach (Compf b, Vecif.VecifForEach forEach) {
        int rows = getRows();
        int cols = getCols();

        Matif matrix = new Matif(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public Matif forEach (float b, Vecif.VecifForEach forEach) {
        return forEach(new Compf(b, 0), forEach);
    }

    public static Matif forEach (int rows, int cols, MatifForEachVecif forEach) {
        Matif matrix = new Matif(rows, cols);

        for (int i=0;i<rows;i++) {
            Vecif Veciftor = forEach.apply(i);
            if (Veciftor.getSize() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, Veciftor);
        }

        return matrix;
    }

    public static Matif forEach (int rows, int cols, MatifForEachIndex forEach) {
        Matif matrix = new Matif(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Matif add (Matif b) {
        return forEach(b, Compf::add);
    }

    public Matif add (Vecif b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).add(b.get(j)));
    }

    public Matif add (Compf b) {
        return forEach(b, Compf::add);
    }

    public Matif add (float b) {
        return forEach(b, Compf::add);
    }

    public Matif subtr (Matif b) {
        return forEach(b, Compf::subtr);
    }

    public Matif subtr (Vecif b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).subtr(b.get(j)));
    }

    public Matif subtr (Compf b) {
        return forEach(b, Compf::subtr);
    }

    public Matif subtr (float b) {
        return forEach(b, Compf::subtr);
    }

    public Matif invSubtr (Vecif b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j).subtr(get(i, j)));
    }

    public Matif invSubtr (Compf b) {
        return forEach(getRows(), getCols(), (i,j) -> b.subtr(get(i, j)));
    }

    public Matif invSubtr (float b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).invSubtr(b));
    }

    public Matif mul (Matif b) {
        int rows = getRows();
        int cols = b.getCols();
        int dig = Math.min(getCols(), b.getRows());

        return forEach(rows, cols, (i, j) -> {
            Compf sum = new Compf();
            for (int k=0;k<dig;k++) {
                sum = sum.add(get(i, k).mul(b.get(k, j)));
            }

            return sum;
        });
    }

    public Vecif mul (Vecif b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Matif div (Matif b) {
        return mul(b.inverse());
    }

    public Matif scalMul (Matif b) {
        return forEach(b, Compf::mul);
    }

    public Matif scalMul (Vecif b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).mul(b.get(j)));
    }

    public Matif scalMul (Compf b) {
        return forEach(b, Compf::mul);
    }

    public Matif scalMul (float b) {
        return forEach(b, Compf::mul);
    }

    public Matif scalDiv (Matif b) {
        return forEach(b, Compf::div);
    }

    public Matif scalDiv (Vecif b) {
        return forEach(getRows(), getCols(), (i,j) -> get(i, j).div(b.get(j)));
    }

    public Matif scalDiv (Compf b) {
        return forEach(b, Compf::div);
    }

    public Matif scalDiv (float b) {
        return forEach(b, Compf::div);
    }

    public Matif scalInvDiv (Vecif b) {
        return forEach(getRows(), getCols(), (i,j) -> b.get(j).div(get(i,j)));
    }

    public Matif scalInvDiv (Compf b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Matif scalInvDiv (float b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Matif inverse() {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to invert non-square matrix");
        }

        return adj().scalMul(det().inverse());
    }

    public Matif cofactor () {
        int rows = getRows();
        if (rows != getCols()) {
            throw new ArithmeticException("Tried to calculate cofactor of non-square matrix");
        }

        if (rows == 2) {
            return new Matif(new Vecif(get(1, 1), get(0, 1).mul(-1)), new Vecif(get(1, 0).mul(-1), get(0, 0)));
        }

        int rowsm1 = rows - 1;
        Matif matrix = new Matif(rows, rows);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<rows;j++) {
                Matif det = new Matif(rowsm1, rowsm1);

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

    public Matif adj () {
        return cofactor().T();
    }

    public Compf det () {
        int k = getRows();
        if (k != getCols()) {
            return new Compf();
        } else if (k == 2) {
            return get(0, 0).mul(get(1, 1)).subtr(get(1, 0).mul(get(0, 1)));
        }

        int km1 = k - 1;
        Compf sum = new Compf();

        for (int q=0;q<k;q++) {
            Matif matrix = new Matif(km1, km1);

            for (int i=1;i<k;i++) {
                for (int j=0;j<km1;j++) {
                    int J = j < q ? j : j + 1;
                    matrix.set(i-1, j, get(i, J));
                }
            }

            Compf n = ((q & 0x1) == 1) ? get(0, q).mul(-1) : get(0, q);
            sum = sum.add(n.mul(matrix.det()));
        }

        return sum;
    }

    public Matif T () {
        int rows = getCols();
        int cols = getRows();

        return forEach(rows, cols, (i, j) -> get(j, i));
    }

    public Mati toDouble () {
        return Mati.forEach(getRows(), getCols(), (i, j) -> get(i, j).toDouble());
    }

    public MatCLif toCL(Context context) {
        return new MatCLif(context, this);
    }

    public MatCLif toCL() {
        return toCL(Context.DEFAULT);
    }

    public Vecif toVectorRow () {
        return Vecif.fromRef(rowMajor());
    }

    public Vecif toVectorCol () {
        return Vecif.fromRef(colMajor());
    }

    public static Matif identity (int k) {
        return forEach(k, k, (i, j) -> i == j ? new Compf(1,0) : new Compf());
    }

    public static Matif fromRef (Ref2Dif ref) {
        return ref instanceof Matif ? (Matif) ref : forEach(ref.getRows(), ref.getCols(), (MatifForEachIndex) ref::get);
    }

    @Override
    public Matif clone() {
        Matif matrix = new Matif(getRows(), getCols());
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
