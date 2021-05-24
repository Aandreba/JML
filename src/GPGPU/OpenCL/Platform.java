package GPGPU.OpenCL;

import org.jocl.*;
import static org.jocl.CL.*;

final public class Platform {
    final private static Platform[] platforms = loadPlatforms();

    final public cl_platform_id id;
    final public String profile, version, name, vendor;

    private Platform (cl_platform_id id) {
        this.id = id;
        this.profile = Query.getString(this, CL_PLATFORM_PROFILE);
        this.version = Query.getString(this, CL_PLATFORM_VERSION);
        this.name = Query.getString(this, CL_PLATFORM_NAME);
        this.vendor = Query.getString(this, CL_PLATFORM_VENDOR);
    }

    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", profile='" + profile + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", vendor='" + vendor + '\'' +
                '}';
    }

    public static Platform[] getPlatforms () {
        return platforms.clone();
    }

    public static Platform getFirst () {
        return platforms[0];
    }

    private static Platform[] loadPlatforms () {
        int num[] = new int[1];
        clGetPlatformIDs(0, null, num);

        cl_platform_id[] id = new cl_platform_id[num[0]];
        clGetPlatformIDs(id.length, id, null);

        Platform[] platforms = new Platform[id.length];
        for (int i=0;i<id.length;i++) {
            platforms[i] = new Platform(id[i]);
        }

        return platforms;
    }
}
