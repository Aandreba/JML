package Mathx;

import Complex.Comp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Function;

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

    public static float factorial (int x) {
        if (x < 0) {
            return factorial((float) x);
        }

        float y = 1;
        for (int i=1;i<=x;i++) {
            y *= i;
        }

        return y;
    }

    public static float factorial (float x) {
        float y = 1;
        float last = 0;

        long k = 1;
        while (y != last) {
            last = y;
            y *= Mathf.pow((k + 1f) / k, x) * k/(x + k);
            k++;
        }

        return y;
    }

    public static float stirling (float x) {
        if (x < 0) {
            throw new ArithmeticException("Can't calculate stirling factorial of negative number");
        } else if (x == 0) {
            return 1;
        }

        return Mathf.sqrt(PI2 * x) * Mathf.pow(x / E, x);
    }

    public static float binomial (float n, float k) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    public static float stirlingBinomial (float n, float k) {
        return stirling(n) / (stirling(k) * stirling(n - k));
    }

    public static Comp[] quadratic (float a, float b, float c) {
        Comp[] x = new Comp[2];
        Comp sqrt = Comp.sqrt(b * b - 4*a*c);

        float a2 = 2 * a;
        x[0] = sqrt.add(-b).div(a2);
        x[1] = sqrt.invSubtr(-b).div(a2);
        return x;
    }

    public static Comp[] cubic (float a, float b, float c, float d) {
        final Comp zeta = Comp.sqrt(-3).subtr(1).div(2);
        Comp[] x = new Comp[3];

        float b2 = b*b;
        float d0 = b2 - 3*a*c;
        float d1 = 2*b2*b - 9*a*b*c + 27*a*a*d;

        Comp C = Comp.sqrt(d1*d1 - 4*d0*d0*d0).add(d1).div(2).pow(1f / 3);
        Comp pow = Comp.ONE;
        float k = -1 / (3 * a);

        for (int i=0;i<3;i++) {
            pow = pow.mul(zeta);
            Comp u = pow.mul(C);

            x[i] = u.add(b).add(u.invDiv(d0)).mul(k);
        }

        return x;
    }

    public static class Plot2D {
        Function<Float,Float> function;
        Color color;

        public Plot2D (Function<Float, Float> function, Color color) {
            this.function = function;
            this.color = color;
        }
    }

    public static BufferedImage plot2D (float from, float to, float fromY, float toY, int width, int height, Plot2D... plots) {
        float ft = from - to;
        float ftY = fromY - toY;
        float htY = height * toY;

        float step = -ft / width;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (Plot2D plot: plots) {
            for (int i = 0; i < width; i++) {
                float x = from + (i * step);
                float y = plot.function.apply(x);

                int h = round((height * y - htY) / ftY);
                if (h < 0 || h >= height) {
                    continue;
                }

                image.setRGB(i, h, plot.color.getRGB());
            }
        }

        return image;
    }
}