package Vector.Single;

import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Matrix.Single.MatCLf;
import References.Single.Ref1Df;
import Vector.Double.VecCL;
import org.jocl.*;
import org.jocl.blast.CLBlast;
import java.util.Arrays;

public class VecCLf extends FloatBuffer {
    public VecCLf(Context context, int size) {
        super(context, size);
    }

    public VecCLf(Context context, Ref1Df values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCLf(Context context, float... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLf(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLf(Ref1Df values) {
        this(Context.DEFAULT, values);
    }

    public VecCLf(float... values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (VecCLf b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLf add (float alpha, VecCLf y) {
        checkCompatibility(y);
        VecCLf result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastSaxpy(size, alpha, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y
     */
    public VecCLf add (VecCLf y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCLf add (float alpha) {
        float[] vals = new float[size];
        Arrays.fill(vals, alpha);

        VecCLf vector = new VecCLf(getContext(), vals);
        VecCLf result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLf subtr (float beta, VecCLf y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLf subtr (VecCLf y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLf subtr (float alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLf mul (float alpha, VecCLf y) {
        checkCompatibility(y);
        MatCLf identity = identityLike();
        VecCLf result = identity.mul(alpha, y);

        identity.release();
        return result;
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCLf mul (VecCLf y) {
        return mul(1, y);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLf mul (float alpha) {
        VecCLf result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastSscal(size, alpha, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLf div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public float dot (VecCLf y) {
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
    public VecCLf norm () {
        return div(magnitude());
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCLf identityLike () {
        MatCLf matrix = new MatCLf(size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public VecCL toDouble() {
        float[] values = toArray();
        double[] casted = new double[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i];
        }

        return new VecCL(getContext(), casted);
    }

    public Vecf toCPU () {
        return new Vecf(toArray());
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
    public VecCLf clone() {
        VecCLf vector = new VecCLf(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
