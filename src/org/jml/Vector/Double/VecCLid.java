package org.jml.Vector.Double;

import org.jml.GPGPU.OpenCL.Buffer.CompdBuffer;
import org.jml.GPGPU.OpenCL.Buffer.DoubleBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Matrix.Double.MatCLid;
import org.jml.Vector.Single.VecCLi;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;
import java.util.Arrays;

public class VecCLid extends CompdBuffer {
    public VecCLid(Context context, int size) {
        super(context, size);
    }

    public VecCLid(Context context, Vecid values) {
        this(context, values.size());
        set(values.toArray());
    }

    public VecCLid(Context context, Compd... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLid(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLid(Vecid values) {
        this(Context.DEFAULT, values);
    }

    public VecCLid(Compd... values) {
        this(Context.DEFAULT, values);
    }

    public VecCLid (CompdBuffer buffer) {
        this(buffer.getContext(), buffer.size);
        set(buffer.size, buffer, 0, 1, 0, 1);
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
        CLBlast.CLBlastZaxpy(size, CompdBuffer.getDoubles(alpha), this.getId(), 0, 1, result.getId(), 0, 1, getContext().queue, event);

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
        CLBlast.CLBlastZaxpy(size, new double[]{ alpha, 0 }, this.getId(), 0, 1, result.getId(), 0, 1, getContext().queue, event);

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
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLid subtr (Compd beta, VecCLid y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLid subtr (VecCLid y) {
        return subtr(Compd.ONE, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLid subtr (Compd alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLid invSubtr (Compd alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLid mul (Compd alpha, VecCLid y) {
        checkCompatibility(y);
        MatCLid identity = identityLike();
        VecCLid result = identity.mul(alpha, y);

        identity.release();
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
        CLBlast.CLBlastZscal(size, CompdBuffer.getDoubles(alpha), result.getId(), 0, 1, getContext().queue, event);

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
        Context context = getContext();
        CompdBuffer buffer = new CompdBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastZdotu(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public double magnitude () {
        Context context = getContext();
        DoubleBuffer buffer = new DoubleBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDznrm2(size, buffer.getId(), 0, this.getId(), 0, 1, context.queue, event);

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
        Context context = getContext();
        CompdBuffer buffer = new CompdBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDzsum(size, buffer.getId(), 0, getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        Compd result = buffer.get(0);
        buffer.release();

        return result;
    }

    public Compd asum () {
        Context context = getContext();
        CompdBuffer buffer = new CompdBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDzasum(size, buffer.getId(), 0, getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        Compd result = buffer.get(0);
        buffer.release();

        return result;
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
        CLBlast.CLBlastZcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }

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

        for (int i = 0; i< size(); i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCLid clone() {
        VecCLid vector = new VecCLid(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return vector;
    }
}
