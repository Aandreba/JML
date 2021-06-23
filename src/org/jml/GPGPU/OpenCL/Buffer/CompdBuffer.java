package org.jml.GPGPU.OpenCL.Buffer;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.GPGPU.OpenCL.Query;
import org.jml.Complex.Double.Compd;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.Vecid;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;
import java.util.Arrays;

import static org.jocl.CL.*;

public class CompdBuffer extends Buffer {
    final public int size;

    public CompdBuffer(Context context, int size) {
        super(context, Sizeof.cl_double2 * size);
        this.size = size;
    }

    public CompdBuffer(Context context, Compd... values) {
        this(context, values.length);
        set(values);
    }

    public int size () {
        return size;
    }

    
    public Compd get (int pos) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        double[] array = new double[2];
        clEnqueueReadBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_double2 * pos, Sizeof.cl_double2, Pointer.to(array), 0, null, null);

        return new Compd(array[0], array[1]);
    }

    public void set (int len, CompdBuffer y, int offsetY, int incY, int offsetX, int incX) {
        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(len, y.id, offsetY, incY, getId(), offsetX, incX, context.queue, event);

        Query.awaitEvents(event);
    }

    public void set (int offset, Compd... vals) {
        if (offset < 0 || offset + vals.length >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_double2 * offset, Sizeof.cl_double2 * vals.length, Pointer.to(getDoubles(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    public void set (Compd... vals) {
        if (vals.length != size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, 0, byteSize, Pointer.to(getDoubles(vals)), 0, null, event);
        Query.awaitEvents(event);
    }

    
    public void set (int pos, Compd val) {
        if (pos < 0 || pos >= size) {
            throw new IllegalArgumentException();
        }

        cl_event event = new cl_event();
        clEnqueueWriteBuffer(context.queue, this.id, CL_TRUE, Sizeof.cl_double2 * pos, Sizeof.cl_double2, Pointer.to(getDoubles(val)), 0, null, event);
        Query.awaitEvents(event);
    }
    
    public void set (Vecid values) {
        set(values.toArray());
    }

    public Compd[] toArray() {
        double[] array = new double[2 * size];

        cl_event event = new cl_event();
        clEnqueueReadBuffer(context.queue, this.id, CL_TRUE, 0, byteSize, Pointer.to(array), 0, null, event);

        Query.awaitEvents(event);
        Compd[] values = new Compd[size];
        for (int i=0;i<size;i++) {
            int j = 2 * i;
            values[i] = new Compd(array[j], array[j+1]);
        }

        return values;
    }

    
    public String toString() {
        return Arrays.toString(toArray());
    }

    
    public CompdBuffer clone() {
        CompdBuffer vector = new CompdBuffer(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastZcopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, context.queue, event);

        Query.awaitEvents(event);
        return vector;
    }

    public static double[] getDoubles (Compd... vals) {
        double[] values = new double[vals.length * 2];
        for (int i=0;i<vals.length;i++) {
            int j = 2 * i;
            values[j] = vals[i].real;
            values[j + 1] = vals[i].imaginary;
        }

        return values;
    }
}
