package Vector.Single;

import Complex.Comp;
import GPGPU.CUDA.CUDA;
import Matrix.Single.MatCUDAi;
import References.Single.Complex.Ref1Di;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

import java.util.Arrays;

public class VecCUDAi implements Ref1Di {
    final public static int ELEMSIZE = 2 * Sizeof.FLOAT;
    static {
        CUDA.init();
    }

    final public int size;
    final public Pointer id;

    public VecCUDAi(int size) {
        this.size = size;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, ELEMSIZE, id);
    }

    public VecCUDAi(Comp... values) {
        this(values.length);
        set(values);
    }

    public VecCUDAi(Veci values) {
        this(values.toArray());
    }

    private void checkCompatibility (VecCUDAi b) {
        if (size != b.size) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCUDAi add (Comp alpha, VecCUDAi y) {
        checkCompatibility(y);

        VecCUDAi result = y.clone();
        JCublas.cublasCaxpy(size, alpha.toCUDA(), this.id, 1, result.id, 1);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCUDAi add (VecCUDAi y) {
        return add(Comp.ONE, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCUDAi add (Comp alpha) {
        Comp[] vals = new Comp[size];
        Arrays.fill(vals, alpha);

        VecCUDAi vector = new VecCUDAi(vals);
        VecCUDAi result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCUDAi subtr (Comp beta, VecCUDAi y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCUDAi subtr (VecCUDAi y) {
        return subtr(Comp.ONE, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCUDAi subtr (Comp alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCUDAi invSubtr (Comp alpha) {
        return mul(Comp.MONE).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCUDAi mul (Comp alpha, VecCUDAi b) {
        checkCompatibility(b);
        return identityLike().mul(alpha, b);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCUDAi mul (VecCUDAi b) {
        return mul(Comp.ONE, b);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAi mul (Comp alpha) {
        VecCUDAi result = clone();
        JCublas.cublasCscal(size, alpha.toCUDA(), result.id, 1);

        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAi div (Comp alpha) {
        return mul(alpha.inverse());
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public Comp dot (VecCUDAi y) {
        return new Comp(JCublas.cublasCdotu(size, id, 1, y.id, 1));
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public float magnitude () {
        return JCublas.cublasScnrm2(size, id, 1);
    }

    /**
     * Calculates vector's magnitude to the second power
     */
    public float magnitude2 () {
        float mag = magnitude();
        return mag * mag;
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCUDAi unit() {
        return div(new Comp(magnitude(), 0));
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCUDAi identityLike () {
        MatCUDAi matrix = new MatCUDAi(size, size);
        JCublas.cublasCcopy(size, id, 1, matrix.id, size + 1);

        return matrix;
    }

    public void set (Comp... values) {
        if (values.length != size) {
            throw new IllegalArgumentException();
        }

        float[] cuda = new float[2 * values.length];
        for (int i=0;i<values.length;i++) {
            int j = 2 * i;
            cuda[j] = values[i].real;
            cuda[j+1] = values[i].imaginary;
        }

        JCublas.cublasSetVector(size, ELEMSIZE, Pointer.to(cuda), 1, id, 1);
    }

    public void release () {
        JCublas.cublasFree(id);
    }

    @Override
    public int getSize() {
        return size;
    }

    private float[] toFloatArray () {
        float[] array = new float[2 * size];
        JCublas.cublasGetVector(size, ELEMSIZE, id, 1, Pointer.to(array), 1);

        return array;
    }

    @Override
    public Comp get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        float[] array = toFloatArray();
        int j = 2 * pos;

        return new Comp(array[j], array[j+1]);
    }

    @Override
    public void set (int pos, Comp val) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        float[] array = toFloatArray();
        int i = 2 * pos;

        array[i] = val.real;
        array[i + 1] = val.imaginary;
        JCublas.cublasSetVector(size, ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    @Override
    public Comp[] toArray() {
        float[] array = toFloatArray();
        Comp[] result = new Comp[size];

        for (int i=0;i<size;i++) {
            int j = 2 * i;
            result[i] = new Comp(array[j], array[j+1]);
        }

        return result;
    }

    public Veci toCPU () {
        return new Veci(toArray());
    }

    public MatCUDAi rowMatrix () {
        return new MatCUDAi(this, 1);
    }

    public MatCUDAi colMatrix () {
        return new MatCUDAi(this, size);
    }

    @Override
    public String toString() {
        Comp[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCUDAi clone() {
        VecCUDAi clone = new VecCUDAi(size);
        JCublas.cublasCcopy(size, id, 1, clone.id, 1);

        return clone;
    }
}
