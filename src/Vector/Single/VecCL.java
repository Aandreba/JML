package Vector.Single;

import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Single.MatCL;
import References.Single.Ref1D;
import Vector.Double.VecCLd;
import org.jocl.*;
import org.jocl.blast.CLBlast;
import java.util.Arrays;

public class VecCL extends FloatBuffer {
    public VecCL(Context context, int size) {
        super(context, size);
    }

    public VecCL(Context context, Ref1D values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCL(Context context, float... values) {
        this(context, values.length);
        set(values);
    }

    public VecCL(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCL(Ref1D values) {
        this(Context.DEFAULT, values);
    }

    public VecCL(float... values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (VecCL b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCL add (float alpha, VecCL y) {
        checkCompatibility(y);
        VecCL result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastSaxpy(size, alpha, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCL add (VecCL y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCL add (float alpha) {
        float[] vals = new float[size];
        Arrays.fill(vals, alpha);

        VecCL vector = new VecCL(getContext(), vals);
        VecCL result = add(vector);
        vector.release(true);

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCL subtr (float beta, VecCL y) {
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
    public VecCL subtr (float alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCL invSubtr (float alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCL mul (float alpha, VecCL y) {
        checkCompatibility(y);
        MatCL identity = identityLike();
        VecCL result = identity.mul(alpha, y);

        identity.release(true);
        return result;
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCL mul (VecCL y) {
        return mul(1, y);
    }

    /**
     * Performs the operation y = alpha * x
     */
    public VecCL mul (float alpha) {
        VecCL result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastSscal(size, alpha, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x / alpha
     */
    public VecCL div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Performs the operation y = alpha / x
     */
    public VecCL invDiv (float alpha) { // TODO
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public float dot (VecCL y) {
        checkCompatibility(y);
        CommandQueue queue = getQueue();
        FloatBuffer buffer = new FloatBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastSdot(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public float magnitude () {
        CommandQueue queue = getQueue();
        FloatBuffer buffer = new FloatBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastSnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public float magnitude2 () {
        return dot(this);
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCL unit () {
        return div(magnitude());
    }

    public float sum () {
        CommandQueue queue = getQueue();
        FloatBuffer buffer = new FloatBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastSsum(size, buffer.getId(), 0, getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public float mean () {
        return sum() / size;
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCL identityLike () {
        MatCL matrix = new MatCL(getContext(), size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public VecCLd toDouble() {
        float[] values = toArray();
        double[] casted = new double[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i];
        }

        return new VecCLd(getContext(), casted);
    }

    public Vec toCPU () {
        return new Vec(toArray());
    }

    public MatCL rowMatrix () {
        return new MatCL(this.clone(), size);
    }

    public MatCL colMatrix () {
        return new MatCL(this.clone(), 1);
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
    public VecCL clone() {
        VecCL vector = new VecCL(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
