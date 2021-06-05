package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Complex.Compd;
import References.Double.Complex.Ref1Did;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

import static org.jocl.CL.*;

public class CompBuffer extends Buffer implements Ref1Did {
    final public int size;

    public CompBuffer(CommandQueue queue, int size) {
        super(queue, Sizeof.cl_double2 * size);
        this.size = size;
    }

    public CompBuffer(Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public CompBuffer(Context context, Compd... values) {
        this(context, values.length);
        set(values);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Compd get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        double[] array = new double[2];
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double2 * pos, Sizeof.cl_double2, Pointer.to(array), 0, null, null);

        return new Compd(array[0], array[1]);
    }

    public void set (int offset, Compd... vals) {
        if (offset < 0 || offset + vals.length >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double2 * offset, Sizeof.cl_double2 * vals.length, Pointer.to(getDoubles(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (Compd... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(getDoubles(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, Compd val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double2 * pos, Sizeof.cl_double2, Pointer.to(getDoubles(val)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1Did values) {
        set(values.toArray());
    }

    @Override
    public Compd[] toArray() {
        double[] array = new double[2 * size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        Compd[] values = new Compd[size];
        for (int i=0;i<size;i++) {
            int j = 2 * i;
            values[i] = new Compd(array[j], array[j+1]);
        }

        return values;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public CompBuffer clone() {
        CompBuffer vector = new CompBuffer(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }

    public static double[] getDoubles (Compd... vals) {
        double[] values = new double[vals.length * 2];
        for (int i=0;i<vals.length;i++) {
            int j = 2 * i;
            values[j] = vals[i].real;
            values[j + 1] = vals[i].imaginary;
        }

        return values;
    }
}
