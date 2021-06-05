package Vector.Double;

import GPGPU.CUDA.CUDA;
import Matrix.Double.MatCUDAd;
import References.Double.Ref1Dd;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

import java.util.Arrays;

public class VecCUDAd implements Ref1Dd {
    static {
        CUDA.init();
    }

    final public int size;
    final public Pointer id;

    public VecCUDAd(int size) {
        this.size = size;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, Sizeof.DOUBLE, id);
    }

    public VecCUDAd(double... values) {
        this(values.length);
        set(values);
    }

    public VecCUDAd(Vecd values) {
        this(values.toArray());
    }

    private void checkCompatibility (VecCUDAd b) {
        if (size != b.size) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCUDAd add (double alpha, VecCUDAd y) {
        checkCompatibility(y);

        VecCUDAd result = y.clone();
        JCublas.cublasDaxpy(size, alpha, this.id, 1, result.id, 1);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCUDAd add (VecCUDAd y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCUDAd add (double alpha) {
        double[] vals = new double[size];
        Arrays.fill(vals, alpha);

        VecCUDAd vector = new VecCUDAd(vals);
        VecCUDAd result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCUDAd subtr (double beta, VecCUDAd y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCUDAd subtr (VecCUDAd y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCUDAd subtr (double alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCUDAd invSubtr (double alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCUDAd mul (double alpha, VecCUDAd b) {
        checkCompatibility(b);
        return identityLike().mul(alpha, b);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCUDAd mul (VecCUDAd b) {
        return mul(1, b);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAd mul (double alpha) {
        VecCUDAd result = clone();
        JCublas.cublasDscal(size, alpha, result.id, 1);

        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAd div (double alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public double dot (VecCUDAd y) {
        return JCublas.cublasDdot(size, id, 1, y.id, 1);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public double magnitude () {
        return JCublas.cublasDnrm2(size, id, 1);
    }

    public double magnitude2 () {
        return dot(this);
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCUDAd unit() {
        return div(magnitude());
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCUDAd identityLike () {
        MatCUDAd matrix = new MatCUDAd(size, size);
        JCublas.cublasDcopy(size, id, 1, matrix.id, size + 1);

        return matrix;
    }

    public void set (double... values) {
        if (values.length != size) {
            throw new IllegalArgumentException();
        }

        JCublas.cublasSetVector(size, Sizeof.FLOAT, Pointer.to(values), 1, id, 1);
    }

    public void release () {
        JCublas.cublasFree(id);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public double get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        return toArray()[pos];
    }

    @Override
    public void set (int pos, double val) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        double[] values = toArray();
        values[pos] = val;
        set(values);
    }

    @Override
    public double[] toArray() {
        double[] array = new double[size];
        JCublas.cublasGetVector(size, Sizeof.DOUBLE, id, 1, Pointer.to(array), 1);

        return array;
    }

    public Vecd toCPU () {
        return new Vecd(toArray());
    }

    public MatCUDAd rowMatrix () {
        return new MatCUDAd(this, 1);
    }

    public MatCUDAd colMatrix () {
        return new MatCUDAd(this, size);
    }

    @Override
    public String toString() {
        double[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCUDAd clone() {
        VecCUDAd clone = new VecCUDAd();
        JCublas.cublasDcopy(size, id, 1, clone.id, 1);

        return clone;
    }
}
