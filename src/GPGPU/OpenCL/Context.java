package GPGPU.OpenCL;

import org.jocl.*;
import java.util.HashMap;
import static org.jocl.CL.*;

public class Context {
    final private static HashMap<Device, Context> open = new HashMap<>();

    final public cl_context id;
    final public Device device;
    final private cl_context_properties properties;

    private Context (Device device) {
        this.device = device;

        this.properties = new cl_context_properties();
        this.properties.addProperty(CL_CONTEXT_PLATFORM, device.platform.id);

        this.id = clCreateContext(this.properties, 1, new cl_device_id[]{ this.device.id }, null, null, null);
        open.put(device, this);
    }

    public static Context get (Device device) {
        return open.getOrDefault(device, new Context(device));
    }

    public static boolean release (Context context) {
        if (clReleaseContext(context.id) == 1) {
            open.remove(context.device);
            return true;
        } else {
            return false;
        }
    }
}
