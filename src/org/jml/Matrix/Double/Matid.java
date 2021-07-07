package org.jml.Matrix.Double;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.Complex.Double.Compd;
import org.jml.Mathx.Mathd;
import org.jml.MT.TaskManager;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Double.Vecid;

import java.util.Arrays;
import java.util.function.BiFunction;

public class Matid {
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

    public int rows() {
        return values.length;
    }

    public int cols() {
        return values[0].size();
    }

    public Compd get (int row, int col) {
        return values[row].get(col);
    }

    public Vecid get (int row) {
        return values[row];
    }

    public Vecid getCol (int col) {
        if (col < 0 || col >= cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Vecid vec = new Vecid(cols());
        for (int i = 0; i<vec.size(); i++) {
            vec.set(i, get(i, col));
        }

        return vec;
    }

    public void set (int row, int col, Compd value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecid value) {
        if (value.size() != cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public void setCol (int col, Vecid value) {
        if (value.size() != rows()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i = 0; i< rows(); i++) {
            set(i, col, value.get(i));
        }
    }

    public boolean isSquare () {
        return rows() == cols();
    }

    private int finalRows (Matid b) {
        return Math.min(rows(), b.rows());
    }

    private int finalCols (Matid b) {
        return Math.min(cols(), b.cols());
    }

    public Matid foreach(Matid b, Vecid.VeciForEach forEach) {
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

    public Matid foreach(Compd b, Vecid.VeciForEach forEach) {
        int rows = rows();
        int cols = cols();

        Matid matrix = new Matid(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public Matid foreach(double b, Vecid.VeciForEach forEach) {
        return foreach(new Compd(b, 0), forEach);
    }

    public static Matid foreach(int rows, int cols, MatiForEachVeci forEach) {
        Matid matrix = new Matid(rows, cols);

        for (int i=0;i<rows;i++) {
            Vecid Vecitor = forEach.apply(i);
            if (Vecitor.size() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, Vecitor);
        }

        return matrix;
    }

    public static Matid foreach(int rows, int cols, MatiForEachIndex forEach) {
        Matid matrix = new Matid(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Matid add (Matid b) {
        return foreach(b, Compd::add);
    }

    public Matid add (Vecid b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).add(b.get(j)));
    }

    public Matid add (Compd b) {
        return foreach(b, Compd::add);
    }

    public Matid add (double b) {
        return foreach(b, Compd::add);
    }

    public Matid subtr (Matid b) {
        return foreach(b, Compd::subtr);
    }

    public Matid subtr (Vecid b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).subtr(b.get(j)));
    }

    public Matid subtr (Compd b) {
        return foreach(b, Compd::subtr);
    }

    public Matid subtr (double b) {
        return foreach(b, Compd::subtr);
    }

    public Matid invSubtr (Vecid b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j).subtr(get(i, j)));
    }

    public Matid invSubtr (Compd b) {
        return foreach(rows(), cols(), (i, j) -> b.subtr(get(i, j)));
    }

    public Matid invSubtr (double b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).invSubtr(b));
    }

    public Matid mul (Matid b) {
        int rows = rows();
        int cols = b.cols();
        int dig = Math.min(cols(), b.rows());

        if (rows * cols <= 7500) {
            return foreach(rows, cols, (i, j) -> {
                Compd sum = Compd.ZERO;
                for (int k=0;k<dig;k++) {
                    sum = sum.add(get(i, k).mul(b.get(k, j)));
                }

                return sum;
            });
        }

        TaskManager tasks = new TaskManager();
        Matid result = new Matid(rows, cols);

        for (int i=0;i<rows;i++) {
            int finalI = i;
            for (int j=0;j<cols;j++) {
                int finalJ = j;
                tasks.add(() -> {
                    Compd sum = Compd.ZERO;
                    for (int k=0;k<dig;k++) {
                        sum = sum.add(get(finalI, k).mul(b.get(k, finalJ)));
                    }

                    result.set(finalI, finalJ, sum);
                });
            }
        }

        tasks.run();
        return result;
    }

