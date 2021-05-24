package Testing;


import GPGPU.OpenCL.Device;
import Vector.VecCL;

public class OpenCL {
    public static void main (String... args) {
        System.out.println(Device.DEFAULT);
        VecCL vector = new VecCL(1, 2, 3);
        System.out.println(vector);
    }
}
