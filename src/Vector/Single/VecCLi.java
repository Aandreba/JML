package Vector.Single;

import GPGPU.OpenCL.Buffer.CompfBuffer;
import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Complex.Compd;
import Complex.Comp;
import Matrix.Single.MatCLi;
import References.Single.Complex.Ref1Dif;
import Vector.Double.VecCLid;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class VecCLi extends CompfBuffer {
    public VecCLi(Context context, int size) {
        super(context, size);
    }

    public VecCLi(Context context, Ref1Dif values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCLi(Context context, Comp... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLi(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLi(Ref1Dif values) {
        this(Context.DEFAULT, values);
    }

    public VecCLi(Comp... values) {
        this(Context.DEFAULT, values);
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
        CLBlast.CLBlastCaxpy(size, CompfBuffer.getFloats(alpha), this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLi add (float alpha, VecCLi y) {
        checkCompatibility(y);
        VecCLi result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastCaxpy(size, new float[]{ alpha, 0 }, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y
     * @param y Vector
     */
    public VecCLi add (VecCLi y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCLi add (Comp alpha) {
        Comp[] vals = new Comp[size];
        Arrays.fill(vals, alpha);

        VecCLi vector = new VecCLi(getContext(), vals);
        VecCLi result = add(vector);
        vector.release(true);

        return result;
    }

    /**
     * Performs the operation y = x + alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCLi add (float alpha) {
        Comp[] vals = new Comp[size];
        Arrays.fill(vals, new Comp(alpha, 0));

        VecCLi vector = new VecCLi(getContext(), vals);
        VecCLi result = add(vector);
        vector.release(true);

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLi subtr (Comp beta, VecCLi y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLi subtr (float beta, VecCLi y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLi subtr (VecCLi y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLi subtr (Comp alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLi subtr (float alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLi invSubtr (Comp alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha - x
     */
    public VecCLi invSubtr (float alpha) {
        return mul(-1).add(alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLi mul (Comp alpha, VecCLi y) {
        checkCompatibility(y);
        MatCLi identity = identityLike();
        VecCLi result = identity.mul(alpha, y);

        identity.release(true);
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
        CLBlast.CLBlastCscal(size, CompfBuffer.getFloats(alpha), result.getId(), 0, 1, getQueue().id, event);

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
        CommandQueue queue = getQueue();
        CompfBuffer buffer = new CompfBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastCdotu(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, queue.id, event);

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
        CLBlast.CLBlastScnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

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
        CommandQueue queue = getQueue();
        CompfBuffer buffer = new CompfBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastScsum(size, buffer.getId(), 0, getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
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
        CLBlast.CLBlastCcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
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

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCLi clone() {
        VecCLi vector = new VecCLi(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
