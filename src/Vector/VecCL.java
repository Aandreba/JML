package Vector;

import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Device;
import References.Single.Ref1Df;
import org.jocl.*;

import static org.jocl.CL.*;

public class VecCL implements Ref1Df {
    final private int size;
    final private int floatSize;
    final private cl_mem pointer;
    private Device device;

    public VecCL (Device device, int size) {
        Context ctx = Context.get(device);

        this.device = device;
        this.size = size;
        this.floatSize = Sizeof.cl_float * size;
        this.pointer = clCreateBuffer(ctx.id, CL_MEM_READ_WRITE, floatSize, null, null);
    }

    public VecCL (int size) {
        this(Device.DEFAULT, size);
    }

    public VecCL (Device device, Vecf values) {
        this(device, values.getSize());
        set(values.toArray());
    }

    public VecCL (Vecf values) {
        this(Device.DEFAULT, values);
    }

    public VecCL (Device device, float... values) {
        this(device, values.length);
        set(values);
    }

    public VecCL (float... values) {
        this(Device.DEFAULT, values);
    }

    public Device getDevice () {
        return device;
    }

    public void setDevice (Device device) { // TODO
        this.device = device;
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
        CommandQueue queue = CommandQueue.get(device);
        clEnqueueReadBuffer(queue.id, this.pointer, CL_TRUE, Sizeof.cl_float * pos, Sizeof.cl_float, Pointer.to(array), 0, null, null);

        return array[0];
    }

    public void set (float... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        CommandQueue queue = CommandQueue.get(device);
        clEnqueueWriteBuffer(queue.id, this.pointer, CL_TRUE, 0, floatSize, Pointer.to(vals), 0, null, null);
    }

    @Override
    public void set (int pos, float val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        CommandQueue queue = CommandQueue.get(device);
        clEnqueueWriteBuffer(queue.id, this.pointer, CL_TRUE, Sizeof.cl_float * pos, Sizeof.cl_float, Pointer.to(new float[]{ val }), 0, null, null);
    }

    @Override
    public void set (Ref1Df values) {
        set(values.toArray());
    }

    @Override
    public float[] toArray() {
        float[] array = new float[size];
        CommandQueue queue = CommandQueue.get(device);
        clEnqueueReadBuffer(queue.id, this.pointer, CL_TRUE, 0, floatSize, Pointer.to(array), 0, null, null);

        return array;
    }

    @Override
    public String toString() {
        float[] vals = toArray();
        StringBuilder builder = new StringBuilder();

        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(vals[i]);
        }

        return "{ " + builder.substring(2) + " }";
    }

    public void release () {
        clReleaseMemObject(this.pointer);
    }
}
