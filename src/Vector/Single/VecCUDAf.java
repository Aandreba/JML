package Vector.Single;

import GPGPU.CUDA.CUDA;
import References.Single.Ref1Df;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import java.util.Arrays;

public class VecCUDAf implements Ref1Df {
    static {
        CUDA.init();
    }

    final int size;
    final Pointer id;

    public VecCUDAf (int size) {
        this.size = size;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, Sizeof.FLOAT, id);
    }

    public VecCUDAf (float... values) {
        this(values.length);
        set(values);
    }

    public VecCUDAf (Vecf values) {
        this(values.toArray());
    }

    private void checkCompatibility (VecCUDAf b) {
        if (size != b.size) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCUDAf add (float alpha, VecCUDAf y) {
        checkCompatibility(y);

        VecCUDAf result = y.clone();
        JCublas.cublasSaxpy(size, alpha, this.id, 1, result.id, 1);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCUDAf add (VecCUDAf y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCUDAf add (float alpha) {
        float[] vals = new float[size];
        Arrays.fill(vals, alpha);

        VecCUDAf vector = new VecCUDAf(vals);
        VecCUDAf result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCUDAf subtr (float beta, VecCUDAf y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCUDAf subtr (VecCUDAf y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCUDAf subtr (float alpha) {
        return add(-alpha);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAf mul (float alpha) {
        VecCUDAf result = clone();
        JCublas.cublasSscal(size, alpha, result.id, 1);

        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAf div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public float dot (VecCUDAf y) {
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
    public VecCUDAf norm () {
        return div(magnitude());
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
    public VecCUDAf clone() {
        VecCUDAf clone = new VecCUDAf();
        JCublas.cublasScopy(size, id, 1, clone.id, 1);

        return clone;
    }
}
