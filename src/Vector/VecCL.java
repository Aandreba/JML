package Vector;

import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Device;
import GPGPU.OpenCL.Query;
import References.Single.Ref1Df;
import org.jocl.*;
import org.jocl.blast.CLBlast;

import java.util.Arrays;

import static org.jocl.CL.*;

public class VecCL extends FloatBuffer {
    public VecCL (Context context, int size) {
        super(context, size);
    }

    public VecCL (Context context, Vecf values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCL (Context context, float... values) {
        this(context, values.length);
        set(values);
    }

    public VecCL (int size) {
        super(Context.DEFAULT, size);
    }

    public VecCL (Vecf values) {
        this(Context.DEFAULT, values);
    }

    public VecCL (float... values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (VecCL b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y, in which x and y are vectors and alpha is a scalar constant.
     * @param alpha Scalar multiplier
     * @param y Vector
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
    public VecCL add (float alpha) {
        float[] vals = new float[size];
        Arrays.fill(vals, alpha);

        VecCL vector = new VecCL(getContext(), vals);
        VecCL result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - alpha * y, in which x and y are vectors and alpha is a scalar constant.
     * @param alpha Scalar multiplier
     * @param y Vector
     */
    public VecCL subtr (float alpha, VecCL y) {
        return y.add(-alpha, this);
    }

    /**
     * Performs the operation x = x - y, in which x and y are vectors and alpha is a scalar constant.
     * @param y Vector
     */
    public VecCL subtr (VecCL y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCL subtr (float alpha) {
        float[] vals = new float[size];
        Arrays.fill(vals, alpha);

        VecCL vector = new VecCL(getContext(), vals);
        VecCL result = subtr(vector);
        vector.release();

        return result;
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     * @param alpha Scalar
     */
    public VecCL mul (float alpha) {
        VecCL result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastSscal(size, alpha, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     * @param alpha Scalar
     */
    public VecCL div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     * @param y Vector
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
     * @return Euclidian norm
     */
    public float length () {
        CommandQueue queue = getQueue();
        FloatBuffer buffer = new FloatBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastSnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public VecCL norm () {
        return div(length());
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
