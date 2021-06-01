package Vector.Single;

import GPGPU.CUDA.CUDA;
import Matrix.Single.MatCUDA;
import References.Single.Ref1Df;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

import java.util.Arrays;

public class VecCUDA implements Ref1Df {
    static {
        CUDA.init();
    }

    final public int size;
    final public Pointer id;

    public VecCUDA(int size) {
        this.size = size;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, Sizeof.FLOAT, id);
    }

    public VecCUDA(float... values) {
        this(values.length);
        set(values);
    }

    public VecCUDA(Vec values) {
        this(values.toArray());
    }

    private void checkCompatibility (VecCUDA b) {
        if (size != b.size) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCUDA add (float alpha, VecCUDA y) {
        checkCompatibility(y);

        VecCUDA result = y.clone();
        JCublas.cublasSaxpy(size, alpha, this.id, 1, result.id, 1);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCUDA add (VecCUDA y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCUDA add (float alpha) {
        float[] vals = new float[size];
        Arrays.fill(vals, alpha);

        VecCUDA vector = new VecCUDA(vals);
        VecCUDA result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCUDA subtr (float beta, VecCUDA y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCUDA subtr (VecCUDA y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCUDA subtr (float alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCUDA invSubtr (float alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCUDA mul (float alpha, VecCUDA b) {
        checkCompatibility(b);
        return identityLike().mul(alpha, b);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCUDA mul (VecCUDA b) {
        return mul(1, b);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCUDA mul (float alpha) {
        VecCUDA result = clone();
        JCublas.cublasSscal(size, alpha, result.id, 1);

        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCUDA div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public float dot (VecCUDA y) {
        return JCublas.cublasSdot(size, id, 1, y.id, 1);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public float magnitude () {
        return JCublas.cublasSnrm2(size, id, 1);
    }

    public float magnitude2 () {
        return dot(this);
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCUDA unit() {
        return div(magnitude());
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCUDA identityLike () {
        MatCUDA matrix = new MatCUDA(size, size);
        JCublas.cublasScopy(size, id, 1, matrix.id, size + 1);

        return matrix;
    }

    public void set (float... values) {
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
    public float get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        return toArray()[pos];
    }

    @Override
    public void set (int pos, float val) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        float[] values = toArray();
        values[pos] = val;
        set(values);
    }

    @Override
    public float[] toArray() {
        float[] array = new float[size];
        JCublas.cublasGetVector(size, Sizeof.FLOAT, id, 1, Pointer.to(array), 1);

        return array;
    }

    public Vec toCPU () {
        return new Vec(toArray());
    }

    public MatCUDA rowMatrix () {
        return new MatCUDA(this, 1);
    }

    public MatCUDA colMatrix () {
        return new MatCUDA(this, size);
    }

    @Override
    public String toString() {
        float[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCUDA clone() {
        VecCUDA clone = new VecCUDA();
        JCublas.cublasScopy(size, id, 1, clone.id, 1);

        return clone;
    }
}
