package org.jml.Matrix.Single;

import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Link.Single.Link2D;
import org.jml.Extra.Intx;
import org.jml.Mathx.Mathf;
import org.jml.Mathx.Rand;
import org.jml.MT.TaskManager;
import org.jml.Matrix.Double.Matd;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Mat extends Link2D implements Serializable {
    final private static long serialVersionUID = 4991458981436962219L;
    final protected Vec[] values;

    public Mat(int rows, int cols) {
        super(rows, cols);

        this.values = new Vec[rows];

        for (int i=0;i<rows;i++) {
            this.values[i] = new Vec(cols);
        }
    }

    public Mat(float[][] values) {
        super(values.length, values[0].length);

        this.values = new Vec[values.length];
        this.values[0] = new Vec(values[0]);

        for (int i=1;i<values.length;i++) {
            set(i, new Vec(values[i]));
        }
    }

    public Mat(Vec... values) {
        super(values.length, values[0].size);

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

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }
    
    public float get (int row, int col) {
        return values[row].get(col);
    }

    public Vec get (int row) {
        return values[row];
    }

    public Vec getCol (int col) {
        if (col < 0 || col >= cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Vec vec = new Vec(cols());
        for (int i = 0; i<vec.size(); i++) {
            vec.set(i, get(i, col));
        }

        return vec;
    }

    public void set (int row, int col, float value) {
        values[row].set(col, value);
    }

    public void set (int row, Vec value) {
        if (value.size() != cols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        values[row] = value;
    }

    public void set (int row, int offsetSrc, int offsetDest, int length, Vec value) {
        values[row].set(offsetSrc, offsetDest, length, value);
    }

    public void setCol (int col, Vec value) {
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

    private int finalRows (Mat b) {
        return Math.min(rows(), b.rows());
    }

    private int finalCols (Mat b) {
        return Math.min(cols(), b.cols());
    }

    public Mat foreachValue (Function<Float, Float> valueFunction) {
        int rows = rows();
        int cols = cols();

        Mat matrix = new Mat(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, valueFunction.apply(get(i,j)));
            }
        }

        return matrix;
    }

    public Mat foreachVector (Function<Vec, Vec> vectorFunction) {
        int rows = rows();
        int cols = cols();

        Mat matrix = new Mat(rows, cols);
        for (int i=0;i<rows;i++) {
            matrix.set(i, vectorFunction.apply(get(i)));
        }

        return matrix;
    }

    public Mat foreach(Mat b, Vec.VecfForEach forEach) {
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

    public Mat foreach (float b, Vec.VecfForEach forEach) {
        int rows = rows();
        int cols = cols();

        Mat matrix = new Mat(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(get(i, j), b));
            }
        }

        return matrix;
    }

    public static Mat foreach(int rows, int cols, MatfForEachVecf forEach) {
        Mat matrix = new Mat(rows, cols);

        for (int i=0;i<rows;i++) {
            Vec vector = forEach.apply(i);
            if (vector.size() > cols) {
                throw new IllegalArgumentException();
            }

            matrix.set(i, vector);
        }

        return matrix;
    }

    public static Mat foreach(int rows, int cols, MatfForEachIndex forEach) {
        Mat matrix = new Mat(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Mat add (Mat b) {
        return foreach(b, Float::sum);
    }

    public Mat add (Vec b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) + b.get(j));
    }

    public Mat add (float b) {
        return foreach(b, Float::sum);
    }

    public Mat subtr (Mat b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Mat subtr (Vec b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) - b.get(j));
    }

    public Mat subtr (float b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Mat invSubtr (Vec b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j) - get(i, j));
    }

    public Mat invSubtr (float b) {
        return foreach(b, (x, y) -> y - x);
    }

    public Mat mul (Mat b) {
        int rows = rows();
        int cols = b.cols();
        int digs = Math.min(cols(), b.rows());

        if (rows * cols <= 15000) {
            return foreach(rows, cols, (i, j) -> {
                float sum = 0;
                for (int k=0;k<digs;k++) {
                    sum += get(i, k) * b.get(k, j);
                }

                return sum;
            });
        }


        TaskManager tasks = new TaskManager();
        Mat result = new Mat(rows, cols);

        for (int i=0;i<rows;i++) {
            int finalI = i;
            for (int j=0;j<cols;j++) {
                int finalJ = j;

                tasks.add(() -> {
                    float sum = 0;
                    for (int k=0;k<digs;k++) {
                        sum += get(finalI, k) * b.get(k, finalJ);
                    }

                    result.set(finalI, finalJ, sum);
                });
            }
        }

        tasks.run();
        return result;
    }

    public Vec mul (Vec b) {
        return mul(b.colMatrix()).T().get(0);
    }

    public Mat div (Mat b) {
        return mul(b.inverse());
    }

    public Mat scalMul (Mat b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Mat scalMul (Vec b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) * b.get(j));
    }

    public Mat scalMul (float b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Mat scalDiv (Mat b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Mat scalDiv (Vec b) {
        return foreach(rows(), cols(), (i, j) -> get(i, j) / b.get(j));
    }

    public Mat scalDiv (float b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Mat scalInvDiv (Vec b) {
        return foreach(rows(), cols(), (i, j) -> b.get(j) / get(i, j));
    }

    public Mat scalInvDiv (float b) {
        return foreach(b, (x, y) -> y / x);
    }

    public Mat inverse () {
        int n = cols();
        int n2 = 2 * n;
        Mat matrix = new Mat(rows(), n2);

        for (int i = 0; i< rows(); i++) {
            Vec vector = new Vec(n2);
            for (int j=0;j<n;j++) {
                vector.set(j, get(i,j));
            }

            vector.set(n + i, 1);
            matrix.set(i, vector);
        }

        Mat rref = matrix.rref();
        Mat result = new Mat(rows(), n);

        for (int i = 0; i< rows(); i++) {
            for (int j=0;j<n;j++) {
                result.set(i, j, rref.get(i,n + j));
            }
        }

        return result;
    }

    public Mat cofactor () {
        return adj().T();
    }

    public Mat adj () {
        return inverse().scalMul(det());
    }

    public Mat rref () {
        Mat result = clone();
        for (int i = 0; i < rows(); i++) {
            if (result.get(i).equals(new Vec(cols()))) {
                continue;
            }

            result.set(i, result.get(i).div(result.get(i,i)));
            for (int j = 0; j < rows(); j++) {
                if (i == j) {
                    continue;
                }

                result.set(j, result.get(j).subtr(result.get(i).mul(result.get(j,i))));
            }
        }

        return result;
    }

    public float tr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate trace of non-square matrix");
        }

        float sum = 0;
        for (int i = 0; i< rows(); i++) {
            sum += get(i,i);
        }

        return sum;
    }

    public float det () {
        int k = rows();
        if (k != cols()) {
            throw new ArithmeticException("Tried to calculate determinant of non-square matrix");
        } else if (k == 1) {
            return get(0,0);
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

    public Vec fadlev () {
        int n = rows();
        Vec poly = new Vec(n+1);
        poly.set(0, 1);

        Mat y = this;
        for (int i=1;i<=n;i++) {
            poly.set(i, -(1f/i) * y.tr());
            y = mul(y).add(scalMul(poly.get(i)));
        }

        return poly;
    }

    /**
     * Calculates largest eigenvalue
     * @see <a href="https://en.wikipedia.org/wiki/Power_iteration">Power iteration</a>
     */
    public Comp eigval () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvalue of non-square matrix");
        }

        Mati A = toComplex();
        Veci b = Rand.getVeci(rows(), new Comp(Rand.getFloat(0.1f, 1), Rand.getFloat(0.1f, 1)), Comp.ONE);
        Veci last = null;

        while (!b.equals(last)) {
            last = b;
            b = A.mul(b).unit();
        }

        Veci eigen = A.mul(b).div(b);
        return eigen.mean();
    }

    public Veci eigvals () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvalues of non-square matrix");
        }

        float tr = tr();
        return Mathf.poly(scalDiv(tr).fadlev().toArray()).mul(tr);
    }

    public Veci eigvec () {
        return eigvec(eigval());
    }

    /**
     * Calculates eigenvector of specific eigenvalue
     * @see <a href="https://en.wikipedia.org/wiki/Inverse_iteration">Inverse iteration</a>
     */
    public Veci eigvec (Comp value) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvector of non-square matrix");
        }

        int n = rows();
        int nm1 = n - 1;

        Mati M = toComplex();
        Mati A = M.subtr(Mati.identity(n).scalMul(value));
        Mati B = A.inverse();

        Veci b = Rand.getVeci(n).mul(value.inverse());
        Veci last = null;

        while (last == null || b.abs().subtr(last.abs()).abs().max() > 1e-7f) {
            last = b;
            b = B.mul(b).unit();
        }

        Veci vec = b.div(b.get(nm1));
        if (vec.mul(value).subtr(M.mul(vec)).abs().max() <= 1e-6f) {
            return vec;
        }

        Mati C = A.rref();
        return Veci.foreach(n, i -> i < nm1 ? C.get(i, nm1).mul(-1) : Comp.ONE);
    }

    public Veci eigvec (int pos) {
        return eigvec(eigvals().get(pos));
    }

    public Mati eigvecs (Veci values) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate eigenvectors of non-square matrix");
        }

        return Mati.foreach(rows(), rows(), i -> eigvec(values.get(i)));
    }

    public Mati eigvecs () {
        return eigvecs(eigvals());
    }

    public Mat exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = rows();
        int k = 1;
        long factorial = 1;
        Mat pow = identity(n);

        Mat result = pow.clone();
        Mat last = null;

        while (!result.equals(last)) {
            pow = pow.mul(this);
            factorial *= k;

            last = result.clone();
            result = result.add(pow.scalDiv(factorial));
            k++;
        }

        return result;
    }

    public Mat log1p() {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate logarithm of non-square matrix");
        }

        Mat y = this;
        Mat last = null;
        Mat pow = this;

        int n = 2;
        while (!y.equals(last)) {
            pow = pow.mul(this);
            Mat x = pow.scalDiv(n);

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

    public Mat log () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate logarithm of non-square matrix");
        }

        return subtr(identity(rows())).log1p();
    }

    public Mat pow (int b) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (b < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        Mat result = identity(rows());
        for (int i=0;i<b;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Mat sqrt () {
        Mat y = this;
        Mat z = identity(rows());

        int n = 1;
        while (n <= 1000) {
            Mat zInverse = z.inverse();
            Mat yInverse = y.inverse();

            Mat newY = y.add(zInverse).scalDiv(2);
            if (newY.equals(y)) {
                return y;
            }

            Mat newZ = z.add(yInverse).scalDiv(2);
            y = newY;
            z = newZ;
            n++;
        }

        throw new ArithmeticException("No square root found");
    }

    public LU lu () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate LU decomposition of non-square matrix");
        }

        return new LU();
    }

    public QR qr () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate QR decomposition of non-square matrix");
        }

        return new QR();
    }

    public Mat T () {
        int rows = cols();
        int cols = rows();

        return foreach(rows, cols, (i, j) -> get(j, i));
    }

    public float[][] toArray () {
        int cols = cols();
        float[][] array = new float[rows()][cols];

        for (int i=0;i<array.length;i++) {
            System.arraycopy(get(i).toArray(), 0, array[i], 0, cols);
        }

        return array;
    }

    public Matd toDouble () {
        return Matd.foreach(rows(), cols(), (Matd.MatForEachIndex) this::get);
    }

    public Mati toComplex() {
        Veci[] complex = new Veci[rows()];
        for (int i = 0; i< rows(); i++) {
            complex[i] = get(i).toComplex();
        }

        return new Mati(complex);
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

    public Vec rowMajor () {
        int n = cols();
        int m = rows();
        Vec array = new Vec(n * m);

        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                array.set((i * n) + j, get(i, j));
            }
        }

        return array;
    }

    public Vec colMajor () {
        int n = cols();
        int m = rows();
        Vec array = new Vec(n * m);

        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                array.set((j * n) + i, get(i, j));
            }
        }

        return array;
    }

    public static Mat identity (int k) {
        return foreach(k, k, (i, j) -> i == j ? 1 : 0);
    }

    public static Mat getTransform (Vec from, Vec to) {
        int n = from.size();
        if (n != to.size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Mat I = identity(n);
        Mat A = Rand.getMat(n, n);
        Mat last = null;

        while (!A.equals(last)) {
            Vec out = A.mul(from);

            last = A;
            A = A.add(I.scalMul(to.subtr(out)));
        }

        return A;
    }

    public Mat clone() {
        Mat matrix = new Mat(rows(), cols());
        for (int i = 0; i< rows(); i++) {
            matrix.set(i, get(i).clone());
        }

        return matrix;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mat ref1Dfs = (Mat) o;
        return Arrays.equals(values, ref1Dfs.values);
    }

    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public class LU {
        final public Mat l, u;

        private LU () {
            int n = rows();
            int nm1 = n - 1;
            this.l = identity(n);
            this.u = new Mat(n, n);

            for (int j=0;j<n;j++) {
                u.set(0, j, get(0, j));
                l.set(j, 0, get(j, 0) / u.get(0, 0));
            }

            for (int q=1;q<nm1;q++) {
                int k = q;
                int im1 = q - 1;

                for (int p=k+1;p<n;p++) {
                    int m = p;
                    float sumA = Mathf.sum(0, im1, j -> l.get(k, j) * u.get(j, k));
                    u.set(k, k, get(k, k) - sumA);

                    float sumB = Mathf.sum(0, im1, j -> l.get(k, j) * u.get(j, m));
                    u.set(k, m, get(k, m) - sumB);

                    float sumC = Mathf.sum(0, im1, j -> l.get(m, j) * u.get(j, k));
                    l.set(m, k, (get(m, k) - sumC) / u.get(k, k));
                }
            }

            float sum = Mathf.sum(0, nm1, p -> l.get(nm1,p) * u.get(p,nm1));
            u.set(nm1, nm1, get(nm1, nm1) - sum);
        }

        
        public String toString() {
            return "LU {" +
                    "l=" + l +
                    ", u=" + u +
                    '}';
        }
    }

    public class QR {
        final public Mat q, r;

        private QR () {
            final BiFunction<Vec, Vec, Vec> projFunc = (u, a) -> u.mul(u.dot(a) / u.dot(u));

            Mat a = T();
            Vec[] u = new Vec[cols()];
            Vec[] e = new Vec[cols()];

            u[0] = a.get(0);
            e[0] = u[0].unit();

            for (int i = 1; i< cols(); i++) {
                Vec proj = new Vec(cols());
                for (int j=0;j<i;j++) {
                    proj = proj.add(projFunc.apply(u[j], a.get(i)));
                }

                u[i] = a.get(i).subtr(proj);
                e[i] = u[i].unit();
            }

            this.q = new Mat(e).T();
            this.r = Mat.foreach(rows(), cols(), (i, j) -> j >= i ? e[i].dot(a.get(j)) : 0);
        }

        
        public String toString() {
            return "QR {" +
                    "q=" + q +
                    ", r=" + r +
                    '}';
        }
    }
}
