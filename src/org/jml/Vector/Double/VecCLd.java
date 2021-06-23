package org.jml.Vector.Double;

import org.jml.GPGPU.OpenCL.Buffer.DoubleBuffer;
import org.jml.GPGPU.OpenCL.Buffer.FloatBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Matrix.Double.MatCLd;
import org.jml.Vector.Single.VecCL;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class VecCLd extends DoubleBuffer {
    public VecCLd(Context context, int size) {
        super(context, size);
    }

    public VecCLd(Context context, Vecd values) {
        this(context, values.size());
        set(values.toArray());
    }

    public VecCLd(Context context, double... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLd(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLd(Vecd values) {
        this(Context.DEFAULT, values);
    }

    public VecCLd(double... values) {
        this(Context.DEFAULT, values);
    }

    public VecCLd (DoubleBuffer buffer) {
        this(buffer.getContext(), buffer.size);
        set(buffer.size, buffer, 0, 1, 0, 1);
    }

    private void checkCompatibility (VecCLd b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     * @param alpha Scalar multiplier
     * @param y Vector
     */
    public VecCLd add (double alpha, VecCLd y) {
        checkCompatibility(y);
        VecCLd result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastDaxpy(size, alpha, this.getId(), 0, 1, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y, in which x and y are vectors.
     * @param y Vector
     */
    public VecCLd add (VecCLd y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCLd add (double alpha) {
        double[] vals = new double[size];
        Arrays.fill(vals, alpha);

        VecCLd vector = new VecCLd(getContext(), vals);
        VecCLd result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLd subtr (double beta, VecCLd y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLd subtr (VecCLd y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLd subtr (double alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLd invSubtr (float alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLd mul (double alpha, VecCLd y) {
        checkCompatibility(y);
        MatCLd identity = identityLike();
        VecCLd result = identity.mul(alpha, y);

        identity.release();
        return result;
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCLd mul (VecCLd y) {
        return mul(1, y);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     * @param alpha Scalar
     */
    public VecCLd mul (double alpha) {
        VecCLd result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastDscal(size, alpha, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     * @param alpha Scalar
     */
    public VecCLd div (double alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     * @param y Vector
     */
    public double dot (VecCLd y) {
        checkCompatibility(y);
        Context context = getContext();
        FloatBuffer buffer = new FloatBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDdot(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public double magnitude() {
        Context context = getContext();
        DoubleBuffer buffer = new DoubleBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, context.queue, event);

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
    public VecCLd unit () {
        return div(magnitude());
    }

    public double sum () {
        Context context = getContext();
        DoubleBuffer buffer = new DoubleBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDsum(size, buffer.getId(), 0, getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public double asum () {
        Context context = getContext();
        DoubleBuffer buffer = new DoubleBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDasum(size, buffer.getId(), 0, getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public double mean () {
        return sum() / size;
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCLd identityLike () {
        MatCLd matrix = new MatCLd(getContext(), size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public VecCL toFloat() {
        double[] values = toArray();
        float[] casted = new float[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = (float) values[i];
        }

        return new VecCL(getContext(), casted);
    }

    public Vecd toCPU () {
        return new Vecd(toArray());
    }

    public MatCLd rowMatrix () {
        return new MatCLd(this.clone(), size);
    }

    public MatCLd colMatrix () {
        return new MatCLd(this.clone(), 1);
    }

    @Override
    public String toString() {
        double[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i< size(); i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCLd clone() {
        VecCLd vector = new VecCLd(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return vector;
    }
}
