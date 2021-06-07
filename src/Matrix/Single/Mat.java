package Matrix.Single;

import Complex.Comp;
import GPGPU.OpenCL.Context;
import Mathx.Extra.Intx;
import Mathx.Mathf;
import Mathx.Rand;
import Matrix.Double.Matd;
import References.Single.Ref1D;
import References.Single.Ref2D;
import Vector.Single.Vec;
import Vector.Single.Veci;

import java.util.Arrays;

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
        float sum = 0;
        int n = Math.min(getRows(), getCols());

        for (int i=0;i<n;i++) {
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

    public class Eigen {
        int n;
        Comp[] values;

        private Eigen () {
            this.n = getRows();
            this.values = calc();
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
                float det = det();

                return Mathf.cubic(-1, tr, (pow(2).tr() - tr * tr) / 2, det);
            }

            Comp[] vals = new Comp[n];
            Comp tr = new Comp(tr(), 0);
            Comp max = closeDownValue(tr);
            Comp min = closeDownValue(Comp.ZERO);

            vals[0] = max;
            vals[1] = min;

            /*if (n == 4) {
                float det = det();

                Comp k = tr.subtr(min.add(max));
                Comp q = min.mul(max).invDiv(det);

                Comp sqrt = k.mul(k).add(q.mul(4)).sqrt();
                Comp x2 = k.mul(-1).add(sqrt).div(2);

                System.out.println(x2+", "+closeDownValue(x2));
                System.out.println(Arrays.toString(vals));
                System.exit(1);
            }*/

            return vals;
        }

        private Comp closeDownValue (Comp value) {
            Mati A = toComplex();
            Mati I = Mati.identity(n);

            float step = 1;
            float dirX = 1;
            float dirY = 1;
            float lastDet = 0;
            boolean flipDirOnReal = true;

            for (int i=0;i<1000;i++) {
                if (i > 0 && i % 100 == 0) {
                    step /= 9;
                }

                Comp det = A.subtr(I.scalMul(value)).det();
                float abs = det.modulus();

                if (i > 0 && abs > lastDet) {
                    if (flipDirOnReal) {
                        dirX *= -1;
                    } else {
                        dirY *= -1;
                    }

                    flipDirOnReal = !flipDirOnReal;
                }

                value = new Comp(value.real + dirX * step, value.imaginary + dirY * step);
                lastDet = abs;
            }

            return value;
        }
    }
}
