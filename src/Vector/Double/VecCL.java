package Vector.Double;

import GPGPU.OpenCL.Buffer.DoubleBuffer;
import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Double.MatCL;
import References.Double.Ref1D;
import Vector.Single.VecCLf;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class VecCL extends DoubleBuffer {
    public VecCL (Context context, int size) {
        super(context, size);
    }

    public VecCL (Context context, Ref1D values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCL (Context context, double... values) {
        this(context, values.length);
        set(values);
    }

    public VecCL (int size) {
        super(Context.DEFAULT, size);
    }

    public VecCL (Ref1D values) {
        this(Context.DEFAULT, values);
    }

    public VecCL (double... values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (VecCL b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     * @param alpha Scalar multiplier
     * @param y Vector
     */
    public VecCL add (double alpha, VecCL y) {
        checkCompatibility(y);
        VecCL result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastDaxpy(size, alpha, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y, in which x and y are vectors.
     * @param y Vector
     */
    public VecCL add (VecCL y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCL add (double alpha) {
        double[] vals = new double[size];
        Arrays.fill(vals, alpha);

        VecCL vector = new VecCL(getContext(), vals);
        VecCL result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCL subtr (double beta, VecCL y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCL subtr (VecCL y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCL subtr (double alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCL mul (double alpha, VecCL y) { // TODO
        checkCompatibility(y);
        MatCL identity = identityLike();
        VecCL result = identity.mul(alpha, y);

        identity.release();
        return result;
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCL mul (VecCL y) {
        return mul(1, y);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     * @param alpha Scalar
     */
    public VecCL mul (double alpha) {
        VecCL result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastDscal(size, alpha, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     * @param alpha Scalar
     */
    public VecCL div (double alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     * @param y Vector
     */
    public double dot (VecCL y) {
        checkCompatibility(y);
        CommandQueue queue = getQueue();
        FloatBuffer buffer = new FloatBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDdot(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public double magnitude() {
        CommandQueue queue = getQueue();
        DoubleBuffer buffer = new DoubleBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public double magnitude2 () {
        return dot(this);
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCL norm () {
        return div(magnitude());
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCL identityLike () {
        MatCL matrix = new MatCL(size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public VecCLf toFloat() {
        double[] values = toArray();
        float[] casted = new float[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = (float) values[i];
        }

        return new VecCLf(getContext(), casted);
    }

    public Vec toCPU () {
        return new Vec(toArray());
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
    public VecCL clone() {
        VecCL vector = new VecCL(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
