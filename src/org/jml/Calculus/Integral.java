package org.jml.Calculus;

import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Function.Real.RealFunction;
import org.jml.Function.Real.FloatReal;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;
import org.jml.Vector.Decimal.Vecb;
import org.jml.Vector.Double.Vecd;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicReference;

public class Integral {
    final private static BigDecimal N945 = BigDecimal.valueOf(945);
    final private static BigDecimal N45 = BigDecimal.valueOf(45);
    final private static BigDecimal N32 = BigDecimal.valueOf(32);
    final private static BigDecimal N12 = BigDecimal.valueOf(12);
    final private static BigDecimal N14 = BigDecimal.valueOf(14);

    public static BigDecimal integ (BigDecimal a, BigDecimal b, RealFunction func, MathContext context) {
        BigDecimal delta = b.subtract(a);
        BigDecimal alpha = func.deriv(6).apply(delta.divide(Mathb.TWO), context);
        int n = (int) (context.getPrecision() * Mathf.max(context.getPrecision(), Mathf.abs(alpha.floatValue())));

        BigDecimal h = delta.divide(BigDecimal.valueOf(n-1), context);
        Vecb f = Vecb.foreach(context, n, z -> a.add(h.multiply(BigDecimal.valueOf(z))));

        BigDecimal error = Mathb.EIGHT.negate().divide(N945, context).multiply(h.pow(7)).multiply(alpha, context);
        BigDecimal sum = Mathb.SEVEN.multiply(func.apply(a, context).add(func.apply(b, context)));
        BigDecimal sum32 = Mathb.ZERO;
        BigDecimal sum12 = Mathb.ZERO;
        BigDecimal sum14 = Mathb.ZERO;

        for (int i=1;i<n;i+=2) {
            sum32 = sum32.add(func.apply(f.get(i), context));
        }

        for (int i=2;i<n-1;i+=4) {
            sum12 = sum12.add(func.apply(f.get(i), context));
        }

        for (int i=4;i<n-3;i+=4) {
            sum14 = sum14.add(func.apply(f.get(i), context));
        }

        return sum.add(sum32.multiply(N32)).add(sum14.multiply(N14)).add(sum12.multiply(N12)).multiply(Mathb.TWO).multiply(h).divide(N45, context).add(error, context);
    }

    public static float integ (float a, float b, RealFunction function) {
        return integ(BigDecimal.valueOf(a), BigDecimal.valueOf(b), function, MathContext.DECIMAL32).floatValue();
    }

    public static double integ (double a, double b, RealFunction function) {
        return integ(BigDecimal.valueOf(a), BigDecimal.valueOf(b), function, MathContext.DECIMAL64).doubleValue();
    }

    public static Comp integ (float a, float b, ComplexFunction func) {
        return null;
    }
}
