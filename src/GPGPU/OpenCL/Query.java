package GPGPU.OpenCL;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import static org.jocl.CL.*;

public class Query {
    // Platform
    public static int[] getInts (Platform platform, int param, int vals) {
        int[] values = new int[vals];
        clGetPlatformInfo(platform.id, param, Sizeof.cl_int * vals, Pointer.to(values), null);
        return values;
    }

    public static long[] getLongs (Platform platform, int param, int vals) {
        long[] values = new long[vals];
        clGetPlatformInfo(platform.id, param, Sizeof.cl_long * vals, Pointer.to(values), null);
        return values;
    }

    public static int getInt (Platform platform, int param) {
        return getInts(platform, param, 1)[0];
    }

    public static long getLong (Platform platform, int param) {
        return getLongs(platform, param, 1)[0];
    }

    public static String getString (Platform platform, int param) {
        long size[] = new long[1];
        clGetPlatformInfo(platform.id, param, 0, null, size);

        byte buffer[] = new byte[(int)size[0]];
        clGetPlatformInfo(platform.id, param, buffer.length, Pointer.to(buffer), null);

        return new String(buffer, 0, buffer.length-1);
    }

    // Device
    public static int[] getInts (Device device, int param, int vals) {
        int[] values = new int[vals];
        clGetDeviceInfo(device.id, param, Sizeof.cl_int * vals, Pointer.to(values), null);
        return values;
    }

    public static long[] getLongs (Device device, int param, int vals) {
        long[] values = new long[vals];
        clGetDeviceInfo(device.id, param, Sizeof.cl_long * vals, Pointer.to(values), null);
        return values;
    }

    public static int getInt (Device device, int param) {
        return getInts(device, param, 1)[0];
    }

    public static long getLong (Device device, int param) {
        return getLongs(device, param, 1)[0];
    }

    public static String getString (Device device, int param) {
        long size[] = new long[1];
        clGetDeviceInfo(device.id, param, 0, null, size);

        byte buffer[] = new byte[(int)size[0]];
        clGetDeviceInfo(device.id, param, buffer.length, Pointer.to(buffer), null);

        return new String(buffer, 0, buffer.length-1);
    }
}
