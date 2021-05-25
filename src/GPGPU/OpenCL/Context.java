package GPGPU.OpenCL;

import org.jocl.*;
import java.util.HashMap;
import java.util.Objects;

import static org.jocl.CL.*;

public class Context {
    final public static Context DEFAULT = new Context(Device.DEFAULT);

    final public cl_context id;
    final public Device device;
    final private cl_context_properties properties;

    public Context (Device device) {
        this.device = device;

        this.properties = new cl_context_properties();
        this.properties.addProperty(CL_CONTEXT_PLATFORM, device.platform.id);

        this.id = clCreateContext(this.properties, 1, new cl_device_id[]{ this.device.id }, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return Objects.equals(id, context.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Context {" +
                "id=" + id +
                ", device='" + device.name + '\'' +
                ", properties=" + properties +
                '}';
    }

    public void release () {
        clReleaseContext(this.id);
    }
}
