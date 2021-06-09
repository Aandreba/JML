package org.jml.Matrix.Single;

import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Mathx.Extra.Intx;
import org.jml.Mathx.Mathf;
import org.jml.Matrix.Double.Matd;
import org.jml.References.Single.Ref1D;
import org.jml.References.Single.Ref2D;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Mat implements Ref2D {
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

    public Mat(Ref1D... values) {
        this.values = new Vec[values.length];
        this.values[0] = Vec.fromRef(values[0]);

        int n = getCols();
        for (int i=1;i<values.length;i++) {
            this.values[i] = new Vec(n);
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

    public Vec getCol (int col) {
        if (col < 0 || col >= getCols()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Vec vec = new Vec(getCols());
        for (int i=0;i<vec.getSize();i++) {
            vec.set(i, get(i, col));
        }

        return vec;
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

    public void setCol (int col, Vec value) {
        if (value.getSize() != getRows()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<getRows();i++) {
            set(i, col, value.get(i));
        }
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

    public Mat foreachValue (Function<Float, Float> valueFunction) {
        int rows = getRows();
        int cols = getCols();

        Mat matrix = new Mat(rows, cols);
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, valueFunction.apply(get(i,j)));
            }
        }

        return matrix;
    }

    public Mat foreachVector (Function<Vec, Vec> vectorFunction) {
        int rows = getRows();
        int cols = getCols();

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

    public static Mat foreach(int rows, int cols, MatfForEachVecf forEach) {
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

    public static Mat foreach(int rows, int cols, MatfForEachIndex forEach) {
        Mat matrix = new Mat(rows, cols);

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                matrix.set(i, j, forEach.apply(i, j));
            }
        }

        return matrix;
    }

    public Mat add (Mat b) {
        return foreach(b, Float::sum);
    }

    public Mat add (Vec b) {
        return foreach(getRows(), getCols(), (i, j) -> get(i, j) + b.get(j));
    }

    public Mat add (float b) {
        return foreach(b, Float::sum);
    }

    public Mat subtr (Mat b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Mat subtr (Vec b) {
        return foreach(getRows(), getCols(), (i, j) -> get(i, j) - b.get(j));
    }

    public Mat subtr (float b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Mat invSubtr (Vec b) {
        return foreach(getRows(), getCols(), (i, j) -> b.get(j) - get(i, j));
    }

    public Mat invSubtr (float b) {
        return foreach(b, (x, y) -> y - x);
    }

    public Mat mul (Mat b) {
        int rows = getRows();
        int cols = b.getCols();
        int dig = Math.min(getCols(), b.getRows());

        return foreach(rows, cols, (i, j) -> {
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
        return foreach(b, (x, y) -> x * y);
    }

    public Mat scalMul (Vec b) {
        return foreach(getRows(), getCols(), (i, j) -> get(i, j) * b.get(j));
    }

    public Mat scalMul (float b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Mat scalDiv (Mat b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Mat scalDiv (Vec b) {
        return foreach(getRows(), getCols(), (i, j) -> get(i, j) / b.get(j));
    }

    public Mat scalDiv (float b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Mat scalInvDiv (Vec b) {
        return foreach(getRows(), getCols(), (i, j) -> b.get(j) / get(i, j));
    }

    public Mat scalInvDiv (float b) {
        return foreach(b, (x, y) -> y / x);
    }

    public Mat inverse () {
        int n = getCols();
        int n2 = 2 * n;
        Mat matrix = new Mat(getRows(), n2);

        for (int i=0;i<getRows();i++) {
            Vec vector = new Vec(n2);
            for (int j=0;j<n;j++) {
                vector.set(j, get(i,j));
            }

            vector.set(n + i, 1);
            matrix.set(i, vector);
        }

        Mat rref = matrix.rref();
        Mat result = new Mat(getRows(), n);

        for (int i=0;i<getRows();i++) {
            for (int j=0;j<n;j++) {
                result.set(i, j, rref.get(i,n + j));
            }
        }

        return result;
    }

    public Mat newtonInverse(Mat guess) {
        Mat y = guess;
        Mat last = null;

        int n = 0;
        while (!y.equals(last) && n < 100) {
            last = y;
            y = y.scalMul(2).subtr(y.mul(this).mul(y));
            n++;
        }

        return y;
    }

    public Mat cofactor () {
        return adj().T();
    }

    public Mat adj () {
        return inverse().scalMul(det());
    }

    public Mat rref () {
        Mat result = clone();
        for (int i=0;i<getRows();i++) {
            if (result.get(i).equals(new Vec(getCols()))) {
                continue;
            }

            result.set(i, result.get(i).div(result.get(i,i)));
            for (int j=0;j<getRows();j++) {
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
        for (int i=0;i<getRows();i++) {
            sum += get(i,i);
        }

        return sum;
    }

    public float det () {
        int k = getRows();
        if (k != getCols()) {
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

    public Eigen eigen () {
        return new Eigen();
    }

    public Mat exp () {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate exponential of non-square matrix");
        }

        int n = getRows();
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

        return subtr(identity(getRows())).log1p();
    }

    public Mat pow (int b) {
        if (!isSquare()) {
            throw new ArithmeticException("Tried to calculate power of non-square matrix");
        } else if (b < 0) {
            throw new ArithmeticException("Tried to calculate negative power of matrix");
        }

        Mat result = identity(getRows());
        for (int i=0;i<b;i++) {
            result = result.mul(this);
        }

        return result;
    }

    public Mat sqrt () {
        Mat y = this;
        Mat z = identity(getRows());

        Mat zInverse = null;
        Mat yInverse = null;
        int n = 1;

        while (n <= 1000) {
            if (n <= 10) {
                zInverse = z.inverse();
                yInverse = y.inverse();
            } else {
                zInverse = z.newtonInverse(zInverse);
                yInverse = y.newtonInverse(yInverse);
            }

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
        int rows = getCols();
        int cols = getRows();

        return foreach(rows, cols, (i, j) -> get(j, i));
    }

    public Matd toDouble () {
        return Matd.forEach(getRows(), getCols(), (Matd.MatForEachIndex) this::get);
    }

    @Override
    public Mati toComplex() {
        Veci[] complex = new Veci[getRows()];
        for (int i=0;i<getRows();i++) {
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

    public Vec toVectorRow () {
        return Vec.fromRef(rowMajor());
    }

    public Vec toVectorCol () {
        return Vec.fromRef(colMajor());
    }

    public static Mat identity (int k) {
        return foreach(k, k, (i, j) -> i == j ? 1 : 0);
    }

    public static Mat fromRef (Ref2D ref) {
        return ref instanceof Mat ? (Mat) ref : foreach(ref.getRows(), ref.getCols(), (MatfForEachIndex) ref::get);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mat ref1Dfs = (Mat) o;
        return Arrays.equals(values, ref1Dfs.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    public class LU {
        final public Mat l, u;

        private LU () {
            int n = getRows();
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
                    float sumA = Mathf.summation(0, im1, j -> l.get(k, j) * u.get(j, k));
                    u.set(k, k, get(k, k) - sumA);

                    float sumB = Mathf.summation(0, im1, j -> l.get(k, j) * u.get(j, m));
                    u.set(k, m, get(k, m) - sumB);

                    float sumC = Mathf.summation(0, im1, j -> l.get(m, j) * u.get(j, k));
                    l.set(m, k, (get(m, k) - sumC) / u.get(k, k));
                }
            }

            float sum = Mathf.summation(0, nm1, p -> l.get(nm1,p) * u.get(p,nm1));
            u.set(nm1, nm1, get(nm1, nm1) - sum);
        }
    }

    public class QR {
        final public Mat q, r;

        private QR () {
            final BiFunction<Vec, Vec, Vec> projFunc = (u, a) -> u.mul(u.dot(a) / u.dot(u));

            Mat a = T();
            Vec[] u = new Vec[getCols()];
            Vec[] e = new Vec[getCols()];

            u[0] = a.get(0);
            e[0] = u[0].unit();

            for (int i=1;i<getCols();i++) {
                Vec proj = new Vec(getCols());
                for (int j=0;j<i;j++) {
                    proj = proj.add(projFunc.apply(u[j], a.get(i)));
                }

                u[i] = a.get(i).subtr(proj);
                e[i] = u[i].unit();
            }

            this.q = new Mat(e).T();
            this.r = Mat.foreach(getRows(), getCols(), (i,j) -> j >= i ? e[i].dot(a.get(j)) : 0);
        }
    }

    public class Eigen {
        int n;
        Comp[] values;

        private Eigen () {
            this.n = getRows();
            this.values = calc();
        }

        public Comp getValue (int pos) {
            return values[pos];
        }

        public Veci getVector (int pos) {
            Comp value = values[pos];
            Mati matrix = toComplex().subtr(Mati.identity(n).scalMul(value)).rref();

            Veci vector = new Veci(n);
            for (int i = 0; i< n -1; i++) {
                vector.set(i, matrix.get(i, n -1).mul(-1).div(matrix.get(i,i)));
            }

            vector.set(n -1, Comp.ONE);
            return vector;
        }

        @Override
        public String toString() {
            return "Eigen {" +
                    "values=" + Arrays.toString(values) +
                    '}';
        }

        private Comp[] calc () {
            if (n == 1) {
                return new Comp[]{ new Comp(get(0,0), 0) };
            } else if (n == 2) {
                return Mathf.quadratic(1, -tr(), det());
            } else if (n == 3) {
                float tr = tr();
                return Mathf.cubic(-1, tr, (pow(2).tr() - tr * tr) / 2, det());
            }

            return null;
        }
    }
}
