package org.jml.GPGPU.OpenCL;

import org.jocl.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.jocl.CL.*;

final public class Device {
    final private static Device[] DEVICES = loadDevices();
    final public static Device DEFAULT = getFirst();

    final public cl_device_id id;
    final public Platform platform;
    final public String name, vendor, version;
    final public DeviceType type;
    final public long computeUnits, itemDimensions, groupSize, maxFrequency, maxMemoryAlloc, globalMemory, localMemory;
    final private long[] itemSizes;
    final public int addressBits;
    final public boolean hasErrorCorrectionSupport, hasDedicatedMemory;

    private Device (Platform platform, cl_device_id id) {
        this.id = id;
        this.platform = platform;

        this.name = Query.getString(this, CL_DEVICE_NAME);
        this.vendor = Query.getString(this, CL_DEVICE_VENDOR);
        this.version = Query.getString(this, CL_DEVICE_VERSION);
        this.type = DeviceType.fromId(Query.getLong(this, CL_DEVICE_TYPE));
        this.computeUnits = Query.getLong(this, CL_DEVICE_MAX_COMPUTE_UNITS);
        this.itemDimensions = Query.getLong(this, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
        this.itemSizes = Query.getLongs(this, CL_DEVICE_MAX_WORK_ITEM_SIZES, 3);
        this.groupSize = Query.getLong(this, CL_DEVICE_MAX_WORK_GROUP_SIZE);
        this.maxFrequency = Query.getLong(this, CL_DEVICE_MAX_CLOCK_FREQUENCY);
        this.addressBits = Query.getInt(this, CL_DEVICE_ADDRESS_BITS);
        this.maxMemoryAlloc = Query.getLong(this, CL_DEVICE_MAX_MEM_ALLOC_SIZE);
        this.globalMemory = Query.getLong(this, CL_DEVICE_GLOBAL_MEM_SIZE);
        this.hasErrorCorrectionSupport = Query.getInt(this, CL_DEVICE_ERROR_CORRECTION_SUPPORT) == 1;
        this.localMemory = Query.getLong(this, CL_DEVICE_LOCAL_MEM_SIZE);
        this.hasDedicatedMemory = Query.getInt(this, CL_DEVICE_LOCAL_MEM_TYPE) == 1;
    }

    public long[] getItemSizes() {
        return itemSizes.clone();
    }

    @Override
    public String toString() {
        return "Device {" +
                "id=" + id +
                ", platform='" + platform.name + '\'' +
                ", name='" + name + '\'' +
                ", vendor='" + vendor + '\'' +
                ", version='" + version + '\'' +
                ", type=" + type +
                ", computeUnits=" + computeUnits +
                ", itemDimensions=" + itemDimensions +
                ", groupSize=" + groupSize +
                ", maxFrequency=" + maxFrequency +
                ", maxMemoryAlloc=" + maxMemoryAlloc +
                ", globalMemory=" + globalMemory +
                ", localMemory=" + localMemory +
                ", itemSizes=" + Arrays.toString(itemSizes) +
                ", addressBits=" + addressBits +
                ", hasErrorCorrectionSupport=" + hasErrorCorrectionSupport +
                ", hasDedicatedMemory=" + hasDedicatedMemory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Device[] getDevices () {
        return DEVICES.clone();
    }

    public static Device getFirst () {
        return DEVICES[0];
    }

    private static Device[] loadDevices () {
        Platform[] platforms = Platform.getPlatforms();
        ArrayList<Device> devices = new ArrayList<>();
        for (Platform platform: platforms) {
            int num[] = new int[1];
            clGetDeviceIDs(platform.id, CL_DEVICE_TYPE_ALL, 0, null, num);

            cl_device_id[] id = new cl_device_id[num[0]];
            clGetDeviceIDs(platform.id, CL_DEVICE_TYPE_ALL, num[0], id, null);

            for (int i=0;i<id.length;i++) {
                devices.add(new Device(platform, id[i]));
            }
        }

        return devices.toArray(Device[]::new);
    }
}
