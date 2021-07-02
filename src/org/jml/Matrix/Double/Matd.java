package org.jml.Matrix.Double;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Mathx.Extra.Intx;
import org.jml.Mathx.Mathd;
import org.jml.Mathx.Rand;
import org.jml.Mathx.TaskManager;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.Vecid;
import org.jml.Vector.Single.Veci;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Matd {
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
        this.values[0] = Vecd.foreach(values[0].size(), i -> values[0].get(i));

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

    public int rows() {
        return values.length;
    }

    public int cols() {
        return values[0].size();
    }

    public double get (int row, int col) {
        return values[row].get(col);
    }

    public Vecd get (int row) {
        return values[row];
    }

    public Vecd getCol (int col) {
        if (col < 0 || col >= cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Vecd vec = new Vecd(cols());
        for (int i = 0; i<vec.size(); i++) {
            vec.set(i, get(i, col));
        }

        return vec;
    }

    public void set (int row, int col, double value) {
        values[row].set(col, value);
    }

    public void set (int row, Vecd value) {
        if (value.size() != cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public void setCol (int col, Vecd value) {
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

    private int finalRows (Matd b) {
        return Math.min(rows(), b.rows());
    }

    private int finalCols (Matd b) {
        return Math.min(cols(), b.cols());
    }

    public Matd foreachValue (Function<Double, Double> valueFunction) {
        int rows = rows();
        int cols = cols();

        Matd matrix = new Matd(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, valueFunction.apply(get(i,j)));
            }
        }

        return matrix;
    }

    public Matd foreachVector (Function<Vecd, Vecd> vectorFunction) {
        int rows = rows();
        int cols = cols();

        Matd matrix = new Matd(rows, cols);
        for (int i=0;i<rows;i++) {
            matrix.set(i, vectorFunction.apply(get(i)));
        }

        return matrix;
    }

    public Matd foreach(Matd b, Vecd.VecForEach forEach) {
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

    public Matd foreach(double b, Vecd.VecForEach forEach) {
        int rows = rows();
        int cols = cols();

        Matd matrix = new Matd(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public static Matd foreach(int rows, int cols, MatForEachVec forEach) {
        Matd matrix = new Matd(rows, cols);

        for (int i=0;i<rows;i++) {
            Vecd vector = forEach.apply(i);
            if (vector.size() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, vector);
        }

        return matrix;
    }

    public static Matd foreach(int rows, int cols, MatForEachIndex forEach) {
        Matd matrix = new Matd(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Matd add (Matd b) {
        return foreach(b, Double::sum);
    }

    public Matd add (Vecd b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) + b.get(j));
    }

    public Matd add (double b) {
        return foreach(b, Double::sum);
    }

    public Matd subtr (Matd b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Matd subtr (Vecd b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) - b.get(j));
    }

    public Matd subtr (double b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Matd invSubtr (Vecd b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j) - get(i, j));
    }

    public Matd invSubtr (double b) {
        return foreach(b, (x, y) -> y - x);
    }

    public Matd mul (Matd b) {
        int rows = rows();
        int cols = b.cols();
        int dig = Math.min(cols(), b.rows());

        if (rows * cols <= 15000) {
            return foreach(rows, cols, (i, j) -> {
                double sum = 0;
                for (int k=0;k<dig;k++) {
                    sum += get(i, k) * b.get(k, j);
                }

                return sum;
            });
        }

        TaskManager tasks = new TaskManager();
        Matd result = new Matd(rows, cols);

        for (int i=0;i<rows;i++) {
            int finalI = i;
            for (int j=0;j<cols;j++) {
                int finalJ = j;
                tasks.add(() -> {
                    double sum = 0;
                    for (int k=0;k<dig;k++) {
                        sum += get(finalI, k) * b.get(k, finalJ);
                    }

                    result.set(finalI, finalJ, sum);
                });
            }
        }

        tasks.run();
        return result;
    }

    public Vecd mul (Vecd b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Matd div (Matd b) {
        return mul(b.inverse());
    }

    public Matd scalMul (Matd b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Matd scalMul (Vecd b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) * b.get(j));
    }

    public Matd scalMul (double b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Matd scalDiv (Matd b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Matd scalDiv (Vecd b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) / b.get(j));
    }

    public Matd scalDiv (double b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Matd scalInvDiv (Vecd b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j) / get(i, j));
    }

    public Matd scalInvDiv (double b) {
        return foreach(b, (x, y) -> y / x);
    }

    public Matd inverse() {
        int n = cols();
        int n2 = 2 * n;
        Matd matrix = new Matd(rows(), n2);

        for (int i = 0; i< rows(); i++) {
            Vecd vector = new Vecd(n2);
            for (int j=0;j<n;j++) {
                vector.set(j, get(i,j));
            }

            vector.set(n + i, 1);
            matrix.set(i, vector);
        }

        Matd rref = matrix.rref();
        Matd result = new Matd(rows(), n);

        for (int i = 0; i< rows(); i++) {
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
        for (int i = 0; i< rows(); i++) {
            if (result.get(i).equals(new Vecd(cols()))) {
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

    public double tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        double sum = 0;
        for (int i = 0; i< rows(); i++) {
            sum += get(i,i);
        }

        return sum;
    }

    public double det () {
        int k = rows();
        if (k != cols()) {
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

    public double[] fadlev () {
        int n = rows();
        double[] poly = new double[n+1];
        poly[0] = 1;

        Matd y = Matd.this;
        for (int i=1;i<=n;i++) {
            poly[i] = -(1f/i) * y.tr();
            y = mul(y).add(scalMul(poly[i]));
        }

        return poly;
    }

    /**
     * Calculates largest eigenvalue
     * @see <a href="https://en.wikipedia.org/wiki/Power_iteration">Power iteration</a>
     */
    public Compd eigval () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvalue of non-square matrix");
        }

        Matid A = toComplex();
        Vecid b = Rand.getVecid(rows(), new Compd(Rand.getDouble(0.1f, 1), Rand.getDouble(0.1f, 1)), Compd.ONE);
        Vecid last = null;

        while (!b.equals(last)) {
            last = b;
            b = A.mul(b).unit();
        }

        Vecid eigen = A.mul(b).div(b);
        return eigen.mean();
    }

    public Vecid eigvals () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvalues of non-square matrix");
        }

        return Mathd.poly(fadlev());
    }

    /**
     * Calculates eigenvector of specific eigenvalue
     * @see <a href="https://en.wikipedia.org/wiki/Inverse_iteration">Inverse iteration</a>
     */
    public Vecid eigvec (Compd value) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvector of non-square matrix");
        }

        int n = rows();
        int nm1 = n - 1;

        Matid M = toComplex();
        Matid A = M.subtr(Matid.identity(n).scalMul(value));
        Matid B = A.inverse();

        Vecid b = Rand.getVecid(n).mul(value.inverse());
        Vecid last = null;

        while (last == null || b.abs().subtr(last.abs()).abs().max() > 1e-15f) {
            last = b;
            b = B.mul(b).unit();
        }

        Vecid vec = b.div(b.get(nm1));
        if (vec.mul(value).subtr(M.mul(vec)).abs().max() <= 1e-6f) {
            return vec;
        }

        Matid C = A.rref();
        return Vecid.foreach(n, i -> i < nm1 ? C.get(i, nm1).mul(-1) : Compd.ONE);
    }

    public Vecid eigvec (int pos) {
        return eigvec(eigvals().get(pos));
    }

    public Matid eigvecs (Vecid values) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvectors of non-square matrix");
        }

        return Matid.foreach(rows(), rows(), i -> eigvec(values.get(i)));
    }

    public Matid eigvecs () {
        return eigvecs(eigvals());
    }

    public Matd pow (int x) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (x < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        Matd result = identity(rows());
        for (int i=0;i<x;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Matd sqrt () {
        Matd y = this;
        Matd z = identity(rows());

        int n = 1;
        while (n <= 1000) {
           Matd zInverse = z.inverse();
           Matd yInverse = y.inverse();

            Matd newY = y.add(zInverse).scalDiv(2);
            if (newY.equals(y)) {
                return y;
            }

            Matd newZ = z.add(yInverse).scalDiv(2);
            y = newY;
            z = newZ;
            n++;
        }

        throw new ArithmeticException("No square root found");
    }

    public Matd exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
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

    public Matd log1p() {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate logarithm of non-square matrix");
        }

        Matd y = this;
        Matd last = null;
        Matd pow = this;

        int n = 2;
        while (!y.equals(last)) {
            pow = pow.mul(this);
            Matd x = pow.scalDiv(n);

            last = y;
            if (Intx.isOdd(n)) {
                y = y.add(x);
            } else {
                y = y.subtr(x);
            }

            n++;
        }

        return y;
    }

    public Matd log () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate logarithm of non-square matrix");
        }

        return subtr(identity(rows())).log1p();
    }

    public LUd lu () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate LU decomposition of non-square matrix");
        }

        return new LUd();
    }

    public QRd qr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate QR decomposition of non-square matrix");
        }

        return new QRd();
    }

    public Matd T () {
        int rows = cols();
        int cols = rows();

        return foreach(rows, cols, (i, j) -> get(j, i));
    }

    public Mat toFloat () {
        return Mat.foreach(rows(), cols(), (i, j) -> (float) get(i, j));
    }
    
    public Matid toComplex() {
        Vecid[] complex = new Vecid[rows()];
        for (int i = 0; i< rows(); i++) {
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

    public MatCUDAd toCUDA () { return new MatCUDAd(this); }

    public static Matd identity (int k) {
        return foreach(k, k, (i, j) -> i == j ? 1 : 0);
    }

    public double[] rowMajor () {
        int n = cols();
        int m = rows();
        double[] array = new double[n * m];

        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                array[(i * n) + j] = get(i, j);
            }
        }

        return array;
    }

    public double[] colMajor () {
        int m = cols();
        int n = rows();
        double[] array = new double[m * n];

        for (int i=0;i<n;i++) {
            for (int j=0;j<m;j++) {
                array[(j * n) + i] = get(i, j);
            }
        }

        return array;
    }
    
    public Matd clone() {
        Matd matrix = new Matd(rows(), cols());
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
        Matd ref1DS = (Matd) o;
        return Arrays.equals(values, ref1DS.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public class LUd {
        final public Matd l, u;

        private LUd () {
            int n = rows();
            int nm1 = n - 1;
            this.l = identity(n);
            this.u = new Matd(n, n);

            for (int j=0;j<n;j++) {
                u.set(0, j, get(0, j));
                l.set(j, 0, get(j, 0) / u.get(0, 0));
            }

            for (int q=1;q<nm1;q++) {
                int k = q;
                int im1 = q - 1;

                for (int p=k+1;p<n;p++) {
                    int m = p;
                    double sumA = Mathd.summation(0, im1, j -> l.get(k, j) * u.get(j, k));
                    u.set(k, k, get(k, k) - sumA);

                    double sumB = Mathd.summation(0, im1, j -> l.get(k, j) * u.get(j, m));
                    u.set(k, m, get(k, m) - sumB);

                    double sumC = Mathd.summation(0, im1, j -> l.get(m, j) * u.get(j, k));
                    l.set(m, k, (get(m, k) - sumC) / u.get(k, k));
                }
            }

            double sum = Mathd.summation(0, nm1, p -> l.get(nm1,p) * u.get(p,nm1));
            u.set(nm1, nm1, get(nm1, nm1) - sum);
        }

        
        public String toString() {
            return "LU {" +
                    "l=" + l +
                    ", u=" + u +
                    '}';
        }
    }

    public class QRd {
        final public Matd q, r;

        private QRd () {
            final BiFunction<Vecd, Vecd, Vecd> projFunc = (u, a) -> u.mul(u.dot(a) / u.dot(u));

            Matd a = T();
            Vecd[] u = new Vecd[cols()];
            Vecd[] e = new Vecd[cols()];

            u[0] = a.get(0);
            e[0] = u[0].unit();

            for (int i = 1; i< cols(); i++) {
                Vecd proj = new Vecd(cols());
                for (int j=0;j<i;j++) {
                    proj = proj.add(projFunc.apply(u[j], a.get(i)));
                }

                u[i] = a.get(i).subtr(proj);
                e[i] = u[i].unit();
            }

            this.q = new Matd(e).T();
            this.r = Matd.foreach(rows(), cols(), (i, j) -> j >= i ? e[i].dot(a.get(j)) : 0);
        }

        
        public String toString() {
            return "QR {" +
                    "q=" + q +
                    ", r=" + r +
                    '}';
        }
    }
}
