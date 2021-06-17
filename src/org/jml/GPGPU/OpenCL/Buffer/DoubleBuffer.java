package org.jml.GPGPU.OpenCL.Buffer;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.References.Double.Ref1Dd;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

import static org.jocl.CL.*;

public class DoubleBuffer extends Buffer implements Ref1Dd {
    final public int size;

    public DoubleBuffer (Context context, int size) {
        super(context, Sizeof.cl_double * size);
        this.size = size;
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
        cl_event event = new cl_event();
        clEnqueueReadBuffer(context.queue, getId(), true, Sizeof.cl_double * pos, Sizeof.cl_double, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        return array[0];
    }

    public void set (int offset, double... vals) {
        if (offset < 0 || offset + vals.length >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_double * offset, Sizeof.cl_double * vals.length, Pointer.to(vals), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (int len, DoubleBuffer y, int offsetY, int incY, int offsetX, int incX) {
        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(len, y.id, offsetY, incY, getId(), offsetX, incX, context.queue, event);

        Query.awaitEvents(event);
    }

    public void set (double... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, 0, byteSize, Pointer.to(vals), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, double val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_double * pos, Sizeof.cl_double, Pointer.to(new double[]{ val }), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1Dd values) {
        set(values.toArray());
    }

    @Override
    public double[] toArray() {
        double[] array = new double[size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(context.queue, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        return array;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public DoubleBuffer clone() {
        DoubleBuffer vector = new DoubleBuffer(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastDcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return vector;
    }
}