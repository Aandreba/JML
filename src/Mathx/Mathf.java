package Mathx;

import java.io.IOException;

final public class Mathf {
    final public static float PI = (float) StrictMath.PI;
    final public static float E = (float) StrictMath.E;
    final public static float SQRT2 = (float) Math.sqrt(2);
    final public static float HALFPI = PI / 2;
    final public static float PI2 = 2 * PI;

    final private static float TO_RADIANS = PI / 180;
    final private static float TO_DEGREES = 180 / PI;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.loadLibrary("mathf_win");
        } else if (os.contains("mac")) {
            try {
                NativeUtils.loadLibraryFromJar("/OSX/mathf_osx.dylib");
            } catch (IOException e) {
                System.load(System.getProperty("user.dir") + "/lib/OSX/mathf_osx.dylib");
            }
        }
    }

    public native static float sin (float x);
    public native static float cos (float x);
    public native static float tan (float x);
    public native static float asin (float x);
    public native static float acos (float x);
    public native static float atan (float x);
    public native static float atan2 (float x, float y);

    public native static float sinh (float x);
    public native static float cosh (float x);
    public native static float tanh (float x);
    public native static float asinh (float x);
    public native static float acosh (float x);
    public native static float atanh (float x);

    public native static float pow (float x, float y);
    public native static float exp (float x);
    public native static float expm1 (float x);
    public native static float log (float x);
    public native static float log1p (float x);
    public native static float log2 (float x);
    public native static float log10 (float x);
    public native static float sqrt (float x);
    public native static float cbrt (float x);
    public native static float hypot (float x, float y);

    public static float toRadians (float x) {
        return x * TO_RADIANS;
    }
    public static float toDegrees (float x) {
        return x * TO_DEGREES;
    }
    public static int floor (float x) { return (int) x; }
    public static int ceil (float x) {
        int floor = (int) x;
        return floor == x ? floor : floor + 1;
    }
    public static int round (float x) { return Math.round(x); }
    public static float random () { return Rand.getFloat(); };
    public static float abs (float x) { return Math.abs(x); }
    public static float max (float x, float y) { return Math.max(x, y); }
    public static float min (float x, float y) { return Math.min(x, y); }
    public static float ulp (float x) { return Math.ulp(x); }
    public static float signum (float x) { return Math.signum(x); }
    public static float copySign (float magnitude, float sign) { return Math.copySign(magnitude, sign); }
    public static int getExponent (float x) { return Math.getExponent(x); }
    public static float nextAfter (float start, double direction) { return Math.nextAfter(start, direction); }
    public static float nextUp (float x) { return Math.nextUp(x); }
    public static float nextDown (float x) { return Math.nextDown(x); }
    public static float scalb (float f, int scaleFactor) { return Math.scalb(f, scaleFactor); }
}
