package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Imaginary.Comp;
import References.Double.Complex.Ref1Di;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_event;

import static org.jocl.CL.*;

public class ComplexBuffer extends Buffer implements Ref1Di {
    final public int size;

    public ComplexBuffer (CommandQueue queue, int size) {
        super(queue, Sizeof.cl_double2 * size);
        this.size = size;
    }

    public ComplexBuffer (Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public ComplexBuffer (Context context, Comp... values) {
        this(context, values.length);
        set(values);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Comp get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        double[] array = new double[2];
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double2 * pos, Sizeof.cl_double2, Pointer.to(array), 0, null, null);

        return new Comp(array[0], array[1]);
    }

    public void set (int offset, Comp... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double2 * offset, Sizeof.cl_double2 * vals.length, Pointer.to(getDoubles(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (Comp... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(getDoubles(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, Comp val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double2 * pos, Sizeof.cl_double2, Pointer.to(getDoubles(val)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1Di values) {
        set(values.toArray());
    }

    @Override
    public Comp[] toArray() {
        double[] array = new double[2 * size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        Comp[] values = new Comp[size];
        for (int i=0;i<size;i++) {
            int j = 2 * i;
            values[i] = new Comp(array[j], array[j+1]);
        }

        return values;
    }

    public static double[] getDoubles (Comp... vals) {
        double[] values = new double[vals.length * 2];
        for (int i=0;i<vals.length;i++) {
            int j = 2 * i;
            values[j] = vals[i].real;
            values[j + 1] = vals[i].imaginary;
        }

        return values;
    }
}
