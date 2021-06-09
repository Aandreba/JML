package org.jml.GPGPU.OpenCL.Buffer;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jocl.Pointer;
import org.jocl.cl_event;
import org.jocl.cl_mem;
import static org.jocl.CL.*;

public class Buffer {
    final protected int byteSize;
    protected cl_mem id;
    protected Context context;

    public Buffer (Context context, int byteSize) {
        this.context = context;
        this.byteSize = byteSize;

        this.id = clCreateBuffer(context.id, CL_MEM_READ_WRITE, byteSize, null, null);
    }

    public cl_mem getId () {
        return id;
    }

    public Context getContext () {
        return context;
    }

    public void setContext (Context context) {
        cl_mem buffer = clCreateBuffer(context.id, CL_MEM_READ_WRITE, byteSize, null, null);

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, buffer, true, 0, byteSize, Pointer.to(this.id), 0, null, null);

        Query.awaitEvents(event);
        this.context = context;
        this.id = buffer;
    }

    public void release () {
        clReleaseMemObject(this.id);
    }

    @Override
    public Buffer clone() {
        Buffer buffer = new Buffer(getContext(), byteSize);

        cl_event event = new cl_event();
        clEnqueueCopyBuffer(context.queue, getId(), buffer.getId(), 0, 0, byteSize, 0, null, event);

        Query.awaitEvents(event);
        return buffer;
    }
}
