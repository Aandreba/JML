package Vector.Double;

import GPGPU.OpenCL.Buffer.CompBuffer;
import GPGPU.OpenCL.Buffer.DoubleBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Complex.Compd;
import Complex.Comp;
import Matrix.Double.MatCLid;
import References.Double.Complex.Ref1Di;
import Vector.Single.VecCLi;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class VecCLid extends CompBuffer {
    public VecCLid(Context context, int size) {
        super(context, size);
    }

    public VecCLid(Context context, Ref1Di values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCLid(Context context, Compd... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLid(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLid(Ref1Di values) {
        this(Context.DEFAULT, values);
    }

    public VecCLid(Compd... values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (VecCLid b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLid add (Compd alpha, VecCLid y) {
        checkCompatibility(y);
        VecCLid result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastZaxpy(size, CompBuffer.getDoubles(alpha), this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLid add (double alpha, VecCLid y) {
        checkCompatibility(y);
        VecCLid result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastZaxpy(size, new double[]{ alpha, 0 }, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y
     * @param y Vector
     */
    public VecCLid add (VecCLid y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCLid add (Compd alpha) {
        Compd[] vals = new Compd[size];
        Arrays.fill(vals, alpha);

        VecCLid vector = new VecCLid(getContext(), vals);
        VecCLid result = add(vector);
        vector.release(true);

        return result;
    }

    /**
     * Performs the operation y = x + alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCLid add (double alpha) {
        Compd[] vals = new Compd[size];
        Arrays.fill(vals, new Compd(alpha, 0));

        VecCLid vector = new VecCLid(getContext(), vals);
        VecCLid result = add(vector);
        vector.release(true);

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLid subtr (Compd beta, VecCLid y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLid subtr (double beta, VecCLid y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLid subtr (VecCLid y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLid subtr (Compd alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLid subtr (double alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLid invSubtr (Compd alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLid invSubtr (double alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLid mul (Compd alpha, VecCLid y) {
        checkCompatibility(y);
        MatCLid identity = identityLike();
        VecCLid result = identity.mul(alpha, y);

        identity.release(true);
        return result;
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLid mul (double alpha, VecCLid y) {
        return mul(new Compd(alpha, 0), y);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCLid mul (VecCLid y) {
        return mul(1, y);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLid mul (Compd alpha) {
        VecCLid result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastZscal(size, CompBuffer.getDoubles(alpha), result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLid mul (double alpha) {
        return mul(new Compd(alpha, 0));
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLid div (Compd alpha) {
        return mul(alpha.inverse());
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLid div (double alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public Compd dot (VecCLid y) {
        checkCompatibility(y);
        CommandQueue queue = getQueue();
        CompBuffer buffer = new CompBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastZdotu(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public double magnitude () {
        CommandQueue queue = getQueue();
        DoubleBuffer buffer = new DoubleBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDznrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
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
    public VecCLid unit () {
        return div(magnitude());
    }

    public Compd sum () {
        CommandQueue queue = getQueue();
        CompBuffer buffer = new CompBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDzsum(size, buffer.getId(), 0, getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public Compd mean () {
        return sum().div(size);
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCLid identityLike () {
        MatCLid matrix = new MatCLid(getContext(), size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public VecCLi toFloat() {
        Compd[] values = toArray();
        Comp[] casted = new Comp[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toFloat();
        }

        return new VecCLi(getContext(), casted);
    }

    public Vecid toCPU () {
        return new Vecid(toArray());
    }

    public MatCLid rowMatrix () {
        return new MatCLid(this.clone(), size);
    }

    public MatCLid colMatrix () {
        return new MatCLid(this.clone(), 1);
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
    public VecCLid clone() {
        VecCLid vector = new VecCLid(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
