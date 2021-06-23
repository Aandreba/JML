package org.jml.Vector.Single;

import org.jml.GPGPU.OpenCL.Buffer.CompBuffer;
import org.jml.GPGPU.OpenCL.Buffer.FloatBuffer;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Matrix.Single.MatCLi;
import org.jml.Vector.Double.VecCLid;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;
import java.util.Arrays;

public class VecCLi extends CompBuffer {
    public VecCLi(Context context, int size) {
        super(context, size);
    }

    public VecCLi(Context context, Veci values) {
        this(context, values.size());
        set(values.toArray());
    }

    public VecCLi(Context context, Comp... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLi(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLi(Veci values) {
        this(Context.DEFAULT, values);
    }

    public VecCLi(Comp... values) {
        this(Context.DEFAULT, values);
    }

    public VecCLi (CompBuffer buffer) {
        this(buffer.getContext(), buffer.size);
        set(buffer.size, buffer, 0, 1, 0, 1);
    }

    private void checkCompatibility (VecCLi b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLi add (Comp alpha, VecCLi y) {
        checkCompatibility(y);
        VecCLi result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastCaxpy(size, CompBuffer.getFloats(alpha), this.getId(), 0, 1, result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y
     * @param y Vector
     */
    public VecCLi add (VecCLi y) {
        return add(Comp.ONE, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCLi add (Comp alpha) {
        Comp[] vals = new Comp[size];
        Arrays.fill(vals, alpha);

        VecCLi vector = new VecCLi(getContext(), vals);
        VecCLi result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLi subtr (Comp beta, VecCLi y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLi subtr (VecCLi y) {
        return subtr(Comp.ONE, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLi subtr (Comp alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLi invSubtr (Comp alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLi mul (Comp alpha, VecCLi y) {
        checkCompatibility(y);
        MatCLi identity = identityLike();
        VecCLi result = identity.mul(alpha, y);

        identity.release();
        return result;
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLi mul (float alpha, VecCLi y) {
        return mul(new Comp(alpha, 0), y);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCLi mul (VecCLi y) {
        return mul(1, y);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLi mul (Comp alpha) {
        VecCLi result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastCscal(size, CompBuffer.getFloats(alpha), result.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLi mul (float alpha) {
        return mul(new Comp(alpha, 0));
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLi div (Comp alpha) {
        return mul(alpha.inverse());
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLi div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public Comp dot (VecCLi y) {
        checkCompatibility(y);
        Context context = getContext();
        CompBuffer buffer = new CompBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastCdotu(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public float magnitude () {
        Context context = getContext();
        FloatBuffer buffer = new FloatBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastScnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return buffer.get(0);
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
    public VecCLi unit () {
        return div(magnitude());
    }

    public Comp sum () {
        Context context = getContext();
        CompBuffer buffer = new CompBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastScsum(size, buffer.getId(), 0, getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        Comp result = buffer.get(0);
        buffer.release();

        return result;
    }

    public Comp asum () {
        Context context = getContext();
        CompBuffer buffer = new CompBuffer(context, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastScasum(size, buffer.getId(), 0, getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        Comp result = buffer.get(0);
        buffer.release();

        return result;
    }

    public Comp mean () {
        return sum().div(size);
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCLi identityLike () {
        MatCLi matrix = new MatCLi(getContext(), size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getContext().queue, event);

        Query.awaitEvents(event);
        return matrix;
    }

    public VecCLid toDouble() {
        Comp[] values = toArray();
        Compd[] casted = new Compd[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toDouble();
        }

        return new VecCLid(getContext(), casted);
    }

    public Veci toCPU () {
        return new Veci(toArray());
    }

    public MatCLi rowMatrix () {
        return new MatCLi(this.clone(), size);
    }

    public MatCLi colMatrix () {
        return new MatCLi(this.clone(), 1);
    }

    @Override
    public String toString() {
        Comp[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i< size(); i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCLi clone() {
        VecCLi vector = new VecCLi(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getContext().queue, event);

        Query.awaitEvents(event);
        return vector;
    }
}
