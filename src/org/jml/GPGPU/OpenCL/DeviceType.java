package org.jml.GPGPU.OpenCL;

import static org.jocl.CL.*;

public enum DeviceType {
    Default (CL_DEVICE_TYPE_DEFAULT),
    CPU (CL_DEVICE_TYPE_CPU),
    GPU (CL_DEVICE_TYPE_GPU),
    Accelerator (CL_DEVICE_TYPE_ACCELERATOR),
    All (CL_DEVICE_TYPE_ALL);

    protected long id;
    DeviceType(long id) {
        this.id = id;
    }

    protected static DeviceType fromId (long id) {
        if (id == CL_DEVICE_TYPE_CPU) {
            return CPU;
        } else if (id == CL_DEVICE_TYPE_GPU) {
            return GPU;
        } else if (id == CL_DEVICE_TYPE_ACCELERATOR) {
            return Accelerator;
        } else if (id == CL_DEVICE_TYPE_ALL) {
            return All;
        }

        return Default;
    }
}