package Vector.Double;

import GPGPU.OpenCL.Buffer.ComplexBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Imaginary.Comp;
import Imaginary.Compf;
import Matrix.Double.MatCLi;
import References.Double.Complex.Ref1Di;
import Vector.Single.VecCLif;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class VecCLi extends ComplexBuffer {
    public VecCLi (Context context, int size) {
        super(context, size);
    }

    public VecCLi (Context context, Ref1Di values) {
        this(context, values.getSize());
        set(values.toArray());
    }

    public VecCLi (Context context, Comp... values) {
        this(context, values.length);
        set(values);
    }

    public VecCLi (int size) {
        super(Context.DEFAULT, size);
    }

    public VecCLi (Ref1Di values) {
        this(Context.DEFAULT, values);
    }

    public VecCLi (Comp... values) {
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
        CLBlast.CLBlastZaxpy(size, ComplexBuffer.getDoubles(alpha), this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Performs the operation y = alpha * x + y
     */
    public VecCLi add (double alpha, VecCLi y) {
        checkCompatibility(y);
        VecCLi result = y.clone();

        cl_event event = new cl_event();
        CLBlast.CLBlastZaxpy(size, new double[]{ alpha, 0 }, this.getId(), 0, 1, result.getId(), 0, 1, getQueue().id, event);

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
        vector.release();

        return result;
    }

    /**
     * Performs the operation y = x + alpha, in which x is a vector and alpha is a scalar.
     * @param alpha Scalar
     */
    public VecCLi add (double alpha) {
        Comp[] vals = new Comp[size];
        Arrays.fill(vals, new Comp(alpha, 0));

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
     * Performs the operation x = x - beta * y
     */
    public VecCLi subtr (double beta, VecCLi y) {
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
    public VecCLi subtr (double alpha) {
        return add(-alpha);
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
    public VecCLi mul (double alpha, VecCLi y) {
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
        CLBlast.CLBlastZscal(size, ComplexBuffer.getDoubles(alpha), result.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return result;
    }

    /**
     * Multiplies n elements of vector x by a scalar constant alpha.
     */
    public VecCLi mul (double alpha) {
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
    public VecCLi div (double alpha) {
        return mul(1 / alpha);
    }

    /**
     * Multiplies n elements of the vectors x and y element-wise and accumulates the results.
     */
    public Comp dot (VecCLi y) {
        checkCompatibility(y);
        CommandQueue queue = getQueue();
        ComplexBuffer buffer = new ComplexBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastZdotu(size, buffer.getId(), 0, this.getId(), 0, 1, y.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    /**
     * Accumulates the square of n elements in the x vector and takes the square root
     */
    public Comp magnitude () {
        CommandQueue queue = getQueue();
        ComplexBuffer buffer = new ComplexBuffer(queue, 1);

        cl_event event = new cl_event();
        CLBlast.CLBlastDznrm2(size, buffer.getId(), 0, this.getId(), 0, 1, queue.id, event);

        Query.awaitEvents(event);
        return buffer.get(0);
    }

    public Comp magnitude2 () {
        return dot(this);
    }

    /**
     * Normalized vector
     * @return Normalized vector
     */
    public VecCLi norm () {
        return div(magnitude());
    }

    /**
     * Represents vector values in form of an identity matrix
     */
    public MatCLi identityLike () {
        MatCLi matrix = new MatCLi(size, size);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(size, getId(), 0, 1, matrix.getId(), 0, size + 1, getQueue().id, event);

        Query.awaitEvents(event);
        return matrix;
    }

    @Override
    public VecCLif toFloat() {
        Comp[] values = toArray();
        Compf[] casted = new Compf[values.length];
        for (int i=0;i<casted.length;i++) {
            casted[i] = values[i].toFloat();
        }

        return new VecCLif(getContext(), casted);
    }

    public Veci toCPU () {
        return new Veci(toArray());
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
        CLBlast.CLBlastZcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
