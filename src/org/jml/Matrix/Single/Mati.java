package org.jml.Matrix.Single;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathf;
import org.jml.MT.TaskManager;
import org.jml.Mathx.Rand;
import org.jml.Matrix.Double.Matid;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.BiFunction;

public class Mati implements Serializable {
    final private static long serialVersionUID = -7364904562555322777L;
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

    public int rows() {
        return values.length;
    }

    public int cols() {
        return values[0].size();
    }

    public Comp get (int row, int col) {
        return values[row].get(col);
    }

    public Veci get (int row) {
        return values[row];
    }

    public Veci getCol (int col) {
        if (col < 0 || col >= cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Veci vec = new Veci(cols());
        for (int i = 0; i< rows(); i++) {
            vec.set(i, get(i, col));
        }

        return vec;
    }

    public void set (int row, int col, Comp value) {
        values[row].set(col, value);
    }

    public void set (int row, Veci value) {
        if (value.size() != cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public void setCol (int col, Veci value) {
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

    private int finalRows (Mati b) {
        return Math.min(rows(), b.rows());
    }

    private int finalCols (Mati b) {
        return Math.min(cols(), b.cols());
    }

    public Mati foreach(Mati b, Veci.VecifForEach forEach) {
        int rows = finalRows(b);
        int cols = finalCols(b);

        Mati matrix = new Mati(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b.get(i, j)));
            }
        }

        return matrix;
    }

    public Mati foreach(Comp b, Veci.VecifForEach forEach) {
        int rows = rows();
        int cols = cols();

        Mati matrix = new Mati(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public Mati foreach(float b, Veci.VecifForEach forEach) {
        return foreach(new Comp(b, 0), forEach);
    }

    public static Mati foreach(int rows, int cols, MatifForEachVecif forEach) {
        Mati matrix = new Mati(rows, cols);

        for (int i=0;i<rows;i++) {
            Veci Veciftor = forEach.apply(i);
            if (Veciftor.size() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, Veciftor);
        }

        return matrix;
    }

    public static Mati foreach(int rows, int cols, MatifForEachIndex forEach) {
        Mati matrix = new Mati(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Mati add (Mati b) {
        return foreach(b, Comp::add);
    }

    public Mati add (Veci b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).add(b.get(j)));
    }

    public Mati add (Comp b) {
        return foreach(b, Comp::add);
    }

    public Mati add (float b) {
        return foreach(b, Comp::add);
    }

    public Mati subtr (Mati b) {
        return foreach(b, Comp::subtr);
    }

    public Mati subtr (Veci b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).subtr(b.get(j)));
    }

    public Mati subtr (Comp b) {
        return foreach(b, Comp::subtr);
    }

    public Mati subtr (float b) {
        return foreach(b, Comp::subtr);
    }

    public Mati invSubtr (Veci b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j).subtr(get(i, j)));
    }

    public Mati invSubtr (Comp b) {
        return foreach(rows(), cols(), (i, j) -> b.subtr(get(i, j)));
    }

    public Mati invSubtr (float b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).invSubtr(b));
    }

    public Mati mul (Mati b) {
        int rows = rows();
        int cols = b.cols();
        int dig = Math.min(cols(), b.rows());

        if (rows * cols <= 7500) {
            return foreach(rows, cols, (i, j) -> {
                Comp sum = Comp.ZERO;
                for (int k=0;k<dig;k++) {
                    sum = sum.add(get(i, k).mul(b.get(k, j)));
                }

                return sum;
            });
        }

        TaskManager tasks = new TaskManager();
        Mati result = new Mati(rows, cols);

        for (int i=0;i<rows;i++) {
            int finalI = i;
            for (int j=0;j<cols;j++) {
                int finalJ = j;
                tasks.add(() -> {
                    Comp sum = Comp.ZERO;
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

    public Veci mul (Veci b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Mati div (Mati b) {
        return mul(b.inverse());
    }

    public Mati scalMul (Mati b) {
        return foreach(b, Comp::mul);
    }

    public Mati scalMul (Veci b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).mul(b.get(j)));
    }

    public Mati scalMul (Comp b) {
        return foreach(b, Comp::mul);
    }

    public Mati scalMul (float b) {
        return foreach(b, Comp::mul);
    }

    public Mati scalDiv (Mati b) {
        return foreach(b, Comp::div);
    }

    public Mati scalDiv (Veci b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j).div(b.get(j)));
    }

    public Mati scalDiv (Comp b) {
        return foreach(b, Comp::div);
    }

    public Mati scalDiv (float b) {
        return foreach(b, Comp::div);
    }

    public Mati scalInvDiv (Veci b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j).div(get(i,j)));
    }

    public Mati scalInvDiv (Comp b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    public Mati scalInvDiv (float b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    
    public Mati conj() {
        return Mati.foreach(rows(), cols(), (i, j) -> get(i,j).conj());
    }

    public Mati inverse () {
        int n = cols();
        int n2 = 2 * n;
        Mati matrix = new Mati(rows(), n2);

        for (int i = 0; i< rows(); i++) {
            Veci vector = new Veci(n2);
            for (int j=0;j<n;j++) {
                vector.set(j, get(i,j));
            }

            vector.set(n + i, Comp.ONE);
            matrix.set(i, vector);
        }

        Mati rref = matrix.rref();
        Mati result = new Mati(rows(), n);

        for (int i = 0; i< rows(); i++) {
            for (int j=0;j<n;j++) {
                result.set(i, j, rref.get(i,n + j));
            }
        }

        return result;
    }

    public Mati cofactor () {
        return adj().T();
    }

    public Mati adj () {
        return inverse().scalMul(det());
    }

    public Comp tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        Comp sum = Comp.ZERO;
        for (int i = 0; i< rows(); i++) {
            sum = sum.add(get(i,i));
        }

        return sum;
    }

    public Comp det () {
        int k = rows();
        if (k != cols()) {
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

    public Mati exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
        int k = 1;
        long factorial = 1;
        Mati pow = identity(n);

        Mati result = pow.clone();
        Mati last = null;

        while (!result.equals(last)) {
            pow = pow.mul(this);
            factorial *= k;

            last = result.clone();
            result = result.add(pow.scalDiv(factorial));
            k++;
        }

        return result;
    }

    public Mati rref () {
        Mati result = clone();
        for (int i = 0; i< rows(); i++) {
            if (result.get(i,i).abs() <= 1e-6f) {
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

    public Mati pow (int x) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (x < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        Mati result = identity(rows());
        for (int i=0;i<x;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Mati sqrt () {
        Mati y = this;
        Mati z = identity(rows());

        int n = 1;
        while (n <= 1000) {
            Mati zInverse = z.inverse();
            Mati yInverse = y.inverse();

            Mati newY = y.add(zInverse).scalDiv(2);
            if (newY.equals(y)) {
                return y;
            }

            Mati newZ = z.add(yInverse).scalDiv(2);
            y = newY;
            z = newZ;
            n++;
        }

        throw new ArithmeticException("No square root found");
    }

    public LUi lu () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate LU decomposition of no-square matrix");
        }

        return new LUi();
    }

    public QRi qr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate QR decomposition of no-square matrix");
        }

        return new QRi();
    }

    public Mati T () {
        int rows = cols();
        int cols = rows();

        return foreach(rows, cols, (i, j) -> get(j, i));
    }

    public Matid toDouble () {
        return Matid.foreach(rows(), cols(), (i, j) -> get(i, j).toDouble());
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

    public Veci rowMajor () {
        int n = cols();
        int m = rows();
        Veci array = new Veci(n * m);

        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                array.set((i * n) + j, get(i, j));
            }
        }

        return array;
    }

    public Veci colMajor () {
        int n = cols();
        int m = rows();
        Veci array = new Veci(n * m);

        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                array.set((j * n) + i, get(i, j));
            }
        }

        return array;
    }

    public static Mati identity (int k) {
        return foreach(k, k, (i, j) -> i == j ? new Comp(1,0) : new Comp());
    }

    public static Mati getTransform (Veci from, Veci to) {
        int n = from.size();
        if (n != to.size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Mati I = identity(n);
        Mati A = Rand.getMati(n, n);
        Mati last = null;

        while (!A.equals(last)) {
            Veci out = A.mul(from);

            last = A;
            A = A.add(I.scalMul(to.subtr(out)));
        }

        return A;
    }

    public Mati clone() {
        Mati matrix = new Mati(rows(), cols());
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
        Mati ref1Difs = (Mati) o;
        return Arrays.equals(values, ref1Difs.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public class LUi {
        final public Mati l, u;

        private LUi() {
            int n = rows();
            int nm1 = n - 1;
            this.l = identity(n);
            this.u = new Mati(n, n);

            for (int j=0;j<n;j++) {
                u.set(0, j, get(0, j));
                l.set(j, 0, get(j, 0).div(u.get(0, 0)));
            }

            for (int q=1;q<nm1;q++) {
                int k = q;
                int im1 = q - 1;

                for (int p=k+1;p<n;p++) {
                    int m = p;
                    Comp sumA = Mathf.sumi(0, im1, j -> l.get(k, j).mul(u.get(j, k)));
                    u.set(k, k, get(k, k).subtr(sumA));

                    Comp sumB = Mathf.sumi(0, im1, j -> l.get(k, j).mul(u.get(j, m)));
                    u.set(k, m, get(k, m).subtr(sumB));

                    Comp sumC = Mathf.sumi(0, im1, j -> l.get(m, j).mul(u.get(j, k)));
                    l.set(m, k, (get(m, k).subtr(sumC)).div(u.get(k, k)));
                }
            }

            Comp sum = Mathf.sumi(0, nm1, p -> l.get(nm1,p).mul(u.get(p,nm1)));
            u.set(nm1, nm1, get(nm1, nm1).subtr(sum));
        }

        
        public String toString() {
            return "LU {" +
                    "l=" + l +
                    ", u=" + u +
                    '}';
        }
    }

    public class QRi {
        final public Mati q, r;

        private QRi() {
            final BiFunction<Veci, Veci, Veci> projFunc = (u, a) -> u.mul(u.inner(a).div(u.inner(u)));

            Mati a = T();
            Veci[] u = new Veci[cols()];
            Veci[] e = new Veci[cols()];

            u[0] = a.get(0);
            e[0] = u[0].unit();

            for (int i = 1; i< cols(); i++) {
                Veci proj = new Veci(cols());
                for (int j=0;j<i;j++) {
                    proj = proj.add(projFunc.apply(u[j], a.get(i)));
                }

                u[i] = a.get(i).subtr(proj);
                e[i] = u[i].unit();
            }

            this.q = new Mati(e).T();
            this.r = Mati.foreach(rows(), cols(), (i, j) -> j >= i ? e[i].inner(a.get(j)) : Comp.ZERO);
        }

        
        public String toString() {
            return "QR {" +
                    "q=" + q +
                    ", r=" + r +
                    '}';
        }
    }
}
