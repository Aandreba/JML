package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Imaginary.Compf;
import References.Single.Complex.Ref1Dif;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_event;

import static org.jocl.CL.*;

public class ComplexfBuffer extends Buffer implements Ref1Dif {
    final public int size;

    public ComplexfBuffer(CommandQueue queue, int size) {
        super(queue, Sizeof.cl_float2 * size);
        this.size = size;
    }

    public ComplexfBuffer(Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public ComplexfBuffer(Context context, Compf... values) {
        this(context, values.length);
        set(values);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Compf get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        float[] array = new float[2];
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float2 * pos, Sizeof.cl_float2, Pointer.to(array), 0, null, null);

        return new Compf(array[0], array[1]);
    }

    public void set (int offset, Compf... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float2 * offset, Sizeof.cl_float2 * vals.length, Pointer.to(getFloats(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (Compf... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(getFloats(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, Compf val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float2 * pos, Sizeof.cl_float2, Pointer.to(getFloats(val)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1Dif values) {
        set(values.toArray());
    }

    @Override
    public Compf[] toArray() {
        float[] array = new float[2 * size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        Compf[] values = new Compf[size];
        for (int i=0;i<size;i++) {
            int j = 2 * i;
            values[i] = new Compf(array[j], array[j+1]);
        }

        return values;
    }

    public static float[] getFloats (Compf... vals) {
        float[] values = new float[vals.length * 2];
        for (int i=0;i<vals.length;i++) {
            int j = 2 * i;
            values[j] = vals[i].real;
            values[j + 1] = vals[i].imaginary;
        }

        return values;
    }
}
