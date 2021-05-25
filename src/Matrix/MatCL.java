package Matrix;

import GPGPU.OpenCL.Buffer.FloatBuffer;
import GPGPU.OpenCL.CommandQueue;
import GPGPU.OpenCL.Context;
import GPGPU.OpenCL.Query;
import Vector.VecCL;
import Vector.Vecf;
import org.jocl.blast.CLBlast;
import org.jocl.cl_event;

import java.util.Arrays;

public class MatCL extends FloatBuffer {
    final int rows, cols;
    public MatCL(Context context, int rows, int cols) {
        super(context, rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    public MatCL(Context context, Matf values) {
        this(context, values.getRows(), values.getCols());
        set(values.colMajor().toArray());
    }

    public MatCL(int size) {
        super(Context.DEFAULT, size);
    }

    public MatCL(Matf values) {
        this(Context.DEFAULT, values);
    }

    private void checkCompatibility (MatCL b) {
        if (rows != b.rows || cols != b.cols || !getContext().equals(b.getContext())) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation y = alpha * A * x + beta * y, in which x is an input vector, y is an input and output vector, A is an input matrix, and alpha and beta are scalars.
     * @param y
     * @return
     */
    public VecCL mul (float alpha, float beta, VecCL y) {
        // TODO

        return null;
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

    @Override
    public MatCL clone() {
        MatCL vector = new MatCL(getContext(), size);

        cl_event event = new cl_event();
        CLBlast.CLBlastScopy(size, this.getId(), 0, 1, vector.getId(), 0, 1, getQueue().id, event);

        Query.awaitEvents(event);
        return vector;
    }
}
