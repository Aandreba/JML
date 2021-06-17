package org.jml.Mathx;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.Vecid;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.io.IOException;
import java.util.function.Function;

final public class Mathd {
    final public static double SQRT2 = Math.sqrt(2);
    final public static double HALFPI = Math.PI / 2;
    final public static double PI2 = 2 * Math.PI;

    final public static double TO_RADIANS = Math.PI / 180;
    final public static double TO_DEGREES = 180 / Math.PI;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            try {
                NativeUtils.loadLibraryFromJar("/org/jml/Mathx/Win/mathd_win.dll");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (os.contains("mac")) {
            try {
                NativeUtils.loadLibraryFromJar("/org/jml/Mathx/OSX/mathd_osx.dylib");
            } catch (IOException e) {
                System.exit(1);
            }
        }
    }

    public native static double asinh (double x);
    public native static double acosh (double x);
    public native static double atanh (double x);
    public native static double log2 (double x);

    public static double summation (int from, int to, Function<Integer, Double> function) {
        double sum = 0;
        for (int i=from;i<=to;i++) {
            sum += function.apply(i);
        }

        return sum;
    }

    public static double product (int from, int to, Function<Integer, Double> function) {
        float prod = 1;
        for (int i=from;i<=to;i++) {
            prod *= function.apply(i);
        }

        return prod;
    }

    public static double factorial (int x) {
        if (x < 0) {
            return factorial((double) x);
        }

        double y = 1;
        for (int i=1;i<=x;i++) {
            y *= i;
        }

        return y;
    }

    public static double factorial (double x) {
        double y = 1;
        double last = 0;

        long k = 1;
        while (y != last) {
            last = y;
            y *= Math.pow((k + 1f) / k, x) * k/(x + k);
            k++;
        }

        return y;
    }

    public static double gamma (double x) {
        return factorial(x - 1);
    }

    public static double stirling (double x) {
        if (x == 0) {
            return 1;
        }

        return Math.sqrt(PI2 * x) * Math.pow(x / Math.E, x);
    }

    public static Compd[] quadratic (double a, double b, double c) {
        Compd[] x = new Compd[2];
        Compd sqrt = Compd.sqrt(b * b - 4*a*c);

        double a2 = 2 * a;
        x[0] = sqrt.add(-b).div(a2);
        x[1] = sqrt.invSubtr(-b).div(a2);
        return x;
    }

    public static Compd[] cubic (double a, double b, double c, double d) {
        final Compd zeta = Compd.sqrt(-3).subtr(1).div(2);
        Compd[] x = new Compd[3];

        double b2 = b*b;
        double d0 = b2 - 3*a*c;
        double d1 = 2*b2*b - 9*a*b*c + 27*a*a*d;

        Compd C = Compd.sqrt(d1*d1 - 4*d0*d0*d0).add(d1).div(2).pow(1f / 3);
        Compd pow = Compd.ONE;
        double k = -1 / (3 * a);

        for (int i=0;i<3;i++) {
            pow = pow.mul(zeta);
            Compd u = pow.mul(C);

            x[i] = u.add(b).add(u.invDiv(d0)).mul(k);
        }

        return x;
    }

    public static Vecid poly (double... c) {
        int n = c.length;

        if (n <= 1) {
            throw new IllegalArgumentException("Must input 2 or more coefficients");
        } else if (n == 2) {
            return new Vecid(new Compd(-c[1], 0));
        } else if (n == 3) {
            return new Vecid(quadratic(c[0], c[1], c[2]));
        } else if (n == 4) {
            return new Vecid(cubic(c[0], c[1], c[2], c[3]));
        }

        // Durand – Kerner method
        System.arraycopy(new Vecd(c).div(c[0]).toArray(), 0, c, 0, n);
        Compd[] lst = new Compd[]{Compd.ONE};
        Vecid X = Vecid.foreach(n - 1, i -> {
            if (i == 0) {
                return Compd.ONE;
            }

            lst[0] = lst[0].mul(new Compd(0.4, 0.9));
            return lst[0];
        });

        int p = 0;
        Vecid last = null;
        while (!X.equals(last) && p < 10000) {
            Vecid y = Vecid.foreach(n - 1, i -> {
                Compd x = X.get(i);
                Compd f = new Compd(c[n - 1], 0);
                Compd pow = Compd.ONE;

                for (int j = n - 2; j >= 0; j--) {
                    pow = pow.mul(x);
                    f = f.add(pow.mul(c[j]));
                }

                Compd div = Compd.ONE;
                for (int j = 0; j < n - 1; j++) {
                    if (i == j) {
                        continue;
                    }

                    div = div.mul(x.subtr(X.get(j)));
                }

                return x.subtr(f.div(div));
            });

            last = X.clone();
            X.set(y);
            p++;
        }

        return X;
    }
}