package org.jml.Mathx;

import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Extra.Intx;
import org.jml.Function.Real.DecimalReal;
import org.jml.Function.Real.DoubleReal;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.Function;

final public class Mathf {
    final public static float PI = (float) StrictMath.PI;
    final public static float E = (float) StrictMath.E;

    final public static float HALFPI = PI / 2;
    final public static float PI2 = 2 * PI;
    final public static float SQRT_PI = (float) Mathd.SQRT_PI;

    final public static float SQRT2 = (float) Math.sqrt(2);

    final public static float LN2 = (float) Mathd.LN2;
    final public static float LN10 = (float) Mathd.LN10;

    final public static float INF = Float.POSITIVE_INFINITY;
    final public static float NINF = Float.NEGATIVE_INFINITY;

    final public static float TO_RADIANS = PI / 180;
    final public static float TO_DEGREES = 180 / PI;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            try {
                NativeUtils.loadLibraryFromJar("/org/jml/Mathx/Win/mathf_win.dll");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (os.contains("mac")) {
            try {
                NativeUtils.loadLibraryFromJar("/org/jml/Mathx/OSX/mathf_osx.dylib");
            } catch (IOException e) {
                System.exit(1);
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
    public static float clamp (float x, float from, float to) {
        return x <= from ? from : Math.min(x, to);
    }
    public static int clamp (int x, int from, int to) {
        return x <= from ? from : Math.min(x, to);
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

    public static boolean equals (Number a, Number b) {
        if (a == null || b == null) { return false; }

        Class type = a.getClass();
        if (!type.equals(b.getClass())) { return false; }

        return type.isPrimitive() ? a.doubleValue() == b.doubleValue() : a.equals(b);
    }

    public static float sum (int from, int to, Function<Integer, Float> function) {
        float sum = 0;
        for (int i=from;i<=to;i++) {
            sum += function.apply(i);
        }

        return sum;
    }

    public static Comp sumi(int from, int to, Function<Integer, Comp> function) {
        Comp sum = Comp.ZERO;
        for (int i=from;i<=to;i++) {
            sum = sum.add(function.apply(i));
        }

        return sum;
    }

    public static float prod(int from, int to, Function<Integer, Float> function) {
        float prod = 1;
        for (int i=from;i<=to;i++) {
            prod *= function.apply(i);
        }

        return prod;
    }

    public static Comp prodi(int from, int to, Function<Integer, Comp> function) {
        Comp sum = Comp.ONE;
        for (int i=from;i<=to;i++) {
            sum = sum.mul(function.apply(i));
        }

        return sum;
    }

    public static int gcd (int a, int b) {
        boolean isMax = a >= b;
        int alpha = isMax ? a : b;
        int beta = isMax ? b : a;

        while (beta != 0) {
            int temp = beta;
            beta = alpha % beta;
            alpha = temp;
        }

        return Math.max(alpha, -alpha);
    }

    public static float erf (float x) {
        float integ = Integral.integ(0, x, (DecimalReal) (t,c) -> Mathb.exp(t.pow(2).negate(), c));
        return 2 * integ / SQRT_PI;
    }

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

    public static float gamma (float x) {
        return factorial(x - 1);
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

    public static Comp[] quadratic (Comp a, Comp b, Comp c) {
        if (a.isReal() && b.isReal() && c.isReal()) {
            return quadratic(a.re, b.re, c.re);
        }

        Comp[] x = new Comp[2];
        Comp sqrt = Comp.sqrt(b.pow(2).subtr(a.mul(c).mul(2)));

        Comp a2 = a.mul(2);
        Comp negB = b.mul(-1);

        x[0] = negB.add(sqrt).div(a2);
        x[1] = negB.subtr(sqrt).div(a2);
        return x;
    }

    public static Comp[] cubic (float a, float b, float c, float d) {
        final Comp zeta = Comp.sqrt(-3).subtr(1).div(2);
        Comp[] x = new Comp[3];

        float b2 = b*b;
        float d0 = b2 - 3*a*c;
        float d1 = 2*b2*b - 9*a*b*c + 27*a*a*d;

        Comp C = Comp.sqrt(d1*d1 - 4*d0*d0*d0).add(d1).div(2).cbrt();
        Comp pow = Comp.ONE;
        float k = -1 / (3 * a);

        for (int i=0;i<3;i++) {
            pow = pow.mul(zeta);
            Comp u = pow.mul(C);

            x[i] = u.add(b).add(u.invDiv(d0)).mul(k);
        }

        return x;
    }

    public static Comp[] cubic (Comp a, Comp b, Comp c, Comp d) {
        final Comp zeta = Comp.sqrt(-3).subtr(1).div(2);
        Comp[] x = new Comp[3];

        Comp b2 = b.mul(b);
        Comp d0 = b2.subtr(a.mul(c).mul(3));
        Comp d1 = b2.mul(b).mul(2).subtr(a.mul(b).mul(c).mul(9)).add(a.mul(a).mul(d).mul(27));

        Comp C = Comp.sqrt(d1.mul(d1).subtr(d0.mul(d0).mul(d0).mul(4))).add(d1).div(2).cbrt();
        Comp pow = Comp.ONE;
        Comp k = Comp.MONE.div(a.mul(3));

        for (int i=0;i<3;i++) {
            pow = pow.mul(zeta);
            Comp u = pow.mul(C);

            x[i] = u.add(b).add(d0.div(u)).mul(k);
        }

        return x;
    }

    public static Veci poly (float... c) {
        int n = c.length;

        if (n <= 1) {
            throw new IllegalArgumentException("Must input 2 or more coefficients");
        } else if (n == 2) {
            return new Veci(new Comp(-c[1], 0));
        } else if (n == 3) {
            return new Veci(quadratic(c[0], c[1], c[2]));
        } else if (n == 4) {
            return new Veci(cubic(c[0], c[1], c[2], c[3]));
        }

        // Durand–Kerner method
        final Vec input = new Vec(c);
        System.arraycopy(input.div(c[0]).toArray(), 0, c, 0, n);
        Comp[] lst = new Comp[]{Comp.ONE};
        Veci X = Veci.foreach(n - 1, i -> {
            if (i == 0) {
                return Comp.ONE;
            }

            lst[0] = lst[0].mul(new Comp(0.4f, 0.9f));
            return lst[0];
        });

        int p = 0;
        Veci last = null;
        boolean isNaN = false;

        while (!X.equals(last) && p < 10000) {
            Veci y = Veci.foreach(n - 1, i -> {
                Comp x = X.get(i);
                Comp f = new Comp(c[n - 1], 0);
                Comp pow = Comp.ONE;

                for (int j = n - 2; j >= 0; j--) {
                    pow = pow.mul(x);
                    f = f.add(pow.mul(c[j]));
                }

                Comp div = Comp.ONE;
                for (int j = 0; j < n - 1; j++) {
                    if (i == j) {
                        continue;
                    }

                    div = div.mul(x.subtr(X.get(j)));
                }

                return x.subtr(f.div(div));
            });

            if (X.any(z -> z.isNan() || z.isInfinite())) {
                return Mathd.poly(input.toDouble().toArray()).toFloat();
            }

            last = X.clone();
            X.set(y);
            p++;
        }

        return X;
    }

    public static float agm (float alpha, float beta) {
        float a = alpha;
        float b = beta;

        while (a != b) {
            a = 0.5f * (a + b);
            b = Mathf.sqrt(a * b);
        }

        return alpha;
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