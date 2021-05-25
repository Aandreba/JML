package GPGPU.OpenCL.Buffer;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import org.jocl.Pointer;
import org.jocl.cl_event;
import org.jocl.cl_mem;

import static org.jocl.CL.*;

public class Buffer {
    final protected int byteSize;
    protected cl_mem id;
    protected CommandQueue queue;

    public Buffer (CommandQueue queue, int byteSize) {
        this.byteSize = byteSize;
        this.queue = queue;
        this.id = clCreateBuffer(queue.context.id, CL_MEM_READ_WRITE, byteSize, null, null);
    }

    public Buffer (Context context, int size) {
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
        clEnqueueWriteBuffer(queue.id, buffer, true, 0, byteSize, Pointer.to(this.id), 0, null, null);

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

    public void release () {
        clReleaseMemObject(this.id);
        queue.release();
    }
}
