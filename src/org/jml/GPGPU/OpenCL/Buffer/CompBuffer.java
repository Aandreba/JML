package org.jml.GPGPU.OpenCL.Buffer;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Complex.Single.Comp;
import org.jml.References.Single.Complex.Ref1Di;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

import static org.jocl.CL.*;

public class CompBuffer extends Buffer implements Ref1Di {
    final public int size;

    public CompBuffer(Context context, int size) {
        super(context, Sizeof.cl_float2 * size);
        this.size = size;
    }

    public CompBuffer(Context context, Comp... values) {
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

        float[] array = new float[2];
        clEnqueueReadBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_float2 * pos, Sizeof.cl_float2, Pointer.to(array), 0, null, null);

        return new Comp(array[0], array[1]);
    }

    public void set (int len, CompBuffer y, int offsetY, int incY, int offsetX, int incX) {
        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(len, y.id, offsetY, incY, getId(), offsetX, incX, context.queue, event);

        Query.awaitEvents(event);
    }

    public void set (int offset, Comp... vals) {
        if (offset < 0 || offset + vals.length >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_float2 * offset, Sizeof.cl_float2 * vals.length, Pointer.to(getFloats(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (Comp... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, 0, byteSize, Pointer.to(getFloats(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, Comp val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_float2 * pos, Sizeof.cl_float2, Pointer.to(getFloats(val)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1Di values) {
        set(values.toArray());
    }

    @Override
    public Comp[] toArray() {
        float[] array = new float[2 * size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(context.queue, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        Comp[] values = new Comp[size];
        for (int i=0;i<size;i++) {
            int j = 2 * i;
            values[i] = new Comp(array[j], array[j+1]);
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
        CLBlast.CLBlastCcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return vector;
    }

    public static float[] getFloats (Comp... vals) {
        float[] values = new float[vals.length * 2];
        for (int i=0;i<vals.length;i++) {
            int j = 2 * i;
            values[j] = vals[i].real;
            values[j + 1] = vals[i].imaginary;
        }

        return values;
    }
}
