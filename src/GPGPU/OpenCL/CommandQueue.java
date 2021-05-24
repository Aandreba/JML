package GPGPU.OpenCL;

import org.jocl.*;
import java.util.HashMap;
import static org.jocl.CL.*;

public class CommandQueue {
    final private static HashMap<Context, CommandQueue> open = new HashMap<>();

    final public cl_command_queue id;
    final public Context context;
    final private cl_queue_properties properties;

    private CommandQueue (Context context) {
        this.context = context;
        this.properties = new cl_queue_properties();
        this.id = clCreateCommandQueueWithProperties(context.id, context.device.id, this.properties, null);
        open.put(context, this);
    }

    public static CommandQueue get (Context ctx) {
        return open.getOrDefault(ctx, new CommandQueue(ctx));
    }

    public static CommandQueue get (Device device) {
        return get(Context.get(device));
    }

    public static boolean release (CommandQueue queue) {
        if (clReleaseCommandQueue(queue.id) == 1) {
            open.remove(queue.context);
            return true;
        } else {
            return false;
        }
    }
}
