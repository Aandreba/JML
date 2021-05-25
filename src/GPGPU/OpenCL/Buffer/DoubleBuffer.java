package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import References.Double.Ref1D;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_event;

import static org.jocl.CL.*;

public class DoubleBuffer extends Buffer implements Ref1D {
    final public int size;

    public DoubleBuffer (CommandQueue queue, int size) {
        super(queue, Sizeof.cl_double * size);
        this.size = size;
    }

    public DoubleBuffer (Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public DoubleBuffer (Context context, double... values) {
        this(context, values.length);
        set(values);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public double get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        double[] array = new double[1];
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double * pos, Sizeof.cl_double, Pointer.to(array), 0, null, null);

        return array[0];
    }

    public void set (int offset, double... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double * offset, Sizeof.cl_double * vals.length, Pointer.to(vals), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (double... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(vals), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, double val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_double * pos, Sizeof.cl_double, Pointer.to(new double[]{ val }), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1D values) {
        set(values.toArray());
    }

    @Override
    public double[] toArray() {
        double[] array = new double[size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        return array;
    }
}
