package org.jml.Vector.Double;

import org.jml.Complex.Double.Compd;
import org.jml.GPGPU.CUDA.CUDA;
import org.jml.Matrix.Double.MatCUDAid;
import org.jml.References.Double.Complex.Ref1Did;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;

import java.util.Arrays;

public class VecCUDAid implements Ref1Did {
    final public static int ELEMSIZE = 2 * Sizeof.DOUBLE;
    static {
        CUDA.init();
    }

    final public int size;
    final public Pointer id;

    public VecCUDAid(int size) {
        this.size = size;
        this.id = new Pointer();
        JCublas.cublasAlloc(size, ELEMSIZE, id);
    }

    public VecCUDAid(Compd... values) {
        this(values.length);
        set(values);
    }

    public VecCUDAid(Vecid values) {
        this(values.toArray());
    }

    private void checkCompatibility (VecCUDAid b) {
        if (size != b.size) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCUDAid add (Compd alpha, VecCUDAid y) {
        checkCompatibility(y);

        VecCUDAid result = y.clone();
        JCublas.cublasZaxpy(size, alpha.toCUDA(), this.id, 1, result.id, 1);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCUDAid add (VecCUDAid y) {
        return add(Compd.ONE, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCUDAid add (Compd alpha) {
        Compd[] vals = new Compd[size];
        Arrays.fill(vals, alpha);

        VecCUDAid vector = new VecCUDAid(vals);
        VecCUDAid result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCUDAid subtr (Compd beta, VecCUDAid y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCUDAid subtr (VecCUDAid y) {
        return subtr(Compd.ONE, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCUDAid subtr (Compd alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCUDAid invSubtr (Compd alpha) {
        return mul(Compd.MONE).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCUDAid mul (Compd alpha, VecCUDAid b) {
        checkCompatibility(b);
        return identityLike().mul(alpha, b);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCUDAid mul (VecCUDAid b) {
        return mul(Compd.ONE, b);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAid mul (Compd alpha) {
        VecCUDAid result = clone();
        JCublas.cublasZscal(size, alpha.toCUDA(), result.id, 1);

        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCUDAid div (Compd alpha) {
        return mul(alpha.inverse());
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public Compd dot (VecCUDAid y) {
        return new Compd(JCublas.cublasZdotu(size, id, 1, y.id, 1));
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public double magnitude () {
        return JCublas.cublasDznrm2(size, id, 1);
    }

    /**
     * Calculates vector's magnitude to the second power
     */
    public double magnitude2 () {
        double mag = magnitude();
        return mag * mag;
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCUDAid unit() {
        return div(new Compd(magnitude(), 0));
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCUDAid identityLike () {
        MatCUDAid matrix = new MatCUDAid(size, size);
        JCublas.cublasZcopy(size, id, 1, matrix.id, size + 1);

        return matrix;
    }

    public void set (Compd... values) {
        if (values.length != size) {
            throw new IllegalArgumentException();
        }

        double[] cuda = new double[2 * size];
        for (int i=0;i<size;i++) {
            int j = 2 * i;
            cuda[j] = values[i].real;
            cuda[j+1] = values[i].imaginary;
        }

        JCublas.cublasSetVector(size, ELEMSIZE, Pointer.to(cuda), 1, id, 1);
    }

    public void release () {
        JCublas.cublasFree(id);
    }

    private double[] toDoubleArray () {
        double[] vals = new double[2 * size];
        JCublas.cublasGetVector(size, ELEMSIZE, id, 1, Pointer.to(vals), 1);

        return vals;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Compd get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        double[] array = toDoubleArray();
        int j = 2 * pos;

        return new Compd(array[j], array[j+1]);
    }

    @Override
    public void set (int pos, Compd val) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException();
        }

        int j = 2 * pos;
        double[] array = toDoubleArray();
        array[j] = val.real;
        array[j+1] = val.imaginary;

        JCublas.cublasSetVector(size, ELEMSIZE, Pointer.to(array), 1, id, 1);
    }

    @Override
    public Compd[] toArray() {
        double[] array = toDoubleArray();
        Compd[] result = new Compd[size];

        for (int i=0;i<size;i++) {
            int j = 2 * i;
            result[i] = new Compd(array[j], array[j+1]);
        }

        return result;
    }

    public Vecid toCPU () {
        return new Vecid(toArray());
    }

    public MatCUDAid rowMatrix () {
        return new MatCUDAid(this, 1);
    }

    public MatCUDAid colMatrix () {
        return new MatCUDAid(this, size);
    }

    @Override
    public String toString() {
        Compd[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCUDAid clone() {
        VecCUDAid clone = new VecCUDAid(size);
        JCublas.cublasZcopy(size, id, 1, clone.id, 1);

        return clone;
    }
}