    public Vecid mul (Vecid b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Matid div (Matid b) {
        return mul(b.inverse());
    }

    public Matid scalMul (Matid b) {
        return foreach(b, Compd::mul);
    }

    public Matid scalMul (Vecid b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).mul(b.get(j)));
    }

    public Matid scalMul (Compd b) {
        return foreach(b, Compd::mul);
    }

    public Matid scalMul (double b) {
        return foreach(b, Compd::mul);
    }

    public Matid scalDiv (Matid b) {
        return foreach(b, Compd::div);
    }

    public Matid scalDiv (Vecid b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).div(b.get(j)));
    }

    public Matid scalDiv (Compd b) {
        return foreach(b, Compd::div);
    }

    public Matid scalDiv (double b) {
        return foreach(b, Compd::div);
    }

    public Matid scalInvDiv (Vecid b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j).div(get(i,j)));
    }

    public Matid scalInvDiv (Compd b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    public Matid scalInvDiv (double b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    public Matid conj() {
        return Matid.foreach(rows(), cols(), (i, j) -> get(i,j).conj());
    }

    public Matid inverse () {
        int n = cols();
        int n2 = 2 * n;
        Matid matrix = new Matid(rows(), n2);

        for (int i = 0; i< rows(); i++) {
            Vecid vector = new Vecid(n2);
            for (int j=0;j<n;j++) {
                vector.set(j, get(i,j));
            }

            vector.set(n + i, Compd.ONE);
            matrix.set(i, vector);
        }

        Matid rref = matrix.rref();
        Matid result = new Matid(rows(), n);

        for (int i = 0; i< rows(); i++) {
            for (int j=0;j<n;j++) {
                result.set(i, j, rref.get(i,n + j));
            }
        }

        return result;
    }

    public Matid cofactor () {
        return adj().T();
    }

    public Matid adj () {
        return inverse().scalMul(det());
    }

    public Compd det () {
        int k = rows();
        if (k != cols()) {
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

        Matid result = identity(rows());
        for (int i=0;i<x;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Matid exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
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

    public Matid rref () {
        Matid result = clone();
        for (int i = 0; i< rows(); i++) {
            if (result.get(i,i).modulus() <= 1e-15) {
                continue;
            }

            result.set(i, result.get(i).div(result.get(i,i)));
            for (int j = 0; j< rows(); j++) {
                if (i == j) {
                    continue;
                }

                result.set(j, result.get(j).subtr(result.get(i).mul(result.get(j,i))));
            }
        }

        return result;
    }

    public LUid lu () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate LU decomposition of non-square matrix");
        }

        return new LUid();
    }

    public QRid qr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate QR decomposition of non-square matrix");
        }

        return new QRid();
    }

    public Matid T () {
        int rows = cols();
        int cols = rows();

        return foreach(rows, cols, (i, j) -> get(j, i));
    }

    public Mati toFloat () {
        return Mati.foreach(rows(), cols(), (i, j) -> get(i, j).toFloat());
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

    public static Matid identity (int k) {
        return foreach(k, k, (i, j) -> i == j ? Compd.ONE : Compd.ZERO);
    }

    public Compd[] rowMajor () {
        int n = cols();
        int m = rows();
        Compd[] array = new Compd[n * m];

        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                array[(i * n) + j] = get(i, j);
            }
        }

        return array;
    }

    public Compd[] colMajor () {
        int m = cols();
        int n = rows();
        Compd[] array = new Compd[m * n];

        for (int i=0;i<n;i++) {
            for (int j=0;j<m;j++) {
                array[(j * n) + i] = get(i, j);
            }
        }

        return array;
    }

    public Matid clone() {
        Matid matrix = new Matid(rows(), cols());
        for (int i = 0; i< rows(); i++) {
            matrix.set(i, get(i).clone());
        }

        return matrix;
    }

    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows(); i++) {
            builder.append(", ").append(get(i));
        }

        return "{ "+builder.substring(2)+" }";
    }

    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matid ref1Dis = (Matid) o;
        return Arrays.equals(values, ref1Dis.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public class LUid {
        final public Matid l, u;

        private LUid() {
            int n = rows();
            int nm1 = n - 1;
            this.l = identity(n);
            this.u = new Matid(n, n);

            for (int j=0;j<n;j++) {
                u.set(0, j, get(0, j));
                l.set(j, 0, get(j, 0).div(u.get(0, 0)));
            }

            for (int q=1;q<nm1;q++) {
                int k = q;
                int im1 = q - 1;

                for (int p=k+1;p<n;p++) {
                    int m = p;
                    Compd sumA = Mathd.summationi(0, im1, j -> l.get(k, j).mul(u.get(j, k)));
                    u.set(k, k, get(k, k).subtr(sumA));

                    Compd sumB = Mathd.summationi(0, im1, j -> l.get(k, j).mul(u.get(j, m)));
                    u.set(k, m, get(k, m).subtr(sumB));

                    Compd sumC = Mathd.summationi(0, im1, j -> l.get(m, j).mul(u.get(j, k)));
                    l.set(m, k, (get(m, k).subtr(sumC)).div(u.get(k, k)));
                }
            }

            Compd sum = Mathd.summationi(0, nm1, p -> l.get(nm1,p).mul(u.get(p,nm1)));
            u.set(nm1, nm1, get(nm1, nm1).subtr(sum));
        }


        public String toString() {
            return "LU {" +
                    "l=" + l +
                    ", u=" + u +
                    '}';
        }
    }

    public class QRid {
        final public Matid q, r;

        private QRid() {
            final BiFunction<Vecid, Vecid, Vecid> projFunc = (u, a) -> u.mul(u.inner(a).div(u.inner(u)));

            Matid a = T();
            Vecid[] u = new Vecid[cols()];
            Vecid[] e = new Vecid[cols()];

            u[0] = a.get(0);
            e[0] = u[0].unit();

            for (int i = 1; i< cols(); i++) {
                Vecid proj = new Vecid(cols());
                for (int j=0;j<i;j++) {
                    proj = proj.add(projFunc.apply(u[j], a.get(i)));
                }

                u[i] = a.get(i).subtr(proj);
                e[i] = u[i].unit();
            }

            this.q = new Matid(e).T();
            this.r = Matid.foreach(rows(), cols(), (i, j) -> j >= i ? e[i].inner(a.get(j)) : Compd.ZERO);
        }


        public String toString() {
            return "QR {" +
                    "q=" + q +
                    ", r=" + r +
                    '}';
        }
    }
}
