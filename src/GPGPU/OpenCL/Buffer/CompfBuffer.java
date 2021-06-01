package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Complex.Comp;
import References.Single.Complex.Ref1Dif;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

import static org.jocl.CL.*;

public class CompfBuffer extends Buffer implements Ref1Dif {
    final public int size;

    public CompfBuffer(CommandQueue queue, int size) {
        super(queue, Sizeof.cl_float2 * size);
        this.size = size;
    }

    public CompfBuffer(Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public CompfBuffer(Context context, Comp... values) {
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
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float2 * pos, Sizeof.cl_float2, Pointer.to(array), 0, null, null);

        return new Comp(array[0], array[1]);
    }

    public void set (int offset, Comp... vals) {
        if (offset < 0 || offset + vals.length >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float2 * offset, Sizeof.cl_float2 * vals.length, Pointer.to(getFloats(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (Comp... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(getFloats(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, Comp val) {
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
    public Comp[] toArray() {
        float[] array = new float[2 * size];

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

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public CompfBuffer clone() {
        CompfBuffer vector = new CompfBuffer(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastCcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

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
