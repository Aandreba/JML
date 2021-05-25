package GPGPU.OpenCL;

import org.jocl.*;
import java.util.HashMap;
import static org.jocl.CL.*;

public class CommandQueue {
    final public cl_command_queue id;
    final public Context context;
    final private cl_queue_properties properties;

    public CommandQueue (Context context) {
        this.context = context;
        this.properties = new cl_queue_properties();

        cl_command_queue queue;
        try {
            queue = clCreateCommandQueueWithProperties(context.id, context.device.id, this.properties, null);
        } catch (Exception e) {
            queue = clCreateCommandQueue(context.id, context.device.id, 0, null);
        }

        this.id = queue;
    }

    @Override
    public String toString() {
        return "CommandQueue {" +
                "id=" + id +
                ", context=" + context +
                ", properties=" + properties +
                '}';
    }

    public void release () {
        clReleaseCommandQueue(this.id);
    }
}
