package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import References.Single.Ref1Df;
import org.jocl.*;
import static org.jocl.CL.*;

public class FloatBuffer implements Ref1Df {
    final public int size;
    final private int byteSize;
    private cl_mem id;
    private CommandQueue queue;

    public FloatBuffer (CommandQueue queue, int size) {
        this.size = size;
        this.byteSize = Sizeof.cl_float * size;
        this.queue = queue;
        this.id = clCreateBuffer(queue.context.id, CL_MEM_READ_WRITE, byteSize, null, null);
    }

    public FloatBuffer (Context context, int size) {
        this(new CommandQueue(context), size);
    }

    public cl_mem getId () {
        return id;
    }

    public CommandQueue getQueue () {
        return queue;
    }

    public void setQueue (CommandQueue queue) {
        cl_mem buffer = clCreateBuffer(queue.context.id, CL_MEM_READ_WRITE, byteSize, null, null);

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(queue.id, buffer, true, 0, byteSize, Pointer.to(toArray()), 0, null, null);

        Query.awaitEvents(event);
        this.queue = queue;
        this.id = buffer;
    }

    public Context getContext () {
        return queue.context;
    }

    public void setContext (Context context) {
        setQueue(new CommandQueue(context));
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
    public void set (Ref1Df values) {
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

    public void release () {
        clReleaseMemObject(this.id);
        queue.release();
    }
}
