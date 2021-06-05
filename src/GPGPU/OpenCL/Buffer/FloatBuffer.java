package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import References.Single.Ref1D;
import org.jocl.*;
import org.jocl.blast.CLBlast;

import java.util.Arrays;

import static org.jocl.CL.*;

public class FloatBuffer extends Buffer implements Ref1D {
    final public int size;

    public FloatBuffer (CommandQueue queue, int size) {
        super(queue, Sizeof.cl_float * size);
        this.size = size;
    }

    public FloatBuffer (Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public FloatBuffer (Context context, float... values) {
        this(context, values.length);
        set(values);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public float get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        float[] array = new float[1];
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float * pos, Sizeof.cl_float, Pointer.to(array), 0, null, null);

        return array[0];
    }

    public void set (int len, int offsetX, int incX, FloatBuffer y, int offsetY, int incY) {
        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(len, y.id, offsetY, incY, getId(), offsetX, incX, queue.id, event);

        Query.awaitEvents(event);
    }

    public void set (int offset, float... vals) {
        if (offset < 0 || offset + vals.length >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float * offset, Sizeof.cl_float * vals.length, Pointer.to(vals), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (float... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(vals), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (int pos, float val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, this.id, CL_TRUE, Sizeof.cl_float * pos, Sizeof.cl_float, Pointer.to(new float[]{ val }), 0, null, event);
        Query.awaitEvents(event);
    }

    @Override
    public void set (Ref1D values) {
        set(values.toArray());
    }

    @Override
    public float[] toArray() {
        float[] array = new float[size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(queue.id, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        return array;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public FloatBuffer clone() {
        FloatBuffer vector = new FloatBuffer(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
