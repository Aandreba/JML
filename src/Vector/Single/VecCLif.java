package Vector.Single;

import GPGPU.OpenCL.Buffer.ComplexfBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Imaginary.Comp;
import Imaginary.Compf;
import Matrix.Single.MatCLif;
import References.Single.Complex.Ref1Dif;
import Vector.Double.VecCLi;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class VecCLif extends ComplexfBuffer {
    public VecCLif(Context context, int size) {
        super(context, size);
    }

    public VecCLif(Context context, Ref1Dif values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCLif(Context context, Compf... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLif(int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLif(Ref1Dif values) {
        this(Context.DEFAULT, values);
    }

    public VecCLif(Compf... values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (VecCLif b) {
        if (size != b.size || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLif add (Compf alpha, VecCLif y) {
        checkCompatibility(y);
        VecCLif result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastCaxpy(size, ComplexfBuffer.getFloats(alpha), this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLif add (float alpha, VecCLif y) {
        checkCompatibility(y);
        VecCLif result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastCaxpy(size, new float[]{ alpha, 0 }, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = x + y
     * @param y Vector
     */
    public VecCLif add (VecCLif y) {
        return add(1, y);
    }

    /**
     * Performs the operation y = x + alpha
     */
    public VecCLif add (Compf alpha) {
        Compf[] vals = new Compf[size];
        Arrays.fill(vals, alpha);

        VecCLif vector = new VecCLif(getContext(), vals);
        VecCLif result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation y = x + alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCLif add (float alpha) {
        Compf[] vals = new Compf[size];
        Arrays.fill(vals, new Compf(alpha, 0));

        VecCLif vector = new VecCLif(getContext(), vals);
        VecCLif result = add(vector);
        vector.release();

        return result;
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLif subtr (Compf beta, VecCLif y) {
        return y.add(beta.mul(-1), this);
    }

    /**
     * Performs the operation x = x - beta * y
     */
    public VecCLif subtr (float beta, VecCLif y) {
        return y.add(-beta, this);
    }

    /**
     * Performs the operation x = x - y
     */
    public VecCLif subtr (VecCLif y) {
        return subtr(1, y);
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLif subtr (Compf alpha) {
        return add(alpha.mul(-1));
    }

    /**
     * Performs the operation y = x - alpha
     */
    public VecCLif subtr (float alpha) {
        return add(-alpha);
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLif mul (Compf alpha, VecCLif y) {
        checkCompatibility(y);
        MatCLif identity = identityLike();
        VecCLif result = identity.mul(alpha, y);

        identity.release();
        return result;
    }

    /**
     * Performs the operation y = alpha * x * y
     */
    public VecCLif mul (float alpha, VecCLif y) {
        return mul(new Compf(alpha, 0), y);
    }

    /**
     * Performs the operation y = x * y
     */
    public VecCLif mul (VecCLif y) {
        return mul(1, y);
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLif mul (Compf alpha) {
        VecCLif result = clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastCscal(size, ComplexfBuffer.getFloats(alpha), result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLif mul (float alpha) {
        return mul(new Compf(alpha, 0));
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLif div (Compf alpha) {
        return mul(alpha.inverse());
    }

    /**
     * Divides n elements of vector x by a scalar constant alpha.
     */
    public VecCLif div (float alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public Compf dot (VecCLif y) {
        checkCompatibility(y);
        CommandQueue queue = getQueue();
        ComplexfBuffer buffer = new ComplexfBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastCdotu(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public Compf magnitude () {
        CommandQueue queue = getQueue();
        ComplexfBuffer buffer = new ComplexfBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastScnrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public Compf magnitude2 () {
        return dot(this);
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCLif norm () {
        return div(magnitude());
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCLif identityLike () {
        MatCLif matrix = new MatCLif(size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public VecCLi toDouble() {
        Compf[] values = toArray();
        Comp[] casted = new Comp[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toDouble();
        }

        return new VecCLi(getContext(), casted);
    }

    public Vecif toCPU () {
        return new Vecif(toArray());
    }

    @Override
    public String toString() {
        Compf[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    @Override
    public VecCLif clone() {
        VecCLif vector = new VecCLif(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
